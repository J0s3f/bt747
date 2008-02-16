package bt747;

import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public class Txt_fr {
    public static void init() {
    }

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
        "Ce logiciel est fourni par les titu-" +
        "|laires et les participants" +
        "|\"TEL QUEL\" ET TOUTES LES GARANTIES" +
        "|EXPLICITES OU IMPLICITES, Y COMPRIS," +
        "|MAIS NON LIMITÉES A CELLES-CI, LES" +
        "|GARANTIES IMPLICITES DE COMMERCIA-" +
        "|LISATION ET D\'ADÉQUATION À UN USAGE"+
        "|PARTICULIER SONT DÉNIÉES. EN AUCUN" +
        "|CAS LES TITULAIRES DU COPYRIGHT OU" +
        "|LES PARTICIPANTS NE PEUVENT ÊTRE" +
        "|TENUS POUR RESPONSABLE DES DOMMAGES" +
        "|DIRECTS, INDIRECTS, FORTUITS," +
        "|PARTICULIERS, EXEMPLAIRES OU" +
        "|CONSÉCUTIFS À UNE ACTION (Y COMPRIS," +
        "|MAIS NON LIMITÉS À CEUX-CI," +
        "|L'ACQUISITION DE MARCHANDISES OU" +
        "|DE SERVICES DE REMPLACEMENT; LES" +
        "|PERTES D'UTILISATIONS, DE DONNÉES" +
        "OU FINANCIÈRES; OU L'INTERRUPTION" +
        "D'ACTIVITÉS) DE QUELQUE MANIÈRE QUE" +
        "|CES DOMMAGES SOIENT CAUSÉS ET CECI" +
        "|POUR TOUTES LES THÉORIES DE" +
        "|RESPONSABILITÉS, QUE CE SOIT DANS" +
        "|UN CONTRAT, POUR DES RESPONSABILITÉS" +
        "| STRICTES OU DES PRÉJUDICES (Y" +
        "|COMPRIS DUS À UNE NÉGLIGENCE OU" +
        "|AUTRE CHOSE) SURVENANT DE QUELQUE" +
        "|MANIÈRE QUE CE SOIT EN DEHORS DE" +
        "|L'UTILISATION DE CE LOGICIEL, MÊME" +
        "|EN CAS D\'AVERTISSEMENT DE LA" +
        "|POSSIBILITÉ DE TELS DOMMAGES.";

    // TAB identification
    public final static String C_FMT  = "FMT";
    public final static String C_CTRL = "Ctrl";
    public final static String C_LOG  = "Log";
    public final static String C_FILE = "File";
    public final static String C_FLTR = "Fltr";
    public final static String C_EASY = "Easy";
    public final static String C_CON  = "Con";
    public final static String C_OTHR = "Othr";

    // Conctrl strings
    public final static String BT_BLUETOOTH = "BLUETOOTH";
    public final static String BT_CONNECT_PRT = "Connect Port Nbr";
    public final static String BT_CLOSE_PRT = "Close port";
    public final static String BT_REOPEN_PRT  = "(Re)open port";
    public final static String MAIN = "Main:";
    public final static String FIRMWARE = "Firmware:";
    public final static String MODEL = "Model:";
    public final static String FLASHINFO = "FlashInfo:";
    public final static String TIME_SEP = "  - Time:";
    public final static String LAT = "Lat:";
    public final static String LON = "Lon:";
    public final static String GEOID = "Geoid:";
    public final static String CALC = "(calc:";
    public final static String HGHT_SEP = " - Hght:";
    
    // Filters tab panel
    public final static String STANDARD = "Standard";
    public final static String ADVANCED = "Advanced";

    
    // BT747_dev class
    public static final String[]C_STR_RCR = {
            "Time", "Speed", "Distance", "Button",
            "App1","App2","App3","App4",
            "App5","App6","App7","App8",
            "App9","AppX","AppY","AppZ"
            };
    public static final String [] logFmtItems = {
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
        "HOLUX M-241" // =0x80000000
    };
    public static final String C_BAD_LOG_FORMAT = "Bad log format";
    

    // Holux specific
    public static final String HOLUX_NAME = "Holux Name";

    public static final String SET = "SET";

    // EASY TAB
    public static final String BT_5HZ_FIX = "5Hz fix and log";
    public static final String BT_2HZ_FIX = "2Hz fix (avoid static nav)";
    public static final String BT_HOT = "Hot start";
    public static final String BT_WARM = "Warm start";
    public static final String BT_COLD = "Cold start";
    public static final String BT_FACT_RESET = "Factory reset";
    public static final String BT_FORCED_ERASE = "Forced erase";
    public static final String BT_PT_WITH_REASON = "Click to log a point with reason:";

    public static final String CONFIRM_FACT_RESET =
            "You are about to perform a factory|" +
            "reset of your GPS Logger Device.|"+
            "||Do you confirm this reset at|"+
            "your own risk ???";    
    
    // File tab
    public static final String OUTPUT_DIR = "Output dir:";
    public static final String LOGFILE = "LogFile:";
    public static final String REPORT = "Report :";
    public static final String CHUNK = "Chunk :";
    public static final String CHUNK_AHEAD = "Chunk ahead request:";
    public static final String READ_TIMEOUT = "Read timeout (ms) :";
    public static final String CARD_VOL = "Card/Volume:";
    public static final String APPLY_SET = "Apply&Set the above values";
    public static final String DEFAULT_SET = "Default settings";

    // Log filter
    public static final String[] STR_VALID= {
            "No fix",
            "SPS",
            "DGPS",
            "PPS",
            "RTK",
            "FRTK",
            "Estimate",
            "Manual",
            "Sim"};

    public static final String TRKPT = "TrkPt";
    public static final String WAYPT = "WayPt";
    
    // Advanced log filter
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String FLTR_REC = "<= record Nbr <= ";
    public static final String FLTR_SPD = "<= speed <= ";
    public static final String FLTR_DST = "<= distance <= ";
    public static final String FLTR_PDOP = "PDOP <= ";
    public static final String FLTR_HDOP = "HDOP <= ";
    public static final String FLTR_VDOP = "VDOP <= ";
    public static final String FLTR_NSAT = "<= NSAT";
    
    public static final String CLEAR = "CLEAR";
    
    // Log format
    public static final String REC_ESTIMATED = " records estimated";
    public static final String SET_ERASE = "Set & erase";
    public static final String SET_NOERASE = "Set (no erase)";
    public static final String ERASE = "Erase";
    public static final String CONFIRM_ERASE = "Confirm erase";
    
    public static final String C_msgWarningFormatIncompatibilityRisk =
        "You will change the format of your device without " +
        "erasing the log.|" +
        "Other software might not understand " +
        "the data in your device!||" +
        "Do you agree to this incompatibility?";
    
    /** Message warning user about impact of changing log format */
    public static final String C_msgWarningFormatAndErase = 
        "You are about to change the" +
        "|logging format of your device." +
        "|and" +
        "|ERASE the log" +
        "|" +
        "|LOG FORMAT CHANGE & ERASE?";
    /** Message warning the user again about the impact of a log format change */           
    public static final String C_msgWarningFormatAndErase2 =
        "This is your last chance to avoid" +
        "|erasing your device." +
        "|" +
        "|LOG FORMAT CHANGE & ERASE?";
    /** Message warning user about impact of changing log format */
    public static final String C_msgEraseWarning = 
        "You are about to" +
        "|erase your device." +
        "|" +
        "|LOG ERASE?";
    public static final String C_msgEraseWarning2 =
        "This is your last chance to avoid" +
        "|erasing your device." +
        "|" +
        "|LOG ERASE?";

    public static final String ONE_FILE = "One file";
    public static final String ONE_FILE_DAY = "One file/ day";
    public static final String ONE_FILE_TRK = "One file/ trk";
    public static final String DEV_LOGONOFF = "Device log on(/off)";
    public static final String INCREMENTAL = "Incremental";
    
    public static final String LOG_OVRWR_FULL = "Log overwrite(/stop) when full";
    public static final String DATE_RANGE = "Date range";
    public static final String GET_LOG = "Get Log";
    public static final String CANCEL_GET = "Cancel";
    public static final String NOFIX_COL = "No Fix Color";
    public static final String TRK_SEP = "Trk sep:";
    public static final String MIN = "min";
    public static final String UTC = "UTC";
    public static final String HGHT_GEOID_DIFF = "hght - geiod diff";
    public static final String TO_CSV = "To CSV";
    public static final String TO_GPX = "To GPX";
    public static final String TO_KML = "To KML";
    public static final String TO_TRK = "To TRK";
    public static final String TO_PLT = "To PLT";
    public static final String TO_GMAP= "To GMAP";
    public static final String TO_NMEA= "To NMEÄ";
    public static final String MEM_USED = "Mem Used   : ";
    public static final String NBR_RECORDS = "Nbr records: ";
    
    
    //Log reason
    public static final String NO_DGPS = "No DGPS";
    public static final String RTCM = "RTCM";
    public static final String WAAS = "WAAS";
    public static final String RCR_TIME = "Time (s)    ";
    public static final String RCR_SPD  = "Speed (km/h)";
    public static final String RCR_DIST = "Distance (m)";
    public static final String FIX_PER = "Fix (ms)";
    public static final String INCL_TST_SBAS = "Incl. Test SBAS";
    public static final String PWR_SAVE_INTRNL = "Power Save (Internal)";

    // NMEA OUTPUT
    public static final String DEFAULTS = "Defaults";
    
    
    // Other tabs
    public static final String TAB_FLSH = "Flsh";
    public static final String TAB_NMEA_OUT = "NMEA Output";
    public static final String TAB_NMEA_FILE = "NMEA File";
    public static final String TAB_HOLUX = "Holux";
    
    
    // Log convert
    public static final String ERROR = "Error";
    public static final String PROBLEM_READING = "Problem reading|";
    public static final String COULD_NOT_OPEN = "Could not open|";
    
    
    // GPS State
    public static final String CANCEL_WAITING = "Cancel waiting";
    public static final String TITLE_WAITING_ERASE =
        "Waiting until erase done";
    public static final String TXT_WAITING_ERASE =
        "Wait until the erase is done.|" +
        "You can cancel waiting (at your risk)";
    
    public static final String UNKNOWN = "Unknown";
    public static final String CHK_PATH =
        "|Check path & if Card is writeable";
    
    public static final String OVERWRITE = "Overwrite";
    public static final String ABORT_DOWNLOAD = "Abort download";
    public static final String DATA_NOT_SAME = 
    "The DATA in the device does NOT|"
    + "correspond to the DATA previously|"
    + "downloaded.|"
    + "Do you wish to overwrite the DATA?";
    public static final String LOGGER = "Logger: ";  // For logger SW version
    
    
    // GPSFile
    public static final String CLOSE_FAILED =
        "Closing failed (closeFile) - probably a bug.";
    public static final String WRITING_CLOSED =
        "Writing to closed file";
        
    // Flash option
    public static final String TIMESLEFT = "TimesLeft";
    public static final String UPDATERATE = "Update Rate (Hz)";
    public static final String BAUDRATE = "Baud Rate";
    public static final String WRITEFLASH = "Write Flash";
    public static final String ABORT = "Abort";
    public static final String TXT_FLASH_LIMITED_WRITES=
        "The number of writes to the flash|" +
        "is limited and a change in settings|" +
        "could render your device inoperable|" +
        "(e.g., a baud rate change)|" +
        "ABORT by clicking abort!!";
    public static final String PERIOD_ABBREV = "Per";
    
    // Forgotton in Advanced track filter
    public static final String IGNORE_0VALUES
        = "Values of 0 are ignored";
    
}


