package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import io.grpc.Status;

public class WanakuRegistrationInfoResolver {
    private final Future<WanakuRegistrationInfo> registrationInfoFuture;
    private volatile WanakuRegistrationInfo registrationInfo;

    public WanakuRegistrationInfoResolver(Future<WanakuRegistrationInfo> registrationInfoFuture) {
        this.registrationInfoFuture = registrationInfoFuture;
    }

    public WanakuRegistrationInfo resolve() {
        WanakuRegistrationInfo info = this.registrationInfo;
        if (info != null) {
            return info;
        }

        if (!registrationInfoFuture.isDone()) {
            throw Status.FAILED_PRECONDITION
                    .withDescription("Camel context is not yet available")
                    .asRuntimeException();
        }

        try {
            info = registrationInfoFuture.get();
            this.registrationInfo = info;
            return info;
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
