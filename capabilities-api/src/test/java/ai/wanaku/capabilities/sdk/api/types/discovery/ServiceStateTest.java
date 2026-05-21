package ai.wanaku.capabilities.sdk.api.types.discovery;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceStateTest {

    @Test
    void newHealthy() {
        ServiceState state = ServiceState.newHealthy();
        assertTrue(state.isHealthy());
        assertEquals(HealthStatus.HEALTHY, state.getHealthStatus());
        assertNotNull(state.getTimestamp());
        assertEquals(StandardMessages.HEALTHY, state.getReason());
    }

    @Test
    void newUnhealthy() {
        ServiceState state = ServiceState.newUnhealthy("timeout");
        assertFalse(state.isHealthy());
        assertEquals(HealthStatus.UNHEALTHY, state.getHealthStatus());
        assertEquals("timeout", state.getReason());
    }

    @Test
    void newMissingInAction() {
        ServiceState state = ServiceState.newMissingInAction();
        assertFalse(state.isHealthy());
        assertEquals(HealthStatus.DOWN, state.getHealthStatus());
        assertEquals(StandardMessages.MISSING_IN_ACTION, state.getReason());
    }

    @Test
    void newInactive() {
        ServiceState state = ServiceState.newInactive();
        assertFalse(state.isHealthy());
        assertEquals(HealthStatus.DOWN, state.getHealthStatus());
        assertEquals(StandardMessages.AUTO_DEREGISTRATION, state.getReason());
    }

    @Test
    void newPending() {
        ServiceState state = ServiceState.newPending();
        assertFalse(state.isHealthy());
        assertEquals(HealthStatus.PENDING, state.getHealthStatus());
    }

    @Test
    void newDown() {
        ServiceState state = ServiceState.newDown("crashed");
        assertFalse(state.isHealthy());
        assertEquals(HealthStatus.DOWN, state.getHealthStatus());
        assertEquals("crashed", state.getReason());
    }

    @Test
    void constructorWithHealthyFlag() {
        Instant now = Instant.now();
        ServiceState state = new ServiceState(now, true, "ok");
        assertTrue(state.isHealthy());
        assertEquals(HealthStatus.HEALTHY, state.getHealthStatus());
        assertEquals(now, state.getTimestamp());
    }

    @Test
    void constructorWithUnhealthyFlag() {
        Instant now = Instant.now();
        ServiceState state = new ServiceState(now, false, "fail");
        assertFalse(state.isHealthy());
        assertEquals(HealthStatus.UNHEALTHY, state.getHealthStatus());
    }

    @Test
    void settersAndGetters() {
        ServiceState state = new ServiceState();
        Instant now = Instant.now();
        state.setTimestamp(now);
        state.setHealthy(true);
        state.setHealthStatus(HealthStatus.HEALTHY);
        state.setReason("all good");

        assertEquals(now, state.getTimestamp());
        assertTrue(state.isHealthy());
        assertEquals(HealthStatus.HEALTHY, state.getHealthStatus());
        assertEquals("all good", state.getReason());
    }

    @Test
    void equalsAndHashCode() {
        Instant now = Instant.now();
        ServiceState a = new ServiceState(now, true, HealthStatus.HEALTHY, "ok");
        ServiceState b = new ServiceState(now, true, HealthStatus.HEALTHY, "ok");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_differentHealth() {
        Instant now = Instant.now();
        ServiceState a = new ServiceState(now, true, "ok");
        ServiceState b = new ServiceState(now, false, "fail");
        assertNotEquals(a, b);
    }
}
