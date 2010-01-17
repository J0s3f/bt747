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
import bt747.sys.RAFile;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747RAFile;

/**
 * Conversion of Wonde Proud logs (Phototrackr, ...) This class is used to
 * convert the binary log to a new format. Basically this class interprets the
 * log and creates a {@link GPSRecord}. The {@link GPSRecord} is then sent to
 * the {@link GPSFileConverterInterface} class object to write it to the
 * output.
 * 
 * @author Mario De Weerd
 */
public final class SkytraqLogConvert extends GPSLogConvertInterface {
    private static final int X_FF = 0xFF;
    private int recordSize = 16;
    private int logFormat;
    protected boolean passToFindFieldsActivatedInLog = false;
    protected int activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
            | (1 << BT747Constants.FMT_LATITUDE_IDX)
            | (1 << BT747Constants.FMT_LONGITUDE_IDX)
            | (1 << BT747Constants.FMT_HEIGHT_IDX);

    // static final int ITRACKU_NUMERIX = 0;
    // static final int PHOTOTRACKR = 1;
    // static final int ITRACKU_SIRFIII = 2;

    // private int logType = WPLogConvert.ITRACKU_NUMERIX;

    public SkytraqLogConvert() {
        super();
    }

    public void setLoggerType(final int logType) {
        super.setLoggerType(logType);
        switch (getLoggerType()) {
        case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
        case BT747Constants.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
            activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
                    | (1 << BT747Constants.FMT_LATITUDE_IDX)
                    | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                    | (1 << BT747Constants.FMT_HEIGHT_IDX)
                    | (1 << BT747Constants.FMT_SPEED_IDX);
            recordSize = 16;
            break;
        case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
        default:
            activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
                    | (1 << BT747Constants.FMT_LATITUDE_IDX)
                    | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                    | (1 << BT747Constants.FMT_HEIGHT_IDX);
            recordSize = 16;
            break;
        }
    }

    /**
     * Creates utc time value of Phototrackr specific format.
     * 
     * @param rawTime
     *            Phototracker time representation.
     * @return long type representing utc time.
     */
    public static final int longToUtcTime(final int rawTime) {
        final int seconds = rawTime & 0x3F;
        final int minutes = (rawTime >> 6) & 0x3F;
        final int hour = (rawTime >> 12) & 0x1F;
        final int day = (rawTime >> 17) & 0x1F;
        final int month = (rawTime >> 22) & 0x0F;
        final int year = (rawTime >> 26) & 0x3F;
        final int utc = (JavaLibBridge.getDateInstance(day, month,
                year + 2000)).dateToUTCepoch1970()
                + 3600 * hour + 60 * minutes + seconds;
        return utc;
    }

