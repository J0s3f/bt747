/**
 * 
 */
package gps.mvc;

import gps.connection.DPL700ResponseModel;
import gps.mvc.commands.dpl700.DPL700IntCommand;
import gps.mvc.commands.dpl700.DPL700StrCommand;

import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Exception;

/**
 * @author Mario
 *
 */
public class DPL700LogDownloadHandler implements DeviceOperationHandlerIF {
    // DPL700 Functionality
    private String DPL700LogFileName;
    private int DPL700Card;
    private static final int C_DPL700_OFF = 0;
    private static final int C_DPL700_NEEDGETLOG = 1;
    private static final int C_DPL700_GETLOG = 2;
    private int DPL700_State = C_DPL700_OFF;

    private final LogFile logFile = new LogFile();
    private final GPSLinkHandler handler;
    
    /* (non-Javadoc)
     * @see gps.mvc.DeviceOperationHandlerIF#analyseResponse(java.lang.Object)
     */
    public boolean analyseResponse(Object o) {
        if (o instanceof DPL700ResponseModel) {
            analyseDPL700Data((DPL700ResponseModel) o);
            return true;
        }
        return false;
    }


    /* (non-Javadoc)
     * @see gps.mvc.DeviceOperationHandlerIF#notifyRun(gps.mvc.GPSLinkHandler)
     */
    public boolean notifyRun(GPSLinkHandler handler) throws BT747Exception {
        // TODO Auto-generated method stub
        return false;
    }
    
    //private final MTKLogDownloadHandler mtkLogHandler; // Temporary (TODO: refactor).
    /**
     * 
     */
    public DPL700LogDownloadHandler(GPSLinkHandler handler) {
        this.handler = handler;
    }
    
    
    public final void getDPL700Log(final String p_FileName, final int card) {
        DPL700LogFileName = p_FileName;
        DPL700Card = card;
        enterDPL700Mode();
        DPL700_State = C_DPL700_NEEDGETLOG;
    }

    public final void reqDPL700Log() {
        DPL700_State = C_DPL700_GETLOG;
        handler.sendCmd(new DPL700IntCommand(0x60B50000, 10 * 1024 * 1024));
        // m_GPSrxtx.virtualReceive("sample dataWP Update Over\0");
    }

    public final void enterDPL700Mode() {
        exitDPL700Mode(); // Exit previous session if still open
        handler.sendCmd(new DPL700StrCommand("W'P Camera Detect", 255));
        // m_GPSrxtx.virtualReceive("WP GPS+BT\0");
    }

    public final void exitDPL700Mode() {
        handler.sendCmd(new DPL700StrCommand("WP AP-Exit",0)); // No reply expected
        DPL700_State = C_DPL700_OFF;
    }

    public final void reqDPL700LogSize() {
        handler.sendCmd(new DPL700IntCommand(0x60B50000, 255));
    }

    public final void reqDPL700Erase() {
        handler.sendCmd(new DPL700IntCommand(0x60B50000, 255));
    }

    public final void reqDPL700DeviceInfo() {
        handler.sendCmd(new DPL700IntCommand(0x5BB00000, 255));
    }

    public final void getDPL700GetSettings() {
        handler.sendCmd(new DPL700IntCommand(0x62B60000, 255));
    }

   protected final void analyseDPL700Data(final DPL700ResponseModel resp) {
        final String s = resp.getResponseType(); 
        if (Generic.isDebug()) {
            Generic.debug("<DPL700 " + s);
        }
        if (s.startsWith("WP GPS")) {
            // WP GPS+BT
            // Response to W'P detect
            if (DPL700_State == C_DPL700_NEEDGETLOG) {
                reqDPL700Log();
            }
        } else if (s.startsWith("WP Update Over")) {
            if (DPL700_State == C_DPL700_GETLOG) {
                if (DPL700LogFileName != null) {
                    if (!DPL700LogFileName.endsWith(".sr")) {
                        DPL700LogFileName += ".sr";
                    }
                    logFile.openNewLog(DPL700LogFileName, DPL700Card);
                    try {
                        logFile.getLogFile().writeBytes(
                                resp.getResponseBuffer(), 0,
                                resp.getResponseSize());
                        logFile.getLogFile().close();
                    } catch (final Exception e) {
                        Generic.debug("", e);
                        // TODO: handle exception
                    }
                    DPL700LogFileName = null;
                    exitDPL700Mode();
                }
            }
        }
    }
}
