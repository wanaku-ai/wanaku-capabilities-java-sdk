package ai.wanaku.capabilities.sdk.discovery;

import ai.wanaku.capabilities.sdk.api.discovery.DiscoveryCallback;
import ai.wanaku.capabilities.sdk.api.discovery.RegistrationManager;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DiscoveryLogCallback implements DiscoveryCallback {
    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryLogCallback.class);

    @Override
    public void onPing(RegistrationManager manager, ServiceTarget target, int status) {
        if (status != 200) {
            LOG.warn("Pinging router failed with status {}", status);
        } else {
            LOG.trace("Pinging router completed successfully");
        }
    }

    @Override
    public void onRegistration(RegistrationManager manager, ServiceTarget target) {
        LOG.debug("The service {} successfully registered with ID {}.", target.getServiceName(), target.getId());
    }

    @Override
    public void onDeregistration(RegistrationManager manager, ServiceTarget target, int status) {
        if (status != 200) {
            LOG.warn("De-registering service {} failed with status {}", target.getServiceType(), status);
        }
    }
}
