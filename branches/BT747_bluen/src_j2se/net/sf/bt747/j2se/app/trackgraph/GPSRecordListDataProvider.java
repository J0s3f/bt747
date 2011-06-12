/**
 * 
 */
package net.sf.bt747.j2se.app.trackgraph;

import gps.log.GPSRecord;
import info.monitorenter.gui.chart.TracePoint2D;

import java.util.List;
import java.util.AbstractList;
import java.util.Iterator;

import bt747.j2se_view.model.DataTypes;
import bt747.j2se_view.model.GPSRecordUtils;

/**
 * @author Mario
 */
public class GPSRecordListDataProvider implements GPSRecordDataProvider {
    
    private final List<List<GPSRecord>> list;
    private GPSRecord listGet(final int i) {
        int currentIdx = i;
        for (int j = 0; j < list.size(); j++) {
            int currentListSize = list.get(j).size();
            if (currentIdx < currentListSize) {
                return list.get(j).get(currentIdx);
            }
            currentIdx -= currentListSize;
        }
        return null;
    }
    
    private final int listSize() {
        int sum = 0;
        for (int j = 0; j < list.size(); j++) {
            sum += list.get(j).size();
        }
        return sum;
    }
    
    private class GPSRecordList extends AbstractList<TracePoint2D> {

        private final int valueType;

        public GPSRecordList(final int valueType) {
            this.valueType = valueType;
        }


        /*
         * (non-Javadoc)
         * 
         * @see java.util.AbstractList#get(int)
         */
        @Override
        public TracePoint2D get(int index) {
            GPSRecord r = listGet(index);
            if (r == null) {
                return null;
            }
            Object value = GPSRecordUtils.getValue(r, valueType);
            double dValue;
            if (value instanceof Double) {
                dValue = (Double) value;
            } else if (value instanceof Float) {
                dValue = (Float) value;
            } else if (value instanceof Integer) {
                dValue = (Integer) value;
            } else {
                dValue = 0;
            }
            return new TracePoint2D(Double.valueOf(r.getUtc()), dValue);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return listSize();
        }

    }

    /**
     * 
     */
    public GPSRecordListDataProvider(final List<List<GPSRecord>> positions) {
        this.list = positions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.app.trackgraph.DataProvider#getHeightIter()
     */
    public Iterator<TracePoint2D> getHeightIter() {
        return new GPSRecordList(DataTypes.HEIGHT_METERS)
                .iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.app.trackgraph.DataProvider#getSpeedIter()
     */
    public Iterator<TracePoint2D> getSpeedIter() {
        return new GPSRecordList(DataTypes.SPEED).iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @seenet.sf.bt747.j2se.app.trackgraph.CoordinateResolver#
     * getRecordOfTimestamp(double)
     */
    public GPSRecord getRecordOfTimestamp(double t) {
        // Simple search
        for (int i = 0; i < listSize(); i++) {
            if (t > listGet(i).getUtc()) {
                if (i > 0) {
                    return listGet(i - 1);
                } else {
                    return listGet(i);
                }
            }
        }
        return null;
    }

}
