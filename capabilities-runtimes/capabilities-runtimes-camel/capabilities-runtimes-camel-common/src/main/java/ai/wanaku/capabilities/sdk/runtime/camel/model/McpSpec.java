package ai.wanaku.capabilities.sdk.runtime.camel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class McpSpec {
    @JsonProperty("mcp")
    private McpContent mcp;

    public McpSpec() {}

    public McpContent getMcp() {
        return mcp;
    }

    public void setMcp(McpContent mcp) {
        this.mcp = mcp;
    }

    public static class McpContent {
        @JsonProperty("tools")
        private McpEntityWrapper tools;

        @JsonProperty("resources")
        private McpEntityWrapper resources;

        public McpContent() {}

        public McpEntityWrapper getTools() {
            return tools;
        }

        public void setTools(McpEntityWrapper tools) {
            this.tools = tools;
        }

        public McpEntityWrapper getResources() {
            return resources;
        }

        public void setResources(McpEntityWrapper resources) {
            this.resources = resources;
        }
    }
}
