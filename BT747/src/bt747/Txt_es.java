package bt747;

import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 *
 ** Class to provide language specific strings.
 */
public class Txt_es {
    
    // BT747 class
    public final static String S_FILE = "Archivo";
    public final static String S_EXIT_APPLICATION = "Salir";
    
    public final static String S_SETTINGS = "Configuración";
    public final static String S_RESTART_CONNECTION = "Reiniciar conexión";
    public final static String S_STOP_CONNECTION = "Detener conexión";
    public final static String S_GPX_UTC_OFFSET_0= "Fijar UTC a 0 en GPX";
    public final static String S_GPX_TRKSEG_WHEN_SMALL = "No dividir tracks GPX";
    public final static String S_GPS_DECODE_ACTIVE= "Activar decodificación GPS";
    public final static String S_FOCUS_HIGHLIGHT= "Auto posicionarse en selección";
    public final static String S_DEBUG= "Mensajes adicionales";
    public final static String S_STATS= "Estadísticas";
    public final static String S_INFO= "Info";
    public final static String S_ABOUT_BT747= "Acerca de BT747";
    public final static String S_ABOUT_SUPERWABA= "Acerca de SuperWaba VM";
    
    public final static String S_TITLE= "BT747 - Control de Logger MTK";

    
    public final static String LB_DOWNLOAD= "Descargar";
    
    public final static String TITLE_ATTENTION = "Atención";
    public final static String CONFIRM_APP_EXIT = "Vas a cerrar la aplicación|" + 
                                                  "¿Salir del programa?";
    
    public final static String YES=
        "Si";
    public final static String NO=
        "No";
    public final static String CANCEL=
        "Cancelar";


    public final static String ABOUT_TITLE=
        "Acerca de BT747 V"+Version.VERSION_NUMBER;
    public final static String ABOUT_TXT=
        "Creado con SuperWaba" + 
        "|http://www.superwaba.org"+ 
        "|" +Version.BUILD_STR + 
        "|creado por Mario De Weerd" + 
        "|m.deweerd@ieee.org"+ 
        "|"+ 
        "|Esta aplicación permite controlar" + 
        "|dispositivos BT747." + 
        "|Permite un control total por bluetooth" + 
        "|aplicando un hardware hack.  " + 
        "|Puedes encontrar información en la web."; 

    
    public final static String ABOUT_SUPERWABA_TITLE=
        "Acerca de SuperWaba";
    public final static String ABOUT_SUPERWABA_TXT=
        "SuperWaba Virtual Machine "+ Settings.versionStr + 
        "|Copyright (c)2000-2007" + 
        "|Guilherme Campos Hazan" + 
        "|www.superwaba.com|" + 
        "|" + 
        "SuperWaba es una versión mejorada" + 
        "|de la máquina virtual Waba" + 
        "|Copyright (c) 1998,1999 WabaSoft" + 
        "|www.wabasoft.com";
    
    public final static String DISCLAIMER_TITLE=
        "Renuncia";
    public final static String DISCLAIMER_TXT=
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

    // TAB identification
    public final static String C_FMT  = "FMT";
    public final static String C_CTRL = "Ctrl";
    public final static String C_LOG  = "Log";
    public final static String C_FILE = "Archivo";
    public final static String C_FLTR = "Fltr";
    public final static String C_EASY = "Fácil";
    public final static String C_CON  = "Con";
    public final static String C_OTHR = "Otros";

    // Conctrl strings
    public final static String BT_BLUETOOTH = "BLUETOOTH";
    public final static String BT_CONNECT_PRT = "Conectar con puerto núm";
    public final static String BT_CLOSE_PRT = "Cerrar puerto";
    public final static String BT_REOPEN_PRT  = "(Re)abrir puerto";
    public final static String MAIN = "Principal:";
    public final static String FIRMWARE = "Firmware:";
    public final static String MODEL = "Modelo:";
    public final static String FLASHINFO = "InfoFlash:";
    public final static String TIME_SEP = "  - Hora:";
    public final static String LAT = "Lat:";
    public final static String LON = "Lon:";
    public final static String GEOID = "Geoid:";
    public final static String CALC = "(calc:";
    public final static String HGHT_SEP = " - Alt:";
    
