/*
 * Created on 12 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

import java.util.GregorianCalendar;

import bt747.util.Date;



/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Time extends java.util.GregorianCalendar {

    /**
     * 
     */
    private static final long serialVersionUID = -4377529854748199705L;
    
    public int getHour() {
        return this.get(GregorianCalendar.HOUR_OF_DAY);
    }
    public void setHour(int hours) {
        super.set(HOUR_OF_DAY, hours);
    }
    public int getMinute() {
        return this.get(GregorianCalendar.MINUTE);
    }
    public void setMinute(int hours) {
        super.set(MINUTE, hours);
    }
    public int getSecond() {
        return this.get(GregorianCalendar.SECOND);
    }
    public void setSecond(int hours) {
        super.set(SECOND, hours);
    }
    public void setDay(int date) {
        super.set(DAY_OF_MONTH, date);
    }
   
    public int getYear() {
        return super.get(GregorianCalendar.YEAR);
    }

    public void setYear(int year) {
        super.set(YEAR, year);
    }
    public int getMonth() {
        return super.get(GregorianCalendar.MONTH);
    }
    public void setMonth(int month) {
        super.set(MONTH, month);
    }
    public int getDay() {
        return super.get(GregorianCalendar.DAY_OF_MONTH);
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
        second=utc % 60;
        utc /= 60;
        minute=utc % 60;
        utc /= 60;
        hour=utc%24;
        utc /= 24;
        this.set(1970,1,1,hour,minute,second);
        this.add(DAY_OF_YEAR, utc-1);
    }

//    public  final void setUTCTime(final int utc_int) {
//        super.setTime(new java.util.Date(utc_int*1000L));
//    }
}
