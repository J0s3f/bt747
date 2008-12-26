/**
 * 
 */
package net.sf.bt747.j2se.map;

import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.WaypointRenderer;

/**
 * @author Mario
 * 
 */
public interface BT747WaypointRenderer extends WaypointRenderer {
    /**
     * Returns true if this renderer knows how to render the object.
     * 
     * @param o
     * @return
     */
    public boolean isRendererOf(Object o);

    /**
     * Returns true if point (relative to waypoint position) is in waypoint
     * rendering (for waypoint selection on mouse operation).
     * 
     * @param p
     * @return
     */
    public boolean contains(java.awt.Point p);
}
