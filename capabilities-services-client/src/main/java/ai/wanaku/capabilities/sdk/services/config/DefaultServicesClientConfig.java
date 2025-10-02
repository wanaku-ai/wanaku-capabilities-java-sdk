package ai.wanaku.capabilities.sdk.services.config;

import ai.wanaku.capabilities.sdk.common.serializer.Serializer;

/**
 * Default implementation of {@link ServicesClientConfig} providing configuration
 * for the Services Client including base URL and serialization settings.
 * This class uses a builder pattern for easy instantiation.
 */
public class DefaultServicesClientConfig implements ServicesClientConfig {

    private final String baseUrl;
    private final Serializer serializer;

    /**
     * Private constructor to enforce the use of the {@link Builder}.
     *
     * @param builder The builder instance containing the configuration parameters.
     */
    private DefaultServicesClientConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.serializer = builder.serializer;
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

    /**
     * Builder class for {@link DefaultServicesClientConfig}.
     */
    public static class Builder {
        private String baseUrl;
        private Serializer serializer = null;

        /**
         * Sets the base URL for the Services API.
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
         * Creates a new builder instance.
         *
         * @return A new {@link Builder} instance.
         */
        public static Builder newBuilder() {
            return new Builder();
        }

        /**
         * Builds a {@link DefaultServicesClientConfig} instance with the configured parameters.
         *
         * @return A new {@link DefaultServicesClientConfig} instance.
         */
        public DefaultServicesClientConfig build() {
            return new DefaultServicesClientConfig(this);
        }
    }
}