    // Filters tab panel
    public final static String STANDARD = "Standard";
    public final static String ADVANCED = "Avanzado";

    
    // BT747_dev class
    public static final String[]C_STR_RCR = {
            "Tiempo", "Velocidad", "Distancia", "Botón",
            "App1","App2","App3","App4",
            "App5","App6","App7","App8",
            "App9","AppX","AppY","AppZ"
            };
    public static final String [] logFmtItems = {
        "UTC",      // = 0x00001    // 0
        "FIJADO",   // = 0x00002    // 1
        "LATITUD",  // = 0x00004    // 2
        "LONGITUD", // = 0x00008    // 3
        "ALTURA",   // = 0x00010    // 4
        "VELOCIDAD",// = 0x00020    // 5
        "RUMBO",    // = 0x00040    // 6
        "DSTA",     // = 0x00080    // 7
        "DAGE",     // = 0x00100    // 8
        "PDOP",     // = 0x00200    // 9
        "HDOP",     // = 0x00400    // A
        "VDOP",     // = 0x00800    // B
        "NSAT",     // = 0x01000    // C
        "SID",      // = 0x02000    // D
        "ELEVACION",// = 0x04000    // E
        "AZIMUT",   // = 0x08000    // F
        "SNR",      // = 0x10000    // 10
        "RCR",      // = 0x20000    // 11
        "MILISEGUNDOS",// = 0x40000   // 12
        "DISTANCIA",  // = 0x80000    // 13
        "HOLUX M-241" // =0x80000000
    };
    public static final String C_BAD_LOG_FORMAT = "Formato log incorrecto";
    

    // Holux specific
    public static final String HOLUX_NAME = "Nombre Holux";

    public static final String SET = "Guardar";

    // EASY TAB
    public static final String BT_5HZ_FIX = "Fijar y grabar a 5Hz";
    public static final String BT_2HZ_FIX = "Fijar a 2Hz";
    public static final String BT_HOT = "Inicio en caliente";
    public static final String BT_WARM = "Inicio templado";
    public static final String BT_COLD = "Inicio en frío";
    public static final String BT_FACT_RESET = "Valores de fábrica";
    public static final String BT_FORCED_ERASE = "Forzar borrado";
    public static final String BT_PT_WITH_REASON = "Click para grabar un punto por...";

    public static final String CONFIRM_FACT_RESET =
            "Vas a volver a los valores de|" +
            "fábrica de tu GPS DataLogger.|"+
            "||¿Confirmas este cambio bajo|"+
            "tu responsabilidad ?";    
    
    // File tab
    public static final String OUTPUT_DIR = "Guardar en:";
    public static final String LOGFILE = "Archivo Log:";
    public static final String REPORT = "Exportados :";
    public static final String CHUNK = "Paquetes :";
    public static final String CHUNK_AHEAD = "Segmentar paquetes:";
    public static final String READ_TIMEOUT = "Tiempo max. lectura (ms) :";
    public static final String CARD_VOL = "Tarjeta/Volumen:";
    public static final String APPLY_SET = "Guardar y Aplicar los ajustes";
    public static final String DEFAULT_SET = "Config por defecto";

    // Log filter
    public static final String[] STR_VALID= {
            "Sin fijar",
            "SPS",
            "DGPS",
            "PPS",
            "RTK",
            "FRTK",
            "Estimado",
            "Manual",
            "Sim"};

    public static final String TRKPT = "TrkPt";
    public static final String WAYPT = "WayPt";
    
    // Advanced log filter
    public static final String ACTIVE = "ACTIVO";
    public static final String INACTIVE = "INACTIVO";
    public static final String FLTR_REC = "<= Punto núm <= ";
    public static final String FLTR_SPD = "<= Velocidad <= ";
    public static final String FLTR_DST = "<= Distancia <= ";
    public static final String FLTR_PDOP = "PDOP <= ";
    public static final String FLTR_HDOP = "HDOP <= ";
    public static final String FLTR_VDOP = "VDOP <= ";
    public static final String FLTR_NSAT = "<= NSAT";
    
    public static final String CLEAR = "BORRAR";
    
    // Log format
    public static final String REC_ESTIMATED = " puntos estimados";
    public static final String SET_ERASE = "Guardar y borrar";
    public static final String SET_NOERASE = "Guardar (no borrar)";
    public static final String ERASE = "Borrar";
    public static final String CONFIRM_ERASE = "Confirmar borrado";
    
    public static final String C_msgWarningFormatIncompatibilityRisk =
        "Vas a cambiar el formato de tu dispositivo " +
        "sin borrar el log.|" +
        "Otros porgramas pueden no entender " +
        "los datos de tu dispositivo!||" +
        "¿Aceptas esta incopativilidad?";
    
