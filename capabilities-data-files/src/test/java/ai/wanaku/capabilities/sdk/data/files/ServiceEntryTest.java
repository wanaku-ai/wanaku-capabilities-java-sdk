package ai.wanaku.capabilities.sdk.data.files;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServiceEntryTest {

    @Test
    void constants_shouldHaveCorrectValues() {
        assertEquals(36, ServiceEntry.ID_LENGTH);
        assertEquals(40, ServiceEntry.BYTES); // 36 (ID_LENGTH) + 4 (Integer.BYTES)
    }

    @Test
    void constructor_noArgsConstructor_shouldCreateInstance() {
        ServiceEntry entry = new ServiceEntry();
        assertNotNull(entry);
        assertNull(entry.getId());
    }

    @Test
    void constructor_withId_shouldSetIdCorrectly() {
        String testId = UUID.randomUUID().toString();
        ServiceEntry entry = new ServiceEntry(testId);
        assertNotNull(entry);
        assertEquals(testId, entry.getId());
    }

    @Test
    void setId_shouldUpdateId() {
        ServiceEntry entry = new ServiceEntry();
        String newId = UUID.randomUUID().toString();
        entry.setId(newId);
        assertEquals(newId, entry.getId());
    }

    @Test
    void getId_shouldReturnCurrentId() {
        String testId = UUID.randomUUID().toString();
        ServiceEntry entry = new ServiceEntry(testId);
        assertEquals(testId, entry.getId());
    }
}
