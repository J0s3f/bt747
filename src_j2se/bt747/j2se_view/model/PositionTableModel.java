// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package bt747.j2se_view.model;

import gps.log.GPSRecord;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * @author Mario De Weerd
 * 
 */
@SuppressWarnings("serial")
public class PositionTableModel extends AbstractTableModel {

    private int[] columnTypes = {
            PositionData.RECORDNUMBER,
            PositionData.GPS_DATE,
            PositionData.GPS_TIME,
            // PositionData.FMT_UTC_IDX,
            // FMT_UTC_VALUE,
            PositionData.RCR, PositionData.RCR_DESCRIPTION,
            PositionData.FIX_VALID, PositionData.LATITUDE,
            PositionData.LONGITUDE, PositionData.POSITION_HEIGHT,
            PositionData.HDOP, PositionData.PDOP,
            PositionData.VOX };

    private volatile List<List<GPSRecord>> gpsData;

    /**
     * @return the gpsData
     */
    public final List<List<GPSRecord>> getGpsData() {
        return gpsData;
    }

    /**
     * @param gpsData
     *                the gpsData to set
     */
    public final void setGpsData(final GPSRecord[] gpsdata) {
        gpsData = null;
        final Vector<List<GPSRecord>> tmp = new Vector<List<GPSRecord>>(1);
        final Vector<GPSRecord> tmpList = new Vector<GPSRecord>(
                gpsdata.length);
        for (final GPSRecord g : gpsdata) {
            tmpList.add(g);
        }
        tmp.add(tmpList);
        gpsData = tmp;
        fireTableDataChanged();
    }

    /**
     * @param gpsData
     *                the gpsData to set
     */
    public final void setGpsData(final List<List<GPSRecord>> gpsdata) {
        gpsData = gpsdata;
        fireTableDataChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columnTypes.length;
    }

    private int columnToType(final int index) {
        if (index < columnTypes.length) {
            return columnTypes[index];
        } else {
            return PositionData.NONE;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(final int columnIndex) {
        if (columnIndex < columnTypes.length) {
            return PositionData.getDataDisplayName(columnToType(columnIndex));
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if (gpsData != null) {
            int count = 0;
            for (final List<GPSRecord> g : gpsData) {
                count += g.size();
            }
            return count;
        } else {
            return 0;
        }
    }

    public GPSRecord getRecordValueAt(final int rowIndex) {
        if (gpsData != null) {
            int count = rowIndex;
            for (final List<GPSRecord> g : gpsData) {
                if (count < g.size()) {
                    return g.get(count);
                }
                count -= g.size();
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final GPSRecord g = getRecordValueAt(rowIndex);
        if (g != null) {
            return PositionData.getValue(g, columnToType(columnIndex));
        }
        return null;
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
        final GPSRecord g = getRecordValueAt(rowIndex);
        if (g != null) {
            PositionData.setValue(value, g, columnToType(columnIndex));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(final int columnIndex) {
        return PositionData.getDataDisplayClass(columnToType(columnIndex));
    }

}
