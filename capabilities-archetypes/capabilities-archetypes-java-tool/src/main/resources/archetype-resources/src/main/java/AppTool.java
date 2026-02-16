package ${package};

import ai.wanaku.core.exchange.v1.ToolInvokeReply;
import ai.wanaku.core.exchange.v1.ToolInvokeRequest;
import ai.wanaku.core.exchange.v1.ToolInvokerGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Map;

public class AppTool extends ToolInvokerGrpc.ToolInvokerImplBase {

    public void invokeTool(ToolInvokeRequest request, StreamObserver<ToolInvokeReply> responseObserver) {

        try {
            // Access tool arguments
            Map<String, String> args = request.getArgumentsMap();

            // Access metadata headers (from wanaku_meta_* prefixed arguments)
            // Example: wanaku_meta_contextId becomes accessible as "contextId"
            Map<String, String> headers = request.getHeadersMap();

            // Access request body (from wanaku_body argument)
            String body = request.getBody();

            // Here, write the code to actually invoke the tool, then set whatever is returned as the
            // response object (i.e; Object response = myToolCall();)
            Object response = null;

            // Build the response
            responseObserver.onNext(
                    ToolInvokeReply.newBuilder()
                            .addAllContent(List.of(response.toString())).build());

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(String.format("Unable to invoke tool: %s", e.getMessage()))
                    .asRuntimeException());
        }
    }
}
