/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.j2se.system;


/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Vm {
    public static final String ERASE_DEBUG = "Erase Debug";
    private static final long appStartTime=System.currentTimeMillis(); 
    
    public static final int getTimeStamp() {
        // Returns the time in ms since the program started.
        //TODO: Find function
        return (int)(System.currentTimeMillis()-appStartTime);
        //return waba.sys.Vm.getTimeStamp();
        //Date.
    }
    
    public static final void debug(final String s) {
        System.out.print(getTimeStamp()+" - ");
        System.out.println(s);
    }

}
