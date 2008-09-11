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

import bt747.io.File;

/**
 * This class is used to convert the binary log to a new format. Basically this
 * class interprets the log and creates a {@link GPSRecord}. The
 * {@link GPSRecord} is then sent to the {@link GPSFile} class object to write
 * it to the output.
 * 
 * @author Mario De Weerd
 */
public final class DPL700LogConvert implements GPSLogConvert {
    private static final int X_FF = 0xFF;
    private int recordSize = 16;
    private int logFormat;
    private File inFile = null;
    private long timeOffsetSeconds = 0;
    protected boolean passToFindFieldsActivatedInLog = false;
    protected int activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
            | (1 << BT747Constants.FMT_LATITUDE_IDX)
            | (1 << BT747Constants.FMT_LONGITUDE_IDX)
            | (1 << BT747Constants.FMT_HEIGHT_IDX);

    private boolean isConvertWGL84ToMSL = false; // If true,remove geoid difference from
    // height

    static final int ITRACKU_NUMERIX = 0;
    static final int PHOTOTRACKR = 1;
    static final int ITRACKU_SIRFIII = 2;

    private int logType = ITRACKU_NUMERIX;

    public DPL700LogConvert() {
        super();
        setTimeOffset(ITRACKU_NUMERIX);
    }

    private String errorInfo;
    public String getErrorInfo() {
        return errorInfo;
    }
    
    public int getLogType() {
        return logType;
    }

    public void setLogType(final int logType) {
        this.logType = logType;
        switch (this.logType) {
        case PHOTOTRACKR:
        case ITRACKU_SIRFIII:
            activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
                    | (1 << BT747Constants.FMT_LATITUDE_IDX)
                    | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                    | (1 << BT747Constants.FMT_HEIGHT_IDX)
                    | (1 << BT747Constants.FMT_SPEED_IDX);
            recordSize = 16;
            break;
        case ITRACKU_NUMERIX:
        default:
            activeFileFields = (1 << BT747Constants.FMT_UTC_IDX)
                    | (1 << BT747Constants.FMT_LATITUDE_IDX)
                    | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                    | (1 << BT747Constants.FMT_HEIGHT_IDX);
            recordSize = 16;
            break;
        }
    }
    
    private boolean stop = false;
    
    public void stopConversion() {
        stop = true;
    }


