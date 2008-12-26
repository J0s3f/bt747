/**
 * 
 */
package bt747.j2se_view.model;

import gps.log.GPSRecord;
import gps.log.out.CommonOut;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;

/** Waypoint with extra data associated to location.
 * @author Mario
 *
 */
public class BT747Waypoint extends Waypoint {
    private Object data;

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    
    private boolean showTag = false;

    /**
     * @return the selected
     */
    public final boolean isShowTag() {
        return this.showTag;
    }

    /**
     * @param showTag
     *            the selected to set
     */
    public final void toggleShowTag() {
        this.showTag = !showTag;
    }
    
    
    private GPSRecord gpsRec;
    /**
     * 
     */
    public BT747Waypoint(final GPSRecord r) {
        gpsRec = r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.Waypoint#getPosition()
     */
    @Override
    public GeoPosition getPosition() {
        return new GeoPosition(gpsRec.latitude, gpsRec.longitude);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.Waypoint#setPosition(org.jdesktop.swingx.mapviewer.GeoPosition)
     */
    @Override
    public void setPosition(GeoPosition coordinate) {
        gpsRec.latitude = coordinate.getLatitude();
        gpsRec.longitude = coordinate.getLongitude();
    }

    /**
     * @return the gpsRec
     */
    public final GPSRecord getGpsRecord() {
        return this.gpsRec;
    }

    /**
     * @param gpsRec
     *            the gpsRec to set
     */
    public final void setGpsRec(GPSRecord gpsRec) {
        this.gpsRec = gpsRec;
    }

    public String getDescription() {
        return "#" + gpsRec.recCount + " " + CommonOut.getRcrSymbolText(gpsRec);
    }

}
