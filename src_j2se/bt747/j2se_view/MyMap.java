/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/*
 * MapReference.java
 * 
 * Created on 23 déc. 2008, 21:59:04
 */

package bt747.j2se_view;

import gps.log.GPSRecord;

import java.awt.Component;
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

import javax.swing.ScrollPaneConstants;

import net.iharder.dnd.DropListener;
import net.iharder.dnd.FileDrop;
import net.sf.bt747.j2se.app.list.BT747WaypointListCellRenderer;
import net.sf.bt747.j2se.app.map.BT747TrackRenderer;
import net.sf.bt747.j2se.app.map.MapFactoryInfos;
import net.sf.bt747.j2se.app.map.MapRendererFactoryMethod;
import net.sf.bt747.j2se.app.map.MyTileFactoryInfo;
import net.sf.bt747.j2se.app.utils.BrowserControl;

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
import bt747.j2se_view.model.PositionData;
import bt747.model.Model;
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

    /** Creates new form MapReference */
    public MyMap() {
        initComponents();
    }

    private J2SEAppController c;
    private J2SEAppModel m;

    public void init(J2SEAppController pC) {
        c = pC;
        m = c.getAppModel();
        m.addListener(this);

        initGui();
        updateMap();
        map.setZoom(tf.getInfo().getMaximumZoomLevel() - 4);
        map.setAddressLocation(new GeoPosition(51.5, 0));

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
        waypointPainter.setRenderer(MapRendererFactoryMethod.getInstance());
        mapViewer.setRecenterOnClickEnabled(true);
        mapViewer.getOverlayPainter();
        CompoundPainter<Object> cp = new CompoundPainter<Object>();
        cp.setPainters(mapViewer.getOverlayPainter(), waypointPainter);
        mapViewer.setOverlayPainter(cp);

        ml = new mouseListener();
        mapViewer.addMouseListener(ml);
        mapViewer.addMouseMotionListener(ml);
        waypointList.setModel(m.getPositionData().getWaypointListModel());
        waypointList.setCellRenderer(new BT747WaypointListCellRenderer());
        // waypointList.setPreferredSize(new Dimension(100,0));
        splitPane.setDividerLocation(100);

        m.getPositionData().addPropertyChangeListener(
                PositionData.WPDISPLAYCHANGE, wpChangeListener);
        m.getPositionData().addPropertyChangeListener(
                PositionData.WAYPOINTSELECTED, wpSelectedListener);

        DropListener dl;
        dl = new DropListener() {
            /*
             * (non-Javadoc)
             * 
             * @see net.iharder.dnd.FileDrop.Listener#filesDropped(java.io.File[])
             */
            public void filesDropped(final java.io.File[] files) {
                m.getPositionData().addFiles(files);
            }
        };
        new FileDrop((Component) this, dl);
    }

    private PropertyChangeListener wpChangeListener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            map.repaint();
        }
    };

    private PropertyChangeListener wpSelectedListener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
            try {
                BT747Waypoint w = (BT747Waypoint) evt.getNewValue();
                if (w != waypointList.getSelectedValue()) {
                    waypointList.setSelectedValue(w, true);
                    ml.selectedWaypoint(w);
                }
                if (w.getGpsRecord().hasLocation()) {
                    map.setAddressLocation(w.getPosition());
                }
                map.repaint();
            } catch (Exception e) {
                bt747.sys.Generic.debug("Waypoint selection", e);
                // TODO: handle exception
            }
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
     */
    public void modelEvent(ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.SETTING_CHANGE:
            try {
                int arg = Integer.valueOf((String) e.getArg());
                switch (arg) {
                case Model.MAPCACHEDIRECTORY:
                    setMapTileCacheDirectory(m
                            .getStringOpt(Model.MAPCACHEDIRECTORY));
                    break;
                case Model.MAPTYPE:
                    updateMap();
                    break;
                }
            } catch (Exception ex) {
                Generic.debug("MyMap modelevent", ex);
                // TODO: handle exception
            }
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
        setMap(m.getIntOpt(Model.MAPTYPE));
    }

    public void setMapTileCacheDirectory() {
        if (m != null) {
            setMapTileCacheDirectory(m.getStringOpt(Model.MAPCACHEDIRECTORY));
        }
    }

    public void setMapTileCacheDirectory(final String path) {
        File f;
        f = new File(path);
        if (f.exists() && f.isDirectory() && tf != null) {
            try {
                tf.getTileCache().setDiskCacheDir(f);
            } catch (Exception e) {
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
        PositionData pd = m.getPositionData();

        if (pd.getTracks().size() != 0) {
            for (List<GPSRecord> trk : pd.getTracks()) {
                for (GPSRecord r : trk) {
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

        for (BT747Waypoint w : m.getPositionData().getBT747Waypoints()) {
            GPSRecord r = w.getGpsRecord();
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

        for (BT747Waypoint w : m.getPositionData().getBT747UserWaypoints()) {
            GPSRecord r = w.getGpsRecord();
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
            Set<GeoPosition> bounds = new HashSet<GeoPosition>();
            bounds.add(new GeoPosition(minlat, minlon));
            bounds.add(new GeoPosition(maxlat, maxlon));
            if (mapViewer.getZoom() > mapViewer.getTileFactory().getInfo()
                    .getMaximumZoomLevel() - 6) {
                mapViewer.setZoom(mapViewer.getTileFactory().getInfo()
                        .getMinimumZoomLevel());
            }
            mapViewer.calculateZoomFrom(bounds);
        }
        return hasPositions;
    }

    enum MapType {
        OpenStreetMap, OsmaRender, Cycle, Map4
    };

    private void setMap(int maptypeOrdinal) {
        try {
            setMap(MapType.values()[maptypeOrdinal]);
        } catch (Exception e) {
            setMap(MapType.values()[0]);
        }
    }

    private class MyLinkAction extends LinkAction<String> {

        /**
         * 
         */
        private static final long serialVersionUID = -3604244390869862416L;
        private String des;
        private String url;

        public MyLinkAction(String description, String b) {
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
        public void actionPerformed(ActionEvent e) {
            BrowserControl.displayURL(url);
        }
    };

    /**
     * 
     */
    private void setMap(MapType maptype) {
        TileFactoryInfo info = null;

        switch (maptype) {
        case OsmaRender:
            info = MapFactoryInfos.tfiOSM_OSMARENDER;
            break;
        case Cycle:
            info = MapFactoryInfos.tfiOSM_OSM_CYCLE;
            break;
        case Map4:
            info = MapFactoryInfos.tfiDigitalGlobe512;
            break;
        case OpenStreetMap:
        default:
            info = MapFactoryInfos.tfiOpenStreetMap;
        }
        final Rectangle r = map.getMainMap().getViewportBounds();
        Set<GeoPosition> bounds = new HashSet<GeoPosition>();
        final TileFactory currentFactory =map.getMainMap().getTileFactory();
        int zoom = map.getMainMap().getZoom();
        bounds.add(currentFactory.pixelToGeo(new Point2D.Double(r.getX(),r.getY()),zoom));
        bounds.add(currentFactory.pixelToGeo(new Point2D.Double(r.getX(),r.getY()+r.getHeight()),zoom));
        bounds.add(currentFactory.pixelToGeo(new Point2D.Double(r.getX()+r.getWidth(),r.getY()),zoom));
        bounds.add(currentFactory.pixelToGeo(new Point2D.Double(r.getX()+r.getWidth(),r.getY()+r.getHeight()),zoom));
        
        tf = new DefaultTileFactory(info);
        setMapTileCacheDirectory();
        map.setTileFactory(tf);

        if (MyTileFactoryInfo.class.isInstance(info)) {
            MyTileFactoryInfo tfi = (MyTileFactoryInfo) info;
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
        System.gc();
    }

    private class MyWaypointPainter<T extends JXMapViewer> extends
            WaypointPainter<JXMapViewer> {
        private JXMapViewer map = mapViewer;

        private BT747TrackRenderer trackRenderer = BT747TrackRenderer
                .getInstance();

        /*
         * (non-Javadoc)
         * 
         * @see org.jdesktop.swingx.mapviewer.WaypointPainter#doPaint(java.awt.Graphics2D,
         *      org.jdesktop.swingx.JXMapViewer, int, int)
         */
        @Override
        protected void doPaint(Graphics2D g, JXMapViewer map, int width,
                int height) {
            for (List<GPSRecord> track : m.getPositionData().getTracks()) {
                trackRenderer.paintTrack(g, map, track);
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
        public void setWaypoints(Iterable<Waypoint> waypoints) {
            Generic.debug("Waypoints should not be set this way");
        }

        public java.lang.Iterable<Waypoint> getWaypoints() {
            return getWaypointsIterable();
        }

        public Waypoint getContains(Point pt) {

            // figure out which waypoints are within this map viewport
            // so, get the bounds
            Rectangle viewportBounds = map.getViewportBounds();
            int zoom = map.getZoom();
            Dimension sizeInTiles = map.getTileFactory().getMapSize(zoom);
            int tileSize = map.getTileFactory().getTileSize(zoom);
            Dimension sizeInPixels = new Dimension(sizeInTiles.width
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
            Rectangle2D vp2 = new Rectangle2D.Double(vpx, viewportBounds
                    .getY(), viewportBounds.getWidth(), viewportBounds
                    .getHeight());
            Rectangle2D vp3 = new Rectangle2D.Double(vpx
                    - sizeInPixels.getWidth(), viewportBounds.getY(),
                    viewportBounds.getWidth(), viewportBounds.getHeight());

            MapRendererFactoryMethod factory = MapRendererFactoryMethod
                    .getInstance();

            Waypoint w;
            w = findContainingWaypoint(m.getPositionData()
                    .getBT747UserWaypoints(), pt, vp2, vp3, factory);
            if (w != null)
                return w;
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
        private Waypoint findContainingWaypoint(List<BT747Waypoint> list,
                Point pt, Rectangle2D vp2, Rectangle2D vp3,
                MapRendererFactoryMethod factory) {
            for (Waypoint w : list) {
                Point2D point = map.getTileFactory().geoToPixel(
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
                Point p = new Point((int) (pt.getX() - x),
                        (int) (pt.getY() - y));
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
        public final void setTrackRenderer(BT747TrackRenderer trackRenderer) {
            this.trackRenderer = trackRenderer;
        }
    }

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
            private Iterator<BT747Waypoint> i;
            private int type = 0;

            public I() {
                i = m.getPositionData().getBT747Waypoints().iterator();
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#hasNext()
             */
            public boolean hasNext() {
                if (i.hasNext()) {
                    return true;
                } else {
                    if (type == 0) {
                        type = 1;
                        i = m.getPositionData().getBT747UserWaypoints()
                                .iterator();
                        return i.hasNext();
                    } else {
                        return false;
                    }
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#next()
             */
            public Waypoint next() {
                return i.next();
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.util.Iterator#remove()
             */
            public void remove() {
                i.remove();
                i = null;
            }
        }
    }

    public java.lang.Iterable<Waypoint> getWaypointsIterable() {
        return new wpIterable();
    };

    private class mouseListener implements MouseListener, MouseMotionListener {

        Point previous;

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e) {
            switch (e.getButton()) {
            case MouseEvent.BUTTON3: {
                Point pt = e.getPoint();

                BT747Waypoint w = (BT747Waypoint) waypointPainter
                        .getContains(pt);
                if (w != null) {
                    w.toggleShowTag();
                    map.repaint();
                    e.consume();
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
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent e) {
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
        private void selectedWaypoint(BT747Waypoint w) {
            if (w != currentWaypoint) {
                clearWaypointSelection();
            }
        }

        private long selectionTime = 0;

        public void mousePressed(MouseEvent e) {
            selectionTime = 0;
            switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                Point pt = e.getPoint();

                try {
                    BT747Waypoint w = (BT747Waypoint) waypointPainter
                            .getContains(pt);

                    if (w == null) {
                        clearWaypointSelection();
                    } else {
                        if (w != currentWaypoint) {
                            clearWaypointSelection();
                        }

                        currentWaypoint = w;
                        w.setSelected(true);
                        previous = pt;
                        Point2D p = mapViewer.convertGeoPositionToPoint(w
                                .getPosition());
                        xOffset = (int) (pt.getX() - p.getX());
                        yOffset = (int) (pt.getY() - p.getY());
                        mapViewer.setPanEnabled(false);
                        repaint();
                    }
                } catch (Exception ex) {
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
        public void mouseReleased(MouseEvent e) {
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
        public void mouseDragged(MouseEvent e) {
            if (selectionTime != 0
                    && (System.currentTimeMillis() - selectionTime < MINIMUM_TIME_FOR_DRAG)) {
                // Moving too fast - not moving waypoint.
                mapViewer.setPanEnabled(true);
                selectionTime = 0;
            }
            if (selectionTime != 0 && currentWaypoint != null) {
                e.consume();
                Point pt = e.getPoint();
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
        public void mouseMoved(MouseEvent e) {
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

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        waypointList
                .setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        waypointList.setOpaque(false);
        wayPointScrollPane.setViewportView(waypointList);

        splitPane.setLeftComponent(wayPointScrollPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                this);
        this.setLayout(layout);
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