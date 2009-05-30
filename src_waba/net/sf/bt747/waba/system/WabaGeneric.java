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
//***  This layer was written for the SuperWaba toolset.           ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package net.sf.bt747.waba.system;

import waba.ui.MainWindow;

import moio.util.Enumeration;

import bt747.sys.interfaces.BT747Thread;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class WabaGeneric {

    // public static void addThread(final BT747Thread t, final boolean b) {
    // MainWindow.getMainWindow().addThread(t, b);
    // }
    //
    // public static void removeThread(final BT747Thread t) {
    // MainWindow.getMainWindow().removeThread(t);
    // }

    private final static moio.util.Hashtable h = new moio.util.Hashtable();
    private final static moio.util.Hashtable oos = new moio.util.Hashtable();

    // java.util.HashSet<Object> tt = new java.util.HashSet<Object>();

    // TODO: Improve next code - for the moment it is functional.

    public final static void addThread(final BT747Thread t, final boolean b) {
        // MainWindow.getMainWindow().addThread(t, b);
        if (!oos.contains(t)) {
            removeIfStoppedThread(t);
        }
        if (!oos.contains(t)) {
            // Log.debug("Adding " + t);
            final WabaThread mt = new WabaThread(t);
            MainWindow.getMainWindow().addThread(mt, b);
            t.started();
            if (mt != null) {
                // System.out.println("new Thread() succeed");
            } else {
                bt747.sys.Generic.debug("new Thread() failed", null);
            }
            h.put(mt, h);
            oos.put(t, oos);
        } else {
            if (bt747.sys.Generic.isDebug()) {
                bt747.sys.Generic.debug("Already present thread " + t, null);
            }
        }
    }

    public final static void removeThread(final BT747Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        final Enumeration e = h.keys();
        while (e.hasMoreElements()) {
            final WabaThread tt = (WabaThread) e.nextElement();
            if (tt.btThread.equals(t)) {
                // tt.jvThread.stop();
                tt.btThread = null; // When this is null, the thread stops.
                h.remove(tt);
                oos.remove(t);
            }
        }

    }

    public final static void removeIfStoppedThread(final BT747Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        final Enumeration e = h.keys();
        while (e.hasMoreElements()) {
            final WabaThread tt = (WabaThread) e.nextElement();
            if (tt.btThread.equals(t)) {
                // tt.jvThread.stop();
                if (tt.running) {
                    // When this is null, the thread stops.)
                    h.remove(tt);
                    oos.remove(t);
                }
            }
        }

    }

}
