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
package gps.log.out;

import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.Convert;

/**
 * Class to write a CSV file.
 * 
 * @author Mario De Weerd
 */
public final class GPSCSVFile extends GPSFile {
    /**
     * Reused StringBuffer for output construction.
     */
    private final StringBuffer rec = new StringBuffer(1024);
    /**
     * CSV field separator - fixed now, but may become a parameter in the future.
     */
    private static final char fieldSep = ',';
    /**
     * Separator in the satellite field - for future parameterization.
     */
    private static final char satSeperator = ';';


    public final boolean needPassToFindFieldsActivatedInLog() {
        return true;
    }

    protected final void writeFileHeader(final String Name) {
        rec.setLength(0);
        // INDEX,RCR,DATE,TIME,VALID,LATITUDE,N/S,LONGITUDE,E/W,HEIGHT,SPEED,
        rec.append("INDEX");
        if ((activeFileFields.rcr != 0) && (selectedFileFields.rcr != 0)) {
            rec.append(fieldSep + "RCR");
        }
        if ((activeFileFields.utc != 0) && (selectedFileFields.utc != 0)) {
            rec.append(fieldSep + "DATE" + fieldSep + "TIME");
        }
        if ((activeFileFields.valid != 0) && (selectedFileFields.valid != 0)) {
            rec.append(fieldSep + "VALID");
        }
        if ((activeFileFields.latitude != 0)
                && (selectedFileFields.latitude != 0)) {
            rec.append(fieldSep + "LATITUDE" + fieldSep + "N/S");
        }
        if ((activeFileFields.longitude != 0)
                && (selectedFileFields.longitude != 0)) {
            rec.append(fieldSep + "LONGITUDE" + fieldSep + "E/W");
        }
        if ((activeFileFields.height != 0) && (selectedFileFields.height != 0)) {
            if (!imperial) {
                rec.append(fieldSep + "HEIGHT(m)");
            } else {
                rec.append(fieldSep + "HEIGHT(ft)");
            }
        }
        if ((activeFileFields.speed != 0) && (selectedFileFields.speed != 0)) {
            if (!imperial) {
                rec.append(fieldSep + "SPEED(km/h)");
            } else {
                rec.append(fieldSep + "SPEED(mph)");
            }
        }
        if ((activeFileFields.heading != 0)
                && (selectedFileFields.heading != 0)) {
            rec.append(fieldSep + "HEADING");
        }
        if ((activeFileFields.dsta != 0) && (selectedFileFields.dsta != 0)) {
            rec.append(fieldSep + "DSTA");
        }
        if ((activeFileFields.dage != 0) && (selectedFileFields.dage != 0)) {
            rec.append(fieldSep + "DAGE");
        }
        if ((activeFileFields.pdop != 0) && (selectedFileFields.pdop != 0)) {
            rec.append(fieldSep + "PDOP");
        }
        if ((activeFileFields.hdop != 0) && (selectedFileFields.hdop != 0)) {
            rec.append(fieldSep + "HDOP");
        }
        if ((activeFileFields.vdop != 0) && (selectedFileFields.vdop != 0)) {
            rec.append(fieldSep + "VDOP");
        }
        if ((activeFileFields.nsat != 0) && (selectedFileFields.nsat != 0)) {
            rec.append(fieldSep + "NSAT (USED/VIEW)");
        }
        // SAT INFO NOT HANDLED
        // if(activeFileFields.milisecond!=0) {
        // rec.append(fieldSep+"MILISECOND");
        // }
        if ((activeFileFields.distance != 0)
                && (selectedFileFields.distance != 0)) {
            if (!imperial) {
                rec.append(fieldSep + "DISTANCE(m)");
            } else {
                rec.append(fieldSep + "DISTANCE(ft)");
            }
        }
        if (activeFileFields.sid != null && (selectedFileFields.sid != null)) {
            rec.append(fieldSep + "SAT INFO (SID");
            if ((activeFileFields.ele != null)
                    && (selectedFileFields.ele != null)) {
                rec.append("-ELE");
            }
            if ((activeFileFields.azi != null)
                    && (selectedFileFields.azi != null)) {
                rec.append("-AZI");
            }
            if ((activeFileFields.snr != null)
                    && (selectedFileFields.snr != null)) {
                rec.append("-SNR");
            }
            rec.append(")");
        }
        if(activeFileFields.voxStr!=null) {
            rec.append(fieldSep + "VOX");
        }
        if (addLogConditionInfo) {
            rec.append(fieldSep + "LOGTIME(s)");
            rec.append(fieldSep + "LOGDIST(m)");
            rec.append(fieldSep + "LOGSPD(km/h)");
        }
        rec.append(fieldSep + "\r\n");
        writeTxt(rec.toString());
        // "NSAT (USED/VIEW),SAT INFO (SID-ELE-AZI-SNR)
    }

