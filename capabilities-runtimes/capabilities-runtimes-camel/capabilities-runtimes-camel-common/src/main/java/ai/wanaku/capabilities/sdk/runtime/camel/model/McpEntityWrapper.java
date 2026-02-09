package ai.wanaku.capabilities.sdk.runtime.camel.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonDeserialize(using = McpEntityWrapper.McpEntityDeserializer.class)
public class McpEntityWrapper {
    private Map<String, Definition> tools = new HashMap<>();

    public McpEntityWrapper() {}

    public Map<String, Definition> getDefinitions() {
        return tools;
    }

    public void setTools(Map<String, Definition> tools) {
        this.tools = tools;
    }

    public static class McpEntityDeserializer extends JsonDeserializer<McpEntityWrapper> {
        @Override
        public McpEntityWrapper deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            McpEntityWrapper wrapper = new McpEntityWrapper();
            ObjectMapper mapper = (ObjectMapper) p.getCodec();

            if (p.currentToken() == JsonToken.START_ARRAY) {
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    if (p.currentToken() == JsonToken.START_OBJECT) {
                        p.nextToken(); // Move to field name
                        String toolName = p.getCurrentName();
                        p.nextToken(); // Move to tool definition object
                        Definition toolDef = mapper.readValue(p, Definition.class);
                        wrapper.tools.put(toolName, toolDef);
                        p.nextToken(); // Move past the tool definition object
                    }
                }
            }

            return wrapper;
        }
    }
}
