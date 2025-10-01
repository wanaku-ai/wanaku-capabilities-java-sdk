package ai.wanaku.capabilities.sdk.discovery.exceptions;

public class InvalidResponseDataException extends RuntimeException {
    public InvalidResponseDataException() {
        super();
    }

    public InvalidResponseDataException(String message) {
        super(message);
    }

    public InvalidResponseDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidResponseDataException(Throwable cause) {
        super(cause);
    }

    protected InvalidResponseDataException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
