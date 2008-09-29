/*
 * SimpleExample.java
 *
 * Created on 23 mars 2008, 10:37
 * 
 * To run:
 *   Path must include RXTX.  
 *   In Eclipse, set in environment, for example (on windows):
 *     PATH  ${project_loc:BT747}/lib/rxtx-2.1-7-bins-r2/Windows/i368-mingw32/;%PATH%
 *   classpath must include:
 *      libBT747.jar
 *      waba_forj2se.jar (if the libBT747 is a debug library)
 *      collections-superwaba.jar (if the libBT747 is a debug library).
 */
package bt747.j2se_view;

import gps.BT747Constants;
import gps.connection.GPSrxtx;
import gps.log.GPSRecord;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Interface;
import bt747.sys.Settings;

/**
 * 
 * @author Mario De Weerd
 */
public class BT747cmd implements bt747.model.ModelListener {

    static {
        Interface
                .setJavaTranslationInterface(new net.sf.bt747.j2se.system.JavaTranslations());
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

    public void setController(Controller c) {
        this.c = c; // Should check that c is an AppController or do it
    }

    public void setModel(Model m) {
        if (this.m != null) {
            this.m.removeListener(this);
        }
        this.m = m;

        //this.m.addListener(this);
        //initAppData();
    }

    private void initAppData() {
        // This is an example without an user interface.
        // BT747Main is an example with a user interface.

        // Set up the paths
        // Common to in/out
        c.setBaseDirPath("/BT747");
        // Input is "/BT747/BT747_sample.bin"
        c.setLogFileRelPath("BT747_sample.bin");
        // Output is "/BT747/GPSDATA*"
        c.setOutputFileRelPath("GPSDATA");

        // Activate some debug.
        c.setDebug(true);
        // Make the connection
        c.openFreeTextPort("COM4");
        // Successfull connection will result in modelEvent.
        // The next release will have an isConnected method in the model.

        // In this example we wait for the ModelEvent#CONNECTED
        // in modelEvent
        // Could also check isConnected.
        // if(m.isConnected()) {
        // afterConnection();
        // }
    }

    private void afterConnection() {
        // We are connected, start a download
        // Disable incremental download (The possible overwrite request is
        // automatically authorised).
        c.setIncremental(true);
        System.out.println("Incremental setting done");
        System.out.flush();
        // If the amount of data in the device is unknown, the download will not
        // start.
        // Wait until all data is retrieved from device
        // (Will/should move this to the controller).
        System.out.println("Outstanding cmds "
                + m.getOutstandingCommandsCount());
        System.out.flush();
        while (m.getOutstandingCommandsCount() > 0) {
            // Thread t=Thread.currentThread();
            try {
                System.out.println("Waiting for cmds "
                        + m.getOutstandingCommandsCount());
                System.out.flush();
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
                // Do nothing
            }
        }
        // Print out the memory in use
        System.out.println(m.logMemUsed() + " bytes in use ("
                + m.logMemUsedPercent() + "%)");

        // c.setChunkSize(0x10000); // The number of bytes per request.
        c.setChunkSize(0x800); // Small to see debug
        // c.setDownloadTimeOut(3500); // Timeout for response
        c.startDefaultDownload();
    }

    private void handleDownloadEnded() {
        int error;
        // In the example, we disconnect.
        c.closeGPS();
        // If the binary corresponds to a holux log, one might want to force
        // holux recognition.
        // c.setForceHolux241(true);

        // After the download we convert to CSV
        // Uses previously stored settings
        error = c.doConvertLog(Model.CSV_LOGTYPE);
        if (error != 0) {
            reportError(c.getLastError(), c.getLastErrorInfo());
        }

        // And we can do something more complex
        // We do not like the previously stored settings or we want to use our
        // own.
        //
        // We select all positions that do not have an invalid fix or estimated
        // fix.
        c
                .setTrkPtValid(

                0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
        c
                .setWayPtValid(0xFFFFFFFF ^ (BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK));
        // Waypoints only when button pressed.
        c.setWayPtRCR(BT747Constants.RCR_BUTTON_MASK);
        // Trackpoints : anything
        c.setTrkPtRCR(BT747Constants.RCR_BUTTON_MASK);
        // To limit the output data, we only select lat,lon and height.
        c.setIntOpt(Model.FILEFIELDFORMAT,
                (1 << BT747Constants.FMT_LATITUDE_IDX)
                        | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                        | (1 << BT747Constants.FMT_HEIGHT_IDX));
        error = c.doConvertLog(Model.GPX_LOGTYPE);
        if (error != 0) {
            reportError(c.getLastError(), c.getLastErrorInfo());
        }

        // We can do the same thing to an array for internal treatment
        GPSRecord[] positions = c.doConvertLogToTrackPoints();
        if (positions == null) {
            // Error occured
            reportError(c.getLastError(), c.getLastErrorInfo());
        } else {
            // Print the first ten positions
            for (int i = 0; i < positions.length && i < 10; i++) {
                GPSRecord record = positions[i];
                System.out.println("Position " + i + ":" + record.latitude
                        + "," + record.longitude);
            }
        }

        // Store the used settings
        c.saveSettings();
        // Dirty exit from example
        System.exit(0);
    }

    private void reportError(final int error, final String errorInfo) {
        switch (error) {
        case BT747Constants.ERROR_COULD_NOT_OPEN:
            System.err.println("Could not open " + errorInfo);
            break;
        case BT747Constants.ERROR_NO_FILES_WERE_CREATED:
            System.err.println("No files were created ");
            break;
        case BT747Constants.ERROR_READING_FILE:
            System.err.println("Problem reading" + errorInfo);
            break;
        default:
            break;
        }
    }

    public void modelEvent(ModelEvent e) {
        // TODO Auto-generated method stub
        int type = e.getType();
        if (type == ModelEvent.GPRMC) {
            // updateRMCData((GPSRecord) e.getArg());
        } else if (type == ModelEvent.DATA_UPDATE) {
        } else if (type == ModelEvent.GPGGA) {
            // updateGPSData((GPSRecord) e.getArg());
        } else if (type == ModelEvent.LOG_FORMAT_UPDATE) {
            // updateLogFormatData();
        } else if (type == ModelEvent.LOGFILEPATH_UPDATE) {
            // getRawLogFilePath();
        } else if (type == ModelEvent.OUTPUTFILEPATH_UPDATE) {
            // getOutputFilePath();
        } else if (type == ModelEvent.WORKDIRPATH_UPDATE) {
            // getWorkDirPath();
        } else if (type == ModelEvent.INCREMENTAL_CHANGE) {
            // getIncremental();
        } else if (type == ModelEvent.TRK_VALID_CHANGE
                || type == ModelEvent.TRK_RCR_CHANGE
                || type == ModelEvent.WAY_VALID_CHANGE
                || type == ModelEvent.WAY_RCR_CHANGE) {
            // updateGuiLogFilterSettings();
        } else if (type == ModelEvent.CONVERSION_STARTED) {
            // conversionStartTime = System.currentTimeMillis();
        } else if (type == ModelEvent.CONVERSION_ENDED) {
            // lbConversionTime
            // .setText("Time to convert: "
            // + ((int) (System.currentTimeMillis() - conversionStartTime))
            // + " ms");
            // lbConversionTime.setVisible(true);
        } else if (type == ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY) {
            // When the data on the device is not the same, overwrite
            // automatically.
            System.out
                    .println("Overwriting previously downloaded data that looks different.");
            c.replyToOkToOverwrite(true);
        } else if (type == ModelEvent.DOWNLOAD_STATE_CHANGE
                || type == ModelEvent.LOG_DOWNLOAD_STARTED) {
            progressUpdate();
        } else if (type == ModelEvent.LOG_DOWNLOAD_DONE) {
            progressUpdate();
            handleDownloadEnded();
        } else if (type == ModelEvent.DEBUG_MSG) {
            System.out.flush();
            System.err.println((String) e.getArg());
            System.err.flush();
            progressUpdate();
        } else if (type == ModelEvent.CONNECTED) {
            // btConnect.setText("Disconnect");
            // btConnectFunctionIsConnect = false;

            // Launching in another thread - not really needed.
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    afterConnection();
                }
            });

        } else if (type == ModelEvent.DISCONNECTED) {
            // btConnect.setText("Connect");
            // btConnectFunctionIsConnect = true;
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
                }
                if ((percent % 10) == 0) {
                    System.out.print("#" + percent + "#");
                    System.out.flush();
                }
            }
        }
    }

    private void handleOptions(final OptionSet options) {
        // Set up the paths
        // Common to in/out
        c.setBaseDirPath(".");

        // Next line gets arguments not related to option
        options.nonOptionArguments();

        c.setDebug(true);
        
        // Input is "/BT747/BT747_sample.bin"
        if (options.has("b")) {
            c.setLogFileRelPath(options.argumentOf("b"));
        } else {
            c.setLogFileRelPath("BT7T7_log.bin");
        }

        if (options.has("s")) {
            c.setBaudRate((((Integer) options.valueOf("s")).intValue()));
        }

        if (options.has("p")) {
            c.setFreeTextPort((String) options.valueOf("p"));
        }
        
        c.connectGPS();
        
        if(m.isConnected()) {
        
        // Make connection

        if (options.has("l")) {
            String arg = options.argumentOf("l").toLowerCase();
            if (arg.equals("on")) {
                c.startLog();
            } else if (arg.equals("off")) {
                c.stopLog();
            } else {
                System.err.println("Argument of '-l' must be 'ON' or 'OFF'");
            }
        }

        if (options.has("m")) {
            String arg = options.argumentOf("l").toLowerCase();
            if (arg.equals("overlap")) {
                c.setLogOverwrite(true);
            } else if (arg.equals("stop")) {
                c.setLogOverwrite(false);
            } else {
                System.err.println("Argument of '-p' must be 'STOP' or 'OVERLAP'");
            }
        }

        if (options.has("E")) {
            c.eraseLog();
        }
        }
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
                accepts("b",
                        "Do not read device, read a previously saved .bin file")
                        .withRequiredArg().describedAs("filename.bin").ofType(
                                File.class);
                accepts("d", "Debug level: 0..7").withRequiredArg()
                        .describedAs("DEBUG_LEVEL").ofType(Integer.class);

                accepts("E", "Erase data log memory");
                accepts("f", "Base name for saved files (.bin and .gpx)")
                        .withRequiredArg().describedAs("filename").ofType(
                                File.class);
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
                                + "DISTANCE").withRequiredArg().describedAs(
                        "log_format").withValuesSeparatedBy(',').ofType(
                        String.class);
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
            }
        };

        try {
            final OptionSet options = parser.parse(args);
            if (options.has("h"))
                parser.printHelpOn(System.out);
            else {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    Model m = new Model();
                    Controller c = new Controller(m);

                    public void run() {
                        new BT747cmd(m, c, options);
                    }
                });
                parser.printHelpOn(System.err);
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
}
