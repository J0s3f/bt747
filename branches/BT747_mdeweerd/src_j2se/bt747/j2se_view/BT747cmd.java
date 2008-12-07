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
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
/*
 * To run:
 *   Path must include RXTX.  
 *   In Eclipse, set in environment, for example (on windows):
 *     PATH  ${project_loc:BT747}/lib/rxtx-2.1-7-bins-r2/Windows/i368-mingw32/;%PATH%
 *   classpath must include:
 *      libBT747.jar
 *      collections-superwaba.jar (if the libBT747 is a debug library).
 */
package bt747.j2se_view;

import gps.BT747Constants;
import gps.connection.GPSrxtx;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import bt747.model.AppSettings;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Interface;
import bt747.sys.Settings;
import bt747.sys.interfaces.BT747FileName;

/**
 * 
 * @author Mario De Weerd
 */
public class BT747cmd implements bt747.model.ModelListener {

    /**
     * Set up system specific classes.
     */
    static {
        // Set up the low level functions interface.
        Interface
                .setJavaTranslationInterface(new net.sf.bt747.j2se.system.J2SEJavaTranslations());
        // Set the serial port class instance to use (also system specific).
        GPSrxtx.setGpsPortInstance(new gps.connection.GPSRxTxPort());

    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Model m;
    private Controller c;

    public BT747cmd(final Model m, final Controller c, final OptionSet options) {
        Settings.setAppSettings(new String(new byte[2048]));
        setController(c);
        setModel(m);
        handleOptions(options);
    }

    public void setController(final Controller c) {
        this.c = c; // Should check that c is an AppController or do it
    }

    public void setModel(final Model m) {
        if (this.m != null) {
            this.m.removeListener(this);
        }
        this.m = m;
    }

    // Code snippet kept for future reference.
    // // We can do the same thing to an array for internal treatment
    // GPSRecord[] positions = c.doConvertLogToTrackPoints();
    // if (positions == null) {
    // // Error occured
    // reportError(c.getLastError(), c.getLastErrorInfo());
    // } else {
    // // Print the first ten positions
    // for (int i = 0; i < positions.length && i < 10; i++) {
    // GPSRecord record = positions[i];
    // System.out.println("Position " + i + ":" + record.latitude
    // + "," + record.longitude);
    // }

    private void reportError(final int error, final String errorInfo) {
        switch (error) {
        case BT747Constants.ERROR_COULD_NOT_OPEN:
            System.err.println("Could not open " + errorInfo);
            break;
        case BT747Constants.ERROR_NO_FILES_WERE_CREATED:
            System.err
                    .println("WARNING - No files were created - Check the input type.");
            break;
        case BT747Constants.ERROR_READING_FILE:
            System.err.println("Problem reading" + errorInfo);
            break;
        default:
            break;
        }
    }

    // Code snippet kept for reference

    // public void modelEvent(ModelEvent e) {
    // // TODO Auto-generated method stub
    // int type = e.getType();
    // } else if (type == ModelEvent.CONVERSION_STARTED) {
    // // conversionStartTime = System.currentTimeMillis();
    // } else if (type == ModelEvent.CONVERSION_ENDED) {
    // // lbConversionTime
    // // .setText("Time to convert: "
    // // + ((int) (System.currentTimeMillis() - conversionStartTime))
    // // + " ms");
    // // lbConversionTime.setVisible(true);
    // } else if (type == ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY) {
    // // When the data on the device is not the same, overwrite
    // // automatically.
    // System.out
    // .println("Overwriting previously downloaded data that looks different.");
    // c.replyToOkToOverwrite(true);
    // } else if (type == ModelEvent.DOWNLOAD_STATE_CHANGE
    // || type == ModelEvent.LOG_DOWNLOAD_STARTED) {
    // progressUpdate();
    // } else if (type == ModelEvent.LOG_DOWNLOAD_DONE) {
    // progressUpdate();
    // handleDownloadEnded();
    // } else if (type == ModelEvent.DEBUG_MSG) {
    // System.out.flush();
    // System.err.println((String) e.getArg());
    // System.err.flush();
    // progressUpdate();
    // } else if (type == ModelEvent.CONNECTED) {
    // // btConnect.setText("Disconnect");
    // // btConnectFunctionIsConnect = false;
    //
    // // Launching in another thread - not really needed.
    // java.awt.EventQueue.invokeLater(new Runnable() {
    // public void run() {
    // afterConnection();
    // }
    // });
    //
    // } else if (type == ModelEvent.DISCONNECTED) {
    // // btConnect.setText("Connect");
    // // btConnectFunctionIsConnect = true;
    // }
    // }

    private boolean downloadIsSuccessFull = true;
    private Boolean eraseOngoing = Boolean.FALSE;
    private long conversionStartTime;
    private long downloadStartTime;

    public void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        // case ModelEvent.DEBUG_MSG:
        // System.out.flush();
        // System.err.println((String) e.getArg());
        // System.err.flush();
        // break;
        case ModelEvent.LOG_DOWNLOAD_STARTED:
            downloadStartTime = System.currentTimeMillis();
            downloadIsSuccessFull = false;
            progressUpdate();
            break;
        case ModelEvent.DOWNLOAD_STATE_CHANGE:
            progressUpdate();
            break;
        case ModelEvent.LOG_DOWNLOAD_DONE:
            progressUpdate();
            if (!downloadIsSuccessFull) {
                System.out.println("\n#### DOWNLOAD FAILED ####");
            } else {
                System.out.println("\n#### DOWNLOAD SUCCESS ####");
            }
            System.out.println("Time to download data (ms): "
                    + ((int) (System.currentTimeMillis() - downloadStartTime))
                    + " ms");
            break;
        case ModelEvent.LOG_DOWNLOAD_SUCCESS:
            downloadIsSuccessFull = true;
            break;
        case ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
            // // When the data on the device is not the same, overwrite
            // // automatically.
            System.out.println("Overwriting previously downloaded data"
                    + " that looks different.");
            c.replyToOkToOverwrite(true);
            break;
        case ModelEvent.ERASE_ONGOING_NEED_POPUP:
            setEraseOngoing(true);
            break;
        case ModelEvent.ERASE_DONE_REMOVE_POPUP:
            setEraseOngoing(false);
            break;
        case ModelEvent.CONVERSION_STARTED:
            conversionStartTime = System.currentTimeMillis();
            break;
        case ModelEvent.CONVERSION_ENDED:
            System.out
                    .println("Time to convert data (ms): "
                            + ((int) (System.currentTimeMillis() - conversionStartTime))
                            + " ms");
            break;
        default:
            break;
        }

    }

    private void flushOutstandingCmds() {
        while (m.getOutstandingCommandsCount() > 0) {
            // Thread t=Thread.currentThread();
            try {
                // System.out.println("Waiting for cmds "
                // + m.getOutstandingCommandsCount());
                // System.out.flush();
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
                // Do nothing
            }
        }
    }

    /**
     * Previous downloaded percentage
     */
    private int prevPercent = -1;

    /**
     * Update the progress status
     */
    private void progressUpdate() {
        int min;
        int max;
        int value;
        int percent = prevPercent;

        if (m.isDownloadOnGoing()) {
            min = m.getStartAddr();
            max = m.getEndAddr();
            value = m.getNextReadAddr();
            if (max != min) {
                percent = (value - min) * 100 / (max - min);
            }
            if (percent != prevPercent) {
                while (prevPercent < percent) {
                    prevPercent++;
                    System.out.print('*');
                    if ((prevPercent % 10) == 0) {
                        System.out.print("#" + percent + "%#");
                        System.out.flush();
                    }
                }
            }
        }
    }

    private void waitForErase() {
        flushOutstandingCmds();
        while (getEraseOngoing()) {
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
                // Do nothing
            }
        }
    }

    public final int convertLog(final int logType) {
        System.out.println("Input file: " + m.getStringOpt(Model.LOGFILEPATH));
        System.out.println("Output directory: "
                + m.getStringOpt(Model.OUTPUTDIRPATH));
        System.out.println("Output basename: "
                + m.getStringOpt(Model.REPORTFILEBASE));

        if (logType == Model.KMZ_LOGTYPE) {
            return c.doConvertLog(logType, new GPSKMZFile(), ".kmz");
        } else {
            return c.doConvertLog(logType);
        }
    }

    private void handleOptions(final OptionSet options) {
        m.init();
        // Set up the paths
        // Common to in/out
        c.setStringOpt(AppSettings.OUTPUTDIRPATH, ".");
        c.setOutputFileRelPath("GPSDATA");
        c.setIntOpt(Model.FILEFIELDFORMAT, 0xFFFFFFFF); // All fields
        c.setTrkSep(60);
        c.setColorValidTrack("0000FF");
        c.setColorInvalidTrack("0000FF");
        c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT, false);
        c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME, false);
        c.setOutputFileSplitType(0);
        c.setHeightConversionMode(Model.HEIGHT_AUTOMATIC);

        // Next line gets arguments not related to option
        options.nonOptionArguments();

        m.addListener(this);

        c.setChunkSize(0x00010000);

        if (options.has("d")) {
            Integer debugLevel;
            debugLevel = (Integer) (options.valueOf("d"));
            switch (debugLevel) {
            case 1:
                c.setDebug(true);
                break;
            case 2:
                c.setDebug(true);
                c.setDebugConn(true);
                break;
            default:
                break;
            }
        }

        // Default value
        c.setStringOpt(AppSettings.LOGFILEPATH, "BT747_log.bin");

        if (options.has("f")) {
            // Basename of files.
            String fullname = options.argumentOf("f");
            String basename;
            int splitIdx;
            String path = "";
            splitIdx = fullname.lastIndexOf('/');
            splitIdx = Math.max(splitIdx, fullname.lastIndexOf('\\'));

            if (splitIdx > 0) {
                path = fullname.substring(0, splitIdx);
                basename = fullname.substring(splitIdx + 1);
            } else {
                path = "";
                basename = fullname;
            }
            if (path.length() != 0) {
                c.setStringOpt(AppSettings.OUTPUTDIRPATH, path);
            }
            c.setStringOpt(AppSettings.LOGFILEPATH, basename + ".bin");
            c.setOutputFileRelPath(basename);

        }

        // Input is "/BT747/BT747_sample.bin"
        if (options.has("b")) {
            c.setStringOpt(AppSettings.LOGFILEPATH, options.argumentOf("b"));
        }

        if (options.has("s")) {
            c.setBaudRate((((Integer) options.valueOf("s")).intValue()));
        }

        if (options.has("p")) {
            String portStr;
            portStr = (String) options.valueOf("p");
            c.setStringOpt(Model.FREETEXTPORT, portStr);
        } else {
            // c.setUsb();
        }

        if (options.has("height")) {
            String heightOpt;
            heightOpt = ((String) options.valueOf("height")).toUpperCase();
            if (heightOpt.equals("AUTOMATIC")) {
                c.setHeightConversionMode(Model.HEIGHT_AUTOMATIC);
            } else if (heightOpt.equals("MSL_TO_WGS84")) {
                c.setHeightConversionMode(Model.HEIGHT_MSL_TO_WGS84);
            } else if (heightOpt.equals("WGS84_TO_MSL")) {
                c.setHeightConversionMode(Model.HEIGHT_WGS84_TO_MSL);
            } else if (heightOpt.equals("KEEP")) {
                c.setHeightConversionMode(Model.HEIGHT_NOCHANGE);
            } else {
                System.err
                        .println("Height parameter (" + heightOpt + "unknown");
            }
        }

        if (options.has("UTC")) {
            Integer offset = (Integer) options.valueOf("UTC");
            c.setIntOpt(AppSettings.GPSTIMEOFFSETHOURS, offset);
        }

        if (options.has("color")) {
            c.setColorValidTrack((String) options.valueOf("color"));
            // Default: bad color is the same
            c.setColorInvalidTrack((String) options.valueOf("color"));
        }

        if (options.has("badcolor")) {
            // Overrides previous default setting in "color"
            c.setColorInvalidTrack((String) options.valueOf("badcolor"));
        }

        if (options.has("splittype")) {
            String option = ((String) options.valueOf("splittype")).toUpperCase();
            /**
             * The way we split the input track:<br>
             * ONE_FILE = 0<br>
             * ONE_FILE_PER_DAY = 1<br>
             * ONE_FILE_PER_TRACK = 2
             * 
             * @return Current setting.
             */
            if (option.equals("DAY")) {
                c.setOutputFileSplitType(1);
            } else if (option.equals("TRACK")) {
                c.setOutputFileSplitType(2);
            } else {
                c.setOutputFileSplitType(0);
            }
        }

        if (options.has("timesplit")) {
            Integer split = (Integer) options.valueOf("timesplit");
            c.setTrkSep(split);
        }

        // Options for which a connection is needed.
        if (options.has("p") || (options.has("a") && !(options.has("b")))
                || options.has("l") || options.has("m") || options.has("r")
                || options.has("E") || options.has("o") || options.has("R")) {
            c.connectGPS();
        }

        if (options.has("device")) {
            String arg = options.argumentOf("l").toLowerCase();
            // AppController.GPS_TYPE_DEFAULT:
            // AppController.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
            // AppController.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
            // AppController.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:

            int deviceType = Model.GPS_TYPE_DEFAULT;
            if (arg.equals("default")) {
                deviceType = Model.GPS_TYPE_DEFAULT;
                c.setBooleanOpt(Model.FORCE_HOLUXM241, false);
            } else if (arg.equals("holux")) {
                deviceType = Model.GPS_TYPE_DEFAULT;
                c.setBooleanOpt(Model.FORCE_HOLUXM241, true);
            }
            c.setIntOpt(AppSettings.GPSTYPE, deviceType);
        }

        if (options.has("trkptinfo")) {
            c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT, true);
        }

        if (options.has("trkptname")) {
            c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME, true);
        }

        if (m.isConnected()) {
            // Connection is made.
            c.reqDeviceInfo();
            // c.req
            // c.reqMtkLogVersion();

            flushOutstandingCmds();
            System.out
                    .println("MTK Firmware: Version: "
                            + m.getFirmwareVersion()
                            + ", ID(Device): "
                            + m.getModel()
                            + ((m.getMainVersion().length() != 0) ? (", MainVersion:" + m
                                    .getMainVersion())
                                    : ""));
            // printf("Log format: (%s) %s\n", $1,
            // describe_log_format($log_format));
            // printf("Size in bytes of each log record: %u + (%u *
            // sats_in_view)\n", $size_wpt + 2, $size_sat);
            // printf("Logging TIME interval: %6.2f s\n", $1 / 10);
            // printf("Logging DISTANCE interval: %6.2f m\n", $1 / 10);
            // printf("Logging SPEED limit: %6.2f km/h\n", $1 / 10);
            // printf("Recording method on memory full: (%u) %s\n", $rec_method,
            // describe_recording_method($rec_method));
            // printf("Log status: (%012b) %s\n", $log_status,
            // describe_log_status($log_status));
            // if ($log_status & $LOG_STATUS_NEED_FORMAT) {
            // printf("WARNING! Log status NEED_FORMAT, log data is not
            // valid!\n");
            // }
            // if ($log_status & $LOG_STATUS_DISABLE) {
            // printf("WARNING! Log status DISABLE_LOG, may too many failed
            // sectors!\n");
            // }
            // printf("Next write address: %u (0x%08X)\n", $next_write_address,
            // $next_write_address);
            // printf("Number of records: %u\n", $expected_records_total);
            // printf("Memory health status (failed sectors mask): %s\n",
            // $fail_sectors);
            // printf(">> Retrieving %u (0x%08X) bytes of log data from
            // device...\n", $bytes_to_read, $bytes_to_read);

            if (options.has("r")) {
                List list = options.valuesOf("r");
                if (list.size() == 3) {
                    System.out
                            .println(">> Setting recording criteria: time, distance, speed\n");
                    int time = (Integer) list.get(0);
                    int speed = (Integer) list.get(1);
                    int distance = (Integer) list.get(2);
                    System.out.println("Setting time interval to " + time);
                    c.setLogTimeInterval(time * 10);
                    System.out.println("Setting speed interval to " + speed);
                    c.setLogSpeedInterval(speed);
                    System.out.println("Setting distance interval to "
                            + distance);
                    c.setLogDistanceInterval(distance * 10);
                } else {
                    System.err.println("parameter for '-r' option is invalid");
                }
            }

            flushOutstandingCmds();

            if (options.has("o")) {
                List list = options.valuesOf("o");
                Iterator iter = list.iterator();

                int newLogFormat = m.getLogFormat();
                while (iter.hasNext()) {
                    String field = ((String) iter.next()).toUpperCase();
                    boolean enableField = true;
                    int logField = 0;
                    if (field.length() > 0) {
                        if (field.charAt(0) == '-') {
                            field = field.substring(1);
                            enableField = false;
                        }
                        if (field.equals("UTC")) {
                            logField = (1 << BT747Constants.FMT_UTC_IDX);
                        } else if (field.equals("VALID")) {
                            logField = (1 << BT747Constants.FMT_VALID_IDX);
                        } else if (field.equals("LATITUDE")) {
                            logField = (1 << BT747Constants.FMT_LATITUDE_IDX);
                        } else if (field.equals("LONGITUDE")) {
                            logField = (1 << BT747Constants.FMT_LONGITUDE_IDX);
                        } else if (field.equals("HEIGHT")) {
                            logField = (1 << BT747Constants.FMT_HEIGHT_IDX);
                        } else if (field.equals("SPEED")) {
                            logField = (1 << BT747Constants.FMT_SPEED_IDX);
                        } else if (field.equals("HEADING")) {
                            logField = (1 << BT747Constants.FMT_HEADING_IDX);
                        } else if (field.equals("DSTA")) {
                            logField = (1 << BT747Constants.FMT_DSTA_IDX);
                        } else if (field.equals("DAGE")) {
                            logField = (1 << BT747Constants.FMT_DAGE_IDX);
                        } else if (field.equals("PDOP")) {
                            logField = (1 << BT747Constants.FMT_PDOP_IDX);
                        } else if (field.equals("HDOP")) {
                            logField = (1 << BT747Constants.FMT_HDOP_IDX);
                        } else if (field.equals("VDOP")) {
                            logField = (1 << BT747Constants.FMT_VDOP_IDX);
                        } else if (field.equals("NSAT")) {
                            logField = (1 << BT747Constants.FMT_NSAT_IDX);
                        } else if (field.equals("SID")) {
                            logField = (1 << BT747Constants.FMT_SID_IDX);
                        } else if (field.equals("ELEVATION")) {
                            logField = (1 << BT747Constants.FMT_ELEVATION_IDX);
                        } else if (field.equals("AZIMUTH")) {
                            logField = (1 << BT747Constants.FMT_AZIMUTH_IDX);
                        } else if (field.equals("SNR")) {
                            logField = (1 << BT747Constants.FMT_SNR_IDX);
                        } else if (field.equals("RCR")) {
                            logField = (1 << BT747Constants.FMT_RCR_IDX);
                        } else if (field.equals("MILLISECOND")) {
                            logField = (1 << BT747Constants.FMT_MILLISECOND_IDX);
                        } else if (field.equals("DISTANCE")) {
                            logField = (1 << BT747Constants.FMT_DISTANCE_IDX);
                        } else if (field.equals("VALID_ONLY")) {
                            logField = (1 << BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX);
                        } else {
                            System.err.println("Field type" + field
                                    + " unknown.");
                        }
                        if (logField != 0) {
                            if (enableField) {
                                newLogFormat |= logField;
                            } else {
                                newLogFormat &= 0xFFFFFFFF ^ logField;
                            }

                        }
                    }
                }
                System.out.println(">> Setting log format\n");

                c.setLogFormat(newLogFormat);
            }
            if (options.has("l")) {
                String arg = options.argumentOf("l").toLowerCase();
                if (arg.equals("on")) {
                    System.out.println(">> Switch recording to ON\n");
                    c.setLoggingActive(true);
                } else if (arg.equals("off")) {
                    System.out.println(">> Switch recording to OFF\n");
                    c.setLoggingActive(false);
                } else {
                    System.err
                            .println("Argument of '-l' must be 'ON' or 'OFF'");
                }
            }

            if (options.has("m")) {
                String arg = options.argumentOf("l").toLowerCase();
                if (arg.equals("overlap")) {
                    System.out
                            .println(">> Setting method OVERLAP on memory full\n");
                    c.setLogOverwrite(true);
                } else if (arg.equals("stop")) {
                    System.out
                            .println(">> Setting method STOP on memory full\n");
                    c.setLogOverwrite(false);
                } else {
                    System.err
                            .println("Argument of '-p' must be 'STOP' or 'OVERLAP'");
                }
            }

            flushOutstandingCmds();

            if (options.has("a") && !(options.has("b"))) {
                c.setDownloadMethod(Model.DOWNLOAD_INCREMENTAL);
                // printf(">> Retrieving %u (0x%08X) bytes of log data from
                // device...\n", $bytes_to_read, $bytes_to_read);
                System.out.println(">> Getting data from device");
                c.startDefaultDownload();

                downloadIsSuccessFull = false;
                while (m.isDownloadOnGoing()) {
                    // Thread t=Thread.currentThread();
                    try {
                        // System.out.println("Waiting for cmds "
                        // + m.getOutstandingCommandsCount());
                        // System.out.flush();
                        progressUpdate();
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Do nothing
                    }
                }

            }

            if (options.has("E") && downloadIsSuccessFull) {
                System.out.println(">> Erasing log memory...\n");
                c.eraseLog();
                waitForErase();
            }

            if (options.has("R") && downloadIsSuccessFull) {
                System.out.println(">> Recover from disable log:"
                        + " ENABLE LOG and FORMAT LOG ALL...\n");
                c.recoveryEraseLog();
                waitForErase();
            }
            c.closeGPS();
        }

        if (options.has("t")) {
            System.out.println("Converting to GPX (trackpoints)");
            c
                    .setTrkPtValid(

                    0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
            c
                    .setWayPtValid(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
            c.setWayPtRCR(0);
            c.setTrkPtRCR(0xFFFFFFFF);
            // The output filename does not depend on the time.
            c.setFileNameBuilder(new BT747FileName() {
                public String getOutputFileName(String baseName,
                        int utcTimeSeconds, String proposedExtension,
                        String proposedTimeSpec) {
                    switch (m.getOutputFileSplitType()) {
                    case 0:
                        return baseName + "_trk" + proposedExtension;
                    default:
                        return baseName + proposedTimeSpec + "_trk" + proposedExtension;
                    }
                }
            });
            int error = convertLog(Model.GPX_LOGTYPE);
            if (error != 0) {
                reportError(c.getLastError(), c.getLastErrorInfo());
            }
        }

        if (options.has("w")) {
            System.out.println("Converting to GPX (waypoints)");
            c
                    .setTrkPtValid(

                    0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
            c
                    .setWayPtValid(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
            c.setWayPtRCR(BT747Constants.RCR_BUTTON_MASK
                    | BT747Constants.RCR_ALL_APP_MASK);
            c.setTrkPtRCR(0);
            c.setFileNameBuilder(new BT747FileName() {
                public String getOutputFileName(String baseName,
                        int utcTimeSeconds, String proposedExtension,
                        String proposedTimeSpec) {
                    switch (m.getOutputFileSplitType()) {
                    case 0:
                        return baseName + "_wpt" + proposedExtension;
                    default:
                        return baseName + proposedTimeSpec + "_wpt" + proposedExtension;
                    }
                }
            });
            int error = convertLog(Model.GPX_LOGTYPE);
            if (error != 0) {
                reportError(c.getLastError(), c.getLastErrorInfo());
            }
        }

        if (options.has("outtype")) {
            List list = options.valuesOf("outtype");
            Iterator iter = list.iterator();

            while (iter.hasNext()) {
                String typeStr = ((String) iter.next()).toUpperCase();
                int type = Model.NO_LOG_LOGTYPE;
                if (typeStr.equals("GPX")) {
                    type = Model.GPX_LOGTYPE;
                } else if (typeStr.equals("NMEA")) {
                    type = Model.NMEA_LOGTYPE;
                } else if (typeStr.equals("GMAP")) {
                    type = Model.GMAP_LOGTYPE;
                } else if (typeStr.equals("CSV")) {
                    type = Model.CSV_LOGTYPE;
                } else if (typeStr.equals("KML")) {
                    type = Model.KML_LOGTYPE;
                } else if (typeStr.equals("KMZ")) {
                    type = Model.KMZ_LOGTYPE;
                } else if (typeStr.equals("PLT")) {
                    type = Model.PLT_LOGTYPE;
                } else if (typeStr.equals("TRK")) {
                    type = Model.TRK_LOGTYPE;
                } else {
                    System.err.println("Unknown outtype '" + typeStr + "'");
                }
                if (type != Model.NO_LOG_LOGTYPE) {
                    System.out.println("Converting to " + typeStr);
                    c
                            .setTrkPtValid(

                            0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
                    c
                            .setWayPtValid(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
                    c.setWayPtRCR(BT747Constants.RCR_BUTTON_MASK
                            | BT747Constants.RCR_ALL_APP_MASK);
                    c.setTrkPtRCR(0xFFFFFFFF);
                    c.setFileNameBuilder(new BT747FileName() {
                        public String getOutputFileName(String baseName,
                                int utcTimeSeconds, String proposedExtension,
                                String proposedTimeSpec) {
                            switch (m.getOutputFileSplitType()) {
                            case 0:
                                return baseName + proposedExtension;
                            default:
                                return baseName + proposedTimeSpec + proposedExtension;
                            }
                        }
                    });
                    int error = convertLog(type);
                    if (error != 0) {
                        reportError(c.getLastError(), c.getLastErrorInfo());
                    }
                }
            }
        }
        System.exit(0);
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String args[]) {
        OptionParser parser = new OptionParser() {
            {
                accepts("h", "Displays help");
                accepts("a", "Read all the log memory (overlapped data)");
                accepts(
                        "b",
                        "Do not read device, read a previously saved file."
                                + "The file type is selected according to the filename extension."
                                + "Recognized file extensions are .bin, .csv, .trl,"
                                + ".nmea, .nme, .nma, .txt, .log, .sr .")
                        .withRequiredArg().describedAs("filename.bin").ofType(
                                String.class);
                accepts("d", "Debug level: 0..2").withRequiredArg()
                        .describedAs("DEBUG_LEVEL").ofType(Integer.class);

                accepts("E", "Erase data log memory");
                accepts("f", "Base name for saved files (.bin and other)")
                        .withRequiredArg().describedAs("filename").ofType(
                                String.class);
                accepts("l", "Turn logging ON/OFF").withRequiredArg()
                        .describedAs("(on|off)").ofType(String.class);
                accepts("m", "Set STOP/OVERLAP recording method on memory full")
                        .withRequiredArg().describedAs("(stop|overlap)")
                        .ofType(String.class);
                accepts(
                        "o",
                        "Enable or disable log fields "
                                + "(FIELD1,-FIELD2,...), available fields: "
                                + "UTC,VALID,LATITUDE,LONGITUDE,HEIGHT,SPEED,HEADING,"
                                + "DSTA,DAGE,PDOP,HDOP,VDOP,"
                                + "NSAT,SID,ELEVATION,AZIMUTH,SNR,RCR,MILLISECOND,"
                                + "DISTANCE,VALID_ONLY").withRequiredArg()
                        .describedAs("log_format").withValuesSeparatedBy(',')
                        .ofType(String.class);
                accepts("p", "Communication port, default: /dev/ttyUSB0")
                        .withRequiredArg().describedAs("port").ofType(
                                String.class);
                accepts("R",
                        "Recover from disabled log: erase data and reset recording criteria");
                accepts("r", "Set logging criteria (zero to disable)")
                        .withRequiredArg().describedAs("time:distance:speed")
                        .ofType(Integer.class).withValuesSeparatedBy(':');
                accepts("s", "Serial port speed, default 115200 baud")
                        .withRequiredArg().describedAs("speed").ofType(
                                Integer.class);
                accepts("t", "Create a gpx file with tracks");
                accepts("v", "Print BT747 version and exit");
                accepts("w", "Create a gpx file with waypoints");
                accepts(
                        "outtype",
                        "Create a gpx file of type NMEA, GPX, GMAP, KML, KMZ, CSV, PLT, TRK."
                                + "More than one format can be specified when separated with ','")
                        .withRequiredArg().describedAs("OUTPUTTYPE")
                        .withValuesSeparatedBy(',');
                accepts("UTC", "Define UTC offset to apply to output file")
                        .withRequiredArg().describedAs("UTCoffset").ofType(
                                Integer.class);
                accepts("device",
                        "Make sure the raw bin file is correctly interpreted (DEFAULT, HOLUX).")
                        .withRequiredArg().describedAs("DEVICE");
                accepts("trkptinfo",
                        "Add record information for each trackpoint.");
                accepts("trkptname",
                        "Give each trackpoint a name (based on time)");
                accepts("color",
                        "Color to use for tracks (HEX RGB value, ex 00FF00)")
                        .withRequiredArg().describedAs("HEXCOLOR");
                accepts("badcolor",
                        "Color to use for 'bad part' in tracks  (HEX RGB value), ex 00FFFF")
                        .withRequiredArg().describedAs("HEXCOLOR");
                ;
                accepts("splittype", "The way to split the input data: NOSPLIT, DAY or TRACK")
                .withRequiredArg().describedAs("SPLITTYPE");
                accepts("timesplit", "Time separation in minutes needed for track segment or track separation.")
                .withRequiredArg().describedAs("MINUTES").ofType(
                        Integer.class);
                accepts("height", "Adjust height.  According to formats when 'AUTOMATIC'," +
                		"WGS84 height (elevation) to MSL (Mean Sea Level) when 'MSL'," +
                		"MSL to WGS84 when 'WGS84'").withRequiredArg().describedAs("AUTOMATIC|KEEP|WGS84_TO_MSL|MSL_TO_WGS84");
            }
        };

        try {
            final OptionSet options = parser.parse(args);
            System.out.println("BT747 Cmd V" + bt747.Version.VERSION_NUMBER
                    + " build " + bt747.Version.BUILD_STR + " GPL V3 LICENSE");
            if (options.has("h") || args.length == 0) {
                parser.printHelpOn(System.out);
            } else if (options.has("v")) {
            } else {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    Model m = new Model();
                    Controller c = new Controller(m);

                    public void run() {
                        new BT747cmd(m, c, options);
                    }
                });
                // parser.printHelpOn(System.err);
            }
        } catch (Exception ex) {
            try {
                parser.printHelpOn(System.err);
            } catch (Exception e) {
            } finally {

            }
            System.err.println("====");
            System.err.println(ex.getMessage());
        }
    }

    private final boolean getEraseOngoing() {
        synchronized (eraseOngoing) {
            return eraseOngoing.booleanValue();
        }
    }

    private final void setEraseOngoing(final Boolean eraseOngoing) {
        synchronized (eraseOngoing) {
            this.eraseOngoing = Boolean.valueOf(eraseOngoing);
        }
    }

    private void initAppSettings() {
        // AppSettings.defaultBaseDirPath = java.lang.System
        // .getProperty("user.home");

        try {
            AppSettings.defaultBaseDirPath = (new File(".")).getCanonicalPath();
        } catch (Exception e) {
            // TODO: handle exception
        }

        Settings.setAppSettings(new String(new byte[2048]));
        m.init();
    }

}
