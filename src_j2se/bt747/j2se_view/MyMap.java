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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import javax.swing.JPanel;

import net.sf.bt747.j2se.map.BT747WayPointRenderer;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

/**
 * @author Mario
 * 
 */
public class MyMap extends JPanel implements MapViewerInterface {
    JXMapKit map;
    JXMapViewer mapViewer;
    MyWaypointPainter<JXMapViewer> waypointPainter;

    /**
     * 
     */
    public MyMap() {
        // TODO Auto-generated constructor stub
        map = new JXMapKit();
        map.setMiniMapVisible(true);
        map.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
        mapViewer = map.getMainMap();
        waypointPainter = new MyWaypointPainter<JXMapViewer>();
        waypointPainter.setRenderer(new BT747WayPointRenderer());
        mapViewer.setOverlayPainter(waypointPainter);

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

        // myPositions.add(new GeoPosition(41.881944, 2.5));
        // myWaypoints.add(new Waypoint(41.88, 2.5));
        // myWaypoints.add(new Waypoint(42.88, 2.56));
        // setWayPoints(myWaypoints);
        // map.setAddressLocation(new GeoPosition(41.881944,2.5));
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.j2se_view.MapViewerInterface#setWayPoints(gps.log.GPSRecord[])
     */
    public void setWayPoints(GPSRecord[] records) {
        java.util.Set<Waypoint> positions = new java.util.HashSet<Waypoint>();
        java.util.Set<GeoPosition> myPositions = new java.util.HashSet<GeoPosition>();

        for (int i = 0; i < records.length; i++) {
            GPSRecord r = records[i];
            if (r.hasLatitude() && r.hasLongitude()) {
                Waypoint w = new Waypoint(r.latitude, r.longitude);
                positions.add(w);
                myPositions.add(w.getPosition());
            }
        }
        mapViewer.calculateZoomFrom(myPositions);
        // Iterator<Waypoint> iter = positions.iterator();
        // while(iter.hasNext()) {
        waypointPainter.setWaypoints(positions);
        // setGoogleMaps();
        waypointPainter.setRenderer(new BT747WayPointRenderer());

        mouseListener ml = new mouseListener();
        mapViewer.addMouseListener(ml);
        mapViewer.addMouseMotionListener(ml);

        // To get position from pixel position do tilefactory.pixelToGeo
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
        private BT747WayPointRenderer renderer = new BT747WayPointRenderer();
        private JXMapViewer map = mapViewer;

        public Waypoint getContains(Point pt) {
            if (renderer == null) {
                return null;
            }

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
                if (renderer.contains(p)) {
                    return w;
                }
            }
            return null;
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
            Point pt = e.getPoint();

            Waypoint w = waypointPainter.getContains(pt);
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
            // TODO Auto-generated method stub
            Point pt = e.getPoint();

            Waypoint w = waypointPainter.getContains(pt);

            if (w != null) {
                currentWaypoint = w;
                previous = pt;
                Point2D p = mapViewer.convertGeoPositionToPoint(
                        w.getPosition());
                xOffset = (int) (pt.getX() - p.getX());
                yOffset = (int) (pt.getY() - p.getY());
                mapViewer.setPanEnabled(false);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e) {
            if (currentWaypoint != null) {
                mapViewer.setPanEnabled(true);
                currentWaypoint = null;
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
                currentWaypoint.setPosition(mapViewer.convertPointToGeoPosition(
                                new Point2D.Double(pt.getX() - xOffset, pt
                                        .getY()
                                        - yOffset)));
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

}
