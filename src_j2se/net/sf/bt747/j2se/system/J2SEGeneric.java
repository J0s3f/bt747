/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.j2se.system;

import moio.util.HashSet;
import moio.util.Iterator;

import bt747.sys.Generic;
import bt747.sys.Vm;
import bt747.sys.interfaces.BT747Thread;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class J2SEGeneric {

    private static HashSet h = new HashSet();
    private static HashSet oos = new HashSet();

    // java.util.HashSet<Object> tt = new java.util.HashSet<Object>();

    // TODO: Improve next code - for the moment it is functional.

    public static void addThread(BT747Thread t, final boolean b) {
        if (!oos.contains(t)) {
            if (Generic.isDebug()) {
                Generic.debug("Adding " + t, null);
            }
            J2SEThread mt = new J2SEThread(t);
            t.started();
            mt.jvThread = new java.lang.Thread(mt);
            if (mt != null) {
                // System.out.println("new Thread() succeed");
            } else {
                Generic.debug("new Thread() failed",null);
            }
            mt.jvThread.start();
            h.add(mt);
            oos.add(t);
        } else {
            if (Generic.isDebug()) {
                Generic.debug("Already present thread " + t, null);
            }
        }
    }

    public static void removeThread(BT747Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        Iterator it = h.iterator();
        while (it.hasNext()) {
            J2SEThread tt = (J2SEThread) it.next();
            if (tt.btThread.equals(t)) {
                // tt.jvThread.stop();
                tt.btThread = null; // When this is null, the thread stops.
                h.remove(tt);
                oos.remove(t);
            }
        }

    }

    public static void removeIfStoppedThread(Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        Iterator it = h.iterator();
        while (it.hasNext()) {
            J2SEThread tt = (J2SEThread) it.next();
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

    public static double pow(double x, double y) {
        return Math.pow(x, y);
    }

    public static double acos(final double x) {
        return Math.acos(x);
    }

    public static void debug(String s, Throwable e) {
        Vm.debug(s);
        if (e != null) {
            e.printStackTrace();
        }
    }

}
