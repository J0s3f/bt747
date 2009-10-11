/**
 * 
 */
package gps.mvc;

import gps.HoluxConstants;

/**
 * Controller for Holux devices (PHLX command set) like the M-1000C.
 * 
 * @author Robert Tomanek <bt747.free.fr@mail.robert.tomanek.org>
 */
public class HoluxController extends MtkController {

    /**
     * @param m
     */
    public HoluxController(final GpsController c, final MtkModel m) {
        super(c, m);
        // TODO Auto-generated constructor stub
    }

    /**
     * Handles commands with no parameters Support for Holux-specific
     * commands, otherwise delegates to standard MTK
     */
    public boolean cmd(final int cmd) {
        switch (cmd) {
        case MtkController.CMD_ERASE_LOG:
            sendCmd(HoluxConstants.PHLX_LOG_ERASE_REQUEST);
            break;
        default:
            return super.cmd(cmd);
        }

        return true;
    }

    
    /**
     * Handles commands with a parameter Support for Holux-specific commands,
     * otherwise delegates to standard MTK
     */
    public boolean cmd(final int cmd, final CmdParam param) {
        switch (cmd) {
        case MtkController.CMD_SET_DEVICE_NAME:
            setHoluxName(param.getString());
            break;
        case CMD_SET_LOG_DISTANCE_INTERVAL:
            setLogDistanceInterval(param.getInt());
            break;
        case CMD_SET_LOG_TIME_INTERVAL:
            setLogTimeInterval(param.getInt());
            break;
        default:
            return super.cmd(cmd, param);
        }
        return true;
    }

    /* (non-Javadoc)
     * @see gps.mvc.MtkController#isSupportedCmd(int)
     */
    public boolean isSupportedCmd(int cmd) {
        return super.isSupportedCmd(cmd);
    }

    /**
     * Sets name of the device.
     * 
     * @param holuxName
     *            new device name
     */
    private void setHoluxName(final String holuxName) {
        sendCmd(HoluxConstants.PHLX_CMD_PREFIX);
        sendCmd(HoluxConstants.PHLX_NAME_SET_REQUEST + "," + holuxName);
        reqData(MtkModel.DATA_DEVICE_NAME);
    }

    /**
     * PHLX-specific implementation of requesting GPS data.
     * 
     * For most cases delegates to a existing PMTK command while new
     * functionality will be gradually implemented via PHLX commands.
     */
    public boolean reqData(final int dataType) {
        switch (dataType) {
        case MtkModel.DATA_DEVICE_NAME:
            sendCmd(HoluxConstants.PHLX_CMD_PREFIX);
            sendCmd(HoluxConstants.PHLX_NAME_GET_REQUEST);
            break;
        case MtkModel.DATA_LOG_TIME_INTERVAL:
        case MtkModel.DATA_LOG_DISTANCE_INTERVAL:
            sendCmd(HoluxConstants.PHLX_LOG_GET_CRITERIA_REQUEST);
            break;
        default:
            return super.reqData(dataType);
        }

        return true;
    }

    /**
     * PHLX-specific implementation of setting logging criteria based on Time
     * Interval. According to ezTour, valid settings range from 1 to 120.
     * 
     * For most cases delegates to a existing PMTK command while new
     * functionality will be gradually implemented via PHLX commands.
     */
    private void setLogTimeInterval(final int value) {
        int z_value = (value+5)/10; // Scale value to seconds.
        if (z_value > 120) {
            z_value = 120;
        } else if (z_value < 1) {
            z_value = 1;
        }

        sendCmd(HoluxConstants.PHLX_LOG_SET_CRITERIA + ","
                + HoluxConstants.PHLX_LOG_CRITERIUM_TIME_PARAM + ","
                + z_value + "," + "1");
    }

    /**
     * PHLX-specific implementation of setting logging criteria based on
     * Distance Interval. According to ezTour, valid settings range from 1 to
     * 1000.
     * 
     * For most cases delegates to a existing PMTK command while new
     * functionality will be gradually implemented via PHLX commands.
     */
    private void setLogDistanceInterval(final int value) {
        int z_value = (value+5)/10; // Scale value to meters.
        if (z_value > 1000) {
            z_value = 1000;
        } else if (z_value < 1) {
            z_value = 1;
        }

        sendCmd(HoluxConstants.PHLX_LOG_SET_CRITERIA + ","
                + HoluxConstants.PHLX_LOG_CRITERIUM_DISTANCE_PARAM + ","
                + "1" + "," + z_value);
    }

}
