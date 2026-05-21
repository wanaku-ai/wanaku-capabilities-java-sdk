package ai.wanaku.capabilities.sdk.config.provider.api;

import java.net.URI;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProvisionedConfigTest {

    @Test
    void recordAccessors() {
        URI configUri = URI.create("file:///config");
        URI secretUri = URI.create("file:///secret");
        ProvisionedConfig config = new ProvisionedConfig(configUri, secretUri);

        assertEquals(configUri, config.configurationsUri());
        assertEquals(secretUri, config.secretsUri());
    }

    @Test
    void equalsAndHashCode() {
        URI configUri = URI.create("file:///config");
        URI secretUri = URI.create("file:///secret");
        ProvisionedConfig a = new ProvisionedConfig(configUri, secretUri);
        ProvisionedConfig b = new ProvisionedConfig(configUri, secretUri);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
