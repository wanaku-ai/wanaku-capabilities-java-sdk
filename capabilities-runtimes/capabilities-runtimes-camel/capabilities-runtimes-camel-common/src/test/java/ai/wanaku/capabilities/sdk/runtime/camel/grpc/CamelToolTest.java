package ai.wanaku.capabilities.sdk.runtime.camel.grpc;

import java.util.List;
import java.util.Map;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;
import ai.wanaku.capabilities.sdk.runtime.camel.model.Property;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CamelToolTest {

    @Test
    void findMissingRequiredParametersReturnsEmptyWhenAllPresent() {
        Definition definition = definitionWithProperties(requiredProperty("city"), requiredProperty("country"));
        List<String> missing =
                CamelTool.findMissingRequiredParameters(definition, Map.of("city", "London", "country", "UK"));

        assertTrue(missing.isEmpty());
    }

    @Test
    void findMissingRequiredParametersReportsMissing() {
        Definition definition = definitionWithProperties(requiredProperty("city"), requiredProperty("country"));
        List<String> missing = CamelTool.findMissingRequiredParameters(definition, Map.of("city", "London"));

        assertEquals(List.of("country"), missing);
    }

    @Test
    void findMissingRequiredParametersReportsAllMissing() {
        Definition definition = definitionWithProperties(requiredProperty("city"), requiredProperty("country"));
        List<String> missing = CamelTool.findMissingRequiredParameters(definition, Map.of());

        assertEquals(2, missing.size());
        assertTrue(missing.contains("city"));
        assertTrue(missing.contains("country"));
    }

    @Test
    void findMissingRequiredParametersIgnoresOptional() {
        Property optional = new Property();
        optional.setName("format");
        optional.setRequired(false);

        Definition definition = definitionWithProperties(requiredProperty("city"), optional);
        List<String> missing = CamelTool.findMissingRequiredParameters(definition, Map.of());

        assertEquals(List.of("city"), missing);
    }

    @Test
    void findMissingRequiredParametersHandlesNullProperties() {
        Definition definition = new Definition();
        List<String> missing = CamelTool.findMissingRequiredParameters(definition, Map.of());

        assertTrue(missing.isEmpty());
    }

    @Test
    void findMissingRequiredParametersIgnoresNullName() {
        Property nullName = new Property();
        nullName.setRequired(true);

        Definition definition = definitionWithProperties(requiredProperty("city"), nullName);
        List<String> missing = CamelTool.findMissingRequiredParameters(definition, Map.of());

        assertEquals(List.of("city"), missing);
    }

    @Test
    void findMissingRequiredParametersHandlesNoRequiredProperties() {
        Property optional = new Property();
        optional.setName("format");
        optional.setRequired(false);

        Definition definition = definitionWithProperties(optional);
        List<String> missing = CamelTool.findMissingRequiredParameters(definition, Map.of());

        assertTrue(missing.isEmpty());
    }

    private static Property requiredProperty(String name) {
        Property property = new Property();
        property.setName(name);
        property.setRequired(true);
        return property;
    }

    private static Definition definitionWithProperties(Property... properties) {
        Definition definition = new Definition();
        definition.setProperties(List.of(properties));
        return definition;
    }
}
