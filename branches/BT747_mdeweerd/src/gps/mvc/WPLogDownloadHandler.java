/**
 * 
 */
package gps.mvc;

import gps.GpsEvent;
import gps.WondeproudConstants;
import gps.connection.WPResponseModel;
import gps.connection.DecoderStateFactory;
import gps.mvc.commands.wp.WPIntCommand;
import gps.mvc.commands.wp.WPStrCommand;

import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747Path;

/**
 * @author Mario
 * 
 */
public class WPLogDownloadHandler implements DeviceOperationHandlerIF,
        WondeproudConstants {
    private final WPController wpC;
    // DPL700 Functionality
    private BT747Path wpLogPath;
    private static final int WP_OFF = 0;
    private static final int WP_NEEDGETLOG = 1;
    private static final int WP_GETLOG = 2;
    private int wpState = WP_OFF;

    private final LogFile logFile = new LogFile();
    private final GpsLinkHandler handler;

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.DeviceOperationHandlerIF#analyseResponse(java.lang.Object)
     */
    public boolean analyseResponse(Object o) {
        if (o instanceof WPResponseModel) {
            analyseWPData((WPResponseModel) o);
            wpC.getMtkModel().postEvent(GpsEvent.LOG_DOWNLOAD_SUCCESS);
            wpC.getMtkModel().postEvent(GpsEvent.LOG_DOWNLOAD_DONE);
            return true;
        } else if (o instanceof String) {

        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.DeviceOperationHandlerIF#notifyRun(gps.mvc.GPSLinkHandler)
     */
    public boolean notifyRun(GpsLinkHandler handler) throws BT747Exception {
        boolean cont = true;
        switch (wpState) {
        case WP_OFF:
            break;
        case WP_NEEDGETLOG:
            break;
        case WP_GETLOG:
            break;

        default:
            break;
        }
        return cont;
    }

    // private final MTKLogDownloadHandler mtkLogHandler; // Temporary (TODO:
    // refactor).
    /**
     * 
     */
    public WPLogDownloadHandler(final WPController dpl,
            final GpsLinkHandler handler) {
        wpC = dpl;
        this.handler = handler;
    }

    public final void getWPLog(final BT747Path path) {
        wpC.setLogDownloadOngoing(true);
        wpLogPath = path;
        enterWPMode();
        wpState = WP_NEEDGETLOG;
    }

    public final void reqWPLog() {
        wpState = WP_GETLOG;
        handler.sendCmd(new WPIntCommand(null, REQ_LOG, -1, 10 * 1024 * 1024));
        wpC.getMtkModel().postEvent(GpsEvent.LOG_DOWNLOAD_STARTED);
        // m_GPSrxtx.virtualReceive("sample dataWP Update Over\0");
    }

    public final void enterWPMode() {
        exitWPMode(); // Exit previous session if still open
        handler.sendCmd(new WPStrCommand(WP_CAMERA_DETECT, 255));
        // m_GPSrxtx.virtualReceive("WP GPS+BT\0");
    }

    public final void exitWPMode() {
        handler.sendCmd(new WPStrCommand(WP_AP_EXIT, 0)); // No reply
                                                              // expected
        wpState = WP_OFF;
    }


    protected final void analyseWPData(final WPResponseModel resp) {
        final String s = resp.getResponseType();
        // Generic.debug("<WP:" + s);
        if (Generic.isDebug()) {
            Generic.debug("<WP:" + s);
        }
        if (s.startsWith("WP GPS")) {
            // WP GPS+BT
            // Response to W'P detect
            if (wpState == WP_NEEDGETLOG) {
                reqWPLog();
            }
        } else if (s.equals(WP_UPDATE_OVER)) {
            if (wpState == WP_GETLOG) {
                if (wpLogPath != null) {
                    if (!wpLogPath.getPath().endsWith(".sr")) {
                        wpLogPath = wpLogPath.proto(wpLogPath + ".sr");
                    }
                    logFile.openNewLog(wpLogPath);
                    try {
                        logFile.getLogFile().writeBytes(
                                resp.getResponseBuffer(), 0,
                                resp.getResponseSize());
                        logFile.getLogFile().close();
                    } catch (final Exception e) {
                        Generic.debug("", e);
                        // TODO: handle exception
                    }
                    wpLogPath = null;
                    exitWPMode();
                    Generic.debug("End WP");
                    handler.getGPSRxtx().newState(DecoderStateFactory.NMEA_STATE);  // Not sure this is appropriate.
                    wpC.setLogDownloadOngoing(false);
                }
            }
        }
    }
}
