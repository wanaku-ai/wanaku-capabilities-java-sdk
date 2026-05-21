package ai.wanaku.capabilities.sdk.api.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextContentTest {

    @Test
    void defaultConstructor() {
        TextContent content = new TextContent();
        assertNull(content.getText());
        assertEquals("text", content.getType());
    }

    @Test
    void parameterizedConstructor() {
        TextContent content = new TextContent("hello");
        assertEquals("hello", content.getText());
        assertEquals("text", content.getType());
    }

    @Test
    void setText() {
        TextContent content = new TextContent();
        content.setText("world");
        assertEquals("world", content.getText());
    }

    @Test
    void equalsAndHashCode() {
        TextContent a = new TextContent("hello");
        TextContent b = new TextContent("hello");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals() {
        TextContent a = new TextContent("hello");
        TextContent b = new TextContent("world");
        assertNotEquals(a, b);
    }

    @Test
    void toStringContainsText() {
        TextContent content = new TextContent("hello");
        String str = content.toString();
        assertTrue(str.contains("hello"));
        assertTrue(str.contains("text"));
    }
}
