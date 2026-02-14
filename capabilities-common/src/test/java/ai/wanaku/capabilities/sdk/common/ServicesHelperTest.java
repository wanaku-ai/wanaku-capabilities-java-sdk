package ai.wanaku.capabilities.sdk.common;

import java.io.File;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServicesHelperTest {

    @Test
    void getCanonicalServiceHome_returnsCorrectPath() {
        String serviceName = "testService";

        String actualPath = ServicesHelper.getCanonicalServiceHome(serviceName);

        assertTrue(actualPath.endsWith(File.separator + serviceName), "The path should end with the service name");
        assertTrue(
                actualPath.startsWith(System.getProperty("user.home")),
                "The path should start with the user home directory");
    }
}
