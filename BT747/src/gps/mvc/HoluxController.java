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
	public HoluxController(final Controller c, final MtkModel m) {
		super(c, m);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Handles commands with no parameters
	 * Support for Holux-specific commands, otherwise delegates to standard MTK
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
	 * Handles commands with a parameter
	 * Support for Holux-specific commands, otherwise delegates to standard MTK
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
    public boolean reqData(final int dataType) {
       switch (dataType) {
        	case MtkModel.DATA_DEVICE_NAME:
        		sendCmd(HoluxConstants.PHLX_CMD_PREFIX);
        		sendCmd(HoluxConstants.PHLX_NAME_GET_REQUEST);
        		break;	
        	default:
        		return super.reqData(dataType);
       }
       
       return true;
    }
	
}
