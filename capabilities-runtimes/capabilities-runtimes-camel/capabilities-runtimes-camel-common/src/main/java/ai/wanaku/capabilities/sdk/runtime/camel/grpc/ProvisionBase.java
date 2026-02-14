package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import java.util.Map;
import io.grpc.stub.StreamObserver;
import ai.wanaku.capabilities.sdk.config.provider.api.ConfigProvisioner;
import ai.wanaku.capabilities.sdk.config.provider.api.ProvisionedConfig;
import ai.wanaku.capabilities.sdk.runtime.provisioners.FileProvisionerLoader;
import ai.wanaku.capabilities.sdk.util.ProvisioningHelper;
import ai.wanaku.core.exchange.PropertySchema;
import ai.wanaku.core.exchange.ProvisionReply;
import ai.wanaku.core.exchange.ProvisionRequest;
import ai.wanaku.core.exchange.ProvisionerGrpc;

public class ProvisionBase extends ProvisionerGrpc.ProvisionerImplBase {

    private final String name;

    public ProvisionBase(String name) {
        this.name = name;
    }

    @Override
    public void provision(ProvisionRequest request, StreamObserver<ProvisionReply> responseObserver) {

        ConfigProvisioner provisioner = FileProvisionerLoader.newConfigProvisioner(request, name);
        final ProvisionedConfig provision = ProvisioningHelper.provision(request, provisioner);

        responseObserver.onNext(ProvisionReply.newBuilder()
                .putAllProperties(properties())
                .setConfigurationUri(provision.configurationsUri().toString())
                .setSecretUri(provision.secretsUri().toString())
                .build());
        responseObserver.onCompleted();
    }

    public Map<String, PropertySchema> properties() {
        return Map.of();
    }
}
