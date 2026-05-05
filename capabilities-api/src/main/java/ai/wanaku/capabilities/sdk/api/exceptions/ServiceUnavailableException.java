package ai.wanaku.capabilities.sdk.api.exceptions;

/**
 * This exception is thrown when a specified service is not available.
 *
 * @see WanakuException
 */
public class ServiceUnavailableException extends WanakuException {
    /**
     * Whether the conditions causing the service to be unavailable are temporary
     */
    private final boolean transientCondition;

    /**
     * Constructs an instance of the exception with no detail message or cause.
     */
    public ServiceUnavailableException() {
        this(false);
    }

    /**
     * Constructs an instance of the exception with no detail message or cause.
     * @param transientCondition whether the conditions causing the service to be unavailable are temporary
     */
    public ServiceUnavailableException(boolean transientCondition) {
        super();
        this.transientCondition = transientCondition;
    }

    /**
     * Constructs an instance of the exception with a specified detail message.
     *
     * @param message            the detail message for this exception
     * @param transientCondition whether the conditions causing the service to be unavailable are temporary
     */
    public ServiceUnavailableException(String message, boolean transientCondition) {
        super(message);
        this.transientCondition = transientCondition;
    }

    /**
     * Constructs an instance of the exception with a specified cause and a detail message.
     *
     * @param message the detail message for this exception
     * @param cause   the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method)
     */
    public ServiceUnavailableException(String message, Throwable cause) {
        this(message, cause, false);
    }

    /**
     * Constructs an instance of the exception with a specified cause and a detail message.
     *
     * @param message the detail message for this exception
     * @param cause   the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method)
     * @param transientCondition whether the conditions causing the service to be unavailable are temporary
     */
    public ServiceUnavailableException(String message, Throwable cause, boolean transientCondition) {
        super(message, cause);
        this.transientCondition = transientCondition;
    }

    /**
     * Constructs an instance of the exception with a specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method)
     * @param transientCondition whether the conditions causing the service to be unavailable are temporary
     */
    public ServiceUnavailableException(Throwable cause, boolean transientCondition) {
        super(cause);
        this.transientCondition = transientCondition;
    }

    /**
     * Constructs an instance of the exception with a specified detail message, cause, suppression enabled or disabled,
     * and writable stack trace enabled or disabled.
     *
     * @param message         the detail message for this exception
     * @param cause           the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method)
     * @param enableSuppression whether suppression is enabled or disabled
     * @param writableStackTrace  whether the stack trace should be writable
     * @param transientCondition whether the conditions causing the service to be unavailable are temporary
     */
    public ServiceUnavailableException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace,
            boolean transientCondition) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.transientCondition = transientCondition;
    }

    /**
     * Whether the unavailability was caused by a temporary condition
     * @return true if temporary, false otherwise
     */
    public boolean isTransientCondition() {
        return transientCondition;
    }

    /**
     * Returns a new instance of this exception with a formatted string indicating that a service was not available.
     *
     * @param serviceName the name of the missing service
     * @return a new instance of this exception for the specified service name
     */
    public static ServiceUnavailableException forName(String serviceName) {
        return new ServiceUnavailableException(String.format("Service is not available at %s", serviceName), false);
    }

    /**
     * Returns a new instance of this exception with a formatted string indicating that a service was not available at
     * a specific address.
     *
     * @param address the address of the missing service
     * @return a new instance of this exception for the specified service name
     */
    public static ServiceUnavailableException forAddress(String address) {
        return new ServiceUnavailableException(
                String.format("Service is not available at the address %s", address), false);
    }
}
