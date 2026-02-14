package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.wanaku.capabilities.sdk.api.types.DataStore;
import ai.wanaku.capabilities.sdk.api.types.WanakuResponse;
import ai.wanaku.capabilities.sdk.services.ServicesHttpClient;

public class DataStoreDownloader implements Downloader {
    private static final Logger LOG = LoggerFactory.getLogger(DataStoreDownloader.class);
    private final ServicesHttpClient servicesHttpClient;
    private final Path dataDir;

    public DataStoreDownloader(ServicesHttpClient servicesHttpClient, Path dataDir) {
        this.servicesHttpClient = servicesHttpClient;
        this.dataDir = dataDir;
    }

    @Override
    public void downloadResource(ResourceRefs<URI> resourceName, Map<ResourceType, Path> downloadedResources)
            throws Exception {
        final String resourceFileName = resourceName.ref().getHost();
        LOG.debug("Downloading resource: {}", resourceName.ref().getPath());

        // Retrieve the data stores from the API
        WanakuResponse<List<DataStore>> response = servicesHttpClient.getDataStoresByName(resourceFileName);

        if (response == null || response.data() == null || response.data().isEmpty()) {
            LOG.warn("No data found for resource: {}", resourceName);
            return;
        }

        List<DataStore> dataStores = response.data();

        // Process each DataStore entry (typically there should be one per resource name)
        for (DataStore dataStore : dataStores) {
            if (dataStore.getData() == null || dataStore.getData().isEmpty()) {
                LOG.warn("DataStore entry for '{}' contains no data", resourceName);
                continue;
            }

            // Decode from base64
            byte[] decodedData = Base64.getDecoder().decode(dataStore.getData());

            // Save to the configured data directory
            Path filePath = dataDir.resolve(resourceFileName);
            Files.write(filePath, decodedData);
            downloadedResources.put(resourceName.resourceType(), filePath);

            LOG.info("Successfully downloaded resource '{}' to {}", resourceName, filePath.toAbsolutePath());
        }
    }
}
