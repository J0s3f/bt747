/**
 * 
 */
package gps.mvc;

import gps.BT747Constants;
import gps.GpsEvent;
import gps.convert.Conv;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * Model for Mtk devices.
 * 
 * Refactoring ongoing.
 * 
 * @author Mario De Weerd.
 * 
 */
public class MtkModel {
    private final GPSLinkHandler handler;
    private MTKLogDownloadHandler mtkLogHandler;

    protected final void setLogHandler(MTKLogDownloadHandler handler) {
        mtkLogHandler = handler;
    }
    
    // Next should be private.

    private int logFormat = 0;

    private int logTimeInterval = 0;

    private int logSpeedInterval = 0;

    private int logDistanceInterval = 0;

    private int logStatus = 0;
    private int initialLogMode = 0;

    public int logNbrLogPts = 0;

    private int logMemUsed = 0;

    public int logMemUsedPercent = 0;

    private int logFixPeriod = 0; // Time between fixes

    private int datum = 0; // Datum WGS84, TOKYO-M, TOKYO-A

    private boolean loggingActive = false;
    public boolean loggerIsFull = false;
    private boolean loggerNeedsInit = false;
    private boolean loggerIsDisabled = false;

    private boolean logFullOverwrite = false; // When true, overwrite log
    // when
    // device is full

    private int dgpsMode = 0;

    // Flash user option values
    private int dtUserOptionTimesLeft;

    private int dtUpdateRate;

    private int dtBaudRate;

    private int dtGLL_Period;

    private int dtRMC_Period;

    private int dtVTG_Period;

    private int dtGSA_Period;

    private int dtGSV_Period;

    private int dtGGA_Period;

    private int dtZDA_Period;

    private int dtMCHN_Period;

    private String mainVersion = "";

    private String model = "";

    private String device = "";

    private String firmwareVersion = "";

    private String MtkLogVersion = "";

    // (1FH = ATMEL), followed by the device code, 65H.
    // Manufacturer and Product ID
    private int flashManuProdID = 0;

    private String sBtMacAddr = "";

    private final int[] NMEA_periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

    private boolean holux = false; // True if Holux M-241 device detected

    private String holuxName = "";

    private boolean SBASEnabled = false;

    private boolean SBASTestEnabled = false;

    private boolean powerSaveEnabled = false;

    private Model context;

    MtkModel(final Model context, final GPSLinkHandler handler) {
        this.handler = handler;
        this.context = context;
    }

    /**
     * <code>dataOK</code> indicates if all volatile data from the device
     * has been fetched. This is useful to know if the settings can be backed
     * up.
     */
    private int dataOK = 0;

    // The next lines indicate bit fields of <code>dataOK</code>
    public static final int C_OK_FIX = 0x0001;
    public static final int C_OK_DGPS = 0x0002;
    public static final int C_OK_SBAS = 0x0004;
    public static final int C_OK_NMEA = 0x0008;
    public static final int C_OK_SBAS_TEST = 0x0010;
    public static final int C_OK_DATUM = 0x0020;
    public static final int C_OK_TIME = 0x0040;
    public static final int C_OK_SPEED = 0x0080;
    public static final int C_OK_DIST = 0x0100;
    public static final int C_OK_FORMAT = 0x0200;

    public final boolean isDataOK(final int mask) {
        return ((dataOK & mask) == mask);
    }

    private final boolean[] dataAvailable = new boolean[MtkModel.DATA_LAST_INDEX + 1];
    private final int[] dataRequested = new int[MtkModel.DATA_LAST_INDEX + 1];
    private final boolean[] dataTimesOut = { // Indicates if data times out
    true, // DATA_MEM_USED
            true, // DATA_MEM_PTS_LOGGED
            false, // DATA_FLASH_TYPE
            false, // DATA_LOG_FORMAT
            false, // DATA_MTK_VERSION
            false, // DATA_MTK_RELEASE
            false, // DATA_INITIAL_LOG
            true, // DATA_LOG_STATUS
            false, // DATA_LOG_VERSION
    };
    public final static int DATA_FLASH_TYPE = 0;
    public final static int DATA_MEM_PTS_LOGGED = 1;
    public final static int DATA_MEM_USED = 2;
    public final static int DATA_LOG_FORMAT = 3;
    public final static int DATA_MTK_VERSION = 4;
    public final static int DATA_MTK_RELEASE = 5;
    public final static int DATA_INITIAL_LOG = 6;
    public final static int DATA_LOG_STATUS = 7;
    public final static int DATA_LOG_VERSION = 8;
    protected final static int DATA_LAST_INDEX = 8; // The last possible index

