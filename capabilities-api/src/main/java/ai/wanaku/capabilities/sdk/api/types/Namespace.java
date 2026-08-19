package ai.wanaku.capabilities.sdk.api.types;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a namespace within the Wanaku system.
 * <p>
 * Namespaces provide logical grouping and isolation for capabilities, tools, and resources.
 * The name serves as the unique identifier, URL path segment, and display name.
 * Names must follow DNS-label conventions: lowercase alphanumeric and hyphens,
 * 1-63 chars, must start and end with an alphanumeric character.
 */
public class Namespace extends LabelsAwareEntity<String> {
    /**
     * Default constructor for serialization frameworks.
     */
    public Namespace() {}

    private String name;

    @JsonIgnore
    @Override
    public String getId() {
        return name;
    }

    @JsonIgnore
    @Override
    public void setId(String id) {
        this.name = id;
    }

    /**
     * Gets the namespace name, which also serves as its identifier and URL path segment.
     *
     * @return the namespace name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the namespace name.
     *
     * @param name the namespace name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
