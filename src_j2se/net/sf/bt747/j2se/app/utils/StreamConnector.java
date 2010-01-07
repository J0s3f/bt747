/**
 * 
 */
package net.sf.bt747.j2se.app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public class StreamConnector extends Thread {
    private final InputStream is;
    private final OutputStream os;
    final byte[] buf;
    private final static int BUF_SIZE = 1024;
    

    /**
     * 
     */
    private StreamConnector(final InputStream is, final OutputStream os) {
        this.is = is;
        this.os = os;
        this.buf = new byte[BUF_SIZE];
    }

    public static final void connect(final InputStream is,
            final OutputStream os) {
        (new StreamConnector(is, os)).start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        //int totalCount = 0;
        try {
            while (true) {
                int count;
                count = is.read(buf);
                if (count > 0) {
                    os.write(buf, 0, count);
                } else if(count < 0) {
                    break;
                }
                //totalCount += count;
            }
        } catch (IOException e) {
            Generic.debug("StreamConnector", e);
        }
        //Generic.debug("StreamConnector Done "+ totalCount);
    }
}
