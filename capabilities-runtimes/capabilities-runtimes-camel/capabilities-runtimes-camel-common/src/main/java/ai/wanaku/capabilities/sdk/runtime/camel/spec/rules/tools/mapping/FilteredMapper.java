package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools.mapping;

import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Mapping;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Property;
import ai.wanaku.core.exchange.ToolInvokeRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilteredMapper implements HeaderMapper {
    private static final Logger LOG = LoggerFactory.getLogger(FilteredMapper.class);

    private static Map<String, Object> convertMcpMapToCamelHeaders(
            Definition toolDefinition, Map<String, String> argumentsMap) {
        final List<Property> properties = toolDefinition.getProperties();
        Map<String, Object> headers = new HashMap<>();

        for (Property property : properties) {
            final Mapping mapping = property.getMapping();
            if (mapping != null) {
                final String mappingType = mapping.getType();

                if (mappingType.equals("header")) {
                    LOG.info(
                            "Adding header named {} with value {}",
                            mapping.getName(),
                            argumentsMap.get(property.getName()));
                    headers.put(mapping.getName(), argumentsMap.get(property.getName()));
                } else {
                    LOG.warn("Mapping type {} is not supported", mappingType);
                }
            }
        }
        return headers;
    }

    @Override
    public Map<String, Object> map(ToolInvokeRequest request, Definition toolDefinition) {
        final Map<String, String> argumentsMap = request.getArgumentsMap();
        return convertMcpMapToCamelHeaders(toolDefinition, argumentsMap);
    }
}
