// ********************************************************************
// *** BT747 ***
// *** (c)2007-2008 Mario De Weerd ***
// *** seesite@bt747.org ***
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

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.Waypoint;

import bt747.j2se_view.model.MapWaypoint;

/**
 * @author Mario
 * 
 */
public class BT747MapWayPointRenderer implements BT747WaypointRenderer {

    private final static float scale = 8f;
    private final static GeneralPath gp = new GeneralPath();
    private final static BT747MapWayPointRenderer instance = new BT747MapWayPointRenderer();

    public static BT747MapWayPointRenderer getInstance() {
        return instance;
    }

    static {
        final Line2D l1 = new Line2D.Float(0.f, 0.f, -1 * scale, -2 * scale);
        gp.append(l1, true);
        final float diam = 2 * scale;
        final Arc2D cc = new Arc2D.Float(-scale, -2 * scale - (diam / 2),
                diam, diam, 180.f, -180.f, Arc2D.OPEN);
        gp.append(cc, true);
        // Line2D l2 = new Line2D.Float(100.0f, 100.0f, 0.0f, 0.0f);
        gp.closePath();
    }

    public BT747MapWayPointRenderer() {
        // super(); // Gets image
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.map.BT747WaypointRenderer#isRendererOf(java.lang.Object)
     */
    public boolean isRendererOf(final Object o) {
        return true || MapWaypoint.class.isInstance(o);
    }

    private Color color = new Color(255, 0, 0, 125);
    private Color selectedColor = new Color(0, 0, 255, 125);

    /**
     * {@inheritDoc}
     * 
     * @param g
     * @param map
     * @param waypoint
     * @return unknown meaning.
     */
    // @Override
    public boolean paintWaypoint(final Graphics2D g, final JXMapViewer map,
            final Waypoint waypoint) {
        try {
            g.setColor(color);
            if (waypoint instanceof MapWaypoint) {
                final MapWaypoint wp = (MapWaypoint) waypoint;
                if (wp.isSelected()) {
                    g.setColor(selectedColor);
                }
            }
            g.draw(gp);
            g.fill(gp);
            g.drawLine(-10, 0, 10, 0);
            g.drawLine(0, -10, 0, 10);

            if (((MapWaypoint) waypoint).isShowTag()) {
                paintWaypointSummary(g, map, (MapWaypoint) waypoint);
            }
        } catch (final Exception e) {
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
     * @return Unknown meaning.
     */
    public boolean paintInfoWindow(final Graphics2D g, final JXMapViewer map,
            final Waypoint waypoint) {
        return false;
    }


    /* (non-Javadoc)
     * @see net.sf.bt747.j2se.app.map.BT747WaypointRenderer#contains(java.awt.Point)
     */
    public boolean contains(final Point pt) {
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

    private static void trimAndPaint(final Graphics2D g, String title,
            final int length, final int x, final int y) {
        if (title != null) {
            if (title.length() > length) {
                title = title.substring(0, length) + "...";
            }
            g.drawString(title, x, y);
        }
    }

    protected void paintWaypointSummary(final Graphics2D g,
            final JXMapViewer map, final MapWaypoint waypoint) {
        final Composite old_comp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.75f));

        // WayPointPanel wp = new WayPointPanel();
        // Point2D pt =
        // map.getTileFactory().geoToPixel(waypoint.getPosition(),map.getZoom());
        // Point2D pt2 = map.getViewportBounds().getLocation();
        // wp.setBounds((int)(pt.getX()-pt2.getX()),(int)(pt.getY()-pt2.getX()),120,120);
        // //map.add(wp);
        // //wp.setLocation(120,120);
        // wp.paint(g);
        // map.remove(wp);
        // g.addRenderingHints(hints)
        g.setColor(Color.GRAY);
        // ap.paintBackground(g, dummy);

        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        final int width = (int) g.getFontMetrics().getStringBounds(
                waypoint.getDescription(), g).getWidth();
        g.fillRoundRect(1, 1, width + 20, 30, 10, 10);

        g.setColor(Color.WHITE);
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

        // WayPointPanel wp = new WayPointPanel();
        // JFrame fm = new JFrame();
        // fm.add(wp);
        // fm.pack();
        // fm.remove(wp);
        // wp.paint(g);

    }

    // http://today.java.net/pub/a/today/2007/11/13/mapping-mashups-with-jxmapviewer.html
    // public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint
    // wp)
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
    // }

}
