package ai.wanaku.capabilities.sdk.api.types;

import java.util.Objects;

/**
 * Represents text content in a prompt message.
 * This is the most common content type for prompts.
 */
public class TextContent implements PromptContent {
    private static final String TYPE = "text";

    /**
     * The text content.
     */
    private String text;

    /**
     * Default constructor for serialization frameworks.
     */
    public TextContent() {}

    /**
     * Creates a new TextContent with the specified text.
     *
     * @param text the text content
     */
    public TextContent(String text) {
        this.text = text;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * Gets the text content.
     *
     * @return the text content
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text content.
     *
     * @param text the text content to set
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TextContent that = (TextContent) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "TextContent{" + "type='" + TYPE + '\'' + ", text='" + text + '\'' + '}';
    }
}
