/**
 * 
 */
package gps.agps;

import bt747.sys.Interface;

/**
 * @author Mario
 * 
 */
public class AGPSSatRecord {

    private final byte[] satRecord;

    /**
     * 
     */
    public AGPSSatRecord() {
        satRecord = new byte[60];
        // TODO Auto-generated constructor stub
    }

    public AGPSSatRecord(final byte[] buffer, final int index) {
        satRecord = new byte[60];
        for (int i = 0; i < 60; i++) {
            satRecord[i] = buffer[index + i];
        }
    }

    public int fillbuffer(final byte[] buffer, final int index) {
        int bufidx = index;
        for (int i = 0; i < 60; i++) {
            buffer[bufidx++] = satRecord[i];
        }
        return bufidx;
    }

    private static final int SECONDS_FROM_1970_TO_1980 = Interface
            .getDateInstance(1, 1, 1980).dateToUTCepoch1970();

    public int getUTCTime() {
        int t;
        // Get time in hours from 1/1/1980
        t = satRecord[0] & 0xFF + ((satRecord[1] & 0xFF) << 8)
                + ((satRecord[2] & 0xFF) << 8);
        // Get seconds
        t *= 3600;
        // Add epoch.
        t += SECONDS_FROM_1970_TO_1980;
        return t;
    }
}
