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

    /**
     * Gets an instance for the requested state. Currently a Singleton
     * instance.
     * 
     * @param stateClass
     * @return Instance for the state.
     */
    public final static DecoderStateInterface getInstance(Class<?> stateClass) {
        final DecoderStateInterface instanceFromHash = (DecoderStateInterface) states
                .get(stateClass);
        if (instanceFromHash != null) {
            return instanceFromHash;
        } else {
            try {
                final DecoderStateInterface newState = (DecoderStateInterface) stateClass
                        .newInstance();
                states.put(stateClass, newState);
                return newState;
            } catch (Exception e) {
                Generic.debug("State instantiation", e);
            }
            return null;
        }
    }
}
