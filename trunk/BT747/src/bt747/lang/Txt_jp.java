package bt747.lang;

import bt747.Version;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public class Txt_jp {
    public static String fontFile="UFFJap";
    public static String encoding="UTF8";
    
    // BT747 class
    // It is possible to write the language tokens directly and then convert
    // to UTF format using http://code.cside.com/3rdpage/us/javaUnicode/converter.html 
    // BT747 class
    public static final String S_FILE = "\u30D5\u30A1\u30A4\u30EB"; 
    public static final String S_EXIT_APPLICATION = "\u7D42\u4E86"; 
    
    public static final String S_SETTINGS = "\u8A2D\u5B9A"; 
    public static final String S_RESTART_CONNECTION = "\u518D\u63A5\u7D9A"; 
    public static final String S_STOP_CONNECTION = "\u63A5\u7D9A\u7D42\u4E86"; 
    public static final String S_GPX_UTC_OFFSET_0= "GPX UTC 0\u88DC\u6B63"; 
    public static final String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg\u8A2D\u5B9A\u5024\u3092\u6709\u52B9\u306B\u3059\u308B"; 
    public static final String S_GPS_DECODE_ACTIVE= "GPS Decode\u3092\u6709\u52B9\u306B\u3059\u308B"; 
    public static final String S_FOCUS_HIGHLIGHT= "\u9078\u629E\u90E8\u5206\u3092\u5F37\u8ABF\u3059\u308B"; 
    public static final String S_DEBUG= "\u30C7\u30D0\u30C3\u30AF"; 
    public static final String S_STATS= "\u7D71\u8A08"; 
    public static final String S_INFO= "\u30A4\u30F3\u30D5\u30A9\u30E1\u30FC\u30B7\u30E7\u30F3"; 
    public static final String S_ABOUT_BT747= "BT747 \u306B\u3064\u3044\u3066"; 
    public static final String S_ABOUT_SUPERWABA= "SuperWaba VM \u306B\u3064\u3044\u3066"; 
    
    public static final String S_TITLE= "BT747 - MTK \u30ED\u30AC\u30FC\u30B3\u30F3\u30C8\u30ED\u30FC\u30EB"; 

    
    public static final String LB_DOWNLOAD= "\u30C0\u30A6\u30F3\u30ED\u30FC\u30C9"; 
     
    public static final String TITLE_ATTENTION = "\u6CE8\u610F"; 
    public static final String CONFIRM_APP_EXIT = "\u30A2\u30D7\u30EA\u30B1\u30FC\u30B7\u30E7\u30F3\u3092\u7D42\u4E86\u3057\u307E\u3059|"
                                                  + "\u3088\u308D\u3057\u3044\u3067\u3059\u304B\uFF1F"; 
    
    public static final String YES=
        "\u306F\u3044"; 
    public static final String NO=
        "\u3044\u3044\u3048"; 
    public static final String CANCEL=
        "\u30AD\u30E3\u30F3\u30BB\u30EB"; 
 

    public static final String ABOUT_TITLE=
        "About BT747 V"+Version.VERSION_NUMBER;
    public static final String ABOUT_TXT=
        "Created with SuperWaba"
        + "|http://www.superwaba.org"
        + "|" +Version.BUILD_STR
        + "|Written by Mario De Weerd"
        + "|m.deweerd@ieee.org"
        + "|"
        + "|Japanese Translation done by Shingo Kato"
        + "|iwashifish@gmail.com"
        + "|"
        + "|This application allows control of"
        + "|the BT747 device."
        + "|Full control using bluetooth is enabled"
        + "|by applying a hardware hack.  "
        + "|You can find information on the web."; 

    
    public static final String ABOUT_SUPERWABA_TITLE=
        "About SuperWaba";
    public static final String ABOUT_SUPERWABA_TXT=
        "SuperWaba Virtual Machine ";
    public static final String ABOUT_SUPERWABA_TXT_CONTINUE=
          "|Copyright (c)2000-2007"
        + "|Guilherme Campos Hazan"
        + "|www.superwaba.com|"
        + "|"
        + "SuperWaba is an enhanced version"
        + "|of the Waba Virtual Machine"
        + "|Copyright (c) 1998,1999 WabaSoft"
        + "|www.wabasoft.com";
    
    public static final String DISCLAIMER_TITLE=
        "Disclaimer";
    public static final String DISCLAIMER_TXT=
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
    public static final String C_FMT  = "FMT";
    public static final String C_CTRL = "Ctrl";
    public static final String C_LOG  = "Log";
    public static final String C_FILE = "File";
    public static final String C_FLTR = "Fltr";
    public static final String C_EASY = "Easy";
    public static final String C_CON  = "Con";
    public static final String C_OTHR = "Othr";

    // Conctrl strings
    public static final String BT_BLUETOOTH = "BLUETOOTH";
    public static final String BT_CONNECT_PRT = "\u63A5\u7D9A\u3059\u308B\u30DD\u30FC\u30C8\u756A\u53F7"; 
    public static final String BT_CLOSE_PRT = "\u30DD\u30FC\u30C8\u3092\u9589\u3058\u308B"; 
    public static final String BT_REOPEN_PRT  = "\u30DD\u30FC\u30C8\u3092\u958B\u304F(\u518D\u958B)"; 
    public static final String MAIN = "\u30E1\u30A4\u30F3:"; 
    public static final String FIRMWARE = "\u30D5\u30A1\u30FC\u30E0\u30A6\u30A7\u30A2:"; 
    public static final String MODEL = "\u578B\u5F0F:"; 
    public static final String FLASHINFO = "\u30E1\u30E2\u30EA\u60C5\u5831:"; 
    public static final String TIME_SEP = "  - \u6642\u9593:"; 
    public static final String LAT = "\u7DEF\u5EA6:"; 
    public static final String LON = "\u7D4C\u5EA6:"; 
    public static final String GEOID = "\u30B8\u30AA\u30A4\u30C9:"; 
    public static final String CALC = "(calc:"; 
    public static final String HGHT_SEP = " - \u6A19\u9AD8:"; 
    public static final String METERS_ABBR = "m"; 

    
    // Filters tab panel
    public static final String STANDARD = "\u6A19\u6E96"; 
    public static final String ADVANCED = "\u4E0A\u7D1A"; 

    
    // BT747_dev class
    public static final String[]C_STR_RCR = {
            "\u6642\u9593", "\u901F\u5EA6", "\u8DDD\u96E2", "\u30DC\u30BF\u30F3",
            "App1","App2","App3","App4",
            "App5","App6","App7","App8",
            "App9","AppX","AppY","AppZ"
            };
    public static final String [] logFmtItems = {
        "\u6642\u9593",      // = 0x00001    // 0
        "\u6E2C\u4F4D",    // = 0x00002    // 1
        "\u7DEF\u5EA6",  // = 0x00004    // 2
        "\u7D4C\u5EA6", // = 0x00008    // 3
        "\u6A19\u9AD8",   // = 0x00010    // 4
        "\u901F\u5EA6",    // = 0x00020    // 5
        "\u65B9\u4F4D",  // = 0x00040    // 6
        "DGPS \u30B9\u30C6\u30FC\u30B7\u30E7\u30F3\u306EID \u756A\u53F7",     // = 0x00080    // 7
        "DGPS \u60C5\u5831\u66F4\u65B0\u304B\u3089\u306E\u7D4C\u904E\u6642\u9593",     // = 0x00100    // 8
        "\u4F4D\u7F6E\u7CBE\u5EA6\uFF083D\uFF09",     // = 0x00200    // 9
        "\u4F4D\u7F6E\u7CBE\u5EA6\uFF082D\uFF09",        // = 0x00400    // A
        "\u6A19\u9AD8\u7CBE\u5EA6",     // = 0x00800    // B
        "\u885B\u661F\u6570",     // = 0x01000    // C
        "\u885B\u661FID",      // = 0x02000    // D
        "\u885B\u661F\u306E\u4EF0\u89D2", // = 0x04000    // E
        "\u885B\u661F\u306E\u65B9\u4F4D",  // = 0x08000    // F
        "\u4FE1\u53F7\u5BFE\u96D1\u97F3\u6BD4",      // = 0x10000    // 10
        "\u8A18\u9332\u6761\u4EF6",      // = 0x20000    // 11
        "\u30DF\u30EA\u79D2", // = 0x40000   // 12
        "\u8DDD\u96E2",  // = 0x80000    // 13
        "VALID POINTS ONLY" // =0x80000000
    };
    public static final String C_BAD_LOG_FORMAT = "\u30ED\u30B0\u30D5\u30A9\u30FC\u30DE\u30C3\u30C8\u304C\u6B63\u3057\u304F\u3042\u308A\u307E\u305B\u3093"; 
    

    // Holux specific
    public static final String HOLUX_NAME = "Holux\u306E\u540D\u79F0"; 
    public static final String SET = "\u8A2D\u5B9A"; 

    // EASY TAB
    public static final String BT_5HZ_FIX = "5Hz\u6E2C\u4F4D + \u30ED\u30B0"; 
    public static final String BT_2HZ_FIX = "2Hz\u6E2C\u4F4D"; 
    public static final String BT_HOT = "\u30DB\u30C3\u30C8\u30B9\u30BF\u30FC\u30C8"; 
    public static final String BT_WARM = "\u30A6\u30A9\u30FC\u30E0\u30B9\u30BF\u30FC\u30C8"; 
    public static final String BT_COLD = "\u30B3\u30FC\u30EB\u30C9\u30B9\u30BF\u30FC\u30C8"; 
    public static final String BT_FACT_RESET = "\u5DE5\u5834\u51FA\u8377\u6642\u8A2D\u5B9A\u306B\u30EA\u30BB\u30C3\u30C8"; 
    public static final String BT_FORCED_ERASE = "\u5F37\u5236\u6D88\u53BB"; 
    public static final String BT_PT_WITH_REASON = "\u30AF\u30EA\u30C3\u30AF\u3057\u3066\u30ED\u30B0\u30DD\u30A4\u30F3\u30C8\u3092\u8A18\u9332:"; 

    public static final String CONFIRM_FACT_RESET =
            "GPS\u30ED\u30AC\u30FC\u306E\u8A2D\u5B9A\u3092|"
            + "\u521D\u671F\u5024\u306B\u30EA\u30BB\u30C3\u30C8\u3057\u307E\u3059\u3002|"
            + "||\u3088\u308D\u3057\u3044\u3067\u3059\u304B\uFF1F|"
            + "\uFF08\u81EA\u5DF1\u8CAC\u4EFB\uFF09";    
    
    // File tab
    public static final String OUTPUT_DIR = "\u4FDD\u5B58\u3059\u308B\u5834\u6240:"; 
    public static final String LOGFILE = "\u30ED\u30B0\u30D5\u30A1\u30A4\u30EB\u540D:"; 
    public static final String REPORT = "\u30EC\u30DD\u30FC\u30C8 :"; 
    public static final String CHUNK = "\u30C1\u30E3\u30F3\u30AF :"; 
    public static final String CHUNK_AHEAD = "\u30C1\u30E3\u30F3\u30AF\u30EA\u30AF\u30A8\u30B9\u30C8:"; 
    public static final String READ_TIMEOUT = "\u30BF\u30A4\u30E0\u30A2\u30A6\u30C8 (\u30DF\u30EA\u79D2) :"; 
    public static final String CARD_VOL = "\u30AB\u30FC\u30C9/\u30DC\u30EA\u30E5\u30FC\u30E0:"; 
    public static final String APPLY_SET = "\u8A2D\u5B9A\u3057\u305F\u5024\u3092\u9069\u7528\u3059\u308B"; 
    public static final String DEFAULT_SET = "\u30C7\u30D5\u30A9\u30EB\u30C8\u5024\u306B\u623B\u3059"; 

    // Log filter
    public static final String[] STR_VALID= {
            "\u6E2C\u4F4D\u306A\u3057", 
            "SPS",
            "DGPS",
            "PPS",
            "RTK",
            "FRTK",
            "\u63A8\u5B9A", 
            "\u624B\u52D5", 
            "Sim"};

    public static final String TRKPT = "TrkPt";
    public static final String WAYPT = "WayPt";
    
    // Advanced log filter
    public static final String ACTIVE = "ACTIVE"; 
    public static final String INACTIVE = "INACTIVE";
    public static final String FLTR_REC = "<= \u30EC\u30B3\u30FC\u30C9\u6570 <= "; 
    public static final String FLTR_SPD = "<= \u901F\u5EA6 <= "; 
    public static final String FLTR_DST = "<= \u8DDD\u96E2 <= "; 
    public static final String FLTR_PDOP = "\u4F4D\u7F6E\u7CBE\u5EA6\uFF083D\uFF09 <= "; 
    public static final String FLTR_HDOP = "\u4F4D\u7F6E\u7CBE\u5EA6\uFF082D\uFF09 <= ";      
    public static final String FLTR_VDOP = "\u6A19\u9AD8\u7CBE\u5EA6 <= "; 
    public static final String FLTR_NSAT = "<= \u885B\u661F\u6570"; 
     
    public static final String CLEAR = "\u30AF\u30EA\u30A2"; 
    
    // Log format
    public static final String REC_ESTIMATED = " \u6700\u5927\u8A18\u9332\u30EC\u30B3\u30FC\u30C9\u6570\uFF08\u63A8\u5B9A\uFF09"; 
    public static final String SET_ERASE = "\u8A2D\u5B9A\u3057\u3066\u6D88\u53BB"; 
    public static final String SET_NOERASE = "\u8A2D\u5B9A\u306E\u307F"; 
    public static final String ERASE = "\u6D88\u53BB"; 
    public static final String CONFIRM_ERASE = "\u6D88\u53BB\u306E\u78BA\u8A8D"; 
    
    public static final String C_msgWarningFormatIncompatibilityRisk =
        "GPS\u30C7\u30D0\u30A4\u30B9\u306E\u30D5\u30A9\u30FC\u30DE\u30C3\u30C8\u3092\u5909\u66F4\u3057\u3088\u3046\u3068\u3057\u3066\u3044\u307E\u3059\u3002 "
        + "\u30ED\u30B0\u306F\u6D88\u53BB\u3055\u308C\u307E\u305B\u3093\u3002|"
        + "\u3053\u306E\u30BD\u30D5\u30C8\u30A6\u30A7\u30A2\u4EE5\u5916\u3067\u306F\u3001 "
        + "\u30C7\u30D0\u30A4\u30B9\u5185\u306E\u30ED\u30B0\u3092\u6B63\u5E38\u306B\u8AAD\u307F\u53D6\u308C\u306A\u304F\u306A\u308B\u5834\u5408\u304C\u3042\u308A\u307E\u3059\u3002||"
        + "\u305D\u308C\u3067\u3082\u3088\u308D\u3057\u3044\u3067\u3059\u304B\uFF1F"; 
    
    
    
    /** Message warning user about impact of changing log format */
    public static final String C_msgWarningFormatAndErase = 
        "GPS\u30C7\u30D0\u30A4\u30B9\u306E\u30D5\u30A9\u30FC\u30DE\u30C3\u30C8\u3092"
        + "|\u5909\u66F4\u3057\u3088\u3046\u3068\u3057\u3066\u3044\u307E\u3059\u3002"
        + "|\u30C7\u30D0\u30A4\u30B9\u5185\u306E\u30ED\u30B0\u306F\u6D88\u53BB\u3055\u308C\u307E\u3059\u3002"
        + "|"
        + "|\u30ED\u30B0\u30D5\u30A9\u30FC\u30DE\u30C3\u30C8\u3092\u5909\u66F4\u3057\u3001"
        + "|\u30ED\u30B0\u3092\u6D88\u53BB\u3057\u307E\u3059\u304B\uFF1F";
        
        
   
   /** Message warning the user again about the impact of a log format change */           
    public static final String C_msgWarningFormatAndErase2 =
        "\u6700\u7D42\u78BA\u8A8D\uFF1AGPS\u30C7\u30D0\u30A4\u30B9\u5185\u306E\u30ED\u30B0\u3092\u6D88\u53BB\u3057\u307E\u3059\u3002"
        + "|\u3088\u308D\u3057\u3044\u3067\u3059\u304B\uFF1F"
        + "|"
        + "|\u30ED\u30B0\u30D5\u30A9\u30FC\u30DE\u30C3\u30C8\u3092\u5909\u66F4\u3057\u3001\u30ED\u30B0\u3092\u6D88\u53BB\u3057\u307E\u3059\u304B\uFF1F";

         
         
 
 /** Message warning user about impact of changing log format */
    public static final String C_msgEraseWarning = 
        "GPS\u30C7\u30D0\u30A4\u30B9\u5185\u306E\u30ED\u30B0\u3092\u6D88\u53BB\u3057\u307E\u3059\u3002"
        + "|\u3088\u308D\u3057\u3044\u3067\u3059\u304B\uFF1F"
        + "|"
        + "|\u30ED\u30B0\u3092\u6D88\u53BB\u3057\u307E\u3059\u304B\uFF1F";
        
        
        
 
 public static final String C_msgEraseWarning2 =
        "\u6700\u7D42\u78BA\u8A8D\uFF1AGPS\u30C7\u30D0\u30A4\u30B9\u5185\u306E\u30ED\u30B0\u3092\u6D88\u53BB\u3057\u307E\u3059\u3002"
        + "|\u3088\u308D\u3057\u3044\u3067\u3059\u304B\uFF1F"
        + "|"
        + "|\u30ED\u30B0\u3092\u6D88\u53BB\u3057\u307E\u3059\u304B\uFF1F";
        
        
        
        
    public static final String ONE_FILE = "\uFF11\u30D5\u30A1\u30A4\u30EB"; 
    public static final String ONE_FILE_DAY = "\uFF11\u30D5\u30A1\u30A4\u30EB/\u65E5"; 
    public static final String ONE_FILE_TRK = "\uFF11\u30D5\u30A1\u30A4\u30EB/\u30C8\u30E9\u30C3\u30AF"; 
    public static final String DEV_LOGONOFF = "\u30C7\u30D0\u30A4\u30B9\u30ED\u30B0 ON/OFF"; 
    public static final String INCREMENTAL = "PC\u4E0A\u306E\u30C7\u30FC\u30BF\u3092\u4E0A\u66F8\u304D"; 
    
    public static final String LOG_OVRWR_FULL = "\u53E4\u3044\u30ED\u30B0\u3092\u4E0A\u66F8\u304D\u3059\u308B(/\u3084\u3081\u308B)"; 
    public static final String DATE_RANGE = "\u65E5\u4ED8"; 
    public static final String GET_LOG = "\u30ED\u30B0\u3092\u30C0\u30A6\u30F3\u30ED\u30FC\u30C9"; 
    public static final String CANCEL_GET = "\u30AD\u30E3\u30F3\u30BB\u30EB"; 
    public static final String NOFIX_COL = "\u6E2C\u4F4D\u306A\u3057\u306E\u8272\u8A2D\u5B9A"; 
    public static final String TRK_SEP = "\u30C8\u30E9\u30C3\u30AD\u30F3\u30B0\u8A2D\u5B9A\u6642\u9593:"; 
    public static final String MIN = "\u5206"; 
    public static final String UTC = "\u6642\u9593"; 
    public static final String HGHT_GEOID_DIFF = "\u6A19\u9AD8\u3068\u30B8\u30AA\u30A4\u30C9\u306E\u5DEE"; 
    public static final String TO_CSV = "CSV\u3078"; 
    public static final String TO_GPX = "GPX\u3078"; 
    public static final String TO_KML = "KML\u3078"; 
    public static final String TO_TRK = "TRK\u3078"; 
    public static final String TO_PLT = "PLT\u3078"; 
    public static final String TO_GMAP= "GMAP\u3078"; 
    public static final String TO_NMEA= "NMEA\u3078"; 
    public static final String MEM_USED = "\u4F7F\u7528\u30E1\u30E2\u30EA\u91CF   : "; 
    public static final String NBR_RECORDS = "\u30EC\u30B3\u30FC\u30C9\u6570: "; 
    
    
    //Log reason
    public static final String NO_DGPS = "DGPS\u306A\u3057"; 
    public static final String RTCM = "RTCM";
    public static final String WAAS = "WAAS";
    public static final String RCR_TIME = "\u6642\u9593 (\u79D2)    "; 
    public static final String RCR_SPD  = "\u6642\u901F (km/h)"; 
    public static final String RCR_DIST = "\u8DDD\u96E2 (m)"; 
    public static final String FIX_PER = "\u6E2C\u4F4D\u6642\u9593 (\u30DF\u30EA\u79D2)"; 
    public static final String INCL_TST_SBAS = "SBAS\u30C6\u30B9\u30C8\u3092\u542B\u3080"; 
    public static final String PWR_SAVE_INTRNL = "\u7701\u96FB\u529B\u30E2\u30FC\u30C9"; 

    // NMEA OUTPUT
    public static final String DEFAULTS = "\u30C7\u30D5\u30A9\u30EB\u30C8"; 
    
    
    // Other tabs
    public static final String TAB_FLSH = "\u30D5\u30E9\u30C3\u30B7\u30E5";
    public static final String TAB_NMEA_OUT = "NMEA\u51FA\u529B"; 
    public static final String TAB_NMEA_FILE = "NMEA\u30D5\u30A1\u30A4\u30EB"; 
    public static final String TAB_HOLUX = "Holux";
    
    
    // Log convert
    public static final String ERROR = "\u30A8\u30E9\u30FC"; 
    public static final String PROBLEM_READING = "\u554F\u984C\u304C\u767A\u751F\u3057\u307E\u3057\u305F|"; 
    public static final String COULD_NOT_OPEN = "\u30D5\u30A1\u30A4\u30EB\u3092\u958B\u3051\u307E\u305B\u3093|"; 
    
    
    // GPS State
    public static final String CANCEL_WAITING = "\u51E6\u7406\u3092\u30AD\u30E3\u30F3\u30BB\u30EB\u3059\u308B"; 
    public static final String TITLE_WAITING_ERASE =
        "\u51E6\u7406\u4E2D\u3067\u3059\u3002\u7D42\u308F\u308B\u307E\u3067\u3057\u3070\u3089\u304F\u304A\u5F85\u3061\u304F\u3060\u3055\u3044\u3002"; 
    public static final String TXT_WAITING_ERASE =
        "\u6D88\u53BB\u304C\u7D42\u308F\u308B\u307E\u3067\u3057\u3070\u3089\u304F\u304A\u5F85\u3061\u304F\u3060\u3055\u3044\u3002|"
        + "\u30AD\u30E3\u30F3\u30BB\u30EB\u3067\u304D\u307E\u3059\uFF08\u81EA\u5DF1\u8CAC\u4EFB\uFF09";
    
    
    
    public static final String UNKNOWN = "\u4E0D\u660E"; 
    public static final String CHK_PATH =
        "|\u30D1\u30B9\u3092\u78BA\u8A8D\u3057\u3066\u304F\u3060\u3055\u3044\u3002"; 
    
    public static final String OVERWRITE = "\u4E0A\u66F8\u304D"; 
    public static final String ABORT_DOWNLOAD = "\u30C0\u30A6\u30F3\u30ED\u30FC\u30C9\u3092\u4E2D\u6B62"; 
    public static final String DATA_NOT_SAME = 
    "\u30C7\u30D0\u30A4\u30B9\u5185\u306E\u30C7\u30FC\u30BF\u306F|"
    + "\u4EE5\u524D\u30C0\u30A6\u30F3\u30ED\u30FC\u30C9\u3057\u305F\u3082\u306E\u3068|"
    + "\u4E00\u81F4\u3057\u3066\u3044\u307E\u305B\u3093\u3002|"
    + "\u30C7\u30FC\u30BF\u3092\u4E0A\u66F8\u304D\u3057\u3066\u3082\u3088\u308D\u3057\u3044\u3067\u3059\u304B\uFF1F";
    
    
    
    public static final String LOGGER = "\u30ED\u30AC\u30FC: ";   // For logger SW version
    
    
    // GPSFile
    public static final String CLOSE_FAILED =
        "\u30D5\u30A1\u30A4\u30EB\u3092\u9589\u3058\u308B\u306E\u306B\u5931\u6557\u3057\u307E\u3057\u305F\u3002\u304A\u305D\u3089\u304F\u30D0\u30B0\u3067\u3059\u3002"; 
    public static final String WRITING_CLOSED =
        "\u9589\u3058\u305F\u30D5\u30A1\u30A4\u30EB\u306B\u66F8\u304D\u8FBC\u3093\u3067\u3044\u307E\u3059\u3002"; 
        
    // Flash option
    public static final String TIMESLEFT = "\u7D4C\u904E\u6642\u9593"; 
    public static final String UPDATERATE = "\u66F4\u65B0\u30EC\u30FC\u30C8 (Hz)"; 
    public static final String BAUDRATE = "\u901A\u4FE1\u30EC\u30FC\u30C8"; 
    public static final String WRITEFLASH = "\u30E1\u30E2\u30EA\u66F8\u8FBC"; 
    public static final String ABORT = "\u4E2D\u6B62"; 
    public static final String TXT_FLASH_LIMITED_WRITES=
        "\u30D5\u30E9\u30C3\u30B7\u30E5\u30E1\u30E2\u30EA\u306B\u66F8\u304D\u8FBC\u3081\u308B\u56DE\u6570\u306B\u306F\u4E0A\u9650\u304C\u3042\u308A\u307E\u3059\u3002|"
        + "\u307E\u305F\u3001\u8A2D\u5B9A\u3092\u5909\u3048\u308B\u3053\u3068\u3067|"
        + "\u30ED\u30AC\u30FC\u304C\u6A5F\u80FD\u3057\u306A\u304F\u306A\u308B\u53EF\u80FD\u6027\u3082\u3042\u308A\u307E\u3059\u3002|"
        + "\uFF08\u4F8B\u3048\u3070\u901A\u4FE1\u30EC\u30FC\u30C8\u306E\u5909\u66F4\u306A\u3069\uFF09|"
        + "\u4E2D\u6B62\u30DC\u30BF\u30F3\u3067\u3001\u8A2D\u5B9A\u306E\u5909\u66F4\u3092\u4E2D\u65AD\u3067\u304D\u307E\u3059\u3002";
        
    public static final String PERIOD_ABBREV = "Per";
    
    // Forgotten in Advanced track filter
    public static final String IGNORE_0VALUES
        = "0\u5024\u306F\u7121\u8996\u3055\u308C\u307E\u3059"; 
    
    public static final String STORE_SETTINGS =
        "\u4FDD\u5B58\u8A2D\u5B9A"; 
    public static final String RESTORE_SETTINGS =
        "\u5909\u66F4\u8A2D\u5B9A"; 

    public static final String WARNING =
        "Warning";
    public static final String NO_FILES_WERE_CREATED =
        "No output files were created!"
        + "||"
        + "This usually means that either:"
        + "|- The filter did not select any points"
        + "|- The log does not contain any data"
        + "|"
        + "|Try selecting all points."
        + "|If that does not work,"
        + "| it may be a bug.";
    public static final String ADD_RECORD_NUMBER =
        "Record nbr info in logs";
    
    public static final String S_DEBUG_CONN= "Debug conn.";
    public static final String S_IMPERIAL= "Imperial Units";
    public static final String BAD_SUPERWABAVERSION =
        "This application was built for|SuperWaba V";
    public static final String BAD_SUPERWABAVERSION_CONT =
          ".|This version is V";
    public static final String BAD_SUPERWABAVERSION_CONT2 =
        ".|Exiting application";

    public static final String S_DEVICE = "Device";
    public static final String S_DEFAULTDEVICE = "Default Device";
    public static final String S_GISTEQTYPE1 = "iTrackU-Nemerix";
    public static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr";
    public static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII";

    public static final String BT_MAC_ADDR = "BT Mac Addr:";
    public static final String S_OUTPUT_LOGCONDITIONS = "Output log conditions";
    public static final String MEM_FREE = "free";
}
