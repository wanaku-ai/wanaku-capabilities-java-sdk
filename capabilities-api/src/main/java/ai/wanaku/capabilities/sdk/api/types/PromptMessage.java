package ai.wanaku.capabilities.sdk.api.types;

import java.util.Objects;

/**
 * Represents a message in an MCP prompt.
 * Each message has a role and content according to the MCP specification.
 */
public class PromptMessage {
    /**
     * The role of the message sender.
     * Valid values: "user", "assistant"
     */
    private String role;

    /**
     * The content of the message.
     * This is a polymorphic field that can be one of:
     * - TextContent
     * - ImageContent
     * - AudioContent
     * - EmbeddedResource
     */
    private PromptContent content;

    /**
     * Default constructor for serialization frameworks.
     */
    public PromptMessage() {}

    /**
     * Creates a new PromptMessage with the specified role and content.
     *
     * @param role the role of the message sender
     * @param content the content of the message
     */
    public PromptMessage(String role, PromptContent content) {
        this.role = role;
        this.content = content;
    }

    /**
     * Gets the role of the message sender.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the message sender.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the content of the message.
     *
     * @return the message content
     */
    public PromptContent getContent() {
        return content;
    }

    /**
     * Sets the content of the message.
     *
     * @param content the content to set
     */
    public void setContent(PromptContent content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PromptMessage that = (PromptMessage) o;
        return Objects.equals(role, that.role) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, content);
    }

    @Override
    public String toString() {
        return "PromptMessage{" + "role='" + role + '\'' + ", content=" + content + '}';
    }
}
