package bt747.lang;

import bt747.Version;

/**
 * @author Mario De Weerd * Class to provide language specific strings. * DUTCH
 *         TRANSLATION of English text Done by Harold Stringer - Corrections by
 *         Mario De Weerd
 */
public class Txt_nl {
    public static final String fontFile = null;
    public static final String encoding = null;

    // BT747 class
    public static final String S_FILE = "Bestand";
    public static final String S_EXIT_APPLICATION = "Verlaat programma";

    public static final String S_SETTINGS = "Instellingen";
    public static final String S_STOP_LOGGING_ON_CONNECT = "Stop log bij verbinding ";
    public static final String S_STOP_CONNECTION = "Stop verbinding";
    public static final String S_GPX_UTC_OFFSET_0 = "GPX UTC offset 0";
    public static final String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg indien klein ";
    public static final String S_GPS_DECODE_ACTIVE = "GPS Decode actief";
    public static final String S_FOCUS_HIGHLIGHT = "Focus benadrukken (PDA)";
    public static final String S_DEBUG = "Debug";
    public static final String S_DEBUG_CONN = "Debug conn.";
    public static final String S_STATS = "Stats";
    public static final String S_INFO = "Info";
    public static final String S_IMPERIAL = "Engelse maten";
    public static final String S_ABOUT_BT747 = "Over BT747";
    public static final String S_ABOUT_SUPERWABA = "Over SuperWaba VM";

    public static final String S_TITLE = "BT747 - MTK Logger Control";

    public static final String LB_DOWNLOAD = "Download";

    public static final String TITLE_ATTENTION = "Let op";
    public static final String CONFIRM_APP_EXIT = "U sluit hiermee het progamma af|"
            + "Bent u zeker?";

    public static final String YES = "Ja";
    public static final String NO = "Nee";
    public static final String CANCEL = "Afbreken";

    public static final String ABOUT_TITLE = "Over BT747 V"
            + Version.VERSION_NUMBER;
    public static final String ABOUT_TXT = "Gemaakt met SuperWaba"
            + "|http://www.superwaba.org" + "|" + Version.BUILD_STR
            + "|Geschreven door Mario De Weerd" + "|m.deweerd@ieee.org" + "|"
            + "|Dit programma geeft u beheer over " + "|het BT747 apparaat."
            + "|Volledig beheer via bluetooth wordt bereikt"
            + "|door een hardware hack.  "
            + "|Voor meer informatie, zie het Internet."
            + "|Vertaling door Harold Stringer ";

    public static final String ABOUT_SUPERWABA_TITLE = "Over SuperWaba";
    public static final String ABOUT_SUPERWABA_TXT = "SuperWaba Virtual Machine ";
    public static final String ABOUT_SUPERWABA_CONTINUE = "|Copyright (c)2000-2007"
            + "|Guilherme Campos Hazan"
            + "|www.superwaba.com|"
            + "|"
            + "SuperWaba is een verbeterde versie"
            + "|van de Waba Virtual Machine"
            + "|Copyright (c) 1998,1999 WabaSoft" + "|www.wabasoft.com";

    public static final String DISCLAIMER_TITLE = "Disclaimer";
    public static final String DISCLAIMER_TXT = "Het programma wordt geleverd 'AS IS,' zonder"
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
    public static final String C_FMT = "FMT";
    public static final String C_CTRL = "Ctrl";
    public static final String C_LOG = "Log";
    public static final String C_FILE = "Conf";
    public static final String C_FLTR = "Fltr";
    public static final String C_EASY = "Snel";
    public static final String C_CON = "Con";
    public static final String C_OTHR = "Divers";

