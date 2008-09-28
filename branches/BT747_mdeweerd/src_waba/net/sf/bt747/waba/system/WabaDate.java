//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  This layer was written for the SuperWaba toolset.           ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package net.sf.bt747.waba.system;

import waba.sys.Time;

import bt747.sys.interfaces.BT747Date;

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
