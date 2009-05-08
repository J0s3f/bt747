/**
 * 
 */
package gps.mvc.commands.dpl700;

import gps.connection.DPL700Writer;
import gps.connection.GPSrxtx;
import gps.mvc.GPSLinkHandler;
import gps.mvc.commands.GpsLinkExecCommand;

/**
 * @author Mario
 *
 */
public class DPL700StrCommand implements GpsLinkExecCommand {

    private final String cmd;
    private final int bufSize;
    
    /**
     * 
     */
    public DPL700StrCommand(final String cmd, final int bufSize) {
        this.cmd = cmd;
        this.bufSize = bufSize;
    }
    
    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSLinkHandler)
     */
    public final void execute(final GPSLinkHandler context) {
        final GPSrxtx gpsRxTx = context.getGPSRxtx();
        DPL700Writer.sendCmd(gpsRxTx, cmd, bufSize);

    }

}