package ai.wanaku.capabilities.sdk.api.types.discovery;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActivityRecordTest {

    @Test
    void defaultConstructor() {
        ActivityRecord record = new ActivityRecord();
        assertEquals(HealthStatus.PENDING, record.getHealthStatus());
        assertTrue(record.getStates().isEmpty());
    }

    @Test
    void isActive_healthy() {
        ActivityRecord record = new ActivityRecord();
        record.setHealthStatus(HealthStatus.HEALTHY);
        assertTrue(record.isActive());
    }

    @Test
    void isActive_pendingAndRecent() {
        ActivityRecord record = new ActivityRecord();
        record.setHealthStatus(HealthStatus.PENDING);
        record.setLastSeen(Instant.now());
        assertTrue(record.isActive());
    }

    @Test
    void isActive_pendingAndStale() {
        ActivityRecord record = new ActivityRecord();
        record.setHealthStatus(HealthStatus.PENDING);
        record.setLastSeen(Instant.now().minusSeconds(ActivityRecord.TIME_TO_LET_GO * 60 + 60));
        assertFalse(record.isActive());
    }

    @Test
    void isActive_pendingNoLastSeen() {
        ActivityRecord record = new ActivityRecord();
        record.setHealthStatus(HealthStatus.PENDING);
        assertFalse(record.isActive());
    }

    @Test
    void isActive_down() {
        ActivityRecord record = new ActivityRecord();
        record.setHealthStatus(HealthStatus.DOWN);
        assertFalse(record.isActive());
    }

    @Test
    void isActive_unhealthy() {
        ActivityRecord record = new ActivityRecord();
        record.setHealthStatus(HealthStatus.UNHEALTHY);
        assertFalse(record.isActive());
    }

    @Test
    void settersAndGetters() {
        ActivityRecord record = new ActivityRecord();
        record.setId("rec-1");
        Instant now = Instant.now();
        record.setLastSeen(now);

        assertEquals("rec-1", record.getId());
        assertEquals(now, record.getLastSeen());
    }

    @Test
    void equalsAndHashCode() {
        Instant now = Instant.now();
        ActivityRecord a = new ActivityRecord();
        a.setId("1");
        a.setLastSeen(now);

        ActivityRecord b = new ActivityRecord();
        b.setId("1");
        b.setLastSeen(now);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_differentId() {
        ActivityRecord a = new ActivityRecord();
        a.setId("1");

        ActivityRecord b = new ActivityRecord();
        b.setId("2");

        assertNotEquals(a, b);
    }
}
