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

    public final static void sendPacket(final GPSrxtx context,
            final String p_Packet) {
        if (context.isConnected()) {
            final StringBuffer rec = new StringBuffer(256);
            // Calculate checksum
            int z_Index = p_Packet.length();
            byte z_Checksum = 0;
            while (--z_Index >= 0) {
                z_Checksum ^= (byte) p_Packet.charAt(z_Index);
            }
            try {
                rec.setLength(0);
                rec.append('$');
                rec.append(p_Packet);
                rec.append('*');
                rec.append(JavaLibBridge.unsigned2hex(z_Checksum, 2));
                rec.append(NMEAWriter.EOL_BYTES);
                context.write(rec.toString());
            } catch (final Exception e) {
                Generic.debug("sendPacket", e);
            }
        } else {
            Generic.debug("Not connected: skipped " + p_Packet);
        }
    }
}
