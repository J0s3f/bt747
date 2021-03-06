/**
 * 
 */
package net.sf.bt747.gps.mtk.agps;

import bt747.sys.JavaLibBridge;

/**
 * @author Mario
 * 
 */
public class AgpsSatRecordModel {

    private final byte[] satRecord;

    /**
     * 
     */
    public AgpsSatRecordModel() {
        satRecord = new byte[60];
        // TODO Auto-generated constructor stub
    }

    public AgpsSatRecordModel(final byte[] buffer, final int index) {
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

    private static final int SECONDS_FROM_1970_TO_1980 = JavaLibBridge
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