    public int parseFile(final GPSFile gpsFile) {
        try {
            GPSRecord gpsRec = new GPSRecord();
            final int C_BUF_SIZE = 0x800;
            byte[] bytes = new byte[C_BUF_SIZE];
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
            while (!stop && nextAddrToRead + recordSize + 1 < fileSize) {
                sizeToRead = C_BUF_SIZE;
                if ((sizeToRead + nextAddrToRead) > fileSize) {
                    sizeToRead = fileSize - nextAddrToRead;
                }

                /* Read the bytes from the file */
                int readResult;
                int offsetInBuffer = 0;

                inFile.setPos(nextAddrToRead);

                /***************************************************************
                 * Not reading header - reading data.
                 */
                readResult = inFile.readBytes(bytes, 0, sizeToRead);
                if (readResult != sizeToRead) {
                    errorInfo = inFile.getPath() + "|" + inFile.getLastError();
                    return BT747Constants.ERROR_READING_FILE;
                }
                nextAddrToRead += sizeToRead;

                /***************************************************************
                 * Interpret the data read in the Buffer as long as the records
                 * are complete
                 */
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
                        /*******************************************************
                         * Get all the information in the record.
                         */
                        gpsRec.recCount = recCount;
                        if (!passToFindFieldsActivatedInLog) {

                            // if (( lc( $o{'i'} ) eq lc( 'iTrackU-Nemerix' ) )
                            // or
                            // # parse the record, this trick took me one whole
                            // day...
                            // ($lon, $lat, $year, $month, $day, $hour, $minute,
                            // $second, $speed, $tag) =
                            // unpack( " V V C C C C C C C C", $_ );
                            // # _Longitude_ _Latitude__ YY MM DD HH MM SS
                            // SpdTag
                            // # ef f3 b7 00 d1 df 02 03 07 09 1d 06 01 2a 00 ff

                            // elsif (( lc( $o{'i'} ) eq lc( 'PhotoTrackr' ) )
                            // or
                            // ( lc( $o{'i'} ) eq 'p' ) or
                            // ( lc( $o{'i'} ) eq lc( 'iTrackU-SIRFIII' ) ) or
                            // ( lc( $o{'i'} ) eq 's' ) ) {
                            // ($lon, $lat, $date, $altitude, $speed, $tag) =
                            // unpack( " V V V s C C", $_ );
                            // # _Longitude_ _Latitude__ ___Date____ _Alt_
                            // SpdTag
                            // # bd 49 90 00 09 0c 1d 03 b4 eb a8 1e 3b 00 02 63

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

                            switch (logType) {
                            case PHOTOTRACKR:
                            case ITRACKU_SIRFIII:
                                // NEMERIX
                                // Get information from log file
                                longitude = (X_FF & bytes[recIdx++]) << 0
                                        | (X_FF & bytes[recIdx++]) << 8
                                        | (X_FF & bytes[recIdx++]) << 16
                                        | (X_FF & bytes[recIdx++]) << 24;
                                latitude = (X_FF & bytes[recIdx++]) << 0
                                        | (X_FF & bytes[recIdx++]) << 8
                                        | (X_FF & bytes[recIdx++]) << 16
                                        | (X_FF & bytes[recIdx++]) << 24;
                                gpsRec.utc = (X_FF & bytes[recIdx++]) << 0
                                        | (X_FF & bytes[recIdx++]) << 8
                                        | (X_FF & bytes[recIdx++]) << 16
                                        | (X_FF & bytes[recIdx++]) << 24;
                                altitude = (X_FF & bytes[recIdx++]) << 0
                                        | (X_FF & bytes[recIdx++]) << 8;
                                gpsRec.height = altitude;
                                if (isConvertWGL84ToMSL) {
                                    gpsRec.height -= Conv.wgs84Separation(
                                            gpsRec.latitude, gpsRec.longitude);
                                }
                                speed = (X_FF & bytes[recIdx++]) << 0;
                                tag = (X_FF & bytes[recIdx++]) << 0;
                                break;
                            default:
                                // NEMERIX
                                // Get information from log file
                                longitude = (X_FF & bytes[recIdx++]) << 0
                                | (X_FF & bytes[recIdx++]) << 8
                                | (X_FF & bytes[recIdx++]) << 16
                                | (X_FF & bytes[recIdx++]) << 24;
                                latitude = (X_FF & bytes[recIdx++]) << 0
                                        | (X_FF & bytes[recIdx++]) << 8
                                        | (X_FF & bytes[recIdx++]) << 16
                                        | (X_FF & bytes[recIdx++]) << 24;
                                year = (X_FF & bytes[recIdx++]) << 0;
                                month = (X_FF & bytes[recIdx++]) << 0;
                                day = (X_FF & bytes[recIdx++]) << 0;
                                hour = (X_FF & bytes[recIdx++]) << 0;
                                minutes = (X_FF & bytes[recIdx++]) << 0;
                                seconds = (X_FF & bytes[recIdx++]) << 0;
                                speed = (X_FF & bytes[recIdx++]) << 0;
                                tag = (X_FF & bytes[recIdx++]) << 0;
                                gpsRec.utc = Conv
                                        .dateToUTCepoch1970(new bt747.util.Date(
                                                day, month, year + 2000));
                                gpsRec.utc += 3600 * hour + 60 * minutes
                                        + seconds;
                                gpsRec.utc += timeOffsetSeconds;
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
                            // The next lines explicitly use an integer division
                            // on longitude to get an integer result!
                            // The objective is to get the first digits
                            // of the number.
                            gpsRec.longitude = ((double) ((int) (longitude / 1000000)))
                                    + ((longitude % 1000000) / 600000.0);
                            gpsRec.latitude = ((double) ((int) (latitude / 1000000)))
                                    + ((latitude % 1000000) / 600000.0);
                            gpsRec.speed = speed * 1.852f;

                            gpsRec.valid = 0xFFFF;
                            gpsRec.rcr = 0x0001; // For filter
                            gpsFile.writeRecord(gpsRec);
                        }
                    }
                } /* ContinueInBuffer */
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
        isConvertWGL84ToMSL = b;
    }

    public int toGPSFile(
            final String fileName,
            final GPSFile gpsFile,
            final int card) {
        int error = BT747Constants.NO_ERROR;
        stop = false;

        try {
            if (File.isAvailable()) {
                inFile = new File(fileName, File.READ_ONLY, card);
                if (!inFile.isOpen()) {
                    errorInfo = fileName + "|" + inFile.getLastError();
                    error = BT747Constants.ERROR_COULD_NOT_OPEN;
                    inFile = null;
                } else {
                    passToFindFieldsActivatedInLog = gpsFile
                            .needPassToFindFieldsActivatedInLog();
                    if (passToFindFieldsActivatedInLog) {
                        gpsFile.setActiveFileFields(
                                getLogFormatRecord(activeFileFields));
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
        }
        return error;
    }

    public GPSRecord getLogFormatRecord(final int logFormat) {
        GPSRecord gpsRec = new GPSRecord();
        gpsRec.utc = -1;
        gpsRec.latitude = -1;
        gpsRec.longitude = -1;
        gpsRec.speed = -1;

        switch (logType) {
        case PHOTOTRACKR:
        case ITRACKU_SIRFIII:
            gpsRec.height = -1;
            break;
        case ITRACKU_NUMERIX:
        default:
            break;
        }

        /* End handling record */
        return gpsRec;
    }

}
