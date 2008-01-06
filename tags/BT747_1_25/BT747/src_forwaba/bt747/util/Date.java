/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.util;

import waba.sys.Time;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Date extends waba.util.Date {

    /**
     * 
     */
    public Date() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param sentDate
     */
    public Date(int sentDate) {
        super(sentDate);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param sentDay
     * @param sentMonth
     * @param sentYear
     */
    public Date(int sentDay, int sentMonth, int sentYear) {
        super(sentDay, sentMonth, sentYear);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param strDate
     */
    public Date(String strDate) {
        super(strDate);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param strDate
     * @param dateFormat
     */
    public Date(String strDate, byte dateFormat) {
        super(strDate, dateFormat);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param t
     */
    public Date(Time t) {
        super(t);
        // TODO Auto-generated constructor stub
    }

}
