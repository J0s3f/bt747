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
import bt747.sys.StringTokenizer;

import bt747.sys.File;
import bt747.sys.Generic;

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
    private WindowedFile mFile = null;
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

    private boolean stop = false;

    public void stopConversion() {
        stop = true;
    }

    /**
     * Convert the input file set using other methods towards gpsFile. ({@link #toGPSFile(String, GPSFile, int)}
     * is one of them.
     * 
     * @param gpsFile
     *            The object representing the output format.
     * @return {@link BT747Constants#NO_ERROR} if no error (0)
     * @see gps.log.in.GPSLogConvert#parseFile(gps.log.out.GPSFile)
     */
    public final int parseFile(final GPSFile gpsFile) {
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
            fileSize = mFile.getSize();
            try {
                fileSize = mFile.getSize();
            } catch (Exception e) {
                Generic.debug("getSize", e);
                // TODO: handle exception
                fileSize = 0;
            }

            while (!stop && nextAddrToRead < fileSize) {
                /***************************************************************
                 * Read data from the data file into the local buffer.
                 */
                // Determine size to read
                sizeToRead = BUF_SIZE;
                if ((sizeToRead + nextAddrToRead) > fileSize) {
                    sizeToRead = fileSize - nextAddrToRead;
                }

                /* Read the bytes from the file */
                boolean continueInBuffer = true;
                int offsetInBuffer = 0;

                /***************************************************************
                 * Not reading header - reading data.
                 */
                try {
                    bytes = mFile.fillBuffer(nextAddrToRead);
                } catch (Exception e) {
                    // TODO: Should check sizeToRead vs fill in buffer.
                    Generic.debug("Problem reading file", e);
                    bytes = null;
                }
                if (bytes == null) {
                    errorInfo = mFile.getPath() + "|" + mFile.getLastError();
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
                    eolPos = offsetInBuffer;
                    // Skip initial white space.
                    while ((eolPos < sizeToRead)
                            && ((bytes[eolPos] == CR) || (bytes[eolPos] == EOL))) {
                        eolPos++;
                    }
                    // Find first EOL.
                    while ((eolPos < sizeToRead) && (bytes[eolPos] != CR)
                            && (bytes[eolPos] != EOL)) {
                        eolPos++;
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
                        try {
                            checkStr += (char) bytes[eolPos - 2];
                            checkStr += (char) bytes[eolPos - 1];
                        } catch (Exception e) {
                            Generic.debug("eolPos " + eolPos, e);
                        }

                        checkSum ^= Conv.hex2Int(checkStr);

                        StringTokenizer fields = new StringTokenizer(s
                                .toString(), ',');
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
                            newLogFormat = CommonIn.analyzeNMEA(sNmea,
                                    gpsNewRec);

                            // Determine if this record belongs to the record
                            // that is ongoing or if it is new.

                            if ((newLogFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0
                                    && (curLogFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0) {
                                // We have a previous and a new log format
                                int oldClockTime = gpsRec.utc % (24 * 3600);
                                int oldDateTime = (gpsRec.utc - oldClockTime)
                                        / (24 * 3600);
                                if (((oldClockTime != 0) && (oldClockTime != gpsNewRec.utc
                                        % (24 * 3600)))
                                        || ((oldDateTime != 0) && (oldDateTime != gpsNewRec.utc
                                                / (24 * 3600)))
                                        || gpsRec.milisecond != gpsNewRec.milisecond) {
                                    // New data is for different time/date -
                                    // write it.
                                    gpsRec.recCount = ++recCount;
                                    finalizeRecord(gpsFile, gpsRec,
                                            curLogFormat);
                                    gpsRec = gpsNewRec;
                                    curLogFormat = newLogFormat;
                                } else {
                                    curLogFormat |= CommonIn.analyzeNMEA(sNmea,
                                            gpsRec);
                                }
                            } else {
                                curLogFormat |= CommonIn.analyzeNMEA(sNmea,
                                        gpsRec);
                            }
                        }
                    } // line found
                } while (continueInBuffer);
                nextAddrToRead -= (sizeToRead - offsetInBuffer);
            } /* nextAddrToRead<fileSize */
            if ((curLogFormat != 0)) {
                // Write the last record
                gpsRec.recCount = ++recCount;
                finalizeRecord(gpsFile, gpsRec, curLogFormat);
            }

        } catch (Exception e) {
            Generic.debug("", e);
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

    private long timeOffsetSeconds;

    public final void setTimeOffset(final long offset) {
        timeOffsetSeconds = offset;
    }

    public final void setConvertWGS84ToMSL(final boolean b) {
        isConvertWGS84ToMSL = b;
    }

    public final int toGPSFile(final String fileName, final GPSFile gpsFile,
            final int card) {
        int error = BT747Constants.NO_ERROR;
        stop = false;
        try {
            if (File.isAvailable()) {
                try {
                    mFile = new WindowedFile(fileName, File.READ_ONLY, card);
                    mFile.setBufferSize(BUF_SIZE);
                    errorInfo = fileName + "|" + mFile.getLastError();
                } catch (Exception e) {
                    Generic.debug("Error during initial open", e);
                }
                if (mFile == null || !mFile.isOpen()) {
                    errorInfo = fileName + "|" + mFile.getLastError();
                    error = BT747Constants.ERROR_COULD_NOT_OPEN;
                    mFile = null;
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

                if (mFile != null) {
                    mFile.close();
                }
            }
        } catch (Exception e) {
            Generic.debug("", e);
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
