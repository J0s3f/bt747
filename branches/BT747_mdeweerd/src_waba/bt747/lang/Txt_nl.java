package bt747.lang;

import bt747.Version;

/**
 * @author Mario De Weerd * Class to provide language specific strings. *
 *         DUTCH TRANSLATION of English text Done by Harold Stringer -
 *         Corrections by Mario De Weerd
 */
public final class Txt_nl implements TxtInterface {
    private static final String fontFile = null;
    private static final String encoding = null;

    // BT747 class
    private static final String S_FILE = "Bestand";
    private static final String S_EXIT_APPLICATION = "Verlaat programma";

    private static final String S_SETTINGS = "Instel";
    private static final String S_STOP_LOGGING_ON_CONNECT = "Stop log bij verbinding ";
    private static final String S_STOP_CONNECTION = "Stop verbinding";
    private static final String S_GPX_UTC_OFFSET_0 = "GPX UTC offset 0";
    private static final String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg indien klein ";
    private static final String S_GPS_DECODE_ACTIVE = "GPS Decode actief";
    private static final String S_FOCUS_HIGHLIGHT = "Focus benadrukken (PDA)";
    private static final String S_DEBUG = "Debug";
    private static final String S_DEBUG_CONN = "Debug conn.";
    private static final String S_STATS = "Stats";
    private static final String S_INFO = "Info";
    private static final String S_IMPERIAL = "Engelse maten";
    private static final String S_ABOUT_BT747 = "Over BT747";
    private static final String S_ABOUT_SUPERWABA = "Over SuperWaba VM";

    private static final String S_TITLE = "BT747 - MTK Logger Control";

    private static final String LB_DOWNLOAD = "Download";

    private static final String TITLE_ATTENTION = "Let op";
    private static final String CONFIRM_APP_EXIT = "U sluit hiermee het progamma af|"
            + "Bent u zeker?";

    private static final String YES = "Ja";
    private static final String NO = "Nee";
    private static final String CANCEL = "Afbreken";

    private static final String ABOUT_TITLE = "Over BT747 V"
            + Version.VERSION_NUMBER;
    private static final String ABOUT_TXT = "Gemaakt met SuperWaba"
            + "|http://www.superwaba.org" + "|" + Version.BUILD_STR
            + "|Geschreven door Mario De Weerd" + "|m.deweerd@ieee.org" + "|"
            + "|Dit programma geeft u beheer over " + "|het BT747 apparaat."
            + "|Volledig beheer via bluetooth wordt bereikt"
            + "|door een hardware hack.  "
            + "|Voor meer informatie, zie het Internet."
            + "|Vertaling door Harold Stringer ";

    private static final String ABOUT_SUPERWABA_TITLE = "Over SuperWaba";
    private static final String ABOUT_SUPERWABA_TXT = "SuperWaba Virtual Machine ";
    @SuppressWarnings("unused")
    private static final String ABOUT_SUPERWABA_CONTINUE = "|Copyright (c)2000-2007"
            + "|Guilherme Campos Hazan"
            + "|www.superwaba.com|"
            + "|"
            + "SuperWaba is een verbeterde versie"
            + "|van de Waba Virtual Machine"
            + "|Copyright (c) 1998,1999 WabaSoft" + "|www.wabasoft.com";

    private static final String DISCLAIMER_TITLE = "Disclaimer";
    private static final String DISCLAIMER_TXT = "Het programma wordt geleverd 'AS IS,' zonder"
            + "| garantie van enige aard. ALL EXPRESS"
            + "|OR IMPLIED REPRESENTATIONS AND "
            + "|WARRANTIES, INCLUDING ANY IMPLIED"
            + "|WARRANTY OF MERCHANTABILITY,"
            + "|FITNESS FOR A PARTICULAR PURPOSE"
            + "|OR NON-INFRINGEMENT, ARE HEREBY"
            + "|EXCLUDED. THE ENTIRE RISK ARISING "
            + "|OUT OF USING THE SOFTWARE IS"
            + "|ASSUMED BY THE USER. Zie de"
            + "|'GNU General Public License' voor meer " + "|details.";

    // TAB identification
    private static final String C_FMT = "FMT";
    private static final String C_CTRL = "Ctrl";
    private static final String C_LOG = "Log";
    private static final String C_FILE = "Conf";
    private static final String C_FLTR = "Fltr";
    private static final String C_EASY = "Snel";
    private static final String C_CON = "Con";
    private static final String C_OTHR = "Divers";

