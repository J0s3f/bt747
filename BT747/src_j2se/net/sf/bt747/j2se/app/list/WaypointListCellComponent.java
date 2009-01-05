/**
 * 
 */
package net.sf.bt747.j2se.app.list;

import java.awt.Component;

import javax.swing.JList;


/**
 * @author Mario
 *
 */
public interface WaypointListCellComponent {
    boolean isRendererOf(Object wp);
    Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus);    
}
