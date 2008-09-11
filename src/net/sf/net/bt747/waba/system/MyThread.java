//********************************************************************
//***                           BT 747                             ***
//***                  (c)2008 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//********************************************************************
package net.sf.net.bt747.waba.system;

public class MyThread implements waba.sys.Thread {

    public java.lang.Thread jvThread;
    public bt747.interfaces.BT747Thread btThread = null;
    public boolean running = false;

    public MyThread(final bt747.interfaces.BT747Thread t) {
        btThread = t;
    }

    public final void run() {
        btThread.run();
    }

    public void started() {
        // TODO Auto-generated method stub

    }

    public void stopped() {
        // TODO Auto-generated method stub

    }

}
