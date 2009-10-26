/*
 * Created on 14 nov. 2007
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.j2se.system;

import java.util.Enumeration;

import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario De Weerd
 */
public final class J2SEHashtable implements BT747Hashtable {

    private final java.util.Hashtable<Object, Object> hash;
    private Enumeration<Object> iterator = null;

    static final long serialVersionUID = 1L;

    /**
     * @param initialCapacity
     */
    public J2SEHashtable(final int initialCapacity) {
        hash = new java.util.Hashtable<Object, Object>(initialCapacity);
    }

    public final Object get(final Object arg1) {
        return hash.get(arg1);
    }

    public final Object put(final Object arg1, final Object arg2) {
        return hash.put(arg1, arg2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747Hashtable#hasNext()
     */
    public boolean hasNext() {
        if (iterator != null) {
            return iterator.hasMoreElements();
        } else {
            bt747.sys.Generic.debug("Iterator is null");
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747Hashtable#iterator()
     */
    public BT747Hashtable iterator() {
        iterator = hash.keys();
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747Hashtable#next()
     */
    public Object nextKey() {
        // TODO Auto-generated method stub
        return iterator.nextElement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747Hashtable#remove(java.lang.Object)
     */
    public void remove(final Object o) {
        hash.remove(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747Hashtable#getSize()
     */
    public int size() {
        return hash.size();
    }
}
