package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import org.apache.camel.CamelContext;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;
import ai.wanaku.capabilities.sdk.runtime.camel.model.McpSpec;

/**
 * Holds the resolved Camel context, MCP specification, and service target
 * that together define a registered capability service instance.
 *
 * @param camelContext the running Camel context
 * @param mcpSpec the parsed MCP tool and resource definitions
 * @param serviceTarget the service endpoint registered with the router
 */
public record WanakuRegistrationInfo(CamelContext camelContext, McpSpec mcpSpec, ServiceTarget serviceTarget) {}
