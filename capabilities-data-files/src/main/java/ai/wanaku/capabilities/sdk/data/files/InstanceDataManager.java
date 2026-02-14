package ai.wanaku.capabilities.sdk.data.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;
import ai.wanaku.capabilities.sdk.api.types.providers.ServiceTarget;

/**
 * Manages persistent storage of service instance data for capability provider services.
 * <p>
 * This class handles the lifecycle of service instance data files, including reading existing
 * service entries, writing new entries, and managing the data directory structure. Each service
 * instance maintains its identity across restarts by persisting its ID to a binary data file.
 * <p>
 * The manager creates data files with the naming convention {@code <service-name>.wanaku.dat}
 * in the specified data directory. These files contain a {@link FileHeader} followed by
 * {@link ServiceEntry} data.
 *
 * @see ServiceEntry
 * @see FileHeader
 * @see InstanceDataReader
 * @see InstanceDataWriter
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

        try (InstanceDataReader reader = new InstanceDataReader(file)) {
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
            throw new WanakuException(e);
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
            throw new WanakuException(e);
        }
    }

    /**
     * Creates a new {@link FileHeader} based on the service type of the given {@link ServiceTarget}.
     *
     * @param serviceTarget The {@link ServiceTarget} to determine the file header from.
     * @return The appropriate {@link FileHeader}.
     */
    private static FileHeader newFileHeader(ServiceTarget serviceTarget) {
        String serviceType = serviceTarget.getServiceType();
        if (serviceType == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }

        return switch (serviceType) {
            case "tool-invoker" -> FileHeader.TOOL_INVOKER;
            case "resource-provider" -> FileHeader.RESOURCE_PROVIDER;
            case "multi-capability" -> FileHeader.MULTI_CAPABILITY;
            case "code-execution-engine" -> FileHeader.MULTI_CAPABILITY; // Code execution engines are multi-capability
            default -> throw new IllegalArgumentException("Unknown service type: " + serviceType);
        };
    }
}
