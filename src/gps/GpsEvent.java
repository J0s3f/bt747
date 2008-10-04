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
package gps;

/**
 * Defines some events for the gps package.
 * 
 * @author Mario De Weerd
 */
public class GpsEvent {
    public static final int DATA_UPDATE = 1;
    public static final int LOG_FORMAT_UPDATE = 2;
    public static final int GPRMC = 3;
    public static final int GPGGA = 4;
    public static final int LOG_DOWNLOAD_STARTED = 5;
    public static final int DOWNLOAD_STATE_CHANGE = 6;
    public static final int LOG_DOWNLOAD_DONE = 7;
    public static final int DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY = 8;
    public static final int ERASE_ONGOING_NEED_POPUP = 9;
    public static final int ERASE_DONE_REMOVE_POPUP = 10;
    public static final int COULD_NOT_OPEN_FILE = 11;
    //public static final int DEBUG_MSG = 12;
    public static final int GPS_FIX_DATA = 13;
    public static final int LOG_DOWNLOAD_SUCCESS = 14;

    private int type;
    private Object arg;

    public GpsEvent(final int type) {
        this.type = type;
        arg = null;
    }

    public GpsEvent(final int type, final Object arg) {
        this.type = type;
        this.arg = arg;
    }

    public final int getType() {
        return type;
    }

    public final Object getArg() {
        return arg;
    }

}
