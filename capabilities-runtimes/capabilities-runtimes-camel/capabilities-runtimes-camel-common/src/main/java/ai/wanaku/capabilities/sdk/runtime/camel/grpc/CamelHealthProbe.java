package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import java.util.Objects;
import java.util.concurrent.Future;
import org.apache.camel.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;
import ai.wanaku.core.exchange.v1.HealthProbeGrpc;
import ai.wanaku.core.exchange.v1.HealthProbeReply;
import ai.wanaku.core.exchange.v1.HealthProbeRequest;
import ai.wanaku.core.exchange.v1.RuntimeStatus;

public class CamelHealthProbe extends HealthProbeGrpc.HealthProbeImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(CamelHealthProbe.class);

    private final CamelContextResolver contextResolver;
    private final ServiceTarget target;

    public CamelHealthProbe(Future<WanakuRegistrationInfo> registrationInfoFuture, ServiceTarget target) {
        this.contextResolver = new CamelContextResolver(registrationInfoFuture);
        this.target = Objects.requireNonNull(target);
    }

    public RuntimeStatus getStatus(ServiceStatus serviceStatus) {
        switch (serviceStatus) {
            case Initializing, Initialized, Starting -> {
                return RuntimeStatus.RUNTIME_STATUS_STARTING;
            }
            case Started -> {
                return RuntimeStatus.RUNTIME_STATUS_STARTED;
            }
            default -> {
                return RuntimeStatus.RUNTIME_STATUS_STOPPED;
            }
        }
    }

    @Override
    public void getStatus(HealthProbeRequest request, StreamObserver<HealthProbeReply> responseObserver) {
        if (target.getId() == null || target.getId().isEmpty()) {
            responseObserver.onError(Status.UNAVAILABLE
                    .withDescription("Service is not yet registered")
                    .asRuntimeException());
            return;
        }

        if (!target.getId().equals(request.getId())) {
            LOG.error("Requested capability ID ({}) doesn't match exiting one {}", request.getId(), target.getId());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request id " + request.getId())
                    .asRuntimeException());
        } else {
            final WanakuRegistrationInfo info;
            try {
                info = contextResolver.resolve();
            } catch (Exception e) {
                responseObserver.onError(e);
                return;
            }

            RuntimeStatus status = getStatus(info.camelContext().getStatus());
            responseObserver.onNext(
                    HealthProbeReply.newBuilder().setStatus(status).build());
            responseObserver.onCompleted();
        }
    }
}
