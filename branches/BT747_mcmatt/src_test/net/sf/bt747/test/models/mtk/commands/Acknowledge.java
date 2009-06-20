/**
 * 
 */
package net.sf.bt747.test.models.mtk.commands;

import gps.BT747Constants;
import gps.connection.GPSrxtx;
import gps.connection.MtkBinDecoderState;
import gps.connection.MtkBinWriter;
import gps.connection.NMEAWriter;
import gps.mvc.commands.GpsRxtxExecCommand;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

/**
 * Represents an acknowledge package to send.
 * 
 * @author Mario
 * 
 */
public class Acknowledge implements GpsRxtxExecCommand {

    private int confirmMain;
    private int confirmSecond;
    private int status;

    public Acknowledge(final String[] nmea, final int status) {
        final int main = Integer.parseInt(nmea[0].substring(4));
        this.status = status;
        confirmMain = main;
        if (main != 182) {
            confirmSecond = -1;
        } else {
            confirmSecond = Integer.parseInt(nmea[1]);
        }
    }

    public Acknowledge(final String[] nmea) {
        this(nmea, BT747Constants.PMTK_ACK_SUCCEEDED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.connection.GPSrxtx)
     */
    public void execute(final GPSrxtx context) {
        if (context.getState() instanceof MtkBinDecoderState) {
            byte[] payload;
            if (confirmSecond == -1) {
                payload = new byte[2 + 1];
                payload[0] = (byte) (confirmMain & 0xFF);
                payload[1] = (byte) ((confirmMain >> 8) & 0xFF);
                payload[2] = (byte) (status);
            } else {
                payload = new byte[2 + 2 + 1];
                payload = new byte[2 + 1];
                payload[0] = (byte) (confirmMain & 0xFF);
                payload[1] = (byte) ((confirmMain >> 8) & 0xFF);
                payload[2] = (byte) (confirmSecond & 0xFF);
                payload[3] = (byte) ((confirmSecond >> 8) & 0xFF);
                payload[4] = (byte) (status);
            }
            MtkBinWriter.sendCmd(context, new MtkBinTransportMessageModel(1,
                    payload));
        } else {
            String nmeaReply;
            if (confirmSecond == -1) {
                nmeaReply = String.format("PMTK001,%03d,%d", confirmMain,
                        status);
            } else {
                nmeaReply = String.format("PMTK001,%03d,%03d,%d",
                        confirmMain, confirmSecond, status);
            }
            NMEAWriter.sendPacket(context, nmeaReply);
        }
    }

}
