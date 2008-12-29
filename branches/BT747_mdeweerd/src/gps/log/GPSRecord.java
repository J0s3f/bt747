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
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
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
            gpsRec.rcr = -1;
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
        return rcr != -1;
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
        rec.append("\nUTC:" + gps.log.out.CommonOut.getDateTimeStr(utc) + "(" + utc
                + ")");
        rec.append("\nVALID:" + Convert.unsigned2hex(valid, 8));
        rec.append("\nLAT;" + latitude);
        rec.append("\nLON:" + longitude);
        rec.append("\nRCR:" + Convert.unsigned2hex(rcr, 8));
        rec.append('\n');
        return rec.toString();
    }
}
