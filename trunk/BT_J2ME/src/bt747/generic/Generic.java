/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.generic;

import java.util.Enumeration;
import java.util.Hashtable;

import org.j4me.logging.Log;

import bt747.sys.MyThread;
import bt747.sys.Thread;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Generic {

    private static Hashtable h = new Hashtable();
    private static Hashtable oos = new Hashtable();

    // java.util.HashSet<Object> tt = new java.util.HashSet<Object>();

    // TODO: Improve next code - for the moment it is functional.

    public static void addThread(final Thread t, final boolean b) {
        // MainWindow.getMainWindow().addThread(t, b);
        if (!oos.contains(t)) {
            removeIfStoppedThread(t);
        }
        if (!oos.contains(t)) {
            Log.debug("Adding " + t);
            MyThread mt = new MyThread(t);
            t.started();
            mt.jvThread = new java.lang.Thread(mt);
            if (mt != null) {
                // System.out.println("new Thread() succeed");
            } else {
                Log.debug("new Thread() failed");
            }
            mt.jvThread.start();
            h.put(mt,h);
            oos.put(t,oos);
        } else {
            Log.debug("Already present thread " + t);
        }
    }

    public static void removeThread(final Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        Enumeration e = h.keys();
        while (e.hasMoreElements()) {
            MyThread tt = (MyThread) e.nextElement();
            if (tt.btThread.equals(t)) {
                // tt.jvThread.stop();
                tt.btThread = null; // When this is null, the thread stops.
                h.remove(tt);
                oos.remove(t);
            }
        }

    }

    public static void removeIfStoppedThread(final Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        Enumeration e = h.keys();
        while (e.hasMoreElements()) {
            MyThread tt = (MyThread) e.nextElement();
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
    
    public static void debug(final String s, final Exception e) {
        Log.debug(s,e);
    }
}
