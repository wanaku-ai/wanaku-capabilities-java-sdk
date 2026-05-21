package ai.wanaku.capabilities.sdk.config.provider.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservedConfigsTest {

    @Test
    void queryParametersPrefix() {
        assertEquals("query.", ReservedConfigs.CONFIG_QUERY_PARAMETERS_PREFIX);
    }

    @Test
    void headerParametersPrefix() {
        assertEquals("header.", ReservedConfigs.CONFIG_HEADER_PARAMETERS_PREFIX);
    }
}
