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
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package net.sf.bt747.j2se.app.utils;

import gps.log.GPSRecord;

import java.util.Comparator;

/**
 * @author Mario
 * 
 */
public class GPSRecordTimeComparator implements Comparator<GPSRecord> {
    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(GPSRecord o1, GPSRecord o2) {
        if(o2==null) {
            return -1;
        }
        if(o1==null) {
            return 1;
        }
        if(!o1.hasTagUtc()) {
            o1.tagutc = o1.tagutc;
        }
        if(!o2.hasTagUtc()) {
            o2.tagutc = o2.tagutc;
        }
          
        return o1.tagutc - o2.tagutc;
    }

}
