/**
 * 
 */
package net.sf.bt747.gps.mtk.agps;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario
 * 
 */
public class AgpsModel {
    private final BT747Hashtable table = JavaLibBridge
            .getHashtableInstance(14 * 4);

    public void addData(final byte[] buffer) {
        int idx;
        idx = 0;
        while (idx + 60 <= buffer.length) {
            final AgpsSatRecordModel r = new AgpsSatRecordModel(buffer, idx);
            AgpsPeriodModel p;
            TimeInt time;
            time = new TimeInt(r.getUTCTime());
            p = (AgpsPeriodModel) table.get(time);
            if (p == null) {
                p = new AgpsPeriodModel();
                table.put(time, p);
            }
            p.addIfBelongsTo(r);
            idx += 60;
        }
    }
    
    // TODO: Implement this.
    public byte[] getData(final int startTime) {
        byte[] result;

        // Determine size of selected data.
        // Get references to data
        final BT747Hashtable iter = table.iterator();
        int size = 0;
        while (iter.hasNext()) {
            final AgpsPeriodModel p = (AgpsPeriodModel) iter
                    .get(iter.nextKey());
            if (p.getTime() >= startTime) {
                size += p.getSize();
            }
        }
        
        // Sort data according to time and satnbr.

        // put data in buffer.
        result = new byte[sizeOfDataAfter(startTime)];
        int idx = 0;
        //idx = p.fillBuffer(result, idx, startIdx, endIdx);        
        return result;
    }

    public int sizeOfDataAfter(final int time) {
        final BT747Hashtable iter = table.iterator();
        int size = 0;
        while (iter.hasNext()) {
            final AgpsPeriodModel p = (AgpsPeriodModel) iter
                    .get(iter.nextKey());
            if (p.getTime() >= time) {
                size += p.getSize();
            }
        }
        return size;
    }

    private static class TimeInt {
        private int time;

        /**
         * 
         */
        public TimeInt(final int time) {
            this.time = time;
        }

        public int getTime() {
            return time;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(final Object obj) {
            if ((obj != null) && (obj instanceof TimeInt)) {
                return time == ((TimeInt) obj).getTime();
            } else {
                return false;
            }
        }
    }

}
