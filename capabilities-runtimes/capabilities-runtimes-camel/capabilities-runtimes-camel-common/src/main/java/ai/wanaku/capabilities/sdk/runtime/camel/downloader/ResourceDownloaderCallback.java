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
    private Map<ResourceType, Path> downloadedResources = new HashMap<>();

    public ResourceDownloaderCallback(DownloaderFactory downloaderFactory, List<ResourceRefs<URI>> resources) {
        this.resources = resources;
        this.downloaderFactory = downloaderFactory;
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

            for (ResourceRefs<URI> resourceName : resources) {
                try {
                    Downloader downloader = downloaderFactory.getDownloader(resourceName.ref());
                    downloader.downloadResource(resourceName, downloadedResources);
                } catch (WanakuWebException e) {
                    if (e.getStatusCode() == 404) {
                        LOG.error(
                                "Failed to download resource (resource not found) '{}': {}",
                                resourceName,
                                e.getMessage());
                    } else {
                        LOG.error("Failed to download resource '{}': {}", resourceName, e.getMessage());
                    }
                } catch (Exception e) {
                    LOG.error("Failed to download resource'{}': {}", resourceName, e.getMessage(), e);
                }
            }
        } finally {
            countDownLatch.countDown();
        }
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
