//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     seesite@bt747.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  This layer was written for the SuperWaba toolset.           ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package net.sf.bt747.waba.system;

import bt747.sys.Generic;

public final class WabaThread implements waba.sys.Thread {

    protected bt747.sys.interfaces.BT747Thread btThread = null;
    protected boolean running = false;

    public WabaThread(final bt747.sys.interfaces.BT747Thread t) {
        btThread = t;
    }
    private boolean runOngoing = false;
    /* (non-Javadoc)
     * @see waba.sys.Thread#run()
     * 
     * Handling of runOngoing is probably not perfect...
     */
    public final void run() {
        if(btThread!=null) {
            if(!runOngoing) {
                runOngoing = true;
                try {
                    btThread.run();
                } catch (Exception e) {
                    Generic.debug("Problem in thread", e);
                }
                runOngoing = false;
            }
        }
    }

    public void started() {
        // TODO Auto-generated method stub

    }

    public void stopped() {
        // TODO Auto-generated method stub

    }

}