    /**
     * Reset the availability of all values - e.g. after loss of connection.
     */
    protected final void setAllUnavailable() {
        final int ts = Generic.getTimeStamp() - 5 * 60 * 1000;
        for (int i = 0; i < dataAvailable.length; i++) {
            dataAvailable[i] = false;
            dataRequested[i] = ts;
        }
    }

    private static final int DATA_TIMEOUT = 3500;
    private boolean autofetch = true;

    public final void setAutoFetch(final boolean isAuto) {
        autofetch = isAuto;
    }

    protected final boolean isDataNeedsRequest(final int ts,
            final int dataType) {
        if (!autofetch) {
            return false; // For debug.
        }
        if (Generic.getDebugLevel() > 1) {
            Generic.debug("ts:" + ts + " type:" + dataType + " timesout:"
                    + dataTimesOut[dataType] + " available:"
                    + dataAvailable[dataType] + " requested:"
                    + (ts - dataRequested[dataType]));
        }
        if ( // Data not available or out of date.
        (((autofetch && dataTimesOut[dataType]) || !isDataAvailable(dataType))
        // Request must have timed out
        && ((ts - dataRequested[dataType]) > MtkModel.DATA_TIMEOUT))) {
            dataRequested[dataType] = ts;
            return true;
        } else {
            return false;
        }
    }

    private final void setAvailable(final int dataType) {
        dataAvailable[dataType] = true;
        switch (dataType) {
        case DATA_FLASH_TYPE:

            break;
        case DATA_LOG_FORMAT:
            dataOK |= MtkModel.C_OK_FORMAT;
            break;
        default:
        case DATA_MEM_USED:
            break;
        }
    }

    public final boolean isDataAvailable(final int dataType) {
        return dataAvailable[dataType];
    }

    protected final void setChanged(final int dataType) {
        dataAvailable[dataType] = false;
        dataRequested[dataType] = 0; // Just changed it - oblige 'timeout'.
    }

    public GPSLinkHandler getHandler() {
        return handler;
    }

    public final void analyseMtkBinData(
            final MtkBinTransportMessageModel response) {
        if (Generic.isDebug()) {
            Generic.debug("<" + response.toString());
        }

        // TODO: Handle response
        // For starters: analyse AGPS response...
    }

