/**
 * 
 */
package bt747.j2se_view.model;

import gps.log.GPSRecord;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;

/**
 * Waypoint with extra data associated to location.
 * 
 * @author Mario
 * 
 */
public class MapWaypoint extends Waypoint {
    
    final BT747Waypoint wpt;
    /**
     * 
     */
    public MapWaypoint(final BT747Waypoint wpt) {
        this.wpt = wpt;
    }
    
    public MapWaypoint(final GPSRecord gpsRec) {
        this.wpt = new BT747Waypoint(gpsRec);
    }
    
//    private Object data;
//
//    public Object getData() {
//        return data;
//    }
//
//    public void setData(final Object data) {
//        this.data = data;
//    }
//
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
     * @param showTag
     *                the selected to set
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

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.Waypoint#getPosition()
     */
    @Override
    public GeoPosition getPosition() {
        final GPSRecord gpsRec = wpt.getGpsRecord();
        return new GeoPosition(gpsRec.latitude, gpsRec.longitude);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.Waypoint#setPosition(org.jdesktop.swingx.mapviewer.GeoPosition)
     */
    @Override
    public void setPosition(final GeoPosition coordinate) {
        final GPSRecord gpsRec = wpt.getGpsRecord();
        gpsRec.latitude = coordinate.getLatitude();
        gpsRec.longitude = coordinate.getLongitude();
    }
    
    
    /**
     * 
     */
    public GPSRecord getGpsRecord() {
        return wpt.getGpsRecord();
    }
    
    public BT747Waypoint getBT747Waypoint() {
        return wpt;
    }
    
    /**
     * 
     */
    public String getDescription() {
        return wpt.getDescription();
    }
}
