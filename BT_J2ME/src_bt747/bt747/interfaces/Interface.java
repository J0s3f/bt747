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
package bt747.interfaces;


/** This class holds the handle for the interface.
 * One can set the class with the translation interface in here.
 * @author Mario De Weerd
 *
 */
public final class Interface {
    /**
     * The pointer to the lower level translation class.
     */
    public static JavaTranslationsInterface tr;

    /** Set the low level translation class.
     * @param t is the class.
     */
    public static final void setJavaTranslationInterface(
            final JavaTranslationsInterface t) {
        tr = t;
    }
}
