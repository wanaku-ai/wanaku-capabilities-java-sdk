package ai.wanaku.capabilities.sdk.api.types.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Request payload for submitting code to be executed.
 * <p>
 * This class represents the input data for a code execution request. It contains
 * the source code to execute along with optional configuration parameters such as
 * timeout, environment variables, and command-line arguments.
 * <p>
 * Example usage:
 * <pre>{@code
 * CodeExecutionRequest request = new CodeExecutionRequest();
 * request.setCode("System.out.println(\"Hello, World!\");");
 * request.setTimeout(30000L);
 * request.setEnvironment(Map.of("ENV_VAR", "value"));
 * }</pre>
 *
 * @since 1.0.0
 */
public class CodeExecutionRequest {
    /**
     * Default timeout in milliseconds (30 seconds).
     */
    public static final long DEFAULT_TIMEOUT_MS = 30000L;

    /**
     * Maximum allowed code size in bytes (1 MB).
     */
    public static final int MAX_CODE_SIZE_BYTES = 1024 * 1024;

    /**
     * Maximum allowed timeout in milliseconds (5 minutes).
     */
    public static final long MAX_TIMEOUT_MS = 300000L;

    /**
     * Minimum allowed timeout in milliseconds (1 millisecond).
     */
    public static final long MIN_TIMEOUT_MS = 1L;

    private String code;
    private Long timeout;
    private Map<String, String> environment;
    private List<String> arguments;
    private Map<String, Object> metadata;

    /**
     * Default constructor for serialization frameworks.
     */
    public CodeExecutionRequest() {
        this.timeout = DEFAULT_TIMEOUT_MS;
        this.environment = new HashMap<>();
        this.arguments = new ArrayList<>();
        this.metadata = new HashMap<>();
    }

    /**
     * Creates a new CodeExecutionRequest with the specified code.
     *
     * @param code the source code to execute
     */
    public CodeExecutionRequest(String code) {
        this();
        this.code = code;
    }

    /**
     * Gets the source code to execute.
     *
     * @return the source code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the source code to execute.
     *
     * @param code the source code to execute (required, must not be null or empty)
     * @throws IllegalArgumentException if code is null, empty, or exceeds maximum size
     */
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code must not be null or empty");
        }
        if (code.getBytes().length > MAX_CODE_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "Code size exceeds maximum allowed size of " + MAX_CODE_SIZE_BYTES + " bytes");
        }
        this.code = code;
    }

    /**
     * Gets the maximum execution time in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    public Long getTimeout() {
        return timeout;
    }

    /**
     * Sets the maximum execution time in milliseconds.
     *
     * @param timeout the timeout in milliseconds (must be between {@link #MIN_TIMEOUT_MS}
     *                and {@link #MAX_TIMEOUT_MS})
     * @throws IllegalArgumentException if timeout is outside the valid range
     */
    public void setTimeout(Long timeout) {
        if (timeout != null && (timeout < MIN_TIMEOUT_MS || timeout > MAX_TIMEOUT_MS)) {
            throw new IllegalArgumentException(
                    "Timeout must be between " + MIN_TIMEOUT_MS + " and " + MAX_TIMEOUT_MS + " milliseconds");
        }
        this.timeout = timeout;
    }

    /**
     * Gets the environment variables for execution.
     *
     * @return a map of environment variable names to values
     */
    public Map<String, String> getEnvironment() {
        return environment;
    }

    /**
     * Sets the environment variables for execution.
     *
     * @param environment a map of environment variable names to values
     */
    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment != null ? environment : new HashMap<>();
    }

    /**
     * Gets the command-line arguments to pass to the code.
     *
     * @return a list of command-line arguments
     */
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * Sets the command-line arguments to pass to the code.
     *
     * @param arguments a list of command-line arguments
     */
    public void setArguments(List<String> arguments) {
        this.arguments = arguments != null ? arguments : new ArrayList<>();
    }

    /**
     * Gets the additional metadata for tracking/logging.
     *
     * @return a map of metadata key-value pairs
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets the additional metadata for tracking/logging.
     *
     * @param metadata a map of metadata key-value pairs
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * Validates this request to ensure all required fields are present and valid.
     *
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code must not be null or empty");
        }
        if (code.getBytes().length > MAX_CODE_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "Code size exceeds maximum allowed size of " + MAX_CODE_SIZE_BYTES + " bytes");
        }
        if (timeout != null && (timeout < MIN_TIMEOUT_MS || timeout > MAX_TIMEOUT_MS)) {
            throw new IllegalArgumentException(
                    "Timeout must be between " + MIN_TIMEOUT_MS + " and " + MAX_TIMEOUT_MS + " milliseconds");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CodeExecutionRequest that = (CodeExecutionRequest) o;
        return Objects.equals(code, that.code)
                && Objects.equals(timeout, that.timeout)
                && Objects.equals(environment, that.environment)
                && Objects.equals(arguments, that.arguments)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, timeout, environment, arguments, metadata);
    }

    @Override
    public String toString() {
        return "CodeExecutionRequest{"
                + "code='" + (code != null ? code.substring(0, Math.min(50, code.length())) + "..." : null) + '\''
                + ", timeout=" + timeout
                + ", environment=" + environment
                + ", arguments=" + arguments
                + ", metadata=" + metadata
                + '}';
    }
}
