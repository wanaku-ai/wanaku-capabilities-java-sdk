package ai.test;

import ai.wanaku.core.exchange.ToolInvokeReply;
import ai.wanaku.core.exchange.ToolInvokeRequest;
import ai.wanaku.core.exchange.ToolInvokerGrpc;
import io.grpc.stub.StreamObserver;
import java.util.List;

public class AppTool extends ToolInvokerGrpc.ToolInvokerImplBase {

    public void invokeTool(ToolInvokeRequest request, StreamObserver<ToolInvokeReply> responseObserver) {

        try {
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