/**
 * 
 */
package gps.mvc.commands.wp;

import bt747.sys.JavaLibBridge;

import gps.connection.WPWriter;
import gps.connection.GPSrxtx;
import gps.mvc.WPModel;
import gps.mvc.commands.GpsLinkExecCommand;

/**
 * @author Mario De Weerd
 * 
 */
public class WPIntCommand implements GpsLinkExecCommand {

    private final int cmd;
    private final int arg;
    private final int bufSize;
    private final int type;
    private final WPModel m;

    /**
     * 
     */
    public WPIntCommand(final WPModel m, final int cmd, final int type, final int bufSize) {
        this(m, cmd, type, 0, bufSize);
    }

    public WPIntCommand(final WPModel m, final int cmd, final int type, final int arg, final int bufSize) {
        this.cmd = cmd;
        this.arg = arg;
        this.bufSize = bufSize;
        this.m = m;
        this.type = type;
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
        if(m!=null) {
            m.setExpectedDataType(type);
        }
        WPWriter.sendCmd(context, cmd, bufSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "WPIntCommand " + JavaLibBridge.unsigned2hex(cmd, 8) + " " + bufSize + " " + arg;
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
