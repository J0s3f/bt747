/*
 * Created on 14 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Settings {
    public static boolean hasWaba= true;
    public static final String platform = waba.sys.Settings.platform;
    public static final int requiredVersion = 583;
    public static final String requiredVersionStr = "5.83";
    public static final int version = waba.sys.Settings.version;
    public static final String versionStr = waba.sys.Settings.versionStr;
    public static final boolean onDevice = waba.sys.Settings.onDevice;
    public static final byte DATE_YMD=waba.sys.Settings.DATE_YMD;
    public static final byte DATE_DMY=waba.sys.Settings.DATE_DMY;

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
    
    public static boolean isWaba() {
        return hasWaba;
    }
}
