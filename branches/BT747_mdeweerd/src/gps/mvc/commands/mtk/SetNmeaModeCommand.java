/**
 * 
 */
package gps.mvc.commands.mtk;

import gps.connection.GPSrxtx;
import gps.connection.MtkBinWriter;
import gps.mvc.commands.GpsLinkExecCommand;

/**
 * @author Mario
 *
 */
public class SetNmeaModeCommand implements GpsLinkExecCommand {

    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.connection.GPSrxtx)
     */
    public void execute(GPSrxtx context) {
        MtkBinWriter.doSetNmeaMode(context, context.getSpeed());
    }
}
