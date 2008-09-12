//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  This layer was written for the SuperWaba toolset.           ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************
package net.sf.bt747.waba.system;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Settings {
    public static boolean hasWaba= true;
    public static String platform = waba.sys.Settings.platform;
    public static int requiredVersion = 583;
    public static String requiredVersionStr = "5.83";
    public static int version = waba.sys.Settings.version;
    public static String versionStr = waba.sys.Settings.versionStr;
    public static boolean onDevice = waba.sys.Settings.onDevice;
    public static byte DATE_YMD=waba.sys.Settings.DATE_YMD;
    public static byte DATE_DMY=waba.sys.Settings.DATE_DMY;

    /**
     * @return the appSettings
     */
    public static String getAppSettings() {
        return waba.sys.Settings.appSettings;
    }
    /**
     * @param appSettings the appSettings to set
     */
    public static void setAppSettings(String appSettings) {
        waba.sys.Settings.appSettings= appSettings;
    }
    
    public static boolean isWaba() {
        return hasWaba;
    }
}
