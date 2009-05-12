/**
 * 
 */
package gps.connection;

import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

/**
 * This state will handle binary responses from the MTK based logger device.
 * 
 * Work in progress.
 * 
 * @author Mario
 * 
 */
public class MtkBinDecoderState implements DecoderStateInterface {
    /** Initial state - waiting for first byte. */
    private final static int HEADER0_STATE = 0;
    /** First byte received. */
    private final static int HEADER1_STATE = 1;
    private final static int LEN0_STATE = 2;
    private final static int LEN1_STATE = 3;
    private final static int PAYLOAD_STATE = 4;
    private final static int CHECKSUM_STATE = 5;
    private final static int END0_STATE = 6;
    private final static int END1_STATE = 7;
    private final static int TYPE0_STATE = 8;
    private final static int TYPE1_STATE = 9;
    private final static int RECOVER_STATE = 10;
    private final static int RESPONSE_OK_STATE = 11;

    /** First byte of transport message. */
    private static final byte startByte0 = (byte) 0x04;
    /** Second byte of transport message. */
    private static final byte startByte1 = (byte) 0x24;
    /** Last-1 byte of transport message. */
    private static final byte endByte0 = (byte) 0x0D;
    /** Last byte of transport message. */
    private static final byte endByte1 = (byte) 0x0A;

    private int state = MtkBinDecoderState.HEADER0_STATE;

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#enterState(gps.connection.GPSrxtx)
     */
    public void enterState(final GPSrxtx context) {
        state = MtkBinDecoderState.HEADER0_STATE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#exitState(gps.connection.GPSrxtx)
     */
    public void exitState(final GPSrxtx context) {
        // TODO Auto-generated method stub

    }

    private int len;
    private byte checksum;
    private int type;
    private byte[] value;
    private int idx;

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#getResponse(gps.connection.GPSrxtx)
     */
    public final Object getResponse(final GPSrxtx context) {
        if (state == MtkBinDecoderState.RESPONSE_OK_STATE) {
            state = MtkBinDecoderState.HEADER0_STATE;
        }
        while (!context.isReadBufferEmpty()
                && (state != MtkBinDecoderState.RESPONSE_OK_STATE)) {
            final byte c = (byte) context.getReadBufferChar();
            // System.err.print(String.format("%02x", (int) c));
            checksum ^= c;
            switch (state) {
            default:
            case HEADER0_STATE:
                if (c == MtkBinDecoderState.startByte0) {
                    state = MtkBinDecoderState.HEADER1_STATE;
                } else if (c == MtkBinDecoderState.endByte1) {
                    state = MtkBinDecoderState.HEADER0_STATE;
                } else {
                    state = MtkBinDecoderState.RECOVER_STATE;
                }
                break;
            case HEADER1_STATE:
                if (c == MtkBinDecoderState.startByte1) {
                    state = MtkBinDecoderState.LEN0_STATE;
                } else {
                    state = MtkBinDecoderState.RECOVER_STATE;
                }
                break;
            case LEN0_STATE:
                len = c & 0xFF;
                checksum = c; // Checksum calculation starts with this byte.
                state = MtkBinDecoderState.LEN1_STATE;
                break;
            case LEN1_STATE:
                len |= ((c) & 0xFF) << 8;
                idx = 0;
                if (len < 9) { // 2 header + 2 trailer + 2 len + 2 type + 1
                                // checksum = 9 bytes
                    state = MtkBinDecoderState.RECOVER_STATE;
                } else {
                    len -= 9;
                }
                value = new byte[len];
                state = MtkBinDecoderState.TYPE0_STATE;
                break;
            case TYPE0_STATE:
                type = c & 0xFF;
                state = MtkBinDecoderState.TYPE1_STATE;
                break;
            case TYPE1_STATE:
                type |= (c & 0xFF) << 8;
                if (len <= 0) {
                    state = MtkBinDecoderState.END0_STATE;
                } else {
                    state = MtkBinDecoderState.PAYLOAD_STATE;
                }
                break;
            case PAYLOAD_STATE:
                value[idx++] = c;
                if (--len <= 0) {
                    // All data read from the link.
                    state = MtkBinDecoderState.CHECKSUM_STATE;
                }
                break;
            case CHECKSUM_STATE:
                // Checksum value should be 0 because Xored with itself.
                if (checksum != 0) {
                    // Error - try to recover
                    state = MtkBinDecoderState.RECOVER_STATE;
                } else {
                    state = MtkBinDecoderState.END0_STATE;
                }
                break;
            case END0_STATE:
                if (c != MtkBinDecoderState.endByte0) {
                    state = MtkBinDecoderState.RECOVER_STATE;
                } else {
                    state = MtkBinDecoderState.END1_STATE;
                }
                break;
            case END1_STATE:
                if (c != MtkBinDecoderState.endByte1) {
                    state = MtkBinDecoderState.RECOVER_STATE;
                } else {
                    state = MtkBinDecoderState.RESPONSE_OK_STATE;
                }
                break;
            case RECOVER_STATE:
                if (c == MtkBinDecoderState.endByte1) {
                    state = MtkBinDecoderState.HEADER0_STATE;
                }
                break;
            }
        }
        if (state == MtkBinDecoderState.RESPONSE_OK_STATE) {
            state = MtkBinDecoderState.HEADER0_STATE;
            return new MtkBinTransportMessageModel(type, value);
        }
        return null;
    }
}
