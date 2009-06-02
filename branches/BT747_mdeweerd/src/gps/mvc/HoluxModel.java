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
	 * Analyzes responses from GPS (PMTK, HOLUX001, PHLX)
	 */
    public int analyseMtkNmea(final String[] sNmea) {
        int result = -1;

        if (sNmea[0].startsWith("PMTK")) {
            result = analysePMTKCommand(sNmea);
        } else if (sNmea[0].equals("HOLUX001")) {
            result = analyseHOLUX001Command(sNmea);
        } else if (sNmea[0].startsWith("PHLX")) {
        	result = analysePHLXCommand(sNmea);
        } else {
        	// TODO: Handle unknown command set
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
    
	/**
	 * HOLUX001 portion of analyseMtkNmea
	 * 
	 * @param sNmea
	 * @return
	 */
	protected int analyseHOLUX001Command(final String[] sNmea) {
		int cmd;
		int result;
		holux = true;
		result = -1; // Suppose cmd not treated
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
		cmd = JavaLibBridge.toInt(sNmea[1]);

		result = -1; // Suppose cmd not treated
		switch (cmd) {
		case BT747Constants.HOLUX_API_DT_NAME:
		    if (sNmea.length == 3) {
		        holuxName = sNmea[2];
		        postEvent(GpsEvent.UPDATE_HOLUX_NAME);
		    }
		    break;
		default:
		    break;
		}
		return result;
	}

	/**
	 * PMTK portion of analyseMtkNmea
	 * 
	 * @param sNmea
	 * @return
	 */
	protected int analysePMTKCommand(final String[] sNmea) {
		int cmd;
		int result;
		if (Generic.isDebug()) {
		    String s;
		    int length = sNmea.length;
		    if (sNmea[1].charAt(0) == '8') {
		        length = 3;
		    }
		    s = "<";
		    for (int i = 0; i < length; i++) {
		        s += sNmea[i];
		        s += ",";
		    }
		    Generic.debug(s);
		}
		cmd = JavaLibBridge.toInt(sNmea[0].substring(4));

		result = -1; // Suppose cmd not treated
		switch (cmd) {
		case BT747Constants.PMTK_CMD_LOG: // CMD 182;
		    result = analyseLogNmea(sNmea);
		    break;
		case BT747Constants.PMTK_TEST: // CMD 000
		    break;
		case BT747Constants.PMTK_ACK: // CMD 001
		    result = analyseMTK_Ack(sNmea);
		    break;
		case BT747Constants.PMTK_SYS_MSG: // CMD 010
		    break;
		case BT747Constants.PMTK_DT_FIX_CTL: // CMD 500
		    if (sNmea.length >= 2) {
		        logFixPeriod = JavaLibBridge.toInt(sNmea[1]);
		        setAvailable(MtkModel.DATA_FIX_PERIOD);
		        postEvent(GpsEvent.UPDATE_FIX_PERIOD);
		    }
		    dataOK |= MtkModel.C_OK_FIX;
		    break;
		case BT747Constants.PMTK_DT_DGPS_MODE: // CMD 501
		    if (sNmea.length == 2) {
		        dgpsMode = JavaLibBridge.toInt(sNmea[1]);
		    }
		    dataOK |= MtkModel.C_OK_DGPS;
		    postEvent(GpsEvent.UPDATE_DGPS_MODE);
		    break;
		case BT747Constants.PMTK_DT_SBAS: // CMD 513
		    if (sNmea.length == 2) {
		        SBASEnabled = (sNmea[1].equals("1"));
		    }
		    dataOK |= MtkModel.C_OK_SBAS;
		    postEvent(GpsEvent.UPDATE_SBAS);
		    break;
		case BT747Constants.PMTK_DT_NMEA_OUTPUT: // CMD 514
		    if (sNmea.length - 1 == BT747Constants.C_NMEA_SEN_COUNT) {
		        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
		            NMEA_periods[i] = JavaLibBridge.toInt(sNmea[i + 1]);
		        }
		    }
		    dataOK |= MtkModel.C_OK_NMEA;
		    postEvent(GpsEvent.UPDATE_OUTPUT_NMEA_PERIOD);
		    break;
		case BT747Constants.PMTK_DT_SBAS_TEST: // CMD 513
		    if (sNmea.length == 2) {
		        SBASTestEnabled = (sNmea[1].equals("0"));
		    }
		    dataOK |= MtkModel.C_OK_SBAS_TEST;
		    postEvent(GpsEvent.UPDATE_SBAS_TEST);
		    break;
		case BT747Constants.PMTK_DT_PWR_SAV_MODE: // CMD 520
		    if (sNmea.length == 2) {
		        powerSaveEnabled = (sNmea[1].equals("1"));
		    }
		    postEvent(GpsEvent.UPDATE_PWR_SAV_MODE);
		    break;
		case BT747Constants.PMTK_DT_DATUM: // CMD 530
		    if (sNmea.length == 2) {
		        datum = JavaLibBridge.toInt(sNmea[1]);
		    }
		    dataOK |= MtkModel.C_OK_DATUM;
		    postEvent(GpsEvent.UPDATE_DATUM);
		    break;
		case BT747Constants.PMTK_DT_FLASH_USER_OPTION: // CMD 590
		    dtUserOptionTimesLeft = JavaLibBridge.toInt(sNmea[1]);
		    dtUpdateRate = JavaLibBridge.toInt(sNmea[2]);
		    dtBaudRate = JavaLibBridge.toInt(sNmea[3]);
		    dtGLL_Period = JavaLibBridge.toInt(sNmea[4]);
		    dtRMC_Period = JavaLibBridge.toInt(sNmea[5]);
		    dtVTG_Period = JavaLibBridge.toInt(sNmea[6]);
		    dtGSA_Period = JavaLibBridge.toInt(sNmea[7]);
		    dtGSV_Period = JavaLibBridge.toInt(sNmea[8]);
		    dtGGA_Period = JavaLibBridge.toInt(sNmea[9]);
		    dtZDA_Period = JavaLibBridge.toInt(sNmea[10]);
		    dtMCHN_Period = JavaLibBridge.toInt(sNmea[11]);
		    postEvent(GpsEvent.UPDATE_FLASH_CONFIG);
		    break;
		case BT747Constants.PMTK_DT_BT_MAC_ADDR: // CMD 592
		    if (sNmea[1].length() == 12) {
		        sBtMacAddr = sNmea[1].substring(10, 12) + ":"
		                + sNmea[1].substring(8, 10) + ":"
		                + sNmea[1].substring(6, 8) + ":"
		                + sNmea[1].substring(4, 6) + ":"
		                + sNmea[1].substring(2, 4) + ":"
		                + sNmea[1].substring(0, 2);
		    }
		    postEvent(GpsEvent.UPDATE_BT_MAC_ADDR);
		    break;
		case BT747Constants.PMTK_DT_DGPS_INFO: // CMD 702
		    /* Not handled */
		    break;
		case BT747Constants.PMTK_DT_VERSION: // CMD 704
		    mainVersion = sNmea[1] + "." + sNmea[2] + "." + sNmea[3];
		    setAvailable(MtkModel.DATA_MTK_VERSION);
		    postEvent(GpsEvent.UPDATE_MTK_VERSION);
		    break;
		case BT747Constants.PMTK_DT_RELEASE: // CMD 705
		    firmwareVersion = sNmea[1];
		    model = sNmea[2];
		    if (sNmea.length >= 4) {
		        device = sNmea[3];
		        firmwareVersion += " (" + device + ")";
		    } else {
		        device = "";
		    }
		    setAvailable(MtkModel.DATA_MTK_RELEASE);
		    postEvent(GpsEvent.UPDATE_MTK_RELEASE);
		    break;
		case BT747Constants.PMTK_DT_EPO_INFO:
		    if (sNmea.length >= 10) {
		        hasAgps = true;
		        agpsDataCount = JavaLibBridge.toInt(sNmea[1]);
		        agpsStartTime = GpsConvert.toTime(JavaLibBridge
		                .toInt(sNmea[2]), JavaLibBridge.toInt(sNmea[3]));
		        agpsEndTime = GpsConvert.toTime(JavaLibBridge
		                .toInt(sNmea[4]), JavaLibBridge.toInt(sNmea[5]));
		        agpsStart2Time = GpsConvert.toTime(JavaLibBridge
		                .toInt(sNmea[6]), JavaLibBridge.toInt(sNmea[7]));
		        agpsEnd2Time = GpsConvert.toTime(JavaLibBridge
		                .toInt(sNmea[8]), JavaLibBridge.toInt(sNmea[9]));
		        setAvailable(MtkModel.DATA_AGPS_STORED_RANGE);
		    }
		    break;
		default:
		    break;
		} // End switch
		return result;
	}
	
    
}
