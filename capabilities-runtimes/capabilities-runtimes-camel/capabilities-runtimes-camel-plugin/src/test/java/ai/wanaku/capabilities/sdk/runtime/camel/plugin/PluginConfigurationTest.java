package ai.wanaku.capabilities.sdk.runtime.camel.plugin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PluginConfigurationTest {

    @Test
    void load_defaults() {
        PluginConfiguration config = PluginConfiguration.load();
        assertEquals(9190, config.getGrpcPort());
        assertEquals("auto", config.getRegistrationAnnounceAddress());
        assertEquals("camel", config.getServiceName());
        assertEquals("/tmp", config.getDataDir());
        assertEquals(12, config.getRetries());
        assertEquals(5, config.getRetryWaitSeconds());
        assertEquals(5, config.getInitialDelay());
        assertEquals(5, config.getPeriod());
        assertFalse(config.isNoWait());
        assertFalse(config.isPingEnabled());
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
