package ai.wanaku.capabilities.sdk.runtime.camel.versions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import ai.wanaku.capabilities.sdk.common.VersionHelper;

public class RuntimeVersionHelper {
    /**
     * The current version of this library, loaded from the "version.txt" file in the root directory.
     */
    public static final Properties VERSIONS;

    static {
        // Initialize the VERSION field with the contents of the "version.txt" file
        VERSIONS = initVersions();
    }

    /**
     * Private constructor to prevent instantiation and ensure this class is used as a utility.
     */
    private RuntimeVersionHelper() {}

    /**
     * Initializes and returns the version string by reading from the "version.txt" file in the root directory.
     *
     * @return The current version of this library.
     */
    private static Properties initVersions() {
        Properties props = new Properties();
        try (InputStream stream = VersionHelper.class.getResourceAsStream("/runtime-versions.properties")) {
            assert stream != null;
            props.load(stream);

            return props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
