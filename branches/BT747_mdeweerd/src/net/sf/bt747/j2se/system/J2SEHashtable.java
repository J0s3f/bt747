/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.j2se.system;

import bt747.interfaces.BT747Hashtable;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class J2SEHashtable  implements
        BT747Hashtable {

    java.util.Hashtable<Object, Object> hash;
    static final long serialVersionUID = 1L;

    /**
     * @param initialCapacity
     */
    public J2SEHashtable(int initialCapacity) {
        hash = new java.util.Hashtable<Object, Object>(initialCapacity);
    }

    public final Object get(Object arg1) {
        return hash.get(arg1);
    }

    public final Object put(Object arg1, Object arg2) {
        return hash.put(arg1, arg2);
    }

}
