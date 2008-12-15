//********************************************************************
//***                           BT747                              ***
//***                 (c)2007-2008 Mario De Weerd                  ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***                                                              ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
/**
 * This needs to be refactored!
 */
package bt747.j2se_view;

import gps.log.GPSRecord;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import net.sf.bt747.j2se.map.BT747TrackRenderer;
import net.sf.bt747.j2se.map.WayPointRendererFactoryMethod;
import net.sf.bt747.j2se.map.WaypointAdapter;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;

import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * @author Mario
 * 
 */
public class MyMap extends JPanel implements MapViewerInterface, ModelListener {
    JXMapKit map;
    JXMapViewer mapViewer;
    MyWaypointPainter<JXMapViewer> waypointPainter;
    DefaultTileFactory tf = null;

    /**
     * 
     */
    public MyMap() {
        // TODO Auto-generated constructor stub
        map = new JXMapKit();
        map.setMiniMapVisible(true);
        // map.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
        setOpenstreetMap();
        mapViewer = map.getMainMap();
        waypointPainter = new MyWaypointPainter<JXMapViewer>();
        waypointPainter
                .setRenderer(WayPointRendererFactoryMethod.getInstance());
        mapViewer.setRecenterOnClickEnabled(true);
        mapViewer.setOverlayPainter(waypointPainter);

        mouseListener ml = new mouseListener();
        mapViewer.addMouseListener(ml);
        mapViewer.addMouseMotionListener(ml);

        add(map);
        org.jdesktop.layout.GroupLayout InfoPanelLayout = new org.jdesktop.layout.GroupLayout(
                this);
        this.setLayout(InfoPanelLayout);
        InfoPanelLayout.setHorizontalGroup(InfoPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                InfoPanelLayout.createSequentialGroup()
                // .addContainerGap()
                        .add(map, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                300, Short.MAX_VALUE)
        // .addContainerGap()
                ));
        InfoPanelLayout.setVerticalGroup(InfoPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                InfoPanelLayout.createSequentialGroup()
                // .addContainerGap()
                        .add(map, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                312, Short.MAX_VALUE)
        // .addContainerGap()
                ));

        // map.setAddressLocation(new GeoPosition(41.881944,2.5));
    }

    private J2SEAppController c;
    private Model m;

    public void init(J2SEAppController pC) {
        c = pC;
        m = c.getModel();
        m.addListener(this);
        
        setMapTileCacheDirectory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
     */
    public void modelEvent(ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.SETTING_CHANGE:
            if (e.getArg() != null) {
                if (e.getArg().equals(String.valueOf(Model.MAPCACHEDIRECTORY))) {
                    setMapTileCacheDirectory(m
                            .getStringOpt(Model.MAPCACHEDIRECTORY));
                }
            }

            break;

        default:
            break;
        }
        // TODO Auto-generated method stub

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

    private static void addToSet(Set<Waypoint> waypoints, GPSRecord[] records) {
        for (int i = 0; i < records.length; i++) {
            GPSRecord r = records[i];
            if (r.hasLatitude() && r.hasLongitude()) {
                Waypoint w = new WaypointAdapter(r);
                waypoints.add(w);
            }
        }
    }

    private java.util.Set<Waypoint> waypoints = new java.util.HashSet<Waypoint>();
    private java.util.Set<Waypoint> filepoints = new java.util.HashSet<Waypoint>();
    private java.util.Set<Waypoint> allpoints = new java.util.HashSet<Waypoint>();

    private void updateWaypoints() {
        java.util.Set<GeoPosition> myPositions = new java.util.HashSet<GeoPosition>();
        allpoints.clear();
        allpoints.addAll(waypoints);
        allpoints.addAll(filepoints);
        waypointPainter.setWaypoints(allpoints);
        setZoom();
    }

    public void setUserWayPoints(GPSRecord[] records) {
        filepoints.clear();
        addToSet(filepoints, records);
        updateWaypoints();
    }

    public void addTrack(List<GPSRecord> track) {
        waypointPainter.addTrack(track);
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
        if (waypointPainter.getTracks() != null) {
            for (List<GPSRecord> trk : waypointPainter.getTracks()) {
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

        for (Waypoint w : allpoints) {
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

    public void setTracks(List<List<GPSRecord>> tracks) {

        waypointPainter.setTracks(tracks);
        setZoom();

    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.j2se_view.MapViewerInterface#setWayPoints(gps.log.GPSRecord[])
     */
    public void setWayPoints(GPSRecord[] records) {
        waypoints.clear();
        addToSet(waypoints, records);
        updateWaypoints();
    }

    /**
     * 
     */
    private void setOpenstreetMap() {
        final int max = 19;
        TileFactoryInfo info = new TileFactoryInfo(1, max - 2, max, 256, true,
                true, // tile size is 256 and x/y orientation is normal
                "http://tile.openstreetmap.org",// 5/15/10.png",
                "x", "y", "z") {
            public String getTileUrl(int x, int y, int zoom) {
                zoom = max - zoom;
                String url = this.baseURL + "/" + zoom + "/" + x + "/" + y
                        + ".png";
                return url;
            }

        };
        tf = new DefaultTileFactory(info);
        setMapTileCacheDirectory();
        map.setTileFactory(tf);
        map.setZoom(tf.getInfo().getMaximumZoomLevel() - 4);
        map.setAddressLocation(new GeoPosition(51.5, 0));
    }

    private void setOtherMaps() {
        // http://khm2.google.com/kh?v=33g&x=1042&y=686&z=11&s=Gali
        final int max = 17;
        TileFactoryInfo info = new TileFactoryInfo(1, max - 2, max, 256, true,
                true, // tile size is 256 and x/y orientation is
                // normal
                // "http://mt2.google.com/mt?n=404&v=w2.21",//5/15/10.png",
                "http://khm2.google.com/kh?v=33", "x", "y", "z") {
            public String getTileUrl(int x, int y, int zoom) {
                zoom = max - zoom;
                String url = this.baseURL + "&x=" + x + "&y=" + y + "&z="
                        + zoom;
                return url;
            }

        };
        TileFactory tf = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tf);

    }

    private class myMapKit extends JXMapKit {

    }

    private class MyWaypointPainter<T extends JXMapViewer> extends
            WaypointPainter<JXMapViewer> {
        private JXMapViewer map = mapViewer;

        private List<List<GPSRecord>> tracks;
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
            if (tracks != null) {
                // Paint tracks
                for (List<GPSRecord> track : tracks) {
                    trackRenderer.paintTrack(g, map, track);
                }
            }

            // Paint waypoints
            super.doPaint(g, map, width, height);
        }

        public void setTracks(List<List<GPSRecord>> trks) {
            tracks = trks;
        }

        /**
         * @return the tracks
         */
        public final List<List<GPSRecord>> getTracks() {
            return this.tracks;
        }

        public void addTrack(List<GPSRecord> track) {
            tracks.add(track);
        }

        public void toggleSelected(Waypoint w) {
            WayPointRendererFactoryMethod f = WayPointRendererFactoryMethod
                    .getInstance();
            f.toggleSelected(w);
            repaint();
        }

        public Waypoint getContains(Point pt) {

            // figure out which waypoints are within this map viewport
            // so, get the bounds
            Rectangle viewportBounds = map.getViewportBounds();
            int zoom = map.getZoom();
            Dimension sizeInTiles = map.getTileFactory().getMapSize(zoom);
            int tileSize = map.getTileFactory().getTileSize(zoom);
            Dimension sizeInPixels = new Dimension(
                    sizeInTiles.width * tileSize, sizeInTiles.height * tileSize);

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
            Rectangle2D vp2 = new Rectangle2D.Double(vpx,
                    viewportBounds.getY(), viewportBounds.getWidth(),
                    viewportBounds.getHeight());
            Rectangle2D vp3 = new Rectangle2D.Double(vpx
                    - sizeInPixels.getWidth(), viewportBounds.getY(),
                    viewportBounds.getWidth(), viewportBounds.getHeight());

            WayPointRendererFactoryMethod factory = WayPointRendererFactoryMethod
                    .getInstance();
            // for each waypoint within these bounds
            for (Waypoint w : (Set<Waypoint>) getWaypoints()) {
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
         *            the trackRenderer to set
         */
        public final void setTrackRenderer(BT747TrackRenderer trackRenderer) {
            this.trackRenderer = trackRenderer;
        }
    }

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
                        .convertPointToGeoPosition(new Point2D.Double(pt.getX()
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

    // From
    // http://today.java.net/pub/a/today/2007/11/13/mapping-mashups-with-jxmapviewer.html
    // final JLabel hoverLabel = new JLabel("Java");
    // hoverLabel.setVisible(false);
    // jXMapKit1.getMainMap().add(hoverLabel);
    //
    // jXMapKit1.getMainMap().addMouseMotionListener(new MouseMotionListener() {
    // public void mouseDragged(MouseEvent e) { }
    //
    // public void mouseMoved(MouseEvent e) {
    // JXMapViewer map = jXMapKit1.getMainMap();
    // //location of Java
    // GeoPosition gp = new GeoPosition(-7.502778, 111.263056);
    // //convert to world bitmap
    // Point2D gp_pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
    // //convert to screen
    // Rectangle rect = map.getViewportBounds();
    // Point converted_gp_pt = new Point((int)gp_pt.getX()-rect.x,
    // (int)gp_pt.getY()-rect.y);
    // //check if near the mouse
    // if(converted_gp_pt.distance(e.getPoint()) < 10) {
    // hoverLabel.setLocation(converted_gp_pt);
    // hoverLabel.setVisible(true);
    // } else {
    // hoverLabel.setVisible(false);
    // }
    // }
    // });

    // @Override protected Set<WikiWaypoint> doInBackground() {
    // try {
    // // example: http://ws.geonames.org/wikipediaSearch?q=london&maxRows=10
    // URL url = new URL("http://ws.geonames.org/wikipediaSearch?q="+
    // jTextField1.getText()+"&maxRows=10");
    //
    // XPath xpath = XPathFactory.newInstance().newXPath();
    // NodeList list = (NodeList) xpath.evaluate("//entry",
    // new InputSource(url.openStream()),
    // XPathConstants.NODESET);
    //
    // Set<WikiWaypoint> waypoints = new
    // HashSet<WikiMashupView.WikiWaypoint>();
    // for(int i = 0; i < list.getLength(); i++) {
    // Node node = list.item(i);
    // String title = (String) xpath.evaluate("title/text()",
    // node, XPathConstants.STRING);
    // Double lat = (Double) xpath.evaluate("lat/text()",
    // node, XPathConstants.NUMBER);
    // Double lon = (Double) xpath.evaluate("lng/text()",
    // node, XPathConstants.NUMBER);
    // waypoints.add(new WikiWaypoint(lat, lon, title));
    // }
    // return waypoints; // return your result
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // return null;
    // }
    // }

    // @Override protected void succeeded(Set<WikiWaypoint> waypoints) {
    // // move to the center
    // jXMapKit1.setAddressLocation(waypoints.iterator().next().getPosition());
    //      
    // WaypointPainter painter = new WaypointPainter();
    //      
    // //set the waypoints
    // painter.setWaypoints(waypoints);
    //      
    // //create a renderer
    // painter.setRenderer(new WaypointRenderer() {
    // public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint wp)
    // {
    // WikiWaypoint wwp = (WikiMashupView.WikiWaypoint) wp;
    //              
    // //draw tab
    // g.setPaint(new Color(0,0,255,200));
    // Polygon triangle = new Polygon();
    // triangle.addPoint(0,0);
    // triangle.addPoint(11,11);
    // triangle.addPoint(-11,11);
    // g.fill(triangle);
    // int width = (int) g.getFontMetrics().getStringBounds(wwp.getTitle(),
    // g).getWidth();
    // g.fillRoundRect(-width/2 -5, 10, width+10, 20, 10, 10);
    //              
    // //draw text w/ shadow
    // g.setPaint(Color.BLACK);
    // g.drawString(wwp.getTitle(), -width/2-1, 26-1); //shadow
    // g.drawString(wwp.getTitle(), -width/2-1, 26-1); //shadow
    // g.setPaint(Color.WHITE);
    // g.drawString(wwp.getTitle(), -width/2, 26); //text
    // return false;
    // }
    // });
    // jXMapKit1.getMainMap().setOverlayPainter(painter);
    // jXMapKit1.getMainMap().repaint();
    // }
    //

}
