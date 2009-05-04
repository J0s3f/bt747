/**
 * 
 */
package gps.mvc;

import gps.BT747Constants;
import gps.mvc.commands.mtk.MtkBinCommand;
import gps.mvc.commands.mtk.SetMtkBinModeCommand;
import gps.mvc.commands.mtk.SetNmeaModeCommand;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

/**
 * Controller for MTK based device (Transystem Type logger currently
 * included).
 * 
 * Refactoring ongoing.
 * 
 * @author Mario De Weerd
 * 
 */
public class MtkController {
    private MtkModel m;

    MtkController(final MtkModel m) {
        this.m = m;
    }

    public final void sendMtkBin(final MtkBinTransportMessageModel msg) {
        m.getHandler().sendCmd(new MtkBinCommand(msg));
    }

    /**
     * Sets the device to binary mode.
     */
    public final void setBinMode() {
        m.getHandler().sendCmd(new SetMtkBinModeCommand());
    }

    /**
     * Set the device to Nmea mode.
     */
    public final void setNmeaMode() {
        m.getHandler().sendCmd(new SetNmeaModeCommand());
    }

    /**
     * Delegates NMEA sending to handler.
     * 
     * @param cmd
     */
    private final void sendNMEA(final String cmd) {
        m.getHandler().sendCmd(cmd);
    }
}
