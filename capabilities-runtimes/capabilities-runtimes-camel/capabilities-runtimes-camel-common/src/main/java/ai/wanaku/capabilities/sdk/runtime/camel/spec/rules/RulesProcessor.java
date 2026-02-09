package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules;

@FunctionalInterface
public interface RulesProcessor<T> {
    void eval(T rule);
}