    /** Message warning user about impact of changing log format */
    public static final String C_msgWarningFormatAndErase = 
        "Vas a cambiar el formato de" +
        "|grabación de tu dispositivo." +
        "|y" +
        "|BORRAR el log" +
        "|" +
        "|¿CAMBIAR Y BORRAR EL LOG?";
    /** Message warning the user again about the impact of a log format change */           
    public static final String C_msgWarningFormatAndErase2 =
        "Esta es tu última oportunidad para" +
        "|evitar borrar el log." +
        "|" +
        "|¿CAMBIAR Y BORRAR EL LOG?";
    /** Message warning user about impact of changing log format */
    public static final String C_msgEraseWarning = 
        "Vas a borrar el log" +
        "|del dispositivo." +
        "|" +
        "|¿BORRAR el log?";
    public static final String C_msgEraseWarning2 =
        "Esta es tu última oportunidad para" +
        "|evitar borrar el log." +
        "|" +
        "|¿BORRAR el Log?";

    public static final String ONE_FILE = "Un archivo";
    public static final String ONE_FILE_DAY = "Un archivo/ día";
    public static final String ONE_FILE_TRK = "Un archivo/ trk";
    public static final String DEV_LOGONOFF = "Grabar log (/no)";
    public static final String INCREMENTAL = "Incrementar";
    
    public static final String LOG_OVRWR_FULL = "Sobreescribir log(/stop) al llenarse";
    public static final String DATE_RANGE = "Rango Fecha";
    public static final String GET_LOG = "Bajar Log";
    public static final String CANCEL_GET = "Cancelar";
    public static final String NOFIX_COL = "Color No fijados";
    public static final String TRK_SEP = "Sep Trk:";
    public static final String MIN = "min";
    public static final String UTC = "UTC";
    public static final String HGHT_GEOID_DIFF = "Ref Alt Geoide";
    public static final String TO_CSV = "A CSV";
    public static final String TO_GPX = "A GPX";
    public static final String TO_KML = "A KML";
    public static final String TO_TRK = "A TRK";
    public static final String TO_PLT = "A PLT";
    public static final String TO_GMAP= "A GMAP";
    public static final String TO_NMEA= "A NMEÄ";
    public static final String MEM_USED = "Mem Usada   : ";
    public static final String NBR_RECORDS = "Puntos grabados: ";
    
    
    //Log reason
    public static final String NO_DGPS = "Sin DGPS";
    public static final String RTCM = "RTCM";
    public static final String WAAS = "WAAS";
    public static final String RCR_TIME = "Tiempo (s)    ";
    public static final String RCR_SPD  = "Velocidad (km/h)";
    public static final String RCR_DIST = "Distancia (m)";
    public static final String FIX_PER = "Fijar (ms)";
    public static final String INCL_TST_SBAS = "Incl. Sat. Prueba";
    public static final String PWR_SAVE_INTRNL = "Guardar energía (Interno)";

    // NMEA OUTPUT
    public static final String DEFAULTS = "Por defecto";
    
    
    // Other tabs
    public static final String TAB_FLSH = "Flsh";
    public static final String TAB_NMEA_OUT = "Salida NMEA";
    public static final String TAB_NMEA_FILE = "Archivo NMEA";
    public static final String TAB_HOLUX = "Holux";
    
    
    // Log convert
    public static final String ERROR = "Error";
    public static final String PROBLEM_READING = "Error de lectura|";
    public static final String COULD_NOT_OPEN = "No puede abrirse|";
    
    
    // GPS State
    public static final String CANCEL_WAITING = "Cancelar espera";
    public static final String TITLE_WAITING_ERASE =
        "Espere a que el borrado finalice";
    public static final String TXT_WAITING_ERASE =
        "Espere a que el borrado finalice.|" +
        "Puedes cancelar la espera (bajo tu responsabilidad)";
    
    public static final String UNKNOWN = "Desconocido";
    public static final String CHK_PATH =
        "|Revisa el destino y si la tarjeta permite escritura";
    
    public static final String OVERWRITE = "Sobreescribir";
    public static final String ABORT_DOWNLOAD = "Abortar descarga";
    public static final String DATA_NOT_SAME = 
    "Los DATOS en este dispositivo NO|"
    + "corresponden a los DATOS previamente|"
    + "descargados.|"
    + "¿Deseas sobreescribir los DATOS?";
    public static final String LOGGER = "Grabador: ";  // For logger SW version
    
    
    // GPSFile
    public static final String CLOSE_FAILED =
        "Error al cerrar (cerrar Archivo).";
    public static final String WRITING_CLOSED =
        "Escribiendo en archivo cerrado";

}
