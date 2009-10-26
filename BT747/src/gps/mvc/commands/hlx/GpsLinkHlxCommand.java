/**
 * 
 */
package gps.mvc.commands.hlx;

import gps.mvc.commands.GpsLinkNmeaCommand;

/**
 * @author Mario
 *
 */
public class GpsLinkHlxCommand extends GpsLinkNmeaCommand {
    protected String expectedReply;
    
    /**
     * 
     */
    public GpsLinkHlxCommand(final String holuxCmd, final String expectedReply) {
        super(holuxCmd);
        this.expectedReply = expectedReply;
    }

}
