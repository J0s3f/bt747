/**
 * 
 */
package gps.mvc.commands.wp;

import gps.WondeproudConstants;
import gps.connection.GPSrxtx;

/**
 * @author Mario
 * 
 */
public class WPExitModeCommand extends WPStrCommand implements
        WondeproudConstants {
    /**
     * 
     */
    public WPExitModeCommand() {
        super(WP_AP_EXIT);
    }

    /* (non-Javadoc)
     * @see gps.mvc.commands.wp.WPStrCommand#execute(gps.connection.GPSrxtx)
     */
    public void execute(GPSrxtx context) {
        super.execute(context);
    }
    
    /* (non-Javadoc)
     * @see gps.mvc.commands.wp.WPStrCommand#hasAck()
     */
    public boolean hasAck() {
        return false;
    }
}
