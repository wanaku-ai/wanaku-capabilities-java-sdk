package ai.wanaku.capabilities.sdk.data.files.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DataFileHelperTest {

    @TempDir
    Path tempDir;

    @Test
    void newRandomizedDataFile_shouldCreateFileInServiceHome() {
        String serviceHome = tempDir.toAbsolutePath().toString();
        File dataFile = DataFileHelper.newRandomizedDataFile(serviceHome);

        assertNotNull(dataFile);
        assertTrue(dataFile.getAbsolutePath().startsWith(serviceHome));
        assertTrue(dataFile.getName().endsWith(".properties"));
        assertFalse(dataFile.exists()); // Should not exist yet, just the File object
    }

    @Test
    void newRandomizedDataFile_shouldGenerateUniqueNames() {
        String serviceHome = tempDir.toAbsolutePath().toString();
        File dataFile1 = DataFileHelper.newRandomizedDataFile(serviceHome);
        File dataFile2 = DataFileHelper.newRandomizedDataFile(serviceHome);

        assertNotEquals(dataFile1.getName(), dataFile2.getName());
    }
}