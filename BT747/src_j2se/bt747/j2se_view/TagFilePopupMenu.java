/**
 * 
 */
package bt747.j2se_view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import bt747.j2se_view.model.FileTableModel;
import bt747.j2se_view.model.PositionData.UserWayPointListModel;

/**
 * @author Mario
 * 
 */
public class TagFilePopupMenu {

    /**
     * http://java.sun.com/docs/books/tutorial/uiswing/components/table.html
     * 
     */

    private static final long serialVersionUID = 1L;

    private JComponent pn;

    private JPopupMenu popupMenu = new JPopupMenu();

    private JTable table = null;
    private JList list = null;

    protected static final String INSERT_CMD = "Insert Rows";

    protected static final String DELETE_CMD = "DeleteSelectedRows";

    public final static String getString(final String s) {
        return J2SEAppController.getString(s);
    }

    public TagFilePopupMenu(final JComponent pn, final JTable tbl) {
        this.pn = pn;
        table = tbl;// new JTable(new FilesPanel());
        JMenuItem menuItem;

        // JMenuItem menuItem = new JMenuItem(getString(INSERT_CMD));
        // menuItem.addActionListener(new ActionAdapter(this));
        // popupMenu.add(menuItem);
        menuItem = new JMenuItem(getString(DELETE_CMD));
        menuItem.addActionListener(new ActionAdapter(this));
        popupMenu.add(menuItem);

        popupMenu.setOpaque(true);
        MouseListener popupListener = new PopupListener();
        table.addMouseListener(popupListener);
        table.getTableHeader().addMouseListener(popupListener);
        // table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }

    public TagFilePopupMenu(final JComponent pn, final JList list) {
        this.pn = pn;
        this.list = list;// new JTable(new FilesPanel());
        JMenuItem menuItem;

        // JMenuItem menuItem = new JMenuItem(getString(INSERT_CMD));
        // menuItem.addActionListener(new ActionAdapter(this));
        // popupMenu.add(menuItem);
        menuItem = new JMenuItem(getString(DELETE_CMD));
        menuItem.addActionListener(new ActionAdapter(this));
        popupMenu.add(menuItem);

        popupMenu.setOpaque(true);
        MouseListener popupListener = new PopupListener();
        list.addMouseListener(popupListener);
        // list.getTableHeader().addMouseListener(popupListener);
        // table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }

    public void insertColumn(ActionEvent e) {
        JOptionPane.showMessageDialog(pn, "Insert Column here.");
        // insert new column here
    }

    public void deleteSelectedRows(ActionEvent e) {
        if (table != null && table.getModel() instanceof FileTableModel) {
            final FileTableModel new_name = (FileTableModel) table.getModel();
            new_name.removeRows(table.getSelectedRows());
        } else if (list != null
                && list.getModel() instanceof UserWayPointListModel) {
            final UserWayPointListModel wptList = (UserWayPointListModel) list
                    .getModel();
            wptList.remove(list.getSelectedValues());
        }
        // table.removeRowSelectionInterval(, index1)
        // JOptionPane.showMessageDialog(pn, "Delete Column here.");
        // delete column here
    }

    class PopupListener implements MouseListener {
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    private static class ActionAdapter implements ActionListener {
        TagFilePopupMenu adapter;

        ActionAdapter(TagFilePopupMenu adapter) {
            this.adapter = adapter;
        }

        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            if (item.getText() == "Insert Rows") {
                adapter.insertColumn(e);
            } else if (item.getText() == getString(DELETE_CMD)) {
                adapter.deleteSelectedRows(e);
            }

        }
    }

} // end of class

