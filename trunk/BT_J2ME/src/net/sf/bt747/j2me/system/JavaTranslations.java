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

import bt747.interfaces.BT747Date;
import bt747.interfaces.BT747Hashtable;
import bt747.interfaces.BT747Time;
import bt747.interfaces.BT747Vector;
import bt747.interfaces.JavaTranslationsInterface;
import bt747.interfaces.BT747Thread;

public class JavaTranslations implements JavaTranslationsInterface {
    public final BT747Date getDateInstance() {
        return new J2MEDate();
    }

    public final BT747Date getDateInstance(final int d, final int m, final int y) {
        return new J2MEDate(d, m, y);
    }

    public final BT747Date getDateInstance(final String strDate,
            final byte dateFormat) {
        return new J2MEDate(strDate, dateFormat);
    }
    
    public final BT747Hashtable getHashtableInstance(final int initialCapacity) {
        return new J2MEHashtable(initialCapacity);
    }

    public final BT747Vector getVectorInstance() {
        return new J2MEVector();
    }

    public final BT747Time getTimeInstance() {
        return new J2METime();
    }
    
    public final void debug(final String s, final Throwable e) {
        Generic.debug(s,e);
    }
    
    public final double pow(final double x, final double y) {
        return Float11.pow(x, y);
    }

    public final double acos(final double x) {
        return Float11.acos(x);
    }
    

    public final void addThread(final BT747Thread t, final boolean b) {
        Generic.addThread(t, b);
    }

    public final void removeThread(final BT747Thread t) {
        Generic.removeThread(t);
    }

}
