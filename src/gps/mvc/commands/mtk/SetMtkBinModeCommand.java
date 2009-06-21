/**
 * 
 */
package gps.mvc.commands.mtk;

import gps.connection.GPSrxtx;
import gps.connection.MtkBinWriter;
import gps.mvc.commands.GpsRxtxExecCommand;

/**
 * @author Mario
 *
 */
public class SetMtkBinModeCommand implements GpsRxtxExecCommand {

    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.connection.GPSrxtx)
     */
    public void execute(GPSrxtx context) {
        MtkBinWriter.setMtkBinMode(context, context.getSpeed());
    }

}
