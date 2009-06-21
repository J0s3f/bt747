/**
 * 
 */
package net.sf.bt747.j2me.app.ftp;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Mario
 * 
 */
public class BufferedInputStream {
    private final InputStream is;

    /**
     * 
     */
    public BufferedInputStream(final InputStream is) {
        this.is = is;
    }

    public final int read(final byte[] b) throws IOException {
        return is.read(b);
    }

    public final void close() throws IOException {
        is.close();
    }
}
