package ai.wanaku.capabilities.sdk.runtime.provisioners;

import ai.wanaku.capabilities.sdk.common.ServicesHelper;
import ai.wanaku.core.config.provider.api.ConfigProvisioner;
import ai.wanaku.core.config.provider.api.ConfigWriter;
import ai.wanaku.core.config.provider.api.DefaultConfigProvisioner;
import ai.wanaku.core.config.provider.api.SecretWriter;
import ai.wanaku.core.config.provider.file.FileConfigurationWriter;
import ai.wanaku.core.config.provider.file.FileSecretWriter;
import ai.wanaku.core.exchange.Configuration;
import ai.wanaku.core.exchange.PayloadType;
import ai.wanaku.core.exchange.ProvisionRequest;
import ai.wanaku.core.exchange.Secret;
import java.io.File;

import static ai.wanaku.capabilities.sdk.data.files.util.DataFileHelper.newRandomizedDataFile;

/**
 * Factory class for creating file-based configuration provisioners.
 * Handles loading and setup of provisioners that write configurations and secrets to files.
 */
public final class FileProvisionerLoader {

    /**
     * Private constructor to prevent instantiation.
     */
    private FileProvisionerLoader() {}

    /**
     * Creates a new file-based configuration provisioner based on the provision request.
     *
     * @param request The provision request containing configuration and secret details.
     * @param name The service name used to determine the storage location.
     * @return A configured {@link ConfigProvisioner} instance.
     * @throws UnsupportedOperationException If the payload type is not supported.
     */
    public static ConfigProvisioner newConfigProvisioner(ProvisionRequest request, String name) {
        final Configuration configuration = request.getConfiguration();
        final Secret secret = request.getSecret();
        ConfigWriter configurationWriter;
        SecretWriter secretWriter;

        final String serviceHome = ServicesHelper.getCanonicalServiceHome(name);

        if (configuration.getType() == PayloadType.BUILTIN) {
            final File dataFile = newRandomizedDataFile(serviceHome);

            configurationWriter = new FileConfigurationWriter(dataFile);
        } else {
            throw new UnsupportedOperationException("Provisioner not supported yet.");
        }

        if (secret.getType() == PayloadType.BUILTIN) {
            final File dataFile = newRandomizedDataFile(serviceHome);

            secretWriter = new FileSecretWriter(dataFile);
        } else {
            throw new UnsupportedOperationException("Provisioner not supported yet.");
        }

        return new DefaultConfigProvisioner(configurationWriter, secretWriter);
    }
}
