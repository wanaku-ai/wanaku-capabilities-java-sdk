package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools.mapping;

import java.util.HashMap;
import java.util.Map;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.core.exchange.v1.ToolInvokeRequest;

public class AutoMapper implements HeaderMapper {

    private static Map<String, Object> convertMcpMapToCamelHeaders(Map<String, String> argumentsMap) {
        Map<String, Object> headers = new HashMap<>();

        for (var entry : argumentsMap.entrySet()) {
            headers.put(String.format("Wanaku.%s", entry.getKey()), entry.getValue());
        }

        return headers;
    }

    @Override
    public Map<String, Object> map(ToolInvokeRequest request, Definition toolDefinition) {
        final Map<String, String> argumentsMap = request.getArgumentsMap();
        return convertMcpMapToCamelHeaders(argumentsMap);
    }
}
