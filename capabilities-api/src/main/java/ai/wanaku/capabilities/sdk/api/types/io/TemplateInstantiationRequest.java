package ai.wanaku.capabilities.sdk.api.types.io;

import java.util.Map;
import java.util.Objects;

/**
 * Request payload for instantiating a service template.
 * <p>
 * Service templates are catalog packages with parameterized properties that can be
 * instantiated into service catalogs by filling in parameter values. This request
 * identifies the template to instantiate and provides the property values to apply.
 */
public final class TemplateInstantiationRequest {
    /**
     * Default constructor for serialization frameworks.
     */
    public TemplateInstantiationRequest() {}

    /**
     * Creates a new TemplateInstantiationRequest for the specified template.
     *
     * @param templateName the name of the template to instantiate
     */
    public TemplateInstantiationRequest(String templateName) {
        this.templateName = templateName;
    }

    private String templateName;
    private Map<String, String> properties;
    private String serviceName;
    private String serviceSystem;

    /**
     * Gets the name of the template to instantiate.
     *
     * @return the template name
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Sets the name of the template to instantiate.
     *
     * @param templateName the template name to set
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * Gets the property values to fill in when instantiating the template.
     *
     * @return the property values, or {@code null} if not provided
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Sets the property values to fill in when instantiating the template.
     *
     * @param properties the property values to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Gets the name to assign to the instantiated service.
     *
     * @return the service name, or {@code null} if not provided
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the name to assign to the instantiated service.
     *
     * @param serviceName the service name to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Gets the system to assign to the instantiated service.
     *
     * @return the service system, or {@code null} if not provided
     */
    public String getServiceSystem() {
        return serviceSystem;
    }

    /**
     * Sets the system to assign to the instantiated service.
     *
     * @param serviceSystem the service system to set
     */
    public void setServiceSystem(String serviceSystem) {
        this.serviceSystem = serviceSystem;
    }

    /**
     * Validates this request to ensure all required fields are present.
     *
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (templateName == null || templateName.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name must not be null or empty");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemplateInstantiationRequest that = (TemplateInstantiationRequest) o;
        return Objects.equals(templateName, that.templateName)
                && Objects.equals(properties, that.properties)
                && Objects.equals(serviceName, that.serviceName)
                && Objects.equals(serviceSystem, that.serviceSystem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateName, properties, serviceName, serviceSystem);
    }

    @Override
    public String toString() {
        return "TemplateInstantiationRequest{"
                + "templateName='" + templateName + '\''
                + ", properties=" + properties
                + ", serviceName='" + serviceName + '\''
                + ", serviceSystem='" + serviceSystem + '\''
                + '}';
    }
}
