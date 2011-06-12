/**
 * 
 */
package gps.mvc;

import gps.HoluxConstants;

/**
 * Controller for Skytraq Devices.
 * 
 * Currently references MtkController, but MtkController should be further
 * refactored to become Mtk independent.
 * 
 * @author Robert Tomanek <bt747.free.fr@mail.robert.tomanek.org>
 */
public class SkytraqController extends MtkController {

    /**
     * @param m
     */
    public SkytraqController(final GpsController c, final MtkModel m) {
        super(c, m);
    }

    /**
     * Handles commands with no parameters Support for Holux-specific
     * commands, otherwise delegates to standard MTK
     */
    public boolean cmd(final int cmd) {
        switch (cmd) {
//        case MtkController.CMD_ERASE_LOG:
//            sendCmd(HoluxConstants.PHLX_LOG_ERASE_REQUEST, false);
//            break;
        default:
            //return super.cmd(cmd);
            return false;
        }

        //return true;
    }

    /**
     * Handles commands with a parameter Support for Holux-specific commands,
     * otherwise delegates to standard MTK
     */
    public boolean cmd(final int cmd, final CmdParam param) {
        switch (cmd) {
//        case MtkController.CMD_SET_DEVICE_NAME:
//            setHoluxName(param.getString());
//            break;
//        case CMD_SET_LOG_DISTANCE_INTERVAL:
//            setLogDistanceInterval(param.getInt());
//            break;
//        case CMD_SET_LOG_TIME_INTERVAL:
//            setLogTimeInterval(param.getInt());
//            break;
        default:
            return false;
            //return super.cmd(cmd, param);
        }
        //return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.MtkController#isSupportedCmd(int)
     */
    public boolean isSupportedCmd(int cmd) {
        return false;
        //return super.isSupportedCmd(cmd);
    }
}
