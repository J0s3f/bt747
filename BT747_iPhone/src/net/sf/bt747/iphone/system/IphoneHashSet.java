package net.sf.bt747.iphone.system;

import java.util.HashSet;
import java.util.Iterator;

import bt747.sys.interfaces.BT747HashSet;

public final class IphoneHashSet implements BT747HashSet {
    private final HashSet<Object> hs = new HashSet<Object>();
    private Iterator<Object> iterator = null;

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

}
