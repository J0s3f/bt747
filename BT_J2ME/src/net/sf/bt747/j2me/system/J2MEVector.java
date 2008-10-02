//********************************************************************
//***                           BT 747                             ***
//***                  (c)2008 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//********************************************************************

package net.sf.bt747.j2me.system;

import java.util.Stack;

import bt747.sys.interfaces.BT747Vector;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class J2MEVector extends Stack implements BT747Vector {   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public int getCount() {
        return this.elementCount;
    }
    
    public void del(final Object o) {
        super.removeElement(o);
    }
    
    public void del(final int i) {
        super.removeElementAt(i);
    }
    public Object[] toObjectArray() {
        Object[] a = new Object[super.elementCount];
        super.copyInto(a);
        return a;
    }

    public String[]   toStringArray() {
        String[] s= new String[elementCount];
        for (int i = 0; i < s.length; i++) {
            s[i]=(String)elementData[i];
        }
        
        return s;
    }
    
    public void mypush(final Object item) {
        super.push(item);
    }
}
