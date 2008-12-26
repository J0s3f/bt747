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
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.bt747.j2se.map.BT747TrackRenderer;
import net.sf.bt747.j2se.map.MapRendererFactoryMethod;
import net.sf.bt747.j2se.utils.BrowserControl;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.hyperlink.LinkAction;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.painter.CompoundPainter;

import bt747.Version;
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

    private void initGui() {
        map.setMiniMapVisible(true);
        wayPointScrollPane
                .setVisible(Version.VERSION_NUMBER.equals("d.evel"));
        // map.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
        mapViewer = map.getMainMap();
        waypointPainter = new MyWaypointPainter<JXMapViewer>();
        waypointPainter.setRenderer(MapRendererFactoryMethod.getInstance());
        mapViewer.setRecenterOnClickEnabled(true);
        mapViewer.getOverlayPainter();
        CompoundPainter<Object> cp = new CompoundPainter<Object>();
        cp.setPainters(mapViewer.getOverlayPainter(), waypointPainter);
        mapViewer.setOverlayPainter(cp);

        mouseListener ml = new mouseListener();
        mapViewer.addMouseListener(ml);
        mapViewer.addMouseMotionListener(ml);
        waypointList.setModel(m.getPositionData().getWaypointListModel());

    }

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
                // TODO: handle exception
            }
            break;
        case J2SEAppModel.UPDATE_TRACKPOINT_LIST:
            setZoom();
            break;
        case J2SEAppModel.UPDATE_WAYPOINT_LIST:
        case J2SEAppModel.UPDATE_USERWAYPOINT_LIST:
            updateWaypoints();
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
                // TODO: handle exception
            }
        }
    }


    private void updateWaypoints() {
        setZoom();
    }

    /**
     * 
     */
    private void setZoom() {
        // TODO Auto-generated method stub

        double minlat = 180f;
        double minlon = 90f;
        double maxlat = -180f;
        double maxlon = -90f;
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
                }
            }
        }

        for (Waypoint w : getWaypointsIterable()) {
            GeoPosition r = w.getPosition();
            if (r.getLatitude() < minlat) {
                minlat = r.getLatitude();
            }
            if (r.getLatitude() > maxlat) {
                maxlat = r.getLatitude();
            }
            if (r.getLongitude() < minlon) {
                minlon = r.getLongitude();
            }
            if (r.getLongitude() > maxlon) {
                maxlon = r.getLongitude();
            }
        }

        Set<GeoPosition> bounds = new HashSet<GeoPosition>();
        bounds.add(new GeoPosition(minlat, minlon));
        bounds.add(new GeoPosition(maxlat, maxlon));
        if (mapViewer.getZoom() > mapViewer.getTileFactory().getInfo()
                .getMaximumZoomLevel() - 6) {
            mapViewer.setZoom(2);
        }
        mapViewer.calculateZoomFrom(bounds);

    }

    private final TileFactoryInfo tfiOpenStreetMap = new MyTileFactoryInfo(
            "osm", 1,
            // tile size is 256 and x/y orientation is normal
            18, 19, 256, true, true, "http://tile.openstreetmap.org", "x",
            "y", "z",
            // Provider Description
            "OpenStreetMap (Mapnik)",
            // Provider Link
            "http://www.openstreetmap.org") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = getTotalMapZoom() - zoom;
            String url = this.baseURL + "/" + zoom + "/" + x + "/" + y
                    + ".png";
            return url;
        }
    };

    private final TileFactoryInfo tfiOSM_OSMARENDER = new MyTileFactoryInfo(
            // tile size is 256 and x/y orientation is normal
            "osmarender", 1, 18, 19, 256, true, true,
            "http://tah.openstreetmap.org/Tiles/tile", "x", "y", "z",
            // Provider Description
            "Open Street Map (Osmarender)",
            // Provider Link
            "http://www.openstreetmap.org/?layers=0B00FTF") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = getTotalMapZoom() - zoom;
            String url = this.baseURL + "/" + zoom + "/" + x + "/" + y
                    + ".png";
            return url;
        }
    };

    private final TileFactoryInfo tfiOSM_OSM_CYCLE = new MyTileFactoryInfo(
            // tile size is 256 and x/y orientation is normal
            "osmcycle", 1, 15, 19, 256, true, true,
            "http://www.thunderflames.org/tiles/cycle", "x", "y", "z",
            // Provider Description
            "OpenCycleMap.org - Creative Commons-by-SA License",
            // Provider Link
            "http://www.opencylemap.org") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = getTotalMapZoom() - zoom;
            String url = this.baseURL + "/" + zoom + "/" + x + "/" + y
                    + ".png";
            return url;
        }

    };

    class MyTileFactoryInfo extends TileFactoryInfo {
        public String description;
        public String url;

        /**
         * @param name
         * @param minimumZoomLevel
         * @param maximumZoomLevel
         * @param totalMapZoom
         * @param tileSize
         * @param xr2l
         * @param yt2b
         * @param baseURL
         * @param xparam
         * @param yparam
         * @param zparam
         */
        public MyTileFactoryInfo(String name, int minimumZoomLevel,
                int maximumZoomLevel, int totalMapZoom, int tileSize,
                boolean xr2l, boolean yt2b, String baseURL, String xparam,
                String yparam, String zparam, String description, String url) {
            super(name, minimumZoomLevel, maximumZoomLevel, totalMapZoom,
                    tileSize, xr2l, yt2b, baseURL, xparam, yparam, zparam);
            this.url = url;
            this.description = description;
        }

    }

    // Not used for license reasons
    private final int GOOGLEMAPS_MAX_ZOOM = 18;
    @SuppressWarnings("unused")
    private TileFactoryInfo tfiGoogleMaps = new TileFactoryInfo(
            // tile size is 256 and x/y orientation is normal
            "gmapstreet", 1, GOOGLEMAPS_MAX_ZOOM - 2, GOOGLEMAPS_MAX_ZOOM,
            256, true, true,
            // "http://mt2.google.com/mt?n=404&v=w2.21",//5/15/10.png",
            "http://khm2.google.com/kh?v=33", "x", "y", "z") {
        public String getTileUrl(int x, int y, int zoom) {
            zoom = GOOGLEMAPS_MAX_ZOOM - zoom;
            String url = this.baseURL + "&x=" + x + "&y=" + y + "&z=" + zoom;
            return url;
        }

    };

    enum MapType {
        OpenStreetMap, OsmaRender, Cycle
    };

    private void setMap(int maptypeOrdinal) {
        setMap(MapType.values()[maptypeOrdinal]);

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
        TileFactoryInfo info;
        ;
        switch (maptype) {
        case OsmaRender:
            info = tfiOSM_OSMARENDER;
            break;
        case Cycle:
            info = tfiOSM_OSM_CYCLE;
            break;
        case OpenStreetMap:
        default:
            info = tfiOpenStreetMap;
        }
        int currentzoom = map.getMainMap().getZoom();
        tf = new DefaultTileFactory(info);
        setMapTileCacheDirectory();
        map.setTileFactory(tf);
        map.getMainMap().setZoom(currentzoom);

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

        public void toggleSelected(Waypoint w) {
            MapRendererFactoryMethod f = MapRendererFactoryMethod
                    .getInstance();
            f.toggleSelected(w);
            repaint();
        }
        
        /* (non-Javadoc)
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

        private Iterable<BT747Waypoint> itr;
        
        /**
         * 
         */
        public wpIterable(Iterable<BT747Waypoint> i) {
            itr = i;
        }
        
        /* (non-Javadoc)
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
            
            /* (non-Javadoc)
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

            /* (non-Javadoc)
             * @see java.util.Iterator#next()
             */
            public Waypoint next() {
                return i.next();
            }

            /* (non-Javadoc)
             * @see java.util.Iterator#remove()
             */
            public void remove() {
                i.remove();
                i = null;
            }
        }
    }

    public java.lang.Iterable<Waypoint> getWaypointsIterable() {
        return new wpIterable(m.getPositionData().getBT747Waypoints());
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
            case MouseEvent.BUTTON3:
                Point pt = e.getPoint();

                Waypoint w = waypointPainter.getContains(pt);
                if (w != null) {
                    waypointPainter.toggleSelected(w);
                    e.consume();
                }

                break;

            default:
                break;
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

        private Waypoint currentWaypoint = null;
        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        int xOffset;
        int yOffset;

        public void mousePressed(MouseEvent e) {
            switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                Point pt = e.getPoint();

                Waypoint w = waypointPainter.getContains(pt);

                if (w != null) {
                    currentWaypoint = w;
                    previous = pt;
                    Point2D p = mapViewer.convertGeoPositionToPoint(w
                            .getPosition());
                    xOffset = (int) (pt.getX() - p.getX());
                    yOffset = (int) (pt.getY() - p.getY());
                    mapViewer.setPanEnabled(false);
                }
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
                    currentWaypoint = null;
                }
            default:
                break;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent e) {
            if (currentWaypoint != null) {
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

        wayPointScrollPane = new javax.swing.JScrollPane();
        waypointList = new javax.swing.JList();
        map = new org.jdesktop.swingx.JXMapKit();

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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(wayPointScrollPane,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(
                                org.jdesktop.layout.LayoutStyle.RELATED).add(
                                map,
                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                304, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(map,
                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 236,
                Short.MAX_VALUE).add(wayPointScrollPane,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236,
                Short.MAX_VALUE));
    }// GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXMapKit map;
    private javax.swing.JScrollPane wayPointScrollPane;
    private javax.swing.JList waypointList;
    // End of variables declaration//GEN-END:variables

}
