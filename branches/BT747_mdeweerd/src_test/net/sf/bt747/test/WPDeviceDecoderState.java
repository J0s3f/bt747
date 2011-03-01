/**
 * 
 */
package net.sf.bt747.test;

import gps.WondeproudConstants;
import gps.connection.DecoderStateInterface;
import gps.connection.GPSPort;
import gps.connection.GPSrxtx;
import gps.mvc.commands.wp.WPIntCommand;

import bt747.sys.Generic;

/**
 * This captures the commands coming from BT747. Should be optimised to be
 * limited to just that.
 * 
 * @author Mario
 * 
 */
public class WPDeviceDecoderState implements DecoderStateInterface {
    private byte[] wpBuffer;
    private int wpBuffer_idx;
    private final byte[] wpEndString = new byte[200];
    private int endStringIdx;

    /** Size of read buffer to create */
    private static int bufferSize = 10;

    public final static void setNewBufferSize(final int size) {
        WPDeviceDecoderState.bufferSize = size;
    }

    private final static int getNewBufferSize() {
        return WPDeviceDecoderState.bufferSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.connection.DecoderStateInterface#enterState(gps.connection.GPSrxtx)
     */
    public final void enterState(final GPSrxtx context) {
        endStringIdx = 0;
        initBuffer();
        current_state = WPDeviceDecoderState.WP_STATE;
    }

    private final void initBuffer() {
        wpBuffer_idx = 0;
        wpBuffer = new byte[WPDeviceDecoderState.getNewBufferSize()];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.connection.DecoderStateInterface#exitState(gps.connection.GPSrxtx)
     */
    public final void exitState(final GPSrxtx context) {
    }

    private static final int WP_STATE = 9;
    private static final int WP_W_STATE = 10;
    private static final int WP_P_STATE = 11;
    private static final int WP_TEXT_STATE = 12;
    private static final int WP_END_STATE = 13;
    private static final int WP_TICK_STATE = 14;
    private static final int WP_COMMAND = 15;

    private int current_state;

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderInterface#getResponse()
     */
    public final Object getResponse(final GPSrxtx context) {
        final GPSPort gpsPort = context.getGpsPort();
        boolean continueReading;
        continueReading = gpsPort.isConnected();

        if (gpsPort.debugActive()) {
            // Test to avoid unnecessary lost time
            gpsPort.writeDebug("\r\nR:" + Generic.getTimeStamp() + ":");
        }

        while (continueReading && !context.isReadBufferEmpty()) {
            // Still bytes in read buffer to interpret
            final char c = context.getReadBufferChar();
            // buffer
            // if((vCmd.getCount()!=0)&&((String[])vCmd.toStringArray())[0].charAt(0)=='P')
            // {
            // bt747.sys.Vm.debug(JavaLibBridge.toString(c));
            // System.err.print("["+c+"]");
            // }
            if (wpBuffer_idx < wpBuffer.length) {
                wpBuffer[wpBuffer_idx++] = (byte) c;
                // } else {
                // rxtxMode = NORMAL_MODE;
            }

            switch (current_state) {
            case WP_STATE:
                // Vm.debug("INIT_STATE");
                // System.err.print(c);
                if (c == 'W') {
                    endStringIdx = 0;
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDeviceDecoderState.WP_W_STATE;
                } else {
                    if (wpBuffer_idx == 7 // Pointing to next position
                            && c == 0) {
                        current_state = WPDeviceDecoderState.WP_COMMAND;
                        continueReading = false;
                    } else {
                        current_state = WPDeviceDecoderState.WP_STATE;
                    }
                }
                break;
            case WP_W_STATE:
                // Vm.debug("W_STATE");
                if (c == 'P') {
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDeviceDecoderState.WP_P_STATE;
                    break;
                } else if (c == 'W') {
                    endStringIdx = 0;
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDeviceDecoderState.WP_W_STATE;
                } else if (c == '\'') {
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDeviceDecoderState.WP_TICK_STATE;
                } else {
                    current_state = WPDeviceDecoderState.WP_STATE;
                }
                break;
            case WP_TICK_STATE:
                // Vm.debug("TICK_STATE");
                if (c == 'P') {
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDeviceDecoderState.WP_P_STATE;
                    break;
                } else if (c == 'W') {
                    endStringIdx = 0;
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDeviceDecoderState.WP_W_STATE;
                } else {
                    current_state = WPDeviceDecoderState.WP_STATE;
                }
                break;
            case WP_P_STATE:
                // Vm.debug("P_STATE");
                if (c == ' ') {
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDeviceDecoderState.WP_TEXT_STATE;
                    break;
                } else if (c == 'W') {
                    current_state = WPDeviceDecoderState.WP_W_STATE;
                } else {
                    current_state = WPDeviceDecoderState.WP_STATE;
                }
                break;
            case WP_TEXT_STATE:
                // Vm.debug("TXT_STATE");
                // Trying to read end string
                if (c == 0) {
                    wpEndString[endStringIdx++] = (byte) c;
                    wpBuffer_idx -= endStringIdx;
                    current_state = WPDeviceDecoderState.WP_END_STATE;
                    Generic.debug("End Wonde Proud");
                    // context.newState(DecoderStateFactory.NMEA_STATE);
                    continueReading = false;
                } else if (((c >= 'A') && (c <= 'Z'))
                        || ((c >= 'a') && (c <= 'z')) || (c == ' ')
                        || (c == '+') || (c == '\'')) {
                    wpEndString[endStringIdx++] = (byte) c;
                } else {
                    current_state = WPDeviceDecoderState.WP_STATE;
                }
                break;
            default:
                current_state = WPDeviceDecoderState.WP_STATE;
                break;
            }
        }

        if (current_state == WPDeviceDecoderState.WP_COMMAND) {
            final WPIntCommand resp = new WPIntCommand(
                    null,
                    ((wpBuffer[0] & 0xFF) << 24)
                            + ((wpBuffer[1] & 0xFF) << 16)
                            + ((wpBuffer[2] & 0xFF) << 8)
                            + ((wpBuffer[3] & 0xFF) << 0),
                    -1,
                    ((wpBuffer[4] & 0xFF) << 8) + ((wpBuffer[5] & 0xFF) << 0),
                    wpBuffer_idx);
            wpBuffer_idx = 0;
            current_state = WP_STATE;
            return resp;
        } else if (current_state == WPDeviceDecoderState.WP_END_STATE) {
            final WPDeviceResponseModel resp = new WPDeviceResponseModel();
            resp
                    .setResponseType(new String(wpEndString, 0,
                            endStringIdx - 1));
            endStringIdx = 0;
            current_state = WP_STATE;
            resp.setResponseBuffer(wpBuffer);
            resp.setResponseSize(wpBuffer_idx);
            initBuffer();
            if (resp.getResponseType().equals(WondeproudConstants.WP_AP_EXIT)) {
                context.newState(new TextOrNMEADecoderState()); // Should call
                                                                // special
                                                                // factory.
            }
            if (gpsPort.debugActive()) {
                // Test to avoid unnecessary lost time
                gpsPort.writeDebug("\r\nWP:" + resp);
            }
            return resp;
        } else {
            return null;
        }
    }
}
