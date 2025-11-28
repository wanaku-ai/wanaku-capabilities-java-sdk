package ai.wanaku.capabilities.sdk.util;

import ai.wanaku.capabilities.sdk.config.provider.api.ConfigProvisioner;
import ai.wanaku.capabilities.sdk.config.provider.api.ProvisionedConfig;
import ai.wanaku.core.exchange.Configuration;
import ai.wanaku.core.exchange.ProvisionRequest;
import ai.wanaku.core.exchange.Secret;
import java.net.URI;

/**
 * Helper class for provisioning configurations and secrets.
 */
public final class ProvisioningHelper {

    private ProvisioningHelper() {}

    /**
     * Provisions a configuration and a secret based on the provided request and provisioner.
     *
     * @param request The {@link ProvisionRequest} containing the configuration and secret payloads.
     * @param provisioner The {@link ConfigProvisioner} used to provision the configuration and secret.
     * @return A {@link ProvisionedConfig} containing the URIs of the provisioned configuration and secret.
     */
    public static ProvisionedConfig provision(ProvisionRequest request, ConfigProvisioner provisioner) {
        final Configuration configuration = request.getConfiguration();
        final Secret secret = request.getSecret();

        final URI configurationUri = provisioner.provisionConfiguration(request.getUri(), configuration.getPayload());
        final URI secretUri = provisioner.provisionSecret(request.getUri(), secret.getPayload());

        return new ProvisionedConfig(configurationUri, secretUri);
    }
}
