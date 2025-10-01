package ai.wanaku.capabilities.sdk.discovery.config;

import ai.wanaku.capabilities.sdk.discovery.serializer.Serializer;

/**
 * Default service configuration class
 */
public class DefaultServiceConfig implements DiscoveryServiceConfig {

    private final String baseUrl;
    private final Serializer serializer;
    private final String clientId;
    private final String secret;
    private final String tokenEndpoint;

    private DefaultServiceConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.serializer = builder.serializer;
        this.clientId = builder.clientId;
        this.secret = builder.secret;
        this.tokenEndpoint = builder.tokenEndpoint;
    }

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

    public static class Builder {
        private String baseUrl;
        private Serializer serializer = null;
        private String clientId;
        private String secret;
        private String tokenEndpoint;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder serializer(Serializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public Builder tokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public DefaultServiceConfig build() {
            return new DefaultServiceConfig(this);
        }
    }
}
