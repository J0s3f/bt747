//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package net.sourceforge.bt747.bt747.model;

import net.sourceforge.bt747.gps.GpsEvent;

/**
 * Defines some events for the gps package.
 * 
 * @author Mario De Weerd
 */
public class ModelEvent extends GpsEvent {
    public static final int CONVERSION_STARTED = getNextAvailableEventId();
    public static final int CONVERSION_ENDED = getNextAvailableEventId();
    public static final int WORKDIRPATH_UPDATE = getNextAvailableEventId();
    public static final int OUTPUTFILEPATH_UPDATE = getNextAvailableEventId();
    public static final int LOGFILEPATH_UPDATE = getNextAvailableEventId();
    public static final int INCREMENTAL_CHANGE = getNextAvailableEventId();
    public static final int TRK_VALID_CHANGE = getNextAvailableEventId();
    public static final int TRK_RCR_CHANGE = getNextAvailableEventId();
    public static final int WAY_VALID_CHANGE = getNextAvailableEventId();
    public static final int WAY_RCR_CHANGE = getNextAvailableEventId();
    public static final int CONNECTED   = getNextAvailableEventId();
    public static final int DISCONNECTED   = getNextAvailableEventId();
    public static final int FILE_LOG_FORMAT_UPDATE = getNextAvailableEventId();

    public ModelEvent(int type, Object arg) {
        super(type,arg);
    }
    
    public ModelEvent(GpsEvent event) {
        super(event.getType(),event.getArg());
    }
}