/**
 * 
 */
package gps.connection;

/**
 * To be used in the receive context to represent a receiver state compatible
 * with the state of the connected device.<br>
 * Used to implement the State Design Pattern.
 * 
 * @author Mario
 * 
 */
public interface DecoderStateInterface { 
    /** Returns valid data from the serial link. */
    public Object getResponse(final GPSrxtx context);
    /** Indicates this state is stopped. Allows end of state operations. */
    public void exitState(final GPSrxtx context);
    /** Indicates this state is entered. Allows initialization operations. */
    public void enterState(final GPSrxtx context);
}
