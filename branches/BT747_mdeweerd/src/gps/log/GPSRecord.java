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
package gps.log;

import gps.BT747Constants;

import bt747.sys.Convert;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Time;

/**
 * Structure to hold GPS data for one point
 * 
 * @author Mario De Weerd
 */
public class GPSRecord {
    public int utc = NO_UTC;
    public int tagutc = NO_UTC; // UTC used to tag.
    public int valid = NO_VALID;
    public double latitude = NO_LAT_LON;
    public double longitude = NO_LAT_LON;
    public float height = NO_HEIGHT;
    public float speed = NO_SPEED;
    public float heading = NO_HEADING;
    public int dsta;
    public int dage;
    public int pdop = NO_XDOP;
    public int hdop = NO_XDOP;
    public int vdop = NO_XDOP;
    public int nsat;

    public int[] sid = null;
    public boolean[] sidinuse = null;
    public int[] ele = null;
    public int[] azi = null;
    public int[] snr = null;

    public int rcr = NO_RCR;
    private static final int NO_RCR = 0x80000000
            | BT747Constants.RCR_DISTANCE_MASK
            | BT747Constants.RCR_SPEED_MASK | BT747Constants.RCR_TIME_MASK;
    private static final int HAS_RCR = BT747Constants.RCR_DISTANCE_MASK
            | BT747Constants.RCR_SPEED_MASK | BT747Constants.RCR_TIME_MASK;

    private static final int NO_UTC = 0;
    private static final int HAS_UTC = 1;
    
    private static final double NO_LAT_LON = -100000;
    private static final double HAS_LAT_LON = 0;
    
    private static final int NO_VALID = -1;
    private static final int HAS_VALID = 0;

    private static final int NO_HEIGHT = -1000000;
    private static final int HAS_HEIGHT = 0;

    private static final int NO_SPEED = -1000000;
    private static final int HAS_SPEED = 0;

    private static final int NO_HEADING = -1;
    private static final int HAS_HEADING = 0;
    
    private static final int NO_XDOP = -1;
    private static final int HAS_XDOP = 0;

    /** Recorder reason */
    public int milisecond;
    public double distance;

    public float geoid; // Value returned by GPS.
    public int recCount;

    public int logPeriod; // Programmed period between logged points
    public int logSpeed; // Programmed minimum speed
    public int logDistance; // Programmed distance between logged points

    public String voxStr; // Voice recording reference.

    public GPSRecord() {
        utc = 0;
    }

    /**
     * 
     */
    public GPSRecord(final GPSRecord r) {
        utc = r.utc;
        tagutc = r.tagutc;
        valid = r.valid;
        latitude = r.latitude;
        longitude = r.longitude;
        height = r.height;
        speed = r.speed;
        heading = r.heading;
        dsta = r.dsta;
        dage = r.dage;
        pdop = r.pdop;
        hdop = r.hdop;
        vdop = r.vdop;
        nsat = r.nsat;
        if (r.sid != null) {
            // Object method clone() does not work on device for arrays.
            // Doing explicit copy until better method found.
            int i = r.sid.length;
            sid = new int[i];
            sidinuse = new boolean[i];
            if (r.ele != null) {
                ele = new int[i];
            }
            if (r.azi != null) {
                azi = new int[i];
            }
            if (r.snr != null) {
                snr = new int[i];
            }

            for (i -= 1; i >= 0; i--) {
                sid[i] = r.sid[i];
                sidinuse[i] = r.sidinuse[i];
                if (r.ele != null) {
                    ele[i] = r.ele[i];
                }
                if (r.azi != null) {
                    azi[i] = r.azi[i];
                }
                if (r.snr != null) {
                    snr[i] = r.snr[i];
                }
            }
        }
        rcr = r.rcr;
        milisecond = r.milisecond;
        distance = r.distance;
        geoid = r.geoid;
        recCount = r.recCount;
        logDistance = r.logDistance;
        logPeriod = r.logPeriod;
        logSpeed = r.logSpeed;
        if (r.voxStr != null) {
            voxStr = r.voxStr;
        }
    }

