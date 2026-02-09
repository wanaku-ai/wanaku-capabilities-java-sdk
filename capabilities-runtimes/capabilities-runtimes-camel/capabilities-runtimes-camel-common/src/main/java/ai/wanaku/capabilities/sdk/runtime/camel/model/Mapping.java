package ai.wanaku.capabilities.sdk.runtime.camel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Mapping {
    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    public Mapping() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
