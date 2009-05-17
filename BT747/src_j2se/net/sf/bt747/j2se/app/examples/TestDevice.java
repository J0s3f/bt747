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
import gps.mvc.commands.mtk.MtkBinCommand;
import gps.mvc.commands.mtk.SetMtkBinModeCommand;
import gps.mvc.commands.mtk.SetNmeaModeCommand;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;
import net.sf.bt747.gps.mtk.agps.AgpsUploadHandler;
import net.sf.bt747.j2se.app.agps.J2SEAGPS;

import bt747.model.AppSettings;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.Settings;
import bt747.sys.interfaces.JavaLibImplementation;

/**
 * 
 * @author Mario De Weerd
 */
public class TestDevice implements bt747.model.ModelListener {

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
    public TestDevice() {
    }

    public TestDevice(Model m, Controller c) {
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
        m.init(); // Initialise the app model.
        // Activate some debug.
        c.setDebug(true);
        c.setAutoFetch(false); // Not sending commands outside the program
        // control.
        // Make the connection
        c.openFreeTextPort("COM4");
        // Successfull connection will result in modelEvent.
        // The next release will have an isConnected method in the model.

        // In this example we wait for the ModelEvent#CONNECTED
        // in modelEvent
        // Could also check isConnected.
        if (m.isConnected()) {
            // afterConnection();
        }
    }

