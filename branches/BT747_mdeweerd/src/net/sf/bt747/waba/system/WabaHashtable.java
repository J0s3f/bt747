//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
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
