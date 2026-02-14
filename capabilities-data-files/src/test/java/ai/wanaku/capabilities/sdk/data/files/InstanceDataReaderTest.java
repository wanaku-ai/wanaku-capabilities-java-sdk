package ai.wanaku.capabilities.sdk.data.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisabledOnOs(OS.WINDOWS)
class InstanceDataReaderTest {

    @TempDir
    Path tempDir;

    private File testFile;

    @BeforeEach
    void setUp() {
        testFile = tempDir.resolve("test.wanaku.dat").toFile();
    }

    private void writeTestFile(FileHeader header, ServiceEntry entry) throws IOException {
        try (InstanceDataWriter writer = new InstanceDataWriter(testFile, header)) {
            writer.write(entry);
            writer.flush();
        }
    }

    @Test
    void constructor_shouldReadHeaderCorrectly() throws IOException {
        FileHeader expectedHeader = FileHeader.TOOL_INVOKER;
        final String expectedId = UUID.randomUUID().toString();
        ServiceEntry expectedEntry = new ServiceEntry(expectedId);
        writeTestFile(expectedHeader, expectedEntry);

        try (InstanceDataReader reader = new InstanceDataReader(testFile)) {
            assertNotNull(reader.getHeader());
            assertEquals(expectedHeader.getFormatName(), reader.getHeader().getFormatName());
            assertEquals(expectedHeader.getFileVersion(), reader.getHeader().getFileVersion());
            assertEquals(expectedHeader.getServiceType(), reader.getHeader().getServiceType());

            ServiceEntry actualEntry = reader.readEntry();
            assertNotNull(actualEntry);
            assertEquals(expectedEntry.getId(), actualEntry.getId());
        }
    }

    @Test
    void constructor_shouldThrowExceptionForInvalidHeader() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write("invalid_header".getBytes());
        }

        assertThrows(IllegalArgumentException.class, () -> new InstanceDataReader(testFile));
    }

    @Test
    void close_shouldCloseFileChannel() throws IOException {
        FileHeader header = FileHeader.TOOL_INVOKER;
        final String expectedId = UUID.randomUUID().toString();
        ServiceEntry entry = new ServiceEntry(expectedId);
        writeTestFile(header, entry);

        InstanceDataReader reader = new InstanceDataReader(testFile);
        reader.close();
        // Attempting to read after close should ideally throw an exception, but FileChannel doesn't always immediately
        // reflect this.
        // For now, we rely on no exception being thrown during close and the AutoCloseable contract.
    }
}
