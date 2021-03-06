// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
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

import gps.convert.ExternalUtils;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;

/**
 * Class to write a CSV file.
 * 
 * @author Mario De Weerd
 */
public final class GPSCSVFile extends GPSFile {
    /**
     * 
     */
    private static final float METERS_TO_FEET = 3.28083989501312f;
    /**
     * Reused StringBuffer for output construction.
     */
    private final StringBuffer rec = new StringBuffer(1024);
    
    private static final char defaultDecimalPoint = '.';
    
    private char decimalPoint = defaultDecimalPoint;
    /**
     * CSV field separator - fixed now, but may become a parameter in the
     * future.
     */
    private char fieldSep = ',';
    /**
     * Separator in the satellite field - for future parameterization.
     */
    private char satSeperator = ';';

    private int posDigits;
    private int heightDigits;
    
    /**
     * Data added at the start of each line.
     */
    private String dataPrefix;
    /**
     * Snippet added at the start of the header.
     */
    private String headerPrefix;

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final BT747Path basename, final String ext,
            final int oneFilePerDay) {
        super.initialiseFile(basename, ext, oneFilePerDay);

        if (getParamObject().hasParam(
                GPSConversionParameters.CSV_DATE_FORMAT_INT)) {
            getParamObject().getIntParam(
                    GPSConversionParameters.CSV_DATE_FORMAT_INT);
        }

        final String ds = getParamObject().getStringParam(
                GPSConversionParameters.CSV_DECIMALPOINT_STRING);
        if (ds != null) {
            decimalPoint = ds.charAt(0);
        }

        final String fs = getParamObject().getStringParam(
                GPSConversionParameters.CSV_FIELD_SEP_STRING);
        if (fs != null) {
            fieldSep = fs.charAt(0);
        }

        final String ss = getParamObject().getStringParam(
                GPSConversionParameters.CSV_SAT_SEP_STRING);
        if (ss != null) {
            satSeperator = ss.charAt(0);
        }
        
        if (getParamObject().hasParam(GPSConversionParameters.POSITION_DIGITS)) {
        	posDigits = getParamObject().getIntParam(GPSConversionParameters.POSITION_DIGITS);
        } else {
            posDigits = 6;
        }
        
        if (getParamObject().hasParam(GPSConversionParameters.HEIGHT_DIGITS)) {
        	heightDigits = getParamObject().getIntParam(GPSConversionParameters.HEIGHT_DIGITS);
        } else {
            heightDigits = 3;
        }
        
        if(getParamObject().hasParam(GPSConversionParameters.CSV_HEADER_PREFIX)) {
        	headerPrefix = getParamObject().getStringParam(GPSConversionParameters.CSV_HEADER_PREFIX);
        } else {
        	headerPrefix = "";
        }
        if(getParamObject().hasParam(GPSConversionParameters.CSV_DATA_PREFIX)) {
        	dataPrefix = getParamObject().getStringParam(GPSConversionParameters.CSV_DATA_PREFIX);
        } else {
        	dataPrefix = "";
        }

    }
    
    public final boolean needPassToFindFieldsActivatedInLog() {
        return true;
    }

    protected final void writeFileHeader(final String Name) {
        rec.setLength(0);
        rec.append(this.headerPrefix);
        // INDEX,RCR,DATE,TIME,VALID,LATITUDE,N/S,LONGITUDE,E/W,HEIGHT,SPEED,
        rec.append("INDEX");
        if ((selectedFileFields.hasRcr())) {
            rec.append(fieldSep + "RCR");
        }
        if ((selectedFileFields.hasUtc())) {
            rec.append(fieldSep + "DATE" + fieldSep + "TIME");
        }
        if ((selectedFileFields.hasValid())) {
            rec.append(fieldSep + "VALID");
        }
        if ((selectedFileFields.hasLatitude())) {
            rec.append(fieldSep + "LATITUDE" + fieldSep + "N/S");
        }
        if ((selectedFileFields.hasLongitude())) {
            rec.append(fieldSep + "LONGITUDE" + fieldSep + "E/W");
        }
        if ((selectedFileFields.hasHeight())) {
            if (!imperial) {
                rec.append(fieldSep + "HEIGHT(m)");
            } else {
                rec.append(fieldSep + "HEIGHT(ft)");
            }
        }
        if ((selectedFileFields.hasGeoid())) {
            if (!imperial) {
                rec.append(fieldSep + "GEOSEP(m)");
            } else {
                rec.append(fieldSep + "GEOSEP(ft)");
            }
        }
        if ((selectedFileFields.hasSpeed())) {
            if (!imperial) {
                rec.append(fieldSep + "SPEED(km/h)");
            } else {
                rec.append(fieldSep + "SPEED(mph)");
            }
        }
        if ((selectedFileFields.hasHeading())) {
            rec.append(fieldSep + "HEADING");
        }
        if ((selectedFileFields.hasDsta())) {
            rec.append(fieldSep + "DSTA");
        }
        if ((selectedFileFields.hasDage())) {
            rec.append(fieldSep + "DAGE");
        }
        if ((selectedFileFields.hasPdop())) {
            rec.append(fieldSep + "PDOP");
        }
        if ((selectedFileFields.hasHdop())) {
            rec.append(fieldSep + "HDOP");
        }
        if ((selectedFileFields.hasVdop())) {
            rec.append(fieldSep + "VDOP");
        }
        if ((selectedFileFields.hasNsat())) {
            rec.append(fieldSep + "NSAT (USED/VIEW)");
        }
        // SAT INFO NOT HANDLED
        if ((selectedFileFields.hasDistance())) {
            if (!imperial) {
                rec.append(fieldSep + "DISTANCE(m)");
            } else {
                rec.append(fieldSep + "DISTANCE(ft)");
            }
        }
        if ((selectedFileFields.hasSid())) {
            rec.append(fieldSep + "SAT INFO (SID");
            if ((selectedFileFields.hasEle())) {
                rec.append("-ELE");
            }
            if ((selectedFileFields.hasAzi())) {
                rec.append("-AZI");
            }
            if ((selectedFileFields.hasSnr())) {
                rec.append("-SNR");
            }
            rec.append(")");
        }
        if (selectedFileFields.hasVoxStr()) {
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

    private final void appendNumber(final StringBuffer r, final String a) {
        if (decimalPoint == defaultDecimalPoint) {
            r.append(a);
        } else {
            final String newDecimalPointString = a.replace(
                    defaultDecimalPoint, decimalPoint);
            r.append(newDecimalPointString);
        }
    }
    
    // private GPSRecord prevRecord=null;
    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#WriteRecord()
     */
    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#writeRecord(gps.log.GPSRecord)
     */
    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);

        if (cachedRecordIsNeeded(r)) {
            rec.setLength(0);
            rec.append(this.dataPrefix);
            if (r.hasRecCount()) {
                rec.append(r.getRecCount());
            }
            if ((selectedFileFields.hasRcr())) {
                rec.append(fieldSep);
                if (r.hasRcr()) {
                    rec.append(CommonOut.getRCRstr(r));
                }
            }
            /* DATE , TIME */
            if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
                rec.append(fieldSep + CommonOut.getDateNumStr(t) + fieldSep
                        + CommonOut.getTimeStr(t));
                if (r.hasMillisecond()) {
                    rec.append(decimalPoint);
                    if (r.milisecond < 100) {
                        rec.append('0');
                    }
                    if (r.milisecond < 10) {
                        rec.append('0');
                    }
                    rec.append(r.milisecond);
                }
            } else if ((selectedFileFields.hasUtc())) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }

            if ((r.hasValid()) && (selectedFileFields.hasValid())) {
                rec.append(fieldSep);
                rec.append(CommonOut.getFixText(r.getValid()));
            } else if ((selectedFileFields.hasValid())) {
                rec.append(fieldSep);
            }

            if ((r.hasLatitude()) && (selectedFileFields.hasLatitude())) {
                rec.append(fieldSep);
                appendNumber(rec, JavaLibBridge.toString(r.getLatitude(), posDigits));
                if (r.getLatitude() >= 0) {
                    rec.append(fieldSep + "N");
                } else {
                    rec.append(fieldSep + "S");
                }
            } else if ((selectedFileFields.hasLatitude())) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }
            if ((r.hasLongitude()) && (selectedFileFields.hasLongitude())) {
                rec.append(fieldSep);
                appendNumber(rec, JavaLibBridge.toString(r.getLongitude(), posDigits));
                if (r.getLongitude() >= 0) {
                    rec.append(fieldSep + "E");
                } else {
                    rec.append(fieldSep + "W");
                }
            } else if ((selectedFileFields.hasLongitude())) {
                rec.append(fieldSep);
                rec.append(fieldSep);
            }
            if ((r.hasHeight()) && (selectedFileFields.hasHeight())) {
                rec.append(fieldSep);
                if (!imperial) {
                    appendNumber(rec, JavaLibBridge.toString(r.getHeight(), heightDigits));
                } else {
                    appendNumber(rec, JavaLibBridge.toString(r.getHeight()
                            * METERS_TO_FEET, heightDigits));
                }

                // Add field concerning geoid separation.
                // if(r.latitude!=0 && r.longitude!=0) {
                // rec.append(fieldSep);
                // rec.append(JavaLibBridge.toString(gps.convert.Conv.wgs84_separation(s.latitude,s.longitude),3));
                // }

            } else if ((selectedFileFields.hasHeight())) {
                rec.append(fieldSep);
            }
            if (selectedFileFields.hasGeoid()) {
                if (r.hasPosition()) {
                    float separation;
                    if (r.hasGeoid()) {
                        separation = r.getGeoid();
                    } else {
                        separation = ((long) (10 * ExternalUtils.wgs84Separation(r
                                .getLatitude(), r.getLongitude()))) / 10.f;
                    }
                    if (imperial) {
                        separation *= METERS_TO_FEET;
                    }
                    appendNumber(rec, JavaLibBridge.toString(separation, heightDigits));
                    rec.append(fieldSep);
                } else {
                    rec.append(fieldSep);
                }
            }
            
            if ((r.hasSpeed()) && (selectedFileFields.hasSpeed())) {
                rec.append(fieldSep);
                if (!imperial) {
                    appendNumber(rec, JavaLibBridge.toString(r.getSpeed(), 3));
                } else {
                    appendNumber(rec, JavaLibBridge.toString(
                            r.getSpeed() * 0.621371192237334f, 3));
                }
            } else if ((selectedFileFields.hasSpeed())) {
                rec.append(fieldSep);
            }
            if ((r.hasHeading()) && (selectedFileFields.hasHeading())) {
                rec.append(fieldSep);
                appendNumber(rec, JavaLibBridge.toString(r.getHeading(), 6));
            } else if ((selectedFileFields.hasHeading())) {
                rec.append(fieldSep);
            }
            if ((r.hasDsta()) && (selectedFileFields.hasDsta())) {
                rec.append(fieldSep);
                rec.append(r.getDsta());
            } else if ((selectedFileFields.hasDsta())) {
                rec.append(fieldSep);
            }
            if ((r.hasDage()) && (selectedFileFields.hasDage())) {
                rec.append(fieldSep);
                rec.append(r.getDage());
            } else if ((selectedFileFields.hasDage())) {
                rec.append(fieldSep);
            }
            if ((r.hasPdop()) && (selectedFileFields.hasPdop())) {
                rec.append(fieldSep);
                appendNumber(rec, JavaLibBridge.toString(r.getPdop() / 100.0f, 2));
            } else if ((selectedFileFields.hasPdop())) {
                rec.append(fieldSep);
            }
            if ((r.hasHdop()) && (selectedFileFields.hasHdop())) {
                rec.append(fieldSep);
                appendNumber(rec, JavaLibBridge.toString(r.getHdop() / 100.0f, 2));
            } else if ((selectedFileFields.hasHdop())) {
                rec.append(fieldSep);
            }
            if ((r.hasVdop()) && (selectedFileFields.hasVdop())) {
                rec.append(fieldSep);
                appendNumber(rec, JavaLibBridge.toString(r.getVdop() / 100.0f, 2));
            } else if ((selectedFileFields.hasVdop())) {
                rec.append(fieldSep);
            }
            if ((r.hasNsat()) && (selectedFileFields.hasNsat())) {
                rec.append(fieldSep);
                rec.append((r.getNsat() & 0xFF00) >> 8);
                rec.append("(" + (r.getNsat() & 0xFF) + ")");
            } else if ((selectedFileFields.hasNsat())) {
                rec.append(fieldSep);
            }
            if ((r.hasDistance()) && (selectedFileFields.hasDistance())) {
                rec.append(fieldSep);
                if (!imperial) {
                    appendNumber(rec, JavaLibBridge.toString(r.distance, 2));
                } else {
                    appendNumber(rec, JavaLibBridge.toString(
                            r.distance * 3.28083989501312, 2));
                }
            } else if ((selectedFileFields.hasDistance())) {
                rec.append(fieldSep);
            }
            if ((selectedFileFields.hasSid())) {
                int j = 0;
                rec.append(fieldSep);
                if (r.hasSid()) {
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
                        if ((selectedFileFields.hasEle())) {
                            rec.append('-');
                            if (r.hasEle()) {
                                if (r.ele[j] < 10) {
                                    rec.append('0');
                                }
                                rec.append(r.ele[j]);
                            }
                        }
                        if ((selectedFileFields.hasAzi())) {
                            rec.append('-');
                            if (r.hasAzi()) {
                                // if(s.azi[j]<100) {
                                // rec.append('0');
                                if (r.azi[j] < 10) {
                                    rec.append('0');
                                }
                                // }
                                rec.append(r.azi[j]);
                            }
                        }
                        if ((selectedFileFields.hasSnr())) {
                            rec.append('-');
                            if (r.hasSnr()) {
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
            // if(r.utc!=0 &&
            // r.longitude!=0 && r.latitude!=0) {
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
            // rec.append(","+JavaLibBridge.toString(speed,3)+","+distance);
            // }
            // prevRecord=new GPSRecord(s);
            // }

            if (selectedFileFields.hasVoxStr()) {
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
                    appendNumber(rec, JavaLibBridge.toString(r.logPeriod / 10.0, 1));
                }
                rec.append(fieldSep);
                if (r.logDistance % 10 == 0) {
                    rec.append(r.logDistance / 10);
                } else {
                    appendNumber(rec, JavaLibBridge
                            .toString(r.logDistance / 10.0, 1));
                }
                rec.append(fieldSep);
                if (r.logSpeed % 10 == 0) {
                    rec.append(r.logSpeed / 10);
                } else {
                    appendNumber(rec, JavaLibBridge.toString(r.logSpeed / 10.0, 1));
                }
            }

            rec.append(fieldSep);

            rec.append("\r\n");
            writeTxt(rec.toString());
            rec.setLength(0);
        } // activeFields!=null
    }

}
