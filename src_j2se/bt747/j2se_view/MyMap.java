/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/*
 * MapReference.java
 * 
 * Created on 23 december 2008, 21:59:04
 */

package bt747.j2se_view;

import gps.BT747Constants;
import gps.convert.Conv;
import gps.log.GPSRecord;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;

import net.sf.bt747.j2se.app.list.BT747WaypointListCellRenderer;
import net.sf.bt747.j2se.app.map.BT747TrackRenderer;
import net.sf.bt747.j2se.app.map.MapFactoryInfos;
import net.sf.bt747.j2se.app.map.MapRendererFactoryMethod;
import net.sf.bt747.j2se.app.map.MyTileFactoryInfo;
import net.sf.bt747.j2se.app.utils.BareBonesBrowserLaunch;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.hyperlink.LinkAction;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.painter.CompoundPainter;

import bt747.j2se_view.model.BT747Waypoint;
import bt747.j2se_view.model.GPSPositionWaypoint;
import bt747.j2se_view.model.PositionData;
import bt747.model.AppSettings;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Generic;

/**
 * 
 * @author Mario
 */
@SuppressWarnings("serial")
public class MyMap extends javax.swing.JPanel implements ModelListener {

    private JXMapViewer mapViewer;
    private MyWaypointPainter<JXMapViewer> waypointPainter;
    private DefaultTileFactory tf = null;
    private BT747TrackRenderer trackRenderer;

    /** Creates new form MapReference */
    public MyMap() {
        initComponents();
    }

    private J2SEAppController c;
    private J2SEAppModel m;

    public void init(final J2SEAppController pC) {
        c = pC;
        m = c.getAppModel();
        m.addListener(this);

        initGui();
        updateMap();
        map.setZoom(tf.getInfo().getMaximumZoomLevel() - 4);
        map.setAddressLocation(new GeoPosition(51.5, 0));

        if (false) { // set to TRUE to get messages from map loader.
            LogManager lm = LogManager.getLogManager();
            java.util.Enumeration<String> iter = lm.getLoggerNames();
            while (iter.hasMoreElements()) {
                Logger l = LogManager.getLogManager().getLogger(
                        iter.nextElement());
                l.setLevel(Level.ALL);
            }
        }

        setMapTileCacheDirectory();
    }

    mouseListener ml;

    private void initGui() {
        map.setMiniMapVisible(true);
        // wayPointScrollPane
        // .setVisible(Version.VERSION_NUMBER.equals("d.evel"));
        wayPointScrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // map.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
        mapViewer = map.getMainMap();
        waypointPainter = new MyWaypointPainter<JXMapViewer>();
        trackRenderer = BT747TrackRenderer.getInstance();
        waypointPainter.setTrackRenderer(trackRenderer);
        waypointPainter.setRenderer(MapRendererFactoryMethod.getInstance());
        mapViewer.setRecenterOnClickEnabled(true);
        mapViewer.getOverlayPainter();
        final CompoundPainter<Object> cp = new CompoundPainter<Object>();
        cp.setPainters(mapViewer.getOverlayPainter(), waypointPainter);
        mapViewer.setOverlayPainter(cp);

        ml = new mouseListener();
        mapViewer.addMouseListener(ml);
        mapViewer.addMouseMotionListener(ml);
        waypointList.setModel(m.getPositionData().getWaypointListModel());
        waypointList.setCellRenderer(new BT747WaypointListCellRenderer());
        // waypointList.setPreferredSize(new Dimension(100,0));
        splitPane.setDividerLocation(100);

        new TagFilePopupMenu(wayPointScrollPane, waypointList);
        m.getPositionData().addPropertyChangeListener(
                PositionData.WPDISPLAYCHANGE, wpChangeListener);
        m.getPositionData().addPropertyChangeListener(
                PositionData.WAYPOINTSELECTED, wpSelectedListener);
    }

