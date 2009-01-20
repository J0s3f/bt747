package net.sf.bt747.waba.system;

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
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
/**
 * @author Mario De Weerd
 */
public final class WabaSemaphore implements BT747Semaphore {
    private int value;

    public WabaSemaphore(final int value) {
        this.value = value;
    }

    public final void down() {
        while (value <= 0) {
            try {
                wait();
            } catch (final Exception e) {
            }
        }
        --value;
    }

    public final void up() {
        ++value;
        //notify();
    }
}
