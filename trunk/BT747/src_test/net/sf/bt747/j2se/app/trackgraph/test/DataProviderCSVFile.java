package net.sf.bt747.j2se.app.trackgraph.test;

import gps.log.GPSRecord;
import info.monitorenter.gui.chart.TracePoint2D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;

import net.sf.bt747.j2se.app.trackgraph.CoordinateResolver;
import net.sf.bt747.j2se.app.trackgraph.DataProvider;

/**
 * Simple CSV data handling for running in stand-alone mode.
 * 
 * Reads Data from a CSV file created by BT747.
 * 
 * @author Matthias
 * 
 */
public class DataProviderCSVFile implements DataProvider, CoordinateResolver {

    class TrackIterator implements Iterator<TracePoint2D> {
        Queue<TracePoint2D> queue;

        public TrackIterator(Queue<TracePoint2D> q) {
            super();
            queue = q;
        }

        public boolean hasNext() {
            if (queue.peek() == null)
                return fillBuffer();
            return true;
        }

        public TracePoint2D next() {
            return queue.remove();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * A simple GPSRecord that only holds the coordinates and the record
     * number.
     * 
     */
    class SimpleGPSRecord extends GPSRecord {
        public SimpleGPSRecord(String[] line) {
            super();

            recCount = Integer.parseInt(line[0]);
            latitude = Double.parseDouble(line[5]);
            if ("S" == line[6])
                latitude *= -1;
            longitude = Double.parseDouble(line[7]);
            if ("W" == line[8])
                longitude *= -1;

        }

        @Override
        public String toString() {

            String ret = (recCount) + ": (";
            ret += fmt.format(latitude);
            if (latitude >= 0)
                ret += " N; ";
            else
                ret += " S; ";

            ret += fmt.format(longitude);
            if (longitude >= 0)
                ret += " E)";
            else
                ret += " W)";

            return ret;
        }
    }

    private BufferedReader reader;

    private Queue<TracePoint2D> valuesHeight;
    private Queue<TracePoint2D> valuesSpeed;

    private TreeMap<Double, GPSRecord> coordinates;

    private DateFormat dformat = new SimpleDateFormat(
            "yyyy/MM/dd+kk:mm:ss.SSS");
    private DecimalFormat fmt = new DecimalFormat("#######0.000000");

    public DataProviderCSVFile(File csvfile) throws IOException {
        reader = new BufferedReader(new FileReader(csvfile));
        reader.readLine(); // drop 1st line

        valuesHeight = new LinkedList<TracePoint2D>();
        valuesSpeed = new LinkedList<TracePoint2D>();

        coordinates = new TreeMap<Double, GPSRecord>();
    }

    private boolean fillBuffer() {
        String[] values;
        double time;

        try {
            values = reader.readLine().split(",");
            time = (double) dformat.parse(values[2] + "+" + values[3])
                    .getTime();
        } catch (Exception e) {
            return false;
        }

        valuesHeight
                .add(new TracePoint2D(time, Double.parseDouble(values[9])));
        valuesSpeed
                .add(new TracePoint2D(time, Double.parseDouble(values[10])));

        coordinates.put(time, new SimpleGPSRecord(values));

        return true;
    }

    public Iterator<TracePoint2D> getHeightIter() {
        return new TrackIterator(valuesHeight);
    }

    public Iterator<TracePoint2D> getSpeedIter() {
        return new TrackIterator(valuesSpeed);
    }

    public GPSRecord getRecordOfTimestamp(double t) {
        
//        final Entry<Double, GPSRecord> e = coordinates.floorEntry(t);
//        return e.getValue();
        return coordinates.get(coordinates.headMap(t).firstKey());
//        return coordinates.get(t);
    }

}
