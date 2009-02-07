/**
 * 
 */
package bt747.j2se_view.model;

import gps.log.GPSRecord;
import gps.log.out.CommonOut;

import java.awt.FontMetrics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;

import net.sf.bt747.j2se.app.filefilters.KnownFileFilter;
import net.sf.bt747.j2se.app.utils.GPSRecordTimeComparator;

import org.jdesktop.beans.AbstractBean;

import bt747.j2se_view.J2SEAppModel;
import bt747.model.Controller;
import bt747.model.ModelEvent;
import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
@SuppressWarnings("serial")
public class PositionData extends AbstractBean {
    public static final String WAYPOINTSELECTED = "selectedwaypoint";
    public final static String WPDISPLAYCHANGE = "wpdisplaychange";
    J2SEAppModel m;
    private List<List<GPSRecord>> trks = new Vector<List<GPSRecord>>();
    private final Vector<BT747Waypoint> wayPoints = new Vector<BT747Waypoint>();
    private final Vector<BT747Waypoint> userWayPoints = new Vector<BT747Waypoint>();

    // private GPSRecord[] wayPoints = null;
    // private GPSRecord[] userWayPoints = null;

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
        if (waypoints != null) {
            for (final GPSRecord wp : waypoints) {
                wayPoints.add(new BT747Waypoint(wp));
            }
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
            m.postEvent(e);
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

    private void fireLogFileUpdate() {
        postEvent(new ModelEvent(ModelEvent.UPDATE_LOG_FILE_LIST, null));
    }

    public final void addFiles(final File[] files) {
        if (files != null) {
            final FileFilter filter = new KnownFileFilter();
            for (int i = 0; i < files.length; i++) {
                try {
                    final File f = files[i];
                    if (f.exists()) {
                        final String path = files[i].getCanonicalPath();
                        if (filter.accept(f)) {
                            // Log file
                            Controller.addLogFile(path, -1);
                        } else {
                            userWpListModel.add(path);
                        }
                    } else {
                        System.err.println("File not found: "
                                + f.getCanonicalPath());
                    }

                } catch (final IOException e) {
                    // TODO: handle exception
                }
            }
            fireLogFileUpdate();
        }
    }

    private void fireWpDisplayChange() {
        firePropertyChange(WPDISPLAYCHANGE, null, Boolean.TRUE);
    }

    @SuppressWarnings("serial")
    public class UserWayPointListModel extends AbstractListModel implements
            PropertyChangeListener {

        private java.util.Hashtable<String, FileWaypoint> imageTable = new Hashtable<String, FileWaypoint>();

        public void add(final String path) {
            synchronized (imageTable) {
                if (!imageTable.contains(path)) {
                    final ImageData id = new ImageData();
                    if(id.setPath(path)) {
                    add(id);
                    } else {
                        final FileWaypoint fw = new FileWaypoint();
                        fw.setPath(path);
                        add(fw);
                    }
                }
            }
        }

        public void add(final FileWaypoint id) {
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

        public void remove(Object[] elements) {
            int count = imageTable.size()-1;
            for (Object element : elements) {
                if (element instanceof ImageData) {
                    final ImageData new_name = (ImageData) element;
                    imageTable.remove(new_name.getPath());
                }
                userWayPoints.remove(element);
            }
            // TODO: may need to be more complex
            userWpListModel.fireIntervalRemoved(this, 0, count);
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

    private void fireWpSelected(final BT747Waypoint w) {
        firePropertyChange(WAYPOINTSELECTED, null, w);
    }

    public static final Object getData(final BT747Waypoint w, final int type) {
        Object result;
        result = getValue(w.getGpsRecord(), type);
        if (result == null) {
            if (ImageData.class.isInstance(w)) {
                final ImageData img = (ImageData) w;
                switch (type) {
                case DataTypes.FILE_DATE:
                    return CommonOut.getDateStr(img.getUtc());
                case DataTypes.FILE_TIME:
                    return CommonOut.getTimeStr(img.getUtc());
                case DataTypes.FILE_DATETIME:
                    return CommonOut.getDateTimeStr(img.getUtc());
                case DataTypes.IMAGE_PATH:
                    return img.getPath();
                case DataTypes.IMAGE_WIDTH:
                    return Integer.valueOf(img.getWidth());
                case DataTypes.IMAGE_HEIGHT:
                    return Integer.valueOf(img.getHeight());
                case DataTypes.GEOMETRY:
                    if (img.getWidth() != 0) {
                        return img.getWidth() + "x" + img.getHeight();
                    }
                    break;
                }
            }
        }
        return result;
    }

    public static final Object getValue(final GPSRecord g, final int type) {
        switch (type) {
        case DataTypes.LOGTIME:
            break;
        case DataTypes.LOGDIST:
            break;
        case DataTypes.LOGSPD:
            break;
        case DataTypes.NS:
            break;
        case DataTypes.EW:
            break;
        case DataTypes.RECORDNUMBER:
            if (g.hasRecCount()) {
                return Integer.valueOf(g.recCount);
            }
            break;
        case DataTypes.UTC_VALUE:
            if (g.hasUtc()) {
                return Long.valueOf(g.utc);
            }
            break;
        case DataTypes.GPS_DATE:
            if (g.hasUtc()) {
                return CommonOut.getDateStr(g.utc);
            }
            break;
        case DataTypes.GPS_TIME:
            if (g.hasUtc()) {
                return CommonOut.getTimeStr(g.utc);
            }
            break;
        case DataTypes.GPS_DATETIME:
            if (g.hasUtc()) {
                return CommonOut.getDateTimeStr(g.utc);
            }
            break;

        case DataTypes.TAG_DATE:
            if (g.hasTagUtc()) {
                return CommonOut.getDateStr(g.tagutc);
            }
            break;
        case DataTypes.TAG_TIME:
            if (g.hasTagUtc()) {
                return CommonOut.getTimeStr(g.tagutc);
            }
            break;
        case DataTypes.TAG_DATETIME:
            if (g.hasTagUtc()) {
                return CommonOut.getDateTimeStr(g.tagutc);
            }
            break;
        case DataTypes.FIX_VALID:
            if (g.hasValid()) {
                return CommonOut.getFixText(g.valid);
            }
            break;
        case DataTypes.LATITUDE_POSITIVE:
            if (g.hasLatitude()) {
                if (g.latitude < 0.) {
                    return -g.latitude;
                } else {
                    return g.latitude;
                }
            }
            break;
        case DataTypes.LONGITUDE_POSITIVE:
            if (g.hasLongitude()) {
                if (g.longitude < 0.) {
                    return -g.longitude;
                } else {
                    return g.longitude;
                }
            }
            break;
        case DataTypes.LATITUDE:
            if (g.hasLatitude()) {
                return Double.valueOf(g.latitude);
            }
            break;
        case DataTypes.LONGITUDE:
            if (g.hasLongitude()) {
                return Double.valueOf(g.longitude);
            }
            break;
        case DataTypes.HEIGHT_METERS:
            if (g.hasHeight()) {
                return Float.valueOf(g.height);
            }
            break;
        case DataTypes.HEIGHT_FEET:
            break;
        case DataTypes.SPEED:
            if (g.hasSpeed()) {
                return Float.valueOf(g.speed);
            }
            break;
        case DataTypes.SPEED_MPH:
            break;
        case DataTypes.HEADING:
            if (g.hasHeading()) {
                return Float.valueOf(g.heading);
            }
            break;
        case DataTypes.DSTA:
            if (g.hasDsta()) {
                return Integer.valueOf(g.dsta);
            }
            break;
        case DataTypes.DAGE:
            if (g.hasDage()) {
                return Integer.valueOf(g.dage);
            }
            break;
        case DataTypes.PDOP:
            if (g.hasPdop()) {
                return Float.valueOf(g.pdop / 100.0f);
            }
            break;
        case DataTypes.HDOP:
            if (g.hasHdop()) {
                return Float.valueOf(g.hdop / 100.0f);
            }
            break;
        case DataTypes.VDOP:
            if (g.hasVdop()) {
                return Float.valueOf(g.vdop / 100.0f);
            }
            break;
        case DataTypes.NSAT:
            if (g.hasNsat()) {
                return Integer.valueOf(g.nsat);
            }
            break;
        case DataTypes.FIXMODE:
            if (g.hasValid()) {
                return CommonOut.getFixText(g.valid);
            }
            break;
        case DataTypes.SID:

            break;
        case DataTypes.VOX:
            if (g.hasVoxStr()) {
                return g.voxStr;
            }
            break;
        case DataTypes.RCR:
            if (g.hasRcr()) {
                return CommonOut.getRCRstr(g);
            }
            break;
        case DataTypes.RCR_DESCRIPTION:
            if (g.hasRcr()) {
                return CommonOut.getRcrSymbolText(g);
            }
            break;
        case DataTypes.MILLISECOND:
            if (g.hasMillisecond()) {
                return Integer.valueOf(g.milisecond);
            }
            break;
        case DataTypes.DISTANCE:
            if (g.hasDistance()) {
                return Double.valueOf(g.distance);
            }
            break;
        case DataTypes.DISTANCE_FEET:
            break;
        // Image specific
        }
        return null; // Default;
    }

    public static final boolean setValue(final Object value,
            final GPSRecord g, final int type) {
        try {
            switch (type) {
            case DataTypes.LOGTIME:
                break;
            case DataTypes.LOGDIST:
                break;
            case DataTypes.LOGSPD:
                break;
            case DataTypes.NS:
                break;
            case DataTypes.EW:
                break;
            case DataTypes.RECORDNUMBER:
                g.recCount = (Integer) value;
                break;
            case DataTypes.GPS_DATE:
                break;
            case DataTypes.GPS_TIME:
                break;
            case DataTypes.FIX_VALID:
                break;
            case DataTypes.LATITUDE_POSITIVE:
                break;
            case DataTypes.LONGITUDE_POSITIVE:
                break;
            case DataTypes.LATITUDE:
                g.latitude = (Double) value;
                break;
            case DataTypes.LONGITUDE:
                g.longitude = g.longitude;
                break;
            case DataTypes.HEIGHT_METERS:
                break;
            case DataTypes.HEIGHT_FEET:
                break;
            case DataTypes.SPEED:
                break;
            case DataTypes.SPEED_MPH:
                break;
            case DataTypes.HEADING:
                break;
            case DataTypes.DSTA:
                break;
            case DataTypes.DAGE:
                break;
            case DataTypes.PDOP:
                break;
            case DataTypes.HDOP:
                break;
            case DataTypes.VDOP:
                break;
            case DataTypes.NSAT:
                break;
            case DataTypes.FIXMODE:
                break;
            case DataTypes.SID:
                break;
            case DataTypes.VOX:
                break;
            case DataTypes.RCR:
                break;
            case DataTypes.RCR_DESCRIPTION:
                break;
            case DataTypes.MILLISECOND:
                break;
            case DataTypes.DISTANCE:
                break;
            case DataTypes.DISTANCE_FEET:
                break;
            }
            return true;
        } catch (final Exception e) {
            Generic.debug("setValue " + type + " " + value, e);
        }
        return false;
    }
}
