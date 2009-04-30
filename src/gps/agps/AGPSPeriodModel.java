/**
 * 
 */
package gps.agps;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Vector;

/**
 * @author Mario
 * 
 */
public class AGPSPeriodModel {
    private int time = 0;

    private final BT747Vector satData = JavaLibBridge.getVectorInstance();

    public boolean addIfBelongsTo(final AGPSSatRecordModel r) {
        final int rTime = r.getUTCTime();
        if (time == 0) {
            time = rTime;
        }
        if (time == rTime) {
            satData.addElement(r);
            return true;
        }
        return false;
    }

    public int getTime() {
        return time;
    }

    public int getSize() {
        return satData.size() * 60;
    }

    public int sizeOf(final byte[] buffer, final int startIdx,
            final int endIdx) {
        return (endIdx - startIdx + 1) * 60;
    }

    /**
     * @param buffer
     * @param bufidx
     * @param startIdx
     * @param endIdx
     * @return new buffer index.
     */
    public int fillBuffer(final byte[] buffer, int bufidx,
            final int startIdx, final int endIdx) {
        for (int i = startIdx; i <= endIdx; i++) {
            bufidx = ((AGPSSatRecordModel) satData.elementAt(i)).fillbuffer(
                    buffer, bufidx);
        }
        return bufidx;
    }
}
