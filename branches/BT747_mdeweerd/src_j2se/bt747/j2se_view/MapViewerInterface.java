/**
 * 
 */
package bt747.j2se_view;

import gps.log.GPSRecord;

/**
 * @author Mario
 *
 */
public interface MapViewerInterface {

    public void setUserWayPoints(GPSRecord[] records);
    public void setWayPoints(GPSRecord[] records);

}