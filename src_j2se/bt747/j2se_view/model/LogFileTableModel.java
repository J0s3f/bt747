/**
 * 
 */
package bt747.j2se_view.model;

import gps.log.LogFileInfo;
import gps.log.out.CommonOut;

import javax.swing.table.AbstractTableModel;

/**
 * @author Mario
 * 
 */
@SuppressWarnings("serial")
public class LogFileTableModel extends AbstractTableModel {

    /**
     * The columns currently shown.
     */
    private int[] columns = { DataTypes.LOG_FILENAME,
            DataTypes.LOG_START_TIME, DataTypes.LOG_END_TIME };

    private java.util.Vector<LogFileInfo> logfileInfos;

    /**
     * 
     */
    public LogFileTableModel(java.util.Vector<LogFileInfo> logfileinfos) {
        this.logfileInfos = logfileinfos;
    }

    public void add(final String path) {
        logfileInfos.add(new LogFileInfo(path, 0));
    }

    /**
     * 
     */
    public void clear() {
        logfileInfos.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(final int columnIndex) {
        return DataTypes.getDataDisplayClass(columnToDataType(columnIndex));
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
        return logfileInfos.size();
    }

    private int columnToDataType(final int column) {
        if (column < columns.length) {
            return columns[column];
        } else {
            return DataTypes.NONE;
        }
    }

    private final Object getData(final LogFileInfo logfileinfo, final int dt) {
        switch (dt) {
        case DataTypes.LOG_START_TIME:
            return CommonOut.getDateTimeStr(logfileinfo.getStartTime());
        case DataTypes.LOG_END_TIME:
            return CommonOut.getDateTimeStr(logfileinfo.getStartTime());
        case DataTypes.LOG_COLOR:
            // label = "TAB_TITLE_Color";
            return null;
        case DataTypes.LOG_FILENAME:
            return logfileinfo.getPath();
        default:
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        return getData(logfileInfos.get(rowIndex),
                columnToDataType(columnIndex));
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
