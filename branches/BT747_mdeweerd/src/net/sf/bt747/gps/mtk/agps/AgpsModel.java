/**
 * 
 */
package net.sf.bt747.gps.mtk.agps;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747InputStream;

/**
 * @author Mario
 * 
 */
public class AgpsModel {
    private final BT747Hashtable table = JavaLibBridge
            .getHashtableInstance(14 * 4);

    public final BT747InputStream getInputStream() {
        return new AgpsStream(table, 0);

    }

    private static class AgpsStream implements BT747InputStream {

        private final BT747Hashtable table;
        @SuppressWarnings("unused")
        private final int currentPos;
        private final int bytesToGo;

        /**
         * 
         */
        public AgpsStream(final BT747Hashtable table, final int startTime) {
            this.table = table;
            currentPos = 0;
            bytesToGo = 0;
            initData(startTime);
        }

        // TODO: Implement this.
        private byte[] initData(final int startTime) {
            byte[] result;

            // Determine size of selected data.
            // Get references to data
            final BT747Hashtable iter = table.iterator();
            @SuppressWarnings("unused")
            int size = 0;
            while (iter.hasNext()) {
                final AgpsPeriodModel p = (AgpsPeriodModel) iter.get(iter
                        .nextKey());
                if (p.getTime() >= startTime) {
                    size += p.getSize();
                }
            }

            // Sort data according to time and satnbr.

            // put data in buffer.
            result = new byte[sizeOfDataAfter(table, startTime)];
            @SuppressWarnings("unused")
            int idx = 0;
            // idx = p.fillBuffer(result, idx, startIdx, endIdx);
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see bt747.sys.interfaces.BT747InputStream#readBytes(byte[], int,
         *      int)
         */
        public final int readBytes(byte[] b, int off, int len) {
            if(bytesToGo>=0) {
                // TODO: fill buffer;
                // return bytes filled.
            }
            return -1;
        }
    }

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

    private static int sizeOfDataAfter(BT747Hashtable table, final int time) {
        final BT747Hashtable iter = table.iterator();
        int size = 0;
        while (iter.hasNext()) {
            final AgpsPeriodModel p = (AgpsPeriodModel) iter.get(iter
                    .nextKey());
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
