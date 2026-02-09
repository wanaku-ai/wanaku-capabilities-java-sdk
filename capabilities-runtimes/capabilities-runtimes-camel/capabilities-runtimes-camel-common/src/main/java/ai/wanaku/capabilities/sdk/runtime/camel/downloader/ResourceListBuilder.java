package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating a list of resource references to be used with ResourceDownloaderCallback.
 * Provides a fluent API for constructing resource lists while handling optional parameters.
 */
public class ResourceListBuilder {
    private final List<ResourceRefs<URI>> resources = new ArrayList<>();
    private boolean hasRoutesRef = false;

    private ResourceListBuilder() {}

    /**
     * Creates a new ResourceListBuilder instance.
     *
     * @return a new builder instance
     */
    public static ResourceListBuilder newBuilder() {
        return new ResourceListBuilder();
    }

    /**
     * Adds a routes reference to the resource list.
     * This is a required resource and must be called before build().
     *
     * @param routesRef the routes reference URI string (e.g., "datastore://routes.yaml" or "file:///path/to/routes.yaml")
     * @return this builder for method chaining
     */
    public ResourceListBuilder addRoutesRef(String routesRef) {
        if (routesRef != null && !routesRef.isEmpty()) {
            resources.add(ResourceRefs.newRoutesRef(routesRef));
            hasRoutesRef = true;
        }
        return this;
    }

    /**
     * Adds a rules reference to the resource list.
     * This is an optional resource.
     *
     * @param rulesRef the rules reference URI string (e.g., "datastore://rules.yaml" or "file:///path/to/rules.yaml")
     * @return this builder for method chaining
     */
    public ResourceListBuilder addRulesRef(String rulesRef) {
        if (rulesRef != null && !rulesRef.isEmpty()) {
            resources.add(ResourceRefs.newRulesRef(rulesRef));
        }
        return this;
    }

    /**
     * Adds a dependencies reference to the resource list.
     * This is an optional resource.
     *
     * @param dependenciesRef the dependencies reference URI string (e.g., "datastore://dependencies.txt" or "file:///path/to/dependencies.txt")
     * @return this builder for method chaining
     */
    public ResourceListBuilder addDependenciesRef(String dependenciesRef) {
        if (dependenciesRef != null && !dependenciesRef.isEmpty()) {
            resources.add(ResourceRefs.newDependencyRef(dependenciesRef));
        }
        return this;
    }

    /**
     * Builds and returns an unmodifiable list of resource references.
     *
     * @return an unmodifiable list of ResourceRefs
     * @throws IllegalStateException if routes reference was not provided
     */
    public List<ResourceRefs<URI>> build() {
        if (!hasRoutesRef) {
            throw new IllegalStateException("Routes reference is required but was not provided");
        }
        return List.copyOf(resources);
    }

    /**
     * Builds and returns an unmodifiable list of resource references.
     *
     * @return an unmodifiable list of ResourceRefs
     * @throws IllegalStateException if routes reference was not provided
     */
    public List<ResourceRefs<URI>> buildForPlugin() {
        return List.copyOf(resources);
    }
}
