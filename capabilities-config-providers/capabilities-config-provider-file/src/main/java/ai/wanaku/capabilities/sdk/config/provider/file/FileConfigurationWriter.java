package ai.wanaku.capabilities.sdk.config.provider.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ai.wanaku.capabilities.sdk.config.provider.api.ConfigWriteException;
import ai.wanaku.capabilities.sdk.config.provider.api.ConfigWriter;

public class FileConfigurationWriter implements ConfigWriter {

    private final File serviceHome;

    public FileConfigurationWriter(String serviceHome) {
        this(new File(serviceHome));
    }

    public FileConfigurationWriter(File serviceHome) {
        this.serviceHome = serviceHome;
    }

    @Override
    public URI write(String id, String data) {
        try {
            final Path path = Paths.get(serviceHome.getAbsolutePath(), id);
            Files.write(path, data.getBytes());

            return path.toUri();
        } catch (IOException e) {
            throw new ConfigWriteException("Failed to write configuration: " + id, e);
        }
    }
}
