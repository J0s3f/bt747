/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Date {
    static private SimpleDateFormat FORMAT_YYYYMMDD = new SimpleDateFormat(
            "yyyy/MM/dd");
    static private SimpleDateFormat FORMAT_DDMMYYYY = new SimpleDateFormat(
            "dd/MM/yyyy");
    static private TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    private Calendar cal = Calendar.getInstance();

    /**
     * Calendar cal = Calendar.getInstance();
     */
    private static final long serialVersionUID = -8694258139978808370L;

    /**
     * 
     */
    public Date() {
        init();
    }

    /**
     * @param sentDate
     */
    public Date(int sentDate) {
        init();
        cal.set(sentDate / 10000, sentDate / 100 % 100 - 1, sentDate % 100,
                0, 0, 0);
    }

    /**
     * @param sentDay
     * @param sentMonth
     * @param sentYear
     */
    public Date(int sentDay, int sentMonth, int sentYear) {
        init();
        cal.set(sentYear, sentMonth - 1, sentDay, 0, 0, 0);
    }

    /**
     * @param strDate
     */
    public Date(String strDate) {
        this(strDate, Settings.DATE_YMD);
    }

    /**
     * @param strDate
     * @param dateFormat
     */
    public Date(String strDate, byte dateFormat) {
        init();
        DateFormat df;
        if (dateFormat == Settings.DATE_YMD) {
            df = FORMAT_YYYYMMDD;
        } else { // if {dateFormat==Settings.DATE_DMY) {
            df = FORMAT_DDMMYYYY;
        }
        df.setTimeZone(GMT_ZONE);
        try {
            java.util.Date d = df.parse(strDate);
            cal.setTime(df.parse(strDate));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void init() {
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * @param t
     */
    // public Date(Time t) {
    // super(t);
    // }
    public void advance(int s) {
        cal.add(java.util.Calendar.SECOND, s);

    }

    public Date(java.util.Date d) {
        cal.setTime(d);
    }

    public Date(Date d) {
        cal.setTime(d.getTime());
    }

    public java.util.Date getTime() {
        return cal.getTime();
    }

    public final int dateToUTCepoch1970() {
        return (int) (cal.getTimeInMillis() / 1000L);
    }

    public String getDateString() {
        FORMAT_DDMMYYYY.setTimeZone(GMT_ZONE);
        return FORMAT_DDMMYYYY.format(cal.getTime());

    }

    // private static final int DAYS_Julian_1970 = (new
    // Date(1,1,1970)).getJulianDay();
    public final int getJulianDay() {
        return this.dateToUTCepoch1970();
    }

    public final int getYear() {
        return cal.get(Calendar.YEAR);
    }

    public final int getMonth() {
        return cal.get(Calendar.MONTH);
    }

    public final int getDay() {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

}
