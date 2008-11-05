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
package gps.log.in;

import gps.BT747Constants;
import gps.convert.Conv;
import gps.log.GPSRecord;
import gps.log.out.GPSFile;

import bt747.sys.Convert;
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
public final class BT747LogConvert implements GPSLogConvert {
    private int minRecordSize;
    private int maxRecordSize;
    private int logFormat;
    private WindowedFile mFile = null;
    private long timeOffsetSeconds = 0;
    protected boolean passToFindFieldsActivatedInLog = false;
    protected int activeFileFields = 0;
    private boolean isConvertWGL84ToMSL = false; // If true,remove geoid
    // difference from
    // height

    private int satIdxOffset;
    private int satRecSize;
    private boolean holux = false;
    private boolean nextPointIsWayPt = false;

    private int badrecord_count = 0;

    private void updateLogFormat(final GPSFile gpsFile, final int newLogFormat) {
        int[] result;
        logFormat = newLogFormat;
        activeFileFields |= logFormat;
        if (!passToFindFieldsActivatedInLog) {
            gpsFile.writeLogFmtHeader(GPSRecord.getLogFormatRecord(logFormat));
        }

        minRecordSize = BT747Constants.logRecordMinSize(logFormat, holux);
        maxRecordSize = BT747Constants.logRecordMaxSize(logFormat, holux);
        result = BT747Constants.logRecordSatOffsetAndSize(logFormat, holux);
        satIdxOffset = result[0];
        satRecSize = result[1];
    }

    /**
     * The size of the file read buffer
     */
    private static final int BUF_SIZE = 0x800;

    private boolean stop = false;

    public final void stopConversion() {
        stop = true;
    }

