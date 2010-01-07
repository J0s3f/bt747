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

import gps.convert.Conv;
import gps.log.GPSFilter;
import gps.log.GPSRecord;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;

/**
 * Class to write a CSV file.
 * 
 * @author Mario De Weerd
 */
public final class GPSPostgresqlFile extends GPSFile {
    /**
     * 
     */
    private static final float METERS_TO_FEET = 3.28083989501312f;
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

    private String tableName = "table";
    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final BT747Path basename, final String ext,
            final int oneFilePerDay) {
        super.initialiseFile(basename, ext, oneFilePerDay);

        if (getParamObject().hasParam(
                GPSConversionParameters.SQL_TABLE_NAME)) {
            getParamObject().getStringParam(
                    GPSConversionParameters.SQL_TABLE_NAME);
        }
    }

    public final boolean needPassToFindFieldsActivatedInLog() {
        return true;
    }

    protected final void writeFileHeader(final String Name) {
        rec.setLength(0);
        rec.append("CREATE TABLE ");
        rec.append(tableName);
        rec.append(" ( ");
        if ((selectedFileFields.hasUtc())) {
            rec.append("  time TIMESTAMP PRIMARY KEY");
        }
        rec.append(", index INTEGER");
        if ((selectedFileFields.hasRcr())) {
            rec.append(", rcr TEXT");
        }
        if ((selectedFileFields.hasValid())) {
            rec.append(fieldSep + "VALID");
            rec.append(", valid INTEGER");
        }
        if ((selectedFileFields.hasLatitude())) {
            rec.append(", longitude FLOAT");
        }
        if ((selectedFileFields.hasLongitude())) {
            rec.append(", latitude FLOAT");
        }
        if ((selectedFileFields.hasHeight())) {
            rec.append(", height FLOAT");
        }
        if ((selectedFileFields.hasGeoid())) {
            rec.append(", geosep FLOAT");
        }
        if ((selectedFileFields.hasSpeed())) {
            rec.append(", speed FLOAT");
            rec.append(" );\n");
        }
        if ((selectedFileFields.hasHeading())) {
            rec.append(", heading FLOAT");
        }
        if ((selectedFileFields.hasDsta())) {
            rec.append(", dsta INTEGER");
        }
        if ((selectedFileFields.hasDage())) {
            rec.append(", dage INTEGER");
        }
        if ((selectedFileFields.hasPdop())) {
            rec.append(", pdop FLOAT");
        }
        if ((selectedFileFields.hasHdop())) {
            rec.append(", hdop FLOAT");
        }
        if ((selectedFileFields.hasVdop())) {
            rec.append(", vdop FLOAT");
        }
        if ((selectedFileFields.hasNsat())) {
            rec.append(", nsatused INTEGER");
            rec.append(", nsatview INTEGER");
        }
        // SAT INFO NOT HANDLED
        if ((selectedFileFields.hasDistance())) {
            rec.append(", distance FLOAT");
        }

        if ((selectedFileFields.hasSid())) {
            rec.append(", satinfo TEXT");
//            rec.append(fieldSep + "SAT INFO (SID");
//            if ((selectedFileFields.hasEle())) {
//                rec.append("-ELE");
//            }
//            if ((selectedFileFields.hasAzi())) {
//                rec.append("-AZI");
//            }
//            if ((selectedFileFields.hasSnr())) {
//                rec.append("-SNR");
//            }
//            rec.append(")");
        }
        if (selectedFileFields.hasVoxStr()) {
            rec.append(", vox TEXT");
        }
        if (addLogConditionInfo) {
            rec.append(", logtime FLOAT");
            rec.append(", logdistance FLOAT");
            rec.append(", logspeed FLOAT");
        }
        rec.append(" );\r\n");
        writeTxt(rec.toString());
        rec.setLength(0);
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
    /*
     * (non-Javadoc)
     * 
     * @see gps.log.out.GPSFile#writeRecord(gps.log.GPSRecord)
     */
    public final void writeRecord(final GPSRecord r) {
        super.writeRecord(r);

        if (cachedRecordIsNeeded(r)) {
            rec.setLength(0);

            rec.append("INSERT INTO ");
            rec.append(tableName);
            rec.append(" VALUES ( ");
            
           
 
            /* DATE , TIME */
            if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
                //'%4d-%02d-%02d %02d:%02d:%02d' 
                //$year, $month, $day, $hour, $minute, $second,
                rec.append(fieldSep + CommonOut.getDateNumStr(t) + fieldSep
                        + CommonOut.getTimeStr(t));
                if (r.hasMillisecond()) {
                    rec.append('.');
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
            if (r.hasRecCount()) {
                rec.append(r.getRecCount());
            }
            if ((selectedFileFields.hasRcr())) {
                rec.append(fieldSep);
                if (r.hasRcr()) {
                    rec.append(CommonOut.getRCRstr(r));
                }
            }
 
            if ((r.hasValid()) && (selectedFileFields.hasValid())) {
                rec.append(fieldSep);
                rec.append(CommonOut.getFixText(r.getValid()));
            } else if ((selectedFileFields.hasValid())) {
                rec.append(fieldSep);
            }

            if ((r.hasLatitude()) && (selectedFileFields.hasLatitude())) {
                rec.append(fieldSep);
                rec.append(JavaLibBridge.toString(r.getLatitude(), 6));
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
                rec.append(JavaLibBridge.toString(r.getLongitude(), 6));
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
                    rec.append(JavaLibBridge.toString(r.getHeight(), 3));
                } else {
                    rec.append(JavaLibBridge.toString(r.getHeight()
                            * METERS_TO_FEET, 3));
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
                        separation = ((long) (10 * Conv.wgs84Separation(r
                                .getLatitude(), r.getLongitude()))) / 10.f;
                    }
                    if (imperial) {
                        separation *= METERS_TO_FEET;
                    }
                    rec.append(JavaLibBridge.toString(separation, 1));
                    rec.append(',');
                } else {
                    rec.append(',');
                }
            }
            
            if ((r.hasSpeed()) && (selectedFileFields.hasSpeed())) {
                rec.append(fieldSep);
                if (!imperial) {
                    rec.append(JavaLibBridge.toString(r.getSpeed(), 3));
                } else {
                    rec.append(JavaLibBridge.toString(
                            r.getSpeed() * 0.621371192237334f, 3));
                }
            } else if ((selectedFileFields.hasSpeed())) {
                rec.append(fieldSep);
            }
            if ((r.hasHeading()) && (selectedFileFields.hasHeading())) {
                rec.append(fieldSep);
                rec.append(JavaLibBridge.toString(r.getHeading(), 6));
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
                rec.append(JavaLibBridge.toString(r.getPdop() / 100.0f, 2));
            } else if ((selectedFileFields.hasPdop())) {
                rec.append(fieldSep);
            }
            if ((r.hasHdop()) && (selectedFileFields.hasHdop())) {
                rec.append(fieldSep);
                rec.append(JavaLibBridge.toString(r.getHdop() / 100.0f, 2));
            } else if ((selectedFileFields.hasHdop())) {
                rec.append(fieldSep);
            }
            if ((r.hasVdop()) && (selectedFileFields.hasVdop())) {
                rec.append(fieldSep);
                rec.append(JavaLibBridge.toString(r.getVdop() / 100.0f, 2));
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
                    rec.append(JavaLibBridge.toString(r.distance, 2));
                } else {
                    rec.append(JavaLibBridge.toString(
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
                    rec.append(JavaLibBridge.toString(r.logPeriod / 10.0, 1));
                }
                rec.append(fieldSep);
                if (r.logDistance % 10 == 0) {
                    rec.append(r.logDistance / 10);
                } else {
                    rec.append(JavaLibBridge
                            .toString(r.logDistance / 10.0, 1));
                }
                rec.append(fieldSep);
                if (r.logSpeed % 10 == 0) {
                    rec.append(r.logSpeed / 10);
                } else {
                    rec.append(JavaLibBridge.toString(r.logSpeed / 10.0, 1));
                }
            }

            rec.append(fieldSep);

            rec.append("\r\n");
            writeTxt(rec.toString());
            rec.setLength(0);
        } // activeFields!=null
    }

}
