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
package net.sf.bt747.j2se.app.exiftool;

import gps.BT747Constants;
import gps.log.GPSRecord;
import gps.log.out.AllWayPointStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.bt747.j2se.app.exif.ExifAttribute;
import net.sf.bt747.j2se.app.exif.ExifConstants;
import net.sf.bt747.j2se.app.exif.ExifJPG;

import bt747.Version;
import bt747.j2se_view.model.FileWaypoint;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747Time;

/**
 * This class uses the ExifTool to tag. Tag writing is in principle
 * implemented for EXIF. Reading EXIF data using the exiftool is not done yet.
 * 
 * Information regarding the arguments can be found by using the appropriate
 * subsections starting from <a
 * href="http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/index.html"
 * >the exiftool tag list</a> .
 * 
 * @author Mario De Weerd
 * 
 */
public class ExiftoolData extends FileWaypoint {
    /**
     * @param r
     */
    public ExiftoolData() {
        super();
    }

    private int width;
    private int height;

    @Override
    protected boolean getInfo() {
        return getImageInfo();
    }

    private double getLatOrLon(final ExifAttribute atr) {
        double xtitude = -99999;
        if (atr.getCount() == 3) {
            final double a = atr.getFloatValue(0);
            final double b = atr.getFloatValue(1);
            final double c = atr.getFloatValue(2);
            xtitude = a + b / 60 + c / 3600;

        } else {
            xtitude = -99999;
        }
        return xtitude;
    }

    private String getMatch(final String regex, final String org) {
        Pattern p = Pattern.compile(regex,Pattern.MULTILINE|Pattern.DOTALL);
        //p = Pattern.compile("GPS.*",Pattern.MULTILINE|Pattern.DOTALL);
        String result = null;
        Matcher m = p.matcher(org);
        if(m.find()) {
            result = m.group(1);
        }
        return result;
    }

    /**
     * @return true if file can get interpreted.
     */
    private boolean getImageInfo() {
        getGpsRecord().voxStr = getPath();
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

        String exifResult;
        try {
            exifResult = getExifToolInfo(getGpsRecord(), getPath());
        } catch (Exception e) {
            return false;
        }

        // Analyse exifResult;
        // Sample response:
        // CreateDate: 2009:02:20 14:24:41
        // DateTimeOriginal: 2009:02:20 14:24:41
        // GPSLatitude: 45.8373693611111
        // GPSLongitude: 6.57369355555556
        // GPSAltitude: 1080.29

        // TODO: May need to change the next line to conform with doc type.
        getGpsRecord().rcr = AllWayPointStyles.GEOTAG_PICTURE_KEY;
        
        String match;
        if ((match = getMatch("^GPSLatitude: *([-0-9.]+)$", exifResult)) != null) {
            getGpsRecord().setLatitude(Double.parseDouble(match));
        }
        if ((match = getMatch("^GPSLongitude: *([-0-9.]+)$", exifResult)) != null) {
            getGpsRecord().setLongitude(Double.parseDouble(match));
        }
        if ((match = getMatch("^GPSAltitude: *([-0-9.]+)$", exifResult)) != null) {
            getGpsRecord().setHeight(Float.parseFloat(match));
        }
        String dateTime = null;
        if ((match = getMatch("^DateTimeOriginal: *([ :0-9]+)$", exifResult)) != null) {
            dateTime = match;
        } else if ((match = getMatch("^CreateDate: *([ :0-9]+)$", exifResult)) != null) {
            dateTime = match;
        }
        if(dateTime!=null) {
            if ((dateTime.length() == 19)
                    && (dateTime.charAt(4) == ':')
                    && (dateTime.charAt(7) == ':')
                    && (dateTime.charAt(10) == ' ')
                    && (dateTime.charAt(13) == ':')
                    && (dateTime.charAt(16) == ':')) {
                int year;
                int month;
                int day;

                int seconds;
                year = JavaLibBridge.toInt(dateTime.substring(0, 4));
                month = JavaLibBridge.toInt(dateTime.substring(5, 7));
                day = JavaLibBridge.toInt(dateTime.substring(8, 10));
                seconds = JavaLibBridge.toInt(dateTime.substring(11, 13))
                        * 3600
                        + JavaLibBridge.toInt(dateTime.substring(14, 16))
                        * 60
                        + JavaLibBridge.toInt(dateTime.substring(17, 19));
                final BT747Date d = JavaLibBridge.getDateInstance(day,
                        month, year);
                setUtc(d.dateToUTCepoch1970() + seconds);
            }
        } else {
            // Get file date & time.
            final File f = new File(getPath());
            final int u = f.getModificationTime();
            if (u != 0) {
                setUtc(u);
            }
        }
        return true;
    }

