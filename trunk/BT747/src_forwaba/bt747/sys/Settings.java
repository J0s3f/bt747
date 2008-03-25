/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

import gps.port.GPSPort;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class Settings {
    public static final boolean hasWaba= true;
    public static final String platform = waba.sys.Settings.platform;
    public static final String versionStr = waba.sys.Settings.versionStr;
    public static final boolean onDevice = waba.sys.Settings.onDevice;
    /**
     * @return the appSettings
     */
    public static final String getAppSettings() {
        return waba.sys.Settings.appSettings;
    }
    /**
     * @param appSettings the appSettings to set
     */
    public static final void setAppSettings(String appSettings) {
        waba.sys.Settings.appSettings= appSettings;
    }
    
}
