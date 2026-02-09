package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools.mapping;

import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.core.exchange.ToolInvokeRequest;
import java.util.HashMap;
import java.util.Map;

public class NoopHeaderMapper implements HeaderMapper {
    private static final Map<String, Object> EMPTY_MAP = new HashMap<>();

    @Override
    public Map<String, Object> map(ToolInvokeRequest request, Definition toolDefinition) {
        return EMPTY_MAP;
    }
}
