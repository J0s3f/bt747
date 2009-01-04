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

import java.util.Enumeration;
import java.util.Hashtable;

import bt747.sys.interfaces.BT747Hashtable;

/** J2ME Implementation of a hashtable.
 */
public final class J2MEHashtable implements
        BT747Hashtable {
    
    private final Hashtable hash;
    private Enumeration enumerator;
    
    public J2MEHashtable(final int a) {
        hash = new Hashtable(a);
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#hasNext()
     */
    public final boolean hasNext() {
        return enumerator.hasMoreElements();
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#iterator()
     */
    public final BT747Hashtable iterator() {
        // TODO Auto-generated method stub
        enumerator = hash.keys();
        return this;
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#next()
     */
    public final Object nextKey() {
        return enumerator.nextElement();
    }
    
    public final void remove(final Object o) {
        hash.remove(o);
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#get(java.lang.Object)
     */
    public final Object get(final Object key) {
        return hash.get(key);
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#put(java.lang.Object, java.lang.Object)
     */
    public final Object put(final Object key, final Object value) {
        return hash.put(key, value);
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#size()
     */
    public int size() {
        return hash.size();
    }
}
