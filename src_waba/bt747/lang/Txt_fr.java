package bt747.lang;

import bt747.Version;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public final class Txt_fr implements TxtInterface {
    private static String fontFile=null;
    private static String encoding=null;

    // BT747 class
    private static final String S_FILE = "Fichier";
    private static final String S_EXIT_APPLICATION = "Quitter";
    
    private static final String S_SETTINGS = "Options";
    private static final String S_STOP_LOGGING_ON_CONNECT = "Arr�t log apr�s conn";
    private static final String S_STOP_CONNECTION = "Arr�t connection";
    private static final String S_GPX_UTC_OFFSET_0= "GPX UTC offset 0";
    private static final String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg qnd petit";
    private static final String S_GPS_DECODE_ACTIVE= "GPS decodage actif";
    private static final String S_FOCUS_HIGHLIGHT= "Focus Highlight";
    private static final String S_DEBUG= "Debug";
    private static final String S_STATS= "Statistics";
    private static final String S_INFO= "Info";
    private static final String S_ABOUT_BT747= "A propos de BT747";
    private static final String S_ABOUT_SUPERWABA= "A propos de SuperWaba VM";
    
    private static final String S_TITLE= "BT747 Contr�le de logger MTK";

    
    private static final String LB_DOWNLOAD= "T�l�ch.";
    
    private static final String TITLE_ATTENTION = "Attention";
    private static final String CONFIRM_APP_EXIT =
        "Vous �tes sur le point de quitter"
        + "|l'application."
        + "|Vous confirmez?";
    
    private static final String YES=
        "Oui";
    private static final String NO=
        "Non";
    private static final String CANCEL=
        "Annuler";


    private static final String ABOUT_TITLE=
        "A propos de BT747 V"+Version.VERSION_NUMBER;
    private static final String ABOUT_TXT=
        "Cr�e avec SuperWaba"
        + "|http://www.superwaba.org"
        + "|" +Version.BUILD_STR
        + "|Ecrit par Mario De Weerd"
        + "|seesite@bt747.org"
        + "|Cette application permets le contr�le"
        + "|de diff�rents types de logger GPS �"
        + "|base de chipset MTK. La modification"
        + "|mat�riel peut �tre n�cessaire"
        + "|pour l'utiliser bluetooth."
        + "|Plus d'information sur le web."; 

    
    private static final String ABOUT_SUPERWABA_TITLE=
        "A propos de SuperWaba";
    private static final String ABOUT_SUPERWABA_TXT=
        "SuperWaba Virtual Machine ";
    @SuppressWarnings("unused")
    private static final String ABOUT_SUPERWABA_TXT_CONTINUE=
          "|Copyright (c)2000-2007"
        + "|Guilherme Campos Hazan"
        + "|www.superwaba.com|"
        + "|"
        + "SuperWaba is an enhanced version"
        + "|of the Waba Virtual Machine"
        + "|Copyright (c) 1998,1999 WabaSoft"
        + "|www.wabasoft.com";
    
    private static final String DISCLAIMER_TITLE=
        "Disclaimer";
    private static final String DISCLAIMER_TXT=
        "Ce logiciel est fourni par les titu-"
        + "|laires et les participants \"TEL QUEL\""
        + "|ET TOUTES LES GARANTIES EXPLICITES"
        + "|OU IMPLICITES, Y COMPRIS, MAIS NON"
        + "|LIMIT�ES A CELLES-CI, LES GARANTIES"
        + "|IMPLICITES DE COMMERCIALISATION"
        + "|ET D\'AD�QUATION � UN USAGE"
        + "|PARTICULIER SONT D�NI�ES. EN AUCUN"
        + "|CAS LES TITULAIRES DU COPYRIGHT"
        + "|OU LES PARTICIPANTS NE PEUVENT"
        + "|�TRE TENUS POUR RESPONSABLE DES"
        + "|DOMMAGES DIRECTS, INDIRECTS,"
        + "|FORTUITS, PARTICULIERS, EXEMPLAIRES"
        + "|OU CONS�CUTIFS � UNE ACTION (Y"
        + "|COMPRIS, MAIS NON LIMIT�S � CEUX"
        + "|-CI, L'ACQUISITION DE MARCHANDISES"
        + "|OU DE SERVICES DE REMPLACEMENT; "
        + "|LES PERTES D'UTILISATIONS, DE"
        + "|DONN�ES OU FINANCI�RES; OU L'"
        + "|INTERRUPTION D'ACTIVIT�S) DE"
        + "|QUELQUE MANI�RE QUE CES DOMMAGES"
        + "|SOIENT CAUS�S ET CECI POUR"
        + "|TOUTES LES TH�ORIES DE"
        + "|RESPONSABILIT�S, QUE CE SOIT"
        + "|DANS UN CONTRAT, POUR DES"
        + "|RESPONSABILIT�S STRICTES OU"
        + "|DES PR�JUDICES (Y COMPRIS DUS"
        + "|� UNE N�GLIGENCE OU AUTRE CHOSE)"
        + "|SURVENANT DE QUELQUE"
        + "|MANI�RE QUE CE SOIT EN DEHORS DE"
        + "|L'UTILISATION DE CE LOGICIEL, M�ME"
        + "|EN CAS D\'AVERTISSEMENT DE LA"
        + "|POSSIBILIT� DE TELS DOMMAGES.";

    // TAB identification
    private static final String C_FMT  = "Fmt";
    private static final String C_CTRL = "Ctrl";
    private static final String C_LOG  = "Log";
    private static final String C_FILE = "Fich";
    private static final String C_FLTR = "Fltr";
    private static final String C_EASY = "Div";
    private static final String C_CON  = "Con";
    private static final String C_OTHR = "Autre";

    // Conctrl strings
    private static final String BT_BLUETOOTH = "Bluetooth";
    private static final String BT_CONNECT_PRT = "Connection port";
    private static final String BT_CLOSE_PRT = "Fermer port";
    private static final String BT_REOPEN_PRT  = "(Re)Ouvrir port";
    private static final String MAIN = "Principal:";
    private static final String FIRMWARE = "Firmware:";
    private static final String MODEL = "Mod�le:";
    private static final String FLASHINFO = "InfoFlash:";
    private static final String TIME_SEP = "  - Heure:";
    private static final String LAT = "Lat:";
    private static final String LON = "Lon:";
    private static final String GEOID = "Geoid:";
    private static final String CALC = "(calc:";
    private static final String HGHT_SEP = " - Hautr:";
    
    // Filters tab panel
    private static final String STANDARD = "Standard";
    private static final String ADVANCED = "Avanc�";

    
    // BT747_dev class
    private static final String[]C_STR_RCR = {
            "Temps", "Vitesse", "Distance", "Bouton",
            "Photo", "Essence", "T�l�ph", "CB",
            "Arr�tBus", "Parking", "Poste", "Train",
            "Resto", "Pont", "Vue", "Autre"
            };
    private static final String [] logFmtItems = {
        "UTC",      // = 0x00001    // 0
        "VALID",    // = 0x00002    // 1
        "LATITUDE", // = 0x00004    // 2
        "LONGITUDE",// = 0x00008    // 3
        "HAUTEUR",   // = 0x00010    // 4
        "VITESSE",    // = 0x00020    // 5
        "DIRECTION",  // = 0x00040    // 6
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
        "RAISON",      // = 0x20000    // 11
        "MILLISECONDE",// = 0x40000   // 12
        "DISTANCE",  // = 0x80000    // 13
        "POINT VALIDES" // =0x80000000
    };
    private static final String C_BAD_LOG_FORMAT = "Mauvais format log";
    

    // Holux specific
    private static final String HOLUX_NAME = "Nom du Holux";

    private static final String SET = "SET";

    // EASY TAB
    private static final String BT_5HZ_FIX = "5Hz fix & log";
    private static final String BT_2HZ_FIX = "2Hz fix";
    private static final String BT_HOT = "D�m.chaud";
    private static final String BT_WARM = "D�m.ti�de";
    private static final String BT_COLD = "D�m.froid";
    private static final String BT_FACT_RESET = "R�init.usine";
    private static final String BT_FORCED_ERASE = "Effacement m�m. usine";
    private static final String BT_PT_WITH_REASON = "Cliquez pour logger un point:";

    private static final String CONFIRM_FACT_RESET =
            "Vous �tes sur le point de r�aliser"
            + "|une r�initialisation usine du"
            + "|Chipset MTK."
            + "||Confirmez-vous cette op�ration"
            + "|� votre risque ???";    
    
    // File tab
    private static final String OUTPUT_DIR = "R�p.sortie:";
    private static final String LOGFILE = "Fich.Log:";
    private static final String REPORT = "Rapport:";
    private static final String CHUNK = "Block :";
    private static final String CHUNK_AHEAD = "Nbr.de blocks avance:";
    private static final String READ_TIMEOUT = "Timeout (ms) :";
    private static final String CARD_VOL = "Carte/Volume:";
    private static final String APPLY_SET = "Appliquer ces valeurs";
    private static final String DEFAULT_SET = "Valeurs par d�faut";

    // Log filter
    private static final String[] STR_VALID= {
            "Sans fix",
            "SPS",
            "DGPS",
            "PPS",
            "RTK",
            "FRTK",
            "Estimation",
            "Manuel",
            "Sim"};

    private static final String TRKPT = "TrkPt";
    private static final String WAYPT = "WayPt";
    
    // Advanced log filter
    private static final String ACTIVE = "ACTIF";
    private static final String INACTIVE = "INACTIF";
    private static final String FLTR_REC = "<= record Nbr <= ";
    private static final String FLTR_SPD = "<= vitesse <= ";
    private static final String FLTR_DST = "<= distance <= ";
    private static final String FLTR_PDOP = "PDOP <= ";
    private static final String FLTR_HDOP = "HDOP <= ";
    private static final String FLTR_VDOP = "VDOP <= ";
    private static final String FLTR_NSAT = "<= NSAT";
    
    private static final String CLEAR = "MISE A ZERO";
    
    // Log format
    private static final String REC_ESTIMATED = " points maximum";
    private static final String SET_ERASE = "OK&Effacer m�m.";
    private static final String SET_NOERASE = "OK";
    private static final String ERASE = "Effacer m�m.";
    private static final String CONFIRM_ERASE = "Effacer";
    
    private static final String C_msgWarningFormatIncompatibilityRisk =
        "Vous �tes sur le point de changer le format de"
        + " votre appareil sans effacer la m�moire."
        + " C'est incompatible avec d'autres logiciels"
        + "||Vous �tes d'accord avec cette incompatibilit�?";
    
    /** Message warning user about impact of changing log format */
    private static final String C_msgWarningFormatAndErase = 
        "Vous �tes sur le point de red�finir le"
        + "|format des points de votre appareil"
        + "|et"
        + "|d'effacer le contenu de la m�moire"
        + "|"
        + "|CHANGER LE FORMAT & EFFACER?";
    /** Message warning the user again about the impact of a log format change */           
    private static final String C_msgWarningFormatAndErase2 =
        "Ceci est votre derni�re chance"
        + "|d'annuler la remise � z�ro"
        + "|de la m�moire."
        + "|"
        + "|CHANGER LE FORMAT & EFFACER?";
    /** Message warning user about impact of changing log format */
    private static final String C_msgEraseWarning = 
        "Vous aller effacer la m�moire"
        + "|"
        + "|EFFACER?";
    private static final String C_msgEraseWarning2 =
        "Ceci est la derni�re chance pour eviter"
        + "|d'effacer la m�moire."
        + "|"
        + "|EFFACER?";

    private static final String ONE_FILE = "Un fichier";
    private static final String ONE_FILE_DAY = "Un fichier/ jr";
    private static final String ONE_FILE_TRK = "1 fichier/ trk";
    private static final String DEV_LOGONOFF = "Logger actif(inactf)";
    private static final String INCREMENTAL = "Incr�mentale";
    
    private static final String LOG_OVRWR_FULL = "Ecraser log (/arr�t) quand plein";
    private static final String DATE_RANGE = "P�riode";
    private static final String GET_LOG = "T�l�ch.";
    private static final String CANCEL_GET = "Annuler";
    private static final String NOFIX_COL = "Coulr sns fix";
    private static final String TRK_SEP = "Trk s�p:";
    private static final String MIN = "min";
    private static final String UTC = "UTC";
    private static final String HGHT_GEOID_DIFF = "Correction hauteur";
    private static final String TO_CSV = "-> CSV";
    private static final String TO_GPX = "-> GPX";
    private static final String TO_KML = "-> KML";
    private static final String TO_TRK = "-> TRK";
    private static final String TO_PLT = "-> PLT";
    private static final String TO_GMAP= "-> GMAP";
    private static final String TO_NMEA= "-> NMEA";
    private static final String MEM_USED = "M�moire utl: ";
    private static final String NBR_RECORDS = "Nbre points: ";
    
    
    //Log reason
    private static final String NO_DGPS = "Sns DGPS";
    private static final String RTCM = "RTCM";
    private static final String WAAS = "WAAS";
    private static final String RCR_TIME = "Temps (s)    ";
    private static final String RCR_SPD  = "Vitesse (km/h)";
    private static final String RCR_DIST = "Distance (m)";
    private static final String FIX_PER = "Fix (ms)";
    private static final String INCL_TST_SBAS = "Incl. Test SBAS";
    private static final String PWR_SAVE_INTRNL = "Power Save (Internal)";

    // NMEA OUTPUT
    private static final String DEFAULTS = "Val. d�faut";
    
    
    // Other tabs
    private static final String TAB_FLSH = "Flsh";
    private static final String TAB_NMEA_OUT = "NMEA app.";
    private static final String TAB_NMEA_FILE = "NMEA Fich";
    private static final String TAB_HOLUX = "Holux";
    
    
    // Log convert
    private static final String ERROR = "Erreur";
    private static final String PROBLEM_READING = "Probl�me de lecture|";
    private static final String COULD_NOT_OPEN = "Impossible d'ouvrir|";
    
    
    // GPS State
    private static final String CANCEL_WAITING = "Annuler attente";
    private static final String TITLE_WAITING_ERASE =
        "Attente de la remise � z�ro";
    private static final String TXT_WAITING_ERASE =
        "Attente de la remise � z�ro|"
        + "Vous pouvez annuler � votre risque";
    
    private static final String UNKNOWN = "Inconnue";
    private static final String CHK_PATH =
        "|V�rifiez le chemin et"
        + "|si la carte peut �tre �crit";
    
    private static final String OVERWRITE = "Ecraser";
    private static final String ABORT_DOWNLOAD = "Annuler t�l�ch.";
    private static final String DATA_NOT_SAME = 
    "Les donn�es dans la m�moire"
    + "|ne correspondent au dernier"
    + "|t�l�chargment"
    + "|Souhaitez-vous �craser le fichier?";
    private static final String LOGGER = "Logger: ";  // For logger SW version
    
    
    // GPSFile
    private static final String CLOSE_FAILED =
        "Probl�me de fermeture - probablement un bug.";
    private static final String WRITING_CLOSED =
        "Ecriture de fichier ferm�";
        
    // Flash option
    private static final String TIMESLEFT = "Nbre d'essais";
    private static final String UPDATERATE = "Fr�quence MAJ (Hz)";
    private static final String BAUDRATE = "Baud Rate";
    private static final String WRITEFLASH = "Ecrire Flash";
    private static final String ABORT = "Annuler";
    private static final String TXT_FLASH_LIMITED_WRITES=
        "Le nombre d'�critures dans la flash"
        + "est limit� et un changement de|"
        + "ces options peut rendre l'appareil|"
        + "inutilisable (changement du baud rate)."
        + "ARRETEZ en selectionnant annulez!!";
    private static final String PERIOD_ABBREV = "P�r";
    
    // Forgotton in Advanced track filter
    private static final String IGNORE_0VALUES
        = "Les z�ros sont ignor�s";

    private static final String METERS_ABBR = "m";

    private static final String STORE_SETTINGS =
        "Sauvegarde conf.";
    private static final String RESTORE_SETTINGS =
        "Restaurer conf.";
    
    private static final String WARNING =
        "Alerte";
    private static final String NO_FILES_WERE_CREATED =
        "Aucun fichier de sortie!"
        + "||"
        + "Habituellement les causes sont:"
        + "|- Les filtres son trop strictes"
        + "|- Il n'y a pas de donn�es dans le log"
        + "|"
        + "|Essayez la selection de tous les points"
        + "|Si cela ne donne pas de succ�s,"
        + "|il y a peut-�tre un bug";
    private static final String ADD_RECORD_NUMBER =
        "Record nbr info in logs";    

    private static final String S_DEBUG_CONN= "Debug conn.";
    private static final String S_IMPERIAL= "Unit�s anglaises";
    private static final String BAD_SUPERWABAVERSION =
        "This application was built for|SuperWaba V";
    private static final String BAD_SUPERWABAVERSION_CONT =
        ".|This version is V";
    private static final String BAD_SUPERWABAVERSION_CONT2 =
        ".|Exiting application";

    private static final String S_DEVICE = "GPS";
    private static final String S_DEFAULTDEVICE = "Par d�faut";
    private static final String S_GISTEQTYPE1 = "iTrackU-Nemerix";
    private static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr";
    private static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII";
    
    private static final String BT_MAC_ADDR = "BT Mac Addr:";
    private static final String S_OUTPUT_LOGCONDITIONS = "Output log conditions";
    private static final String MEM_FREE = "libre";

    private static final String TRKPTCOMMENT = "TRK PT INFO";
    private static final String TRKPTNAME = "TRK PT NAME";

    private static final String DOWNLOAD_INCREMENTAL = "T�l�chmt incr.";
    private static final String DOWNLOAD_FULL = "T�l�chmt cmplt";
    private static final String DOWNLOAD_NORMAL = "T�l�chmt nrml";

    private static final String HEIGHT_CONV_AUTOMATIC = "Alti Auto";
    private static final String HEIGHT_CONV_MSL_TO_WGS84 = "-> WGS84";
    private static final String HEIGHT_CONV_WGS84_TO_MSL = "-> MSL";
    private static final String HEIGHT_CONV_NONE = "Alti orig";

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
            DOWNLOAD_INCREMENTAL, DOWNLOAD_FULL, DOWNLOAD_NORMAL,
            null, null, null, null, null, null, null, null , null, 
            null, /* MI_LANGUAGE */
            HEIGHT_CONV_AUTOMATIC,
            HEIGHT_CONV_MSL_TO_WGS84, HEIGHT_CONV_WGS84_TO_MSL,
            HEIGHT_CONV_NONE,};

}


