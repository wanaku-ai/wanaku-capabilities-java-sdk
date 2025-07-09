package ai.wanaku.capabilities.sdk.discovery.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonDeserializer implements Deserializer {

    @Override
    public  <T> T deserialize(String content, TypeReference<T> typeRef) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, typeRef);
    }
}