    /**
     * 
     */
    private static String getExifToolInfo(final GPSRecord g, final String path) {
        String exifResult;
        ArrayList<String> params = new ArrayList<String>(
                exifGetParams.length + 5);
        for (String p : exifGetParams) {
            params.add(p);
        }

        params.add(path);
        try {
            exifResult = new String(ExifTool.execExifTool(params));
            Generic.debug(exifResult);
            return exifResult;
        } catch (Exception e) {
            Generic.debug("ExifTool read of " + path, e);
        }
        return null;
    }

    public final void writeImage(final String destPath, final int card) {
        writeImage(getPath(), destPath, card);
    }

    private final static String SW = "BT747 " + Version.VERSION_NUMBER;

    /**
     * Build the list of arguments to write the EXIF data. This does not
     * include the 'pathname' parameters which still need to be added. (Image
     * file path, tool path).
     * 
     * @param g
     *            GPSRecord with position data.
     * @return List of Exif arguments
     */
    private List<String> writeExifArguments(final GPSRecord g) {
        // Build the arguments for the exiftool
        final List<String> exifToolArgs = new ArrayList<String>();
        // option -n: -n write values as numbers instead of words
        exifToolArgs.add("-n"); //$NON-NLS-1$
        // option -s: use tag names instead of descriptions
        exifToolArgs.add("-s"); //$NON-NLS-1$

        if (g.hasPosition()) {
            if (g.hasPosition()) {
                exifToolArgs
                        .add("-GPSLatitudeRef=" + (g.latitude >= 0.0 ? 'N' : 'S')); //$NON-NLS-1$
                exifToolArgs.add("-GPSLatitude=" + Math.abs(g.latitude)); //$NON-NLS-1$
                exifToolArgs
                        .add("-GPSLongitudeRef=" + (g.longitude >= 0.0 ? 'E' : 'W')); //$NON-NLS-1$
                exifToolArgs.add("-GPSLongitude=" + Math.abs(g.longitude)); //$NON-NLS-1$
            }
            if (g.hasPdop()) {
                exifToolArgs.add("-GPSMeasureMode=3"); //$NON-NLS-1$
                exifToolArgs.add("-GPSDOP=" + g.pdop); //$NON-NLS-1$
            } else if (g.hasHdop()) {
                exifToolArgs.add("-GPSMeasureMode=2"); //$NON-NLS-1$
                exifToolArgs.add("-GPSDOP=" + g.hdop); //$NON-NLS-1$
            }
            if (g.hasUtc()) {
                final BT747Time t = JavaLibBridge.getTimeInstance();
                t.setUTCTime(g.utc);
                // exifJpg.setGpsTime(t.getYear(), t.getMonth(), t.getDay(), t
                // .getHour(), t.getMinute(), t.getSecond());
                final String timeStr = String.format("%02u:%02u:%02u", t
                        .getHour(), t.getMinute(), t.getSecond());
                final String dateStr = String.format("%04u:%02u%:02u", t
                        .getYear(), t.getMonth(), t.getDay());
                exifToolArgs.add("-GPSDateStamp=" + dateStr); //$NON-NLS-1$
                exifToolArgs.add("-GPSTimeStamp=" + timeStr); //$NON-NLS-1$            }
            }
            if (g.hasHeight()) {
                // TODO: CommonIn.convertHeight(r,
                // factorConversionWGS84ToMSL,
                // logFormat);
                // Or make sure in application.
                // Should be MSL height.
                exifToolArgs
                        .add("-GPSAltitudeRef=" + ((g.height < 0f) ? "1" : "0")); //$NON-NLS-1$
                exifToolArgs.add("-GPSAltitude=" + Math.abs(g.height)); //$NON-NLS-1$            }
            }
            String satInfo = "";
            if (g.hasNsat()) {
                satInfo = ((g.nsat >> 8) & 0xFF) + "(" + (g.nsat & 0xFF)
                        + ")";
            }
            if (g.hasSid()) {
                if (satInfo.length() != 0) {
                    satInfo += " ";
                }
                satInfo += ExiftoolData.nsatInfoToString(g);
            }
            if (satInfo.length() != 0) {
                exifToolArgs.add("-GPSSatellites=" + satInfo);
            }
            if (g.hasHeading()) {
                exifToolArgs.add("-GPSTrackRef=T");
                exifToolArgs.add("-GPSTrack=" + g.heading);
            }
            if (g.hasSpeed()) {
                exifToolArgs.add("-GPSSpeedRef=K");
                exifToolArgs.add("-GPSSpeed=" + g.speed);
            }
            if (g.hasValid()) {
                exifToolArgs
                        .add("-GPSDifferential="
                                + (((g.valid & BT747Constants.VALID_DGPS_MASK) != 0) ? "1"
                                        : "0"));
            }
            exifToolArgs.add("-Software=" + SW);
        }

        return exifToolArgs;
    }

