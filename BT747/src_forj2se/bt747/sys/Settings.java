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
    public static boolean hasWaba= false;
    public static final String platform = java.lang.System.getProperty("os.name");
    public static final String versionStr = java.lang.System.getProperty("os.version");
    public static final boolean onDevice = false;
    public static final byte DATE_YMD=1;
    public static final byte DATE_DMY=2;

    private static String appSettings=""; // TODO: Implement other solution
    /**
     * @return the appSettings
     */
    public static final String getAppSettings() {
        return Settings.appSettings;
    }
    /**
     * @param appSettings the appSettings to set
     */
    public static final void setAppSettings(String appSettings) {
        Settings.appSettings= appSettings;
    }
    
    public static boolean isWaba() {
        return hasWaba;
    }
}
