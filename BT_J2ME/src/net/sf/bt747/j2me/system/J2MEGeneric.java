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
package net.sf.bt747.j2me.system;

import java.util.Enumeration;
import java.util.Hashtable;

import org.j4me.logging.Log;

import bt747.sys.interfaces.BT747Thread;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class J2MEGeneric {

    private static Hashtable h = new Hashtable();
    private static Hashtable oos = new Hashtable();

    // java.util.HashSet<Object> tt = new java.util.HashSet<Object>();

    // TODO: Improve next code - for the moment it is functional.

    public static void addThread(final BT747Thread t, final boolean b) {
        // MainWindow.getMainWindow().addThread(t, b);
        if (!oos.contains(t)) {
            removeIfStoppedThread(t);
        }
        if (!oos.contains(t)) {
            Log.debug("Adding " + t);
            J2METhread mt = new J2METhread(t);
            t.started();
            mt.jvThread = new java.lang.Thread(mt);
            if (mt != null) {
                // System.out.println("new Thread() succeed");
            } else {
                Log.debug("new Thread() failed");
            }
            mt.jvThread.start();
            h.put(mt, h);
            oos.put(t, oos);
        } else {
            Log.debug("Already present thread " + t);
        }
    }

    public static void removeThread(final BT747Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        Enumeration e = h.keys();
        while (e.hasMoreElements()) {
            J2METhread tt = (J2METhread) e.nextElement();
            if (tt.btThread.equals(t)) {
                // tt.jvThread.stop();
                tt.btThread = null; // When this is null, the thread stops.
                h.remove(tt);
                oos.remove(t);
            }
        }

    }

    public static void removeIfStoppedThread(final BT747Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        Enumeration e = h.keys();
        while (e.hasMoreElements()) {
            J2METhread tt = (J2METhread) e.nextElement();
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

    public static double pow(final double x, final double y) {
        return Float11.pow(x, y);
    }

    public static double acos(final double x) {
        return Float11.acos(x);
    }

    public static void debug(final String s, final Throwable e) {
        Log.debug(s, e);
    }
}
