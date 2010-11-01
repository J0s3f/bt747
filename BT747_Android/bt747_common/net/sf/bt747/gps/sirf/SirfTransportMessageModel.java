/**
 * Initial definition of SirfIII packet.
 */
package net.sf.bt747.gps.sirf;

/**
 * @author Mario
 * 
 */
public class SirfTransportMessageModel {

    /** The message payload. */
    private byte[] payload;

    /** First byte of transport message. */
    private static final byte startByte0 = (byte) 0xA0;
    /** Second byte of transport message. */
    private static final byte startByte1 = (byte) 0xA1;
    /** Last-1 byte of transport message. */
    private static final byte endByte0 = (byte) 0xB0;
    /** Last byte of transport message. */
    private static final byte endByte1 = (byte) 0xB1;

    public SirfTransportMessageModel(byte[] payload) {

    }

    /**
     * Get the transport message payload.
     * 
     * @return Transport message payload.
     */
    public byte[] getPayLoad() {
        return payload;
    }

    /**
     * Provide the transport message.
     * 
     * @return The transport message.
     */
    public byte[] getMessage() {
        final int transportSize = payload.length + 8;
        byte[] message = new byte[transportSize];
        // Start token
        message[0] = startByte0;
        message[1] = startByte1;
        // Payload length
        message[2] = (byte) (payload.length >> 8);
        message[3] = (byte) (payload.length & 0xFF);

        int checksum = 0;
        for (int i = 0; i < payload.length; i++) {
            message[i + 4] = payload[i];
            checksum += payload[i];
            checksum &= 0x7FFF;
        }
        message[transportSize - 3] = (byte) (checksum >> 8);
        message[transportSize - 2] = (byte) (checksum & 0xFF);
        message[transportSize - 2] = endByte0;
        message[transportSize - 1] = endByte1;
        return message;
    }

    /**
     * Sets the message. Extracts the payload internally and checks validity
     * of message.
     * 
     * @param message
     * @return true if the message is valid.
     */
    public boolean setMessage(byte[] message) {
        payload = null;

        if (message.length < 8) {
            // Message is not big enough to hold the transport layer
            return false;
        }

        if (message[0] != startByte0 || message[1] != startByte1
                || message[message.length - 2] != endByte0
                || message[message.length - 1] != endByte1) {
            // Message has bad layer
            return false;
        }
        int payloadlenght = (message[2] << 8) | (message[3] & 0xFF);
        if (message.length != payloadlenght + 8) {
            // Message has wrong length
            return false;
        }

        // Get the payload
        final int expected_checksum = (message[message.length - 4] << 8)
                | (message[message.length - 3] & 0xFF);
        payload = new byte[payloadlenght];
        int checksum = 0;
        for (int i = 0; i < payloadlenght; i++) {
            payload[i] = message[i + 4];
            checksum += payload[i];
            checksum &= 0x7FFF;
        }
        if (checksum != expected_checksum) {
            return false;
        }

        return true;
    }
}
