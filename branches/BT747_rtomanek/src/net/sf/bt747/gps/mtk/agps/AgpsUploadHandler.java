/**
 * 
 */
package net.sf.bt747.gps.mtk.agps;

import gps.BT747Constants;
import gps.connection.MtkBinWriter;
import gps.mvc.DeviceOperationHandlerIF;
import gps.mvc.GPSLinkHandler;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

import bt747.model.EventPoster;
import bt747.model.ModelEvent;
import bt747.sys.ByteDataStream;
import bt747.sys.Generic;
import bt747.sys.I18N;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747InputStream;
import bt747.sys.interfaces.BT747Int;

/**
 * Serial handler for AGPS data upload.
 * 
 * @author Mario De Weerd
 * 
 */
public class AgpsUploadHandler implements DeviceOperationHandlerIF {
    /** Data stream where the data is fetched from. */
    private BT747InputStream agpsDataStream;
    /** The amount of useful payload being sent in each packet. */
    private static final int AGPS_PAYLOAD = 180;
    /** Delay after which this mode times out. */
    private static final int TIMEOUT = 6000; // MS
    /**
     * Index for next packet to be sent. Index becomes negative when no more
     * packets.
     */
    private int nxtPacketIdx;

    /** Next index to increment percent. */
    private int nextPacketPercentIdx;
    private int nextPacketPercentOffset;
    private int percentBytes = (32 * 60 * 4 * 14) / 100; // Max 14d data

    private final static int PERCENT_STEP = 10;
    private final EventPoster poster;

    public AgpsUploadHandler(final EventPoster poster) {
        this.poster = poster;
    }

    public AgpsUploadHandler() {
        this(null);
    }

    /**
     * 
     */
    private boolean sendNextOK;
    /** Absolute time at which the timeout occurs. */
    private int timesOutAt;
    /** Last command sent */
    private MtkBinTransportMessageModel cmd;
    /** Error count */
    private int errorCnt;
    /** Maximum error count */
    private static final int MAX_ERROR_CNT = 3;
    /** Percentage indicating advancement that has been reported */
    private int percent;

