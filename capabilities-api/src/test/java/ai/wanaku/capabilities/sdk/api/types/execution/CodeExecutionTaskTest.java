package ai.wanaku.capabilities.sdk.api.types.execution;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeExecutionTaskTest {

    @Test
    void defaultConstructor_setsPendingStatus() {
        CodeExecutionTask task = new CodeExecutionTask();
        assertEquals(CodeExecutionStatus.PENDING, task.getStatus());
        assertNotNull(task.getSubmittedAt());
        assertNull(task.getStartedAt());
        assertNull(task.getCompletedAt());
        assertNull(task.getExitCode());
    }

    @Test
    void parameterizedConstructor() {
        CodeExecutionRequest request = new CodeExecutionRequest("code");
        CodeExecutionTask task = new CodeExecutionTask("id-1", request, "jvm", "java");
        assertEquals("id-1", task.getTaskId());
        assertEquals(request, task.getRequest());
        assertEquals("jvm", task.getEngineType());
        assertEquals("java", task.getLanguage());
        assertEquals(CodeExecutionStatus.PENDING, task.getStatus());
    }

    @Test
    void markStarted() {
        CodeExecutionTask task = new CodeExecutionTask("id-1", new CodeExecutionRequest("code"), "jvm", "java");
        task.markStarted();
        assertEquals(CodeExecutionStatus.RUNNING, task.getStatus());
        assertNotNull(task.getStartedAt());
    }

    @Test
    void markCompleted() {
        CodeExecutionTask task = new CodeExecutionTask("id-1", new CodeExecutionRequest("code"), "jvm", "java");
        task.markStarted();
        task.markCompleted(0);
        assertEquals(CodeExecutionStatus.COMPLETED, task.getStatus());
        assertEquals(0, task.getExitCode());
        assertNotNull(task.getCompletedAt());
        assertTrue(task.isTerminal());
    }

    @Test
    void markFailed() {
        CodeExecutionTask task = new CodeExecutionTask("id-1", new CodeExecutionRequest("code"), "jvm", "java");
        task.markStarted();
        task.markFailed(1);
        assertEquals(CodeExecutionStatus.FAILED, task.getStatus());
        assertEquals(1, task.getExitCode());
        assertTrue(task.isTerminal());
    }

    @Test
    void markTimeout() {
        CodeExecutionTask task = new CodeExecutionTask("id-1", new CodeExecutionRequest("code"), "jvm", "java");
        task.markStarted();
        task.markTimeout();
        assertEquals(CodeExecutionStatus.TIMEOUT, task.getStatus());
        assertTrue(task.isTerminal());
    }

    @Test
    void markCancelled() {
        CodeExecutionTask task = new CodeExecutionTask("id-1", new CodeExecutionRequest("code"), "jvm", "java");
        task.markCancelled();
        assertEquals(CodeExecutionStatus.CANCELLED, task.getStatus());
        assertTrue(task.isTerminal());
    }

    @Test
    void isTerminal_pendingIsFalse() {
        CodeExecutionTask task = new CodeExecutionTask();
        assertFalse(task.isTerminal());
    }

    @Test
    void executionDurationMs_nullWhenNotStarted() {
        CodeExecutionTask task = new CodeExecutionTask();
        assertNull(task.getExecutionDurationMs());
    }

    @Test
    void executionDurationMs_nullWhenNotCompleted() {
        CodeExecutionTask task = new CodeExecutionTask("id-1", new CodeExecutionRequest("code"), "jvm", "java");
        task.markStarted();
        assertNull(task.getExecutionDurationMs());
    }

    @Test
    void executionDurationMs_nonNegativeWhenCompleted() {
        CodeExecutionTask task = new CodeExecutionTask("id-1", new CodeExecutionRequest("code"), "jvm", "java");
        task.markStarted();
        task.markCompleted(0);
        assertNotNull(task.getExecutionDurationMs());
        assertTrue(task.getExecutionDurationMs() >= 0);
    }

    @Test
    void equalsAndHashCode() {
        CodeExecutionRequest request = new CodeExecutionRequest("code");
        CodeExecutionTask a = new CodeExecutionTask("id-1", request, "jvm", "java");
        CodeExecutionTask b = new CodeExecutionTask("id-1", request, "jvm", "java");
        a.setSubmittedAt(b.getSubmittedAt());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
