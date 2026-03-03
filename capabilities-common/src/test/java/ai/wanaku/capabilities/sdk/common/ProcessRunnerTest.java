package ai.wanaku.capabilities.sdk.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessRunnerTest {

    private static String[] shellCommand(String script) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return new String[] {"cmd.exe", "/c", script};
        } else {
            return new String[] {"sh", "-c", script};
        }
    }

    @Test
    void runWithOutput_returnsCommandOutput() {
        String output = ProcessRunner.runWithOutput(shellCommand("echo hello"));

        assertNotNull(output, "Output should not be null");
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.contains("hello"), "Output should contain 'hello'");
    }

    @Test
    void runWithOutput_nonZeroExitDoesNotThrow() {
        String[] command = shellCommand("echo output; exit 1");
        String output = assertDoesNotThrow(() -> ProcessRunner.runWithOutput(command));

        assertNotNull(output, "Output should not be null");
        assertTrue(output.contains("output"), "Output should contain 'output'");
    }

    @Test
    void runWithOutput_capturesStderrViaMergedStream() {
        String output = ProcessRunner.runWithOutput(shellCommand("echo stdout && echo stderr 1>&2"));

        assertNotNull(output, "Output should not be null");
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.contains("stdout"), "Output should contain stdout content");
        assertTrue(output.contains("stderr"), "Output should contain stderr content (merged stream)");
    }
}
