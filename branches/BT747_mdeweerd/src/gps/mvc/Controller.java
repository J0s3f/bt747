/**
 * 
 */
package gps.mvc;

import gps.GpsEvent;
import gps.ProtocolConstants;

import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747Thread;

/**
 * Refactoring ongoing.
 * 
 * @author Mario
 * 
 */
public class Controller implements BT747Thread, ProtocolConstants,
        DeviceControllerIF {

    private final Model gpsM;
    private MtkModel mtkM;
    // private MtkController mtkC;
    private ProtectedDevControllerIF mtkC;

    private final GPSLinkHandler handler;

    public final static Controller getInstance(final Model model,
            final int protocol) {
        return new Controller(model, protocol);
    }

    /** The current protocol in use. */
    private int protocol = PROTOCOL_INVALID;

    private Controller(final Model model, final int protocol) {
        gpsM = model;
        mtkM = gpsM.getMtkModel();
        // this probably needs to be refactored to create the correct
        // controller
        setProtocol(protocol);
        handler = gpsM.getHandler();
    }

    /**
     * Sets or changes the protocol on the link. This will result in a change
     * in the GPS Controller (e.g., MtkController).
     * 
     * Refactoring will change this code but in principle not its interface.
     * 
     * @param newProtocol
     *            The new protocol. Chosen from {@link #PROTOCOL_MTK},
     *            {@link #PROTOCOL_SIRFIII}, {@link #PROTOCOL_HOLUX_PHLX}
     */
    public final void setProtocol(final int newProtocol) {
        if (protocol != newProtocol && mtkC != null) {
            /* Need only change if protocol changed. */
            if (handler.isConnected()) {
                // TODO: Revisit to review code leading to this long path:
                handler.getGPSRxtx().closePort();
            }
            mtkC = null;
        }
        mtkM = gpsM.getMtkModel();
        if (mtkC != null) {
            /** Previous controller is still valid. */
            return;
        }
        protocol = newProtocol;

        switch (protocol) {
        default:
        case PROTOCOL_MTK:
        case PROTOCOL_SIRFIII:
            mtkC = new MtkController(this, mtkM);
            break;
        case PROTOCOL_HOLUX_PHLX:
            mtkC = new HoluxController(this, mtkM);
            break;
        case PROTOCOL_WONDEPROUD:
            mtkC = new DPL700Controller(this, mtkM);
            break;
        }

    }

    public final Model getModel() {
        return gpsM;
    }

    /**
     * TODO: Should no longer exist after refactoring.
     * 
     * @return
     */
    public final MtkController getMtkController() {
        return (MtkController) mtkC;
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
                mtkC.reqData(dataType);
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
        mtkC.reqData(MtkModel.DATA_DEVICE_NAME); // Mainly here to identify
        // Holux device
        setDataNeeded(MtkModel.DATA_AGPS_STORED_RANGE);
    }

    public final void reqLogOnOffStatus() {
        mtkC.reqData(MtkModel.DATA_LOG_STATUS);
    }

    private final void getLogCtrlInfo() {
        setDataNeeded(MtkModel.DATA_LOG_VERSION);
        setDataNeeded(MtkModel.DATA_MEM_USED);
        setDataNeeded(MtkModel.DATA_MEM_PTS_LOGGED);
        mtkC.reqData(MtkModel.DATA_LOG_OVERWRITE_STATUS);
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
        mtkC.cmd(MtkController.CMD_STOP_WAITING_FOR_ERASE);
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
        if (next > MtkModel.DATA_LAST_AUTO_INDEX) {
            next = 0;
        }
        nextValueToCheck = next;
    }

    private volatile DeviceOperationHandlerIF operationHandler;

    public final void setDeviceOperationHandler(
            final DeviceOperationHandlerIF h) {
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
        try {
            final int timeStamp = Generic.getTimeStamp();
            final ProtectedDevControllerIF mtkC = this.mtkC;
            if (timeStamp >= nextRun) {
                nextRun = timeStamp + 10;
                int loopsToGo = 0; // Setting to 0 for more responsiveness
                if (handler.isConnected()) {
                    mtkC.notifyRun();
                    {
                        // local value
                        final DeviceOperationHandlerIF h = operationHandler;
                        if (h != null) {
                            try {
                                if (!h.notifyRun(handler)) {
                                    setDeviceOperationHandler(null);
                                }
                            } catch (BT747Exception e) {
                                setDeviceOperationHandler(null);
                                Generic.debug("Handler: ", e);
                                gpsM.postEvent(new GpsEvent(
                                        GpsEvent.EXCEPTION, e));
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
                            && !mtkM.isLogDownloadOngoing()) {
                        if (eraseRequested) {
                            eraseRequested = false; // Erase request handled
                            mtkC.cmd(MtkController.CMD_ERASE_LOG);
                        }
                        nextAvailableRun = nextRun + 300;
                        checkNextAvailable();
                    }
                } else {
                    Generic.removeThread(this);
                    mtkC.notifyDisconnected();
                }
            }
        } catch (Exception e) {
            Generic.debug("Exception in Controller run", e);
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

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.DeviceControllerIF#cmd(int)
     */
    public final boolean cmd(int cmd) {
        return mtkC.cmd(cmd);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.DeviceControllerIF#cmd(int, gps.mvc.CmdParam)
     */
    public final boolean cmd(int cmd, CmdParam param) {
        return mtkC.cmd(cmd, param);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.DeviceControllerIF#isSupportedCmd(int)
     */
    public final boolean isSupportedCmd(int cmd) {
        return mtkC.isSupportedCmd(cmd);
    }
    
    public final void setAgpsData(final byte[] agpsData) {
        mtkC.setAgpsData(agpsData);
    }

    /* (non-Javadoc)
     * @see gps.mvc.DeviceControllerIF#getLog(java.lang.String, int)
     */
    public void getLog(String fileName, int card) {
        mtkC.getLog(fileName, card);
    }


}
