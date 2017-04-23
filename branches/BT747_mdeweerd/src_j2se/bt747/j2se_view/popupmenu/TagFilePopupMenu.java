/**
 * 
 */
package bt747.j2se_view.popupmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import bt747.j2se_view.J2SEAppController;
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

    @SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	private JComponent pn;

	private JPopupMenu popupMenu = new JPopupMenu();

	private JTable table = null;
	private JList list = null;

	private POIActionHandler poiActionHandler;

	protected static final String INSERT_CMD = "Insert Rows";

	protected static final String DELETE_CMD = "DeleteSelectedRows";

	protected static final String REMOVE_CMD = "RemoveSelectedItems";

	protected static final String ADD_POSITION_CMD = "AddPosition";

	protected static final String COMMIT_TO_DISK_CMD = "CommitToDisk";

	public final static String getString(final String s) {
		return J2SEAppController.getString(s);
	}

	public TagFilePopupMenu(final JComponent pn, final JTable tbl,
			final POIActionHandler poiActionHandler) {
		this.pn = pn;
		this.poiActionHandler = poiActionHandler;
		table = tbl;// new JTable(new FilesPanel());
		JMenuItem menuItem;

		// JMenuItem menuItem = new JMenuItem(getString(INSERT_CMD));
		// menuItem.addActionListener(new ActionAdapter(this));
		// popupMenu.add(menuItem);
		menuItem = new JMenuItem(getString(REMOVE_CMD));
		menuItem.addActionListener(new ActionAdapter(this));
		popupMenu.add(menuItem);

		popupMenu.setOpaque(true);
		MouseListener popupListener = new PopupListener();
		table.addMouseListener(popupListener);
		table.getTableHeader().addMouseListener(popupListener);
		// table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	}

	public TagFilePopupMenu(final JComponent pn, final JList list,
			final POIActionHandler poiActionHandler) {
		this.pn = pn;
		this.poiActionHandler = poiActionHandler;
		this.list = list;// new JTable(new FilesPanel());
		JMenuItem menuItem;

		// JMenuItem menuItem = new JMenuItem(getString(INSERT_CMD));
		// menuItem.addActionListener(new ActionAdapter(this));
		// popupMenu.add(menuItem);
		menuItem = new JMenuItem(getString(DELETE_CMD));
		menuItem.addActionListener(new ActionAdapter(this));
		popupMenu.add(menuItem);
		menuItem = new JMenuItem(getString(ADD_POSITION_CMD));
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
			synchronized (new_name) {
				new_name.removeRows(table.getSelectedRows());
			}

		} else if (list != null
				&& list.getModel() instanceof UserWayPointListModel) {
			final UserWayPointListModel wptList = (UserWayPointListModel) list
					.getModel();
			wptList.remove(list.getSelectedValuesList().toArray());
		}
		// table.removeRowSelectionInterval(, index1)
		// JOptionPane.showMessageDialog(pn, "Delete Column here.");
		// delete column here
	}

	public void addPosition(ActionEvent e) {
		if (poiActionHandler == null)
			return;
		if (table != null && table.getModel() instanceof FileTableModel) {
			final FileTableModel new_name = (FileTableModel) table.getModel();
			synchronized (new_name) {
				int[] list = table.getSelectedRows();
				for (int idx : list) {
					poiActionHandler.addPosition(new_name.getElementAt(idx));
				}
				new_name.fireTableDataChanged();
			}
		} else if (list != null
				&& list.getModel() instanceof UserWayPointListModel) {
			final UserWayPointListModel wptList = (UserWayPointListModel) list
					.getModel();
			synchronized (wptList) {
				int[] lst =  list.getSelectedIndices();
				for (int idx : lst) {
					poiActionHandler.addPosition(wptList.getElementAt(idx));
					wptList.fireChange(idx);
				}
			}
		}
	}

	private class PopupListener extends MouseAdapter {
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
			} else if (item.getText().equals(getString(DELETE_CMD))) {
				adapter.deleteSelectedRows(e);

			} else if (item.getText().equals(getString(ADD_POSITION_CMD))) {
				adapter.addPosition(e);
			}
		}
	}

} // end of class

