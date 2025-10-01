package ai.wanaku.capabilities.sdk.common;

import java.io.File;

/**
 * Helper class for common service-related utilities.
 */
public class ServicesHelper {
    /**
     * Returns the canonical home directory for a given service name.
     * This is typically the user's home directory followed by the service name.
     *
     * @param name The name of the service.
     * @return The canonical home directory path for the service.
     */
    public static String getCanonicalServiceHome(String name) {
        return System.getProperty("user.home")
                + File.separator
                + ".wanaku"
                + File.separator
                + "services"
                + File.separator
                + name;
    }
}
