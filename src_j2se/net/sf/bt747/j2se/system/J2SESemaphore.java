package net.sf.bt747.j2se.system;

import java.util.concurrent.Semaphore;

import bt747.sys.interfaces.BT747Semaphore;

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
/**
 * @author Mario De Weerd
 */
public final class J2SESemaphore implements BT747Semaphore {
    private Semaphore available;

    public J2SESemaphore(int value) {
        available = new Semaphore(value,true);
    }

    public void down() {
        try {
            available.acquire();
        } catch (InterruptedException e) {
        }
    }

    public void up() {
        available.release();
    }
}
