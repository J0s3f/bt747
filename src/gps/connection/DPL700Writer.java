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
public final class DPL700Writer {

    public final static void sendCmd(final GPSrxtx context, final int cmd,
            final int rcvBufferSize) {
        if (context.isConnected()) {
            final byte[] sendbuffer = new byte[7];
            try {
                DPL700DecoderState.setNewBufferSize(rcvBufferSize);
                context.newState(DecoderStateFactory.DPL700_STATE);
                if (Generic.isDebug()) {
                    Generic.debug(">0x" + JavaLibBridge.unsigned2hex(cmd, 8)
                            + "000000");
                }
                sendbuffer[0] = (byte) ((cmd >> 24) & 0xFF);
                sendbuffer[1] = (byte) ((cmd >> 16) & 0xFF);
                sendbuffer[2] = (byte) ((cmd >> 8) & 0xFF);
                sendbuffer[3] = (byte) ((cmd >> 0) & 0xFF);
                sendbuffer[4] = 0;
                sendbuffer[5] = 0;
                sendbuffer[6] = 0;
                context.write(sendbuffer);
            } catch (final Exception e) {
                Generic.debug("sendAndGetDPL700", e);
            }
        }
    }

    public final static void sendCmd(final GPSrxtx context, final String cmd,
            final int rcvBufferSize) {
        if (context.isConnected()) {
            final StringBuffer rec = new StringBuffer(256);
            try {
                DPL700DecoderState.setNewBufferSize(rcvBufferSize);
                context.newState(DecoderStateFactory.DPL700_STATE);
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
                DPL700DecoderState.setNewBufferSize(0);
                context.newState(DecoderStateFactory.DPL700_STATE);
                if (Generic.isDebug()) {
                    Generic.debug(">" + cmd);
                }
                rec.setLength(0);
                rec.append(cmd);
                rec.append("\0");
                context.write(rec.toString());
            } catch (final Exception e) {
                Generic.debug("sendDPL700Cmd", e);
            }
        }
    }

}
