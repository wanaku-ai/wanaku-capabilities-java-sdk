package ai.wanaku.capabilities.sdk.discovery.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Defines the contract for deserializing string content, typically JSON, into Java objects.
 */
public interface Deserializer {
    /**
     * Deserializes the given content into an object of the specified type.
     *
     * @param <T> The type of the object to deserialize to.
     * @param content The string content to deserialize.
     * @param typeRef A {@link TypeReference} to specify the target type, especially for generic types.
     * @return The deserialized object.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    <T> T deserialize(String content, TypeReference<T> typeRef) throws JsonProcessingException;
}
