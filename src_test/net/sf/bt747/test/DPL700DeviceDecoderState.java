/**
 * 
 */
package net.sf.bt747.test;

import org.jdesktop.swingx.decorator.ResetDTCRColorHighlighter;

import gps.WondeproudConstants;
import gps.connection.DecoderStateFactory;
import gps.connection.DecoderStateInterface;
import gps.connection.GPSPort;
import gps.connection.GPSrxtx;
import gps.mvc.commands.dpl700.DPL700IntCommand;

import bt747.sys.Generic;

/**
 * This captures the commands coming from BT747. Should be optimised to be
 * limited to just that.
 * 
 * @author Mario
 * 
 */
public class DPL700DeviceDecoderState implements DecoderStateInterface {
    private byte[] DPL700_buffer;
    private int DPL700_buffer_idx;
    private final byte[] DPL700_EndString = new byte[200];
    private int endStringIdx;

    /** Size of read buffer to create */
    private static int bufferSize = 10;

    public final static void setNewBufferSize(final int size) {
        DPL700DeviceDecoderState.bufferSize = size;
    }

    private final static int getNewBufferSize() {
        return DPL700DeviceDecoderState.bufferSize;
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
        current_state = DPL700DeviceDecoderState.C_DPL700_STATE;
    }

    private final void initBuffer() {
        DPL700_buffer_idx = 0;
        DPL700_buffer = new byte[DPL700DeviceDecoderState.getNewBufferSize()];
    }
    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.connection.DecoderStateInterface#exitState(gps.connection.GPSrxtx)
     */
    public final void exitState(final GPSrxtx context) {
    }

    private static final int C_DPL700_STATE = 9;
    private static final int C_DPL700_W_STATE = 10;
    private static final int C_DPL700_P_STATE = 11;
    private static final int C_DPL700_TEXT_STATE = 12;
    private static final int C_DPL700_END_STATE = 13;
    private static final int C_DPL700_TICK_STATE = 14;
    private static final int C_DPL700_COMMAND = 15;

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
            if (DPL700_buffer_idx < DPL700_buffer.length) {
                DPL700_buffer[DPL700_buffer_idx++] = (byte) c;
                // } else {
                // rxtxMode = NORMAL_MODE;
            }

            switch (current_state) {
            case C_DPL700_STATE:
                // Vm.debug("INIT_STATE");
                // System.err.print(c);
                if (c == 'W') {
                    endStringIdx = 0;
                    DPL700_EndString[endStringIdx++] = (byte) c;
                    current_state = DPL700DeviceDecoderState.C_DPL700_W_STATE;
                } else {
                    if (DPL700_buffer_idx == 6
                            && DPL700_buffer[DPL700_buffer_idx] == 0) {
                        current_state = DPL700DeviceDecoderState.C_DPL700_COMMAND;
                        continueReading = false;
                    } else {
                        current_state = DPL700DeviceDecoderState.C_DPL700_STATE;
                    }
                }
                break;
            case C_DPL700_W_STATE:
                // Vm.debug("W_STATE");
                if (c == 'P') {
                    DPL700_EndString[endStringIdx++] = (byte) c;
                    current_state = DPL700DeviceDecoderState.C_DPL700_P_STATE;
                    break;
                } else if (c == 'W') {
                    endStringIdx = 0;
                    DPL700_EndString[endStringIdx++] = (byte) c;
                    current_state = DPL700DeviceDecoderState.C_DPL700_W_STATE;
                } else if (c == '\'') {
                    DPL700_EndString[endStringIdx++] = (byte) c;
                    current_state = DPL700DeviceDecoderState.C_DPL700_TICK_STATE;
                } else {
                    current_state = DPL700DeviceDecoderState.C_DPL700_STATE;
                }
                break;
            case C_DPL700_TICK_STATE:
                // Vm.debug("TICK_STATE");
                if (c == 'P') {
                    DPL700_EndString[endStringIdx++] = (byte) c;
                    current_state = DPL700DeviceDecoderState.C_DPL700_P_STATE;
                    break;
                } else if (c == 'W') {
                    endStringIdx = 0;
                    DPL700_EndString[endStringIdx++] = (byte) c;
                    current_state = DPL700DeviceDecoderState.C_DPL700_W_STATE;
                } else {
                    current_state = DPL700DeviceDecoderState.C_DPL700_STATE;
                }
                break;
            case C_DPL700_P_STATE:
                // Vm.debug("P_STATE");
                if (c == ' ') {
                    DPL700_EndString[endStringIdx++] = (byte) c;
                    current_state = DPL700DeviceDecoderState.C_DPL700_TEXT_STATE;
                    break;
                } else if (c == 'W') {
                    current_state = DPL700DeviceDecoderState.C_DPL700_W_STATE;
                } else {
                    current_state = DPL700DeviceDecoderState.C_DPL700_STATE;
                }
                break;
            case C_DPL700_TEXT_STATE:
                // Vm.debug("TXT_STATE");
                // Trying to read end string
                if (c == 0) {
                    DPL700_EndString[endStringIdx++] = (byte) c;
                    DPL700_buffer_idx -= endStringIdx;
                    current_state = DPL700DeviceDecoderState.C_DPL700_END_STATE;
                    Generic.debug("End DPL700");
                    //context.newState(DecoderStateFactory.NMEA_STATE);
                    continueReading = false;
                } else if (((c >= 'A') && (c <= 'Z'))
                        || ((c >= 'a') && (c <= 'z')) || (c == ' ')
                        || (c == '+') || (c == '\'')) {
                    DPL700_EndString[endStringIdx++] = (byte) c;
                } else {
                    current_state = DPL700DeviceDecoderState.C_DPL700_STATE;
                }
                break;
            default:
                current_state = DPL700DeviceDecoderState.C_DPL700_STATE;
                break;
            }
        }

        if (current_state == DPL700DeviceDecoderState.C_DPL700_COMMAND) {
            final DPL700IntCommand resp = new DPL700IntCommand(
                    ((DPL700_buffer[0] & 0xFF) << 24) + ((DPL700_buffer[1] & 0xFF) << 16) + ((DPL700_buffer[2] & 0xFF) << 8) + ((DPL700_buffer[3] & 0xFF) << 0),
                    ((DPL700_buffer[4] & 0xFF) << 8) + ((DPL700_buffer[5] & 0xFF) << 0),
                    DPL700_buffer_idx);
            return resp;
        } else if (current_state == DPL700DeviceDecoderState.C_DPL700_END_STATE) {
            final DPL700DeviceResponseModel resp = new DPL700DeviceResponseModel();
            resp.setResponseType(new String(DPL700_EndString, 0,
                    endStringIdx - 1));
            endStringIdx = 0;
            current_state = C_DPL700_STATE;
            resp.setResponseBuffer(DPL700_buffer);
            resp.setResponseSize(DPL700_buffer_idx);
            initBuffer();
            if(resp.getResponseType().equals(WondeproudConstants.WP_AP_EXIT)) {
                context.newState(new TextOrNMEADecoderState());  // Should call special factory.
            }
            if (gpsPort.debugActive()) {
                // Test to avoid unnecessary lost time
                gpsPort.writeDebug("\r\nDPL700:" + resp);
            }
            return resp;
        } else {
            return null;
        }
    }
}
