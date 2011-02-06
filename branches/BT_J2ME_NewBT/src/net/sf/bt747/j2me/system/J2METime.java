// ********************************************************************
// *** BT 747 ***
// *** (c)2008 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// ********************************************************************

package net.sf.bt747.j2me.system;

import java.util.Calendar;
import java.util.TimeZone;

import bt747.sys.interfaces.BT747Time;

/**
 * Implements the time functionality for the J2ME platform.
 * 
 * @author Mario De Weerd
 */
public final class J2METime implements BT747Time {
    private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    private final Calendar cal = Calendar.getInstance(J2METime.GMT_ZONE);

    public J2METime() {
    }

    public final int getHour() {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public final void setHour(final int hours) {
        cal.set(Calendar.HOUR_OF_DAY, hours);
    }

    public final int getMinute() {
        return cal.get(Calendar.MINUTE);
    }

    public final void setMinute(final int hours) {
        cal.set(Calendar.MINUTE, hours);
    }

    public final int getSecond() {
        return cal.get(Calendar.SECOND);
    }

    public final void setSecond(final int hours) {
        cal.set(Calendar.SECOND, hours);
    }

    public final void setDay(final int date) {
        cal.set(Calendar.DAY_OF_MONTH, date);
    }

    public final int getYear() {
        return cal.get(Calendar.YEAR);
    }

    public final void setYear(final int year) {
        cal.set(Calendar.YEAR, year);
    }

    public final int getMonth() {
        return cal.get(Calendar.MONTH) + 1;
    }

    public final void setMonth(final int month) {
        cal.set(Calendar.MONTH, month - 1);
    }

    public final int getDay() {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    // UTC time in java depends on implementation (leap seconds, ...)
    // So we need to implement our own function.
    public final void setUTCTime(final int utc) {
        cal.setTime(new java.util.Date(utc * 1000L));
    }
}
