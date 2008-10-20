package bt747.lang;

import bt747.Version;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public class Txt_fr {
    public static String fontFile=null;
    public static String encoding=null;

    // BT747 class
    public static final String S_FILE = "Fichier";
    public static final String S_EXIT_APPLICATION = "Quitter";
    
    public static final String S_SETTINGS = "Options";
    public static final String S_STOP_LOGGING_ON_CONNECT = "Arrêt log après conn";
    public static final String S_STOP_CONNECTION = "Arrêt connection";
    public static final String S_GPX_UTC_OFFSET_0= "GPX UTC offset 0";
    public static final String S_GPX_TRKSEG_WHEN_SMALL = "GPX Trkseg qnd petit";
    public static final String S_GPS_DECODE_ACTIVE= "GPS decodage actif";
    public static final String S_FOCUS_HIGHLIGHT= "Focus Highlight";
    public static final String S_DEBUG= "Debug";
    public static final String S_STATS= "Statistics";
    public static final String S_INFO= "Info";
    public static final String S_ABOUT_BT747= "A propos de BT747";
    public static final String S_ABOUT_SUPERWABA= "A propos de SuperWaba VM";
    
    public static final String S_TITLE= "BT747 Contrôle de logger MTK";

    
    public static final String LB_DOWNLOAD= "Téléch.";
    
    public static final String TITLE_ATTENTION = "Attention";
    public static final String CONFIRM_APP_EXIT =
        "Vous êtes sur le point de quitter"
        + "|l'application."
        + "|Vous confirmez?";
    
    public static final String YES=
        "Oui";
    public static final String NO=
        "Non";
    public static final String CANCEL=
        "Annuler";


    public static final String ABOUT_TITLE=
        "A propos de BT747 V"+Version.VERSION_NUMBER;
    public static final String ABOUT_TXT=
        "Crée avec SuperWaba"
        + "|http://www.superwaba.org"
        + "|" +Version.BUILD_STR
        + "|Ecrit par Mario De Weerd"
        + "|m.deweerd@ieee.org"
        + "|Cette application permets le contrôle"
        + "|de différents types de logger GPS à"
        + "|base de chipset MTK. La modification"
        + "|matériel peut être nécessaire"
        + "|pour l'utiliser bluetooth."
        + "|Plus d'information sur le web."; 

    
    public static final String ABOUT_SUPERWABA_TITLE=
        "A propos de SuperWaba";
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
        "Ce logiciel est fourni par les titu-"
        + "|laires et les participants \"TEL QUEL\""
        + "|ET TOUTES LES GARANTIES EXPLICITES"
        + "|OU IMPLICITES, Y COMPRIS, MAIS NON"
        + "|LIMITÉES A CELLES-CI, LES GARANTIES"
        + "|IMPLICITES DE COMMERCIALISATION"
        + "|ET D\'ADÉQUATION À UN USAGE"
        + "|PARTICULIER SONT DÉNIÉES. EN AUCUN"
        + "|CAS LES TITULAIRES DU COPYRIGHT"
        + "|OU LES PARTICIPANTS NE PEUVENT"
        + "|ÊTRE TENUS POUR RESPONSABLE DES"
        + "|DOMMAGES DIRECTS, INDIRECTS,"
        + "|FORTUITS, PARTICULIERS, EXEMPLAIRES"
        + "|OU CONSÉCUTIFS À UNE ACTION (Y"
        + "|COMPRIS, MAIS NON LIMITÉS À CEUX"
        + "|-CI, L'ACQUISITION DE MARCHANDISES"
        + "|OU DE SERVICES DE REMPLACEMENT; "
        + "|LES PERTES D'UTILISATIONS, DE"
        + "|DONNÉES OU FINANCIÈRES; OU L'"
        + "|INTERRUPTION D'ACTIVITÉS) DE"
        + "|QUELQUE MANIÈRE QUE CES DOMMAGES"
        + "|SOIENT CAUSÉS ET CECI POUR"
        + "|TOUTES LES THÉORIES DE"
        + "|RESPONSABILITÉS, QUE CE SOIT"
        + "|DANS UN CONTRAT, POUR DES"
        + "|RESPONSABILITÉS STRICTES OU"
        + "|DES PRÉJUDICES (Y COMPRIS DUS"
        + "|À UNE NÉGLIGENCE OU AUTRE CHOSE)"
        + "|SURVENANT DE QUELQUE"
        + "|MANIÈRE QUE CE SOIT EN DEHORS DE"
        + "|L'UTILISATION DE CE LOGICIEL, MÊME"
        + "|EN CAS D\'AVERTISSEMENT DE LA"
        + "|POSSIBILITÉ DE TELS DOMMAGES.";

    // TAB identification
    public static final String C_FMT  = "Fmt";
    public static final String C_CTRL = "Ctrl";
    public static final String C_LOG  = "Log";
    public static final String C_FILE = "Fich";
    public static final String C_FLTR = "Fltr";
    public static final String C_EASY = "Div";
    public static final String C_CON  = "Con";
    public static final String C_OTHR = "Autre";

    // Conctrl strings
    public static final String BT_BLUETOOTH = "Bluetooth";
    public static final String BT_CONNECT_PRT = "Connection port";
    public static final String BT_CLOSE_PRT = "Fermer port";
    public static final String BT_REOPEN_PRT  = "(Re)Ouvrir port";
    public static final String MAIN = "Principal:";
    public static final String FIRMWARE = "Firmware:";
    public static final String MODEL = "Modèle:";
    public static final String FLASHINFO = "InfoFlash:";
    public static final String TIME_SEP = "  - Heure:";
    public static final String LAT = "Lat:";
    public static final String LON = "Lon:";
    public static final String GEOID = "Geoid:";
    public static final String CALC = "(calc:";
    public static final String HGHT_SEP = " - Hautr:";
    
    // Filters tab panel
    public static final String STANDARD = "Standard";
    public static final String ADVANCED = "Avancé";

    
    // BT747_dev class
    public static final String[]C_STR_RCR = {
            "Temps", "Vitesse", "Distance", "Bouton",
            "Photo", "Essence", "Téléph", "CB",
            "ArrêtBus", "Parking", "Poste", "Train",
            "Resto", "Pont", "Vue", "Autre"
            };
    public static final String [] logFmtItems = {
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
    public static final String C_BAD_LOG_FORMAT = "Mauvais format log";
    

    // Holux specific
    public static final String HOLUX_NAME = "Nom du Holux";

    public static final String SET = "SET";

    // EASY TAB
    public static final String BT_5HZ_FIX = "5Hz fix & log";
    public static final String BT_2HZ_FIX = "2Hz fix";
    public static final String BT_HOT = "Dém.chaud";
    public static final String BT_WARM = "Dém.tiède";
    public static final String BT_COLD = "Dém.froid";
    public static final String BT_FACT_RESET = "Réinit.usine";
    public static final String BT_FORCED_ERASE = "Effacement mém. usine";
    public static final String BT_PT_WITH_REASON = "Cliquez pour logger un point:";

    public static final String CONFIRM_FACT_RESET =
            "Vous êtes sur le point de réaliser"
            + "|une réinitialisation usine du"
            + "|Chipset MTK."
            + "||Confirmez-vous cette opération"
            + "|à votre risque ???";    
    
    // File tab
    public static final String OUTPUT_DIR = "Rép.sortie:";
    public static final String LOGFILE = "Fich.Log:";
    public static final String REPORT = "Rapport:";
    public static final String CHUNK = "Block :";
    public static final String CHUNK_AHEAD = "Nbr.de blocks avance:";
    public static final String READ_TIMEOUT = "Timeout (ms) :";
    public static final String CARD_VOL = "Carte/Volume:";
    public static final String APPLY_SET = "Appliquer ces valeurs";
    public static final String DEFAULT_SET = "Valeurs par défaut";

    // Log filter
    public static final String[] STR_VALID= {
            "Sans fix",
            "SPS",
            "DGPS",
            "PPS",
            "RTK",
            "FRTK",
            "Estimation",
            "Manuel",
            "Sim"};

    public static final String TRKPT = "TrkPt";
    public static final String WAYPT = "WayPt";
    
    // Advanced log filter
    public static final String ACTIVE = "ACTIF";
    public static final String INACTIVE = "INACTIF";
    public static final String FLTR_REC = "<= record Nbr <= ";
    public static final String FLTR_SPD = "<= vitesse <= ";
    public static final String FLTR_DST = "<= distance <= ";
    public static final String FLTR_PDOP = "PDOP <= ";
    public static final String FLTR_HDOP = "HDOP <= ";
    public static final String FLTR_VDOP = "VDOP <= ";
    public static final String FLTR_NSAT = "<= NSAT";
    
    public static final String CLEAR = "MISE A ZERO";
    
    // Log format
    public static final String REC_ESTIMATED = " points maximum";
    public static final String SET_ERASE = "OK&Effacer mém.";
    public static final String SET_NOERASE = "OK";
    public static final String ERASE = "Effacer mém.";
    public static final String CONFIRM_ERASE = "Effacer";
    
    public static final String C_msgWarningFormatIncompatibilityRisk =
        "Vous êtes sur le point de changer le format de"
        + " votre appareil sans effacer la mémoire."
        + " C'est incompatible avec d'autres logiciels"
        + "||Vous êtes d'accord avec cette incompatibilité?";
    
    /** Message warning user about impact of changing log format */
    public static final String C_msgWarningFormatAndErase = 
        "Vous êtes sur le point de redéfinir le"
        + "|format des points de votre appareil"
        + "|et"
        + "|d'effacer le contenu de la mémoire"
        + "|"
        + "|CHANGER LE FORMAT & EFFACER?";
    /** Message warning the user again about the impact of a log format change */           
    public static final String C_msgWarningFormatAndErase2 =
        "Ceci est votre dernière chance"
        + "|d'annuler la remise à zéro"
        + "|de la mémoire."
        + "|"
        + "|CHANGER LE FORMAT & EFFACER?";
    /** Message warning user about impact of changing log format */
    public static final String C_msgEraseWarning = 
        "Vous aller effacer la mémoire"
        + "|"
        + "|EFFACER?";
    public static final String C_msgEraseWarning2 =
        "Ceci est la dernière chance pour eviter"
        + "|d'effacer la mémoire."
        + "|"
        + "|EFFACER?";

    public static final String ONE_FILE = "Un fichier";
    public static final String ONE_FILE_DAY = "Un fichier/ jr";
    public static final String ONE_FILE_TRK = "1 fichier/ trk";
    public static final String DEV_LOGONOFF = "Logger actif(inactf)";
    public static final String INCREMENTAL = "Incrémentale";
    
    public static final String LOG_OVRWR_FULL = "Ecraser log (/arrêt) quand plein";
    public static final String DATE_RANGE = "Période";
    public static final String GET_LOG = "Téléch.";
    public static final String CANCEL_GET = "Annuler";
    public static final String NOFIX_COL = "Coulr sns fix";
    public static final String TRK_SEP = "Trk sép:";
    public static final String MIN = "min";
    public static final String UTC = "UTC";
    public static final String HGHT_GEOID_DIFF = "Correction hauteur";
    public static final String TO_CSV = "-> CSV";
    public static final String TO_GPX = "-> GPX";
    public static final String TO_KML = "-> KML";
    public static final String TO_TRK = "-> TRK";
    public static final String TO_PLT = "-> PLT";
    public static final String TO_GMAP= "-> GMAP";
    public static final String TO_NMEA= "-> NMEA";
    public static final String MEM_USED = "Mémoire utl: ";
    public static final String NBR_RECORDS = "Nbre points: ";
    
    
    //Log reason
    public static final String NO_DGPS = "Sns DGPS";
    public static final String RTCM = "RTCM";
    public static final String WAAS = "WAAS";
    public static final String RCR_TIME = "Temps (s)    ";
    public static final String RCR_SPD  = "Vitesse (km/h)";
    public static final String RCR_DIST = "Distance (m)";
    public static final String FIX_PER = "Fix (ms)";
    public static final String INCL_TST_SBAS = "Incl. Test SBAS";
    public static final String PWR_SAVE_INTRNL = "Power Save (Internal)";

    // NMEA OUTPUT
    public static final String DEFAULTS = "Val. défaut";
    
    
    // Other tabs
    public static final String TAB_FLSH = "Flsh";
    public static final String TAB_NMEA_OUT = "NMEA app.";
    public static final String TAB_NMEA_FILE = "NMEA Fich";
    public static final String TAB_HOLUX = "Holux";
    
    
    // Log convert
    public static final String ERROR = "Erreur";
    public static final String PROBLEM_READING = "Problème de lecture|";
    public static final String COULD_NOT_OPEN = "Impossible d'ouvrir|";
    
    
    // GPS State
    public static final String CANCEL_WAITING = "Annuler attente";
    public static final String TITLE_WAITING_ERASE =
        "Attente de la remise à zéro";
    public static final String TXT_WAITING_ERASE =
        "Attente de la remise à zéro|"
        + "Vous pouvez annuler à votre risque";
    
    public static final String UNKNOWN = "Inconnue";
    public static final String CHK_PATH =
        "|Vérifiez le chemin et"
        + "|si la carte peut être écrit";
    
    public static final String OVERWRITE = "Ecraser";
    public static final String ABORT_DOWNLOAD = "Annuler téléch.";
    public static final String DATA_NOT_SAME = 
    "Les données dans la mémoire"
    + "|ne correspondent au dernier"
    + "|téléchargment"
    + "|Souhaitez-vous écraser le fichier?";
    public static final String LOGGER = "Logger: ";  // For logger SW version
    
    
    // GPSFile
    public static final String CLOSE_FAILED =
        "Problème de fermeture - probablement un bug.";
    public static final String WRITING_CLOSED =
        "Ecriture de fichier fermé";
        
    // Flash option
    public static final String TIMESLEFT = "Nbre d'essais";
    public static final String UPDATERATE = "Fréquence MAJ (Hz)";
    public static final String BAUDRATE = "Baud Rate";
    public static final String WRITEFLASH = "Ecrire Flash";
    public static final String ABORT = "Annuler";
    public static final String TXT_FLASH_LIMITED_WRITES=
        "Le nombre d'écritures dans la flash"
        + "est limité et un changement de|"
        + "ces options peut rendre l'appareil|"
        + "inutilisable (changement du baud rate)."
        + "ARRETEZ en selectionnant annulez!!";
    public static final String PERIOD_ABBREV = "Pér";
    
    // Forgotton in Advanced track filter
    public static final String IGNORE_0VALUES
        = "Les zéros sont ignorés";

    public static final String METERS_ABBR = "m";

    public static final String STORE_SETTINGS =
        "Sauvegarde conf.";
    public static final String RESTORE_SETTINGS =
        "Restaurer conf.";
    
    public static final String WARNING =
        "Alerte";
    public static final String NO_FILES_WERE_CREATED =
        "Aucun fichier de sortie!"
        + "||"
        + "Habituellement les causes sont:"
        + "|- Les filtres son trop strictes"
        + "|- Il n'y a pas de données dans le log"
        + "|"
        + "|Essayez la selection de tous les points"
        + "|Si cela ne donne pas de succès,"
        + "|il y a peut-être un bug";
    public static final String ADD_RECORD_NUMBER =
        "Record nbr info in logs";    

    public static final String S_DEBUG_CONN= "Debug conn.";
    public static final String S_IMPERIAL= "Unités anglaises";
    public static final String BAD_SUPERWABAVERSION =
        "This application was built for|SuperWaba V";
    public static final String BAD_SUPERWABAVERSION_CONT =
        ".|This version is V";
    public static final String BAD_SUPERWABAVERSION_CONT2 =
        ".|Exiting application";

    public static final String S_DEVICE = "Appareil";
    public static final String S_DEFAULTDEVICE = "Par defaut";
    public static final String S_GISTEQTYPE1 = "iTrackU-Nemerix";
    public static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr";
    public static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII";
    
    public static final String BT_MAC_ADDR = "BT Mac Addr:";
    public static final String S_OUTPUT_LOGCONDITIONS = "Output log conditions";
    public static final String MEM_FREE = "libre";

    public static final String TRKPTCOMMENT = "TRK PT INFO";
    public static final String TRKPTNAME = "TRK PT NAME";

    public static final String DOWNLOAD_INCREMENTAL = "Téléchmt incr.";
    public static final String DOWNLOAD_FULL = "Téléchmt cmplt";
    public static final String DOWNLOAD_NORMAL = "Téléchmt nrml";

}


