package ai.wanaku.capabilities.sdk.data.files;

import ai.wanaku.api.types.providers.ServiceTarget;
import ai.wanaku.api.types.providers.ServiceType;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceDataManagerTest {

    @TempDir
    Path tempDir;

    private String dataDir;
    private String serviceName;
    private InstanceDataManager instanceDataManager;

    @BeforeEach
    void setUp() {
        dataDir = tempDir.toAbsolutePath().toString();
        serviceName = "testService";
        instanceDataManager = new InstanceDataManager(dataDir, serviceName);
    }

    @Test
    void dataFileExists_shouldReturnFalseInitially() {
        assertFalse(instanceDataManager.dataFileExists());
    }

    @Test
    void createDataDirectory_shouldCreateDirectory() throws IOException {
        instanceDataManager.createDataDirectory();
        assertTrue(Files.exists(Path.of(dataDir)));
    }

    @Test
    void writeAndReadEntry_shouldWorkCorrectly() throws IOException {
        instanceDataManager.createDataDirectory();

        final String expectedID = UUID.randomUUID().toString();
        ServiceTarget serviceTarget = ServiceTarget
                .newEmptyTarget("testService", "localhost", 9190, ServiceType.TOOL_INVOKER);
        serviceTarget.setId(expectedID);

        instanceDataManager.writeEntry(serviceTarget);

        assertTrue(instanceDataManager.dataFileExists());

        ServiceEntry readEntry = instanceDataManager.readEntry();
        assertNotNull(readEntry);
        assertEquals(expectedID, readEntry.getId());
    }

    @Test
    void readEntry_shouldReturnNullIfFileDoesNotExist() {
        assertNull(instanceDataManager.readEntry());
    }
}