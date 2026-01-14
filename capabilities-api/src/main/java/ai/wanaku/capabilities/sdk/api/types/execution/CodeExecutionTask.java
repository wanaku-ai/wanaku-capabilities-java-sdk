package ai.wanaku.capabilities.sdk.api.types.execution;

import java.time.Instant;
import java.util.Objects;

/**
 * Internal representation of a code execution task.
 * <p>
 * This class is used internally to track the state and metadata of a code execution
 * task throughout its lifecycle. It is not exposed directly via the REST API but is
 * used by the execution engine to manage tasks.
 * <p>
 * This class combines the original request with runtime state information such as
 * execution timestamps and status.
 *
 * @since 1.0.0
 */
public class CodeExecutionTask {
    private String taskId;
    private CodeExecutionRequest request;
    private String engineType;
    private String language;
    private CodeExecutionStatus status;
    private Instant submittedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Integer exitCode;

    /**
     * Default constructor for serialization frameworks.
     */
    public CodeExecutionTask() {
        this.status = CodeExecutionStatus.PENDING;
        this.submittedAt = Instant.now();
    }

    /**
     * Creates a new CodeExecutionTask with the specified parameters.
     *
     * @param taskId the UUID identifier for this task
     * @param request the original execution request
     * @param engineType the engine type to use for execution (e.g., "jvm", "interpreted")
     * @param language the programming language of the code
     */
    public CodeExecutionTask(String taskId, CodeExecutionRequest request, 
                            String engineType, String language) {
        this();
        this.taskId = taskId;
        this.request = request;
        this.engineType = engineType;
        this.language = language;
    }

    /**
     * Gets the UUID identifier for this task.
     *
     * @return the task ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Sets the UUID identifier for this task.
     *
     * @param taskId the task ID to set
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * Gets the original execution request.
     *
     * @return the execution request
     */
    public CodeExecutionRequest getRequest() {
        return request;
    }

    /**
     * Sets the original execution request.
     *
     * @param request the execution request to set
     */
    public void setRequest(CodeExecutionRequest request) {
        this.request = request;
    }

    /**
     * Gets the engine type to use for execution.
     *
     * @return the engine type (e.g., "jvm", "interpreted")
     */
    public String getEngineType() {
        return engineType;
    }

    /**
     * Sets the engine type to use for execution.
     *
     * @param engineType the engine type to set (e.g., "jvm", "interpreted")
     */
    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    /**
     * Gets the programming language of the code.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the programming language of the code.
     *
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets the current execution status.
     *
     * @return the execution status
     */
    public CodeExecutionStatus getStatus() {
        return status;
    }

    /**
     * Sets the current execution status.
     *
     * @param status the status to set
     */
    public void setStatus(CodeExecutionStatus status) {
        this.status = status;
    }

    /**
     * Gets the timestamp when the task was submitted.
     *
     * @return the submission timestamp
     */
    public Instant getSubmittedAt() {
        return submittedAt;
    }

    /**
     * Sets the timestamp when the task was submitted.
     *
     * @param submittedAt the submission timestamp to set
     */
    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    /**
     * Gets the timestamp when execution started.
     *
     * @return the start timestamp, or null if not started
     */
    public Instant getStartedAt() {
        return startedAt;
    }

    /**
     * Sets the timestamp when execution started.
     *
     * @param startedAt the start timestamp to set
     */
    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    /**
     * Gets the timestamp when execution completed.
     *
     * @return the completion timestamp, or null if not completed
     */
    public Instant getCompletedAt() {
        return completedAt;
    }

    /**
     * Sets the timestamp when execution completed.
     *
     * @param completedAt the completion timestamp to set
     */
    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    /**
     * Gets the process exit code.
     *
     * @return the exit code, or null if not completed
     */
    public Integer getExitCode() {
        return exitCode;
    }

    /**
     * Sets the process exit code.
     *
     * @param exitCode the exit code to set
     */
    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Marks the task as started and sets the start timestamp.
     */
    public void markStarted() {
        this.status = CodeExecutionStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    /**
     * Marks the task as completed with the given exit code.
     *
     * @param exitCode the process exit code
     */
    public void markCompleted(int exitCode) {
        this.status = CodeExecutionStatus.COMPLETED;
        this.exitCode = exitCode;
        this.completedAt = Instant.now();
    }

    /**
     * Marks the task as failed with the given exit code.
     *
     * @param exitCode the process exit code
     */
    public void markFailed(int exitCode) {
        this.status = CodeExecutionStatus.FAILED;
        this.exitCode = exitCode;
        this.completedAt = Instant.now();
    }

    /**
     * Marks the task as timed out.
     */
    public void markTimeout() {
        this.status = CodeExecutionStatus.TIMEOUT;
        this.completedAt = Instant.now();
    }

    /**
     * Marks the task as cancelled.
     */
    public void markCancelled() {
        this.status = CodeExecutionStatus.CANCELLED;
        this.completedAt = Instant.now();
    }

    /**
     * Calculates the execution duration in milliseconds.
     *
     * @return the duration in milliseconds, or null if not started or not completed
     */
    public Long getExecutionDurationMs() {
        if (startedAt == null || completedAt == null) {
            return null;
        }
        return completedAt.toEpochMilli() - startedAt.toEpochMilli();
    }

    /**
     * Checks if the task is in a terminal state.
     *
     * @return true if the task has finished execution
     */
    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CodeExecutionTask that = (CodeExecutionTask) o;
        return Objects.equals(taskId, that.taskId)
                && Objects.equals(request, that.request)
                && Objects.equals(engineType, that.engineType)
                && language == that.language
                && status == that.status
                && Objects.equals(submittedAt, that.submittedAt)
                && Objects.equals(startedAt, that.startedAt)
                && Objects.equals(completedAt, that.completedAt)
                && Objects.equals(exitCode, that.exitCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, request, engineType, language, status, 
                          submittedAt, startedAt, completedAt, exitCode);
    }

    @Override
    public String toString() {
        return "CodeExecutionTask{"
                + "taskId='" + taskId + '\''
                + ", engineType=" + engineType
                + ", language=" + language
                + ", status=" + status
                + ", submittedAt=" + submittedAt
                + ", startedAt=" + startedAt
                + ", completedAt=" + completedAt
                + ", exitCode=" + exitCode
                + ", executionDurationMs=" + getExecutionDurationMs()
                + '}';
    }
}
