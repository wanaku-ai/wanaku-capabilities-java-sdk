package ai.wanaku.capabilities.sdk.api.types;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputSchemaTest {

    @Test
    void defaultConstructor() {
        InputSchema schema = new InputSchema();
        assertNull(schema.getType());
        assertNotNull(schema.getProperties());
        assertTrue(schema.getProperties().isEmpty());
        assertNull(schema.getRequired());
    }

    @Test
    void settersAndGetters() {
        InputSchema schema = new InputSchema();
        schema.setType("object");

        Property prop = new Property();
        prop.setType("string");
        schema.setProperties(Map.of("name", prop));
        schema.setRequired(List.of("name"));

        assertEquals("object", schema.getType());
        assertEquals(1, schema.getProperties().size());
        assertEquals(List.of("name"), schema.getRequired());
    }

    @Test
    void equalsAndHashCode() {
        InputSchema a = new InputSchema();
        a.setType("object");

        InputSchema b = new InputSchema();
        b.setType("object");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_differentType() {
        InputSchema a = new InputSchema();
        a.setType("object");

        InputSchema b = new InputSchema();
        b.setType("array");

        assertNotEquals(a, b);
    }
}
