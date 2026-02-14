package ai.wanaku.capabilities.sdk.data.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceDataWriterTest {

    @TempDir
    Path tempDir;

    private File testFile;

    @BeforeEach
    void setUp() {
        testFile = tempDir.resolve("test.wanaku.dat").toFile();
    }

    @Test
    void constructor_shouldWriteHeaderToFile() throws IOException {
        FileHeader header = FileHeader.TOOL_INVOKER;
        try (InstanceDataWriter writer = new InstanceDataWriter(testFile, header)) {
            // Header is written in constructor, no explicit call needed
        }

        assertTrue(testFile.exists());

        // Verify header by reading it back
        try (InstanceDataReader reader = new InstanceDataReader(testFile)) {
            FileHeader readHeader = reader.getHeader();
            assertNotNull(readHeader);
            assertEquals(header.getFormatName(), readHeader.getFormatName());
            assertEquals(header.getFileVersion(), readHeader.getFileVersion());
            assertEquals(header.getServiceType(), readHeader.getServiceType());
        }
    }

    @Test
    void write_shouldWriteServiceEntryToFile() throws IOException {
        FileHeader header = FileHeader.RESOURCE_PROVIDER;
        ServiceEntry entry = new ServiceEntry("test-service-id-12345678901234567890");

        try (InstanceDataWriter writer = new InstanceDataWriter(testFile, header)) {
            writer.write(entry);
            writer.flush();
        }

        assertTrue(testFile.exists());

        // Verify entry by reading it back
        try (InstanceDataReader reader = new InstanceDataReader(testFile)) {
            reader.getHeader(); // Read header first
            ServiceEntry readEntry = reader.readEntry();
            assertNotNull(readEntry);
            assertEquals(entry.getId(), readEntry.getId());
        }
    }

    @Test
    void write_shouldWriteServiceEntryBeyondCapacityToFile() throws IOException {
        FileHeader header = FileHeader.RESOURCE_PROVIDER;
        final String expectedId = UUID.randomUUID().toString();
        ServiceEntry entry = new ServiceEntry(expectedId);

        try (InstanceDataWriter writer = new InstanceDataWriter(testFile, header)) {
            writer.write(entry);
            writer.flush();
        }

        assertTrue(testFile.exists());

        // Verify entry by reading it back
        try (InstanceDataReader reader = new InstanceDataReader(testFile)) {
            reader.getHeader(); // Read header first
            ServiceEntry readEntry = reader.readEntry();
            assertNotNull(readEntry);
            assertEquals(expectedId, readEntry.getId(), "The maximum size for the ID is 36");
        }
    }

    @Test
    void flush_shouldWriteBufferedDataToDisk() throws IOException {
        FileHeader header = FileHeader.TOOL_INVOKER;
        ServiceEntry entry = new ServiceEntry(UUID.randomUUID().toString());

        try (InstanceDataWriter writer = new InstanceDataWriter(testFile, header)) {
            writer.write(entry);
            // Data is buffered, not yet written to disk until flush or close
            assertTrue(
                    testFile.length() < FileHeader.BYTES + ServiceEntry.BYTES); // Should be only header size initially
            writer.flush();
        }
    }

    @Test
    void close_shouldFlushAndCloseFile() throws IOException {
        FileHeader header = FileHeader.RESOURCE_PROVIDER;
        ServiceEntry entry = new ServiceEntry(UUID.randomUUID().toString());

        InstanceDataWriter writer = new InstanceDataWriter(testFile, header);
        writer.write(entry);
        writer.close();

        assertTrue(testFile.exists());

        // Verify that the file is closed by trying to open it again (implicitly done by InstanceDataReader)
        try (InstanceDataReader reader = new InstanceDataReader(testFile)) {
            assertNotNull(reader.getHeader());
        }
    }
}
