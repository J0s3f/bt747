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

package bt747.sys;

import bt747.sys.interfaces.BT747Date;

/**
 * Generic Implementation for BT747Date.
 * 
 * @author Mario De Weerd
 */
public final class Date {

    private BT747Date date;

    public Date() {
        date = Interface.tr.getDateInstance();
    }

    public Date(final int sentDay, final int sentMonth, final int sentYear) {
        date = Interface.tr.getDateInstance(sentDay, sentMonth, sentYear);
    }

    public Date(final String strDate, final byte dateFormat) {
        date = Interface.tr.getDateInstance(strDate, dateFormat);
    }

    public final void advance(final int s) {
        date.advance(s);
    }

    public final int dateToUTCepoch1970() {
        return date.dateToUTCepoch1970();
    }

    public final String getDateString() {
        return date.getDateString();
    }

    public final int getDay() {
        return date.getDay();
    }

    public final int getJulianDay() {
        return date.getJulianDay();
    }

    public final int getMonth() {
        // TODO Auto-generated method stub
        return date.getMonth();
    }

    public final int getYear() {
        // TODO Auto-generated method stub
        return date.getYear();
    }
}
