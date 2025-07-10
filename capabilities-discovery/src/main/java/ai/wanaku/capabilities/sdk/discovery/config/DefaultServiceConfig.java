package ai.wanaku.capabilities.sdk.discovery.config;

import ai.wanaku.capabilities.sdk.discovery.serializer.Serializer;

public class DefaultServiceConfig implements DiscoveryServiceConfig {

    private final String baseUrl;
    private final Serializer serializer;

    private DefaultServiceConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.serializer = builder.serializer;
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

    public static class Builder {
        private String baseUrl;
        private Serializer serializer = null;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder serializer(Serializer serializer) {
            this.serializer = serializer;
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
