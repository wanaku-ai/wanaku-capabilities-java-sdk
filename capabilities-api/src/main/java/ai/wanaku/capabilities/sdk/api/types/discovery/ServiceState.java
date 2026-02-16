package ai.wanaku.capabilities.sdk.api.types.discovery;

import java.time.Instant;
import java.util.Objects;

/**
 * Contains the state of the service for an specific point in time
 */
public class ServiceState {
    private Instant timestamp;
    private boolean healthy;
    private HealthStatus healthStatus;
    private String reason;

    /**
     * Default constructor for serialization frameworks.
     */
    public ServiceState() {}

    /**
     * Saves the current state of the service
     * @param timestamp the current timestamp
     * @param healthy whether it is healthy (true for healthy, false otherwise)
     * @param reason Optional state message (ignored if healthy)
     */
    public ServiceState(Instant timestamp, boolean healthy, String reason) {
        this.timestamp = timestamp;
        this.healthy = healthy;
        this.healthStatus = healthy ? HealthStatus.HEALTHY : HealthStatus.UNHEALTHY;
        this.reason = reason;
    }

    /**
     * Saves the current state of the service with an explicit health status
     * @param timestamp the current timestamp
     * @param healthy whether it is healthy (true for healthy, false otherwise)
     * @param healthStatus the detailed health status
     * @param reason Optional state message (ignored if healthy)
     */
    public ServiceState(Instant timestamp, boolean healthy, HealthStatus healthStatus, String reason) {
        this.timestamp = timestamp;
        this.healthy = healthy;
        this.healthStatus = healthStatus;
        this.reason = reason;
    }

    /**
     * Gets the timestamp when this service state was recorded.
     *
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when this service state was recorded.
     *
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Checks if the service is healthy.
     *
     * @return {@code true} if the service is healthy, {@code false} otherwise
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * Sets the health status of the service.
     *
     * @param healthy the health status to set
     */
    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    /**
     * Gets the detailed health status.
     *
     * @return the health status enum value
     */
    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    /**
     * Sets the detailed health status.
     *
     * @param healthStatus the health status to set
     */
    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    /**
     * Gets the reason for the current service state.
     *
     * @return the reason message, or {@code null} if not set
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for the current service state.
     *
     * @param reason the reason message to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServiceState that = (ServiceState) o;
        return healthy == that.healthy
                && healthStatus == that.healthStatus
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, healthy, healthStatus, reason);
    }

    @Override
    public String toString() {
        return "ServiceState{" + "timestamp=" + timestamp + ", healthy=" + healthy + ", healthStatus=" + healthStatus
                + ", reason='" + reason + '\'' + '}';
    }

    /**
     * Creates a new healthy service state with the current timestamp.
     *
     * @return a new healthy ServiceState instance
     */
    public static ServiceState newHealthy() {
        return new ServiceState(Instant.now(), true, HealthStatus.HEALTHY, StandardMessages.HEALTHY);
    }

    /**
     * Creates a new unhealthy service state with the current timestamp and the specified reason.
     *
     * @param reason the reason for the unhealthy state
     * @return a new unhealthy ServiceState instance
     */
    public static ServiceState newUnhealthy(String reason) {
        return new ServiceState(Instant.now(), false, HealthStatus.UNHEALTHY, reason);
    }

    /**
     * Creates a new service state indicating the service is missing in action.
     *
     * @return a new ServiceState instance with missing-in-action status
     */
    public static ServiceState newMissingInAction() {
        return new ServiceState(Instant.now(), false, HealthStatus.DOWN, StandardMessages.MISSING_IN_ACTION);
    }

    /**
     * Creates a new inactive service state with the current timestamp.
     *
     * @return a new inactive ServiceState instance
     */
    public static ServiceState newInactive() {
        return new ServiceState(Instant.now(), false, HealthStatus.DOWN, StandardMessages.AUTO_DEREGISTRATION);
    }

    /**
     * Creates a new pending service state with the current timestamp.
     *
     * @return a new pending ServiceState instance
     */
    public static ServiceState newPending() {
        return new ServiceState(Instant.now(), false, HealthStatus.PENDING, "pending health check");
    }

    /**
     * Creates a new down service state with the current timestamp and the specified reason.
     *
     * @param reason the reason the service is down
     * @return a new down ServiceState instance
     */
    public static ServiceState newDown(String reason) {
        return new ServiceState(Instant.now(), false, HealthStatus.DOWN, reason);
    }
}