    // Conctrl strings
    private static final String BT_BLUETOOTH = "BLUETOOTH";
    private static final String BT_CONNECT_PRT = "Poort openen";
    private static final String BT_CLOSE_PRT = "Poort sluiten";
    private static final String BT_REOPEN_PRT = "(Her)open poort";
    private static final String MAIN = "Main:"; // TODO: translate to DUTCH
    private static final String FIRMWARE = "Firmware:";
    private static final String MODEL = "Model:";
    private static final String FLASHINFO = "FlashInfo:";
    private static final String TIME_SEP = "  - Tijd:";
    private static final String LAT = "Breedte:";
    private static final String LON = "Lengte:";
    private static final String GEOID = "Geoid:";
    private static final String CALC = "(berekend:";
    private static final String HGHT_SEP = " - Hoogte:";
    private static final String METERS_ABBR = "m";

    // Filters tab panel
    private static final String STANDARD = "Standaard";
    private static final String ADVANCED = "Geavanceerd";

    // BT747_dev class
    private static final String[] C_STR_RCR = { "Tijd", "Snelheid",
            "Afstand", "Knop", "Foto", "Benzine", "Telefoon", "Geld",
            "Bushalte", "Parking", "Postbus", "Trein", "Rstaurnt", "Brug",
            "Zicht", "Andere"

    };
    private static final String[] logFmtItems = { "TIJD (UTC)", // = 0x00001
                                                                // //
            // 0
            "GELDIG", // = 0x00002 // 1
            "BREEDTE GRAAD", // = 0x00004 // 2
            "LENGTE GRAAD",// = 0x00008 // 3
            "HOOGTE", // = 0x00010 // 4
            "SNELHEID", // = 0x00020 // 5
            "RICHTING", // = 0x00040 // 6
            "DSTA", // = 0x00080 // 7
            "DAGE", // = 0x00100 // 8
            "PDOP", // = 0x00200 // 9
            "HDOP", // = 0x00400 // A
            "VDOP", // = 0x00800 // B
            "NSAT", // = 0x01000 // C
            "SID", // = 0x02000 // D
            "ELEVATION",// = 0x04000 // E
            "AZIMUTH", // = 0x08000 // F
            "SNR", // = 0x10000 // 10
            "RCR", // = 0x20000 // 11
            "MILLISECONDEN",// = 0x40000 // 12
            "AFSTAND", // = 0x80000 // 13
            "Enkel GELDIGE PNTN" // =0x80000000
    };
    private static final String C_BAD_LOG_FORMAT = "Verkeerd log format";

    // Holux specific
    private static final String HOLUX_NAME = "Holux Naam";

    private static final String SET = "Instellen";

    // EASY TAB
    private static final String BT_5HZ_FIX = "5Hz fix + log";
    private static final String BT_2HZ_FIX = "2Hz fix";
    private static final String BT_HOT = "Hete start";
    private static final String BT_WARM = "Warme start";
    private static final String BT_COLD = "Koude start";
    private static final String BT_FACT_RESET = "Fabrieksinstellingen";
    private static final String BT_FORCED_ERASE = "Volledig wissen";
    private static final String BT_PT_WITH_REASON = "Sla een punt op met als kenmerk:";

    private static final String CONFIRM_FACT_RESET = "U staat op het punt om uw GPS Logger apparaat |"
            + "tot de fabrieksinstellingen te resetten. |"
            + "||Bevestigt u deze reset op|" + "uw eigen risico ???";

    // File tab
    private static final String OUTPUT_DIR = "Opslag folder:";
    private static final String LOGFILE = "Log-bestand:";
    private static final String REPORT = "Resultaat :";
    private static final String CHUNK = "Blokgrootte :";
    private static final String CHUNK_AHEAD = "Blokken vooruitlezen:";
    private static final String READ_TIMEOUT = "Lees timeout (ms) :";
    private static final String CARD_VOL = "Kaart:";
    private static final String APPLY_SET = "Toepassen";
    private static final String DEFAULT_SET = "Standaard instellingen";

    // Log filter
    private static String[] STR_VALID = { "Geen fix", "SPS", "DGPS", "PPS",
            "RTK", "FRTK", "Schatting", "Handmatig", "Sim" };

    private static final String TRKPT = "TrkPt";
    private static final String WAYPT = "WayPt";

