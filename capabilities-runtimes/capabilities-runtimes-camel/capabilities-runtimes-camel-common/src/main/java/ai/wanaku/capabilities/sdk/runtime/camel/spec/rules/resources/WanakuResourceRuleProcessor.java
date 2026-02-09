package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.resources;

import ai.wanaku.capabilities.sdk.api.types.ResourceReference;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.RulesProcessor;
import ai.wanaku.capabilities.sdk.services.ServicesHttpClient;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WanakuResourceRuleProcessor implements RulesProcessor<ResourceReference> {
    private static final Logger LOG = LoggerFactory.getLogger(WanakuResourceRuleProcessor.class);

    private final ServicesHttpClient servicesClient;
    private final List<ResourceReference> registered = new CopyOnWriteArrayList<>();

    public WanakuResourceRuleProcessor(ServicesHttpClient servicesClient) {
        this.servicesClient = servicesClient;

        Runtime.getRuntime().addShutdownHook(new Thread(this::deregisterResources));
    }

    @Override
    public void eval(ResourceReference toolReference) {
        try {
            registered.add(toolReference);
            servicesClient.exposeResource(toolReference);
        } catch (Exception e) {
            LOG.warn("Unable to expose resource {}", e.getMessage(), e);
        }
    }

    private void deregisterResources() {
        for (ResourceReference ref : registered) {
            try {
                LOG.debug("Deregistering resource {}", ref);
                servicesClient.removeResource(ref.getName());
            } catch (Exception e) {
                LOG.warn("Unable to deregister tool {}: {}", ref.getName(), e.getMessage());
            }
        }
    }
}
