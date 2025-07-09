package ai.wanaku.capabilities.sdk.discovery;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import ai.wanaku.api.discovery.RegistrationManager;
import ai.wanaku.api.exceptions.WanakuException;
import ai.wanaku.api.types.WanakuResponse;
import ai.wanaku.api.types.discovery.ServiceState;
import ai.wanaku.api.types.providers.ServiceTarget;
import ai.wanaku.capabilities.sdk.data.files.InstanceDataManager;
import ai.wanaku.capabilities.sdk.data.files.ServiceEntry;
import ai.wanaku.capabilities.sdk.discovery.deserializer.Deserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeroDepRegistrationManager implements RegistrationManager {
    private static final Logger LOG = LoggerFactory.getLogger(ZeroDepRegistrationManager.class);

    private final DiscoveryServiceHttpClient client;
    private final ServiceTarget target;
    private int maxRetries;
    private final int waitSeconds;
    private final String dataDir;
    private final InstanceDataManager instanceDataManager;
    private final Deserializer deserializer;
    private volatile boolean registered;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> registrationTask;
    private final long initialDelay;
    private final long period;
    private final TimeUnit timeUnit;

    public ZeroDepRegistrationManager(DiscoveryServiceHttpClient client, ServiceTarget target,
            int maxRetries, int waitSeconds, String dataDir, Deserializer deserializer,
            long initialDelay, long period, TimeUnit timeUnit) {
        this.client = client;
        this.target = target;
        this.maxRetries = maxRetries;
        this.waitSeconds = waitSeconds;
        this.dataDir = dataDir;
        this.deserializer = deserializer;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;

        instanceDataManager = new InstanceDataManager(dataDir, target.getService());
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
    }

    private boolean isRegistered() {
        return registered;
    }

    private void tryRegistering() {
        // Reset retries for each new registration attempt
        int retries = this.maxRetries;
        do {
            try {
                final HttpResponse<String> response = client.register(target);
                if (response.statusCode() != Response.Status.OK.getStatusCode()) {
                    LOG.warn("The service {} failed to register. Response code: {}", target.getService(),
                            response.statusCode());
                }

                final String body = response.body();
                final WanakuResponse<ServiceTarget> entity = deserializer.deserialize(body, new TypeReference<>() {});
                if (entity == null || entity.data() == null) {
                    throw new WanakuException("Could not register service because the provided response is null or invalid");
                }

                target.setId(entity.data().getId());
                instanceDataManager.writeEntry(target);
                registered = true;
                LOG.debug("The service {} successfully registered with ID {}.", target.getService(), target.getId());
                break;
            } catch (WebApplicationException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Unable to register service because of: {} ({})", e.getMessage(), e.getResponse().getStatus(), e);
                } else {
                    LOG.warn("Unable to register service because of: {} ({})", e.getMessage(), e.getResponse().getStatus());
                }
                retries = waitAndRetry(target.getService(), e, retries, waitSeconds);
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Unable to register service because of: {}", e.getMessage(), e);
                } else {
                    LOG.warn("Unable to register service because of: {}", e.getMessage());
                }
                retries = waitAndRetry(target.getService(), e, retries, waitSeconds);
            }
        } while (retries > 0);
    }

    private int waitAndRetry(String serviceName, Exception e, int currentRetries, int waitSeconds) {
        if (currentRetries > 0) {
            LOG.info("Retrying registration for service {} in {} seconds. Retries left: {}", serviceName, waitSeconds, currentRetries - 1);
            try {
                Thread.sleep(waitSeconds * 1000L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                LOG.warn("Interrupted while waiting to retry registration.", ie);
            }
        }
        return currentRetries - 1;
    }

    @Override
    public void register() {
        if (isRegistered()) {
            ping();
        } else {
            tryRegistering();
        }

    }

    private void tryDeregistering() {
        if (target != null && target.getId() != null) {
            try {
                final HttpResponse<String> response = client.deregister(target);
                if (response.statusCode() != 200) {
                    LOG.warn("De-registering service {} failed with status {}", target.getServiceType().asValue(), response.statusCode());
                }
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("De-registering failed with {}", e.getMessage(), e);
                } else {
                    LOG.warn("De-registering failed with {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void deregister() {
        try {
            stop();
        } finally {
            tryDeregistering();
        }
    }

    @Override
    public void ping() {
        if (target != null && target.getId() != null) {
            LOG.trace("Pinging router ...");
            try {
                // Assuming client.ping(target.getId()) exists and returns HttpResponse<String>
                final HttpResponse<String> response = client.ping(target.getId());
                if (response.statusCode() != 200) {
                    LOG.warn("Pinging router failed with status {}", response.statusCode());
                }

                if (LOG.isDebugEnabled()) {
                    LOG.trace("Pinging router completed successfully");
                }
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Pinging router failed with {}", e.getMessage(), e);
                } else {
                    LOG.warn("Pinging router failed with {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void lastAsFail(String reason) {
        if (target.getId() == null) {
            LOG.warn("Trying to update the state of an unknown service {}", target.getService());
            return;
        }

        try {
            final HttpResponse<String> response = client.updateState(target.getId(), ServiceState.newUnhealthy(reason));
            if (response.statusCode() != 200) {
                LOG.error("Could not update the state of an service {} ({})", target.getService(), target.getId());
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Updating last status failed with {}", e.getMessage(), e);
            } else {
                LOG.warn("Updating last status failed with {}", e.getMessage());
            }
        }
    }

    @Override
    public void lastAsSuccessful() {
        if (target.getId() == null) {
            LOG.warn("Trying to update the state of an unknown service {}", target.getService());
            return;
        }

        try {
            final HttpResponse<String> response = client.updateState(target.getId(), ServiceState.newHealthy());
            if (response.statusCode() != 200) {
                LOG.error("Could not update the state of an service {} ({})", target.getService(), target.getId());
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Updating last status failed with {}", e.getMessage(), e);
            } else {
                LOG.warn("Updating last status failed with {}", e.getMessage());
            }
        }
    }

    public void start() {
        registrationTask = scheduler.scheduleAtFixedRate(this::register, initialDelay, period, timeUnit);
    }

    private void stop() {
        try {
            registrationTask.cancel(true);
        } finally {
            scheduler.shutdown();
        }

    }
}
