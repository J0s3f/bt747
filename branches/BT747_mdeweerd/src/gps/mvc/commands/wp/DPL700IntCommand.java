/**
 * 
 */
package gps.mvc.commands.wp;

import bt747.sys.JavaLibBridge;

import gps.connection.DPL700Writer;
import gps.connection.GPSrxtx;
import gps.mvc.commands.GpsLinkExecCommand;

/**
 * @author Mario De Weerd
 * 
 */
public class DPL700IntCommand implements GpsLinkExecCommand {

    private final int cmd;
    private final int arg;
    private final int bufSize;

    /**
     * 
     */
    public DPL700IntCommand(final int cmd, final int bufSize) {
        this(cmd, 0, bufSize);
    }

    public DPL700IntCommand(final int cmd, final int arg, final int bufSize) {
        this.cmd = cmd;
        this.arg = arg;
        this.bufSize = bufSize;
    }
    
    public final int getCmd() {
        return cmd;
    }
    
    public final int getArg() {
        return arg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSrxtx)
     */
    public final void execute(final GPSrxtx context) {
        DPL700Writer.sendCmd(context, cmd, bufSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "DPL700IntCommand " + JavaLibBridge.unsigned2hex(cmd, bufSize*2) + " " + arg;
    }

}
