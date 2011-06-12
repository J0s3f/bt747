/**
 * 
 */
package net.sf.bt747.j2se.app.trackgraph;

import gps.log.GPSRecord;
import info.monitorenter.gui.chart.TracePoint2D;

import java.util.AbstractList;
import java.util.Iterator;

import bt747.j2se_view.model.DataTypes;
import bt747.j2se_view.model.GPSRecordUtils;

/**
 * @author Mario
 */
public class GPSRecordArrayDataProvider implements GPSRecordDataProvider {
    private static class GPSRecordArray extends AbstractList<TracePoint2D> {

        private final int valueType;
        private final GPSRecord[] list;

        public GPSRecordArray(final GPSRecord[] list, final int valueType) {
            this.valueType = valueType;
            this.list = list;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.AbstractList#get(int)
         */
        @Override
        public TracePoint2D get(int index) {
            Object value = GPSRecordUtils.getValue(list[index], valueType);
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
            return new TracePoint2D(Double.valueOf(list[index].getUtc()), dValue);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.AbstractCollection#size()
         */
        @Override
        public int size() {
            return list.length;
        }

    }


    private GPSRecord[] positions = null;

    /**
     * 
     */
    public GPSRecordArrayDataProvider(final GPSRecord[] positions) {
        this.positions = positions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.app.trackgraph.DataProvider#getHeightIter()
     */
    public Iterator<TracePoint2D> getHeightIter() {
        return new GPSRecordArray(positions, DataTypes.HEIGHT_METERS)
                .iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.app.trackgraph.DataProvider#getSpeedIter()
     */
    public Iterator<TracePoint2D> getSpeedIter() {
        return new GPSRecordArray(positions, DataTypes.SPEED).iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @seenet.sf.bt747.j2se.app.trackgraph.CoordinateResolver#
     * getRecordOfTimestamp(double)
     */
    public GPSRecord getRecordOfTimestamp(double t) {
        // Simple search
        for (int i = 0; i < positions.length; i++) {
            if (t > positions[i].getUtc()) {
                if (i > 0) {
                    return positions[i - 1];
                } else {
                    return positions[0];
                }
            }
        }
        return null;
    }

}
