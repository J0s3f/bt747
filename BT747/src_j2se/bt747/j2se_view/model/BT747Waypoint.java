/**
 * 
 */
package bt747.j2se_view.model;

import gps.log.GPSRecord;
import gps.log.out.CommonOut;

/**
 * @author Mario
 *
 */
public class BT747Waypoint {
    private GPSRecord gpsRec;

    /**
     * 
     */
    public BT747Waypoint(final GPSRecord r) {
        gpsRec = r;
    }

    /**
     * @return the gpsRec
     */
    public final GPSRecord getGpsRecord() {
        return gpsRec;
    }

    /**
     * @param gpsRec
     *                the gpsRec to set
     */
    public final void setGpsRec(final GPSRecord gpsRec) {
        this.gpsRec = gpsRec;
    }

    public String getDescription() {
        if(gpsRec.hasRecCount()) {
        return "#" + gpsRec.getRecCount() + " "
                + CommonOut.getRcrSymbolText(gpsRec);
        } else {
            return CommonOut.getRcrSymbolText(gpsRec);
        }
    }

}
