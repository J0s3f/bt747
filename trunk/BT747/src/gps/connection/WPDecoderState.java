/**
 * 
 */
package gps.connection;

import bt747.sys.Generic;

/**
 * Refactoring is ongoing.
 * 
 * @author Mario
 * 
 */
public class WPDecoderState implements DecoderStateInterface {
    private byte[] wpBuffer;
    private int wpBuffer_idx;
    private final byte[] wpEndString = new byte[200];
    private int endStringIdx;

    /** Size of read buffer to create */
    private static int bufferSize;

    public final static void setNewBufferSize(final int size) {
        WPDecoderState.bufferSize = size;
    }

    private final static int getNewBufferSize() {
        return WPDecoderState.bufferSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.connection.DecoderStateInterface#enterState(gps.connection.GPSrxtx)
     */
    public final void enterState(final GPSrxtx context) {
        endStringIdx = 0;
        current_state = WPDecoderState.WP_STATE;
    }

    private final void initBuffer() {
        wpBuffer_idx = 0;
        wpBuffer = new byte[WPDecoderState.getNewBufferSize()];
        //Generic.debug("wpBuffer init=" + wpBuffer.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.connection.DecoderStateInterface#exitState(gps.connection.GPSrxtx)
     */
    public final void exitState(final GPSrxtx context) {
        wpBuffer = null;
    }

    private static final int WP_STATE = 9;
    private static final int WP_W_STATE = 10;
    private static final int WP_P_STATE = 11;
    private static final int WP_TEXT_STATE = 12;
    private static final int WP_END_STATE = 13;
    private static final int WP_TICK_STATE = 14;
    private static final int WP_BUFFER_FULL_STATE = 15;

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
            byte c = context.getReadBufferByte();
            // buffer
            // if((vCmd.getCount()!=0)&&((String[])vCmd.toStringArray())[0].charAt(0)=='P')
            // {
            // bt747.sys.Vm.debug(JavaLibBridge.toString(c));
            // System.err.print("["+c+"]");
            // }
            if (wpBuffer == null) {
                initBuffer();
            }
            if (current_state == WP_STATE) {
                while (wpBuffer_idx < wpBuffer.length) {
                    wpBuffer[wpBuffer_idx++] = (byte) c;
                    if (c != 'W' && !context.isReadBufferEmpty()) {
                        // get next character.
                        c = context.getReadBufferByte();
                    } else {
                        // Need to change state.
                        break;
                    }
                }
            } else {
                if (wpBuffer_idx < wpBuffer.length) {
                    wpBuffer[wpBuffer_idx++] = (byte) c;
                    // } else {
                    // rxtxMode = NORMAL_MODE;
                }
            }

            if (wpBuffer_idx == wpBuffer.length) {
                current_state = WP_BUFFER_FULL_STATE;
                break;
            }
            switch (current_state) {
            case WP_END_STATE:
            case WP_STATE:
                // Vm.debug("INIT_STATE");
                // System.err.print(c);
                if (c == 'W') {
                    endStringIdx = 0;
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDecoderState.WP_W_STATE;
                } else {
                    current_state = WPDecoderState.WP_STATE;
                }
                break;
            case WP_W_STATE:
                // Vm.debug("W_STATE");
                if (c == 'P') {
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDecoderState.WP_P_STATE;
                    break;
                } else if (c == 'W') {
                    endStringIdx = 0;
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDecoderState.WP_W_STATE;
                } else if (c == '\'') {
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDecoderState.WP_TICK_STATE;
                } else {
                    current_state = WPDecoderState.WP_STATE;
                }
                break;
            case WP_TICK_STATE:
                // Vm.debug("TICK_STATE");
                if (c == 'P') {
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDecoderState.WP_P_STATE;
                    break;
                } else if (c == 'W') {
                    endStringIdx = 0;
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDecoderState.WP_W_STATE;
                } else {
                    current_state = WPDecoderState.WP_STATE;
                }
                break;
            case WP_P_STATE:
                // Vm.debug("P_STATE");
                if (c == ' ') {
                    wpEndString[endStringIdx++] = (byte) c;
                    current_state = WPDecoderState.WP_TEXT_STATE;
                    break;
                } else if (c == 'W') {
                    current_state = WPDecoderState.WP_W_STATE;
                } else {
                    current_state = WPDecoderState.WP_STATE;
                }
                break;
            case WP_TEXT_STATE:
                // Vm.debug("TXT_STATE");
                // Trying to read end string
                if (c == 0) {
                    wpEndString[endStringIdx++] = (byte) c;
                    wpBuffer_idx -= endStringIdx;
                    current_state = WPDecoderState.WP_END_STATE;
                    continueReading = false;
                } else if (((c >= 'A') && (c <= 'Z'))
                        || ((c >= 'a') && (c <= 'z')) || (c == ' ')
                        || (c == '+') || (c == '\'')) {
                    wpEndString[endStringIdx++] = (byte) c;
                } else {
                    current_state = WPDecoderState.WP_STATE;
                }
                break;
            default:
                current_state = WPDecoderState.WP_STATE;
                break;
            }
        }

        if (current_state == WPDecoderState.WP_BUFFER_FULL_STATE) {
            final WPResponseModel resp = new WPResponseModel();
            resp.setResponseType("buffer");
            resp.setResponseBuffer(wpBuffer);
            resp.setResponseSize(wpBuffer_idx);
//            Generic.debug("\r\nWP:" + resp + " " + wpBuffer_idx
//                    + " " + wpBuffer.length);
            wpBuffer = null;
            current_state = WP_STATE;
            return resp;
        } else if (current_state == WPDecoderState.WP_END_STATE) {
            // context.newState(DecoderStateFactory.NMEA_STATE);
            final WPResponseModel resp = new WPResponseModel();
            resp.setResponseType(new String(wpEndString, 0,
                    endStringIdx - 1));
            resp.setResponseBuffer(wpBuffer);
            resp.setResponseSize(wpBuffer_idx);
            Generic.debug("\r\nWP:" + resp + " " + wpBuffer_idx
                    + " " + wpBuffer.length);
            wpBuffer = null;
            current_state = WP_STATE;
            // context.newState(DecoderStateFactory.NMEA_STATE); // Not sure
            // this is appropriate.
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
