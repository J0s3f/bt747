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
package bt747.sys;

import bt747.interfaces.BT747Semaphore;
import bt747.interfaces.Interface;

/**
 * @author Mario De Weerd
 */
public final class Semaphore {
    private BT747Semaphore s;

    public Semaphore(final int value) {
        s = Interface.tr.getSemaphoreInstance(value);
    }

    public final void down() {
        s.down();
    }

    public final void up() {
        s.up();
        // notify();
    }
}
