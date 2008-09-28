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

import bt747.sys.interfaces.BT747Thread;
import bt747.sys.interfaces.Interface;


/**
 * @author Mario De Weerd
 */
public final class Generic {
    public static final void debug(final String s, 
            Throwable e) {
        Interface.tr.debug(s,e);
    }
    
    public final static double pow(final double x, final double y) {
        return Interface.tr.pow(x,y);
    }

    public final static double acos(final double x) {
        return Interface.tr.acos(x);
    }
    
    public final static void addThread(final BT747Thread o, final boolean highPrio) {
        Interface.tr.addThread(o,highPrio);
    }
    
    public final static void removeThread(final BT747Thread o) {
        Interface.tr.removeThread(o); 
    }
}
