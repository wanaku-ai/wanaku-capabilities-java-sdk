package ai.wanaku.capabilities.sdk.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessRunnerTest {

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    @Test
    void runWithOutput_returnsCommandOutput() {
        String output;
        if (isWindows()) {
            output = ProcessRunner.runWithOutput("cmd.exe", "/c", "echo hello");
        } else {
            output = ProcessRunner.runWithOutput("sh", "-c", "echo hello");
        }

        assertNotNull(output, "Output should not be null");
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.contains("hello"), "Output should contain 'hello'");
    }

    @Test
    void runWithOutput_nonZeroExitDoesNotThrow() {
        String output;
        if (isWindows()) {
            output = assertDoesNotThrow(() -> ProcessRunner.runWithOutput("cmd.exe", "/c", "echo output && exit /b 1"));
        } else {
            output = assertDoesNotThrow(() -> ProcessRunner.runWithOutput("sh", "-c", "echo output; exit 1"));
        }

        assertNotNull(output, "Output should not be null");
        assertTrue(output.contains("output"), "Output should contain 'output'");
    }

    @Test
    void runWithOutput_capturesStderrViaMergedStream() {
        String output;
        if (isWindows()) {
            output = ProcessRunner.runWithOutput("cmd.exe", "/c", "echo stdout && echo stderr 1>&2");
        } else {
            output = ProcessRunner.runWithOutput("sh", "-c", "echo stdout && echo stderr 1>&2");
        }

        assertNotNull(output, "Output should not be null");
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.contains("stdout"), "Output should contain stdout content");
        assertTrue(output.contains("stderr"), "Output should contain stderr content (merged stream)");
    }
}
