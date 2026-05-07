package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import java.util.concurrent.Future;
import org.apache.camel.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ai.wanaku.core.exchange.v1.HealthProbeGrpc;
import ai.wanaku.core.exchange.v1.HealthProbeReply;
import ai.wanaku.core.exchange.v1.HealthProbeRequest;
import ai.wanaku.core.exchange.v1.RuntimeStatus;

public class CamelHealthProbe extends HealthProbeGrpc.HealthProbeImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(CamelHealthProbe.class);

    private final WanakuRegistrationInfoResolver registrationInfoResolver;

    public CamelHealthProbe(Future<WanakuRegistrationInfo> registrationInfoFuture) {
        this.registrationInfoResolver = new WanakuRegistrationInfoResolver(registrationInfoFuture);
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
        final WanakuRegistrationInfo info;
        try {
            info = registrationInfoResolver.resolve();
        } catch (Exception e) {
            responseObserver.onError(e);
            return;
        }

        if (!info.serviceTarget().getId().equals(request.getId())) {
            LOG.error(
                    "Requested capability ID ({}) doesn't match existing one {}",
                    request.getId(),
                    info.serviceTarget().getId());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request id " + request.getId())
                    .asRuntimeException());
        } else {
            RuntimeStatus status = getStatus(info.camelContext().getStatus());
            responseObserver.onNext(
                    HealthProbeReply.newBuilder().setStatus(status).build());
            responseObserver.onCompleted();
        }
    }
}
