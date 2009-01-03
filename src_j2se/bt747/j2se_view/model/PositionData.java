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

    public static final int NONE = 0;
    public static final int FIX_VALID = 1;
    public static final int LATITUDE = 2;
    public static final int LONGITUDE = 3;
    public static final int POSITION_HEIGHT = 4;
    public static final int SPEED = 5;
    public static final int HEADING = 6;
    public static final int DSTA = 7;
    public static final int DAGE = 8;
    public static final int PDOP = 9;
    public static final int HDOP = 10;
    public static final int VDOP = 11;
    public static final int NSAT = 12;
    public static final int SID = 13;
    public static final int ELEVATION = 14;
    public static final int AZIMUTH = 15;
    public static final int SNR = 16;
    public static final int RCR = 17;
    public static final int MILLISECOND = 18;
    public static final int DISTANCE = 19;
    public static final int LOGDIST = 20;
    public static final int LOGSPD = 21;
    public static final int LOGTIME = 22;
    public static final int DISTANCE_FEET = 23;
    public static final int EW = 24;
    public static final int FMT_FIXMODE = 25;
    public static final int FMT_HEIGHT_FT_IDX = 26;
    public static final int LATITUDE_POSITIVE = 27;
    public static final int LONGITUDE_POSITIVE = 28;
    public static final int NS = 29;
    public static final int RCR_DESCRIPTION = 30;
    public static final int RECORDNUMBER = 31;
    public static final int SPEED_MPH = 32;
    public static final int UTC_VALUE = 33;
    public static final int VOX = 34;
    public static final int IMAGE_PATH = 35;
    public static final int IMAGE_WIDTH = 36;
    public static final int IMAGE_HEIGHT = 37;
    public static final int GEOMETRY = 38;
    public static final int DATETIME = 39;
    public static final int DATE = 40;
    public static final int TIME = 41;
    public static final int UTC_TIME = 42;

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
        case LOGTIME:
            return Float.class;
        case LOGDIST:
            return Float.class;
        case LOGSPD:
            return Float.class;
        case NS:
            return String.class;
        case EW:
            return String.class;
        case RECORDNUMBER:
            return Integer.class;
        case UTC_TIME:
            return String.class;
        case UTC_VALUE:
            return Long.class;
        case FIX_VALID:
            return String.class;
        case LATITUDE_POSITIVE:
            return String.class;
        case LONGITUDE_POSITIVE:
            return String.class;
        case POSITION_HEIGHT:
            break;
        case FMT_HEIGHT_FT_IDX:
            break;
        case SPEED:
            return Float.class;
        case SPEED_MPH:
            return Float.class;
        case HEADING:
            return Float.class;
        case DSTA:
            return Integer.class;
        case DAGE:
            return Integer.class;
        case PDOP:
            return Float.class;
        case HDOP:
            return Float.class;
        case VDOP:
            return Float.class;
        case NSAT:
            return Integer.class;
        case FMT_FIXMODE:
            return String.class;
        case SID:
            break;
        case VOX:
            return String.class;
        case RCR:
            return String.class;
        case RCR_DESCRIPTION:
            return String.class;
        case MILLISECOND:
            return Integer.class;
        case DISTANCE:
            return Float.class;
        case DISTANCE_FEET:
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
        case LOGTIME:
            return "Log time";
        case LOGDIST:
            return "Log Dist";
        case LOGSPD:
            return "Log speed";
        case NS:
            return "N/S";
        case EW:
            return "E/W";
        case RECORDNUMBER:
            return "Record nbr";
        case UTC_TIME:
            return "Date & Time";
        case UTC_VALUE:
            return "UTC Value";
        case FIX_VALID:
            return "Valid";
        case LATITUDE_POSITIVE:
            break;
        case LONGITUDE_POSITIVE:
            break;
        case POSITION_HEIGHT:
            return "Height (m)";
        case FMT_HEIGHT_FT_IDX:
            return "Height (ft)";
        case SPEED:
            return "Speed (km/h)";
        case SPEED_MPH:
            return "Speed (mph)";
        case HEADING:
            return "Heading";
        case DSTA:
            return "DSTA";
        case DAGE:
            return "DAGE";
        case PDOP:
            return "PDOP";
        case HDOP:
            return "HDOP";
        case VDOP:
            return "VDOP";
        case NSAT:
            return "NSAT";
        case FMT_FIXMODE:
            return "Fix";
        case SID:
            return "SID";
        case VOX:
            return "VOX/File";
        case RCR:
            return "RCR";
        case RCR_DESCRIPTION:
            return "RCR Description";
        case MILLISECOND:
            return "MS";
        case DISTANCE:
            return "Distance (m)";
        case DISTANCE_FEET:
            return "Distance (ft)";
        default:
            return null;
        }
        return null;
    }

    public static final Object getValue(final GPSRecord g, final int type) {
        switch (type) {
        case LOGTIME:
            break;
        case LOGDIST:
            break;
        case LOGSPD:
            break;
        case NS:
            break;
        case EW:
            break;
        case RECORDNUMBER:
            return Integer.valueOf(g.recCount);
        case UTC_TIME:
            return CommonOut.getDateTimeStr(g.utc);
        case UTC_VALUE:
            return Long.valueOf(g.utc);
        case DATE:
            return CommonOut.getDateStr(g.utc);
        case TIME:
            return CommonOut.getTimeStr(g.utc);
        case FIX_VALID:
            return CommonOut.getFixText(g.valid);
        case LATITUDE_POSITIVE:
            break;
        case LONGITUDE_POSITIVE:
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
        case SPEED_MPH:
            break;
        case HEADING:
            return new Float(g.heading);
        case DSTA:
            return new Integer(g.dsta);
        case DAGE:
            return new Integer(g.dage);
        case PDOP:
            return new Float(g.pdop / 100.0f);
        case HDOP:
            return new Float(g.hdop / 100.0f);
        case VDOP:
            return new Float(g.vdop / 100.0f);
        case NSAT:
            return new Integer(g.nsat);
        case FMT_FIXMODE:
            return CommonOut.getFixText(g.valid);
        case SID:
            break;
        case VOX:
            return g.voxStr;
        case RCR:
            return CommonOut.getRCRstr(g);
        case RCR_DESCRIPTION:
            return CommonOut.getRcrSymbolText(g);
        case MILLISECOND:
            return new Integer(g.milisecond);
        case DISTANCE:
            return new Double(g.distance);
        case DISTANCE_FEET:
            break;
        }
        return null; // Default;
    }

    public static final boolean setValue(final Object value,
            final GPSRecord g, final int type) {
        try {
            switch (type) {
            case LOGTIME:
                break;
            case LOGDIST:
                break;
            case LOGSPD:
                break;
            case NS:
                break;
            case EW:
                break;
            case RECORDNUMBER:
                g.recCount = (Integer) value;
                break;
            case DATE:
                break;
            case TIME:
                break;
            case FIX_VALID:
                break;
            case LATITUDE_POSITIVE:
                break;
            case LONGITUDE_POSITIVE:
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
            case SPEED_MPH:
                break;
            case HEADING:
                break;
            case DSTA:
                break;
            case DAGE:
                break;
            case PDOP:
                break;
            case HDOP:
                break;
            case VDOP:
                break;
            case NSAT:
                break;
            case FMT_FIXMODE:
                break;
            case SID:
                break;
            case VOX:
                break;
            case RCR:
                break;
            case RCR_DESCRIPTION:
                break;
            case MILLISECOND:
                break;
            case DISTANCE:
                break;
            case DISTANCE_FEET:
                break;
            }
            return true;
        } catch (final Exception e) {
            Generic.debug("setValue " + type + " " + value, e);
        }
        return false;
    }

}