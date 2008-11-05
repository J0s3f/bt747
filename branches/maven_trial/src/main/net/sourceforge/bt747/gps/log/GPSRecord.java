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
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************       
package net.sourceforge.bt747.gps.log;

import net.sourceforge.bt747.gps.BT747Constants;

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

    public GPSRecord() {
        utc = 0;
    }

    /**
     * 
     */
    public GPSRecord(GPSRecord r) {
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
    }

    public final GPSRecord cloneRecord() {
        return new GPSRecord(this);
    }

    public static GPSRecord getLogFormatRecord(final int logFormat) {
        GPSRecord gpsRec = new GPSRecord();
        if ((logFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0) {
            gpsRec.utc = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_VALID_IDX)) != 0) {
            gpsRec.valid = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0) {
            gpsRec.latitude = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0) {
            gpsRec.longitude = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEIGHT_IDX)) != 0) {
            gpsRec.height = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_SPEED_IDX)) != 0) {
            gpsRec.speed = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEADING_IDX)) != 0) {
            gpsRec.heading = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DSTA_IDX)) != 0) {
            gpsRec.dsta = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DAGE_IDX)) != 0) {
            gpsRec.dage = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_PDOP_IDX)) != 0) {
            gpsRec.pdop = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HDOP_IDX)) != 0) {
            gpsRec.hdop = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_VDOP_IDX)) != 0) {
            gpsRec.vdop = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_NSAT_IDX)) != 0) {
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
            gpsRec.rcr = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_MILLISECOND_IDX)) != 0) {
            gpsRec.milisecond = -1;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DISTANCE_IDX)) != 0) {
            gpsRec.distance = -1;
        }

        /* End handling record */
        return gpsRec;
    }
}