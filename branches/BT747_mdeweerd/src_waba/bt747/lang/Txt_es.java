package bt747.lang;

import bt747.Version;

/**
 * @author Mario De Weerd
 * traducido por (translated by) Allsts. allststr@terra.es
 *
 ** Class to provide language specific strings.
 */
public final class Txt_es {
    private static String fontFile=null;
    private static String encoding=null;
        
    // BT747 class
    private static final String S_FILE = "Archivo";
    private static final String S_EXIT_APPLICATION = "Salir";
    
    private static final String S_SETTINGS = "Configuración";
    private static final String S_STOP_LOGGING_ON_CONNECT = "Stop log on connect";
    private static final String S_STOP_CONNECTION = "Detener conexión";
    private static final String S_GPX_UTC_OFFSET_0= "Fijar UTC a 0 en GPX";
    private static final String S_GPX_TRKSEG_WHEN_SMALL = "No dividir tracks GPX";
    private static final String S_GPS_DECODE_ACTIVE= "Decodificar datos GPS";
    private static final String S_FOCUS_HIGHLIGHT= "Autoposición en selección";
    private static final String S_DEBUG= "Mensajes adicionales";
    private static final String S_STATS= "Estadísticas";
    private static final String S_INFO= "Info";
    private static final String S_ABOUT_BT747= "Acerca de BT747";
    private static final String S_ABOUT_SUPERWABA= "Acerca de SuperWaba VM";
    
    private static final String S_TITLE= "BT747 - Control Logger MTK";

    
    private static final String LB_DOWNLOAD= "Descargar";
    
    private static final String TITLE_ATTENTION = "Atención";
    private static final String CONFIRM_APP_EXIT = "Vas a cerrar la aplicación|"
                                                  + "¿Salir del programa?";
    
    private static final String YES=
        "Si";
    private static final String NO=
        "No";
    private static final String CANCEL=
        "Cancelar";


    private static final String ABOUT_TITLE=
        "Acerca de BT747 V"+Version.VERSION_NUMBER;
    private static final String ABOUT_TXT=
        "Creado con SuperWaba"
        + "|http://www.superwaba.org"
        + "|" +Version.BUILD_STR
        + "|creado por Mario De Weerd"
        + "|m.deweerd@ieee.org"
        + "|Esta aplicación permite controlar"
        + "|dispositivos BT747."
        + "|Permite el control por bluetooth"
        + "|del GPS con un hardware hack.  "
        + "|Más información en la web."
        + "|Traducido por Allsts"
        + "|allststr@terra.es"; 

    
    private static final String ABOUT_SUPERWABA_TITLE =
        "Acerca de SuperWaba";
    private static final String ABOUT_SUPERWABA_TXT =
        "SuperWaba Virtual Machine ";
    private static final String ABOUT_SUPERWABA_TXT_CONTINUE =
          "|Copyright (c)2000-2007"
        + "|Guilherme Campos Hazan"
        + "|www.superwaba.com|"
        + "|"
        + "SuperWaba es una versión mejorada"
        + "|de la máquina virtual Waba"
        + "|Copyright (c) 1998,1999 WabaSoft"
        + "|www.wabasoft.com";
    
    private static final String DISCLAIMER_TITLE=
        "Renuncia";
    private static final String DISCLAIMER_TXT=
        "El programa se proporciona 'COMO"
        + "|ESTA',sin garantía de ningún tipo."
        + "|Todas las representaciones expresas"
        + "|o implícitas y las garantías,"
        + "|incluyendo cualquier garantía de"
        + "|comerciabilidad, adecuación para un"
        + "|uso particular o no-infringimiento"
        + "|están aquí excluidas. Los riesgros"
        + "|que surgan por emplear este"
        + "|programa serán asumidos por el"
        + "|usuario. Ver la licencia pública"
        + "|general (GNU) para más detalles."
        + "|"
        + "TODAS LAS REPRESENTACIONES EXPRESAS"
        + "|OF IMPLÍCITAS Y LAS GARANTÍAS,"
        + "|INCLUYENDO CUALQUIER GARANTÍA"
        + "|IMPLÍCITA DE COMERCIABILIDAD,"
        + "|ADECUACION (QUE SEA ADECUADO"
        + "|O TENGA LAS CARACTERISTICAS"
        + "|NECESARIAS) PARA UN USO PARTICULAR"
        + "|O NO-INFRINGIMIENTO, ESTÁN AQUÍ"
        + "|EXCLUIDAS.  LOS RIESGOS QUE SURJAN"
        + "|POR UTILIZAR EL SOFTWARE SERAN"
        + "|ASUMIDOS POR EL USUARIO.  VER LA"
        + "|LICENCIA PUBLICA GENERAL GNU PARA MAS DETALLE" ;



