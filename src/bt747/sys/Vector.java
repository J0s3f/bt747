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
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package bt747.sys;

import bt747.sys.interfaces.BT747Vector;

/**
 * @author Mario De Weerd
 * 
 */
public final class Vector {

    private final BT747Vector vector;

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

    public final void mypush(final Object item) {
        vector.mypush(item);

    }

    public final String[] toStringArrayAndEmpty() {
        String[] result = new String[vector.size()];
        for (int i = vector.size() - 1; i >= 0; i--) {
            result[i] = (String) vector.elementAt(i);
        }
        vector.removeAllElements();
        return result;
    }

}
