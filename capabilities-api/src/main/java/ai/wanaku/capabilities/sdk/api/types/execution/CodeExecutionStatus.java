package ai.wanaku.capabilities.sdk.api.types.execution;

/**
 * Enumeration of execution task states.
 * <p>
 * Represents the lifecycle states of a code execution task from submission to completion.
 * The typical state flow is:
 * <pre>
 * PENDING → RUNNING → {COMPLETED, FAILED, TIMEOUT}
 * PENDING → CANCELLED
 * RUNNING → CANCELLED
 * </pre>
 *
 * @since 1.0.0
 */
public enum CodeExecutionStatus {
    /**
     * Task has been submitted and is waiting to start execution.
     * This is the initial state when a code execution request is accepted.
     */
    PENDING,

    /**
     * Code is currently being executed.
     * The task has started and is actively running.
     */
    RUNNING,

    /**
     * Execution finished successfully.
     * The code ran to completion without errors.
     */
    COMPLETED,

    /**
     * Execution failed with an error.
     * The code encountered an error during execution or failed to run.
     */
    FAILED,

    /**
     * Execution was cancelled by the user.
     * The task was terminated before completion at the user's request.
     */
    CANCELLED,

    /**
     * Execution exceeded the specified timeout limit.
     * The task was terminated because it ran longer than the allowed duration.
     */
    TIMEOUT;

    /**
     * Checks if this status represents a terminal state.
     * Terminal states are those where the execution has finished and no further
     * state transitions are possible.
     *
     * @return {@code true} if this is a terminal state (COMPLETED, FAILED, CANCELLED, or TIMEOUT),
     *         {@code false} otherwise
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == TIMEOUT;
    }

    /**
     * Checks if this status represents a successful completion.
     *
     * @return {@code true} if the status is COMPLETED, {@code false} otherwise
     */
    public boolean isSuccess() {
        return this == COMPLETED;
    }

    /**
     * Checks if this status represents an error state.
     *
     * @return {@code true} if the status is FAILED or TIMEOUT, {@code false} otherwise
     */
    public boolean isError() {
        return this == FAILED || this == TIMEOUT;
    }
}
