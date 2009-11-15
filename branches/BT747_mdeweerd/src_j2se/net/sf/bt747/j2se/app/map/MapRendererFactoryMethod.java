/**
 * 
 */
package net.sf.bt747.j2se.app.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Hashtable;
import java.util.Vector;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

import bt747.j2se_view.model.MapWaypoint;

/**
 * @author Mario
 * 
 */
public class MapRendererFactoryMethod implements WaypointRenderer {

    private static MapRendererFactoryMethod factory = new MapRendererFactoryMethod();
    // private static final BT747WaypointRenderer defaultRenderer = new
    // DefaultBT747WaypointRenderer();
    // private static class DefaultBT747WaypointRenderer extends
    // DefaultWaypointRenderer implements BT747WaypointRenderer {
    // /* (non-Javadoc)
    // * @see
    // bt747.j2se_view.model.BT747WaypointRenderer#isRendererOf(java.lang.Object)
    // */
    // public boolean isRendererOf(Object o) {
    // // Renders every waypoint
    // return true;
    // }
    //        
    // }

    private static final Hashtable<Class<?>, BT747WaypointRenderer> renderHash = new Hashtable<Class<?>, BT747WaypointRenderer>();
    private static final java.util.Vector<BT747WaypointRenderer> renderers = new Vector<BT747WaypointRenderer>();

    static {
        // renderers.add(defaultRenderer);
        renderers.add(new BT747MapWayPointRenderer());
        renderers.add(new IconWayPointRenderer());
    }

    final static public MapRendererFactoryMethod getInstance() {
        return factory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.mapviewer.WaypointRenderer#paintWaypoint(java.awt.Graphics2D,
     *      org.jdesktop.swingx.JXMapViewer,
     *      org.jdesktop.swingx.mapviewer.Waypoint)
     */
    public final boolean paintWaypoint(final Graphics2D g,
            final JXMapViewer map, final Waypoint waypoint) {

        return getRenderer(waypoint).paintWaypoint(g, map, waypoint);
    }

    public final BT747WaypointRenderer getRenderer(final Waypoint waypoint) {
        BT747WaypointRenderer renderer;
        renderer = renderHash.get(waypoint.getClass());
        if (renderer == null) {
            for (int i = renderers.size() - 1; i >= 0; i--) {
                final BT747WaypointRenderer r = renderers.get(i);
                if (r.isRendererOf(waypoint)) {
                    renderHash.put(waypoint.getClass(), r);
                    return r;
                }
            }
        }
        return renderer;
    }

    public final boolean rendererContains(final Waypoint waypoint,
            final Point pt) {
        return getRenderer(waypoint).contains(pt);
    }

    public final void toggleSelected(final Waypoint waypoint) {
        if (waypoint instanceof MapWaypoint) {
            ((MapWaypoint) (waypoint)).toggleShowTag();
        }
    }
}
