package ai.wanaku.capabilities.sdk.runtime.camel.plugin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PluginConfigurationTest {

    @Test
    void load_defaults() {
        PluginConfiguration config = PluginConfiguration.load();
        assertEquals(9190, config.getGrpcPort());
        assertEquals("camel", config.getServiceName());
    }

    @Test
    void load_nullableFieldsAreNull() {
        PluginConfiguration config = PluginConfiguration.load();
        assertNull(config.getRoutesRef());
        assertNull(config.getRulesRef());
        assertNull(config.getTokenEndpoint());
        assertNull(config.getClientId());
        assertNull(config.getClientSecret());
        assertNull(config.getDependenciesRef());
        assertNull(config.getRepositoriesList());
        assertNull(config.getInitFrom());
    }

    @Test
    void validate_missingRegistrationUrl_throws() {
        PluginConfiguration config = PluginConfiguration.load();
        assertThrows(IllegalStateException.class, config::validate);
    }
}
