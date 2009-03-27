/*
 * Created on 14 nov. 2007
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package bt747.sys;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class Settings {
    public final static byte DATE_YMD = 1;
    public final static byte DATE_DMY = 2;

    /**
     * @return the appSettings
     */
    public static final String getAppSettings() {
        return Interface.tr.getAppSettings();
    }

    /**
     * @param appSettings
     *                the appSettings to set
     */
    public static final void setAppSettings(final String appSettings) {
        Interface.tr.setAppSettings(appSettings);
    }
}
