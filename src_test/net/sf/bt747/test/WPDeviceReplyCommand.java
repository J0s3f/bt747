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
public class WPDeviceReplyCommand implements GpsLinkExecCommand {

    private final byte[] b;

    public WPDeviceReplyCommand(final byte[] b) {
        this.b = new byte[b.length];
        int i = 0;
        for (byte x : b) {
            this.b[i++] = x;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSrxtx)
     */
    public final void execute(final GPSrxtx context) {
        WPDeviceWriter.sendCmd(context, b);
    }
    
    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#mustBeFirstInQueue()
     */
    public boolean mustBeFirstInQueue() {
        return true;
    }
}
