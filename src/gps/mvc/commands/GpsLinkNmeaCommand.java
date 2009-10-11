/**
 * 
 */
package gps.mvc.commands;

import gps.connection.GPSrxtx;
import gps.connection.NMEAWriter;

/**
 * @author Mario De Weerd
 * 
 * TODO: Could add 'check' method to validate if an acknowledge corresponds to
 * the command.
 * 
 */
public class GpsLinkNmeaCommand implements GpsLinkExecCommand {
    private final String nmeaCmd;
    
    public GpsLinkNmeaCommand(final String nmeaString) {
        nmeaCmd = nmeaString;
    }
    
    public String getNmeaValue() {
        return nmeaCmd;
    }

    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsRxtxExecCommand#execute(gps.connection.GPSrxtx)
     */
    public void execute(final GPSrxtx context) {
        NMEAWriter.sendPacket(context, nmeaCmd);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return nmeaCmd;
    }
}
