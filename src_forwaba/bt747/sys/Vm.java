/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

import bt747.interfaces.Interface;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Vm {
    public static final int getTimeStamp() {
        return Interface.tr.getTimeStamp();
    }

    public static final void debug(final String s) {
        Interface.tr.debug(getTimeStamp() + " - " + s);
    }
}
