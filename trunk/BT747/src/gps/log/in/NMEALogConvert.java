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
package gps.log.in;

import gps.BT747Constants;
import gps.GpsEvent;
import gps.convert.Conv;
import gps.log.GPSRecord;
import gps.log.out.GPSFile;

import bt747.io.File;
import bt747.sys.Convert;
import bt747.sys.Settings;
import bt747.util.Date;

import moio.util.StringTokenizer;

/**
 * This class is used to convert the binary log to a new format. Basically this
 * class interprets the log and creates a {@link GPSRecord}. The
 * {@link GPSRecord} is then sent to the {@link GPSFile} class object to write
 * it to the output.
 * 
 * @author Mario De Weerd
 */
public final class NMEALogConvert implements GPSLogConvert {
    private static final int EOL = 0x0D;
    private static final int CR = 0x0A;
    private int logFormat;
    private File inFile = null;
    private long timeOffsetSeconds = 0;
    protected boolean passToFindFieldsActivatedInLog = false;
    protected int activeFileFields = 0;
    /**
     * When true, corrects height by removing MSL to WGS84 offset.
     */
    private boolean isConvertWGS84ToMSL = false;

    private static final int C_LOGTIME = -9;
    private static final int C_LOGDIST = -8;
    private static final int C_LOGSPD = -7;
    private static final int FMT_NS = -6;
    private static final int FMT_EW = -5;
    private static final int FMT_REC_NBR = -4;
    private static final int FMT_DATE = -3;
    private static final int FMT_TIME = -2;
    private static final int FMT_NO_FIELD = -1;

    // private static final int DAYS_BETWEEN_1970_1983 = 4748;
    private static final int DAYS_JULIAN_1970 = (new Date(1, 1, 1970))
            .getJulianDay();

    private String errorInfo;

    public String getErrorInfo() {
        return errorInfo;
    }

    private static final int FMT_HEIGHT_FT_IDX = BT747Constants.FMT_HEIGHT_IDX + 100;
    private static final int FMT_SPEED_MPH_IDX = BT747Constants.FMT_SPEED_IDX + 100;
    private static final int FMT_DISTANCE_FT_IDX = BT747Constants.FMT_DISTANCE_IDX + 100;
    /**
     * Maximum number of fields allowed/expected in CSV file.
     */
    private static final int MAX_RECORDS = 30;
    /**
     * The size of the file read buffer.
     */
    private static final int BUF_SIZE = 0x800;