    /**
     * Parse the binary input file and convert it.
     * 
     * @return non zero in case of err. The error text can be retrieved using
     *         {@link #getErrorInfo()}.
     * @param gpsFile -
     *            object doing actual write to files
     * 
     */
    public final int parseFile(final GPSFile gpsFile) {
        GPSRecord r = new GPSRecord();
        byte[] bytes;
        int sizeToRead;
        int nextAddrToRead;
        int recCount;
        int fileSize;
        int satCntIdx;
        int satcnt;

        recCount = 0;
        logFormat = 0;
        nextAddrToRead = 0;
        nextPointIsWayPt = false;
        badrecord_count = 0;
        try {
            fileSize = mFile.getSize();
        } catch (Exception e) {
            Generic.debug("getSize", e);
            // TODO: handle exception
            fileSize = 0;
        }
        while (!stop && nextAddrToRead < fileSize) {
            int okInBuffer = -1; // Last ending position in buffer

            /*******************************************************************
             * Read data from the raw data file into the local buffer.
             */
            // Determine size to read
            if ((nextAddrToRead & 0xFFFF) < 0x200) {
                // Read the header

                nextAddrToRead = (nextAddrToRead & 0xFFFF0000);
            }
            int endOfBlock = (nextAddrToRead & 0xFFFF0000) | 0xFFFF;
            sizeToRead = endOfBlock + 1 - nextAddrToRead;
            if (sizeToRead > BUF_SIZE) {
                sizeToRead = BUF_SIZE;
            }
            if ((sizeToRead + nextAddrToRead) > fileSize) {
                sizeToRead = fileSize - nextAddrToRead;
            }

            boolean continueInBuffer = true;
            int offsetInBuffer = 0;
            int newLogFormat;

            try {
                bytes = mFile.fillBuffer(nextAddrToRead);
            } catch (Exception e) {
                // TODO: Should check sizeToRead vs fill in buffer.
                Generic.debug("Problem reading file", e);
                bytes = null;
            }
            if (bytes == null) {
                Generic.debug("fillBuffer failed", null);

                errorInfo = mFile.getPath() + "|" + mFile.getLastError();
                return BT747Constants.ERROR_READING_FILE;
            }

            if ((nextAddrToRead & 0xFFFF) == 0) {
                /***************************************************************
                 * This is the header. Only 20 bytes are read - just enough to
                 * get the log format.
                 */
                newLogFormat = (0xFF & bytes[2]) << 0 | (0xFF & bytes[3]) << 8
                        | (0xFF & bytes[4]) << 16 | (0xFF & bytes[5]) << 24;
                logMode = (0xFF & bytes[6]) << 0 | (0xFF & bytes[7]) << 8;

                logPeriod = (0xFF & bytes[8]) << 0 | (0xFF & bytes[9]) << 8
                        | (0xFF & bytes[10]) << 16 | (0xFF & bytes[11]) << 24;

                logDistance = (0xFF & bytes[12]) << 0 | (0xFF & bytes[13]) << 8
                        | (0xFF & bytes[14]) << 16 | (0xFF & bytes[15]) << 24;

                logSpeed = (0xFF & bytes[16]) << 0 | (0xFF & bytes[17]) << 8
                        | (0xFF & bytes[18]) << 16 | (0xFF & bytes[19]) << 24;
                rcr_mask = 0;  // Header always defines all conditions.
                if (logPeriod != 0) {
                    rcr_mask |= BT747Constants.RCR_TIME_MASK;
                } else {
                    rcr_mask &= ~BT747Constants.RCR_TIME_MASK;
                }
                if (logDistance != 0) {
                    rcr_mask |= BT747Constants.RCR_DISTANCE_MASK;
                } else {
                    rcr_mask &= ~BT747Constants.RCR_DISTANCE_MASK;
                }
                if (logSpeed != 0) {
                    rcr_mask |= BT747Constants.RCR_SPEED_MASK;
                } else {
                    rcr_mask &= ~BT747Constants.RCR_SPEED_MASK;
                }

                if (newLogFormat == 0xFFFFFFFF) {
                    // TODO: Treat error
                    if (logFormat == 0) {
                        newLogFormat = 0x8000001D; // Supposing holux M-241
                    } else {
                        newLogFormat = logFormat;
                    }
                }
                if (newLogFormat != logFormat) {
                    updateLogFormat(gpsFile, newLogFormat);
                }

                nextAddrToRead += 0x200;
                continueInBuffer = false;
            } else {
                nextAddrToRead += sizeToRead;
            }
            /*******************************************************************
             * Data read from file into local buffer
             ******************************************************************/

            /*******************************************************************
             * Interpret the data read in the local buffer
             */
            while (continueInBuffer) {
                boolean lookForRecord = true;

                while (lookForRecord && (sizeToRead - 16 > offsetInBuffer) // Enough
                // bytes
                // in
                // buffer
                ) {
                    int nbrBytes;
                    nbrBytes = getSpecialRecord(bytes, offsetInBuffer, gpsFile);
                    lookForRecord = (nbrBytes != 0);
                    offsetInBuffer += nbrBytes;
                }

                /***************************************************************
                 * Look for a record
                 */
                boolean foundRecord = false;
                boolean foundAnyRecord = false;
                int satRecords;

                if ((sizeToRead > offsetInBuffer + minRecordSize
                        + (holux ? 1 : 2)) // Enough bytes in buffer
                ) { // As long as record may fit in data still to read.
                    int indexInBuffer = offsetInBuffer;
                    int checkSum = 0;
                    int allFF = 0xFF; // If 0xFF, all bytes are FF.
                    foundRecord = false;
                    satcnt = 0;
                    satCntIdx = 0;
                    satRecords = 0;

                    /***********************************************************
                     * Get some satellite record information.
                     */
                    if ((logFormat & (1 << BT747Constants.FMT_SID_IDX)) != 0) {
                        satCntIdx = offsetInBuffer + satIdxOffset;
                        satcnt = (0xFF & bytes[satCntIdx + 2]) << 0
                                | (0xFF & bytes[satCntIdx + 3]) << 8;
                        if ((satcnt > 32) || (satcnt < 0)) {
                            // TODO: handle error [but ok when end of block or
                            // end of file]
                            satcnt = 32;
                        }
                        if (satcnt != 0) {
                            satRecords = satcnt * satRecSize - 4;
                        }
                    }

                    /***********************************************************
                     * Skip minimum number of bytes in a record.
                     */
                    if ((minRecordSize + satRecords + offsetInBuffer) <= (sizeToRead - 2)) {
                        // Record fits in buffer
                        int cnt;
                        cnt = minRecordSize + satRecords + offsetInBuffer
                                - indexInBuffer;
                        while (cnt-- > 0) {
                            allFF &= bytes[indexInBuffer];
                            checkSum ^= bytes[indexInBuffer++];
                        }

                        if ((allFF != 0xFF)
                                && ((!holux && ((bytes[indexInBuffer] == '*') && ((checkSum & 0xFF) == (0xFF & bytes[indexInBuffer + 1])))) || (holux && ((checkSum & 0xFF) == (0xFF & bytes[indexInBuffer]))))) {
                            if (!holux) {
                                indexInBuffer += 2; // Point just past end ('*'
                                // and checksum).
                            } else {
                                indexInBuffer += 1;
                            }

                            int recIdx = offsetInBuffer;

                            offsetInBuffer = indexInBuffer;
                            // okInBuffer = indexInBuffer;
                            // foundRecord = true;

                            int rcrIdx; // Offset to first field after sat data.
                            if (!holux) {
                                rcrIdx = offsetInBuffer
                                        - 2
                                        - ((((logFormat & (1 << BT747Constants.FMT_DISTANCE_IDX)) != 0) ? BT747Constants.logFmtByteSizes[BT747Constants.FMT_DISTANCE_IDX]
                                                : 0)
                                                + (((logFormat & (1 << BT747Constants.FMT_MILLISECOND_IDX)) != 0) ? BT747Constants.logFmtByteSizes[BT747Constants.FMT_MILLISECOND_IDX]
                                                        : 0) + (((logFormat & (1 << BT747Constants.FMT_RCR_IDX)) != 0) ? BT747Constants.logFmtByteSizes[BT747Constants.FMT_RCR_IDX]
                                                : 0));
                            } else {
                                rcrIdx = offsetInBuffer
                                        - 1
                                        - ((((logFormat & (1 << BT747Constants.FMT_DISTANCE_IDX)) != 0) ? BT747Constants.logFmtByteSizesHolux[BT747Constants.FMT_DISTANCE_IDX]
                                                : 0)
                                                + (((logFormat & (1 << BT747Constants.FMT_MILLISECOND_IDX)) != 0) ? BT747Constants.logFmtByteSizesHolux[BT747Constants.FMT_MILLISECOND_IDX]
                                                        : 0) + (((logFormat & (1 << BT747Constants.FMT_RCR_IDX)) != 0) ? BT747Constants.logFmtByteSizesHolux[BT747Constants.FMT_RCR_IDX]
                                                : 0));
                            }

                            recCount++;
                            // System.out.println(recCount);

                            /***************************************************
                             * Get all the information in the record.
                             */
                            r.recCount = recCount;
                            if (passToFindFieldsActivatedInLog) {
                                okInBuffer = indexInBuffer;
                                foundRecord = true;
                            } else {
                                // Only interpret fields if not looking for
                                // logFormat changes only

                                // Generic.debug(Convert.unsigned2hex(nextAddrToRead-sizeToRead+recIdx,
                                // 8)); // record start position
                                // Generic.debug("Offset:"+recIdx+"
                                // "+offsetInBuffer);
                                boolean valid;

                                // Retrieve the record from the file (in
                                // buffer).
                                valid = getRecord(bytes, r, recIdx,
                                        rcrIdx, satcnt);
                                if (valid) {
                                    gpsFile.writeRecord(r);
                                    okInBuffer = offsetInBuffer;
                                    foundRecord = true;
                                } else {
                                    Generic.debug("Bad record @"
                                            + r.recCount
                                            + "("
                                            + Convert.unsigned2hex(
                                                    nextAddrToRead - sizeToRead
                                                            + recIdx, 8) + ")",
                                            null);
                                    // Recover ...
                                    recCount--;
                                    foundRecord = false;
                                    offsetInBuffer = recIdx;
                                    badrecord_count++;
                                }
                            }
                            /***************************************************
                             * Information from record retrieved
                             **************************************************/
                            foundAnyRecord |= foundRecord;
                        } else {
                            // Problem in checksum, data format, ... .
                            // Skip FF
                            if (((bytes[indexInBuffer] & 0xFF) == 0xFF)
                                    && (holux || ((0xFF & bytes[indexInBuffer + 1]) == 0xFF))) {
                                if (!holux) {
                                    indexInBuffer += 2; // Point just past
                                    // end ('*'
                                } else {
                                    indexInBuffer += 1;
                                }
                                offsetInBuffer = indexInBuffer;
                                okInBuffer = indexInBuffer;
                                foundAnyRecord = true; // Fake to avoid extra
                                // byte skip.
                                // Generic.debug(indexInBuffer +"skip ff",null);
                            } else {
                                badrecord_count++;
                            }
                        }
                    } else {
                        continueInBuffer = false;
                    }
                    lookForRecord = foundRecord;
                } // End if (or while previously) for possible good record.

                if (!foundAnyRecord && continueInBuffer) {
                    if (sizeToRead > offsetInBuffer + maxRecordSize
                            + (holux ? 1 : 2)) { // TODO: recover when 16
                        // bytes available too.
                        // Did not find any record - expected at least one.
                        // Try to recover.
                        offsetInBuffer++;
                    } else {
                        // There is not enough data in the buffer, we'll need to
                        // get some more.
                        continueInBuffer = false;
                    }
                }
            } /* ContinueInBuffer */
            if (okInBuffer > 0) {
                nextAddrToRead -= (sizeToRead - okInBuffer);
            }
        } /* nextAddrToRead<fileSize */
        return BT747Constants.NO_ERROR;
    }

