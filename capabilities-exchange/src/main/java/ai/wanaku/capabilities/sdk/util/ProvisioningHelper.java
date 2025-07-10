package ai.wanaku.capabilities.sdk.util;

import ai.wanaku.core.config.provider.api.ConfigProvisioner;
import ai.wanaku.core.config.provider.api.ProvisionedConfig;
import ai.wanaku.core.exchange.Configuration;
import ai.wanaku.core.exchange.ProvisionRequest;
import ai.wanaku.core.exchange.Secret;
import java.net.URI;

public final class ProvisioningHelper {

    private ProvisioningHelper() {}

    public static ProvisionedConfig provision(ProvisionRequest request, ConfigProvisioner provisioner) {
        final Configuration configuration = request.getConfiguration();
        final Secret secret = request.getSecret();

        final URI configurationUri = provisioner.provisionConfiguration(request.getUri(), configuration.getPayload());
        final URI secretUri = provisioner.provisionSecret(request.getUri(), secret.getPayload());

        return new ProvisionedConfig(configurationUri, secretUri);
    }
}
