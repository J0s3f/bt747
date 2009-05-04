/**
 * Initial definition of MTK binary packet.
 */
package net.sf.bt747.gps.mtk;

/**
 * Model for MTK binary data package.
 * 
 * @author Mario De Weerd
 * 
 */
public class MtkBinTransportMessageModel {

    /** The message payload. */
    private byte[] payload;
    private int payloadType;

    /** First byte of transport message. */
    private static final byte startByte0 = (byte) 0x04;
    /** Second byte of transport message. */
    private static final byte startByte1 = (byte) 0x24;
    /** Last-1 byte of transport message. */
    private static final byte endByte0 = (byte) 0x0D;
    /** Last byte of transport message. */
    private static final byte endByte1 = (byte) 0x0A;

    /**
     * @param type
     * @param payload
     *                The Payload - warning the pointer is taken !
     */
    public MtkBinTransportMessageModel(int type, byte[] payload) {
        payloadType = type;
        this.payload = payload;
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
        final int transportSize = payload.length + 9;
        byte[] message = new byte[transportSize];
        // Start token
        message[0] = startByte0;
        message[1] = startByte1;
        // Payload length
        message[2] = (byte) (transportSize & 0xFF);
        message[3] = (byte) (transportSize >> 8);
        message[4] = (byte) (payloadType & 0xFF);
        message[5] = (byte) (payloadType >> 8);

        for (int i = 0; i < payload.length; i++) {
            message[i + 6] = payload[i];
        }

        byte xorChecksum = 0;
        for (int i = 2; i < transportSize - 3; i++) {
            xorChecksum ^= message[i];
        }
        message[transportSize - 3] = xorChecksum;
        message[transportSize - 2] = endByte0;
        message[transportSize - 1] = endByte1;
        return message;
    }

    /**
     * Sets the message. Extracts the payload internally and checks validity
     * of message.
     * 
     * @param message
     * @return
     */
    public boolean setMessage(byte[] message) {
        payload = null;

        if (message.length < 9) {
            // Message is not big enough to hold the transport layer
            return false;
        }

        if (message[0] != startByte0 || message[1] != startByte1
                || message[message.length - 2] != endByte0
                || message[message.length - 1] != endByte1) {
            // Message has bad layer
            return false;
        }
        int payloadlenght = (message[3] << 8) | (message[2] & 0xFF);
        if (message.length != payloadlenght + 9) {
            // Message has wrong length
            return false;
        }

        payloadType = (message[5] << 8) & 0xFF00 + (message[4] & 0xFF);

        // Get the payload
        final byte expected_checksum = message[message.length - 3];
        byte xorChecksum = 0;
        for (int i = 2; i < message.length - 3; i++) {
            xorChecksum ^= message[i];
        }
        if (xorChecksum != expected_checksum) {
            return false;
        }

        payload = new byte[payloadlenght];
        for (int i = 0; i < payloadlenght; i++) {
            payload[i] = message[i + 6];
        }
        return true;
    }
}
