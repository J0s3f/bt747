/**
 * 
 */
package bt747.j2se_view.model;

import gps.log.GPSRecord;

import java.util.List;

import org.jdesktop.swingx.JXPanel;

import bt747.j2se_view.J2SEAppModel;
import bt747.model.ModelEvent;

/**
 * @author Mario
 * 
 */
@SuppressWarnings("serial")
public class PositionData extends JXPanel {
    J2SEAppModel m;
    private List<List<GPSRecord>> trks = null;
    private GPSRecord[] wayPoints = null;
    private GPSRecord[] userWayPoints = null;
    
    /**
     * 
     */
    public PositionData() {
        // TODO Auto-generated constructor stub
    }
    
    public PositionData(J2SEAppModel m) {
        this.m = m;
    }

    public final List<List<GPSRecord>> getTracks() {
        return this.trks;
    }

    public final void setTracks(List<List<GPSRecord>> trks) {
        this.trks = trks;
        fireTrackPointListChange();
    }

    public final GPSRecord[] getWayPoints() {
        return this.wayPoints;
    }

    public final void setWayPoints(GPSRecord[] waypoints) {
        this.wayPoints = waypoints;
        fireWaypointListUpdate();
    }

    public final GPSRecord[] getUserWayPoints() {
        return this.userWayPoints;
    }

    public final void setUserWayPoints(GPSRecord[] userWayPoints) {
        this.userWayPoints = userWayPoints;
        fireUserWaypointUpdate();
    }

    
    
    
    
    
    
    
    private void fireWaypointListUpdate() {
        postEvent(new ModelEvent(J2SEAppModel.UPDATE_WAYPOINT_LIST, null));
    }

    /**
     * 
     */
    private void fireUserWaypointUpdate() {
        postEvent(new ModelEvent(J2SEAppModel.UPDATE_USERWAYPOINT_LIST, null));
    }

    private final void fireTrackPointListChange() {
        postEvent(new ModelEvent(J2SEAppModel.UPDATE_TRACKPOINT_LIST, trks));
    }

    private final void postEvent(final ModelEvent e) {
        if (m != null) {
            m.postModelEvent(e);
        }
    }

}
