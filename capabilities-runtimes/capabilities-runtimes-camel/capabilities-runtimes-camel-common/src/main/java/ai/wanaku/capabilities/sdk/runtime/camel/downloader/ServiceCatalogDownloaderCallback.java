package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.wanaku.capabilities.sdk.api.discovery.DiscoveryCallback;
import ai.wanaku.capabilities.sdk.api.discovery.RegistrationManager;
import ai.wanaku.capabilities.sdk.api.types.DataStore;
import ai.wanaku.capabilities.sdk.api.types.WanakuResponse;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;

public class ServiceCatalogDownloaderCallback implements DiscoveryCallback {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceCatalogDownloaderCallback.class);

    private final DownloaderFactory downloaderFactory;
    private final String catalogName;
    private final String systemName;
    private final DownloaderConfiguration configuration;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private Map<ResourceType, Path> downloadedResources = new HashMap<>();
    private boolean success;

    public ServiceCatalogDownloaderCallback(
            DownloaderFactory downloaderFactory, String catalogName, String systemName) {
        this(downloaderFactory, catalogName, systemName, DownloaderConfiguration.defaultConfig());
    }

    public ServiceCatalogDownloaderCallback(
            DownloaderFactory downloaderFactory,
            String catalogName,
            String systemName,
            DownloaderConfiguration configuration) {
        this.downloaderFactory = downloaderFactory;
        this.catalogName = catalogName;
        this.systemName = systemName;
        this.configuration = configuration;
    }

    @Override
    public void onPing(RegistrationManager manager, ServiceTarget target, int status) {}

    @Override
    public void onRegistration(RegistrationManager manager, ServiceTarget target) {
        try {
            downloadServiceCatalogWithRetry();
        } finally {
            countDownLatch.countDown();
        }
    }

    @Override
    public void onDeregistration(RegistrationManager manager, ServiceTarget target, int status) {}

    private void downloadServiceCatalogWithRetry() {
        RetryPolicy retryPolicy = configuration.getRetryPolicy();
        int maxAttempts = 1 + retryPolicy.maxRetries();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                LOG.info(
                        "Downloading service catalog '{}' for system '{}' (attempt {}/{})",
                        catalogName,
                        systemName,
                        attempt,
                        maxAttempts);

                WanakuResponse<DataStore> response =
                        downloaderFactory.getServicesHttpClient().getServiceCatalog(catalogName);
                if (response == null || response.data() == null) {
                    LOG.error("Service catalog '{}' not found", catalogName);
                    return;
                }

                DataStore catalog = response.data();
                if (catalog.getData() == null || catalog.getData().isBlank()) {
                    LOG.error("Service catalog '{}' contains no data", catalogName);
                    return;
                }

                downloadedResources =
                        ServiceCatalogExtractor.extract(catalog.getData(), systemName, downloaderFactory.getDataDir());
                LOG.info("Extracted {} resource(s) from service catalog", downloadedResources.size());
                success = true;
                return;
            } catch (Exception e) {
                if (attempt >= maxAttempts || !retryPolicy.isRetryable(e)) {
                    LOG.error("Failed to download service catalog '{}': {}", catalogName, e.getMessage(), e);
                    return;
                }

                long delay = retryPolicy.getDelayMillis(attempt);
                LOG.warn(
                        "Download attempt {}/{} failed for service catalog '{}': {}. Retrying in {} ms",
                        attempt,
                        maxAttempts,
                        catalogName,
                        e.getMessage(),
                        delay);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public boolean waitForDownloads() {
        LOG.info("Waiting for service catalog download");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return success;
    }

    public Map<ResourceType, Path> getDownloadedResources() {
        return downloadedResources;
    }
}
