package ai.wanaku.capabilities.sdk.api.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PromptMessageTest {

    @Test
    void defaultConstructor() {
        PromptMessage message = new PromptMessage();
        assertNull(message.getRole());
        assertNull(message.getContent());
    }

    @Test
    void parameterizedConstructor() {
        TextContent content = new TextContent("hello");
        PromptMessage message = new PromptMessage("user", content);
        assertEquals("user", message.getRole());
        assertEquals(content, message.getContent());
    }

    @Test
    void settersAndGetters() {
        PromptMessage message = new PromptMessage();
        message.setRole("assistant");
        TextContent content = new TextContent("response");
        message.setContent(content);

        assertEquals("assistant", message.getRole());
        assertEquals(content, message.getContent());
    }

    @Test
    void equalsAndHashCode() {
        TextContent content = new TextContent("hello");
        PromptMessage a = new PromptMessage("user", content);
        PromptMessage b = new PromptMessage("user", content);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_differentRole() {
        TextContent content = new TextContent("hello");
        PromptMessage a = new PromptMessage("user", content);
        PromptMessage b = new PromptMessage("assistant", content);
        assertNotEquals(a, b);
    }

    @Test
    void equalsSameInstance() {
        PromptMessage a = new PromptMessage("user", new TextContent("hello"));
        assertEquals(a, a);
    }
}
