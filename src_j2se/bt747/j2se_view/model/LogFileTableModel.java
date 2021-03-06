/**
 * 
 */
package bt747.j2se_view.model;

import java.awt.FontMetrics;
import java.util.HashSet;

import gps.log.LogFileInfo;
import gps.log.out.CommonOut;

import javax.swing.table.AbstractTableModel;

import bt747.model.Model;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Vector;

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
            DataTypes.LOG_START_DATETIME, DataTypes.LOG_END_DATETIME };

    /**
     * 
     */
    public LogFileTableModel() {
    }

    public final BT747Vector getLogfileInfos() {
        return Model.logFiles;
    }
    
    public void removeRows(int[] indexes) {
        final HashSet<Object> set = new HashSet<Object>(indexes.length);
        final BT747Vector objects = getLogfileInfos();
        for(int i=0;i<indexes.length;i++) {
            set.add(objects.elementAt(indexes[i]));
        }
        for(int i=objects.size()-1;i>=0;i--) {
            if(set.contains(objects.elementAt(i))) {
                objects.removeElementAt(i);
            }
        }
        notifyUpdate();
    }


    public final void setLogfileInfos(
            bt747.sys.interfaces.BT747Vector logfileInfos) {
        // this.logfileInfos = logfileInfos;
        fireTableStructureChanged();
        fireTableDataChanged();
    }

    public void add(final String path) {
        getLogfileInfos().addElement(new LogFileInfo(new BT747Path(path)));
    }

    public void notifyUpdate() {
        // bt747.sys.Generic.debug("Notified logTableChange");
        fireTableDataChanged();
    }

    /**
     * 
     */
    public void clear() {
        getLogfileInfos().removeAllElements();
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
        // bt747.sys.Generic.debug("Row count "+getLogfileInfos().size());
        return getLogfileInfos().size();
    }

    private int columnToDataType(final int column) {
        if (column < columns.length) {
            return columns[column];
        } else {
            return DataTypes.NONE;
        }
    }

    private final Object getData(final Object o, final int dt) {
        // final LogFileInfo logfileinfo
        // bt747.sys.Generic.debug("GetData "+getLogfileInfos().size());
        if (o instanceof LogFileInfo) {
            LogFileInfo logfileinfo = (LogFileInfo) o;

            switch (dt) {
            case DataTypes.LOG_START_DATETIME:
                final int st = logfileinfo.getStartTime();
                if (st != 0x7FFFFFFF && st != 0) {
                    return CommonOut.getDateTimeStr(st);
                } else {
                    return null;
                }
            case DataTypes.LOG_END_DATETIME:
                final int et = logfileinfo.getEndTime();
                if (et != 0x7FFFFFFF && et != 0) {
                    return CommonOut.getDateTimeStr(et);
                } else {
                    return null;
                }
            case DataTypes.LOG_COLOR:
                // label = "TAB_TITLE_Color";
                return null;
            case DataTypes.LOG_FILENAME:
                return logfileinfo.getBT747Path();
            default:
                return null;
            }
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        return getData(getLogfileInfos().elementAt(rowIndex),
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

    public final int getPreferredWidth(final FontMetrics fm, final int columnIndex) {
        return DataTypes.defaultDataWidth(columnToDataType(columnIndex), fm);
    }

}
