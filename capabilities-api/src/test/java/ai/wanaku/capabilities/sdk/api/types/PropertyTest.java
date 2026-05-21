package ai.wanaku.capabilities.sdk.api.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PropertyTest {

    @Test
    void defaultConstructor_fieldsAreNull() {
        Property property = new Property();
        assertNull(property.getType());
        assertNull(property.getDescription());
        assertNull(property.getTarget());
        assertNull(property.getScope());
        assertNull(property.getValue());
    }

    @Test
    void settersAndGetters() {
        Property property = new Property();
        property.setType("string");
        property.setDescription("A test property");
        property.setTarget("header");
        property.setScope("request");
        property.setValue("hello");

        assertEquals("string", property.getType());
        assertEquals("A test property", property.getDescription());
        assertEquals("header", property.getTarget());
        assertEquals("request", property.getScope());
        assertEquals("hello", property.getValue());
    }

    @Test
    void equalsAndHashCode() {
        Property a = new Property();
        a.setType("string");
        a.setDescription("desc");

        Property b = new Property();
        b.setType("string");
        b.setDescription("desc");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_differentType() {
        Property a = new Property();
        a.setType("string");

        Property b = new Property();
        b.setType("integer");

        assertNotEquals(a, b);
    }

    @Test
    void notEquals_null() {
        Property a = new Property();
        assertNotEquals(a, null);
    }
}
