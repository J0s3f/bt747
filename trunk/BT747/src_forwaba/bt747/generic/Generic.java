/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.generic;

import waba.ui.MainWindow;

import bt747.sys.Thread;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Generic {

    public static void addThread(final Thread t, final boolean b) {
        MainWindow.getMainWindow().addThread(t, b);
    }

    public static void removeThread(final Thread t) {
        MainWindow.getMainWindow().removeThread(t);
    }
    
    public static double pow(double x, double y) {
        return Math.pow(x,y);
    }

    public static double acos(final double x) {
        return Math.acos(x);
    }

}
