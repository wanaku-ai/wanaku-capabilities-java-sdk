package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import org.apache.camel.CamelContext;
import ai.wanaku.capabilities.sdk.runtime.camel.model.McpSpec;

public record WanakuRegistrationInfo(CamelContext camelContext, McpSpec mcpSpec) {}