    // Advanced log filter
    private static final String ACTIVE = "AKTIEF";
    private static final String INACTIVE = "INAKTIEF";
    private static final String FLTR_REC = "<= positieindex <= ";
    private static final String FLTR_SPD = "<= snelheid <= ";
    private static final String FLTR_DST = "<= afstand <= ";
    private static final String FLTR_PDOP = "PDOP <= ";
    private static final String FLTR_HDOP = "HDOP <= ";
    private static final String FLTR_VDOP = "VDOP <= ";
    private static final String FLTR_NSAT = "<= NSAT";

    private static final String CLEAR = "WISSEN";

    // Log format
    private static final String REC_ESTIMATED = " logposities geschat";
    private static final String SET_ERASE = "Instellen & wissen";
    private static final String SET_NOERASE = "Instellen";
    private static final String ERASE = "Wissen";
    private static final String CONFIRM_ERASE = "Bevestig wissen";

    private static final String C_msgWarningFormatIncompatibilityRisk = "Uw keuze verandert het log-formaat"
            + "zonder het log-bestand te wissen. |"
            + "Andere software kan zeer waarschijnlijk"
            + "|niet meer "
            + "met de gegevens in uw apparaat werken!||"
            + "Accepteert u deze incompatibiliteit?";

    /** Message warning user about impact of changing log format */
    private static final String C_msgWarningFormatAndErase = " Uw keuze VERANDERT het"
            + "|log-FORMAAT van uw apparaat "
            + "|en"
            + "|WIST het log-BESTAND"
            + "|"
            + "|LOG-formaat VERANDEREN & WISSEN?";
    /** Message warning the user again about the impact of a log format change */
    private static final String C_msgWarningFormatAndErase2 = "Dit is uw laatste kans om te"
            + "|het wissen van Uw apparaat"
            + "|te voorkomen."
            + "|"
            + "| LOG-formaat VERANDEREN & WISSEN?";
    /** Message warning user about impact of changing log format */
    private static final String C_msgEraseWarning = "Uw keuze WIST het"
            + "|log-bestand op uw apparaat." + "|" + "|LOG-BESTAND WISSEN?";
    private static final String C_msgEraseWarning2 = " Dit is uw laatste kans om te voorkomen "
            + "|het wissen af te breken " + "|" + "|LOG-bestand WISSEN?";

    private static final String ONE_FILE = "E�n bestand";
    private static final String ONE_FILE_DAY = "E�n bestand / dag";
    private static final String ONE_FILE_TRK = "E�n bestand / trk";
    private static final String DEV_LOGONOFF = "Loggen aan(/uit)";
    private static final String INCREMENTAL = "Incrementeel";

    private static final String LOG_OVRWR_FULL = "Log overschrijven(/stop) indien vol";
    private static final String DATE_RANGE = "Periode: ";
    private static final String GET_LOG = "Log ophalen";
    private static final String CANCEL_GET = "Afbreken";
    private static final String NOFIX_COL = "Geen fix:";
    private static final String TRK_SEP = "Trk splitsing:";
    private static final String MIN = "min";
    private static final String UTC = "UTC";
    private static final String HGHT_GEOID_DIFF = "Juiste hoogte";
    private static final String TO_CSV = "-> CSV";
    private static final String TO_GPX = "-> GPX";
    private static final String TO_KML = "-> KML";
    private static final String TO_TRK = "-> TRK";
    private static final String TO_PLT = "-> PLT";
    private static final String TO_GMAP = "-> GMAP";
    private static final String TO_NMEA = "-> NMEA";
    private static final String MEM_USED = "Geheugen gebruikt : ";
    private static final String NBR_RECORDS = "Aantal velden: ";

    // Log reason
    private static final String NO_DGPS = "Geen DGPS";
    private static final String RTCM = "RTCM";
    private static final String WAAS = "WAAS";
    private static final String RCR_TIME = "Tijd (s)           ";
    private static final String RCR_SPD = "Snelheid (km/h) ";
    private static final String RCR_DIST = "Afstand (m)     ";
    private static final String FIX_PER = "Fix (ms)";
    private static final String INCL_TST_SBAS = "Incl. Test SBAS";
    private static final String PWR_SAVE_INTRNL = "Stroombesparing (Intern)";

    // NMEA OUTPUT
    private static final String DEFAULTS = "Standaard";

    // Other tabs
    private static final String TAB_FLSH = "Flsh";
    private static final String TAB_NMEA_OUT = "NMEA uit";
    private static final String TAB_NMEA_FILE = "NMEA bestand";
    private static final String TAB_HOLUX = "Holux";

    // Log convert
    private static final String ERROR = "Fout";
    private static final String PROBLEM_READING = "Leesprobleem|";
    private static final String COULD_NOT_OPEN = "Kan niet openen|";

