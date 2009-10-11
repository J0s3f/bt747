/**
 * 
 */
package gps.mvc.commands.dpl700;

import gps.connection.DPL700Writer;
import gps.connection.GPSrxtx;
import gps.mvc.commands.GpsLinkExecCommand;

/**
 * @author Mario
 *
 */
public class DPL700StrCommand implements GpsLinkExecCommand {

    private final String cmd;
    private final int bufSize;

    public DPL700StrCommand(final String cmd) {
        this(cmd, cmd.length()+1);
    }

    /**
     * 
     */
    public DPL700StrCommand(final String cmd, final int bufSize) {
        this.cmd = cmd;
        this.bufSize = bufSize;
    }
    
    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSrxtx)
     */
    public final void execute(final GPSrxtx context) {
        DPL700Writer.sendCmd(context, cmd, bufSize);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DPL700Str:"+cmd;
    }
}