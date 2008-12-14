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
package gps;

import bt747.ui.Event;

/** Defines some events for the gps package
 * 
 * @author Mario De Weerd
 */
public class GpsEvent extends Event {
    public static final int DATA_UPDATE = bt747.ui.Event.getNextAvailableEventId();
    public static final int CONNECTED   = getNextAvailableEventId();
    public static final int GPRMC       = getNextAvailableEventId();
    public static final int GPGGA       = getNextAvailableEventId();
    
    public GpsEvent(int type, Object target, int timeStamp)
    {
        super(type,target,timeStamp);
    }
    
    
}