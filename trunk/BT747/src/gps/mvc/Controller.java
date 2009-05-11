/**
 * 
 */
package gps.mvc;

import gps.BT747Constants;
import gps.GpsEvent;
import gps.connection.GPSrxtx;
import gps.log.GPSRecord;
import gps.log.in.CommonIn;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747StringTokenizer;
import bt747.sys.interfaces.BT747Thread;

/**
 * Refactoring ongoing.
 * 
 * @author Mario
 * 
 */
public class Controller implements BT747Thread {

    private final Model gpsM;
    private final MtkModel mtkM;
    private final MtkController mtkC;

    private final GPSLinkHandler handler;

    public final static Controller getInstance(final GPSrxtx gpsRxTx) {
        final Model m = new Model(gpsRxTx);
        return Controller.getInstance(m);
    }

    public final static Controller getInstance(final Model model) {
        return new Controller(model);
    }

    private Controller(final Model model) {
        gpsM = model;
        mtkM = gpsM.getMtkModel();
        mtkC = new MtkController(mtkM);
        handler = gpsM.getHandler();
    }

    public final Model getModel() {
        return gpsM;
    }
    
    public final MtkController getMtkController() {
        return mtkC;
    }

    private boolean eraseRequested;
    
    public final void setGpsDecode(final boolean gpsDecode) {
        gpsM.setGpsDecode(gpsDecode);
    }
    
    /**
     * Check if the data is available and if it is not, requests it.
     * 
     * @param dataType
     * @return
     */
    public final void setDataNeeded(final int dataType) {
        if (handler.isConnected()) {
            final int ts = Generic.getTimeStamp();
            if (mtkM.isDataNeedsRequest(ts, dataType)) {
                switch (dataType) {
                case MtkModel.DATA_MEM_USED:
                    mtkC.reqLogMemUsed();
                    break;
                case MtkModel.DATA_MEM_PTS_LOGGED:
                    mtkC.reqLogMemPtsLogged();
                    break;
                case MtkModel.DATA_FLASH_TYPE:
                    mtkC.reqFlashManuID();
                    break;
                case MtkModel.DATA_LOG_FORMAT:
                    mtkC.reqLogFormat();
                    break;
                case MtkModel.DATA_MTK_VERSION:
                    mtkC.reqDeviceVersion();
                    break;
                case MtkModel.DATA_MTK_RELEASE:
                    mtkC.reqDeviceRelease();
                    break;
                case MtkModel.DATA_INITIAL_LOG:
                    mtkC.reqInitialLogMode();
                    break;
                case MtkModel.DATA_LOG_STATUS:
                    mtkC.reqLogStatus();
                    break;
                case MtkModel.DATA_LOG_VERSION:
                    mtkC.reqMtkLogVersion();
                    break;
                default:
                    break;
                }
            }
        }
        if (Generic.isDebug() && (Generic.getDebugLevel() >= 2)) {
            Generic.debug("Data request of " + dataType + " skipped");
        }
    }

    
    public final void reqDeviceInfo() {
        setDataNeeded(MtkModel.DATA_MTK_RELEASE);
        setDataNeeded(MtkModel.DATA_MTK_VERSION);
        setDataNeeded(MtkModel.DATA_FLASH_TYPE);
        setDataNeeded(MtkModel.DATA_LOG_VERSION);
    }

    public final void setDownloadTimeOut(final int downloadTimeOut) {
        handler.setDownloadTimeOut(downloadTimeOut);
    }

    /** Get the current status of the device */
    public final void reqStatus() {
        setDataNeeded(MtkModel.DATA_LOG_FORMAT);
        getLogCtrlInfo();
        // getLogReasonStatus();
        // getPowerSaveEnabled();
        // getSBASEnabled();
        // getDGPSMode();
        // getDatumMode();
        // getFixInterval();
        mtkC.reqHoluxName(); // Mainly here to identify Holux device
    }

    public final void reqLogOnOffStatus() {
        mtkC.reqLogStatus();
    }

    private final void getLogCtrlInfo() {
        setDataNeeded(MtkModel.DATA_LOG_VERSION);
        setDataNeeded(MtkModel.DATA_MEM_USED);
        setDataNeeded(MtkModel.DATA_MEM_PTS_LOGGED);
        mtkC.reqLogOverwrite();
    }


    /**
     * Erase the log.
     */
    public final void eraseLog() {
        eraseRequested = true;
        // TODO: do not change mtk model here.
        mtkM.setEraseOngoing(true); // For popup
    }

    /**
     * The log is being erased - the user request to abandon waiting for the
     * end of this operation.
     */
    public final void stopErase() {
        eraseRequested = false;
        mtkC.getMtkLogHandler().stopErase();
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
        setDataNeeded(next);
        next += 1;
        if (next > MtkModel.DATA_LAST_INDEX) {
            next = 0;
        }
        nextValueToCheck = next;
    }

    private DeviceOperationHandlerIF operationHandler;

    public void setDeviceOperationHandler(DeviceOperationHandlerIF h) {
        operationHandler = h;
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
                mtkC.getMtkLogHandler().notifyRun();
                {
                    // local value
                    final DeviceOperationHandlerIF h = operationHandler;
                    if (h != null) {
                        if (!h.notifyRun(handler)) {
                            setDeviceOperationHandler(null);
                        }
                    }
                }
                final DeviceOperationHandlerIF h = operationHandler;
                do {
                    final Object lastResponse = handler.getResponse();
                    if (lastResponse != null) {
                        if (h != null) {
                            if (h.analyseResponse(lastResponse)) {
                                continue; // Skip other analyzers.
                            }
                        }
                        gpsM.analyseResponse(lastResponse);
                    } else {
                        loopsToGo = 0; // Exit do/while
                    }
                    handler.checkSendCmdFromQueue();
                } while ((loopsToGo-- > 0));
                if ((nextAvailableRun < timeStamp)
                        && (handler.getOutStandingCmdsCount() == 0)
                        && !mtkC.getMtkLogHandler().isLogDownloadOnGoing()) {
                    if (eraseRequested) {
                        eraseRequested = false; // Erase request handled
                        mtkC.getMtkLogHandler().eraseLog();
                    }
                    nextAvailableRun = nextRun + 300;
                    checkNextAvailable();
                }
            } else {
                Generic.removeThread(this);
                mtkC.getMtkLogHandler().notifyDisconnected();
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
