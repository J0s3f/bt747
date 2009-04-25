/**
 * 
 */
package gps.mvc;

import gps.connection.GPSrxtx;

import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Thread;

/**
 * Refactoring ongoing
 * 
 * @author Mario
 * 
 */
public class Controller implements BT747Thread {

    private Model gpsM;

    private final GPSLinkHandler handler;
    private final MTKLogDownloadHandler mtkLogHandler;
    
    public final static Controller getInstance(GPSrxtx gpsRxTx) {
        return getInstance(new Model(gpsRxTx));
    }

    public final static Controller getInstance(Model model) {
        return new Controller(model);
    }

    private Controller(Model model) {
        this.gpsM = model;
        this.handler = model.getHandler();
        this.mtkLogHandler = model.getMtkLogHandler();
    }

    public final Model getModel() {
        return gpsM;
    }
    

    private boolean eraseRequested;
    /**
     * Erase the log.
     */
    public final void eraseLog() {
        eraseRequested = true;
        gpsM.setEraseOngoing(true);  // For popup
    }
    
    /**
     * The log is being erased - the user request to abandon waiting for the
     * end of this operation.
     */
    public final void stopErase() {
        eraseRequested = false;
        mtkLogHandler.stopErase();
    }



    /**
     * Start the timer To be called once the port is opened. The timer is used
     * to launch functions that will check if there is information on the
     * serial connection or to send to the GPS device.
     */
    public final void initConnection() {
        // TODO: set up thread in gpsRxTx directly (through controller)
        if (handler.isConnected()) {
            nextRun = Generic.getTimeStamp() + 300; // Delay before first
            resetAvailable();
            handler.initConnected();
            // transaction
            Generic.addThread(this, false);
        }
    }
    
    private void resetAvailable() {
        gpsM.setAllUnavailable();
        nextValueToCheck = 0;
    }

    private int nextValueToCheck = 0;

    /**
     * Called regularly to check if values are available and request them.
     */
    private void checkNextAvailable() {
        int next = nextValueToCheck;
        gpsM.setDataNeeded(next);
        next += 1;
        if (next > Model.DATA_LAST_INDEX) {
            next = 0;
        }
        nextValueToCheck = next;
    }

    /*************************************************************************
     * Thread methods implementation
     */

    private int nextRun = 0;
    private int nextAvailableRun = 0;

    /*
     * (non-Javadoc)
     * 
     * @see waba.sys.Thread#run()
     */
    public final void run() {
        final int timeStamp = Generic.getTimeStamp();
        if (timeStamp >= nextRun) {
            nextRun = timeStamp + 10;
            int loopsToGo = 0; // Setting to 0 for more responsiveness
            if (handler.isConnected()) {
                mtkLogHandler.notifyRun();
                String[] lastResponse;
                do {
                    lastResponse = handler.getResponse();
                    if (lastResponse != null) {
                        gpsM.analyseNMEA(lastResponse);
                    }
                    handler.checkSendCmdFromQueue();
                } while ((loopsToGo-- > 0) && (lastResponse != null));
                if ((nextAvailableRun < timeStamp)
                        && (gpsM.getOutStandingCmdsCount() == 0)
                        && !gpsM.isLogDownloadOnGoing()) {
                    if(eraseRequested) {
                        eraseRequested = false; // Erase request handled
                        mtkLogHandler.eraseLog();
                    }
                    nextAvailableRun = nextRun + 300;
                    checkNextAvailable();
                }
            } else {
                Generic.removeThread(this);
                mtkLogHandler.notifyDisconnected();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.sys.Thread#started()
     */
    public final void started() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.sys.Thread#stopped()
     */
    public final void stopped() {

    }

}