    public final GPSRecord cloneRecord() {
        return new GPSRecord(this);
    }

    public final int getLogFormat() {
        // Return log format based on content.
        return 0xFFFFFFFF;
    }

    public final static GPSRecord getLogFormatRecord(final int logFormat) {
        final GPSRecord gpsRec = new GPSRecord();
        if ((logFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0) {
            gpsRec.utc = HAS_UTC;
        }
        if ((logFormat & (1 << BT747Constants.FMT_VALID_IDX)) != 0) {
            gpsRec.valid = HAS_VALID;
        }
        if ((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0) {
            gpsRec.latitude = HAS_LAT_LON;
        }
        if ((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0) {
            gpsRec.longitude = HAS_LAT_LON;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEIGHT_IDX)) != 0) {
            gpsRec.height = HAS_HEIGHT;
        }
        if ((logFormat & (1 << BT747Constants.FMT_SPEED_IDX)) != 0) {
            gpsRec.speed = HAS_SPEED;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEADING_IDX)) != 0) {
            gpsRec.heading = HAS_HEADING;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DSTA_IDX)) == 0) {
            gpsRec.dsta = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DAGE_IDX)) == 0) {
            gpsRec.dage = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_PDOP_IDX)) != 0) {
            gpsRec.pdop = HAS_XDOP;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HDOP_IDX)) != 0) {
            gpsRec.hdop = HAS_XDOP;
        }
        if ((logFormat & (1 << BT747Constants.FMT_VDOP_IDX)) != 0) {
            gpsRec.vdop = HAS_XDOP;
        }
        if ((logFormat & (1 << BT747Constants.FMT_NSAT_IDX)) == 0) {
            gpsRec.nsat = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_SID_IDX)) != 0) {
            gpsRec.sid = new int[0];
            gpsRec.sidinuse = new boolean[0];
        }

        if ((logFormat & (1 << BT747Constants.FMT_ELEVATION_IDX)) != 0) {
            gpsRec.ele = new int[0];
        }
        if ((logFormat & (1 << BT747Constants.FMT_AZIMUTH_IDX)) != 0) {
            gpsRec.azi = new int[0];
        }
        if ((logFormat & (1 << BT747Constants.FMT_SNR_IDX)) != 0) {
            gpsRec.snr = new int[0];
        }
        if ((logFormat & (1 << BT747Constants.FMT_RCR_IDX)) != 0) {
            gpsRec.rcr = HAS_RCR;
        }
        if ((logFormat & (1 << BT747Constants.FMT_MILLISECOND_IDX)) == 0) {
            gpsRec.milisecond = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DISTANCE_IDX)) == 0) {
            gpsRec.distance = -1;
        }

        /* End handling record */
        return gpsRec;
    }

    public final boolean hasRecCount() {
        return recCount != 0;
    }

    public final boolean hasUtc() {
        return utc != NO_UTC;
    }

    public final boolean hasTagUtc() {
        return tagutc != NO_UTC;
    }

    public final boolean hasValid() {
        return valid != NO_VALID;
    }

    public final boolean hasLocation() {
        return hasLatitude() && hasLongitude();
    }

    public final boolean hasLatitude() {
        return latitude != NO_LAT_LON;
    }

    public final boolean hasLongitude() {
        return longitude != NO_LAT_LON;
    }

    public final boolean hasHeight() {
        return height != NO_HEIGHT;
    }

    public final boolean hasSpeed() {
        return speed != NO_SPEED;
    }

    public final boolean hasHeading() {
        return heading != -1000000;
    }

    public final boolean hasDsta() {
        return dsta != -1;
    }

    public final boolean hasDage() {
        return dage != -1;
    }

    public final boolean hasPdop() {
        return pdop != NO_XDOP;
    }

    public final boolean hasHdop() {
        return hdop != NO_XDOP;
    }

    public final boolean hasVdop() {
        return vdop != NO_XDOP;
    }

    public final boolean hasNsat() {
        return nsat != -1;
    }

    public final boolean hasSid() {
        return sid != null;
    }

    public final boolean hasSidInUse() {
        return sidinuse != null;
    }

    public final boolean hasEle() {
        return ele != null;
    }

    public final boolean hasAzi() {
        return azi != null;
    }

    public final boolean hasSnr() {
        return snr != null;
    }

    public final boolean hasRcr() {
        return rcr != NO_RCR;
    }

    public final boolean hasMilisecond() {
        return milisecond != -1;
    }

    public final boolean hasDistance() {
        return distance != -1;
    }

    public final boolean hasVoxStr() {
        return voxStr != null;
    }

    public final boolean equalsFormat(final GPSRecord r) {
        return (hasUtc() == r.hasUtc()) && (hasValid() == r.hasValid())
                && (hasLatitude() == r.hasLatitude())
                && (hasLongitude() == r.hasLongitude())
                && (hasHeight() == r.hasHeight())
                && (hasSpeed() == r.hasSpeed())
                && (hasHeading() == r.hasHeading())
                && (hasDsta() == r.hasDsta()) && (hasDage() == r.hasDage())
                && (hasPdop() == r.hasPdop()) && (hasHdop() == r.hasHdop())
                && (hasVdop() == r.hasVdop()) && (hasNsat() == r.hasNsat())
                && (hasSid() == r.hasSid())
                && (hasSidInUse() == r.hasSidInUse())
                && (hasEle() == r.hasEle()) && (hasAzi() == r.hasAzi())
                && (hasSnr() == r.hasSnr()) && (hasRcr() == r.hasRcr())
                && (hasMilisecond() == r.hasMilisecond())
                && (hasDistance() == r.hasDistance())
                && (hasVoxStr() == r.hasVoxStr());
    }

    public final BT747Time getBT747Time() {
        final BT747Time t = Interface.getTimeInstance();
        t.setUTCTime(utc);
        return t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final StringBuffer rec = new StringBuffer(100);
        rec.setLength(0);
        rec.append("CNT:" + recCount);
        if (hasUtc()) {
            rec.append("\nUTC:" + gps.log.out.CommonOut.getDateTimeStr(utc)
                    + "(" + utc + ")");
        }
        if (hasValid()) {
            rec.append("\nVALID:" + Convert.unsigned2hex(valid, 8));
        }
        if (hasLatitude()) {
            rec.append("\nLAT;" + latitude);
        }
        if (hasLongitude()) {
            rec.append("\nLON:" + longitude);
        }
        if (hasRcr()) {
            rec.append("\nRCR:" + Convert.unsigned2hex(rcr, 8));
        }
        if (hasHeight()) {
            rec.append("\nHEIGHT:" + height);
        }
        if (hasSpeed()) {
            rec.append("\nSPEED:" + speed);
        }
        if (hasHdop()) {
            rec.append("\nHDOP:" + hdop / 100.f);
        }
        if (hasVdop()) {
            rec.append("\nVDOP:" + vdop / 100.f);
        }
        if (hasPdop()) {
            rec.append("\nPDOP:" + pdop / 100.f);
        }
        rec.append('\n');
        return rec.toString();
    }

    public final int getUtc() {
        return utc;
    }

    public final void setUtc(final int utc) {
        this.utc = utc;
    }

    public final int getValid() {
        return valid;
    }

    public final void setValid(final int valid) {
        this.valid = valid;
    }

    public final double getLatitude() {
        return latitude;
    }

    public final void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public final double getLongitude() {
        return longitude;
    }

    public final void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public final float getHeight() {
        return height;
    }

    public final void setHeight(final float height) {
        this.height = height;
    }

    public final float getSpeed() {
        return speed;
    }

    public final void setSpeed(final float speed) {
        this.speed = speed;
    }

    public final float getHeading() {
        return heading;
    }

    public final void setHeading(final float heading) {
        this.heading = heading;
    }

    public final int getDsta() {
        return dsta;
    }

    public final void setDsta(final int dsta) {
        this.dsta = dsta;
    }

    public final int getDage() {
        return dage;
    }

    public final void setDage(final int dage) {
        this.dage = dage;
    }

    public final int getPdop() {
        return pdop;
    }

    public final void setPdop(final int pdop) {
        this.pdop = pdop;
    }

    public final int getHdop() {
        return hdop;
    }

    public final void setHdop(final int hdop) {
        this.hdop = hdop;
    }

    public final int getVdop() {
        return vdop;
    }

    public final void setVdop(final int vdop) {
        this.vdop = vdop;
    }

    public final int getNsat() {
        return nsat;
    }

    public final void setNsat(final int nsat) {
        this.nsat = nsat;
    }

    public final int[] getSid() {
        return sid;
    }

    public final void setSid(final int[] sid) {
        this.sid = sid;
    }

    public final boolean[] getSidinuse() {
        return sidinuse;
    }

    public final void setSidinuse(final boolean[] sidinuse) {
        this.sidinuse = sidinuse;
    }

    public final int[] getEle() {
        return ele;
    }

    public final void setEle(final int[] ele) {
        this.ele = ele;
    }

    public final int[] getAzi() {
        return azi;
    }

    public final void setAzi(final int[] azi) {
        this.azi = azi;
    }

    public final int[] getSnr() {
        return snr;
    }

    public final void setSnr(final int[] snr) {
        this.snr = snr;
    }

    public final int getRcr() {
        return rcr;
    }

    public final void setRcr(final int rcr) {
        this.rcr = rcr;
    }

    public final int getMilisecond() {
        return milisecond;
    }

    public final void setMilisecond(final int milisecond) {
        this.milisecond = milisecond;
    }

    public final double getDistance() {
        return distance;
    }

    public final void setDistance(final double distance) {
        this.distance = distance;
    }

    public final float getGeoid() {
        return geoid;
    }

    public final void setGeoid(final float geoid) {
        this.geoid = geoid;
    }

    public final int getRecCount() {
        return recCount;
    }

    public final void setRecCount(final int recCount) {
        this.recCount = recCount;
    }

    public final int getLogPeriod() {
        return logPeriod;
    }

    public final void setLogPeriod(final int logPeriod) {
        this.logPeriod = logPeriod;
    }

    public final int getLogSpeed() {
        return logSpeed;
    }

    public final void setLogSpeed(final int logSpeed) {
        this.logSpeed = logSpeed;
    }

    public final int getLogDistance() {
        return logDistance;
    }

    public final void setLogDistance(final int logDistance) {
        this.logDistance = logDistance;
    }

    public final String getVoxStr() {
        return voxStr;
    }

    public final void setVoxStr(final String voxStr) {
        this.voxStr = voxStr;
    }

    public final void tagFromRecord(final GPSRecord r) {
        // utc = r.utc;
        utc = r.utc;
        valid = r.valid;
        latitude = r.latitude;
        longitude = r.longitude;
        height = r.height;
        speed = r.speed;
        heading = r.heading;
        dsta = r.dsta;
        dage = r.dage;
        pdop = r.pdop;
        hdop = r.hdop;
        vdop = r.vdop;
        nsat = r.nsat;
        if (r.sid != null) {
            int i = r.sid.length;
            sid = new int[i];
            sidinuse = new boolean[i];
            if (r.ele != null) {
                ele = new int[i];
            }
            if (r.azi != null) {
                azi = new int[i];
            }
            if (r.snr != null) {
                snr = new int[i];
            }

            for (i -= 1; i >= 0; i--) {
                sid[i] = r.sid[i];
                sidinuse[i] = r.sidinuse[i];
                if (r.ele != null) {
                    ele[i] = r.ele[i];
                }
                if (r.azi != null) {
                    azi[i] = r.azi[i];
                }
                if (r.snr != null) {
                    snr[i] = r.snr[i];
                }
            }
        }
        // this.rcr = r.rcr;
        milisecond = r.milisecond;
        distance = r.distance;
        geoid = r.geoid;
        // this.recCount = r.recCount;
        logDistance = r.logDistance;
        logPeriod = r.logPeriod;
        logSpeed = r.logSpeed;
        // if (r.voxStr != null) {
        // this.voxStr = r.voxStr;
        // }
    }
}
