/*
 * Created on 12 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

import bt747.sys.interfaces.BT747Time;
import bt747.sys.interfaces.Interface;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class Time {

    private BT747Time time;

    public Time() {
        time = Interface.tr.getTimeInstance();
    }

    public final int getDay() {
        return time.getDay();
    }

    public final int getHour() {
        return time.getHour();
    }

    public final int getMinute() {
        return time.getMinute();
    }

    public final int getMonth() {
        return time.getMonth();
    }

    public final int getSecond() {
        return time.getSecond();
    }

    public final int getYear() {
        return time.getYear();
    }

    public final void setUTCTime(final int utc) {
        time.setUTCTime(utc);
    }
}
