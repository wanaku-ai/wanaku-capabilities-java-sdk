package ai.wanaku.capabilities.sdk.data.files.util;

import java.io.File;
import java.util.UUID;

public final class DataFileHelper {

    private DataFileHelper() {}

    public static File newRandomizedDataFile(String serviceHome) {
        String uuid = UUID.randomUUID() + ".properties";
        File dataFile = new File(serviceHome, uuid);
        return dataFile;
    }
}
