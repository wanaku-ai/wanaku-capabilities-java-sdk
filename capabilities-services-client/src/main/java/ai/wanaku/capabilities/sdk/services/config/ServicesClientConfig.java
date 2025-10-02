package ai.wanaku.capabilities.sdk.services.config;

import ai.wanaku.capabilities.sdk.common.serializer.Serializer;

/**
 * Configuration interface for the Services Client, defining essential parameters
 * like the base URL and the serializer to be used for communication.
 */
public interface ServicesClientConfig {
    /**
     * Returns the base URL of the Wanaku Services API.
     *
     * @return The base URL as a {@code String}.
     */
    String getBaseUrl();

    /**
     * Returns the {@link Serializer} instance used for serializing data sent to the Services API.
     *
     * @return The {@link Serializer} instance.
     */
    Serializer getSerializer();
}
