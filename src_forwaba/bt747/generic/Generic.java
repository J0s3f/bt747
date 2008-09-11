/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.generic;

import bt747.interfaces.BT747Thread;
import bt747.interfaces.Interface;


/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class Generic {
    public static final void debug(final String s, 
            Throwable e) {
        Interface.tr.debug(s,e);
    }
    
    public final static double pow(final double x, final double y) {
        return Interface.tr.pow(x,y);
    }

    public final static double acos(final double x) {
        return Interface.tr.acos(x);
    }
    
    public final static void addThread(final BT747Thread o, final boolean highPrio) {
        Interface.tr.addThread(o,highPrio);
    }
    
    public final static void removeThread(final BT747Thread o) {
        Interface.tr.removeThread(o); 
    }
}
