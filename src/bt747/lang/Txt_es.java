package bt747.lang;

import bt747.Version;

/**
 * @author Mario De Weerd
 * traducido por (translated by) Allsts. allststr@terra.es
 *
 ** Class to provide language specific strings.
 */
public class Txt_es {
    public static String fontFile=null;
    public static String encoding=null;
        
    // BT747 class
    public static final String S_FILE = "Archivo";
    public static final String S_EXIT_APPLICATION = "Salir";
    
    public static final String S_SETTINGS = "Configuraci�n";
    public static final String S_STOP_LOGGING_ON_CONNECT = "Stop log on connect";
    public static final String S_STOP_CONNECTION = "Detener conexi�n";
    public static final String S_GPX_UTC_OFFSET_0= "Fijar UTC a 0 en GPX";
    public static final String S_GPX_TRKSEG_WHEN_SMALL = "No dividir tracks GPX";
    public static final String S_GPS_DECODE_ACTIVE= "Decodificar datos GPS";
    public static final String S_FOCUS_HIGHLIGHT= "Autoposici�n en selecci�n";
    public static final String S_DEBUG= "Mensajes adicionales";
    public static final String S_STATS= "Estad�sticas";
    public static final String S_INFO= "Info";
    public static final String S_ABOUT_BT747= "Acerca de BT747";
    public static final String S_ABOUT_SUPERWABA= "Acerca de SuperWaba VM";
    
    public static final String S_TITLE= "BT747 - Control Logger MTK";

    
    public static final String LB_DOWNLOAD= "Descargar";
    
    public static final String TITLE_ATTENTION = "Atenci�n";
    public static final String CONFIRM_APP_EXIT = "Vas a cerrar la aplicaci�n|"
                                                  + "�Salir del programa?";
    
    public static final String YES=
        "Si";
    public static final String NO=
        "No";
    public static final String CANCEL=
        "Cancelar";


    public static final String ABOUT_TITLE=
        "Acerca de BT747 V"+Version.VERSION_NUMBER;
    public static final String ABOUT_TXT=
        "Creado con SuperWaba"
        + "|http://www.superwaba.org"
        + "|" +Version.BUILD_STR
        + "|creado por Mario De Weerd"
        + "|m.deweerd@ieee.org"
        + "|Esta aplicaci�n permite controlar"
        + "|dispositivos BT747."
        + "|Permite el control por bluetooth"
        + "|del GPS con un hardware hack.  "
        + "|M�s informaci�n en la web."
        + "|Traducido por Allsts"
        + "|allststr@terra.es"; 

    
    public static final String ABOUT_SUPERWABA_TITLE =
        "Acerca de SuperWaba";
    public static final String ABOUT_SUPERWABA_TXT =
        "SuperWaba Virtual Machine ";
    public static final String ABOUT_SUPERWABA_TXT_CONTINUE =
          "|Copyright (c)2000-2007"
        + "|Guilherme Campos Hazan"
        + "|www.superwaba.com|"
        + "|"
        + "SuperWaba es una versi�n mejorada"
        + "|de la m�quina virtual Waba"
        + "|Copyright (c) 1998,1999 WabaSoft"
        + "|www.wabasoft.com";
    
    public static final String DISCLAIMER_TITLE=
        "Renuncia";
    public static final String DISCLAIMER_TXT=
        "El programa se proporciona 'COMO"
        + "|ESTA',sin garant�a de ning�n tipo."
        + "|Todas las representaciones expresas"
        + "|o impl�citas y las garant�as,"
        + "|incluyendo cualquier garant�a de"
        + "|comerciabilidad, adecuaci�n para un"
        + "|uso particular o no-infringimiento"
        + "|est�n aqu� excluidas. Los riesgros"
        + "|que surgan por emplear este"
        + "|programa ser�n asumidos por el"
        + "|usuario. Ver la licencia p�blica"
        + "|general (GNU) para m�s detalles."
        + "|"
        + "TODAS LAS REPRESENTACIONES EXPRESAS"
        + "|OF IMPL�CITAS Y LAS GARANT�AS,"
        + "|INCLUYENDO CUALQUIER GARANT�A"
        + "|IMPL�CITA DE COMERCIABILIDAD,"
        + "|ADECUACION (QUE SEA ADECUADO"
        + "|O TENGA LAS CARACTERISTICAS"
        + "|NECESARIAS) PARA UN USO PARTICULAR"
        + "|O NO-INFRINGIMIENTO, EST�N AQU�"
        + "|EXCLUIDAS.  LOS RIESGOS QUE SURJAN"
        + "|POR UTILIZAR EL SOFTWARE SERAN"
        + "|ASUMIDOS POR EL USUARIO.  VER LA"
        + "|LICENCIA PUBLICA GENERAL GNU PARA MAS DETALLE" ;



