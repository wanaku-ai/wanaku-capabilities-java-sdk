package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.io.IOException;
import ai.wanaku.capabilities.sdk.common.exceptions.WanakuWebException;

/**
 * A {@link RetryPolicy} implementation that uses exponential backoff between retry attempts.
 *
 * <p>This policy retries on server-side errors (HTTP 5xx) and transient network exceptions,
 * but does not retry on client errors (HTTP 4xx) or local file I/O errors.</p>
 */
public class ExponentialBackoffRetryPolicy implements RetryPolicy {
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_INITIAL_DELAY_MILLIS = 1000;
    private static final long DEFAULT_MAX_DELAY_MILLIS = 30000;

    private final int maxRetries;
    private final long initialDelayMillis;
    private final long maxDelayMillis;

    private ExponentialBackoffRetryPolicy(Builder builder) {
        this.maxRetries = builder.maxRetries;
        this.initialDelayMillis = builder.initialDelayMillis;
        this.maxDelayMillis = builder.maxDelayMillis;
    }

    @Override
    public boolean isRetryable(Exception e) {
        if (e instanceof WanakuWebException wex) {
            int status = wex.getStatusCode();
            // Do not retry client errors (4xx)
            return status >= 500;
        }

        // Do not retry local file errors
        if (e instanceof IOException) {
            return false;
        }

        // Retry other transient exceptions (e.g., network timeouts)
        return true;
    }

    @Override
    public long getDelayMillis(int attempt) {
        int shift = Math.min(attempt - 1, Long.SIZE - 2);
        long delay = initialDelayMillis * (1L << shift);
        if (delay <= 0) {
            return maxDelayMillis;
        }
        return Math.min(delay, maxDelayMillis);
    }

    @Override
    public int maxRetries() {
        return maxRetries;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private int maxRetries = DEFAULT_MAX_RETRIES;
        private long initialDelayMillis = DEFAULT_INITIAL_DELAY_MILLIS;
        private long maxDelayMillis = DEFAULT_MAX_DELAY_MILLIS;

        private Builder() {}

        public Builder maxRetries(int maxRetries) {
            if (maxRetries < 0) {
                throw new IllegalArgumentException("maxRetries must be >= 0, but was " + maxRetries);
            }
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder initialDelayMillis(long initialDelayMillis) {
            this.initialDelayMillis = initialDelayMillis;
            return this;
        }

        public Builder maxDelayMillis(long maxDelayMillis) {
            this.maxDelayMillis = maxDelayMillis;
            return this;
        }

        public ExponentialBackoffRetryPolicy build() {
            return new ExponentialBackoffRetryPolicy(this);
        }
    }
}
