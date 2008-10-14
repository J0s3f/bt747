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
package net.sf.bt747.j2se.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import bt747.sys.Settings;
import bt747.sys.interfaces.BT747Date;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class J2SEDate implements BT747Date {
    static private final SimpleDateFormat FORMAT_YYYYMMDD = new SimpleDateFormat(
            "yyyy/MM/dd");
    static private final SimpleDateFormat FORMAT_DDMMYYYY = new SimpleDateFormat(
            "dd/MM/yyyy");
    static private final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    private final Calendar cal = Calendar.getInstance();

    /**
     * Calendar cal = Calendar.getInstance();
     */
    private static final long serialVersionUID = -8694258139978808370L;

    /**
     * 
     */
    public J2SEDate() {
        init();
    }

    /**
     * @param sentDate
     */
    public J2SEDate(int sentDate) {
        init();
        cal.set(sentDate / 10000, sentDate / 100 % 100 - 1, sentDate % 100,
                0, 0, 0);
    }

    /**
     * @param sentDay
     * @param sentMonth
     * @param sentYear
     */
    public J2SEDate(int sentDay, int sentMonth, int sentYear) {
        init();
        cal.set(sentYear, sentMonth - 1, sentDay, 0, 0, 0);
    }

    /**
     * @param strDate
     */
    public J2SEDate(String strDate) {
        this(strDate, Settings.DATE_YMD);
    }

    /**
     * @param strDate
     * @param dateFormat
     */
    public J2SEDate(String strDate, byte dateFormat) {
        init();
        DateFormat df;
        if (dateFormat == Settings.DATE_YMD) {
            df = FORMAT_YYYYMMDD;
        } else { // if {dateFormat==Settings.DATE_DMY) {
            df = FORMAT_DDMMYYYY;
        }
        df.setTimeZone(GMT_ZONE);
        try {
            cal.setTime(df.parse(strDate));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void init() {
        cal.setTimeZone(GMT_ZONE);
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

    public J2SEDate(java.util.Date d) {
        cal.setTime(d);
    }

    public J2SEDate(J2SEDate d) {
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