    private final PropertyChangeListener wpChangeListener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            map.repaint();
        }
    };

    private final PropertyChangeListener wpSelectedListener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            try {
                final BT747Waypoint w = (BT747Waypoint) evt.getNewValue();
                if (w != waypointList.getSelectedValue()) {
                    waypointList.setSelectedValue(w, true);
                    ml.selectedWaypoint(w);
                }
                if (w.getGpsRecord().hasPosition()) {
                    map.setAddressLocation(w.getPosition());
                }
                map.repaint();
            } catch (Exception e) {
                bt747.sys.Generic.debug("Waypoint selection", e);
                // TODO: handle exception
            }
        }
    };

    private volatile GPSPositionWaypoint gpsPosition = null;

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
     */
    public void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.SETTING_CHANGE:
            try {
                final int arg = Integer.valueOf((String) e.getArg());
                switch (arg) {
                case AppSettings.MAPCACHEDIRECTORY:
                    setMapTileCacheDirectory(m
                            .getStringOpt(AppSettings.MAPCACHEDIRECTORY));
                    break;
                case AppSettings.MAPTYPE:
                    updateMap();
                    break;
                case AppSettings.COLOR_VALIDTRACK:
                    final Color validColor = new Color(Conv.hex2Int(m
                            .getStringOpt(AppSettings.COLOR_VALIDTRACK)));
                    trackRenderer.setColor(validColor);
                    break;
                }
            } catch (final Exception ex) {
                Generic.debug("MyMap modelevent", ex);
                // TODO: handle exception
            }
            break;
        case ModelEvent.GPRMC:
            // Center on the current GPS position.
            final GPSRecord r = (GPSRecord) e.getArg();
            if (r.hasPosition() && r.hasValid()
                    && (r.valid & BT747Constants.VALID_NO_FIX_MASK) == 0) {
                if (gpsPosition == null) {
                    // Seems to be valid position
                    mapViewer.setCenterPosition(new GeoPosition(r.latitude,
                            r.longitude));
                    mapViewer.setZoom(4);
                    gpsPosition = new GPSPositionWaypoint(r);
                } else {
                    gpsPosition.setGpsRec(r);
                }
                mapViewer.repaint();
            }
            // updateRMCData((GPSRecord) e.getArg());
            break;
        case J2SEAppModel.UPDATE_TRACKPOINT_LIST:
            setZoom();
            break;
        case J2SEAppModel.UPDATE_WAYPOINT_LIST:
        case J2SEAppModel.UPDATE_USERWAYPOINT_LIST:
            setZoom();
            break;

        default:
            break;
        }
        // TODO Auto-generated method stub

    }

    private void updateMap() {
        setMap(m.getIntOpt(AppSettings.MAPTYPE));
    }

    public void setMapTileCacheDirectory() {
        if (m != null) {
            setMapTileCacheDirectory(m
                    .getStringOpt(AppSettings.MAPCACHEDIRECTORY));
        }
    }

    public void setMapTileCacheDirectory(final String path) {
        File f;
        f = new File(path);
        if (f.exists() && f.isDirectory() && (tf != null)) {
            try {
                tf.getTileCache().setDiskCacheDir(f);
            } catch (final Exception e) {
                Generic.debug("Map tile directory setting", e);
                // TODO: handle exception
            }
        }
    }

    /**
     * 
     */
    private boolean setZoom() {
        // TODO Auto-generated method stub

        double minlat = 180f;
        double minlon = 90f;
        double maxlat = -180f;
        double maxlon = -90f;
        boolean hasPositions = false;
        final PositionData pd = m.getPositionData();
        final List<List<GPSRecord>> trks = pd.getTracks();

        if (trks != null && trks.size() != 0) {
            for (final List<GPSRecord> trk : trks) {
                for (final GPSRecord r : trk) {
                    if(!r.hasPosition()) {
                        continue;
                    }
                    if (r.latitude < minlat) {
                        minlat = r.latitude;
                    }
                    if (r.latitude > maxlat) {
                        maxlat = r.latitude;
                    }
                    if (r.longitude < minlon) {
                        minlon = r.longitude;
                    }
                    if (r.longitude > maxlon) {
                        maxlon = r.longitude;
                    }
                    hasPositions = true;
                }
            }
        }

        for (final BT747Waypoint w : m.getPositionData().getBT747Waypoints()) {
            final GPSRecord r = w.getGpsRecord();
            if (r.hasLatitude() && r.hasLongitude()) {
                if (r.latitude < minlat) {
                    minlat = r.latitude;
                }
                if (r.latitude > maxlat) {
                    maxlat = r.latitude;
                }
                if (r.longitude < minlon) {
                    minlon = r.longitude;
                }
                if (r.longitude > maxlon) {
                    maxlon = r.longitude;
                }
                hasPositions = true;
            }
        }

        for (final BT747Waypoint w : m.getPositionData()
                .getBT747UserWaypoints()) {
            final GPSRecord r = w.getGpsRecord();
            if (r.hasLatitude() && r.hasLongitude()) {
                if (r.latitude < minlat) {
                    minlat = r.latitude;
                }
                if (r.latitude > maxlat) {
                    maxlat = r.latitude;
                }
                if (r.longitude < minlon) {
                    minlon = r.longitude;
                }
                if (r.longitude > maxlon) {
                    maxlon = r.longitude;
                }
                hasPositions = true;
            }
        }

        if (hasPositions) {
            final Set<GeoPosition> bounds = new HashSet<GeoPosition>();
            bounds.add(new GeoPosition(minlat, minlon));
            bounds.add(new GeoPosition(maxlat, maxlon));
            mapViewer.calculateZoomFrom(bounds);
        }
        return hasPositions;
    }

    enum MapType {
        OpenStreetMap, OsmaRender, Cycle, Map4
    };

    private void setMap(final int maptypeOrdinal) {
        try {
            setMap(MapType.values()[maptypeOrdinal]);
        } catch (final Exception e) {
            setMap(MapType.values()[0]);
        }
    }

    private static class MyLinkAction extends LinkAction<String> {

        /**
         * 
         */
        private static final long serialVersionUID = -3604244390869862416L;
        private String des;
        private String url;

        public MyLinkAction(final String description, final String b) {
            des = description;
            url = b;
        }

        public String toString() {
            return des;
        };

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(final ActionEvent e) {
            BareBonesBrowserLaunch.openURL(url);
            // BrowserControl.displayURL(url);
        }
    };

    /**
     * 
     */
    private void setMap(final MapType maptype) {
        TileFactoryInfo info = null;

        switch (maptype) {
        // Not used because that would likely be a license violation.
        // case GoogleMaps:
        // info = MapFactoryInfos.tfiGOOGLEMAPS;
        // break;
        case OsmaRender:
            info = MapFactoryInfos.tfiOSM_OSMARENDER;
            break;
        case Cycle:
            info = MapFactoryInfos.tfiOSM_OSM_CYCLE;
            break;
//        case Map4:
//            info = MapFactoryInfos.tfiDigitalGlobe512;
//            break;
        case OpenStreetMap:
        default:
            info = MapFactoryInfos.tfiOpenStreetMap;
        }
        final Rectangle r = map.getMainMap().getViewportBounds();
        final Set<GeoPosition> bounds = new HashSet<GeoPosition>();
        final TileFactory currentFactory = map.getMainMap().getTileFactory();
        final int zoom = map.getMainMap().getZoom();
        bounds.add(currentFactory.pixelToGeo(new Point2D.Double(r.getX(), r
                .getY()), zoom));
        bounds.add(currentFactory.pixelToGeo(new Point2D.Double(r.getX(), r
                .getY()
                + r.getHeight()), zoom));
        bounds.add(currentFactory.pixelToGeo(new Point2D.Double(r.getX()
                + r.getWidth(), r.getY()), zoom));
        bounds.add(currentFactory.pixelToGeo(new Point2D.Double(r.getX()
                + r.getWidth(), r.getY() + r.getHeight()), zoom));

        tf = new DefaultTileFactory(info);
        setMapTileCacheDirectory();
        map.setTileFactory(tf);

        if (MyTileFactoryInfo.class.isInstance(info)) {
            final MyTileFactoryInfo tfi = (MyTileFactoryInfo) info;
            map.getDataProviderLink().setAction(
                    new MyLinkAction(tfi.description, tfi.url));
            map.getDataProviderLink().setText(tfi.description);
            // map.setDataProviderCreditShown(true);
            map.setDataProviderLinkShown(true);
            // map.setAddressLocationShown(true);
        } else {
            // map.setDataProviderCreditShown(false);
            map.setDataProviderLinkShown(false);
        }
        map.setAddressLocationShown(false);
        mapViewer.calculateZoomFrom(bounds);
    }

    private class MyWaypointPainter<T extends JXMapViewer> extends
            WaypointPainter<JXMapViewer> {
        private JXMapViewer map = mapViewer;

        private BT747TrackRenderer trackRenderer;

        /*
         * (non-Javadoc)
         * 
         * @see org.jdesktop.swingx.mapviewer.WaypointPainter#doPaint(java.awt.Graphics2D,
         *      org.jdesktop.swingx.JXMapViewer, int, int)
         */
        @Override
        protected void doPaint(final Graphics2D g, final JXMapViewer map,
                final int width, final int height) {
            final List<List<GPSRecord>> trks = m.getPositionData()
                    .getTracks();
            if (trks != null) {
                for (final List<GPSRecord> track : trks) {
                    trackRenderer.paintTrack(g, map, track);
                }
            }

            // Paint waypoints
            super.doPaint(g, map, width, height);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jdesktop.swingx.mapviewer.WaypointPainter#setWaypoints(java.lang.Iterable)
         */
        @Override
        public void setWaypoints(final Iterable<Waypoint> waypoints) {
            Generic.debug("Waypoints should not be set this way");
        }

        public java.lang.Iterable<Waypoint> getWaypoints() {
            return getWaypointsIterable();
        }

        public Waypoint getContains(final Point pt) {

            // figure out which waypoints are within this map viewport
            // so, get the bounds
            final Rectangle viewportBounds = map.getViewportBounds();
            final int zoom = map.getZoom();
            final Dimension sizeInTiles = map.getTileFactory().getMapSize(
                    zoom);
            final int tileSize = map.getTileFactory().getTileSize(zoom);
            final Dimension sizeInPixels = new Dimension(sizeInTiles.width
                    * tileSize, sizeInTiles.height * tileSize);

            double vpx = viewportBounds.getX();
            // normalize the left edge of the viewport to be positive
            while (vpx < 0) {
                vpx += sizeInPixels.getWidth();
            }
            // normalize the left edge of the viewport to no wrap around the
            // world
            while (vpx > sizeInPixels.getWidth()) {
                vpx -= sizeInPixels.getWidth();
            }

            // create two new viewports next to eachother
            final Rectangle2D vp2 = new Rectangle2D.Double(vpx,
                    viewportBounds.getY(), viewportBounds.getWidth(),
                    viewportBounds.getHeight());
            final Rectangle2D vp3 = new Rectangle2D.Double(vpx
                    - sizeInPixels.getWidth(), viewportBounds.getY(),
                    viewportBounds.getWidth(), viewportBounds.getHeight());

            final MapRendererFactoryMethod factory = MapRendererFactoryMethod
                    .getInstance();

            Waypoint w;
            w = findContainingWaypoint(m.getPositionData()
                    .getBT747UserWaypoints(), pt, vp2, vp3, factory);
            if (w != null) {
                return w;
            }
            w = findContainingWaypoint(m.getPositionData()
                    .getBT747Waypoints(), pt, vp2, vp3, factory);
            return w;
        }

        /**
         * @param pt
         * @param vp2
         * @param vp3
         * @param factory
         * @return
         */
        private Waypoint findContainingWaypoint(
                final List<BT747Waypoint> list, final Point pt,
                final Rectangle2D vp2, final Rectangle2D vp3,
                final MapRendererFactoryMethod factory) {
            for (final Waypoint w : list) {
                final Point2D point = map.getTileFactory().geoToPixel(
                        w.getPosition(), map.getZoom());
                int x, y;
                if (vp2.contains(point)) {
                    x = (int) (point.getX() - vp2.getX());
                    y = (int) (point.getY() - vp2.getY());
                } else if (vp3.contains(point)) {
                    x = (int) (point.getX() - vp3.getX());
                    y = (int) (point.getY() - vp3.getY());
                } else {
                    continue;
                }
                final Point p = new Point((int) (pt.getX() - x), (int) (pt
                        .getY() - y));
                if (factory.rendererContains(w, p)) {
                    return w;
                }
            }
            return null;
        }

        /**
         * @param trackRenderer
         *                the trackRenderer to set
         */
        public final void setTrackRenderer(
                final BT747TrackRenderer trackRenderer) {
            this.trackRenderer = trackRenderer;
        }
    }

    private enum WaypointTypes {
        GpsPosition, Waypoints, UserWaypoints
    };

    private class wpIterable implements Iterable<Waypoint> {

        /**
         * 
         */
        public wpIterable() {
            // itr = i;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Iterable#iterator()
         */
        public Iterator<Waypoint> iterator() {
            return new I();
        }

        private class I implements Iterator<Waypoint> {
            private Iterator<BT747Waypoint> i = null;
            private WaypointTypes type = WaypointTypes.GpsPosition;

            public I() {
                // i = m.getPositionData().getBT747Waypoints().iterator();
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#hasNext()
             */
            public boolean hasNext() {
                if (i != null && i.hasNext()) {
                    return true;
                } else {
                    switch (type) {
                    case GpsPosition:
                        if (gpsPosition != null) {
                            return true;
                        }
                        type = WaypointTypes.Waypoints;
                        i = m.getPositionData().getBT747Waypoints()
                                .iterator();
                        if (i.hasNext()) {
                            return true;
                        }
                        /* fall through */
                    case Waypoints:
                        type = WaypointTypes.UserWaypoints;
                        i = m.getPositionData().getBT747UserWaypoints()
                                .iterator();
                        return i.hasNext();
                    case UserWaypoints:
                        return false;
                    }
                }
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#next()
             */
            public Waypoint next() {
                if (type == WaypointTypes.GpsPosition) {
                    type = WaypointTypes.Waypoints;
                    i = m.getPositionData().getBT747Waypoints().iterator();
                    return gpsPosition;
                }
                if (i != null) {
                    return i.next();
                }
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#remove()
             */
            public void remove() {
                if (i != null) {
                    i.remove();
                    i = null;
                }
            }
        }
    }

    public java.lang.Iterable<Waypoint> getWaypointsIterable() {
        return new wpIterable();
    };

    private class mouseListener implements MouseListener, MouseMotionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(final MouseEvent e) {
            switch (e.getButton()) {
            case MouseEvent.BUTTON3: {
                final Point pt = e.getPoint();

                final BT747Waypoint w = (BT747Waypoint) waypointPainter
                        .getContains(pt);
                if (w != null) {
                    w.toggleShowTag();
                    map.repaint();
                    e.consume();
                    if (false) {
                        GPSRecord g = w.getGpsRecord();
                        JFrame wpframe = new JFrame("Waypoint");
                        JLabel wpInfo = new JLabel("<html>"
                                + g.toString().replaceAll("\n", "<br>"));
                        wpframe.getContentPane().add(wpInfo);
                        wpframe.pack();
                        wpframe.setLocation(e.getPoint());
                        wpframe.setVisible(true);
                    }
                }

                break;
            }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(final MouseEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(final MouseEvent e) {
            // TODO Auto-generated method stub

        }

        private BT747Waypoint currentWaypoint = null;
        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        int xOffset;
        int yOffset;

        private void clearWaypointSelection() {
            if (currentWaypoint != null) {
                currentWaypoint.setSelected(false);
                currentWaypoint = null;
            }
        }

        /**
         * 
         */
        private void selectedWaypoint(final BT747Waypoint w) {
            if (w != currentWaypoint) {
                clearWaypointSelection();
            }
        }

        private long selectionTime = 0;

        public void mousePressed(final MouseEvent e) {
            selectionTime = 0;
            switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                final Point pt = e.getPoint();

                try {
                    final BT747Waypoint w = (BT747Waypoint) waypointPainter
                            .getContains(pt);

                    if (w == null) {
                        clearWaypointSelection();
                    } else {
                        if (w != currentWaypoint) {
                            clearWaypointSelection();
                        }

                        currentWaypoint = w;
                        w.setSelected(true);
                        final Point2D p = mapViewer
                                .convertGeoPositionToPoint(w.getPosition());
                        xOffset = (int) (pt.getX() - p.getX());
                        yOffset = (int) (pt.getY() - p.getY());
                        mapViewer.setPanEnabled(false);
                        repaint();
                    }
                } catch (final Exception ex) {
                    Generic.debug("Mouse pressed", ex);
                    // TODO: handle exception
                }
                selectionTime = System.currentTimeMillis();
                break;

            default:
                break;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(final MouseEvent e) {
            switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                if (currentWaypoint != null) {
                    mapViewer.setPanEnabled(true);
                }
            default:
                break;
            }
        }

        private final static int MINIMUM_TIME_FOR_DRAG = 150;

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(final MouseEvent e) {
            if ((selectionTime != 0)
                    && (System.currentTimeMillis() - selectionTime < MINIMUM_TIME_FOR_DRAG)) {
                // Moving too fast - not moving waypoint.
                mapViewer.setPanEnabled(true);
                selectionTime = 0;
            }
            if ((selectionTime != 0) && (currentWaypoint != null)) {
                e.consume();
                final Point pt = e.getPoint();
                currentWaypoint.setPosition(mapViewer
                        .convertPointToGeoPosition(new Point2D.Double(pt
                                .getX()
                                - xOffset, pt.getY() - yOffset)));
                mapViewer.repaint();

            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved(final MouseEvent e) {
            // TODO Auto-generated method stub

        }

    }

    /**
     * This method is called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {// GEN-BEGIN:initComponents

        splitPane = new javax.swing.JSplitPane();
        map = new org.jdesktop.swingx.JXMapKit();
        wayPointScrollPane = new javax.swing.JScrollPane();
        waypointList = new javax.swing.JList();

        splitPane.setBorder(null);
        splitPane.setOneTouchExpandable(true);
        splitPane.setOpaque(false);
        splitPane.setRightComponent(map);

        wayPointScrollPane
                .setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        wayPointScrollPane
                .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        waypointList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Waypoint" };

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(final int i) {
                return strings[i];
            }
        });
        waypointList
                .setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        waypointList.setOpaque(false);
        wayPointScrollPane.setViewportView(waypointList);

        splitPane.setLeftComponent(wayPointScrollPane);

        final org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                this);
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(0, 0, 0).add(splitPane)));
        layout.setVerticalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(0, 0, 0).add(splitPane)));
    }// GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXMapKit map;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JScrollPane wayPointScrollPane;
    private javax.swing.JList waypointList;
    // End of variables declaration//GEN-END:variables

}
