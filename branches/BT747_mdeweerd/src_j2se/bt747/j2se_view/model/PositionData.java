/**
 * 
 */
package bt747.j2se_view.model;

import gps.BT747Constants;
import gps.log.GPSRecord;

import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;

import org.jdesktop.swingx.JXPanel;

import bt747.j2se_view.J2SEAppModel;
import bt747.j2se_view.WayPointPanel;
import bt747.model.ModelEvent;

/**
 * @author Mario
 * 
 */
@SuppressWarnings("serial")
public class PositionData extends JXPanel {
    J2SEAppModel m;
    private List<List<GPSRecord>> trks = new Vector<List<GPSRecord>>();
    private final Vector<BT747Waypoint> wayPoints = new Vector<BT747Waypoint>();
    private final Vector<BT747Waypoint> userWayPoints = new Vector<BT747Waypoint>();
    // private GPSRecord[] wayPoints = null;
    // private GPSRecord[] userWayPoints = null;
    protected static final int C_LOGDIST = -8;
    protected static final int C_LOGSPD = -7;
    protected static final int C_LOGTIME = -9;
    protected static final int FMT_DATE = -3;
    protected static final int FMT_DISTANCE_FT_IDX = BT747Constants.FMT_DISTANCE_IDX + 100;
    protected static final int FMT_EW = -5;
    protected static final int FMT_FIXMODE = -13;
    protected static final int FMT_HEIGHT_FT_IDX = BT747Constants.FMT_HEIGHT_IDX + 100;
    protected static final int FMT_LATNS = -10;
    protected static final int FMT_LONEW = -11;
    protected static final int FMT_NO_FIELD = -1;
    protected static final int FMT_NS = -6;
    protected static final int FMT_RCR_DESCRIPTION = -14;
    protected static final int FMT_REC_NBR = -4;
    protected static final int FMT_SPEED_MPH_IDX = BT747Constants.FMT_SPEED_IDX + 100;
    protected static final int FMT_TIME = -2;
    /**
     * Column identifications. Some taken from BT747Constants.
     */
    protected static final int FMT_UTC_VALUE = -15;
    protected static final int FMT_VOX = -12;

    /**
     * 
     */
    public PositionData() {
        // TODO Auto-generated constructor stub
    }

    public PositionData(J2SEAppModel m) {
        this.m = m;
    }

    public final List<List<GPSRecord>> getTracks() {
        return this.trks;
    }

    public final void setTracks(List<List<GPSRecord>> trks) {
        this.trks = trks;
        fireTrackPointListChange();
    }

    public final GPSRecord[] getWayPoints() {
        GPSRecord[] r = new GPSRecord[wayPoints.size()];
        int index = 0;
        for (BT747Waypoint w : wayPoints) {
            r[index++] = w.getGpsRecord();
        }
        return r;
    }
    
    public final List<BT747Waypoint> getBT747Waypoints() {
        return wayPoints;
    }

    public final List<BT747Waypoint> getBT747UserWaypoints() {
        return userWayPoints;
    }


    public final void setWayPoints(GPSRecord[] waypoints) {
        wayPoints.removeAllElements();
        for (GPSRecord wp : waypoints) {
            wayPoints.add(new BT747Waypoint(wp));
        }
        fireWaypointListUpdate();
    }


    public final GPSRecord[] getUserWayPoints() {
        GPSRecord[] r = new GPSRecord[userWayPoints.size()];
        int index = 0;
        for (BT747Waypoint w : userWayPoints) {
            r[index++] = w.getGpsRecord();
        }
        return r;
    }

    public final void setUserWayPoints(GPSRecord[] userWaypoints) {
        userWayPoints.removeAllElements();
        for (GPSRecord wp : userWaypoints) {
            userWayPoints.add(new BT747Waypoint(wp));
        }
        fireUserWaypointUpdate();
    }

