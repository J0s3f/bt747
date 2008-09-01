/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.util;

// import java.text.DateFormat;
// import java.text.SimpleDateFormat;
import gov.nist.core.StringTokenizer;

import java.util.Calendar;
import java.util.TimeZone;

import bt747.sys.Convert;
import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Date {
    private static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    // private Calendar cal = Calendar.getInstance();
    private java.util.Date date;

    /**
     * Calendar cal = Calendar.getInstance();
     */
    private static final long serialVersionUID = -8694258139978808370L;

    /**
     * 
     */
    public Date() {
        date = new java.util.Date();
    }

    /**
     * @param sentDate
     */
    public Date(final int sentDate) {
        Calendar cal = Calendar.getInstance(GMT_ZONE);
        cal.set(Calendar.DAY_OF_MONTH, sentDate / 10000);
        cal.set(Calendar.MONTH, sentDate / 100 % 100 - (1 + Calendar.JANUARY));
        cal.set(Calendar.YEAR, 2000 + sentDate % 100);
        date = cal.getTime();
    }

    /**
     * @param sentDay
     * @param sentMonth
     * @param sentYear
     */
    public Date(final int sentDay, final int sentMonth, final int sentYear) {
        Calendar cal = Calendar.getInstance(GMT_ZONE);
        cal.set(Calendar.DAY_OF_MONTH, sentDay);
        cal.set(Calendar.MONTH - (1 + Calendar.JANUARY), sentMonth);
        cal.set(Calendar.YEAR, sentYear);
        date = cal.getTime();
    }

    /**
     * @param strDate
     */
    public Date(final String strDate) {
        this(strDate, Settings.DATE_YMD);
    }

    /**
     * @param strDate
     * @param dateFormat
     */
    public Date(final String strDate, final byte dateFormat) {
        Calendar cal = Calendar.getInstance(GMT_ZONE);

        try {
            StringTokenizer fields = new StringTokenizer(strDate.toString(),
                    '/');
            int arg0;
            int arg1;
            int arg2;

            arg0 = Convert.toInt(fields.nextToken());
            arg1 = Convert.toInt(fields.nextToken());
            arg2 = Convert.toInt(fields.nextToken());

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
            date = cal.getTime();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * @param t
     */
    // public Date(Time t) {
    // super(t);
    // }
    public void advance(final int s) {
        date.setTime(date.getTime() + s * 1000L);
    }

    public Date(final java.util.Date d) {
        date = new java.util.Date(d.getTime());
    }

    public Date(final Date d) {
        date.setTime(d.getTime().getTime());
    }

    public final java.util.Date getTime() {
        return date;
    }

    public final int dateToUTCepoch1970() {
        return (int) (date.getTime() / 1000L);
    }

    public final String getDateString() {
        return getDay() + "/" + getMonth() + "/" + getYear();
    }

    // private static final int DAYS_Julian_1970 = (new
    // Date(1,1,1970)).getJulianDay();
    public final int getJulianDay() {
        return this.dateToUTCepoch1970();
    }

    public final int getYear() {
        Calendar cal = Calendar.getInstance(GMT_ZONE);
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public final int getMonth() {
        Calendar cal = Calendar.getInstance(GMT_ZONE);
        cal.setTime(date);
        return cal.get(Calendar.MONTH + (1 + Calendar.JANUARY));
    }

    public final int getDay() {
        Calendar cal = Calendar.getInstance(GMT_ZONE);
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
