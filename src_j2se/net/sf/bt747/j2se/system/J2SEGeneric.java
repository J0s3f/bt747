/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.j2se.system;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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

    private static final HashSet<Object> h = new HashSet<Object>();
    private static final HashSet<Object> oos = new HashSet<Object>();

    // java.util.HashSet<Object> tt = new java.util.HashSet<Object>();

    // TODO: Improve next code - for the moment it is functional.

    public static void addThread(final BT747Thread t, final boolean b) {
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
                Generic.debug("new Thread() failed", null);
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

    public static void removeThread(final BT747Thread t) {
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

    public static void removeIfStoppedThread(final Thread t) {
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

    public static double pow(final double x, final double y) {
        return Math.pow(x, y);
    }

    public static double acos(final double x) {
        return Math.acos(x);
    }

    public static void debug(final String s, final Throwable e) {
        if (listeners.isEmpty()) {
            System.out.println(s);
            if (e != null) {
                e.printStackTrace();
            }
        } else {
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            printWriter.println(s);
            if (e != null) {
                e.printStackTrace(printWriter);
            }
            String message = result.toString();

            System.out.print(message);

            Iterator<J2SEMessageListener> it = listeners.iterator();
            while (it.hasNext()) {
                try {
                    J2SEMessageListener l;
                    l = it.next();
                    l.postMessage(message);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    private static final HashSet<J2SEMessageListener> listeners = new HashSet<J2SEMessageListener>();

    /** add a listener to event thrown by this class */
    public static final void addListener(final J2SEMessageListener l) {
        listeners.add(l);
    }

    public static final void removeListener(final J2SEMessageListener l) {
        listeners.remove(l);
    }

}
