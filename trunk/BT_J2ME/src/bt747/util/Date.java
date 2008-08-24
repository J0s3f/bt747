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

import org.j4me.logging.Log;

import bt747.sys.Convert;
import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Date {
    // static private TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
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
        cal.set(Calendar.DAY_OF_MONTH, sentDate / 10000);
        cal.set(Calendar.MONTH, sentDate / 100 % 100);
        cal.set(Calendar.YEAR, sentDate % 100);
    }

    /**
     * @param sentDay
     * @param sentMonth
     * @param sentYear
     */
    public Date(int sentDay, int sentMonth, int sentYear) {
        init();
        cal.set(Calendar.DAY_OF_MONTH, sentDay / 10000);
        cal.set(Calendar.MONTH, sentMonth);
        cal.set(Calendar.YEAR, sentYear);
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
        StringTokenizer fields = new StringTokenizer(strDate.toString(), '/');

        try {
            int arg0;
            int arg1;
            int arg2;

            arg0 = Convert.toInt(fields.nextToken());
            arg1 = Convert.toInt(fields.nextToken());
            arg2 = Convert.toInt(fields.nextToken());

            // TODO: may need to correct year.
            if (dateFormat == Settings.DATE_YMD) {
                cal.set(Calendar.DAY_OF_MONTH, arg2);
                cal.set(Calendar.MONTH, arg1);
                cal.set(Calendar.YEAR, arg0);
            } else {
                cal.set(Calendar.DAY_OF_MONTH, arg0);
                cal.set(Calendar.MONTH, arg1);
                cal.set(Calendar.YEAR, arg2);
            }
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
        cal.setTime(new java.util.Date(cal.getTime().getTime() + s * 1000L));

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
        try {
            return (int) (cal.getTime().getTime() / 1000L);
        } catch (Exception e) {
            Log.error("dateToUTCepoch1970 problem ...", e);
            Log.info("Timezone:" + cal.getTimeZone().getID());
            // TODO: handle exception
            return 0;
        }
    }

    public String getDateString() {
        return getDay() + "/" + getMonth() + "/" + getYear();
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
