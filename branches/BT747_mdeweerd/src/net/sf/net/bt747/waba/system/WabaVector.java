/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.net.bt747.waba.system;

import bt747.interfaces.BT747Vector;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class WabaVector extends waba.util.Vector implements BT747Vector {
    public final Object elementAt(int i) {
        return items[i];
    }
    
    public final String[] toStringArray() {
        return (String[]) toObjectArray();
    }
    
    public final void mypush(Object item) {
        super.push(item);
    }
}
