package ai.wanaku.capabilities.sdk.config.provider.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoopConfigStoreTest {

    @Test
    void getEntries_returnsEmptyMap() {
        NoopConfigStore store = new NoopConfigStore();
        assertNotNull(store.getEntries());
        assertTrue(store.getEntries().isEmpty());
    }

    @Test
    void getEntries_withPrefix_returnsEmptyMap() {
        NoopConfigStore store = new NoopConfigStore();
        assertNotNull(store.getEntries("any.prefix"));
        assertTrue(store.getEntries("any.prefix").isEmpty());
    }

    @Test
    void get_returnsEmptyString() {
        NoopConfigStore store = new NoopConfigStore();
        assertEquals("", store.get("any.key"));
    }
}
