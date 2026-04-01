package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.wanaku.capabilities.sdk.api.discovery.DiscoveryCallback;
import ai.wanaku.capabilities.sdk.api.discovery.RegistrationManager;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;
import ai.wanaku.capabilities.sdk.common.exceptions.WanakuWebException;

public class ResourceDownloaderCallback implements DiscoveryCallback {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceDownloaderCallback.class);

    private final List<ResourceRefs<URI>> resources;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final DownloaderFactory downloaderFactory;
    private final ResourceDownloaderConfiguration configuration;
    private Map<ResourceType, Path> downloadedResources = new HashMap<>();

    public ResourceDownloaderCallback(DownloaderFactory downloaderFactory, List<ResourceRefs<URI>> resources) {
        this(downloaderFactory, resources, ResourceDownloaderConfiguration.defaultConfig());
    }

    public ResourceDownloaderCallback(
            DownloaderFactory downloaderFactory,
            List<ResourceRefs<URI>> resources,
            ResourceDownloaderConfiguration configuration) {
        this.resources = resources;
        this.downloaderFactory = downloaderFactory;
        this.configuration = configuration;
    }

    @Override
    public void onPing(RegistrationManager manager, ServiceTarget target, int status) {}

    @Override
    public void onRegistration(RegistrationManager manager, ServiceTarget target) {
        downloadResources();
    }

    @Override
    public void onDeregistration(RegistrationManager manager, ServiceTarget target, int status) {}

    private void downloadResources() {
        if (resources == null || resources.isEmpty()) {
            LOG.debug("No resources to download");
            return;
        }

        try {
            LOG.info("Starting download of {} resource(s)", resources.size());
            RetryPolicy retryPolicy = configuration.getRetryPolicy();

            for (ResourceRefs<URI> resourceName : resources) {
                downloadWithRetry(resourceName, retryPolicy);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    private void downloadWithRetry(ResourceRefs<URI> resourceName, RetryPolicy retryPolicy) {
        int maxAttempts = 1 + retryPolicy.maxRetries();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Downloader downloader = downloaderFactory.getDownloader(resourceName.ref());
                downloader.downloadResource(resourceName, downloadedResources);
                return;
            } catch (WanakuWebException e) {
                if (!handleRetryableFailure(e, resourceName, retryPolicy, attempt, maxAttempts)) {
                    if (e.getStatusCode() == 404) {
                        LOG.error(
                                "Failed to download resource (resource not found) '{}': {}",
                                resourceName,
                                e.getMessage());
                    } else {
                        LOG.error("Failed to download resource '{}': {}", resourceName, e.getMessage());
                    }
                    return;
                }
            } catch (Exception e) {
                if (!handleRetryableFailure(e, resourceName, retryPolicy, attempt, maxAttempts)) {
                    LOG.error("Failed to download resource '{}': {}", resourceName, e.getMessage(), e);
                    return;
                }
            }
        }
    }

    /**
     * Checks whether the failure is retryable and sleeps before the next attempt if so.
     *
     * @return {@code true} if the caller should continue to the next attempt, {@code false} if it should give up
     */
    private boolean handleRetryableFailure(
            Exception e, ResourceRefs<URI> resourceName, RetryPolicy retryPolicy, int attempt, int maxAttempts) {
        if (attempt >= maxAttempts || !retryPolicy.isRetryable(e)) {
            return false;
        }

        long delay = retryPolicy.getDelayMillis(attempt);
        LOG.warn(
                "Download attempt {}/{} failed for '{}': {}. Retrying in {} ms",
                attempt,
                maxAttempts,
                resourceName,
                e.getMessage(),
                delay);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }

    public boolean waitForDownloads() {
        LOG.info("Waiting for resources to download");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return resources.size() == downloadedResources.size();
    }

    public Map<ResourceType, Path> getDownloadedResources() {
        return downloadedResources;
    }
}
