/**
 * 
 */
package net.sf.bt747.gps.mtk.agps;

import bt747.sys.Generic;

import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;
import gps.BT747Constants;
import gps.connection.MtkBinWriter;
import gps.mvc.DeviceOperationHandlerIF;
import gps.mvc.GPSLinkHandler;

/**
 * @author Mario De Weerd
 * 
 */
public class AgpsUploadHandler implements DeviceOperationHandlerIF {
    private byte[] agpsData;
    private int idx;

    private boolean firstData;
    private boolean okForNext;

    /**
     * These methods are called one after the other - not in separate threads.
     */
    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.DeviceOperationHandlerIF#analyseResponse(java.lang.Object)
     */
    public boolean analyseResponse(Object o) {
        if (o instanceof MtkBinTransportMessageModel) {
            MtkBinTransportMessageModel msg = (MtkBinTransportMessageModel) o;

            // Debugging for now:
            Generic.debug("<<" + msg.toString());
            if (firstData) {
                // No AGPS data has been sent.
                // Ok to send data.

                firstData = false;
                okForNext = true;
                return false;
            } else {
                // Must receive acknowledge for previous data.
                switch (msg.getType()) {
                case 1:
                case 2:
                    okForNext = true;
                    break;
                default:
                    break;
                }
            }
            // Check if data corresponds to sent data.
            // If not - handle error.
            // If timeout - handle error (send again?)
            // If no more data to send, remove handler
            return true; // Return true to remove handler.
        }
        // TODO Auto-generated method stub
        return false;
    }

    private static final int AGPS_PAYLOAD = 180;
    private boolean nextDone;

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.DeviceOperationHandlerIF#notifyRun(gps.mvc.GPSLinkHandler)
     */
    public boolean notifyRun(GPSLinkHandler handler) {
        if (nextDone) {
            return false; // End handler.
        }
        if (okForNext) {// if ok to send data // Should check response.
            okForNext = false;
            // Get the payload
            byte[] payload = new byte[AGPS_PAYLOAD]; // Inits to 0
            int i;
            for (i = 0; i < AGPS_PAYLOAD && idx < agpsData.length; i++, idx++) {
                payload[i] = agpsData[idx];
            }
            if (i == 0) { // No more data - send empty data and then done.
                nextDone = true;
            }
            // Send next piece of data to device.
            final MtkBinTransportMessageModel cmd = new MtkBinTransportMessageModel(
                    BT747Constants.PMTK_SET_EPO_DATA, payload);
            MtkBinWriter.sendCmd(handler.getGPSRxtx(), cmd);
        }
        return true; // Continue to run.
    }

    public final void setAgpsData(AgpsModel agpsModel) {
        setAgpsData(agpsModel.getData(0));
        idx = 0;
    }

    public final void setAgpsData(byte[] data) {
        agpsData = data;
        idx = 0;
        nextDone = false;
        firstData = true;
        okForNext = false;
    }

    private void done() {
        // Notify the application that this operation is finished.
        // Could be the controller too who notifies handler is removed.
    }

}
