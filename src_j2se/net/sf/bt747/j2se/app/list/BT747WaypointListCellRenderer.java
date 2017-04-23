/**
 * 
 */
package net.sf.bt747.j2se.app.list;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;

import bt747.j2se_view.model.MapWaypoint;
import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public class BT747WaypointListCellRenderer implements
        javax.swing.ListCellRenderer {

    private static final Hashtable<Class<?>, WaypointListCellComponent> renderHash = new Hashtable<Class<?>, WaypointListCellComponent>();
    private static final java.util.Vector<WaypointListCellComponent> renderers = new Vector<WaypointListCellComponent>();

    static {
        // Last one added has priority.
        renderers.add(new FileListCellRenderer());
        renderers.add(new ImageListCellRenderer());
    }

    public final WaypointListCellComponent getRenderer(final Object waypoint) {
        if (waypoint instanceof MapWaypoint) {
            final MapWaypoint wpt = (MapWaypoint) waypoint;
            final WaypointListCellComponent renderer = renderHash.get(wpt
                    .getClass());
            if (renderer == null) {
                for (int i = renderers.size() - 1; i >= 0; i--) {
                    final WaypointListCellComponent r = renderers.get(i);
                    if (r.isRendererOf(waypoint)) {
                        renderHash.put(wpt.getClass(), r);
                        return r;
                    }
                }
            }
            return renderer;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax
     * .swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(final JList list,
            final Object value, final int index, final boolean isSelected,
            final boolean cellHasFocus) {

        try {
            final WaypointListCellComponent c = getRenderer(value);
            return c.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
        } catch (final Exception e) {
            Generic.debug("ListCellRender " + value, e);
        }
        final JLabel lb = new JLabel();
        lb.setText(value.toString());
        return lb;
    }
}
