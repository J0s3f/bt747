/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.j2se.system;

import java.util.HashSet;
import java.util.Iterator;

import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Thread;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class J2SEGeneric {

    private final static HashSet<Object> h = new HashSet<Object>();
    private final static HashSet<Object> oos = new HashSet<Object>();

    // java.util.HashSet<Object> tt = new java.util.HashSet<Object>();

    // TODO: Improve next code - for the moment it is functional.

    public final static void addThread(final BT747Thread t, final boolean b) {
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

    public final static void removeThread(final BT747Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        final Iterator<Object> it = h.iterator();
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

    public final static void removeIfStoppedThread(final Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        final Iterator<Object> it = h.iterator();
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

    public final static double pow(final double x, final double y) {
        return Math.pow(x, y);
    }

    public final static double acos(final double x) {
        return Math.acos(x);
    }
}
