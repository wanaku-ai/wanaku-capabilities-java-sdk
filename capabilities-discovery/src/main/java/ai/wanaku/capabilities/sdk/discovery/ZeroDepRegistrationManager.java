package ai.wanaku.capabilities.sdk.discovery;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.wanaku.capabilities.sdk.api.discovery.DiscoveryCallback;
import ai.wanaku.capabilities.sdk.api.discovery.RegistrationManager;
import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;
import ai.wanaku.capabilities.sdk.api.types.WanakuResponse;
import ai.wanaku.capabilities.sdk.api.types.discovery.ServiceState;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;
import ai.wanaku.capabilities.sdk.data.files.InstanceDataManager;
import ai.wanaku.capabilities.sdk.data.files.ServiceEntry;
import ai.wanaku.capabilities.sdk.discovery.config.RegistrationConfig;
import ai.wanaku.capabilities.sdk.discovery.deserializer.Deserializer;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * An implementation of {@link RegistrationManager} that handles the lifecycle registration
 * of a capability service with the Wanaku Discovery and Registration API.
 * This manager is responsible for registering, deregistering, pinging, and updating the state of a service,
 * and it uses {@link InstanceDataManager} to persist service-related data.
 */
public class ZeroDepRegistrationManager implements RegistrationManager {
    private static final Logger LOG = LoggerFactory.getLogger(ZeroDepRegistrationManager.class);

    private final DiscoveryServiceHttpClient client;
    private final ServiceTarget target;
    private final RegistrationConfig config;
    private final InstanceDataManager instanceDataManager;
    private final Deserializer deserializer;
    private volatile boolean registered;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> registrationTask;
    private final List<DiscoveryCallback> callbacks = new CopyOnWriteArrayList<>();

    /**
     * Constructs a {@code ZeroDepRegistrationManager}.
     *
     * @param client The {@link DiscoveryServiceHttpClient} to communicate with the Discovery API.
     * @param target The {@link ServiceTarget} representing the service to manage.
     * @param config The {@link RegistrationConfig} for registration parameters.
     * @param deserializer The {@link Deserializer} to deserialize API responses.
     */
    public ZeroDepRegistrationManager(
            DiscoveryServiceHttpClient client,
            ServiceTarget target,
            RegistrationConfig config,
            Deserializer deserializer) {
        this.client = client;
        this.target = target;
        this.config = config;
        this.deserializer = deserializer;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        instanceDataManager = new InstanceDataManager(config.getDataDir(), target.getServiceName());
        if (instanceDataManager.dataFileExists()) {
            final ServiceEntry serviceEntry = instanceDataManager.readEntry();
            if (serviceEntry != null) {
                target.setId(serviceEntry.getId());
            }
        } else {
            try {
                instanceDataManager.createDataDirectory();
            } catch (IOException e) {
                throw new WanakuException(e);
            }
        }

        callbacks.add(new DiscoveryLogCallback());
    }

    /**
     * Checks if the service is currently registered.
     *
     * @return {@code true} if the service is registered, {@code false} otherwise.
     */
    private boolean isRegistered() {
        return registered;
    }

