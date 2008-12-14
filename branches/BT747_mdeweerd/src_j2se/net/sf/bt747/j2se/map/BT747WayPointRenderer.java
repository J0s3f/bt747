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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

/**
 * @author Mario
 * 
 */
public class BT747WayPointRenderer implements WaypointRenderer {

    private final static float scale = 8f;
    private final static GeneralPath gp = new GeneralPath();
    private final static BT747WayPointRenderer instance = new BT747WayPointRenderer();

    public static BT747WayPointRenderer getInstance() {
        return instance;
    }

    static {
        Line2D l1 = new Line2D.Float(0.f, 0.f, -1 * scale, -2 * scale);
        gp.append(l1, true);
        final float diam = 2 * scale;
        Arc2D cc = new Arc2D.Float(-scale, -2 * scale - (diam / 2), diam, diam,
                180.f, -180.f, Arc2D.OPEN);
        gp.append(cc, true);
        Line2D l2 = new Line2D.Float(100.0f, 100.0f, 0.0f, 0.0f);
        gp.closePath();
    }

    public BT747WayPointRenderer() {
        // super(); // Gets image
    }

    private Color color = new Color(255, 0, 0, 125);

    /**
     * {@inheritDoc}
     * 
     * @param g
     * @param map
     * @param waypoint
     * @return
     */
    // @Override
    public boolean paintWaypoint(Graphics2D g, JXMapViewer map,
            Waypoint waypoint) {
        g.setColor(color);
        g.draw(gp);
        g.fill(gp);
        g.drawLine(-10, 0, 10, 0);
        g.drawLine(0, -10, 0, 10);

        try {
            if (((WaypointAdapter) waypoint).isSelected()) {
                paintWaypointSummary(g, map, (WaypointAdapter)waypoint);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }

    /**
     * At the position of the waypoint.
     * 
     * @param g
     * @param map
     * @param waypoint
     * @return
     */
    public boolean paintInfoWindow(Graphics2D g, JXMapViewer map,
            Waypoint waypoint) {
        return false;
    }

    /**
     * @param pt
     *            relative to position of waypoint.
     * @return
     */
    public boolean contains(Point pt) {
        return gp.contains(pt);
    }

    // private static Rectangle getSummaryBounds(JXMapViewer map,
    // Trip.Waypoint waypoint) {
    // // Point2D center =
    // // GoogleUtil.getBitmapCoordinate(waypoint.getPosition(),
    // // map.getZoom());
    // Point2D center = map.getTileFactory().getBitmapCoordinate(
    // waypoint.getPosition(), map.getZoom());
    // Rectangle bounds = map.getViewportBounds();
    // int x = (int) (center.getX() - bounds.getX());
    // int y = (int) (center.getY() - bounds.getY());
    // return new Rectangle(x - 55, y + 20, 170, 90);
    // }

    private static void trimAndPaint(Graphics2D g, String title, int length,
            int x, int y) {
        if (title != null) {
            if (title.length() > length) {
                title = title.substring(0, length) + "...";
            }
            g.drawString(title, x, y);
        }
    }

    protected void paintWaypointSummary(Graphics2D g, JXMapViewer map,
            WaypointAdapter waypoint) {
        Composite old_comp = g.getComposite();
        g.setComposite(AlphaComposite
                .getInstance(AlphaComposite.SRC_OVER, 0.75f));
        //g.addRenderingHints(hints)
        g.setColor(Color.GRAY);
        g.fillRoundRect(1, 1, 150, 30, 10, 10);
        // ap.paintBackground(g, dummy);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        trimAndPaint(g, "" + waypoint.getDescription(), 18, 10, 20);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
        // trimAndPaint(g, waypoint.getTitle(), 23, 10, 36);
        g.setStroke(new BasicStroke(1f));

        // if (waypoint.getPhotoCount() > 0) {
        // try {
        // BufferedImage image = waypoint.getPhoto(0).getImage();
        // if (image != null) {
        // image = GraphicsUtil.createThumbnail(image, 150);
        // CroppedImageIcon icon = new CroppedImageIcon(image, 37);
        // icon.paintIcon(null, g, 10, 43);
        // }
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
        // }

        g.setComposite(old_comp);
        // /g.translate(-summaryBounds.x, -summaryBounds.y);
    }
}
