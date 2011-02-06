/**
 * 
 */
package net.sf.bt747.j2me.app.ftp;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Mario
 * 
 */
public class BufferedWriter {
    private final OutputStreamWriter os;

    BufferedWriter(final OutputStreamWriter os) {
        this.os = os;
    }

    public void write(final String s) throws IOException {
        os.write(s);
    }

    public void flush() throws IOException {
        os.flush();
    }
}
