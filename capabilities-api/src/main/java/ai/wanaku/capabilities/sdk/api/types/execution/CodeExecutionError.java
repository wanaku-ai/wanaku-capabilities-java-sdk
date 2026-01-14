/*
 * Copyright 2026 Wanaku AI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.wanaku.capabilities.sdk.api.types.execution;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Error response DTO for code execution failures.
 * <p>
 * This class represents error responses returned by the code execution
 * endpoints when validation fails, tasks are not found, or execution
 * errors occur.
 * </p>
 */
public class CodeExecutionError {

    /**
     * The error type or category.
     * Examples: "VALIDATION_ERROR", "NOT_FOUND", "EXECUTION_ERROR", "TIMEOUT"
     */
    @JsonProperty("error")
    private String error;

    /**
     * Human-readable error message describing what went wrong.
     */
    @JsonProperty("message")
    private String message;

    /**
     * The task ID if applicable.
     * Present when the error relates to a specific task.
     */
    @JsonProperty("taskId")
    private String taskId;

    /**
     * The timestamp when the error occurred (Unix timestamp in milliseconds).
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * Additional error details as key-value pairs.
     * Can include field-specific validation errors, stack traces, etc.
     */
    @JsonProperty("details")
    private Map<String, String> details;

    /**
     * Default constructor for JSON serialization.
     */
    public CodeExecutionError() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor with error type and message.
     *
     * @param error the error type
     * @param message the error message
     */
    public CodeExecutionError(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor with all fields except details.
     *
     * @param error the error type
     * @param message the error message
     * @param taskId the task ID
     */
    public CodeExecutionError(String error, String message, String taskId) {
        this.error = error;
        this.message = message;
        this.taskId = taskId;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "CodeExecutionError{" + "error='"
                + error + '\'' + ", message='"
                + message + '\'' + ", taskId='"
                + taskId + '\'' + ", timestamp="
                + timestamp + ", details="
                + details + '}';
    }
}
