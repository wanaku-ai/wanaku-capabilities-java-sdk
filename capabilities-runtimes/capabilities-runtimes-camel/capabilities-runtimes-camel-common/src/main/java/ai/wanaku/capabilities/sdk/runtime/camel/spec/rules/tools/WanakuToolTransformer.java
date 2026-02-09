package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools;

import ai.wanaku.capabilities.sdk.api.types.InputSchema;
import ai.wanaku.capabilities.sdk.api.types.Property;
import ai.wanaku.capabilities.sdk.api.types.ToolReference;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.RulesProcessor;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.RulesTransformer;
import java.util.ArrayList;
import java.util.List;

public class WanakuToolTransformer implements RulesTransformer {
    public static final String DEFAULT_INPUT_SCHEMA_TYPE = "object";
    private final String name;
    private final RulesProcessor<ToolReference> processor;
    private final List<String> required = new ArrayList<>();

    public WanakuToolTransformer(String name, RulesProcessor<ToolReference> processor) {
        this.name = name;
        this.processor = processor;
    }

    @Override
    public void transform(String ruleName, Definition toolDefinition) {
        ToolReference toolReference = new ToolReference();

        toolReference.setName(ruleName);
        toolReference.setDescription(toolDefinition.getDescription());
        toolReference.setUri(String.format("%s://%s", name, ruleName));
        toolReference.setType(name);
        toolReference.setNamespace(toolDefinition.getNamespace());

        InputSchema inputSchema = new InputSchema();
        inputSchema.setType(DEFAULT_INPUT_SCHEMA_TYPE);

        final List<ai.wanaku.capabilities.sdk.runtime.camel.model.Property> properties = toolDefinition.getProperties();
        parseProperties(properties, inputSchema);

        toolReference.setInputSchema(inputSchema);

        processor.eval(toolReference);
    }

    private void parseProperties(List<ai.wanaku.capabilities.sdk.runtime.camel.model.Property> properties, InputSchema inputSchema) {
        if (properties == null) {
            return;
        }

        for (var property : properties) {
            Property wanakuProperty = new Property();
            wanakuProperty.setType(property.getType());
            wanakuProperty.setDescription(property.getDescription());

            if (property.isRequired()) {
                required.add(property.getName());
            }

            inputSchema.getProperties().put(property.getName(), wanakuProperty);
        }

        if (!required.isEmpty()) {
            inputSchema.setRequired(required);
        }
    }
}
