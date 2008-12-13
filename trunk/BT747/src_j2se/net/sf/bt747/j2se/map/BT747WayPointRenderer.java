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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import org.jdesktop.swingx.JXMapViewer;
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
}
