/*
 * Created on 12 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.net.bt747.waba.system;

import bt747.interfaces.BT747Time;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class WabaTime implements BT747Time {
    private waba.sys.Time m_Time;
    public WabaTime()
    {
       m_Time=new waba.sys.Time();
    }

    public WabaTime(long t) {
        m_Time=new waba.sys.Time(t);
    }
    
    public final int getYear() {
        return m_Time.year;
    }
    public final void setYear(int year) {
        m_Time.year=year;
    }
    public final int getMonth() {
        return m_Time.month;
    }
    public final void setMonth(int month) {
        m_Time.month=month;
    }
    public final int getDay() {
        return m_Time.day;
    }
    public final void setDay(int day) {
        m_Time.day=day;
    }
    public final int getHour() {
        return m_Time.hour;
    }
    public final void setHour(int hour) {
        m_Time.hour=hour;
    }
    public final int getMinute() {
        return m_Time.minute;
    }
    public final void setMinute(int minute) {
        m_Time.minute=minute;
    }
    public final int getSecond() {
        return m_Time.second;
    }
    public final void setSecond(int second) {
        m_Time.second=second;
    }
    public final int getMillis() {
        return m_Time.millis;
    }
    public final void setMillis(int millis) {
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
        WabaDate d = new WabaDate(1, 1, 1983); // Minimum = 1983
        d.advance(((int) utc) - DAYS_BETWEEN_1970_1983);
        setYear(d.getYear());
        setMonth(d.getMonth());
        setDay(d.getDay());
    }
}
