package bt747;

import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public class Txt_de {

   // BT747 class
   public final static String S_FILE = "Datei";
   public final static String S_EXIT_APPLICATION = "Ende";

   public final static String S_SETTINGS = "Einstellungen";
   public final static String S_RESTART_CONNECTION = "Verbindung neu starten";
   public final static String S_STOP_CONNECTION = "Verbindung unterbrechen";
   public final static String S_GPX_UTC_OFFSET_0= "GPX UTC offset 0";
   public final static String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg when small";
   public final static String S_GPS_DECODE_ACTIVE= "GPS Dekodierung aktiv";
   public final static String S_FOCUS_HIGHLIGHT= "Focus Highlight";
   public final static String S_DEBUG= "Debug";
   public final static String S_STATS= "Stats";
   public final static String S_INFO= "Info";
   public final static String S_ABOUT_BT747= "�ber BT747";
   public final static String S_ABOUT_SUPERWABA= "�ber SuperWaba VM";

   public final static String S_TITLE= "BT747 - MTK Logger Control";


   public final static String LB_DOWNLOAD= "Download";

   public final static String TITLE_ATTENTION = "Achtung";
   public final static String CONFIRM_APP_EXIT = "Sie versuchen die Anwendung zu beenden|" +
                                                 "Sind sie sicher?";

   public final static String YES=
       "Ja";
   public final static String NO=
       "Nein";
   public final static String CANCEL=
       "Abbruch";


   public final static String ABOUT_TITLE=
       "�ber BT747 V"+Version.VERSION_NUMBER;
   public final static String ABOUT_TXT=
       "Created with SuperWaba" +
       "|http://www.superwaba.org"+
       "|" +Version.BUILD_STR +
       "|Written by Mario De Weerd" +
       "|m.deweerd@ieee.org"+
       "|"+
       "|Diese Anwendung erlaubt die Kontrolle �ber" +
       "|den iBlue 747 Empf�nger." +
       "|Volle Kontrolle per Bluetooth kann per" +
       "|Hardwaremodifikation erreicht werden. " +
       "|Weitere Informationen im Internet.";


   public final static String ABOUT_SUPERWABA_TITLE=
       "�ber SuperWaba";
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
       "Die Software wird zur Verf�gung gestellt," +
       "|wie sie ist, ohne eine Garantie irgendwelcher Art." +
       "|ALL EXPRESS" +
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
   public final static String BT_CONNECT_PRT = "Schnittstelle verbinden";
   public final static String BT_CLOSE_PRT = "Schnittstelle schlie�en";
   public final static String BT_REOPEN_PRT  = "Schnittstelle �ffnen";
   public final static String MAIN = "Main:";
   public final static String FIRMWARE = "Firmware:";
   public final static String MODEL = "Model:";
   public final static String FLASHINFO = "FlashInfo:";
   public final static String TIME_SEP = "  - Zeit:";
   public final static String LAT = "Breite:";
   public final static String LON = "L�nge:";
   public final static String GEOID = "Geoid:";
   public final static String CALC = "(calc:";
   public final static String HGHT_SEP = " - H�he:";

   // Filters tab panel
   public final static String STANDARD = "Standard";
   public final static String ADVANCED = "Erweitert";


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
   public static final String C_BAD_LOG_FORMAT = "Falsches Log-Format";


   // Holux specific
   public static final String HOLUX_NAME = "Holux Name";

   public static final String SET = "SET";

   // EASY TAB
   public static final String BT_5HZ_FIX = "5Hz fix und log";
   public static final String BT_2HZ_FIX = "2Hz fix (verhindert Navigation)";
   public static final String BT_HOT = "Hei�start";
   public static final String BT_WARM = "Warmstart";
   public static final String BT_COLD = "Kaltstart";
   public static final String BT_FACT_RESET = "Firmware-Reset";
   public static final String BT_FORCED_ERASE = "Erzwungenes L�schen";
   public static final String BT_PT_WITH_REASON = "Klicken f�r entsprechendes Log-Erzeugnis:";

   public static final String CONFIRM_FACT_RESET =
           "Sie wollen einen Firmware-Reset ihres|" +
           "GPS-Loggers durchf�hren.|"+
           "||Durchf�hrung erfolgt auf ihr|"+
           "eigenes Risko ???";

   // File tab
   public static final String OUTPUT_DIR = "Ausgabe-Verzeichnis:";
   public static final String LOGFILE = "Log-Datei:";
   public static final String REPORT = "Report :";
   public static final String CHUNK = "Chunk :";
   public static final String CHUNK_AHEAD = "Chunk ahead request:";
   public static final String READ_TIMEOUT = "Read timeout (ms) :";
   public static final String CARD_VOL = "Karte/Laufwerk:";
   public static final String APPLY_SET = "Werte �bernehmen";
   public static final String DEFAULT_SET = "Voreinstellungen";

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
   public static final String FLTR_REC = "<= Datenstatznr. <= ";
   public static final String FLTR_SPD = "<= Geschwindigkeit <= ";
   public static final String FLTR_DST = "<= Abstand <= ";
   public static final String FLTR_PDOP = "PDOP <= ";
   public static final String FLTR_HDOP = "HDOP <= ";
   public static final String FLTR_VDOP = "VDOP <= ";
   public static final String FLTR_NSAT = "<= NSAT";

   public static final String CLEAR = "L�schen";

   // Log format
   public static final String REC_ESTIMATED = " gesch�tzte Datens�tze";
   public static final String SET_ERASE = "Setzen und L�schen";
   public static final String SET_NOERASE = "Setzen (nicht L�schen)";
   public static final String ERASE = "L�schen";
   public static final String CONFIRM_ERASE = "L�schen best�tigen";

   public static final String C_msgWarningFormatIncompatibilityRisk =
       "Ihre Wahl wird das Log-Format setzen ohne den " +
       "Log-Speicher zu l�schen.|" +
       "Andere Software kann m�glicherweise nicht mit " +
       "Daten arbeiten!||" +
       "Sind sie damit einverstaden?";

   /** Message warning user about impact of changing log format */
   public static final String C_msgWarningFormatAndErase =
       "Sie wollen das Log-Format �ndern" +
       "|und" +
       "|den Log-Speicher L�SCHEN" +
       "|" +
       "|LOG-FORMAT �NDERN UND L�SCHEN?";
   /** Message warning the user again about the impact of a log format change */
   public static final String C_msgWarningFormatAndErase2 =
       "Dies ist ihre letzte Chance" +
       "|das L�schen abzubrechen." +
       "|" +
       "|LOG-FORMAT �NDERN UND L�SCHEN?";
   /** Message warning user about impact of changing log format */
   public static final String C_msgEraseWarning =
       "Sie wollen den" +
       "|Log-Speicher l�schen." +
       "|" +
       "|LOG L�SCHEN?";
   public static final String C_msgEraseWarning2 =
       "Dies ist ihre letzte Chance" +
       "|das L�schen abzubrechen." +
       "|" +
       "|LOG L�SCHEN?";

   public static final String ONE_FILE = "Eine Datei";
   public static final String ONE_FILE_DAY = "Eine Datei/Tag";
   public static final String ONE_FILE_TRK = "Eine Datei/Track";
   public static final String DEV_LOGONOFF = "Log ein(/aus)";
   public static final String INCREMENTAL = "Incremental";

   public static final String LOG_OVRWR_FULL = "Log �berschreiben(/stop) wenn voll";
   public static final String DATE_RANGE = "Datum-Bereich";
   public static final String GET_LOG = "Log laden";
   public static final String CANCEL_GET = "Abbruch";
   public static final String NOFIX_COL = "No Fix Farbe";
   public static final String TRK_SEP = "Trk sep:";
   public static final String MIN = "min";
   public static final String UTC = "UTC";
   public static final String HGHT_GEOID_DIFF = "hght - geiod diff";
   public static final String TO_CSV = "Nach CSV";
   public static final String TO_GPX = "Nach GPX";
   public static final String TO_KML = "Nach KML";
   public static final String TO_TRK = "Nach TRK";
   public static final String TO_PLT = "Nach PLT";
   public static final String TO_GMAP= "Nach GMAP";
   public static final String TO_NMEA= "Nach NMEA";
   public static final String MEM_USED = "Speicher genutzt   : ";
   public static final String NBR_RECORDS = "Anzahl Datens�tze: ";


   //Log reason
   public static final String NO_DGPS = "Kein DGPS";
   public static final String RTCM = "RTCM";
   public static final String WAAS = "WAAS";
   public static final String RCR_TIME = "Zeit (s)    ";
   public static final String RCR_SPD  = "Geschwindigkeit (km/h)";
   public static final String RCR_DIST = "Entfernung (m)";
   public static final String FIX_PER = "Fix (ms)";
   public static final String INCL_TST_SBAS = "Inkl. Test SBAS";
   public static final String PWR_SAVE_INTRNL = "Stromsparend (Intern)";

   // NMEA OUTPUT
   public static final String DEFAULTS = "Voreinstellungen";


   // Other tabs
   public static final String TAB_FLSH = "Flsh";
   public static final String TAB_NMEA_OUT = "NMEA Ausgabe";
   public static final String TAB_NMEA_FILE = "NMEA Datei";
   public static final String TAB_HOLUX = "Holux";


   // Log convert
   public static final String ERROR = "Fehler";
   public static final String PROBLEM_READING = "Leseproblem|";
   public static final String COULD_NOT_OPEN = "Kann nicht �ffnen|";


   // GPS State
   public static final String CANCEL_WAITING = "Schreiben abgebrochen";
   public static final String TITLE_WAITING_ERASE =
       "Warten bis L�schen beendet";
   public static final String TXT_WAITING_ERASE =
       "Warten bis L�schen beendet.|" +
       "Sie k�nnen das Warten auf eigenes Risiko abbrechen";

   public static final String UNKNOWN = "Unbekannt";
   public static final String CHK_PATH =
       "|Pr�fen ob Pfad und Karte beschreibbar";

   public static final String OVERWRITE = "�berschreiben";
   public static final String ABORT_DOWNLOAD = "Laden abbrechen";
   public static final String DATA_NOT_SAME =
   "Die DATEN vom Logger stimmen NICHT mit|"
   + "den zuvor geladenen DATEN|"
   + "�berein.|"
   + "Sollen die DATEN �berschrieben werden?";
   public static final String LOGGER = "Logger: ";  // For logger SW version


   // GPSFile
   public static final String CLOSE_FAILED =
       "Schlie�en fehlgeschlagen (Datei schlie�en) - eventuell ein Bug.";
   public static final String WRITING_CLOSED =
       "Schreiben in geschlossene Datei";

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