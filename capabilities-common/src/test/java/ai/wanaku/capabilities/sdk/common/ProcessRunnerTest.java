package ai.wanaku.capabilities.sdk.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessRunnerTest {

    @Test
    void runWithOutput_returnsCommandOutput() {
        String output = ProcessRunner.runWithOutput("echo", "hello");

        assertNotNull(output, "Output should not be null");
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.contains("hello"), "Output should contain 'hello'");
    }
}
