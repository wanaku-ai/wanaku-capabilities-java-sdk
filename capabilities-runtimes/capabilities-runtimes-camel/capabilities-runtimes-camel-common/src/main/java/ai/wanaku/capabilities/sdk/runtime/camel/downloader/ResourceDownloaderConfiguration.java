package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

/**
 * Configuration for {@link ResourceDownloaderCallback}.
 * Built using the builder pattern, this class holds the retry policy and any other
 * download-related configuration.
 */
public class ResourceDownloaderConfiguration {
    private static final RetryPolicy NO_RETRY = new NoRetryPolicy();

    private final RetryPolicy retryPolicy;

    private ResourceDownloaderConfiguration(Builder builder) {
        this.retryPolicy = builder.retryPolicy;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /**
     * Returns a default configuration with no retry.
     */
    public static ResourceDownloaderConfiguration defaultConfig() {
        return newBuilder().build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private RetryPolicy retryPolicy = NO_RETRY;

        private Builder() {}

        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public ResourceDownloaderConfiguration build() {
            return new ResourceDownloaderConfiguration(this);
        }
    }

    /**
     * A no-op retry policy that never retries.
     */
    private static final class NoRetryPolicy implements RetryPolicy {
        @Override
        public boolean isRetryable(Exception e) {
            return false;
        }

        @Override
        public long getDelayMillis(int attempt) {
            return 0;
        }

        @Override
        public int maxRetries() {
            return 0;
        }
    }
}
