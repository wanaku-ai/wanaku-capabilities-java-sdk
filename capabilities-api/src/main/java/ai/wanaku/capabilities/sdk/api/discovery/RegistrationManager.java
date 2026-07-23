package ai.wanaku.capabilities.sdk.api.discovery;

/**
 * The `RegistrationManager` interface defines the contract for a class responsible for managing the lifecycle
 * registration of a capability service within the Wanaku ecosystem.
 *
 * <p>Implementations of this interface handle the processes of registering, deregistering,
 * and monitoring the status of a service's registration. It acts as the primary interface
 * for a capability service to interact with the Wanaku registration mechanism.</p>
 */
public interface RegistrationManager {

    /**
     * Registers the capability service with Wanaku
     */
    void register();

    /**
     * Deregisters the capability service from the Wanaku registration system.
     * This method notifies the registry that the service is no longer available
     * or is shutting down, allowing for proper cleanup and resource release.
     */
    void deregister();

    /**
     * Adds a callback to be run after some operations have executed
     * @param callback the callback to add
     */
    void addCallBack(DiscoveryCallback callback);
}
