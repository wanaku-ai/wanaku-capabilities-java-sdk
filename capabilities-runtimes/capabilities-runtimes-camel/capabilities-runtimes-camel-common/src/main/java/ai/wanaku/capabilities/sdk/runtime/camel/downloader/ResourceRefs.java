package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.net.URI;

public record ResourceRefs<T>(ResourceType resourceType, T ref) {

    public static ResourceRefs<URI> newRoutesRef(String routesRef) {
        return new ResourceRefs<>(ResourceType.ROUTES_REF, URI.create(routesRef));
    }

    public static ResourceRefs<URI> newRulesRef(String rulesRef) {
        return new ResourceRefs<>(ResourceType.RULES_REF, URI.create(rulesRef));
    }

    public static ResourceRefs<URI> newDependencyRef(String dependencyRef) {
        return new ResourceRefs<>(ResourceType.DEPENDENCY_REF, URI.create(dependencyRef));
    }
}
