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

import bt747.sys.Convert;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747Time;

import gps.BT747Constants;

/**
 * Structure to hold GPS data for one point
 * 
 * @author Mario De Weerd
 */
public class GPSRecord {
    public int utc;
    public int valid;
    public double latitude;
    public double longitude;
    public float height;
    public float speed;
    public float heading;
    public int dsta;
    public int dage;
    public int pdop;
    public int hdop;
    public int vdop;
    public int nsat;

    public int[] sid;
    public boolean[] sidinuse;
    public int[] ele;
    public int[] azi;
    public int[] snr;

    public int rcr;
    private static final int NO_RCR = 0x80000000
            | BT747Constants.RCR_DISTANCE_MASK
            | BT747Constants.RCR_SPEED_MASK | BT747Constants.RCR_TIME_MASK;

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
        this.utc = r.utc;
        this.valid = r.valid;
        this.latitude = r.latitude;
        this.longitude = r.longitude;
        this.height = r.height;
        this.speed = r.speed;
        this.heading = r.heading;
        this.dsta = r.dsta;
        this.dage = r.dage;
        this.pdop = r.pdop;
        this.hdop = r.hdop;
        this.vdop = r.vdop;
        this.nsat = r.nsat;
        if (r.sid != null) {
            // Object method clone() does not work on device for arrays.
            // Doing explicit copy until better method found.
            int i = r.sid.length;
            this.sid = new int[i];
            this.sidinuse = new boolean[i];
            if (r.ele != null) {
                this.ele = new int[i];
            }
            if (r.azi != null) {
                this.azi = new int[i];
            }
            if (r.snr != null) {
                this.snr = new int[i];
            }

            for (i -= 1; i >= 0; i--) {
                this.sid[i] = r.sid[i];
                this.sidinuse[i] = r.sidinuse[i];
                if (r.ele != null) {
                    this.ele[i] = r.ele[i];
                }
                if (r.azi != null) {
                    this.azi[i] = r.azi[i];
                }
                if (r.snr != null) {
                    this.snr[i] = r.snr[i];
                }
            }
        }
        this.rcr = r.rcr;
        this.milisecond = r.milisecond;
        this.distance = r.distance;
        this.geoid = r.geoid;
        this.recCount = r.recCount;
        this.logDistance = r.logDistance;
        this.logPeriod = r.logPeriod;
        this.logSpeed = r.logSpeed;
        if (r.voxStr != null) {
            this.voxStr = r.voxStr;
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
        GPSRecord gpsRec = new GPSRecord();
        if ((logFormat & (1 << BT747Constants.FMT_UTC_IDX)) == 0) {
            gpsRec.utc = 0;
        } else {
            gpsRec.utc = 1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_VALID_IDX)) == 0) {
            gpsRec.valid = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) == 0) {
            gpsRec.latitude = -1000000;
        }
        if ((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) == 0) {
            gpsRec.longitude = -1000000;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEIGHT_IDX)) == 0) {
            gpsRec.height = -1000000;
        }
        if ((logFormat & (1 << BT747Constants.FMT_SPEED_IDX)) == 0) {
            gpsRec.speed = -1000000;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEADING_IDX)) == 0) {
            gpsRec.heading = -1000000;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DSTA_IDX)) == 0) {
            gpsRec.dsta = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DAGE_IDX)) == 0) {
            gpsRec.dage = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_PDOP_IDX)) == 0) {
            gpsRec.pdop = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HDOP_IDX)) == 0) {
            gpsRec.hdop = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_VDOP_IDX)) == 0) {
            gpsRec.vdop = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_NSAT_IDX)) == 0) {
            gpsRec.nsat = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_SID_IDX)) == 0) {
            gpsRec.sid = null;
            gpsRec.sidinuse = null;
        } else {
            gpsRec.sid = new int[0];
            gpsRec.sidinuse = new boolean[0];
        }

        if ((logFormat & (1 << BT747Constants.FMT_ELEVATION_IDX)) == 0) {
            gpsRec.ele = null;
        } else {
            gpsRec.ele = new int[0];
        }
        if ((logFormat & (1 << BT747Constants.FMT_AZIMUTH_IDX)) == 0) {
            gpsRec.azi = null;
        } else {
            gpsRec.azi = new int[0];
        }
        if ((logFormat & (1 << BT747Constants.FMT_SNR_IDX)) == 0) {
            gpsRec.snr = null;
        } else {
            gpsRec.snr = new int[0];
        }
        if ((logFormat & (1 << BT747Constants.FMT_RCR_IDX)) == 0) {
            gpsRec.rcr = NO_RCR;
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

    public final boolean hasUtc() {
        return utc != 0;
    }

    public final boolean hasValid() {
        return valid != -1;
    }

    public final boolean hasLocation() {
        return hasLatitude() && hasLongitude();
    }

    public final boolean hasLatitude() {
        return latitude != -1000000;
    }

    public final boolean hasLongitude() {
        return longitude != -1000000;
    }

    public final boolean hasHeight() {
        return height != -1000000;
    }

    public final boolean hasSpeed() {
        return speed != -1000000;
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
        return pdop != -1;
    }

    public final boolean hasHdop() {
        return hdop != -1;
    }

    public final boolean hasVdop() {
        return vdop != -1;
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
        BT747Time t = Interface.getTimeInstance();
        t.setUTCTime(utc);
        return t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer rec = new StringBuffer(100);
        rec.setLength(0);
        rec.append("CNT:" + recCount);
        if (hasUtc())
            rec.append("\nUTC:" + gps.log.out.CommonOut.getDateTimeStr(utc)
                    + "(" + utc + ")");
        if (hasValid())
            rec.append("\nVALID:" + Convert.unsigned2hex(valid, 8));
        if (hasLatitude())
            rec.append("\nLAT;" + latitude);
        if (hasLongitude())
            rec.append("\nLON:" + longitude);
        if (hasRcr())
            rec.append("\nRCR:" + Convert.unsigned2hex(rcr, 8));
        if (hasHeight())
            rec.append("\nHEIGHT:" + height);
        if (hasSpeed())
            rec.append("\nSPEED:" + speed);
        if (hasHdop())
            rec.append("\nHDOP:" + hdop / 100.f);
        if (hasVdop())
            rec.append("\nVDOP:" + vdop / 100.f);
        if (hasPdop())
            rec.append("\nPDOP:" + pdop / 100.f);
        rec.append('\n');
        return rec.toString();
    }

    public final int getUtc() {
        return this.utc;
    }

    public final void setUtc(int utc) {
        this.utc = utc;
    }

    public final int getValid() {
        return this.valid;
    }

    public final void setValid(int valid) {
        this.valid = valid;
    }

    public final double getLatitude() {
        return this.latitude;
    }

    public final void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public final double getLongitude() {
        return this.longitude;
    }

    public final void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public final float getHeight() {
        return this.height;
    }

    public final void setHeight(float height) {
        this.height = height;
    }

    public final float getSpeed() {
        return this.speed;
    }

    public final void setSpeed(float speed) {
        this.speed = speed;
    }

    public final float getHeading() {
        return this.heading;
    }

    public final void setHeading(float heading) {
        this.heading = heading;
    }

    public final int getDsta() {
        return this.dsta;
    }

    public final void setDsta(int dsta) {
        this.dsta = dsta;
    }

    public final int getDage() {
        return this.dage;
    }

    public final void setDage(int dage) {
        this.dage = dage;
    }

    public final int getPdop() {
        return this.pdop;
    }

    public final void setPdop(int pdop) {
        this.pdop = pdop;
    }

    public final int getHdop() {
        return this.hdop;
    }

    public final void setHdop(int hdop) {
        this.hdop = hdop;
    }

    public final int getVdop() {
        return this.vdop;
    }

    public final void setVdop(int vdop) {
        this.vdop = vdop;
    }

    public final int getNsat() {
        return this.nsat;
    }

    public final void setNsat(int nsat) {
        this.nsat = nsat;
    }

    public final int[] getSid() {
        return this.sid;
    }

    public final void setSid(int[] sid) {
        this.sid = sid;
    }

    public final boolean[] getSidinuse() {
        return this.sidinuse;
    }

    public final void setSidinuse(boolean[] sidinuse) {
        this.sidinuse = sidinuse;
    }

    public final int[] getEle() {
        return this.ele;
    }

    public final void setEle(int[] ele) {
        this.ele = ele;
    }

    public final int[] getAzi() {
        return this.azi;
    }

    public final void setAzi(int[] azi) {
        this.azi = azi;
    }

    public final int[] getSnr() {
        return this.snr;
    }

    public final void setSnr(int[] snr) {
        this.snr = snr;
    }

    public final int getRcr() {
        return this.rcr;
    }

    public final void setRcr(int rcr) {
        this.rcr = rcr;
    }

    public final int getMilisecond() {
        return this.milisecond;
    }

    public final void setMilisecond(int milisecond) {
        this.milisecond = milisecond;
    }

    public final double getDistance() {
        return this.distance;
    }

    public final void setDistance(double distance) {
        this.distance = distance;
    }

    public final float getGeoid() {
        return this.geoid;
    }

    public final void setGeoid(float geoid) {
        this.geoid = geoid;
    }

    public final int getRecCount() {
        return this.recCount;
    }

    public final void setRecCount(int recCount) {
        this.recCount = recCount;
    }

    public final int getLogPeriod() {
        return this.logPeriod;
    }

    public final void setLogPeriod(int logPeriod) {
        this.logPeriod = logPeriod;
    }

    public final int getLogSpeed() {
        return this.logSpeed;
    }

    public final void setLogSpeed(int logSpeed) {
        this.logSpeed = logSpeed;
    }

    public final int getLogDistance() {
        return this.logDistance;
    }

    public final void setLogDistance(int logDistance) {
        this.logDistance = logDistance;
    }

    public final String getVoxStr() {
        return this.voxStr;
    }

    public final void setVoxStr(String voxStr) {
        this.voxStr = voxStr;
    }
}
