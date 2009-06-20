/**
 * 
 */
package net.sf.bt747.test;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * @author Mario De Weerd
 * 
 */
public class HlxController {
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
    public HlxController(final IBlue747Model mtkDeviceModel) {
        this.mtkDeviceModel = mtkDeviceModel;
    }

    public static class HlxDataModel {
        public String holuxDeviceName = "BT747 Model";
        public int logCriteriaType = 0;
        public int logTimeCriteria = 5;
        public int logDistanceCriteria = 100;

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
        analyseNMEA((String[]) response);
    }

    public int analyseNMEA(final String[] p_nmea) {
        int z_Cmd;
        int z_Result = 0;

        final StringBuffer nmea = new StringBuffer();
        Object response = null;
        for (final String s : p_nmea) {
            nmea.append(s);
            nmea.append(',');
        }

        Generic.debug(nmea.toString());

        if (p_nmea[0].startsWith("PHLX")) {
            z_Cmd = JavaLibBridge.toInt(p_nmea[0].substring(4));

            z_Result = -1; // Suppose cmd not treated
            switch (z_Cmd) {
            case 810:
                switch (mtkDeviceModel.mtkData.modelType) {
                case HOLUXM1000C:
                    response = "PHLX852,M1000C";
                    break;
                }
                break;
            case 826:
                response = "PHLX859";
                break;
            case 828:
                break;
            case 830: // HoluxConstants.PHLX_NAME_SET_REQUEST:
                if (p_nmea.length == 2) {
                    hlxData.holuxDeviceName = p_nmea[1];
                    // PHLX_NAME_SET_ACK
                    response = "PHLX862";
                }
                break;
            case 831:
                response = "PHLX863," + hlxData.holuxDeviceName;
                break;
            case 832:
                break;
            case 833:
                response = "PHLX866," + hlxData.logCriteriaType + ","
                        + hlxData.logTimeCriteria + ","
                        + hlxData.logDistanceCriteria;
                break;
            }
        }

        if (response != null) {
            if (response instanceof String) {
                final String resp = (String) response;
                mtkDeviceModel.sendPacket(resp);
                if (z_Result == -1) {
                    z_Result = 0;
                }
            }
        }

        if (z_Result < 0) {
            Generic.debug("No response from holux model to "
                    + nmea.toString());
        }
        return z_Result;
    } // End method

}