    // TAB identification
    private static final String C_FMT  = "Fmt";
    private static final String C_CTRL = "Ctrl";
    private static final String C_LOG  = "Log";
    private static final String C_FILE = "Arch";
    private static final String C_FLTR = "Fltr";
    private static final String C_EASY = "Fácil";
    private static final String C_CON  = "Con";
    private static final String C_OTHR = "Otros";

    // Conctrl strings
    private static final String BT_BLUETOOTH = "Bluetooth";
    private static final String BT_CONNECT_PRT = "Puerto COM";
    private static final String BT_CLOSE_PRT = "Cerrar puerto";
    private static final String BT_REOPEN_PRT  = "(Re)abrir puerto";
    private static final String MAIN = "Principal:";
    private static final String FIRMWARE = "Firmware: ";
    private static final String MODEL = "Modelo: ";
    private static final String FLASHINFO = "InfoFlash: ";
    private static final String TIME_SEP = "  - Hora: ";
    private static final String LAT = "Lat: ";
    private static final String LON = "Lon: ";
    private static final String GEOID = "Geoide: ";
    private static final String CALC = " (calc: ";
    private static final String HGHT_SEP = " - Alt: ";
    
    // Filters tab panel
    private static final String STANDARD = "Standard";
    private static final String ADVANCED = "Avanzado";

    
    // BT747_dev class
    private static final String[]C_STR_RCR = {
            "Tiempo", "Velocid", "Distanc", "Botón",
            "Picture", "Gas Stat", "Phone", "ATM",
            "Bus stop", "Parking", "Post Box", "Railway",
            "Rstaurnt", "Bridge", "View", "Other"
            };
    private static final String [] logFmtItems = {
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
        "SOLO LOG FIJADOS" // =0x80000000
    };
    private static final String C_BAD_LOG_FORMAT = "Formato log incorrecto";
    

    // Holux specific
    private static final String HOLUX_NAME = "Nombre Holux";

    private static final String SET = "Guardar";

    // EASY TAB
    private static final String BT_5HZ_FIX = "Fijar y grabar a 5Hz";
    private static final String BT_2HZ_FIX = "Fijar a 2Hz";
    private static final String BT_HOT = "Rein Cali";
    private static final String BT_WARM = "Rein Temp";
    private static final String BT_COLD = "Rein Frío";
    private static final String BT_FACT_RESET = "Valores de fábrica";
    private static final String BT_FORCED_ERASE = "Forzar borrado";
    private static final String BT_PT_WITH_REASON = "Click para grabar un punto por...";

    private static final String CONFIRM_FACT_RESET =
            "Vas a volver a los valores de|"
            + "fábrica de tu GPS DataLogger.|"
            + "|¿Confirmas este cambio bajo|"
            + "tu responsabilidad ?";    
    
    // File tab
    private static final String OUTPUT_DIR = "Guardar en:";
    private static final String LOGFILE = "Archivo Log:";
    private static final String REPORT = "Exportados :";
    private static final String CHUNK = "Paquetes :";
    private static final String CHUNK_AHEAD = "Segmentar paquetes:";
    private static final String READ_TIMEOUT = "Tiempo max. lectura (ms) :";
    private static final String CARD_VOL = "Tarjeta/Volumen:";
    private static final String APPLY_SET = "Guardar y Aplicar los ajustes";
    private static final String DEFAULT_SET = "Configuración por defecto";

    // Log filter
    private static final String[] STR_VALID= {
            "Sin Fijar",
            "SPS",
            "DGPS",
            "PPS",
            "RTK",
            "FRTK",
            "Estimado",
            "Manual",
            "Simulación"};

    private static final String TRKPT = "TrkPt";
    private static final String WAYPT = "WayPt";
    
