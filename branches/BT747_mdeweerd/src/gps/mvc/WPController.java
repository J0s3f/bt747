/**
 * 
 */
package gps.mvc;

import bt747.sys.interfaces.BT747Path;

import gps.WondeproudConstants;
import gps.mvc.commands.wp.WPEnterModeCommand;
import gps.mvc.commands.wp.WPExitModeCommand;
import gps.mvc.commands.wp.WPIntCommand;

/**
 * Controller for Wonde Proud devices like the BT-BT110m
 * 
 * @author Mario De Weerd
 */
public class WPController extends MtkController implements WondeproudConstants {

	private WPModel m;
	private GpsController c;

	/**
	 * @param m
	 */
	public WPController(final GpsController c, final MtkModel m) {
		super(c, m);
		this.c = c;
		this.m = (WPModel) m;
	}

	/**
	 * Fetches a log to the given filename.
	 * 
	 * @param path
	 *            Path reference.
	 */
	public void getLog(final BT747Path path) {
		WPLogDownloadHandler h = new WPLogDownloadHandler(this, m.getHandler());
		h.getWPLog(path);
		c.setDeviceOperationHandler(h);
	}

	/**
	 * Handles commands with no parameters Support for Holux-specific commands,
	 * otherwise delegates to standard MTK
	 */
	public boolean cmd(final int cmd) {
		switch (cmd) {
		// case MtkController.CMD_ERASE_LOG:
		// sendCmd(HoluxConstants.PHLX_LOG_ERASE_REQUEST);
		// break;
		case CMD_STOP_WAITING_FOR_ERASE:
			break;
		case CMD_ERASE_LOG:
			reqWPErase();
			// mtkLogHandler.eraseLog();
			break;
		case CMD_CANCEL_GETLOG:
			// mtkLogHandler.cancelGetLog();
			break;
		default:
			return false;
		}
		return true;
	}

	/**
	 * Handles commands with a parameter Support for Holux-specific commands,
	 * otherwise delegates to standard MTK
	 */
	public boolean cmd(final int cmd, final CmdParam param) {
		if (param == null) {
			return cmd(cmd);
		}
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
			// return super.cmd(cmd, param);
		}
		return false;
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
		if (logDownloadOngoing) {
			return false;
		}
		switch (dataType) {
		case MtkModel.DATA_LAST_LOG_BLOCK:
		case MtkModel.DATA_INITIAL_LOG:
			return false;
		case MtkModel.DATA_LOG_STATUS:
			reqWPDateTime();
			break;
		case MtkModel.DATA_MEM_PTS_LOGGED:
			return false;
		case MtkModel.DATA_LOG_VERSION:
			reqWPDeviceInfo();
			break;
		case MtkModel.DATA_FLASH_TYPE:
			return false;
		case MtkModel.DATA_LOG_FORMAT:
		case MtkModel.DATA_LOG_TIME_INTERVAL:
		case MtkModel.DATA_LOG_SPEED_INTERVAL:
		case MtkModel.DATA_LOG_DISTANCE_INTERVAL:
			reqWPGetSettings();
			break;
		case MtkModel.DATA_LOG_FLASH_STATUS:
		case MtkModel.DATA_LOG_FLASH_SECTOR_STATUS:
		case MtkModel.DATA_LOG_OVERWRITE_STATUS:
			return false;
		case MtkModel.DATA_MEM_USED:
			reqMemInUse();
			break;
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

		return true;
	}

	// Not sure if 4 bytes or 16 bytes of data.
	// 00 00 00 00 0A 0B FF FF FF FF FF FF FF FF FF FF
	private final void reqMemInUse() {
		m.getHandler().sendCmd(new WPEnterModeCommand());
		m.getHandler().sendCmd(
				new WPIntCommand(m, REQ_MEM_IN_USE, MtkModel.DATA_MEM_USED, 4));
		m.getHandler().sendCmd(new WPExitModeCommand());
		// m_GPSrxtx.virtualReceive("sample dataWP Update Over\0");
	}

	public final static int DATA_DATE_TIME = 1000;
	public final static int DATA_DEV_INFO1 = 1001;
	public final static int DATA_DEV_PARAM = 1002;

	/**
	 * Request data from the log. Command 0x60b80000. Response: 16 bytes of
	 * representing date and time.
	 * 
	 * 0E 18 05 09 0A 0B FF FF FF FF FF FF FF FF FF FF
	 */
	public final void reqWPDateTime() {
		m.getHandler().sendCmd(new WPEnterModeCommand());
		m.getHandler().sendCmd(
				new WPIntCommand(m, REQ_DATE_TIME, DATA_DATE_TIME, 4));
		m.getHandler().sendCmd(new WPExitModeCommand());
	}

	/**
	 * Do some selftest. Command 0x60b80000.
	 * 
	 */
	public final void reqWPTest() {
		m.getHandler().sendCmd(new WPEnterModeCommand());
		m.getHandler().sendCmd(new WPIntCommand(m, REQ_SELFTEST, -1, 4));
		m.getHandler().sendCmd(new WPExitModeCommand());
	}

	// public final void reqWPLogSize() {
	// handler.sendCmd(new WPIntCommand(REQ_LOG_SIZE, 255));
	// }

	/**
	 * Erases log data. Command 0x61b60000. Response: WP Update Over
	 * 
	 */
	public final void reqWPErase() {
		m.getHandler().sendCmd(new WPEnterModeCommand());
		m.getHandler().sendCmd(new WPIntCommand(m, REQ_ERASE, -1, 4));
		m.getHandler().sendCmd(new WPExitModeCommand());
	}

	/**
	 * Get device information Command 0x5bb00000. Response: Byte 8-5: Serial
	 * number Byte 41-48: Device type [BT-CD100 = Nemerix] [BT-CD160=SIRFIII]
	 * 
	 */
	public final void reqWPDeviceInfo() {
		m.getHandler().sendCmd(new WPEnterModeCommand());
		m.getHandler().sendCmd(
				new WPIntCommand(m, REQ_DEV_INFO1, DATA_DEV_INFO1, 255));
		m.getHandler().sendCmd(new WPExitModeCommand());
	}

	/**
	 * Get device information Command 0x62B60000. Response: Byte 4-1: Time step
	 * Byte 8-5: Distance step Byte 9: Sensitivity: 2-high, 1-middle, 3-low,
	 * 0-disable Byte 25: Tag : 0-off, 1-on
	 */

	public final void reqWPGetSettings() {
		m.getHandler().sendCmd(new WPEnterModeCommand());
		m.getHandler().sendCmd(
				new WPIntCommand(m, REQ_DEV_PARAM, DATA_DEV_PARAM, 255));
		m.getHandler().sendCmd(new WPExitModeCommand());
	}
}
