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
import gps.convert.Conv;
import gps.log.GPSRecord;
import gps.log.out.GPSFile;
import moio.util.StringTokenizer;

import bt747.io.File;
import bt747.sys.Convert;
import bt747.util.Date;

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
    private static final double KNOT_PER_KMH = 1.852;

    private int logFormat;
    private File inFile = null;
    protected boolean passToFindFieldsActivatedInLog = false;
    protected int activeFileFields = 0;
    /**
     * When true, corrects height by removing MSL to WGS84 offset.
     */
    private boolean isConvertWGS84ToMSL = false;

    private String errorInfo;

    public String getErrorInfo() {
        return errorInfo;
    }

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
        int curLogFormat;

        try {

            recCount = 0;
            logFormat = 0;
            curLogFormat = 0;
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
                            // on
                            // time change.
                            cmd = fields.nextToken();
                            sNmea = new String[fields.countTokens() + 1];
                            int idx;
                            idx = 0;
                            sNmea[idx++] = cmd;
                            while (fields.hasMoreTokens()) {
                                sNmea[idx++] = fields.nextToken();
                            }

                            GPSRecord gpsNewRec = new GPSRecord(); // Value
                            // after
                            int newLogFormat;
                            newLogFormat = analyzeNMEA(sNmea, gpsNewRec);

                            // Determine if this record belongs to the record
                            // that is ongoing or if it is new.

                            if ((newLogFormat != 0) && (curLogFormat != 0)) {
                                // We have a previous and a new log format
                                int oldClockTime = gpsRec.utc % (24 * 3600);
                                int oldDateTime = (gpsRec.utc - oldClockTime)
                                        / (24 * 3600);
                                if (((oldClockTime != 0) && (oldClockTime != gpsNewRec.utc
                                        % (24 * 3600)))
                                        || ((oldDateTime != 0) && (oldDateTime != gpsNewRec.utc
                                                / (24 * 3600)))) {
                                    // New data is for different time/date -
                                    // write it.
                                    gpsRec.recCount = ++recCount;
                                    finalizeRecord(gpsFile, gpsRec,
                                            curLogFormat);
                                    gpsRec = gpsNewRec;
                                    curLogFormat=newLogFormat;
                                } else {
                                    curLogFormat |= analyzeNMEA(sNmea, gpsRec);
                                }
                            } else {
                                curLogFormat |= analyzeNMEA(sNmea, gpsRec);
                            }
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

    private void finalizeRecord(GPSFile gpsFile, GPSRecord gpsRec,
            int curLogFormat) {
        if (isConvertWGS84ToMSL
                && ((curLogFormat & (1 << BT747Constants.FMT_HEIGHT_IDX)) != 0)
                && ((curLogFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0)
                && ((curLogFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0)) {
            gpsRec.height -= Conv.wgs84Separation(gpsRec.latitude,
                    gpsRec.longitude);
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

        if (curLogFormat != 0 && !passToFindFieldsActivatedInLog) { // Should
            // add time
            // or
            // position change
            // condition.
            gpsFile.writeRecord(gpsRec);
        }
    }

    private void setTime(GPSRecord gpsRec, int time) {
        int newTime;
        newTime = gpsRec.utc;
        newTime -= gpsRec.utc % (24 * 3600);
        newTime += time;
        gpsRec.utc = newTime;
    }

    private boolean setTime(GPSRecord gpsRec, String nmeaTimeStr) {
        int timePart = Convert.toInt(nmeaTimeStr.substring(0, 2)) * 3600
                + Convert.toInt(nmeaTimeStr.substring(2, 4)) * 60
                + Convert.toInt(nmeaTimeStr.substring(4, 6));
        setTime(gpsRec, timePart);
        if(nmeaTimeStr.substring(6).equals(".")) {
            gpsRec.milisecond=(int)(Convert.toFloat(nmeaTimeStr.substring(6))*1000);
            return true;
        } else {
            return false;
        }
    }

    private void setDate(GPSRecord gpsRec, int date) {
        int newTime;
        newTime = gpsRec.utc;
        newTime = gpsRec.utc % (24 * 3600);
        newTime += date;
        gpsRec.utc = newTime;
    }

    private void setDate(GPSRecord gpsRec, String date) {
        int dateInt = Convert.toInt(date);
        int day = dateInt / 10000;
        int month = (dateInt / 100) % 100;
        int year = dateInt % 100 + 2000;
        setDate(gpsRec, (new Date(day, month, year)).dateToUTCepoch1970());
    }

    private void setLatitude(GPSRecord gpsRec, String lat, String pol) {
        // GPSRecord gps = new GPSRecord();
        gpsRec.latitude = (Convert.toDouble(lat.substring(0, 2)) + Convert
                .toDouble(lat.substring(2)) / 60)
                * (pol.equals("N") ? 1 : -1);

    }

    private void setLongitude(GPSRecord gpsRec, String lon, String pol) {
        // GPSRecord gps = new GPSRecord();
        gpsRec.longitude = (Convert.toDouble(lon.substring(0, 3)) + Convert
                .toDouble(lon.substring(3)) / 60)
                * (pol.equals("E") ? 1 : -1);
    }

    private int analyzeNMEA(String[] sNmea, GPSRecord gpsRec) {
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

    final int analyzeGPRMC(final String[] sNmea, GPSRecord gpsRec) {
        int logFormat = 0;
        if (sNmea[0].equals("GPRMC") && (sNmea.length >= 10)) {
            // UTC time
            try {
                if(setTime(gpsRec, sNmea[1])) {
                    logFormat |= (1 << BT747Constants.FMT_MILLISECOND_IDX);
                }
                logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
            } finally {
            }
            // sNmea[2] = valid/invalid
            // latitude
            try {
                // GPSRecord gps = new GPSRecord();
                setLatitude(gpsRec, sNmea[3], sNmea[4]);
                logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);

            } finally {
            }
            // longitude
            try {
                setLongitude(gpsRec, sNmea[5], sNmea[6]);
                logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
            } finally {
            }
            try {
                gpsRec.speed = Convert.toFloat(sNmea[7]) * ((float)KNOT_PER_KMH);
                logFormat |= (1 << BT747Constants.FMT_SPEED_IDX);
            } finally {
            }
            try {
                gpsRec.heading = Convert.toFloat(sNmea[8]);
                logFormat |= (1 << BT747Constants.FMT_HEADING_IDX);
            } finally {
            }
            try {
                setDate(gpsRec, sNmea[9]);
                logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
            } finally {
            }
        }
        return logFormat;
    }

    private int analyzeGPGGA(String[] sNmea, GPSRecord gpsRec) {
        int logFormat = 0;
        if (sNmea[0].equals("GPGGA") && (sNmea.length == 12)) {
            try {
                if(setTime(gpsRec, sNmea[1])) {
                    logFormat |= (1 << BT747Constants.FMT_MILLISECOND_IDX);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                // GPSRecord gps = new GPSRecord();
                setLatitude(gpsRec, sNmea[2], sNmea[3]);
                logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);

            } finally {
            }
            try {
                setLongitude(gpsRec, sNmea[4], sNmea[5]);
                logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
            } finally {
            }
            try {
                gpsRec.valid=Convert.toInt(sNmea[6]);
                logFormat |= (1 << BT747Constants.FMT_VALID_IDX);
            } finally {
            }
            try {
                gpsRec.nsat=Convert.toInt(sNmea[7]);
                logFormat |= (1 << BT747Constants.FMT_NSAT_IDX);
            } finally {
            }
            try {
                gpsRec.hdop=(int)(Convert.toFloat(sNmea[7])*100);
                logFormat |= (1 << BT747Constants.FMT_HDOP_IDX);
            } finally {
            }
            try {
                gpsRec.height=(int)(Convert.toFloat(sNmea[8])*100);
                logFormat |= (1 << BT747Constants.FMT_HEIGHT_IDX);
                gpsRec.geoid = Convert.toFloat(sNmea[10]);
                gpsRec.height+=gpsRec.geoid;
            } finally {
            }
            try {
                gpsRec.dage = Convert.toInt(sNmea[12]);
                logFormat |= (1 << BT747Constants.FMT_DAGE_IDX);
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                gpsRec.dsta = Convert.toInt(sNmea[12]);
                logFormat |= (1 << BT747Constants.FMT_DSTA_IDX);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } // GPGGA
        return logFormat;
    }

    private long timeOffsetSeconds;

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
                        gpsFile.setActiveFileFields(CommonIn
                                .getLogFormatRecord(activeFileFields));
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
            gpsFile.writeLogFmtHeader(CommonIn.getLogFormatRecord(logFormat));
        }
    }
}
