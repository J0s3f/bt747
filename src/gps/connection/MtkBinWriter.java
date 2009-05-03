/**
 * 
 */
package gps.connection;

import gps.BT747Constants;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

/**
 * Writes binary MTK packages.
 * 
 * @author Mario
 * 
 */
public final class MtkBinWriter {
    public final static void sendCmd(final GPSrxtx context,
            final MtkBinTransportMessageModel msg) {
        if (context.isConnected()) {
            context.write(msg.getMessage());
        }
    }

    /**
     * Sets the mtk nmea mode.
     * 
     * TODO: check that the current state is indeed BIN mode.
     * 
     * @param context
     * @param baudrate
     */
    public final static void doSetNmeaMode(final GPSrxtx context,
            final int baudrate) {
        if (context.getState() instanceof MtkBinDecoderState) {
            final byte[] payload = new byte[4];
            payload[0] = 0;
            payload[1] = 0;
            payload[2] = (byte) (baudrate >> 8);
            payload[3] = (byte) (baudrate & 0xFF);
            sendCmd(context, new MtkBinTransportMessageModel(
                    BT747Constants.PMTK_SET_BIN_MODE, payload));
        }
    }

    /**
     * Sets the mtk bin mode.
     * 
     * @param context
     * @param baudrate
     */
    public final static void setMtkBinMode(final GPSrxtx context,
            final int baudrate) {
        if (context.getState() instanceof NMEADecoderState) {
            context.newState(DecoderStateFactory.MTKBIN_STATE);
            NMEAWriter.sendPacket(context, "PMTK"
                    + BT747Constants.PMTK_SET_BIN_MODE_STR + ",1" + ","
                    + baudrate);
        }
    }
}
