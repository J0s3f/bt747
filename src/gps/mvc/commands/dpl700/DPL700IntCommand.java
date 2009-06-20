/**
 * 
 */
package gps.mvc.commands.dpl700;

import gps.connection.DPL700Writer;
import gps.connection.GPSrxtx;
import gps.mvc.GPSLinkHandler;
import gps.mvc.commands.GpsRxtxExecCommand;

/**
 * @author Mario De Weerd
 *
 */
public class DPL700IntCommand implements GpsRxtxExecCommand {

    private final int cmd;
    private final int bufSize;
    
    /**
     * 
     */
    public DPL700IntCommand(final int cmd, final int bufSize) {
        this.cmd = cmd;
        this.bufSize = bufSize;
    }
    
    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSrxtx)
     */
    public final void execute(final GPSrxtx context) {
        DPL700Writer.sendCmd(context, cmd, bufSize);
    }

}
