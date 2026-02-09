package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools.mapping;

import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;

public final class HeaderMapperFactory {

    public static HeaderMapper create(Definition toolDefinition) {
        if (toolDefinition == null) {
            return new NoopHeaderMapper();
        }

        if (toolDefinition.getProperties() == null) {
            return new AutoMapper();
        }

        return new FilteredMapper();
    }
}
