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
    private int[] columns = {
            DataTypes.FILE_DATE,
            DataTypes.FILE_TIME,
            DataTypes.GPS_TIME, // PositionData.TAG_TIME,
            DataTypes.IMAGE_PATH, DataTypes.GEOMETRY,
            DataTypes.LATITUDE, DataTypes.LONGITUDE,
            DataTypes.HEIGHT_METERS };

    /**
     * 
     */
    public FileTableModel(final UserWayPointListModel m) {
        wpListModel = m;
        wpListModel.addListDataListener(new WPListDataListener());
    }

    private final class WPListDataListener implements ListDataListener {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
         */
        public void contentsChanged(final ListDataEvent e) {
            fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
         */
        public void intervalAdded(final ListDataEvent e) {
            fireTableRowsInserted(e.getIndex0(), e.getIndex1());
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
         */
        public void intervalRemoved(final ListDataEvent e) {
            fireTableRowsDeleted(e.getIndex0(), e.getIndex1());

        }

    }

    public void add(final String path) {
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
    public Class<?> getColumnClass(final int columnIndex) {
        return DataTypes
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
    public String getColumnName(final int columnIndex) {
        return DataTypes.getDataDisplayName(columnToDataType(columnIndex));
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
            return DataTypes.NONE;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        return PositionData.getData((ImageData) wpListModel
                .getElementAt(rowIndex), columnToDataType(columnIndex));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
     *      int)
     */
    public void setValueAt(final Object value, final int rowIndex,
            final int columnIndex) {
        // TODO Auto-generated method stub

    }

}
