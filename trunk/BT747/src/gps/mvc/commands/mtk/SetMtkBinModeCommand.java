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
public class SetMtkBinModeCommand implements GpsLinkExecCommand {

    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.connection.GPSrxtx)
     */
    public void execute(GPSrxtx context) {
        MtkBinWriter.setMtkBinMode(context, context.getSpeed());
    }

    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#mustBeFirstInQueue()
     */
    public boolean mustBeFirstInQueue() {
        return true;
    }

    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#hasAck()
     */
    public boolean hasAck() {
        return false;
    }
}
