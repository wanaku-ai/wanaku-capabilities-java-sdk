package ai.wanaku.capabilities.sdk.api.discovery;

import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;

/**
 * Callback interface for receiving notifications about service registration lifecycle events.
 *
 * <p>Implementations of this interface can be registered with a {@link RegistrationManager}
 * to receive notifications when registration-related operations occur, such as successful
 * registration or deregistration attempts.</p>
 *
 * <p>Callbacks are invoked synchronously after the corresponding operation completes.
 * If a callback throws an exception, it will be logged but will not prevent other
 * registered callbacks from executing.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * RegistrationManager manager = ...;
 * manager.addCallBack(new DiscoveryCallback() {
 *     @Override
 *     public void onRegistration(RegistrationManager manager, ServiceTarget target) {
 *         // Handle successful registration
 *     }
 *
 *     @Override
 *     public void onDeregistration(RegistrationManager manager, ServiceTarget target, int status) {
 *         // Handle deregistration result
 *     }
 * });
 * }</pre>
 *
 * @see RegistrationManager#addCallBack(DiscoveryCallback)
 */
public interface DiscoveryCallback {

    /**
     * Invoked after the service has been successfully registered with the discovery service.
     * This callback is only called when registration completes successfully.
     *
     * @param manager the {@link RegistrationManager} that performed the registration
     * @param target the {@link ServiceTarget} that was registered
     */
    void onRegistration(RegistrationManager manager, ServiceTarget target);

    /**
     * Invoked after a deregistration attempt is made with the discovery service.
     * This callback is triggered when the service is being shut down or explicitly
     * removed from the registry.
     *
     * @param manager the {@link RegistrationManager} that performed the deregistration operation
     * @param target the {@link ServiceTarget} that was deregistered
     * @param status the HTTP status code returned by the deregistration operation
     *               (200 indicates success, other values indicate various failure conditions)
     */
    void onDeregistration(RegistrationManager manager, ServiceTarget target, int status);
}
