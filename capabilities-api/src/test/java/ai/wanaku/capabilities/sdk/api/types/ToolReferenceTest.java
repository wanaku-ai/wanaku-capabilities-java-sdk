package ai.wanaku.capabilities.sdk.api.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ToolReferenceTest {

    @Test
    void defaultConstructor() {
        ToolReference ref = new ToolReference();
        assertNull(ref.getId());
        assertNull(ref.getName());
        assertNull(ref.getDescription());
        assertNull(ref.getUri());
        assertNull(ref.getType());
        assertNull(ref.getInputSchema());
        assertNull(ref.getConfigurationURI());
        assertNull(ref.getSecretsURI());
    }

    @Test
    void settersAndGetters() {
        ToolReference ref = new ToolReference();
        ref.setId("tool-1");
        ref.setName("myTool");
        ref.setDescription("A tool");
        ref.setUri("http://tool");
        ref.setType("http");
        ref.setNamespace("default");
        ref.setConfigurationURI("file:///config");
        ref.setSecretsURI("file:///secret");

        InputSchema schema = new InputSchema();
        ref.setInputSchema(schema);

        assertEquals("tool-1", ref.getId());
        assertEquals("myTool", ref.getName());
        assertEquals("A tool", ref.getDescription());
        assertEquals("http://tool", ref.getUri());
        assertEquals("http", ref.getType());
        assertEquals("default", ref.getNamespace());
        assertEquals(schema, ref.getInputSchema());
        assertEquals("file:///config", ref.getConfigurationURI());
        assertEquals("file:///secret", ref.getSecretsURI());
    }

    @Test
    void equalsIncludesLabels() {
        ToolReference a = new ToolReference();
        a.setId("1");
        a.setName("tool");
        a.addLabel("env", "prod");

        ToolReference b = new ToolReference();
        b.setId("1");
        b.setName("tool");
        b.addLabel("env", "prod");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_differentLabels() {
        ToolReference a = new ToolReference();
        a.setId("1");
        a.addLabel("env", "prod");

        ToolReference b = new ToolReference();
        b.setId("1");
        b.addLabel("env", "dev");

        assertNotEquals(a, b);
    }
}