    // TAB identification
    public static final String C_FMT  = "Fmt";
    public static final String C_CTRL = "Ctrl";
    public static final String C_LOG  = "Log";
    public static final String C_FILE = "Arch";
    public static final String C_FLTR = "Fltr";
    public static final String C_EASY = "F�cil";
    public static final String C_CON  = "Con";
    public static final String C_OTHR = "Otros";

    // Conctrl strings
    public static final String BT_BLUETOOTH = "Bluetooth";
    public static final String BT_CONNECT_PRT = "Puerto COM";
    public static final String BT_CLOSE_PRT = "Cerrar puerto";
    public static final String BT_REOPEN_PRT  = "(Re)abrir puerto";
    public static final String MAIN = "Principal:";
    public static final String FIRMWARE = "Firmware: ";
    public static final String MODEL = "Modelo: ";
    public static final String FLASHINFO = "InfoFlash: ";
    public static final String TIME_SEP = "  - Hora: ";
    public static final String LAT = "Lat: ";
    public static final String LON = "Lon: ";
    public static final String GEOID = "Geoide: ";
    public static final String CALC = " (calc: ";
    public static final String HGHT_SEP = " - Alt: ";
    
    // Filters tab panel
    public static final String STANDARD = "Standard";
    public static final String ADVANCED = "Avanzado";

    
    // BT747_dev class
    public static final String[]C_STR_RCR = {
            "Tiempo", "Velocid", "Distanc", "Bot�n",
            "Picture", "Gas Stat", "Phone", "ATM",
            "Bus stop", "Parking", "Post Box", "Railway",
            "Rstaurnt", "Bridge", "View", "Other"
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
        "SOLO LOG FIJADOS" // =0x80000000
    };
    public static final String C_BAD_LOG_FORMAT = "Formato log incorrecto";
    

    // Holux specific
    public static final String HOLUX_NAME = "Nombre Holux";

    public static final String SET = "Guardar";

    // EASY TAB
    public static final String BT_5HZ_FIX = "Fijar y grabar a 5Hz";
    public static final String BT_2HZ_FIX = "Fijar a 2Hz";
    public static final String BT_HOT = "Rein Cali";
    public static final String BT_WARM = "Rein Temp";
    public static final String BT_COLD = "Rein Fr�o";
    public static final String BT_FACT_RESET = "Valores de f�brica";
    public static final String BT_FORCED_ERASE = "Forzar borrado";
    public static final String BT_PT_WITH_REASON = "Click para grabar un punto por...";

    public static final String CONFIRM_FACT_RESET =
            "Vas a volver a los valores de|"
            + "f�brica de tu GPS DataLogger.|"
            + "|�Confirmas este cambio bajo|"
            + "tu responsabilidad ?";    
    
    // File tab
    public static final String OUTPUT_DIR = "Guardar en:";
    public static final String LOGFILE = "Archivo Log:";
    public static final String REPORT = "Exportados :";
    public static final String CHUNK = "Paquetes :";
    public static final String CHUNK_AHEAD = "Segmentar paquetes:";
    public static final String READ_TIMEOUT = "Tiempo max. lectura (ms) :";
    public static final String CARD_VOL = "Tarjeta/Volumen:";
    public static final String APPLY_SET = "Guardar y Aplicar los ajustes";
    public static final String DEFAULT_SET = "Configuraci�n por defecto";

    // Log filter
    public static final String[] STR_VALID= {
            "Sin Fijar",
            "SPS",
            "DGPS",
            "PPS",
            "RTK",
            "FRTK",
            "Estimado",
            "Manual",
            "Simulaci�n"};

    public static final String TRKPT = "TrkPt";
    public static final String WAYPT = "WayPt";
    
