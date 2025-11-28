package ai.wanaku.capabilities.sdk.config.provider.api;

import java.util.Map;

/**
 * A no-operation implementation of {@link ConfigStore} that returns empty results.
 * <p>
 * This implementation is useful as a default or placeholder when no actual
 * configuration storage is available or needed. All methods return empty
 * maps or empty strings rather than null values.
 */
public class NoopConfigStore implements ConfigStore {
    @Override
    public Map<String, String> getEntries() {
        return Map.of();
    }

    @Override
    public Map<String, String> getEntries(String prefix) {
        return Map.of();
    }

    @Override
    public String get(String name) {
        return "";
    }
}
