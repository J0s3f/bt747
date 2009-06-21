/**
 * 
 */
package net.sf.bt747.j2me.app.ftp;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Mario
 * 
 */
public class BufferedReader {

    private final InputStreamReader is;

    BufferedReader(final InputStreamReader is) {
        this.is = is;
    }

    public String readLine() throws IOException {
        final StringBuffer s = new StringBuffer(100);
        char c;
        while (true) {
            c = (char) is.read();
            if (c == '\n') {
                break;
            }
            s.append(c);
        }
        ;
        return s.toString();
    }
}
