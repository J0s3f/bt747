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



/**
 * Class providing a set of conversion methods.
 * 
 * @author Mario De Weerd
 */
public final class Convert {
    public final static String toString(final boolean p) {
        return Interface.tr.toString(p);
    }

//    public final static String toString(final int p) {
//        return Interface.tr.toString(p);
//    }

    public final static String toString(final float p) {
        return Interface.tr.toString(p);
    }

    public final static String toString(final double p) {
        return Interface.tr.toString(p);
    }

    public final static String toString(final double p, final int i) {
        return Interface.tr.toString(p, i);
    }

    public final static String unsigned2hex(final int p, final int i) {
        return Interface.tr.unsigned2hex(p, i);
    }

    public final static int toInt(final String s) {
        return Interface.tr.toInt(s);
    }

    public final static float toFloat(final String s) {
        return Interface.tr.toFloat(s);
    }

    public final static double toDouble(final String s) {
        return Interface.tr.toDouble(s);
    }

    public final static double longBitsToDouble(final long l) {
        return Interface.tr.longBitsToDouble(l);
    }

    public final static float toFloatBitwise(final int l) {
        return Interface.tr.toFloatBitwise(l);
    }

    public final static int toIntBitwise(final float f) {
        return Interface.tr.toIntBitwise(f);
    }
}
