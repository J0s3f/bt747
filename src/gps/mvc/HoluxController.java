/**
 * 
 */
package gps.mvc;

import bt747.sys.Generic;
import gps.BT747Constants;
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
	public HoluxController(MtkModel m) {
		super(m);
		// TODO Auto-generated constructor stub
	}
		
	/* (non-Javadoc)
	 * @see gps.mvc.MtkController#cmd(int, gps.mvc.CmdParam)
	 */
	public boolean cmd(final int cmd, final CmdParam param) {
	    switch(cmd) {
	    case MtkController.CMD_SET_DEVICE_NAME:
	        setHoluxName(param.getString());
	        break;
	    default:
	        return super.cmd(cmd, param);
	    }
	    return true;
	}
	
	
	/**
	 * Sets name of the device.
	 * 
	 * @param holuxName new device name
	 */
	private void setHoluxName(final String holuxName) {
		sendCmd(HoluxConstants.PHLX_CMD_PREFIX);
		sendCmd(HoluxConstants.PHLX_NAME_SET_REQUEST + "," + holuxName);
		reqData(MtkModel.DATA_DEVICE_NAME);
	}

	/**
	 * PHLX-specific implementation of requesting GPS data.
	 * 
	 * For most cases delegates to a existing PMTK command while 
	 * new functionality will be gradually implemented via PHLX commands. 
	 */
    protected boolean reqData(final int dataType) {
       switch (dataType) {
        	case MtkModel.DATA_DEVICE_NAME:
        		sendCmd(HoluxConstants.PHLX_CMD_PREFIX);
        		sendCmd(HoluxConstants.PHLX_NAME_GET_REQUEST);
        		break;
        		
        	default:
        		return super.reqData(dataType);
        }
       return true;
        /*
        switch (dataType) {
        case MtkModel.DATA_FLASH_TYPE:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_FLASH_STR + "," + "9F";
            break;
        case MtkModel.DATA_LOG_FORMAT:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_FORMAT_STR;
            break;
        case MtkModel.DATA_LOG_STATUS:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_LOG_STATUS_STR;
            break;
        case MtkModel.DATA_MEM_USED:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_MEM_USED_STR;
            break;
        case MtkModel.DATA_MEM_PTS_LOGGED:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_NBR_LOG_PTS_STR;
            break;
        case MtkModel.DATA_LOG_VERSION:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_VERSION_STR;
            break;
        case MtkModel.DATA_MTK_VERSION:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_Q_VERSION_STR;
            break;
        case MtkModel.DATA_MTK_RELEASE:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_Q_RELEASE_STR;
            break;
        case MtkModel.DATA_INITIAL_LOG:
            //
             / Request the initial log mode (the first value logged in
             // memory). Will be analyzed in {@link #analyseLogNmea(String[])}.<br>
             // Must be accessed through {@link #DATA_INITIAL_LOG}
             //
            // 6 is the log mode offset in the log,
            // 2 is the size
            // Required to know if log is in overwrite mode.
            mtkLogHandler.readLog(6, 2);
            return;
        case MtkModel.DATA_LOG_TIME_INTERVAL:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_TIME_INTERVAL_STR;
            break;
        case MtkModel.DATA_LOG_SPEED_INTERVAL:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_SPEED_INTERVAL_STR;
            break;
        case MtkModel.DATA_LOG_DISTANCE_INTERVAL:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_DISTANCE_INTERVAL_STR;
            break;
        case MtkModel.DATA_LOG_FLASH_STATUS:
            // Needed for erase 
            // Immediate sending! 
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_FLASH_STAT_STR;
            doSendCmd(nmeaCmd);
            return;
        case MtkModel.DATA_LOG_FLASH_SECTOR_STATUS:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_FLASH_SECTORS_STR;
            break;
        case MtkModel.DATA_FIX_PERIOD:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_API_Q_FIX_CTL;
            break;
        case MtkModel.DATA_AGPS_STORED_RANGE:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_Q_EPO_INFO;
            break;
        case MtkModel.DATA_LOG_OVERWRITE_STATUS:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_REC_METHOD_STR;
            break;
        case MtkModel.DATA_SBAS_TEST_STATUS:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_SBAS_TEST_STR;
            break;
        case MtkModel.DATA_SBAS_STATUS:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_API_Q_SBAS_STR;
            ;
            break;
        case MtkModel.DATA_POWERSAVE_STATUS:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_PWR_SAV_MOD_STR;
            break;
        case MtkModel.DATA_DATUM_MODE:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_DATUM_STR;
            break;
        case MtkModel.DATA_NMEA_OUTPUT_PERIODS:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_NMEA_OUTPUT;
            break;

        case MtkModel.DATA_DGPS_MODE:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_DGPS_MODE_STR;
            break;
        case MtkModel.DATA_BT_MAC_ADDR:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_BT_MAC_ADDR;
            break;
        case MtkModel.DATA_FLASH_USER_OPTION:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_GET_USER_OPTION_STR;
            break;
        case MtkModel.DATA_HOLUX_NAME:
            nmeaCmd = BT747Constants.HOLUX_MAIN_CMD
                    + BT747Constants.HOLUX_API_Q_NAME;
            break;
        default:
            break;
        }
        */
    }
	
}
