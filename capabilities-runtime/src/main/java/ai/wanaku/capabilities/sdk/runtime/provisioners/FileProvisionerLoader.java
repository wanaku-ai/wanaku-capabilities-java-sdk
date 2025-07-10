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

public final class FileProvisionerLoader {

    private FileProvisionerLoader() {}

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
