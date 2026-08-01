package ai.wanaku.capabilities.sdk.api.types;

import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Summary information about a service template, as returned by the template listing endpoint.
 * <p>
 * Service templates are catalog packages with parameterized properties that can be
 * instantiated into service catalogs by filling in parameter values.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateSummary {
    /**
     * Default constructor for serialization frameworks.
     */
    public ServiceTemplateSummary() {}

    private String id;
    private String name;
    private String icon;
    private String description;
    private List<String> services;
    private boolean hasProperties;

    /**
     * Gets the unique identifier of the template.
     *
     * @return the template ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the template.
     *
     * @param id the template ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of the template.
     *
     * @return the template name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the template.
     *
     * @param name the template name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the icon associated with the template.
     *
     * @return the template icon, or {@code null} if not provided
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Sets the icon associated with the template.
     *
     * @param icon the template icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Gets the description of the template.
     *
     * @return the template description, or {@code null} if not provided
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the template.
     *
     * @param description the template description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the names of the services included in the template.
     *
     * @return the service names
     */
    public List<String> getServices() {
        return services;
    }

    /**
     * Sets the names of the services included in the template.
     *
     * @param services the service names to set
     */
    public void setServices(List<String> services) {
        this.services = services;
    }

    /**
     * Checks whether the template declares configurable properties.
     *
     * @return {@code true} if the template declares properties, {@code false} otherwise
     */
    public boolean isHasProperties() {
        return hasProperties;
    }

    /**
     * Sets whether the template declares configurable properties.
     *
     * @param hasProperties {@code true} if the template declares properties
     */
    public void setHasProperties(boolean hasProperties) {
        this.hasProperties = hasProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceTemplateSummary that = (ServiceTemplateSummary) o;
        return hasProperties == that.hasProperties
                && Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(icon, that.icon)
                && Objects.equals(description, that.description)
                && Objects.equals(services, that.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, icon, description, services, hasProperties);
    }

    @Override
    public String toString() {
        return "ServiceTemplateSummary{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", icon='" + icon + '\''
                + ", description='" + description + '\''
                + ", services=" + services
                + ", hasProperties=" + hasProperties
                + '}';
    }
}
