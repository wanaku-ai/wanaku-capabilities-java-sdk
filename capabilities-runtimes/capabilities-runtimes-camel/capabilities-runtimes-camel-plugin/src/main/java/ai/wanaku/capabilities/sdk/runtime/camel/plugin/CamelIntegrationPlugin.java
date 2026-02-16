package ai.wanaku.capabilities.sdk.runtime.camel.plugin;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.ContextServicePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceType;
import ai.wanaku.capabilities.sdk.common.ServicesHelper;
import ai.wanaku.capabilities.sdk.common.VersionHelper;
import ai.wanaku.capabilities.sdk.common.config.DefaultServiceConfig;
import ai.wanaku.capabilities.sdk.common.config.ServiceConfig;
import ai.wanaku.capabilities.sdk.common.serializer.JacksonSerializer;
import ai.wanaku.capabilities.sdk.discovery.DiscoveryServiceHttpClient;
import ai.wanaku.capabilities.sdk.discovery.ZeroDepRegistrationManager;
import ai.wanaku.capabilities.sdk.discovery.config.DefaultRegistrationConfig;
import ai.wanaku.capabilities.sdk.discovery.deserializer.JacksonDeserializer;
import ai.wanaku.capabilities.sdk.discovery.util.DiscoveryHelper;
import ai.wanaku.capabilities.sdk.runtime.camel.downloader.DownloaderFactory;
import ai.wanaku.capabilities.sdk.runtime.camel.downloader.ResourceDownloaderCallback;
import ai.wanaku.capabilities.sdk.runtime.camel.downloader.ResourceListBuilder;
import ai.wanaku.capabilities.sdk.runtime.camel.downloader.ResourceRefs;
import ai.wanaku.capabilities.sdk.runtime.camel.downloader.ResourceType;
import ai.wanaku.capabilities.sdk.runtime.camel.grpc.CamelHealthProbe;
import ai.wanaku.capabilities.sdk.runtime.camel.grpc.CamelResource;
import ai.wanaku.capabilities.sdk.runtime.camel.grpc.CamelTool;
import ai.wanaku.capabilities.sdk.runtime.camel.grpc.ProvisionBase;
import ai.wanaku.capabilities.sdk.runtime.camel.init.Initializer;
import ai.wanaku.capabilities.sdk.runtime.camel.init.InitializerFactory;
import ai.wanaku.capabilities.sdk.runtime.camel.model.McpSpec;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.resources.WanakuResourceRuleProcessor;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.resources.WanakuResourceTransformer;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools.WanakuToolRuleProcessor;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools.WanakuToolTransformer;
import ai.wanaku.capabilities.sdk.runtime.camel.util.McpRulesManager;
import ai.wanaku.capabilities.sdk.security.TokenEndpoint;
import ai.wanaku.capabilities.sdk.services.ServicesHttpClient;

/**
 * Camel ContextServicePlugin implementation for integrating the Camel Integration Capability
 * into an existing Camel application. This plugin is discovered via SPI.
 *
 * <p>Configuration is loaded from properties file and/or environment variables.
 * See {@link PluginConfiguration} for configuration options.</p>
 */
public class CamelIntegrationPlugin implements ContextServicePlugin {
    private static final Logger LOG = LoggerFactory.getLogger(CamelIntegrationPlugin.class);

    private Server grpcServer;
    private ZeroDepRegistrationManager registrationManager;
    private PluginConfiguration config;

