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
public class WPEnterModeCommand extends WPStrCommand implements
        WondeproudConstants {
    /**
     * 
     */
    public WPEnterModeCommand() {
        super(WP_CAMERA_DETECT);
    }
    
    /* (non-Javadoc)
     * @see gps.mvc.commands.wp.WPStrCommand#execute(gps.connection.GPSrxtx)
     */
    public void execute(GPSrxtx context) {
        (new WPExitModeCommand()).execute(context);
        super.execute(context);
    }

}
