package ai.wanaku.capabilities.sdk.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An implementation of the {@link Serializer} interface that uses Jackson for JSON serialization.
 */
public class JacksonSerializer implements Serializer {

    /**
     * Serializes the given object into a JSON string using Jackson's {@link ObjectMapper}.
     *
     * @param <T> The type of the object to serialize.
     * @param object The object to serialize.
     * @return The JSON string representation of the object.
     * @throws JsonProcessingException If an error occurs during JSON serialization.
     */
    @Override
    public <T> String serialize(T object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
