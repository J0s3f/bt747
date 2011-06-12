/**
 * 
 */
package bt747.j2se_view.model;

import gps.log.GPSRecord;
import gps.log.out.CommonOut;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;

/**
 * Waypoint with extra data associated to location.
 * 
 * @author Mario
 * 
 */
public class GPSPositionWaypoint extends Waypoint {
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public static final String PROPERTY_SELECTED = "selected";
    public static final String PROPERTY_SHOWTAG = "showtag";

    /**
     * @param selected
     *                the selected to set
     */
    public void setSelected(boolean selected) {
        if (selected != this.selected) {
            this.selected = selected;
            firePropertyChange(PROPERTY_SELECTED, !selected, selected);
        }
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    private boolean showTag = false;
    private boolean selected = false;

    /**
     * @return the selected
     */
    public final boolean isShowTag() {
        return showTag;
    }

    /**
     * Toggles the 'show tag' status.
     */
    public final void toggleShowTag() {
        setShowTag(!showTag);
    }

    public final void setShowTag(boolean b) {
        if (b != showTag) {
            showTag = b;
            firePropertyChange(PROPERTY_SHOWTAG, !b, b);
        }
    }

    private GPSRecord gpsRec;

    /**
     * 
     */
    public GPSPositionWaypoint(final GPSRecord r) {
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
    public void setPosition(final GeoPosition coordinate) {
        gpsRec.latitude = coordinate.getLatitude();
        gpsRec.longitude = coordinate.getLongitude();
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
