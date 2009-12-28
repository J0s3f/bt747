// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.log.out;

import gps.BT747Constants;
import gps.log.GPSFilter;
import gps.log.GPSRecord;
import gps.log.TracksAndWayPoints;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Vector;

/**
 * Class to output to an internal array.
 * 
 * @author Mario De Weerd
 */
public final class GPSArray extends GPSFile {
    private BT747Vector track;
    private BT747Vector gpsWayPoints;
    private TracksAndWayPoints result = new TracksAndWayPoints();

    public GPSArray() {
        super();
        numberOfPasses = 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#initialiseFile(java.lang.String,
     * java.lang.String, int, int)
     */
    public void initialiseFile(final BT747Path baseName,
            final String extension, final int fileSeparationFreq) {
        super.initialiseFile(baseName, extension, fileSeparationFreq);
        result = new TracksAndWayPoints();
        gpsWayPoints = result.waypoints;
        track = JavaLibBridge.getVectorInstance();
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

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#finaliseFile()
     */
    public void finaliseFile() {
        endTrack();
    }

    public final boolean nextPass() {
        super.nextPass();
        if (nbrOfPassesToGo > 0) {
            nbrOfPassesToGo--;
            previousDate = 0;
            return true;
        } else {
            return false;
        }

    }

    private void endTrack() {
        result.tracks.addElement(track);
        track = JavaLibBridge.getVectorInstance();
    }

    boolean isNewTrack = true;

    // private GPSRecord prevRecord=null;
    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord s) {
        super.writeRecord(s);

        if (ptFilters[GPSFilter.WAYPT].doFilter(s)) {
            if (nbrOfPassesToGo == 0) {
                gpsWayPoints.addElement(s.cloneRecord());
            }
        }
        /**
         * Handle split of track
         */
        final boolean isTrackneededRecord = ptFilters[GPSFilter.TRKPT]
                .doFilter(s);
        boolean isNeedTrackSplit = needsToSplitTrack;

        if (!isTrackneededRecord) {
            isNeedTrackSplit |= !ignoreBadPoints;
        }

        if (!isNewTrack && !firstRecord) {
            // Only if we do not have a first track and we have written some
            // records.
            if (needsToSplitTrack) {
                endTrack();
            }
        }

        if (isTrackneededRecord) {
            // This is a selected trackpoint.

            isNewTrack = false;  // Something was actually added to the track.
            track.addElement(s.cloneRecord());
        }
    }

    protected int createFile(final int utc, final String extra_ext,
            final boolean createNewFile) {
        //super.getNbrFilesCreated();
        filesCreated++; // Always a success
        // Override to avoid file creation.
        return BT747Constants.NO_ERROR;
    }

    protected final void closeFile() {
        // Override to avoid file related errors
    }

    public final GPSRecord[] getGpsTrackPoints() {
        return result.getTrackPoints();
    }

    public final GPSRecord[] getGpsWayPoints() {
        return result.getWayPoints();
    }

    /**
     * @return the result
     */
    public final TracksAndWayPoints getResult() {
        return result;
    }
}
