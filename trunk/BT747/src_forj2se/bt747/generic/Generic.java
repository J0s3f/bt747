/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.generic;

import moio.util.HashSet;
import moio.util.Iterator;

import bt747.sys.MyThread;
import bt747.sys.Thread;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class Generic {

    static HashSet h = new HashSet();
    static HashSet oos = new HashSet();

    //java.util.HashSet<Object> tt = new java.util.HashSet<Object>();
    
    // TODO: Improve next code - for the moment it is functional.

    public static void addThread(Thread t, final boolean b) {
        // MainWindow.getMainWindow().addThread(t, b);
        if (!oos.contains(t)) {
            System.out.println("Adding " + t);
            MyThread mt = new MyThread(t);
            t.started();
            mt.jvThread = new java.lang.Thread(mt);
            if (mt != null) {
                // System.out.println("new Thread() succeed");
            } else {
                System.out.println("new Thread() failed");
            }
            mt.jvThread.start();
            h.add(mt);
            oos.add(t);
        } else {
            System.out.println("Already present thread " + t);
        }
    }

    public static void removeThread(Thread t) {
        // MainWindow.getMainWindow().removeThread(t);
        Iterator it = h.iterator();
        while (it.hasNext()) {
            MyThread tt = (MyThread) it.next();
            if (tt.btThread.equals(t)) {
                // tt.jvThread.stop();
                tt.btThread = null; // When this is null, the thread stops.
                h.remove(tt);
            }
        }

    }

}
