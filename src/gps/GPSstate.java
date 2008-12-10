//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package gps;

import gps.connection.GPSrxtx;
import gps.convert.Conv;
import gps.log.GPSRecord;
import gps.log.in.CommonIn;

import bt747.sys.Convert;
import bt747.sys.Generic;
import bt747.sys.Interface;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747StringTokenizer;
import bt747.sys.interfaces.BT747Thread;

/**
 * GPSstate maintains a higher level state of communication with the GPS device.
 * It currently contains very specific commands MTK loggers but that could
 * change in the future by extending GPSstate with such features in a derived
 * class.
 * 
 * @author Mario De Weerd
 * @see GPSrxtx
 * 
 */
/* Final for the moment */
public final class GPSstate implements BT747Thread {
    private final GPSLinkHandler handler = new GPSLinkHandler();
    private final MTKLogDownloadHandler mtkLogHandler = new MTKLogDownloadHandler(
            this);

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
    public boolean loggerNeedsInit = false;
    public boolean loggerIsDisabled = false;

    private boolean logFullOverwrite = false; // When true, overwrite log when
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

    private boolean GPS_STATS = false; // (!Settings.onDevice);

    private boolean holux = false; // True if Holux M-241 device detected

    private String holuxName = "";

    /**
     * Initialiser
     * 
     * 
     */
    public GPSstate(final GPSrxtx gpsRxTx) {
        setGPSRxtx(gpsRxTx);
    }

    public final void setGPSRxtx(final GPSrxtx gpsRxTx) {
        handler.setGPSRxtx(gpsRxTx);
    }

    public final void setStats(final boolean stats) {
        GPS_STATS = stats;
    }

    private final boolean[] dataAvailable = new boolean[DATA_LAST_INDEX + 1];
    private final int[] dataRequested = new int[DATA_LAST_INDEX + 1];
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
    public final static int DATA_MEM_USED = 0;
    public final static int DATA_MEM_PTS_LOGGED = 1;
    public final static int DATA_FLASH_TYPE = 2;
    public final static int DATA_LOG_FORMAT = 3;
    public final static int DATA_MTK_VERSION = 4;
    public final static int DATA_MTK_RELEASE = 5;
    public final static int DATA_INITIAL_LOG = 6;
    public final static int DATA_LOG_STATUS = 7;
    public final static int DATA_LOG_VERSION = 8;
    private final static int DATA_LAST_INDEX = 8; // The last possible index

    /**
     * Reset the availability of all values - e.g. after loss of connection.
     */
    public final void resetAvailable() {
        for (int i = 0; i < dataAvailable.length; i++) {
            dataAvailable[i] = false;
            dataRequested[i] = 0;
        }
        nextValueToCheck = 0;
    }

    private int nextValueToCheck = 0;

    /**
     * Called regularly to check if values are available and request them.
     */
    private void checkNextAvailable() {
        int next = nextValueToCheck;
        checkAvailable(next);
        next += 1;
        if (next >= dataAvailable.length) {
            next = 0;
        }
        nextValueToCheck = next;
    }

    /**
     * Check if the data is available and if it is not, requests it.
     * 
     * @param dataType
     * @return
     */
    public final boolean checkAvailable(final int dataType) {
        int ts = Generic.getTimeStamp();
        if ((dataTimesOut[dataType] || !dataAvailable[dataType])
                && ((ts - dataRequested[dataType]) > 3500)) {
            dataRequested[dataType] = ts;
            switch (dataType) {
            case DATA_MEM_USED:
                reqLogMemUsed();
                break;
            case DATA_MEM_PTS_LOGGED:
                reqLogMemPtsLogged();
                break;
            case DATA_FLASH_TYPE:
                reqFlashManuID();
                break;
            case DATA_LOG_FORMAT:
                reqLogFormat();
                break;
            case DATA_MTK_VERSION:
                reqDeviceVersion();
                break;
            case DATA_MTK_RELEASE:
                reqDeviceRelease();
                break;
            case DATA_INITIAL_LOG:
                reqInitialLogMode();
                break;
            case DATA_LOG_STATUS:
                reqLogStatus();
                break;
            case DATA_LOG_VERSION:
                reqMtkLogVersion();
                break;
            default:
                break;
            }
            return false;
        } else {
            return true;
        }
    }

    private void setAvailable(final int dataType) {
        dataAvailable[dataType] = true;
        switch (dataType) {
        case DATA_FLASH_TYPE:

            break;
        case DATA_LOG_FORMAT:
            dataOK |= C_OK_FORMAT;
            break;
        default:
        case DATA_MEM_USED:
            break;
        }
    }

    public final boolean  isAvailable(final int dataType) {
        return dataAvailable[dataType];
    }

    private void setChanged(final int dataType) {
        dataAvailable[dataType] = false;
        dataRequested[dataType] = 0; // Just changed it - oblige 'timeout'.
        checkAvailable(dataType);
    }

