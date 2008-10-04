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

package gps.parser;

import gps.BT747Constants;
import gps.log.GPSRecord;
import gps.log.in.GPSLogConvert;
import gps.log.out.GPSFile;

import bt747.sys.Generic;

/**
 * This class is used to convert the binary log to a new format. Basically this
 * class interprets the log and creates a {@link GPSRecord}. The
 * {@link GPSRecord} is then sent to the {@link GPSFile} class object to write
 * it to the output.
 * 
 * @author Mario De Weerd
 */
public final class NewLogConvert implements GPSLogConvert {
    protected boolean passToFindFieldsActivatedInLog = false;
    protected int activeFileFields = 0;
    String[] argv = new String[1];

    private String errorInfo;

    public final String getErrorInfo() {
        return errorInfo;
    }

    /** Parse the input file and convert.
     * @return returns non zero in case of error. error text is available
     *         through {@link #getErrorInfo()}
     * @param gpsFile -
     *            object doing actual write to files
     * 
     */
    public final int parseFile(final GPSFile gpsFile) {
        int error = BT747Constants.NO_ERROR;
        int format = 0;
        try {
            LogFile lf = new LogFile(argv[0], new LogDataParserHolux());

            int i = 0;
            for (LogPacket packet : lf) {
                i++;
                GPSRecord r = toGPSRecord(packet);
                if (passToFindFieldsActivatedInLog) {
                    format |= packet.recFormat();
                } else {
                    if (format != packet.recFormat()) {
                        gpsFile
                                .writeLogFmtHeader(getLogFormatRecord(format = packet
                                        .recFormat()));
                    }
                    r.recCount = i;
                    gpsFile.writeRecord(r);
                }
                if(stop) {
                    return -1;  // TODO: better error identification
                }
            }
        } catch (Exception e) {
            Generic.debug("? Problem reading file " + argv[0],e);
            errorInfo = argv[0];
            error = BT747Constants.ERROR_READING_FILE;
        }
        activeFileFields = format;
        return error;
    }

    public final void setTimeOffset(final long offset) {
    }

    public final void setConvertWGS84ToMSL(final boolean b) {
    }

    public final int toGPSFile(final String fileName, final GPSFile gpsFile,
            final int Card) {
        int error = BT747Constants.NO_ERROR;
        Generic.debug("Using new parser");
        argv[0] = fileName;
        passToFindFieldsActivatedInLog = gpsFile
                .needPassToFindFieldsActivatedInLog();
        if (passToFindFieldsActivatedInLog) {
            activeFileFields = 0;
            parseFile(gpsFile);
            gpsFile.setActiveFileFields(getLogFormatRecord(activeFileFields));
            gpsFile.writeLogFmtHeader(getLogFormatRecord(0xFFFFFF));
        }
        passToFindFieldsActivatedInLog = false;
        do {
            parseFile(gpsFile);
        } while (gpsFile.nextPass());
        gpsFile.finaliseFile();
        if (gpsFile.getFilesCreated() == 0) {
            error = BT747Constants.ERROR_NO_FILES_WERE_CREATED;
        }
        return error;
    }

    /**
     * @param holux
     *            The holux to set.
     */
    public void setHolux(boolean holux) {
    }

    private final GPSRecord toGPSRecord(final LogPacket r) {
        GPSRecord d = new GPSRecord();
        d.utc = (int) (r.utc.getTime() / 1000L);
        // d.valid= r.valid;
        if (r.fix != null) {
            d.valid = r.fix;
        } else {
            d.valid = 0xFFFF;
        }
        ;
        if (r.latitude != null)
            d.latitude = r.latitude;
        if (r.longitude != null)
            d.longitude = r.longitude;
        if (r.height != null)
            d.height = r.height;
        if (r.speed != null)
            d.speed = r.speed;
        if (r.heading != null)
            d.heading = r.heading;
        if (r.dsta != null)
            d.dsta = r.dsta;
        if (r.dage != null)
            d.dage = r.dage;
        if (r.pdop != null)
            d.pdop = r.pdop;
        if (r.hdop != null)
            d.hdop = r.hdop;
        if (r.vdop != null)
            d.vdop = r.vdop;
        if (r.nSatInUse != null && r.nSatInView != null) {
            d.nsat = (r.nSatInUse << 8) + r.nSatInView;
        }
        if (r.sats != null) {
            // Object method clone() does not work on device for arrays.
            // Doing explicit copy until better method found.
            int i = r.sats.length;
            d.sid = new int[i];
            d.sidinuse = new boolean[i];
            if (r.sats[0].elevation != null) {
                d.ele = new int[i];
            }
            if (r.sats[0].azimuth != null) {
                d.azi = new int[i];
            }
            if (r.sats[0].SNR != null) {
                d.snr = new int[i];
            }

            for (i -= 1; i >= 0; i--) {
                d.sid[i] = r.sats[i].id;
                d.sidinuse[i] = r.sats[i].isInUse;
                if (r.sats[i].elevation != null) {
                    d.ele[i] = r.sats[i].elevation;
                }
                if (r.sats[i].azimuth != null) {
                    d.azi[i] = r.sats[i].azimuth;
                }
                if (r.sats[i].SNR != null) {
                    d.snr[i] = r.sats[i].SNR;
                }
            }
        }
        if (r.recordingMethod != null)
            d.rcr = r.recordingMethod;
        else
            d.rcr = 1;
        if (r.milliseconds != null)
            d.milisecond = r.milliseconds;
        if (r.distance != null)
            d.distance = r.distance;
        // d.geoid=r.geoid;
        // d.recCount=r.recCount;

        return d;
    }

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
    
    private boolean stop = false;
    public final void stopConversion() {
        stop = true;
    }

}