    public final void setTimeOffset(final long offset) {
        timeOffsetSeconds = offset;
    }

    public final void setConvertWGS84ToMSL(final boolean b) {
        isConvertWGL84ToMSL = b;
    }

    private String errorInfo;

    public final String getErrorInfo() {
        return errorInfo;
    }

    public final int toGPSFile(final String fileName, final GPSFile gpsFile,
            final int Card) {
        int error = BT747Constants.NO_ERROR;
        stop = false;
        if (File.isAvailable()) {
            try {
                mFile = new WindowedFile(fileName, File.READ_ONLY, Card);
                mFile.setBufferSize(BUF_SIZE);
                errorInfo = fileName + "|" + mFile.getLastError();
            } catch (Exception e) {
                Generic.debug("Error during initial open", e);
            }
            if (mFile == null || !mFile.isOpen()) {
                errorInfo = fileName;
                if (mFile != null) {
                    errorInfo += "|" + mFile.getLastError();
                }
                error = BT747Constants.ERROR_COULD_NOT_OPEN;
                mFile = null;
            } else {
                passToFindFieldsActivatedInLog = gpsFile
                        .needPassToFindFieldsActivatedInLog();
                if (passToFindFieldsActivatedInLog) {
                    activeFileFields = 0;
                    error = parseFile(gpsFile);
                    gpsFile.setActiveFileFields(GPSRecord
                            .getLogFormatRecord(activeFileFields));
                }
                passToFindFieldsActivatedInLog = false;
                if (error == BT747Constants.NO_ERROR) {
                    do {
                        error = parseFile(gpsFile);
                    } while (error == BT747Constants.NO_ERROR
                            && gpsFile.nextPass());
                }
                gpsFile.finaliseFile();
                if (gpsFile.getFilesCreated() == 0) {
                    error = BT747Constants.ERROR_NO_FILES_WERE_CREATED;
                }
            }

            if (mFile != null) {
                try {
                    mFile.close();
                } catch (Exception e) {
                    Generic.debug("close", e);
                    // TODO: handle exception
                }
            }
        }
        Generic.debug("Conversion done", null);
        return error;
    }

