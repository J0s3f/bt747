/**
 * 
 */
package gps.connection;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * @author Mario
 * 
 */
public final class NMEAWriter {
    private static final char[] EOL_BYTES = { '\015', '\012' };

    /**
     * Adds $ to start of string and checksum (including star) before writing.
     * 
     * @param context
     * @param packetNoCheckSum
     */
    public final static void sendPacket(final GPSrxtx context,
            final String packetNoCheckSum) {
        final boolean isConnected = context.isConnected();
        if (Generic.isDebug()) {
            Generic.debug(">" + packetNoCheckSum + " " + isConnected);
        }
        if (isConnected) {
            final StringBuffer rec = new StringBuffer(256);
            // Calculate checksum
            int z_Index = packetNoCheckSum.length();
            byte z_Checksum = 0;
            while (--z_Index >= 0) {
                z_Checksum ^= (byte) packetNoCheckSum.charAt(z_Index);
            }
            try {
                rec.setLength(0);
                rec.append('$');
                rec.append(packetNoCheckSum);
                rec.append('*');
                rec.append(JavaLibBridge.unsigned2hex(z_Checksum, 2));
                rec.append(NMEAWriter.EOL_BYTES);
                final String s = rec.toString();
                context.write(s);

            } catch (final Exception e) {
                Generic.debug("sendPacket", e);
            }
        } else {
            Generic.debug("Not connected: skipped " + packetNoCheckSum);
        }
    }
}
