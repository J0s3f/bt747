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
package bt747.sys;

import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747Hashtable;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747StringTokenizer;
import bt747.sys.interfaces.BT747Time;
import bt747.sys.interfaces.BT747Vector;
import bt747.sys.interfaces.JavaTranslationsInterface;


/** This class holds the handle for the interface.
 * One can set the class with the translation interface in here.
 * @author Mario De Weerd
 *
 */
public final class Interface {
    /**
     * The pointer to the lower level translation class.
     */
    protected static JavaTranslationsInterface tr;
    
    /** Set the low level translation class.
     * @param t is the class.
     */
    public static void setJavaTranslationInterface(
            final JavaTranslationsInterface t) {
        tr = t;
    }

    public static final BT747Hashtable getHashtableInstance(int initialCapacity) {
        return tr.getHashtableInstance(initialCapacity);
    }

    public static final BT747Semaphore getSemaphoreInstance(final int value) {
        return tr.getSemaphoreInstance(value);
    }

    public static final BT747Time getTimeInstance() {
        return tr.getTimeInstance();
    }

    public static final BT747StringTokenizer getStringTokenizerInstance(final String a, final char b) {
        return tr.getStringTokenizer(a, b);
    }

    public static final String[] toStringArrayAndEmpty(BT747Vector vector) {
        String[] result = new String[vector.size()];
        for (int i = vector.size() - 1; i >= 0; i--) {
            result[i] = (String) vector.elementAt(i);
        }
        vector.removeAllElements();
        return result;
    }

    public static final BT747Vector getVectorInstance() {
        return tr.getVectorInstance();
    }

    public static final BT747HashSet getHashSetInstance() {
        return tr.getHashSetInstance();
    }
    
    public static final BT747Date getDateInstance() {
        return tr.getDateInstance();
    }

    public static final BT747Date getDateInstance(final int d, final int m, final int y) {
        return tr.getDateInstance(d, m, y);
    }

    public static final BT747Date getDateInstance(final String date, final byte format) {
        return tr.getDateInstance(date, format);
    }

}
