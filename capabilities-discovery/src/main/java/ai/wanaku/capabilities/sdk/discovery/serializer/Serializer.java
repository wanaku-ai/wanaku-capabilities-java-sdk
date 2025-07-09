package ai.wanaku.capabilities.sdk.discovery.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Serializer {
    <T> String serialize(T object) throws JsonProcessingException;
}
