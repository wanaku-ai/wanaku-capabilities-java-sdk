package ai.wanaku.capabilities.sdk.runtime.camel.plugin;

import java.util.ServiceLoader;
import org.apache.camel.spi.ContextServicePlugin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link CamelIntegrationPlugin}.
 */
@EnabledIfEnvironmentVariable(named = "REGISTRATION_URL", matches = ".+")
class CamelIntegrationPluginTest {

    @Test
    void testImplementsContextServicePlugin() {
        CamelIntegrationPlugin plugin = new CamelIntegrationPlugin();
        assertInstanceOf(ContextServicePlugin.class, plugin);
    }

    @Test
    void testSpiDiscovery() {
        // Verify the plugin is discoverable via ServiceLoader
        ServiceLoader<ContextServicePlugin> loader = ServiceLoader.load(ContextServicePlugin.class);

        boolean found = false;
        for (ContextServicePlugin plugin : loader) {
            if (plugin instanceof CamelIntegrationPlugin) {
                found = true;
                break;
            }
        }

        assertTrue(found, "CamelIntegrationPlugin should be discoverable via SPI");
    }

    @Test
    void testLoadFailsWithoutConfiguration() {
        CamelIntegrationPlugin plugin = new CamelIntegrationPlugin();

        // Without required configuration (env vars or properties), load should fail
        RuntimeException exception = assertThrows(RuntimeException.class, () -> plugin.load(null));
        assertTrue(
                exception.getMessage().contains("Failed to initialize")
                        || exception.getCause() instanceof IllegalStateException,
                "Expected initialization failure due to missing configuration");
    }

    @Test
    void testUnloadWithoutLoad() {
        CamelIntegrationPlugin plugin = new CamelIntegrationPlugin();

        // unload without load should not throw
        assertDoesNotThrow(() -> plugin.unload(null));
    }
}
