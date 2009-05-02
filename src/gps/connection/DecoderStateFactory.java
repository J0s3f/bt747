/**
 * 
 */
package gps.connection;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;

/**
 * @author Mario
 * 
 */
public final class DecoderStateFactory {
    private final static BT747Hashtable states = JavaLibBridge
            .getHashtableInstance(2);

    public final static int NMEA_STATE = 0;
    public final static int DPL700_STATE = 1;

    /**
     * Gets an instance for the requested state. Currently a Singleton
     * instance.
     * 
     * @param stateClass
     * @return Instance for the state.
     */
    public final static DecoderStateInterface getInstance(final int state) {
        final String stateStr = "" + state;
        final DecoderStateInterface instanceFromHash = (DecoderStateInterface) states
                .get("" + stateStr);
        if (instanceFromHash != null) {
            return instanceFromHash;
        } else {
            DecoderStateInterface newState;
            switch (state) {
            default:
            case NMEA_STATE:
                newState = new NMEADecoderState();
                break;
            case DPL700_STATE:
                newState = new DPL700DecoderState();
                break;
            }
            states.put(stateStr, newState);
            return newState;
        }
    }
}
