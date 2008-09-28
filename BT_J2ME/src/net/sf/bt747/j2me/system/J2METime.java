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

import java.util.Calendar;
import java.util.TimeZone;

import bt747.sys.interfaces.BT747Time;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class J2METime implements BT747Time {
    final static private TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    private Calendar cal = Calendar.getInstance(GMT_ZONE);

    public J2METime() {
    }

    /**
     * 
     */
    private static final long serialVersionUID = -4377529854748199705L;

    public int getHour() {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public void setHour(int hours) {
        cal.set(Calendar.HOUR_OF_DAY, hours);
    }

    public int getMinute() {
        return cal.get(Calendar.MINUTE);
    }

    public void setMinute(int hours) {
        cal.set(Calendar.MINUTE, hours);
    }

    public int getSecond() {
        return cal.get(Calendar.SECOND);
    }

    public void setSecond(int hours) {
        cal.set(Calendar.SECOND, hours);
    }

    public void setDay(int date) {
        cal.set(Calendar.DAY_OF_MONTH, date);
    }

    public int getYear() {
        return cal.get(Calendar.YEAR);
    }

    public void setYear(int year) {
        cal.set(Calendar.YEAR, year);
    }

    public int getMonth() {
        return cal.get(Calendar.MONTH) + 1;
    }

    public void setMonth(int month) {
        cal.set(Calendar.MONTH, month - 1);
    }

    public int getDay() {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    // UTC time in java depends on implementation (leap seconds, ...)
    // So we need to implement our own function.
    public final void setUTCTime(final int utc) {
//        cal.set(Calendar.YEAR, 1970);
//        cal.set(Calendar.MONTH, 0);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//        cal.getInstance(GMT_ZONE).setTime(new java.util.Date())

        cal.setTime(new java.util.Date(utc * 1000L));
    }

    // public final void setUTCTime(final int utc_int) {
    // cal.setTime(new java.util.Date(utc_int*1000L));
    // }
}
