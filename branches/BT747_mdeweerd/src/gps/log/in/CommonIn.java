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
package gps.log.in;

import gps.BT747Constants;
import gps.convert.Conv;
import gps.log.GPSRecord;

import bt747.sys.Convert;
import bt747.sys.Generic;
import bt747.sys.Interface;

public final class CommonIn {
    /**
     * Conversion factor of KMH to KNOTS.
     */
    private static final double KNOT_PER_KMH = 1.852;

    public static final GPSRecord getLogFormatRecord(final int logFormat) {
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

    /**
     * Sets HH:MM:SS in the gpsRecord's time (leaves the date information
     * intact)
     * 
     * @param gpsRec
     *                record to update.
     * @param time
     *                time to set.
     */
    private static final void setTime(final GPSRecord gpsRec, final int time) {
        int newTime;
        newTime = gpsRec.utc;
        newTime -= (gpsRec.utc % (24 * 3600));
        newTime += time;
        gpsRec.utc = newTime;
    }

    private static final void setDate(final GPSRecord gpsRec, final int date) {
        int newTime;
        newTime = (gpsRec.utc % (24 * 3600));
        newTime += date;
        gpsRec.utc = newTime;
    }

    /**
     * Sets HH:MM:SS.MS in the gpsRecord's time (leaves the date information
     * intact) from a typical NMEA time string.
     * 
     * @param gpsRec
     *                record to update.
     * @param nmeaTimeStr
     *                time to set.
     * @return true if milliseconds were also set.
     */
    private static final boolean setTime(final GPSRecord gpsRec,
            final String nmeaTimeStr) {
        try {
            int timePart = Convert.toInt(nmeaTimeStr.substring(0, 2)) * 3600
                    + Convert.toInt(nmeaTimeStr.substring(2, 4)) * 60
                    + Convert.toInt(nmeaTimeStr.substring(4, 6));
            setTime(gpsRec, timePart);
            if (nmeaTimeStr.charAt(6) == '.') {
                gpsRec.milisecond = (int) (Convert.toFloat(nmeaTimeStr
                        .substring(6)) * 1000);
                return true;
            }
        } catch (Exception e) {
            // Conversion did not work, so millisecond input format no good.
            return false;
        }
        return false;
    }

    private static final void setDate(final GPSRecord gpsRec,
            final String date) {
        int dateInt = Convert.toInt(date);
        int day = dateInt / 10000;
        int month = (dateInt / 100) % 100;
        int year = dateInt % 100 + 2000;
        setDate(gpsRec, (Interface.getDateInstance(day, month, year))
                .dateToUTCepoch1970());
    }

    private static final void setLatitude(final GPSRecord gpsRec,
            final String lat, final String pol) {
        // GPSRecord gps = new GPSRecord();
        gpsRec.latitude = (Convert.toDouble(lat.substring(0, 2)) + Convert
                .toDouble(lat.substring(2)) / 60)
                * (pol.equals("N") ? 1 : -1);

    }

    private static final void setLongitude(final GPSRecord gpsRec,
            final String lon, final String pol) {
        // GPSRecord gps = new GPSRecord();
        gpsRec.longitude = (Convert.toDouble(lon.substring(0, 3)) + Convert
                .toDouble(lon.substring(3)) / 60)
                * (pol.equals("E") ? 1 : -1);
    }

    /**
     * Analyze a NMEA sentence.
     * 
     * @param sNmea
     *                elements of the sentence.
     * @param gpsRec
     *                The record that will hold the updated information.
     * @return logFormat indicating the fields that were filled.
     */
    public static final int analyzeNMEA(final String[] sNmea,
            final GPSRecord gpsRec) {
        int logFormat;
        logFormat = analyzeGPGGA(sNmea, gpsRec);
        if (logFormat != 0) {
            return logFormat;
        }
        logFormat = analyzeGPRMC(sNmea, gpsRec);
        if (logFormat != 0) {
            return logFormat;
        }
        logFormat = analyzeGPGSV(sNmea, gpsRec);
        if (logFormat != 0) {
            return logFormat;
        }
        return logFormat;
    }

    /**
     * Analyze a GPRMC sentence.
     * 
     * @param sNmea
     *                elements of the sentence.
     * @param gpsRec
     *                The record that will hold the updated information.
     * @return logFormat indicating the fields that were filled.
     */
    public static final int analyzeGPRMC(final String[] sNmea,
            final GPSRecord gpsRec) {
        int logFormat = 0;
        if (sNmea[0].equals("GPRMC") && (sNmea.length >= 10)) {
            // UTC time
            try {
                if (sNmea[1].length() != 0) {
                    if (setTime(gpsRec, sNmea[1])) {
                        logFormat |= (1 << BT747Constants.FMT_MILLISECOND_IDX);
                    }
                    logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPRMC1:" + sNmea[1], e);
            }
            // sNmea[2] = valid/invalid
            // latitude
            try {
                if (sNmea[3].length() != 0) {
                    setLatitude(gpsRec, sNmea[3], sNmea[4]);
                    logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPRMC3:" + sNmea[3], e);
            }
            // longitude
            try {
                if (sNmea[5].length() != 0) {
                    setLongitude(gpsRec, sNmea[5], sNmea[6]);
                    logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPRMC5:" + sNmea[5], e);
            }
            try {
                if (sNmea[7].length() != 0) {
                    gpsRec.speed = Convert.toFloat(sNmea[7])
                            * ((float) KNOT_PER_KMH);
                    logFormat |= (1 << BT747Constants.FMT_SPEED_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPRMC7:" + sNmea[7], e);
            }
            try {
                if (sNmea[8].length() != 0) {
                    gpsRec.heading = Convert.toFloat(sNmea[8]);
                    logFormat |= (1 << BT747Constants.FMT_HEADING_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPRMC8:" + sNmea[8], e);
            }
            try {
                if (sNmea[9].length() != 0) {
                    setDate(gpsRec, sNmea[9]);
                    logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPRMC9:" + sNmea[9], e);
            }
        }
        return logFormat;
    }

    /**
     * Analyze a GPGGA sentence.
     * 
     * @param sNmea
     *                elements of the sentence.
     * @param gpsRec
     *                The record that will hold the updated information.
     * @return logFormat indicating the fields that were filled.
     */
    public static final int analyzeGPGGA(final String[] sNmea,
            final GPSRecord gpsRec) {
        int logFormat = 0;
        if (sNmea[0].equals("GPGGA") && (sNmea.length >= 12)) {
            try {
                if (sNmea[1].length() != 0) {
                    if (setTime(gpsRec, sNmea[1])) {
                        logFormat |= (1 << BT747Constants.FMT_MILLISECOND_IDX);
                    }
                    logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPGGA1:" + sNmea[1], e);
            }
            try {
                if (sNmea[4].length() != 0) {
                    setLatitude(gpsRec, sNmea[2], sNmea[3]);
                    logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPGGA2:" + sNmea[2], e);
            }
            try {
                if (sNmea[4].length() != 0) {
                    setLongitude(gpsRec, sNmea[4], sNmea[5]);
                    logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPGGA4:" + sNmea[4], e);
            }
            try {
                if (sNmea[6].length() != 0) {
                    gpsRec.valid = 1 << Convert.toInt(sNmea[6]);
                    logFormat |= (1 << BT747Constants.FMT_VALID_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPGGA6:" + sNmea[6], e);
            }
            try {
                if (sNmea[7].length() != 0) {
                    gpsRec.nsat = (Convert.toInt(sNmea[7]) << 8)
                            | (gpsRec.nsat & 0xFF);
                    logFormat |= (1 << BT747Constants.FMT_NSAT_IDX);
                }
            } catch (Exception e) {
                Generic.debug("GPGGA7:" + sNmea[7], e);
            }
            try {
                if (sNmea[8].length() != 0) {
                    gpsRec.hdop = (int) (Convert.toFloat(sNmea[8]) * 100);
                    logFormat |= (1 << BT747Constants.FMT_HDOP_IDX);
                } else {
                    gpsRec.hdop = 999;
                }
            } catch (Exception e) {
                Generic.debug("GPGGA8:" + sNmea[8], e);
            }
            try {
                if (sNmea[9].length() != 0) {
                    gpsRec.height = (Convert.toFloat(sNmea[9]));
                    logFormat |= (1 << BT747Constants.FMT_HEIGHT_IDX);
                }
                if (sNmea[11].length() != 0) {
                    gpsRec.geoid = Convert.toFloat(sNmea[11]);
                    gpsRec.height += gpsRec.geoid;
                } else {
                    if (((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0)
                            && ((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0)) {
                        gpsRec.geoid = (float) Conv.wgs84Separation(
                                gpsRec.latitude, gpsRec.longitude);
                    } else {
                        gpsRec.geoid = 0;
                    }
                }
            } catch (Exception e) {
                Generic.debug("GPGGA9:" + sNmea[9], e);
            }
            if (sNmea.length >= 14) {
                try {
                    if (sNmea[13].length() != 0) {
                        gpsRec.dage = Convert.toInt(sNmea[13]);
                        logFormat |= (1 << BT747Constants.FMT_DAGE_IDX);
                    }
                } catch (Exception e) {
                    Generic.debug("GPGGA13:" + sNmea[13], e);
                }
            }
            if (sNmea.length >= 15) {
                try {
                    if (sNmea[14].length() != 0) {
                        gpsRec.dsta = Convert.toInt(sNmea[14]);
                        logFormat |= (1 << BT747Constants.FMT_DSTA_IDX);
                    }
                } catch (Exception e) {
                    Generic.debug("GPGGA14:" + sNmea[14], e);
                }
            }
        } // GPGGA
        return logFormat;
    }

    /**
     * Analyze a GPGSV sentence.
     * 
     * @param sNmea
     *                elements of the sentence.
     * @param gpsRec
     *                The record that will hold the updated information.
     * @return logFormat indicating the fields that were filled.
     */
    public static final int analyzeGPGSV(final String[] sNmea,
            final GPSRecord gpsRec) {

        // $GPGSV
        // GPS Satellites in view
        //
        // eg.
        // $GPGSV,3,1,11,03,03,111,00,04,15,270,00,06,01,010,00,13,06,292,00*74
        // $GPGSV,3,2,11,14,25,170,00,16,57,208,39,18,67,296,40,19,40,246,00*74
        // $GPGSV,3,3,11,22,42,067,42,24,14,311,43,27,05,244,00,,,,*4D
        //
        // $GPGSV,1,1,13,02,02,213,,03,-3,000,,11,00,121,,14,13,172,05*62
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

        // TODO: Not finished.
        int logFormat = 0;
        if (sNmea[0].equals("GPGSV") && (sNmea.length >= 19)) {
            int GSVindex = 0;
            int GSVtotal = 0;
            try {
                if (sNmea[1].length() != 0) {
                    GSVtotal = Convert.toInt(sNmea[1]);
                }
            } catch (Exception e) {
                Generic.debug("GPGSV1:" + sNmea[1], e);
            }
            try {
                if (sNmea[2].length() != 0) {
                    GSVindex = Convert.toInt(sNmea[2]);
                }
            } catch (Exception e) {
                Generic.debug("GPGSV2:" + sNmea[2], e);
            }
            try {
                if (sNmea[3].length() != 0) {
                    gpsRec.nsat = (gpsRec.nsat & 0xFF00)
                            | Convert.toInt(sNmea[3]);
                }
            } catch (Exception e) {
                Generic.debug("GPGSV3:" + sNmea[3], e);
            }
            if (gpsRec.nsat != 0) {

            }
        }
        return logFormat;
    }

    public final static void convertHeight(final GPSRecord r,
            final int factorConversionWGS84ToMSL, final int logFormat) {
        if (factorConversionWGS84ToMSL != 0
                && ((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0)
                && ((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0)
        // && valid
        ) {
            if (factorConversionWGS84ToMSL < 0) {
                r.height -= Conv.wgs84Separation(r.latitude, r.longitude);
            } else { // > 0
                r.height += Conv.wgs84Separation(r.latitude, r.longitude);
            }
        }

    }

    public final static void convertHeight(final GPSRecord r,
            final int factorConversionWGS84ToMSL) {
        if (factorConversionWGS84ToMSL != 0 && r.hasLocation()
                && r.hasHeight()) {
            if (factorConversionWGS84ToMSL < 0) {
                r.height -= Conv.wgs84Separation(r.latitude, r.longitude);
            } else { // > 0
                r.height += Conv.wgs84Separation(r.latitude, r.longitude);
            }
        }

    }

}
