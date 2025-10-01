package ai.wanaku.capabilities.sdk.data.files;

import ai.wanaku.api.exceptions.WanakuException;
import ai.wanaku.api.types.providers.ServiceTarget;
import ai.wanaku.api.types.providers.ServiceType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages permanent data for a capabilities instance, such as its service ID.
 * This class is used by a {@code RegistrationManager} to store information
 * after registration with the Wanaku Discovery and Registration API.
 */
public class InstanceDataManager {
    private static final Logger LOG = LoggerFactory.getLogger(InstanceDataManager.class);

    private final String dataDir;
    private final String name;

    /**
     * Constructs an {@code InstanceDataManager}.
     *
     * @param dataDir The directory where the instance data file will be stored.
     * @param name The name of the service, used to construct the data file name.
     */
    public InstanceDataManager(String dataDir, String name) {
        this.dataDir = dataDir;
        this.name = name;

        LOG.debug("Using {} as the data directory", dataDir);
    }

    /**
     * Checks if the instance data file exists.
     *
     * @return {@code true} if the data file exists, {@code false} otherwise.
     */
    public boolean dataFileExists() {
        final File serviceFile = serviceFile();
        LOG.debug("Checking if file {} exists", serviceFile);
        return serviceFile.exists();
    }

    /**
     * Creates the data directory if it does not already exist.
     *
     * @throws IOException If an I/O error occurs during directory creation.
     */
    public void createDataDirectory() throws IOException {
        Files.createDirectories(serviceFile().getParentFile().toPath());
    }

    /**
     * Reads the {@link ServiceEntry} from the instance data file.
     *
     * @return The {@code ServiceEntry} if the file exists and can be read, or {@code null} if the file does not exist.
     * @throws RuntimeException If an I/O error occurs during reading.
     */
    public ServiceEntry readEntry() {
        final File file = serviceFile();
        if (!file.exists()) {
            return null;
        }

        try (InstanceDataReader reader = new InstanceDataReader(file))  {
            final FileHeader header = reader.getHeader();
            if (header == null) {
                throw new WanakuException("Invalid data file");
            }

            final String formatName = reader.getHeader().getFormatName();
            if (formatName == null) {
                throw new WanakuException("Invalid data file");
            }

            return reader.readEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the {@link File} object representing the instance data file.
     *
     * @return The instance data file.
     */
    private File serviceFile() {
        return new File(dataDir, name + ".wanaku.dat");
    }

    /**
     * Writes a new {@link ServiceEntry} to the instance data file.
     * If the file already exists, this method does nothing.
     *
     * @param serviceTarget The {@link ServiceTarget} containing information to be written.
     * @throws RuntimeException If an I/O error occurs during writing.
     */
    public void writeEntry(ServiceTarget serviceTarget) {
        final File file = serviceFile();
        if (file.exists()) {
            return;
        }

        final FileHeader fileHeader = newFileHeader(serviceTarget);

        try (InstanceDataWriter writer = new InstanceDataWriter(file, fileHeader)) {
            writer.write(new ServiceEntry(serviceTarget.getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new {@link FileHeader} based on the {@link ServiceType} of the given {@link ServiceTarget}.
     *
     * @param serviceTarget The {@link ServiceTarget} to determine the file header from.
     * @return The appropriate {@link FileHeader}.
     */
    private static FileHeader newFileHeader(ServiceTarget serviceTarget) {
        if (serviceTarget.getServiceType() == ServiceType.RESOURCE_PROVIDER) {
            return FileHeader.RESOURCE_PROVIDER;
        } else {
            return FileHeader.TOOL_INVOKER;
        }
    }
}
