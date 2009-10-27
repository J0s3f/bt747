package net.sf.bt747.j2me.system;

import java.util.Enumeration;

import bt747.sys.interfaces.BT747HashSet;

public final class J2MEHashSet implements BT747HashSet {
    private final java.util.Hashtable hs = new java.util.Hashtable();
    private Enumeration iterator;

    public final BT747HashSet iterator() {
        iterator = hs.keys();
        return this;
    }

    public final void add(final Object o) {
        hs.put(o, o);
    }

    public final void remove(final Object o) {
        hs.remove(o);
    }

    public final boolean hasNext() {
        return iterator.hasMoreElements();
    }

    public final Object next() {
        return iterator.nextElement();
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747HashSet#count()
     */
    public int count() {
        return hs.size();
    }
}
