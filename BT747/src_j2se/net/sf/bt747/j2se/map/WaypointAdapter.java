/**
 * 
 */
package net.sf.bt747.j2se.map;

import gps.log.GPSRecord;
import gps.log.out.CommonOut;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

/**
 * @author Mario
 * 
 */
public class WaypointAdapter extends Waypoint {

    private GPSRecord gpsRec;
    private WaypointRenderer renderer = BT747WayPointRenderer.getInstance();

    /**
     * 
     */
    public WaypointAdapter(final GPSRecord r) {
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
    public final GPSRecord getGpsRec() {
        return this.gpsRec;
    }

    /**
     * @param gpsRec
     *            the gpsRec to set
     */
    public final void setGpsRec(GPSRecord gpsRec) {
        this.gpsRec = gpsRec;
    }

    /**
     * @return the renderer
     */
    public final WaypointRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * @param renderer
     *            the renderer to set
     */
    public final void setRenderer(WaypointRenderer renderer) {
        this.renderer = renderer;
    }

    private boolean selected = false;

    /**
     * @return the selected
     */
    public final boolean isSelected() {
        return this.selected;
    }

    /**
     * @param selected
     *            the selected to set
     */
    public final void toggleSelected() {
        this.selected = !selected;
    }

    public String getDescription() {
        return "#" + gpsRec.recCount + " " + CommonOut.getRcrSymbolText(gpsRec);
    }
}
