/**
 * 
 */
package net.sf.bt747.test;

import gps.connection.DPL700Writer;
import gps.connection.GPSrxtx;
import gps.mvc.GPSLinkHandler;
import gps.mvc.commands.GpsRxtxExecCommand;

/**
 * @author Mario
 * 
 */
public class DPL700DeviceStrCommand implements GpsRxtxExecCommand {

    private final String cmd;
    private final int bufSize;

    public DPL700DeviceStrCommand(final String cmd) {
        this(cmd, cmd.length() + 1);
    }

    /**
     * 
     */
    public DPL700DeviceStrCommand(final String cmd, final int bufSize) {
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

}
