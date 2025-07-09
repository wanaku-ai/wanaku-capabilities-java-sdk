package ai.wanaku.capabilities.sdk.discovery.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

public interface Deserializer {
    <T> T deserialize(String content, TypeReference<T> typeRef) throws JsonProcessingException;
}
