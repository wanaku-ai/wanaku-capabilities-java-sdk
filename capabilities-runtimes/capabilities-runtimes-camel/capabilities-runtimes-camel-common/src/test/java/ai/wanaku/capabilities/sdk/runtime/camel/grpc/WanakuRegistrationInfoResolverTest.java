package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import java.util.concurrent.CompletableFuture;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WanakuRegistrationInfoResolverTest {

    @Test
    void resolveThrowsFailedPreconditionWhenFutureNotDone() {
        CompletableFuture<WanakuRegistrationInfo> future = new CompletableFuture<>();
        WanakuRegistrationInfoResolver resolver = new WanakuRegistrationInfoResolver(future);

        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, resolver::resolve);
        assertEquals(Status.FAILED_PRECONDITION.getCode(), ex.getStatus().getCode());
    }

    @Test
    void resolveReturnsInfoWhenContextNotYetStarted() {
        CamelContext context = new DefaultCamelContext();
        WanakuRegistrationInfo info = new WanakuRegistrationInfo(context, null, null);
        CompletableFuture<WanakuRegistrationInfo> future = CompletableFuture.completedFuture(info);
        WanakuRegistrationInfoResolver resolver = new WanakuRegistrationInfoResolver(future);

        assertNotNull(resolver.resolve());
    }

    @Test
    void resolveSucceedsWhenContextStarted() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        try {
            WanakuRegistrationInfo info = new WanakuRegistrationInfo(context, null, null);
            CompletableFuture<WanakuRegistrationInfo> future = CompletableFuture.completedFuture(info);
            WanakuRegistrationInfoResolver resolver = new WanakuRegistrationInfoResolver(future);

            assertNotNull(resolver.resolve());
        } finally {
            context.stop();
        }
    }

    @Test
    void resolveReturnsCachedInstanceOnSubsequentCalls() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();
        try {
            WanakuRegistrationInfo info = new WanakuRegistrationInfo(context, null, null);
            CompletableFuture<WanakuRegistrationInfo> future = CompletableFuture.completedFuture(info);
            WanakuRegistrationInfoResolver resolver = new WanakuRegistrationInfoResolver(future);

            WanakuRegistrationInfo first = resolver.resolve();
            WanakuRegistrationInfo second = resolver.resolve();
            assertSame(first, second);
        } finally {
            context.stop();
        }
    }
}
