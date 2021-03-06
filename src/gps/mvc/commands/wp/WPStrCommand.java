/**
 * 
 */
package gps.mvc.commands.wp;

import gps.connection.WPWriter;
import gps.connection.GPSrxtx;
import gps.mvc.commands.GpsLinkExecCommand;

/**
 * @author Mario
 * 
 */
public class WPStrCommand implements GpsLinkExecCommand {

    private final String cmd;
    private final int bufSize;

    public WPStrCommand(final String cmd) {
        this(cmd, cmd.length() + 1);
    }

    /**
     * 
     */
    public WPStrCommand(final String cmd, final int bufSize) {
        this.cmd = cmd;
        this.bufSize = bufSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSrxtx)
     */
    public void execute(final GPSrxtx context) {
        WPWriter.sendCmd(context, cmd, bufSize);
    }

    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#mustBeFirstInQueue()
     */
    public boolean mustBeFirstInQueue() {
        return true;
    }
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "WPStr:" + cmd;
    }
    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#hasAck()
     */
    public boolean hasAck() {
        return false;
    }
}
