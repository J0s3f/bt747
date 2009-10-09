/**
 * 
 */
package net.sf.bt747.test;

import gps.WondeproudConstants;
import gps.connection.GPSrxtx;

/**
 * @author Mario
 * 
 */
public final class DPL700DeviceWriter implements WondeproudConstants {

    static final byte[] EOS = { '\0' };

    public final static void sendCmd(final GPSrxtx context, byte[] b) {
        byte[] out = new byte[128];
        for (int i = 0; i < b.length;) {
            // Limit the size of what we write to not overflow the pipe.
            int maxidx = i + out.length;
            int lIdx = 0;
            if(maxidx>b.length) {
                maxidx = b.length;
                out = new byte[maxidx-i];
            }
            while (i < maxidx) {
                out[lIdx++] = b[i++];
            }
            context.write(out);
        }
        context.write(WP_UPDATE_OVER);
        context.write(EOS);
    }
}
