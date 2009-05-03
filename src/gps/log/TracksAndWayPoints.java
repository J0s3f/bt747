package gps.log;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Vector;

/**
 * Result type.
 * 
 * @author Mario
 * 
 */
final public class TracksAndWayPoints {
    /**
     * {@link BT747Vector} of tracks ({@link BT747Vector}) of trackpoints
     * {@link GPSRecord}.
     */
    public BT747Vector tracks = JavaLibBridge.getVectorInstance();
    /**
     * {@link BT747Vector} of waypoints of type {@link GPSRecord} .
     */
    public BT747Vector waypoints = JavaLibBridge.getVectorInstance();

    public GPSRecord[] getTrackPoints() {
        GPSRecord[] t;
        int total;
        total = 0;
        for (int i = 0; i < tracks.size(); i++) {
            total += ((BT747Vector) (tracks.elementAt(i))).size();
        }
        t = new GPSRecord[total];
        int j;
        j = 0;
        for (int i = 0; i < tracks.size(); i++) {
            final BT747Vector tr = (BT747Vector) (tracks.elementAt(i));
            for (int x = 0; x < tr.size(); x++) {
                t[j] = (GPSRecord) tr.elementAt(x);
                j++;
            }
        }
        return t;
    }

    public final GPSRecord[] getWayPoints() {
        GPSRecord[] t;
        t = new GPSRecord[waypoints.size()];
        int j;
        j = 0;

        for (int x = 0; x < waypoints.size(); x++) {
            t[j] = (GPSRecord) waypoints.elementAt(x);
            j++;
        }
        return t;
    }
}
