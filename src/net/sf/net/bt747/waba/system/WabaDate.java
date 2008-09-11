/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sf.net.bt747.waba.system;

import waba.sys.Time;

import bt747.interfaces.BT747Date;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WabaDate extends waba.util.Date implements BT747Date {

    /**
     * 
     */
    public WabaDate() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param sentDate
     */
    public WabaDate(int sentDate) {
        super(sentDate);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param sentDay
     * @param sentMonth
     * @param sentYear
     */
    public WabaDate(int sentDay, int sentMonth, int sentYear) {
        super(sentDay, sentMonth, sentYear);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param strDate
     */
    public WabaDate(String strDate) {
        super(strDate);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param strDate
     * @param dateFormat
     */
    public WabaDate(String strDate, byte dateFormat) {
        super(strDate, dateFormat);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param t
     */
    public WabaDate(Time t) {
        super(t);
        // TODO Auto-generated constructor stub
    }
    
    public String getDateString() {
        return getDate();
    }

    public WabaDate (waba.util.Date d) {
        super(d.getDay(),d.getMonth(),d.getYear());
    }
    
    private final static int JULIAN_DAY_1_1_1970=18264;   

    public final int dateToUTCepoch1970() {
        return (getJulianDay()-JULIAN_DAY_1_1_1970)*24*60*60;
    }
}
