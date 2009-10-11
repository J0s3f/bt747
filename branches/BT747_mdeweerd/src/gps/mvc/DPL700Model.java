/**
 * 
 */
package gps.mvc;

import gps.GpsEvent;
import gps.HoluxConstants;
import gps.connection.DPL700ResponseModel;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * Model for Holux devices (PHLX command set) like the M-1000C.
 * 
 * @author Robert Tomanek <bt747.free.fr@mail.robert.tomanek.org>
 */
public class DPL700Model extends MtkModel {
    /**
     * True is the device is a Holux with PHLX command set
     */
    protected boolean holuxPHLX = false;

    /**
     * @param context
     * @param handler
     */
    public DPL700Model(Model context, GPSLinkHandler handler) {
        super(context, handler);
        // TODO Auto-generated constructor stub
    }

    private int expectedDataType = -1;

    final protected void setExpectedDataType(int type) {
        expectedDataType = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.MtkModel#analyseResponse(java.lang.Object)
     */
    protected boolean analyseResponse(Object response) {
        boolean result = false;
        if (response instanceof DPL700ResponseModel) {
            try {
            DPL700ResponseModel r = (DPL700ResponseModel) response;
            byte[] rb = r.getResponseBuffer();
            switch (expectedDataType) {
            case MtkModel.DATA_MEM_USED:
                setLogMemUsed((rb[0]&0xFF)+
                        ((rb[1]&0xFF)<<8)+
                        ((rb[2]&0xFF)<<16)+
                        ((rb[2]&0xFF)<<24));
                expectedDataType = -1;
                result = true;
                break;
            default:
                break;
            }
            } catch (Exception e) {
                Generic.debug("In DPL700Model decode", e);
            }
        } else {
            result= super.analyseResponse(response);
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
    // protected int analysePHLXCommand(final String[] sNmea) {
    // int result = -1;
    //
    // // receiving a PHLX command means we have a device that supports PHLX
    // // command set :)
    // holuxPHLX = true;
    // // this is probably asking for trouble...
    // holux = true;
    //
    // if (Generic.isDebug()) {
    // String s;
    // final int length = sNmea.length;
    //
    // s = "<";
    // for (int i = 0; i < length; i++) {
    // s += sNmea[i];
    // s += ",";
    // }
    // Generic.debug(s);
    // }
    //
    // String cmd = sNmea[0];
    // if (cmd.equals(HoluxConstants.PHLX_NAME_GET_RESPONSE)) {
    // if (sNmea.length == 2) {
    // holuxName = sNmea[1];
    // postEvent(GpsEvent.UPDATE_HOLUX_NAME);
    // }
    // } else if (cmd.equals(HoluxConstants.PHLX_LOG_ERASE_ACK)) {
    // // from
    // // gps.mvc.MTKLogDownloadHandler.handleLogFlashStatReply(String)
    // // however: simplified the mind-boggling path to access self
    // if (this.isEraseOngoing()) {
    // mtkLogHandler.signalEraseDone();
    // }
    // } else if (cmd.equals(HoluxConstants.PHLX_LOG_GET_CRITERIA_RESPONSE)) {
    // if (sNmea[1].equals(HoluxConstants.PHLX_LOG_CRITERIUM_TIME_PARAM)) {
    // logTimeIntervalX100ms = JavaLibBridge.toInt(sNmea[2])*10;
    // logDistanceIntervalDm = 0;
    // } else if (sNmea[1]
    // .equals(HoluxConstants.PHLX_LOG_CRITERIUM_DISTANCE_PARAM)) {
    // logTimeIntervalX100ms = 0;
    // logDistanceIntervalDm = JavaLibBridge.toInt(sNmea[3])*10;
    // } else {
    // // better error handling should be here
    // return result;
    // }
    //
    // dataOK |= MtkModel.C_OK_TIME;
    // setAvailable(MtkModel.DATA_LOG_TIME_INTERVAL);
    // postEvent(GpsEvent.UPDATE_LOG_TIME_INTERVAL);
    //
    // dataOK |= MtkModel.C_OK_DIST;
    // setAvailable(MtkModel.DATA_LOG_DISTANCE_INTERVAL);
    // postEvent(GpsEvent.UPDATE_LOG_DISTANCE_INTERVAL);
    // }
    //
    // return result;
    // }
    //
    // public boolean isTimeDistanceLogConditionExclusive() {
    // return true;
    // }
}
