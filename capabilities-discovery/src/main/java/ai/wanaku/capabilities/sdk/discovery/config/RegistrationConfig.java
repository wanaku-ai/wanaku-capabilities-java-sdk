package ai.wanaku.capabilities.sdk.discovery.config;

public interface RegistrationConfig {
    int getMaxRetries();
    int getWaitSeconds();
    String getDataDir();
    long getInitialDelay();
    long getPeriod();
}
