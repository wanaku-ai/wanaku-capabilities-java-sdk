package ai.wanaku.capabilities.sdk.api.types;

import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Detailed information about a service template, as returned by the template retrieval endpoint.
 * <p>
 * In addition to the template metadata, the detail includes per-system information about
 * the files that make up each service definition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateDetail {
    /**
     * Default constructor for serialization frameworks.
     */
    public ServiceTemplateDetail() {}

    private String id;
    private String name;
    private String icon;
    private String description;
    private List<ServiceTemplateSystem> services;

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
     * Gets the per-system information for the services included in the template.
     *
     * @return the service system information
     */
    public List<ServiceTemplateSystem> getServices() {
        return services;
    }

    /**
     * Sets the per-system information for the services included in the template.
     *
     * @param services the service system information to set
     */
    public void setServices(List<ServiceTemplateSystem> services) {
        this.services = services;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceTemplateDetail that = (ServiceTemplateDetail) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(icon, that.icon)
                && Objects.equals(description, that.description)
                && Objects.equals(services, that.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, icon, description, services);
    }

    @Override
    public String toString() {
        return "ServiceTemplateDetail{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", icon='" + icon + '\''
                + ", description='" + description + '\''
                + ", services=" + services
                + '}';
    }
}
