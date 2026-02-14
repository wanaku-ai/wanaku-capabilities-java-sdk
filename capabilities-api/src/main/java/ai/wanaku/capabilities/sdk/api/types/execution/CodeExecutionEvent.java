package ai.wanaku.capabilities.sdk.api.types.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Events streamed via SSE to communicate execution progress and results.
 * <p>
 * This class represents individual events that are sent to clients during code execution
 * via Server-Sent Events (SSE). Events can represent different stages of execution such
 * as start, output, errors, and completion.
 * <p>
 * Typical event flow:
 * <ol>
 *   <li>STARTED event - execution begins</li>
 *   <li>Multiple OUTPUT events - stdout chunks</li>
 *   <li>Multiple ERROR events - stderr chunks (if any)</li>
 *   <li>COMPLETED or FAILED event - execution finishes</li>
 * </ol>
 * <p>
 * Example usage:
 * <pre>{@code
 * CodeExecutionEvent event = new CodeExecutionEvent();
 * event.setEventType(CodeExecutionEventType.OUTPUT);
 * event.setTaskId("550e8400-e29b-41d4-a716-446655440000");
 * event.setStatus(CodeExecutionStatus.RUNNING);
 * event.setOutput("Hello, World!\n");
 * event.setTimestamp(System.currentTimeMillis());
 * }</pre>
 *
 * @since 1.0.0
 */
public class CodeExecutionEvent {
    private CodeExecutionEventType eventType;
    private String taskId;
    private long timestamp;
    private CodeExecutionStatus status;
    private String output;
    private String error;
    private Integer exitCode;
    private String message;
    private Map<String, Object> metadata;

    /**
     * Default constructor for serialization frameworks.
     */
    public CodeExecutionEvent() {
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }

    /**
     * Creates a new CodeExecutionEvent with the specified event type, task ID, and status.
     *
     * @param eventType the type of event
     * @param taskId the UUID of the execution task
     * @param status the current execution status
     */
    public CodeExecutionEvent(CodeExecutionEventType eventType, String taskId, CodeExecutionStatus status) {
        this();
        this.eventType = eventType;
        this.taskId = taskId;
        this.status = status;
    }

    /**
     * Gets the type of event.
     *
     * @return the event type
     */
    public CodeExecutionEventType getEventType() {
        return eventType;
    }