    // GPS State
    private static final String CANCEL_WAITING = "Wachten afbreken";
    private static final String TITLE_WAITING_ERASE = "Wacht tot wissen klaar is";
    private static final String TXT_WAITING_ERASE = "Wacht tot wissen klaar is.|"
            + "Wachten afbreken (op eigen risico)";

    private static final String UNKNOWN = "Onbekend";
    private static final String CHK_PATH = "|Controleer pad & of kaart schrijfbaar is ";

    private static final String OVERWRITE = "Overschrijven";
    private static final String ABORT_DOWNLOAD = "Download afbreken";
    private static final String DATA_NOT_SAME = "De DATA op het apparaat komen NIET|"
            + "overeen met de DATA eerder|"
            + "gedownload.|"
            + "Wilt u de DATA overschrijven?";
    private static final String LOGGER = "Logger: "; // For logger SW
                                                        // version

    // GPSFile
    private static final String CLOSE_FAILED = "Sluiten mislukt (closeFile) - waarschijnlijk a bug.";
    private static final String WRITING_CLOSED = "Schrijven in gesloten bestand";

    // Flash option
    private static final String TIMESLEFT = "Aantal resterend";
    private static final String UPDATERATE = "Update snelheid (Hz)";
    private static final String BAUDRATE = "Baud Rate";
    private static final String WRITEFLASH = "Schrijf Flash";
    private static final String ABORT = "Afbreken";
    private static final String TXT_FLASH_LIMITED_WRITES = "Het aantal malen dat het flashgeheugen|"
            + "geschreven kan worden is beperkt en een|"
            + "verandering in de instellingen |"
            + "kan uw apparaat onbruikbaar maken|"
            + "(bijv. een verandering in baud rate)|"
            + "AFBREKEN door op afbreken te klikken!!";
    private static final String PERIOD_ABBREV = "Per";

    // Forgotten in Advanced track filter
    private static final String IGNORE_0VALUES = "Nul-waarden worden genegeerd";

    private static final String STORE_SETTINGS = "Opslaan";
    private static final String RESTORE_SETTINGS = "Herstellen instellingen";
    private static final String WARNING = "Waarschuwing";
    private static final String NO_FILES_WERE_CREATED = "Er zijn geen output bestanden gemaakt!"
            + "||"
            + "Dit betekent meestal dat of:"
            + "|- Het filter geen punten heeft geselecteerd "
            + "|- Het log geen gegevens bevat "
            + "|"
            + "|Probeer alle punten te selecteren."
            + "|Als dat niet werkt,"
            + "|kan het een fout in het programma zijn.";
    private static final String ADD_RECORD_NUMBER = "Positienr in log bestanden";

    private static final String BAD_SUPERWABAVERSION = "Dit programma was geschreven voor |SuperWaba V";
    private static final String BAD_SUPERWABAVERSION_CONT = ".|Deze versie is V";
    private static final String BAD_SUPERWABAVERSION_CONT2 = ".|Programma beeindigen";

    private static final String S_DEVICE = "App";
    private static final String S_DEFAULTDEVICE = "Standaard apparaat ";
    private static final String S_GISTEQTYPE1 = "iTrackU-Nemerix";
    private static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr";
    private static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII";

    private static final String BT_MAC_ADDR = "BT MAC-adres:";

    private static final String S_OUTPUT_LOGCONDITIONS = "Schrijf log condities";
    private static final String MEM_FREE = "Vrij";

    private static final String TRKPTCOMMENT = "TRK PT INFO";
    private static final String TRKPTNAME = "TRK PT NAAM";

    private static final String DOWNLOAD_INCREMENTAL = "Slimme dwnld";
    private static final String DOWNLOAD_FULL = "Volledige dwnld";
    private static final String DOWNLOAD_NORMAL = "Normale dwnld";

    private static final String HEIGHT_CONV_AUTOMATIC = "Auto Hgte";
    private static final String HEIGHT_CONV_MSL_TO_WGS84 = "-> WGS84";
    private static final String HEIGHT_CONV_WGS84_TO_MSL = "-> Zee";
    private static final String HEIGHT_CONV_NONE = "Orig Hgte";

