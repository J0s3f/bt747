/**
 * 
 */
package gps.mvc.commands.mtk;

import gps.connection.GPSrxtx;
import gps.connection.MtkBinWriter;
import gps.mvc.GPSLinkHandler;
import gps.mvc.commands.GpsLinkExecCommand;

/**
 * @author Mario
 *
 */
public class SetMtkBinModeCommand implements GpsLinkExecCommand {

    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkCommand#execute(gps.mvc.GPSLinkHandler)
     */
    public void execute(GPSLinkHandler context) {
        final GPSrxtx gpsRxTx = context.getGPSRxtx(); 
        MtkBinWriter.setMtkBinMode(gpsRxTx, 115200);
    }

}