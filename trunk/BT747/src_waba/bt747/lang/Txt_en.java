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
package bt747.lang;

import bt747.Version;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public final class Txt_en implements TxtInterface {
    private static final String fontFile=null;
    private static final String encoding=null;
    
    // BT747 class
    private static final String S_FILE = "File";
    private static final String S_EXIT_APPLICATION = "Exit application";
    
    private static final String S_SETTINGS = "Settings";
    private static final String S_STOP_LOGGING_ON_CONNECT = "Stop log on connect";
    private static final String S_STOP_CONNECTION = "Stop connection";
    private static final String S_GPX_UTC_OFFSET_0= "GPX UTC offset 0";
    private static final String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg when small";
    private static final String S_GPS_DECODE_ACTIVE= "GPS Decode active";
    private static final String S_FOCUS_HIGHLIGHT= "Focus Highlight";
    private static final String S_DEBUG= "Debug";
    private static final String S_DEBUG_CONN= "Debug conn.";
    private static final String S_STATS= "Stats";
    private static final String S_INFO= "Info";
    private static final String S_IMPERIAL= "Imperial Units";
    private static final String S_ABOUT_BT747= "About BT747";
    private static final String S_ABOUT_SUPERWABA= "About SuperWaba VM";
    
    private static final String S_TITLE= "BT747 - MTK Logger Control";

    
    private static final String LB_DOWNLOAD= "Download";
    
    private static final String TITLE_ATTENTION = "Attention";
    private static final String CONFIRM_APP_EXIT = "You are about to exit the application|"
                                                  + "Confirm application exit?";
    
    private static final String YES=
        "Yes";
    private static final String NO=
        "No";
    private static final String CANCEL=
        "Cancel";


    private static final String ABOUT_TITLE=
        "About BT747 V"+Version.VERSION_NUMBER;
    private static final String ABOUT_TXT=
        "Created with SuperWaba"
        + "|http://www.superwaba.org"
        + "|" +Version.BUILD_STR
        + "|Written by Mario De Weerd"
        + "|m.deweerd@ieee.org"
        + "|"
        + "|This application allows control of"
        + "|the BT747 device."
        + "|Full control using bluetooth is enabled"
        + "|by applying a hardware hack.  "
        + "|You can find information on the web."; 

    
    private static final String ABOUT_SUPERWABA_TITLE=
        "About SuperWaba";
    private static final String ABOUT_SUPERWABA_TXT=
        "SuperWaba Virtual Machine ";

    private static final String DISCLAIMER_TITLE=
        "Disclaimer";
    private static final String DISCLAIMER_TXT=
        "Software is provided 'AS IS,' without"
        + "|a warranty of any kind. ALL EXPRESS"
        + "|OR IMPLIED REPRESENTATIONS AND "
        + "|WARRANTIES, INCLUDING ANY IMPLIED"
        + "|WARRANTY OF MERCHANTABILITY,"
        + "|FITNESS FOR A PARTICULAR PURPOSE"
        + "|OR NON-INFRINGEMENT, ARE HEREBY"
        + "|EXCLUDED. THE ENTIRE RISK ARISING "
        + "|OUT OF USING THE SOFTWARE IS"
        + "|ASSUMED BY THE USER. See the"
        + "|GNU General Public License for more"
        + "|details." ;

    // TAB identification
    private static final String C_FMT  = "FMT";
    private static final String C_CTRL = "Ctrl";
    private static final String C_LOG  = "Log";
    private static final String C_FILE = "File";
    private static final String C_FLTR = "Fltr";
    private static final String C_EASY = "Easy";
    private static final String C_CON  = "Con";
    private static final String C_OTHR = "Othr";

    // Conctrl strings
    private static final String BT_BLUETOOTH = "BLUETOOTH";
    private static final String BT_CONNECT_PRT = "Connect Port Nbr";
    private static final String BT_CLOSE_PRT = "Close port";
    private static final String BT_REOPEN_PRT  = "(Re)open port";
    private static final String MAIN = "Main:";
    private static final String FIRMWARE = "Firmware:";
    private static final String MODEL = "Model:";
    private static final String FLASHINFO = "FlashInfo:";
    private static final String TIME_SEP = "  - Time:";
    private static final String LAT = "Lat:";
    private static final String LON = "Lon:";
    private static final String GEOID = "Geoid:";
    private static final String CALC = "(calc:";
    private static final String HGHT_SEP = " - Hght:";
    private static final String METERS_ABBR = "m";

    
    // Filters tab panel
    private static final String STANDARD = "Standard";
    private static final String ADVANCED = "Advanced";

    // BT747_dev class
    private static final String[]C_STR_RCR = {
            "Time", "Speed", "Distance", "Button",
            "Picture", "Gas Stat", "Phone", "ATM",
            "Bus stop", "Parking", "Post Box", "Railway",
            "Rstaurnt", "Bridge", "View", "Other"
            };
    private static final String [] logFmtItems = {
        "UTC",      // = 0x00001    // 0
        "VALID",    // = 0x00002    // 1
        "LATITUDE", // = 0x00004    // 2
        "LONGITUDE",// = 0x00008    // 3
        "HEIGHT",   // = 0x00010    // 4
        "SPEED",    // = 0x00020    // 5
        "HEADING",  // = 0x00040    // 6
        "DSTA",     // = 0x00080    // 7
        "DAGE",     // = 0x00100    // 8
        "PDOP",     // = 0x00200    // 9
        "HDOP",     // = 0x00400    // A
        "VDOP",     // = 0x00800    // B
        "NSAT",     // = 0x01000    // C
        "SID",      // = 0x02000    // D
        "ELEVATION",// = 0x04000    // E
        "AZIMUTH",  // = 0x08000    // F
        "SNR",      // = 0x10000    // 10
        "RCR",      // = 0x20000    // 11
        "MILISECOND",// = 0x40000   // 12
        "DISTANCE",  // = 0x80000    // 13
        "VALID PTS ONLY" // =0x80000000
    };
    private static final String C_BAD_LOG_FORMAT = "Bad log format";
    

    // Holux specific
    private static final String HOLUX_NAME = "Holux Name";

    private static final String SET = "SET";

    // EASY TAB
    private static final String BT_5HZ_FIX = "5Hz fix + log";
    private static final String BT_2HZ_FIX = "2Hz fix";
    private static final String BT_HOT = "Hot start";
    private static final String BT_WARM = "Warm start";
    private static final String BT_COLD = "Cold start";
    private static final String BT_FACT_RESET = "Factory reset";
    private static final String BT_FORCED_ERASE = "Forced erase";
    private static final String BT_PT_WITH_REASON = "Click to log a point with reason:";

    private static final String CONFIRM_FACT_RESET =
            "You are about to perform a factory|"
            + "reset of your GPS Logger Device.|"
            + "||Do you confirm this reset at|"
            + "your own risk ???";    
    
    // File tab
    private static final String OUTPUT_DIR = "Output dir:";
    private static final String LOGFILE = "LogFile:";
    private static final String REPORT = "Report :";
    private static final String CHUNK = "Chunk :";
    private static final String CHUNK_AHEAD = "Chunk ahead request:";
    private static final String READ_TIMEOUT = "Read timeout (ms) :";
    private static final String CARD_VOL = "Card/Volume:";
    private static final String APPLY_SET = "Apply&Set the above values";
    private static final String DEFAULT_SET = "Default settings";

    // Log filter
    private static String[] STR_VALID= {
            "No fix",
            "SPS",
            "DGPS",
            "PPS",
            "RTK",
            "FRTK",
            "Estimate",
            "Manual",
            "Sim"};

    private static final String TRKPT = "TrkPt";
    private static final String WAYPT = "WayPt";
    
    // Advanced log filter
    private static final String ACTIVE = "ACTIVE";
    private static final String INACTIVE = "INACTIVE";
    private static final String FLTR_REC = "<= record Nbr <= ";
    private static final String FLTR_SPD = "<= speed <= ";
    private static final String FLTR_DST = "<= distance <= ";
    private static final String FLTR_PDOP = "PDOP <= ";
    private static final String FLTR_HDOP = "HDOP <= ";
    private static final String FLTR_VDOP = "VDOP <= ";
    private static final String FLTR_NSAT = "<= NSAT";
    
    private static final String CLEAR = "CLEAR";
    
    // Log format
    private static final String REC_ESTIMATED = " records estimated";
    private static final String SET_ERASE = "Set & erase";
    private static final String SET_NOERASE = "Set (no erase)";
    private static final String ERASE = "Erase";
    private static final String CONFIRM_ERASE = "Confirm erase";
    
    private static final String C_msgWarningFormatIncompatibilityRisk =
        "You will change the format of your device without "
        + "erasing the log.|"
        + "Other software might not understand "
        + "the data in your device!||"
        + "Do you agree to this incompatibility?";
    
    /** Message warning user about impact of changing log format */
    private static final String C_msgWarningFormatAndErase = 
        "You are about to change the"
        + "|logging format of your device."
        + "|and"
        + "|ERASE the log"
        + "|"
        + "|LOG FORMAT CHANGE & ERASE?";
    /** Message warning the user again about the impact of a log format change */           
    private static final String C_msgWarningFormatAndErase2 =
        "This is your last chance to avoid"
        + "|erasing your device."
        + "|"
        + "|LOG FORMAT CHANGE & ERASE?";
    /** Message warning user about impact of changing log format */
    private static final String C_msgEraseWarning = 
        "You are about to"
        + "|erase your device."
        + "|"
        + "|LOG ERASE?";
    private static final String C_msgEraseWarning2 =
        "This is your last chance to avoid"
        + "|erasing your device."
        + "|"
        + "|LOG ERASE?";

    private static final String ONE_FILE = "One file";
    private static final String ONE_FILE_DAY = "One file/ day";
    private static final String ONE_FILE_TRK = "One file/ trk";
    private static final String DEV_LOGONOFF = "Device log on(/off)";
    private static final String INCREMENTAL = "Incremental";

    private static final String LOG_OVRWR_FULL = "Log overwrite(/stop) when full";
    private static final String DATE_RANGE = "Date range";
    private static final String GET_LOG = "Get Log";
    private static final String CANCEL_GET = "Cancel";
    private static final String NOFIX_COL = "No Fix Color";
    private static final String TRK_SEP = "Trk sep:";
    private static final String MIN = "min";
    private static final String UTC = "UTC";
    private static final String HGHT_GEOID_DIFF = "hght - geoid diff";
    private static final String TO_CSV = "To CSV";
    private static final String TO_GPX = "To GPX";
    private static final String TO_KML = "To KML";
    private static final String TO_TRK = "To TRK";
    private static final String TO_PLT = "To PLT";
    private static final String TO_GMAP= "To GMAP";
    private static final String TO_NMEA= "To NMEA";
    private static final String MEM_USED = "Mem Used   : ";
    private static final String NBR_RECORDS = "Nbr records: ";
    
    
    //Log reason
    private static final String NO_DGPS = "No DGPS";
    private static final String RTCM = "RTCM";
    private static final String WAAS = "WAAS";
    private static final String RCR_TIME = "Time (s)    ";
    private static final String RCR_SPD  = "Speed (km/h)";
    private static final String RCR_DIST = "Distance (m)";
    private static final String FIX_PER = "Fix (ms)";
    private static final String INCL_TST_SBAS = "Incl. Test SBAS";
    private static final String PWR_SAVE_INTRNL = "Power Save (Internal)";

    // NMEA OUTPUT
    private static final String DEFAULTS = "Defaults";
    
    
    // Other tabs
    private static final String TAB_FLSH = "Flsh";
    private static final String TAB_NMEA_OUT = "NMEA Output";
    private static final String TAB_NMEA_FILE = "NMEA File";
    private static final String TAB_HOLUX = "Holux";
    
    
    // Log convert
    private static final String ERROR = "Error";
    private static final String PROBLEM_READING = "Problem reading|";
    private static final String COULD_NOT_OPEN = "Could not open|";
    
    
    // GPS State
    private static final String CANCEL_WAITING = "Cancel waiting";
    private static final String TITLE_WAITING_ERASE =
        "Waiting until erase done";
    private static final String TXT_WAITING_ERASE =
        "Wait until the erase is done.|"
        + "You can cancel waiting (at your risk)";
    
    private static final String UNKNOWN = "Unknown";
    private static final String CHK_PATH =
        "|Check path & if Card is writeable";
    
    private static final String OVERWRITE = "Overwrite";
    private static final String ABORT_DOWNLOAD = "Abort download";
    private static final String DATA_NOT_SAME = 
    "The DATA in the device does NOT|"
    + "correspond to the DATA previously|"
    + "downloaded.|"
    + "Do you wish to overwrite the DATA?";
    private static final String LOGGER = "Logger: ";  // For logger SW version
    
    
    // GPSFile
    private static final String CLOSE_FAILED =
        "Closing failed (closeFile) - probably a bug.";
    private static final String WRITING_CLOSED =
        "Writing to closed file";
        
    // Flash option
    private static final String TIMESLEFT = "TimesLeft";
    private static final String UPDATERATE = "Update Rate (Hz)";
    private static final String BAUDRATE = "Baud Rate";
    private static final String WRITEFLASH = "Write Flash";
    private static final String ABORT = "Abort";
    private static final String TXT_FLASH_LIMITED_WRITES=
        "The number of writes to the flash|"
        + "is limited and a change in settings|"
        + "could render your device inoperable|"
        + "(e.g., a baud rate change)|"
        + "ABORT by clicking abort!!";
    private static final String PERIOD_ABBREV = "Per";
    
    // Forgotten in Advanced track filter
    private static final String IGNORE_0VALUES
        = "Values of 0 are ignored";
    
    private static final String STORE_SETTINGS =
        "Store settings";
    private static final String RESTORE_SETTINGS =
        "Restore settings";
    private static final String WARNING =
        "Warning";
    private static final String NO_FILES_WERE_CREATED =
        "No output files were created!"
        + "||"
        + "This usually means that either:"
        + "|- The filter did not select any points"
        + "|- The log does not contain any data"
        + "|"
        + "|Try selecting all points."
        + "|If that does not work,"
        + "| it may be a bug.";
    private static final String ADD_RECORD_NUMBER =
        "Record nbr info in logs";
    
    private static final String BAD_SUPERWABAVERSION =
        "This application was built for|SuperWaba V";
    private static final String BAD_SUPERWABAVERSION_CONT =
        ".|This version is V";
    private static final String BAD_SUPERWABAVERSION_CONT2 =
        ".|Exiting application";

    private static final String S_DEVICE = "Device";
    private static final String S_DEFAULTDEVICE = "Default Device";
    private static final String S_GISTEQTYPE1 = "iTrackU-Nemerix";
    private static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr";
    private static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII";
    
    private static final String BT_MAC_ADDR = "BT Mac Addr:";

    private static final String S_OUTPUT_LOGCONDITIONS = "Output log conditions";

    private static final String MEM_FREE = "Free";
    
    private static final String TRKPTCOMMENT = "TRK PT INFO";
    private static final String TRKPTNAME = "TRK PT NAME";

    private static final String DOWNLOAD_INCREMENTAL = "Smart dwnld";
    private static final String DOWNLOAD_FULL = "Full dwnld";
    private static final String DOWNLOAD_NORMAL = "Normal dwnld";
    
    private final static String LANG_DE= "Deutsch";
    private final static String LANG_EN= "English";
    private final static String LANG_ES= "Spanish";
    private final static String LANG_FR= "Français";
    private final static String LANG_IT= "Italiano";
    private final static String LANG_JP= "Japanese";
    private final static String LANG_KO= "Korean";
    private final static String LANG_NL= "Nederlands";
    private final static String LANG_ZH= "Chinese";
    private final static String MI_LANGUAGE= "Lang";

    /* (non-Javadoc)
     * @see bt747.lang.NexTxtInterface#getRcrString(int)
     */
    public final String getRcrString(final int i) {
        return C_STR_RCR[i];
    }

    /* (non-Javadoc)
     * @see bt747.lang.NexTxtInterface#getValidString(int)
     */
    public final String getValidString(final int i) {
        return STR_VALID[i];
    }

    /* (non-Javadoc)
     * @see bt747.lang.NexTxtInterface#getLogFmtItem(int)
     */
    public final String getLogFmtItem(final int i) {
        return logFmtItems[i];
    }
    
    /* (non-Javadoc)
     * @see bt747.lang.NexTxtInterface#getTranslation(int)
     */
    public final String getTranslation(final int i) {
        if(i<translation.length) {
            return translation[i];
        } else {
            return null;
        }
    }
    
    private static final String[] translation = { fontFile, encoding, S_FILE,
            S_EXIT_APPLICATION, S_SETTINGS, S_STOP_LOGGING_ON_CONNECT,
            S_STOP_CONNECTION, S_GPX_UTC_OFFSET_0, S_GPX_TRKSEG_WHEN_SMALL,
            S_GPS_DECODE_ACTIVE, S_FOCUS_HIGHLIGHT, S_DEBUG, S_DEBUG_CONN,
            S_STATS, S_INFO, S_IMPERIAL, S_ABOUT_BT747, S_ABOUT_SUPERWABA,
            S_TITLE, LB_DOWNLOAD, TITLE_ATTENTION, CONFIRM_APP_EXIT, YES, NO,
            CANCEL, ABOUT_TITLE, ABOUT_TXT, ABOUT_SUPERWABA_TITLE,
            ABOUT_SUPERWABA_TXT, DISCLAIMER_TITLE,
            DISCLAIMER_TXT, C_FMT, C_CTRL, C_LOG, C_FILE, C_FLTR, C_EASY,
            C_CON, C_OTHR, BT_BLUETOOTH, BT_CONNECT_PRT, BT_CLOSE_PRT,
            BT_REOPEN_PRT, MAIN, FIRMWARE, MODEL, FLASHINFO, TIME_SEP, LAT,
            LON, GEOID, CALC, HGHT_SEP, METERS_ABBR, STANDARD, ADVANCED,
            C_BAD_LOG_FORMAT, HOLUX_NAME, SET, BT_5HZ_FIX, BT_2HZ_FIX, BT_HOT,
            BT_WARM, BT_COLD, BT_FACT_RESET, BT_FORCED_ERASE,
            BT_PT_WITH_REASON, CONFIRM_FACT_RESET, OUTPUT_DIR, LOGFILE, REPORT,
            CHUNK, CHUNK_AHEAD, READ_TIMEOUT, CARD_VOL, APPLY_SET, DEFAULT_SET,
            TRKPT, WAYPT, ACTIVE, INACTIVE, FLTR_REC, FLTR_SPD, FLTR_DST,
            FLTR_PDOP, FLTR_HDOP, FLTR_VDOP, FLTR_NSAT, CLEAR, REC_ESTIMATED,
            SET_ERASE, SET_NOERASE, ERASE, CONFIRM_ERASE,
            C_msgWarningFormatIncompatibilityRisk, C_msgWarningFormatAndErase,
            C_msgWarningFormatAndErase2, C_msgEraseWarning, C_msgEraseWarning2,
            ONE_FILE, ONE_FILE_DAY, ONE_FILE_TRK, DEV_LOGONOFF, INCREMENTAL,
            LOG_OVRWR_FULL, DATE_RANGE, GET_LOG, CANCEL_GET, NOFIX_COL,
            TRK_SEP, MIN, UTC, HGHT_GEOID_DIFF, TO_CSV, TO_GPX, TO_KML, TO_TRK,
            TO_PLT, TO_GMAP, TO_NMEA, MEM_USED, NBR_RECORDS, NO_DGPS, RTCM,
            WAAS, RCR_TIME, RCR_SPD, RCR_DIST, FIX_PER, INCL_TST_SBAS,
            PWR_SAVE_INTRNL, DEFAULTS, TAB_FLSH, TAB_NMEA_OUT, TAB_NMEA_FILE,
            TAB_HOLUX, ERROR, PROBLEM_READING, COULD_NOT_OPEN, CANCEL_WAITING,
            TITLE_WAITING_ERASE, TXT_WAITING_ERASE, UNKNOWN, CHK_PATH,
            OVERWRITE, ABORT_DOWNLOAD, DATA_NOT_SAME, LOGGER, CLOSE_FAILED,
            WRITING_CLOSED, TIMESLEFT, UPDATERATE, BAUDRATE, WRITEFLASH, ABORT,
            TXT_FLASH_LIMITED_WRITES, PERIOD_ABBREV, IGNORE_0VALUES,
            STORE_SETTINGS, RESTORE_SETTINGS, WARNING, NO_FILES_WERE_CREATED,
            ADD_RECORD_NUMBER, BAD_SUPERWABAVERSION, BAD_SUPERWABAVERSION_CONT,
            BAD_SUPERWABAVERSION_CONT2, S_DEVICE, S_DEFAULTDEVICE,
            S_GISTEQTYPE1, S_GISTEQTYPE2, S_GISTEQTYPE3, BT_MAC_ADDR,
            S_OUTPUT_LOGCONDITIONS, MEM_FREE, TRKPTCOMMENT, TRKPTNAME,
            DOWNLOAD_INCREMENTAL, DOWNLOAD_FULL, DOWNLOAD_NORMAL, LANG_DE,
            LANG_EN, LANG_ES, LANG_FR, LANG_IT, LANG_JP, LANG_KO, LANG_NL,
            LANG_ZH, MI_LANGUAGE,

    };

}