    /**
     * @return The useful bytes in the log.
     */
    public final int logMemUsefullSize() {
        checkAvailable(DATA_FLASH_TYPE);
        return (int) ((getLogMemSize() >> 16) * (0x10000 - 0x200)); // 16Mb
    }

    /**
     * @return Useful free bytes in the log.
     */
    public final int logFreeMemUsefullSize() {
        checkAvailable(DATA_FLASH_TYPE);
        checkAvailable(DATA_MEM_USED);
        return (int) ((getLogMemSize() - getLogMemUsed()) - (((getLogMemSize() - getLogMemUsed()) >> 16) * (0x200))); // 16Mb
    }

    /**
     * Start the timer To be called once the port is opened. The timer is used
     * to launch functions that will check if there is information on the serial
     * connection or to send to the GPS device.
     */
    public final void setupTimer() {
        // TODO: set up thread in gpsRxTx directly (through controller)
        if (handler.isConnected()) {
            nextRun = Generic.getTimeStamp() + 300; // Delay before first
            // transaction
            Generic.addThread(this, false);
        }
    }

    /**
     * Set the logging format of the device. <br>
     * Best followed by eraseLog.
     * 
     * @param newLogFormat
     *            The format to set.
     */
    public final void setLogFormat(final int newLogFormat) {
        // Ensure option consistency.
        int logFmt;
        logFmt = newLogFormat;
        if ((logFmt & (1 << BT747Constants.FMT_SID_IDX)) == 0) {
            // If SID is not set, some other settings can not be
            // set either. Be sure they are disabled in that
            // case.
            logFmt &= ~((1 << BT747Constants.FMT_ELEVATION_IDX)
                    | (1 << BT747Constants.FMT_AZIMUTH_IDX) | (1 << BT747Constants.FMT_SNR_IDX));
        }
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_SET_STR + ","
                + BT747Constants.PMTK_LOG_FORMAT_STR + ","
                + Convert.unsigned2hex(logFmt, 8));
        setChanged(DATA_LOG_FORMAT);
    }

    public final void doHotStart() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_HOT_START_STR);
    }

    public final void doWarmStart() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_WARM_START_STR);
    }

    public final void doColdStart() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_COLD_START_STR);
    }

    public final void doFullColdStart() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_FULL_COLD_START_STR);
    }

    private final void reqDeviceVersion() {
        sendNMEA("PMTK" + BT747Constants.PMTK_Q_VERSION_STR);
    }

    private final void reqDeviceRelease() {
        sendNMEA("PMTK" + BT747Constants.PMTK_Q_RELEASE_STR);
    }

    public final void reqDeviceInfo() {
        checkAvailable(DATA_MTK_RELEASE);
        checkAvailable(DATA_MTK_VERSION);
        checkAvailable(DATA_FLASH_TYPE);
        checkAvailable(DATA_LOG_VERSION);
    }

    /**
     * Request the initial log mode (the first value logged in memory). Will be
     * analyzed in {@link #analyseLogNmea(String[])}.<br>
     * Must be accessed through {@link #DATA_INITIAL_LOG}
     */
    private final void reqInitialLogMode() {
        mtkLogHandler.readLog(6, 2); // 6 is the log mode offset in the log,
        // 2 is the size
        // Required to know if log is in overwrite mode.
    }

    /**
     * Must be accessed through {@link #DATA_FLASH_TYPE}
     */
    private final void reqFlashManuID() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_FLASH_STR + "," + "9F");
    }

    public final void setDownloadTimeOut(final int downloadTimeOut) {
        handler.setDownloadTimeOut(downloadTimeOut);
    }

    /**
     * Request the current log format from the device.<br>
     * Must use {@link #checkAvailable(int)} and {@link #DATA_LOG_FORMAT}.
     */
    private final void reqLogFormat() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_FORMAT_STR);
    }

    /**
     * Request the current log status from the device.<br>
     * Must use {@link #checkAvailable(int)} and {@link #DATA_LOG_STATUS}.
     */
    protected final void reqLogStatus() {

        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_LOG_STATUS_STR);
    }

    /**
     * Requests the amount of memory currently used.<br>
     * Must use {@link #checkAvailable(int)} and {@link #DATA_MEM_USED}.
     */
    private final void reqLogMemUsed() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_MEM_USED_STR);
    }

    /**
     * Requests the number of points logged in memory.<br>
     * Must use {@link #checkAvailable(int)} and {@link #DATA_MEM_PTS_LOGGED}.
     */
    private final void reqLogMemPtsLogged() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_NBR_LOG_PTS_STR);
    }

    private final void reqMtkLogVersion() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_VERSION_STR);
    }

    public final void reqLogReasonStatus() {
        /* Get log distance interval */
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_DISTANCE_INTERVAL_STR);
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_SPEED_INTERVAL_STR);
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_TIME_INTERVAL_STR);

    }

    public final void reqLogFlashStatus() {
        /* Get flash status - immediate (not in output buffer) */
        /* Needed for erase */
        handler.doSendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_FLASH_STAT_STR);
    }

    public final void reqLogFlashSectorStatus() {
        /* Get flash status - immediate (not in output buffer) */
        /* Needed for erase */
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_FLASH_SECTORS_STR);
    }

    public final void logImmediate(final int value) {
        if (!loggingActive) {
            startLog();
        }
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_SET_STR + ","
                + BT747Constants.PMTK_LOG_USER + ","
                + Convert.unsigned2hex(value, 4));
    }

    public final void setLogTimeInterval(final int value) {
        int z_value = value;
        if (z_value != 0 && z_value > 36000) {
            z_value = 36000;
        }
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_SET_STR + ","
                + BT747Constants.PMTK_LOG_TIME_INTERVAL_STR + ","
                + Convert.toString(z_value));
    }

    public final void setLogDistanceInterval(final int value) {
        int z_value = value;
        if (z_value != 0 && z_value > 36000) {
            z_value = 36000;
        } else if (z_value != 0 && z_value < 1) {
            z_value = 1;
        }

        /* Get log distance interval */
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_SET_STR + ","
                + BT747Constants.PMTK_LOG_DISTANCE_INTERVAL_STR + ","
                + Convert.toString(z_value));
    }

    public final void setLogSpeedInterval(final int value) {
        int z_value = value;
        if (z_value != 0 && z_value > 36000) {
            z_value = 36000;
        } else if (z_value != 0 && z_value < 1) {
            z_value = 1;
        }
        /* Get log distance interval */
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_SET_STR + ","
                + BT747Constants.PMTK_LOG_SPEED_INTERVAL_STR + ","
                + Convert.toString(z_value * 10));
    }

    public final void setFixInterval(final int value) {
        int z_value = value;
        if (z_value > 30000) {
            z_value = 30000;
        } else if (z_value < 200) {
            z_value = 200;
        }

        /* Set log distance interval */
        sendNMEA("PMTK" + BT747Constants.PMTK_API_SET_FIX_CTL + ","
                + Convert.toString(z_value) + ",0,0,0.0,0.0");
    }

    public final void reqFixInterval() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_Q_FIX_CTL);
    }

    /** Get the current status of the device */
    public final void reqStatus() {
        checkAvailable(DATA_LOG_FORMAT);
        getLogCtrlInfo();
        // getLogReasonStatus();
        // getPowerSaveEnabled();
        // getSBASEnabled();
        // getDGPSMode();
        // getDatumMode();
        // getFixInterval();
        reqHoluxName(); // Mainly here to identify Holux device
    }

    public final void reqLogOnOffStatus() {
        reqLogStatus();
    }

    private final void getLogCtrlInfo() {
        checkAvailable(DATA_LOG_VERSION);
        checkAvailable(DATA_MEM_USED);
        checkAvailable(DATA_MEM_PTS_LOGGED);
        reqLogOverwrite();
    }

    /** Activate the logging by the device. */
    public final void startLog() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_ON);
        loggingActive = true; // This should be the result of the action.
        // The device will eventually tell the new status
    }

    /** Stop the automatic logging of the device. */
    public final void stopLog() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_OFF);
        loggingActive = false; // This should be the result of the action.
        // The device will eventually tell the new status
    }

    private boolean SBASEnabled = false;

    private boolean SBASTestEnabled = false;

    private boolean powerSaveEnabled = false;

    public final void setLogOverwrite(final boolean set) {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_SET_STR + ","
                + BT747Constants.PMTK_LOG_REC_METHOD_STR + ","
                + (set ? "1" : "2"));
    }

    public final void reqLogOverwrite() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_REC_METHOD_STR);
    }

    public final void setSBASTestEnabled(final boolean set) {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_SET_SBAS_TEST_STR + ","
                + (set ? "0" : "1"));
    }

    public final void reqSBASTestEnabled() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_Q_SBAS_TEST_STR);
    }

    public final void setSBASEnabled(final boolean set) {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_SET_SBAS_STR + ","
                + (set ? "1" : "0"));
    }

    public final void reqSBASEnabled() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_Q_SBAS_STR);
    }

    public final void setPowerSaveEnabled(final boolean set) {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_SET_PWR_SAV_MODE_STR + ","
                + (set ? "1" : "0"));
    }

    public final void reqPowerSaveEnabled() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_Q_PWR_SAV_MOD_STR);
    }

    public final void setDGPSMode(final int mode) {
        // Request log format from device
        if (mode >= 0 && mode <= 2) {
            sendNMEA("PMTK" + BT747Constants.PMTK_API_SET_DGPS_MODE_STR + ","
                    + Convert.toString(mode));
        }
    }

    public final void reqDGPSMode() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_Q_DGPS_MODE_STR);
    }

    public final void setDatumMode(final int mode) {
        // Request log format from device
        if (mode >= 0 && mode <= 2) {
            sendNMEA("PMTK" + BT747Constants.PMTK_API_SET_DATUM_STR + ","
                    + Convert.toString(mode));
        }
    }

    public final void reqDatumMode() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_Q_DATUM_STR);
    }

    public final void reqNMEAPeriods() {
        sendNMEA("PMTK" + BT747Constants.PMTK_API_Q_NMEA_OUTPUT);
    }

    public final void reqHoluxName() {
        sendNMEA(BT747Constants.HOLUX_MAIN_CMD
                + BT747Constants.HOLUX_API_Q_NAME);
    }

    /**
     * @param holuxName
     *            The holuxName to set.
     */
    public final void setHoluxName(final String holuxName) {
        sendNMEA(BT747Constants.HOLUX_MAIN_CMD
                + BT747Constants.HOLUX_API_SET_NAME + "," + holuxName);
        reqHoluxName();
    }

    /**
     * Requests the current mac address for bluetooth (Holux 241 devices).
     */
    public final void reqBtMacAddr() {
        sendNMEA("PMTK" + BT747Constants.PMTK_API_Q_BT_MAC_ADDR);
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
     * Sets the current mac address for bluetooth (Holux 241 devices).
     * 
     * @param btMacAddr
     *            The Mac address to set in the following format:<br>
     *            00:1F:14:15:12:13.
     */
    public final void setBtMacAddr(final String btMacAddr) {
        String myMacAddr = "";
        BT747StringTokenizer fields = Interface.getStringTokenizerInstance(
                btMacAddr, ':');
        while (fields.hasMoreTokens()) {
            myMacAddr = fields.nextToken() + myMacAddr;
        }

        if (myMacAddr.length() == 12) {
            sendNMEA("PMTK" + BT747Constants.PMTK_API_SET_BT_MAC_ADDR + ","
                    + myMacAddr.substring(0, 6) + ","
                    + myMacAddr.substring(6, 12));
            reqBtMacAddr();
        }
    }

    public final void setNMEAPeriods(final int[] periods) {
        StringBuffer sb = new StringBuffer(255);
        sb.setLength(0);
        sb.append("PMTK" + BT747Constants.PMTK_API_SET_NMEA_OUTPUT);
        for (int i = 0; i < periods.length; i++) {
            sb.append(',');
            sb.append(periods[i]);
        }
        sendNMEA(sb.toString());
    }

    public final void setNMEADefaultPeriods() {
        int[] periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            periods[i] = 0;
        }
        periods[BT747Constants.NMEA_SEN_RMC_IDX] = 1;
        periods[BT747Constants.NMEA_SEN_GGA_IDX] = 1;
        periods[BT747Constants.NMEA_SEN_GSA_IDX] = 1;
        periods[BT747Constants.NMEA_SEN_GSV_IDX] = 1;
        periods[BT747Constants.NMEA_SEN_MDBG_IDX] = 1;
        setNMEAPeriods(periods);
        reqNMEAPeriods();
    }

    public final void setFlashUserOption(final boolean lock,
            final int updateRate, final int baudRate, final int GLL_Period,
            final int RMC_Period, final int VTG_Period, final int GSA_Period,
            final int GSV_Period, final int GGA_Period, final int ZDA_Period,
            final int MCHN_Period) {
        // Request log format from device
        sendNMEA("PMTK"
                + BT747Constants.PMTK_API_SET_USER_OPTION
                + ","
                + "0" // Lock:
                // currently
                // ignore
                // parameter
                + "," + GLL_Period + "," + RMC_Period + "," + VTG_Period + ","
                + GSA_Period + "," + GSV_Period + "," + GGA_Period + ","
                + ZDA_Period + "," + MCHN_Period);

    }

    public final void reqFlashUserOption() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_API_GET_USER_OPTION_STR);
    }

    /**
     * Analyze GPRMC data.
     * 
     * @param sNmea
     * @param gps
     */
    private final void analyzeGPRMC(final String[] sNmea, final GPSRecord gps) {
        if (CommonIn.analyzeGPRMC(sNmea, gps) != 0) {
            postGpsEvent(GpsEvent.GPRMC, gps);
        }
    }

    /**
     * Analyze GPGGA data.
     * 
     * @param sNmea
     * @param gps
     */
    private final void analyzeGPGGA(final String[] sNmea, final GPSRecord gps) {
        if (CommonIn.analyzeGPGGA(sNmea, gps) != 0) {
            gps.height -= gps.geoid; // Default function adds the two
            postGpsEvent(GpsEvent.GPGGA, gps);
        }
    }

    private boolean gpsDecode = true;
    private final GPSRecord gpsPos = new GPSRecord();

    public final int analyseNMEA(final String[] sNmea) {
        int cmd;
        int result;
        result = 0;
        try {
            // if(GPS_DEBUG&&!p_nmea[0].startsWith("G")) {
            // waba.sys.debugMsg("ANA:"+p_nmea[0]+","+p_nmea[1]);}
            if (sNmea.length == 0) {
                // Should not happen, problem in program
                Generic.debug("Problem - report NMEA is 0 length");
            } else if (sNmea.length == 1 && sNmea[0].startsWith("WP")) {
                AnalyseDPL700Data(sNmea[0]);
            } else if (gpsDecode && !isLogDownloadOnGoing() // Not
                    // during
                    // log
                    // download for
                    // performance.
                    && sNmea[0].length() != 0 && sNmea[0].charAt(0) == 'G') {
                // Commented - not interpreted.
                // Generic.debug("Before"+sNmea[0]+(new
                // java.util.Date(gpsPos.utc*1000L)).toString()+"("+gpsPos.utc+")");
                if (sNmea[0].startsWith("GPGGA")) {
                    analyzeGPGGA(sNmea, gpsPos);
                } else if (sNmea[0].startsWith("GPRMC")) {

                    analyzeGPRMC(sNmea, gpsPos);
                }
                // Generic.debug("After"+sNmea[0]+(new
                // java.util.Date(gpsPos.utc*1000L)+"("+gpsPos.utc+")").toString());
                // else if(p_nmea[0].startsWith("GPZDA")) {
                // // GPZDA,$time,$msec,$DD,$MO,$YYYY,03,00
                // } else if(p_nmea[0].startsWith("GPRMC")) {
                // //
                // GPRMC,$time,$fix,$latf1,$ns,$lonf1,$ew,$knots,$bear,$date,$magnvar,$magnew,$magnfix
                // } else if(p_nmea[0].startsWith("GPSTPV")) {
                // // GPSTPV,$epoch.$msec,?,$lat,$lon,,$alt,,$speed,,$bear,,,,A
                // }
            } else if (sNmea[0].startsWith("PMTK")) {
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
                cmd = Convert.toInt(sNmea[0].substring(4));

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
                        logFixPeriod = Convert.toInt(sNmea[1]);
                        postGpsEvent(GpsEvent.UPDATE_FIX_PERIOD, null);
                    }
                    dataOK |= C_OK_FIX;
                    break;
                case BT747Constants.PMTK_DT_DGPS_MODE: // CMD 501
                    if (sNmea.length == 2) {
                        dgpsMode = Convert.toInt(sNmea[1]);
                    }
                    dataOK |= C_OK_DGPS;
                    postEvent(GpsEvent.UPDATE_DGPS_MODE);
                    break;
                case BT747Constants.PMTK_DT_SBAS: // CMD 513
                    if (sNmea.length == 2) {
                        SBASEnabled = (sNmea[1].equals("1"));
                    }
                    dataOK |= C_OK_SBAS;
                    postEvent(GpsEvent.UPDATE_SBAS);
                    break;
                case BT747Constants.PMTK_DT_NMEA_OUTPUT: // CMD 514
                    if (sNmea.length - 1 == BT747Constants.C_NMEA_SEN_COUNT) {
                        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
                            NMEA_periods[i] = Convert.toInt(sNmea[i + 1]);
                        }
                    }
                    dataOK |= C_OK_NMEA;
                    postEvent(GpsEvent.UPDATE_OUTPUT_NMEA_PERIOD);
                    break;
                case BT747Constants.PMTK_DT_SBAS_TEST: // CMD 513
                    if (sNmea.length == 2) {
                        SBASTestEnabled = (sNmea[1].equals("0"));
                    }
                    dataOK |= C_OK_SBAS_TEST;
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
                        datum = Convert.toInt(sNmea[1]);
                    }
                    dataOK |= C_OK_DATUM;
                    postEvent(GpsEvent.UPDATE_DATUM);
                    break;
                case BT747Constants.PMTK_DT_FLASH_USER_OPTION: // CMD 590
                    dtUserOptionTimesLeft = Convert.toInt(sNmea[1]);
                    dtUpdateRate = Convert.toInt(sNmea[2]);
                    dtBaudRate = Convert.toInt(sNmea[3]);
                    dtGLL_Period = Convert.toInt(sNmea[4]);
                    dtRMC_Period = Convert.toInt(sNmea[5]);
                    dtVTG_Period = Convert.toInt(sNmea[6]);
                    dtGSA_Period = Convert.toInt(sNmea[7]);
                    dtGSV_Period = Convert.toInt(sNmea[8]);
                    dtGGA_Period = Convert.toInt(sNmea[9]);
                    dtZDA_Period = Convert.toInt(sNmea[10]);
                    dtMCHN_Period = Convert.toInt(sNmea[11]);
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
                    setAvailable(DATA_MTK_VERSION);
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
                    setAvailable(DATA_MTK_RELEASE);
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
                    int length = sNmea.length;

                    s = "<";
                    for (int i = 0; i < length; i++) {
                        s += sNmea[i];
                        s += ",";
                    }
                    Generic.debug(s);
                }
                cmd = Convert.toInt(sNmea[1]);

                result = -1; // Suppose cmd not treated
                switch (cmd) {
                case BT747Constants.HOLUX_API_DT_NAME:
                    if (sNmea.length == 3) {
                        this.holuxName = sNmea[2];
                        postEvent(GpsEvent.UPDATE_HOLUX_NAME);
                    }
                    break;
                default:
                    break;
                }
            } // End if
        } catch (Exception e) {
            Generic.debug("AnalyzeNMEA", e);
        }
        return result;
    } // End method

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
     *            the logMemUsed to set
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

    /***************************************************************************
     * Thread methods implementation
     */

    private int nextRun = 0;
    private int nextAvailableRun = 0;

    /*
     * (non-Javadoc)
     * 
     * @see waba.sys.Thread#run()
     */
    public final void run() {
        int timeStamp = Generic.getTimeStamp();
        if (timeStamp >= nextRun) {
            nextRun = timeStamp + 10;
            int loopsToGo = 0; // Setting to 0 for more responsiveness
            if (handler.isConnected()) {
                mtkLogHandler.notifyRun();
                String[] lastResponse;
                do {
                    lastResponse = handler.getResponse();
                    if (lastResponse != null) {
                        analyseNMEA(lastResponse);
                    }
                    handler.checkSendCmdFromQueue();
                } while ((loopsToGo-- > 0) && lastResponse != null);
                if ((nextAvailableRun < timeStamp)
                        && (getOutStandingCmdsCount() == 0)
                        && !isLogDownloadOnGoing()) {
                    nextAvailableRun = nextRun + 300;
                    checkNextAvailable();
                }
            } else {
                Generic.removeThread(this);
                mtkLogHandler.notifyDisconnected();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.sys.Thread#started()
     */
    public final void started() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.sys.Thread#stopped()
     */
    public final void stopped() {

    }

    /***************************************************************************
     * LOGGING FUNCTIONALITY
     **************************************************************************/

    /**
     * <code>dataOK</code> indicates if all volatile data from the device has
     * been fetched. This is usefull to know if the settings can be backed up.
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

    /**
     * @param sNmea
     *            Elements of the NMEA packet to analyze. <br>
     *            Example: PMTK182,3,4 <br>
     *            nmea[0] PMTK182 <br>
     *            nmea[1] 3 <br>
     *            nmea[2] 4
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
            switch (Convert.toInt(sNmea[1])) {
            case BT747Constants.PMTK_LOG_DT:
                // Parameter information
                // TYPE = Parameter type
                // DATA = Parameter data
                // $PMTK182,3,TYPE,DATA
                int z_type = Convert.toInt(sNmea[2]);
                if (sNmea.length == 4) {
                    switch (z_type) {
                    case BT747Constants.PMTK_LOG_FLASH_STAT:
                        mtkLogHandler.handleLogFlashStatReply(sNmea[3]);
                        break;
                    case BT747Constants.PMTK_LOG_FORMAT: // 2;
                        // if(GPS_DEBUG) {
                        // waba.sys.debugMsg("FMT:"+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+","+p_nmea[3]+"\n");}
                        logFormat = Conv.hex2Int(sNmea[3]);
                        setAvailable(DATA_LOG_FORMAT);
                        postEvent(GpsEvent.UPDATE_LOG_FORMAT);
                        break;
                    case BT747Constants.PMTK_LOG_TIME_INTERVAL: // 3;
                        logTimeInterval = Convert.toInt(sNmea[3]);
                        dataOK |= C_OK_TIME;
                        postEvent(GpsEvent.UPDATE_LOG_TIME_INTERVAL);
                        break;
                    case BT747Constants.PMTK_LOG_DISTANCE_INTERVAL: // 4;
                        logDistanceInterval = Convert.toInt(sNmea[3]);
                        dataOK |= C_OK_DIST;
                        postEvent(GpsEvent.UPDATE_LOG_DISTANCE_INTERVAL);
                        break;
                    case BT747Constants.PMTK_LOG_SPEED_INTERVAL: // 5;
                        logSpeedInterval = Convert.toInt(sNmea[3]) / 10;
                        dataOK |= C_OK_SPEED;
                        postEvent(GpsEvent.UPDATE_LOG_SPEED_INTERVAL);
                        break;
                    case BT747Constants.PMTK_LOG_REC_METHOD: // 6;
                        logFullOverwrite = (Convert.toInt(sNmea[3]) == 1);
                        postEvent(GpsEvent.UPDATE_LOG_REC_METHOD);
                        break;
                    case BT747Constants.PMTK_LOG_LOG_STATUS: // 7; // bit 2 =
                        // logging
                        // on/off
                        logStatus = Convert.toInt(sNmea[3]);
                        // logFullOverwrite = (((logStatus &
                        // BT747Constants.PMTK_LOG_STATUS_LOGSTOP_OVER_MASK) !=
                        // 0));
                        loggingActive = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGONOF_MASK) != 0));
                        loggerIsFull = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGISFULL_MASK) != 0));
                        loggerNeedsInit = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGMUSTINIT_MASK) != 0));
                        loggerIsDisabled = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGDISABLED_MASK) != 0));
                        setAvailable(DATA_LOG_STATUS);
                        postEvent(GpsEvent.UPDATE_LOG_LOG_STATUS);
                        break;
                    case BT747Constants.PMTK_LOG_MEM_USED: // 8;
                        setLogMemUsed(Conv.hex2Int(sNmea[3]));
                        logMemUsedPercent = (100 * (getLogMemUsed() - (0x200 * ((getLogMemUsed() + 0xFFFF) / 0x10000))))
                                / logMemUsefullSize();
                        setAvailable(DATA_MEM_USED);
                        postEvent(GpsEvent.UPDATE_LOG_MEM_USED);
                        break;
                    case BT747Constants.PMTK_LOG_FLASH: // 9;
                        flashManuProdID = Conv.hex2Int(sNmea[3]);
                        setAvailable(DATA_FLASH_TYPE);
                        postEvent(GpsEvent.UPDATE_LOG_FLASH);
                        break;
                    case BT747Constants.PMTK_LOG_NBR_LOG_PTS: // 10;
                        logNbrLogPts = Conv.hex2Int(sNmea[3]);
                        setAvailable(DATA_MEM_PTS_LOGGED);
                        postEvent(GpsEvent.UPDATE_LOG_NBR_LOG_PTS);
                        break;
                    case BT747Constants.PMTK_LOG_FLASH_SECTORS: // 11;
                        postEvent(GpsEvent.UPDATE_LOG_FLASH_SECTORS);
                        break;
                    case BT747Constants.PMTK_LOG_VERSION: // 12:
                        MtkLogVersion = "V"
                                + Convert.toString(
                                        Convert.toInt(sNmea[3]) / 100f, 2);
                        setAvailable(DATA_LOG_VERSION);
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
                        initialLogMode = Conv.hex2Int(sNmea[3].substring(0, 4));
                        // correct endian.
                        initialLogMode = (initialLogMode & 0xFF << 8)
                                | (initialLogMode >> 8);
                        setAvailable(DATA_INITIAL_LOG);
                    }
                } catch (Exception e) {
                    // Do not care about exception
                }
                // logFullOverwrite = (((logStatus &
                // BT747Constants.PMTK_LOG_STATUS_LOGSTOP_OVER_MASK) != 0));

                try {
                    // waba.sys.debugMsg("Before
                    // AnalyzeLog:"+p_nmea[3].length());
                    mtkLogHandler.analyzeLogPart(Conv.hex2Int(sNmea[2]),
                            sNmea[3]);
                } catch (Exception e) {
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

    /***************************************************************************
     * Getters and Setters
     * 
     */

    /**
     * @return Returns the gpsDecode.
     */
    public final boolean isGpsDecode() {
        return gpsDecode;
    }

    /**
     * @param gpsDecode
     *            Activate gps decoding if true, do not decode if false. This
     *            may improve performance.
     */
    public final void setGpsDecode(final boolean gpsDecode) {
        this.gpsDecode = gpsDecode;
        updateIgnoreNMEA();
    }

    protected final void updateIgnoreNMEA() {
        handler.setIgnoreNMEA((!this.gpsDecode)
                || isLogDownloadOnGoing());
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
     *            Indicates if this device needs special holux decoding.
     */
    public final void setHolux(final boolean forceHolux) {
        this.holux = forceHolux;
    }

    /**
     * @return Returns the holuxName.
     */
    public final String getHoluxName() {
        return holuxName;
    }

    protected void postGpsEvent(final int type, final Object o) {
        postEvent(new GpsEvent(type, o));
    }

    public final GPSRecord getGpsRecord() {
        return gpsPos;
    }

    private final BT747HashSet listeners = Interface.getHashSetInstance();

    /** add a listener to event thrown by this class */
    public final void addListener(final GPSListener l) {
        listeners.add(l);
    }

    public final void removeListener(final GPSListener l) {
        listeners.remove(l);
    }

    protected void postEvent(final int event) {
        postEvent(new GpsEvent(event));
    }

    protected final void postEvent(final GpsEvent e) {
        BT747HashSet it = listeners.iterator();
        while (it.hasNext()) {
            GPSListener l = (GPSListener) it.next();
            l.gpsEvent(e);
        }
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

    public final boolean isLogFullOverwrite() {
        return logFullOverwrite;
    }

    public final void setDatum(final int datum) {
        this.datum = datum;
    }

    public final int getDatum() {
        return datum;
    }

    // DPL700 Functionality
    private String DPL700LogFileName;
    private int DPL700Card;
    private static final int C_DPL700_OFF = 0;
    private static final int C_DPL700_NEEDGETLOG = 1;
    private static final int C_DPL700_GETLOG = 2;
    private int DPL700_State = C_DPL700_OFF;

    public final void getDPL700Log(final String p_FileName, final int card) {
        DPL700LogFileName = p_FileName;
        DPL700Card = card;
        enterDPL700Mode();
        DPL700_State = C_DPL700_NEEDGETLOG;
    }

    public final void reqDPL700Log() {
        DPL700_State = C_DPL700_GETLOG;
        handler.sendCmdAndGetDPL700Response(0x60B50000, 10 * 1024 * 1024);
        // m_GPSrxtx.virtualReceive("sample dataWP Update Over\0");
    }

    public final void enterDPL700Mode() {
        exitDPL700Mode(); // Exit previous session if still open
        handler.sendCmdAndGetDPL700Response("W'P Camera Detect", 255);
        // m_GPSrxtx.virtualReceive("WP GPS+BT\0");
    }

    public final void exitDPL700Mode() {
        handler.sendDPL700Cmd("WP AP-Exit"); // No reply expected
        DPL700_State = C_DPL700_OFF;
    }

    public final void reqDPL700LogSize() {
        handler.sendCmdAndGetDPL700Response(0x60B50000, 255);
    }

    public final void reqDPL700Erase() {
        handler.sendCmdAndGetDPL700Response(0x60B50000, 255);
    }

    public final void reqDPL700DeviceInfo() {
        handler.sendCmdAndGetDPL700Response(0x5BB00000, 255);
    }

    public final void getDPL700GetSettings() {
        handler.sendCmdAndGetDPL700Response(0x62B60000, 255);
    }

    private void AnalyseDPL700Data(final String s) {
        if (Generic.isDebug()) {
            Generic.debug("<DPL700 " + s);
        }
        if (s.startsWith("WP GPS")) {
            // WP GPS+BT
            // Response to W'P detect
            if (DPL700_State == C_DPL700_NEEDGETLOG) {
                reqDPL700Log();
            }
        } else if (s.startsWith("WP Update Over")) {
            if (DPL700_State == C_DPL700_GETLOG) {
                if (DPL700LogFileName != null) {
                    if (!DPL700LogFileName.endsWith(".sr")) {
                        DPL700LogFileName += ".sr";
                    }
                    mtkLogHandler.openNewLog(DPL700LogFileName, DPL700Card);
                    try {
                        mtkLogHandler.getLogFile().writeBytes(
                                handler.getDPL700_buffer(), 0,
                                handler.getDPL700_buffer_idx());
                        mtkLogHandler.getLogFile().close();
                    } catch (Exception e) {
                        Generic.debug("", e);
                        // TODO: handle exception
                    }
                    DPL700LogFileName = null;
                    exitDPL700Mode();
                }
            }
        }
    }

    public final void setLogRequestAhead(final int logRequestAhead) {
        mtkLogHandler.setLogRequestAhead(logRequestAhead);
    }

    public final int getNMEAPeriod(final int i) {
        return NMEA_periods[i];
    }

    /**
     * Send an NMEA string to the link. The parameter should not include the
     * checksum - this is added by the method.
     * 
     * @param s
     *            NMEA string to send.
     */
    public final void sendNMEA(final String s) {
        handler.sendNMEA(s);
    }

    /**
     * Get the number of Cmds that are still waiting to be sent and/or waiting
     * for acknowledgement.
     * 
     * @return
     */
    public final int getOutStandingCmdsCount() {
        return handler.getOutStandingCmdsCount();
    }

    public final boolean isLoggingActive() {
        return loggingActive;
    }

    protected final boolean isEraseOngoing() {
        return handler.isEraseOngoing();
    }

    protected final void setEraseOngoing(final boolean eraseOngoing) {
        handler.setEraseOngoing(eraseOngoing);
    }

    protected final int timeSinceLastStamp() {
        return handler.timeSinceLastStamp();
    }

    protected final void resetLogTimeOut() {
        handler.resetLogTimeOut();
    }

    protected final boolean isConnected() {
        return handler.isConnected();
    }

    public final void eraseLog() {
        mtkLogHandler.eraseLog();
    }

    /**
     * A 'recovery Erase' attempts to recover memory that was previously
     * identified as 'bad'.
     */
    public final void recoveryEraseLog() {
        mtkLogHandler.recoveryEraseLog();

    }

    /**
     * The GpsModel is waiting for a reply to the question if the currently
     * existing log with different data can be overwritten. This method must be
     * called to replay to this question which is in principle the result of a
     * user reply to a message box.
     * 
     * @param isOkToOverwrite
     *            If true, the existing log can be overwritten
     */
    public final void replyToOkToOverwrite(final boolean isOkToOverwrite) {
        mtkLogHandler.replyToOkToOverwrite(isOkToOverwrite);
    }

    /**
     * The log is being erased - the user request to abandon waiting for the end
     * of this operation.
     */
    public final void stopErase() {
        mtkLogHandler.stopErase();
    }

    public final void getLogInit(final int startAddr, final int endAddr,
            final int requestStep, final String fileName, final int card,
            final boolean isIncremental // True if incremental read
    ) {
        mtkLogHandler.getLogInit(startAddr, endAddr, requestStep, fileName,
                card, isIncremental);
    }

    /**
     * Cancel the log download process.
     */
    public final void cancelGetLog() {
        mtkLogHandler.cancelGetLog();
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
}