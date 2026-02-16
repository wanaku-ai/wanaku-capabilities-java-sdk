package ai.wanaku.capabilities.sdk.api.types.discovery;

/**
 * Represents the health status of a service in the Wanaku system.
 */
public enum HealthStatus {
    PENDING("pending"),
    HEALTHY("healthy"),
    UNHEALTHY("unhealthy"),
    DOWN("down");

    private final String value;

    HealthStatus(String value) {
        this.value = value;
    }

    /**
     * Returns the lowercase string representation of this health status.
     *
     * @return the status value as a lowercase string
     */
    public String asValue() {
        return value;
    }

    /**
     * Converts a string value to a HealthStatus enum constant.
     *
     * @param value the string value to convert
     * @return the matching HealthStatus, or {@link #PENDING} if no match is found or value is null
     */
    public static HealthStatus fromValue(String value) {
        if (value == null) {
            return PENDING;
        }
        for (HealthStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return PENDING;
    }
}
