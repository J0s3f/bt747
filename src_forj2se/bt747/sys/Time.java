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
}
