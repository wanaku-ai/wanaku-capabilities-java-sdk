package ai.wanaku.capabilities.sdk.common;

import java.io.File;

public class ServicesHelper {
    public static String getCanonicalServiceHome(String name) {
        return System.getProperty("user.home") + File.separator + name;
    }
}
