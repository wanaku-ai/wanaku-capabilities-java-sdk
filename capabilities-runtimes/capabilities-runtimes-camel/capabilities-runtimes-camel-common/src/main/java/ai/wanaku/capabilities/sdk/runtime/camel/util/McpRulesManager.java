package ai.wanaku.capabilities.sdk.runtime.camel.util;

import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.capabilities.sdk.runtime.camel.model.McpSpec;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.RulesTransformer;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpRulesManager {
    private static final Logger LOG = LoggerFactory.getLogger(McpRulesManager.class);
    private final String name;
    private final String routesRules;

    public McpRulesManager(String name, String routesRules) {
        this.name = name;
        this.routesRules = routesRules;
    }

    private McpSpec loadMcpSpec() {
        if (routesRules == null || routesRules.isEmpty()) {
            LOG.warn("No routes rules file specified");
            return null;
        }

        try {
            McpSpec mcpSpec = McpRulesReader.readMcpSpecFromFile(routesRules);
            LOG.info("Successfully loaded MCP spec from: {}", routesRules);
            return mcpSpec;
        } catch (IOException e) {
            LOG.error("Failed to load MCP spec from: {}", routesRules, e);
            throw new RuntimeException("Failed to load MCP spec", e);
        }
    }

    private <T> void registerDefinitions(Map<String, Definition> definitions, RulesTransformer transformer) {
        if (definitions == null) {
            return;
        }

        for (Map.Entry<String, Definition> entry : definitions.entrySet()) {
            final Definition toolDef = entry.getValue();
            transformer.transform(entry.getKey(), toolDef);
        }
    }

    public <T> McpSpec loadMcpSpecAndRegister(RulesTransformer toolTransformer, RulesTransformer resourceTransformer) {
        McpSpec mcpSpec = loadMcpSpec();
        if (mcpSpec != null && mcpSpec.getMcp() != null) {
            McpSpec.McpContent mcp = mcpSpec.getMcp();

            // Register tools
            if (mcp.getTools() != null) {
                LOG.info("Registering {} tools", mcp.getTools().getDefinitions().size());
                registerDefinitions(mcp.getTools().getDefinitions(), toolTransformer);
            }

            // Register resources
            if (mcp.getResources() != null) {
                LOG.info(
                        "Registering {} resources",
                        mcp.getResources().getDefinitions().size());
                registerDefinitions(mcp.getResources().getDefinitions(), resourceTransformer);
            }
        } else {
            LOG.warn("No MCP spec registered for {}", name);
        }

        return mcpSpec;
    }
}
