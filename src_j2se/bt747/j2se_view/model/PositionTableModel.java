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

import gps.BT747Constants;
import gps.log.GPSRecord;
import gps.log.out.CommonOut;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import bt747.sys.Generic;

/**
 * @author Mario De Weerd
 * 
 */
@SuppressWarnings("serial")
public class PositionTableModel extends AbstractTableModel {

    private int[] columnTypes = {
            PositionData.FMT_REC_NBR,
            PositionData.FMT_DATE,
            PositionData.FMT_TIME,
            // BT747Constants.FMT_UTC_IDX,
            // FMT_UTC_VALUE,
            BT747Constants.FMT_RCR_IDX, PositionData.FMT_RCR_DESCRIPTION,
            BT747Constants.FMT_VALID_IDX, BT747Constants.FMT_LATITUDE_IDX,
            BT747Constants.FMT_LONGITUDE_IDX, BT747Constants.FMT_HEIGHT_IDX,
            BT747Constants.FMT_HDOP_IDX, BT747Constants.FMT_PDOP_IDX,
            PositionData.FMT_VOX };

    private volatile List<List<GPSRecord>> gpsData;

    /**
     * @return the gpsData
     */
    public final List<List<GPSRecord>> getGpsData() {
        return this.gpsData;
    }

    /**
     * @param gpsData
     *                the gpsData to set
     */
    public final void setGpsData(final GPSRecord[] gpsdata) {
        gpsData = null;
        Vector<List<GPSRecord>> tmp = new Vector<List<GPSRecord>>(1);
        Vector<GPSRecord> tmpList = new Vector<GPSRecord>(gpsdata.length);
        for (GPSRecord g : gpsdata) {
            tmpList.add(g);
        }
        tmp.add(tmpList);
        gpsData = tmp;
        fireTableStructureChanged();
    }

    /**
     * @param gpsData
     *                the gpsData to set
     */
    public final void setGpsData(final List<List<GPSRecord>> gpsdata) {
        gpsData = gpsdata;
        fireTableStructureChanged();
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
            return PositionData.FMT_NO_FIELD;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        if (columnIndex < columnTypes.length) {
            return PositionData
                    .getDataDescriptionTitle(columnToType(columnIndex));
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
            for (List<GPSRecord> g : gpsData) {
                count += g.size();
            }
            return count;
        } else {
            return 0;
        }
    }

