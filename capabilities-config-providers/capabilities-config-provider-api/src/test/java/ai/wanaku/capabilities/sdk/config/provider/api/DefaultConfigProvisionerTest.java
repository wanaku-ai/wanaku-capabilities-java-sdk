package ai.wanaku.capabilities.sdk.config.provider.api;

import java.net.URI;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultConfigProvisionerTest {

    @Test
    void provisionConfiguration_delegatesToConfigWriter() {
        URI expectedUri = URI.create("file:///config/data");
        ConfigWriter configWriter = (id, data) -> expectedUri;
        SecretWriter secretWriter = (id, data) -> URI.create("file:///unused");

        DefaultConfigProvisioner provisioner = new DefaultConfigProvisioner(configWriter, secretWriter);
        URI result = provisioner.provisionConfiguration("id-1", "payload");

        assertEquals(expectedUri, result);
    }

    @Test
    void provisionSecret_delegatesToSecretWriter() {
        URI expectedUri = URI.create("file:///secrets/data");
        ConfigWriter configWriter = (id, data) -> URI.create("file:///unused");
        SecretWriter secretWriter = (id, data) -> expectedUri;

        DefaultConfigProvisioner provisioner = new DefaultConfigProvisioner(configWriter, secretWriter);
        URI result = provisioner.provisionSecret("id-1", "secret-payload");

        assertEquals(expectedUri, result);
    }
}
