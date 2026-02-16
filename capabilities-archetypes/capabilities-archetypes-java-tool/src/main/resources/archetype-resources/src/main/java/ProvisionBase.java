package ${package};

import ai.wanaku.capabilities.sdk.config.provider.api.ConfigProvisioner;
import ai.wanaku.capabilities.sdk.config.provider.api.ProvisionedConfig;
import ai.wanaku.capabilities.sdk.runtime.provisioners.FileProvisionerLoader;
import ai.wanaku.capabilities.sdk.util.ProvisioningHelper;
import ai.wanaku.core.exchange.v1.PropertySchema;
import ai.wanaku.core.exchange.v1.ProvisionReply;
import ai.wanaku.core.exchange.v1.ProvisionRequest;
import ai.wanaku.core.exchange.v1.ProvisionerGrpc;
import io.grpc.stub.StreamObserver;
import java.util.Map;

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
        // Use this to push any server-side properties/arguments. Use the toPropertySchema to serialize their additional details
        // Map.of("argument-name", toPropertySchema("The description for what argument-name is", "string", true));
        return Map.of();
    }

    /*
     * You can use this to serialize the server-side properties to push.
     */
    private static PropertySchema toPropertySchema(String description, String type, boolean required) {
        return PropertySchema.newBuilder()
                .setDescription(description)
                .setType(type)
                .setRequired(required)
                .build();
    }
}
