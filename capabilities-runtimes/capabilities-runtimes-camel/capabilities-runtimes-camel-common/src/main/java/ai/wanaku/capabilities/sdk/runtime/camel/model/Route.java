package ai.wanaku.capabilities.sdk.runtime.camel.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Route {
    @JsonProperty("uri")
    private String uri;

    @JsonProperty("id")
    private String id;

    public Route() {}

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
