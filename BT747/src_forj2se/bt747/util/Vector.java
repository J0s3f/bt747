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
public class Vector extends java.util.Stack<Object> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public int getCount() {
        return this.elementCount;
    }

    public void del(Object o) {
        remove(o);
    }
    
    public boolean del(int i) {
        try {
            remove(i);
            return true;
        } catch (Exception e) {
            return false;
        }
            
    }
    public Object[] toObjectArray() {
        return toArray();
    }

    public String[]   toStringArray() {
        String[] s= new String[elementCount];
        for (int i = 0; i < s.length; i++) {
            s[i]=(String)elementData[i];
            //System.out.println(s[i]);
        }
        
        return s;
    }
    
    

}
