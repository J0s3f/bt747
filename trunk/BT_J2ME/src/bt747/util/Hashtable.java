/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.util;

import bt747.interfaces.BT747Hashtable;
import bt747.interfaces.Interface;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class Hashtable implements BT747Hashtable {

    private BT747Hashtable hash;

    /**
     * @param initialCapacity
     */
    public Hashtable(int initialCapacity) {
        hash = Interface.tr.getHashtableInstance(initialCapacity);
    }

    public final Object get(Object arg1) {
        return hash.get(arg1);
    }

    public final Object put(Object arg1, Object arg2) {
        return hash.put(arg1, arg2);
    }

}
