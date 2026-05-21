package ai.wanaku.capabilities.sdk.api.types.execution;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeExecutionRequestTest {

    @Test
    void defaultConstructor_setsDefaults() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        assertEquals(CodeExecutionRequest.DEFAULT_TIMEOUT_MS, request.getTimeout());
        assertNotNull(request.getEnvironment());
        assertTrue(request.getEnvironment().isEmpty());
        assertNotNull(request.getArguments());
        assertTrue(request.getArguments().isEmpty());
        assertNotNull(request.getMetadata());
        assertTrue(request.getMetadata().isEmpty());
    }

    @Test
    void codeConstructor_setsCode() {
        CodeExecutionRequest request = new CodeExecutionRequest("print('hello')");
        assertEquals("print('hello')", request.getCode());
        assertEquals(CodeExecutionRequest.DEFAULT_TIMEOUT_MS, request.getTimeout());
    }

    @Test
    void setCode_nullThrows() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        assertThrows(IllegalArgumentException.class, () -> request.setCode(null));
    }

    @Test
    void setCode_emptyThrows() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        assertThrows(IllegalArgumentException.class, () -> request.setCode("   "));
    }

    @Test
    void setCode_oversizedThrows() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        String oversized = "x".repeat(CodeExecutionRequest.MAX_CODE_SIZE_BYTES + 1);
        assertThrows(IllegalArgumentException.class, () -> request.setCode(oversized));
    }

    @Test
    void setTimeout_belowMinThrows() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        assertThrows(IllegalArgumentException.class, () -> request.setTimeout(0L));
    }

    @Test
    void setTimeout_aboveMaxThrows() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        assertThrows(IllegalArgumentException.class, () -> request.setTimeout(CodeExecutionRequest.MAX_TIMEOUT_MS + 1));
    }

    @Test
    void setTimeout_nullIsAllowed() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setTimeout(null);
        assertEquals(null, request.getTimeout());
    }

    @Test
    void validate_validRequest() {
        CodeExecutionRequest request = new CodeExecutionRequest("code");
        request.validate();
    }

    @Test
    void validate_nullCodeThrows() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        assertThrows(IllegalArgumentException.class, request::validate);
    }

    @Test
    void setEnvironment_nullBecomesEmpty() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setEnvironment(null);
        assertNotNull(request.getEnvironment());
        assertTrue(request.getEnvironment().isEmpty());
    }

    @Test
    void setArguments_nullBecomesEmpty() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setArguments(null);
        assertNotNull(request.getArguments());
        assertTrue(request.getArguments().isEmpty());
    }

    @Test
    void setMetadata_nullBecomesEmpty() {
        CodeExecutionRequest request = new CodeExecutionRequest();
        request.setMetadata(null);
        assertNotNull(request.getMetadata());
        assertTrue(request.getMetadata().isEmpty());
    }

    @Test
    void equalsAndHashCode() {
        CodeExecutionRequest a = new CodeExecutionRequest("code");
        a.setTimeout(5000L);
        a.setEnvironment(Map.of("K", "V"));
        a.setArguments(List.of("arg1"));

        CodeExecutionRequest b = new CodeExecutionRequest("code");
        b.setTimeout(5000L);
        b.setEnvironment(Map.of("K", "V"));
        b.setArguments(List.of("arg1"));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_differentCode() {
        CodeExecutionRequest a = new CodeExecutionRequest("code1");
        CodeExecutionRequest b = new CodeExecutionRequest("code2");
        assertNotEquals(a, b);
    }
}
