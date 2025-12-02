package ai.wanaku.capabilities.sdk.data.files.util;

import java.io.File;
import java.util.UUID;

/**
 * Utility class for handling data file operations.
 */
public final class DataFileHelper {

    private DataFileHelper() {}

    /**
     * Generates a new randomized data file within the specified service home directory.
     *
     * @param serviceHome The home directory for the service.
     * @return A {@link File} object representing the new randomized data file.
     */
    public static File newRandomizedDataFile(String serviceHome) {
        String uuid = UUID.randomUUID() + ".properties";
        return new File(serviceHome, uuid);
    }
}
