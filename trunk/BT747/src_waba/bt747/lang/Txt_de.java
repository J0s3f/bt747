package bt747.lang;

import bt747.Version;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public final class Txt_de implements TxtInterface {
    private static String fontFile = null;
    private static String encoding = null;

    // BT747 class
    private static final String S_FILE = "Datei";
    private static final String S_EXIT_APPLICATION = "Ende";

    private static final String S_SETTINGS = "Einst.";
    private static final String S_STOP_LOGGING_ON_CONNECT = "Ende log wenn verb.";
    private static final String S_STOP_CONNECTION = "Verbindung unterbrechen";
    private static final String S_GPX_UTC_OFFSET_0 = "GPX UTC Offset 0";
    private static final String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg wenn klein";
    private static final String S_GPS_DECODE_ACTIVE = "GPS Dekodierung aktiv";
    private static final String S_FOCUS_HIGHLIGHT = "Focus Highlight";
    private static final String S_DEBUG = "Debug";
    private static final String S_STATS = "Stats";
    private static final String S_INFO = "Info";
    private static final String S_ABOUT_BT747 = "Über BT747";
    private static final String S_ABOUT_SUPERWABA = "Über SuperWaba VM";

    private static final String S_TITLE = "BT747 - MTK Logger Control";


    private static final String LB_DOWNLOAD = "Download"; 

    private static final String TITLE_ATTENTION = "Achtung";
    private static final String CONFIRM_APP_EXIT = "Sie versuchen die Anwendung zu beenden|"
                                                  + "Sind sie sicher?";

    private static final String YES =
        "Ja";
    private static final String NO =
        "Nein";
    private static final String CANCEL =
        "Abbruch";


    private static final String ABOUT_TITLE =
        "Über BT747 V"+Version.VERSION_NUMBER;
    private static final String ABOUT_TXT =
        "Erstellt mit SuperWaba"
        + "|http://www.superwaba.org"
        + "|" + Version.BUILD_STR
        + "|von Mario De Weerd"
        + "|m.deweerd@ieee.org"
        + "|Diese Anw. erlaubt die Kontrolle"
        + "|über den iBlue 747 Empfänger."
        + "|Volle Kontr. per Bluetooth kann per"
        + "|Hardwaremod. erreicht werden. "
        + "|Weitere Informationen im Internet."
        + "|Übersetzung von Dirk Haase";


    private static final String ABOUT_SUPERWABA_TITLE =
        "Über SuperWaba";
    private static final String ABOUT_SUPERWABA_TXT =
        "SuperWaba Virtual Machine ";
    private static final String ABOUT_SUPERWABA_TXT_CONTINUE =
          "|Copyright (c)2000-2007"
        + "|Guilherme Campos Hazan"
        + "|www.superwaba.com|"
        + "|"
        + "SuperWaba is an enhanced version"
        + "|of the Waba Virtual Machine"
        + "|Copyright (c) 1998,1999 WabaSoft"
        + "|www.wabasoft.com";

    private static final String DISCLAIMER_TITLE =
        "Disclaimer";
    private static final String DISCLAIMER_TXT =
        "bt747 wird zur Verfügung gestellt,"
        + "|wie sie ist, ohne Garantie irgendw. Art."
        + "|ALL EXPRESS"
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
    private static final String C_FMT  = "Fmt"; // "Format";
    private static final String C_CTRL = "Int"; //"Intervall"; // Ctrl
    private static final String C_LOG  = "Log";
    private static final String C_FILE = "Dat"; //"Datei"; 
    private static final String C_FLTR = "Fltr"; // "Filter";
    private static final String C_EASY = "Enfch"; //"Einfach";
    private static final String C_CON  = "Verb"; //"Verbindung"; 
    private static final String C_OTHR = "Sons"; //"Sonstiges"; 

    // Conctrl strings
    private static final String BT_BLUETOOTH = "Bluetooth";
    private static final String BT_CONNECT_PRT = "Port verbinden";
    private static final String BT_CLOSE_PRT = "Port schließen";
    private static final String BT_REOPEN_PRT  = "Port neu öffnen";
    private static final String MAIN = "Main: ";  //TODO: translate to german
    private static final String FIRMWARE = "Firmware: "; 
    private static final String MODEL = "Model: ";
    private static final String FLASHINFO = "FlashInfo: ";
    private static final String TIME_SEP = "  - Zeit :";
    private static final String LAT = "Breite: ";
    private static final String LON = "Länge: ";
    private static final String GEOID = "Geoid: ";
    private static final String CALC = "(berechn.: ";
    private static final String HGHT_SEP = " - Höhe:";
    private static final String METERS_ABBR = "m";
    // Filters tab panel
    private static final String STANDARD = "Standard";
    private static final String ADVANCED = "Erweitert";


    // BT747_dev class
    private static final String[]C_STR_RCR = {
            "Zeit", "Geschwin.", "Entfern.", "Button",
            "Picture", "Gas Stat", "Phone", "ATM",
            "Bus stop", "Parking", "Post Box", "Railway",
            "Rstaurnt", "Bridge", "View", "Other"
            };
    private static final String [] logFmtItems = {
        "ZEIT (UTC)",        // = 0x00001    // 0
        "GÜLTIG",      // = 0x00002    // 1
        "GEO. BREITE", // = 0x00004    // 2
        "GEO. LÄNGE",  // = 0x00008    // 3
        "HÖHE",        // = 0x00010    // 4
        "GESCHW.",     // = 0x00020    // 5
        "RICHTUNG",  // = 0x00040    // 6 
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
        "MILISEK.",// = 0x40000   // 12
        "ENTFERNUNG",  // = 0x80000    // 13
        "NÜR GLTG DATEI" // =0x80000000
    };
    private static final String C_BAD_LOG_FORMAT = "Falsches Log-Format";


    // Holux specific
    private static final String HOLUX_NAME = "Holux Name";

    private static final String SET = "Setzen";  //TODO*: translate to german ?

    // EASY TAB
    private static final String BT_5HZ_FIX = "5Hz fix und log";
    private static final String BT_2HZ_FIX = "2Hz fix"; //(verhindert Navigation)";
    private static final String BT_HOT = "Heißstart";
    private static final String BT_WARM = "Warmstart";
    private static final String BT_COLD = "Kaltstart";
    private static final String BT_FACT_RESET = "Firmware-Reset";
    private static final String BT_FORCED_ERASE = "Löschen erzwingen";
    private static final String BT_PT_WITH_REASON = "Klicken für entspr. Log-Ereignis:";

    private static final String CONFIRM_FACT_RESET =
            "Sie wollen einen Firmware-Reset ihres|"
            + "GPS-Loggers durchführen.|"
            + "||Durchführung erfolgt auf ihr|"
            + "eigenes Risko ???";

    // File tab
    private static final String OUTPUT_DIR = "Ausgabe-Verzeichnis:";
    private static final String LOGFILE = "Log-Datei:";
    private static final String REPORT = "Bericht :";  
    private static final String CHUNK = "Blockgröße :";
    private static final String CHUNK_AHEAD = "Blockgröße Wiederholung:";
    private static final String READ_TIMEOUT = "Lese-Zeitbeschränkung (ms) :";
    private static final String CARD_VOL = "Karte/Laufwerk:";
    private static final String APPLY_SET = "Werte übernehmen";
    private static final String DEFAULT_SET = "Voreinstellungen";

    // Log filter
    private static final String[] STR_VALID= {
            "Kein Fix",
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
    private static final String ACTIVE = "AKTIV";
    private static final String INACTIVE = "INAKTIV";
    private static final String FLTR_REC = "<= Datenstatznr. <= ";
    private static final String FLTR_SPD = "<= Geschwindigkeit <= ";
    private static final String FLTR_DST = "<= Abstand <= ";
    private static final String FLTR_PDOP = "PDOP <= ";
    private static final String FLTR_HDOP = "HDOP <= ";
    private static final String FLTR_VDOP = "VDOP <= ";
    private static final String FLTR_NSAT = "<= NSAT";

    private static final String CLEAR = "Löschen";

    // Log format
    private static final String REC_ESTIMATED = " geschätzte Datensätze";
    private static final String SET_ERASE = "Setzen&Löschen";
    private static final String SET_NOERASE = "Nur setzen";
    private static final String ERASE = "Löschen";
    private static final String CONFIRM_ERASE = "Löschen bestätigen";

    private static final String C_msgWarningFormatIncompatibilityRisk =
        "Ihre Wahl wird das Log-Format setzen ohne den "
        + "Log-Speicher zu löschen.|"
        + "Andere Software kann möglicherweise nicht mit "
        + "Daten arbeiten!||"
        + "Sind sie damit einverstanden?"; 

    /** Message warning user about impact of changing log format */
    private static final String C_msgWarningFormatAndErase =
        "Sie wollen das Log-Format ändern"
        + "|und"
        + "|den Log-Speicher LÖSCHEN"
        + "|"
        + "|LOG-FORMAT ÄNDERN UND LÖSCHEN?";
    /** Message warning the user again about the impact of a log format change */
    private static final String C_msgWarningFormatAndErase2 =
        "Dies ist ihre letzte Chance"
        + "|das Löschen abzubrechen."
        + "|"
        + "|LOG-FORMAT ÄNDERN UND LÖSCHEN?";
    /** Message warning user about impact of changing log format */
    private static final String C_msgEraseWarning =
        "Sie wollen den"
        + "|Log-Speicher löschen."
        + "|"
        + "|LOG LÖSCHEN?";
    private static final String C_msgEraseWarning2 =
        "Dies ist ihre letzte Chance"
        + "|das Löschen abzubrechen."
        + "|"
        + "|LOG LÖSCHEN?";

    private static final String ONE_FILE = "Eine Datei";
    private static final String ONE_FILE_DAY = "Eine Datei/Tag";
    private static final String ONE_FILE_TRK = "Eine Datei/Track";
    private static final String DEV_LOGONOFF = "Log ein(/aus)";
    private static final String INCREMENTAL = "Incremental";

    private static final String LOG_OVRWR_FULL = "Log überschreiben(/stop) wenn voll";
    private static final String DATE_RANGE = "Datum: ";
    private static final String GET_LOG = "Log laden";
    private static final String CANCEL_GET = "Stop";
    private static final String NOFIX_COL = "NoFixFarbe";
    private static final String TRK_SEP = "Trk sep:";
    private static final String MIN = "min";
    private static final String UTC = "UTC";
    private static final String HGHT_GEOID_DIFF = "Höhe-geoid diff";  // diff = difference
    private static final String TO_CSV = "-> CSV";
    private static final String TO_GPX = "-> GPX";
    private static final String TO_KML = "-> KML";
    private static final String TO_TRK = "-> TRK";
    private static final String TO_PLT = "-> PLT";
    private static final String TO_GMAP= "-> GMAP";
    private static final String TO_NMEA= "-> NMEA";
    private static final String MEM_USED = "Speicher genutzt   : ";
    private static final String NBR_RECORDS = "Anzahl Datensätze: ";


    //Log reason
    private static final String NO_DGPS = "Kein DGPS";
    private static final String RTCM = "RTCM";
    private static final String WAAS = "WAAS";
    private static final String RCR_TIME = "Zeit (s)    ";
    private static final String RCR_SPD  = "Geschwindigkeit (km/h)";
    private static final String RCR_DIST = "Entfernung (m)";
    private static final String FIX_PER = "Fix (ms)";
    private static final String INCL_TST_SBAS = "Inkl. Test SBAS";
    private static final String PWR_SAVE_INTRNL = "Stromsparend (Intern)";

    // NMEA OUTPUT
    private static final String DEFAULTS = "Vorgabe";


    // Other tabs
    private static final String TAB_FLSH = "Flsh";
    private static final String TAB_NMEA_OUT = "NMEA Ausgabe";
    private static final String TAB_NMEA_FILE = "NMEA Datei";
    private static final String TAB_HOLUX = "Holux";


    // Log convert
    private static final String ERROR = "Fehler";
    private static final String PROBLEM_READING = "Leseproblem|";
    private static final String COULD_NOT_OPEN = "Kann nicht öffnen|";


    // GPS State
    private static final String CANCEL_WAITING = "Löschen abbrechen";
    private static final String TITLE_WAITING_ERASE =
        "Warten bis Löschen beendet";
    private static final String TXT_WAITING_ERASE =
        "Warten bis Löschen beendet.|"
        + "Sie können das Warten auf eigenes|"
        + "Risiko abbrechen";
    private static final String UNKNOWN = "Unbekannt";
    private static final String CHK_PATH =
        "|Prüfen ob Pfad und Karte beschreibbar";

    private static final String OVERWRITE = "Überschreiben";
    private static final String ABORT_DOWNLOAD = "Laden abbrechen";
    private static final String DATA_NOT_SAME =
    "Die DATEN vom Logger stimmen NICHT mit|"
    + "den zuvor geladenen DATEN überein.|"
    + "|"
    + "Sollen die DATEN überschrieben werden?";
    private static final String LOGGER = "Logger: ";  // For logger SW version


    // GPSFile
    private static final String CLOSE_FAILED =
        "Schließen fehlgeschlagen (Datei schließen) - eventuell ein Bug.";
    private static final String WRITING_CLOSED =
        "Schreiben in geschlossene Datei";

    // Flash option
    private static final String TIMESLEFT = "Verbleib. Zeit";  
    private static final String UPDATERATE = "Wiederhol. Rate (Hz)";
    private static final String BAUDRATE = "Baudrate"; 
    private static final String WRITEFLASH = "Schreibe Flash"; 
    private static final String ABORT = "Über";  
    private static final String TXT_FLASH_LIMITED_WRITES =
        "Die Anzahl der Schreibvorgänge des|"
        + "des Flash-Speichers ist begrenzt und|"
        + "eine Änderung der Einstellungen kann|"
        + "das Gerät unbrauchbar machen.|"
        + "(z.B., eine Änderung der Baudrate)|"
        + "ABBRECHEN durch klicken auf Abbrechen!!";
    private static final String PERIOD_ABBREV = "Per";

    // Forgotten in Advanced track filter
    private static final String IGNORE_0VALUES
        = "0-Werte werden ignoriert.";

    private static final String STORE_SETTINGS =
        "Konf. speichern";  // To save device settings
    private static final String RESTORE_SETTINGS =
        "Konf. laden";  // To restore device settings
    private static final String WARNING =
        "Warnung";
    private static final String NO_FILES_WERE_CREATED =
        "Es wurde keine Ausgabe-Datei erzeugt!"
        + "||"
        + "Dies kann folgende Ursachen haben:"
        + "|- der Filter hat nicht alle Punkte gewählt"
        + "|- das Log enthält keine Daten"
        + "|"
        + "|Versuchen Sie alle Punkte zu wählen."
        + "|Wenn dies nicht funktioniert,"
        + "| ist es möglicherweise ein Fehler/Bug.";
    private static final String ADD_RECORD_NUMBER =
        "Rekord-Nummer im Log";
    
    private static final String S_DEBUG_CONN= "Debug Verbindung";
    private static final String S_IMPERIAL= "Britische Einheiten";
    private static final String BAD_SUPERWABAVERSION =
        "Diese Anwendung wurde erstellt|für SuperWaba V";
    private static final String BAD_SUPERWABAVERSION_CONT =
        ".|Diese Version ist V";
    private static final String BAD_SUPERWABAVERSION_CONT2 =
        "|bt747 beenden";

    private static final String S_DEVICE = "Gerät";
    private static final String S_DEFAULTDEVICE = "Vorgegeb. Gerät";
    private static final String S_GISTEQTYPE1 = "Gisteq Typ 1";
    private static final String S_GISTEQTYPE2 = "Gisteq Typ 2";
    private static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII";
    

    private static final String BT_MAC_ADDR = "BT Mac Addr:";

    private static final String S_OUTPUT_LOGCONDITIONS = "Bed. für Log-Ausgabe";
    private static final String MEM_FREE = "frei";

    private static final String TRKPTCOMMENT = "TRK PT INFO";
    private static final String TRKPTNAME = "TRK PT NAME";
    
    private static final String DOWNLOAD_INCREMENTAL = "Smart dwnld";
    private static final String DOWNLOAD_FULL = "Full dwnld";
    private static final String DOWNLOAD_NORMAL = "Normal dwnld";

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
            DOWNLOAD_INCREMENTAL, DOWNLOAD_FULL, DOWNLOAD_NORMAL, };

}