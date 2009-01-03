/**
 * 
 */
package bt747.j2se_view;

import bt747.j2se_view.model.PositionData;
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
    
    private final PositionData positionData = new PositionData(this);

    public void postModelEvent(ModelEvent e) {
        super.postEvent(e);
    }

    /**
     * @return the positionData
     */
    public PositionData getPositionData() {
        return positionData;
    }
}
