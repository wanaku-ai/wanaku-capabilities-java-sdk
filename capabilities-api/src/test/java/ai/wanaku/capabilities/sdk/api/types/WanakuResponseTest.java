package ai.wanaku.capabilities.sdk.api.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class WanakuResponseTest {

    @Test
    void noArgConstructor() {
        WanakuResponse<String> response = new WanakuResponse<>();
        assertNull(response.error());
        assertNull(response.data());
    }

    @Test
    void errorStringConstructor() {
        WanakuResponse<String> response = new WanakuResponse<>("something went wrong");
        assertNotNull(response.error());
        assertEquals("something went wrong", response.error().message());
        assertNull(response.data());
    }

    @Test
    void dataConstructor() {
        WanakuResponse<Integer> response = new WanakuResponse<>(42);
        assertNull(response.error());
        assertEquals(42, response.data());
    }

    @Test
    void canonicalConstructor() {
        WanakuError error = new WanakuError("err");
        WanakuResponse<Integer> response = new WanakuResponse<>(error, 42);
        assertEquals(error, response.error());
        assertEquals(42, response.data());
    }
}
