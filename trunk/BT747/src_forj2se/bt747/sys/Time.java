/*
 * Created on 12 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;



/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Time extends java.util.Date {

    /**
     * 
     */
    private static final long serialVersionUID = -4377529854748199705L;
    
    public int getHour() {
        return getHours();
    }
    public void setHour(int hours) {
        setHours(hours);
    }
    public int getMinute() {
        return getMinutes();
    }
    public void setMinute(int hours) {
        setMinutes(hours);
    }
    public int getSecond() {
        return getSeconds();
    }
    public void setSecond(int hours) {
        setSeconds(hours);
    }
    public void setDay(int date) {
        setDate(date);
    }
   
    public int getYear() {
        return super.getYear()+1900;
    }

    public void setYear(int year) {
        super.setYear(year-1900);
    }

    public  final void setUTCTime(final int utc_int) {
        setTime(utc_int*1000L);
    }
}