    /**
     * Attempts to register the service with the Wanaku Discovery API.
     * This method includes retry logic based on the configured maximum retries and wait time.
     */
    private void tryRegistering() {
        // Reset retries for each new registration attempt
        int retries = config.getMaxRetries();
        do {
            try {
                final HttpResponse<String> response = client.register(target);
                if (response.statusCode() != Response.Status.OK.getStatusCode()) {
                    LOG.warn(
                            "The service {} failed to register. Response code: {}",
                            target.getServiceName(),
                            response.statusCode());
                }

                final String body = response.body();
                final WanakuResponse<ServiceTarget> entity = deserializer.deserialize(body, new TypeReference<>() {});
                if (entity == null || entity.data() == null) {
                    throw new WanakuException(
                            "Could not register service because the provided response is null or invalid");
                }

                target.setId(entity.data().getId());
                instanceDataManager.writeEntry(target);
                registered = true;
                runCallBack(c -> c.onRegistration(this, target));
                break;
            } catch (WebApplicationException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn(
                            "Unable to register service because of: {} ({})",
                            e.getMessage(),
                            e.getResponse().getStatus(),
                            e);
                } else {
                    LOG.warn(
                            "Unable to register service because of: {} ({})",
                            e.getMessage(),
                            e.getResponse().getStatus());
                }
                retries = waitAndRetry(target.getServiceName(), e, retries, config.getWaitSeconds());
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Unable to register service because of: {}", e.getMessage(), e);
                } else {
                    LOG.warn("Unable to register service because of: {}", e.getMessage());
                }
                retries = waitAndRetry(target.getServiceName(), e, retries, config.getWaitSeconds());
            }
        } while (retries > 0);
    }

    /**
     * Waits for a specified duration and decrements the retry count.
     *
     * @param serviceName The name of the service being registered.
     * @param e The exception that caused the retry.
     * @param currentRetries The current number of retries remaining.
     * @param waitSeconds The number of seconds to wait before retrying.
     * @return The updated number of retries remaining.
     */
    private int waitAndRetry(String serviceName, Exception e, int currentRetries, int waitSeconds) {
        if (currentRetries > 0) {
            LOG.info(
                    "Retrying registration for service {} in {} seconds. Retries left: {}",
                    serviceName,
                    waitSeconds,
                    currentRetries - 1);
            try {
                Thread.sleep(waitSeconds * 1000L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                LOG.warn("Interrupted while waiting to retry registration.", ie);
            }
        }
        return currentRetries - 1;
    }

    /**
     * Registers the service. If already registered, it pings the service; otherwise, it attempts registration.
     */
    @Override
    public void register() {
        if (isRegistered()) {
            ping();
        } else {
            tryRegistering();
        }
    }

    /**
     * Attempts to deregister the service from the Wanaku Discovery API.
     */
    private void tryDeregistering() {
        if (target != null && target.getId() != null) {
            try {
                final HttpResponse<String> response = client.deregister(target);
                final int status = response.statusCode();
                runCallBack(c -> c.onDeregistration(this, target, status));
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("De-registering failed with {}", e.getMessage(), e);
                } else {
                    LOG.warn("De-registering failed with {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Deregisters the service and stops any scheduled tasks.
     */
    @Override
    public void deregister() {
        try {
            stop();
        } finally {
            tryDeregistering();
        }
    }

    /**
     * Sends a ping to the Wanaku Discovery API to keep the service registration alive.
     */
    @Override
    public void ping() {
        if (target != null && target.getId() != null) {
            LOG.trace("Pinging router ...");
            try {
                // Assuming client.ping(target.getId()) exists and returns HttpResponse<String>
                final HttpResponse<String> response = client.ping(target.getId());
                final int status = response.statusCode();
                runCallBack(c -> c.onPing(this, target, status));
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Pinging router failed with {}", e.getMessage(), e);
                } else {
                    LOG.warn("Pinging router failed with {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Updates the service's state to 'unhealthy' with a given reason.
     *
     * @param reason The reason for the service being unhealthy.
     */
    @Override
    public void lastAsFail(String reason) {
        if (target.getId() == null) {
            LOG.warn("Trying to update the state of an unknown service {}", target.getServiceName());
            return;
        }

        try {
            final HttpResponse<String> response = client.updateState(target.getId(), ServiceState.newUnhealthy(reason));
            if (response.statusCode() != 200) {
                LOG.error("Could not update the state of an service {} ({})", target.getServiceName(), target.getId());
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Updating last status failed with {}", e.getMessage(), e);
            } else {
                LOG.warn("Updating last status failed with {}", e.getMessage());
            }
        }
    }

    /**
     * Updates the service's state to 'healthy'.
     */
    @Override
    public void lastAsSuccessful() {
        if (target.getId() == null) {
            LOG.warn("Trying to update the state of an unknown service {}", target.getServiceName());
            return;
        }

        try {
            final HttpResponse<String> response = client.updateState(target.getId(), ServiceState.newHealthy());
            if (response.statusCode() != 200) {
                LOG.error("Could not update the state of an service {} ({})", target.getServiceName(), target.getId());
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Updating last status failed with {}", e.getMessage(), e);
            } else {
                LOG.warn("Updating last status failed with {}", e.getMessage());
            }
        }
    }

    /**
     * Starts the scheduled registration task, which periodically calls the {@link #register()} method.
     * The initial delay and period are configured via {@link RegistrationConfig}.
     */
    public void start() {
        registrationTask = scheduler.scheduleAtFixedRate(
                this::register, config.getInitialDelay(), config.getPeriod(), TimeUnit.SECONDS);
    }

    /**
     * Stops the scheduled registration task and shuts down the scheduler.
     */
    private void stop() {
        try {
            registrationTask.cancel(true);
        } finally {
            scheduler.shutdown();
        }
    }

    private void runCallBack(Consumer<DiscoveryCallback> registrationManagerConsumer) {
        for (DiscoveryCallback callback : callbacks) {
            try {
                registrationManagerConsumer.accept(callback);
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn(
                            "Unable to run callback {} due to {}",
                            callback.getClass().getName(),
                            e.getMessage(),
                            e);
                } else {
                    LOG.warn(
                            "Unable to run callback {}} due to {}",
                            callback.getClass().getName(),
                            e.getMessage());
                }
            }
        }
    }

    @Override
    public void addCallBack(DiscoveryCallback callback) {
        callbacks.add(callback);
    }

    public ServiceTarget getTarget() {
        return target;
    }
}
