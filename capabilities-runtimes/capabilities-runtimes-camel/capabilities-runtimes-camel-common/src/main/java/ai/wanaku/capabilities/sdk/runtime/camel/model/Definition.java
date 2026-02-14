package ai.wanaku.capabilities.sdk.runtime.camel.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Definition {
    @JsonProperty("route")
    private Route route;

    @JsonProperty("description")
    private String description;

    @JsonProperty("properties")
    private List<Property> properties;

    @JsonProperty("namespace")
    private String namespace;

    public Definition() {}

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
