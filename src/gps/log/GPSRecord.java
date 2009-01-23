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
    public int utc = GPSRecord.NO_UTC;
    public int tagutc = GPSRecord.NO_UTC; // UTC used to tag.
    public int valid = GPSRecord.NO_VALID;
    public double latitude = GPSRecord.NO_LAT_LON;
    public double longitude = GPSRecord.NO_LAT_LON;
    public float height = GPSRecord.NO_HEIGHT;
    public float speed = GPSRecord.NO_SPEED;
    public float heading = GPSRecord.NO_HEADING;
    public int dsta = GPSRecord.NO_DSTA;
    public int dage = GPSRecord.NO_DAGE;
    public int pdop = GPSRecord.NO_XDOP;
    public int hdop = GPSRecord.NO_XDOP;
    public int vdop = GPSRecord.NO_XDOP;
    public int nsat = GPSRecord.NO_NSAT;

    public int[] sid = null;
    public boolean[] sidinuse = null;
    public int[] ele = null;
    public int[] azi = null;
    public int[] snr = null;

    /** Recorder reason */
    public int rcr = GPSRecord.NO_RCR;

    public int milisecond = GPSRecord.NO_MILISECOND;
    public double distance = GPSRecord.NO_DISTANCE;

    public float geoid = GPSRecord.NO_GEIOD; // Value returned by GPS.
    public int recCount = GPSRecord.NO_RECCOUNT;

    public int logPeriod = GPSRecord.NO_LOGCONSTRAINT; // Programmed period
    // between logged
    // points
    public int logSpeed = GPSRecord.NO_LOGCONSTRAINT; // Programmed minimum
    // speed
    public int logDistance = GPSRecord.NO_LOGCONSTRAINT; // Programmed
    // distance
    // between logged
    // points

    public String voxStr; // Voice recording reference.

    private static final int NO_RCR = 0x80000000
            | BT747Constants.RCR_DISTANCE_MASK
            | BT747Constants.RCR_SPEED_MASK | BT747Constants.RCR_TIME_MASK;
    private static final int HAS_RCR = BT747Constants.RCR_DISTANCE_MASK
            | BT747Constants.RCR_SPEED_MASK | BT747Constants.RCR_TIME_MASK;

    private static final int NO_UTC = 0;
    private static final int HAS_UTC = 1;

    private static final double NO_LAT_LON = -100000;
    private static final double HAS_LAT_LON = 0;

    private static final int NO_RECCOUNT = -1;
    private static final int HAS_RECCOUNT = 0;

    private static final int NO_VALID = -1;
    private static final int HAS_VALID = 0;

    private static final float NO_HEIGHT = -100000;
    private static final float HAS_HEIGHT = 0;

    private static final float NO_SPEED = -100000;
    private static final float HAS_SPEED = 0;

    private static final float NO_HEADING = -100000;
    private static final float HAS_HEADING = 0;

    private static final float NO_GEIOD = -100000;
    private static final float HAS_GEOID = 0;

    private static final int NO_LOGCONSTRAINT = 0;
    // private static final int LOG_CONSTRAINT = 0;

    private static final int NO_XDOP = -1;
    private static final int HAS_XDOP = 0;

    private static final int NO_DSTA = -1;
    private static final int HAS_DSTA = 0;

    private static final int NO_DAGE = -1;
    private static final int HAS_DAGE = 0;

    private static final int NO_NSAT = -1;
    private static final int HAS_NSAT = 0;

    private static final int NO_MILISECOND = -1;
    private static final int HAS_MILISECOND = 0;

    private static final int NO_DISTANCE = -1;
    private static final int HAS_DISTANCE = 0;

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

    public final void cloneActiveFields(final GPSRecord r) {
        if (r.hasUtc()) {
            utc = r.utc;
        }
        if (r.hasUtc()) {
            tagutc = r.tagutc;
        }
        if (r.hasValid()) {
            valid = r.valid;
        }
        if (r.hasLatitude()) {
            latitude = r.latitude;
        }
        if (r.hasLongitude()) {
            longitude = r.longitude;
        }
        if (r.hasHeight()) {
            height = r.height;
        }
        if (r.hasSpeed()) {
            speed = r.speed;
        }
        if (r.hasHeading()) {
            heading = r.heading;
        }
        if (r.hasDsta()) {
            dsta = r.dsta;
        }
        if (r.hasDage()) {
            dage = r.dage;
        }
        if (r.hasPdop()) {
            pdop = r.pdop;
        }
        if (r.hasHdop()) {
            hdop = r.hdop;
        }
        if (r.hasVdop()) {
            vdop = r.vdop;
        }
        if (r.hasNsat()) {
            nsat = r.nsat;
        }
        if (r.hasSid()) {
            // Object method clone() does not work on device for arrays.
            // Doing explicit copy until better method found.
            int i = r.sid.length;
            sid = new int[i];
            sidinuse = new boolean[i];
            if (r.hasEle()) {
                ele = new int[i];
            }
            if (r.hasAzi()) {
                azi = new int[i];
            }
            if (r.hasSnr()) {
                snr = new int[i];
            }

            for (i -= 1; i >= 0; i--) {
                sid[i] = r.sid[i];
                sidinuse[i] = r.sidinuse[i];
                if (r.hasEle()) {
                    ele[i] = r.ele[i];
                }
                if (r.hasAzi()) {
                    azi[i] = r.azi[i];
                }
                if (r.hasSnr()) {
                    snr[i] = r.snr[i];
                }
            }
        }
        if (r.hasRcr()) {
            rcr = r.rcr;
        }
        if (r.hasMillisecond()) {
            milisecond = r.milisecond;
        }
        if (r.hasDistance()) {
            distance = r.distance;
        }
        if (r.hasGeoid()) {
            geoid = r.geoid;
        }
        if (r.hasRecCount()) {
            recCount = r.recCount;
        }
        if (r.hasLogDistance()) {
            logDistance = r.logDistance;
        }
        if (r.hasLogPeriod()) {
            logPeriod = r.logPeriod;
        }
        if (r.hasLogSpeed()) {
            logSpeed = r.logSpeed;
        }
        if (r.hasVoxStr()) {
            voxStr = r.voxStr;
        }
    }

    public final GPSRecord cloneRecord() {
        return new GPSRecord(this);
    }

    /** Return log format based on content. */
    public final int getLogFormat() {
        int logFormat = 0;
        if (hasUtc()) {
            logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
        }
        if (hasValid()) {
            logFormat |= (1 << BT747Constants.FMT_VALID_IDX);
        }
        if (hasLatitude()) {
            logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);
        }
        if (hasLongitude()) {
            logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
        }
        if (hasHeight()) {
            logFormat |= (1 << BT747Constants.FMT_HEIGHT_IDX);
        }
        if (hasSpeed()) {
            logFormat |= (1 << BT747Constants.FMT_SPEED_IDX);
        }
        if (hasHeading()) {
            logFormat |= (1 << BT747Constants.FMT_HEADING_IDX);
        }
        if (hasDsta()) {
            logFormat |= (1 << BT747Constants.FMT_DSTA_IDX);
        }
        if (hasDage()) {
            logFormat |= (1 << BT747Constants.FMT_DAGE_IDX);
        }
        if (hasPdop()) {
            logFormat |= (1 << BT747Constants.FMT_PDOP_IDX);
        }
        if (hasHdop()) {
            logFormat |= (1 << BT747Constants.FMT_HDOP_IDX);
        }
        if (hasVdop()) {
            logFormat |= (1 << BT747Constants.FMT_VDOP_IDX);
        }
        if (hasNsat()) {
            logFormat |= (1 << BT747Constants.FMT_NSAT_IDX);
        }
        if (hasSid()) {
            logFormat |= (1 << BT747Constants.FMT_SID_IDX);
        }
        if (hasEle()) {
            logFormat |= (1 << BT747Constants.FMT_ELEVATION_IDX);
        }
        if (hasAzi()) {
            logFormat |= (1 << BT747Constants.FMT_AZIMUTH_IDX);
        }
        if (hasSnr()) {
            logFormat |= (1 << BT747Constants.FMT_SNR_IDX);
        }
        if (hasRcr()) {
            logFormat |= (1 << BT747Constants.FMT_RCR_IDX);
        }
        if (hasMillisecond()) {
            logFormat |= (1 << BT747Constants.FMT_MILLISECOND_IDX);
        }
        if (hasDistance()) {
            logFormat |= (1 << BT747Constants.FMT_DISTANCE_IDX);
        }
        return logFormat;
    }

    public final static GPSRecord getLogFormatRecord(final int logFormat) {
        final GPSRecord gpsRec = new GPSRecord();
        if ((logFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0) {
            gpsRec.utc = GPSRecord.HAS_UTC;
        }
        if ((logFormat & (1 << BT747Constants.FMT_VALID_IDX)) != 0) {
            gpsRec.valid = GPSRecord.HAS_VALID;
        }
        if ((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0) {
            gpsRec.latitude = GPSRecord.HAS_LAT_LON;
        }
        if ((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0) {
            gpsRec.longitude = GPSRecord.HAS_LAT_LON;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEIGHT_IDX)) != 0) {
            gpsRec.height = GPSRecord.HAS_HEIGHT;
        }
        if ((logFormat & (1 << BT747Constants.FMT_SPEED_IDX)) != 0) {
            gpsRec.speed = GPSRecord.HAS_SPEED;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEADING_IDX)) != 0) {
            gpsRec.heading = GPSRecord.HAS_HEADING;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DSTA_IDX)) != 0) {
            gpsRec.dsta = GPSRecord.HAS_DSTA;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DAGE_IDX)) != 0) {
            gpsRec.dage = GPSRecord.HAS_DAGE;
        }
        if ((logFormat & (1 << BT747Constants.FMT_PDOP_IDX)) != 0) {
            gpsRec.pdop = GPSRecord.HAS_XDOP;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HDOP_IDX)) != 0) {
            gpsRec.hdop = GPSRecord.HAS_XDOP;
        }
        if ((logFormat & (1 << BT747Constants.FMT_VDOP_IDX)) != 0) {
            gpsRec.vdop = GPSRecord.HAS_XDOP;
        }
        if ((logFormat & (1 << BT747Constants.FMT_NSAT_IDX)) != 0) {
            gpsRec.nsat = GPSRecord.HAS_NSAT;
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
            gpsRec.rcr = GPSRecord.HAS_RCR;
        }
        if ((logFormat & (1 << BT747Constants.FMT_MILLISECOND_IDX)) != 0) {
            gpsRec.milisecond = GPSRecord.HAS_MILISECOND;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DISTANCE_IDX)) != 0) {
            gpsRec.distance = GPSRecord.HAS_DISTANCE;
        }

        /* End handling record */
        return gpsRec;
    }

    public final boolean hasRecCount() {
        return recCount != GPSRecord.NO_RECCOUNT;
    }

    public final boolean hasUtc() {
        return utc != GPSRecord.NO_UTC;
    }

    public final boolean hasTagUtc() {
        return tagutc != GPSRecord.NO_UTC;
    }

    public final boolean hasValid() {
        return valid != GPSRecord.NO_VALID;
    }

    public final boolean hasPosition() {
        return hasLatitude() && hasLongitude();
    }

    public final boolean hasLatitude() {
        return latitude != GPSRecord.NO_LAT_LON;
    }

    public final boolean hasLongitude() {
        return longitude != GPSRecord.NO_LAT_LON;
    }

    public final boolean hasHeight() {
        return height != GPSRecord.NO_HEIGHT;
    }

    public final boolean hasSpeed() {
        return speed != GPSRecord.NO_SPEED;
    }

    public final boolean hasHeading() {
        return heading != GPSRecord.NO_HEADING;
    }

    public final boolean hasDsta() {
        return dsta != GPSRecord.NO_DSTA;
    }

    public final boolean hasDage() {
        return dage != GPSRecord.NO_DAGE;
    }

    public final boolean hasPdop() {
        return pdop != GPSRecord.NO_XDOP;
    }

    public final boolean hasHdop() {
        return hdop != GPSRecord.NO_XDOP;
    }

    public final boolean hasVdop() {
        return vdop != GPSRecord.NO_XDOP;
    }

    public final boolean hasNsat() {
        return nsat != GPSRecord.NO_NSAT;
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
        return rcr != GPSRecord.NO_RCR;
    }

    public final boolean hasMillisecond() {
        return milisecond != GPSRecord.NO_MILISECOND;
    }

    public final boolean hasDistance() {
        return distance != GPSRecord.NO_DISTANCE;
    }

    public final boolean hasVoxStr() {
        return voxStr != null;
    }

    public final boolean hasGeoid() {
        return geoid != GPSRecord.NO_GEIOD;
    }

    public final boolean hasLogDistance() {
        return logDistance != GPSRecord.NO_LOGCONSTRAINT;
    }

    public final boolean hasLogPeriod() {
        return logPeriod != GPSRecord.NO_LOGCONSTRAINT;
    }

    public final boolean hasLogSpeed() {
        return logSpeed != GPSRecord.NO_LOGCONSTRAINT;
    }

    public final boolean equalsFormat(final GPSRecord r) {
        return (hasUtc() == r.hasUtc())
                && (hasValid() == r.hasValid())
                && (hasLatitude() == r.hasLatitude())
                && (hasLongitude() == r.hasLongitude())
                && (hasHeight() == r.hasHeight())
                && (hasSpeed() == r.hasSpeed())
                && (hasHeading() == r.hasHeading())
                && (hasDsta() == r.hasDsta())
                && (hasDage() == r.hasDage())
                && (hasPdop() == r.hasPdop())
                && (hasHdop() == r.hasHdop())
                && (hasVdop() == r.hasVdop())
                && (hasNsat() == r.hasNsat())
                && (hasSid() == r.hasSid())
                && (hasSidInUse() == r.hasSidInUse())
                && (hasEle() == r.hasEle())
                && (hasAzi() == r.hasAzi())
                && (hasSnr() == r.hasSnr())
                && (hasRcr() == r.hasRcr())
                && (hasMillisecond() == r.hasMillisecond())
                && (hasDistance() == r.hasDistance())
                && ((hasVoxStr() == r.hasVoxStr())
                        && (hasLogDistance() == r.hasLogDistance())
                        && (hasLogSpeed() == r.hasLogSpeed()) && (hasLogPeriod() == r
                        .hasLogSpeed()));
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
