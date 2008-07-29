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
package bt747.model;

import gps.GpsEvent;

/**
 * Defines some events for the gps package.
 * 
 * @author Mario De Weerd
 */
public class ModelEvent extends GpsEvent{
    public static final int CONVERSION_STARTED = 256;
    public static final int CONVERSION_ENDED = 257;
    public static final int WORKDIRPATH_UPDATE = 258;
    public static final int OUTPUTFILEPATH_UPDATE = 259;
    public static final int LOGFILEPATH_UPDATE = 260;
    public static final int INCREMENTAL_CHANGE = 261;
    public static final int TRK_VALID_CHANGE = 262;
    public static final int TRK_RCR_CHANGE = 263;
    public static final int WAY_VALID_CHANGE = 264;
    public static final int WAY_RCR_CHANGE = 265;
    public static final int CONNECTED   = 266;
    public static final int DISCONNECTED   = 267;
    public static final int FILE_LOG_FORMAT_UPDATE = 268;
    /**
     * A setting changed - int param = index of changed setting
     */
    public static final int SETTING_CHANGE = 269;

    public ModelEvent(int type, Object arg) {
        super(type,arg);
    }
    
    public ModelEvent(GpsEvent event) {
        super(event.getType(),event.getArg());
    }
}
