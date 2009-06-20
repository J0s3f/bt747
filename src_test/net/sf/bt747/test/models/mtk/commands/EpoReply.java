/**
 * 
 */
package net.sf.bt747.test.models.mtk.commands;

import gps.connection.GPSrxtx;
import gps.connection.MtkBinWriter;
import gps.mvc.commands.GpsRxtxExecCommand;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

/**
 * Represents an acknowledge package to send.
 * 
 * @author Mario
 * 
 */
public class EpoReply implements GpsRxtxExecCommand {

    private byte[] payload = new byte[4];

    public EpoReply(final MtkBinTransportMessageModel msg) {
        final byte[] msgPayload = msg.getPayLoad();
        payload[0] = msgPayload[0];
        payload[1] = msgPayload[1];
        payload[2] = (byte) 1;
        // final int main = Integer.parseInt(nmea[0].substring(4));
        // this.status = status;
        // confirmMain = main;
        // if (main != 182) {
        // confirmSecond = -1;
        // } else {
        // confirmSecond = Integer.parseInt(nmea[1]);
        // }
    }

    public final int getPacketNbr() {
        return (payload[0] & 0xFF) + ((payload[1] & 0xFF) << 8);
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.connection.GPSrxtx)
     */
    public void execute(final GPSrxtx context) {
        MtkBinWriter.sendCmd(context, new MtkBinTransportMessageModel(2,
                payload));
    }

}
