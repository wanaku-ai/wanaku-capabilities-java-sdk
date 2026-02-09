package ai.wanaku.capabilities.sdk.runtime.camel.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * No-operation initializer used when no initialization is required.
 */
public class NoOpInitializer implements Initializer {
    private static final Logger LOG = LoggerFactory.getLogger(NoOpInitializer.class);

    @Override
    public void initialize() throws Exception {
        LOG.debug("No initialization required");
    }
}