    // Advanced log filter
    private static final String ACTIVE = "ACTIVO";
    private static final String INACTIVE = "INACTIVO";
    private static final String FLTR_REC = "<= Punto núm <= ";
    private static final String FLTR_SPD = "<= Velocidad <= ";
    private static final String FLTR_DST = "<= Distancia <= ";
    private static final String FLTR_PDOP = "PDOP <= ";
    private static final String FLTR_HDOP = "HDOP <= ";
    private static final String FLTR_VDOP = "VDOP <= ";
    private static final String FLTR_NSAT = "<= NSAT";
    
    private static final String CLEAR = "Borrar";
    
    // Log format
    private static final String REC_ESTIMATED = " puntos estimados";
    private static final String SET_ERASE = "Guardar y borrar";
    private static final String SET_NOERASE = "Guardar";
    private static final String ERASE = "Borrar";
    private static final String CONFIRM_ERASE = "Confirmar borrado";
    
    private static final String C_msgWarningFormatIncompatibilityRisk =
        "Vas a cambiar el formato de tu dispositivo "
        + "sin borrar el log.|"
        + "Otros programas|"
        + "pueden no entender|"
        + "los datos de tu dispositivo!||"
        + "¿Aceptas esta incompativilidad?";
    
    /** Message warning user about impact of changing log format */
    private static final String C_msgWarningFormatAndErase = 
        "Vas a cambiar el formato de"
        + "|grabación de tu dispositivo."
        + "|y"
        + "|BORRAR el log"
        + "|"
        + "|¿CAMBIAR Y BORRAR EL LOG?";
    /** Message warning the user again about the impact of a log format change */           
    private static final String C_msgWarningFormatAndErase2 =
        "Esta es tu última oportunidad para"
        + "|evitar borrar el log."
        + "|"
        + "|¿CAMBIAR Y BORRAR EL LOG?";
    /** Message warning user about impact of changing log format */
    private static final String C_msgEraseWarning = 
        "Vas a borrar el log"
        + "|del dispositivo."
        + "|"
        + "|¿BORRAR el log?";
    private static final String C_msgEraseWarning2 =
        "Esta es tu última oportunidad para"
        + "|evitar borrar el log."
        + "|"
        + "|¿BORRAR el Log?";

    private static final String ONE_FILE = "Un archivo";
    private static final String ONE_FILE_DAY = "Un archivo/ día";
    private static final String ONE_FILE_TRK = "Un archivo/ trk";
    private static final String DEV_LOGONOFF = "Grabar log (/no)";
    private static final String INCREMENTAL = "Incrementar";
    
    private static final String LOG_OVRWR_FULL = "Sobreescribir log(/stop) al llenarse";
    private static final String DATE_RANGE = "Rango Fecha";
    private static final String GET_LOG = "Bajar Log";
    private static final String CANCEL_GET = "Cancel";
    private static final String NOFIX_COL = "Color no fij";
    private static final String TRK_SEP = "Separar Track:";
    private static final String MIN = "min";
    private static final String UTC = "UTC";
    private static final String HGHT_GEOID_DIFF = "Ref Alt Geoide";
    private static final String TO_CSV = "A CSV";
    private static final String TO_GPX = "A GPX";
    private static final String TO_KML = "A KML";
    private static final String TO_TRK = "A TRK";
    private static final String TO_PLT = "A PLT";
    private static final String TO_GMAP= "A GMAP";
    private static final String TO_NMEA= "A NMEA";
    private static final String MEM_USED = "Memoria Usada: ";
    private static final String NBR_RECORDS = "Puntos Grabados: ";
    
    
    //Log reason
    private static final String NO_DGPS = "Sin DGPS";
    private static final String RTCM = "RTCM";
    private static final String WAAS = "WAAS";
    private static final String RCR_TIME = "Tiempo (s)    ";
    private static final String RCR_SPD  = "Velocidad (km/h)";
    private static final String RCR_DIST = "Distancia (m)";
    private static final String FIX_PER = "Fijar cada (ms)";
    private static final String INCL_TST_SBAS = "Incl. Sat. Prueba";
    private static final String PWR_SAVE_INTRNL = "Guardar energía (Interno)";

