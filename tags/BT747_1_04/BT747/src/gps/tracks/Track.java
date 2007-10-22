/**
 * Reimplementation of Mark McClures Javascript PolylineEncoder
 * All the mathematical logic is more or less copied by McClure
 *  
 * @author Mark Rambow
 * @e-mail markrambow[at]gmail[dot]com
 * @version 0.1
 * 
 */

package gps.tracks;

import waba.util.Vector;

public class Track {

    private Vector trackpoints = new Vector();

    public Vector getTrackpoints() {
        return this.trackpoints;
    }

    public void setTrackpoints(Vector trackpoints) {
        this.trackpoints = trackpoints;
    }

    public void addTrackpoint(Trackpoint trkpt) {
        this.trackpoints.add(trkpt);
    }
    
    public Trackpoint get(int i) {
        return (Trackpoint) this.trackpoints.items[i];
    }
    
    public int size() {
        return this.trackpoints.size();
    }

}
