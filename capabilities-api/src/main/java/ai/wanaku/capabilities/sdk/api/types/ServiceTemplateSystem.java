package ai.wanaku.capabilities.sdk.api.types;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Per-system information within a service template, describing the files that make up
 * the service definition for that system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateSystem {
    /**
     * Default constructor for serialization frameworks.
     */
    public ServiceTemplateSystem() {}

    private String name;
    private String routesFile;
    private String rulesFile;
    private String dependenciesFile;
    private String propertiesFile;

    /**
     * Gets the name of the system.
     *
     * @return the system name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the system.
     *
     * @param name the system name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the routes file for this system.
     *
     * @return the routes file, or {@code null} if not provided
     */
    public String getRoutesFile() {
        return routesFile;
    }

    /**
     * Sets the routes file for this system.
     *
     * @param routesFile the routes file to set
     */
    public void setRoutesFile(String routesFile) {
        this.routesFile = routesFile;
    }

    /**
     * Gets the rules file for this system.
     *
     * @return the rules file, or {@code null} if not provided
     */
    public String getRulesFile() {
        return rulesFile;
    }

    /**
     * Sets the rules file for this system.
     *
     * @param rulesFile the rules file to set
     */
    public void setRulesFile(String rulesFile) {
        this.rulesFile = rulesFile;
    }

    /**
     * Gets the dependencies file for this system.
     *
     * @return the dependencies file, or {@code null} if not provided
     */
    public String getDependenciesFile() {
        return dependenciesFile;
    }

    /**
     * Sets the dependencies file for this system.
     *
     * @param dependenciesFile the dependencies file to set
     */
    public void setDependenciesFile(String dependenciesFile) {
        this.dependenciesFile = dependenciesFile;
    }

    /**
     * Gets the properties file for this system.
     *
     * @return the properties file, or {@code null} if not provided
     */
    public String getPropertiesFile() {
        return propertiesFile;
    }

    /**
     * Sets the properties file for this system.
     *
     * @param propertiesFile the properties file to set
     */
    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceTemplateSystem that = (ServiceTemplateSystem) o;
        return Objects.equals(name, that.name)
                && Objects.equals(routesFile, that.routesFile)
                && Objects.equals(rulesFile, that.rulesFile)
                && Objects.equals(dependenciesFile, that.dependenciesFile)
                && Objects.equals(propertiesFile, that.propertiesFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, routesFile, rulesFile, dependenciesFile, propertiesFile);
    }

    @Override
    public String toString() {
        return "ServiceTemplateSystem{"
                + "name='" + name + '\''
                + ", routesFile='" + routesFile + '\''
                + ", rulesFile='" + rulesFile + '\''
                + ", dependenciesFile='" + dependenciesFile + '\''
                + ", propertiesFile='" + propertiesFile + '\''
                + '}';
    }
}
