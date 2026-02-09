package ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.tools;

import ai.wanaku.capabilities.sdk.api.types.ToolReference;
import ai.wanaku.capabilities.sdk.common.exceptions.WanakuWebException;
import ai.wanaku.capabilities.sdk.runtime.camel.spec.rules.RulesProcessor;
import ai.wanaku.capabilities.sdk.services.ServicesHttpClient;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WanakuToolRuleProcessor implements RulesProcessor<ToolReference> {
    private static final Logger LOG = LoggerFactory.getLogger(WanakuToolRuleProcessor.class);

    private final ServicesHttpClient servicesClient;
    private final List<ToolReference> registered = new CopyOnWriteArrayList<>();

    public WanakuToolRuleProcessor(ServicesHttpClient servicesClient) {
        this.servicesClient = servicesClient;

        Runtime.getRuntime().addShutdownHook(new Thread(this::deregisterTools));
    }

    @Override
    public void eval(ToolReference toolReference) {
        try {
            servicesClient.addTool(toolReference);
            registered.add(toolReference);
        } catch (WanakuWebException e) {
            if (e.getStatusCode() == 409) {
                LOG.warn("Skipping adding tool {} because it already exists", toolReference.getName());
            } else {
                LOG.warn("Unable to add tool {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            LOG.warn("Unable to add tool {}", e.getMessage(), e);
        }
    }

    private void deregisterTools() {
        for (ToolReference ref : registered) {
            try {
                LOG.debug("Removing tooling {}", ref);
                servicesClient.removeTool(ref.getName());
            } catch (Exception e) {
                LOG.warn("Unable to deregister tool {}: {}", ref.getName(), e.getMessage());
            }
        }
    }
}
