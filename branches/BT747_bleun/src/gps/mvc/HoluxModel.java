/**
 *
 */
package gps.mvc;

import gps.GpsEvent;
import gps.HoluxConstants;
import gps.convert.Conv;

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
    public HoluxModel(GpsModel context, GpsLinkHandler handler) {
        super(context, handler);
        // TODO Auto-generated constructor stub
    }

    /**
     * Analyzes responses from GPS (PHLX and others (via superclass))
     */
    public boolean analyseMtkNmea(final String[] sNmea) {
        boolean result = false;

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
     * @param sNmea
     *            array of strings (command string split on commas)
     * @return good question
     */
    protected boolean analysePHLXCommand(final String[] sNmea) {
        boolean result = false;

        // receiving a PHLX command means we have a device that supports PHLX
        // command set :)
        holuxPHLX = true;
        // this is probably asking for trouble...
        holux = true;

        if (Generic.isDebug()) {
            StringBuffer s = new StringBuffer();
            final int length = sNmea.length;

            s.append("<");
            for (int i = 0; i < length; i++) {
                s.append(sNmea[i]);
                s.append(",");
            }
            Generic.debug(s.toString());
        }

        String cmd = sNmea[0];
        if (cmd.equals(HoluxConstants.PHLX_NAME_GET_RESPONSE)) {
            if (sNmea.length == 2) {
                holuxName = sNmea[1];
                postEvent(GpsEvent.UPDATE_HOLUX_NAME);
            }
        } else if (cmd.equals(HoluxConstants.PHLX_LOG_ERASE_ACK)) {
            // from
            // gps.mvc.MTKLogDownloadHandler.handleLogFlashStatReply(String)
            // however: simplified the mind-boggling path to access self
            if (this.isEraseOngoing()) {
                mtkLogHandler.signalEraseDone();
            }
        } else if (cmd.equals(HoluxConstants.PHLX_LOG_GET_CRITERIA_RESPONSE)) {
            if (sNmea[1].equals(HoluxConstants.PHLX_LOG_CRITERIUM_TIME_PARAM)) {
                logTimeIntervalX100ms = JavaLibBridge.toInt(sNmea[2])*10;
                logDistanceIntervalDm = 0;
            } else if (sNmea[1]
                    .equals(HoluxConstants.PHLX_LOG_CRITERIUM_DISTANCE_PARAM)) {
                logTimeIntervalX100ms = 0;
                logDistanceIntervalDm = JavaLibBridge.toInt(sNmea[3])*10;
            } else {
                // better error handling should be here
                return result;
            }

            dataOK |= MtkModel.C_OK_TIME;
            setAvailable(MtkModel.DATA_LOG_TIME_INTERVAL);
            postEvent(GpsEvent.UPDATE_LOG_TIME_INTERVAL);

            dataOK |= MtkModel.C_OK_DIST;
            setAvailable(MtkModel.DATA_LOG_DISTANCE_INTERVAL);
            postEvent(GpsEvent.UPDATE_LOG_DISTANCE_INTERVAL);
        } else if(cmd.equals(HoluxConstants.PHLX_DT_DEVICE_ID)) {
        	this.deviceId = sNmea[1];
        	firmwareVersion = this.holuxFWVersion + " (" + this.deviceId + ")";
        	// TODO: postEvent
        	postEvent(GpsEvent.UPDATE_MTK_VERSION);
			setAvailable(MtkModel.DATA_LOG_VERSION);
			postEvent(GpsEvent.UPDATE_LOG_VERSION);

        } else if(cmd.equals(HoluxConstants.PHLX_DT_FW_VERSION)) {
        	this.holuxFWVersion = sNmea[1].charAt(0)+"."+sNmea[1].substring(1);
        	firmwareVersion = this.holuxFWVersion + " (" + this.deviceId + ")";
        	// TODO: postEvent
        	postEvent(GpsEvent.UPDATE_MTK_VERSION);
			setAvailable(MtkModel.DATA_LOG_VERSION);
			postEvent(GpsEvent.UPDATE_LOG_VERSION);

        } else if(cmd.equals(HoluxConstants.PHLX_DT_MEMUSED_PERCENT)) {
        	setLogMemUsed((getLogMemSize()*JavaLibBridge.toInt(sNmea[1]))/100);
        } else if(cmd.equals(HoluxConstants.PHLX_DT_OVERWRITE_MODE)) {
			logFullOverwrite = (JavaLibBridge.toInt(sNmea[2]) == 1);
			postEvent(GpsEvent.UPDATE_LOG_REC_METHOD);
        } else if(cmd.equals(HoluxConstants.PHLX_DT_SPORTS_MODE)) {
        	SportMode = JavaLibBridge.toInt(sNmea[2]);
        	// TODO: postEvent
        } else if(cmd.equals(HoluxConstants.PHLX_DT_NUMBER_OF_TRACKS)) {
			logNbrTracks = JavaLibBridge.toInt(sNmea[1]);
        } else if (cmd.equals(HoluxConstants.PHLX_DT_LOG_DOWNLOAD_ANNOUNCE_TOTAL)) {
        	logNbrLogPts = JavaLibBridge.toInt(sNmea[1]);
        	int checksum = Conv.hex2Int(sNmea[2]);
        	setLogMemUsed(logNbrLogPts*32);
			setAvailable(MtkModel.DATA_MEM_PTS_LOGGED);
			postEvent(GpsEvent.UPDATE_LOG_NBR_LOG_PTS);
			postEvent(GpsEvent.UPDATE_LOG_MEM_USED);
        }
        return result;
    }

	private String deviceId = "???";
    private String holuxFWVersion = "?.??";
    private int SportMode = 0;
    protected int logNbrTracks =0;

    public boolean isTimeDistanceLogConditionExclusive() {
        return true;
    }
}
