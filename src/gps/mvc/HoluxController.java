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
	public HoluxController(MtkModel m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets name of the device.
	 * 
	 * @param holuxName new device name
	 */
	public void setHoluxName(final String holuxName) {
		sendCmd(HoluxConstants.PHLX_CMD_PREFIX);
		sendCmd(HoluxConstants.PHLX_NAME_SET_REQUEST + "," + holuxName);
		// reqData(MtkModel.DATA_HOLUX_NAME);
	}
}
