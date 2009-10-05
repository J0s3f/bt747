/**
 * 
 */
package net.sf.bt747.test;

import gps.mvc.commands.GpsRxtxExecCommand;
import gps.mvc.commands.dpl700.DPL700StrCommand;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * @author Mario De Weerd
 * 
 */
public class BTCD110mController {
    /**
     * This extends the mtkDeviceModel;
     */
    private IBlue747Model mtkDeviceModel;

    /**
     * Initiate the instance.
     * 
     * @param mtkDeviceModel
     *                Reference to basic mtkDevice modeling.
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

    public final void analyseResponse(final Object response) {
        if(response instanceof String) {
            String r = (String)response;
            analyseText(r);
        }
        
    }

    public int analyseText(String text) {
        GpsRxtxExecCommand reply;
        if(text.equals("W'P Camera Detect")) {
            reply = new DPL700StrCommand("WP GPS+BT");
            mtkDeviceModel.setDeviceMode(IBlue747Model.DeviceMode.DEVICE_MODE_DPL700);
            if (reply != null) {
                reply.execute(mtkDeviceModel.gpsRxTx);
            }
        }
        if(text.equals("WP AP-Exit")) {
            mtkDeviceModel.setDeviceMode(IBlue747Model.DeviceMode.DEVICE_MODE_NMEA);
        }
        return 0;
    } // End method

}
