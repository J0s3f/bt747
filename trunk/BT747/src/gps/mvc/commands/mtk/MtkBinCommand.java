/**
 * 
 */
package gps.mvc.commands.mtk;

import gps.connection.GPSrxtx;
import gps.connection.MtkBinWriter;
import gps.mvc.GPSLinkHandler;
import gps.mvc.commands.GpsLinkExecCommand;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

/**
 * @author Mario
 * 
 */
public class MtkBinCommand implements GpsLinkExecCommand {
    private final MtkBinTransportMessageModel msg;

    /**
     * 
     */
    public MtkBinCommand(final MtkBinTransportMessageModel msg) {
        this.msg = msg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSLinkHandler)
     */
    public final void execute(final GPSLinkHandler context) {
        final GPSrxtx gpsRxTx = context.getGPSRxtx();
        MtkBinWriter.sendCmd(gpsRxTx, msg);
    }
}
