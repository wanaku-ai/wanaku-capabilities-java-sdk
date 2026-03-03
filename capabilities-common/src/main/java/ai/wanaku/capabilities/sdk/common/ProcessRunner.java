package ai.wanaku.capabilities.sdk.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;

/**
 * Convenience utility for running external processes. This is intended for use by capabilities and CLI code.
 * It MUST NOT be used in router code.
 */
public class ProcessRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessRunner.class);

    private ProcessRunner() {}

    public static String runWithOutput(String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            processBuilder.redirectErrorStream(true);

            final Process process = processBuilder.start();
            LOG.info("Waiting for process to finish...");

            String output = readOutput(process);

            waitForExit(process);
            return output;
        } catch (IOException e) {
            LOG.error("I/O Error: {}", e.getMessage(), e);
            throw new WanakuException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Interrupted: {}", e.getMessage(), e);
            throw new WanakuException(e);
        }
    }

    private static String readOutput(Process process) throws IOException {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        }
    }

    public static void run(File directory, String... command) {
        run(directory, null, command);
    }

    public static void run(File directory, Map<String, String> environmentVariables, String... command) {
        try {
            LOG.info("About to run command: {}", String.join(" ", command));
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command).inheritIO().directory(directory);

            if (environmentVariables != null) {
                processBuilder.environment().putAll(environmentVariables);
            }

            final Process process = processBuilder.start();

            LOG.info("Waiting for process to finish...");
            waitForExit(process);
        } catch (IOException e) {
            LOG.error("I/O Error: {}", e.getMessage(), e);
            throw new WanakuException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Interrupted: {}", e.getMessage(), e);
            throw new WanakuException(e);
        }
    }

    private static void waitForExit(Process process) throws InterruptedException {
        Thread thread = new Thread(() -> {
            if (process.isAlive()) {
                process.destroy();
            }
        });

        try {
            Runtime.getRuntime().addShutdownHook(thread);

            final int ret = process.waitFor();
            if (ret != 0) {
                LOG.warn("Process did not execute successfully: (return status {})", ret);
            }
        } finally {
            if (!process.isAlive()) {
                Runtime.getRuntime().removeShutdownHook(thread);
            }
        }
    }
}