    private void getOutstandingCmds() {
        while (m.getOutstandingCommandsCount() > 0) {
            // Thread t=Thread.currentThread();
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                Generic.debug("", e);
                // Do nothing
            }
        }
    }

    private void testSingleNMEA(final Object cmd) {
        c.sendCmd(cmd);
        getOutstandingCmds();
    }

    private void afterConnection() {

        /** ************************************************************** */
        /*********************************************************************
         * Currently using this example to test the AGPS programming mode.
         ********************************************************************/
        Generic.setDebugLevel(2); // We are debugging, so raising debug
        // level.
        getOutstandingCmds();
        c.setStringOpt(Model.OUTPUTDIRPATH, ".");
        c.setDebugConn(true);

        // GET AGPS Data
        //J2SEAGPS agpsFetcher = new J2SEAGPS();
        //agpsFetcher.getBytesFromUrl("ftp://tsi0001:passwd@www.transystem.com.tw/MTK7d.EPO")
        byte[] agpsData = J2SEAGPS.getBytesFromUrl("http://bt747.free.fr/MTK7d.EPO");
        
        // Initialise the handler.
        AgpsUploadHandler handler = new AgpsUploadHandler();
        handler.setAgpsData(agpsData);
        // Handler initialised.

        c.setDeviceOperationHandler(handler);
        
        /* Enter AGPS programming mode */
        // Checked: the sent packet corresponds to what is needed. (seen in a
        // trace)
        c.sendCmd(new SetMtkBinModeCommand());

        // getOutstandingCmds();
        if (false) {
            /* Wild guess at a packet to see if that works too. */
            /*
             * Exluded because entering and exiting the mode does not work on
             * my device
             */
            byte[] payload;
            payload = new byte[4];
            payload[0] = (byte) (BT747Constants.PMTK_LOG_Q);
            payload[1] = (byte) (BT747Constants.PMTK_LOG_Q >> 8);
            payload[2] = (byte) (BT747Constants.PMTK_LOG_VERSION);
            payload[3] = (byte) (BT747Constants.PMTK_LOG_VERSION >> 8);
            c.sendCmd(new MtkBinCommand(new MtkBinTransportMessageModel(
                    BT747Constants.PMTK_CMD_LOG, payload)));
            // private final void reqMtkLogVersion() {
            // sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
            // + BT747Constants.PMTK_LOG_Q + ","
            // + BT747Constants.PMTK_LOG_VERSION_STR);
            // }
        }

        /* Enter the NMEA mode again */
        // Checked: the sent packet corresponds to what is needed. (seen in a
        // trace)
        c.sendCmd(new SetNmeaModeCommand());

        /*
         * Perform some other request so that Outstanding commands has to wait
         * at least for the response to this one and get the communication of
         * the previous ones in the serial protocol monitor.
         */
        c.reqDGPSMode();

        getOutstandingCmds();
        System.exit(0); // Testing just part of it so cowardly exiting
                        // program.
        
        // Next is a list of commands to see what the device replies.
        
        // testSingleNMEA("PMTK391"); // Resets the device

        // testSingleNMEA("PMTK392"); // Resets the device

        // testSingleNMEA("PMTK395");

        // testSingleNMEA("PMTK399");

        testSingleNMEA("PMTK402");

        testSingleNMEA("PMTK403");

        testSingleNMEA("PMTK404");

        testSingleNMEA("PMTK405");

        testSingleNMEA("PMTK406");

        testSingleNMEA("PMTK407");

        testSingleNMEA("PMTK408");

        testSingleNMEA("PMTK409");

        testSingleNMEA("PMTK410");

        testSingleNMEA("PMTK411");

        testSingleNMEA("PMTK412");

        testSingleNMEA("PMTK415");

        testSingleNMEA("PMTK416");

        testSingleNMEA("PMTK417");

        testSingleNMEA("PMTK418");

        testSingleNMEA("PMTK412");

        testSingleNMEA("PMTK421");

        testSingleNMEA("PMTK435");

        testSingleNMEA("PMTK445");

        testSingleNMEA("PMTK450");

        testSingleNMEA("PMTK451");

        testSingleNMEA("PMTK452");

        testSingleNMEA("PMTK453");

        testSingleNMEA("PMTK454");

        testSingleNMEA("PMTK455");

        testSingleNMEA("PMTK456");

        testSingleNMEA("PMTK457");

        testSingleNMEA("PMTK458");

        testSingleNMEA("PMTK459");

        testSingleNMEA("PMTK460");

        testSingleNMEA("PMTK461");

        testSingleNMEA("PMTK462");

        testSingleNMEA("PMTK463");

        testSingleNMEA("PMTK464");

        testSingleNMEA("PMTK465");

        testSingleNMEA("PMTK466");

        testSingleNMEA("PMTK467");

        testSingleNMEA("PMTK472");

        testSingleNMEA("PMTK473");

        testSingleNMEA("PMTK474");

        testSingleNMEA("PMTK480");

        testSingleNMEA("PMTK481");

        testSingleNMEA("PMTK491");

        testSingleNMEA("PMTK492");

        testSingleNMEA("PMTK495");

        testSingleNMEA("PMTK499");

        testSingleNMEA("PMTK600");

        testSingleNMEA("PMTK601");

        testSingleNMEA("PMTK603");

        testSingleNMEA("PMTK605");

        testSingleNMEA("PMTK650");

        testSingleNMEA("PMTK651");

        testSingleNMEA("PMTK711");

        testSingleNMEA("PMTK712");

        testSingleNMEA("PMTK713");

        testSingleNMEA("PMTK714");

        testSingleNMEA("PMTK715");

        testSingleNMEA("PMTK716");

        testSingleNMEA("PMTK717");

        testSingleNMEA("PMTK718");

        testSingleNMEA("PMTK800");

        testSingleNMEA("PMTK801");

        testSingleNMEA("PMTK802");

        testSingleNMEA("PMTK803");

        testSingleNMEA("PMTK804");

        testSingleNMEA("PMTK806");

        testSingleNMEA("PMTK810");

        testSingleNMEA("PMTK815");

        System.exit(0);
    }

    private void handleDownloadEnded() {
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
     *                the command line arguments
     */
    public static void main(String args[]) {
        java.lang.Thread
                .setDefaultUncaughtExceptionHandler(new java.lang.Thread.UncaughtExceptionHandler() {

                    public void uncaughtException(Thread t, Throwable e) {
                        Generic.debug("Uncaught Exception", e);
                    }
                });

        Settings.setAppSettings(new String(new byte[AppSettings.SIZE]));

        java.awt.EventQueue.invokeLater(new Runnable() {

            final Model m = new Model();
            final Controller c = new Controller(m);

            public void run() {
                new TestDevice(m, c);
            }
        });
    }
}
