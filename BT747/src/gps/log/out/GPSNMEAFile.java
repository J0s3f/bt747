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
import gps.convert.Conv;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Time;

/**
 * Class to write a NMEA file.
 * 
 * @author Mario De Weerd
 */
public final class GPSNMEAFile extends GPSFile {
    private static final double KMH_PER_KNOT = 0.53995680345572354211663066954644;

    private final StringBuffer rec = new StringBuffer(1024); // reused
    // stringbuffer

    private int fieldsNmeaOut;

    // getParamObject().getIntParam( GPSConversionParameters.NMEA_OUTFIELDS);

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#initialiseFile(java.lang.String,
     * java.lang.String, int, int)
     */
    public void initialiseFile(final BT747Path baseName, final String extension,
            final int fileSeparationFreq) {
        super.initialiseFile(baseName, extension, fileSeparationFreq);
        fieldsNmeaOut = getParamObject().getIntParam(
                GPSConversionParameters.NMEA_OUTFIELDS);
    }

    private final void writeNMEA(final String s) {
        int z_Checksum = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            z_Checksum ^= (byte) s.charAt(i);
        }
        writeTxt("$");
        writeTxt(s);
        writeTxt("*" + JavaLibBridge.unsigned2hex(z_Checksum, 2) + "\r\n");
    }

    /**
     * Returns true when the record is used by the format.
     * 
     * Override parent class because only the trackpoint filter is used.
     */
    protected final boolean recordIsNeeded(final GPSRecord r) {
        return ptFilters[GPSFilter.TRKPT].doFilter(r);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);
        if (cachedRecordIsNeeded(r)) {
//                ptFilters[GPSFilter.TRKPT].doFilter(r)) {
            final String timeStr = getTimeStr(t, r, selectedFileFields);

            if ((fieldsNmeaOut & (1 << BT747Constants.NMEA_SEN_ZDA_IDX)) != 0) {
                writeZDA(r, timeStr);
            }
            if ((fieldsNmeaOut & (1 << BT747Constants.NMEA_SEN_RMC_IDX)) != 0) {
                writeRMC(r, timeStr);
            }
            if ((fieldsNmeaOut & (1 << BT747Constants.NMEA_SEN_GGA_IDX)) != 0) {
                writeGGA(r, timeStr);
            }
            if ((fieldsNmeaOut & (1 << BT747Constants.NMEA_SEN_GSA_IDX)) != 0) {
                writeGSA(r, timeStr);
            }
            if ((fieldsNmeaOut & (1 << BT747Constants.NMEA_SEN_GSV_IDX)) != 0) {
                writeGSV(r, timeStr);
            }
            if ((fieldsNmeaOut & (1 << BT747Constants.NMEA_SEN_WPL_IDX)) != 0) {
                if(ptFilters[GPSFilter.WAYPT].doFilter(r)) {
                    writeWPL(r);
                }
            }
        }
    }

    /**
     * @param r
     * @return
     */
    private final static String getTimeStr(final BT747Time t,
            final GPSRecord r, final GPSRecord selectedFileFields) {
        String timeStr = "";
        if ((r.hasUtc() && selectedFileFields.hasUtc())) {
            timeStr = (t.getHour() < 10 ? "0" : "") + t.getHour()
                    + (t.getMinute() < 10 ? "0" : "") + t.getMinute()
                    + (t.getSecond() < 10 ? "0" : "") + t.getSecond();
            if (r.hasMillisecond() && selectedFileFields.hasMillisecond()) {
                timeStr += "." + ((r.milisecond < 100) ? "0" : "")
                        + ((r.milisecond < 10) ? "0" : "") + (r.milisecond);
            }
        }
        return timeStr;
    }

    private void writeRMC(final GPSRecord r, final String timeStr) {
        writeNMEA(toRMC(rec, r, t, timeStr, selectedFileFields));
    }

    /**
     * Return RMC string for record.
     * 
     * @param r
     *            Record to use.
     * @return RMC sentence.
     */
    public final static String toRMC(final GPSRecord r) {
        final GPSRecord selected = GPSRecord.getLogFormatRecord(0xFFFFFFFF);
        final BT747Time t = JavaLibBridge.getTimeInstance();
        t.setUTCTime(r.getUtc()); // Initialization needed later too!
        return toRMC(new StringBuffer(255), r, t, getTimeStr(t, r, selected),
                selected);
    }

    /** Add lat and lon information to the NMEA sentence.
     * @param rec
     * @param r
     * @param selectedFileFields
     */
    private final static void appendLatLon(final StringBuffer rec,
            final GPSRecord r, final GPSRecord selectedFileFields) {
        if (r.hasLatitude() && selectedFileFields.hasLatitude()) {
            String sl;
            double l;
            if (r.getLatitude() >= 0) {
                sl = ",N,";
                l = r.getLatitude();
            } else {
                sl = ",S,";
                l = -r.getLatitude();
            }
            final int a = (int) Math.floor(l);
            rec.append(((a < 10) ? "0" : "") + a);
            l -= a;
            l *= 60;
            // TODO: check if bug when a number like 9.9999999
            rec.append(((l < 10) ? "0" : "") + JavaLibBridge.toString(l, 6));
            rec.append(sl);
        } else {
            rec.append(",,");
        }

        if (r.hasLongitude() && selectedFileFields.hasLongitude()) {
            String sl;
            double l;
            if (r.getLongitude() >= 0) {
                sl = ",E,";
                l = r.getLongitude();
            } else {
                sl = ",W,";
                l = -r.getLongitude();
            }
            final int a = (int) Math.floor(l);
            rec.append(((a < 100) ? "0" : "") + ((a < 10) ? "0" : "") + a);
            l -= a;
            l *= 60;
            // TODO: check if bug when a number like 9.9999999
            rec.append(((l < 10) ? "0" : "") + JavaLibBridge.toString(l, 6));
            rec.append(sl);
        } else {
            rec.append(",,");
        }
    }

    /**
     * Return RMC string for record.
     */
    private final static String toRMC(final StringBuffer rec,
            final GPSRecord r, final BT747Time t, final String timeStr,
            final GPSRecord selectedFileFields) {
        // eg4.
        // GPRMC,hhmmss.ss,A,llll.ll,a,yyyyy.yy,a,x.x,x.x,ddmmyy,x.x,a*hh
        // 1 = UTC of position fix (hhmmss.ss)
        // 2 = Data status (V=navigation receiver warning) (A)
        // 3 = Latitude of fix (llll.ll)
        // 4 = N or S (a)
        // 5 = Longitude of fix (yyyyy.yy)
        // 6 = E or W (a)
        // 7 = Speed over ground in knots (x.x)
        // 8 = Track made good in degrees True (x.x)
        // 9 = UT date (ddmmyy)
        // 10 = Magnetic variation degrees (Easterly var. subtracts from
        // true course) (x.x)
        // 11 = E or W (a)
        // Transystem extrafield: A=Autonomous, D=DGPS
        // 12 = Checksum
        // From the device (position values changed, so checksum incorrect):
        // GPRMC,hhmmss.ss, A,llll.ll, a,yyyyy.yy, a,x.x, x.x
        // ,ddmmyy,x.x,a*hh
        // GPRMC,190633.500,A,4800.0000,N,00200.0000,E,0.31,123.10,261007,,,A*68

        rec.setLength(0);
        rec.append("GPRMC,");

        if ((r.hasUtc() && selectedFileFields.hasUtc())) {
            // 1 = UTC of position fix
            rec.append(timeStr);
        }

        // 2 = Data status (V=navigation receiver warning)
        if ((r.hasValid() && selectedFileFields.hasValid())) {
            String tmp;
            switch (r.getValid()) {
            case BT747Constants.VALID_NO_FIX_MASK:
                tmp = ",V,"; // "No fix";
                break;
            default:
                tmp = ",A,";
            }
            rec.append(tmp);
        } else {
            rec.append(",,");
        }

        // 3 = Latitude of fix
        // 4 = N or S
        // 5 = Longitude of fix
        // 6 = E or W
        appendLatLon(rec, r, selectedFileFields);

        // 7 = Speed over ground in knots
        // 8 = Track made good in degrees True
        // 9 = UT date
        // 10 = Magnetic variation degrees (Easterly var. subtracts from
        // true course)
        // 11 = E or W
        // 12 = Checksum
        if (r.hasSpeed() && selectedFileFields.hasSpeed()) {
            rec.append(JavaLibBridge.toString(r.getSpeed()
                    * GPSNMEAFile.KMH_PER_KNOT, 3));
        }
        rec.append(",");
        if (r.hasHeading() && selectedFileFields.hasHeading()) {
            rec.append(JavaLibBridge.toString(r.getHeading(), 6));
        }
        rec.append(",");
        if ((r.hasUtc() && selectedFileFields.hasUtc())) {
            // DATE & TIME
            rec.append((t.getDay() < 10 ? "0" : "") + t.getDay()
                    + (t.getMonth() < 10 ? "0" : "") + t.getMonth()
                    + (((t.getYear() % 100) < 10) ? "0" : "")
                    + (t.getYear() % 100));
        }
        rec.append(",,");
        // Extra field on transystem
        if ((r.hasValid() && selectedFileFields.hasValid() && (r.getValid() == 0x0004))) {
            rec.append(",D");
        } else {
            rec.append(",A");
        }
        final String result = rec.toString();
        rec.setLength(0);
        return result;
    }

    private void writeGGA(final GPSRecord r, final String timeStr) {
        writeNMEA(toGGA(rec, r, timeStr, selectedFileFields));
    }

    public final static String toGGA(final GPSRecord r) {
        final GPSRecord selected = GPSRecord.getLogFormatRecord(0xFFFFFFFF);
        final BT747Time t = JavaLibBridge.getTimeInstance();
        t.setUTCTime(r.getUtc()); // Initialization needed later too!
        return toGGA(new StringBuffer(255), r, getTimeStr(t, r, selected),
                selected);
    }

    /**
     * Creates an NMEA string from the record.
     * 
     * @param r
     * @param timeStr
     */
    private static final String toGGA(final StringBuffer rec,
            final GPSRecord r, final String timeStr,
            final GPSRecord selectedFileFields) {
        rec.setLength(0);
        rec.append("GPGGA,");

        if ((r.hasUtc() && selectedFileFields.hasUtc())) {
            rec.append(timeStr);
        }
        rec.append(",");
        appendLatLon(rec, r, selectedFileFields);


        // - 1 is the fix quality. The fix quality can have a value
        // between 0 and 3, defined as follows
        // - 0=no fix
        // - 1=GPS or standard positioning service (SPS) fix
        // - 2=DGPS fix
        // - 3=Precise positioning service (PPS) fix

        if ((r.hasValid() && selectedFileFields.hasValid())) {
            String tmp = "";
            switch (r.getValid()) {
            case BT747Constants.VALID_NO_FIX_MASK:
                tmp = "0"; // "No fix";
                break;
            case BT747Constants.VALID_SPS_MASK:
                tmp = "1"; // "SPS";
                break;
            case BT747Constants.VALID_DGPS_MASK:
                tmp = "2"; // DGPS
                break;
            case BT747Constants.VALID_PPS_MASK:
                tmp = "3"; // PPS, Military signal
                break;
            case 0x0010:
                tmp = "4";
                break;
            case 0x0020:
                tmp = "5";
                break;
            case BT747Constants.VALID_ESTIMATED_MASK:
                // tmp+= "Estimated mode";
                break;
            case BT747Constants.VALID_MANUAL_MASK:
                // tmp+= "Manual input mode";
                break;
            case BT747Constants.VALID_SIMULATOR_MASK:
                // tmp+= "Simulator mode";
                break;
            default:
                // tmp+="Unknown mode";
            }
            rec.append(tmp);
        }
        rec.append(",");

        // - 08 is the number of SV's being tracked
        if (r.hasNsat() && selectedFileFields.hasNsat()) {
            rec.append((r.getNsat() & 0xFF00) >> 8);
            // rec+="("+JavaLibBridge.toString(s.nsat&0xFF)+")";
        } else {
            rec.append("8");
        }
        rec.append(",");

        // - 0.9 is the horizontal dilution of position (HDOP)
        if (r.hasHdop() && selectedFileFields.hasHdop()) {
            rec.append(JavaLibBridge.toString(r.getHdop() / 100.0f, 2));
        }
        rec.append(",");
        // - 133.4,M is the altitude, in meters, above mean sea level
        if ((r.hasHeight() && selectedFileFields.hasHeight())) {
            float separation = 0.0f;
            final boolean hasLatLon = r.hasPosition()
                    && selectedFileFields.hasPosition();
            if (hasLatLon) {
                separation = ((long) (10 * Conv.wgs84Separation(r
                        .getLatitude(), r.getLongitude()))) / 10.f;
            }
            rec.append(JavaLibBridge.toString(r.getHeight() - separation, 3));
            if (hasLatLon) {
                rec.append(",M,");
                rec.append(JavaLibBridge.toString(separation, 1));
                rec.append(",M,");
            } else {
                rec.append(",M,0,M,");
            }
        } else {
            rec.append(",,,,");
        }
        // - 46.9,M is the height of the geoid (mean sea level) above
        // the WGS84 ellipsoid
        // - (empty field) is the DGPS station ID number
        // - *42 is the checksum field

        if ((r.hasDage() && selectedFileFields.hasDage())) {
            rec.append(r.getDage());
        }
        rec.append(",");

        if ((r.hasDsta() && selectedFileFields.hasDsta())) {
            rec.append(r.getDsta());
        }

        final String result = rec.toString();
        rec.setLength(0);
        return result;
    }

    private void writeGSV(final GPSRecord r, final String timeStr) {
        // GPGSV
        // GPS Satellites in view
        //
        // eg.
        // GPGSV,3,1,11,03,03,111,00,04,15,270,00,06,01,010,00,13,06,292,00*74
        // GPGSV,3,2,11,14,25,170,00,16,57,208,39,18,67,296,40,19,40,246,00*74
        // GPGSV,3,3,11,22,42,067,42,24,14,311,43,27,05,244,00,,,,*4D
        //
        // GPGSV,1,1,13,02,02,213,,03,-3,000,,11,00,121,,14,13,172,05*62
        //
        // 1 = Total number of messages of this type in this cycle
        // 2 = Message number
        // 3 = Total number of SVs in view
        // 4 = SV PRN number
        // 5 = Elevation in degrees, 90 maximum
        // 6 = Azimuth, degrees from true north, 000 to 359
        // 7 = SNR, 00-99 dB (null when not tracking)
        // 8-11 = Information about second SV, same as field 4-7
        // 12-15= Information about third SV, same as field 4-7
        // 16-19= Information about fourth SV, same as field 4-7
        //
        if (r.hasSid() && selectedFileFields.hasSid()) {
            int j;
            int i;
            int m;
            for (i = (r.sid.length - 1), m = 1, j = 0; i >= 0; m++) {
                rec.setLength(0);
                rec.append("GPGSV,");
                rec.append((r.sid.length + 3) / 4);
                rec.append(",");
                rec.append(m);
                rec.append(",");
                // rec.append(JavaLibBridge.toString((s.nsat&0xFF00)>>8)); //
                // in use
                rec.append(r.getNsat() & 0xFF); // in
                // view
                rec.append(",");
                int n;
                for (n = 4; n > 0; i--, n--, j++) {

                    if (i >= 0) {
                        if (r.sid[j] < 10) {
                            rec.append('0');
                        }
                        rec.append(r.sid[j]);
                        rec.append(",");

                        if (r.hasEle() && selectedFileFields.hasEle()) {
                            if (r.ele[j] < 10) {
                                rec.append('0');
                            }
                            rec.append(r.ele[j]);
                        }
                        rec.append(",");
                        if (r.hasAzi() && selectedFileFields.hasAzi()) {
                            // if(s.azi[j]<100) {
                            // rec.append('0');
                            if (r.azi[j] < 10) {
                                rec.append('0');
                            }
                            // }
                            rec.append(r.azi[j]);
                        }
                        rec.append(",");
                        if (r.hasSnr() && selectedFileFields.hasSnr()) {
                            if (r.snr[j] < 10) {
                                rec.append('0');
                            }
                            rec.append(r.snr[j]);
                        }
                        rec.append(",");
                    } else {
                        rec.append(",,,,");
                    }
                }
                writeNMEA(rec.toString());
                rec.setLength(0);
            }
        }
    }

    private void writeZDA(final GPSRecord r, final String timeStr) {
        if ((r.hasUtc() && selectedFileFields.hasUtc())) {
            /*
             * Write GPZDA sentence if time is available
             */
            rec.setLength(0);
            rec.append("GPZDA,");

            // DATE & TIME
            rec.append(timeStr);
            rec.append("," + (t.getDay() < 10 ? "0" : "") + t.getDay() + ","
                    + (t.getMonth() < 10 ? "0" : "") + t.getMonth() + ","
                    + t.getYear() + ",,");

            writeNMEA(rec.toString());
            rec.setLength(0);
        }
    }

    // FIX
    private void writeGSA(final GPSRecord r, final String timeStr) {
        if (r.hasValid() && selectedFileFields.hasValid() || r.hasSid()
                && selectedFileFields.hasSid() || r.hasPdop()
                && selectedFileFields.hasPdop() || r.hasHdop()
                && selectedFileFields.hasHdop() || r.hasVdop()
                && selectedFileFields.hasVdop()) {
            // GPGSA
            // GPS DOP and active satellites
            //
            // eg1. GPGSA,A,3,,,,,,16,18,,22,24,,,3.6,2.1,2.2*3C
            // eg2. GPGSA,A,3,19,28,14,18,27,22,31,39,,,,,1.7,1.0,1.3*34
            // GPGSA,A,3,15, 9,22,28,18,26, , ,,,,,4.4,1.9,4.0*17
            //
            // 1 = Mode:
            // M=Manual, forced to operate in 2D or 3D
            // A=Automatic, 3D/2D
            // 2 = Mode:
            // 1=Fix not available
            // 2=2D
            // 3=3D
            // 3-14 = PRN's of Satellite Vehicles (SV's) used in position
            // fix (null for unused fields)
            // 15 = Position Dilution of Precision (PDOP)
            // 16 = Horizontal Dilution of Precision (HDOP)
            // 17 = Vertical Dilution of Precision (VDOP)

            rec.setLength(0);
            rec.append("GPGSA,A,");
            // Mode
            if ((r.hasValid() && selectedFileFields.hasValid())) {
                if (r.getValid() == 1) {
                    rec.append("1");
                } else {
                    rec.append("3");
                }
            }
            rec.append(",");

            int sid_len;
            if ((r.sid != null)) {
                sid_len = r.sid.length;
            } else {
                sid_len = 0;
            }
            int i;
            int n;
            int j;
            for (i = (sid_len - 1), n = 12, j = 0; n > 0; n--, i--, j++) {
                if (i > 0) {
                    if (r.sidinuse[j]) {
                        rec.append(r.sid[j]);
                    }
                }
                rec.append(",");
            }

            if (r.hasPdop() && selectedFileFields.hasPdop()) {
                rec.append(JavaLibBridge.toString(r.getPdop() / 100f, 2));
            }
            rec.append(",");
            if (r.hasHdop() && selectedFileFields.hasHdop()) {
                rec.append(JavaLibBridge.toString(r.getHdop() / 100f, 2));
            }
            rec.append(",");
            if (r.hasVdop() && selectedFileFields.hasVdop()) {
                rec.append(JavaLibBridge.toString(r.getVdop() / 100f, 2));
            }
            // rec.append(",");
            writeNMEA(rec.toString());
            rec.setLength(0);
        }

    }

    private void writeWPL(final GPSRecord r) {
        //Waypoint location

        // eg1. $GPWPL,4917.16,N,12310.64,W,003*65

        if (r.hasPosition()) {
            rec.setLength(0);
            rec.append("GPWPL,");
            appendLatLon(rec, r, selectedFileFields);
            rec.append(CommonOut.getRCRstr(r));
            writeNMEA(rec.toString());
            rec.setLength(0);
        }
    }
}
