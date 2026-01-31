package ai.wanaku.capabilities.sdk.config.provider.file;

import static org.junit.jupiter.api.Assertions.*;

import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PropertyFileProviderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsPropertiesFromValidFile() throws IOException {
        Path propsFile = tempDir.resolve("config.properties");
        Files.writeString(propsFile, "key1=value1\nkey2=value2\n");

        PropertyFileProvider provider = new PropertyFileProvider(propsFile.toUri());
        Properties props = provider.getProperties();

        assertEquals("value1", props.getProperty("key1"));
        assertEquals("value2", props.getProperty("key2"));
    }

    @Test
    void returnsEmptyPropertiesForNullUri() {
        PropertyFileProvider provider = new PropertyFileProvider(null);
        Properties props = provider.getProperties();

        assertNotNull(props);
        assertTrue(props.isEmpty());
    }

    @Test
    void throwsWanakuExceptionForNonExistentFile() {
        URI nonExistentUri = new File(tempDir.toFile(), "non-existent.properties").toURI();

        assertThrows(WanakuException.class, () -> new PropertyFileProvider(nonExistentUri));
    }

    @Test
    void handlesEmptyPropertiesFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty.properties");
        Files.writeString(emptyFile, "");

        PropertyFileProvider provider = new PropertyFileProvider(emptyFile.toUri());
        Properties props = provider.getProperties();

        assertNotNull(props);
        assertTrue(props.isEmpty());
    }

    @Test
    void handlesPropertiesWithSpecialCharacters() throws IOException {
        Path propsFile = tempDir.resolve("special.properties");
        Files.writeString(propsFile, "url=https://example.com/path?query=1&other=2\npath=/home/user/dir\n");

        PropertyFileProvider provider = new PropertyFileProvider(propsFile.toUri());
        Properties props = provider.getProperties();

        assertEquals("https://example.com/path?query=1&other=2", props.getProperty("url"));
        assertEquals("/home/user/dir", props.getProperty("path"));
    }

    @Test
    void handlesPropertiesWithWhitespaceInKeys() throws IOException {
        Path propsFile = tempDir.resolve("whitespace.properties");
        // Java Properties trims leading whitespace from keys and values, but not trailing whitespace from values
        Files.writeString(propsFile, "  key1=value1\nkey2=value with spaces\n");

        PropertyFileProvider provider = new PropertyFileProvider(propsFile.toUri());
        Properties props = provider.getProperties();

        assertEquals("value1", props.getProperty("key1"));
        assertEquals("value with spaces", props.getProperty("key2"));
    }
}
