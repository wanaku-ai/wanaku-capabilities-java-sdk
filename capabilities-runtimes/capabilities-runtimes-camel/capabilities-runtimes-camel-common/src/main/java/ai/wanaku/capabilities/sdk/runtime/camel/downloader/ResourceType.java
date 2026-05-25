package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

/**
 * Identifies the type of downloadable resource within a service catalog.
 */
public enum ResourceType {
    /** Camel route definitions. */
    ROUTES_REF,
    /** MCP rule files that govern tool and resource behaviour. */
    RULES_REF,
    /** Maven dependency coordinates required at runtime. */
    DEPENDENCY_REF,
    /** Configuration properties files. */
    PROPERTIES_REF,
}
