// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
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

import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.JavaLibBridge;

/**
 * Class to write a PLT file (OZI).
 * 
 * @author Mario De Weerd
 */
public final class GPSPLTFile extends GPSFile {

    // Track File (.plt)
    // Line 1 : File type and version information
    // Line 2 : Geodetic Datum used for the Lat/Lon positions for each
    // trackpoint
    // Line 3 : "Altitude is in feet" - just a reminder that the altitude is
    // always stored in feet
    // Line 4 : Reserved for future use
    // Line 5 : multiple fields as below
    //
    // Field 1 : always zero (0)
    // Field 2 : width of track plot line on screen - 1 or 2 are usually the
    // best
    // Field 3 : track color (RGB)
    // Field 4 : track description (no commas allowed)
    // Field 5 : track skip value - reduces number of track points plotted,
    // usually set to 1
    // Field 6 : track type - 0 = normal , 10 = closed polygon , 20 = Alarm
    // Zone
    // Field 7 : track fill style - 0 =bsSolid; 1 =bsClear; 2 =bsBdiagonal; 3
    // =bsFdiagonal; 4 =bsCross;
    // 5 =bsDiagCross; 6 =bsHorizontal; 7 =bsVertical;
    // Field 8 : track fill color (RGB)
    //
    // Line 6 : Number of track points in the track, not used, the number of
    // points is determined when reading the points file
    //

    public void writeFileHeader(final String s) {
        super.writeFileHeader(s);
        writeTxt("BT747 Track Point File http://www.bt747.org Version "
                + bt747.Version.VERSION_NUMBER
                + "\r\n"
                + "WGS 84\r\n"
                + "Altitude is in feet\r\n"
                + "Reserved 3\r\n"
                + "0,2,255,"
                + s
                + ",0,0,2,8421376\r\n" + "50000\r\n" // number
                // of
        // points
        // in
        // the
        // track,
        // not
        // used,
        // unknown
        // at
        // this
        // point.
        );
        // "NSAT (USED/VIEW),SAT INFO (SID-ELE-AZI-SNR)
    }

    /**
     * Returns true when the record is used by the format.
     * 
     * Override parent class because only the trackpoint filter is used.
     */
    protected boolean recordIsNeeded(final GPSRecord s) {
        return ptFilters[GPSFilter.TRKPT].doFilter(s);
    }

    // Trackpoint data
    //
    // One line per trackpoint
    // each field separated by a comma
    // non essential fields need not be entered but comma separators must
    // still
    // be used (example ,,)
    // defaults will be used for empty fields
    //
    //
    // Note that OziExplorer reads the Date/Time from field 5, the date and
    // time
    // in fields 6 & 7 are ignored.
    //
    // Example
    // -27.350436, 153.055540,1,-777,36169.6307194, 09-Jan-99, 3:08:14
    // -27.348610, 153.055867,0,-777,36169.6307194, 09-Jan-99, 3:08:14

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);

        if ((r != null) && ptFilters[GPSFilter.TRKPT].doFilter(r)) {
            String rec = "";

            // Field 1 : Latitude - decimal degrees.
            if ((r.hasLatitude())
                    && (selectedFileFields.hasLatitude())) {
                rec += JavaLibBridge.toString(r.getLatitude(), 6);
            }
            rec += ",";
            // Field 2 : Longitude - decimal degrees.
            if ((r.hasLongitude())
                    && (selectedFileFields.hasLongitude())) {
                rec += JavaLibBridge.toString(r.getLongitude(), 6);
            }
            rec += ",";
            // Field 3 : Code - 0 if normal, 1 if break in track line
            rec += "0,"; // Normal for the moment - could detect break
            // later
            // ...
            // Field 4 : Altitude in feet (-777 if not valid)
            if ((r.hasHeight())
                    && (selectedFileFields.hasHeight())) {
                rec += ((int) (r.getHeight() * 3.2808398950131233595800524934383f));
            } else {
                rec += "-777";
            }
            rec += ",";
            // Field 5 : Date - see Date Format below, if blank a preset date
            // will be used
            // TDateTime value is the number of days that have passed since
            // 12/30/1899.
            // private static final int DAYS_BETWEEN_19700101_18991230=4748;

            // Field 6 : Date as a string
            // Field 7 : Time as a string
            if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
                rec += JavaLibBridge
                        .toString(
                                (r.getUtc() + ((r.hasMillisecond())
                                        && (selectedFileFields
                                                .hasMillisecond()) ? (r.milisecond / 1000.0)
                                        : 0)) / 86400.0 + 25569, // Days
                                // since
                                // 30/12/1899
                                7); // 7 fractional digits
                rec += ",";
                rec += (t.getMonth() < 10 ? "0" : "") + t.getMonth() + "/"
                        + (t.getDay() < 10 ? "0" : "") + t.getDay() + "/"
                        + t.getYear() + "," + (t.getHour() < 10 ? "0" : "")
                        + t.getHour() + ":" + (t.getMinute() < 10 ? "0" : "")
                        + t.getMinute() + ":"
                        + (t.getSecond() < 10 ? "0" : "") + t.getSecond();
                if ((r.hasMillisecond())
                        && (selectedFileFields.hasMillisecond())) {
                    rec += ".";
                    rec += (r.milisecond < 100) ? "0" : "";
                    rec += (r.milisecond < 10) ? "0" : "";
                    rec += r.milisecond;
                }
            } else {
                rec += ",,";
            }
            rec += "\r\n";
            writeTxt(rec);
        } // s!=null
    }
}
