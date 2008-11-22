package bt747;

import bt747.lang.*;

/**
 * @author Mario De Weerd * Class to provide language specific strings.
 */
public final class Txt {
    private static TxtInterface bundle;
    private final static String defaultLang = "en";
    private final static TxtInterface en = new Txt_en();

    static {
        setLang(defaultLang);
    }

    public static final void setLang(final String lang) {
        try {
            bundle = (TxtInterface) Class.forName("bt747.lang.Txt_" + lang)
                    .newInstance();
        } catch (Exception e) {
            bundle = en;
        }
    }

    public static final String getRcrString(final int i) {
        return bundle.getRcrString(i);
    }

    public static final String getValidString(final int i) {
        return bundle.getValidString(i);
    }

    public static final String getLogFmtItem(final int i) {
        return bundle.getLogFmtItem(i);
    }

    /**
     * Use index from {@link TxtInterface}.
     */
    public static final String getString(final int i) {
        String s;
        s = bundle.getTranslation(i);
        if (s == null) {
            s = en.getTranslation(i);
        }
        return s;
    }

    public static final int fontFile = 0;
    public static final int encoding = 1;
    public static final int S_FILE = 2;
    public static final int S_EXIT_APPLICATION = 3;
    public static final int S_SETTINGS = 4;
    public static final int S_STOP_LOGGING_ON_CONNECT = 5;
    public static final int S_STOP_CONNECTION = 6;
    public static final int S_GPX_UTC_OFFSET_0 = 7;
    public static final int S_GPX_TRKSEG_WHEN_SMALL = 8;
    public static final int S_GPS_DECODE_ACTIVE = 9;
    public static final int S_FOCUS_HIGHLIGHT = 10;
    public static final int S_DEBUG = 11;
    public static final int S_DEBUG_CONN = 12;
    public static final int S_STATS = 13;
    public static final int S_INFO = 14;
    public static final int S_IMPERIAL = 15;
    public static final int S_ABOUT_BT747 = 16;
    public static final int S_ABOUT_SUPERWABA = 17;
    public static final int S_TITLE = 18;
    public static final int LB_DOWNLOAD = 19;
    public static final int TITLE_ATTENTION = 20;
    public static final int CONFIRM_APP_EXIT = 21;
    public static final int YES = 22;
    public static final int NO = 23;
    public static final int CANCEL = 24;
    public static final int ABOUT_TITLE = 25;
    public static final int ABOUT_TXT = 26;
    public static final int ABOUT_SUPERWABA_TITLE = 27;
    public static final int ABOUT_SUPERWABA_TXT = 28;
    public static final int DISCLAIMER_TITLE = 29;
    public static final int DISCLAIMER_TXT = 30;
    public static final int C_FMT = 31;
    public static final int C_CTRL = 32;
    public static final int C_LOG = 33;
    public static final int C_FILE = 34;
    public static final int C_FLTR = 35;
    public static final int C_EASY = 36;
    public static final int C_CON = 37;
    public static final int C_OTHR = 38;
    public static final int BT_BLUETOOTH = 39;
    public static final int BT_CONNECT_PRT = 40;
    public static final int BT_CLOSE_PRT = 41;
    public static final int BT_REOPEN_PRT = 42;
    public static final int MAIN = 43;
    public static final int FIRMWARE = 44;
    public static final int MODEL = 45;
    public static final int FLASHINFO = 46;
    public static final int TIME_SEP = 47;
    public static final int LAT = 48;
    public static final int LON = 49;
    public static final int GEOID = 50;
    public static final int CALC = 51;
    public static final int HGHT_SEP = 52;
    public static final int METERS_ABBR = 53;
    public static final int STANDARD = 54;
    public static final int ADVANCED = 55;
    public static final int C_BAD_LOG_FORMAT = 56;
    public static final int HOLUX_NAME = 57;
    public static final int SET = 58;
    public static final int BT_5HZ_FIX = 59;
    public static final int BT_2HZ_FIX = 60;
    public static final int BT_HOT = 61;
    public static final int BT_WARM = 62;
    public static final int BT_COLD = 63;
    public static final int BT_FACT_RESET = 64;
    public static final int BT_FORCED_ERASE = 65;
    public static final int BT_PT_WITH_REASON = 66;
    public static final int CONFIRM_FACT_RESET = 67;
    public static final int OUTPUT_DIR = 68;
    public static final int LOGFILE = 69;
    public static final int REPORT = 70;
    public static final int CHUNK = 71;
    public static final int CHUNK_AHEAD = 72;
    public static final int READ_TIMEOUT = 73;
    public static final int CARD_VOL = 74;
    public static final int APPLY_SET = 75;
    public static final int DEFAULT_SET = 76;
    public static final int TRKPT = 77;
    public static final int WAYPT = 78;
    public static final int ACTIVE = 79;
    public static final int INACTIVE = 80;
    public static final int FLTR_REC = 81;
    public static final int FLTR_SPD = 82;
    public static final int FLTR_DST = 83;
    public static final int FLTR_PDOP = 84;
    public static final int FLTR_HDOP = 85;
    public static final int FLTR_VDOP = 86;
    public static final int FLTR_NSAT = 87;
    public static final int CLEAR = 88;
    public static final int REC_ESTIMATED = 89;
    public static final int SET_ERASE = 90;
    public static final int SET_NOERASE = 91;
    public static final int ERASE = 92;
    public static final int CONFIRM_ERASE = 93;
    public static final int C_msgWarningFormatIncompatibilityRisk = 94;
    public static final int C_msgWarningFormatAndErase = 95;
    public static final int C_msgWarningFormatAndErase2 = 96;
    public static final int C_msgEraseWarning = 97;
    public static final int C_msgEraseWarning2 = 98;
    public static final int ONE_FILE = 99;
    public static final int ONE_FILE_DAY = 100;
    public static final int ONE_FILE_TRK = 101;
    public static final int DEV_LOGONOFF = 102;
    public static final int INCREMENTAL = 103;
    public static final int LOG_OVRWR_FULL = 104;
    public static final int DATE_RANGE = 105;
    public static final int GET_LOG = 106;
    public static final int CANCEL_GET = 107;
    public static final int NOFIX_COL = 108;
    public static final int TRK_SEP = 109;
    public static final int MIN = 110;
    public static final int UTC = 111;
    public static final int HGHT_GEOID_DIFF = 112;
    public static final int TO_CSV = 113;
    public static final int TO_GPX = 114;
    public static final int TO_KML = 115;
    public static final int TO_TRK = 116;
    public static final int TO_PLT = 117;
    public static final int TO_GMAP = 118;
    public static final int TO_NMEA = 119;
    public static final int MEM_USED = 120;
    public static final int NBR_RECORDS = 121;
    public static final int NO_DGPS = 122;
    public static final int RTCM = 123;
    public static final int WAAS = 124;
    public static final int RCR_TIME = 125;
    public static final int RCR_SPD = 126;
    public static final int RCR_DIST = 127;
    public static final int FIX_PER = 128;
    public static final int INCL_TST_SBAS = 129;
    public static final int PWR_SAVE_INTRNL = 130;
    public static final int DEFAULTS = 131;
    public static final int TAB_FLSH = 132;
    public static final int TAB_NMEA_OUT = 133;
    public static final int TAB_NMEA_FILE = 134;
    public static final int TAB_HOLUX = 135;
    public static final int ERROR = 136;
    public static final int PROBLEM_READING = 137;
    public static final int COULD_NOT_OPEN = 138;
    public static final int CANCEL_WAITING = 139;
    public static final int TITLE_WAITING_ERASE = 140;
    public static final int TXT_WAITING_ERASE = 141;
    public static final int UNKNOWN = 142;
    public static final int CHK_PATH = 143;
    public static final int OVERWRITE = 144;
    public static final int ABORT_DOWNLOAD = 145;
    public static final int DATA_NOT_SAME = 146;
    public static final int LOGGER = 147;
    public static final int CLOSE_FAILED = 148;
    public static final int WRITING_CLOSED = 149;
    public static final int TIMESLEFT = 150;
    public static final int UPDATERATE = 151;
    public static final int BAUDRATE = 152;
    public static final int WRITEFLASH = 153;
    public static final int ABORT = 154;
    public static final int TXT_FLASH_LIMITED_WRITES = 155;
    public static final int PERIOD_ABBREV = 156;
    public static final int IGNORE_0VALUES = 157;
    public static final int STORE_SETTINGS = 158;
    public static final int RESTORE_SETTINGS = 159;
    public static final int WARNING = 160;
    public static final int NO_FILES_WERE_CREATED = 161;
    public static final int ADD_RECORD_NUMBER = 162;
    public static final int BAD_SUPERWABAVERSION = 163;
    public static final int BAD_SUPERWABAVERSION_CONT = 164;
    public static final int BAD_SUPERWABAVERSION_CONT2 = 165;
    public static final int S_DEVICE = 166;
    public static final int S_DEFAULTDEVICE = 167;
    public static final int S_GISTEQTYPE1 = 168;
    public static final int S_GISTEQTYPE2 = 169;
    public static final int S_GISTEQTYPE3 = 170;
    public static final int BT_MAC_ADDR = 171;
    public static final int S_OUTPUT_LOGCONDITIONS = 172;
    public static final int MEM_FREE = 173;
    public static final int TRKPTCOMMENT = 174;
    public static final int TRKPTNAME = 175;
    public static final int DOWNLOAD_INCREMENTAL = 176;
    public static final int DOWNLOAD_FULL = 177;
    public static final int DOWNLOAD_NORMAL = 178;
    public static final int LANG_DE = 179;
    public static final int LANG_EN = 180;
    public static final int LANG_ES = 181;
    public static final int LANG_FR = 182;
    public static final int LANG_IT = 183;
    public static final int LANG_JP = 184;
    public static final int LANG_KO = 185;
    public static final int LANG_NL = 186;
    public static final int LANG_ZH = 187;
    public static final int MI_LANGUAGE = 188;

}
