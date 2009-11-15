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
/*
 * SimpleExample.java
 * 
 * Created on 23 mars 2008, 10:37
 * 
 * To run: Path must include RXTX. In Eclipse, set in environment, for example
 * (on windows): PATH
 * ${project_loc:BT747}/lib/rxtx-2.1-7-bins-r2/Windows/i368-mingw32/;%PATH%
 * classpath must include: libBT747.jar waba_forj2se.jar (if the libBT747 is a
 * debug library) collections-superwaba.jar (if the libBT747 is a debug
 * library).
 */
package net.sf.bt747.j2se.app.examples;

import gps.BT747Constants;
import gps.connection.GPSrxtx;
import gps.log.GPSRecord;

import bt747.model.AppSettings;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.JavaLibBridge;
import bt747.sys.Settings;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.JavaLibImplementation;

/**
 * 
 * @author Mario De Weerd
 */
public class SimpleExample implements bt747.model.ModelListener {

    /**
     * Initialize the bridge to the platform. Required for BT747 that runs on
     * at least 3 different platforms.
     */
    static {
        /* Get instance of implementation */
        final JavaLibImplementation imp = new net.sf.bt747.j2se.system.J2SEJavaTranslations();
        /* Declare the implementation */
        JavaLibBridge.setJavaLibImplementation(imp);
        /* Set the serial port class instance to use (also system specific). */
        GPSrxtx.setDefaultGpsPortInstance(new gps.connection.GPSRxTxPort());
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Model m;
    private Controller c;

    /** Creates new form SimpleExample */
    public SimpleExample() {
    }

    public SimpleExample(Model m, Controller c) {
        setController(c);
        setModel(m);
    }

    public void setController(Controller c) {
        this.c = c; // Should check that c is an AppController or do it
    }

    public void setModel(Model m) {
        if (this.m != null) {
            this.m.removeListener(this);
        }
        this.m = m;
        this.m.addListener(this);
        initAppData();
    }

    private void initAppData() {
        m.init(); // Initialise the app
        // This is an example without an user interface.
        // BT747Main is an example with a user interface.

        // Set up the paths
        // Common to in/out
        c.setStringOpt(AppSettings.OUTPUTDIRPATH, "/BT747");

        // Input is "/BT747/BT747_sample.bin"
        c.setStringOpt(AppSettings.LOGFILEPATH, "/BT747/BT747_sample.bin");
        // setStringOpt(ModelEvent.LOGFILEPATH_UPDATE, logFile, C_LOGFILE_IDX,
        // C_LOGFILE_SIZE);
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
        // If the amount of data in the device is unknown, the download will
        // not
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
        // We select all positions that do not have an invalid fix or
        // estimated
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
                System.out.println("Position " + i + ":"
                        + record.getLatitude() + "," + record.getLongitude());
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
        try {
            int type = e.getType();
            if (type == ModelEvent.GPRMC) {
                // updateRMCData((GPSRecord) e.getArg());
            } else if (type == ModelEvent.GPGGA) {
                // updateGPSData((GPSRecord) e.getArg());
            } else if (type == ModelEvent.UPDATE_LOG_FORMAT) {
                // updateLogFormatData();
                // } else if (type == ModelEvent.LOGFILEPATH_UPDATE) {
                // // getRawLogFilePath();
                // } else if (type == ModelEvent.OUTPUTFILEPATH_UPDATE) {
                // // getOutputFilePath();
                // } else if (type == ModelEvent.WORKDIRPATH_UPDATE) {
                // // getWorkDirPath();
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
                // + ((int) (System.currentTimeMillis() -
                // conversionStartTime))
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
                // } else if (type == ModelEvent.DEBUG_MSG) {
                // System.out.flush();
                // System.err.println((String) e.getArg());
                // System.err.flush();
                // progressUpdate();
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
        } catch (BT747Exception e2) {
            // TODO: handle exception
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

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String args[]) {
        Settings.setAppSettings(new String(new byte[AppSettings.SIZE]));
        java.awt.EventQueue.invokeLater(new Runnable() {
            private Model m = new Model();
            private Controller c = new Controller(m);

            public void run() {
                new SimpleExample(m, c);
            }
        });
    }
}
