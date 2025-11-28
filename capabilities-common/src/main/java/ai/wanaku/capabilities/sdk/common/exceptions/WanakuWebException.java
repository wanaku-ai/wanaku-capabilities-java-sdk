package ai.wanaku.capabilities.sdk.common.exceptions;

import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;

/**
 * Exception thrown when web-related operations fail in the Wanaku capabilities SDK.
 * <p>
 * This exception is used to indicate failures in HTTP/web communications, including
 * network errors, HTTP errors, or other web-related issues that occur during
 * capability operations. It captures the HTTP status code associated with the failure.
 * </p>
 */
public class WanakuWebException extends WanakuException {
    private final int statusCode;

    /**
     * Constructs a new WanakuWebException with the specified HTTP status code.
     *
     * @param statusCode the HTTP status code associated with this exception
     */
    public WanakuWebException(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Constructs a new WanakuWebException with the specified detail message and HTTP status code.
     *
     * @param message the detail message explaining the reason for the exception
     * @param statusCode the HTTP status code associated with this exception
     */
    public WanakuWebException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a new WanakuWebException with the specified detail message, cause, and HTTP status code.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of this exception
     * @param statusCode the HTTP status code associated with this exception
     */
    public WanakuWebException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a new WanakuWebException with the specified cause and HTTP status code.
     *
     * @param cause the underlying cause of this exception
     * @param statusCode the HTTP status code associated with this exception
     */
    public WanakuWebException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a new WanakuWebException with the specified detail message, cause,
     * suppression enabled or disabled, writable stack trace enabled or disabled, and HTTP status code.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of this exception
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     * @param statusCode the HTTP status code associated with this exception
     */
    public WanakuWebException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int statusCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.statusCode = statusCode;
    }

    /**
     * Returns the HTTP status code associated with this exception.
     *
     * @return the HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }
}
