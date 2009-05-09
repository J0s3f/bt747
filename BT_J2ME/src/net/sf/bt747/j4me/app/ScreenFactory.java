/**
 * 
 */
package net.sf.bt747.j4me.app;

import bt747.sys.Generic;

import net.sf.bt747.j4me.app.log.LogScreen;
import net.sf.bt747.j4me.app.screens.BT747Dialog;

/**
 * @author Mario
 * 
 */
public final class ScreenFactory {

    public static final int LOGDOWNLOADSCREEN = 1;
    public static final int LOGGERSTATUSSCREEN = 2;
    public static final int LOGFIELDSELECTSCREEN = 3;
    public static final int LOGDOWNLOADCONFIGSCREEN = 4;
    public static final int GPSPOSITIONSCREEN = 5;
    public static final int FILEFIELDSELECTSCREEN = 6;
    public static final int CONVERTTOSCREEN = 7;
    public static final int LOGCONDITIONSCONFIGSCREEN = 8;
    public static final int AGPSSCREEN = 9;

    public static final BT747Dialog getScreen(final int type) {
        switch (type) {
        case LOGDOWNLOADSCREEN:
            return new LogDownloadConfigScreen();
        case LOGGERSTATUSSCREEN:
            return new LoggerStatusScreen();
        case LOGFIELDSELECTSCREEN:
            return new LogFieldSelectScreen();
        case LOGDOWNLOADCONFIGSCREEN:
            return new LogDownloadConfigScreen();
        case GPSPOSITIONSCREEN:
            return new GpsPositionScreen();
        case FILEFIELDSELECTSCREEN:
            return new FileFieldSelectScreen();
        case CONVERTTOSCREEN:
            return new ConvertToScreen();
        case LOGCONDITIONSCONFIGSCREEN:
            return new LogConditionsConfigScreen();
        case AGPSSCREEN:
            return new AgpsScreen();
        default:
            Generic.debug("Invalid screen number " + type);
        // TODO : select rather the message log.
            return new LoggerStatusScreen();
        }

    }
}