    public GPSRecord getRecordValueAt(int rowIndex) {
        if (gpsData != null) {
            int count = rowIndex;
            for (List<GPSRecord> g : gpsData) {
                if (count < g.size()) {
                    return g.get(count);
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        GPSRecord g = getRecordValueAt(rowIndex);
        if (g != null) {
            return getValue(g, columnIndex);
        }
        return null;
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
        GPSRecord g = getRecordValueAt(rowIndex);
        if (g != null) {
            setValue(value, g, columnIndex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnToType(columnIndex)) {
        case PositionData.C_LOGTIME:
            return Float.class;
        case PositionData.C_LOGDIST:
            return Float.class;
        case PositionData.C_LOGSPD:
            return Float.class;
        case PositionData.FMT_NS:
            return String.class;
        case PositionData.FMT_EW:
            return String.class;
        case PositionData.FMT_REC_NBR:
            return Integer.class;
        case BT747Constants.FMT_UTC_IDX:
            return String.class;
        case PositionData.FMT_UTC_VALUE:
            return Long.class;
        case PositionData.FMT_DATE:
            return String.class;
        case PositionData.FMT_TIME:
            return String.class;
        case BT747Constants.FMT_VALID_IDX:
            return String.class;
        case PositionData.FMT_LATNS:
            return String.class;
        case PositionData.FMT_LONEW:
            return String.class;
        case BT747Constants.FMT_LATITUDE_IDX:
            break;
        case BT747Constants.FMT_LONGITUDE_IDX:
            break;
        case BT747Constants.FMT_HEIGHT_IDX:
            break;
        case PositionData.FMT_HEIGHT_FT_IDX:
            break;
        case BT747Constants.FMT_SPEED_IDX:
            return Float.class;
        case PositionData.FMT_SPEED_MPH_IDX:
            return Float.class;
        case BT747Constants.FMT_HEADING_IDX:
            return Float.class;
        case BT747Constants.FMT_DSTA_IDX:
            return Integer.class;
        case BT747Constants.FMT_DAGE_IDX:
            return Integer.class;
        case BT747Constants.FMT_PDOP_IDX:
            return Float.class;
        case BT747Constants.FMT_HDOP_IDX:
            return Float.class;
        case BT747Constants.FMT_VDOP_IDX:
            return Float.class;
        case BT747Constants.FMT_NSAT_IDX:
            return Integer.class;
        case PositionData.FMT_FIXMODE:
            return String.class;
        case BT747Constants.FMT_MAX_SATS:
            return Integer.class;
        case BT747Constants.FMT_SID_IDX:
            break;
        case PositionData.FMT_VOX:
            return String.class;
        case BT747Constants.FMT_RCR_IDX:
            return String.class;
        case PositionData.FMT_RCR_DESCRIPTION:
            return String.class;
        case BT747Constants.FMT_MILLISECOND_IDX:
            return Integer.class;
        case BT747Constants.FMT_DISTANCE_IDX:
            return Float.class;
        case PositionData.FMT_DISTANCE_FT_IDX:
            return Float.class;
        }
        return Object.class;
    }

    private final Object getValue(final GPSRecord g, final int index) {
        switch (columnToType(index)) {
        case PositionData.C_LOGTIME:
            break;
        case PositionData.C_LOGDIST:
            break;
        case PositionData.C_LOGSPD:
            break;
        case PositionData.FMT_NS:
            break;
        case PositionData.FMT_EW:
            break;
        case PositionData.FMT_REC_NBR:
            return Integer.valueOf(g.recCount);
        case BT747Constants.FMT_UTC_IDX:
            return CommonOut.getDateTimeStr(g.utc);
        case PositionData.FMT_UTC_VALUE:
            return Long.valueOf(g.utc);
        case PositionData.FMT_DATE:
            return CommonOut.getDateStr(g.utc);
        case PositionData.FMT_TIME:
            return CommonOut.getTimeStr(g.utc);
        case BT747Constants.FMT_VALID_IDX:
            return CommonOut.getFixText(g.valid);
        case PositionData.FMT_LATNS:
            break;
        case PositionData.FMT_LONEW:
            break;
        case BT747Constants.FMT_LATITUDE_IDX:
            return new Double(g.latitude);
        case BT747Constants.FMT_LONGITUDE_IDX:
            return new Double(g.longitude);
        case BT747Constants.FMT_HEIGHT_IDX:
            return new Float(g.height);
        case PositionData.FMT_HEIGHT_FT_IDX:
            break;
        case BT747Constants.FMT_SPEED_IDX:
            return new Float(g.speed);
        case PositionData.FMT_SPEED_MPH_IDX:
            break;
        case BT747Constants.FMT_HEADING_IDX:
            return new Float(g.heading);
        case BT747Constants.FMT_DSTA_IDX:
            return new Integer(g.dsta);
        case BT747Constants.FMT_DAGE_IDX:
            return new Integer(g.dage);
        case BT747Constants.FMT_PDOP_IDX:
            return new Float(g.pdop / 100.0f);
        case BT747Constants.FMT_HDOP_IDX:
            return new Float(g.hdop / 100.0f);
        case BT747Constants.FMT_VDOP_IDX:
            return new Float(g.vdop / 100.0f);
        case BT747Constants.FMT_NSAT_IDX:
            return new Integer(g.nsat);
        case PositionData.FMT_FIXMODE:
            return CommonOut.getFixText(g.valid);
        case BT747Constants.FMT_MAX_SATS:
            break;
        case BT747Constants.FMT_SID_IDX:
            break;
        case PositionData.FMT_VOX:
            return g.voxStr;
        case BT747Constants.FMT_RCR_IDX:
            return CommonOut.getRCRstr(g);
        case PositionData.FMT_RCR_DESCRIPTION:
            return CommonOut.getRcrSymbolText(g);
        case BT747Constants.FMT_MILLISECOND_IDX:
            return new Integer(g.milisecond);
        case BT747Constants.FMT_DISTANCE_IDX:
            return new Double(g.distance);
        case PositionData.FMT_DISTANCE_FT_IDX:
            break;
        }
        return null; // Default;
    }

    private final void setValue(final Object o, final GPSRecord g,
            final int index) {
        try {
            switch (columnToType(index)) {
            case PositionData.C_LOGTIME:
                break;
            case PositionData.C_LOGDIST:
                break;
            case PositionData.C_LOGSPD:
                break;
            case PositionData.FMT_NS:
                break;
            case PositionData.FMT_EW:
                break;
            case PositionData.FMT_REC_NBR:
                g.recCount = (Integer) o;
                break;
            case PositionData.FMT_DATE:
                break;
            case PositionData.FMT_TIME:
                break;
            case BT747Constants.FMT_VALID_IDX:
                break;
            case PositionData.FMT_LATNS:
                break;
            case PositionData.FMT_LONEW:
                break;
            case BT747Constants.FMT_LATITUDE_IDX:
                g.latitude = (Double) o;
                break;
            case BT747Constants.FMT_LONGITUDE_IDX:
                g.longitude = (Double) g.longitude;
                break;
            case BT747Constants.FMT_HEIGHT_IDX:
                break;
            case PositionData.FMT_HEIGHT_FT_IDX:
                break;
            case BT747Constants.FMT_SPEED_IDX:
                break;
            case PositionData.FMT_SPEED_MPH_IDX:
                break;
            case BT747Constants.FMT_HEADING_IDX:
                break;
            case BT747Constants.FMT_DSTA_IDX:
                break;
            case BT747Constants.FMT_DAGE_IDX:
                break;
            case BT747Constants.FMT_PDOP_IDX:
                break;
            case BT747Constants.FMT_HDOP_IDX:
                break;
            case BT747Constants.FMT_VDOP_IDX:
                break;
            case BT747Constants.FMT_NSAT_IDX:
                break;
            case PositionData.FMT_FIXMODE:
                break;
            case BT747Constants.FMT_MAX_SATS:
                break;
            case BT747Constants.FMT_SID_IDX:
                break;
            case PositionData.FMT_VOX:
                break;
            case BT747Constants.FMT_RCR_IDX:
                break;
            case PositionData.FMT_RCR_DESCRIPTION:
                break;
            case BT747Constants.FMT_MILLISECOND_IDX:
                break;
            case BT747Constants.FMT_DISTANCE_IDX:
                break;
            case PositionData.FMT_DISTANCE_FT_IDX:
                break;
            case BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX:
                break;
            }
        } catch (Exception e) {
            Generic.debug("setValue " + index + columnToType(index) + o, e);
        }
    }

}
