/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.util;

import java.text.DateFormat;
import java.util.Locale;

import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Date extends java.util.Date {

    java.util.Date d;
    /**
     * 
     */
    public Date() {
        super();
        //d=new java.util.Date();
    }

    /**
     * @param sentDate
     */
    public Date(int sentDate) {
        //super(sentDate);
        super(sentDate / 10000,
                sentDate / 100 % 100,
                sentDate % 100);
        ;
    }

    /**
     * @param sentDay
     * @param sentMonth
     * @param sentYear
     */
    public Date(int sentDay, int sentMonth, int sentYear) {
        super(sentYear, sentMonth, sentDay);
    }

    /**
     * @param strDate
     */
    public Date(String strDate) {
        super(strDate);
    }

    /**
     * @param strDate
     * @param dateFormat
     */
    public Date(String strDate, byte dateFormat) {
        super();
        DateFormat df;
        switch (dateFormat) {

        case Settings.DATE_YMD:
            df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
            break;
        case Settings.DATE_DMY:
        default:
            df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
            break;
        }
        try {
        java.util.Date d=df.parse(strDate);
        super.setDate(d.getDate());
        super.setMonth(d.getMonth());
        super.setYear(d.getYear());
        } catch (Exception e){
            // TODO: handle exception
        }
    }

    /**
     * @param t
     */
//    public Date(Time t) {
//        super(t);
//    }
    
    public void advance(int s) {
        setTime(getTime()+s*1000);
    }
    
    public Date (java.util.Date d) {
        super();
        this.setDate(d.getDate());
    }
    
    public Date (Date d) {
        super();
        this.setDate(d.getDate());
    }
    
    public final int dateToUTCepoch1970() {
        return (int)(getTime()/1000L);
    }

   private String getDateString() {
    // TODO Auto-generated method stub
       return "";

   }
   
//   private static final int DAYS_Julian_1970 = (new Date(1,1,1970)).getJulianDay();
   public final int getJulianDay() {
       return dateToUTCepoch1970();
   }

   
   

}
