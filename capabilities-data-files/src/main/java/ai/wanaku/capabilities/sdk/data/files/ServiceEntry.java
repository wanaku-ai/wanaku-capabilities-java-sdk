package ai.wanaku.capabilities.sdk.data.files;

/**
 * Represents an entry for a service, primarily storing its unique identifier (ID).
 */
public class ServiceEntry {
    /**
     * The fixed length of the service ID in characters.
     */
    public static final int ID_LENGTH = 36;
    /**
     * The total number of bytes occupied by a {@code ServiceEntry} when serialized.
     * This includes the ID length and an additional 4 bytes (likely for an integer field).
     */
    public static final int BYTES = ID_LENGTH + 4;

    private String id;

    /**
     * Default constructor for {@code ServiceEntry}.
     */
    ServiceEntry() {}

    /**
     * Constructs a {@code ServiceEntry} with the specified service ID.
     *
     * @param id The unique identifier of the service.
     */
    ServiceEntry(String id) {
        this.id = id;
    }

    /**
     * Returns the unique identifier of the service.
     *
     * @return The service ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the service.
     *
     * @param id The service ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }
}
