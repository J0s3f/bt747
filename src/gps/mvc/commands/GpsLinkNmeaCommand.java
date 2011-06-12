/**
 * 
 */
package gps.mvc.commands;

import gps.connection.GPSrxtx;
import gps.connection.NMEAWriter;

/**
 * @author Mario De Weerd
 * 
 *         TODO: Could add 'check' method to validate if an acknowledge
 *         corresponds to the command.
 * 
 */
public class GpsLinkNmeaCommand implements GpsLinkExecCommand {
    private final String nmeaCmd;
    private final boolean hasAck;

    public GpsLinkNmeaCommand(final String nmeaString) {
        this(nmeaString, true);
    }

    /**
     * 
     */
    public GpsLinkNmeaCommand(final String nmeaString, final boolean hasAck) {
        nmeaCmd = nmeaString;
        this.hasAck = hasAck;
    }

    public String getNmeaValue() {
        return nmeaCmd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.commands.GpsLinkExecCommand#mustBeFirstInQueue()
     */
    public boolean mustBeFirstInQueue() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.mvc.commands.GpsRxtxExecCommand#execute(gps.connection.GPSrxtx)
     */
    public void execute(final GPSrxtx context) {
        NMEAWriter.sendPacket(context, nmeaCmd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return nmeaCmd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.commands.GpsLinkExecCommand#hasAck()
     */
    public boolean hasAck() {
        return hasAck;
    }
}
