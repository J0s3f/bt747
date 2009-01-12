// ********************************************************************
// *** BT747 ***
// *** (c)2007-2008 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
/**
 * This needs to be refactored!
 */
package net.sf.bt747.j2se.app.map;

import gps.log.GPSRecord;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * @author Mario
 * 
 */
public class BT747TrackRenderer implements TrackRenderer {

    //private final static GeneralPath gp = new GeneralPath();
    private final static BT747TrackRenderer instance = new BT747TrackRenderer();

    public static BT747TrackRenderer getInstance() {
        return instance;
    }

    public BT747TrackRenderer() {
        // super(); // Gets image
    }

    private Color color = Color.BLUE;

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.map.TrackRenderer#paintWaypoint(java.awt.Graphics2D,
     *      org.jdesktop.swingx.JXMapViewer, java.util.Set)
     */
    public boolean paintTrack(final Graphics2D g, final JXMapViewer map,
            final Collection<GPSRecord> track) {
        if (track.size() > 0) {
            final Rectangle viewportBounds = map.getViewportBounds();
            //final int zoom = map.getZoom();
            // Dimension sizeInTiles = map.getTileFactory().getMapSize(zoom);
            // int tileSize = map.getTileFactory().getTileSize(zoom);
            // Dimension sizeInPixels = new Dimension(sizeInTiles.width
            // * tileSize, sizeInTiles.height * tileSize);

            // Looking at one viewport only.

            final double vpx = viewportBounds.getX();
            // // normalize the left edge of the viewport to be positive
            // while (vpx < 0) {
            // vpx += sizeInPixels.getWidth();
            // }
            // // normalize the left edge of the viewport to no wrap around
            // the
            // // world
            // while (vpx > sizeInPixels.getWidth()) {
            // vpx -= sizeInPixels.getWidth();
            // }

            // create two new viewports next to eachother
            final Rectangle2D vp2 = new Rectangle2D.Double(vpx,
                    viewportBounds.getY(), viewportBounds.getWidth(),
                    viewportBounds.getHeight());
            // Rectangle2D vp3 = new Rectangle2D.Double(vpx
            // - sizeInPixels.getWidth(), viewportBounds.getY(),
            // viewportBounds.getWidth(), viewportBounds.getHeight());

            final Stroke org = g.getStroke();
            g.setStroke(new BasicStroke(1.5f));
            g.setPaint(color);
            GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO, track
                    .size());
            Point2D prev = null;
            boolean first = true;
            float x = 0f, y = 0f;

            boolean previousShown = false;
            double vpX = 0.0d;
            double vpY = 0.0d;
            for (final GPSRecord tp : track) {
                final Point2D point = map.getTileFactory().geoToPixel(
                        new GeoPosition(tp.latitude, tp.longitude),
                        map.getZoom());
                boolean show = true;
                // This has to be refactored.
                // if (vp3.contains(point)) {
                // vpX = vp3.getX();
                // vpY = vp3.getY();
                // Vp = 3;
                // } else
                if (vp2.contains(point)) {
                    vpX = vp2.getX();
                    vpY = vp2.getY();
                } else {
                    show = false;
                }

                // if (prevVp == 2 && Vp == 3) {
                // // This position is not shown - end path and start new.
                // if (prev == null) {
                // g.draw(gp);
                // gp = new GeneralPath(GeneralPath.WIND_NON_ZERO, track
                // .size());
                // first = true;
                // }
                // previousShown = false;
                // }

                if ((prev != null) && !previousShown && show) {
                    // There was a previous point that was not shown.
                    // Draw it to get a line from outside the viewport
                    x = (float) (prev.getX() - vpX);
                    y = (float) (prev.getY() - vpY);
                    if (first) {
                        gp.moveTo(x, y);
                        first = false;
                    } else {
                        gp.lineTo(x, y);
                    }
                }

                if (show) {
                    prev = null;
                }
                if (show || previousShown) {
                    prev = null;
                    x = (float) (point.getX() - vpX);
                    y = (float) (point.getY() - vpY);
                    if (first) {
                        gp.moveTo(x, y);
                        first = false;
                    } else {
                        gp.lineTo(x, y);
                    }
                }
                if (previousShown && !show) {
                    // This position is not shown - end path and start new.
                    if (prev == null) {
                        g.draw(gp);
                        gp = new GeneralPath(GeneralPath.WIND_NON_ZERO, track
                                .size());
                        first = true;
                    }
                }
                prev = point;
                previousShown = show;
            }

            g.draw(gp);
            g.setStroke(org);
        }
        return false;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
