package ai.wanaku.capabilities.sdk.api.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class WanakuExceptionTest {

    @Test
    void noArgConstructor() {
        WanakuException ex = new WanakuException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void messageConstructor() {
        WanakuException ex = new WanakuException("error");
        assertEquals("error", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void messageAndCauseConstructor() {
        RuntimeException cause = new RuntimeException("root");
        WanakuException ex = new WanakuException("error", cause);
        assertEquals("error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void causeConstructor() {
        RuntimeException cause = new RuntimeException("root");
        WanakuException ex = new WanakuException(cause);
        assertEquals(cause, ex.getCause());
    }

    @Test
    void fullConstructor() {
        RuntimeException cause = new RuntimeException("root");
        WanakuException ex = new WanakuException("error", cause, true, true);
        assertEquals("error", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertNotNull(ex.getStackTrace());
    }

    @Test
    void isRuntimeException() {
        WanakuException ex = new WanakuException("test");
        assertNotNull(ex);
        assertEquals(RuntimeException.class, ex.getClass().getSuperclass());
    }
}
