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
package gps.log.out;

import gps.BT747Constants;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

/**
 * Class to write a CSV file.
 * 
 * @author Mario De Weerd
 */
public final class GPSArray extends GPSFile {
    private GPSRecord[] gpsTrackPoints;
    private GPSRecord[] gpsWayPoints;

    public GPSArray() {
        super();
        numberOfPasses = 2;
        trackPointCount = 0;
        wayPointCount = 0;
    }

    public final boolean needPassToFindFieldsActivatedInLog() {
        return false;
    }

    /**
     * Returns true when the record is used by the format.
     * 
     * Override parent class because only the trackpoint filter is used.
     */
    protected final boolean recordIsNeeded(final GPSRecord s) {
        return ptFilters[GPSFilter.TRKPT].doFilter(s);
    }

    public final boolean nextPass() {
        super.nextPass();
        if (nbrOfPassesToGo > 0) {
            nbrOfPassesToGo--;
            previousDate = 0;
            if (nbrOfPassesToGo == 0) {
                gpsTrackPoints = new GPSRecord[trackPointCount];
                gpsWayPoints = new GPSRecord[wayPointCount];
            }
            trackPointCount = 0;
            wayPointCount = 0;
            return true;
        } else {
            return false;
        }

    }

    private int trackPointCount;
    private int wayPointCount;

    // private GPSRecord prevRecord=null;
    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord s) {
        super.writeRecord(s);
        if (ptFilters[GPSFilter.TRKPT].doFilter(s)) {
            if (nbrOfPassesToGo == 0) {
                gpsTrackPoints[trackPointCount] = s.cloneRecord();
            }
            trackPointCount++;
        }

        if (ptFilters[GPSFilter.WAYPT].doFilter(s)) {
            if (nbrOfPassesToGo == 0) {
                gpsWayPoints[wayPointCount] = s.cloneRecord();
            }
            wayPointCount++;
        }
    }

    protected int createFile(final String extra_ext) {
        filesCreated++; // Always a success
        // Override to avoid file creation.
        return BT747Constants.NO_ERROR;
    }

    protected final void closeFile() {
        // Override to avoid file related errors
    }

    public final GPSRecord[] getGpsTrackPoints() {
        return gpsTrackPoints;
    }

    public final GPSRecord[] getGpsWayPoints() {
        return gpsWayPoints;
    }

}
