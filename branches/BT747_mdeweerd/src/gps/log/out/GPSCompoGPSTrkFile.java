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

import gps.convert.Conv;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.Version;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;

/**
 * Class to write a CompeGPS TRK file or WPT file.
 * 
 * @author Mario De Weerd
 */
public final class GPSCompoGPSTrkFile extends GPSFile {

    // The next information is from gpsbabel:

    // the meaning of leading characters in CompeGPS data lines (enhanced
    // PCX):
    //
    // header lines:
    //    
    // "G": WGS 84 - Datum of the map
    // "N": Anybody - Name of the user
    // "L": -02:00:00 - Difference to UTC
    // "M": ... - Any comments
    // "R": 16711680 , xxxx , 1 - Route header
    // "U": 1 - System of coordinates (0=UTM 1=Latitude/Longitude)
    //
    // "C": 0 0 255 2 -1.000000 - ???
    // "V": 0.0 0.0 0 0 0 0 0.0 - ???
    // "E": 0|1|00-NUL-00 00:00:00|00:00:00|0 - ???
    //    
    // data lines:
    //    
    // "W": if (route) routepoint; else waypoint
    // "T": trackpoint
    // "t": if (track) additionally track info
    // if (!track) additionally trackpoint info
    // "a": link to ...
    // "w": waypoint additional info
    // End fo gpsbabel info
    // "z": 0.0,0.0,0.0,0.0 // bounding box
    // "P": device information, version, ...

    private boolean isWayType;

    public GPSCompoGPSTrkFile() {
        super();
        numberOfPasses = 2;
    }

    public final void initialiseFile(final BT747Path basename, final String ext,
            final int card, final int oneFilePerDay) {
        super.initialiseFile(basename, ext, oneFilePerDay);
        isWayType = false;
    }

    public final void writeFileHeader(final String s) {
        super.writeFileHeader(s);
        writeTxt("G  WGS 84\r\n" // WGS 84
                + "U  1\r\n" // LAT .LON FORMAT
                + "M  Generated using BT747 "
                + Version.VERSION_NUMBER
                + " http://sf.net/projects/bt747 for CompeGPS\r\n");
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
    private final StringBuffer rec = new StringBuffer(1024);
    private final StringBuffer wrec = new StringBuffer(1024);

    public void writeRecord(final GPSRecord s) {
        super.writeRecord(s);
        boolean trackpt;
        boolean waypt;
        trackpt = !isWayType && ptFilters[GPSFilter.TRKPT].doFilter(s);
        waypt = isWayType && ptFilters[GPSFilter.WAYPT].doFilter(s);

        if ((trackpt || waypt)) {
            rec.setLength(0);

            rec.append("T  A ");

            if ((s.hasLatitude()) && (selectedFileFields.hasLatitude())) {
                if (s.getLatitude() >= 0) {
                    rec.append(JavaLibBridge.toString(s.getLatitude(), 8)
                            + ((char) 0xBA) + "N");
                } else {
                    rec.append(JavaLibBridge.toString(-s.getLatitude(), 8)
                            + ((char) 0xBA) + "S");
                }
            } else {
                rec.append("0" + ((char) 0xBA) + "N");
            }

            rec.append(" ");
            if ((s.hasLongitude()) && (selectedFileFields.hasLongitude())) {
                if (s.getLongitude() >= 0) {
                    rec.append(JavaLibBridge.toString(s.getLongitude(), 8)
                            + ((char) 0xBA) + "E");
                } else {
                    rec.append(JavaLibBridge.toString(-s.getLongitude(), 8)
                            + ((char) 0xBA) + "W");
                }
            } else {
                rec.append("0" + (char) 0xBA + "E");
            }
            rec.append(" ");

            if ((s.hasUtc()) && (selectedFileFields.hasUtc())) {
                // rec.append(JavaLibBridge.toString(
                // (s.utc+(activeFields.milisecond!=0?(s.milisecond/1000.0):0))
                // /86400.0+25569, //Days since 30/12/1899
                // 7)); // 7 fractional digits
                // rec.append(",");
                rec.append(
                // Day calculation
                        ((t.getDay() < 10) ? "0" : "")
                                + t.getDay()
                                + "-"
                                // Month calculation
                                + Conv.idxToShortMonthStr(t.getMonth() - 1)
                                + "-"
                                + (((t.getYear() % 100) < 10) ? "0" : "")
                                // Year calculation
                                + (t.getYear() % 100)
                                + " "
                                // Hour
                                + ((t.getHour() < 10) ? "0" : "")
                                + t.getHour()
                                + ":"
                                // Minute
                                + ((t.getMinute() < 10) ? "0" : "")
                                + t.getMinute() + ":"
                                // Second
                                + ((t.getSecond() < 10) ? "0" : "")
                                + t.getSecond());
                // if (activeFields.milisecond!=0) {
                // rec+=".";
                // rec+=(s.milisecond<100)?"0":"";
                // rec+=(s.milisecond<10)?"0":"";
                // rec+=JavaLibBridge.toString(s.milisecond);
                // }
                rec.append(" ");
            } else {
                rec.append("01-JAN-70 00:00:00 ");
            }

            if (waypt) {
                wrec.setLength(0);
                wrec.append("W  ");
                wrec.append("waypt-"); // name
                if (s.hasRecCount()) {
                    wrec.append(s.getRecCount());
                }
                wrec.append(rec.toString().substring(2));
            }
            rec.append("s ");

            if ((s.hasHeight()) && (selectedFileFields.hasHeight())) {
                rec.append(JavaLibBridge.toString(s.getHeight(), 1));
                if (waypt) {
                    wrec.append(JavaLibBridge.toString(s.getHeight(), 1));
                }
            } else {
                rec.append("0.0");
                if (waypt) {
                    wrec.append("0.0");
                }
            }
            rec.append(" 0.0 0.0 0.0 0 -1000.0 -1.0 ");
            // if (waypt) {
            // wrec.append("Description")
            // }
            if ((s.hasNsat()) && (selectedFileFields.hasNsat())) {
                rec.append((s.getNsat() & 0xFF00) >> 8); // in use
                rec.append(" ");
            } else {
                rec.append("-1 ");
            }
            rec.append("-1.0 -1.0");

            rec.append("\r\n");
            if (!trackpt) {
                rec.setLength(0);
            }
            if (waypt) {
                rec.append(wrec.toString());
                rec.append("\r\n");
            }
            writeTxt(rec.toString());
            rec.setLength(0);
        }
    }

    public final boolean nextPass() {
        super.nextPass();
        if (!isWayType) {
            previousDate = 0;
            isWayType = true;
            ext = ".WPT";
            return true;
        } else {
            return false;
        }
    }

    // public void finaliseFile() {
    // if (m_File!=null) {
    // String footer;
    // //writeDataFooter();
    // footer= "F 1234";
    // writeTxt(footer);
    // }
    // super.finaliseFile();
    //        
    // }
}