    public int parseFile(final Object file,
            final GPSFileConverterInterface gpsFile) {
        try {
            final BT747RAFile inFile = (BT747RAFile) file;
            GPSRecord r = GPSRecord.getLogFormatRecord(0);
            final int C_BUF_SIZE = 4096; // block size is 4096
            final byte[] bytes = new byte[C_BUF_SIZE];
            int sizeToRead;
            int nextAddrToRead;
            int recCount;
            int fileSize;

            if (!passToFindFieldsActivatedInLog) {
                gpsFile.writeLogFmtHeader(getLogFormatRecord(logFormat));
            }

            // recordSize = 15;

            recCount = 0;
            logFormat = 0;
            nextAddrToRead = 0;
            fileSize = inFile.getSize();
            while (!stop && (nextAddrToRead + recordSize + 1 < fileSize)) {
                sizeToRead = C_BUF_SIZE;
                if ((sizeToRead + nextAddrToRead) > fileSize) {
                    sizeToRead = fileSize - nextAddrToRead;
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
                while (!stop && offsetInBuffer < sizeToRead) {
                    int recType = 0x7 & (bytes[offsetInBuffer] >> 5);
                    switch (recType) {
                    case 0x3:
                        /* Empty storage. Skip */
                        offsetInBuffer += 2;
                        break;
                    case 0x2: /* Fix full */
                        getFull(bytes, offsetInBuffer);
                        offsetInBuffer += 18;
                        break;
                    case 0x4:
                        /* Fix compact */
                        offsetInBuffer += 8;
                    default:
                        /* Fix full POI. */
                        offsetInBuffer += 18;
                        break;
                    }
                }

                // A block of bytes has been read, read the records
                while (sizeToRead > offsetInBuffer + recordSize) {
                    // As long as record may fit in data still to read.
                    int indexInBuffer = offsetInBuffer;
                    // int checkSum = 0;
                    //
                    // while ((indexInBuffer < recordSize + offsetInBuffer)
                    // && (indexInBuffer < sizeToRead - 1)) {
                    // checkSum ^= bytes[indexInBuffer++];
                    // }
                    //
                    // indexInBuffer += 1;

                    indexInBuffer += recordSize;
                    int recIdx = offsetInBuffer;
                    offsetInBuffer = indexInBuffer;

                    recCount++;

                    if (true) { // (((checkSum & 0xFF) == (0xFF &
                        // bytes[indexInBuffer - 1]))) {
                        /*****************************************************
                         * Get all the information in the record.
                         */
                        r.recCount = recCount;
                        if (!passToFindFieldsActivatedInLog) {

                            // if (( lc( $o{'i'} ) eq lc( 'iTrackU-Nemerix' )
                            // )
                            // or
                            // parse the record, this trick took me one
                            // whole
                            // day...
                            // ($lon, $lat, $year, $month, $day, $hour,
                            // minute, $second, $speed, $tag) =
                            // 
                            // unpack( " V V C C C C C C C C", $_ );
                            // _Longitude_ _Latitude__ YY MM DD HH MM SS
                            // SpdTag
                            // ef f3 b7 00 d1 df 02 03 07 09 1d 06 01 2a 00
                            // ff

                            // elsif (( lc( $o{'i'} ) eq lc( 'PhotoTrackr' ) )
                            // or
                            // ( lc( $o{'i'} ) eq 'p' ) or
                            // ( lc( $o{'i'} ) eq lc( 'iTrackU-SIRFIII' ) ) or
                            // ( lc( $o{'i'} ) eq 's' ) ) {
                            // ($lon, $lat, $date, $altitude, $speed, $tag) =
                            // unpack( " V V V s C C", $_ );
                            // _Longitude_ _Latitude__ ___Date____ _Alt_
                            // SpdTag
                            // bd 49 90 00 09 0c 1d 03 b4 eb a8 1e 3b 00 02
                            // 63

                            int longitude;
                            int latitude;
                            int year;
                            int month;
                            int day;
                            int hour;
                            int minutes;
                            int seconds;
                            int speed;
                            int tag;
                            int altitude;

                            int rawTime;

                            switch (getLoggerType()) {
                            case BT747Constants.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
                            case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
                                // case
                                // BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
                                // NEMERIX
                                // Get information from log file
                                longitude = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 8
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 16
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 24;
                                latitude = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 8
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 16
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 24;
                                rawTime = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 8
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 16
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 24;
                                altitude = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 8;
                                r.height = altitude;
                                CommonIn
                                        .convertHeight(r,
                                                factorConversionWGS84ToMSL,
                                                logFormat);
                                speed = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                tag = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                r.utc = longToUtcTime(rawTime);
                                break;
                            default:
                                // NEMERIX
                                // Get information from log file
                                longitude = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 8
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 16
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 24;
                                latitude = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 8
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 16
                                        | (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 24;
                                year = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                month = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                day = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                hour = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                minutes = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                seconds = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                speed = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                tag = (SkytraqLogConvert.X_FF & bytes[recIdx++]) << 0;
                                r.utc = (JavaLibBridge.getDateInstance(day,
                                        month, year + 2000))
                                        .dateToUTCepoch1970();
                                r.utc += 3600 * hour + 60 * minutes + seconds;
                                break;
                            }

                            if (latitude == 0xFFFFFFFF) {
                                // TODO : cleaner exit
                                return BT747Constants.NO_ERROR;
                            }
                            if ((longitude & 0x80000000) != 0) {
                                longitude = -(longitude & 0x7FFFFFFF);
                            }
                            if ((latitude & 0x80000000) != 0) {
                                latitude = -(latitude & 0x7FFFFFFF);
                            }

                            // Convert information to log record
                            // The next lines explicitly use an integer
                            // division
                            // on longitude to get an integer result!
                            // The objective is to get the first digits
                            // of the number.
                            r.longitude = (((longitude / 1000000)))
                                    + ((longitude % 1000000) / 600000.0);
                            r.latitude = (((latitude / 1000000)))
                                    + ((latitude % 1000000) / 600000.0);
                            r.speed = speed * 1.852f;

                            r.valid = 0xFFFF;
                            r.rcr = 0x0001; // For filter
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

    static private class SkytraqPositionRecord {
        public long tow;
        public int wn;
        public long x;
        public long y;
        public long z;
        public int speed;
        
    };
    private SkytraqPositionRecord getFull(byte[] bytes, int recIdx) {
        final SkytraqPositionRecord r = new SkytraqPositionRecord();
        r.speed = (bytes[recIdx] & 0x3) << 8 + (bytes[recIdx + 1] & SkytraqLogConvert.X_FF);
        r.tow = ((bytes[recIdx + 4] & 0xFFL) << 16)
                & ((bytes[recIdx + 5] & 0xFFL) << 8)
                + ((bytes[recIdx + 2] >> 4) & 0x0F);
        r.wn = (bytes[recIdx + 2] & 0x3) << 8 + (bytes[recIdx + 2] & SkytraqLogConvert.X_FF);
        r.x = ((bytes[recIdx + 6] & 0xFFL) << 8)
                + ((bytes[recIdx + 7] & 0xFFL))
                + ((bytes[recIdx + 8] & 0xFFL) << 24)
                + ((bytes[recIdx + 9] & 0xFFL) << 16);
        r.y = ((bytes[recIdx + 10] & 0xFFL) << 8)
                + ((bytes[recIdx + 11] & 0xFFL))
                + ((bytes[recIdx + 12] & 0xFFL) << 24)
                + ((bytes[recIdx + 3] & 0xFFL) << 16);
        r.z = ((bytes[recIdx + 14] & 0xFFL) << 8)
                + ((bytes[recIdx + 15] & 0xFFL))
                + ((bytes[recIdx + 16] & 0xFFL) << 24)
                + ((bytes[recIdx + 17] & 0xFFL) << 16);
        return r;
    }

    private SkytraqPositionRecord getCompact(SkytraqPositionRecord previous, byte[] bytes, int recIdx) {
        final SkytraqPositionRecord r = new SkytraqPositionRecord();
        r.speed = previous.speed;
        r.tow = previous.tow;
        r.speed = (bytes[recIdx] & 0x3) << 8 + (bytes[recIdx + 1] & SkytraqLogConvert.X_FF);
        r.tow = ((bytes[recIdx + 4] & 0xFFL) << 16)
                & ((bytes[recIdx + 5] & 0xFFL) << 8)
                + ((bytes[recIdx + 2] >> 4) & 0x0F);
        r.wn = (bytes[recIdx + 2] & 0x3) << 8 + (bytes[recIdx + 2] & SkytraqLogConvert.X_FF);
        r.x = ((bytes[recIdx + 6] & 0xFFL) << 8)
                + ((bytes[recIdx + 7] & 0xFFL))
                + ((bytes[recIdx + 8] & 0xFFL) << 24)
                + ((bytes[recIdx + 9] & 0xFFL) << 16);
        r.y = ((bytes[recIdx + 10] & 0xFFL) << 8)
                + ((bytes[recIdx + 11] & 0xFFL))
                + ((bytes[recIdx + 12] & 0xFFL) << 24)
                + ((bytes[recIdx + 3] & 0xFFL) << 16);
        r.z = ((bytes[recIdx + 14] & 0xFFL) << 8)
                + ((bytes[recIdx + 15] & 0xFFL))
                + ((bytes[recIdx + 16] & 0xFFL) << 24)
                + ((bytes[recIdx + 17] & 0xFFL) << 16);
        return r;
    }

    private int error;

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#getFileObject()
     */
    protected Object getFileObject(final BT747Path fileName) {
        File inFile = null;

        if (File.isAvailable()) {
            inFile = new File(fileName, File.READ_ONLY);
            if (!inFile.isOpen()) {
                errorInfo = fileName + "|" + inFile.getLastError();
                error = BT747Constants.ERROR_COULD_NOT_OPEN;
                inFile = null;
            }
        }
        return inFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.log.in.GPSLogConvertInterface#closeFileObject(java.lang.Object)
     */
    protected void closeFileObject(final Object o) {
        ((File) o).close();
    }

    public int toGPSFile(final BT747Path path,
            final GPSFileConverterInterface gpsFile) {
        Object inFile;
        error = BT747Constants.NO_ERROR;

        try {
            inFile = getFileObject(path);
            if (inFile != null) {
                passToFindFieldsActivatedInLog = gpsFile
                        .needPassToFindFieldsActivatedInLog();
                if (passToFindFieldsActivatedInLog) {
                    gpsFile
                            .setActiveFileFields(getLogFormatRecord(activeFileFields));
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

    public GPSRecord getLogFormatRecord(final int logFormat) {
        int logfmt = (1 << BT747Constants.FMT_UTC_IDX)
                | (1 << BT747Constants.FMT_LATITUDE_IDX)
                | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                | (1 << BT747Constants.FMT_LONGITUDE_IDX | (1 << BT747Constants.FMT_SPEED_IDX));

        switch (getLoggerType()) {
        case BT747Constants.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
        case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
            logfmt |= (1 << BT747Constants.FMT_HEIGHT_IDX);
            break;
        case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
        default:
            break;
        }

        return GPSRecord.getLogFormatRecord(logfmt);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#getType()
     */
    public int getType() {
        return Model.SR_LOGTYPE;
    }
}
