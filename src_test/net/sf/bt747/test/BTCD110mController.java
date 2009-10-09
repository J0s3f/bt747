/**
 * 
 */
package net.sf.bt747.test;

import gps.WondeproudConstants;
import gps.mvc.commands.GpsRxtxExecCommand;
import gps.mvc.commands.dpl700.DPL700IntCommand;

import java.io.FileInputStream;

import bt747.sys.File;
import bt747.sys.Generic;

/**
 * @author Mario De Weerd
 * 
 */
public class BTCD110mController implements WondeproudConstants {
    /**
     * This extends the mtkDeviceModel;
     */
    private IBlue747Model mtkDeviceModel;

    /**
     * Initiate the instance.
     * 
     * @param mtkDeviceModel
     *            Reference to basic mtkDevice modeling.
     */
    public BTCD110mController(final IBlue747Model mtkDeviceModel) {
        this.mtkDeviceModel = mtkDeviceModel;
    }

    public static class HlxDataModel {
    }

    private final HlxDataModel hlxData = new HlxDataModel();

    /**
     * Check if the controller responds to the NMEA command.
     * 
     * @return
     */
    public final boolean handles(final String nmea0) {
        switch (mtkDeviceModel.mtkData.modelType) {
        case HOLUXM1000C:
            return nmea0.startsWith("PHLX");
        default:
            return false;
        }
    }

    public final int analyseResponse(final Object response) {
        if (response instanceof String) {
            String r = (String) response;
            return analyseText(r);
        } else if (response instanceof DPL700IntCommand) {
            return analyzeDPL700Response((DPL700IntCommand) response);
        } else if (response instanceof DPL700DeviceResponseModel) {
            return analyseText(((DPL700DeviceResponseModel) response).getResponseType());
        }
        return -1;
    }

    public static final String TEST_BTCD110m_FILE = "/net/sf/bt747/gps/log/in/test/logfiles/x.sr";

    public String getResourcePath(String rsc) {
        return getClass().getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }

    private void replyLog() {
        GpsRxtxExecCommand reply = null;
        String fn = getResourcePath(TEST_BTCD110m_FILE);
        File fh = new File(fn);
        int size = fh.getSize();
        try {
            FileInputStream fi = new FileInputStream(fn);
            byte[] log = new byte[(int) size];
            fi.read(log);
            fi.close();
            reply = new DPL700DeviceReplyCommand(log);
        } catch (Exception e) {
            Generic.debug("Issue loading log in model", e);
        }

        if (reply != null) {
            reply.execute(mtkDeviceModel.gpsRxTx);
        }
    }

    private final String LOG_FILE_NAME = getResourcePath(TEST_BTCD110m_FILE);

    public final int analyzeDPL700Response(DPL700IntCommand response) {
        switch (response.getCmd()) {
        case REQ_LOG:
            Generic.debug("Log requested");
            replyLog();
            break;
        case REQ_DATE_TIME:
            Generic.debug("Date time requested");
            break;
        case REQ_ERASE:
            Generic.debug("Erase requested");
            break;
        case REQ_DEV_PARAM:
            Generic.debug("Device parameters requested");
            break;
        case REQ_SELFTEST:
            Generic.debug("Self test requested");
            break;
        case REQ_DEV_INFO1:
            Generic.debug("Device info requested");
            break;
        }
        return 0;
    }

    public int analyseText(String text) {
        GpsRxtxExecCommand reply;
        if (text.equals(WP_CAMERA_DETECT)) {
            reply = new DPL700DeviceStrCommand(WP_GPS_PLUS_RESPONSE);
            mtkDeviceModel
                    .setDeviceMode(IBlue747Model.DeviceMode.DEVICE_MODE_DPL700);
            if (reply != null) {
                reply.execute(mtkDeviceModel.gpsRxTx);
            }
            return 0;
        }
        if (text.equals(WP_AP_EXIT)) {
            mtkDeviceModel
                    .setDeviceMode(IBlue747Model.DeviceMode.DEVICE_MODE_NMEA);
            return 0;
        }
        return -1;
    } // End method

}
