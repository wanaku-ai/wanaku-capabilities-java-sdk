package ai.wanaku.capabilities.sdk.discovery.config;

/**
 * Default implementation of {@link RegistrationConfig} providing configuration for service registration.
 * This class uses a builder pattern for easy instantiation.
 */
public class DefaultRegistrationConfig implements RegistrationConfig {

    private final int maxRetries;
    private final int waitSeconds;
    private final String dataDir;
    private final long initialDelay;
    private final long period;

    /**
     * Private constructor to enforce the use of the {@link Builder}.
     *
     * @param builder The builder instance containing the configuration parameters.
     */
    private DefaultRegistrationConfig(Builder builder) {
        this.maxRetries = builder.maxRetries;
        this.waitSeconds = builder.waitSeconds;
        this.dataDir = builder.dataDir;
        this.initialDelay = builder.initialDelay;
        this.period = builder.period;
    }

    /**
     * Builder class for {@link DefaultRegistrationConfig}.
     */
    public static class Builder {
        private int maxRetries;
        private int waitSeconds;
        private String dataDir;
        private long initialDelay;
        private long period;

        /**
         * Sets the maximum number of retries for registration attempts.
         *
         * @param maxRetries The maximum number of retries.
         * @return The builder instance.
         */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Sets the waiting time in seconds between registration retries.
         *
         * @param waitSeconds The waiting time in seconds.
         * @return The builder instance.
         */
        public Builder waitSeconds(int waitSeconds) {
            this.waitSeconds = waitSeconds;
            return this;
        }

        /**
         * Sets the directory for storing instance data.
         *
         * @param dataDir The data directory path.
         * @return The builder instance.
         */
        public Builder dataDir(String dataDir) {
            this.dataDir = dataDir;
            return this;
        }

        /**
         * Sets the initial delay in seconds before the first registration attempt.
         *
         * @param initialDelay The initial delay in seconds.
         * @return The builder instance.
         */
        public Builder initialDelay(long initialDelay) {
            this.initialDelay = initialDelay;
            return this;
        }

        /**
         * Sets the period in seconds between subsequent registration attempts.
         *
         * @param period The period in seconds.
         * @return The builder instance.
         */
        public Builder period(long period) {
            this.period = period;
            return this;
        }

        /**
         * Creates a new builder instance.
         *
         * @return A new {@link Builder} instance.
         */
        public static Builder newBuilder() {
            return new Builder();
        }

        /**
         * Builds a {@link DefaultRegistrationConfig} instance with the configured parameters.
         *
         * @return A new {@link DefaultRegistrationConfig} instance.
         */
        public DefaultRegistrationConfig build() {
            return new DefaultRegistrationConfig(this);
        }
    }

    /**
     * Returns the maximum number of retries for registration attempts.
     *
     * @return The maximum number of retries.
     */
    @Override
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Returns the waiting time in seconds between registration retries.
     *
     * @return The waiting time in seconds.
     */
    @Override
    public int getWaitSeconds() {
        return waitSeconds;
    }

    /**
     * Returns the directory for storing instance data.
     *
     * @return The data directory path.
     */
    @Override
    public String getDataDir() {
        return dataDir;
    }

    /**
     * Returns the initial delay in seconds before the first registration attempt.
     *
     * @return The initial delay in seconds.
     */
    @Override
    public long getInitialDelay() {
        return initialDelay;
    }

    /**
     * Returns the period in seconds between subsequent registration attempts.
     *
     * @return The period in seconds.
     */
    @Override
    public long getPeriod() {
        return period;
    }
}
