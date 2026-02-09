package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Downloader for local files using the file:// URI scheme.
 * Copies files from the local filesystem to the data directory.
 */
public class FileDownloader implements Downloader {
    private static final Logger LOG = LoggerFactory.getLogger(FileDownloader.class);
    private final Path dataDir;

    public FileDownloader(Path dataDir) {
        this.dataDir = dataDir;
    }

    @Override
    public void downloadResource(ResourceRefs<URI> resourceName, Map<ResourceType, Path> downloadedResources)
            throws Exception {
        final URI fileUri = resourceName.ref();
        LOG.debug("Processing file resource: {}", fileUri);

        // Construct the Path directly from the URI to correctly handle all valid file:// URIs
        Path sourceFile = Paths.get(fileUri);

        if (!Files.exists(sourceFile)) {
            throw new IOException("File not found: " + sourceFile.toAbsolutePath());
        }

        if (!Files.isRegularFile(sourceFile)) {
            throw new IOException("Path is not a regular file: " + sourceFile.toAbsolutePath());
        }

        // Extract filename and copy to data directory
        String fileName = sourceFile.getFileName().toString();
        Path targetPath = dataDir.resolve(fileName);

        Files.copy(sourceFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
        downloadedResources.put(resourceName.resourceType(), targetPath);

        LOG.info(
                "Successfully copied file resource '{}' to {}",
                sourceFile.toAbsolutePath(),
                targetPath.toAbsolutePath());
    }
}