    /**
     * @param holux
     *            The holux to set.
     */
    public final void setHolux(final boolean holux) {
        this.holux = holux;
    }

    private int logMode = 0;
    private int rcr_mask = 0; // Default RCR based on log settings
    private int logSpeed = 0;
    private int logDistance = 0;
    private int logPeriod = 0;

    /**
     * Tries to find a special record at the indicated offset.
     * 
     * @return int / number of bytes found
     */
    private final int getSpecialRecord(final byte[] bytes,
            final int offsetInBuffer, final GPSFile gpsFile) {
        int newLogFormat;
        int nbrBytesDone = 0;
        if (((0xFF & bytes[offsetInBuffer + 0]) == 0xAA)
                && ((0xFF & bytes[offsetInBuffer + 1]) == 0xAA)
                && ((0xFF & bytes[offsetInBuffer + 2]) == 0xAA)
                && ((0xFF & bytes[offsetInBuffer + 3]) == 0xAA)
                && ((0xFF & bytes[offsetInBuffer + 4]) == 0xAA)
                && ((0xFF & bytes[offsetInBuffer + 5]) == 0xAA)
                && ((0xFF & bytes[offsetInBuffer + 6]) == 0xAA)
                && ((0xFF & bytes[offsetInBuffer + 12]) == 0xBB)
                && ((0xFF & bytes[offsetInBuffer + 13]) == 0xBB)
                && ((0xFF & bytes[offsetInBuffer + 14]) == 0xBB)
                && ((0xFF & bytes[offsetInBuffer + 15]) == 0xBB)) {
            int value = (0xFF & bytes[offsetInBuffer + 8]) << 0
                    | (0xFF & bytes[offsetInBuffer + 9]) << 8
                    | (0xFF & bytes[offsetInBuffer + 10]) << 16
                    | (0xFF & bytes[offsetInBuffer + 11]) << 24;
            // There is a special operation here
            switch (0xFF & bytes[offsetInBuffer + 7]) {
            case 0x02: // logBitMaskChange
                newLogFormat = value;
                if (newLogFormat != logFormat) {
                    updateLogFormat(gpsFile, newLogFormat);
                }
                // bt747.sys.Generic.debug("Log format set to
                // :"+Convert.unsigned2hex(value, 8));
                break;
            case 0x03: // log Period change
                logPeriod = value;
                if (value != 0) {
                    rcr_mask |= BT747Constants.RCR_TIME_MASK;
                } else {
                    rcr_mask &= ~BT747Constants.RCR_TIME_MASK;
                }
                // bt747.sys.Generic.debug("Log period set to :"+value);
                break;
            case 0x04: // log distance change
                logDistance = value;
                if (value != 0) {
                    rcr_mask |= BT747Constants.RCR_DISTANCE_MASK;
                } else {
                    rcr_mask &= ~BT747Constants.RCR_DISTANCE_MASK;
                }
                // bt747.sys.Generic.debug("Log distance set to :"+value);
                break;
            case 0x05: // log speed change
                logSpeed = value;
                if (value != 0) {
                    rcr_mask |= BT747Constants.RCR_SPEED_MASK;
                } else {
                    rcr_mask &= ~BT747Constants.RCR_SPEED_MASK;
                }
                // bt747.sys.Generic.debug("Log speed set to :"+value);
                break;
            case 0x06: // value: 0x0106= logger on 0x0107= logger off 0x104=??
                logMode = value;
                // bt747.sys.Generic.debug("Logger off :"+value);
                break;
            case 0x07: // value: 0x0106= logger on 0x0107= logger off 0x104=??
                logMode = value;
                // bt747.sys.Generic.debug("Logger off :"+value);
                break;
            default:
                break; // Added to set SW breakpoint to discover other records.

            }
            // No data: on/off
            nbrBytesDone += 16;
        } else if (((0xFF & bytes[offsetInBuffer + 0]) == 'H')
                && ((0xFF & bytes[offsetInBuffer + 1]) == 'O')
                && ((0xFF & bytes[offsetInBuffer + 2]) == 'L')
                && ((0xFF & bytes[offsetInBuffer + 3]) == 'U')
                && ((0xFF & bytes[offsetInBuffer + 4]) == 'X')) {

            // No data: on/off
            if (!holux) {
                holux = true; // currently set like this
                updateLogFormat(gpsFile, logFormat);
            }
            nbrBytesDone += 16;
            if (// ((0xFF&bytes[offsetInBuffer+5])=='G')
            // &&((0xFF&bytes[offsetInBuffer+6])=='R')
            // &&((0xFF&bytes[offsetInBuffer+7])=='2')
            // &&((0xFF&bytes[offsetInBuffer+8])=='4')
            // &&((0xFF&bytes[offsetInBuffer+9])=='1')
            // &&
            ((0xFF & bytes[offsetInBuffer + 10]) == 'W')
                    && ((0xFF & bytes[offsetInBuffer + 11]) == 'A')
                    && ((0xFF & bytes[offsetInBuffer + 12]) == 'Y')
                    && ((0xFF & bytes[offsetInBuffer + 13]) == 'P')
                    && ((0xFF & bytes[offsetInBuffer + 14]) == 'N')
                    && ((0xFF & bytes[offsetInBuffer + 15]) == 'T')) {
                nextPointIsWayPt = true;
                // Generic.debug("Holux Waypoint");
            }
        }
        return nbrBytesDone;
    }

