/**
 * 
 */
package bt747.j2se_view.agps;

import bt747.sys.Convert;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario
 *
 */
public class AGPS {
    private final BT747Hashtable table = Interface.getHashtableInstance(14*4);
    

    public void addData(byte[] buffer) {
        int idx;
        idx = 0;
        while(idx+60<=buffer.length) {
            AGPSSatRecord r = new AGPSSatRecord(buffer,idx);
            AGPSPeriodData p;
            TimeInt time;
            time = new TimeInt(r.getUTCTime());
            p = (AGPSPeriodData) table.get(time);
            if(p==null) {
                p = new AGPSPeriodData();
                table.put(time, p);
            }
            p.addIfBelongsTo(r);
            idx+=60;
        }
    }
    
    public int sizeOfDataAfter(int time) {
        BT747Hashtable iter = table.iterator();
        int size = 0;
        while(iter.hasNext()) {
            AGPSPeriodData p = (AGPSPeriodData) iter.get(iter.nextKey());
            if(p.getTime()>=time) {
                size += p.getSize();
            }
        }
        return size;
    }

    private class TimeInt {
        private int time;

        /**
         * 
         */
        public TimeInt(int time) {
            this.time = time;
        }
        
        public int getTime() {
            return time;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return time == ((TimeInt)obj).getTime();
        }
    }

}