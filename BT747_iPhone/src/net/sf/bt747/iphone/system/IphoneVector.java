/*
 * Created on 14 nov. 2007
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package net.sf.bt747.iphone.system;

import bt747.sys.interfaces.BT747Vector;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class IphoneVector extends java.util.Stack<Object> implements
        BT747Vector {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public final int getCount() {
        return elementCount;
    }

    public final void del(final Object o) {
        remove(o);
    }

    public final void del(final int i) {
        try {
            remove(i);
        } catch (final Exception e) {
            // return false;
        }

    }

    public final Object[] toObjectArray() {
        return toArray();
    }

    public final String[] toStringArray() {
        final String[] s = new String[elementCount];
        for (int i = 0; i < s.length; i++) {
            s[i] = (String) elementData[i];
            // System.out.println(s[i]);
        }

        return s;
    }

    public final void mypush(final Object item) {
        super.push(item);
    }
}
