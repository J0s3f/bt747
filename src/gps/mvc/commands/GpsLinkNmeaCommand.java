/**
 * 
 */
package gps.mvc.commands;

/**
 * @author Mario De Weerd
 * 
 * TODO: Could add 'check' method to validate if an acknowledge corresponds to
 * the command.
 * 
 */
public class GpsLinkNmeaCommand {
    private final String nmeaValue;
    
    GpsLinkNmeaCommand(final String nmeaString) {
        nmeaValue = nmeaString;
    }
    
    public String getNmeaValue() {
        return nmeaValue;
    }
}
