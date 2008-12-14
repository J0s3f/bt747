/**
 * 
 */
package net.sf.bt747.j2se.map;

import java.awt.Graphics2D;
import java.awt.Point;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultWaypointRenderer;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

/**
 * @author Mario
 * 
 */
public class WayPointRendererFactoryMethod implements WaypointRenderer {

    private static WayPointRendererFactoryMethod factory = new WayPointRendererFactoryMethod();
    private static final WaypointRenderer defaultRenderer = new DefaultWaypointRenderer();

    final static public WayPointRendererFactoryMethod getInstance() {
        return factory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.WaypointRenderer#paintWaypoint(java.awt.Graphics2D,
     *      org.jdesktop.swingx.JXMapViewer,
     *      org.jdesktop.swingx.mapviewer.Waypoint)
     */
    public final boolean paintWaypoint(Graphics2D g, JXMapViewer map,
            Waypoint waypoint) {

        return getRenderer(waypoint).paintWaypoint(g, map, waypoint);
    }
    
    public final WaypointRenderer getRenderer(Waypoint waypoint) {
        WaypointRenderer renderer = null;
        try {
            renderer = ((WaypointAdapter) waypoint).getRenderer();
        } catch (ClassCastException e) {
            renderer = defaultRenderer;
        }
        return renderer;
    }
    
    public final boolean rendererContains(Waypoint waypoint, Point pt) {
        boolean contains;
        try {
            contains = ((BT747WayPointRenderer)getRenderer(waypoint)).contains(pt);
        } catch (ClassCastException e) {
            contains = false;
        }
        return contains; 
    }

    public final void toggleSelected(Waypoint waypoint) {
        try {
            ((WaypointAdapter)(waypoint)).toggleSelected();
        } catch (ClassCastException e) {
            
        }

    }
}
