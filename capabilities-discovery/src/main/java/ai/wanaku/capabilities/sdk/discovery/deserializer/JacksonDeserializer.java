package ai.wanaku.capabilities.sdk.discovery.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An implementation of the {@link Deserializer} interface that uses Jackson for JSON deserialization.
 */
public class JacksonDeserializer implements Deserializer {

    /**
     * Deserializes the given JSON content into an object of the specified type using Jackson's {@link ObjectMapper}.
     *
     * @param <T> The type of the object to deserialize to.
     * @param content The JSON string content to deserialize.
     * @param typeRef A {@link TypeReference} to specify the target type, especially for generic types.
     * @return The deserialized object.
     * @throws JsonProcessingException If an error occurs during JSON deserialization.
     */
    @Override
    public <T> T deserialize(String content, TypeReference<T> typeRef) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, typeRef);
    }
}
