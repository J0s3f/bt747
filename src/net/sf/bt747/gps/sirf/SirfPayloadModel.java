/**
 * 
 */
package net.sf.bt747.gps.sirf;

/**
 * @author Mario
 *
 */
public class SirfPayloadModel {
    /** Payload is bitmapped value */
    public static final int PAYLOAD_TYPE_DISCRETE = 0;
    /** Payload has signed two's complement value */
    public static final int PAYLOAD_TYPE_SIGNED = 1;
    /** Payload has unsigned value */
    public static final int PAYLOAD_TYPE_UNSIGNED = 2;
    /** Payload has double precision floating point value */
    public static final int PAYLOAD_TYPE_DBL = 3;
    /** Payload has single precision floating point value */
    public static final int PAYLOAD_TYPE_SGL = 4;
    
    private byte[] payload;
    
    public final void setPayload(byte[] payload) {
        this.payload = payload;
    }
    
    public final byte[] getPayload() {
        return payload;
    }
    
    public final byte getMessageId() {
        return payload[0];
    }
    
    
}