    /**
     * Returns true when the record is used by the format.
     * 
     * Override parent class because only the trackpoint filter is used.
     */
    protected final boolean recordIsNeeded(final GPSRecord s) {
        return ptFilters[GPSFilter.TRKPT].doFilter(s)
                || ptFilters[GPSFilter.WAYPT].doFilter(s);
    }

    // private GPSRecord prevRecord=null;
    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);

        if (activeFields != null && recordIsNeeded(r)) {
            rec.setLength(0);
            rec.append(Convert.toString(r.recCount));
            if ((activeFileFields.rcr != 0) && (selectedFileFields.rcr != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.rcr != 0) && (selectedFileFields.rcr != 0)) {
                rec.append(CommonOut.getRCRstr(r));
            }
            if ((activeFields.utc != 0) && (selectedFileFields.utc != 0)) {
                rec.append(fieldSep + Convert.toString(t.getYear()) + "/"
                        + (t.getMonth() < 10 ? "0" : "")
                        + Convert.toString(t.getMonth()) + "/"
                        + (t.getDay() < 10 ? "0" : "")
                        + Convert.toString(t.getDay()) + fieldSep
                        + (t.getHour() < 10 ? "0" : "")
                        + Convert.toString(t.getHour()) + ":"
                        + (t.getMinute() < 10 ? "0" : "")
                        + Convert.toString(t.getMinute()) + ":"
                        + (t.getSecond() < 10 ? "0" : ""));
                if (activeFields.milisecond == 0) {
                    rec.append(Convert.toString(t.getSecond()));
                } else {
                    rec.append(Convert.toString((float) t.getSecond()
                            + r.milisecond / 1000.0, 3));
                }
            } else if ((activeFileFields.utc != 0)
                    && (selectedFileFields.utc != 0)) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }

            if ((activeFields.valid != 0) && (selectedFileFields.valid != 0)) {
                rec.append(fieldSep);
                rec.append(CommonOut.getFixText(r.valid));
            } else if ((activeFileFields.valid != 0)
                    && (selectedFileFields.valid != 0)) {
                rec.append(fieldSep);
            }

            if ((activeFields.latitude != 0)
                    && (selectedFileFields.latitude != 0)) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.latitude, 6));
                if (r.latitude >= 0) {
                    rec.append(fieldSep + "N");
                } else {
                    rec.append(fieldSep + "S");
                }
            } else if ((activeFileFields.latitude != 0)
                    && (selectedFileFields.latitude != 0)) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }
            if ((activeFields.longitude != 0)
                    && (selectedFileFields.longitude != 0)) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.longitude, 6));
                if (r.longitude >= 0) {
                    rec.append(fieldSep + "E");
                } else {
                    rec.append(fieldSep + "W");
                }
            } else if ((activeFileFields.longitude != 0)
                    && (selectedFileFields.longitude != 0)) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }
            if ((activeFields.height != 0) && (selectedFileFields.height != 0)) {
                rec.append(fieldSep);
                if (!imperial) {
                    rec.append(Convert.toString(r.height, 3));
                } else {
                    rec
                            .append(Convert.toString(
                                    r.height * 3.28083989501312, 3));
                }

                // Add field concerning geoid separation.
                // if(activeFields.latitude!=0 && activeFields.longitude!=0) {
                // rec.append(fieldSep);
                // rec.append(Convert.toString(gps.convert.Conv.wgs84_separation(s.latitude,s.longitude),3));
                // }

            } else if ((activeFileFields.height != 0)
                    && (selectedFileFields.height != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.speed != 0) && (selectedFileFields.speed != 0)) {
                rec.append(fieldSep);
                if (!imperial) {
                    rec.append(Convert.toString(r.speed, 3));
                } else {
                    rec
                            .append(Convert.toString(
                                    r.speed * 0.621371192237334, 3));
                }
            } else if ((activeFileFields.speed != 0)
                    && (selectedFileFields.speed != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.heading != 0)
                    && (selectedFileFields.heading != 0)) {
                rec.append(fieldSep);
                rec.append(Convert.toString((double) r.heading, 6));
            } else if ((activeFileFields.heading != 0)
                    && (selectedFileFields.heading != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.dsta != 0) && (selectedFileFields.dsta != 0)) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.dsta));
            } else if ((activeFileFields.dsta != 0)
                    && (selectedFileFields.dsta != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.dage != 0) && (selectedFileFields.dage != 0)) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.dage));
            } else if ((activeFileFields.dage != 0)
                    && (selectedFileFields.dage != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.pdop != 0) && (selectedFileFields.pdop != 0)) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.pdop / 100.0, 2));
            } else if ((activeFileFields.pdop != 0)
                    && (selectedFileFields.pdop != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.hdop != 0) && (selectedFileFields.hdop != 0)) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.hdop / 100.0, 2));
            } else if ((activeFileFields.hdop != 0)
                    && (selectedFileFields.hdop != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.vdop != 0) && (selectedFileFields.vdop != 0)) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.vdop / 100.0, 2));
            } else if ((activeFileFields.vdop != 0)
                    && (selectedFileFields.vdop != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.nsat != 0) && (selectedFileFields.nsat != 0)) {
                rec.append(fieldSep);
                rec.append(Convert.toString((r.nsat & 0xFF00) >> 8));
                rec.append("(" + Convert.toString(r.nsat & 0xFF) + ")");
            } else if ((activeFileFields.nsat != 0)
                    && (selectedFileFields.nsat != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFields.distance != 0)
                    && (selectedFileFields.distance != 0)) {
                rec.append(fieldSep);
                if (!imperial) {
                    rec.append(Convert.toString(r.distance, 2));
                } else {
                    rec.append(Convert.toString(r.distance * 3.28083989501312,
                            2));
                }
            } else if ((activeFileFields.distance != 0)
                    && (selectedFileFields.distance != 0)) {
                rec.append(fieldSep);
            }
            if ((activeFileFields.sid != null)
                    && (selectedFileFields.sid != null)) {
                int j = 0;
                rec.append(fieldSep);
                if (activeFields.sid != null) {
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
                        if (activeFileFields.ele != null
                                && (selectedFileFields.ele != null)) {
                            rec.append('-');
                            if (activeFields.ele != null) {
                                if (r.ele[j] < 10) {
                                    rec.append('0');
                                }
                                rec.append(r.ele[j]);
                            }
                        }
                        if (activeFileFields.azi != null
                                && (selectedFileFields.azi != null)) {
                            rec.append('-');
                            if (activeFields.azi != null) {
                                // if(s.azi[j]<100) {
                                // rec.append('0');
                                if (r.azi[j] < 10) {
                                    rec.append('0');
                                }
                                // }
                                rec.append(r.azi[j]);
                            }
                        }
                        if (activeFileFields.snr != null
                                && (selectedFileFields.snr != null)) {
                            rec.append('-');
                            if (activeFields.snr != null) {
                                if (r.snr[j] < 10) {
                                    rec.append('0');
                                }
                                rec.append(r.snr[j]);
                            }
                        }
                        j++;
                    }
                }
            }
            // if(activeFields.utc!=0 &&
            // activeFields.longitude!=0 && activeFields.latitude!=0) {
            //                
            // // Next lines calculate speed, distance (trial)
            // if(prevRecord!=null && prevRecord.utc>1000) {
            // double distance;
            // distance=Conv.earth_distance(s.latitude,s.longitude,prevRecord.latitude,prevRecord.longitude);
            // double speed;
            // speed=s.utc-prevRecord.utc+(s.milisecond-prevRecord.milisecond)*0.001;
            // // Currently time
            // if(speed>0.1) {
            // // Not 0 and some difference
            // speed=3.6*distance/speed;
            // } else {
            // speed=0.000;
            // }
            // rec.append(","+Convert.toString(speed,3)+","+distance);
            // }
            // prevRecord=new GPSRecord(s);
            // }

            if (activeFileFields.voxStr != null) {
                rec.append(fieldSep);
                if (r.voxStr != null) {
                    rec.append(r.voxStr);
                }
            }

            if (addLogConditionInfo) {
                rec.append(fieldSep);
                if (r.logPeriod % 10 == 0) {
                    rec.append(Convert.toString(r.logPeriod / 10));
                } else {
                    rec.append(Convert.toString(r.logPeriod / 10.0, 1));
                }
                rec.append(fieldSep);
                if (r.logDistance % 10 == 0) {
                    rec.append(Convert.toString(r.logDistance / 10));
                } else {
                    rec.append(Convert.toString(r.logDistance / 10.0, 1));
                }
                rec.append(fieldSep);
                if (r.logSpeed % 10 == 0) {
                    rec.append(Convert.toString(r.logSpeed / 10));
                } else {
                    rec.append(Convert.toString(r.logSpeed / 10.0, 1));
                }
            }

            rec.append(fieldSep);

            rec.append("\r\n");
            writeTxt(rec.toString());
        } // activeFields!=null
    }

}
