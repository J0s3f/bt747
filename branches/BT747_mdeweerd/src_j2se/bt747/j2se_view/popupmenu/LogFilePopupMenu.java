/**
 * 
 */
package bt747.j2se_view.popupmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import bt747.j2se_view.J2SEAppController;
import bt747.j2se_view.model.LogFileTableModel;

/**
 * @author Mario
 * 
 */
public class LogFilePopupMenu {

    /**
     * http://java.sun.com/docs/books/tutorial/uiswing/components/table.html
     * 
     */

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private JPanel pn;

    private JPopupMenu popupMenu = new JPopupMenu();

    private JTable table;

    public static final String INSERT_CMD = "Insert Rows";

    public static final String DELETE_CMD = "DeleteSelectedRows";

    public final static String getString(final String s) {
        return J2SEAppController.getString(s);
    }

    public LogFilePopupMenu(final JPanel pn, final JTable tbl) {
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
        //table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }

    // private static void createAndShowGUI() {
    // // Create and set up the window.
    // JFrame frame = new JFrame("TableDemo");
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //
    // // Create and set up the content pane.
    // FilePopupMenu popUp = new FilePopupMenu();
    // popUp.setOpaque(true); // content panes must be opaque
    // frame.setContentPane(popUp);
    //
    // // Display the window.
    // frame.pack();
    // frame.setVisible(true);
    // }

    // public static void main(String[] args) {
    // // Schedule a job for the event-dispatching thread:
    // // creating and showing this application's GUI.
    // javax.swing.SwingUtilities.invokeLater(new Runnable() {
    // public void run() {
    // createAndShowGUI();
    // }
    // });
    // }

    public void insertColumn(ActionEvent e) {
        JOptionPane.showMessageDialog(pn, "Insert Column here.");
        // insert new column here
    }

    public void deleteSelectedRows(ActionEvent e) {
        if (table.getModel() instanceof LogFileTableModel) {
            LogFileTableModel new_name = (LogFileTableModel) table.getModel();
            new_name.removeRows(table.getSelectedRows());
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
        LogFilePopupMenu adapter;

        ActionAdapter(LogFilePopupMenu adapter) {
            this.adapter = adapter;
        }

        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();
            if (item.getText() == "Insert Rows") {
                adapter.insertColumn(e);
            } else if (item.getText().equals(getString(DELETE_CMD))) {
                adapter.deleteSelectedRows(e);
            }

        }
    }

} // end of class

