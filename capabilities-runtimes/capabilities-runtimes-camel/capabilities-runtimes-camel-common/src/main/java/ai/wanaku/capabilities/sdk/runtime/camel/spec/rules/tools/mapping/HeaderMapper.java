package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools.mapping;

import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.core.exchange.ToolInvokeRequest;
import java.util.Map;

public interface HeaderMapper {

    Map<String, Object> map(ToolInvokeRequest request, Definition toolDefinition);
}
