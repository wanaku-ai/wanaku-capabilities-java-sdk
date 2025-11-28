package ai.wanaku.capabilities.sdk.data.files;

import ai.wanaku.capabilities.sdk.api.types.providers.ServiceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileHeaderTest {

    @Test
    void constants_shouldHaveCorrectValues() {
        assertEquals("wanaku", FileHeader.FORMAT_NAME);
        assertEquals(6, FileHeader.FORMAT_NAME_SIZE);
        assertEquals(1, FileHeader.CURRENT_FILE_VERSION);
        // BYTES calculation: FORMAT_NAME_SIZE (6) + Integer.BYTES (4) + Integer.BYTES (4) + Integer.BYTES (4) + 2 = 20
        assertEquals(20, FileHeader.BYTES);
    }

    @Test
    void constructor_shouldSetFieldsCorrectly() {
        String formatName = "testFormat";
        ServiceType serviceType = ServiceType.TOOL_INVOKER;
        int fileVersion = 2;

        FileHeader fileHeader = new FileHeader(formatName, serviceType, fileVersion);

        assertEquals(formatName, fileHeader.getFormatName());
        assertEquals(serviceType, fileHeader.getServiceType());
        assertEquals(fileVersion, fileHeader.getFileVersion());
    }

    @Test
    void predefinedHeaders_shouldHaveCorrectValues() {
        // TOOL_INVOKER
        assertEquals(FileHeader.FORMAT_NAME, FileHeader.TOOL_INVOKER.getFormatName());
        assertEquals(ServiceType.TOOL_INVOKER, FileHeader.TOOL_INVOKER.getServiceType());
        assertEquals(FileHeader.CURRENT_FILE_VERSION, FileHeader.TOOL_INVOKER.getFileVersion());

        // RESOURCE_PROVIDER
        assertEquals(FileHeader.FORMAT_NAME, FileHeader.RESOURCE_PROVIDER.getFormatName());
        assertEquals(ServiceType.RESOURCE_PROVIDER, FileHeader.RESOURCE_PROVIDER.getServiceType());
        assertEquals(FileHeader.CURRENT_FILE_VERSION, FileHeader.RESOURCE_PROVIDER.getFileVersion());
    }
}