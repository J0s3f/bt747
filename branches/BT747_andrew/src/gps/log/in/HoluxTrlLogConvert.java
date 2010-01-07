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
import gps.log.GPSRecord;

import bt747.model.Model;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747RAFile;

/**
 * This class is used to convert the binary log to a new format. Basically
 * this class interprets the log and creates a {@link GPSRecord}. The
 * {@link GPSRecord} is then sent to the {@link GPSFileConverterInterface}
 * class object to write it to the output.
 * 
 * @author Mario De Weerd
 */
public final class HoluxTrlLogConvert extends GPSLogConvertInterface {
    private int recordSize;
    private final static int logFormat = (1 << BT747Constants.FMT_UTC_IDX)
            | (1 << BT747Constants.FMT_LATITUDE_IDX)
            | (1 << BT747Constants.FMT_LONGITUDE_IDX)
            | (1 << BT747Constants.FMT_HEIGHT_IDX);
    protected boolean passToFindFieldsActivatedInLog = false;
    protected final int activeFileFields = HoluxTrlLogConvert.logFormat;

    public final int parseFile(final Object file,
            final GPSFileConverterInterface gpsFile) {
        try {
            final BT747RAFile inFile = (BT747RAFile) file;
            GPSRecord r = GPSRecord.getLogFormatRecord(0);
            final int C_BUF_SIZE = 0x800;
            final byte[] bytes = new byte[C_BUF_SIZE];
            int sizeToRead;
            int nextAddrToRead;
            int recCount;
            int fileSize;

            if (!passToFindFieldsActivatedInLog) {
                gpsFile.writeLogFmtHeader(GPSRecord
                        .getLogFormatRecord(HoluxTrlLogConvert.logFormat));
            }

            recordSize = 15;

            recCount = 0;
            nextAddrToRead = 0;
            fileSize = inFile.getSize();
            while (!stop && ((nextAddrToRead + recordSize + 1) < fileSize)) {
                sizeToRead = C_BUF_SIZE;
                if ((sizeToRead + nextAddrToRead) > fileSize) {
                    sizeToRead = (fileSize - nextAddrToRead);
                }

                /* Read the bytes from the file */
                int readResult;
                int offsetInBuffer = 0;

                inFile.setPos(nextAddrToRead);

                /*************************************************************
                 * Not reading header - reading data.
                 */
                readResult = inFile.readBytes(bytes, 0, sizeToRead);
                if (readResult != sizeToRead) {
                    errorInfo = inFile.getPath() + "|"
                            + inFile.getLastError();
                    return BT747Constants.ERROR_READING_FILE;
                }
                nextAddrToRead += sizeToRead;

                /*************************************************************
                 * Interpret the data read in the Buffer as long as the
                 * records are complete
                 */
                // A block of bytes has been read, read the records
                while (sizeToRead > (offsetInBuffer + recordSize)) {
                    // As long as record may fit in data still to read.
                    int indexInBuffer = offsetInBuffer;
                    int checkSum = 0;

                    while ((indexInBuffer < (recordSize + offsetInBuffer))
                            && (indexInBuffer < (sizeToRead - 1))) {
                        checkSum ^= bytes[indexInBuffer++];
                    }

                    indexInBuffer += 1;

                    int recIdx = offsetInBuffer;
                    offsetInBuffer = indexInBuffer;

                    recCount++;

                    if (((checkSum & 0xFF) == (0xFF & bytes[indexInBuffer - 1]))) {
                        /*****************************************************
                         * Get all the information in the record.
                         */
                        r.recCount = recCount;
                        if (!passToFindFieldsActivatedInLog) {
                            r.valid = 0xFFFF;
                            r.rcr = 0x0001; // For filter
                            // Only interpret fiels if not looking for
                            // logFormat
                            // changes only
                            r.utc = (0xFF & bytes[recIdx++]) << 0
                                    | (0xFF & bytes[recIdx++]) << 8
                                    | (0xFF & bytes[recIdx++]) << 16
                                    | (0xFF & bytes[recIdx++]) << 24;

                            final int latitude = (0xFF & bytes[recIdx++]) << 0
                                    | (0xFF & bytes[recIdx++]) << 8
                                    | (0xFF & bytes[recIdx++]) << 16
                                    | (0xFF & bytes[recIdx++]) << 24;
                            r.latitude = JavaLibBridge.toFloatBitwise(latitude);

                            final int longitude = (0xFF & bytes[recIdx++]) << 0
                                    | (0xFF & bytes[recIdx++]) << 8
                                    | (0xFF & bytes[recIdx++]) << 16
                                    | (0xFF & bytes[recIdx++]) << 24;
                            r.longitude = JavaLibBridge.toFloatBitwise(longitude);// *1.0;

                            final int height =

                            (0xFF & bytes[recIdx++]) << 8
                                    | (0xFF & bytes[recIdx++]) << 16
                                    | (0xFF & bytes[recIdx++]) << 24;
                            r.height = JavaLibBridge.toFloatBitwise(height);
                            CommonIn.convertHeight(r,
                                    factorConversionWGS84ToMSL,
                                    HoluxTrlLogConvert.logFormat);
                            gpsFile.addLogRecord(r);
                            r = GPSRecord.getLogFormatRecord(0);
                        }
                    }
                } /* ContinueInBuffer */
                nextAddrToRead -= (sizeToRead - offsetInBuffer);
            } /* nextAddrToRead<fileSize */
        } catch (final Exception e) {
            Generic.debug("", e);
        }
        return BT747Constants.NO_ERROR;
    }

    private int error;

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#getFileObject()
     */
    protected Object getFileObject(final BT747Path path) {
        File inFile = null;

        if (File.isAvailable()) {
            inFile = new File(path, File.READ_ONLY);
            if (!inFile.isOpen()) {
                errorInfo = path + "|" + inFile.getLastError();
                error = BT747Constants.ERROR_COULD_NOT_OPEN;
                inFile = null;
            }
        }
        return inFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#closeFileObject(java.lang.Object)
     */
    protected void closeFileObject(final Object o) {
        ((File) o).close();
    }

    public final int toGPSFile(final BT747Path path,
            final GPSFileConverterInterface gpsFile) {
        Object inFile;
        error = BT747Constants.NO_ERROR;

        try {
            inFile = getFileObject(path);
            if (inFile != null) {
                passToFindFieldsActivatedInLog = gpsFile
                        .needPassToFindFieldsActivatedInLog();
                if (passToFindFieldsActivatedInLog) {
                    gpsFile.setActiveFileFields(GPSRecord
                            .getLogFormatRecord(activeFileFields));
                }
                passToFindFieldsActivatedInLog = false;
                if (error == BT747Constants.NO_ERROR) {
                    do {
                        error = parseFile(inFile, gpsFile);
                    } while (gpsFile.nextPass());
                }
                gpsFile.finaliseFile();
                closeFileObject(inFile);
            }
        } catch (final Exception e) {
            Generic.debug("", e);
        }
        return error;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#getType()
     */
    public int getType() {
        return Model.TRL_LOGTYPE;
    }

}
