/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

import bt747.sys.interfaces.BT747Vector;
import bt747.sys.interfaces.Interface;


/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class Vector {

    BT747Vector vector;
    
    public Vector() {
        vector = Interface.tr.getVectorInstance();
    }
    
    public final void addElement(final Object o) {
        vector.addElement(o);
    }
    
    public final int size() {
        return vector.size();
    }

    public final Object elementAt(final int arg0) {
        return vector.elementAt(arg0);
    }

    public final void removeAllElements() {
        vector.removeAllElements();
    }
    
    public final Object pop() {
        return vector.pop();
    }
    public final void removeElementAt(final int index) {
        vector.removeElementAt(index);
    }

    public final void mypush(Object item) {
        vector.mypush(item);
        
    }

    public final String[] toStringArray() {
        return vector.toStringArray();
    }

}
