package bt747;

import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public class Txt_zh {
    public static final String fontFile="UFFChi";
    public static final String encoding="UTF8";
    
    
    // BT747 class
    
    // It is possible to write the language tokens directly and then convert
    // to UTF format using http://code.cside.com/3rdpage/us/javaUnicode/converter.html 
    //public static final String S_FILE = "??";
    public static final String S_FILE = "\u6587\u4EF6";
    public static final String S_EXIT_APPLICATION = "\u51FA\u53E3\u5E94\u7528";

    public static final String S_SETTINGS = "\u8BBE\u7F6E";
    public static final String S_RESTART_CONNECTION = "\u518D\u5F00\u59CB\u8FDE\u63A5";
    public static final String S_STOP_CONNECTION = "\u4E2D\u6B62\u8FDE\u63A5";
    public static final String S_GPX_UTC_OFFSET_0= "GPX UTC\u62B5\u9500\u4E860";
    public static final String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg\uFF0C\u5F53\u5C0F";
    public static final String S_GPS_DECODE_ACTIVE= "GPS\u89E3\u7801\u6FC0\u6D3B";
    public static final String S_FOCUS_HIGHLIGHT= "\u7126\u70B9\u805A\u7126";
    public static final String S_DEBUG= "\u8C03\u8BD5";
    public static final String S_STATS= "Stats";
    public static final String S_INFO= "\u4FE1\u606F";
    public static final String S_ABOUT_BT747= "\u5173\u4E8EBT747";
    public static final String S_ABOUT_SUPERWABA= "\u5173\u4E8ESuperWaba VM";

    public static final String S_TITLE= "BT747 - MTK\u65E5\u5FD7\u8BB0\u5F55\u5668\u63A7\u5236";


    public static final String LB_DOWNLOAD= "\u4E0B\u8F7D";

    public static final String TITLE_ATTENTION = "\u6CE8\u610F";
    public static final String CONFIRM_APP_EXIT = "\u60A8\u5C06\u9000\u51FA\u5E94\u7528|" + 
    "\u8BC1\u5B9E\u5E94\u7528\u51FA\u53E3\uFF1F";

    public static final String YES=
    "\u662F";
    public static final String NO=
    "\u6CA1\u6709";
    public static final String CANCEL=
    "\u53D6\u6D88";


    public static final String ABOUT_TITLE=
    "\u5173\u4E8EBT747 v " +Version.VERSION_NUMBER;
    public static final String ABOUT_TXT=
    "\u521B\u9020\u4E0ESuperWaba" + 
    "|http://www.superwaba.org " + 
    "|" +Version.BUILD_STR + 
    "|\u5199\u7531Mario De Weerd" + 
    "|m.deweerd@ieee.org " + 
    "|" + 
    "|\u8FD9\u79CD\u5E94\u7528\u5141\u8BB8\u63A7\u5236" + 
    "|BT747\u8BBE\u5907\u3002" + 
    "|\u5B8C\u5168\u63A7\u5236\u4F7F\u7528bluetooth\u4F7F\u80FD" + 
    "|\u901A\u8FC7\u5E94\u7528\u786C\u4EF6\u6587\u4E10\u3002 " + 
    "|\u60A8\u80FD\u627E\u5230\u4FE1\u606F\u5173\u4E8E\u7F51\u3002"; 


    public static final String ABOUT_SUPERWABA_TITLE=
    "\u5173\u4E8ESuperWaba";
    public static final String ABOUT_SUPERWABA_TXT=
    "SuperWaba\u865A\u62DF\u673A"+ Settings.versionStr + 
    "|\u590D\u5236\u6743(c) 2000-2007" + 
    "|Guilherme Campos\u72B9\u592A\u4EBA\u6559\u5802\u9886\u5531\u8005" + 
    "|www.superwaba.com|" + 
    "|" + 
    "SuperWaba\u662F\u4E00\u4E2A\u6539\u8FDB\u7684\u7248\u672C" + 
    "|Waba\u865A\u62DF\u673A" + 
    "|\u7248\u6743(c) 1998,1999 WabaSoft" + 
    "|www.wabasoft.com";

    public static final String DISCLAIMER_TITLE=
    "\u58F0\u660E";
    public static final String DISCLAIMER_TXT=
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

//    \u5236\u8868\u7B26\u8BC1\u660E
    public static final String C_FMT = "\u683C\u5F0F";
    public static final String C_CTRL = "\u63A7\u5236";
    public static final String C_LOG = "\u65E5\u5FD7";
    public static final String C_FILE = "\u6587\u4EF6";
    public static final String C_FLTR = "\u8FC7\u6EE4\u5668";
    public static final String C_EASY = "\u5BB9\u6613";
    public static final String C_CON = "\u8FDE\u63A5";
    public static final String C_OTHR = "\u5176\u4ED6";

//     Conctrl\u4E32
    public static final String BT_BLUETOOTH = "BLUETOOTH";
    public static final String BT_CONNECT_PRT = "\u8FDE\u63A5\u901A\u9053\u6570";
    public static final String BT_CLOSE_PRT = "\u63A5\u8FD1\u7684\u53E3\u5CB8";
    public static final String BT_REOPEN_PRT = "(\u5173\u4E8E)\u5F00\u7AEF\u53E3";
    public static final String MAIN = "\u627C\u8981\uFF1A";
    public static final String FIRMWARE = "\u56FA\u4EF6\uFF1A";
    public static final String MODEL = "\u6A21\u578B\uFF1A";
    public static final String FLASHINFO = "\u4E00\u5239\u90A3\u4FE1\u606F\uFF1A";
    public static final String TIME_SEP = "-\u65F6\u95F4\uFF1A";
    public static final String LAT = "\u7EAC\u5EA6\uFF1A";
    public static final String LON = "\u7ECF\u5EA6\uFF1A";
    public static final String GEOID = "Geoid \uFF1A";
    public static final String CALC = "(\u8BA1\u7B97\uFF1A";
    public static final String HGHT_SEP = "-\u9AD8\u5EA6\uFF1A";
    public static final String METERS_ABBR = "m";


//    \u8FC7\u6EE4\u5236\u8868\u7B26\u76D8\u533A
    public static final String STANDARD = "\u6807\u51C6";
    public static final String ADVANCED = "\u63A8\u8FDB\u4E86";


//     BT747_dev\u7C7B
    public static final String[]C_STR_RCR = {
    "\u65F6\u95F4", "\u901F\u5EA6", "\u8DDD\u79BB", "\u6309\u94AE",
    "App1", "App2", "App3", "App4",
    "App5", "App6", "App7", "App8",
    "App9", "AppX", "AppY", "AppZ"
    };
    public static final String  [] logFmtItems = {
    "UTC", // = 0x00001 // 0
    "\u5408\u6CD5", // = 0x00002 // 1
    "\u7EAC\u5EA6", // = 0x00004 // 2
    "\u7ECF\u5EA6",//= 0x00008 // 3
    "\u9AD8\u5EA6", // = 0x00010 // 4
    "\u52A0\u901F", // = 0x00020 // 5
    "\u671D\u5411", // = 0x00040 // 6
    "DSTA", // = 0x00080 // 7
    "DAGE", // = 0x00100 // 8
    "PDOP", // = 0x00200 // 9
    "HDOP", // = 0x00400 // A
    "VDOP", // = 0x00800 // B
    "NSAT", // = 0x01000 // C
    "SID", // = 0x02000 // D
    "\u6D77\u62D4",//= 0x04000 // E
    "\u65B9\u4F4D\u89D2", // = 0x08000 // F
    "SNR", // = 0x10000 // 10
    "RCR", // = 0x20000 // 11
    "MILISECOND",//= 0x40000 // 12
    "\u8DDD\u79BB", // = 0x80000 // 13
    "HOLUX M-241" // =0x80000000
    };
    public static final String C_BAD_LOG_FORMAT = "\u574F\u65E5\u5FD7\u683C\u5F0F";


//     Holux\u5177\u4F53
    public static final String HOLUX_NAME = "Holux\u540D\u5B57";

    public static final String SET = "\u8BBE\u7F6E\u4E86";

//    \u5BB9\u6613\u7684\u5236\u8868\u7B26
    public static final String BT_5HZ_FIX = "5Hz\u56FA\u5B9A+\u65E5\u5FD7";
    public static final String BT_2HZ_FIX = "2Hz\u56FA\u5B9A";
    public static final String BT_HOT = "\u70ED\u8D77\u52A8";
    public static final String BT_WARM = "\u534A\u70ED\u6001\u8D77\u52A8";
    public static final String BT_COLD = "\u51B7\u8D77\u52A8";
    public static final String BT_FACT_RESET = "\u88AB\u91CD\u65B0\u8BBE\u7F6E\u7684\u5DE5\u5382";
    public static final String BT_FORCED_ERASE = "\u5F3A\u8FEB\u4E86\u5220\u6389";
    public static final String BT_PT_WITH_REASON = "\u91C7\u4F10\u70B9\u7684\u70B9\u51FB\u4EE5\u539F\u56E0\uFF1A";

    public static final String CONFIRM_FACT_RESET =
    "\u60A8\u5C06\u6267\u884C\u5DE5\u5382|" +
    "\u60A8\u7684GPS\u65E5\u5FD7\u8BB0\u5F55\u5668\u8BBE\u5907\u91CD\u65B0\u8BBE\u7F6E |" +
    "||\u60A8\u8BC1\u5B9E\u8FD9\u91CD\u65B0\u8BBE\u7F6E\u5728|" +
    "\u60A8\u81EA\u5DF1\u7684\u98CE\u9669\u6001\u5EA6\u6076\u52A3"; 

//    \u6587\u4EF6\u5236\u8868\u7B26
    public static final String OUTPUT_DIR = "\u8F93\u51FA\u7684\u65B9\u5411\uFF1A";
    public static final String LOGFILE = "\u65E5\u5FD7\u6587\u4EF6\uFF1A";
    public static final String REPORT = "\u62A5\u544A\uFF1A";
    public static final String CHUNK = "\u5927\u5757\uFF1A";
    public static final String CHUNK_AHEAD = "\u524D\u9762\u5927\u5757\u8BF7\u6C42\uFF1A";
    public static final String READ_TIMEOUT = "\u8BFB\u4E86\u6682\u505C(\u5973\u58EB) \uFF1A";
    public static final String CARD_VOL = "\u5361\u7247\u6216\u5BB9\u91CF\uFF1A";
    public static final String APPLY_SET = "\u8FD0\u7528\u5E76\u4E14\u8BBE\u7F6E\u4E0A\u8FF0\u4EF7\u503C";
    public static final String DEFAULT_SET = "\u7F3A\u7701\u8BBE\u7F6E";

//    \u65E5\u5FD7\u8FC7\u6EE4\u5668
    public static final String[] STR_VALID= {
    "\u6CA1\u6709\u56FA\u5B9A",
    "SPS",
    "DGPS",
    "PPS",
    "RTK",
    "FRTK",
    "\u4F30\u8BA1",
    "\u6307\u5357",
    "\u6A21\u4EFF"};

    public static final String TRKPT = "\u8F68\u9053\u70B9";
    public static final String WAYPT = "\u65B9\u5F0F\u70B9";

//    \u5148\u8FDB\u7684\u65E5\u5FD7\u8FC7\u6EE4\u5668
    public static final String ACTIVE = "\u6FC0\u6D3B";
    public static final String INACTIVE = "\u4E0D\u6D3B\u6CFC";
    public static final String FLTR_REC = "<=\u8BB0\u5F55Nbr <=";
    public static final String FLTR_SPD = "<=\u901F\u5EA6<=";
    public static final String FLTR_DST = "<=\u8DDD\u79BB<=";
    public static final String FLTR_PDOP = "PDOP <=";
    public static final String FLTR_HDOP = "HDOP <=";
    public static final String FLTR_VDOP = "VDOP <=";
    public static final String FLTR_NSAT = "<= NSAT";

    public static final String CLEAR = "\u6E05\u695A";

//    \u65E5\u5FD7\u683C\u5F0F
    public static final String REC_ESTIMATED = "\u8BB0\u5F55\u4F30\u8BA1";
    public static final String SET_ERASE = "\u8BBE\u7F6E\u4E86\u5E76\u4E14\u5220\u6389";
    public static final String SET_NOERASE = "\u8BBE\u7F6E\u4E86(\u6CA1\u6709\u5220\u6389)";
    public static final String ERASE = "\u5220\u6389";
    public static final String CONFIRM_ERASE = "\u8BC1\u5B9E\u5220\u6389";

    public static final String C_msgWarningFormatIncompatibilityRisk =
    "\u60A8\u5C06\u6539\u53D8\u60A8\u7684\u8BBE\u5907\u5F62\u5F0F\uFF0C\u4E0D\u7528" +
    "\u5220\u6389\u65E5\u5FD7|" +
    "\u5176\u4ED6\u8F6F\u4EF6\u4E5F\u8BB8\u4E0D\u4E86\u89E3" +
    "\u6570\u636E\u5728\u60A8\u7684\u8BBE\u5907! ||" +
    "\u60A8\u662F\u5426\u8D5E\u6210\u8FD9\u4E0D\u534F\u8C03\u6027\uFF1F";

    public static final String C_msgWarningFormatAndErase = 
    "\u60A8\u5C06\u6539\u53D8" +
    "|\u60A8\u7684\u8BBE\u5907\u91C7\u4F10\u7684\u683C\u5F0F\u3002" +
    "|\u5E76\u4E14" +
    "|\u5220\u6389\u65E5\u5FD7" +
    "|" +
    "|\u91C7\u4F10\u683C\u5F0F\u53D8\u52A8\u5E76\u4E14\u5220\u6389\uFF1F";
     
    public static final String C_msgWarningFormatAndErase2 =
    "\u8FD9\u662F\u60A8\u7684\u6700\u540E\u673A\u4F1A\u907F\u514D" +
    "|\u5220\u6389\u60A8\u7684\u8BBE\u5907\u3002" +
    "|" +
    "|\u91C7\u4F10\u683C\u5F0F\u53D8\u52A8\u5E76\u4E14\u5220\u6389\uFF1F";
    public static final String C_msgEraseWarning = 
    "\u60A8\u662F" +
    "|\u5220\u6389\u60A8\u7684\u8BBE\u5907\u3002" +
    "|" +
    "|\u65E5\u5FD7\u5220\u6389\uFF1F";
    public static final String C_msgEraseWarning2 =
    "\u8FD9\u662F\u60A8\u7684\u6700\u540E\u673A\u4F1A\u907F\u514D" +
    "|\u5220\u6389\u60A8\u7684\u8BBE\u5907\u3002" +
    "|" +
    "|\u65E5\u5FD7\u5220\u6389\uFF1F";

    public static final String ONE_FILE = "\u4E00\u4E2A\u6587\u4EF6";
    public static final String ONE_FILE_DAY = "\u4E00\u6587\u4EF6\u5929";
    public static final String ONE_FILE_TRK = "\u4E00\u6587\u4EF6trk";
    public static final String DEV_LOGONOFF = "\u8BBE\u5907\u6CE8\u518C(/)";
    public static final String INCREMENTAL = "\u589E\u52A0";

    public static final String LOG_OVRWR_FULL = "\u65E5\u5FD7\u91CD\u5199(/\u4E2D\u6B62)\uFF0C\u5F53\u5145\u5206";
    public static final String DATE_RANGE = "\u65E5\u671F\u8303\u56F4";
    public static final String GET_LOG = "\u5F97\u5230\u65E5\u5FD7";
    public static final String CANCEL_GET = "\u53D6\u6D88";
    public static final String NOFIX_COL = "\u6CA1\u6709\u56FA\u5B9A\u989C\u8272";
    public static final String TRK_SEP = "Trk 9\u6708\uFF1A";
    public static final String MIN = "\u6781\u5C0F\u503C";
    public static final String UTC = "UTC";
    public static final String HGHT_GEOID_DIFF = "hght - geiod diff";
    public static final String TO_CSV = "\u5BF9CSV";
    public static final String TO_GPX = "\u5BF9GPX";
    public static final String TO_KML = "\u5BF9KML";
    public static final String TO_TRK = "\u5BF9TRK";
    public static final String TO_PLT = "\u5BF9PLT";
    public static final String TO_GMAP= "\u5BF9GMAP";
    public static final String TO_NMEA= "\u5BF9NMEA";
    public static final String MEM_USED = "\u534A\u65B0\u7684\u8BB0\u5FC6\uFF1A ";
    public static final String NBR_RECORDS = "\u7EAA\u5F55\u6570\uFF1A ";


//    Log\u539F\u56E0
    public static final String NO_DGPS = "\u6CA1\u6709DGPS";
    public static final String RTCM = "RTCM";
    public static final String WAAS = "WAAS";
    public static final String RCR_TIME = "\u65F6\u95F4(s)";
    public static final String RCR_SPD = "\u901F\u5EA6(km/h)";
    public static final String RCR_DIST = "\u8DDD\u79BB(m)";
    public static final String FIX_PER = "\u56FA\u5B9A(\u5973\u58EB)";
    public static final String INCL_TST_SBAS = "\u5305\u62EC\u6D4B\u8BD5SBAS";
    public static final String PWR_SAVE_INTRNL = "\u7701\u7535(\u5185\u90E8)";

//     NMEA\u4EA7\u54C1
    public static final String DEFAULTS = "\u9ED8\u8BA4";


//    \u5176\u4ED6\u5236\u8868\u7B26
    public static final String TAB_FLSH = "\u95EA\u5149";
    public static final String TAB_NMEA_OUT = "\u8F93\u51FA\u7684NMEA";
    public static final String TAB_NMEA_FILE = "NMEA\u6587\u4EF6";
    public static final String TAB_HOLUX = "Holux";


//    \u65E5\u5FD7\u6539\u53D8\u4FE1\u4EF0\u8005
    public static final String ERROR = "\u9519\u8BEF";
    public static final String PROBLEM_READING = "\u95EE\u9898\u8BFB\u4E66|";
    public static final String COULD_NOT_OPEN = "\u4E0D\u80FD\u5F00\u59CB|";


//     GPS\u72B6\u6001
    public static final String CANCEL_WAITING = "\u53D6\u6D88\u7B49\u5F85";
    public static final String TITLE_WAITING_ERASE =
    "\u7B49\u5F85\u76F4\u5230\u5220\u6389\u505A";
    public static final String TXT_WAITING_ERASE =
    "\u7B49\u5F85\uFF0C\u76F4\u5230\u5220\u6389\u5B8C\u6210|" +
    "\u60A8\u80FD\u53D6\u6D88\u7B49\u5F85(\u5728\u60A8\u7684\u98CE\u9669)";

    public static final String UNKNOWN = "\u672A\u77E5\u6570";
    public static final String CHK_PATH =
    "|\u68C0\u67E5\u9053\u8DEF\uFF0C\u5E76\u4E14\uFF0C\u5982\u679C\u5361\u7247\u662F\u53EF\u5199";

    public static final String OVERWRITE = "\u91CD\u5199";
    public static final String ABORT_DOWNLOAD = "\u653E\u5F03\u4E0B\u8F7D";
    public static final String DATA_NOT_SAME = 
    "\u6570\u636E\u5728\u8BBE\u5907\u4E0D|"
    + "\u65E9\u5148\u5BF9\u5E94\u4E8E\u6570\u636E|"
    + "\u4E0B\u8F7D|"
    + "\u60A8\u662F\u5426\u60F3\u91CD\u5199\u6570\u636E\uFF1F";
    public static final String LOGGER = "\u65E5\u5FD7\u8BB0\u5F55\u5668\uFF1A "; //\u4E3A\u65E5\u5FD7\u8BB0\u5F55\u5668SW\u7248\u672C


//     GPSFile
    public static final String CLOSE_FAILED =
    "\u51FA\u6545\u969C(closeFile) -\u53EF\u80FD\u81ED\u866B\u7684\u5173\u95ED\u3002";
    public static final String WRITING_CLOSED =
    "\u5199\u7ED9\u5173\u95ED\u7684\u6587\u4EF6";

//    \u4E00\u5239\u90A3\u9009\u62E9
    public static final String TIMESLEFT = "\u7559\u7ED9\u7684\u65F6\u95F4";
    public static final String UPDATERATE = "\u66F4\u65B0\u7387(\u8D6B\u5179)";
    public static final String BAUDRATE = "\u6CE2\u7279\u901F\u7387";
    public static final String WRITEFLASH = "\u5199\u95EA\u5149";
    public static final String ABORT = "\u653E\u5F03";
    public static final String TXT_FLASH_LIMITED_WRITES=
    "\u6570\u5B57\u7ED9\u95EA\u5149\u5199|" +
    "\u662F\u6709\u9650\u548C\u5728\u8BBE\u7F6E\u4E0A\u7684\u4E00\u4E2A\u53D8\u5316|" +
    "\u80FD\u56DE\u62A5\u60A8\u8BBE\u5907\u4E0D\u80FD\u52A8\u624B\u672F|" +
    "(\u5373\uFF0C\u6CE2\u7279\u901F\u7387\u53D8\u52A8)|" +
    "\u653E\u5F03\u901A\u8FC7\u70B9\u51FB\u653E\u5F03!! ";
    public static final String PERIOD_ABBREV = "\u6BCF";

//     Forgotton\u5728\u5148\u8FDB\u7684\u8F68\u9053\u8FC7\u6EE4\u5668
    public static final String IGNORE_0VALUES
    = "\u503C\u4E3A0\u88AB\u5FFD\u7565";

    public static final String STORE_SETTINGS =
    "\u5B58\u653E\u8BBE\u7F6E";
    public static final String RESTORE_SETTINGS =
    "\u6062\u590D\u8BBE\u7F6E";
    
    public static final String WARNING =
        "Warning";
    public static final String NO_FILES_WERE_CREATED =
        "No output files were created!" +
        "||" +
        "This usually means that either:" +
        "|- The filter did not select any points" +
        "|- The log does not contain any data" +
        "|" +
        "|Try selecting all points." +
        "|If that does not work," +
        "| it may be a bug.";
    public static final String ADD_RECORD_NUMBER =
        "Record nbr info in logs";    

    public static final String S_DEBUG_CONN= "Debug conn.";
    public static final String S_IMPERIAL= "Imperial Units";
    public static final String BAD_SUPERWABAVERSION =
        "This application was built for|SuperWaba V" +
        Settings.requiredVersionStr + "."
        +"|This version is V"+Settings.versionStr+"."
        +"|Exiting application";

    public static final String S_DEVICE = "Device";
    public static final String S_DEFAULTDEVICE = "Default Device";
    public static final String S_GISTEQTYPE1 = "iTrackU-Nemerix";
    public static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr";
    public static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII";
}


