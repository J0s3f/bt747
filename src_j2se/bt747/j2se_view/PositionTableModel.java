//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package bt747.j2se_view;

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
public class PositionTableModel extends AbstractTableModel {

    /**
     * Column identifications. Some taken from BT747Constants.
     */
    private static final int FMT_UTC_VALUE = -15; // TODO: currently ignored
    private static final int FMT_RCR_DESCRIPTION = -14; // TODO: currently
                                                        // ignored
    private static final int FMT_FIXMODE = -13; // TODO: currently ignored
    private static final int FMT_VOX = -12;
    private static final int FMT_LONEW = -11;
    private static final int FMT_LATNS = -10;
    private static final int C_LOGTIME = -9;
    private static final int C_LOGDIST = -8;
    private static final int C_LOGSPD = -7;
    private static final int FMT_NS = -6;
    private static final int FMT_EW = -5;
    private static final int FMT_REC_NBR = -4;
    private static final int FMT_DATE = -3;
    private static final int FMT_TIME = -2;
    private static final int FMT_NO_FIELD = -1;
    private static final int FMT_HEIGHT_FT_IDX = BT747Constants.FMT_HEIGHT_IDX + 100;
    private static final int FMT_SPEED_MPH_IDX = BT747Constants.FMT_SPEED_IDX + 100;
    private static final int FMT_DISTANCE_FT_IDX = BT747Constants.FMT_DISTANCE_IDX + 100;

    private int[] columnTypes = {
            FMT_REC_NBR,
            FMT_DATE,
            FMT_TIME,
            // BT747Constants.FMT_UTC_IDX,
            // FMT_UTC_VALUE,
            BT747Constants.FMT_RCR_IDX, FMT_RCR_DESCRIPTION,
            BT747Constants.FMT_VALID_IDX, BT747Constants.FMT_LATITUDE_IDX,
            BT747Constants.FMT_LONGITUDE_IDX, BT747Constants.FMT_HEIGHT_IDX,
            BT747Constants.FMT_HDOP_IDX, BT747Constants.FMT_PDOP_IDX, FMT_VOX };

    private volatile List<List<GPSRecord>> gpsData;

    /**
     * @return the gpsData
     */
    public final List<List<GPSRecord>> getGpsData() {
        return this.gpsData;
    }

