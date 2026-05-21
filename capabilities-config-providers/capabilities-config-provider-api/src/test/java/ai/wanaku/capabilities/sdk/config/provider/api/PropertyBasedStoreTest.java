package ai.wanaku.capabilities.sdk.config.provider.api;

import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyBasedStoreTest {

    private PropertyBasedStore store;

    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        props.setProperty("db.host", "localhost");
        props.setProperty("db.port", "5432");
        props.setProperty("app.name", "wanaku");

        store = new PropertyBasedStore(() -> props) {};
    }

    @Test
    void getEntries_returnsAll() {
        Map<String, String> entries = store.getEntries();
        assertEquals(3, entries.size());
        assertEquals("localhost", entries.get("db.host"));
        assertEquals("5432", entries.get("db.port"));
        assertEquals("wanaku", entries.get("app.name"));
    }

    @Test
    void getEntries_withPrefix() {
        Map<String, String> entries = store.getEntries("db.");
        assertEquals(2, entries.size());
        assertEquals("localhost", entries.get("db.host"));
        assertEquals("5432", entries.get("db.port"));
    }

    @Test
    void getEntries_withPrefix_noMatch() {
        Map<String, String> entries = store.getEntries("cache.");
        assertTrue(entries.isEmpty());
    }

    @Test
    void get_existingKey() {
        assertEquals("localhost", store.get("db.host"));
    }

    @Test
    void get_nonExistingKey() {
        assertNull(store.get("missing.key"));
    }
}
