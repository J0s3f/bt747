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
package gps.log.in;

import gps.BT747Constants;
import gps.convert.Conv;
import gps.log.GPSRecord;

import bt747.model.Model;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747StringTokenizer;

/**
 * This class is used to convert the binary log to a new format. Basically
 * this class interprets the log and creates a {@link GPSRecord}. The
 * {@link GPSRecord} is then sent to the {@link GPSFileConverterInterface}
 * class object to write it to the output.
 * 
 * @author Mario De Weerd
 */
public final class NMEALogConvert extends GPSLogConvertInterface {
    private static final int EOL = 0x0D;
    private static final int CR = 0x0A;

    private int logFormat;
    protected boolean passToFindFieldsActivatedInLog = false;
    protected int activeFileFields = 0;

    /**
     * The size of the file read buffer.
     */
    private static final int BUF_SIZE = 0x800;

    /**
     * Convert the input file set using other methods towards gpsFile. ({@link #toGPSFile(String, GPSFileConverterInterface, int)}
     * is one of them.
     * 
     * @param gpsFile
     *                The object representing the output format.
     * @return {@link BT747Constants#NO_ERROR} if no error (0)
     * @see gps.log.in.GPSLogConvertInterface#parseFile(gps.log.out.GPSFileConverterInterface)
     */
    public final int parseFile(final Object file,
            final GPSFileConverterInterface gpsFile) {
        final WindowedFile mFile = (WindowedFile) file;
        GPSRecord gpsRec = GPSRecord.getLogFormatRecord(0);
        byte[] bytes;
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
            } catch (final Exception e) {
                Generic.debug("getSize", e);
                // TODO: handle exception
                fileSize = 0;
            }

            while (!stop && (nextAddrToRead < fileSize)) {
                /*************************************************************
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

                /*************************************************************
                 * Not reading header - reading data.
                 */
                try {
                    bytes = mFile.fillBuffer(nextAddrToRead);
                } catch (final Exception e) {
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

                /*************************************************************
                 * Interpret the data read in the Buffer as long as the
                 * records are complete
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
                        final StringBuffer s = new StringBuffer(eolPos
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
                        } catch (final Exception e) {
                            Generic.debug("eolPos " + eolPos, e);
                        }

                        checkSum ^= Conv.hex2Int(checkStr);

                        final BT747StringTokenizer fields = Interface
                                .getStringTokenizerInstance(s.toString(), ',');
                        offsetInBuffer = eolPos;
                        for (; (offsetInBuffer < sizeToRead)
                                && ((bytes[offsetInBuffer] == CR) || (bytes[offsetInBuffer] == EOL)); offsetInBuffer++) {
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

                            final GPSRecord gpsNewRec = GPSRecord
                                    .getLogFormatRecord(0); // Value
                            // after
                            int newLogFormat;
                            newLogFormat = CommonIn.analyzeNMEA(sNmea,
                                    gpsNewRec);

                            // Determine if this record belongs to the record
                            // that is ongoing or if it is new.

                            if (((newLogFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0)
                                    && ((curLogFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0)) {
                                // We have a previous and a new log format
                                final int oldClockTime = gpsRec.utc
                                        % (24 * 3600);
                                final int oldDateTime = (gpsRec.utc - oldClockTime)
                                        / (24 * 3600);
                                final int newDateTime = gpsNewRec.utc
                                        / (24 * 3600);
                                if (((oldClockTime != 0) && (oldClockTime != gpsNewRec.utc
                                        % (24 * 3600)))
                                        || ((oldDateTime != 0)
                                                && (newDateTime != 0) && (oldDateTime != newDateTime))
                                        || (gpsRec.milisecond != gpsNewRec.milisecond)) {
                                    // New data is for different time/date -
                                    // write it.
                                    gpsRec.recCount = ++recCount;
                                    finalizeRecord(gpsFile, gpsRec,
                                            curLogFormat);
                                    gpsRec = gpsNewRec;
                                    curLogFormat = newLogFormat;
                                } else {
                                    curLogFormat |= CommonIn.analyzeNMEA(
                                            sNmea, gpsRec);
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

        } catch (final Exception e) {
            Generic.debug("", e);
        }
        return BT747Constants.NO_ERROR;
    }

    private void finalizeRecord(final GPSFileConverterInterface gpsFile,
            final GPSRecord r, final int curLogFormat) {
        CommonIn.convertHeight(r, factorConversionWGS84ToMSL, curLogFormat);

        if (curLogFormat != logFormat) {
            updateLogFormat(gpsFile, curLogFormat);
        }
        if (r.rcr == 0) {
            r.rcr = 1; // Suppose time (for filter)
        }
        // if (valid) {
        if (r.valid == 0) {
            r.valid = BT747Constants.VALID_SPS_MASK;
        }

        if ((curLogFormat != 0) && !passToFindFieldsActivatedInLog) { // Should
            // add time
            // or
            // position change
            // condition.
            gpsFile.addLogRecord(r);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#getFileObject()
     */
    protected Object getFileObject(String fileName, int card) {
        WindowedFile mFile = null;
        if (File.isAvailable()) {
            try {
                mFile = new WindowedFile(fileName, File.READ_ONLY, card);
                mFile.setBufferSize(BUF_SIZE);
                errorInfo = fileName + "|" + mFile.getLastError();
            } catch (final Exception e) {
                Generic.debug("Error during initial open", e);
            }
            if ((mFile == null) || !mFile.isOpen()) {
                errorInfo = fileName;
                if (mFile != null) {
                    errorInfo += "|" + mFile.getLastError();
                }
                error = BT747Constants.ERROR_COULD_NOT_OPEN;
                mFile = null;
            }
        }
        return mFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#closeFileObject(java.lang.Object)
     */
    protected void closeFileObject(Object o) {
        ((WindowedFile) o).close();
    }

    private int error;

    public final int toGPSFile(final String fileName,
            final GPSFileConverterInterface gpsFile, final int card) {
        Object mFile;
        error = BT747Constants.NO_ERROR;
        try {
            mFile = getFileObject(fileName, card);
            if (mFile != null) {
                passToFindFieldsActivatedInLog = gpsFile
                        .needPassToFindFieldsActivatedInLog();
                if (passToFindFieldsActivatedInLog) {
                    activeFileFields = 0;
                    error = parseFile(mFile, gpsFile);
                    gpsFile.setActiveFileFields(GPSRecord
                            .getLogFormatRecord(activeFileFields));
                }
                passToFindFieldsActivatedInLog = false;
                if (error == BT747Constants.NO_ERROR) {
                    do {
                        error = parseFile(mFile, gpsFile);
                    } while (gpsFile.nextPass());
                }
                gpsFile.finaliseFile();
                closeFileObject(mFile);
            }
        } catch (final Exception e) {
            Generic.debug("", e);
            // TODO: handle exception
        }
        return error;
    }

    private void updateLogFormat(final GPSFileConverterInterface gpsFile,
            final int newLogFormat) {
        logFormat = newLogFormat;
        activeFileFields |= logFormat;
        if (!passToFindFieldsActivatedInLog) {
            gpsFile
                    .writeLogFmtHeader(GPSRecord
                            .getLogFormatRecord(logFormat));
        }
    }
    
    /* (non-Javadoc)
     * @see gps.log.in.GPSLogConvertInterface#getType()
     */
    public int getType() {
        return Model.NMEA_LOGTYPE;
    }


}
