package ai.wanaku.capabilities.sdk.discovery.config;

public class DefaultRegistrationConfig implements RegistrationConfig {

    private final int maxRetries;
    private final int waitSeconds;
    private final String dataDir;
    private final long initialDelay;
    private final long period;

    private DefaultRegistrationConfig(Builder builder) {
        this.maxRetries = builder.maxRetries;
        this.waitSeconds = builder.waitSeconds;
        this.dataDir = builder.dataDir;
        this.initialDelay = builder.initialDelay;
        this.period = builder.period;
    }

    public static class Builder {
        private int maxRetries;
        private int waitSeconds;
        private String dataDir;
        private long initialDelay;
        private long period;

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder waitSeconds(int waitSeconds) {
            this.waitSeconds = waitSeconds;
            return this;
        }

        public Builder dataDir(String dataDir) {
            this.dataDir = dataDir;
            return this;
        }

        public Builder initialDelay(long initialDelay) {
            this.initialDelay = initialDelay;
            return this;
        }

        public Builder period(long period) {
            this.period = period;
            return this;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public DefaultRegistrationConfig build() {
            return new DefaultRegistrationConfig(this);
        }
    }

    @Override
    public int getMaxRetries() {
        return maxRetries;
    }

    @Override
    public int getWaitSeconds() {
        return waitSeconds;
    }

    @Override
    public String getDataDir() {
        return dataDir;
    }

    @Override
    public long getInitialDelay() {
        return initialDelay;
    }

    @Override
    public long getPeriod() {
        return period;
    }
}
