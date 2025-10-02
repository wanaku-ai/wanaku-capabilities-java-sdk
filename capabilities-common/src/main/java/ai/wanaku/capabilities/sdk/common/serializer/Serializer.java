package ai.wanaku.capabilities.sdk.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Defines the contract for serializing Java objects into a string representation, typically JSON.
 */
public interface Serializer {
    /**
     * Serializes the given object into a string.
     *
     * @param <T> The type of the object to serialize.
     * @param object The object to serialize.
     * @return The serialized string representation of the object.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    <T> String serialize(T object) throws JsonProcessingException;
}
