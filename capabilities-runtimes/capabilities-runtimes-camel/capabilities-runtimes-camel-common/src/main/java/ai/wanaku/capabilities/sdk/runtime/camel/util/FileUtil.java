package ai.wanaku.capabilities.sdk.runtime.camel.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {}

    public static boolean untilAvailable(File input, boolean isDirectory) throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = isDirectory ? input.toPath() : input.getParentFile().toPath();

        if (input.exists()) {
            LOG.info("File {} already available", input);
            return true;
        }

        // We watch for both the file creation and truncation
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

        do {
            LOG.info("Waiting indefinitely for {} to be available", input);

            WatchKey watchKey = watchService.poll(1, TimeUnit.SECONDS);

            if (watchKey == null) {
                continue;
            }

            for (WatchEvent<?> event : watchKey.pollEvents()) {

                /*
                 It should return a Path object for ENTRY_CREATE and ENTRY_MODIFY events
                */
                Object context = event.context();
                if (!(context instanceof Path contextPath)) {
                    LOG.warn("Received an unexpected event of kind {} for context {}", event.kind(), event.context());
                    continue;
                }

                if (contextPath.toString().equals(input.getName())) {
                    LOG.debug(
                            "File at the path {} had a matching event of type: {}",
                            input.getParentFile().getPath(),
                            event.kind());

                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                            || event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        LOG.info(
                                "File at the path {} was created or modified",
                                input.getParentFile().getPath());

                        break;
                    }
                } else {
                    LOG.debug(
                            "Ignoring a watch event at build path {} of type {} for file: {}",
                            input.getParentFile().getPath(),
                            event.kind(),
                            contextPath.getFileName());
                }
            }
            watchKey.reset();
        } while (!input.exists());

        return input.exists();
    }
}
