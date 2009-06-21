// ********************************************************************
// *** BT 747 ***
// *** (c)2008 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// ********************************************************************
package net.sf.bt747.j2me.system;

public final class J2METhread implements java.lang.Runnable {

    protected java.lang.Thread jvThread;
    protected bt747.sys.interfaces.BT747Thread btThread = null;
    protected boolean running = false;

    public J2METhread(final bt747.sys.interfaces.BT747Thread t) {
        btThread = t;
    }

    public final void run() {
        running = true;
        // System.out.println("new Thread().run() succeed");
        while (btThread != null) {
            try {
                btThread.run();
                java.lang.Thread.sleep(2);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        running = false;
    }

}
