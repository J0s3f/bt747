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
import net.sf.bt747.j2se.app.exif.ExifAttribute;
import net.sf.bt747.j2se.app.exif.ExifConstants;
import net.sf.bt747.j2se.app.exif.ExifJPG;

import bt747.sys.Convert;
import bt747.sys.File;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Date;
import bt747.sys.interfaces.BT747Time;

/**
 * @author Mario De Weerd
 * 
 */
public class ImageData extends BT747Waypoint {
    /**
     * @param r
     */
    public ImageData() {
        super(GPSRecord.getLogFormatRecord(0));
        // TODO Auto-generated constructor stub
    }

    // values.
    private int utc;

    private int width;
    private int height;

    private String path;
    private int card;

    public void setPath(final String path, final int card) {
        setCard(card);
        setPath(path);
    }

    public void setPath(final String path) {
        this.path = path;
        getImageInfo();
    }

    public String getPath() {
        return path;
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

    private void getImageInfo() {
        getGpsRecord().voxStr = path;
        // TODO: change path setting.
        int idx1 = getGpsRecord().voxStr.lastIndexOf('/');
        final int idx2 = getGpsRecord().voxStr.lastIndexOf('\\');
        if (idx2 > idx1) {
            idx1 = idx2;
        }
        getGpsRecord().valid = BT747Constants.VALID_MANUAL_MASK;

        // TODO Replace by constant to define in AllWayPointStyles
        // Default = document
        getGpsRecord().rcr = 0x0104;
        if ((idx1 >= 0) && (idx1 < getGpsRecord().voxStr.length())) {
            getGpsRecord().voxStr = getGpsRecord().voxStr.substring(idx1 + 1);
        }
        final ExifJPG exifJpg = new ExifJPG();
        if (exifJpg.setPath(getPath())) {
            // bt747.sys.Generic.debug(exifJpg.toString());
            // TODO Replace by constant to define in AllWayPointStyles
            getGpsRecord().rcr = 0x0101;
            ExifAttribute atr;
            atr = exifJpg.getExifAttribute(ExifConstants.TAG_PIXELXDIMENSION);
            if (atr != null) {
                setWidth(atr.getIntValue(0));
            }

            atr = exifJpg.getExifAttribute(ExifConstants.TAG_PIXELYDIMENSION);
            if (atr != null) {
                setHeight(atr.getIntValue(0));
            }
            atr = exifJpg.getGpsAttribute(ExifConstants.TAG_GPSLATITUDE);
            if (atr != null) {
                getGpsRecord().latitude = getLatOrLon(atr);
                atr = exifJpg
                        .getGpsAttribute(ExifConstants.TAG_GPSLATITUDEREF);
                if (atr != null) {
                    if (atr.getStringValue().toUpperCase().indexOf('S') >= 0) {
                        getGpsRecord().latitude = -getGpsRecord().latitude;
                    }
                }
            }
            atr = exifJpg.getGpsAttribute(ExifConstants.TAG_GPSLONGITUDE);
            if (atr != null) {
                getGpsRecord().longitude = getLatOrLon(atr);
                atr = exifJpg
                        .getGpsAttribute(ExifConstants.TAG_GPSLONGITUDEREF);
                if (atr != null) {
                    if (atr.getStringValue().toUpperCase().indexOf('W') >= 0) {
                        getGpsRecord().longitude = -getGpsRecord().longitude;
                    }
                }
            }

            atr = exifJpg.getGpsAttribute(ExifConstants.TAG_GPSALTITUDE);
            if (atr != null) {
                float altitude = atr.getFloatValue(0);
                atr = exifJpg
                        .getGpsAttribute(ExifConstants.TAG_GPSALTITUDEREF);
                if (atr != null) {
                    if (atr.getIntValue(0) == 1) {
                        altitude = -altitude;
                    }
                }
                getGpsRecord().height = altitude;
            }

            {
                int day = 0;
                int month = 0;
                int year = 0;
                int hour = 0;
                int minutes = 0;
                int seconds = 0;
                boolean hasData = false;
                atr = exifJpg.getGpsAttribute(ExifConstants.TAG_GPSDATESTAMP);
                if (atr != null) {
                    final String dateStr = atr.getStringValue();
                    try {
                        year = Integer.valueOf(dateStr.substring(0, 4));
                        month = Integer.valueOf(dateStr.substring(5, 6));
                        day = Integer.valueOf(dateStr.substring(7, 8));
                        hasData = true;
                    } catch (final Exception e) {
                        // TODO: handle exception
                    }
                }

                atr = exifJpg.getGpsAttribute(ExifConstants.TAG_GPSTIMESTAMP);
                if (atr != null) {
                    try {
                        hour = (int) atr.getFloatValue(0);
                        minutes = (int) atr.getFloatValue(1);
                        seconds = (int) atr.getFloatValue(2);
                        hasData = true;
                    } catch (final Exception e) {
                        // TODO: handle exception
                    }
                }
                if (hasData) {
                    int t = Interface.getDateInstance(day, month, year)
                            .dateToUTCepoch1970();
                    t += hour * 3600 + minutes * 60 + seconds;
                    getGpsRecord().utc = t;
                }
            }

            atr = exifJpg
                    .getExifAttribute(ExifConstants.TAG_DATETIMEDIGITIZED);
            if (atr == null) {
                atr = exifJpg
                        .getExifAttribute(ExifConstants.TAG_DATETIMEORIGINAL);
                if (atr == null) {
                    atr = exifJpg
                            .getExifAttribute(ExifConstants.TAG_DATETIME);
                }
            }
            if (atr != null) {
                String DateTime = null;
                DateTime = atr.getStringValue();
                // Format is: "2007:08:05 13:13:43"
                if ((DateTime.length() == 20) && (DateTime.charAt(4) == ':')
                        && (DateTime.charAt(4) == ':')
                        && (DateTime.charAt(7) == ':')
                        && (DateTime.charAt(10) == ' ')
                        && (DateTime.charAt(13) == ':')
                        && (DateTime.charAt(16) == ':')) {
                    int year;
                    int month;
                    int day;

                    int seconds;
                    year = Convert.toInt(DateTime.substring(0, 4));
                    month = Convert.toInt(DateTime.substring(5, 7));
                    day = Convert.toInt(DateTime.substring(8, 10));
                    seconds = Convert.toInt(DateTime.substring(11, 13))
                            * 3600
                            + Convert.toInt(DateTime.substring(14, 16)) * 60
                            + Convert.toInt(DateTime.substring(17, 19));
                    final BT747Date d = Interface.getDateInstance(day, month,
                            year);
                    setUtc(d.dateToUTCepoch1970() + seconds);
                }
            } else {
                // Get file date & time.
                final File f = new File(path);
                final int u = f.getModificationTime();
                if (u != 0) {
                    setUtc(u);
                }
            }
        }
    }

    public final void writeImage(final String destPath, final int card) {
        writeImage(getPath(), destPath, card);
    }

    public final void writeImage(final String orgPath, final String destPath,
            final int card) {
        if (getGpsRecord().hasLatitude() && getGpsRecord().hasLongitude()) {
            final ExifJPG exifJpg = new ExifJPG();
            exifJpg.setPath(orgPath); // Get exif data from file
            final GPSRecord g = getGpsRecord();
            if (g.hasPosition()) {
                exifJpg.setGpsPosition(g.latitude, g.longitude);
            }
            if (g.hasPdop()) {
                exifJpg.setGpsPDOP(g.pdop);
            } else if (g.hasHdop()) {
                exifJpg.setGpsHDOP(g.hdop);
            }
            if (g.hasUtc()) {
                final BT747Time t = Interface.getTimeInstance();
                t.setUTCTime(g.utc);
                exifJpg.setGpsTime(t.getYear(), t.getMonth(), t.getDay(), t
                        .getHour(), t.getMinute(), t.getSecond());
            }

            if (g.hasHeight()) {
                // TODO: CommonIn.convertHeight(r, factorConversionWGS84ToMSL,
                // logFormat);
                // Or make sure in application.
                // Should be MSL height.
                exifJpg.setGpsAltitudeMSL(g.height);
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
                satInfo += nsatInfoToString(g);
            }
            if (satInfo.length() != 0) {
                exifJpg.setGpsSatInformation(satInfo);
            }
            if (g.hasHeading()) {
                exifJpg.setGpsTrack(g.heading);
            }
            if (g.hasSpeed()) {
                exifJpg.setGpsSpeedKmH(g.speed);
            }
            if (g.hasValid()) {
                exifJpg
                        .setDifferential((g.valid & BT747Constants.VALID_DGPS_MASK) != 0);
            }
            exifJpg.copyTo(destPath, card);
            exifJpg.setUsedSoftWare();
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
     * @param utc
     *                the utc to set
     */
    private void setUtc(final int utc) {
        getGpsRecord().tagutc = utc;
        this.utc = utc;
    }

    /**
     * @return the utc
     */
    public int getUtc() {
        return utc;
    }

    /**
     * @param width
     *                the width to set
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
     *                the height to set
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

    /**
     * @param card
     *                the card to set
     */
    private void setCard(final int card) {
        this.card = card;
    }

    /**
     * @return the card
     */
    public int getCard() {
        return card;
    }
}
