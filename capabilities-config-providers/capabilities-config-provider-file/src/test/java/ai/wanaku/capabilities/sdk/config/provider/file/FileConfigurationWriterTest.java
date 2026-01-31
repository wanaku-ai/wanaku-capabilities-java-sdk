package ai.wanaku.capabilities.sdk.config.provider.file;

import static org.junit.jupiter.api.Assertions.*;

import ai.wanaku.capabilities.sdk.config.provider.api.ConfigWriteException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileConfigurationWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesDataToFileAndReturnsUri() throws IOException {
        FileConfigurationWriter writer = new FileConfigurationWriter(tempDir.toFile());
        String data = "key=value\nother=data";

        URI uri = writer.write("config.properties", data);

        assertNotNull(uri);
        Path writtenFile = Path.of(uri);
        assertTrue(Files.exists(writtenFile));
        assertEquals(data, Files.readString(writtenFile));
    }

    @Test
    void writesEmptyData() throws IOException {
        FileConfigurationWriter writer = new FileConfigurationWriter(tempDir.toFile());

        URI uri = writer.write("empty.txt", "");

        Path writtenFile = Path.of(uri);
        assertTrue(Files.exists(writtenFile));
        assertEquals("", Files.readString(writtenFile));
    }

    @Test
    void overwritesExistingFile() throws IOException {
        Path existingFile = tempDir.resolve("existing.txt");
        Files.writeString(existingFile, "old content");

        FileConfigurationWriter writer = new FileConfigurationWriter(tempDir.toFile());
        String newData = "new content";

        writer.write("existing.txt", newData);

        assertEquals(newData, Files.readString(existingFile));
    }

    @Test
    void constructorAcceptsStringPath() throws IOException {
        FileConfigurationWriter writer = new FileConfigurationWriter(tempDir.toString());
        String data = "test=value";

        URI uri = writer.write("test.properties", data);

        Path writtenFile = Path.of(uri);
        assertTrue(Files.exists(writtenFile));
        assertEquals(data, Files.readString(writtenFile));
    }

    @Test
    void throwsConfigWriteExceptionForInvalidDirectory() {
        File nonExistentDir = new File("/non/existent/path/that/does/not/exist");
        FileConfigurationWriter writer = new FileConfigurationWriter(nonExistentDir);

        assertThrows(ConfigWriteException.class, () -> writer.write("file.txt", "data"));
    }

    @Test
    void writesMultilineContent() throws IOException {
        FileConfigurationWriter writer = new FileConfigurationWriter(tempDir.toFile());
        String multilineData = "line1\nline2\nline3\n";

        URI uri = writer.write("multiline.txt", multilineData);

        Path writtenFile = Path.of(uri);
        assertEquals(multilineData, Files.readString(writtenFile));
    }

    @Test
    void writesContentWithUnicodeCharacters() throws IOException {
        FileConfigurationWriter writer = new FileConfigurationWriter(tempDir.toFile());
        String unicodeData = "key=日本語\nother=émoji🎉";

        URI uri = writer.write("unicode.txt", unicodeData);

        Path writtenFile = Path.of(uri);
        assertTrue(Files.exists(writtenFile));
    }
}