    /**
     * Tries to find a normal record at the indicated offset.
     * 
     * @return true if success
     */
    private boolean getRecord(
            final byte[] bytes, // The data string
            final GPSRecord r, final int startIdx, final int rcrIdx,
            final int satcnt) {
        int recIdx;
        boolean valid;
        int satidx;
        int idx;
        recIdx = startIdx;
        valid = true;

        if ((logFormat & (1 << BT747Constants.FMT_UTC_IDX)) != 0) {
            r.logPeriod = logPeriod;
            r.logDistance = logDistance;
            r.logSpeed = logSpeed;
            r.utc = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8
                    | (0xFF & bytes[recIdx++]) << 16
                    | (0xFF & bytes[recIdx++]) << 24;
            if ((r.utc & 0x80000000) != 0) {
                Generic.debug("Invalid time:" + r.utc);
                valid = false;
            }
            r.utc += timeOffsetSeconds;
        } else {
            r.utc = 1000; // Value after earliest date
        }
        if ((logFormat & (1 << BT747Constants.FMT_VALID_IDX)) != 0) {
            r.valid = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8;
        } else {
            r.valid = 0xFFFF;
        }
        if ((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0) {
            if (!holux) {
                long latitude = (0xFFL & bytes[recIdx++]) << 0
                        | (0xFFL & bytes[recIdx++]) << 8
                        | (0xFFL & bytes[recIdx++]) << 16
                        | (0xFFL & bytes[recIdx++]) << 24
                        | (0xFFL & bytes[recIdx++]) << 32
                        | (0xFFL & bytes[recIdx++]) << 40
                        | (0xFFL & bytes[recIdx++]) << 48
                        | (0xFFL & bytes[recIdx++]) << 56;
                r.latitude = Convert.longBitsToDouble(latitude);
            } else {
                int latitude = (0xFF & bytes[recIdx++]) << 0
                        | (0xFF & bytes[recIdx++]) << 8
                        | (0xFF & bytes[recIdx++]) << 16
                        | (0xFF & bytes[recIdx++]) << 24;
                r.latitude = Convert.toFloatBitwise(latitude);
            }
            if (r.latitude > 90.00 || r.latitude < -90.00) {
                Generic.debug("Invalid latitude:" + r.latitude);
                valid = false;
            }
        }
        if ((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0) {
            if (!holux) {
                long longitude = (0xFFL & bytes[recIdx++]) << 0
                        | (0xFFL & bytes[recIdx++]) << 8
                        | (0xFFL & bytes[recIdx++]) << 16
                        | (0xFFL & bytes[recIdx++]) << 24
                        | (0xFFL & bytes[recIdx++]) << 32
                        | (0xFFL & bytes[recIdx++]) << 40
                        | (0xFFL & bytes[recIdx++]) << 48
                        | (0xFFL & bytes[recIdx++]) << 56;
                r.longitude = Convert.longBitsToDouble(longitude);
            } else {
                int longitude = (0xFF & bytes[recIdx++]) << 0
                        | (0xFF & bytes[recIdx++]) << 8
                        | (0xFF & bytes[recIdx++]) << 16
                        | (0xFF & bytes[recIdx++]) << 24;
                r.longitude = Convert.toFloatBitwise(longitude);// *1.0;
            }
            if (r.longitude > 180.00 || r.latitude < -180.00) {
                Generic.debug("Invalid longitude:" + r.height);
                valid = false;
            }
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEIGHT_IDX)) != 0) {
            if (!holux) {
                int height = (0xFF & bytes[recIdx++]) << 0
                        | (0xFF & bytes[recIdx++]) << 8
                        | (0xFF & bytes[recIdx++]) << 16
                        | (0xFF & bytes[recIdx++]) << 24;
                r.height = Convert.toFloatBitwise(height);
            } else {
                int height =

                (0xFF & bytes[recIdx++]) << 8 | (0xFF & bytes[recIdx++]) << 16
                        | (0xFF & bytes[recIdx++]) << 24;
                r.height = Convert.toFloatBitwise(height);
            }
            if (isConvertWGL84ToMSL
                    && ((logFormat & (1 << BT747Constants.FMT_LATITUDE_IDX)) != 0)
                    && ((logFormat & (1 << BT747Constants.FMT_LONGITUDE_IDX)) != 0)
                    && valid) {
                r.height -= Conv.wgs84Separation(r.latitude,
                        r.longitude);
            }
            if (((r.valid & 0x0001) != 1) // record has a fix
                    && (r.height < -10000. || r.height > 10000.)) {
                Generic.debug("Invalid height:" + r.height);
                valid = false;
            }
        }
        if ((logFormat & (1 << BT747Constants.FMT_SPEED_IDX)) != 0) {
            int speed = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8
                    | (0xFF & bytes[recIdx++]) << 16
                    | (0xFF & bytes[recIdx++]) << 24;
            r.speed = Convert.toFloatBitwise(speed);
            if (r.speed < -10.) {
                Generic.debug("Invalid speed:" + r.speed);
                valid = false;
            }
        }
        if ((logFormat & (1 << BT747Constants.FMT_HEADING_IDX)) != 0) {
            int heading = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8
                    | (0xFF & bytes[recIdx++]) << 16
                    | (0xFF & bytes[recIdx++]) << 24;
            r.heading = Convert.toFloatBitwise(heading);
        }
        if ((logFormat & (1 << BT747Constants.FMT_DSTA_IDX)) != 0) {
            r.dsta = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DAGE_IDX)) != 0) {
            r.dage = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8
                    | (0xFF & bytes[recIdx++]) << 16
                    | (0xFF & bytes[recIdx++]) << 24;
        }
        if ((logFormat & (1 << BT747Constants.FMT_PDOP_IDX)) != 0) {
            r.pdop = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8;
        }
        if ((logFormat & (1 << BT747Constants.FMT_HDOP_IDX)) != 0) {
            r.hdop = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8;
        }
        if ((logFormat & (1 << BT747Constants.FMT_VDOP_IDX)) != 0) {
            r.vdop = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8;
        }
        if ((logFormat & (1 << BT747Constants.FMT_NSAT_IDX)) != 0) {
            r.nsat = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8;
        }
        idx = 0;
        satidx = 0;
        if (rcrIdx - recIdx > 0) {
            idx = (0xFF & bytes[recIdx + 2]) << 0
                    | (0xFF & bytes[recIdx + 3]) << 8;
            r.sid = new int[idx];
            r.sidinuse = new boolean[idx];
            r.ele = new int[idx];
            r.azi = new int[idx];
            r.snr = new int[idx];
            if (idx == 0) {
                recIdx += 4;
            }
        }
        if (satcnt == idx) {
            while (idx-- > 0) {
                if ((logFormat & (1 << BT747Constants.FMT_SID_IDX)) != 0) {
                    r.sid[satidx] = (0xFF & bytes[recIdx++]) << 0;
                    r.sidinuse[satidx] = ((0xFF & bytes[recIdx++]) << 0) != 0;
                    // if(false) {
                    // // satcnt is not used - skipping with iffalse)
                    // satcnt=
                    // (0xFF&bytes[recIdx++])<<0
                    // |(0xFF&bytes[recIdx++])<<8;
                    // } else {
                    recIdx += 2;
                    // }
                }
                if ((logFormat & (1 << BT747Constants.FMT_ELEVATION_IDX)) != 0) {
                    r.ele[satidx] = (0xFF & bytes[recIdx++]) << 0
                            | (0xFF & bytes[recIdx++]) << 8;
                }
                if ((logFormat & (1 << BT747Constants.FMT_AZIMUTH_IDX)) != 0) {
                    r.azi[satidx] = (0xFF & bytes[recIdx++]) << 0
                            | (0xFF & bytes[recIdx++]) << 8;
                }
                if ((logFormat & (1 << BT747Constants.FMT_SNR_IDX)) != 0) {
                    r.snr[satidx] = (0xFF & bytes[recIdx++]) << 0
                            | (0xFF & bytes[recIdx++]) << 8;
                }
                satidx++;
            }
        } else {
            Generic.debug("Problem in sat decode", null);
        }
        // Generic.debug("Offset1:"+recIdx+" "+rcrIdx);
        if (recIdx != rcrIdx) {
            Generic.debug("Problem in sat decode (end idx)", null);
        }
        recIdx = rcrIdx; // Sat information limit is rcrIdx
        if ((logFormat & (1 << BT747Constants.FMT_RCR_IDX)) != 0) {
            r.rcr = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8;
        } else {
            r.rcr = rcr_mask; // For filter
        }
        if (nextPointIsWayPt) {
            r.rcr |= BT747Constants.RCR_BUTTON_MASK;
            nextPointIsWayPt = false;
        }
        if ((logFormat & (1 << BT747Constants.FMT_MILLISECOND_IDX)) != 0) {
            r.milisecond = (0xFF & bytes[recIdx++]) << 0
                    | (0xFF & bytes[recIdx++]) << 8;
        } else {
            r.milisecond = 0;
        }
        if ((logFormat & (1 << BT747Constants.FMT_DISTANCE_IDX)) != 0) {
            long distance = (0xFFL & bytes[recIdx++]) << 0
                    | (0xFFL & bytes[recIdx++]) << 8
                    | (0xFFL & bytes[recIdx++]) << 16
                    | (0xFFL & bytes[recIdx++]) << 24
                    | (0xFFL & bytes[recIdx++]) << 32
                    | (0xFFL & bytes[recIdx++]) << 40
                    | (0xFFL & bytes[recIdx++]) << 48
                    | (0xFFL & bytes[recIdx++]) << 56;
            r.distance = Convert.longBitsToDouble(distance);
        }

        return valid;

    }
}