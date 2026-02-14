package ai.wanaku.capabilities.sdk.data.files;

import ai.wanaku.capabilities.sdk.api.types.providers.ServiceType;

/**
 * Represents the header of a data file, containing metadata such as format name, service type, and file version.
 */
public class FileHeader {
    /**
     * The expected format name for the data files.
     */
    public static final String FORMAT_NAME = "wanaku";
    /**
     * The size of the format name in bytes.
     */
    public static final int FORMAT_NAME_SIZE = FORMAT_NAME.length();
    /**
     * The current version of the file format.
     */
    public static final int CURRENT_FILE_VERSION = 1;
    /**
     * The total number of bytes occupied by the file header.
     */
    public static final int BYTES;

    private final String formatName;
    private final ServiceType serviceType;
    private final int fileVersion;

    /**
     * Predefined {@code FileHeader} for a TOOL_INVOKER service type.
     */
    public static final FileHeader TOOL_INVOKER =
            new FileHeader(FORMAT_NAME, ServiceType.TOOL_INVOKER, CURRENT_FILE_VERSION);
    /**
     * Predefined {@code FileHeader} for a RESOURCE_PROVIDER service type.
     */
    public static final FileHeader RESOURCE_PROVIDER =
            new FileHeader(FORMAT_NAME, ServiceType.RESOURCE_PROVIDER, CURRENT_FILE_VERSION);

    /**
     * Predefined {@code FileHeader} for a RESOURCE_PROVIDER service type.
     */
    public static final FileHeader MULTI_CAPABILITY =
            new FileHeader(FORMAT_NAME, ServiceType.MULTI_CAPABILITY, CURRENT_FILE_VERSION);

    static {
        // The underlying header size is format + file version + service type;
        BYTES = FORMAT_NAME_SIZE + Integer.BYTES + Integer.BYTES + Integer.BYTES + 2;
    }

    /**
     * Constructs a {@code FileHeader} with the specified format name, service type, and file version.
     *
     * @param formatName The format name of the file.
     * @param serviceType The type of service associated with the file.
     * @param fileVersion The version of the file format.
     */
    FileHeader(String formatName, ServiceType serviceType, int fileVersion) {
        this.formatName = formatName;
        this.serviceType = serviceType;
        this.fileVersion = fileVersion;
    }

    /**
     * Returns the service type associated with this file header.
     *
     * @return The {@link ServiceType}.
     */
    public ServiceType getServiceType() {
        return serviceType;
    }

    /**
     * Returns the file version of this file header.
     *
     * @return The file version.
     */
    public int getFileVersion() {
        return fileVersion;
    }

    /**
     * Returns the format name of this file header.
     *
     * @return The format name.
     */
    public String getFormatName() {
        return formatName;
    }
}
