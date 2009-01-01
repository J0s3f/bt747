/**
 * 
 */
package bt747.j2se_view.model;

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
import bt747.sys.Generic;

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

    public static final int NONE = 200;
    public static final int IMAGE_PATH = 201;
    public static final int IMAGE_WIDTH = 202;
    public static final int IMAGE_HEIGHT = 203;
    public static final int GEOMETRY = 204;
//    public static final int LATITUDE = 205;
//    public static final int LONGITUDE = 206;
    public static final int DATETIME = 207;
    public static final int DATE = 208;
    public static final int TIME = 209;

    

    public static final int UTC_TIME = 0;

    public static final int FIX_VALID = 1;

    public static final int LATITUDE = 2;

    public static final int LONGITUDE = 3;
    public static final int POSITION_HEIGHT = 4;
    public static final int SPEED = 5;
    public static final int HEADING = 6;
    public static final int DSTA = 7;
    /** Index of bit for log format setting */
    public static final int DAGE = 8;
    /** Index of bit for log format setting */
    public static final int FMT_PDOP_IDX = 9;
    /** Index of bit for log format setting */
    public static final int FMT_HDOP_IDX = 10;
    /** Index of bit for log format setting */
    public static final int FMT_VDOP_IDX = 11;
    /** Index of bit for log format setting */
    public static final int FMT_NSAT_IDX = 12;
    public static final int FMT_MAX_SATS = 32; // Guess (maximum)
    /** Index of bit for log format setting */
    public static final int FMT_SID_IDX = 13;
    /** Index of bit for log format setting */
    public static final int FMT_ELEVATION_IDX = 14;
    /** Index of bit for log format setting */
    public static final int FMT_AZIMUTH_IDX = 15;
    /** Index of bit for log format setting */
    public static final int FMT_SNR_IDX = 16;
    /** Index of bit for log format setting */
    public static final int FMT_RCR_IDX = 17;
    /** Index of bit for log format setting */
    public static final int FMT_MILLISECOND_IDX = 18;
    /** Index of bit for log format setting */
    public static final int FMT_DISTANCE_IDX = 19;
    /** Index of bit for log format setting */
    public static final int FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX = 31;

    protected static final int C_LOGDIST = -8;
    protected static final int C_LOGSPD = -7;
    protected static final int C_LOGTIME = -9;
    protected static final int FMT_DATE = -3;
    protected static final int FMT_DISTANCE_FT_IDX = FMT_DISTANCE_IDX + 100;
    protected static final int FMT_EW = -5;
    protected static final int FMT_FIXMODE = -13;
    protected static final int FMT_HEIGHT_FT_IDX = POSITION_HEIGHT + 100;
    protected static final int FMT_LATNS = -10;
    protected static final int FMT_LONEW = -11;
    protected static final int FMT_NO_FIELD = -1;
    protected static final int FMT_NS = -6;
    protected static final int FMT_RCR_DESCRIPTION = -14;
    protected static final int FMT_REC_NBR = -4;
    protected static final int FMT_SPEED_MPH_IDX = SPEED + 100;
    protected static final int FMT_TIME = -2;

    protected static final int FMT_UTC_VALUE = -15;
    protected static final int FMT_VOX = -12;

    /**
     * 
     */
    public PositionData() {
        // TODO Auto-generated constructor stub
    }

    public PositionData(final J2SEAppModel m) {
        this.m = m;
    }

    public final List<List<GPSRecord>> getTracks() {
        return trks;
    }

    public final void setTracks(final List<List<GPSRecord>> trks) {
        this.trks = trks;
        fireTrackPointListChange();
    }

    public final GPSRecord[] getWayPoints() {
        final GPSRecord[] r = new GPSRecord[wayPoints.size()];
        int index = 0;
        for (final BT747Waypoint w : wayPoints) {
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

    public final void setWayPoints(final GPSRecord[] waypoints) {
        wayPoints.removeAllElements();

        for (final GPSRecord wp : waypoints) {
            wayPoints.add(new BT747Waypoint(wp));
        }
        fireWaypointListUpdate();
    }

    public final GPSRecord[] getUserWayPointsGPSRecords() {
        final GPSRecord[] r = new GPSRecord[userWayPoints.size()];
        int index = 0;
        for (final BT747Waypoint w : userWayPoints) {
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
        for (final BT747Waypoint w : userWayPoints) {
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
                } catch (final IOException e) {
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
                    final ImageData id = new ImageData();
                    id.setPath(path);
                    add(id);
                }
            }
        }

        public void add(final ImageData id) {
            synchronized (imageTable) {
                imageTable.put(id.getPath(), id);
                add((BT747Waypoint) id);
            }
        }

        public void add(final BT747Waypoint wp) {
            synchronized (userWayPoints) {
                userWayPoints.add(wp);
                final int row = userWayPoints.size() - 1;
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
        public Object getElementAt(final int index) {
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
        protected void fireContentsChanged(final Object source,
                final int index0, final int index1) {

            super.fireContentsChanged(source, index0, index1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.AbstractListModel#fireIntervalAdded(java.lang.Object,
         *      int, int)
         */
        @Override
        protected void fireIntervalAdded(final Object source,
                final int index0, final int index1) {
            super.fireIntervalAdded(source, index0, index1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.AbstractListModel#fireIntervalRemoved(java.lang.Object,
         *      int, int)
         */
        @Override
        protected void fireIntervalRemoved(final Object source,
                final int index0, final int index1) {
            // TODO Auto-generated method stub
            super.fireIntervalRemoved(source, index0, index1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(BT747Waypoint.PROPERTY_SELECTED)) {
                fireWpDisplayChange();
                try {
                    if ((Boolean) evt.getNewValue()) {
                        fireWpSelected((BT747Waypoint) evt.getSource());
                    }
                } catch (final Exception e) {
                    // TODO: handle exception
                }
            } else if (evt.getPropertyName().equals(
                    BT747Waypoint.PROPERTY_SHOWTAG)) {
                fireWpDisplayChange();
            }
        }

    }

    public static final String WAYPOINTSELECTED = "selectedwaypoint";

    private void fireWpSelected(final BT747Waypoint w) {
        firePropertyChange(WAYPOINTSELECTED, null, w);
    }

    public static Object getData(final ImageData img, final int dataType) {
        switch (dataType) {
        case NONE:
            return null;
        case IMAGE_PATH:
            return img.getPath();
        case IMAGE_WIDTH:
            return Integer.valueOf(img.getWidth());
        case IMAGE_HEIGHT:
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

    public static final Class<?> getDataDisplayClass(final int datatype) {
        switch (datatype) {
        case NONE:
            return null;
        case IMAGE_PATH:
            return String.class;
        case IMAGE_WIDTH:
            return Integer.class;
        case IMAGE_HEIGHT:
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
        case C_LOGTIME:
            return Float.class;
        case C_LOGDIST:
            return Float.class;
        case C_LOGSPD:
            return Float.class;
        case FMT_NS:
            return String.class;
        case FMT_EW:
            return String.class;
        case FMT_REC_NBR:
            return Integer.class;
        case UTC_TIME:
            return String.class;
        case FMT_UTC_VALUE:
            return Long.class;
        case FMT_DATE:
            return String.class;
        case FMT_TIME:
            return String.class;
        case FIX_VALID:
            return String.class;
        case FMT_LATNS:
            return String.class;
        case FMT_LONEW:
            return String.class;
        case POSITION_HEIGHT:
            break;
        case FMT_HEIGHT_FT_IDX:
            break;
        case SPEED:
            return Float.class;
        case FMT_SPEED_MPH_IDX:
            return Float.class;
        case HEADING:
            return Float.class;
        case DSTA:
            return Integer.class;
        case DAGE:
            return Integer.class;
        case FMT_PDOP_IDX:
            return Float.class;
        case FMT_HDOP_IDX:
            return Float.class;
        case FMT_VDOP_IDX:
            return Float.class;
        case FMT_NSAT_IDX:
            return Integer.class;
        case FMT_FIXMODE:
            return String.class;
        case FMT_MAX_SATS:
            return Integer.class;
        case FMT_SID_IDX:
            break;
        case FMT_VOX:
            return String.class;
        case FMT_RCR_IDX:
            return String.class;
        case FMT_RCR_DESCRIPTION:
            return String.class;
        case FMT_MILLISECOND_IDX:
            return Integer.class;
        case FMT_DISTANCE_IDX:
            return Float.class;
        case FMT_DISTANCE_FT_IDX:
            return Float.class;
        default:
            return Object.class;
        }
        return Object.class;
    }

    public static final String getDataDisplayName(final int datatype) {
        switch (datatype) {
        case NONE:
            return "None";
        case IMAGE_PATH:
            return "Image path";
        case IMAGE_WIDTH:
            return "Width";
        case IMAGE_HEIGHT:
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
        case C_LOGTIME:
            break;
        case C_LOGDIST:
            break;
        case C_LOGSPD:
            break;
        case FMT_NS:
            break;
        case FMT_EW:
            break;
        case FMT_REC_NBR:
            return "Record nbr";
        case UTC_TIME:
            return "Date & Time";
        case FMT_UTC_VALUE:
            return "UTC Value";
        case FMT_DATE:
            return "Date";
        case FMT_TIME:
            return "Time";
        case FIX_VALID:
            return "Valid";
        case FMT_LATNS:
            break;
        case FMT_LONEW:
            break;
        case POSITION_HEIGHT:
            return "Height (m)";
        case FMT_HEIGHT_FT_IDX:
            return "Height (ft)";
        case SPEED:
            return "Speed (km/h)";
        case FMT_SPEED_MPH_IDX:
            return "Speed (mph)";
        case HEADING:
            return "Heading";
        case DSTA:
            return "DSTA";
        case DAGE:
            return "DAGE";
        case FMT_PDOP_IDX:
            return "PDOP";
        case FMT_HDOP_IDX:
            return "HDOP";
        case FMT_VDOP_IDX:
            return "VDOP";
        case FMT_NSAT_IDX:
            return "NSAT";
        case FMT_FIXMODE:
            return "Fix";
        case FMT_MAX_SATS:
            return "Max Sats";
        case FMT_SID_IDX:
            return "SID";
        case FMT_VOX:
            return "VOX/File";
        case FMT_RCR_IDX:
            return "RCR";
        case FMT_RCR_DESCRIPTION:
            return "RCR Description";
        case FMT_MILLISECOND_IDX:
            return "MS";
        case FMT_DISTANCE_IDX:
            return "Distance (m)";
        case FMT_DISTANCE_FT_IDX:
            return "Distance (ft)";
        default:
            return null;
        }
        return null;
    }

    public static final Object getValue(final GPSRecord g, final int type) {
        switch (type) {
        case C_LOGTIME:
            break;
        case C_LOGDIST:
            break;
        case C_LOGSPD:
            break;
        case FMT_NS:
            break;
        case FMT_EW:
            break;
        case FMT_REC_NBR:
            return Integer.valueOf(g.recCount);
        case UTC_TIME:
            return CommonOut.getDateTimeStr(g.utc);
        case FMT_UTC_VALUE:
            return Long.valueOf(g.utc);
        case FMT_DATE:
            return CommonOut.getDateStr(g.utc);
        case FMT_TIME:
            return CommonOut.getTimeStr(g.utc);
        case FIX_VALID:
            return CommonOut.getFixText(g.valid);
        case FMT_LATNS:
            break;
        case FMT_LONEW:
            break;
        case LATITUDE:
            return new Double(g.latitude);
        case LONGITUDE:
            return new Double(g.longitude);
        case POSITION_HEIGHT:
            return new Float(g.height);
        case FMT_HEIGHT_FT_IDX:
            break;
        case SPEED:
            return new Float(g.speed);
        case FMT_SPEED_MPH_IDX:
            break;
        case HEADING:
            return new Float(g.heading);
        case DSTA:
            return new Integer(g.dsta);
        case DAGE:
            return new Integer(g.dage);
        case FMT_PDOP_IDX:
            return new Float(g.pdop / 100.0f);
        case FMT_HDOP_IDX:
            return new Float(g.hdop / 100.0f);
        case FMT_VDOP_IDX:
            return new Float(g.vdop / 100.0f);
        case FMT_NSAT_IDX:
            return new Integer(g.nsat);
        case FMT_FIXMODE:
            return CommonOut.getFixText(g.valid);
        case FMT_MAX_SATS:
            break;
        case FMT_SID_IDX:
            break;
        case FMT_VOX:
            return g.voxStr;
        case FMT_RCR_IDX:
            return CommonOut.getRCRstr(g);
        case FMT_RCR_DESCRIPTION:
            return CommonOut.getRcrSymbolText(g);
        case FMT_MILLISECOND_IDX:
            return new Integer(g.milisecond);
        case FMT_DISTANCE_IDX:
            return new Double(g.distance);
        case FMT_DISTANCE_FT_IDX:
            break;
        }
        return null; // Default;
    }

    public static final boolean setValue(final Object value,
            final GPSRecord g, final int type) {
        try {
            switch (type) {
            case C_LOGTIME:
                break;
            case C_LOGDIST:
                break;
            case C_LOGSPD:
                break;
            case FMT_NS:
                break;
            case FMT_EW:
                break;
            case FMT_REC_NBR:
                g.recCount = (Integer) value;
                break;
            case FMT_DATE:
                break;
            case FMT_TIME:
                break;
            case FIX_VALID:
                break;
            case FMT_LATNS:
                break;
            case FMT_LONEW:
                break;
            case LATITUDE:
                g.latitude = (Double) value;
                break;
            case LONGITUDE:
                g.longitude = g.longitude;
                break;
            case POSITION_HEIGHT:
                break;
            case FMT_HEIGHT_FT_IDX:
                break;
            case SPEED:
                break;
            case FMT_SPEED_MPH_IDX:
                break;
            case HEADING:
                break;
            case DSTA:
                break;
            case DAGE:
                break;
            case FMT_PDOP_IDX:
                break;
            case FMT_HDOP_IDX:
                break;
            case FMT_VDOP_IDX:
                break;
            case FMT_NSAT_IDX:
                break;
            case FMT_FIXMODE:
                break;
            case FMT_MAX_SATS:
                break;
            case FMT_SID_IDX:
                break;
            case FMT_VOX:
                break;
            case FMT_RCR_IDX:
                break;
            case FMT_RCR_DESCRIPTION:
                break;
            case FMT_MILLISECOND_IDX:
                break;
            case FMT_DISTANCE_IDX:
                break;
            case FMT_DISTANCE_FT_IDX:
                break;
            case FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX:
                break;
            }
            return true;
        } catch (final Exception e) {
            Generic.debug("setValue " + type + " " + value, e);
        }
        return false;
    }

}
