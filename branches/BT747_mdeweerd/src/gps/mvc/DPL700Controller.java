/**
 * 
 */
package gps.mvc;

import gps.WondeproudConstants;

/**
 * Controller for Wondeproud devices like the BT-BT110m
 * 
 * @author Mario De Weerd
 */
public class DPL700Controller extends MtkController {

    private MtkModel m;
    private Controller c;

    /**
     * @param m
     */
    public DPL700Controller(final Controller c, final MtkModel m) {
        super(c, m);
        this.c = c;
        this.m = m;
    }

    /**
     * Fetches a log to the given filename.
     * 
     * @param fileName
     * @param card
     */
    public void getLog(final String fileName, final int card) {
        DPL700LogDownloadHandler h = new DPL700LogDownloadHandler(m
                .getHandler());
        h.getDPL700Log(fileName, card);
        c.setDeviceOperationHandler(h);
    }

    /**
     * Handles commands with no parameters Support for Holux-specific
     * commands, otherwise delegates to standard MTK
     */
    public boolean cmd(final int cmd) {
        switch (cmd) {
        // case MtkController.CMD_ERASE_LOG:
        // sendCmd(HoluxConstants.PHLX_LOG_ERASE_REQUEST);
        // break;
        default:
            return super.cmd(cmd);
        }

        // return true;
    }

    /**
     * Handles commands with a parameter Support for Holux-specific commands,
     * otherwise delegates to standard MTK
     */
    public boolean cmd(final int cmd, final CmdParam param) {
        switch (cmd) {
        // case MtkController.CMD_SET_DEVICE_NAME:
        // setHoluxName(param.getString());
        // break;
        // case CMD_SET_LOG_DISTANCE_INTERVAL:
        // setLogDistanceInterval(param.getInt());
        // break;
        // case CMD_SET_LOG_TIME_INTERVAL:
        // setLogTimeInterval(param.getInt());
        // break;
        default:
            return super.cmd(cmd, param);
        }
        // return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.MtkController#isSupportedCmd(int)
     */
    public boolean isSupportedCmd(int cmd) {
        return super.isSupportedCmd(cmd);
    }

    /**
     * PHLX-specific implementation of requesting GPS data.
     * 
     * For most cases delegates to a existing PMTK command while new
     * functionality will be gradually implemented via PHLX commands.
     */
    public boolean reqData(final int dataType) {
        switch (dataType) {
        // case MtkModel.DATA_DEVICE_NAME:
        // sendCmd(HoluxConstants.PHLX_CMD_PREFIX);
        // sendCmd(HoluxConstants.PHLX_NAME_GET_REQUEST);
        // break;
        // case MtkModel.DATA_LOG_TIME_INTERVAL:
        // case MtkModel.DATA_LOG_DISTANCE_INTERVAL:
        // sendCmd(HoluxConstants.PHLX_LOG_GET_CRITERIA_REQUEST);
        // break;
        default:
            return super.reqData(dataType);
        }

        // return true;
    }
}