    /**
     * @param sNmea
     *                Elements of the NMEA packet to analyze. <br>
     *                Example: PMTK182,3,4 <br>
     *                nmea[0] PMTK182 <br>
     *                nmea[1] 3 <br>
     *                nmea[2] 4
     * @return
     * @see #reqInitialLogMode()
     */
    private int analyseLogNmea(final String[] sNmea) {
        // if(GPS_DEBUG) {
        // waba.sys.debugMsg("LOG:"+p_nmea.length+':'+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+"\n");}
        // Suppose that the command is ok (PMTK182)

        // Currently taking care of replies from the device only.
        // The other data we send ourselves
        handler.resetLogTimeOut(); // Reset timeout
        if (sNmea.length > 2) {
            switch (JavaLibBridge.toInt(sNmea[1])) {
            case BT747Constants.PMTK_LOG_DT:
                // Parameter information
                // TYPE = Parameter type
                // DATA = Parameter data
                // $PMTK182,3,TYPE,DATA
                final int z_type = JavaLibBridge.toInt(sNmea[2]);
                if (sNmea.length == 4) {
                    switch (z_type) {
                    case BT747Constants.PMTK_LOG_FLASH_STAT:
                        mtkLogHandler
                                .handleLogFlashStatReply(sNmea[3]);
                        break;
                    case BT747Constants.PMTK_LOG_FORMAT: // 2;
                        // if(GPS_DEBUG) {
                        // waba.sys.debugMsg("FMT:"+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+","+p_nmea[3]+"\n");}
                        logFormat = Conv.hex2Int(sNmea[3]);
                        setAvailable(MtkModel.DATA_LOG_FORMAT);
                        postEvent(GpsEvent.UPDATE_LOG_FORMAT);
                        break;
                    case BT747Constants.PMTK_LOG_TIME_INTERVAL: // 3;
                        logTimeInterval = JavaLibBridge.toInt(sNmea[3]);
                        dataOK |= MtkModel.C_OK_TIME;
                        postEvent(GpsEvent.UPDATE_LOG_TIME_INTERVAL);
                        break;
                    case BT747Constants.PMTK_LOG_DISTANCE_INTERVAL: // 4;
                        logDistanceInterval = JavaLibBridge.toInt(sNmea[3]);
                        dataOK |= MtkModel.C_OK_DIST;
                        postEvent(GpsEvent.UPDATE_LOG_DISTANCE_INTERVAL);
                        break;
                    case BT747Constants.PMTK_LOG_SPEED_INTERVAL: // 5;
                        logSpeedInterval = JavaLibBridge.toInt(sNmea[3]) / 10;
                        dataOK |= MtkModel.C_OK_SPEED;
                        postEvent(GpsEvent.UPDATE_LOG_SPEED_INTERVAL);
                        break;
                    case BT747Constants.PMTK_LOG_REC_METHOD: // 6;
                        logFullOverwrite = (JavaLibBridge.toInt(sNmea[3]) == 1);
                        postEvent(GpsEvent.UPDATE_LOG_REC_METHOD);
                        break;
                    case BT747Constants.PMTK_LOG_LOG_STATUS: // 7; // bit 2
                        // =
                        // logging
                        // on/off
                        logStatus = JavaLibBridge.toInt(sNmea[3]);
                        // logFullOverwrite = (((logStatus &
                        // BT747Constants.PMTK_LOG_STATUS_LOGSTOP_OVER_MASK)
                        // !=
                        // 0));
                        setLoggingActive((((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGONOF_MASK) != 0)));
                        loggerIsFull = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGISFULL_MASK) != 0));
                        loggerNeedsInit = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGMUSTINIT_MASK) != 0));
                        loggerIsDisabled = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGDISABLED_MASK) != 0));
                        setAvailable(MtkModel.DATA_LOG_STATUS);
                        postEvent(GpsEvent.UPDATE_LOG_LOG_STATUS);
                        break;
                    case BT747Constants.PMTK_LOG_MEM_USED: // 8;
                        setLogMemUsed(Conv.hex2Int(sNmea[3]));
                        logMemUsedPercent = (100 * (getLogMemUsed() - (0x200 * ((getLogMemUsed() + 0xFFFF) / 0x10000))))
                                / logMemUsefullSize();
                        setAvailable(MtkModel.DATA_MEM_USED);
                        postEvent(GpsEvent.UPDATE_LOG_MEM_USED);
                        break;
                    case BT747Constants.PMTK_LOG_FLASH: // 9;
                        flashManuProdID = Conv.hex2Int(sNmea[3]);
                        setAvailable(MtkModel.DATA_FLASH_TYPE);
                        postEvent(GpsEvent.UPDATE_LOG_FLASH);
                        break;
                    case BT747Constants.PMTK_LOG_NBR_LOG_PTS: // 10;
                        logNbrLogPts = Conv.hex2Int(sNmea[3]);
                        setAvailable(MtkModel.DATA_MEM_PTS_LOGGED);
                        postEvent(GpsEvent.UPDATE_LOG_NBR_LOG_PTS);
                        break;
                    case BT747Constants.PMTK_LOG_FLASH_SECTORS: // 11;
                        postEvent(GpsEvent.UPDATE_LOG_FLASH_SECTORS);
                        break;
                    case BT747Constants.PMTK_LOG_VERSION: // 12:
                        MtkLogVersion = "V"
                                + JavaLibBridge.toString(JavaLibBridge
                                        .toInt(sNmea[3]) / 100f, 2);
                        setAvailable(MtkModel.DATA_LOG_VERSION);
                        postEvent(GpsEvent.UPDATE_LOG_VERSION);
                        break;
                    default:
                    }
                }
                break;
            case BT747Constants.PMTK_LOG_DT_LOG:
                // Data from the log
                // $PMTK182,8,START_ADDRESS,DATA

                try {
                    // Get the initial mode.
                    /** @see #reqInitialLogMode() */

                    if (Conv.hex2Int(sNmea[2]) == 6) {
                        initialLogMode = Conv.hex2Int(sNmea[3]
                                .substring(0, 4));
                        // correct endian.
                        initialLogMode = (initialLogMode & 0xFF << 8)
                                | (initialLogMode >> 8);
                        setAvailable(MtkModel.DATA_INITIAL_LOG);
                    }
                } catch (final Exception e) {
                    // Do not care about exception
                }
                // logFullOverwrite = (((logStatus &
                // BT747Constants.PMTK_LOG_STATUS_LOGSTOP_OVER_MASK) != 0));

                try {
                    // waba.sys.debugMsg("Before
                    // AnalyzeLog:"+p_nmea[3].length());
                    mtkLogHandler.analyzeLogPart(Conv
                            .hex2Int(sNmea[2]), sNmea[3]);
                } catch (final Exception e) {
                    Generic.debug("analyzeLogNMEA", e);

                    // During debug: array index out of bounds
                    // TODO: handle exception
                }
                break;
            default:
                // Nothing - unexpected
            }
        }
        return 0; // Done.
    }

    public final int analyseMtkNmea(final String[] sNmea) {
        int cmd;
        int result;
        result = 0;
        if (sNmea[0].startsWith("PMTK")) {
            if (Generic.isDebug()) {
                String s;
                int length = sNmea.length;
                if (sNmea[1].charAt(0) == '8') {
                    length = 3;
                }
                s = "<";
                for (int i = 0; i < length; i++) {
                    s += sNmea[i];
                    s += ",";
                }
                Generic.debug(s);
            }
            cmd = JavaLibBridge.toInt(sNmea[0].substring(4));

            result = -1; // Suppose cmd not treated
            switch (cmd) {
            case BT747Constants.PMTK_CMD_LOG: // CMD 182;
                result = analyseLogNmea(sNmea);
                break;
            case BT747Constants.PMTK_TEST: // CMD 000
                break;
            case BT747Constants.PMTK_ACK: // CMD 001
                result = handler.analyseMTK_Ack(sNmea);
                break;
            case BT747Constants.PMTK_SYS_MSG: // CMD 010
                break;
            case BT747Constants.PMTK_DT_FIX_CTL: // CMD 500
                if (sNmea.length >= 2) {
                    logFixPeriod = JavaLibBridge.toInt(sNmea[1]);
                    postEvent(GpsEvent.UPDATE_FIX_PERIOD);
                }
                dataOK |= MtkModel.C_OK_FIX;
                break;
            case BT747Constants.PMTK_DT_DGPS_MODE: // CMD 501
                if (sNmea.length == 2) {
                    dgpsMode = JavaLibBridge.toInt(sNmea[1]);
                }
                dataOK |= MtkModel.C_OK_DGPS;
                postEvent(GpsEvent.UPDATE_DGPS_MODE);
                break;
            case BT747Constants.PMTK_DT_SBAS: // CMD 513
                if (sNmea.length == 2) {
                    SBASEnabled = (sNmea[1].equals("1"));
                }
                dataOK |= MtkModel.C_OK_SBAS;
                postEvent(GpsEvent.UPDATE_SBAS);
                break;
            case BT747Constants.PMTK_DT_NMEA_OUTPUT: // CMD 514
                if (sNmea.length - 1 == BT747Constants.C_NMEA_SEN_COUNT) {
                    for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
                        NMEA_periods[i] = JavaLibBridge.toInt(sNmea[i + 1]);
                    }
                }
                dataOK |= MtkModel.C_OK_NMEA;
                postEvent(GpsEvent.UPDATE_OUTPUT_NMEA_PERIOD);
                break;
            case BT747Constants.PMTK_DT_SBAS_TEST: // CMD 513
                if (sNmea.length == 2) {
                    SBASTestEnabled = (sNmea[1].equals("0"));
                }
                dataOK |= MtkModel.C_OK_SBAS_TEST;
                postEvent(GpsEvent.UPDATE_SBAS_TEST);
                break;
            case BT747Constants.PMTK_DT_PWR_SAV_MODE: // CMD 520
                if (sNmea.length == 2) {
                    powerSaveEnabled = (sNmea[1].equals("1"));
                }
                postEvent(GpsEvent.UPDATE_PWR_SAV_MODE);
                break;
            case BT747Constants.PMTK_DT_DATUM: // CMD 530
                if (sNmea.length == 2) {
                    datum = JavaLibBridge.toInt(sNmea[1]);
                }
                dataOK |= MtkModel.C_OK_DATUM;
                postEvent(GpsEvent.UPDATE_DATUM);
                break;
            case BT747Constants.PMTK_DT_FLASH_USER_OPTION: // CMD 590
                dtUserOptionTimesLeft = JavaLibBridge.toInt(sNmea[1]);
                dtUpdateRate = JavaLibBridge.toInt(sNmea[2]);
                dtBaudRate = JavaLibBridge.toInt(sNmea[3]);
                dtGLL_Period = JavaLibBridge.toInt(sNmea[4]);
                dtRMC_Period = JavaLibBridge.toInt(sNmea[5]);
                dtVTG_Period = JavaLibBridge.toInt(sNmea[6]);
                dtGSA_Period = JavaLibBridge.toInt(sNmea[7]);
                dtGSV_Period = JavaLibBridge.toInt(sNmea[8]);
                dtGGA_Period = JavaLibBridge.toInt(sNmea[9]);
                dtZDA_Period = JavaLibBridge.toInt(sNmea[10]);
                dtMCHN_Period = JavaLibBridge.toInt(sNmea[11]);
                postEvent(GpsEvent.UPDATE_FLASH_CONFIG);
                break;
            case BT747Constants.PMTK_DT_BT_MAC_ADDR: // CMD 592
                if (sNmea[1].length() == 12) {
                    sBtMacAddr = sNmea[1].substring(10, 12) + ":"
                            + sNmea[1].substring(8, 10) + ":"
                            + sNmea[1].substring(6, 8) + ":"
                            + sNmea[1].substring(4, 6) + ":"
                            + sNmea[1].substring(2, 4) + ":"
                            + sNmea[1].substring(0, 2);
                }
                postEvent(GpsEvent.UPDATE_BT_MAC_ADDR);
                break;
            case BT747Constants.PMTK_DT_DGPS_INFO: // CMD 702
                /* Not handled */
                break;
            case BT747Constants.PMTK_DT_VERSION: // CMD 704
                mainVersion = sNmea[1] + "." + sNmea[2] + "." + sNmea[3];
                setAvailable(MtkModel.DATA_MTK_VERSION);
                postEvent(GpsEvent.UPDATE_MTK_VERSION);
                break;
            case BT747Constants.PMTK_DT_RELEASE: // CMD 705
                firmwareVersion = sNmea[1];
                model = sNmea[2];
                if (sNmea.length >= 4) {
                    device = sNmea[3];
                    firmwareVersion += " (" + device + ")";
                } else {
                    device = "";
                }
                setAvailable(MtkModel.DATA_MTK_RELEASE);
                postEvent(GpsEvent.UPDATE_MTK_RELEASE);
                break;

            default:
                break;
            } // End switch
        } else if (sNmea[0].equals("HOLUX001")) {
            holux = true;
            result = -1; // Suppose cmd not treated
            if (Generic.isDebug()) {
                String s;
                final int length = sNmea.length;

                s = "<";
                for (int i = 0; i < length; i++) {
                    s += sNmea[i];
                    s += ",";
                }
                Generic.debug(s);
            }
            cmd = JavaLibBridge.toInt(sNmea[1]);

            result = -1; // Suppose cmd not treated
            switch (cmd) {
            case BT747Constants.HOLUX_API_DT_NAME:
                if (sNmea.length == 3) {
                    holuxName = sNmea[2];
                    postEvent(GpsEvent.UPDATE_HOLUX_NAME);
                }
                break;
            default:
                break;
            }
        } // End if

        return result;
    } // End method

    protected final void postEvent(final int eventNbr) {
        context.postEvent(eventNbr);
    }

    protected final void postEvent(final int eventNbr, final Object o) {
        context.postGpsEvent(eventNbr, o);
    }

    /*************************************************************************
     * Getters and setters.
     */

    /**
     * @return The useful bytes in the log.
     */
    public final int logMemUsefullSize() {
        return ((getLogMemSize() >> 16) * (0x10000 - 0x200)); // 16Mb
    }

    /**
     * @return Useful free bytes in the log.
     */
    public final int logFreeMemUsefullSize() {
        return ((getLogMemSize() - getLogMemUsed()) - (((getLogMemSize() - getLogMemUsed()) >> 16) * (0x200))); // 16Mb
    }

    /**
     * Returns the current mac address for bluetooth (Holux 241 devices).
     * 
     * @return Bluetooth Mac Address
     */
    public final String getBtMacAddr() {
        return sBtMacAddr;
    }

    /**
     * @return Returns the flashManuProdID.
     */
    public final int getFlashManuProdID() {
        return flashManuProdID;
    }

    /**
     * @return Returns the flashDesc.
     */
    public final String getFlashDesc() {
        return BT747Constants.getFlashDesc(flashManuProdID);
    }

    /**
     * @return Returns the mtkLogVersion.
     */
    public final String getMtkLogVersion() {
        return MtkLogVersion;
    }

    /**
     * @return Returns the holux.
     */
    public final boolean isHolux() {
        return holux;
    }

    /**
     * @param forceHolux
     *                Indicates if this device needs special holux decoding.
     */
    public final void setHolux(final boolean forceHolux) {
        holux = forceHolux;
    }

    /**
     * @return Returns the holuxName.
     */
    public final String getHoluxName() {
        return holuxName;
    }

    public final int getLogFormat() {
        return logFormat;
    }

    public final int getDtUpdateRate() {
        return dtUpdateRate;
    }

    public final int getDtGLL_Period() {
        return dtGLL_Period;
    }

    public final int getDtRMC_Period() {
        return dtRMC_Period;
    }

    public final int getDtVTG_Period() {
        return dtVTG_Period;
    }

    public final int getDtGSA_Period() {
        return dtGSA_Period;
    }

    public final int getDtGSV_Period() {
        return dtGSV_Period;
    }

    public final int getDtGGA_Period() {
        return dtGGA_Period;
    }

    public final int getDtZDA_Period() {
        return dtZDA_Period;
    }

    public final int getDtMCHN_Period() {
        return dtMCHN_Period;
    }

    public final int getDtBaudRate() {
        return dtBaudRate;
    }

    public final int getDtUserOptionTimesLeft() {
        return dtUserOptionTimesLeft;
    }

    public final int getLogTimeInterval() {
        return logTimeInterval;
    }

    public final int getLogSpeedInterval() {
        return logSpeedInterval;
    }

    public final int getLogDistanceInterval() {
        return logDistanceInterval;
    }

    public final int getLogFixPeriod() {
        return logFixPeriod;
    }

    public final boolean isSBASEnabled() {
        return SBASEnabled;
    }

    public final boolean isSBASTestEnabled() {
        return SBASTestEnabled;
    }

    public final boolean isPowerSaveEnabled() {
        return powerSaveEnabled;
    }

    public final boolean isInitialLogOverwrite() {
        return (initialLogMode & BT747Constants.PMTK_LOG_STATUS_LOGSTOP_OVER_MASK) == 0;
    }

    public final int getDgpsMode() {
        return dgpsMode;
    }

    /**
     * @param loggingActive
     *                the loggingActive to set
     */
    protected void setLoggingActive(final boolean loggingActive) {
        this.loggingActive = loggingActive;
    }

    public final boolean isLogFullOverwrite() {
        return logFullOverwrite;
    }

    public final void setDatum(final int datum) {
        this.datum = datum;
    }

    public final int getDatum() {
        return datum;
    }

    public final int getNMEAPeriod(final int i) {
        return NMEA_periods[i];
    }
    
    private boolean eraseOngoing = false;

    protected final boolean isEraseOngoing() {
        return eraseOngoing;
    }

    protected final void setEraseOngoing(final boolean eraseOngoing) {
        if (this.eraseOngoing != eraseOngoing) {
            this.eraseOngoing = eraseOngoing;
            if (eraseOngoing) {
                postEvent(GpsEvent.ERASE_ONGOING_NEED_POPUP);
            } else {
                postEvent(GpsEvent.ERASE_DONE_REMOVE_POPUP);
                handler.setEraseOngoing(false);
            }
        }
    }


    /**
     * Get the 'logging activation' status of the device.
     * 
     * @return true if the device is currently logging positions to memory.
     */
    public final boolean isLoggingActive() {
        return loggingActive;
    }

    /**
     * Get the 'automatic logging activation' status of the device.<br>
     * This concerns time, speed and distance logging.
     * 
     * @return true if the device is currently automatically logging positions
     *         to memory.
     */
    public final boolean isLoggingDisabled() {
        return loggerIsDisabled;
    }
    
    public final boolean isLoggerNeedsFormat() {
        return loggerNeedsInit;
    }

    public final String getFirmwareVersion() {
        return firmwareVersion;
    }

    public final String getMainVersion() {
        return mainVersion;
    }

    /**
     * Get the total size of the memory.
     * 
     * @return the logMemSize
     */
    public final int getLogMemSize() {
        return BT747Constants.getFlashSize(flashManuProdID);
    }

    /**
     * Get the amount of memory used.
     * 
     * @param logMemUsed
     *                the logMemUsed to set
     */
    private void setLogMemUsed(final int logMemUsed) {
        this.logMemUsed = logMemUsed;
    }

    /**
     * @return the logMemUsed
     */
    public final int getLogMemUsed() {
        return logMemUsed;
    }

    /**
     * Get a representation of the model.
     * 
     * @return Textual identification of the device model.
     */
    private final String modelName() {
        return BT747Constants.modelName(Conv.hex2Int(model), device);
    }

    public final String getModel() {
        return model.length() != 0 ? model + " (" + modelName() + ')' : "";
    }

    /**
     * Get the start address for the log download. To be used for the download
     * progress bar.
     * 
     * @return the startAddr
     */
    public final int getStartAddr() {
        return mtkLogHandler.getStartAddr();
    }

    /**
     * Get the end address for the log download. To be used for the download
     * progress bar.
     * 
     * @return the endAddr
     */
    public final int getEndAddr() {
        return mtkLogHandler.getEndAddr();
    }

    /**
     * Get 'download ongoing' status.
     * 
     * @return true if the download is currently ongoing. This is usefull for
     *         the download progress bar.
     */
    public final boolean isLogDownloadOnGoing() {
        return mtkLogHandler.isLogDownloadOnGoing();
    }

    /**
     * Get the log address that we are now expecting to receive data for. This
     * is usefull for the download progress bar.
     * 
     * @return the nextReadAddr
     */
    public final int getNextReadAddr() {
        return mtkLogHandler.getNextReadAddr();
    }
    
    /**
     * @deprecated
     */
    public final MTKLogDownloadHandler getMtkLogHandler() {
        return mtkLogHandler;
    }
}
