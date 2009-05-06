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

    private int state = HEADER0_STATE;

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#enterState(gps.connection.GPSrxtx)
     */
    public void enterState(GPSrxtx context) {
        state = HEADER0_STATE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#exitState(gps.connection.GPSrxtx)
     */
    public void exitState(GPSrxtx context) {
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
        if (state == RESPONSE_OK_STATE) {
            state = HEADER0_STATE;
        }
        while (!context.isReadBufferEmpty() && state != RESPONSE_OK_STATE) {
            final byte c = (byte) context.getReadBufferChar();
            checksum ^= c;
            switch (state) {
            default:
            case HEADER0_STATE:
                if (c == startByte0) {
                    state = HEADER1_STATE;
                } else {
                    state = RECOVER_STATE;
                }
                break;
            case HEADER1_STATE:
                if (c == startByte1) {
                    state = LEN0_STATE;
                } else {
                    state = RECOVER_STATE;
                }
                break;
            case LEN0_STATE:
                len = c & 0xFF;
                checksum = c; // Checksum calculation starts with this byte.
                state = LEN1_STATE;
                break;
            case LEN1_STATE:
                len |= ((c) & 0xFF) << 8;
                idx = 0;
                if (len < 9) {  // 2 header + 2 trailer + 2 len + 2 type + 1 checksum = 9 bytes
                    state = RECOVER_STATE;
                } else {
                    len -= 9;
                }
                value = new byte[len];
                state = TYPE0_STATE;
                break;
            case TYPE0_STATE:
                type = c & 0xFF;
                state = TYPE1_STATE;
                break;
            case TYPE1_STATE:
                type |= (c & 0xFF) << 8;
                if (len <= 0) {
                    state = END0_STATE;
                } else {
                    state = PAYLOAD_STATE;
                }
                break;
            case PAYLOAD_STATE:
                value[idx++] = c;
                if (--len <= 0) {
                    // All data read from the link.
                    state = CHECKSUM_STATE;
                }
                break;
            case CHECKSUM_STATE:
                // Checksum value should be 0 because Xored with itself.
                if (checksum != 0) {
                    // Error - try to recover
                    state = RECOVER_STATE;
                } else {
                    state = END0_STATE;
                }
                break;
            case END0_STATE:
                if (c != endByte0) {
                    state = RECOVER_STATE;
                } else {
                    state = END1_STATE;
                }
                break;
            case END1_STATE:
                if (c != endByte1) {
                    state = RECOVER_STATE;
                } else {
                    state = RESPONSE_OK_STATE;
                }
                break;
            case RECOVER_STATE:
                if (c == endByte1) {
                    state = HEADER0_STATE;
                }
                break;
            }
        }
        if (state == RESPONSE_OK_STATE) {
            state = HEADER0_STATE;
            return new MtkBinTransportMessageModel(type, value);
        }
        return null;
    }
}
