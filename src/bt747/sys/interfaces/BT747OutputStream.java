/**
 * 
 */
package bt747.sys.interfaces;

/**
 * @author Mario
 *
 */
public interface BT747OutputStream {
    /**
     * Write bytes from byte buffer to file.
     * 
     * @param b
     *                Byte buffer.
     * @param off
     *                Start offset in buffer to start writing to file.
     * @param len
     *                Number of bytes to write to file.
     * @return number of bytes written.
     */
    int writeBytes(final byte[] b, final int off, final int len);
}
