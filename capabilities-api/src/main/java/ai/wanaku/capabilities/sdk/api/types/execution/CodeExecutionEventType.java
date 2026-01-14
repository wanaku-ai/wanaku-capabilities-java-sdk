package ai.wanaku.capabilities.sdk.api.types.execution;

/**
 * Enumeration of possible event types in the execution stream.
 * <p>
 * These event types are used in Server-Sent Events (SSE) to communicate
 * the progress and results of code execution to clients.
 *
 * @since 1.0.0
 */
public enum CodeExecutionEventType {
    /**
     * Execution has begun.
     * This event is sent when the code execution task starts running.
     */
    STARTED,

    /**
     * Standard output chunk received.
     * This event is sent whenever the executing code produces output to stdout.
     * Multiple OUTPUT events may be sent during execution.
     */
    OUTPUT,

    /**
     * Standard error chunk received.
     * This event is sent whenever the executing code produces output to stderr.
     * Multiple ERROR events may be sent during execution.
     */
    ERROR,

    /**
     * Execution completed successfully.
     * This is a terminal event indicating the code ran to completion without errors.
     */
    COMPLETED,

    /**
     * Execution failed with an error.
     * This is a terminal event indicating the code encountered an error during execution.
     */
    FAILED,

    /**
     * Execution was cancelled.
     * This is a terminal event indicating the task was terminated at the user's request.
     */
    CANCELLED,

    /**
     * Execution exceeded timeout limit.
     * This is a terminal event indicating the task was terminated because it ran too long.
     */
    TIMEOUT;

    /**
     * Checks if this event type represents a terminal event.
     * Terminal events indicate that the execution has finished and no further events will be sent.
     *
     * @return {@code true} if this is a terminal event (COMPLETED, FAILED, CANCELLED, or TIMEOUT),
     *         {@code false} otherwise
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == TIMEOUT;
    }

    /**
     * Checks if this event type represents an output event.
     * Output events carry data from the executing code (stdout or stderr).
     *
     * @return {@code true} if this is an output event (OUTPUT or ERROR),
     *         {@code false} otherwise
     */
    public boolean isOutput() {
        return this == OUTPUT || this == ERROR;
    }
}
