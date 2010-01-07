/**
 * 
 */
package net.sf.bt747.j2se.app.map;

import org.jdesktop.swingx.mapviewer.WaypointRenderer;

/**
 * @author Mario De Weerd
 * 
 */
public interface BT747WaypointRenderer extends WaypointRenderer {
    /**
     * Returns true if this renderer knows how to render the object.
     * 
     * @param o
     * @return true if this renderer can render the object.
     */
    public boolean isRendererOf(Object o);

    /**
     * Returns true if point (relative to waypoint position) is in waypoint
     * rendering (for waypoint selection on mouse operation).
     * 
     * @param p
     * @return true if the point (in GUI) is part of teh rendering.
     */
    public boolean contains(java.awt.Point p);
}
