package ai.wanaku.capabilities.sdk.api.types;

import java.util.Objects;

/**
 * Represents image content in a prompt message.
 * Images are provided as base64-encoded data with a MIME type.
 */
public class ImageContent implements PromptContent {
    private static final String TYPE = "image";

    /**
     * Base64-encoded image data.
     */
    private String data;

    /**
     * MIME type of the image (e.g., "image/png", "image/jpeg").
     */
    private String mimeType;

    /**
     * Default constructor for serialization frameworks.
     */
    public ImageContent() {}

    /**
     * Creates a new ImageContent with the specified data and MIME type.
     *
     * @param data the base64-encoded image data
     * @param mimeType the MIME type of the image
     */
    public ImageContent(String data, String mimeType) {
        this.data = data;
        this.mimeType = mimeType;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * Gets the base64-encoded image data.
     *
     * @return the image data
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the base64-encoded image data.
     *
     * @param data the image data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the MIME type of the image.
     *
     * @return the MIME type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the MIME type of the image.
     *
     * @param mimeType the MIME type to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageContent that = (ImageContent) o;
        return Objects.equals(data, that.data) && Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, mimeType);
    }

    @Override
    public String toString() {
        return "ImageContent{" + "type='"
                + TYPE + '\'' + ", mimeType='"
                + mimeType + '\'' + ", dataLength="
                + (data != null ? data.length() : 0) + '}';
    }
}