    private static final String[] exifGetParams = {
            "-S", // Short output
            "-n", // Numbers (not for human)
            "-CreateDate", "-DateTimeOriginal", "-GPSLatitude",
            "-GPSLongitude", "-GPSAltitude", "-GPSDateTime", "-GPSTrack",
            "-GPSSpeed", "-GPSSatellites", "-GPSDifferential",
            "-GPSTimeStamp", "-GPSDateStamp", "-GPSMeasureMode", "-GPSDOP" };

    public final void writeImage(final String orgPath, final String destPath,
            final int card) {
        List<String> exiftoolArgs = writeExifArguments(getGpsRecord());
        exiftoolArgs.add("-o");
        exiftoolArgs.add(destPath);
        exiftoolArgs.add(orgPath);
        try {
            ExifTool.execExifTool(exiftoolArgs);
        } catch (Exception e) {
            Generic.debug("While tagging " + destPath, e);
        }
    }

    private final static String nsatInfoToString(final GPSRecord r) {
        final char satSeperator = ';';
        final StringBuffer rec = new StringBuffer();
        if (r.hasSid()) {
            int j = 0;
            if (r.hasSid()) {
                for (int i = r.sid.length - 1; i >= 0; i--) {
                    if (j != 0) {
                        rec.append(satSeperator);
                    }
                    if (r.sidinuse[j]) {
                        rec.append('#');
                    }
                    if (r.sid[j] < 10) {
                        rec.append('0');
                    }
                    rec.append(r.sid[j]);
                    rec.append('-');
                    if (r.hasEle()) {
                        if (r.ele[j] < 10) {
                            rec.append('0');
                        }
                        rec.append(r.ele[j]);
                    }
                    rec.append('-');
                    if (r.hasAzi()) {
                        // if(s.azi[j]<100) {
                        // rec.append('0');
                        if (r.azi[j] < 10) {
                            rec.append('0');
                        }
                        // }
                        rec.append(r.azi[j]);
                    }
                    rec.append('-');
                    if (r.hasSnr()) {
                        if (r.snr[j] < 10) {
                            rec.append('0');
                        }
                        rec.append(r.snr[j]);
                    }
                    j++;
                }
            }
        }
        return rec.toString();
    }

    /**
     * @param width
     *            the width to set
     */
    private void setWidth(final int width) {
        this.width = width;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param height
     *            the height to set
     */
    private void setHeight(final int height) {
        this.height = height;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

}
