package ai.wanaku.capabilities.sdk.api.types;

import java.util.List;
import java.util.Objects;

/**
 * Represents a prompt reference in the MCP protocol.
 * Prompts are reusable templates that can leverage multiple tools and provide
 * example interactions for LLMs.
 */
public class PromptReference implements WanakuEntity<String> {
    /**
     * Default constructor for serialization frameworks.
     */
    public PromptReference() {}

    private String id;

    /**
     * The name of the prompt.
     */
    private String name;

    /**
     * A description of what the prompt does.
     */
    private String description;

    /**
     * The prompt messages following MCP specification.
     * Each message has a role (user, assistant) and content.
     */
    private List<PromptMessage> messages;

    /**
     * List of arguments/parameters this prompt accepts.
     */
    private List<PromptArgument> arguments;

    /**
     * Optional list of tool names that this prompt may use.
     */
    private List<String> toolReferences;

    /**
     * The namespace this prompt belongs to.
     */
    private String namespace;

    /**
     * Configuration URI for the prompt.
     */
    private String configurationURI;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of the prompt.
     *
     * @return the prompt name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the prompt.
     *
     * @param name the prompt name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of what the prompt does.
     *
     * @return the prompt description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of what the prompt does.
     *
     * @param description the prompt description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the prompt messages following MCP specification.
     *
     * @return the list of prompt messages
     */
    public List<PromptMessage> getMessages() {
        return messages;
    }

    /**
     * Sets the prompt messages following MCP specification.
     *
     * @param messages the list of prompt messages to set
     */
    public void setMessages(List<PromptMessage> messages) {
        this.messages = messages;
    }

    /**
     * Gets the list of arguments/parameters this prompt accepts.
     *
     * @return the list of prompt arguments
     */
    public List<PromptArgument> getArguments() {
        return arguments;
    }

    /**
     * Sets the list of arguments/parameters this prompt accepts.
     *
     * @param arguments the list of prompt arguments to set
     */
    public void setArguments(List<PromptArgument> arguments) {
        this.arguments = arguments;
    }

    /**
     * Gets the optional list of tool names that this prompt may use.
     *
     * @return the list of tool references
     */
    public List<String> getToolReferences() {
        return toolReferences;
    }

    /**
     * Sets the optional list of tool names that this prompt may use.
     *
     * @param toolReferences the list of tool references to set
     */
    public void setToolReferences(List<String> toolReferences) {
        this.toolReferences = toolReferences;
    }

    /**
     * Gets the namespace this prompt belongs to.
     *
     * @return the namespace identifier
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the namespace this prompt belongs to.
     *
     * @param namespace the namespace identifier to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Gets the configuration URI for the prompt.
     *
     * @return the configuration URI
     */
    public String getConfigurationURI() {
        return configurationURI;
    }

    /**
     * Sets the configuration URI for the prompt.
     *
     * @param configurationURI the configuration URI to set
     */
    public void setConfigurationURI(String configurationURI) {
        this.configurationURI = configurationURI;
    }

    /**
     * Represents an argument/parameter for a prompt.
     */
    public static class PromptArgument {
        /**
         * Default constructor for serialization frameworks.
         */
        public PromptArgument() {}

        /**
         * The name of the argument.
         */
        private String name;

        /**
         * A description of the argument.
         */
        private String description;

        /**
         * Whether this argument is required.
         */
        private boolean required;

        /**
         * Gets the name of the argument.
         *
         * @return the argument name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the argument.
         *
         * @param name the argument name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the description of the argument.
         *
         * @return the argument description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the description of the argument.
         *
         * @param description the argument description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Checks if this argument is required.
         *
         * @return {@code true} if required, {@code false} otherwise
         */
        public boolean isRequired() {
            return required;
        }

        /**
         * Sets whether this argument is required.
         *
         * @param required the required flag to set
         */
        public void setRequired(boolean required) {
            this.required = required;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PromptArgument that = (PromptArgument) o;
            return required == that.required
                    && Objects.equals(name, that.name)
                    && Objects.equals(description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description, required);
        }

        @Override
        public String toString() {
            return "PromptArgument{" + "name='"
                    + name + '\'' + ", description='"
                    + description + '\'' + ", required="
                    + required + '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PromptReference that = (PromptReference) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(messages, that.messages)
                && Objects.equals(arguments, that.arguments)
                && Objects.equals(toolReferences, that.toolReferences)
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(configurationURI, that.configurationURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, messages, arguments, toolReferences, namespace, configurationURI);
    }

    @Override
    public String toString() {
        return "PromptReference{" + "id='"
                + id + '\'' + ", name='"
                + name + '\'' + ", description='"
                + description + '\'' + ", messages="
                + messages + ", arguments="
                + arguments + ", toolReferences="
                + toolReferences + ", namespace='"
                + namespace + '\'' + ", configurationURI='"
                + configurationURI + '\'' + '}';
    }
}
