package ${package};

import java.util.concurrent.Callable;
import java.util.concurrent.Callable;

import ai.wanaku.api.discovery.RegistrationManager;
import ai.wanaku.api.types.providers.ServiceTarget;
import ai.wanaku.api.types.providers.ServiceType;
import ai.wanaku.capabilities.sdk.common.ServicesHelper;
import ai.wanaku.capabilities.sdk.discovery.DiscoveryServiceHttpClient;
import ai.wanaku.capabilities.sdk.discovery.ZeroDepRegistrationManager;
import ai.wanaku.capabilities.sdk.discovery.config.DefaultRegistrationConfig;
import ai.wanaku.capabilities.sdk.discovery.config.DefaultServiceConfig;
import ai.wanaku.capabilities.sdk.discovery.deserializer.JacksonDeserializer;
import ai.wanaku.capabilities.sdk.discovery.serializer.JacksonSerializer;
import ai.wanaku.capabilities.sdk.discovery.util.DiscoveryHelper;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import picocli.CommandLine;

/**
 * Hello world!
 *
 */
public class App implements Callable<Integer> {
    @CommandLine.Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;

    @CommandLine.Option(names = {"--registration-url"}, description = "The registration URL to use", required = true)
    private String registrationUrl;

    @CommandLine.Option(names = {"--grpc-port"}, description = "The gRPC port to use", defaultValue = "9190")
    private int grpcPort;

    @CommandLine.Option(names = {"--registration-announce-address"}, description = "The announce address to use when registering",
            defaultValue = "auto", required = true)
    private String registrationAnnounceAddress;

    @CommandLine.Option(names = {"--name"}, description = "The service name to use", defaultValue = "camel")
    private String name;

    @CommandLine.Option(names = {"--retries"}, description = "The maximum number of retries for registration", defaultValue = "3")
    private int retries;

    @CommandLine.Option(names = {"--wait-seconds"}, description = "The retry wait seconds between attempts", defaultValue = "1")
    private int retryWaitSeconds;

    @CommandLine.Option(names = {"--initial-delay"}, description = "Initial delay for registration attempts in seconds", defaultValue = "0")
    private long initialDelay;

    @CommandLine.Option(names = {"--period"}, description = "Period between registration attempts in seconds", defaultValue = "5")
    private long period;

    public static void main( String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);

        System.exit(exitCode);
    }

    /**
     * Creates a new registration manager based on the arguments passed via CLI
     * @return
     */
    public RegistrationManager newRegistrationManager() {
        String address = DiscoveryHelper.resolveRegistrationAddress(registrationAnnounceAddress);
        final ServiceTarget serviceTarget = ServiceTarget.newEmptyTarget(name, address, grpcPort, ServiceType.TOOL_INVOKER);

        final DefaultServiceConfig serviceConfig = DefaultServiceConfig.Builder.newBuilder()
                .baseUrl(registrationUrl)
                .serializer(new JacksonSerializer())
                .build();

        DiscoveryServiceHttpClient discoveryServiceHttpClient = new DiscoveryServiceHttpClient(serviceConfig);

        final DefaultRegistrationConfig registrationConfig = DefaultRegistrationConfig.Builder.newBuilder()
                .initialDelay(initialDelay)
                .period(period)
                .dataDir(ServicesHelper.getCanonicalServiceHome(name))
                .maxRetries(retries)
                .waitSeconds(retryWaitSeconds)
                .build();

        ZeroDepRegistrationManager
                registrationManager = new ZeroDepRegistrationManager(discoveryServiceHttpClient, serviceTarget, registrationConfig, new JacksonDeserializer());
        registrationManager.start();

        return registrationManager;
    }

    @Override
    public Integer call() throws Exception {
        RegistrationManager registry = newRegistrationManager();
        try {

            final ServerBuilder<?> serverBuilder = Grpc.newServerBuilderForPort(grpcPort, InsecureServerCredentials.create());
            final Server server = serverBuilder.addService(new AppTool())
                    .addService(new ProvisionBase(name))
                    .build();

            server.start();
            server.awaitTermination();
        } finally {
            registry.deregister();
        }

        return 0;
    }
}