/*
 * Created on 12 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

import bt747.sys.Time;
import bt747.util.Date;


/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class Time {
    private waba.sys.Time m_Time;
    public Time()
    {
       m_Time=new waba.sys.Time();
    }

    public Time(long t) {
        m_Time=new waba.sys.Time(t);
    }
    
    public int getYear() {
        return m_Time.year;
    }
    public void setYear(int year) {
        m_Time.year=year;
    }
    public int getMonth() {
        return m_Time.month;
    }
    public void setMonth(int month) {
        m_Time.month=month;
    }
    public int getDay() {
        return m_Time.day;
    }
    public void setDay(int day) {
        m_Time.day=day;
    }
    public int getHour() {
        return m_Time.hour;
    }
    public void setHour(int hour) {
        m_Time.hour=hour;
    }
    public int getMinute() {
        return m_Time.minute;
    }
    public void setMinute(int minute) {
        m_Time.minute=minute;
    }
    public int getSecond() {
        return m_Time.second;
    }
    public void setSecond(int second) {
        m_Time.second=second;
    }
    public int getMillis() {
        return m_Time.millis;
    }
    public void setMillis(int millis) {
        m_Time.millis=millis;
    }
    
    private static final int DAYS_BETWEEN_1970_1983 = 4748;

    public final void setUTCTime(final int utc_int) {
        // long utc=utc_int&0xFFFFFFFFL;
        int utc = utc_int;
        // Time t=new Time();
        setSecond((int) utc % 60);
        utc /= 60;
        setMinute((int) utc % 60);
        utc /= 60;
        setHour((int) utc % 24);
        utc /= 24;
        // Now days since 1/1/1970
        Date d = new Date(1, 1, 1983); // Minimum = 1983
        d.advance(((int) utc) - DAYS_BETWEEN_1970_1983);
        setYear(d.getYear());
        setMonth(d.getMonth());
        setDay(d.getDay());
    }
}
