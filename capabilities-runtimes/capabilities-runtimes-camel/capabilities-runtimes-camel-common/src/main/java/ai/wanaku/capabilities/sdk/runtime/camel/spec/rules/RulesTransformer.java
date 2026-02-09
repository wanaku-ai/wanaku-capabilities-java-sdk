package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules;

import ai.wanaku.capabilities.sdk.runtime.camel.model.Definition;

@FunctionalInterface
public interface RulesTransformer {
    void transform(String name, Definition definition);
}
