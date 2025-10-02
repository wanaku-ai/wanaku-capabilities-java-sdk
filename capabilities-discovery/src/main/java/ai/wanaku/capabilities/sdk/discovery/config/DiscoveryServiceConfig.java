package ai.wanaku.capabilities.sdk.discovery.config;

import ai.wanaku.capabilities.sdk.common.serializer.Serializer;

/**
 * Configuration interface for the Discovery Service, defining essential parameters
 * like the base URL and the serializer to be used for communication.
 */
public interface DiscoveryServiceConfig {
    /**
     * Returns the base URL of the Discovery Service API.
     *
     * @return The base URL as a {@code String}.
     */
    String getBaseUrl();
    /**
     * Returns the {@link Serializer} instance used for serializing data sent to the Discovery Service.
     *
     * @return The {@link Serializer} instance.
     */
    Serializer getSerializer();

    /**
     * Returns the client ID used for authentication with the Discovery Service.
     *
     * @return The client ID as a {@code String}.
     */
    String getClientId();

    /**
     * Returns the secret used for authentication with the Discovery Service.
     *
     * @return The secret as a {@code String}.
     */
    String getSecret();

    /**
     * Returns the URL for the OpenID Connect token endpoint (e.g., http://address/.well-known/openid-configuration).
     *
     * @return The OpenID Connect token endpoint as a {@code String}.
     */
    String getTokenEndpoint();
}
