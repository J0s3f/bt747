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
package gps.log.in;

import bt747.sys.Convert;
import bt747.sys.Date;
import bt747.sys.Generic;

import gps.BT747Constants;
import gps.log.GPSRecord;

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
     *            record to update.
     * @param time
     *            time to set.
     */
    private static final void setTime(final GPSRecord gpsRec, final int time) {
        int newTime;
        newTime = gpsRec.utc;
        newTime -= gpsRec.utc % (24 * 3600);
        newTime += time;
        gpsRec.utc = newTime;
    }

    /**
     * Sets HH:MM:SS.MS in the gpsRecord's time (leaves the date information
     * intact) from a typical NMEA time string.
     * 
     * @param gpsRec
     *            record to update.
     * @param nmeaTimeStr
     *            time to set.
     */
    private static final boolean setTime(final GPSRecord gpsRec,
            final String nmeaTimeStr) {
        int timePart = Convert.toInt(nmeaTimeStr.substring(0, 2)) * 3600
                + Convert.toInt(nmeaTimeStr.substring(2, 4)) * 60
                + Convert.toInt(nmeaTimeStr.substring(4, 6));
        setTime(gpsRec, timePart);
        try {
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

    private static final void setDate(final GPSRecord gpsRec, final int date) {
        int newTime;
        newTime = gpsRec.utc;
        newTime = gpsRec.utc % (24 * 3600);
        newTime += date;
        gpsRec.utc = newTime;
    }

    private static final void setDate(final GPSRecord gpsRec, final String date) {
        int dateInt = Convert.toInt(date);
        int day = dateInt / 10000;
        int month = (dateInt / 100) % 100;
        int year = dateInt % 100 + 2000;
        setDate(gpsRec, (new Date(day, month, year)).dateToUTCepoch1970());
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
     *            elements of the sentence.
     * @param gpsRec
     *            The record that will hold the updated information.
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
        return logFormat;
    }

    /**
     * Analyze a GPRMC sentence.
     * 
     * @param sNmea
     *            elements of the sentence.
     * @param gpsRec
     *            The record that will hold the updated information.
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
                Generic.debug(sNmea[1], e);
            }
            // sNmea[2] = valid/invalid
            // latitude
            try {
                if (sNmea[3].length() != 0) {
                    setLatitude(gpsRec, sNmea[3], sNmea[4]);
                    logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);
                }
            } catch (Exception e) {
                Generic.debug(sNmea[3], e);
            }
            // longitude
            try {
                if (sNmea[5].length() != 0) {
                    setLongitude(gpsRec, sNmea[5], sNmea[6]);
                    logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
                }
            } catch (Exception e) {
                Generic.debug(sNmea[5], e);
            }
            try {
                if (sNmea[7].length() != 0) {
                    gpsRec.speed = Convert.toFloat(sNmea[7])
                            * ((float) KNOT_PER_KMH);
                    logFormat |= (1 << BT747Constants.FMT_SPEED_IDX);
                }
            } catch (Exception e) {
                Generic.debug(sNmea[7], e);
            }
            try {
                if (sNmea[8].length() != 0) {
                    gpsRec.heading = Convert.toFloat(sNmea[8]);
                    logFormat |= (1 << BT747Constants.FMT_HEADING_IDX);
                }
            } catch (Exception e) {
                Generic.debug(sNmea[8], e);
            }
            try {
                if (sNmea[9].length() != 0) {
                    setDate(gpsRec, sNmea[9]);
                    logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
                }
            } catch (Exception e) {
                Generic.debug(sNmea[9], e);
            }
        }
        return logFormat;
    }

    /**
     * Analyze a GPGGA sentence.
     * 
     * @param sNmea
     *            elements of the sentence.
     * @param gpsRec
     *            The record that will hold the updated information.
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
                }
            } catch (Exception e) {
                Generic.debug("1:" + sNmea[1], e);
            }
            try {
                if (sNmea[4].length() != 0) {
                    setLatitude(gpsRec, sNmea[2], sNmea[3]);
                    logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);
                }
            } catch (Exception e) {
                Generic.debug("2:" + sNmea[2], e);
            }
            try {
                if (sNmea[4].length() != 0) {
                    setLongitude(gpsRec, sNmea[4], sNmea[5]);
                    logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
                }
            } catch (Exception e) {
                Generic.debug("4:" + sNmea[4], e);
            }
            try {
                if (sNmea[6].length() != 0) {
                    gpsRec.valid = 1 << Convert.toInt(sNmea[6]);
                    logFormat |= (1 << BT747Constants.FMT_VALID_IDX);
                }
            } catch (Exception e) {
                Generic.debug("6:" + sNmea[6], e);
            }
            try {
                if (sNmea[7].length() != 0) {
                    gpsRec.nsat = (Convert.toInt(sNmea[7]) << 8)
                            | (gpsRec.nsat & 0xFF);
                    logFormat |= (1 << BT747Constants.FMT_NSAT_IDX);
                }
            } catch (Exception e) {
                Generic.debug("7:" + sNmea[7], e);
            }
            try {
                if (sNmea[8].length() != 0) {
                    gpsRec.hdop = (int) (Convert.toFloat(sNmea[8]) * 100);
                    logFormat |= (1 << BT747Constants.FMT_HDOP_IDX);
                } else {
                    gpsRec.hdop = 999;
                }
            } catch (Exception e) {
                Generic.debug("8:" + sNmea[8], e);
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
                    gpsRec.geoid = 0;
                }
            } catch (Exception e) {
                Generic.debug("9:" + sNmea[9], e);
            }
            if (sNmea.length >= 14) {
                try {
                    if (sNmea[13].length() != 0) {
                        gpsRec.dage = Convert.toInt(sNmea[13]);
                        logFormat |= (1 << BT747Constants.FMT_DAGE_IDX);
                    }
                } catch (Exception e) {
                    Generic.debug("13:" + sNmea[13], e);
                }
            }
            if (sNmea.length >= 15) {
                try {
                    if (sNmea[14].length() != 0) {
                        gpsRec.dsta = Convert.toInt(sNmea[14]);
                        logFormat |= (1 << BT747Constants.FMT_DSTA_IDX);
                    }
                } catch (Exception e) {
                    Generic.debug("14:" + sNmea[14], e);
                }
            }
        } // GPGGA
        return logFormat;
    }
}
