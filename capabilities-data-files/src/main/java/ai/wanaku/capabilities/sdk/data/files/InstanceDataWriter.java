package ai.wanaku.capabilities.sdk.data.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes instance data to a file, including file headers and service entries.
 * This class implements {@link AutoCloseable} to ensure proper resource management.
 */
public class InstanceDataWriter implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(InstanceDataWriter.class);

    private final FileChannel fileChannel;

    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(FileHeader.BYTES + ServiceEntry.BYTES);

    /**
     * Constructs an {@code InstanceDataWriter}.
     * @param file The file to write instance data to.
     * @param fileHeader The {@link FileHeader} to write at the beginning of the file.
     * @throws IOException If an I/O error occurs during file channel creation or header writing.
     */
    InstanceDataWriter(final File file, final FileHeader fileHeader) throws IOException {
        fileChannel = new FileOutputStream(file).getChannel();

        writeHeader(fileHeader);
    }

    /**
     * Writes the content of the byte buffer to the file channel.
     * After writing, the buffer is flipped and cleared for subsequent writes.
     * @throws IOException If an I/O error occurs during writing.
     */
    private void write() throws IOException {
        byteBuffer.flip();

        while (byteBuffer.hasRemaining()) {
            fileChannel.write(byteBuffer);
        }

        byteBuffer.flip();
        byteBuffer.clear();
    }


    /**
     * Writes the provided {@link FileHeader} to the file.
     * @param header The {@link FileHeader} to write.
     * @throws IOException If an I/O error occurs during writing the header.
     */
    private void writeHeader(final FileHeader header) throws IOException {
        byteBuffer.clear();
        byteBuffer.put(header.getFormatName().getBytes());
        byteBuffer.putInt(header.getFileVersion());
        byteBuffer.putInt(header.getServiceType().intValue());

        write();
    }


    /**
     * Writes a {@link ServiceEntry} to the file.
     * @param entry The {@link ServiceEntry} to write.
     * @throws IOException If an I/O error occurs during writing the entry.
     */
    public void write(ServiceEntry entry) throws IOException {
        checkBufferCapacity();

        if (entry.getId().isEmpty()) {
            throw new IOException("Empty service entry");
        }

        if (entry.getId().length() != ServiceEntry.ID_LENGTH) {
            throw new IOException("Service entry id too long (it must not exceed the length of " + ServiceEntry.BYTES + ")" );
        }

        byteBuffer.put(entry.getId().getBytes());
    }

    /**
     * Checks if there is enough capacity in the byte buffer for a new {@link ServiceEntry}.
     * If not, the current buffer content is written to the file.
     * @throws IOException If an I/O error occurs during writing the buffer.
     */
    private void checkBufferCapacity() throws IOException {
        final int remaining = byteBuffer.remaining();

        if (remaining < ServiceEntry.BYTES) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("There is not enough space on the buffer for a new entry: {}", remaining);
            }

            write();
        }
    }

    /**
     * Flushes any buffered data to the underlying file and forces it to be written to the storage device.
     * @throws IOException If an I/O error occurs during flushing.
     */
    public void flush() throws IOException {
        write();
        fileChannel.force(true);
    }

    /**
     * Closes the underlying file channel and releases any system resources associated with it.
     * Any {@code IOException} that occurs during closing is logged.
     */
    @Override
    public void close() {
        try {
            flush();
            fileChannel.close();
        } catch (IOException e) {
            LOG.error("Error closing the writer: {}", e.getMessage(), e);
        }
    }
}
