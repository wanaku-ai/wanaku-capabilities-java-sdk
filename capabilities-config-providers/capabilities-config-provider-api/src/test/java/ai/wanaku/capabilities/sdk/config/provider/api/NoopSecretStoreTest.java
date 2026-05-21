package ai.wanaku.capabilities.sdk.config.provider.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoopSecretStoreTest {

    @Test
    void implementsSecretStore() {
        NoopSecretStore store = new NoopSecretStore();
        assertInstanceOf(SecretStore.class, store);
        assertInstanceOf(ConfigStore.class, store);
    }

    @Test
    void getEntries_returnsEmptyMap() {
        NoopSecretStore store = new NoopSecretStore();
        assertTrue(store.getEntries().isEmpty());
    }

    @Test
    void get_returnsEmptyString() {
        NoopSecretStore store = new NoopSecretStore();
        assertEquals("", store.get("secret.key"));
    }
}
