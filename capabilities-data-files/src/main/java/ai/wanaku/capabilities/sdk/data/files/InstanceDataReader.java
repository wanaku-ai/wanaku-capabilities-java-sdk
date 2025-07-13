package ai.wanaku.capabilities.sdk.data.files;

import ai.wanaku.api.types.providers.ServiceType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads instance data from a file, including file headers and service entries.
 * This class implements {@link AutoCloseable} to ensure proper resource management.
 */
public class InstanceDataReader implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(InstanceDataReader.class);

    private final FileChannel fileChannel;
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(FileHeader.BYTES + ServiceEntry.BYTES);

    private final FileHeader fileHeader;

    static {
        assert (((FileHeader.BYTES + ServiceEntry.BYTES) % 20) == 0):
                "File header and the file entries must be aligned on a 20 bytes boundary";
    }


    /**
     * Constructs an {@code InstanceDataReader}.
     * @param fileName The file to read instance data from.
     * @throws IOException If an I/O error occurs during file channel creation or header reading.
     */
    InstanceDataReader(final File fileName) throws IOException {
        fileChannel = new FileInputStream(fileName).getChannel();

        fileHeader = readHeader();
    }

    /**
     * Gets the file header that was read upon initialization.
     * @return The {@link FileHeader} of the data file.
     */
    public FileHeader getHeader() {
        return fileHeader;
    }

    /**
     * Reads the file header from the beginning of the file.
     * @return The {@link FileHeader} read from the file.
     * @throws IOException If an I/O error occurs during reading.
     * @throws IllegalArgumentException If the file does not contain a valid header.
     */
    private FileHeader readHeader() throws IOException {
        byteBuffer.clear();
        int bytesRead = fileChannel.read(byteBuffer);
        if (bytesRead <= 0) {
            throw new IllegalArgumentException("The file does not contain a valid header");
        }

        LOG.trace("Read {} bytes from the file channel", bytesRead);
        byteBuffer.flip();

        byte[] name = new byte[FileHeader.FORMAT_NAME_SIZE];
        byteBuffer.get(name, 0, FileHeader.FORMAT_NAME_SIZE);
        LOG.trace("File format name: '{}'", new String(name));

        int fileVersion = byteBuffer.getInt();
        LOG.trace("File version: '{}'", fileVersion);

        ServiceType serviceType = ServiceType.fromIntValue(byteBuffer.getInt());
        LOG.trace("Role: '{}'", serviceType.intValue());

        return new FileHeader(new String(name), serviceType, fileVersion);
    }

    /**
     * Reads a {@link ServiceEntry} from the file.
     * @return A {@link ServiceEntry} from the file, or {@code null} if the end of the file is reached.
     * @throws IOException If an I/O error occurs during reading the entry.
     */
    public ServiceEntry readEntry() throws IOException {
        logBufferInfo();

        if (byteBuffer.hasRemaining()) {
            byte[] idBytes = new byte[ServiceEntry.ID_LENGTH];
            byteBuffer.get(idBytes, 0, ServiceEntry.ID_LENGTH);

            return new ServiceEntry(new String(idBytes));
        }

        byteBuffer.compact();
        int read = fileChannel.read(byteBuffer);
        if (read <= 0) {
            return null;
        }
        byteBuffer.flip();

        byte[] idBytes = new byte[ServiceEntry.ID_LENGTH];
        byteBuffer.get(idBytes, 0, ServiceEntry.ID_LENGTH);

        return new ServiceEntry(new String(idBytes));
    }

    /**
     * Logs information about the current state of the byte buffer for debugging purposes.
     */
    private void logBufferInfo() {
        LOG.trace("Remaining: {}", byteBuffer.remaining());
        LOG.trace("Position: {}", byteBuffer.position());
        LOG.trace("Has Remaining: {}", byteBuffer.hasRemaining());
    }

    /**
     * Closes the underlying file channel and releases any system resources associated with it.
     * Any {@code IOException} that occurs during closing is logged.
     */
    @Override
    public void close() {
        try {
            fileChannel.close();
        } catch (IOException e) {
            LOG.error("Error closing the reader: {}", e.getMessage(), e);
        }
    }
}
