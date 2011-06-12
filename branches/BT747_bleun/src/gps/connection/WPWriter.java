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
public final class WPWriter {

    public final static void sendCmd(final GPSrxtx context, final int cmd,
            final int rcvBufferSize) {
        sendCmd(context, cmd, 0, rcvBufferSize);
    }

    /**
     * Send command and argument. Argument has not been seen different from
     * '0' but added for future provision.
     * 
     * @param context
     * @param cmd
     * @param arg
     * @param rcvBufferSize
     */
    public final static void sendCmd(final GPSrxtx context, final int cmd,
            final int arg, final int rcvBufferSize) {
        if (context.isConnected()) {
            final byte[] sendbuffer = new byte[7];
            try {
                WPDecoderState.setNewBufferSize(rcvBufferSize);
                context.newState(DecoderStateFactory.WP_STATE);
                if (Generic.isDebug()) {
                    Generic.debug(">0x" + JavaLibBridge.unsigned2hex(cmd, 8)
                            + "000000");
                }
                sendbuffer[0] = (byte) ((cmd >> 24) & 0xFF);
                sendbuffer[1] = (byte) ((cmd >> 16) & 0xFF);
                sendbuffer[2] = (byte) ((cmd >> 8) & 0xFF);
                sendbuffer[3] = (byte) ((cmd >> 0) & 0xFF);
                sendbuffer[4] = (byte) ((arg >> 8) & 0xFF);
                sendbuffer[5] = (byte) ((arg >> 0) & 0xFF);
                sendbuffer[6] = 0;
                context.write(sendbuffer);
            } catch (final Exception e) {
                Generic.debug("sendAndGetWP", e);
            }
        }
    }

    public final static void sendCmd(final GPSrxtx context, final String cmd,
            final int rcvBufferSize) {
        if (context.isConnected()) {
            final StringBuffer rec = new StringBuffer(256);
            try {
                WPDecoderState.setNewBufferSize(rcvBufferSize);
                context.newState(DecoderStateFactory.WP_STATE);
                if (Generic.isDebug()) {
                    Generic.debug(">" + cmd);
                }
                rec.setLength(0);
                rec.append(cmd);
                rec.append("\0");
                context.write(rec.toString());
            } catch (final Exception e) {
                Generic.debug("send and get resp", e);
            }
        }
    }

    public final static void sendCmd(final GPSrxtx context, final String cmd) {
        if (context.isConnected()) {
            final StringBuffer rec = new StringBuffer(256);
            try {
                WPDecoderState.setNewBufferSize(0);
                context.newState(DecoderStateFactory.WP_STATE);
                if (Generic.isDebug()) {
                    Generic.debug(">" + cmd);
                }
                rec.setLength(0);
                rec.append(cmd);
                rec.append("\0");
                context.write(rec.toString());
            } catch (final Exception e) {
                Generic.debug("sendWPCmd", e);
            }
        }
    }

}
