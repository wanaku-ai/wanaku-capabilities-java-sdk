package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.camel.CamelContext;
import io.grpc.Status;

public class CamelContextResolver {
    private final Future<CamelContext> camelContextFuture;
    private volatile CamelContext camelContext;

    public CamelContextResolver(Future<CamelContext> camelContextFuture) {
        this.camelContextFuture = camelContextFuture;
    }

    public CamelContext resolve() {
        CamelContext ctx = this.camelContext;
        if (ctx != null) {
            return ctx;
        }

        if (!camelContextFuture.isDone()) {
            throw Status.FAILED_PRECONDITION
                    .withDescription("Camel context is not yet available")
                    .asRuntimeException();
        }

        try {
            ctx = camelContextFuture.get();
            this.camelContext = ctx;
            return ctx;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw Status.UNAVAILABLE
                    .withDescription("Interrupted while obtaining Camel context")
                    .asRuntimeException();
        } catch (ExecutionException e) {
            throw Status.UNAVAILABLE
                    .withDescription("Failed to initialize Camel context: "
                            + e.getCause().getMessage())
                    .asRuntimeException();
        }
    }
}
