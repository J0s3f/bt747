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
package net.sf.bt747.j2se.map;

import gps.log.GPSRecord;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Set;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.Waypoint;

/**
 * @author Mario
 * 
 */
public class BT747TrackRenderer implements TrackRenderer {

    private final static GeneralPath gp = new GeneralPath();
    private final static BT747TrackRenderer instance = new BT747TrackRenderer();

    public static BT747TrackRenderer getInstance() {
        return instance;
    }

    public BT747TrackRenderer() {
        // super(); // Gets image
    }

    private Color color = new Color(0, 0, 255, 125);

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.map.TrackRenderer#paintWaypoint(java.awt.Graphics2D,
     *      org.jdesktop.swingx.JXMapViewer, java.util.Set)
     */
    public boolean paintTrack(Graphics2D g, JXMapViewer map,
            Collection<GPSRecord> track) {
        if (track.size() > 0) {
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

            TileFactory tf = map.getTileFactory();
            Stroke org = g.getStroke();
            g.setStroke(new BasicStroke(1.5f));
            g.setPaint(Color.BLUE);
            GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO, track
                    .size());
            Point2D prev = null;
            boolean first = true;
            float x = 0f, y = 0f;


            for (GPSRecord tp : track) {
                Point2D point = map.getTileFactory().geoToPixel(
                        new GeoPosition(tp.latitude, tp.longitude),
                        map.getZoom());
                boolean show = true;
                if (vp2.contains(point)) {
                    if(prev!=null) {
                        x = (float) (prev.getX() - vp2.getX());
                        y = (float) (prev.getY() - vp2.getY());
                        if (first) {
                            gp.moveTo(x, y);
                            first = false;
                        } else {
                            gp.lineTo(x, y);
                        }
                    }
                    x = (float) (point.getX() - vp2.getX());
                    y = (float) (point.getY() - vp2.getY());
                    
                } else if (vp3.contains(point)) {
                    if(prev!=null) {
                        x = (float) (prev.getX() - vp3.getX());
                        y = (float) (prev.getY() - vp3.getY());
                        if (first) {
                            gp.moveTo(x, y);
                            first = false;
                        } else {
                            gp.lineTo(x, y);
                        }
                    }
                    x = (float) (point.getX() - vp3.getX());
                    y = (float) (point.getY() - vp3.getY());
                } else {
                    show = false;
                    if(prev==null) {
                        g.draw(gp);
                        gp = new GeneralPath(GeneralPath.WIND_NON_ZERO, track
                            .size());
                        first = true;
                    }
                    prev = point;
                }
                if (show) {
                    prev = null;
                    if (first) {
                        gp.moveTo(x, y);
                        first = false;
                    } else {
                        gp.lineTo(x, y);
                    }
                }
            }

            g.draw(gp);
            g.setStroke(org);
        }
        return false;
    }
}
