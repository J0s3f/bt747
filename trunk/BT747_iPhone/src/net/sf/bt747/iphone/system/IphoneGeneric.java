/*
 * Created on 14 nov. 2007
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.iphone.system;

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
public final class IphoneGeneric {

    private static final HashSet<Object> h = new HashSet<Object>();
    private static final HashSet<Object> oos = new HashSet<Object>();

    // java.util.HashSet<Object> tt = new java.util.HashSet<Object>();

    // TODO: Improve next code - for the moment it is functional.

    public static void addThread(final BT747Thread t, final boolean b) {
        if (oos.contains(t)) {
            removeIfStoppedThread(t);
        }
        if (!oos.contains(t)) {
            if (Generic.isDebug()) {
                Generic.debug("Adding " + t, null);
            }
            final IphoneThread mt = new IphoneThread(t);
            t.started();
            mt.jvThread = new java.lang.Thread(mt, t.toString());
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
            final IphoneThread tt = (IphoneThread) it.next();
            if (tt.btThread.equals(t)) {
                tt.setRunning(false);
                h.remove(tt);
                oos.remove(t);
            }
        }

    }

    public static void removeIfStoppedThread(final BT747Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        final Iterator<Object> it = h.iterator();
        while (it.hasNext()) {
            final IphoneThread tt = (IphoneThread) it.next();
            if (tt.btThread.equals(t)) {
                if (!tt.isRunning()) {
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
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            printWriter.println(s);
            if (e != null) {
                e.printStackTrace(printWriter);
            }
            final String message = result.toString();

            System.out.print(message);

            final Iterator<IphoneMessageListener> it = listeners.iterator();
            while (it.hasNext()) {
                try {
                    IphoneMessageListener l;
                    l = it.next();
                    l.postMessage(message);
                } catch (final Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    private static final HashSet<IphoneMessageListener> listeners = new HashSet<IphoneMessageListener>();

    /** add a listener to event thrown by this class */
    public static final void addListener(final IphoneMessageListener l) {
        listeners.add(l);
    }

    public static final void removeListener(final IphoneMessageListener l) {
        listeners.remove(l);
    }

}
