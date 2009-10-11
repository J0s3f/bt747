/**
 * 
 */
package gps.mvc;

import gps.WondeproudConstants;
import gps.mvc.commands.wp.DPL700IntCommand;
import gps.mvc.commands.wp.DPL700StrCommand;

/**
 * Controller for Wondeproud devices like the BT-BT110m
 * 
 * @author Mario De Weerd
 */
public class DPL700Controller extends MtkController implements
        WondeproudConstants {

    private DPL700Model m;
    private Controller c;

    /**
     * @param m
     */
    public DPL700Controller(final Controller c, final MtkModel m) {
        super(c, m);
        this.c = c;
        this.m = (DPL700Model)m;
    }

    /**
     * Fetches a log to the given filename.
     * 
     * @param fileName
     * @param card
     */
    public void getLog(final String fileName, final int card) {
        DPL700LogDownloadHandler h = new DPL700LogDownloadHandler(this, m
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
        case MtkController.CMD_SET_DEVICE_NAME:
        case MtkController.CMD_SET_LOG_DISTANCE_INTERVAL:
        case MtkController.CMD_SET_LOG_SPEED_INTERVAL:
        case MtkController.CMD_SET_LOG_TIME_INTERVAL:
        case MtkController.CMD_SET_LOG_OVERWRITE:
            return false;
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

    private boolean logDownloadOngoing = false;

    protected void setLogDownloadOngoing(final boolean ongoing) {
        logDownloadOngoing = ongoing;
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
     * Device-specific implementation of requesting GPS data.
     * 
     * For most cases delegates to a existing PMTK command while new
     * functionality will be gradually implemented for this device type.
     */
    public boolean reqData(final int dataType) {
        if(logDownloadOngoing) {
            return false;
        }
        switch (dataType) {
        case MtkModel.DATA_LOG_STATUS:
        case MtkModel.DATA_MEM_PTS_LOGGED:
        case MtkModel.DATA_LOG_VERSION:
        case MtkModel.DATA_FLASH_TYPE:
        case MtkModel.DATA_LOG_FORMAT:
        case MtkModel.DATA_LOG_TIME_INTERVAL:
        case MtkModel.DATA_LOG_SPEED_INTERVAL:
        case MtkModel.DATA_LOG_DISTANCE_INTERVAL:
        case MtkModel.DATA_LOG_FLASH_STATUS:
        case MtkModel.DATA_LOG_FLASH_SECTOR_STATUS:
        case MtkModel.DATA_LOG_OVERWRITE_STATUS:
            return false;
        case MtkModel.DATA_MEM_USED:
            m.setExpectedDataType(MtkModel.DATA_MEM_USED);
            reqMemInUse();
            return true;
        case MtkModel.DATA_INITIAL_LOG:
            return false;
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

    public final void reqMemInUse() {
        m.getHandler().sendCmd(new DPL700IntCommand(REQ_MEM_IN_USE, 4));
        m.getHandler().sendCmd(new DPL700StrCommand(WP_AP_EXIT));
        // m_GPSrxtx.virtualReceive("sample dataWP Update Over\0");
    }

}
