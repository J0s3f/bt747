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

    public static final int UPDATE_WAYPOINT_LIST = 1000;
    public static final int UPDATE_TRACKPOINT_LIST = 1001;
    public static final int UPDATE_USERWAYPOINT_LIST = 1002;
    public static final int CHANGE_TO_MAP = 1003;

    private final PositionData positionData = new PositionData(this);

    public final void postModelEvent(final ModelEvent e) {
        super.postEvent(e);
    }

    /**
     * @return the positionData
     */
    public final PositionData getPositionData() {
        return positionData;
    }
}
