package ai.wanaku.capabilities.cee.langchain4j;

/**
 * Exception thrown when code execution fails.
 * <p>
 * This exception is thrown when the Wanaku Code Execution Engine reports
 * a failure, timeout, or cancellation during code execution.
 *
 * @since 1.0.0
 */
public class CodeExecutionException extends RuntimeException {
    private final Integer exitCode;

    /**
     * Creates a new CodeExecutionException with the specified message and exit code.
     *
     * @param message the error message
     * @param exitCode the process exit code, or null if not available
     */
    public CodeExecutionException(String message, Integer exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    /**
     * Creates a new CodeExecutionException with the specified message, cause, and exit code.
     *
     * @param message the error message
     * @param cause the underlying cause
     * @param exitCode the process exit code, or null if not available
     */
    public CodeExecutionException(String message, Throwable cause, Integer exitCode) {
        super(message, cause);
        this.exitCode = exitCode;
    }

    /**
     * Gets the process exit code.
     *
     * @return the exit code, or null if the code execution did not complete
     */
    public Integer getExitCode() {
        return exitCode;
    }
}
