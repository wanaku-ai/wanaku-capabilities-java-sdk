package ai.wanaku.capabilities.sdk.api.types.providers;

import java.util.Objects;
import ai.wanaku.capabilities.sdk.api.types.WanakuEntity;

/**
 * Represents a target service endpoint that can be either a resource provider or a tool invoker.
 *
 * This class encapsulates information about a service, including its target and configurations,
 * providing a structured way to represent and manage services in a system.
 */
public class ServiceTarget implements WanakuEntity<String> {
    private String id;
    private String serviceName;
    private String host;
    private int port;
    private String serviceType;
    private String serviceSubType;
    private String languageName;
    private String languageType;
    private String languageSubType;

    /**
     * Default constructor for ServiceTarget.
     */
    public ServiceTarget() {}

    /**
     * Constructs a new instance of {@link ServiceTarget}.
     *
     * @param id                The unique identifier for the service.
     * @param serviceName       The name of the service.
     * @param host              The host address of the service.
     * @param port              The port number of the service.
     * @param serviceType       The type of service (e.g., "resource-provider", "tool-invoker", "code-execution-engine").
     * @param serviceSubType    The subtype of the service (e.g., "jvm", "interpreted").
     * @param languageName      The name of the programming language (e.g., "Java", "Python").
     * @param languageType      The type of the language (e.g., "compiled", "interpreted").
     * @param languageSubType   The subtype of the language (e.g., "jvm", "cpython").
     */
    public ServiceTarget(
            String id,
            String serviceName,
            String host,
            int port,
            String serviceType,
            String serviceSubType,
            String languageName,
            String languageType,
            String languageSubType) {
        this.id = id;
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
        this.serviceType = serviceType;
        this.serviceSubType = serviceSubType;
        this.languageName = languageName;
        this.languageType = languageType;
        this.languageSubType = languageSubType;
    }

    /**
     * Gets the name of the service.
     *
     * @return The name of the service.
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the name of the service.
     *
     * @param serviceName The name of the service.
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Gets the host address of the service.
     *
     * @return The host address of the service.
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port number of the service.
     *
     * @return The port number of the service.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the type of service (e.g., "resource-provider", "tool-invoker", "code-execution-engine").
     *
     * @return The type of service.
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Sets the type of service.
     *
     * @param serviceType The type of service.
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Gets the subtype of the service (e.g., "jvm", "interpreted").
     *
     * @return The subtype of the service, or null if not specified.
     */
    public String getServiceSubType() {
        return serviceSubType;
    }

    /**
     * Sets the subtype of the service.
     *
     * @param serviceSubType The subtype of the service.
     */
    public void setServiceSubType(String serviceSubType) {
        this.serviceSubType = serviceSubType;
    }

    /**
     * Gets the name of the programming language.
     *
     * @return The name of the programming language, or null if not specified.
     */
    public String getLanguageName() {
        return languageName;
    }

    /**
     * Sets the name of the programming language.
     *
     * @param languageName The name of the programming language.
     */
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    /**
     * Gets the type of the language (e.g., "compiled", "interpreted").
     *
     * @return The type of the language, or null if not specified.
     */
    public String getLanguageType() {
        return languageType;
    }

    /**
     * Sets the type of the language.
     *
     * @param languageType The type of the language.
     */
    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    /**
     * Gets the subtype of the language (e.g., "jvm", "cpython").
     *
     * @return The subtype of the language, or null if not specified.
     */
    public String getLanguageSubType() {
        return languageSubType;
    }

    /**
     * Sets the subtype of the language.
     *
     * @param languageSubType The subtype of the language.
     */
    public void setLanguageSubType(String languageSubType) {
        this.languageSubType = languageSubType;
    }

    /**
     * Returns a string representation of the service address in the format "host:port".
     *
     * @return A string representation of the service address.
     */
    public String toAddress() {
        return String.format("%s:%d", host, port);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceTarget that = (ServiceTarget) o;
        return port == that.port
                && Objects.equals(id, that.id)
                && Objects.equals(serviceName, that.serviceName)
                && Objects.equals(host, that.host)
                && Objects.equals(serviceType, that.serviceType)
                && Objects.equals(serviceSubType, that.serviceSubType)
                && Objects.equals(languageName, that.languageName)
                && Objects.equals(languageType, that.languageType)
                && Objects.equals(languageSubType, that.languageSubType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, serviceName, host, port, serviceType, serviceSubType, languageName, languageType, languageSubType);
    }

    @Override
    public String toString() {
        return "ServiceTarget{" + "id='"
                + id + '\'' + ", serviceName='"
                + serviceName + '\'' + ", host='"
                + host + '\'' + ", port="
                + port + ", serviceType='"
                + serviceType + '\'' + ", serviceSubType='"
                + serviceSubType + '\'' + ", languageName='"
                + languageName + '\'' + ", languageType='"
                + languageType + '\'' + ", languageSubType='"
                + languageSubType + '\'' + '}';
    }

    /**
     * Creates a new instance of {@link ServiceTarget} with the specified parameters with the given service type.
     *
     * @param serviceName       The name of the service.
     * @param address           The host address of the service.
     * @param port              The port number of the service.
     * @param serviceType       The type of service (e.g., "resource-provider", "tool-invoker", "code-execution-engine").
     * @param serviceSubType    The subtype of the service (e.g., "jvm", "interpreted"), or null if not applicable.
     * @param languageName      The name of the programming language, or null if not applicable.
     * @param languageType      The type of the language, or null if not applicable.
     * @param languageSubType   The subtype of the language, or null if not applicable.
     * @return A new instance of {@link ServiceTarget}.
     */
    public static ServiceTarget newEmptyTarget(
            String serviceName,
            String address,
            int port,
            String serviceType,
            String serviceSubType,
            String languageName,
            String languageType,
            String languageSubType) {
        return new ServiceTarget(
                null,
                serviceName,
                address,
                port,
                serviceType,
                serviceSubType,
                languageName,
                languageType,
                languageSubType);
    }

    /**
     * Creates a new instance of {@link ServiceTarget} with the specified parameters without a service subtype.
     * This is a convenience method for services that don't require a subtype or language information.
     *
     * @param serviceName The name of the service.
     * @param address The host address of the service.
     * @param port The port number of the service.
     * @param serviceType The type of service (e.g., "resource-provider", "tool-invoker").
     * @return A new instance of {@link ServiceTarget}.
     */
    public static ServiceTarget newEmptyTarget(String serviceName, String address, int port, String serviceType) {
        return new ServiceTarget(null, serviceName, address, port, serviceType, null, null, null, null);
    }
}
