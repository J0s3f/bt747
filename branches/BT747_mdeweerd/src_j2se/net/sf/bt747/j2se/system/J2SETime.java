// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package net.sf.bt747.j2se.system;

import java.util.Calendar;
import java.util.TimeZone;

import bt747.sys.interfaces.BT747Time;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class J2SETime implements BT747Time {
    // static private SimpleDateFormat FORMAT_YYYYMMDD = new
    // java.text.SimpleDateFormat("yyyy/MM/dd");
    // static private SimpleDateFormat FORMAT_DDMMYYYY = new
    // java.text.SimpleDateFormat("dd/MM/yyyy");
    static private TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    private Calendar cal = Calendar.getInstance();

    private void init() {
        cal.setTimeZone(GMT_ZONE);
    }

    public J2SETime() {
        init();
    }

    /**
     * 
     */
    private static final long serialVersionUID = -4377529854748199705L;

    public int getHour() {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public void setHour(final int hours) {
        cal.set(Calendar.HOUR_OF_DAY, hours);
    }

    public int getMinute() {
        return cal.get(Calendar.MINUTE);
    }

    public void setMinute(final int hours) {
        cal.set(Calendar.MINUTE, hours);
    }

    public int getSecond() {
        return cal.get(Calendar.SECOND);
    }

    public void setSecond(final int hours) {
        cal.set(Calendar.SECOND, hours);
    }

    public void setDay(final int date) {
        cal.set(Calendar.DAY_OF_MONTH, date);
    }

    public int getYear() {
        return cal.get(Calendar.YEAR);
    }

    public void setYear(final int year) {
        cal.set(Calendar.YEAR, year);
    }

    public int getMonth() {
        return cal.get(Calendar.MONTH) + 1;
    }

    public void setMonth(final int month) {
        cal.set(Calendar.MONTH, month - 1);
    }

    public int getDay() {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    // UTC time in java depends on implementation (leap seconds, ...)
    // So we need to implement our own function.
    public final void setUTCTime(final int utc_int) {
        // long utc=utc_int&0xFFFFFFFFL;
        int utc = utc_int;
        int hour;
        int minute;
        int second;
        // Time t=new Time();
        second = utc % 60;
        utc /= 60;
        minute = utc % 60;
        utc /= 60;
        hour = utc % 24;
        utc /= 24;
        cal.set(1970, 0, 1, hour, minute, second);
        cal.add(Calendar.DAY_OF_YEAR, utc);
    }

    // public final void setUTCTime(final int utc_int) {
    // cal.setTime(new java.util.Date(utc_int*1000L));
    // }
}
