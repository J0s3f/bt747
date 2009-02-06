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
     * CSV field separator - fixed now, but may become a parameter in the
     * future.
     */
    private String fieldSep = ",";
    /**
     * Separator in the satellite field - for future parameterization.
     */
    private String satSeperator = ";";

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final String basename, final String ext,
            final int ard, final int oneFilePerDay) {
        super.initialiseFile(basename, ext, ard, oneFilePerDay);

        if (getParamObject().hasParam(
                GPSConversionParameters.CSV_DATE_FORMAT_INT)) {
            getParamObject().getIntParam(
                    GPSConversionParameters.CSV_DATE_FORMAT_INT);
        }
        final String fs = getParamObject().getStringParam(
                GPSConversionParameters.CSV_FIELD_SEP_STRING);
        if (fs != null) {
            fieldSep = fs;
        }

        final String ss = getParamObject().getStringParam(
                GPSConversionParameters.CSV_SAT_SEP_STRING);
        if (ss != null) {
            satSeperator = ss;
        }

    }

    public final boolean needPassToFindFieldsActivatedInLog() {
        return true;
    }

    protected final void writeFileHeader(final String Name) {
        rec.setLength(0);
        // INDEX,RCR,DATE,TIME,VALID,LATITUDE,N/S,LONGITUDE,E/W,HEIGHT,SPEED,
        rec.append("INDEX");
        if ((activeFileFields.hasRcr()) && (selectedFileFields.hasRcr())) {
            rec.append(fieldSep + "RCR");
        }
        if ((activeFileFields.hasUtc()) && (selectedFileFields.hasUtc())) {
            rec.append(fieldSep + "DATE" + fieldSep + "TIME");
        }
        if ((activeFileFields.hasValid()) && (selectedFileFields.hasValid())) {
            rec.append(fieldSep + "VALID");
        }
        if ((activeFileFields.hasLatitude())
                && (selectedFileFields.hasLatitude())) {
            rec.append(fieldSep + "LATITUDE" + fieldSep + "N/S");
        }
        if ((activeFileFields.hasLongitude())
                && (selectedFileFields.hasLongitude())) {
            rec.append(fieldSep + "LONGITUDE" + fieldSep + "E/W");
        }
        if ((activeFileFields.hasHeight())
                && (selectedFileFields.hasHeight())) {
            if (!imperial) {
                rec.append(fieldSep + "HEIGHT(m)");
            } else {
                rec.append(fieldSep + "HEIGHT(ft)");
            }
        }
        if ((activeFileFields.hasSpeed()) && (selectedFileFields.hasSpeed())) {
            if (!imperial) {
                rec.append(fieldSep + "SPEED(km/h)");
            } else {
                rec.append(fieldSep + "SPEED(mph)");
            }
        }
        if ((activeFileFields.hasHeading())
                && (selectedFileFields.hasHeading())) {
            rec.append(fieldSep + "HEADING");
        }
        if ((activeFileFields.hasDsta()) && (selectedFileFields.hasDsta())) {
            rec.append(fieldSep + "DSTA");
        }
        if ((activeFileFields.hasDage()) && (selectedFileFields.hasDage())) {
            rec.append(fieldSep + "DAGE");
        }
        if ((activeFileFields.hasPdop()) && (selectedFileFields.hasPdop())) {
            rec.append(fieldSep + "PDOP");
        }
        if ((activeFileFields.hasHdop()) && (selectedFileFields.hasHdop())) {
            rec.append(fieldSep + "HDOP");
        }
        if ((activeFileFields.hasVdop()) && (selectedFileFields.hasVdop())) {
            rec.append(fieldSep + "VDOP");
        }
        if ((activeFileFields.hasNsat()) && (selectedFileFields.hasNsat())) {
            rec.append(fieldSep + "NSAT (USED/VIEW)");
        }
        // SAT INFO NOT HANDLED
        if ((activeFileFields.hasDistance())
                && (selectedFileFields.hasDistance())) {
            if (!imperial) {
                rec.append(fieldSep + "DISTANCE(m)");
            } else {
                rec.append(fieldSep + "DISTANCE(ft)");
            }
        }
        if (activeFileFields.hasSid() && (selectedFileFields.hasSid())) {
            rec.append(fieldSep + "SAT INFO (SID");
            if ((activeFileFields.hasEle()) && (selectedFileFields.hasEle())) {
                rec.append("-ELE");
            }
            if ((activeFileFields.hasAzi()) && (selectedFileFields.hasAzi())) {
                rec.append("-AZI");
            }
            if ((activeFileFields.hasSnr()) && (selectedFileFields.hasSnr())) {
                rec.append("-SNR");
            }
            rec.append(")");
        }
        if (activeFileFields.hasVoxStr()) {
            rec.append(fieldSep + "VOX");
        }
        if (addLogConditionInfo) {
            rec.append(fieldSep + "LOGTIME(s)");
            rec.append(fieldSep + "LOGDIST(m)");
            rec.append(fieldSep + "LOGSPD(km/h)");
        }
        rec.append(fieldSep + "\r\n");
        writeTxt(rec.toString());
        rec.setLength(0);
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
    /* (non-Javadoc)
     * @see gps.log.out.GPSFile#writeRecord(gps.log.GPSRecord)
     */
    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);

        if ((activeFields != null) && cachedRecordIsNeeded(r)) {
            rec.setLength(0);
            rec.append(r.recCount);
            if ((activeFileFields.hasRcr()) && (selectedFileFields.hasRcr())) {
                rec.append(fieldSep);
            }
            /* RCR value */
            if ((activeFields.hasRcr()) && (selectedFileFields.hasRcr())) {
                rec.append(CommonOut.getRCRstr(r));
            }
            /* DATE , TIME */
            if ((activeFields.hasUtc()) && (selectedFileFields.hasUtc())) {
                rec.append(fieldSep + CommonOut.getDateNumStr(t) + fieldSep
                        + CommonOut.getTimeStr(t));
                if (activeFields.hasMillisecond()) {
                    rec.append('.');
                    if (r.milisecond < 100) {
                        rec.append('0');
                    }
                    if (r.milisecond < 10) {
                        rec.append('0');
                    }
                    rec.append(r.milisecond);
                }
            } else if ((activeFileFields.hasUtc())
                    && (selectedFileFields.hasUtc())) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }

            if ((activeFields.hasValid()) && (selectedFileFields.hasValid())) {
                rec.append(fieldSep);
                rec.append(CommonOut.getFixText(r.valid));
            } else if ((activeFileFields.hasValid())
                    && (selectedFileFields.hasValid())) {
                rec.append(fieldSep);
            }

            if ((activeFields.hasLatitude())
                    && (selectedFileFields.hasLatitude())) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.latitude, 6));
                if (r.latitude >= 0) {
                    rec.append(fieldSep + "N");
                } else {
                    rec.append(fieldSep + "S");
                }
            } else if ((activeFileFields.hasLatitude())
                    && (selectedFileFields.hasLatitude())) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }
            if ((activeFields.hasLongitude())
                    && (selectedFileFields.hasLongitude())) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.longitude, 6));
                if (r.longitude >= 0) {
                    rec.append(fieldSep + "E");
                } else {
                    rec.append(fieldSep + "W");
                }
            } else if ((activeFileFields.hasLongitude())
                    && (selectedFileFields.hasLongitude())) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }
            if ((activeFields.hasHeight())
                    && (selectedFileFields.hasHeight())) {
                rec.append(fieldSep);
                if (!imperial) {
                    rec.append(Convert.toString(r.height, 3));
                } else {
                    rec.append(Convert.toString(r.height * 3.28083989501312,
                            3));
                }

                // Add field concerning geoid separation.
                // if(activeFields.latitude!=0 && activeFields.longitude!=0) {
                // rec.append(fieldSep);
                // rec.append(Convert.toString(gps.convert.Conv.wgs84_separation(s.latitude,s.longitude),3));
                // }

            } else if ((activeFileFields.hasHeight())
                    && (selectedFileFields.hasHeight())) {
                rec.append(fieldSep);
            }
            if ((activeFields.hasSpeed()) && (selectedFileFields.hasSpeed())) {
                rec.append(fieldSep);
                if (!imperial) {
                    rec.append(Convert.toString(r.speed, 3));
                } else {
                    rec.append(Convert.toString(r.speed * 0.621371192237334,
                            3));
                }
            } else if ((activeFileFields.hasSpeed())
                    && (selectedFileFields.hasSpeed())) {
                rec.append(fieldSep);
            }
            if ((activeFields.hasHeading())
                    && (selectedFileFields.hasHeading())) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.heading, 6));
            } else if ((activeFileFields.hasHeading())
                    && (selectedFileFields.hasHeading())) {
                rec.append(fieldSep);
            }
            if ((activeFields.hasDsta()) && (selectedFileFields.hasDsta())) {
                rec.append(fieldSep);
                rec.append(r.dsta);
            } else if ((activeFileFields.hasDsta())
                    && (selectedFileFields.hasDsta())) {
                rec.append(fieldSep);
            }
            if ((activeFields.hasDage()) && (selectedFileFields.hasDage())) {
                rec.append(fieldSep);
                rec.append(r.dage);
            } else if ((activeFileFields.hasDage())
                    && (selectedFileFields.hasDage())) {
                rec.append(fieldSep);
            }
            if ((activeFields.hasPdop()) && (selectedFileFields.hasPdop())) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.pdop / 100.0, 2));
            } else if ((activeFileFields.hasPdop())
                    && (selectedFileFields.hasPdop())) {
                rec.append(fieldSep);
            }
            if ((activeFields.hasHdop()) && (selectedFileFields.hasHdop())) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.hdop / 100.0, 2));
            } else if ((activeFileFields.hasHdop())
                    && (selectedFileFields.hasHdop())) {
                rec.append(fieldSep);
            }
            if ((activeFields.hasVdop()) && (selectedFileFields.hasVdop())) {
                rec.append(fieldSep);
                rec.append(Convert.toString(r.vdop / 100.0, 2));
            } else if ((activeFileFields.hasVdop())
                    && (selectedFileFields.hasVdop())) {
                rec.append(fieldSep);
            }
            if ((activeFields.hasNsat()) && (selectedFileFields.hasNsat())) {
                rec.append(fieldSep);
                rec.append((r.nsat & 0xFF00) >> 8);
                rec.append("(" + (r.nsat & 0xFF) + ")");
            } else if ((activeFileFields.hasNsat())
                    && (selectedFileFields.hasNsat())) {
                rec.append(fieldSep);
            }
            if ((activeFields.hasDistance())
                    && (selectedFileFields.hasDistance())) {
                rec.append(fieldSep);
                if (!imperial) {
                    rec.append(Convert.toString(r.distance, 2));
                } else {
                    rec.append(Convert.toString(
                            r.distance * 3.28083989501312, 2));
                }
            } else if ((activeFileFields.hasDistance())
                    && (selectedFileFields.hasDistance())) {
                rec.append(fieldSep);
            }
            if ((activeFileFields.hasSid()) && (selectedFileFields.hasSid())) {
                int j = 0;
                rec.append(fieldSep);
                if (activeFields.hasSid()) {
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
                        if (activeFileFields.hasEle()
                                && (selectedFileFields.hasEle())) {
                            rec.append('-');
                            if (activeFields.hasEle()) {
                                if (r.ele[j] < 10) {
                                    rec.append('0');
                                }
                                rec.append(r.ele[j]);
                            }
                        }
                        if (activeFileFields.hasAzi()
                                && (selectedFileFields.hasAzi())) {
                            rec.append('-');
                            if (activeFields.hasAzi()) {
                                // if(s.azi[j]<100) {
                                // rec.append('0');
                                if (r.azi[j] < 10) {
                                    rec.append('0');
                                }
                                // }
                                rec.append(r.azi[j]);
                            }
                        }
                        if (activeFileFields.hasSnr()
                                && (selectedFileFields.hasSnr())) {
                            rec.append('-');
                            if (activeFields.hasSnr()) {
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

            if (activeFileFields.hasVoxStr()) {
                rec.append(fieldSep);
                if (r.voxStr != null) {
                    rec.append(r.voxStr);
                }
            }

            if (addLogConditionInfo) {
                rec.append(fieldSep);
                if (r.logPeriod % 10 == 0) {
                    rec.append(r.logPeriod / 10);
                } else {
                    rec.append(Convert.toString(r.logPeriod / 10.0, 1));
                }
                rec.append(fieldSep);
                if (r.logDistance % 10 == 0) {
                    rec.append(r.logDistance / 10);
                } else {
                    rec.append(Convert.toString(r.logDistance / 10.0, 1));
                }
                rec.append(fieldSep);
                if (r.logSpeed % 10 == 0) {
                    rec.append(r.logSpeed / 10);
                } else {
                    rec.append(Convert.toString(r.logSpeed / 10.0, 1));
                }
            }

            rec.append(fieldSep);

            rec.append("\r\n");
            writeTxt(rec.toString());
            rec.setLength(0);
        } // activeFields!=null
    }

}
