// ********************************************************************
// *** BT747 ***
// *** (c)2007-2008 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package bt747.j2se_view.model;

import gps.BT747Constants;
import gps.log.GPSRecord;
import gps.log.out.AllWayPointStyles;

import bt747.sys.File;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario De Weerd
 * 
 */
public class FileWaypoint extends BT747Waypoint {
    public FileWaypoint() {
        super(GPSRecord.getLogFormatRecord(0));
        // TODO Auto-generated constructor stub
    }

    // values.
    private int utc;

    private BT747Path path;

    public boolean setFilePath(final BT747Path path) {
        this.path = path;
        return getInfo();
    }

    public BT747Path getFilePath() {
        return path;
    }

    /**
     * @return true if file can ge interpreted.
     */
    protected boolean getInfo() {
        getGpsRecord().voxStr = path.getPath();
        // TODO: change path setting.
        int idx1 = getGpsRecord().voxStr.lastIndexOf('/');
        final int idx2 = getGpsRecord().voxStr.lastIndexOf('\\');
        if (idx2 > idx1) {
            idx1 = idx2;
        }
        getGpsRecord().valid = BT747Constants.VALID_MANUAL_MASK;

        // TODO Replace by constant to define in AllWayPointStyles
        // Default = document
        getGpsRecord().rcr = AllWayPointStyles.GEOTAG_DOCUMENT_KEY;

        if ((idx1 >= 0) && (idx1 < getGpsRecord().voxStr.length())) {
            getGpsRecord().voxStr = getGpsRecord().voxStr.substring(idx1 + 1);
        }

        // Get file date & time.
                final File f = new File(path);
                final int u = f.getModificationTime();
                if (u != 0) {
                    setUtc(u);
                }
            return true;
    }

    /**
     * @param utc
     *                the utc to set
     */
    protected void setUtc(final int utc) {
        getGpsRecord().tagutc = utc;
        this.utc = utc;
    }

    /**
     * @return the utc
     */
    public int getUtc() {
        return utc;
    }
}
