/**
 * 
 */
package net.sf.bt747.test;

import gps.WondeproudConstants;
import gps.mvc.commands.GpsLinkExecCommand;
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
            return analyseText(((DPL700DeviceResponseModel) response)
                    .getResponseType());
        }
        return -1;
    }

    public static final String TEST_BTCD110m_FILE = "/net/sf/bt747/gps/log/in/test/logfiles/x.sr";

    public String getResourcePath(String rsc) {
        return getClass().getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }

    private void replyLog() {
        GpsLinkExecCommand reply = null;
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

    private final byte[] Initial = { 0x57, 0x6F, 0x6E, 0x64, 0x65, 0x50,
            0x72, 0x6F, 0x75, 0x64, 0x20, 0x54, 0x65, 0x63, 0x68, 0x2E, 0x20,
            0x43, 0x6F, 0x2E, 0x20, 0x42, 0x54, 0x2D, 0x43, 0x44, 0x31, 0x31,
            0x30, 0x0D, 0x0A, 0x00 };

    private static final byte[] DevInfo = {
            0x00,
            0x0A,
            0x09,
            0x1E, // ??
            0x00,
            0x00,
            0x00,
            0x00, // User Id ??
            'W', 'o', 'n', 'd', 'e', 'P', 'r', 'o', 'u', 'd', ' ', 'T', 'e',
            'c', 'h', '.', ' ', 'C',
            'o',
            '.',
            // WondeProud Tech Co.
            0x00,
            (byte) 0xEA,
            (byte) 0x92,
            0x7C,// ...
            0x64, (byte) 0xF7, 0x12, 0x00, 0x30,
            0x00,
            0x00,
            0x00, // ???
            'B', 'T', '-', 'C', 'D', '1', '6', '0', 'M',
            'T',
            'K',
            0x00,// BT-CD160MTK\0
            'e', 'c', 'h', '.', ' ', 'C', 'o', '.', 0x00, (byte) 0xEA,
            (byte) 0x92, 0x7C, 0x64, (byte) 0xF7, 0x12, 0x00, 0x30, 0x00,
            0x00, 0x00, (byte) 0xAE, 0x65, 0x26, (byte) 0x80, (byte) 0xC8,
            0x54, 0x2A, 0x03,// d...0...®e&amp;..T*.
            0x0B, 0x23, 0x69, 0x26, 0x12, 0x00, 0x00, (byte) 0xFF, // .#i&amp;....
    };

    private static final DPL700DeviceReplyCommand infoRep = new DPL700DeviceReplyCommand(
            DevInfo);

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
            byte[] xx = { 0, 0, 0x40, 0 };
            mtkDeviceModel.gpsRxTx.write(xx);
            // (new
            // DPL700IntCommand(0x00004000,4)).execute(mtkDeviceModel.gpsRxTx);
            break;
        case REQ_MEM_IN_USE:
            byte[] memuse = { 0, (byte)0xD0, 7, 0 };  // 512000 = 1/8 of the memory.
            mtkDeviceModel.gpsRxTx.write(memuse);
            break;
        case REQ_DEV_INFO1:
            Generic.debug("Device info requested");
            // sleep();
            infoRep.execute(mtkDeviceModel.gpsRxTx);
            // mtkDeviceModel.gpsRxTx.write(xx);
            break;
        }
        return 0;
    }

    private boolean sendInitial = false;

    private void sleep() {
        try {
            Thread.sleep(160);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public int analyseText(String text) {
        GpsLinkExecCommand reply;
        if (text.equals(WP_CAMERA_DETECT)) {
            Generic.setDebugLevel(2);
            if (sendInitial) {
                mtkDeviceModel.gpsRxTx.write(Initial);
                sendInitial = false;
            }
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
            // try {
            // Thread.sleep(160);
            // } catch (Exception e) {
            // // TODO: handle exception
            // }
            // mtkDeviceModel.gpsRxTx.write(Initial);
            return 0;
        }
        return -1;
    } // End method

}
