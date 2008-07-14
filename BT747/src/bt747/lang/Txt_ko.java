package bt747.lang;

import bt747.Version;
import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public class Txt_ko {
    public static final String fontFile="UFFKor";
    public static final String encoding="UTF8";
    
    // BT747 class
    // BT747 class
    public static final String S_FILE = "\uD30C\uC77C";
    public static final String S_EXIT_APPLICATION = "\uD504\uB85C\uADF8\uB7A8 \uB05D\uB0B4\uAE30";
    
    public static final String S_SETTINGS = "\uC124\uC815";
    public static final String S_RESTART_CONNECTION = "\uB2E4\uC2DC \uC811\uC18D\uD558\uAE30";
    public static final String S_STOP_CONNECTION = "\uC811\uC18D \uB05D\uB0B4\uAE30";
    public static final String S_GPX_UTC_OFFSET_0= "GPX UTC \uC624\uD504\uC14B 0";
    public static final String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg when small";
    public static final String S_GPS_DECODE_ACTIVE= "GPS \uAE30\uAE30 \uD65C\uC131\uD654";
    public static final String S_FOCUS_HIGHLIGHT= "\uD3EC\uCEE4\uC2A4 \uD558\uC774\uB77C\uC774\uD2B8";
    public static final String S_DEBUG= "\uB514\uBC84\uADF8";
    public static final String S_STATS= "\uD1B5\uACC4";
    public static final String S_INFO= "\uC815\uBCF4";
    public static final String S_ABOUT_BT747= "BT747\uC5D0 \uAD00\uD558\uC5EC";
    public static final String S_ABOUT_SUPERWABA= "SuperWaba VM\uC5D0 \uAD00\uD558\uC5EC";
    
    public static final String S_TITLE= "BT747 - MTK \uB85C\uAC70 \uCEE8\uD2B8\uB864";

    
    public static final String LB_DOWNLOAD= "\uB2E4\uC6B4\uB85C\uB4DC";
    
    public static final String TITLE_ATTENTION = "\uC8FC\uC758";
    public static final String CONFIRM_APP_EXIT = "\uD504\uB85C\uADF8\uB7A8\uC744 \uB05D\uB0B4\uB824\uACE0 \uD569\uB2C8\uB2E4.|"
                                                  + "\uC815\uB9D0 \uD504\uB85C\uADF8\uB7A8\uC744 \uB05D\uB0C5\uB2C8\uAE4C?";
    
    public static final String YES=
        "\uB124";
    public static final String NO=
        "\uC544\uB2C8\uC624";
    public static final String CANCEL=
        "\uCDE8\uC18C";


    public static final String ABOUT_TITLE=
        "BT747 V"+Version.VERSION_NUMBER+"\uC5D0 \uAD00\uD558\uC5EC";
    public static final String ABOUT_TXT=
        "SuperWaba\uB97C \uC774\uC6A9\uD558\uC5EC "
        + "|http://www.superwaba.org"
        + "|" +Version.BUILD_STR
        + "|Mario De Weerd\uAC00 \uC791\uC131\uD588\uC2B5\uB2C8\uB2E4."
        + "|m.deweerd@ieee.org"
        + "|"
        + "|\uC774 \uD504\uB85C\uADF8\uB7A8\uC740 BT747 \uAE30\uAE30\uB97C \uC870\uC808\uD569\uB2C8\uB2E4."
        + "|\uD558\uB4DC\uC6E8\uC5B4\uB97C \uD574\uD0B9\uD558\uC5EC \uBE14\uB8E8\uD22C\uC2A4\uB97C \uD1B5\uD55C"
        + "|\uAE30\uAE30\uC758 \uC870\uC808\uC774 \uAC00\uB2A5\uD569\uB2C8\uB2E4."
        + "|\uC790\uC138\uD55C \uC815\uBCF4\uB294 \uC6F9\uC744 \uCC38\uACE0\uD558\uC138\uC694."; 

    
    public static final String ABOUT_SUPERWABA_TITLE=
        "SuperWaba\uC5D0 \uAD00\uD558\uC5EC";
    public static final String ABOUT_SUPERWABA_TXT=
        "SuperWaba Virtual Machine "+ Settings.versionStr
        + "|Copyright (c)2000-2007"
        + "|Guilherme Campos Hazan"
        + "|www.superwaba.com|"
        + "|"
        + "SuperWaba\uB294 Waba Virtual Machine\uC758 "
        + "|\uAC1C\uB7C9 \uBC84\uC804\uC785\uB2C8\uB2E4."
        + "|Copyright (c) 1998,1999 WabaSoft"
        + "|www.wabasoft.com";
    
    public static final String DISCLAIMER_TITLE=
        "\uCC45\uC784\uC758 \uD55C\uACC4";
    public static final String DISCLAIMER_TXT=
        "\uC774 \uC18C\uD504\uD2B8\uC6E8\uC5B4\uB294 \uC5B4\uB5A4 \uC885\uB958\uC758 \uBCF4\uC99D\uC5C6\uC774 "
        + "|'\uC788\uB294 \uADF8\uB300\uB85C' \uC81C\uACF5\uB429\uB2C8\uB2E4. "
        + "|\uBB35\uC2DC\uC801\uC778 \uC5B4\uB5A4 \uC885\uB958\uC758 \uC0C1\uC5C5\uC131, "
        + "|\uD2B9\uC815 \uC6A9\uB3C4\uC5D0\uC758 \uC801\uD569\uC131\uC774\uB098 \uBB34\uD574\uD568\uC744 "
        + "|\uD3EC\uD568\uD558\uC5EC \uC5B4\uB5A4 \uC885\uB958\uC758 \uD45C\uD604, \uAC00\uC815\uC774\uB098 \uBCF4\uC99D\uB3C4 "
        + "|\uAC00\uC9C0\uACE0 \uC788\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4."
        + "|\uC18C\uD504\uD2B8\uC6E8\uC5B4\uB97C \uC0AC\uC6A9\uD558\uBA74\uC11C "
        + "|\uBC1C\uC0DD\uD560 \uC218 \uC788\uB294 \uBAA8\uB4E0 \uC704\uD5D8\uC740 \uC0AC\uC6A9\uC790\uAC00 "
        + "|\uAC10\uC218\uD569\uB2C8\uB2E4. \uC880 \uB354 \uC790\uC138\uD55C \uAC83\uC740 "
        + "|GNU \uC77C\uBC18 \uACF5\uACF5 \uB77C\uC774\uC13C\uC2A4\uB97C \uCC38\uACE0\uD558\uC138\uC694.";

    // TAB identification
    public static final String C_FMT  = "\uD3EC\uB9F7";
    public static final String C_CTRL = "\uCEE8\uD2B8\uB864";
    public static final String C_LOG  = "\uB85C\uADF8";
    public static final String C_FILE = "\uD30C\uC77C";
    public static final String C_FLTR = "\uD544\uD130";
    public static final String C_EASY = "\uAC04\uB2E8";
    public static final String C_CON  = "\uCF58\uC194";
    public static final String C_OTHR = "\uAE30\uD0C0";

    // Conctrl strings
    public static final String BT_BLUETOOTH = "\uBE14\uB8E8\uD22C\uC2A4";
    public static final String BT_CONNECT_PRT = "\uC811\uC18D \uD3EC\uD2B8 \uBC88\uD638";
    public static final String BT_CLOSE_PRT = "\uD3EC\uD2B8 \uB2EB\uAE30";
    public static final String BT_REOPEN_PRT  = "\uD3EC\uD2B8 (\uB2E4\uC2DC) \uC5F4\uAE30";
    public static final String MAIN = "\uC8FC:";
    public static final String FIRMWARE = "\uD38C\uC6E8\uC5B4:";
    public static final String MODEL = "\uBAA8\uB378:";
    public static final String FLASHINFO = "\uD50C\uB798\uC2DC \uC815\uBCF4:";
    public static final String TIME_SEP = "  - \uC2DC\uAC04:";
    public static final String LAT = "\uC704\uB3C4:";
    public static final String LON = "\uACBD\uB3C4:";
    public static final String GEOID = "\uC9C0\uC624\uC774\uB4DC:";
    public static final String CALC = "(\uACC4\uC0B0:";
    public static final String HGHT_SEP = " - \uACE0\uB3C4:";
    
    // Filters tab panel
    public static final String STANDARD = "\uD45C\uC900";
    public static final String ADVANCED = "\uACE0\uAE09";

    
    // BT747_dev class
    public static final String[]C_STR_RCR = {
            "\uC2DC\uAC04", "\uC18D\uB3C4", "\uAC70\uB9AC", "\uBC84\uD2BC",
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
        "VALID PTS ONLY" // =0x80000000
    };
    public static final String C_BAD_LOG_FORMAT = "\uBE44\uC815\uC0C1\uC801\uC778 \uB85C\uADF8";
    

    // Holux specific
    public static final String HOLUX_NAME = "Holux \uC774\uB984";

    public static final String SET = "\uC124\uC815";

    // EASY TAB
    public static final String BT_5HZ_FIX = "5Hz \uACE0\uC815 + \uB85C\uADF8";
    public static final String BT_2HZ_FIX = "2Hz \uACE0\uC815";
    public static final String BT_HOT = "\uD56B \uC2A4\uD0C0\uD2B8";
    public static final String BT_WARM = "\uC6DC \uC2A4\uD0C0\uD2B8";
    public static final String BT_COLD = "\uCF5C\uB4DC \uC2A4\uD0C0\uD2B8";
    public static final String BT_FACT_RESET = "\uACF5\uC7A5 \uCD08\uAE30\uAC12";
    public static final String BT_FORCED_ERASE = "\uAC15\uC81C \uC9C0\uC6B0\uAE30";
    public static final String BT_PT_WITH_REASON = "\uB85C\uADF8\uB97C \uC800\uC7A5\uD560 \uBC29\uC2DD\uC744 \uC120\uD0DD\uD558\uC138\uC694 :";

    public static final String CONFIRM_FACT_RESET =
            "GPS \uB85C\uAC70 \uAE30\uAE30\uC758 \uC0C1\uD0DC\uB97C \uACF5\uC7A5 \uCD08\uAE30\uAC12\uC73C\uB85C |"
            + "\uBC14\uAFB8\uB824\uACE0 \uD569\uB2C8\uB2E4.|"
            + "||\uC704\uD5D8\uC744 \uAC10\uC218\uD558\uACE0 \uC2E4\uD589\uD558\uACA0\uC2B5\uB2C8\uAE4C???";    
    
    // File tab
    public static final String OUTPUT_DIR = "\uC800\uC7A5 \uB514\uB809\uD1A0\uB9AC :";
    public static final String LOGFILE = "\uB85C\uADF8\uD30C\uC77C :";
    public static final String REPORT = "\uB9AC\uD3EC\uD2B8 :";
    public static final String CHUNK = "\uC870\uAC01 :";
    public static final String CHUNK_AHEAD = "\uC870\uAC01 \uC804 \uC694\uCCAD :";
    public static final String READ_TIMEOUT = "\uC77D\uAE30 \uD0C0\uC784\uC544\uC6C3 (ms) :";
    public static final String CARD_VOL = "\uCE74\uB4DC/\uC6A9\uB7C9:";
    public static final String APPLY_SET = "\uC704\uC758 \uAC12\uC73C\uB85C \uC124\uC815\uD558\uACE0 \uC800\uC7A5";
    public static final String DEFAULT_SET = "\uCD08\uAE30\uAC12";

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

    public static final String TRKPT = "\uD2B8\uB799\uD3EC\uC778\uD2B8";
    public static final String WAYPT = "\uC6E8\uC774\uD3EC\uC778\uD2B8";
    
    // Advanced log filter
    public static final String ACTIVE = "\uD65C\uC131";
    public static final String INACTIVE = "\uBE44\uD65C\uC131";
    public static final String FLTR_REC = "<= \uB808\uCF54\uB4DC \uBC88\uD638 <= ";
    public static final String FLTR_SPD = "<= \uC18D\uB3C4 <= ";
    public static final String FLTR_DST = "<= \uAC70\uB9AC <= ";
    public static final String FLTR_PDOP = "PDOP <= ";
    public static final String FLTR_HDOP = "HDOP <= ";
    public static final String FLTR_VDOP = "VDOP <= ";
    public static final String FLTR_NSAT = "<= NSAT";
    
    public static final String CLEAR = "\uC9C0\uC6B0\uAE30";
    
    // Log format
    public static final String REC_ESTIMATED = " \uAC1C\uC758 \uB808\uCF54\uB4DC (\uCD94\uC815)";
    public static final String SET_ERASE = "\uC124\uC815\uD558\uACE0 \uC0AD\uC81C";
    public static final String SET_NOERASE = "\uC124\uC815 (\uC0AD\uC81C\uD558\uC9C0 \uC54A\uC74C)";
    public static final String ERASE = "\uC0AD\uC81C";
    public static final String CONFIRM_ERASE = "\uC0AD\uC81C \uD655\uC778";
    
    public static final String C_msgWarningFormatIncompatibilityRisk =
        "\uB85C\uADF8\uB97C \uC0AD\uC81C\uD558\uC9C0 \uC54A\uACE0 \uB85C\uADF8 \uD3EC\uB9F7\uC744 \uBCC0\uACBD\uD569\uB2C8\uB2E4.|"
        + "\uB2E4\uB978 \uC18C\uD504\uD2B8\uC6E8\uC5B4\uC5D0\uC11C \uAE30\uAE30\uC758 \uC790\uB8CC\uB97C \uC778\uC2DD\uD558\uC9C0 "
        + "\uBABB\uD560\uC218\uB3C4 \uC788\uC2B5\uB2C8\uB2E4!||"
        + "\uC774\uB7F0 \uBE44\uD638\uD658\uC131\uC5D0 \uB3D9\uC758\uD569\uB2C8\uAE4C?";
    
    /** Message warning user about impact of changing log format */
    public static final String C_msgWarningFormatAndErase = 
        "\uB85C\uADF8\uB97C \uC0AD\uC81C\uD558\uACE0 \uB85C\uADF8 \uD3EC\uB9F7\uC744 \uBCC0\uACBD\uD569\uB2C8\uB2E4."
        + "|"
        + "|\uB85C\uADF8 \uC0AD\uC81C\uC640 \uD3EC\uB9F7 \uBCC0\uACBD?";
    /** Message warning the user again about the impact of a log format change */           
    public static final String C_msgWarningFormatAndErase2 =
        "\uAE30\uAE30\uC758 \uC790\uB8CC\uB97C \uC0AD\uC81C\uD558\uC9C0 \uC54A\uC744 \uC218 \uC788\uB294 "
        + "|\uB9C8\uC9C0\uB9C9 \uAE30\uD68C\uC785\uB2C8\uB2E4."
        + "|"
        + "|\uB85C\uADF8 \uC0AD\uC81C\uC640 \uD3EC\uB9F7 \uBCC0\uACBD?";
    /** Message warning user about impact of changing log format */
    public static final String C_msgEraseWarning = 
        "\uAE30\uAE30\uC758 \uB85C\uADF8\uB97C \uC0AD\uC81C\uD569\uB2C8\uB2E4."
        + "|"
        + "|\uB85C\uADF8 \uC0AD\uC81C?";
    public static final String C_msgEraseWarning2 =
        "\uAE30\uAE30\uC758 \uC790\uB8CC\uB97C \uC0AD\uC81C\uD558\uC9C0 \uC54A\uC744 \uC218 \uC788\uB294 "
        + "|\uB9C8\uC9C0\uB9C9 \uAE30\uD68C\uC785\uB2C8\uB2E4."
        + "|"
        + "|\uB85C\uADF8 \uC0AD\uC81C?";

    public static final String ONE_FILE = "\uD558\uB098\uC758 \uD30C\uC77C";
    public static final String ONE_FILE_DAY = "\uB0A0\uC790\uBCC4 \uD30C\uC77C";
    public static final String ONE_FILE_TRK = "\uD2B8\uB799\uBCC4 \uD30C\uC77C";
    public static final String DEV_LOGONOFF = "\uAE30\uAE30 \uB85C\uADF8 \uC800\uC7A5\uD558\uAE30(/\uD558\uC9C0\uC54A\uAE30)";
    public static final String INCREMENTAL = "\uC21C\uCC28\uC801";
    
    public static final String LOG_OVRWR_FULL = "\uBA54\uBAA8\uB9AC \uBD80\uC871\uC2DC \uB85C\uADF8 \uB36E\uC5B4\uC4F0\uAE30(/\uD558\uC9C0\uC54A\uAE30)";
    public static final String DATE_RANGE = "\uB0A0\uC790 \uBC94\uC704";
    public static final String GET_LOG = "\uB85C\uADF8 \uAC00\uC838\uC624\uAE30";
    public static final String CANCEL_GET = "\uCDE8\uC18C";
    public static final String NOFIX_COL = "\uBE44 \uC218\uC815 \uC0C9";
    public static final String TRK_SEP = "\uD2B8\uB799 \uAC04\uACA9:";
    public static final String MIN = "\uBD84";
    public static final String UTC = "UTC";
    public static final String HGHT_GEOID_DIFF = "\uACE0\uB3C4 - \uC9C0\uC624\uC774\uB4DC \uCC28\uC774";
    public static final String TO_CSV = "CSV \uD3EC\uB9F7\uC73C\uB85C";
    public static final String TO_GPX = "GPX \uD3EC\uB9F7\uC73C\uB85C";
    public static final String TO_KML = "KML \uD3EC\uB9F7\uC73C\uB85C";
    public static final String TO_TRK = "TRK \uD3EC\uB9F7\uC73C\uB85C";
    public static final String TO_PLT = "PLT \uD3EC\uB9F7\uC73C\uB85C";
    public static final String TO_GMAP= "GMAP \uD3EC\uB9F7\uC73C\uB85C";
    public static final String TO_NMEA= "NMEA \uD3EC\uB9F7\uC73C\uB85C";
    public static final String MEM_USED = "\uBA54\uBAA8\uB9AC \uC0AC\uC6A9\uB7C9   : ";
    public static final String NBR_RECORDS = "\uB808\uCF54\uB4DC \uAC1C\uC218: ";
    
    
    //Log reason
    public static final String NO_DGPS = "No DGPS";
    public static final String RTCM = "RTCM";
    public static final String WAAS = "WAAS";
    public static final String RCR_TIME = "\uC2DC\uAC04 (s)    ";
    public static final String RCR_SPD  = "\uC18D\uB3C4 (km/h)";
    public static final String RCR_DIST = "\uAC70\uB9AC (m)";
    public static final String FIX_PER = "\uACE0\uC815 (ms)";
    public static final String INCL_TST_SBAS = "SBAS \uC2DC\uD5D8 \uD3EC\uD568";
    public static final String PWR_SAVE_INTRNL = "\uC804\uC6D0 \uC808\uC57D (\uB0B4\uBD80)";

    // NMEA OUTPUT
    public static final String DEFAULTS = "\uAE30\uBCF8\uAC12";
    
    
    // Other tabs
    public static final String TAB_FLSH = "\uD50C\uB798\uC2DC";
    public static final String TAB_NMEA_OUT = "NMEA \uCD9C\uB825";
    public static final String TAB_NMEA_FILE = "NMEA \uC800\uC7A5";
    public static final String TAB_HOLUX = "Holux";
    
    
    // Log convert
    public static final String ERROR = "\uC5D0\uB7EC";
    public static final String PROBLEM_READING = "\uC77D\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4|";
    public static final String COULD_NOT_OPEN = "\uC5F4 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4|";
    
    
    // GPS State
    public static final String CANCEL_WAITING = "\uB300\uAE30 \uCDE8\uC18C";
    public static final String TITLE_WAITING_ERASE =
        "\uC0AD\uC81C\uC911";
    public static final String TXT_WAITING_ERASE =
        "\uC0AD\uC81C\uAC00 \uB05D\uB0A0\uB54C\uAE4C\uC9C0 \uAE30\uB2E4\uB824 \uC8FC\uC138\uC694.|"
        + "\uC704\uD5D8\uC744 \uAC10\uC218\uD558\uACE0 \uCDE8\uC18C\uD560 \uC218 \uC788\uC2B5\uB2C8\uB2E4.";
    
    public static final String UNKNOWN = "\uBBF8\uD655\uC778";
    public static final String CHK_PATH =
        "|\uD328\uC2A4\uB97C \uC62C\uBC14\uB978\uC9C0\uC640 \uCE74\uB4DC\uC5D0 \uC4F8 \uC218 \uC788\uB294\uC9C0 \uD655\uC778\uD558\uC138\uC694";
    
    public static final String OVERWRITE = "\uB36E\uC5B4\uC4F0\uAE30";
    public static final String ABORT_DOWNLOAD = "\uB2E4\uC6B4\uB85C\uB4DC \uCDE8\uC18C";
    public static final String DATA_NOT_SAME = 
    "\uAE30\uAE30\uC758 \uC790\uB8CC\uAC00 \uC774\uC804 \uB2E4\uC6B4\uB85C\uB4DC \uBC1B\uC740 \uC790\uB8CC\uC640 |"
    + "\uC77C\uCE58\uD558\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4.|"
    + "\uC790\uB8CC\uB97C \uB36E\uC5B4\uC501\uB2C8\uAE4C?";
    public static final String LOGGER = "\uAE30\uAE30 \uBC84\uC804: ";  // For logger SW version
    
    
    // GPSFile
    public static final String CLOSE_FAILED =
        "\uB2EB\uAE30\uC5D0 \uC2E4\uD328\uD588\uC2B5\uB2C8\uB2E4 (closeFile) - \uBC84\uADF8\uC758 \uAC00\uB2A5\uC131\uC774 \uB192\uC2B5\uB2C8\uB2E4.";
    public static final String WRITING_CLOSED =
        "\uB2EB\uD78C \uD30C\uC77C\uC5D0 \uC4F0\uACE0 \uC788\uC2B5\uB2C8\uB2E4.";
        
    // Flash option
    public static final String TIMESLEFT = "\uB0A8\uC740\uC2DC\uAC04";
    public static final String UPDATERATE = "\uAC31\uC2E0 \uBE48\uB3C4 (Hz)";
    public static final String BAUDRATE = "\uBCC0\uC870\uC18D\uB3C4";
    public static final String WRITEFLASH = "\uD50C\uB798\uC26C\uC5D0 \uC4F0\uAE30";
    public static final String ABORT = "\uC911\uB2E8";
    public static final String TXT_FLASH_LIMITED_WRITES=
        "\uD50C\uB798\uC26C \uBA54\uBAA8\uB9AC\uC5D0 \uC4F8 \uC218 \uC788\uB294 \uD69F\uC218\uB294 |"
        + "\uC81C\uD55C\uB418\uC5B4 \uC788\uC73C\uBA70 \uC124\uC815\uC744 \uBCC0\uACBD\uD55C \uB2E4\uC74C |"
        + "\uAE30\uAE30\uB97C \uC0AC\uC6A9\uD560 \uC218 \uC5C6\uC744 \uC218\uB3C4 \uC788\uC2B5\uB2C8\uB2E4 |"
        + "(\uC608. \uBCC0\uC870\uC18D\uB3C4 \uBCC0\uACBD)|"
        + "\uC911\uB2E8\uD558\uB824\uBA74 \uCDE8\uC18C\uB97C \uB204\uB974\uC138\uC694!!";
    public static final String PERIOD_ABBREV = "Per";
    
    // Forgotten in Advanced track filter
    public static final String IGNORE_0VALUES
        = "\uC218\uCE58 0\uC740 \uBB34\uC2DC\uD569\uB2C8\uB2E4";
    
    public static final String STORE_SETTINGS =
        "\uC124\uC815 \uC800\uC7A5";
    public static final String RESTORE_SETTINGS =
        "\uC124\uC815 \uAC00\uC838\uC624\uAE30";

    public static final String METERS_ABBR = "m";
    public static final String WARNING =
        "\uACBD\uACE0";
    public static final String NO_FILES_WERE_CREATED =
        "\uC800\uC7A5\uD560 \uD30C\uC77C\uC774 \uB9CC\uB4E4\uC5B4\uC9C0\uC9C0 \uC54A\uC558\uC2B5\uB2C8\uB2E4!"
        + "||"
        + "|- \uD544\uD130\uAC00 \uC544\uBB34\uB7F0 \uD3EC\uC778\uD2B8\uB97C \uC120\uD0DD\uD558\uC9C0 \uC54A\uC558\uAC70\uB098 "
        + "|- \uB85C\uADF8\uAC00 \uC544\uBB34\uB7F0 \uC790\uB8CC\uB97C \uD3EC\uD568\uD558\uC9C0 \uC54A\uC744\uC218 \uC788\uC2B5\uB2C8\uB2E4."
        + "|"
        + "|\uBAA8\uB4E0 \uD3EC\uC778\uD2B8\uB97C \uC120\uD0DD\uD558\uC138\uC694."
        + "|\uADF8\uB798\uB3C4 \uC5D0\uB7EC\uAC00 \uBC1C\uC0DD\uD55C\uB2E4\uBA74,"
        + "| \uBC84\uADF8\uC758 \uAC00\uB2A5\uC131\uC774 \uC788\uC2B5\uB2C8\uB2E4.";
    public static final String ADD_RECORD_NUMBER =
        "\uB85C\uADF8\uC5D0 \uB808\uCF54\uB4DC \uAC1C\uC218\uB97C \uAE30\uB85D";    

    public static final String S_DEBUG_CONN= "\uB514\uBC84\uADF8 \uCF5C\uC194.";
    public static final String S_IMPERIAL= "Imperial Units";
    public static final String BAD_SUPERWABAVERSION =
        "This application was built for|SuperWaba V"
        + Settings.requiredVersionStr + "."
        +"|This version is V"+Settings.versionStr+"."
        +"|Exiting application";

    public static final String S_DEVICE = "Device";
    public static final String S_DEFAULTDEVICE = "Default Device";
    public static final String S_GISTEQTYPE1 = "iTrackU-Nemerix";
    public static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr";
    public static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII";

    public static final String BT_MAC_ADDR = "BT Mac Addr:";
    public static final String S_OUTPUT_LOGCONDITIONS = "Output log conditions";
    public static final String MEM_FREE = "free";
}


