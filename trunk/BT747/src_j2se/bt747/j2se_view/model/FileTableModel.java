/**
 * 
 */
package bt747.j2se_view.model;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import bt747.j2se_view.model.PositionData.UserWayPointListModel;

/**
 * @author Mario
 * 
 */
@SuppressWarnings("serial")
public class FileTableModel extends AbstractTableModel {

    private UserWayPointListModel wpListModel;

    /**
     * The columns currently shown.
     */
    private int[] columns = { PositionData.FILE_DATE, PositionData.FILE_TIME,
            PositionData.GPS_TIME, //PositionData.TAG_TIME,
            PositionData.IMAGE_PATH, PositionData.GEOMETRY,
            PositionData.LATITUDE, PositionData.LONGITUDE };

    /**
     * 
     */
    public FileTableModel(UserWayPointListModel m) {
        wpListModel = m;
        wpListModel.addListDataListener(new WPListDataListener());
    }

    private final class WPListDataListener implements ListDataListener {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
         */
        public void contentsChanged(ListDataEvent e) {
            fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
         */
        public void intervalAdded(ListDataEvent e) {
            fireTableRowsInserted(e.getIndex0(), e.getIndex1());
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
         */
        public void intervalRemoved(ListDataEvent e) {
            fireTableRowsDeleted(e.getIndex0(), e.getIndex1());

        }

    }

    public void add(String path) {
        wpListModel.add(path);
    }

    /**
     * 
     */
    public void clear() {
        wpListModel.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int columnIndex) {
        return PositionData
                .getDataDisplayClass(columnToDataType(columnIndex));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columns.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        return PositionData.getDataDisplayName(columnToDataType(columnIndex));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return wpListModel.getSize();
    }

    private int columnToDataType(final int column) {
        if (column < columns.length) {
            return columns[column];
        } else {
            return PositionData.NONE;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return PositionData.getData((ImageData) wpListModel
                .getElementAt(rowIndex), columnToDataType(columnIndex));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
     *      int)
     */
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub

    }

}
