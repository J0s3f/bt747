/**
 * 
 */
package net.sf.bt747.j2me.app.ftp;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Mario
 * 
 */
public class BufferedOutputStream {
    private final OutputStream os;

    /**
     * 
     */
    public BufferedOutputStream(final OutputStream os) {
        this.os = os;
    }

    /**
     * 
     */
    public void flush() throws IOException {
        os.flush();
    }

    public void write(final byte[] b, final int off, final int len)
            throws IOException {
        os.write(b, off, len);
    }

    public void close() throws IOException {
        os.close();
    }
}