    // Advanced log filter
    public static final String ACTIVE = "ACTIVO";
    public static final String INACTIVE = "INACTIVO";
    public static final String FLTR_REC = "<= Punto n�m <= ";
    public static final String FLTR_SPD = "<= Velocidad <= ";
    public static final String FLTR_DST = "<= Distancia <= ";
    public static final String FLTR_PDOP = "PDOP <= ";
    public static final String FLTR_HDOP = "HDOP <= ";
    public static final String FLTR_VDOP = "VDOP <= ";
    public static final String FLTR_NSAT = "<= NSAT";
    
    public static final String CLEAR = "Borrar";
    
    // Log format
    public static final String REC_ESTIMATED = " puntos estimados";
    public static final String SET_ERASE = "Guardar y borrar";
    public static final String SET_NOERASE = "Guardar";
    public static final String ERASE = "Borrar";
    public static final String CONFIRM_ERASE = "Confirmar borrado";
    
    public static final String C_msgWarningFormatIncompatibilityRisk =
        "Vas a cambiar el formato de tu dispositivo "
        + "sin borrar el log.|"
        + "Otros programas|"
        + "pueden no entender|"
        + "los datos de tu dispositivo!||"
        + "�Aceptas esta incompativilidad?";
    
    /** Message warning user about impact of changing log format */
    public static final String C_msgWarningFormatAndErase = 
        "Vas a cambiar el formato de"
        + "|grabaci�n de tu dispositivo."
        + "|y"
        + "|BORRAR el log"
        + "|"
        + "|�CAMBIAR Y BORRAR EL LOG?";
    /** Message warning the user again about the impact of a log format change */           
    public static final String C_msgWarningFormatAndErase2 =
        "Esta es tu �ltima oportunidad para"
        + "|evitar borrar el log."
        + "|"
        + "|�CAMBIAR Y BORRAR EL LOG?";
    /** Message warning user about impact of changing log format */
    public static final String C_msgEraseWarning = 
        "Vas a borrar el log"
        + "|del dispositivo."
        + "|"
        + "|�BORRAR el log?";
    public static final String C_msgEraseWarning2 =
        "Esta es tu �ltima oportunidad para"
        + "|evitar borrar el log."
        + "|"
        + "|�BORRAR el Log?";

    public static final String ONE_FILE = "Un archivo";
    public static final String ONE_FILE_DAY = "Un archivo/ d�a";
    public static final String ONE_FILE_TRK = "Un archivo/ trk";
    public static final String DEV_LOGONOFF = "Grabar log (/no)";
    public static final String INCREMENTAL = "Incrementar";
    
    public static final String LOG_OVRWR_FULL = "Sobreescribir log(/stop) al llenarse";
    public static final String DATE_RANGE = "Rango Fecha";
    public static final String GET_LOG = "Bajar Log";
    public static final String CANCEL_GET = "Cancel";
    public static final String NOFIX_COL = "Color no fij";
    public static final String TRK_SEP = "Separar Track:";
    public static final String MIN = "min";
    public static final String UTC = "UTC";
    public static final String HGHT_GEOID_DIFF = "Ref Alt Geoide";
    public static final String TO_CSV = "A CSV";
    public static final String TO_GPX = "A GPX";
    public static final String TO_KML = "A KML";
    public static final String TO_TRK = "A TRK";
    public static final String TO_PLT = "A PLT";
    public static final String TO_GMAP= "A GMAP";
    public static final String TO_NMEA= "A NMEA";
    public static final String MEM_USED = "Memoria Usada: ";
    public static final String NBR_RECORDS = "Puntos Grabados: ";
    
    
    //Log reason
    public static final String NO_DGPS = "Sin DGPS";
    public static final String RTCM = "RTCM";
    public static final String WAAS = "WAAS";
    public static final String RCR_TIME = "Tiempo (s)    ";
    public static final String RCR_SPD  = "Velocidad (km/h)";
    public static final String RCR_DIST = "Distancia (m)";
    public static final String FIX_PER = "Fijar cada (ms)";
    public static final String INCL_TST_SBAS = "Incl. Sat. Prueba";
    public static final String PWR_SAVE_INTRNL = "Guardar energ�a (Interno)";

