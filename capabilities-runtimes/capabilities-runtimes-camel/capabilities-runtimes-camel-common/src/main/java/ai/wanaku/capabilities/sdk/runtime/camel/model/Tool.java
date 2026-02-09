package ai.wanaku.capabilities.sdk.runtime.camel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Tool {
    @JsonProperty("tools")
    private McpEntityWrapper toolsWrapper;

    public Tool() {}

    public Map<String, Definition> getTools() {
        return toolsWrapper != null ? toolsWrapper.getDefinitions() : null;
    }

    public void setToolsWrapper(McpEntityWrapper toolsWrapper) {
        this.toolsWrapper = toolsWrapper;
    }
}
