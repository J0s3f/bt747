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

import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Generic;

/**
 * 
 * @author Mario De Weerd
 */
public class TestDevice implements bt747.model.ModelListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Model m;
    Controller c;

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
        // Activate some debug.
        c.setDebug(true);
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
                Generic.debug("",e);
                // Do nothing
            }
        }
    }
    private void afterConnection() {
        getOutstandingCmds();
        // c.sendNMEA("PMTK391");  // Resets
        getOutstandingCmds();
        // c.sendNMEA("PMTK392");   // Resets
        getOutstandingCmds();
        //c.sendNMEA("PMTK395");
        getOutstandingCmds();
        //c.sendNMEA("PMTK399");
        getOutstandingCmds();
        c.sendNMEA("PMTK402");
        getOutstandingCmds();
        c.sendNMEA("PMTK403");
        getOutstandingCmds();
        c.sendNMEA("PMTK404");
        getOutstandingCmds();
        c.sendNMEA("PMTK405");
        getOutstandingCmds();
        c.sendNMEA("PMTK406");
        getOutstandingCmds();
        c.sendNMEA("PMTK407");
        getOutstandingCmds();
        c.sendNMEA("PMTK408");
        getOutstandingCmds();
        c.sendNMEA("PMTK409");
        getOutstandingCmds();
        c.sendNMEA("PMTK410");
        getOutstandingCmds();
        c.sendNMEA("PMTK411");
        getOutstandingCmds();
        c.sendNMEA("PMTK412");
        getOutstandingCmds();
        c.sendNMEA("PMTK415");
        getOutstandingCmds();
        c.sendNMEA("PMTK416");
        getOutstandingCmds();
        c.sendNMEA("PMTK417");
        getOutstandingCmds();
        c.sendNMEA("PMTK418");
        getOutstandingCmds();
        c.sendNMEA("PMTK412");
        getOutstandingCmds();
        c.sendNMEA("PMTK421");
        getOutstandingCmds();
        c.sendNMEA("PMTK435");
        getOutstandingCmds();
        c.sendNMEA("PMTK445");
        getOutstandingCmds();
        c.sendNMEA("PMTK450");
        getOutstandingCmds();
        c.sendNMEA("PMTK451");
        getOutstandingCmds();
        c.sendNMEA("PMTK452");
        getOutstandingCmds();
        c.sendNMEA("PMTK453");
        getOutstandingCmds();
        c.sendNMEA("PMTK454");
        getOutstandingCmds();
        c.sendNMEA("PMTK455");
        getOutstandingCmds();
        c.sendNMEA("PMTK456");
        getOutstandingCmds();
        c.sendNMEA("PMTK457");
        getOutstandingCmds();
        c.sendNMEA("PMTK458");
        getOutstandingCmds();
        c.sendNMEA("PMTK459");
        getOutstandingCmds();
        c.sendNMEA("PMTK460");
        getOutstandingCmds();
        c.sendNMEA("PMTK461");
        getOutstandingCmds();
        c.sendNMEA("PMTK462");
        getOutstandingCmds();
        c.sendNMEA("PMTK463");
        getOutstandingCmds();
        c.sendNMEA("PMTK464");
        getOutstandingCmds();
        c.sendNMEA("PMTK465");
        getOutstandingCmds();
        c.sendNMEA("PMTK466");
        getOutstandingCmds();
        c.sendNMEA("PMTK467");
        getOutstandingCmds();
        c.sendNMEA("PMTK472");
        getOutstandingCmds();
        c.sendNMEA("PMTK473");
        getOutstandingCmds();
        c.sendNMEA("PMTK474");
        getOutstandingCmds();
        c.sendNMEA("PMTK480");
        getOutstandingCmds();
        c.sendNMEA("PMTK481");
        getOutstandingCmds();
        c.sendNMEA("PMTK491");
        getOutstandingCmds();
        c.sendNMEA("PMTK492");
        getOutstandingCmds();
        c.sendNMEA("PMTK495");
        getOutstandingCmds();
        c.sendNMEA("PMTK499");
        getOutstandingCmds();
        c.sendNMEA("PMTK600");
        getOutstandingCmds();
        c.sendNMEA("PMTK601");
        getOutstandingCmds();
        c.sendNMEA("PMTK603");
        getOutstandingCmds();
        c.sendNMEA("PMTK605");
        getOutstandingCmds();
        c.sendNMEA("PMTK650");
        getOutstandingCmds();
        c.sendNMEA("PMTK651");
        getOutstandingCmds();
        c.sendNMEA("PMTK711");
        getOutstandingCmds();
        c.sendNMEA("PMTK712");
        getOutstandingCmds();
        c.sendNMEA("PMTK713");
        getOutstandingCmds();
        c.sendNMEA("PMTK714");
        getOutstandingCmds();
        c.sendNMEA("PMTK715");
        getOutstandingCmds();
        c.sendNMEA("PMTK716");
        getOutstandingCmds();
        c.sendNMEA("PMTK717");
        getOutstandingCmds();
        c.sendNMEA("PMTK718");
        getOutstandingCmds();
        c.sendNMEA("PMTK800");
        getOutstandingCmds();
        c.sendNMEA("PMTK801");
        getOutstandingCmds();
        c.sendNMEA("PMTK802");
        getOutstandingCmds();
        c.sendNMEA("PMTK803");
        getOutstandingCmds();
        c.sendNMEA("PMTK804");
        getOutstandingCmds();
        c.sendNMEA("PMTK806");
        getOutstandingCmds();
        c.sendNMEA("PMTK810");
        getOutstandingCmds();
        c.sendNMEA("PMTK815");
        getOutstandingCmds();
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
//        } else if (type == ModelEvent.DEBUG_MSG) {
//            System.out.flush();
//            System.err.println((String) e.getArg());
//            System.err.flush();
//            progressUpdate();
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
     *            the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            Model m = new Model();
            Controller c = new Controller(m);

            public void run() {
                new TestDevice(m, c);
            }
        });
    }
}
