/**
 * 
 */
package gps.mvc.commands;

import gps.connection.GPSrxtx;

/**
 * A {@link GPSLinkCommand} can be put on the command queue where the
 * {@link #execute(GPSLinkHandler)} method will be called with the
 * {@link GPSLinkHandler} context. This allows for improved flexibility in
 * extending the link interface protocol and managing link state changes.
 * 
 * Some concrete Commands can probably be implemented as Singletons in some
 * cases but that may not be really needed for performance. If so, a
 * getInstance method should be provided in the concrete class.
 * 
 * @author Mario De Weerd
 * 
 */
public interface GpsLinkExecCommand {
    /**
     * Do what needs to be done for the specific command.
     * 
     * @param context
     *                A reference to the link on which this command operates.
     */
    public void execute(final GPSrxtx context);
    
}
