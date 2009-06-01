/**
 * 
 */
package gps.mvc;

import gps.BT747Constants;
import gps.GpsEvent;
import gps.HoluxConstants;
import net.sf.bt747.util.GpsConvert;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * Model for Holux devices (PHLX command set) like the M-1000C.
 * 
 * @author Robert Tomanek <bt747.free.fr@mail.robert.tomanek.org>
 */
public class HoluxModel extends MtkModel {
	/**
	 * True is the device is a Holux with PHLX command set
	 */
	protected boolean holuxPHLX = false;

	/**
	 * @param context
	 * @param handler
	 */
	public HoluxModel(Model context, GPSLinkHandler handler) {
		super(context, handler);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Analyzes responses from GPS (PHLX and others (via superclass))
	 */
    public int analyseMtkNmea(final String[] sNmea) {
        int result = -1;

        if (sNmea[0].startsWith("PHLX")) {
        	result = analysePHLXCommand(sNmea);
        } else {
        	result = super.analyseMtkNmea(sNmea);
        }

        return result;
    } 

    /**
     * Analyzes a PHLX command sent by the GPS device (i.e. a response)
     * 
     * @param sNmea array of strings (command string split on commas)
     * @return good question
     */
	protected int analysePHLXCommand(final String[] sNmea) {
		int result = -1;

		// receiving a PHLX command means we have a device that supports PHLX command set :)
		holuxPHLX = true;
		// this is probably asking for trouble...
		holux = true;

		if (Generic.isDebug()) {
		    String s;
		    final int length = sNmea.length;

		    s = "<";
		    for (int i = 0; i < length; i++) {
		        s += sNmea[i];
		        s += ",";
		    }
		    Generic.debug(s);
		}
		
		String cmd = sNmea[0];
		if (cmd.equals(HoluxConstants.PHLX_NAME_GET_RESPONSE)) {
		    if (sNmea.length == 2) {
		        holuxName = sNmea[1];
		        postEvent(GpsEvent.UPDATE_HOLUX_NAME);
		    }
		} else if (cmd.equals(HoluxConstants.PHLX_LOG_ERASE_ACK)) {
			// from gps.mvc.MTKLogDownloadHandler.handleLogFlashStatReply(String)
			// however: simplified the mind-boggling path to access self
            if (this.isEraseOngoing()) {
            	mtkLogHandler.signalEraseDone();
            }
		}
		
		return result;		
	}    
        
}