    // Conctrl strings
    public static final String BT_BLUETOOTH = "BLUETOOTH";
    public static final String BT_CONNECT_PRT = "Poort openen";
    public static final String BT_CLOSE_PRT = "Poort sluiten";
    public static final String BT_REOPEN_PRT = "(Her)open poort";
    public static final String MAIN = "Main:"; // TODO: translate to DUTCH
    public static final String FIRMWARE = "Firmware:";
    public static final String MODEL = "Model:";
    public static final String FLASHINFO = "FlashInfo:";
    public static final String TIME_SEP = "  - Tijd:";
    public static final String LAT = "Breedte:";
    public static final String LON = "Lengte:";
    public static final String GEOID = "Geoid:";
    public static final String CALC = "(berekend:";
    public static final String HGHT_SEP = " - Hoogte:";
    public static final String METERS_ABBR = "m";

    // Filters tab panel
    public static final String STANDARD = "Standaard";
    public static final String ADVANCED = "Geavanceerd";

    // BT747_dev class
    public static final String[] C_STR_RCR = { "Tijd", "Snelheid", "Afstand",
            "Knop",
            "Foto", "Benzine", "Telefoon", "Geld",
            "Bushalte", "Parking", "Postbus", "Trein",
            "Rstaurnt", "Brug", "Zicht", "Andere"
 
            };
    public static final String[] logFmtItems = { "TIJD (UTC)", // = 0x00001 //
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
    public static final String C_BAD_LOG_FORMAT = "Verkeerd log format";

    // Holux specific
    public static final String HOLUX_NAME = "Holux Naam";

    public static final String SET = "Instellen";

    // EASY TAB
    public static final String BT_5HZ_FIX = "5Hz fix + log";
    public static final String BT_2HZ_FIX = "2Hz fix";
    public static final String BT_HOT = "Hete start";
    public static final String BT_WARM = "Warme start";
    public static final String BT_COLD = "Koude start";
    public static final String BT_FACT_RESET = "Fabrieksinstellingen";
    public static final String BT_FORCED_ERASE = "Volledig wissen"; // TODO:
    // translate
    // to DUTCH
    // "Forced
    // erase"
    public static final String BT_PT_WITH_REASON = "Sla een punt op met als kenmerk:";

    public static final String CONFIRM_FACT_RESET = "U staat op het punt om uw GPS Logger apparaat |"
            + "tot de fabrieksinstellingen te resetten. |"
            + "||Bevestigt u deze reset op|" + "uw eigen risico ???";

    // File tab
    public static final String OUTPUT_DIR = "Opslag folder:";
    public static final String LOGFILE = "Log-bestand:";
    public static final String REPORT = "Resultaat :"; // TODO: translate to
    // DUTCH " Report "
    public static final String CHUNK = "Blokgrootte :";
    public static final String CHUNK_AHEAD = "Blokken vooruitlezen:";
    public static final String READ_TIMEOUT = "Lees timeout (ms) :"; // TODO:
    // translate
    // to
    // DUTCH
    // "
    // Volume
    // "
    public static final String CARD_VOL = "Kaart:"; // TODO: translate to DUTCH
    // " Volume "
    public static final String APPLY_SET = "Toepassen";
    public static final String DEFAULT_SET = "Standaard instellingen";

    // Log filter
    public static String[] STR_VALID = { "Geen fix", "SPS", "DGPS", "PPS",
            "RTK", "FRTK", "Schatting", "Handmatig", "Sim" };

    public static final String TRKPT = "TrkPt";
    public static final String WAYPT = "WayPt";

    // Advanced log filter
    public static final String ACTIVE = "AKTIEF";
    public static final String INACTIVE = "INAKTIEF";
    public static final String FLTR_REC = "<= positieindex <= ";
    public static final String FLTR_SPD = "<= snelheid <= ";
    public static final String FLTR_DST = "<= afstand <= ";
    public static final String FLTR_PDOP = "PDOP <= ";
    public static final String FLTR_HDOP = "HDOP <= ";
    public static final String FLTR_VDOP = "VDOP <= ";
    public static final String FLTR_NSAT = "<= NSAT";

    public static final String CLEAR = "WISSEN";

    // Log format
    public static final String REC_ESTIMATED = " logposities geschat";
    public static final String SET_ERASE = "Instellen & wissen";
    public static final String SET_NOERASE = "Instellen";
    public static final String ERASE = "Wissen";
    public static final String CONFIRM_ERASE = "Bevestig wissen";

    public static final String C_msgWarningFormatIncompatibilityRisk = "Uw keuze verandert het log-formaat"
            + "zonder het log-bestand te wissen. |"
            + "Andere software kan zeer waarschijnlijk"
            + "|niet meer "
            + "met de gegevens in uw apparaat werken!||"
            + "Accepteert u deze incompatibiliteit?";

    /** Message warning user about impact of changing log format */
    public static final String C_msgWarningFormatAndErase = " Uw keuze VERANDERT het"
            + "|log-FORMAAT van uw apparaat "
            + "|en"
            + "|WIST het log-BESTAND"
            + "|" + "|LOG-formaat VERANDEREN & WISSEN?";
    /** Message warning the user again about the impact of a log format change */
    public static final String C_msgWarningFormatAndErase2 = "Dit is uw laatste kans om te"
            + "|het wissen van Uw apparaat"
            + "|te voorkomen."
            + "|"
            + "| LOG-formaat VERANDEREN & WISSEN?";
    /** Message warning user about impact of changing log format */
    public static final String C_msgEraseWarning = "Uw keuze WIST het"
            + "|log-bestand op uw apparaat." + "|" + "|LOG-BESTAND WISSEN?";
    public static final String C_msgEraseWarning2 = " Dit is uw laatste kans om te voorkomen "
            + "|het wissen af te breken " + "|" + "|LOG-bestand WISSEN?";

    public static final String ONE_FILE = "Eén bestand";
    public static final String ONE_FILE_DAY = "Eén bestand / dag";
    public static final String ONE_FILE_TRK = "Eén bestand / trk";
    public static final String DEV_LOGONOFF = "Loggen aan(/uit)";
    public static final String INCREMENTAL = "Incrementeel"; // TODO:
    // translate
    // to DUTCH " "

    public static final String LOG_OVRWR_FULL = "Log overschrijven(/stop) indien vol";
    public static final String DATE_RANGE = "Periode: ";
    public static final String GET_LOG = "Log ophalen";
    public static final String CANCEL_GET = "Afbreken";
    public static final String NOFIX_COL = "Geen fix:";
    public static final String TRK_SEP = "Trk splitsing:";
    public static final String MIN = "min";
    public static final String UTC = "UTC";
    public static final String HGHT_GEOID_DIFF = "Juiste hoogte";
    public static final String TO_CSV = "-> CSV";
    public static final String TO_GPX = "-> GPX";
    public static final String TO_KML = "-> KML";
    public static final String TO_TRK = "-> TRK";
    public static final String TO_PLT = "-> PLT";
    public static final String TO_GMAP = "-> GMAP";
    public static final String TO_NMEA = "-> NMEA";
    public static final String MEM_USED = "Geheugen gebruikt : ";
    public static final String NBR_RECORDS = "Aantal velden: ";

    // Log reason
    public static final String NO_DGPS = "Geen DGPS";
    public static final String RTCM = "RTCM";
    public static final String WAAS = "WAAS";
    public static final String RCR_TIME = "Tijd (s)           ";
    public static final String RCR_SPD = "Snelheid (km/h) ";
    public static final String RCR_DIST = "Afstand (m)     ";
    public static final String FIX_PER = "Fix (ms)";
    public static final String INCL_TST_SBAS = "Incl. Test SBAS";
    public static final String PWR_SAVE_INTRNL = "Stroombesparing (Intern)";

    // NMEA OUTPUT
    public static final String DEFAULTS = "Standaard";

    // Other tabs
    public static final String TAB_FLSH = "Flsh";
    public static final String TAB_NMEA_OUT = "NMEA uit";
    public static final String TAB_NMEA_FILE = "NMEA bestand";
    public static final String TAB_HOLUX = "Holux";

    // Log convert
    public static final String ERROR = "Fout";
    public static final String PROBLEM_READING = "Leesprobleem|";
    public static final String COULD_NOT_OPEN = "Kan niet openen|";

    // GPS State
    public static final String CANCEL_WAITING = "Wachten afbreken";
    public static final String TITLE_WAITING_ERASE = "Wacht tot wissen klaar is";
    public static final String TXT_WAITING_ERASE = "Wacht tot wissen klaar is.|"
            + "Wachten afbreken (op eigen risico)";

    public static final String UNKNOWN = "Onbekend";
    public static final String CHK_PATH = "|Controleer pad & of kaart schrijfbaar is ";

    public static final String OVERWRITE = "Overschrijven";
    public static final String ABORT_DOWNLOAD = "Download afbreken";
    public static final String DATA_NOT_SAME = "De DATA op het apparaat komen NIET|"
            + "overeen met de DATA eerder|"
            + "gedownload.|"
            + "Wilt u de DATA overschrijven?";
    public static final String LOGGER = "Logger: "; // For logger SW version

    // GPSFile
    public static final String CLOSE_FAILED = "Sluiten mislukt (closeFile) - waarschijnlijk a bug.";
    public static final String WRITING_CLOSED = "Schrijven in gesloten bestand";

    // Flash option
    public static final String TIMESLEFT = "Aantal resterend";
    public static final String UPDATERATE = "Update snelheid (Hz)";
    public static final String BAUDRATE = "Baud Rate";
    public static final String WRITEFLASH = "Schrijf Flash";
    public static final String ABORT = "Afbreken";
    public static final String TXT_FLASH_LIMITED_WRITES = "Het aantal malen dat het flashgeheugen|"
            + "geschreven kan worden is beperkt en een|"
            + "verandering in de instellingen |"
            + "kan uw apparaat onbruikbaar maken|"
            + "(bijv. een verandering in baud rate)|"
            + "AFBREKEN door op afbreken te klikken!!";
    public static final String PERIOD_ABBREV = "Per";

    // Forgotten in Advanced track filter
    public static final String IGNORE_0VALUES = "Nul-waarden worden genegeerd";

    public static final String STORE_SETTINGS = "Opslaan";
    public static final String RESTORE_SETTINGS = "Herstellen instellingen";
    public static final String WARNING = "Waarschuwing";
    public static final String NO_FILES_WERE_CREATED = "Er zijn geen output bestanden gemaakt!"
            + "||"
            + "Dit betekent meestal dat of:"
            + "|- Het filter geen punten heeft geselecteerd "
            + "|- Het log geen gegevens bevat "
            + "|"
            + "|Probeer alle punten te selecteren."
            + "|Als dat niet werkt,"
            + "|kan het een fout in het programma zijn.";
    public static final String ADD_RECORD_NUMBER = "Positienr in log bestanden";

    public static final String BAD_SUPERWABAVERSION = "Dit programma was geschreven voor |SuperWaba V";
    public static final String BAD_SUPERWABAVERSION_CONT = ".|Deze versie is V";
    public static final String BAD_SUPERWABAVERSION_CONT2 = ".|Programma beeindigen";

    public static final String S_DEVICE = "Apparaat";
    public static final String S_DEFAULTDEVICE = "Standaard apparaat ";
    public static final String S_GISTEQTYPE1 = "iTrackU-Nemerix";
    public static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr";
    public static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII";

    public static final String BT_MAC_ADDR = "BT MAC-adres:";

    public static final String S_OUTPUT_LOGCONDITIONS = "Schrijf log condities"; // TODO:
    // translate
    // to
    // DUTCH
    // " "

    public static final String MEM_FREE = "Vrij";

    public static final String TRKPTCOMMENT = "TRK PT INFO";
    public static final String TRKPTNAME = "TRK PT NAAM";

    public static final String DOWNLOAD_INCREMENTAL = "Slimme dwnld";
    public static final String DOWNLOAD_FULL = "Volledige dwnld";
    public static final String DOWNLOAD_NORMAL = "Normale dwnld";
}
