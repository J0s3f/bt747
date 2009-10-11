/**
 * 
 */
package gps.mvc.commands.mtk;

import gps.connection.GPSrxtx;
import gps.connection.MtkBinWriter;
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

    /* (non-Javadoc)
     * @see gps.mvc.commands.GpsLinkExecCommand#execute(gps.mvc.GPSrxtx)
     */
    public final void execute(final GPSrxtx context) {
        MtkBinWriter.sendCmd(context, msg);
    }
}