    /**
     * Sets the type of event.
     *
     * @param eventType the event type to set
     */
    public void setEventType(CodeExecutionEventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the UUID of the execution task.
     *
     * @return the task ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Sets the UUID of the execution task.
     *
     * @param taskId the task ID to set
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * Gets the timestamp (epoch milliseconds) when the event occurred.
     *
     * @return the event timestamp in epoch milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp (epoch milliseconds) when the event occurred.
     *
     * @param timestamp the timestamp in epoch milliseconds to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
     * Gets the standard output content.
     * <p>
     * This field is typically populated for OUTPUT events.
     *
     * @return the output content, or null if not applicable
     */
    public String getOutput() {
        return output;
    }

    /**
     * Sets the standard output content.
     *
     * @param output the output content to set
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * Gets the error output content.
     * <p>
     * This field is typically populated for ERROR events.
     *
     * @return the error content, or null if not applicable
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the error output content.
     *
     * @param error the error content to set
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Gets the process exit code.
     * <p>
     * This field is typically populated for COMPLETED or FAILED events.
     *
     * @return the exit code, or null if not applicable
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
     * Gets the human-readable message.
     *
     * @return the message, or null if not set
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the human-readable message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the additional event-specific metadata.
     *
     * @return a map of metadata key-value pairs
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets the additional event-specific metadata.
     *
     * @param metadata a map of metadata key-value pairs
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * Creates a STARTED event.
     *
     * @param taskId the task ID
     * @return a new STARTED event
     */
    public static CodeExecutionEvent started(String taskId) {
        CodeExecutionEvent event =
                new CodeExecutionEvent(CodeExecutionEventType.STARTED, taskId, CodeExecutionStatus.RUNNING);
        event.setMessage("Code execution started");
        return event;
    }

    /**
     * Creates an OUTPUT event.
     *
     * @param taskId the task ID
     * @param output the output content
     * @return a new OUTPUT event
     */
    public static CodeExecutionEvent output(String taskId, String output) {
        CodeExecutionEvent event =
                new CodeExecutionEvent(CodeExecutionEventType.OUTPUT, taskId, CodeExecutionStatus.RUNNING);
        event.setOutput(output);
        return event;
    }

    /**
     * Creates an ERROR event.
     *
     * @param taskId the task ID
     * @param error the error content
     * @return a new ERROR event
     */
    public static CodeExecutionEvent error(String taskId, String error) {
        CodeExecutionEvent event =
                new CodeExecutionEvent(CodeExecutionEventType.ERROR, taskId, CodeExecutionStatus.RUNNING);
        event.setError(error);
        return event;
    }

    /**
     * Creates a COMPLETED event.
     *
     * @param taskId the task ID
     * @param exitCode the process exit code
     * @return a new COMPLETED event
     */
    public static CodeExecutionEvent completed(String taskId, int exitCode) {
        CodeExecutionEvent event =
                new CodeExecutionEvent(CodeExecutionEventType.COMPLETED, taskId, CodeExecutionStatus.COMPLETED);
        event.setExitCode(exitCode);
        event.setMessage("Code execution completed successfully");
        return event;
    }

    /**
     * Creates a FAILED event.
     *
     * @param taskId the task ID
     * @param exitCode the process exit code
     * @param errorMessage the error message
     * @return a new FAILED event
     */
    public static CodeExecutionEvent failed(String taskId, int exitCode, String errorMessage) {
        CodeExecutionEvent event =
                new CodeExecutionEvent(CodeExecutionEventType.FAILED, taskId, CodeExecutionStatus.FAILED);
        event.setExitCode(exitCode);
        event.setMessage(errorMessage);
        return event;
    }

    /**
     * Creates a TIMEOUT event.
     *
     * @param taskId the task ID
     * @return a new TIMEOUT event
     */
    public static CodeExecutionEvent timeout(String taskId) {
        CodeExecutionEvent event =
                new CodeExecutionEvent(CodeExecutionEventType.TIMEOUT, taskId, CodeExecutionStatus.TIMEOUT);
        event.setMessage("Code execution exceeded timeout limit");
        return event;
    }

    /**
     * Creates a CANCELLED event.
     *
     * @param taskId the task ID
     * @return a new CANCELLED event
     */
    public static CodeExecutionEvent cancelled(String taskId) {
        CodeExecutionEvent event =
                new CodeExecutionEvent(CodeExecutionEventType.CANCELLED, taskId, CodeExecutionStatus.CANCELLED);
        event.setMessage("Code execution was cancelled");
        return event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CodeExecutionEvent that = (CodeExecutionEvent) o;
        return eventType == that.eventType
                && Objects.equals(taskId, that.taskId)
                && Objects.equals(timestamp, that.timestamp)
                && status == that.status
                && Objects.equals(output, that.output)
                && Objects.equals(error, that.error)
                && Objects.equals(exitCode, that.exitCode)
                && Objects.equals(message, that.message)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, taskId, timestamp, status, output, error, exitCode, message, metadata);
    }

    @Override
    public String toString() {
        return "CodeExecutionEvent{"
                + "eventType=" + eventType
                + ", taskId='" + taskId + '\''
                + ", timestamp=" + timestamp
                + ", status=" + status
                + ", output='" + (output != null ? output.substring(0, Math.min(50, output.length())) + "..." : null)
                + '\''
                + ", error='" + (error != null ? error.substring(0, Math.min(50, error.length())) + "..." : null) + '\''
                + ", exitCode=" + exitCode
                + ", message='" + message + '\''
                + ", metadata=" + metadata
                + '}';
    }
}
