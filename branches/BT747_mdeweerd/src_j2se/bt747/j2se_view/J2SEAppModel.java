/**
 * 
 */
package bt747.j2se_view;

import gps.log.GPSRecord;

import java.util.List;

import bt747.model.Model;
import bt747.model.ModelEvent;

/**
 * @author Mario
 * 
 */
public class J2SEAppModel extends Model {

    static public final int UPDATE_WAYPOINT_LIST = 1000;
    static public final int UPDATE_TRACKPOINT_LIST = 1001;
    static public final int UPDATE_USERWAYPOINT_LIST = 1002;

    private List<List<GPSRecord>> trks = null;
    private GPSRecord[] wayPoints = null;
    private GPSRecord[] userWayPoints = null;

    public final List<List<GPSRecord>> getTracks() {
        return this.trks;
    }

    public final void setTracks(List<List<GPSRecord>> trks) {
        this.trks = trks;
        postEvent(new ModelEvent(UPDATE_TRACKPOINT_LIST, trks));
    }

    public final GPSRecord[] getWayPoints() {
        return this.wayPoints;
    }

    public final void setWayPoints(GPSRecord[] waypoints) {
        this.wayPoints = waypoints;
        postEvent(new ModelEvent(UPDATE_WAYPOINT_LIST, waypoints));
    }

    public final GPSRecord[] getUserWayPoints() {
        return this.userWayPoints;
    }

    public final void setUserWayPoints(GPSRecord[] userWayPoints) {
        this.userWayPoints = userWayPoints;
        postEvent(new ModelEvent(UPDATE_USERWAYPOINT_LIST, userWayPoints));
    }

}
