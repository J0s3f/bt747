package bt747;

import waba.sys.Settings;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public final class Txt {
    
    // BT747 class
    public final static String S_FILE = "File";
    public final static String S_EXIT_APPLICATION = "Exit application";
    
    public final static String S_SETTINGS = "Settings";
    public final static String S_RESTART_CONNECTION = "Restart connection";
    public final static String S_STOP_CONNECTION = "Stop connection";
    public final static String S_GPX_UTC_OFFSET_0= "GPX UTC offset 0";
    public final static String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg when small";
    public final static String S_GPS_DECODE_ACTIVE= "GPS Decode active";
    public final static String S_FOCUS_HIGHLIGHT= "Focus Highlight";
    public final static String S_DEBUG= "Debug";
    public final static String S_STATS= "Stats";
    public final static String S_INFO= "Info";
    public final static String S_ABOUT_BT747= "About BT747";
    public final static String S_ABOUT_SUPERWABA= "About SuperWaba VM";
    
    public final static String S_TITLE= "BT747 - MTK Logger Control";

    
    public final static String LB_DOWNLOAD= "Download";
    
    public final static String TITLE_ATTENTION = "Attention";
    public final static String CONFIRM_APP_EXIT = "You are about to exit the application|" + 
                                                  "Confirm application exit?";
    
    public final static String YES=
        "Yes";
    public final static String NO=
        "No";
    public final static String CANCEL=
        "Cancel";


    public final static String ABOUT_TITLE=
        "About BT747 V"+Version.VERSION_NUMBER;
    public final static String ABOUT_TXT=
        "Created with SuperWaba" + 
        "|http://www.superwaba.org"+ 
        "|" +Version.BUILD_STR + 
        "|Written by Mario De Weerd" + 
        "|m.deweerd@ieee.org"+ 
        "|"+ 
        "|This application allows control of" + 
        "|the BT747 device." + 
        "|Full control using bluetooth is enabled" + 
        "|by applying a hardware hack.  " + 
        "|You can find information on the web."; 

    
    public final static String ABOUT_SUPERWABA_TITLE=
        "About SuperWaba";
    public final static String ABOUT_SUPERWABA_TXT=
        "SuperWaba Virtual Machine "+ Settings.versionStr + 
        "|Copyright (c)2000-2007" + 
        "|Guilherme Campos Hazan" + 
        "|www.superwaba.com|" + 
        "|" + 
        "SuperWaba is an enhanced version" + 
        "|of the Waba Virtual Machine" + 
        "|Copyright (c) 1998,1999 WabaSoft" + 
        "|www.wabasoft.com";
    
    public final static String DISCLAIMER_TITLE=
        "Disclaimer";
    public final static String DISCLAIMER_TXT=
        "Software is provided 'AS IS,' without" + 
        "|a warranty of any kind. ALL EXPRESS" + 
        "|OR IMPLIED REPRESENTATIONS AND " + 
        "|WARRANTIES, INCLUDING ANY IMPLIED" + 
        "|WARRANTY OF MERCHANTABILITY," + 
        "|FITNESS FOR A PARTICULAR PURPOSE" + 
        "|OR NON-INFRINGEMENT, ARE HEREBY" + 
        "|EXCLUDED. THE ENTIRE RISK ARISING " + 
        "|OUT OF USING THE SOFTWARE IS" + 
        "|ASSUMED BY THE USER. See the" + 
        "|GNU General Public License for more" + 
        "|details." ;

}
