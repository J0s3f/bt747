/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Date extends java.util.GregorianCalendar {

    /**
     * 
     */
    private static final long serialVersionUID = -8694258139978808370L;
    /**
     * 
     */
    public Date() {
        super();
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
        super(sentYear,sentMonth,sentDay);
        System.err.println(sentDay+"/"+sentMonth+"/"+sentYear);
        System.err.println(getDateString());
    }

    /**
     * @param strDate
     */
    public Date(String strDate) {
        this(strDate,Settings.DATE_YMD);
    }

    /**
     * @param strDate
     * @param dateFormat
     */
    public Date(String strDate, byte dateFormat) {
        super();
        DateFormat df;
        if(dateFormat==Settings.DATE_YMD) {
            df = new SimpleDateFormat("yyyy/MM/dd");
        } else { //if {dateFormat==Settings.DATE_DMY) {
            df = new SimpleDateFormat("dd/MM/yyyy");
        }
        try {
        java.util.Date d=df.parse(strDate);
        super.setTime(d);
        } catch (Exception e){
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
    /**
     * @param t
     */
//    public Date(Time t) {
//        super(t);
//    }
    
    public void advance(int s) {
        super.add(GregorianCalendar.SECOND, s);

    }
    
    public Date (java.util.Date d) {
        super();
        this.setTime(d);
    }
    
    public Date (Date d) {
        super();
        super.setTime(d.getTime());
    }
    
    public final int dateToUTCepoch1970() {
        return (int)(this.getTimeInMillis()/1000L);
    }

   public String getDateString() {
       return new SimpleDateFormat("dd/MM/yyyy").format(this.getTime());

   }
   
//   private static final int DAYS_Julian_1970 = (new Date(1,1,1970)).getJulianDay();
   public final int getJulianDay() {
       return this.dateToUTCepoch1970();
   }

   public final int getYear() {
       return this.get(GregorianCalendar.YEAR);
   }
   public final int getMonth() {
       return this.get(GregorianCalendar.MONTH);
   }
   public final int getDay() {
       return this.get(GregorianCalendar.DAY_OF_MONTH);
   }
   

}
