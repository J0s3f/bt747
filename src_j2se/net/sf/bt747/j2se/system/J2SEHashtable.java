/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.j2se.system;

import java.util.Enumeration;

import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario De Weerd
 */
public final class J2SEHashtable  implements
        BT747Hashtable {

    private final java.util.Hashtable<Object, Object> hash;
    private Enumeration<Object> iterator = null;
    
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

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#hasNext()
     */
    public boolean hasNext() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#iterator()
     */
    public BT747Hashtable iterator() {
        iterator = hash.keys();
        return this;
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#next()
     */
    public Object next() {
        // TODO Auto-generated method stub
        return iterator.nextElement();
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#remove(java.lang.Object)
     */
    public void remove(Object o) {
        hash.remove(o);
    }
}
