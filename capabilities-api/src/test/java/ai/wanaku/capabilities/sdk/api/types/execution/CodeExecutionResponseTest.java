package ai.wanaku.capabilities.sdk.api.types.execution;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeExecutionResponseTest {

    @Test
    void validConstruction() {
        CodeExecutionResponse response =
                new CodeExecutionResponse("task-1", "http://host/stream", CodeExecutionStatus.PENDING, 1000L);
        assertEquals("task-1", response.taskId());
        assertEquals("http://host/stream", response.streamUrl());
        assertEquals(CodeExecutionStatus.PENDING, response.status());
        assertEquals(1000L, response.submittedAt());
    }

    @Test
    void nullTaskId_throws() {
        assertThrows(
                NullPointerException.class,
                () -> new CodeExecutionResponse(null, "http://host/stream", CodeExecutionStatus.PENDING, 1000L));
    }

    @Test
    void nullStreamUrl_throws() {
        assertThrows(
                NullPointerException.class,
                () -> new CodeExecutionResponse("task-1", null, CodeExecutionStatus.PENDING, 1000L));
    }

    @Test
    void nullStatus_throws() {
        assertThrows(
                NullPointerException.class,
                () -> new CodeExecutionResponse("task-1", "http://host/stream", null, 1000L));
    }

    @Test
    void emptyTaskId_throws() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CodeExecutionResponse("  ", "http://host/stream", CodeExecutionStatus.PENDING, 1000L));
    }

    @Test
    void emptyStreamUrl_throws() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CodeExecutionResponse("task-1", "  ", CodeExecutionStatus.PENDING, 1000L));
    }

    @Test
    void createFactory() {
        CodeExecutionResponse response =
                CodeExecutionResponse.create("task-1", "http://host/stream", CodeExecutionStatus.RUNNING);
        assertEquals("task-1", response.taskId());
        assertEquals(CodeExecutionStatus.RUNNING, response.status());
        assertTrue(response.submittedAt() > 0);
    }

    @Test
    void createPendingFactory() {
        CodeExecutionResponse response = CodeExecutionResponse.createPending("task-1", "http://host/stream");
        assertEquals(CodeExecutionStatus.PENDING, response.status());
        assertNotNull(response.taskId());
    }
}
