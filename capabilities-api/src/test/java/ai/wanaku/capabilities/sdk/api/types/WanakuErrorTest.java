package ai.wanaku.capabilities.sdk.api.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WanakuErrorTest {

    @Test
    void noArgConstructor() {
        WanakuError error = new WanakuError();
        assertNull(error.message());
    }

    @Test
    void messageConstructor() {
        WanakuError error = new WanakuError("something failed");
        assertEquals("something failed", error.message());
    }
}
