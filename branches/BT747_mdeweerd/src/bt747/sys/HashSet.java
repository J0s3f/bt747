package bt747.sys;

import bt747.sys.interfaces.BT747HashSet;

public final class HashSet {

    private final BT747HashSet hs = Interface.tr.getHashSetInstance();

    public final BT747HashSet iterator() {
        return hs.iterator();
    }
    
    public final void add(final Object o) {
        hs.add(o);
    }
    
    public final void remove(final Object o) {
        hs.remove(o);
    }
    
    public final boolean hasNext() {
        return hs.hasNext();
    }
    
    public final Object next() {
        return hs.next();
    }
}
