package ai.wanaku.capabilities.sdk.discovery.config;

/**
 * Configuration interface for service registration, defining parameters such as
 * retry behavior, data storage, and scheduling for registration attempts.
 */
public interface RegistrationConfig {
    /**
     * Returns the maximum number of retries for registration attempts.
     *
     * @return The maximum number of retries.
     */
    int getMaxRetries();
    /**
     * Returns the waiting time in seconds between registration retries.
     *
     * @return The waiting time in seconds.
     */
    int getWaitSeconds();
    /**
     * Returns the directory where instance data (e.g., service ID) should be stored.
     *
     * @return The data directory path.
     */
    String getDataDir();
    /**
     * Returns the initial delay in seconds before the first registration attempt.
     *
     * @return The initial delay in seconds.
     */
    long getInitialDelay();
    /**
     * Returns the period in seconds between subsequent registration attempts (e.g., for pinging).
     *
     * @return The period in seconds.
     */
    long getPeriod();
}
