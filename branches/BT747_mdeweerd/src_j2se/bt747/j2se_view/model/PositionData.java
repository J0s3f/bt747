/**
 * 
 */
package bt747.j2se_view.model;

import gps.BT747Constants;
import gps.log.GPSRecord;
import gps.log.out.CommonOut;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;

import net.sf.bt747.j2se.app.utils.GPSRecordTimeComparator;

import org.jdesktop.beans.AbstractBean;

import bt747.j2se_view.J2SEAppModel;
import bt747.model.ModelEvent;

/**
 * @author Mario
 * 
 */
@SuppressWarnings("serial")
public class PositionData extends AbstractBean {
    J2SEAppModel m;
    private List<List<GPSRecord>> trks = new Vector<List<GPSRecord>>();
    private final Vector<BT747Waypoint> wayPoints = new Vector<BT747Waypoint>();
    private final Vector<BT747Waypoint> userWayPoints = new Vector<BT747Waypoint>();
    // private GPSRecord[] wayPoints = null;
    // private GPSRecord[] userWayPoints = null;

    public static final int NONE = 0;
    public static final int PATH = 1;
    public static final int WIDTH = 2;
    public static final int HEIGHT = 3;
    public static final int GEOMETRY = 4;
    public static final int LATITUDE = 5;
    public static final int LONGITUDE = 6;
    public static final int DATETIME = 7;
    public static final int DATE = 8;
    public static final int TIME = 9;

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

    public final GPSRecord[] getUserWayPointsGPSRecords() {
        GPSRecord[] r = new GPSRecord[userWayPoints.size()];
        int index = 0;
        for (BT747Waypoint w : userWayPoints) {
            r[index++] = w.getGpsRecord();
        }
        return r;
    }

    public final List<BT747Waypoint> getUserWayPoints() {
        return userWayPoints;
    }

    public void userWaypointsUpdated() {
        fireUserWaypointUpdate();
    }
    
