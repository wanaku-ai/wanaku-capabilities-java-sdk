package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.capabilities.sdk.runtime.camel.model.McpSpec;
import ai.wanaku.core.exchange.ResourceAcquirerGrpc;
import ai.wanaku.core.exchange.ResourceReply;
import ai.wanaku.core.exchange.ResourceRequest;
import io.grpc.stub.StreamObserver;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelResource extends ResourceAcquirerGrpc.ResourceAcquirerImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(CamelResource.class);

    private final McpSpec mcpSpec;
    private final CamelContext camelContext;

    public CamelResource(CamelContext camelContext, McpSpec mcpSpec) {
        this.camelContext = camelContext;
        this.mcpSpec = mcpSpec;
    }

    public Map<String, Definition> getResources(McpSpec mcpSpec) {
        if (mcpSpec == null || mcpSpec.getMcp() == null || mcpSpec.getMcp().getResources() == null) {
            return Collections.emptyMap();
        }
        return mcpSpec.getMcp().getResources().getDefinitions();
    }

    @Override
    public void resourceAcquire(ResourceRequest request, StreamObserver<ResourceReply> responseObserver) {
        LOG.debug("About to run a Camel route as resource provider");

        final String uri = request.getLocation();
        URI routeUri = URI.create(uri);
        final String host = routeUri.getHost();

        Map<String, Definition> resources = getResources(mcpSpec);
        Definition definition = resources.get(host);

        if (definition == null) {
            LOG.error("No resource definition found for: {}", host);
            responseObserver.onNext(ResourceReply.newBuilder()
                    .setIsError(true)
                    .addAllContent(List.of("No resource definition found for: " + host))
                    .build());
            responseObserver.onCompleted();
            return;
        }

        try (final ConsumerTemplate consumerTemplate = camelContext.createConsumerTemplate()) {
            final String endpointUri = resolveEndpoint(definition, camelContext);
            LOG.info("Consuming from {} as {}", endpointUri, routeUri);

            Object ret = consumerTemplate.receiveBody(endpointUri, 5000, String.class);
            if (ret != null) {
                responseObserver.onNext(ResourceReply.newBuilder()
                        .setIsError(false)
                        .addAllContent(List.of(ret.toString()))
                        .build());
            } else {
                responseObserver.onNext(ResourceReply.newBuilder()
                        .setIsError(true)
                        .addAllContent(List.of("No response for the requested resource call"))
                        .build());
            }
        } catch (Exception e) {
            reportRouteFailure(responseObserver, e, definition);
        } finally {
            responseObserver.onCompleted();
        }
    }

    private static String resolveEndpoint(Definition definition, CamelContext camelContext) {
        final String routeId = definition.getRoute().getId();
        final Route route = camelContext.getRoute(routeId);
        return route.getEndpoint().getEndpointUri();
    }

    private static void reportRouteFailure(
            StreamObserver<ResourceReply> responseObserver, Exception e, Definition definition) {
        final String routeId = definition.getRoute().getId();

        if (LOG.isDebugEnabled()) {
            LOG.error("Camel route {} did not produce a result: {}", routeId, e.getMessage(), e);
        } else {
            LOG.error("Camel route {} did not produce a result: {}", routeId, e.getMessage());
        }

        responseObserver.onNext(ResourceReply.newBuilder()
                .setIsError(true)
                .addAllContent(List.of(String.format("Unable to acquire resource: %s", e.getMessage())))
                .build());
    }
}
