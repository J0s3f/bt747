/**
 * 
 */
package gps.connection;

/**
 * This state will handle binary responses from the MTK based logger device.
 * 
 * Work in progress.
 * 
 * @author Mario
 * 
 */
public class MtkBinDecoderState implements DecoderStateInterface {
    private final static int INIT_STATE = 0;
    private final static int HEADER0_STATE = 1;
    private final static int HEADER1_STATE = 2;
    private final static int LEN0_STATE = 3;
    private final static int LEN1_STATE = 4;
    private final static int PAYLOAD_STATE = 5;
    private final static int CHECKSUM_STATE = 6;
    private final static int END0_STATE = 7;
    private final static int END1_STATE = 8;
    private final static int END_RECOVER_STATE = 9;

    private int state = INIT_STATE;

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#enterState(gps.connection.GPSrxtx)
     */
    public void enterState(GPSrxtx context) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#exitState(gps.connection.GPSrxtx)
     */
    public void exitState(GPSrxtx context) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#getResponse(gps.connection.GPSrxtx)
     */
    public final Object getResponse(final GPSrxtx context) {
        while (!context.isReadBufferEmpty()) {
            final char c = context.getReadBufferChar();
            switch (state) {
            default:
            case INIT_STATE:
            case HEADER0_STATE:
            case HEADER1_STATE:
            case LEN0_STATE:
            case LEN1_STATE:
            case PAYLOAD_STATE:
            case CHECKSUM_STATE:
            case END0_STATE:
            case END1_STATE:
            case END_RECOVER_STATE:
                break;
            }
        }
        // TODO Auto-generated method stub
        return null;
    }

}
