// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps;


import gps.connection.GPSrxtx;

/**
 * Refactoring the original GPSstate in a Model and a Controller.
 * This is a placeholder for backward compatibility (for the time being).
 * 
 * @author Mario De Weerd
 * @see GPSrxtx
 * 
 */
/* Final for the moment */
public final class GPSstate extends gps.mvc.Model  {
    /**
     * Initialiser
     * 
     * 
     */
    public GPSstate(final GPSrxtx gpsRxTx) {
        super(gpsRxTx);
    }

}