    /**
     * @param gpsData
     *            the gpsData to set
     */
    public final void setGpsData(final GPSRecord[] gpsdata) {
        gpsData = null;
        Vector<List<GPSRecord>> tmp = new Vector<List<GPSRecord>>(1);
        Vector<GPSRecord>tmpList = new Vector<GPSRecord>(gpsdata.length);
        for(GPSRecord g:gpsdata) {
            tmpList.add(g);
        }
        tmp.add(tmpList);
        gpsData = tmp;
        fireTableStructureChanged();
    }

    
    /**
     * @param gpsData
     *            the gpsData to set
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
            return FMT_NO_FIELD;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        if (columnIndex < columnTypes.length) {
            switch (columnToType(columnIndex)) {
            case C_LOGTIME:
                break;
            case C_LOGDIST:
                break;
            case C_LOGSPD:
                break;
            case FMT_NS:
                break;
            case FMT_EW:
                break;
            case FMT_REC_NBR:
                return "Record nbr";
            case BT747Constants.FMT_UTC_IDX:
                return "Date & Time";
            case FMT_UTC_VALUE:
                return "UTC Value";
            case FMT_DATE:
                return "Date";
            case FMT_TIME:
                return "Time";
            case BT747Constants.FMT_VALID_IDX:
                return "Valid";
            case FMT_LATNS:
                break;
            case FMT_LONEW:
                break;
            case BT747Constants.FMT_LATITUDE_IDX:
                return "Latitude";
            case BT747Constants.FMT_LONGITUDE_IDX:
                return "Longitude";
            case BT747Constants.FMT_HEIGHT_IDX:
                return "Height (m)";
            case FMT_HEIGHT_FT_IDX:
                return "Height (ft)";
            case BT747Constants.FMT_SPEED_IDX:
                return "Speed (km/h)";
            case FMT_SPEED_MPH_IDX:
                return "Speed (mph)";
            case BT747Constants.FMT_HEADING_IDX:
                return "Heading";
            case BT747Constants.FMT_DSTA_IDX:
                return "DSTA";
            case BT747Constants.FMT_DAGE_IDX:
                return "DAGE";
            case BT747Constants.FMT_PDOP_IDX:
                return "PDOP";
            case BT747Constants.FMT_HDOP_IDX:
                return "HDOP";
            case BT747Constants.FMT_VDOP_IDX:
                return "VDOP";
            case BT747Constants.FMT_NSAT_IDX:
                return "NSAT";
            case FMT_FIXMODE:
                return "Fix";
            case BT747Constants.FMT_MAX_SATS:
                return "Max Sats";
            case BT747Constants.FMT_SID_IDX:
                return "SID";
            case FMT_VOX:
                return "VOX/File";
            case BT747Constants.FMT_RCR_IDX:
                return "RCR";
            case FMT_RCR_DESCRIPTION:
                return "RCR Description";
            case BT747Constants.FMT_MILLISECOND_IDX:
                return "MS";
            case BT747Constants.FMT_DISTANCE_IDX:
                return "Distance (m)";
            case FMT_DISTANCE_FT_IDX:
                return "Distance (ft)";
            }
            return null;
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if (gpsData != null) {
            int count = 0;
            for(List<GPSRecord> g:gpsData) {
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
            for(List<GPSRecord> g:gpsData) {
                if(count<g.size()) {
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
        if(g!=null) {
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
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        GPSRecord g = getRecordValueAt(rowIndex);
        if(g!=null) {
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
        case C_LOGTIME:
            return Float.class;
        case C_LOGDIST:
            return Float.class;
        case C_LOGSPD:
            return Float.class;
        case FMT_NS:
            return String.class;
        case FMT_EW:
            return String.class;
        case FMT_REC_NBR:
            return Integer.class;
        case BT747Constants.FMT_UTC_IDX:
            return String.class;
        case FMT_UTC_VALUE:
            return Long.class;
        case FMT_DATE:
            return String.class;
        case FMT_TIME:
            return String.class;
        case BT747Constants.FMT_VALID_IDX:
            return String.class;
        case FMT_LATNS:
            return String.class;
        case FMT_LONEW:
            return String.class;
        case BT747Constants.FMT_LATITUDE_IDX:
            break;
        case BT747Constants.FMT_LONGITUDE_IDX:
            break;
        case BT747Constants.FMT_HEIGHT_IDX:
            break;
        case FMT_HEIGHT_FT_IDX:
            break;
        case BT747Constants.FMT_SPEED_IDX:
            return Float.class;
        case FMT_SPEED_MPH_IDX:
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
        case FMT_FIXMODE:
            return String.class;
        case BT747Constants.FMT_MAX_SATS:
            return Integer.class;
        case BT747Constants.FMT_SID_IDX:
            break;
        case FMT_VOX:
            return String.class;
        case BT747Constants.FMT_RCR_IDX:
            return String.class;
        case FMT_RCR_DESCRIPTION:
            return String.class;
        case BT747Constants.FMT_MILLISECOND_IDX:
            return Integer.class;
        case BT747Constants.FMT_DISTANCE_IDX:
            return Float.class;
        case FMT_DISTANCE_FT_IDX:
            return Float.class;
        }
        return Object.class;
    }

    private final Object getValue(final GPSRecord g, final int index) {
        switch (columnToType(index)) {
        case C_LOGTIME:
            break;
        case C_LOGDIST:
            break;
        case C_LOGSPD:
            break;
        case FMT_NS:
            break;
        case FMT_EW:
            break;
        case FMT_REC_NBR:
            return Integer.valueOf(g.recCount);
        case BT747Constants.FMT_UTC_IDX:
            return CommonOut.getDateTimeStr(g.utc);
        case FMT_UTC_VALUE:
            return Long.valueOf(g.utc);
        case FMT_DATE:
            return CommonOut.getDateStr(g.utc);
        case FMT_TIME:
            return CommonOut.getTimeStr(g.utc);
        case BT747Constants.FMT_VALID_IDX:
            return CommonOut.getFixText(g.valid);
        case FMT_LATNS:
            break;
        case FMT_LONEW:
            break;
        case BT747Constants.FMT_LATITUDE_IDX:
            return new Double(g.latitude);
        case BT747Constants.FMT_LONGITUDE_IDX:
            return new Double(g.longitude);
        case BT747Constants.FMT_HEIGHT_IDX:
            return new Float(g.height);
        case FMT_HEIGHT_FT_IDX:
            break;
        case BT747Constants.FMT_SPEED_IDX:
            return new Float(g.speed);
        case FMT_SPEED_MPH_IDX:
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
        case FMT_FIXMODE:
            return CommonOut.getFixText(g.valid);
        case BT747Constants.FMT_MAX_SATS:
            break;
        case BT747Constants.FMT_SID_IDX:
            break;
        case FMT_VOX:
            return g.voxStr;
        case BT747Constants.FMT_RCR_IDX:
            return CommonOut.getRCRstr(g);
        case FMT_RCR_DESCRIPTION:
            return CommonOut.getRcrSymbolText(g);
        case BT747Constants.FMT_MILLISECOND_IDX:
            return new Integer(g.milisecond);
        case BT747Constants.FMT_DISTANCE_IDX:
            return new Double(g.distance);
        case FMT_DISTANCE_FT_IDX:
            break;
        }
        return null; // Default;
    }

    private final void setValue(final Object o, final GPSRecord g,
            final int index) {
        try {
            switch (columnToType(index)) {
            case C_LOGTIME:
                break;
            case C_LOGDIST:
                break;
            case C_LOGSPD:
                break;
            case FMT_NS:
                break;
            case FMT_EW:
                break;
            case FMT_REC_NBR:
                g.recCount = (Integer) o;
                break;
            case FMT_DATE:
                break;
            case FMT_TIME:
                break;
            case BT747Constants.FMT_VALID_IDX:
                break;
            case FMT_LATNS:
                break;
            case FMT_LONEW:
                break;
            case BT747Constants.FMT_LATITUDE_IDX:
                g.latitude = (Double) o;
                break;
            case BT747Constants.FMT_LONGITUDE_IDX:
                g.longitude = (Double) g.longitude;
                break;
            case BT747Constants.FMT_HEIGHT_IDX:
                break;
            case FMT_HEIGHT_FT_IDX:
                break;
            case BT747Constants.FMT_SPEED_IDX:
                break;
            case FMT_SPEED_MPH_IDX:
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
            case FMT_FIXMODE:
                break;
            case BT747Constants.FMT_MAX_SATS:
                break;
            case BT747Constants.FMT_SID_IDX:
                break;
            case FMT_VOX:
                break;
            case BT747Constants.FMT_RCR_IDX:
                break;
            case FMT_RCR_DESCRIPTION:
                break;
            case BT747Constants.FMT_MILLISECOND_IDX:
                break;
            case BT747Constants.FMT_DISTANCE_IDX:
                break;
            case FMT_DISTANCE_FT_IDX:
                break;
            case BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX:
                break;
            }
        } catch (Exception e) {
            Generic.debug("setValue " + index + columnToType(index) + o, e);
        }
    }

}
