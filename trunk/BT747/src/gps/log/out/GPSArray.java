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
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************  
package gps.log.out;

import gps.BT747Constants;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

/**Class to write a CSV file.
 * 
 * @author Mario De Weerd
 */
public class GPSArray extends GPSFile {
    GPSRecord[] gpsTrackPoints;
    GPSRecord[] gpsWayPoints;
    
    public GPSArray() {
        super();
        C_NUMBER_OF_PASSES = 2;
        trackPointCount=0;
        wayPointCount=0;
    }

    
    public boolean needPassToFindFieldsActivatedInLog() {
        return false;
    }

    /**
     * Returns true when the record is used by the format.
     * 
     * Override parent class because only the trackpoint filter is used.
     */
    protected boolean recordIsNeeded(GPSRecord s) {
        return m_Filters[GPSFilter.C_TRKPT_IDX].doFilter(s);
    }
    
    public boolean nextPass() {
        super.nextPass();
        if (m_nbrOfPassesToGo > 0) {
            m_nbrOfPassesToGo--;
            m_prevdate = 0;
            if(m_nbrOfPassesToGo==0) {
                gpsTrackPoints= new GPSRecord[trackPointCount];
                gpsWayPoints= new GPSRecord[wayPointCount];
                trackPointCount=0;
                wayPointCount=0;
            }
            return true;
        } else {
            return false;
        }

    }
    
    int trackPointCount;
    int wayPointCount;
    
//    private GPSRecord prevRecord=null;
    /* (non-Javadoc)
     * @see gps.GPSFile#WriteRecord()
     */
    public void writeRecord(final GPSRecord s) {
        // NO CALL TO super.writeRecord(s); TO INHIBIT FILE CREATION
        if(m_Filters[GPSFilter.C_TRKPT_IDX].doFilter(s)) {
            if(m_nbrOfPassesToGo==0) {
                gpsTrackPoints[trackPointCount]=s.cloneRecord();
            }
            trackPointCount++;
        }

        if(m_Filters[GPSFilter.C_WAYPT_IDX].doFilter(s)) {
            if(m_nbrOfPassesToGo==0) {
                gpsWayPoints[wayPointCount]=s.cloneRecord();
            }
            wayPointCount++;
        }
    }
    
    protected int createFile(final String extra_ext) {
        // Override to avoid file creation.
        return BT747Constants.NO_ERROR;
    }

    protected void closeFile() {
        // Override to avoid file related errors
    }

    public final GPSRecord[] getGpsTrackPoints() {
        return gpsTrackPoints;
    }


    public final GPSRecord[] getGpsWayPoints() {
        return gpsWayPoints;
    }
}
