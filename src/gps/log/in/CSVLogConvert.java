// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
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
import bt747.sys.JavaLibBridge;
import bt747.sys.Settings;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747StringTokenizer;

/**
 * This class is used to convert the binary log to a new format. Basically
 * this class interprets the log and creates a {@link GPSRecord}. The
 * {@link GPSRecord} is then sent to the {@link GPSFileConverterInterface}
 * class object to write it to the output.
 * 
 * @author Mario De Weerd
 */
public final class CSVLogConvert extends GPSLogConvertInterface {
    private static final String[] MONTHS_AS_TEXT = { "JAN", "FEB", "MAR",
            "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
    private static final int EOL = 0x0D;
    private static final int CR = 0x0A;
    private GPSRecord logFormatRecord;
    private GPSRecord activeFileFieldsRecord;
    protected boolean passToFindFieldsActivatedInLog = false;

    private static final int FMT_FIXMODE = -13; // TODO: currently ignored
    private static final int FMT_VOX = -12;
    private static final int FMT_LONEW = -11;
    private static final int FMT_LATNS = -10;
    private static final int C_LOGTIME = -9;
    private static final int C_LOGDIST = -8;
    private static final int C_LOGSPD = -7;
    private static final int FMT_NS = -6;
    private static final int FMT_EW = -5;
    private static final int FMT_REC_NBR = -4;
    private static final int FMT_DATE = -3;
    private static final int FMT_TIME = -2;
    private static final int FMT_NO_FIELD = -1;

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
     * Parse the source file (here a CSV) and provide the records to gpsFile
     * (the builder).
     * 
     * @param file
     *            Source file representation.
     * @param gpsFile
     *            The object representing the output builder.
     * @return {@link BT747Constants#NO_ERROR} if no error (0)
     * @see gps.log.in.GPSFileConverterInterface
     */
    public final int parseFile(final Object file,
            final GPSFileConverterInterface gpsFile) {
        final WindowedFile mFile = (WindowedFile) file;

        GPSRecord r = GPSRecord.getLogFormatRecord(0);
        byte[] bytes;
        int sizeToRead;
        int nextAddrToRead;
        int recCount;
        int fileSize;

        boolean firstline = true;
        final int[] records = new int[CSVLogConvert.MAX_RECORDS];
        try {
            records[0] = CSVLogConvert.FMT_NO_FIELD; // Indicates that
            // there are no
            // records

            recCount = 0;
            logFormatRecord = GPSRecord.getLogFormatRecord(0);
            nextAddrToRead = 0;
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
                sizeToRead = CSVLogConvert.BUF_SIZE;
                if ((sizeToRead + nextAddrToRead) > fileSize) {
                    sizeToRead = fileSize - nextAddrToRead;
                }

                /* Read the bytes from the file */
                boolean continueInBuffer = true;
                int offsetInBuffer = 0;

                /*************************************************************
                 * Reading
                 */
                try {
                    bytes = mFile.fillBuffer(nextAddrToRead);
                } catch (final Exception e) {
                    // TODO: Should check sizeToRead vs fill in buffer.
                    Generic.debug("Problem reading file", e);
                    bytes = null;
                }

                if (bytes == null) {
                    Generic.debug("fillBuffer failed", null);

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
                    // Find end of line
                    for (eolPos = offsetInBuffer; (eolPos < sizeToRead)
                            && (bytes[eolPos] != CSVLogConvert.CR)
                            && (bytes[eolPos] != CSVLogConvert.EOL); eolPos++) {
                        ; // Empty on purpose
                    }
                    continueInBuffer = (eolPos < sizeToRead); // True when
                    // \r\n

                    if (continueInBuffer) {
                        final StringBuffer s = new StringBuffer(eolPos
                                - offsetInBuffer + 1);

                        for (int i = offsetInBuffer; i < eolPos; i++) {
                            s.append((char) bytes[i]);
                        }

                        final BT747StringTokenizer fields = JavaLibBridge
                                .getStringTokenizerInstance(s.toString(), ',');
                        offsetInBuffer = eolPos;
                        for (; (offsetInBuffer < sizeToRead)
                                && ((bytes[offsetInBuffer] == CSVLogConvert.CR) || (bytes[offsetInBuffer] == CSVLogConvert.EOL)); offsetInBuffer++) {
                            ; // Empty on purpose
                        }
                        if (s.length() != 0) {
                            if (firstline) {
                                // Get header
                                firstline = false;
                                int activeFileFields = 0;
                                activeFileFieldsRecord = GPSRecord
                                        .getLogFormatRecord(0);

                                for (int i = 0; fields.hasMoreTokens()
                                        && (i < CSVLogConvert.MAX_RECORDS); i++) {
                                    final String string = fields.nextToken();
                                    if (string.equals("INDEX")) {
                                        records[i] = CSVLogConvert.FMT_REC_NBR;

                                    } else if (string.equals("RCR")
                                            || string.equals("TAG")) {
                                        records[i] = BT747Constants.FMT_RCR_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_RCR_IDX);
                                    } else if (string.equals("TIME")) {
                                        records[i] = CSVLogConvert.FMT_TIME;
                                        activeFileFields |= (1 << BT747Constants.FMT_UTC_IDX);
                                    } else if (string.equals("DATE")) {
                                        records[i] = CSVLogConvert.FMT_DATE;
                                        activeFileFields |= (1 << BT747Constants.FMT_UTC_IDX);
                                    } else if (string.equals("FIX MODE")) {
                                        records[i] = CSVLogConvert.FMT_FIXMODE;
                                        activeFileFields |= (1 << BT747Constants.FMT_VALID_IDX);
                                    } else if (string.equals("VALID")) {
                                        records[i] = BT747Constants.FMT_VALID_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_VALID_IDX);
                                    } else if (string.equals("LATITUDE")) {
                                        records[i] = BT747Constants.FMT_LATITUDE_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_LATITUDE_IDX);
                                    } else if (string.equals("N/S")) {
                                        records[i] = CSVLogConvert.FMT_NS;
                                    } else if (string.equals("LONGITUDE")) {
                                        records[i] = BT747Constants.FMT_LONGITUDE_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
                                    } else if (string.equals("E/W")) {
                                        records[i] = CSVLogConvert.FMT_EW;
                                    } else if (string
                                            .startsWith("HEIGHT(ft)")) {
                                        records[i] = CSVLogConvert.FMT_HEIGHT_FT_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_HEIGHT_IDX);
                                    } else if (string.startsWith("HEIGHT")) {
                                        records[i] = BT747Constants.FMT_HEIGHT_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_HEIGHT_IDX);
                                    } else if (string
                                            .startsWith("SPEED(mph)")) {
                                        records[i] = CSVLogConvert.FMT_SPEED_MPH_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_SPEED_IDX);
                                    } else if (string.startsWith("SPEED")) {
                                        records[i] = BT747Constants.FMT_SPEED_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_SPEED_IDX);
                                    } else if (string.equals("HEADING")) {
                                        records[i] = BT747Constants.FMT_HEADING_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_HEADING_IDX);
                                    } else if (string.equals("DSTA")) {
                                        records[i] = BT747Constants.FMT_DSTA_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_DSTA_IDX);
                                    } else if (string.equals("DAGE")) {
                                        records[i] = BT747Constants.FMT_DAGE_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_DAGE_IDX);
                                    } else if (string.equals("PDOP")) {
                                        records[i] = BT747Constants.FMT_PDOP_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_PDOP_IDX);
                                    } else if (string.equals("HDOP")) {
                                        records[i] = BT747Constants.FMT_HDOP_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_HDOP_IDX);
                                    } else if (string.equals("VDOP")) {
                                        records[i] = BT747Constants.FMT_VDOP_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_VDOP_IDX);
                                    } else if (string
                                            .equals("NSAT (USED/VIEW)")) {
                                        records[i] = BT747Constants.FMT_NSAT_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_NSAT_IDX);
                                    } else if (string
                                            .startsWith("DISTANCE(ft)")) {
                                        records[i] = CSVLogConvert.FMT_DISTANCE_FT_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_DISTANCE_IDX);
                                    } else if (string.startsWith("DISTANCE")) {
                                        records[i] = BT747Constants.FMT_DISTANCE_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_DISTANCE_IDX);
                                    } else if (string
                                            .startsWith("SAT INFO (SID")) {
                                        records[i] = BT747Constants.FMT_SID_IDX;
                                        activeFileFields |= (1 << BT747Constants.FMT_SID_IDX);
                                        if (string.indexOf("-ELE", 12) != -1) {
                                            activeFileFields |= (1 << BT747Constants.FMT_ELEVATION_IDX);
                                        }
                                        if (string.indexOf("-AZI", 12) != -1) {
                                            activeFileFields |= (1 << BT747Constants.FMT_AZIMUTH_IDX);
                                        }
                                        if (string.indexOf("-SNR", 12) != -1) {
                                            activeFileFields |= (1 << BT747Constants.FMT_SNR_IDX);
                                        }
                                    } else if (string.equals("LATITUDE N/S")) {
                                        records[i] = CSVLogConvert.FMT_LATNS;
                                        activeFileFields |= (1 << BT747Constants.FMT_LATITUDE_IDX);
                                    } else if (string.equals("LONGITUDE E/W")) {
                                        records[i] = CSVLogConvert.FMT_LONEW;
                                        activeFileFields |= (1 << BT747Constants.FMT_LONGITUDE_IDX);
                                    } else if (string.startsWith("LOGTIME")) {
                                        records[i] = CSVLogConvert.C_LOGTIME;
                                        activeFileFieldsRecord.logPeriod = 1;
                                    } else if (string.startsWith("LOGDIST")) {
                                        records[i] = CSVLogConvert.C_LOGDIST;
                                        activeFileFieldsRecord.logDistance = 1;
                                    } else if (string.startsWith("LOGSPD")) {
                                        records[i] = CSVLogConvert.C_LOGSPD;
                                        activeFileFieldsRecord.logSpeed = 1;
                                    } else if (string.equals("VOX")) {
                                        records[i] = CSVLogConvert.FMT_VOX;
                                        activeFileFieldsRecord.voxStr = "";
                                    } else {
                                        records[i] = -100;// FMT_UNKNOWN_FIELD;
                                    }
                                    records[i + 1] = CSVLogConvert.FMT_NO_FIELD;

                                }
                                activeFileFieldsRecord
                                        .cloneActiveFields(GPSRecord
                                                .getLogFormatRecord(activeFileFields));
                                if (passToFindFieldsActivatedInLog) {
                                    // Some brute force return
                                    return BT747Constants.NO_ERROR;
                                }
                            } else {

                                // Find end of line
                                // Seperate line
                                // Split fields
                                // Interpret fields

                                int fieldNbr = 0;
                                // Defaults
                                r = GPSRecord.getLogFormatRecord(0); // Value
                                // after
                                // earliest date
                                r.recCount = ++recCount;
                                // r.valid = 0xFFFF; // In case valid is not
                                // logged
                                // r.rcr = 1; // In case RCR is not logged -
                                // default = time

                                while (fields.hasMoreTokens()
                                        && (fieldNbr < CSVLogConvert.MAX_RECORDS)
                                        && (records[fieldNbr] != CSVLogConvert.FMT_NO_FIELD)) {
                                    final String field = fields.nextToken()
                                            .trim();
                                    if (field.length() != 0) {
                                        final int fieldType = records[fieldNbr];
                                        recCount = readField(fieldType,
                                                field, r, recCount);
                                    }
                                    // Next condition should be handled by
                                    // stopping
                                    // interpretation after the first line.
                                    // if (!passToFindFieldsActivatedInLog) {
                                    fieldNbr++;
                                }
                                updateLogFormat(gpsFile, r);
                                CommonIn.adjustHeight(r,
                                        factorConversionWGS84ToMSL);

                                if (r.rcr == 0) {
                                    r.rcr = 1; // Suppose time (for filter)
                                }
                                // if (valid) {
                                gpsFile.addLogRecord(r);
                                r = GPSRecord.getLogFormatRecord(0);
                                // } // offsetInBuffer++;
                            } // if header
                        }
                    } // line found
                } while (continueInBuffer);
                if (offsetInBuffer == 0) {
                    // Nothing usefull found in buffer / end of file
                    break;
                }
                nextAddrToRead -= (sizeToRead - offsetInBuffer);
            } /* nextAddrToRead<fileSize */
        } catch (final Exception e) {
            Generic.debug("parseFile", e);
        }
        return BT747Constants.NO_ERROR;
    }
    
    
    // Previous index in month list - to speed up.
    private int previousIdx = 0;

    /**
     * @param fieldType
     * @param field
     * @param r
     * @param recCount
     * @return
     */
    private final int readField(final int fieldType, String field,
            final GPSRecord r, int recCount) {
        try {
            switch (fieldType) {
            case C_LOGTIME:
                r.logPeriod = (int) (JavaLibBridge.toFloat(field) * 10);
                break;
            case C_LOGDIST:
                r.logDistance = (int) (JavaLibBridge.toFloat(field) * 10);
                break;
            case C_LOGSPD:
                r.logSpeed = (int) (JavaLibBridge.toFloat(field) * 10);
                break;
            case FMT_NS:
                // Supposes longitude preceded
                if (field.equals("N")) {
                    r.latitude = Math.abs(r.latitude);
                } else if (field.equals("S")) {
                    r.latitude = -Math.abs(r.latitude);
                }
                break;
            case FMT_EW:
                // Supposes latitude preceded
                if (field.equals("E")) {
                    r.longitude = Math.abs(r.longitude);
                } else if (field.equals("W")) {
                    r.longitude = -Math.abs(r.longitude);
                }
                break;
            case FMT_REC_NBR:
                r.recCount = JavaLibBridge.toInt(field);
                recCount = r.recCount;
                break;
            case FMT_DATE: {
                if (field.length() == 10) {
                    byte format;
                    if (field.indexOf('/') == 4) {
                        format = Settings.DATE_YMD;
                    } else {
                        format = Settings.DATE_DMY;
                    }

                    final int date = (JavaLibBridge
                            .getDateInstance(field, format))
                            .dateToUTCepoch1970();
                    r.utc += date;
                } else if (field.length() == 9) {
                    if (field.charAt(2) == '-' && field.charAt(6) == '-') {
                        int day;
                        int year;
                        day = JavaLibBridge.toInt(field.substring(0, 2));
                        year = JavaLibBridge.toInt(field.substring(7,9)) + 2000;
                        String m = field.substring(3, 6);
                        int idx = 0;
                        if (m.equals(MONTHS_AS_TEXT[previousIdx])) {
                            idx = previousIdx;
                        } else {
                            while (idx < MONTHS_AS_TEXT.length
                                    && !m.equals(m)) {
                                idx++;
                            }
                        }
                        if (idx < MONTHS_AS_TEXT.length) {
                            r.utc += JavaLibBridge.getDateInstance(day, idx + 1,
                                    year).dateToUTCepoch1970();
                        }
                    }
                } else if (field.length() == 6) {
                    int day;
                    int month;
                    int year;
                    year = JavaLibBridge.toInt(field.substring(0, 2)) + 2000;
                    month = JavaLibBridge.toInt(field.substring(2, 4));
                    day = JavaLibBridge.toInt(field.substring(4, 6));
                    r.utc += JavaLibBridge.getDateInstance(day, month, year)
                            .dateToUTCepoch1970();
                }
            }
                break;
            case FMT_TIME: {
                if (field.length() == 6) {
                    int secondes;
                    int minutes;
                    int hours;
                    hours = JavaLibBridge.toInt(field.substring(0, 2));
                    minutes = JavaLibBridge.toInt(field.substring(2, 4));
                    secondes = JavaLibBridge.toInt(field.substring(4, 6));
                    r.utc += secondes + minutes * 60 + 3600 * hours;
                } else {
                    // gpsRec.utc=;
                    int dotidx;
                    if ((dotidx = field.indexOf('.')) != -1) {
                        // TODO: check if idx out
                        // of
                        // range.
                        r.milisecond = JavaLibBridge.toInt(field
                                .substring(dotidx + 1));
                        field = field.substring(0, dotidx);
                    }
                    final BT747StringTokenizer tfields = JavaLibBridge
                            .getStringTokenizerInstance(field, ':');
                    if (tfields.countTokens() == 3) {
                        r.utc += JavaLibBridge.toInt(tfields.nextToken()) * 3600
                                + JavaLibBridge.toInt(tfields.nextToken()) * 60
                                + JavaLibBridge.toInt(tfields.nextToken());
                    }
                }
            }
                break;
            case BT747Constants.FMT_VALID_IDX:
                if (field.equals("No fix")) {
                    r.valid = 0x0001;
                } else if (field.equals("SPS")) {
                    r.valid = 0x0002;
                } else if (field.equals("DGPS")) {
                    r.valid = 0x0004;
                } else if (field.equals("PPS")) {
                    r.valid = 0x0008;
                } else if (field.equals("RTK")) {
                    r.valid = 0x0010;
                } else if (field.equals("FRTK")) {
                    r.valid = 0x0020;
                } else if (field.equals("Estimated mode")) {
                    r.valid = 0x0040;
                } else if (field.equals("Manual input mode")) {
                    r.valid = 0x0080;
                } else if (field.equals("Simulator mode")) {
                    r.valid = 0x0100;
                } else {
                    //r.valid = 0x0000;
                }
                break;
            case FMT_LATNS: {
                final String latns = field.substring(0, field.length() - 1);
                final char or = field.charAt(latns.length());
                r.latitude = JavaLibBridge.toDouble(latns);
                if (((or == 'N') && (r.latitude < 0))
                        || ((or == 'S') && (r.latitude > 0))) {
                    r.latitude = -r.latitude;
                }
            }
                break;
            case FMT_LONEW: {
                final String lonns = field.substring(0, field.length() - 1);
                final char or = field.charAt(lonns.length());
                r.longitude = JavaLibBridge.toDouble(lonns);
                if (((or == 'E') && (r.longitude < 0))
                        || ((or == 'W') && (r.longitude > 0))) {
                    r.longitude = -r.longitude;
                }
            }
                break;
            case BT747Constants.FMT_LATITUDE_IDX:
                r.latitude = JavaLibBridge.toDouble(field);
                break;
            case BT747Constants.FMT_LONGITUDE_IDX:
                r.longitude = JavaLibBridge.toDouble(field);
                break;
            case BT747Constants.FMT_HEIGHT_IDX: {
                final BT747StringTokenizer n = JavaLibBridge
                        .getStringTokenizerInstance(field, ' ');
                r.height = JavaLibBridge.toFloat(n.nextToken());
            }
                break;
            case FMT_HEIGHT_FT_IDX: {
                final BT747StringTokenizer n = JavaLibBridge
                        .getStringTokenizerInstance(field, ' ');
                r.height = JavaLibBridge.toFloat(n.nextToken()) / 3.28083989501312F;
            }
                break;

            case BT747Constants.FMT_SPEED_IDX: {
                final BT747StringTokenizer n = JavaLibBridge
                        .getStringTokenizerInstance(field, ' ');
                r.speed = JavaLibBridge.toFloat(n.nextToken());
            }
                break;
            case FMT_SPEED_MPH_IDX: {
                final BT747StringTokenizer n = JavaLibBridge
                        .getStringTokenizerInstance(field, ' ');
                r.speed = JavaLibBridge.toFloat(n.nextToken()) / 0.621371192237334F;
            }
                break;

            // gpsRec.speed=JavaLibBridge.toFloatBitwise(speed);
            case BT747Constants.FMT_HEADING_IDX:
                r.heading = JavaLibBridge.toFloat(field);
                break;
            case BT747Constants.FMT_DSTA_IDX:
                r.dsta = JavaLibBridge.toInt(field);
                break;
            case BT747Constants.FMT_DAGE_IDX:
                r.dage = JavaLibBridge.toInt(field);
                break;
            case BT747Constants.FMT_PDOP_IDX:
                r.pdop = (int) (JavaLibBridge.toFloat(field) * 100);
                break;
            case BT747Constants.FMT_HDOP_IDX:
                r.hdop = (int) (JavaLibBridge.toFloat(field) * 100);
                break;
            case BT747Constants.FMT_VDOP_IDX:
                r.vdop = (int) (JavaLibBridge.toFloat(field) * 100);
                break;
            case BT747Constants.FMT_NSAT_IDX: {
                final BT747StringTokenizer nfields = JavaLibBridge
                        .getStringTokenizerInstance(field, '(');
                if (nfields.countTokens() >= 2) {
                    r.nsat = JavaLibBridge.toInt(nfields.nextToken()) * 256;
                    String t = nfields.nextToken();
                    t = t.substring(0, t.indexOf(')'));
                    r.nsat += JavaLibBridge.toInt(t);
                }
            }
                // Need to handle ()
                break;
            case FMT_FIXMODE:
                // TODO: handle '3D' and '2D'
                // values ?
                break;
            case BT747Constants.FMT_MAX_SATS:
                break;
            case BT747Constants.FMT_SID_IDX: {
                final BT747StringTokenizer SatFields = JavaLibBridge
                        .getStringTokenizerInstance(field, ';');
                final int cnt = SatFields.countTokens();
                r.sid = new int[cnt];
                r.sidinuse = new boolean[cnt];
                r.ele = new int[cnt];
                r.azi = new int[cnt];
                r.snr = new int[cnt];
                for (int i = 0; i < cnt; i++) {
                    final String satf = SatFields.nextToken();
                    if (satf.length() != 0) {
                        r.sidinuse[i] = (satf.charAt(0) == '#');
                        final BT747StringTokenizer sinfos = JavaLibBridge
                                .getStringTokenizerInstance(satf
                                        .substring(r.sidinuse[i] ? 1 : 0),
                                        '-');
                        // Vm.debug(sinfos[0]);
                        if (sinfos.hasMoreTokens()) {
                            r.sid[i] = JavaLibBridge.toInt(sinfos.nextToken());
                        }
                        if (activeFileFieldsRecord.hasEle()
                                && sinfos.hasMoreTokens()) {
                            r.ele[i] = JavaLibBridge.toInt(sinfos.nextToken());
                        }
                        if (activeFileFieldsRecord.hasAzi()
                                && sinfos.hasMoreTokens()) {
                            r.azi[i] = JavaLibBridge.toInt(sinfos.nextToken());
                        }
                        if (activeFileFieldsRecord.hasSnr()
                                && sinfos.hasMoreTokens()) {
                            r.snr[i] = JavaLibBridge.toInt(sinfos.nextToken());
                        }
                    } // length
                } // for
            }
                break;
            case FMT_VOX: {
                if (field.length() != 0) {
                    r.voxStr = field;
                    // TODO: identify as VOX field
                    // ??
                }
            }
                break;
            case BT747Constants.FMT_RCR_IDX: {
                r.rcr = 0;
                if (field.charAt(0) != 'X') {
                    if (field.indexOf('B', 0) != -1) {
                        r.rcr |= BT747Constants.RCR_BUTTON_MASK;
                    }
                    if (field.indexOf('T', 0) != -1) {
                        r.rcr |= BT747Constants.RCR_TIME_MASK;
                    }
                    if (field.indexOf('S', 0) != -1) {
                        r.rcr |= BT747Constants.RCR_SPEED_MASK;
                    }
                    if (field.indexOf('D', 0) != -1) {
                        r.rcr |= BT747Constants.RCR_DISTANCE_MASK;
                    }
                    if (field.indexOf('V', 0) != -1) {
                        // Voice record on
                        // VGPS-900
                        r.rcr = 0x0300; // TODO:
                        // change
                        // in
                        // better
                        // value
                    }
                    if (field.indexOf('C', 0) != -1) {
                        // Way Point on VGPS-900
                        r.rcr = 0x0500; // TODO:
                        // change
                        // in
                        // better
                        // value
                    }
                } else {

                    if (field.length() == 5) {
                        r.rcr = Conv.hex2Int(field.substring(1));
                    }
                }
                // // Still 16-4 = 12
                // possibilities.
                // // Taking numbers from 1 to 9
                // // Then letters X, Y and Z
                // char c = '1';
                // int i;
                // for (i = 0x10; c <= '9'; i <<=
                // 1, c++) {
                // if ((field.indexOf(c, 0) !=
                // -1)) {
                // gpsRec.rcr |= i;
                // }
                // }
                // c = 'X';
                // for (i = 0x10; c <= '9'; i <<=
                // 1, c++) {
                // if ((field.indexOf(c, 0) !=
                // -1)) {
                // gpsRec.rcr |= i;
                // }
                // }
            }
                break;
            case BT747Constants.FMT_MILLISECOND_IDX:
                // gpsRec.milisecond=
                break;
            case BT747Constants.FMT_DISTANCE_IDX: {
                final BT747StringTokenizer n = JavaLibBridge
                        .getStringTokenizerInstance(field, ' ');
                r.distance = JavaLibBridge.toDouble(n.nextToken());
            }
                break;
            case FMT_DISTANCE_FT_IDX: {
                final BT747StringTokenizer n = JavaLibBridge
                        .getStringTokenizerInstance(field, ' ');
                r.distance = JavaLibBridge.toDouble(n.nextToken()) / 3.28083989501312;
            }
                break;
            case BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX:
                break;
            default:
                // Error message to show.
            }
        } catch (final Exception e) {
            Generic.debug("Problem reading record " + r.recCount
                    + " field \"" + field + "\"");
            // TODO: handle exception
        }
        return recCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#getFileObject()
     */
    protected Object getFileObject(final BT747Path fileName) {
        WindowedFile mFile = null;
        if (File.isAvailable()) {
            try {
                mFile = new WindowedFile(fileName, File.READ_ONLY);
                mFile.setBufferSize(CSVLogConvert.BUF_SIZE);
                errorInfo = fileName + "|" + mFile.getLastError();
            } catch (final Exception e) {
                Generic.debug("Error during initial open", e);
            }
            if ((mFile == null) || !mFile.isOpen()) {
                errorInfo = fileName.toString();
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
    protected void closeFileObject(final Object o) {
        ((WindowedFile) o).close();
    }

    private int error;

    public final int toGPSFile(final BT747Path path,
            final GPSFileConverterInterface gpsFile) {
        Object mFile;
        error = BT747Constants.NO_ERROR;
        try {
            mFile = getFileObject(path);
            if (mFile != null) {
                passToFindFieldsActivatedInLog = gpsFile
                        .needPassToFindFieldsActivatedInLog();
                if (passToFindFieldsActivatedInLog) {
                    error = parseFile(mFile, gpsFile);
                    gpsFile.setActiveFileFields(activeFileFieldsRecord);
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
            Generic.debug("toGPSFile", e);
            // TODO: handle exception
        }
        return error;
    }

    private void updateLogFormat(final GPSFileConverterInterface gpsFile,
            final GPSRecord r) {
        if (!r.equalsFormat(logFormatRecord)) {
            logFormatRecord = r.cloneRecord();
            activeFileFieldsRecord.cloneActiveFields(logFormatRecord);
            if (!passToFindFieldsActivatedInLog) {
                gpsFile.writeLogFmtHeader(logFormatRecord);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.log.in.GPSLogConvertInterface#getType()
     */
    public int getType() {
        return Model.CSV_LOGTYPE;
    }

}
