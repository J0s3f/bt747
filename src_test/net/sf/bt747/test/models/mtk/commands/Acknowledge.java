/**
 * 
 */
package net.sf.bt747.test.models.mtk.commands;


import gps.mvc.GPSLinkHandler;
import gps.mvc.commands.GpsLinkExecCommand;

/**
 * @author Mario
 *
 */
public class Acknowledge implements GpsLinkExecCommand  {

    Acknowledge(String[] nmea) {
        
    }
    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSLinkHandler)
     */
    public void execute(GPSLinkHandler context) {
        // TODO Auto-generated method stub
        
    }

}
