/**
 * Reimplementation of Mark McClures Javascript PolylineEncoder
 * All the mathematical logic is more or less copied by McClure
 *  
 * @author Mark Rambow
 * @e-mail markrambow[at]gmail[dot]com
 * @version 0.1
 * 
 */

package net.sourceforge.bt747.gps.tracks;

import net.sourceforge.bt747.bt747.util.Vector;

public class Track {

    private Vector trackpoints = new Vector();

    public final Vector getTrackpoints() {
        return this.trackpoints;
    }

    public final void setTrackpoints(final Vector trackpoints) {
        this.trackpoints = trackpoints;
    }

    public final void addTrackpoint(final Trackpoint trkpt) {
        this.trackpoints.addElement(trkpt);
    }
    
    public final Trackpoint get(final int i) {
        return (Trackpoint) this.trackpoints.elementAt(i);
    }
    
    public final int size() {
        return this.trackpoints.size();
    }

}