package ai.wanaku.capabilities.sdk.discovery.exceptions;

import ai.wanaku.api.exceptions.WanakuException;

public class DiscoveryAuthException extends WanakuException {
    public DiscoveryAuthException() {
        super();
    }

    public DiscoveryAuthException(String message) {
        super(message);
    }

    public DiscoveryAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiscoveryAuthException(Throwable cause) {
        super(cause);
    }

    public DiscoveryAuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
