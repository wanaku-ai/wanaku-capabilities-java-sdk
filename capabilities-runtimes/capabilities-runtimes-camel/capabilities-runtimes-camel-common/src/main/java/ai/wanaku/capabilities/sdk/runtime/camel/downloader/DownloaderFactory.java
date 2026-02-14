package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.net.URI;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.wanaku.capabilities.sdk.services.ServicesHttpClient;

public class DownloaderFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DownloaderFactory.class);

    private final ServicesHttpClient servicesHttpClient;
    private final Path dataDir;

    private FileDownloader fileDownloader;
    private DataStoreDownloader dataStoreDownloader;

    public DownloaderFactory(ServicesHttpClient servicesHttpClient, Path dataDir) {
        this.servicesHttpClient = servicesHttpClient;
        this.dataDir = dataDir;
    }

    public Downloader getDownloader(URI uri) {
        if (uri == null || uri.getScheme() == null) {
            throw new IllegalArgumentException("URI and scheme cannot be null");
        }

        String scheme = uri.getScheme().toLowerCase();

        return switch (scheme) {
            case "datastore" -> getDataStoreDownloader();
            case "file" -> getFileDownloader();
            default ->
                throw new IllegalArgumentException(
                        "Unsupported URI scheme: " + scheme + ". Supported schemes: datastore://, file://");
        };
    }

    private DataStoreDownloader getDataStoreDownloader() {
        if (dataStoreDownloader == null) {
            LOG.debug("Creating DataStoreDownloader instance");
            dataStoreDownloader = new DataStoreDownloader(servicesHttpClient, dataDir);
        }
        return dataStoreDownloader;
    }

    private FileDownloader getFileDownloader() {
        if (fileDownloader == null) {
            LOG.debug("Creating FileDownloader instance");
            fileDownloader = new FileDownloader(dataDir);
        }
        return fileDownloader;
    }
}