    @Override
    public void load(CamelContext camelContext) {
        LOG.info("Camel Integration Capability Plugin {} is loading", VersionHelper.VERSION);

        try {
            // Load and validate configuration
            config = PluginConfiguration.load();
            config.validate();

            // Create the data directory first (needed by initializers)
            Path dataDirPath = Paths.get(config.getDataDir());
            Files.createDirectories(dataDirPath);
            LOG.info("Using data directory: {}", dataDirPath.toAbsolutePath());

            // Resource initialization / acquisition
            Initializer initializer = InitializerFactory.createInitializer(config.getInitFrom(), dataDirPath);
            initializer.initialize();

            final ServiceConfig serviceConfig = DefaultServiceConfig.Builder.newBuilder()
                    .baseUrl(config.getRegistrationUrl())
                    .serializer(new JacksonSerializer())
                    .clientId(config.getClientId())
                    .tokenEndpoint(TokenEndpoint.autoResolve(config.getRegistrationUrl(), config.getTokenEndpoint()))
                    .secret(config.getClientSecret())
                    .build();

            ServicesHttpClient httpClient = new ServicesHttpClient(serviceConfig);
            DownloaderFactory downloaderFactory = new DownloaderFactory(httpClient, dataDirPath);

            List<ResourceRefs<URI>> resources = ResourceListBuilder.newBuilder()
                    .addRoutesRef(config.getRoutesRef())
                    .addRulesRef(config.getRulesRef())
                    .addDependenciesRef(config.getDependenciesRef())
                    .buildForPlugin();

            ResourceDownloaderCallback resourcesDownloaderCallback =
                    new ResourceDownloaderCallback(downloaderFactory, resources);

            final ServiceTarget toolInvokerTarget = newServiceTarget();
            registrationManager = newRegistrationManager(toolInvokerTarget, resourcesDownloaderCallback, serviceConfig);

            if (!resourcesDownloaderCallback.waitForDownloads()) {
                LOG.error("Failed to download required resources (check the logs)");
                throw new IllegalStateException("Failed to download required resources");
            }

            final Map<ResourceType, Path> downloadedResources = resourcesDownloaderCallback.getDownloadedResources();

            McpSpec mcpSpec = createMcpSpec(httpClient, downloadedResources);

            // Start gRPC server
            final ServerBuilder<?> serverBuilder =
                    Grpc.newServerBuilderForPort(config.getGrpcPort(), InsecureServerCredentials.create());
            grpcServer = serverBuilder
                    .addService(new CamelTool(camelContext, mcpSpec))
                    .addService(new CamelResource(camelContext, mcpSpec))
                    .addService(new ProvisionBase(config.getServiceName()))
                    .addService(new CamelHealthProbe(camelContext, registrationManager.getTarget()))
                    .build();

            grpcServer.start();
            LOG.info("Camel Integration Capability Plugin started on gRPC port {}", config.getGrpcPort());

        } catch (Exception e) {
            LOG.error("Failed to initialize Camel Integration Capability Plugin", e);
            throw new RuntimeException("Failed to initialize Camel Integration Capability Plugin", e);
        }
    }

    @Override
    public void unload(CamelContext camelContext) {
        LOG.info("Camel Integration Capability Plugin is unloading");

        try {
            // Deregister from discovery service
            if (registrationManager != null) {
                registrationManager.deregister();
            }

            // Stop gRPC server
            if (grpcServer != null) {
                grpcServer.shutdown();
                grpcServer.awaitTermination();
            }

            LOG.info("Camel Integration Capability Plugin stopped successfully");
        } catch (Exception e) {
            LOG.error("Error during plugin shutdown", e);
        }
    }

    private ServiceTarget newServiceTarget() {
        String address = DiscoveryHelper.resolveRegistrationAddress(config.getRegistrationAnnounceAddress());
        return ServiceTarget.newEmptyTarget(
                config.getServiceName(), address, config.getGrpcPort(), ServiceType.MULTI_CAPABILITY.asValue());
    }

    private ZeroDepRegistrationManager newRegistrationManager(
            ServiceTarget serviceTarget,
            ResourceDownloaderCallback resourcesDownloaderCallback,
            ServiceConfig serviceConfig) {
        DiscoveryServiceHttpClient discoveryServiceHttpClient = new DiscoveryServiceHttpClient(serviceConfig);

        final DefaultRegistrationConfig registrationConfig = DefaultRegistrationConfig.Builder.newBuilder()
                .initialDelay(config.getInitialDelay())
                .period(config.getPeriod())
                .dataDir(ServicesHelper.getCanonicalServiceHome(config.getServiceName()))
                .maxRetries(config.getRetries())
                .waitSeconds(config.getRetryWaitSeconds())
                .build();

        ZeroDepRegistrationManager manager = new ZeroDepRegistrationManager(
                discoveryServiceHttpClient, serviceTarget, registrationConfig, new JacksonDeserializer());

        manager.addCallBack(resourcesDownloaderCallback);
        manager.start();

        return manager;
    }

    private McpSpec createMcpSpec(ServicesHttpClient servicesClient, Map<ResourceType, Path> downloadedResources) {
        String rulesRef =
                downloadedResources.get(ResourceType.RULES_REF).toAbsolutePath().toString();
        McpRulesManager mcpRulesManager = new McpRulesManager(config.getServiceName(), rulesRef);
        final WanakuToolTransformer toolTransformer =
                new WanakuToolTransformer(config.getServiceName(), new WanakuToolRuleProcessor(servicesClient));
        final WanakuResourceTransformer resourceTransformer =
                new WanakuResourceTransformer(config.getServiceName(), new WanakuResourceRuleProcessor(servicesClient));

        return mcpRulesManager.loadMcpSpecAndRegister(toolTransformer, resourceTransformer);
    }
}
