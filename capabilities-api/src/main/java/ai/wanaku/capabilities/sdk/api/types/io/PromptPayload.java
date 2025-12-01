package ai.wanaku.capabilities.sdk.api.types.io;

import ai.wanaku.capabilities.sdk.api.types.PromptReference;

/**
 * Represents a payload for prompt operations that may require provisioning.
 */
public final class PromptPayload implements ProvisionAwarePayload<PromptReference> {
    /**
     * Default constructor for serialization frameworks.
     */
    public PromptPayload() {}

    private PromptReference promptReference;
    private String configurationData;
    private String secretsData;

    @Override
    public PromptReference getPayload() {
        return promptReference;
    }

    /**
     * Sets the prompt reference for this payload.
     *
     * @param promptReference the prompt reference to set
     */
    public void setPayload(PromptReference promptReference) {
        this.promptReference = promptReference;
    }

    @Override
    public String getConfigurationData() {
        return configurationData;
    }

    /**
     * Sets the configuration data for provisioning this prompt.
     *
     * @param configurationData the configuration data to set
     */
    public void setConfigurationData(String configurationData) {
        this.configurationData = configurationData;
    }

    @Override
    public String getSecretsData() {
        return secretsData;
    }

    /**
     * Sets the secrets data required for provisioning this prompt.
     *
     * @param secretsData the secrets data to set
     */
    public void setSecretsData(String secretsData) {
        this.secretsData = secretsData;
    }
}
