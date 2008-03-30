/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.util;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Vector extends waba.util.Vector {
    public final Object elementAt(int i) {
        return items[i];
    }
    
    public String[] toStringArray() {
        return (String[]) toObjectArray();
    }
    
    public final void addElement(Object obj) {
        // TODO Auto-generated method stub
        super.add(obj);
    } 
    
    public final void mypush(Object item) {
        super.push(item);
    }
}
