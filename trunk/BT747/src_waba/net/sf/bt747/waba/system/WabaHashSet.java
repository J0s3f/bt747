package net.sf.bt747.waba.system;

import bt747.sys.interfaces.BT747HashSet;

public final class WabaHashSet implements BT747HashSet {
    private final moio.util.HashSet hs = new moio.util.HashSet();
    private moio.util.Iterator iterator = null;

    public final BT747HashSet iterator() {
        iterator = hs.iterator();
        return this;
    }
    
    public final void add(final Object o) {
        hs.add(o);
    }
    
    public final void remove(final Object o) {
        hs.remove(o);
    }
    
    public final boolean hasNext() {
        return iterator.hasNext();
    }
    
    public final Object next() {
        return iterator.next();
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747HashSet#count()
     */
    public int count() {
        return hs.size();
    }

}
