package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import java.util.Objects;
import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;
import ai.wanaku.core.exchange.HealthProbeGrpc;
import ai.wanaku.core.exchange.HealthProbeReply;
import ai.wanaku.core.exchange.HealthProbeRequest;
import ai.wanaku.core.exchange.RuntimeStatus;

public class CamelHealthProbe extends HealthProbeGrpc.HealthProbeImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(CamelHealthProbe.class);

    private final CamelContext camelContext;
    private final ServiceTarget target;

    public CamelHealthProbe(CamelContext camelContext, ServiceTarget target) {
        this.camelContext = camelContext;
        this.target = Objects.requireNonNull(target);

        if (target.getId().isEmpty()) {
            throw new WanakuException("Invalid capability service ID");
        }
    }

    public RuntimeStatus getStatus(ServiceStatus serviceStatus) {
        switch (serviceStatus) {
            case Initializing, Initialized, Starting -> {
                return RuntimeStatus.STARTING;
            }
            case Started -> {
                return RuntimeStatus.STARTED;
            }
            default -> {
                return RuntimeStatus.STOPPED;
            }
        }
    }

    @Override
    public void getStatus(HealthProbeRequest request, StreamObserver<HealthProbeReply> responseObserver) {
        if (!target.getId().equals(request.getId())) {
            LOG.error("Requested capability ID ({}) doesn't match exiting one {}", request.getId(), target.getId());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid request id " + request.getId())
                    .asRuntimeException());
        } else {
            RuntimeStatus status = getStatus(camelContext.getStatus());
            responseObserver.onNext(
                    HealthProbeReply.newBuilder().setStatus(status).build());
            responseObserver.onCompleted();
        }
    }
}
