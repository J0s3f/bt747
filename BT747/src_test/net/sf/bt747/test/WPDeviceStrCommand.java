/**
 * 
 */
package net.sf.bt747.test;

import gps.connection.GPSrxtx;
import gps.mvc.commands.GpsLinkExecCommand;

/**
 * @author Mario
 * 
 */
public class WPDeviceStrCommand implements GpsLinkExecCommand {

    private final String cmd;
    private final int bufSize;

    public WPDeviceStrCommand(final String cmd) {
        this(cmd, cmd.length() + 1);
    }

    /**
     * 
     */
    public WPDeviceStrCommand(final String cmd, final int bufSize) {
        this.cmd = cmd;
        this.bufSize = bufSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSrxtx)
     */
    public final void execute(final GPSrxtx context) {
        context.write(cmd + "\0");
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
