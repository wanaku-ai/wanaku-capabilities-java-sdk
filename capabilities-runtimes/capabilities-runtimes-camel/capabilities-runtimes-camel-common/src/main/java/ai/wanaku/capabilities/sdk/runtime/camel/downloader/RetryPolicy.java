package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

/**
 * Abstraction for retry behavior during resource downloads.
 * Implementations determine which exceptions are retryable and how long to wait between attempts.
 */
public interface RetryPolicy {

    /**
     * Determines whether a failed download should be retried based on the exception.
     *
     * @param e the exception thrown during the download attempt
     * @return {@code true} if the download should be retried, {@code false} otherwise
     */
    boolean isRetryable(Exception e);

    /**
     * Returns the delay in milliseconds before the next retry attempt.
     *
     * @param attempt the current attempt number (1-based, so 1 means the delay before the first retry)
     * @return the delay in milliseconds
     */
    long getDelayMillis(int attempt);

    /**
     * Returns the maximum number of retry attempts.
     *
     * @return the maximum number of retries (0 means no retries)
     */
    int maxRetries();
}
