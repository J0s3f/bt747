/**
 * 
 */
package bt747.j2se_view;

import gps.log.GPSRecord;
import gps.log.out.CommonOut;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import net.sf.bt747.j2se.map.GPSRecordWaypointAdapter;

/**
 * @author Mario
 * 
 */
public class PictureListCellRenderer extends
        javax.swing.DefaultListCellRenderer {

    private final static Border empty = BorderFactory.createEmptyBorder(3, 3,
            5, 3);
    private final static Border selection = new CompoundBorder(BorderFactory
            .createEmptyBorder(0, 0, 2, 0), BorderFactory.createLineBorder(
            Color.WHITE, 3));

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList,
     *      java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        // Use a label augmented with an icon representing the picture.

        GPSRecord record = null;
        if (value != null) {
            GPSRecordWaypointAdapter wp = (GPSRecordWaypointAdapter) value;
            record = wp.getGpsRec();
        }
        JLabel label = new JLabel();
        label.setText("No picture for now.");
        label.setOpaque(false);

        // label.setIcon();

        label.setBackground(null);
        label.setBorder(isSelected ? selection : empty);
        label.setOpaque(false);
        if (record != null) {
            label.setToolTipText("<html>" + CommonOut.getHtml(record));
        }

        return label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#getMaximumSize()
     */
    @Override
    public Dimension getMaximumSize() {
        // TODO Auto-generated method stub
        return getPreferredSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize() {
        // TODO Auto-generated method stub
        return getPreferredSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        Insets insets = getInsets();
        if (getIcon() == null) {
            return new Dimension(50, 50);
        }
        return new Dimension(getIcon().getIconWidth() + insets.left
                + insets.right, getIcon().getIconHeight() + insets.top
                + insets.bottom);
    }

}