    /*
     * (non-Javadoc)
     * 
     * @see bt747.lang.NexTxtInterface#getRcrString(int)
     */
    public final String getRcrString(final int i) {
        return C_STR_RCR[i];
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.lang.NexTxtInterface#getValidString(int)
     */
    public final String getValidString(final int i) {
        return STR_VALID[i];
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.lang.NexTxtInterface#getLogFmtItem(int)
     */
    public final String getLogFmtItem(final int i) {
        return logFmtItems[i];
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.lang.NexTxtInterface#getTranslation(int)
     */
    public final String getTranslation(final int i) {
        if (i < translation.length) {
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
            ABOUT_SUPERWABA_TXT, DISCLAIMER_TITLE, DISCLAIMER_TXT, C_FMT,
            C_CTRL, C_LOG, C_FILE, C_FLTR, C_EASY, C_CON, C_OTHR,
            BT_BLUETOOTH, BT_CONNECT_PRT, BT_CLOSE_PRT, BT_REOPEN_PRT, MAIN,
            FIRMWARE, MODEL, FLASHINFO, TIME_SEP, LAT, LON, GEOID, CALC,
            HGHT_SEP, METERS_ABBR, STANDARD, ADVANCED, C_BAD_LOG_FORMAT,
            HOLUX_NAME, SET, BT_5HZ_FIX, BT_2HZ_FIX, BT_HOT, BT_WARM,
            BT_COLD, BT_FACT_RESET, BT_FORCED_ERASE, BT_PT_WITH_REASON,
            CONFIRM_FACT_RESET, OUTPUT_DIR, LOGFILE, REPORT, CHUNK,
            CHUNK_AHEAD, READ_TIMEOUT, CARD_VOL, APPLY_SET, DEFAULT_SET,
            TRKPT, WAYPT, ACTIVE, INACTIVE, FLTR_REC, FLTR_SPD, FLTR_DST,
            FLTR_PDOP, FLTR_HDOP, FLTR_VDOP, FLTR_NSAT, CLEAR, REC_ESTIMATED,
            SET_ERASE, SET_NOERASE, ERASE, CONFIRM_ERASE,
            C_msgWarningFormatIncompatibilityRisk,
            C_msgWarningFormatAndErase, C_msgWarningFormatAndErase2,
            C_msgEraseWarning, C_msgEraseWarning2, ONE_FILE, ONE_FILE_DAY,
            ONE_FILE_TRK, DEV_LOGONOFF, INCREMENTAL, LOG_OVRWR_FULL,
            DATE_RANGE, GET_LOG, CANCEL_GET, NOFIX_COL, TRK_SEP, MIN, UTC,
            HGHT_GEOID_DIFF, TO_CSV, TO_GPX, TO_KML, TO_TRK, TO_PLT, TO_GMAP,
            TO_NMEA, MEM_USED, NBR_RECORDS, NO_DGPS, RTCM, WAAS, RCR_TIME,
            RCR_SPD, RCR_DIST, FIX_PER, INCL_TST_SBAS, PWR_SAVE_INTRNL,
            DEFAULTS, TAB_FLSH, TAB_NMEA_OUT, TAB_NMEA_FILE, TAB_HOLUX,
            ERROR, PROBLEM_READING, COULD_NOT_OPEN, CANCEL_WAITING,
            TITLE_WAITING_ERASE, TXT_WAITING_ERASE, UNKNOWN, CHK_PATH,
            OVERWRITE, ABORT_DOWNLOAD, DATA_NOT_SAME, LOGGER, CLOSE_FAILED,
            WRITING_CLOSED, TIMESLEFT, UPDATERATE, BAUDRATE, WRITEFLASH,
            ABORT, TXT_FLASH_LIMITED_WRITES, PERIOD_ABBREV, IGNORE_0VALUES,
            STORE_SETTINGS, RESTORE_SETTINGS, WARNING, NO_FILES_WERE_CREATED,
            ADD_RECORD_NUMBER, BAD_SUPERWABAVERSION,
            BAD_SUPERWABAVERSION_CONT, BAD_SUPERWABAVERSION_CONT2, S_DEVICE,
            S_DEFAULTDEVICE, S_GISTEQTYPE1, S_GISTEQTYPE2, S_GISTEQTYPE3,
            BT_MAC_ADDR, S_OUTPUT_LOGCONDITIONS, MEM_FREE, TRKPTCOMMENT,
            TRKPTNAME, DOWNLOAD_INCREMENTAL, DOWNLOAD_FULL, DOWNLOAD_NORMAL,
            null, null, null, null, null, null, null, null, null, null, /* MI_LANGUAGE */
            HEIGHT_CONV_AUTOMATIC, HEIGHT_CONV_MSL_TO_WGS84,
            HEIGHT_CONV_WGS84_TO_MSL, HEIGHT_CONV_NONE, };

}