    // NMEA OUTPUT
    private static final String DEFAULTS = "Por defec";
    
    
    // Other tabs
    private static final String TAB_FLSH = "Flsh";
    private static final String TAB_NMEA_OUT = "Datos NMEA";
    private static final String TAB_NMEA_FILE = "Arch NMEA";
    private static final String TAB_HOLUX = "Holux";
    
    
    // Log convert
    private static final String ERROR = "Error";
    private static final String PROBLEM_READING = "Error de lectura|";
    private static final String COULD_NOT_OPEN = "No puede abrirse|";
    
    
    // GPS State
    private static final String CANCEL_WAITING = "Cancelar espera";
    private static final String TITLE_WAITING_ERASE =
        "Espere a que el borrado finalice";
    private static final String TXT_WAITING_ERASE =
        "Espere a que el borrado finalice.|"
        + "Cancelar (Bajo tu responsabilidad)";
    
    private static final String UNKNOWN = "Desconocido";
    private static final String CHK_PATH =
        "|Revisa el destino y si la|"
	+ "tarjeta permite la escritura";
    
    private static final String OVERWRITE = "Sobreescribir";
    private static final String ABORT_DOWNLOAD = "Abortar descarga";
    private static final String DATA_NOT_SAME = 
    "Los DATOS en este dispositivo NO|"
    + "corresponden a los DATOS|"
    + "previamente descargados.|"
    + "¿Sobreescribir los DATOS?";
    private static final String LOGGER = "Grabador: ";  // For logger SW version
    
    
    // GPSFile
    private static final String CLOSE_FAILED =
        "Error al cerrar (cerrar Archivo).";
    private static final String WRITING_CLOSED =
        "Escribiendo en archivo cerrado";


    // Flash option
    private static final String TIMESLEFT = "Cambios Restantes";
    private static final String UPDATERATE = "Frecuencia (Hz)";
    private static final String BAUDRATE = "Baud Rate";
    private static final String WRITEFLASH = "Grabar Flash";
    private static final String ABORT = "Abortar";
    private static final String TXT_FLASH_LIMITED_WRITES=
        "El número de cambios restantes es|"
        + "limitada y cambiar la configuración|"
        + "puede inutilizar el dispositivo|"
        + "(p.e., un cambio de baud rate)|"
        + "ABORTAR clicando abortar!!";
    private static final String PERIOD_ABBREV = "Per";

    // Forgotton in Advanced track filter
    private static final String IGNORE_0VALUES
        = "Los valores 0 son ignorados";
    private static final String STORE_SETTINGS =
        "Guardar conf";
    private static final String RESTORE_SETTINGS =
        "Restaurar conf";

    private static final String METERS_ABBR = "m";

    private static final String WARNING =
        "Alerta";
    private static final String NO_FILES_WERE_CREATED =
        "No se crearon archivos de salida!"
        + "||"
        + "Esto puede significar que:"
        + "|- El filtro no seleccionó ningún punto"
        + "|- El log no contiene datos"
        + "|"
        + "|Prueba seleccionando todos los puntos."
        + "|Si no funciona,"
        + "|puede ser un error del programa.";
    
    private static final String ADD_RECORD_NUMBER =
        "Info núm en el log";

    private static final String S_DEBUG_CONN= "Depurar conn.";
    private static final String S_IMPERIAL= "Unid Imperiales"; 
    private static final String BAD_SUPERWABAVERSION = 
      "Esta aplicación se creó para|SuperWaba V";
    
    private static final String BAD_SUPERWABAVERSION_CONT = 
         ".|Esta es la versión V"; 
    private static final String BAD_SUPERWABAVERSION_CONT2 = 
       "|Cerrando aplicación"; 
    private static final String S_DEVICE = "Dispositivo"; 
    private static final String S_DEFAULTDEVICE = "Disp. por defecto"; //"Dispositivo por defecto";                        (you can also use "") 
    private static final String S_GISTEQTYPE1 = "iTrackU-Nemerix"; 
    private static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr"; 
    private static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII"; 
    private static final String BT_MAC_ADDR = "Dirección Mac BT:"; //     (you can also use "Direc. Mac BT") 
    private static final String S_OUTPUT_LOGCONDITIONS = "Cond. del log de salida"; // //"Condiciones del log de salida";                         (you can also use )
    private static final String MEM_FREE = "disp.";

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