    /**
     * Convert the input file set using other methods towards gpsFile. ({@link #toGPSFile(String, GPSFile, int)}
     * is one of them.
     * 
     * @param gpsFile
     *            The object representing the output format.
     * @return {@link BT747Constants#NO_ERROR} if no error (0)
     * @see gps.log.in.GPSLogConvert#parseFile(gps.log.out.GPSFile)
     */
    public int parseFile(final GPSFile gpsFile) {
        GPSRecord gpsRec = new GPSRecord();
        byte[] bytes = new byte[BUF_SIZE];
        int sizeToRead;
        int nextAddrToRead;
        int recCount;
        int fileSize;

        boolean firstline = true;
        int[] records = new int[MAX_RECORDS];
        try {
            records[0] = FMT_NO_FIELD; // Indicates that there are no records

            recCount = 0;
            logFormat = 0;
            nextAddrToRead = 0;
            fileSize = inFile.getSize();

            while (nextAddrToRead < fileSize) {
                /***************************************************************
                 * Read data from the data file into the local buffer.
                 */
                // Determine size to read
                sizeToRead = BUF_SIZE;
                if ((sizeToRead + nextAddrToRead) > fileSize) {
                    sizeToRead = fileSize - nextAddrToRead;
                }

                /* Read the bytes from the file */
                int readResult;
                boolean continueInBuffer = true;
                int offsetInBuffer = 0;

                inFile.setPos(nextAddrToRead);

                /***************************************************************
                 * Not reading header - reading data.
                 */
                readResult = inFile.readBytes(bytes, 0, sizeToRead);
                if (readResult != sizeToRead) {
                    errorInfo = inFile.getPath() + "|" + inFile.lastError;
                    return BT747Constants.ERROR_READING_FILE;
                }
                nextAddrToRead += sizeToRead;

                // ///////////////////////////////
                // DATA has been read in 'bytes'
                //

                /***************************************************************
                 * Interpret the data read in the Buffer as long as the records
                 * are complete
                 */
                // A block of bytes has been read, read the records
                do {
                    int eolPos;
                    // Find end of line
                    for (eolPos = offsetInBuffer; (eolPos < sizeToRead)
                            && (bytes[eolPos] != CR) && (bytes[eolPos] != EOL); eolPos++) {
                        ; // Empty on purpose
                    }
                    continueInBuffer = (eolPos < sizeToRead); // True when
                    // \r\n

                    if (continueInBuffer) {
                        StringBuffer s = new StringBuffer(eolPos
                                - offsetInBuffer + 1);
                        byte checkSum = 0;
                        byte firstChar;
                        String checkStr = "";
                        firstChar = bytes[offsetInBuffer];
                        for (int i = offsetInBuffer + 1; i < eolPos - 3; i++) {
                            s.append((char) bytes[i]);
                            checkSum ^= bytes[i];
                        }
                        checkStr += (char) bytes[eolPos - 2];
                        checkStr += (char) bytes[eolPos - 1];

                        checkSum ^= Conv.hex2Int(checkStr);

                        StringTokenizer fields = new StringTokenizer(s
                                .toString(), ",");
                        offsetInBuffer = eolPos;
                        for (; offsetInBuffer < sizeToRead
                                && (bytes[offsetInBuffer] == CR || bytes[offsetInBuffer] == EOL); offsetInBuffer++) {
                            ; // Empty on purpose
                        }
                        if ((firstChar == '$') && (bytes[eolPos - 3] == '*')
                                && (checkSum == 0) && (s.length() != 0)
                                && fields.hasMoreTokens()) {
                            String cmd;
                            String[] sNmea;
                            int curLogFormat = 0; // Should be set to 0 only
                            // on
                            // time change.
                            cmd = fields.nextToken();
                            sNmea = new String[fields.countTokens()];
                            int idx;
                            idx = 0;
                            while (fields.hasMoreTokens()) {
                                sNmea[idx++] = fields.nextToken();
                            }
                            if (cmd.equals("GPGGA")) {
                                if (sNmea.length == 11) {
                                    gpsRec = new GPSRecord(); // Value after

                                    try {
                                        // Time only!
                                        gpsRec.utc = Convert.toInt(sNmea[0]
                                                .substring(0, 2))
                                                * 3600
                                                + Convert.toInt(sNmea[0]
                                                        .substring(2, 4))
                                                * 60
                                                + Convert.toInt(sNmea[0]
                                                        .substring(4, 6));
                                        curLogFormat |= (1 << BT747Constants.FMT_UTC_IDX);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                    try {
                                        // GPSRecord gps = new GPSRecord();
                                        gpsRec.latitude = (Convert
                                                .toDouble(sNmea[1].substring(0,
                                                        2)) + Convert
                                                .toDouble(sNmea[1].substring(2)) / 60)
                                                * (sNmea[2].equals("N") ? 1
                                                        : -1);
                                        curLogFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);

                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                    try {
                                        gpsRec.longitude = (Convert
                                                .toDouble(sNmea[3].substring(0,
                                                        3)) + Convert
                                                .toDouble(sNmea[3].substring(3)) / 60)
                                                * (sNmea[4].equals("E") ? 1
                                                        : -1);
                                        curLogFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                    try {
                                        gpsRec.height = Convert
                                                .toFloat(sNmea[8]);
                                        curLogFormat |= (1 << BT747Constants.FMT_HEIGHT_IDX);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                    try {
                                        gpsRec.geoid = Convert
                                                .toFloat(sNmea[10]);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                }
                            } // GPGGA

                            if (!passToFindFieldsActivatedInLog) {
                                if (isConvertWGS84ToMSL
                                        && ((curLogFormat & (1 << BT747Constants.FMT_HEIGHT_IDX)) != 0)
                                        && ((curLogFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0)
                                        && ((curLogFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0)) {
                                    gpsRec.height -= Conv.wgs84Separation(
                                            gpsRec.latitude, gpsRec.longitude);
                                }
                                if (curLogFormat != logFormat) {
                                    updateLogFormat(gpsFile, curLogFormat);
                                }
                                if (gpsRec.rcr == 0) {
                                    gpsRec.rcr = 1; // Suppose time (for filter)
                                }
                                // if (valid) {
                                if (gpsRec.valid == 0) {
                                    gpsRec.valid = BT747Constants.VALID_SPS_MASK;
                                }
                                if (curLogFormat != 0) { // Should add time
                                    // or
                                    // position change
                                    // condition.
                                    gpsFile.writeRecord(gpsRec);
                                }
                            }
                            // } // offsetInBuffer++;
                        }
                    } // line found
                } while (continueInBuffer);
                nextAddrToRead -= (sizeToRead - offsetInBuffer);
            } /* nextAddrToRead<fileSize */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BT747Constants.NO_ERROR;
    }

    public void setTimeOffset(final long offset) {
        timeOffsetSeconds = offset;
    }

    public void setConvertWGS84ToMSL(final boolean b) {
        isConvertWGS84ToMSL = b;
    }

    public int toGPSFile(final String fileName, final GPSFile gpsFile,
            final int card) {
        int error = BT747Constants.NO_ERROR;
        try {
            if (File.isAvailable()) {
                inFile = new File(fileName, File.READ_ONLY, card);
                if (!inFile.isOpen()) {
                    errorInfo = fileName + "|" + inFile.lastError;
                    error = BT747Constants.ERROR_COULD_NOT_OPEN;
                    inFile = null;
                } else {
                    passToFindFieldsActivatedInLog = gpsFile
                            .needPassToFindFieldsActivatedInLog();
                    if (passToFindFieldsActivatedInLog) {
                        activeFileFields = 0;
                        error = parseFile(gpsFile);
                        gpsFile
                                .setActiveFileFields(getLogFormatRecord(activeFileFields));
                    }
                    passToFindFieldsActivatedInLog = false;
                    if (error == BT747Constants.NO_ERROR) {
                        do {
                            error = parseFile(gpsFile);
                        } while (gpsFile.nextPass());
                    }
                    gpsFile.finaliseFile();
                }

                if (inFile != null) {
                    inFile.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return error;
    }

    private void updateLogFormat(final GPSFile gpsFile, final int newLogFormat) {
        logFormat = newLogFormat;
        activeFileFields |= logFormat;
        if (!passToFindFieldsActivatedInLog) {
            gpsFile.writeLogFmtHeader(getLogFormatRecord(logFormat));
        }
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