    // NMEA OUTPUT
    public static final String DEFAULTS = "Por defec";
    
    
    // Other tabs
    public static final String TAB_FLSH = "Flsh";
    public static final String TAB_NMEA_OUT = "Datos NMEA";
    public static final String TAB_NMEA_FILE = "Arch NMEA";
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
        "Espere a que el borrado finalice.|"
        + "Cancelar (Bajo tu responsabilidad)";
    
    public static final String UNKNOWN = "Desconocido";
    public static final String CHK_PATH =
        "|Revisa el destino y si la|"
	+ "tarjeta permite la escritura";
    
    public static final String OVERWRITE = "Sobreescribir";
    public static final String ABORT_DOWNLOAD = "Abortar descarga";
    public static final String DATA_NOT_SAME = 
    "Los DATOS en este dispositivo NO|"
    + "corresponden a los DATOS|"
    + "previamente descargados.|"
    + "�Sobreescribir los DATOS?";
    public static final String LOGGER = "Grabador: ";  // For logger SW version
    
    
    // GPSFile
    public static final String CLOSE_FAILED =
        "Error al cerrar (cerrar Archivo).";
    public static final String WRITING_CLOSED =
        "Escribiendo en archivo cerrado";


    // Flash option
    public static final String TIMESLEFT = "Cambios Restantes";
    public static final String UPDATERATE = "Frecuencia (Hz)";
    public static final String BAUDRATE = "Baud Rate";
    public static final String WRITEFLASH = "Grabar Flash";
    public static final String ABORT = "Abortar";
    public static final String TXT_FLASH_LIMITED_WRITES=
        "El n�mero de cambios restantes es|"
        + "limitada y cambiar la configuraci�n|"
        + "puede inutilizar el dispositivo|"
        + "(p.e., un cambio de baud rate)|"
        + "ABORTAR clicando abortar!!";
    public static final String PERIOD_ABBREV = "Per";

    // Forgotton in Advanced track filter
    public static final String IGNORE_0VALUES
        = "Los valores 0 son ignorados";
    public static final String STORE_SETTINGS =
        "Guardar conf";
    public static final String RESTORE_SETTINGS =
        "Restaurar conf";

    public static final String METERS_ABBR = "m";

    public static final String WARNING =
        "Alerta";
    public static final String NO_FILES_WERE_CREATED =
        "No se crearon archivos de salida!"
        + "||"
        + "Esto puede significar que:"
        + "|- El filtro no seleccion� ning�n punto"
        + "|- El log no contiene datos"
        + "|"
        + "|Prueba seleccionando todos los puntos."
        + "|Si no funciona,"
        + "|puede ser un error del programa.";
    
    public static final String ADD_RECORD_NUMBER =
        "Info n�m en el log";

    public static final String S_DEBUG_CONN= "Depurar conn.";
    public static final String S_IMPERIAL= "Unid Imperiales"; 
    public static final String BAD_SUPERWABAVERSION = 
      "Esta aplicaci�n se cre� para|SuperWaba V";
    
    public static final String BAD_SUPERWABAVERSION_CONT = 
         ".|Esta es la versi�n V"; 
    public static final String BAD_SUPERWABAVERSION_CONT2 = 
       "|Cerrando aplicaci�n"; 
    public static final String S_DEVICE = "Dispositivo"; 
    public static final String S_DEFAULTDEVICE = "Disp. por defecto"; //"Dispositivo por defecto";                        (you can also use "") 
    public static final String S_GISTEQTYPE1 = "iTrackU-Nemerix"; 
    public static final String S_GISTEQTYPE2 = "iTrackU-PhotoTrackr"; 
    public static final String S_GISTEQTYPE3 = "iTrackU-SIRFIII"; 
    public static final String BT_MAC_ADDR = "Direcci�n Mac BT:"; //     (you can also use "Direc. Mac BT") 
    public static final String S_OUTPUT_LOGCONDITIONS = "Cond. del log de salida"; // //"Condiciones del log de salida";                         (you can also use )
    public static final String MEM_FREE = "disp.";

    public static final String TRKPTCOMMENT = "TRK PT INFO";
    public static final String TRKPTNAME = "TRK PT NAME";

    public static final String DOWNLOAD_INCREMENTAL = "Smart dwnld";
    public static final String DOWNLOAD_FULL = "Full dwnld";
    public static final String DOWNLOAD_NORMAL = "Normal dwnld";

}