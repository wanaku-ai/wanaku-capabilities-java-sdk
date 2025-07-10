package ai.wanaku.capabilities.sdk.discovery.config;

import ai.wanaku.capabilities.sdk.discovery.serializer.Serializer;

public interface DiscoveryServiceConfig {
    String getBaseUrl();
    Serializer getSerializer();
}
