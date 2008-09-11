/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.net.bt747.waba.system;

import bt747.interfaces.BT747Hashtable;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class WabaHashtable extends waba.util.Hashtable implements
        BT747Hashtable {

    /**
     * @param initialCapacity
     */
    public WabaHashtable(int initialCapacity) {
        super(initialCapacity);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param initialCapacity
     * @param loadFactor
     */
    public WabaHashtable(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param res
     */
    public WabaHashtable(String res) {
        super(res);
        // TODO Auto-generated constructor stub
    }

}