    /**
     * These methods are called one after the other - not in separate threads.
     */
    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.DeviceOperationHandlerIF#analyseResponse(java.lang.Object)
     */
    public boolean analyseResponse(final Object o) {
        if (o instanceof MtkBinTransportMessageModel) {
            final MtkBinTransportMessageModel msg = (MtkBinTransportMessageModel) o;

            // Debugging for now:
            Generic.debug("<<" + msg.toString());
            if (nxtPacketIdx == 0) {
                sendNextOK = true;
            } else {
                // Must receive acknowledge for previous data.
                switch (msg.getType()) {
                case 1:
                    break;
                case 2:
                    errorCnt = 0;

                    // Check if data corresponds to sent data.
                    final byte[] p = msg.getPayLoad();
                    final int pkt = (p[0] & 0xFF) + ((p[1] & 0xFF) << 8);
                    final int rsp = p[2];
                    if (rsp != 1) {
                        // Exceed error count to force stop.
                        errorCnt = MAX_ERROR_CNT;
                        Generic
                                .debug("Error packet from device during AGPS upload.");
                    }
                    if (pkt >= nxtPacketIdx - 1) {
                        sendNextOK = true;
                    }
                    // else : Do not care for now - the device sometimes sends
                    // a double confirmation.
                    // Let the timeout happen or wait for the actual
                    // confirmation.

                    break;
                default:
                    break;
                }
            }
            return true; // Return true to indicate message has been
            // treated and to skip other handlers.
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.DeviceOperationHandlerIF#notifyRun(gps.mvc.GPSLinkHandler)
     */
    public boolean notifyRun(final GPSLinkHandler handler)
            throws BT747Exception {
        if (nxtPacketIdx < 0) {
            stopUploadMode(handler);
            return false; // End handler.
        }
        if (timesOutAt == 0) {
            resetTimeOut();
        } else if ((Generic.getTimeStamp() > timesOutAt)) {
            errorCnt++;
            if (errorCnt < AgpsUploadHandler.MAX_ERROR_CNT) {
                MtkBinWriter.sendCmd(handler, cmd);
                resetTimeOut();
                return true;
            }
        }
        if (errorCnt >= MAX_ERROR_CNT) {
            /* Max error count exceeded - stop. */
            stopUploadMode(handler);
            throw new BT747Exception(I18N
                    .i18n("Too many errors during AGPS upload"));
        }
        if (sendNextOK) {
            // OK to send data
            sendNextOK = false;
            /*
             * The command could be stored to send it again in case of
             * communication failure.
             */
            cmd = getNextPacketCmd();
            MtkBinWriter.sendCmd(handler, cmd);
            if (nxtPacketIdx * AGPS_PAYLOAD > nextPacketPercentOffset) {
                percent++;
                nextPacketPercentOffset += percentBytes;
                notifyPercent(percent);
            }
            if (Generic.isDebug()) {
                Generic.debug("Sent AGPS data:" + cmd.toString());
            }
            resetTimeOut();
        }
        return true; // Continue to run.
    }

    /**
     * Sets the data stream to fetch the AGPS data from. This interface allows
     * some flexibility in the actual source.
     * 
     * @param is
     */
    public final void setAgpsData(final BT747InputStream is) {
        agpsDataStream = is;
        nxtPacketIdx = 0;
        sendNextOK = false;
        timesOutAt = 0;
        errorCnt = 0;
        percent = 0;
        nextPacketPercentIdx = PERCENT_STEP;
        nextPacketPercentOffset = percentBytes;
    }

    /**
     * Uses the bytes data as AGPS data.
     * 
     * @param data
     */
    public final void setAgpsData(final byte[] data) {
        percentBytes = data.length / 100;
        setAgpsData(new ByteDataStream(data));
    }

    /**
     * Prepares the next packet command to send.
     * 
     * @return Next packet command.
     */
    private final MtkBinTransportMessageModel getNextPacketCmd() {
        final byte[] payload = new byte[AgpsUploadHandler.AGPS_PAYLOAD + 2];
        final int size = agpsDataStream.readBytes(payload, 2,
                AgpsUploadHandler.AGPS_PAYLOAD);
        if (size > 0) {
            // Still data to send
            payload[0] = (byte) (nxtPacketIdx & 0xFF);
            payload[1] = (byte) ((nxtPacketIdx >> 8) & 0xFF);
            nxtPacketIdx++;
        } else {
            // No more data to send
            payload[0] = (byte) 0xFF;
            payload[1] = (byte) 0xFF;
            // The data is 0.
            // No more data - send empty data and then done.
            nxtPacketIdx = -1;
        }
        // Send next piece of data to device.
        return new MtkBinTransportMessageModel(
                BT747Constants.PMTK_SET_EPO_DATA, payload);
    }

    /**
     * Resets the timeout to a new value.
     */
    private final void resetTimeOut() {
        timesOutAt = Generic.getTimeStamp() + AgpsUploadHandler.TIMEOUT;
    }

    /**
     * Ends the upload mode.
     * 
     * @param handler
     */
    private final void stopUploadMode(final GPSLinkHandler handler) {
        MtkBinWriter.doSetNmeaMode(handler.getGPSRxtx());
        Generic.debug("AGPS upload stopped/finished");
        notifyDone();
    }

    /**
     * Notify model listeners that the upload is done.
     */
    private final void notifyDone() {
        if (poster != null) {
            final ModelEvent e = new ModelEvent(ModelEvent.AGPS_UPLOAD_DONE,
                    null);
            poster.postEvent(e);
        }
    }

    /**
     * Notify model listeners that we advanced in the upload.
     * 
     * @param percent
     */
    private final void notifyPercent(final int percent) {
        if (poster != null) {
            final ModelEvent e = new ModelEvent(
                    ModelEvent.AGPS_UPLOAD_PERCENT, BT747Int.get(percent));
            poster.postEvent(e);
        }
    }
}
