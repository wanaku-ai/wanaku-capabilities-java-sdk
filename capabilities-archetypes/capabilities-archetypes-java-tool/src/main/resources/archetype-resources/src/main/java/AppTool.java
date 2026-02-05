package ${package};

import ai.wanaku.core.exchange.ToolInvokeReply;
import ai.wanaku.core.exchange.ToolInvokeRequest;
import ai.wanaku.core.exchange.ToolInvokerGrpc;
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
                            .setIsError(false)
                            .addAllContent(List.of(response.toString())).build());

            responseObserver.onCompleted();
        } finally {
            // cleanup
        }
    }
}