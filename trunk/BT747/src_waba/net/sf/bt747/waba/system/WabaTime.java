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

import bt747.sys.interfaces.BT747Time;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class WabaTime implements BT747Time {
    private waba.sys.Time time;

    public WabaTime() {
        time = new waba.sys.Time();
    }

    public WabaTime(final long t) {
        time = new waba.sys.Time(t);
    }

    public int getYear() {
        return time.year;
    }

    public void setYear(final int year) {
        time.year = year;
    }

    public int getMonth() {
        return time.month;
    }

    public void setMonth(final int month) {
        time.month = month;
    }

    public int getDay() {
        return time.day;
    }

    public void setDay(final int day) {
        time.day = day;
    }

    public int getHour() {
        return time.hour;
    }

    public void setHour(final int hour) {
        time.hour = hour;
    }

    public int getMinute() {
        return time.minute;
    }

    public void setMinute(final int minute) {
        time.minute = minute;
    }

    public int getSecond() {
        return time.second;
    }

    public void setSecond(final int second) {
        time.second = second;
    }

    public int getMillis() {
        return time.millis;
    }

    public void setMillis(final int millis) {
        time.millis = millis;
    }

    private static final int DAYS_BETWEEN_1970_1983 = 4748;

    public void setUTCTime(final int utc_int) {
        // long utc=utc_int&0xFFFFFFFFL;
        int utc = utc_int;
        // Time t=new Time();
        setSecond((int) utc % 60);
        utc /= 60;
        setMinute((int) utc % 60);
        utc /= 60;
        setHour((int) utc % 24);
        utc /= 24;
        // Now days since 1/1/1970
        WabaDate d = new WabaDate(1, 1, 1983); // Minimum = 1983
        d.advance(((int) utc) - DAYS_BETWEEN_1970_1983);
        setYear(d.getYear());
        setMonth(d.getMonth());
        setDay(d.getDay());
    }
}
