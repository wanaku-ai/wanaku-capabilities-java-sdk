package ai.wanaku.capabilities.sdk.discovery.config;

import ai.wanaku.capabilities.sdk.common.serializer.Serializer;

/**
 * Default implementation of {@link DiscoveryServiceConfig} providing configuration
 * for the Discovery Service including authentication and serialization settings.
 * This class uses a builder pattern for easy instantiation.
 */
public class DefaultDiscoveryServiceConfig implements DiscoveryServiceConfig {

    private final String baseUrl;
    private final Serializer serializer;
    private final String clientId;
    private final String secret;
    private final String tokenEndpoint;

    /**
     * Private constructor to enforce the use of the {@link Builder}.
     *
     * @param builder The builder instance containing the configuration parameters.
     */
    private DefaultDiscoveryServiceConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.serializer = builder.serializer;
        this.clientId = builder.clientId;
        this.secret = builder.secret;
        this.tokenEndpoint = builder.tokenEndpoint;
    }

    /**
     * Creates a new builder instance.
     *
     * @return A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public Serializer getSerializer() {
        return serializer;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getSecret() {
        return secret;
    }

    @Override
    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    /**
     * Builder class for {@link DefaultDiscoveryServiceConfig}.
     */
    public static class Builder {
        private String baseUrl;
        private Serializer serializer = null;
        private String clientId;
        private String secret;
        private String tokenEndpoint;

        /**
         * Sets the base URL for the Discovery Service.
         *
         * @param baseUrl The base URL.
         * @return The builder instance.
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets the serializer for data conversion.
         *
         * @param serializer The serializer instance.
         * @return The builder instance.
         */
        public Builder serializer(Serializer serializer) {
            this.serializer = serializer;
            return this;
        }

        /**
         * Sets the OAuth2 client ID for authentication.
         *
         * @param clientId The client ID.
         * @return The builder instance.
         */
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Sets the OAuth2 client secret for authentication.
         *
         * @param secret The client secret.
         * @return The builder instance.
         */
        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        /**
         * Sets the OAuth2 token endpoint URL.
         *
         * @param tokenEndpoint The token endpoint URL.
         * @return The builder instance.
         */
        public Builder tokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
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
         * Builds a {@link DefaultDiscoveryServiceConfig} instance with the configured parameters.
         *
         * @return A new {@link DefaultDiscoveryServiceConfig} instance.
         */
        public DefaultDiscoveryServiceConfig build() {
            return new DefaultDiscoveryServiceConfig(this);
        }
    }
}
