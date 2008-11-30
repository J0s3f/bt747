/**
 * 
 */
package bt747.j2se_view;

import gps.log.GPSRecord;
import gps.log.out.CommonOut;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import bt747.j2se_view.image.ImageData;
import bt747.j2se_view.utils.GPSRecordComparator;

/**
 * @author Mario
 * 
 */
public class FileTableModel extends AbstractTableModel {
    private java.util.Hashtable<String, ImageData> imageTable = new Hashtable<String, ImageData>();
    private Vector<String> imageOrder = new Vector<String>();

    public void add(final String path) {
        if (!imageTable.contains(path)) {
            ImageData id = new ImageData();
            id.setImagePath(path);
            imageTable.put(path, id);
            imageOrder.add(path);
            int row = imageOrder.size();
            fireTableRowsInserted(row, row);
        }
    }

    public GPSRecord[] getSortedGPSRecords() {
        GPSRecord[] rcrds;
        rcrds = new GPSRecord[imageOrder.size()];
        for (int i = 0; i < imageOrder.size(); i++) {
            rcrds[i] = imageTable.get(imageOrder.get(i)).getGpsInfo();
        }
        java.util.Arrays.sort(rcrds, new GPSRecordComparator());
        return rcrds;
    }

    public void clear() {
        int lastRow = getRowCount() - 1;
        imageOrder.clear();
        imageTable.clear();
        fireTableRowsDeleted(0, lastRow);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnToDataType(columnIndex)) {
        case NONE:
            return null;
        case PATH:
            return String.class;
        case WIDTH:
            return Integer.class;
        case HEIGHT:
            return Integer.class;
        case GEOMETRY:
            return String.class;
        case LATITUDE:
            return Object.class;// return Double.class;
        case LONGITUDE:
            return Object.class;// return Double.class;
        case DATETIME:
            return String.class;
        default:
            return null;
        }
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
        switch (columnToDataType(columnIndex)) {
        case NONE:
            return "None";
        case PATH:
            return "Image path";
        case WIDTH:
            return "Width";
        case HEIGHT:
            return "Height";
        case GEOMETRY:
            return "Geometry";
        case LATITUDE:
            return "Latitude";
        case LONGITUDE:
            return "Longitude";
        case DATETIME:
            return "Date/Time";
        default:
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return imageTable.size();
    }

    public static final int NONE = 0;
    public static final int PATH = 1;
    public static final int WIDTH = 2;
    public static final int HEIGHT = 3;
    public static final int GEOMETRY = 4;
    public static final int LATITUDE = 5;
    public static final int LONGITUDE = 6;
    public static final int DATETIME = 7;

    private int[] columns = { DATETIME, PATH, GEOMETRY, LATITUDE, LONGITUDE };

    private int columnToDataType(final int column) {
        if (column < columns.length) {
            return columns[column];
        } else {
            return NONE;
        }
    }

    private Object getData(final ImageData img, final int dataType) {
        switch (dataType) {
        case NONE:
            return null;
        case PATH:
            return img.getPath();
        case WIDTH:
            return Integer.valueOf(img.getWidth());
        case HEIGHT:
            return Integer.valueOf(img.getHeight());
        case GEOMETRY:
            if (img.getWidth() != 0) {
                return img.getWidth() + "x" + img.getHeight();
            } else {
                return null;
            }
        case LATITUDE:
            if (img.getGpsInfo().hasLatitude()) {
                return Double.valueOf(img.getGpsInfo().latitude);
            } else {
                return null;
            }
        case LONGITUDE:
            if (img.getGpsInfo().hasLongitude()) {
                return Double.valueOf(img.getGpsInfo().longitude);
            } else {
                return null;
            }
        case DATETIME:
            if (img.getGpsInfo().hasUtc()) {
                return CommonOut.getTimeStr(img.getGpsInfo().utc);
            } else {
                return null;
            }
        default:
            return null;
        }

    }

    private Object getColumn(final ImageData img, final int column) {
        return getData(img, columnToDataType(column));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return getColumn(imageTable.get(imageOrder.elementAt(rowIndex)),
                columnIndex);
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
        // TODO Auto-generated method stub

    }

}
