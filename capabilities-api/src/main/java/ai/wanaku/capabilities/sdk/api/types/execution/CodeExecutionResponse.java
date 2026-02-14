package ai.wanaku.capabilities.sdk.api.types.execution;

import java.util.Objects;

/**
 * Response from POST endpoint containing task information and stream URL.
 * <p>
 * This immutable record represents the response returned when a code execution
 * request is successfully accepted. It contains the task identifier and the URL
 * where execution events can be streamed via Server-Sent Events (SSE).
 * <p>
 * Example response:
 * <pre>{@code
 * {
 *   "taskId": "550e8400-e29b-41d4-a716-446655440000",
 *   "streamUrl": "http://host:8080/api/v2/code-execution-engine/jvm/java/550e8400-e29b-41d4-a716-446655440000",
 *   "status": "PENDING",
 *   "submittedAt": 1706620800000
 * }
 * }</pre>
 *
 * @param taskId the UUID identifying the execution task
 * @param streamUrl the full URL to the SSE stream endpoint
 * @param status the initial status of the task (typically PENDING)
 * @param submittedAt the timestamp (epoch milliseconds) when the task was submitted
 * @since 1.0.0
 */
public record CodeExecutionResponse(String taskId, String streamUrl, CodeExecutionStatus status, long submittedAt) {

    /**
     * Compact constructor with validation.
     *
     * @throws IllegalArgumentException if any required field is null or empty
     */
    public CodeExecutionResponse {
        Objects.requireNonNull(taskId, "taskId must not be null");
        Objects.requireNonNull(streamUrl, "streamUrl must not be null");
        Objects.requireNonNull(status, "status must not be null");

        if (taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("taskId must not be empty");
        }
        if (streamUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("streamUrl must not be empty");
        }
    }

    /**
     * Creates a new CodeExecutionResponse with the current timestamp.
     *
     * @param taskId the UUID identifying the execution task
     * @param streamUrl the full URL to the SSE stream endpoint
     * @param status the initial status of the task
     * @return a new CodeExecutionResponse with the current timestamp
     */
    public static CodeExecutionResponse create(String taskId, String streamUrl, CodeExecutionStatus status) {
        return new CodeExecutionResponse(taskId, streamUrl, status, System.currentTimeMillis());
    }

    /**
     * Creates a new CodeExecutionResponse with PENDING status and current timestamp.
     *
     * @param taskId the UUID identifying the execution task
     * @param streamUrl the full URL to the SSE stream endpoint
     * @return a new CodeExecutionResponse with PENDING status
     */
    public static CodeExecutionResponse createPending(String taskId, String streamUrl) {
        return create(taskId, streamUrl, CodeExecutionStatus.PENDING);
    }
}