    public static String getDataDescriptionTitle(int type) {
        switch (type) {
        case PositionData.C_LOGTIME:
            break;
        case PositionData.C_LOGDIST:
            break;
        case PositionData.C_LOGSPD:
            break;
        case PositionData.FMT_NS:
            break;
        case PositionData.FMT_EW:
            break;
        case PositionData.FMT_REC_NBR:
            return "Record nbr";
        case BT747Constants.FMT_UTC_IDX:
            return "Date & Time";
        case PositionData.FMT_UTC_VALUE:
            return "UTC Value";
        case PositionData.FMT_DATE:
            return "Date";
        case PositionData.FMT_TIME:
            return "Time";
        case BT747Constants.FMT_VALID_IDX:
            return "Valid";
        case PositionData.FMT_LATNS:
            break;
        case PositionData.FMT_LONEW:
            break;
        case BT747Constants.FMT_LATITUDE_IDX:
            return "Latitude";
        case BT747Constants.FMT_LONGITUDE_IDX:
            return "Longitude";
        case BT747Constants.FMT_HEIGHT_IDX:
            return "Height (m)";
        case PositionData.FMT_HEIGHT_FT_IDX:
            return "Height (ft)";
        case BT747Constants.FMT_SPEED_IDX:
            return "Speed (km/h)";
        case PositionData.FMT_SPEED_MPH_IDX:
            return "Speed (mph)";
        case BT747Constants.FMT_HEADING_IDX:
            return "Heading";
        case BT747Constants.FMT_DSTA_IDX:
            return "DSTA";
        case BT747Constants.FMT_DAGE_IDX:
            return "DAGE";
        case BT747Constants.FMT_PDOP_IDX:
            return "PDOP";
        case BT747Constants.FMT_HDOP_IDX:
            return "HDOP";
        case BT747Constants.FMT_VDOP_IDX:
            return "VDOP";
        case BT747Constants.FMT_NSAT_IDX:
            return "NSAT";
        case PositionData.FMT_FIXMODE:
            return "Fix";
        case BT747Constants.FMT_MAX_SATS:
            return "Max Sats";
        case BT747Constants.FMT_SID_IDX:
            return "SID";
        case PositionData.FMT_VOX:
            return "VOX/File";
        case BT747Constants.FMT_RCR_IDX:
            return "RCR";
        case PositionData.FMT_RCR_DESCRIPTION:
            return "RCR Description";
        case BT747Constants.FMT_MILLISECOND_IDX:
            return "MS";
        case BT747Constants.FMT_DISTANCE_IDX:
            return "Distance (m)";
        case PositionData.FMT_DISTANCE_FT_IDX:
            return "Distance (ft)";
        }
        return null;
    }

    private void fireWaypointListUpdate() {
        postEvent(new ModelEvent(J2SEAppModel.UPDATE_WAYPOINT_LIST, null));
    }

    /**
     * 
     */
    private void fireUserWaypointUpdate() {
        postEvent(new ModelEvent(J2SEAppModel.UPDATE_USERWAYPOINT_LIST, null));
    }

    private final void fireTrackPointListChange() {
        postEvent(new ModelEvent(J2SEAppModel.UPDATE_TRACKPOINT_LIST, trks));
    }

    private final void postEvent(final ModelEvent e) {
        if (m != null) {
            m.postModelEvent(e);
        }
    }

    private WayPointListModel wpListModel;
    public AbstractListModel getWaypointListModel() {
        if(wpListModel==null) {
            wpListModel = new WayPointListModel();
        }
        return wpListModel;
    }
    
    @SuppressWarnings("serial")
    private class WayPointListModel extends AbstractListModel {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.ListModel#getElementAt(int)
         */
        public Object getElementAt(int index) {
            if (index < userWayPoints.size()) {
                return userWayPoints.get(index);
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.ListModel#getSize()
         */
        public int getSize() {
            return userWayPoints.size();
        }

    }

}
