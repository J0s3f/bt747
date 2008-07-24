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

        try {

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
                            sNmea = new String[fields.countTokens() + 1];
                            int idx;
                            idx = 0;
                            sNmea[idx++] = cmd;
                            while (fields.hasMoreTokens()) {
                                sNmea[idx++] = fields.nextToken();
                            }
                            gpsRec = new GPSRecord(); // Value after
                            curLogFormat |= analyzeGPGGA(sNmea, gpsRec);

                            if (!passToFindFieldsActivatedInLog) {
                                if(curLogFormat!=0) {
                                    recCount++;
                                    gpsRec.recCount=recCount;
                                    finalizeRecord(gpsFile, gpsRec, curLogFormat);
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
        if (curLogFormat != 0) { // Should add time
            // or
            // position change
            // condition.
            gpsFile.writeRecord(gpsRec);
        }
    }

    private int analyzeGPGGA(String[] sNmea, GPSRecord gpsRec) {
        int logFormat = 0;
        if (sNmea[0].equals("GPGGA") && (sNmea.length == 12)) {

            try {
                // Time only!
                gpsRec.utc = Convert.toInt(sNmea[1].substring(0, 2)) * 3600
                        + Convert.toInt(sNmea[1].substring(2, 4)) * 60
                        + Convert.toInt(sNmea[1].substring(4, 6));
                logFormat |= (1 << BT747Constants.FMT_UTC_IDX);
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                // GPSRecord gps = new GPSRecord();
                gpsRec.latitude = (Convert.toDouble(sNmea[2].substring(0, 2)) + Convert
                        .toDouble(sNmea[2].substring(2)) / 60)
                        * (sNmea[3].equals("N") ? 1 : -1);
                logFormat |= (1 << BT747Constants.FMT_LATITUDE_IDX);

            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                gpsRec.longitude = (Convert.toDouble(sNmea[4].substring(0, 3)) + Convert
                        .toDouble(sNmea[4].substring(3)) / 60)
                        * (sNmea[5].equals("E") ? 1 : -1);
                logFormat |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                gpsRec.height = Convert.toFloat(sNmea[8]);
                logFormat |= (1 << BT747Constants.FMT_HEIGHT_IDX);
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                gpsRec.geoid = Convert.toFloat(sNmea[10]);
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
