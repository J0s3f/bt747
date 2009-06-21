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
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package net.sf.bt747.j2me.system;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import bt747.sys.JavaLibBridge;
import bt747.sys.Generic;
import bt747.sys.Settings;
import bt747.sys.interfaces.BT747Date;

/**
 * J2ME Implementation for BT747Date.
 * 
 * @author Mario De Weerd
 */
public final class J2MEDate implements BT747Date {
    /**
     * Constant 100 to divide by 100 in algorithm.
     */
    private static final int INT_100 = 100;

    /**
     * Constant 10000 to divide by 10000 in algorithm.
     */
    private static final int INT_10000 = 10000;

    /**
     * Year 2000.
     */
    private static final int YEAR_2000 = 2000;

    /**
     * The number of milliseconds in a second.
     */
    private static final long MILLISECONDS_PER_SECOND = 1000L;

    /**
     * A reference to the GMT time zone - reused - to avoid constructing one
     * every time.
     */
    private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

    private static final Date INIT_DATE = new Date(0);

    /**
     * OS specific construct holding the actual data.
     */
    private final java.util.Date date;

    /**
     * Constructor - default date.
     */
    public J2MEDate() {
        long t = System.currentTimeMillis();
        t -= t % (1000L * 24 * 60 * 60);
        date = new java.util.Date(t);
    }

    /**
     * Constructor with date in integer format.
     * 
     * @param sentDate
     *                Date expressed as an integer in the format DDMMYY. Where
     *                YY is base 2000.
     */
    public J2MEDate(final int sentDate) {
        final Calendar cal = Calendar.getInstance(J2MEDate.GMT_ZONE);
        cal.set(Calendar.DAY_OF_MONTH, sentDate / J2MEDate.INT_10000);
        cal.set(Calendar.MONTH, sentDate / J2MEDate.INT_100
                % J2MEDate.INT_100 - (1 + Calendar.JANUARY));
        cal.set(Calendar.YEAR, J2MEDate.YEAR_2000 + sentDate
                % J2MEDate.INT_100);
        date = cal.getTime();
    }

    /**
     * Constructor.
     * 
     * @param sentDay
     *                Day of month.
     * @param sentMonth
     *                Month (January = 1).
     * @param sentYear
     *                Year (full year like 1980).
     */
    public J2MEDate(final int sentDay, final int sentMonth, final int sentYear) {
        final Calendar cal = Calendar.getInstance(J2MEDate.GMT_ZONE);
        cal.setTime(J2MEDate.INIT_DATE);
        cal.set(Calendar.DAY_OF_MONTH, sentDay);
        cal.set(Calendar.MONTH, sentMonth - (1 + Calendar.JANUARY));
        cal.set(Calendar.YEAR, sentYear);
        date = cal.getTime();
    }

    /**
     * Constructor.
     * 
     * @param strDate
     *                Date in YYMMDD format.
     */
    public J2MEDate(final String strDate) {
        this(strDate, Settings.DATE_YMD);
    }

    /**
     * Constructor.
     * 
     * @param strDate
     *                Date in 'AA/BB/CC' format.
     * @param dateFormat
     *                The format used for the date {@link Settings#DATE_DMY}
     *                or {@link Settings#DATE_YMD}.
     */
    public J2MEDate(final String strDate, final byte dateFormat) {
        final Calendar cal = Calendar.getInstance(J2MEDate.GMT_ZONE);
        cal.setTime(J2MEDate.INIT_DATE);
        java.util.Date tmp = null;

        try {
            final J2MEStringTokenizer fields = new J2MEStringTokenizer(
                    strDate, '/');
            int arg0;
            int arg1;
            int arg2;

            arg0 = JavaLibBridge.toInt(fields.nextToken());
            arg1 = JavaLibBridge.toInt(fields.nextToken());
            arg2 = JavaLibBridge.toInt(fields.nextToken());

            // TODO: may need to correct year.
            if (dateFormat == Settings.DATE_YMD) {
                cal.set(Calendar.DAY_OF_MONTH, arg2);
                cal.set(Calendar.MONTH, arg1 - (1 + Calendar.JANUARY));
                cal.set(Calendar.YEAR, arg0);
            } else {
                cal.set(Calendar.DAY_OF_MONTH, arg0);
                cal.set(Calendar.MONTH, arg1 - (1 + Calendar.JANUARY));
                cal.set(Calendar.YEAR, arg2);
            }
            tmp = cal.getTime();
        } catch (final Exception e) {
            Generic.debug("Date", e);
        }
        date = tmp;
    }

    /**
     * Date constructor. Application specific to be able to use the native
     * type to construct.
     * 
     * @param d
     *                The date to initialise with.
     */
    public J2MEDate(final java.util.Date d) {
        date = new java.util.Date(d.getTime());
    }

    // /**
    // * Date constructor. Application specific to be able to use the native
    // type
    // * to construct.
    // *
    // * @param d
    // * The date to initialise with.
    // */
    // public J2MEDate(final J2MEDate d) {
    // date.setTime(d.getTime().getTime());
    // }

    /**
     * @see bt747.util.BT747Date#advance(int)
     * @param s
     *                number of seconds to advance the time.
     */
    public final void advance(final int s) {
        date.setTime(date.getTime() + s * J2MEDate.MILLISECONDS_PER_SECOND);
    }

    /**
     * Get Date in native format. Application specific.
     * 
     * @return a representation of the date.
     */
    public final java.util.Date getTime() {
        return date;
    }

    /**
     * @see bt747.util.BT747Date#dateToUTCepoch1970()
     * @return number of seconds since epoch.
     */
    public final int dateToUTCepoch1970() {
        return (int) (date.getTime() / J2MEDate.MILLISECONDS_PER_SECOND);
    }

    /**
     * @see bt747.util.BT747Date#getDateString()
     * @return "d/m/YYYY" representation of date.
     */
    public final String getDateString() {
        return getDay() + "/" + getMonth() + "/" + getYear();
    }

    /**
     * @see bt747.util.BT747Date#getYear()
     * @return year for current date.
     */
    public final int getYear() {
        final Calendar cal = Calendar.getInstance(J2MEDate.GMT_ZONE);
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * @see bt747.util.BT747Date#getMonth()
     * @return month for current date.
     */
    public final int getMonth() {
        final Calendar cal = Calendar.getInstance(J2MEDate.GMT_ZONE);
        cal.setTime(date);
        return cal.get(Calendar.MONTH + (1 + Calendar.JANUARY));
    }

    /**
     * @see bt747.util.BT747Date#getDay()
     * @return day for current date.
     */
    public final int getDay() {
        final Calendar cal = Calendar.getInstance(J2MEDate.GMT_ZONE);
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
