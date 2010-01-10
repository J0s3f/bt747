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
    private static final String LOGSPEED_FIELD = "logspeed";
    /**
     * 
     */
    private static final String LOGDISTANCE_FIELD = "logdistance";
    /**
     * 
     */
    private static final String LOGTIME_FIELD = "logtime";
    /**
     * 
     */
    private static final String VOX_FIELD = "vox";
    /**
     * 
     */
    private static final String SATINFO_FIELD = "satinfo";
    /**
     * 
     */
    private static final String DISTANCE_FIELD = "distance";
    /**
     * 
     */
    private static final String NSATVIEW_FIELD = "nsatview";
    /**
     * 
     */
    private static final String NSATUSED_FIELD = "nsatused";
    /**
     * 
     */
    private static final String VDOP_FIELD = "vdop";
    /**
     * 
     */
    private static final String HDOP_FIELD = "hdop";
    /**
     * 
     */
    private static final String PDOP_FIELD = "pdop";
    /**
     * 
     */
    private static final String DAGE_FIELD = "dage";
    /**
     * 
     */
    private static final String DSTA_FIELD = "dsta";
    /**
     * 
     */
    private static final String HEADING_FIELD = "heading";
    /**
     * 
     */
    private static final String SPEED_FIELD = "speed";
    /**
     * 
     */
    private static final String GEOID_FIELD = "geosep";
    /**
     * 
     */
    private static final String HEIGHT_FIELD = "height";
    /**
     * 
     */
    private static final String LATITUDE_FIELD = "latitude";
    /**
     * 
     */
    private static final String INDEX_FIELD = "idx";
    /**
     * 
     */
    private static final String TIME_FIELD = "time";
    /**
     * 
     */
    private static final String LONGITUDE_FIELD = "longitude";
    /**
     * 
     */
    private static final String VALID_FIELD = "valid";
    /**
     * 
     */
    private static final String RCR_FIELD = "rcr";
    /**
     * 
     */
    private static final float METERS_TO_FEET = 3.28083989501312f;
    /**
     * Reused StringBuffer for output construction.
     */
    private final StringBuffer rec = new StringBuffer(1024);
    private final StringBuffer recPost = new StringBuffer(1024);
    /**
     * Separator in the satellite field - for future parameterization.
     */
    private String satSeperator = ";";

    private String tableName = "defaulttable";
    private String dbName = "defaultdb";

    /*
     * (non-Javadoc)
     * 
     * @see gps.GPSFile#InitialiseFile(java.lang.String, java.lang.String)
     */
    public void initialiseFile(final BT747Path basename, final String ext,
            final int oneFilePerDay) {
        super.initialiseFile(basename, ext, oneFilePerDay);

        if (getParamObject().hasParam(GPSConversionParameters.SQL_TABLE_NAME)) {
            tableName = getParamObject().getStringParam(
                    GPSConversionParameters.SQL_TABLE_NAME);
        }
    }

    public final boolean needPassToFindFieldsActivatedInLog() {
        return true;
    }

    protected final void writeFileHeader(final String Name) {
        rec.setLength(0);
        recPost.setLength(0);

        /** Create db with postgis. */
        // rec.append("CREATE DATABASE my_spatial_db TEMPLATE=template_postgis ;\r\n");

        /** DB is selected before executing SQL */
        char separator = ' ';
        rec.append("CREATE TABLE ");
        rec.append(tableName);
        rec.append(" (");

        if ((selectedFileFields.hasUtc())) {
            rec.append(separator);
            rec.append(" " + TIME_FIELD + " TIMESTAMP PRIMARY KEY");
            separator = ',';
        }
        rec.append(separator);
        rec.append(" " + INDEX_FIELD + " INTEGER");
        separator = ',';
        if ((selectedFileFields.hasRcr())) {
            rec.append(", " + RCR_FIELD + " TEXT");
        }
        if ((selectedFileFields.hasValid())) {
            rec.append(", " + VALID_FIELD + " INTEGER");
        }
        if ((selectedFileFields.hasLatitude())) {
            rec.append(", " + LONGITUDE_FIELD + " FLOAT");
        }
        if ((selectedFileFields.hasLongitude())) {
            rec.append(", " + LATITUDE_FIELD + " FLOAT");
        }
        if ((selectedFileFields.hasHeight())) {
            rec.append(", " + HEIGHT_FIELD + " FLOAT");
        }
        if ((selectedFileFields.hasGeoid())) {
            rec.append(", " + GEOID_FIELD + " FLOAT");
        }
        if ((selectedFileFields.hasSpeed())) {
            rec.append(", " + SPEED_FIELD + " FLOAT");
        }
        if ((selectedFileFields.hasHeading())) {
            rec.append(", " + HEADING_FIELD + " FLOAT");
        }
        if ((selectedFileFields.hasDsta())) {
            rec.append(", " + DSTA_FIELD + " INTEGER");
        }
        if ((selectedFileFields.hasDage())) {
            rec.append(", " + DAGE_FIELD + " INTEGER");
        }
        if ((selectedFileFields.hasPdop())) {
            rec.append(", " + PDOP_FIELD + " FLOAT");
        }
        if ((selectedFileFields.hasHdop())) {
            rec.append(", " + HDOP_FIELD + " FLOAT");
        }
        if ((selectedFileFields.hasVdop())) {
            rec.append(", " + VDOP_FIELD + " FLOAT");
        }
        if ((selectedFileFields.hasNsat())) {
            rec.append(", " + NSATUSED_FIELD + " INTEGER");
            rec.append(", " + NSATVIEW_FIELD + " INTEGER");
        }
        // SAT INFO NOT HANDLED
        if ((selectedFileFields.hasDistance())) {
            rec.append(", " + DISTANCE_FIELD + " FLOAT");
        }

        if ((selectedFileFields.hasSid())) {
            rec.append(", " + SATINFO_FIELD + " TEXT");
            // rec.append(fieldSep + "SAT INFO (SID");
            // if ((selectedFileFields.hasEle())) {
            // rec.append("-ELE");
            // }
            // if ((selectedFileFields.hasAzi())) {
            // rec.append("-AZI");
            // }
            // if ((selectedFileFields.hasSnr())) {
            // rec.append("-SNR");
            // }
            // rec.append(")");
        }
        if (selectedFileFields.hasVoxStr()) {
            rec.append(", " + VOX_FIELD + " TEXT");
        }
        if (addLogConditionInfo) {
            rec.append(", " + LOGTIME_FIELD + " FLOAT");
            rec.append(", " + LOGDISTANCE_FIELD + " FLOAT");
            rec.append(", " + LOGSPEED_FIELD + " FLOAT");
        }
        rec.append(" );\r\n");
        rec.append("CREATE INDEX " + tableName + "_" + INDEX_FIELD
                + "_index ON " + tableName + "(" + INDEX_FIELD + ");\r\n");
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
            /** The rows are filled while using named fields */
            rec.setLength(0);
            recPost.setLength(0);

            rec.append("INSERT INTO ");
            rec.append(tableName);
            rec.append(" (");
            recPost.append(" VALUES (");
            char separator = ' ';

            /* DATE , TIME */
            if ((r.hasUtc()) && (selectedFileFields.hasUtc())) {
                rec.append(TIME_FIELD);
                separator = ',';
                // '%4d-%02d-%02d %02d:%02d:%02d'
                // $year, $month, $day, $hour, $minute, $second,
                // rec.append(b)
                recPost.append(',' + CommonOut.getDateNumStr(t) + ','
                        + CommonOut.getTimeStr(t));
                if (r.hasMillisecond()) {
                    recPost.append('.');
                    if (r.milisecond < 100) {
                        recPost.append('0');
                    }
                    if (r.milisecond < 10) {
                        recPost.append('0');
                    }
                    recPost.append(r.milisecond);
                }
            }

            if (r.hasRecCount()) {
                rec.append(separator);
                rec.append(INDEX_FIELD);
                recPost.append(separator);
                recPost.append(r.getRecCount());
            }
            if ((selectedFileFields.hasRcr())) {
                rec.append(',');
                rec.append(RCR_FIELD);
                recPost.append(',');
                recPost.append('"');
                if (r.hasRcr()) {
                    recPost.append(CommonOut.getRCRstr(r));
                }
                recPost.append('"');
            }

            if ((r.hasValid()) && (selectedFileFields.hasValid())) {
                rec.append(',');
                rec.append(VALID_FIELD);
                recPost.append(',');
                recPost.append(CommonOut.getFixText(r.getValid()));
            }

            if ((r.hasLatitude()) && (selectedFileFields.hasLatitude())) {
                rec.append(',');
                rec.append(LATITUDE_FIELD);
                recPost.append(',');
                recPost.append(JavaLibBridge.toString(r.getLatitude(), 6));
            }
            if ((r.hasLongitude()) && (selectedFileFields.hasLongitude())) {
                rec.append(',');
                rec.append(LONGITUDE_FIELD);
                recPost.append(',');
                recPost.append(JavaLibBridge.toString(r.getLongitude(), 6));
            }
            if ((r.hasHeight()) && (selectedFileFields.hasHeight())) {
                rec.append(',');
                rec.append(HEIGHT_FIELD);
                recPost.append(',');
                if (!imperial) {
                    recPost.append(JavaLibBridge.toString(r.getHeight(), 3));
                } else {
                    recPost.append(JavaLibBridge.toString(r.getHeight()
                            * METERS_TO_FEET, 3));
                }
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
                    rec.append(',');
                    rec.append(GEOID_FIELD);
                    recPost.append(',');
                    recPost.append(JavaLibBridge.toString(separation, 1));
                }
            }

            if ((r.hasSpeed()) && (selectedFileFields.hasSpeed())) {
                rec.append(',');
                rec.append(SPEED_FIELD);
                recPost.append(',');
                if (!imperial) {
                    recPost.append(JavaLibBridge.toString(r.getSpeed(), 3));
                } else {
                    recPost.append(JavaLibBridge.toString(
                            r.getSpeed() * 0.621371192237334f, 3));
                }
            }
            if ((r.hasHeading()) && (selectedFileFields.hasHeading())) {
                rec.append(',');
                rec.append(HEADING_FIELD);
                recPost.append(',');
                recPost.append(JavaLibBridge.toString(r.getHeading(), 6));
            }
            if ((r.hasDsta()) && (selectedFileFields.hasDsta())) {
                rec.append(',');
                rec.append(DSTA_FIELD);
                recPost.append(',');
                recPost.append(r.getDsta());
            }
            if ((r.hasDage()) && (selectedFileFields.hasDage())) {
                rec.append(',');
                rec.append(DAGE_FIELD);
                recPost.append(',');
                recPost.append(r.getDage());
            } else if ((selectedFileFields.hasDage())) {
                recPost.append(',');
            }
            if ((r.hasPdop()) && (selectedFileFields.hasPdop())) {
                rec.append(',');
                rec.append(PDOP_FIELD);
                recPost.append(',');
                recPost.append(JavaLibBridge
                        .toString(r.getPdop() / 100.0f, 2));
            }
            if ((r.hasHdop()) && (selectedFileFields.hasHdop())) {
                rec.append(',');
                rec.append(HDOP_FIELD);
                recPost.append(',');
                recPost.append(JavaLibBridge
                        .toString(r.getHdop() / 100.0f, 2));
            }
            if ((r.hasVdop()) && (selectedFileFields.hasVdop())) {
                rec.append(',');
                rec.append(VDOP_FIELD);
                recPost.append(',');
                recPost.append(JavaLibBridge
                        .toString(r.getVdop() / 100.0f, 2));
            }
            if ((r.hasNsat()) && (selectedFileFields.hasNsat())) {
                rec.append(',');
                rec.append(NSATVIEW_FIELD);
                rec.append(',');
                rec.append(NSATUSED_FIELD);
                recPost.append(',');
                recPost.append((r.getNsat() & 0xFF00) >> 8);
                recPost.append(',');
                recPost.append(r.getNsat() & 0xFF);
            }
            if ((r.hasDistance()) && (selectedFileFields.hasDistance())) {
                rec.append(',');
                rec.append(DISTANCE_FIELD);
                recPost.append(',');
                if (!imperial) {
                    recPost.append(JavaLibBridge.toString(r.distance, 2));
                } else {
                    recPost.append(JavaLibBridge.toString(
                            r.distance * 3.28083989501312, 2));
                }
            }
            if ((selectedFileFields.hasSid())) {
                int j = 0;
                rec.append(',');
                rec.append(SATINFO_FIELD);
                recPost.append(",\"");
                if (r.hasSid()) {
                    for (int i = r.sid.length - 1; i >= 0; i--) {
                        if (j != 0) {
                            recPost.append(satSeperator);
                        }
                        if (r.sidinuse[j]) {
                            recPost.append('#');
                        }
                        if (r.sid[j] < 10) {
                            recPost.append('0');
                        }
                        recPost.append(r.sid[j]);
                        if ((selectedFileFields.hasEle())) {
                            recPost.append('-');
                            if (r.hasEle()) {
                                if (r.ele[j] < 10) {
                                    recPost.append('0');
                                }
                                recPost.append(r.ele[j]);
                            }
                        }
                        if ((selectedFileFields.hasAzi())) {
                            recPost.append('-');
                            if (r.hasAzi()) {
                                // if(s.azi[j]<100) {
                                // fields_val.append('0');
                                if (r.azi[j] < 10) {
                                    recPost.append('0');
                                }
                                // }
                                recPost.append(r.azi[j]);
                            }
                        }
                        if ((selectedFileFields.hasSnr())) {
                            recPost.append('-');
                            if (r.hasSnr()) {
                                if (r.snr[j] < 10) {
                                    recPost.append('0');
                                }
                                recPost.append(r.snr[j]);
                            }
                        }
                        j++;
                    }
                }
                recPost.append('"');
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
            // fields_val.append(","+JavaLibBridge.toString(speed,3)+","+distance);
            // }
            // prevRecord=new GPSRecord(s);
            // }

            if (selectedFileFields.hasVoxStr()) {
                rec.append(',');
                rec.append(VOX_FIELD);
                recPost.append(",\"");
                if (r.voxStr != null) {
                    recPost.append(r.voxStr);
                }
                recPost.append('"');
            }

            if (addLogConditionInfo) {
                rec.append(',');
                rec.append(LOGTIME_FIELD);
                rec.append(',');
                rec.append(LOGDISTANCE_FIELD);
                rec.append(',');
                rec.append(LOGSPEED_FIELD);
                recPost.append(',');
                if (r.logPeriod % 10 == 0) {
                    recPost.append(r.logPeriod / 10);
                } else {
                    recPost.append(JavaLibBridge.toString(r.logPeriod / 10.0,
                            1));
                }
                recPost.append(',');
                if (r.logDistance % 10 == 0) {
                    recPost.append(r.logDistance / 10);
                } else {
                    recPost.append(JavaLibBridge.toString(
                            r.logDistance / 10.0, 1));
                }
                recPost.append(',');
                if (r.logSpeed % 10 == 0) {
                    recPost.append(r.logSpeed / 10);
                } else {
                    recPost.append(JavaLibBridge.toString(r.logSpeed / 10.0,
                            1));
                }
            }

            rec.append(" ) ");
            recPost.append(" )");

            recPost.append(";\r\n");
            rec.append(recPost);
            recPost.setLength(0);
            writeTxt(rec.toString());
            rec.setLength(0);
        } // activeFields!=null
    }

}
