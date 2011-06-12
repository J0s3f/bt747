/**
 * 
 */
package bt747.sys.interfaces;

/**
 * @author Mario
 *
 */
public interface BT747InputStream {
    /**
     * Read bytes from file to buffer.
     * 
     * @param b
     *                Byte buffer.
     * @param off
     *                Start offset in buffer to start reading from file.
     * @param len
     *                Number of bytes to read from file.
     * @return number of bytes read.
     */
    int readBytes(final byte[] b, final int off, final int len);
}