    public void dataUpdated() {
        fireTrackPointListChange();
        fireWaypointListUpdate();
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
        userWpListModel.fireContentsChanged(userWpListModel, 0,
                userWpListModel.getSize() - 1);
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

    private UserWayPointListModel userWpListModel = new UserWayPointListModel();;

    public UserWayPointListModel getWaypointListModel() {
        return userWpListModel;
    }

    public GPSRecord[] getSortedGPSRecords() {
        GPSRecord[] rcrds;
        rcrds = new GPSRecord[userWayPoints.size()];
        int i = 0;
        for (BT747Waypoint w : userWayPoints) {
            GPSRecord r = w.getGpsRecord();
            if (r != null) {
                rcrds[i++] = r;
            } else {
                r = GPSRecord.getLogFormatRecord(0);
                bt747.sys.Generic.debug("Null GPS Record found");
            }
        }
        java.util.Arrays.sort(rcrds, new GPSRecordTimeComparator());
        return rcrds;
    }

    public final void addFiles(final File[] files) {
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                try {
                    userWpListModel.add(files[i].getCanonicalPath());
                } catch (IOException e) {
                    // TODO: handle exception
                }
            }
        }
    }

    public final static String WPDISPLAYCHANGE = "wpdisplaychange";

    private void fireWpDisplayChange() {
        firePropertyChange(WPDISPLAYCHANGE, null, Boolean.TRUE);
    }

    @SuppressWarnings("serial")
    public class UserWayPointListModel extends AbstractListModel implements
            PropertyChangeListener {

        private java.util.Hashtable<String, ImageData> imageTable = new Hashtable<String, ImageData>();

        public void add(final String path) {
            synchronized (imageTable) {
                if (!imageTable.contains(path)) {
                    ImageData id = new ImageData();
                    id.setPath(path);
                    add(id);
                }
            }
        }

        public void add(ImageData id) {
            synchronized (imageTable) {
                imageTable.put(id.getPath(), id);
                add((BT747Waypoint) id);
            }
        }

        public void add(BT747Waypoint wp) {
            synchronized (userWayPoints) {
                userWayPoints.add(wp);
                int row = userWayPoints.size() - 1;
                wp.addPropertyChangeListener(this);
                fireIntervalAdded(this, row, row);
            }
        }

        public void clear() {
            int org;
            org = userWayPoints.size();
            userWayPoints.removeAllElements();
            imageTable.clear();
            userWpListModel.fireIntervalRemoved(userWpListModel, 0, org - 1);
        }

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

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.AbstractListModel#fireContentsChanged(java.lang.Object,
         *      int, int)
         */
        @Override
        protected void fireContentsChanged(Object source, int index0,
                int index1) {

            super.fireContentsChanged(source, index0, index1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.AbstractListModel#fireIntervalAdded(java.lang.Object,
         *      int, int)
         */
        @Override
        protected void fireIntervalAdded(Object source, int index0, int index1) {
            super.fireIntervalAdded(source, index0, index1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.AbstractListModel#fireIntervalRemoved(java.lang.Object,
         *      int, int)
         */
        @Override
        protected void fireIntervalRemoved(Object source, int index0,
                int index1) {
            // TODO Auto-generated method stub
            super.fireIntervalRemoved(source, index0, index1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(BT747Waypoint.PROPERTY_SELECTED)) {
                fireWpDisplayChange();
                try {
                    if ((Boolean) evt.getNewValue()) {
                        fireWpSelected((BT747Waypoint) evt.getSource());
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            } else if (evt.getPropertyName().equals(
                    BT747Waypoint.PROPERTY_SHOWTAG)) {
                fireWpDisplayChange();
            }
        }

    }

    public static final String WAYPOINTSELECTED = "selectedwaypoint";

    private void fireWpSelected(BT747Waypoint w) {
        firePropertyChange(WAYPOINTSELECTED, null, w);
    }

    public static Object getData(final ImageData img, final int dataType) {
        switch (dataType) {
        case NONE:
            return null;
        case PATH:
            return img.getPath();
        case WIDTH:
            return Integer.valueOf(img.getWidth());
        case HEIGHT:
            return Integer.valueOf(img.getHeight());
        case GEOMETRY:
            if (img.getWidth() != 0) {
                return img.getWidth() + "x" + img.getHeight();
            } else {
                return null;
            }
        case LATITUDE:
            if (img.getGpsRecord().hasLatitude()) {
                return Double.valueOf(img.getGpsRecord().latitude);
            } else {
                return null;
            }
        case LONGITUDE:
            if (img.getGpsRecord().hasLongitude()) {
                return Double.valueOf(img.getGpsRecord().longitude);
            } else {
                return null;
            }
        case DATETIME:
            if (img.getGpsRecord().hasUtc()) {
                return CommonOut.getDateTimeStr(img.getGpsRecord().utc);
            } else {
                return null;
            }
        case DATE:
            if (img.getGpsRecord().hasUtc()) {
                return CommonOut.getDateStr(img.getGpsRecord().utc);
            } else {
                return null;
            }
        case TIME:
            if (img.getGpsRecord().hasUtc()) {
                return CommonOut.getTimeStr(img.getGpsRecord().utc);
            } else {
                return null;
            }
        default:
            return null;
        }

    }

    public static final Class<?> getDataDisplayClass(int datatype) {
        switch (datatype) {
        case NONE:
            return null;
        case PATH:
            return String.class;
        case WIDTH:
            return Integer.class;
        case HEIGHT:
            return Integer.class;
        case GEOMETRY:
            return String.class;
        case LATITUDE:
            return Object.class;// return Double.class;
        case LONGITUDE:
            return Object.class;// return Double.class;
        case DATETIME:
        case DATE:
        case TIME:
            return String.class;
        default:
            return null;
        }
    }

    public static final String getDataDisplayName(int datatype) {
        switch (datatype) {
        case NONE:
            return "None";
        case PATH:
            return "Image path";
        case WIDTH:
            return "Width";
        case HEIGHT:
            return "Height";
        case GEOMETRY:
            return "Geometry";
        case LATITUDE:
            return "Latitude";
        case LONGITUDE:
            return "Longitude";
        case DATETIME:
            return "Date/Time";
        case DATE:
            return "Date";
        case TIME:
            return "Time";
        default:
            return null;
        }
    }

}
