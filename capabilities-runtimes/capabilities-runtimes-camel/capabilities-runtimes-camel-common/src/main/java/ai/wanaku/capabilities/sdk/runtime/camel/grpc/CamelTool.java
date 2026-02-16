package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.capabilities.sdk.runtime.camel.model.McpSpec;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools.mapping.HeaderMapper;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools.mapping.HeaderMapperFactory;
import ai.wanaku.core.exchange.v1.ToolInvokeReply;
import ai.wanaku.core.exchange.v1.ToolInvokeRequest;
import ai.wanaku.core.exchange.v1.ToolInvokerGrpc;

public class CamelTool extends ToolInvokerGrpc.ToolInvokerImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(CamelTool.class);

    private final McpSpec mcpSpec;
    private final CamelContext camelContext;

    public CamelTool(CamelContext camelContext, McpSpec spec) {
        this.camelContext = camelContext;
        this.mcpSpec = spec;
    }

    public Map<String, Definition> getTools(McpSpec mcpSpec) {
        if (mcpSpec == null || mcpSpec.getMcp() == null || mcpSpec.getMcp().getTools() == null) {
            return Collections.emptyMap();
        }
        return mcpSpec.getMcp().getTools().getDefinitions();
    }

    @Override
    public void invokeTool(ToolInvokeRequest request, StreamObserver<ToolInvokeReply> responseObserver) {
        LOG.debug("About to run a Camel route as tool");

        final String uri = request.getUri();
        final URI routeUri = URI.create(uri);
        final String host = routeUri.getHost();

        // Try to find the definition in tools first, then in resources
        Map<String, Definition> tools = getTools(mcpSpec);
        Definition toolDefinition = tools.get(host);

        if (toolDefinition == null) {
            LOG.error("No tool definition found for: {}", host);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No tool or resource definition found for: " + host)
                    .asRuntimeException());
            return;
        }

        final String endpointUri = resolveEndpoint(toolDefinition, camelContext);

        try (ProducerTemplate producerTemplate = camelContext.createProducerTemplate()) {
            final Object reply;

            if (!request.getArgumentsMap().isEmpty()) {
                final Map<String, Object> headers = extractHeaderParameters(request, toolDefinition);

                reply = producerTemplate.requestBodyAndHeaders(endpointUri, request.getBody(), headers);
            } else {
                reply = producerTemplate.requestBody(endpointUri, request.getBody());
            }

            responseObserver.onNext(ToolInvokeReply.newBuilder()
                    .addAllContent(List.of(reply.toString()))
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            reportRouteFailure(responseObserver, e, toolDefinition);
        }
    }

    private static void reportRouteFailure(
            StreamObserver<ToolInvokeReply> responseObserver, Exception e, Definition toolDefinition) {
        final String routeId = toolDefinition.getRoute().getId();

        if (LOG.isDebugEnabled()) {
            LOG.error("Camel route {} could not be invoked: {}", routeId, e.getMessage(), e);
        } else {
            LOG.error("Camel route {} could not be invoked: {}", routeId, e.getMessage());
        }

        responseObserver.onError(Status.INTERNAL
                .withDescription(String.format("Unable to invoke tool: %s", e.getMessage()))
                .asRuntimeException());
    }

    private static String resolveEndpoint(Definition toolDefinition, CamelContext camelContext) {
        final String routeId = toolDefinition.getRoute().getId();
        final Route route = camelContext.getRoute(routeId);
        return route.getEndpoint().getEndpointUri();
    }

    private static Map<String, Object> extractHeaderParameters(ToolInvokeRequest request, Definition toolDefinition) {
        HeaderMapper headerMapper = HeaderMapperFactory.create(toolDefinition);

        return headerMapper.map(request, toolDefinition);
    }
}
