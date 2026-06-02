package ai.wanaku.capabilities.sdk.runtime.camel.versions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import ai.wanaku.capabilities.sdk.common.VersionHelper;

public class RuntimeVersionHelper {
    private static final Properties VERSIONS;

    static {
        VERSIONS = initVersions();
    }

    private RuntimeVersionHelper() {}

    /**
     * Returns a defensive copy of the runtime version properties loaded from
     * {@code runtime-versions.properties}.
     */
    public static Properties getVersions() {
        Properties copy = new Properties();
        copy.putAll(VERSIONS);
        return copy;
    }

    private static Properties initVersions() {
        Properties props = new Properties();
        try (InputStream stream = VersionHelper.class.getResourceAsStream("/runtime-versions.properties")) {
            if (stream == null) {
                throw new IllegalStateException("Missing runtime-versions.properties on classpath");
            }
            props.load(stream);

            return props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
