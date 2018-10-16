//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     seesite@bt747.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  This layer was written for the SuperWaba toolset.           ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package net.sf.bt747.waba.system;

import waba.util.Hashtable;
import waba.util.Vector;

import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class WabaHashtable implements BT747Hashtable {

    private final Hashtable hs;
    private Vector iterator = null;
    private int iteratorIdx;

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.waba.system.BT747Hashtable#iterator()
     */
    public final BT747Hashtable iterator() {
        iterator = hs.getKeys();
        iteratorIdx = 0;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.waba.system.BT747Hashtable#put(java.lang.Object,
     *      java.lang.Object)
     */
    public final Object put(final Object key, final Object value) {
        return hs.put(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.waba.system.BT747Hashtable#get(java.lang.Object)
     */
    public final Object get(final Object key) {
        return hs.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.waba.system.BT747Hashtable#remove(java.lang.Object)
     */
    public final void remove(final Object o) {
        hs.remove(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.waba.system.BT747Hashtable#hasNext()
     */
    public final boolean hasNext() {
        return (iteratorIdx < iterator.size());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.waba.system.BT747Hashtable#next()
     */
    public final Object nextKey() {
        if (hasNext()) {
            return iterator.items[iteratorIdx++];
        } else {
            return null;
        }
    }

    /**
     * @param initialCapacity
     */
    public WabaHashtable(final int initialCapacity) {
        hs = new Hashtable(initialCapacity);
    }

    /**
     * @param initialCapacity
     * @param loadFactor
     */
    public WabaHashtable(final int initialCapacity, final float loadFactor) {
        hs = new Hashtable(initialCapacity, loadFactor);
    }

    /* (non-Javadoc)
     * @see bt747.sys.interfaces.BT747Hashtable#getSize()
     */
    public final int size() {
        // TODO Auto-generated method stub
        return hs.size();
    }
    
    
}
