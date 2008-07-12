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
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***
//***  WabaSoft, Inc.                                              ***
//********************************************************************                              
package gps;

import gps.connection.GPSrxtx;
import gps.convert.Conv;
import gps.log.GPSRecord;
import moio.util.HashSet;
import moio.util.Iterator;
import moio.util.StringTokenizer;

import bt747.generic.Generic;
import bt747.io.File;
import bt747.sys.Convert;
import bt747.sys.Thread;
import bt747.sys.Vm;
import bt747.util.Vector;

/*
 * Created on 12 may 2007
 */

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
public class GPSstate implements Thread {
    private boolean GPS_DEBUG = false; // (!Settings.onDevice);

    private GPSrxtx gpsRxTx = null;

    private boolean m_getFullLog = true; // If true, get the entire log
    // (based
    // on block head)

    private int logFormat = 0;

    public int logRecordMaxSize = 0;

    private int logTimeInterval = 0;

    private int logSpeedInterval = 0;

    private int logDistanceInterval = 0;

    public int logStatus = 0;

    public int logRecMethod = 0;

    public int logNbrLogPts = 0;

    public int logMemSize = 16 * 1024 * 1024 / 8; // 16Mb -> 2MB

    public int logMemUsed = 0;

    public int logMemUsedPercent = 0;

    public int logMemFree = 0;

    public int logMemMax = 0;

    private int logFixPeriod = 0; // Time between fixes

    private int datum = 0; // Datum WGS84, TOKYO-M, TOKYO-A

    public boolean isLoggingActive = false;
    public boolean loggerIsFull = false;
    public boolean loggerNeedsInit = false;
    public boolean loggerIsDisabled = false;
    public boolean forcedErase = false;

    public boolean loggingIsActiveBeforeDownload = false;

    private boolean logFullOverwrite = false; // When true, overwrite log when
    // device is full

    private int dgps_mode = 0;

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
    private int FlashManuProdID = 0;

    private String FlashDesc = "";
    
    private String BT_MAC_ADDR_STR="";

    public int[] NMEA_periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

    private boolean GPS_STATS = false; // (!Settings.onDevice);

    private static final int C_LOGERASE_TIMEOUT = 2000; // Timeout between log
    // status requests for
    // erase.

    private boolean holux = false; // True if Holux M-241 device detected

    private String holuxName = "";

    /**
     * Initialiser
     * 
     * 
     */
    public GPSstate(final GPSrxtx gpsRxTx) {
        this.setGPSRxtx(gpsRxTx);
    }
    
    public final void setGPSRxtx(final GPSrxtx gpsRxTx) {
        if(this.gpsRxTx != null) {
            // TODO  Remove myself as listener
        }
        this.gpsRxTx = gpsRxTx;
        // TODO: Add myself as listener
    }

    public final void setDebug(final boolean dbg) {
        GPS_DEBUG = dbg;
    }
    
    public final boolean isDebug() {
        return GPS_DEBUG;
    }

    public final void setStats(final boolean stats) {
        GPS_STATS = stats;
    }

    /**
     * @return The usefull bytes int the log.
     */
    public final int logMemUsefullSize() {
        return (int) ((logMemSize >> 16) * (0x10000 - 0x200)); // 16Mb
    }

    /**
     * Start the timer To be called once the port is opened. The timer is used
     * to launch functions that will check if there is information on the serial
     * connection or to send to the GPS device.
     */
    public final void setupTimer() {
        // TODO: set up thread in gpsRxTx directly (through controller)
        if (gpsRxTx.isConnected()) {
            Generic.addThread(this, false);
        }
    }

    /**
     * Set the logging format of the device. <br>
     * Must be followed by eraseLog.
     * 
     * @param p_logFormat
     *            The format to set.
     */
    public final void setLogFormat(final int p_logFormat) {
        // Ensure option consistency.
        int logFmt;
        logFmt = p_logFormat;
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
    }


    private boolean isEraseOngoing=false;
    private void waitEraseDone() {
        isEraseOngoing=true;
        postEvent(GpsEvent.ERASE_ONGOING_NEED_POPUP);
        m_logState = C_LOG_ERASE_STATE;
        resetLogTimeOut();
        // readLogFlashStatus(); - Will be done after timeout
    }
    private void signalEraseDone() {
        postEvent(GpsEvent.ERASE_DONE_REMOVE_POPUP);
    }
    
    public final void stopErase() {
        if (isEraseOngoing && (m_logState == C_LOG_ERASE_STATE) ) {
            m_logState = C_LOG_NOLOGGING;
            gpsRxTx.setIgnoreNMEA(!gpsDecode);
            postEvent(GpsEvent.ERASE_DONE_REMOVE_POPUP);
        }
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

    public final void reqDeviceInfo() {
        sendNMEA("PMTK" + BT747Constants.PMTK_Q_VERSION_STR);
        sendNMEA("PMTK" + BT747Constants.PMTK_Q_RELEASE_STR);
        readFlashManuID(); // Should be last
        reqLoggerVersion();
    }

    /**
     * erase the log - takes a while TODO: Find out a way to follow up on erasal
     * (status) (check response on cmd)
     */

    public final void eraseLog() {
        if (gpsRxTx.isConnected()) {
            sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                    + BT747Constants.PMTK_LOG_ERASE + ","
                    + BT747Constants.PMTK_LOG_ERASE_YES_STR);
            waitEraseDone();
        }
    }

    public final void recoveryEraseLog() {
        // Get some information (when debug mode active)
        stopLog(); // Stop logging for this operation
        reqLogStatus(); // Check status
        reqLogFlashSectorStatus(); // Get flash sector information from device
        // TODO:
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_ENABLE);

        reqLogStatus(); // Check status

        forcedErase = true;
        eraseLog();

    }

    private void postRecoveryEraseLog() {
        reqLogStatus();
        reqLogFlashSectorStatus(); // Get flash sector information from device

        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_INIT);

        reqLogFlashSectorStatus(); // Get flash sector information from device
        reqLogStatus();
    }

    // public static final int PMTK_LOG_ENABLE = 10;
    // public static final int PMTK_LOG_DISABLE = 11;

    /**
     * A single request to get information from the device's log.
     * 
     * @param startAddr
     *            start address of the data range requested
     * @param size
     *            size of the data range requested
     */
    public final void readLog(final int startAddr, final int size) {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_REQ_DATA_STR + ","
                + Convert.unsigned2hex(startAddr, 8) + ","
                + Convert.unsigned2hex(size, 8));
    }

    public final void readFlashManuID() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_FLASH_STR + "," + "9F");
    }

    static final int C_SEND_BUF_SIZE = 5;

    Vector sentCmds = new Vector(); // List of sent commands

    static final int C_MAX_SENT_COMMANDS = 10; // Max commands to put in list

    Vector toSendCmds = new Vector(); // List of sent commands

    static final int C_MAX_TOSEND_COMMANDS = 20; // Max commands to put in
    // list

    static final int C_MAX_CMDS_SENT = 4;

    static final int C_MIN_TIME_BETWEEN_CMDS = 30;

    public final void sendNMEA(final String p_Cmd) {
        int cmdsWaiting;
        cmdsWaiting = sentCmds.size();
        if ((logStatus != C_LOG_ERASE_STATE) && (cmdsWaiting == 0)
                && (Vm.getTimeStamp() > nextCmdSendTime)) {
            // All sent commands were acknowledged, send cmd immediately
            doSendNMEA(p_Cmd);
        } else if (cmdsWaiting < C_MAX_TOSEND_COMMANDS) {
            // Ok to buffer more cmds
            toSendCmds.addElement(p_Cmd);
        }
    }

    private int nextCmdSendTime = 0;

    private void doSendNMEA(final String p_Cmd) {
        resetLogTimeOut();
        if (p_Cmd.startsWith("PMTK")) {
            sentCmds.addElement(p_Cmd);
        }
        gpsRxTx.sendPacket(p_Cmd);
        if (GPS_DEBUG) {
            debugMsg(">" + p_Cmd);
        }
        nextCmdSendTime = Vm.getTimeStamp() + C_MIN_TIME_BETWEEN_CMDS;
        if (sentCmds.size() > C_MAX_SENT_COMMANDS) {
            sentCmds.removeElementAt(0);
        }
        // if (GPS_DEBUG) {
        // debugMsg(p_Cmd);
        // }
    }

    private int downloadTimeOut=3500;
    
    public final void setDownloadTimeOut(final int downloadTimeOut) {
        this.downloadTimeOut = downloadTimeOut;
    }

    private void checkSendCmdFromQueue() {
        int cTime = Vm.getTimeStamp();
        if (logStatus != C_LOG_ERASE_STATE) {
            if ((sentCmds.size() != 0)
                    && (cTime - logTimer) >= downloadTimeOut) {
                // TimeOut!!
                sentCmds.removeElementAt(0);
                logTimer = cTime;
            }
            if ((toSendCmds.size() != 0) && (sentCmds.size() < C_MAX_CMDS_SENT)
                    && (Vm.getTimeStamp() > nextCmdSendTime)) {
                // No more commands waiting for acknowledge
                doSendNMEA((String) toSendCmds.elementAt(0));
                toSendCmds.removeElementAt(0);
            }
        }
    }

    /** Request the current log format from the device */
    public final void reqLogFormat() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_FORMAT_STR);
    }

    public final void reqLogStatus() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_LOG_STATUS_STR);
    }

    public final void reqLogMemUsed() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_MEM_USED_STR);
    }

    public final void reqLogMemPoints() {
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_QUERY_STR + ","
                + BT747Constants.PMTK_LOG_NBR_LOG_PTS_STR);
    }

    public final void reqLoggerVersion() {
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
        doSendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
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
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_SET_STR + "," + BT747Constants.PMTK_LOG_USER
                + "," + Convert.toString(value));
    }

    public final void setLogTimeInterval(final int value) {
        int z_value = value;
        if (z_value != 0 && z_value > 999) {
            z_value = 999;
        }
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_SET_STR + ","
                + BT747Constants.PMTK_LOG_TIME_INTERVAL_STR + ","
                + Convert.toString(z_value));
    }

    public final void setLogDistanceInterval(final int value) {
        int z_value = value;
        if (z_value != 0 && z_value > 9999) {
            z_value = 9999;
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
        if (z_value != 0 && z_value > 999) {
            z_value = 999;
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
    public final void getStatus() {
        reqLogFormat();
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
        reqLoggerVersion();
        // Request mem size from device
        reqLogMemUsed();
        // Request number of log points
        reqLogMemPoints();
        reqLogOverwrite();
    }

    /** Activate the logging by the device */
    public final void startLog() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_ON);
        isLoggingActive = true; // This should be the result of the action.
                                // The device will eventually tell the new status
    }

    /** Stop the automatic logging of the device */
    public final void stopLog() {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_OFF);
        isLoggingActive = false; // This should be the result of the action.
                                 // The device will eventually tell the new status
    }

    private boolean SBASEnabled = false;

    private boolean SBASTestEnabled = false;

    private boolean powerSaveEnabled = false;

    public final void setLogOverwrite(final boolean set) {
        // Request log format from device
        sendNMEA("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                + BT747Constants.PMTK_LOG_SET_STR + ","
                + BT747Constants.PMTK_LOG_REC_METHOD_STR + "," + (set ? "1" : "2"));
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
        sendNMEA(BT747Constants.HOLUX_MAIN_CMD + BT747Constants.HOLUX_API_Q_NAME);
    }

    /**
     * @param holuxName
     *            The holuxName to set.
     */
    public final void setHoluxName(final String holuxName) {
        sendNMEA(BT747Constants.HOLUX_MAIN_CMD + BT747Constants.HOLUX_API_SET_NAME + ","
                + holuxName);
        reqHoluxName();
    }

    /** Returns the current mac address for bluetooth (Holux 241 devices)
     * @return
     */
    public final void reqBT_MAC_ADDR() {
        sendNMEA("PMTK" + BT747Constants.PMTK_API_Q_BT_MAC_ADDR);
    }

    /** Returns the current mac address for bluetooth (Holux 241 devices)
     * @return
     */
    public final String getBT_MAC_ADDR() {
        return BT_MAC_ADDR_STR;
    }

    /** Sets the current mac address for bluetooth (Holux 241 devices)
     * TODO
     */
    public final void setBT_MAC_ADDR(final String bt_mac_addr) {
        String MyMacAddr="";
        StringTokenizer Fields = new StringTokenizer(bt_mac_addr,",");
        while(Fields.hasMoreTokens()) {
            MyMacAddr=Fields.nextToken()+MyMacAddr;
        }

        if(MyMacAddr.length()==12) {
            sendNMEA("PMTK" + BT747Constants.PMTK_API_SET_BT_MAC_ADDR
                    + "," + MyMacAddr.substring(0,6)
                    + "," + MyMacAddr.substring(6,12) );
            reqBT_MAC_ADDR();
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
        int[] Periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            Periods[i] = 0;
        }
        Periods[BT747Constants.NMEA_SEN_RMC_IDX] = 1;
        Periods[BT747Constants.NMEA_SEN_GGA_IDX] = 1;
        Periods[BT747Constants.NMEA_SEN_GSA_IDX] = 1;
        Periods[BT747Constants.NMEA_SEN_GSV_IDX] = 1;
        Periods[BT747Constants.NMEA_SEN_MDBG_IDX] = 1;
        setNMEAPeriods(Periods);
        reqNMEAPeriods();
    }

    public final void setFlashUserOption(final boolean lock, final int updateRate,
            final int baudRate, final int GLL_Period, final int RMC_Period,
            final int VTG_Period, final int GSA_Period, final int GSV_Period,
            final int GGA_Period, final int ZDA_Period, final int MCHN_Period) {
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

    final boolean removeFromSentCmds(final String match) {
        int z_CmdIdx = -1;
        for (int i = 0; i < sentCmds.size(); i++) {
            if (((String) sentCmds.elementAt(i)).startsWith(match)) {
                z_CmdIdx = i;
                break;
            }

        }
        // Remove all cmds up to
        for (int i = z_CmdIdx; i >= 0; i--) {
            // if(GPS_DEBUG) {
            // debugMsg("Remove:"+(String)sentCmds.items[0]);
            // }
            sentCmds.removeElementAt(0);
        }
        return z_CmdIdx != -1;
    }

    // TODO: When acknowledge is missing for some commands, take appropriate
    // action.
    public final int analyseMTK_Ack(final String[] p_nmea) {
        // PMTK001,Cmd,Flag
        int z_Flag;
        int z_Result = -1;
        // if(GPS_DEBUG) { waba.sys.debugMsg(p_nmea[0]+","+p_nmea[1]+"\n");}

        if (p_nmea.length >= 3) {
            String z_MatchString;
            z_Flag = Convert.toInt(p_nmea[p_nmea.length - 1]); // Last
            // parameter
            z_MatchString = "PMTK" + p_nmea[1];
            for (int i = 2; i < p_nmea.length - 1; i++) {
                // ACK is variable length, can have parameters of cmd.
                z_MatchString += "," + p_nmea[i];
            }
            // if(GPS_DEBUG) {
            // debugMsg("Before:"+sentCmds.size()+" "+z_MatchString);
            // }

            removeFromSentCmds(z_MatchString);
            // if(GPS_DEBUG) {
            // debugMsg("After:"+sentCmds.size());
            // }
            // sentCmds.find(z_MatchString);
            // if(GPS_DEBUG) {
            // waba.sys.debugMsg("IDX:"+Convert.toString(z_CmdIdx)+"\n");}
            // if(GPS_DEBUG) {
            // waba.sys.debugMsg("FLAG:"+Convert.toString(z_Flag)+"\n");}
            switch (z_Flag) {
            case BT747Constants.PMTK_ACK_INVALID:
                // 0: Invalid cmd or packet
                z_Result = 0;
                break;
            case BT747Constants.PMTK_ACK_UNSUPPORTED:
                // 1: Unsupported cmd or packet
                z_Result = 0;
                break;
            case BT747Constants.PMTK_ACK_FAILED:
                // 2: Valid cmd or packet but action failed
                z_Result = 0;
                break;
            case BT747Constants.PMTK_ACK_SUCCEEDED:
                // 3: Valid cmd or packat but action succeeded
                z_Result = 0;
                break;
            default:
                z_Result = -1;
                break;
            }
        }
        return z_Result;
    }

    GPSRecord gps = new GPSRecord();

    final void analyzeGPRMC(final String[] p_nmea) {
        if (p_nmea.length >= 11) {
            gps.utc = Convert.toInt(p_nmea[1].substring(0, 2)) * 3600
                    + Convert.toInt(p_nmea[1].substring(2, 4)) * 60
                    + Convert.toInt(p_nmea[1].substring(4, 6));
            postGpsEvent(GpsEvent.GPRMC, gps);
        }
    }

    final void analyzeGPGGA(final String[] p_nmea) {
        // Partial decode to compare height with calculated geoid.
        if (p_nmea.length >= 15) {
            try {
                // GPSRecord gps = new GPSRecord();
                gps.latitude = (Convert.toDouble(p_nmea[2].substring(0, 2)) + Convert
                        .toDouble(p_nmea[2].substring(2)) / 60)
                        * (p_nmea[3].equals("N") ? 1 : -1);
                gps.longitude = (Convert.toDouble(p_nmea[4].substring(0, 3)) + Convert
                        .toDouble(p_nmea[4].substring(3)) / 60)
                        * (p_nmea[5].equals("E") ? 1 : -1);
                gps.height = Convert.toFloat(p_nmea[9]);
                gps.geoid = Convert.toFloat(p_nmea[11]);
                // if(GPS_DEBUG) {
                // double geoid=Conv.wgs84_separation(gps.latitude,
                // gps.longitude);
                // debugMsg("geoid GPS: "+Convert.toString(gps.geoid,3)
                // + " geoid calc:"+Convert.toString(geoid)
                // );
                // }
                postGpsEvent(GpsEvent.GPRMC, gps);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    boolean gpsDecode = true;

    public final int analyseNMEA(final String[] p_nmea) {
        int z_Cmd;
        int z_Result;
        z_Result = 0;
        // if(GPS_DEBUG&&!p_nmea[0].startsWith("G")) {
        // waba.sys.debugMsg("ANA:"+p_nmea[0]+","+p_nmea[1]);}
        if (p_nmea.length == 0) {
            // Shoudl not happen, problem in program
            debugMsg("Problem - report NMEA is 0 length");
        } else if (p_nmea.length == 1 && p_nmea[0].startsWith("WP")) {
            AnalyseDPL700Data(p_nmea[0]);
        } else if (gpsDecode && (m_logState == C_LOG_NOLOGGING) // Not during
                // log
                // download for
                // performance.
                && p_nmea[0].startsWith("G")) {
            // Commented - not interpreted.
            if (p_nmea[0].startsWith("GPGGA")) {
                analyzeGPGGA(p_nmea);
            } else if (p_nmea[0].startsWith("GPRMC")) {
                analyzeGPRMC(p_nmea);
            }
            // else if(p_nmea[0].startsWith("GPZDA")) {
            // // GPZDA,$time,$msec,$DD,$MO,$YYYY,03,00
            // } else if(p_nmea[0].startsWith("GPRMC")) {
            // //
            // GPRMC,$time,$fix,$latf1,$ns,$lonf1,$ew,$knots,$bear,$date,$magnvar,$magnew,$magnfix
            // } else if(p_nmea[0].startsWith("GPSTPV")) {
            // // GPSTPV,$epoch.$msec,?,$lat,$lon,,$alt,,$speed,,$bear,,,,A
            // }
        } else if (p_nmea[0].startsWith("PMTK")) {
            if (GPS_DEBUG) {
                String s;
                int length = p_nmea.length;
                if (p_nmea[1].charAt(0) == '8') {
                    length = 3;
                }
                s = "<";
                for (int i = 0; i < length; i++) {
                    s += p_nmea[i];
                    s += ",";
                }
                debugMsg(s);
            }
            z_Cmd = Convert.toInt(p_nmea[0].substring(4));

            z_Result = -1; // Suppose cmd not treated
            switch (z_Cmd) {
            case BT747Constants.PMTK_CMD_LOG: // CMD 182;
                z_Result = analyseLogNmea(p_nmea);
                break;
            case BT747Constants.PMTK_TEST: // CMD 000
                break;
            case BT747Constants.PMTK_ACK: // CMD 001
                z_Result = analyseMTK_Ack(p_nmea);
                break;
            case BT747Constants.PMTK_SYS_MSG: // CMD 010
                break;
            case BT747Constants.PMTK_DT_FIX_CTL: // CMD 500
                if (p_nmea.length >= 2) {
                    logFixPeriod = Convert.toInt(p_nmea[1]);
                }
                dataOK |= C_OK_FIX;
                break;
            case BT747Constants.PMTK_DT_DGPS_MODE: // CMD 501
                if (p_nmea.length == 2) {
                    dgps_mode = Convert.toInt(p_nmea[1]);
                }
                dataOK |= C_OK_DGPS;
                PostStatusUpdateEvent();
                break;
            case BT747Constants.PMTK_DT_SBAS: // CMD 513
                if (p_nmea.length == 2) {
                    SBASEnabled = (p_nmea[1].equals("1"));
                }
                dataOK |= C_OK_SBAS;
                PostStatusUpdateEvent();
                break;
            case BT747Constants.PMTK_DT_NMEA_OUTPUT: // CMD 514
                if (p_nmea.length - 1 == BT747Constants.C_NMEA_SEN_COUNT) {
                    for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
                        NMEA_periods[i] = Convert.toInt(p_nmea[i + 1]);
                    }
                }
                dataOK |= C_OK_NMEA;
                PostStatusUpdateEvent();
                break;
            case BT747Constants.PMTK_DT_SBAS_TEST: // CMD 513
                if (p_nmea.length == 2) {
                    SBASTestEnabled = (p_nmea[1].equals("0"));
                }
                dataOK |= C_OK_SBAS_TEST;
                PostStatusUpdateEvent();
                break;
            case BT747Constants.PMTK_DT_PWR_SAV_MODE: // CMD 520
                if (p_nmea.length == 2) {
                    powerSaveEnabled = (p_nmea[1].equals("1"));
                }
                PostStatusUpdateEvent();
                break;
            case BT747Constants.PMTK_DT_DATUM: // CMD 530
                if (p_nmea.length == 2) {
                    datum = Convert.toInt(p_nmea[1]);
                }
                dataOK |= C_OK_DATUM;
                PostStatusUpdateEvent();
                break;
            case BT747Constants.PMTK_DT_FLASH_USER_OPTION: // CMD 590

                dtUserOptionTimesLeft = Convert.toInt(p_nmea[1]);
                dtUpdateRate = Convert.toInt(p_nmea[2]);
                dtBaudRate = Convert.toInt(p_nmea[3]);
                dtGLL_Period = Convert.toInt(p_nmea[4]);
                dtRMC_Period = Convert.toInt(p_nmea[5]);
                dtVTG_Period = Convert.toInt(p_nmea[6]);
                dtGSA_Period = Convert.toInt(p_nmea[7]);
                dtGSV_Period = Convert.toInt(p_nmea[8]);
                dtGGA_Period = Convert.toInt(p_nmea[9]);
                dtZDA_Period = Convert.toInt(p_nmea[10]);
                dtMCHN_Period = Convert.toInt(p_nmea[11]);

                PostStatusUpdateEvent();
                break;
            case BT747Constants.PMTK_DT_BT_MAC_ADDR: // CMD 592
                if(p_nmea[1].length()==12) {
                    BT_MAC_ADDR_STR=
                            p_nmea[1].substring(10, 12) +
                        ":"+p_nmea[1].substring( 8, 10) +
                        ":"+p_nmea[1].substring( 6,  8) +
                        ":"+p_nmea[1].substring( 4,  6) +
                        ":"+p_nmea[1].substring( 2,  4) +
                        ":"+p_nmea[1].substring( 0,  2);
                }
                PostStatusUpdateEvent();
                break;
            case BT747Constants.PMTK_DT_DGPS_INFO: // CMD 702
                /* Not handled */
                break;
            case BT747Constants.PMTK_DT_VERSION: // CMD 704
                mainVersion = p_nmea[1] + "." + p_nmea[2] + "." + p_nmea[3];
                PostStatusUpdateEvent();
                break;
            case BT747Constants.PMTK_DT_RELEASE: // CMD 705
                firmwareVersion = p_nmea[1];
                model = p_nmea[2];
                if (p_nmea.length >= 4) {
                    device = p_nmea[3];
                    firmwareVersion += " (" + device + ")";
                } else {
                    device = "";
                }
                PostStatusUpdateEvent();
                break;

            default:
                break;
            } // End switch
        } else if (p_nmea[0].equals("HOLUX001")) {
            holux = true;
            z_Result = -1; // Suppose cmd not treated
            if (GPS_DEBUG) {
                String s;
                int length = p_nmea.length;

                s = "<";
                for (int i = 0; i < length; i++) {
                    s += p_nmea[i];
                    s += ",";
                }
                debugMsg(s);
            }
            z_Cmd = Convert.toInt(p_nmea[1]);

            z_Result = -1; // Suppose cmd not treated
            switch (z_Cmd) {
            case BT747Constants.HOLUX_API_DT_NAME:
                if (p_nmea.length == 3) {
                    this.holuxName = p_nmea[2];
                    PostStatusUpdateEvent();
                }
                break;
                default:
                    break;
            }
        } // End if
        return z_Result;
    } // End method

    public final String getFirmwareVersion() {
        return firmwareVersion;
    }

    public final String getMainVersion() {
        return mainVersion;
    }

    private final String modelName() {
        int md = Conv.hex2Int(model);
        String mdStr;
        logMemSize = 16 * 1024 * 1024 / 8; // 16Mb -> 2MB
        holux = false;
        switch (md) {
        case 0x0000:
        case 0x0001:
        case 0x0013:
        case 0x0051:
            // Can also be Polaris iBT-GPS or Holux M1000
            mdStr = "iBlue 737/Qstarz 810";
            break;
        case 0x0002:
            mdStr = "Qstarz 815/iBlue 747";
            break;
        case 0x0005:
            mdStr = "Holux M-241/QT-1000P";
            break;
        case 0x0007:
            mdStr = "Just Mobile� Blucard";
            break;
        case 0x0011: // Seen in FCC OUP940760101
        case 0x001B:
            mdStr = "iBlue 747";
            break;
        case 0x001D:
            mdStr = "747/Q1000/BGL-32";
            break;
        case 0x0023:
            mdStr = "Holux M-241";
            break;
        case 0x0131:
            mdStr = "EB-85A";
            break;
        case 0x1388:
            mdStr = "757/ZI v1";
            // logMemSize = 8 * 1024 * 1024 / 8; //8Mb -> 1MB
            break;
        case 0x5202:
            mdStr = "757/ZI v2";
            // logMemSize = 8 * 1024 * 1024 / 8; //8Mb -> 1MB
            break;
        case 0x8300:
            mdStr = "Qstarz BT-1200";
            // logMemSize = 32 * 1024 * 1024 / 8; //32Mb -> 4MB
            break;
        default:
            mdStr = "?"; // Unknown model
        }
        // Recognition based on 'device'
        if (device.length() == 0) {
            // Do nothing
        } else if (device.startsWith("TSI747")) {
            mdStr = "iBlue 747";
        } else if (device.startsWith("TSI757")) {
            mdStr = "iBlue 757";
        } else if (device.equals("QST1000P")) {
            mdStr = "Qstarz BT-1000P";
        }
        return mdStr;
    }

    public final String getModel() {
        return model.length() != 0 ? model + " (" + modelName() + ')' : "";
    }

    private void analyseFlashManuProdID() {
        int Manufacturer;
        int DevType;

        Manufacturer = (FlashManuProdID >> 24) & 0xFF;
        DevType = (FlashManuProdID >> 16) & 0xFF;

        switch (Manufacturer) {
        case BT747Constants.SPI_MAN_ID_MACRONIX:
            if ((DevType == 0x20) || (DevType == 0x24)) {
                // +/* MX25L chips are SPI, first byte of device id is memory
                // type,
                // + second byte of device id is log(bitsize)-9 */
                // +#define MX_25L512 0x2010 /* 2^19 kbit or 2^16 kByte */
                // +#define MX_25L1005 0x2011
                // +#define MX_25L2005 0x2012
                // +#define MX_25L4005 0x2013 /* MX25L4005{,A} */
                // +#define MX_25L8005 0x2014
                // +#define MX_25L1605 0x2015 /* MX25L1605{,A,D} */
                // +#define MX_25L3205 0x2016 /* MX25L3205{,A} */
                // +#define MX_25L6405 0x2017 /* MX25L3205{,D} */
                // +#define MX_25L1635D 0x2415
                // +#define MX_25L3235D 0x2416
                logMemSize = 0x1 << ((FlashManuProdID >> 8) & 0xFF);
                FlashDesc = "(MX," + logMemSize / (1024 * 1024) + "MB)";

            }
            break;
        case BT747Constants.SPI_MAN_ID_EON:
            if ((DevType == 0x20)) { // || (DevType == 0x24)) {
                // Supposing the same rule as macronix.
                // Example device: EN25P16
                logMemSize = 0x1 << ((FlashManuProdID >> 8) & 0xFF);
                FlashDesc = "(EON," + logMemSize / (1024 * 1024) + "MB)";

            }
            break;

        default:
            break;
        }
    }

    /***************************************************************************
     * Thread methods implementation
     */

    private int nextRun = 0;

    /*
     * (non-Javadoc)
     * 
     * @see waba.sys.Thread#run()
     */
    public final void run() {
        String[] lastResponse;

        if (Vm.getTimeStamp() >= nextRun) {
            nextRun = Vm.getTimeStamp() + 10;
            int loops_to_go = 0; // Setting to 0 for more responsiveness
            if (gpsRxTx.isConnected()) {
                // debugMsg(Convert.toString(m_logState));
                if (((m_logState != C_LOG_NOLOGGING) && (m_logState != C_LOG_ERASE_STATE))
                        && (sentCmds.size() == 0) && (toSendCmds.size() == 0)) {
                    // Sending command on next timer adds some delay after
                    // the end of the previous command (reception)
                    getLogPartNoOutstandingRequests();
                } else if (m_logState == C_LOG_ACTIVE) {
                    getNextLogPart();
                } else if (m_logState == C_LOG_ERASE_STATE) {
                    if ((Vm.getTimeStamp() - logTimer) > C_LOGERASE_TIMEOUT) {
                        reqLogFlashStatus();
                    }
                }
                do {
                    lastResponse = gpsRxTx.getResponse();
                    if (lastResponse != null) {
                        analyseNMEA(lastResponse);
                    }
                    checkSendCmdFromQueue();
                } while ((loops_to_go-- > 0) && lastResponse != null);
            } else {
                Generic.removeThread(this);
                if (m_logState != C_LOG_NOLOGGING) {
                    endGetLog();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.sys.Thread#started()
     */
    public void started() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.sys.Thread#stopped()
     */
    public void stopped() {

    }

    /***************************************************************************
     * LOGGING FUNCTIONALITY
     **************************************************************************/
    // Fields to keep track of logging status
    private int logTimer = 0;

    private int m_StartAddr;

    private int m_EndAddr;

    private int m_NextReqAddr;

    private int m_NextReadAddr;

    private int m_Step;

    /** File handle for binary log being downloaded. */
    private File m_logFile = null;

    /** Card (for Palm) of binary log file. Defaults to last card in device. */
    private int m_logFileCard = -1;

    // States for log reception state machine.
    private static final int C_LOG_NOLOGGING = 0;

    private static final int C_LOG_CHECK = 1;

    private static final int C_LOG_ACTIVE = 2;

    private static final int C_LOG_RECOVER = 3;

    private static final int C_LOG_ERASE_STATE = 4;

    /**
     * Waiting for a reply from the application concerning the
     * authorisation to overwrite data that is not the same.
     */
    private static final int C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY = 5;
    
    private int m_logState = C_LOG_NOLOGGING;

    private int usedLogRequestAhead = 0;

    /**
     * Start of block position to verify if log in device corresponds to log in
     * file.
     */
    private static final int C_BLOCKVERIF_START = 0x200;

    /** Size of block to validate that log in device is log in file. */
    private static final int C_BLOCKVERIF_SIZE = 0x200;

    private static final int C_MAX_FILEBLOCK_WRITE = 0x800;

    private byte[] m_Data = new byte[0x800]; // buffer used for reading data.

    /**
     * Request the block to validate that log in device is log in file.
     */
    private void requestCheckBlock() {
        readLog(C_BLOCKVERIF_START, C_BLOCKVERIF_SIZE); // Read 200 bytes, just
        // past header.
    }

    /**
     * Resets the condition to determine if a log request timeout occurs.
     */
    private final void resetLogTimeOut() {
        logTimer = Vm.getTimeStamp();
    }

    private void closeLog() {
        try {
            if (m_logFile != null && m_logFile.isOpen()) {
                m_logFile.close();
            }
        } catch (Exception e) {
        }
    }

    private void endGetLog() {
        m_logState = C_LOG_NOLOGGING;
        closeLog();

        if (loggingIsActiveBeforeDownload) {
            startLog();
            reqLogOnOffStatus();
        }
        postEvent(GpsEvent.DOWNLOAD_STATE_CHANGE);
    }
    
    public final boolean isDownloadOnGoing() {
        return m_logState!=C_LOG_NOLOGGING;
    }
    
    public final int getStartAddr() {
        return m_StartAddr;
    }

    /**
     * @return the endAddr
     */
    public final int getEndAddr() {
        return m_EndAddr;
    }
    
    public final int getNextReadAddr() {
        return m_NextReadAddr;
    }

    public final void cancelGetLog() {
        endGetLog();
    }
    
    private int logRequestAhead=0;
    

    /**
     * @param p_StartAddr
     * @param p_EndAddr
     * @param p_Step
     * @param p_FileName
     */
    public final void getLogInit(final int p_StartAddr, final int p_EndAddr,
            final int p_Step, final String p_FileName, final int Card,
            final boolean incremental // True if incremental read
    ) {
        try {
            if (m_logState == C_LOG_NOLOGGING) {
                // Disable device logging while downloading
                loggingIsActiveBeforeDownload = isLoggingActive;
                stopLog();
                reqLogOnOffStatus();
            }

            m_StartAddr = p_StartAddr;
            m_EndAddr = ((p_EndAddr + 0xFFFF) & 0xFFFF0000) - 1;
            m_NextReqAddr = m_StartAddr;
            m_NextReadAddr = m_StartAddr;
            m_Step = p_Step;
            if (m_Step > 0x800) {
                usedLogRequestAhead = 0;
            } else {
                usedLogRequestAhead = logRequestAhead;
            }

            if (incremental) {
                reOpenLogRead(p_FileName, Card);
                if (m_logFile != null && m_logFile.isOpen()) {
                    // There is a file with data.
                    if (m_logFile.getSize() >= (C_BLOCKVERIF_START + C_BLOCKVERIF_SIZE)) {
                        // There are enough bytes in the saved file.

                        // Find first incomplete block
                        int blockHeadPos = 0;
                        boolean continueLoop;
                        do {
                            byte[] bytes = new byte[2];
                            m_logFile.setPos(blockHeadPos);
                            continueLoop = (m_logFile.readBytes(bytes, 0, 2) == 2);
                            if (continueLoop) {
                                // Break the loop if this block was incomplete.
                                continueLoop = !(((bytes[0] & 0xFF) == 0xFF) && ((bytes[1] & 0xFF) == 0xFF));
                            }
                            if (continueLoop) {
                                // This block is fully filled
                                blockHeadPos += 0x10000;
                                continueLoop = (blockHeadPos <= (m_logFile
                                        .getSize() & 0xFFFF0000));
                            }
                        } while (continueLoop);

                        if (blockHeadPos > m_logFile.getSize()) {
                            // All blocks already had data - continue from end
                            // of
                            // file.
                            m_NextReadAddr = m_logFile.getSize();
                            m_NextReqAddr = m_NextReadAddr;
                        } else {
                            // Start just past block header
                            m_NextReadAddr = blockHeadPos + 0x200;
                            continueLoop = true;
                            do {
                                // Find a block
                                m_logFile.setPos(m_NextReadAddr);
                                continueLoop = ((m_logFile.readBytes(m_Data, 0,
                                        0x200) == 0x200));
                                if (continueLoop) {
                                    // Check if all FFs in the file.
                                    for (int i = 0; continueLoop && (i < 0x200); i++) {
                                        continueLoop = ((m_Data[i] & 0xFF) == 0xFF);
                                    }
                                    continueLoop = !continueLoop; // Continue
                                    // if
                                    // something else
                                    // than 0xFF
                                    // found.
                                    if (continueLoop) {
                                        m_NextReadAddr += 0x200;
                                    }
                                }
                            } while (continueLoop);
                            m_NextReadAddr -= 0x200;
                            m_NextReqAddr = m_NextReadAddr;

                            // TODO: should read 2 bytes in header once rest of
                            // block was loaded
                            // in order to have precise header information
                            // -> We can not load this value from memory know as
                            // we
                            // might
                            // corrupt the data (0xFFFF present if restarting
                            // download)
                        }

                        requestCheckBlock();
                        m_logState = C_LOG_CHECK;
                    }
                }
                gpsRxTx.setIgnoreNMEA((!gpsDecode)
                        || (m_logState != C_LOG_NOLOGGING));
            }
            if (!(m_logState == C_LOG_CHECK)) {
                // File could not be opened or is not incremental.
                openNewLog(p_FileName, Card);
                m_logState = C_LOG_ACTIVE;
            }
            if(m_logState!=C_LOG_NOLOGGING) {
                postEvent(GpsEvent.DOWNLOAD_STATE_CHANGE);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void openNewLog(final String fileName, final int Card) {
        try {
            if (m_logFile != null && m_logFile.isOpen()) {
                m_logFile.close();
            }

            m_logFile = new File(fileName, bt747.io.File.DONT_OPEN, Card);
            m_logFileCard = Card;
            if (m_logFile.exists()) {
                m_logFile.delete();
            }

            m_logFile = new File(fileName, bt747.io.File.CREATE, Card);
            // lastError 10530 = Read only
            m_logFileCard = Card;
            m_logFile.close();
            m_logFile = new File(fileName, bt747.io.File.READ_WRITE, Card);
            m_logFileCard = Card;

            if ((m_logFile == null) || !(m_logFile.isOpen())) {
                postGpsEvent(GpsEvent.COULD_NOT_OPEN_FILE, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }
    
    private final void debugMsg(final String dbgStr) {
        postGpsEvent(GpsEvent.DEBUG_MSG, dbgStr);
    }

    private void reOpenLogRead(final String fileName, final int Card) {
        closeLog();
        try {
            m_logFile = new File(fileName, File.READ_ONLY, Card);
            m_logFileCard = Card;
        } catch (Exception e) {
            // debugMsg("Exception reopen log read");
        }
    }

    private void reOpenLogWrite(final String fileName, final int Card) {
        closeLog();
        try {
            m_logFile = new File(fileName, File.READ_WRITE, Card);
            m_logFileCard = Card;
        } catch (Exception e) {
            // debugMsg("Exception reopen log write");
        }
    }

    // Called regularly
    private void getNextLogPart() {
        if (m_logState != C_LOG_NOLOGGING) {
            int z_Step;
            z_Step = m_EndAddr - m_NextReqAddr + 1;

            switch (m_logState) {
            case C_LOG_ACTIVE:
                if (m_NextReqAddr > m_NextReadAddr + m_Step * usedLogRequestAhead) {
                    z_Step = 0;
                }
                break;
            case C_LOG_RECOVER:
                if (m_NextReqAddr > m_NextReadAddr) {
                    z_Step = 0;
                } else if (z_Step > 0x800) {
                    z_Step = 0x800;
                }
                break;
            default:
                z_Step = 0;
            }

            if (z_Step > 0) {
                if (z_Step > m_Step) {
                    z_Step = m_Step;
                }
                readLog(m_NextReqAddr, z_Step);
                m_NextReqAddr += z_Step;
                if (m_logState == C_LOG_ACTIVE) {
                    getNextLogPart(); // Recursive to get requests 'ahead'
                }
            }
        }
    }

    // Called when no outstanding requests
    private void getLogPartNoOutstandingRequests() {
        switch (m_logState) {
        case C_LOG_ACTIVE:
        case C_LOG_RECOVER:
            m_NextReqAddr = m_NextReadAddr;
            getNextLogPart();
            break;
        case C_LOG_CHECK:
            requestCheckBlock();
            default:
                break;
        }
    }

    private void recoverFromLogError() {
        m_NextReqAddr = m_NextReadAddr;
        m_logState = C_LOG_RECOVER;
    }

    private void analyzeLogPart(final int p_StartAddr, final String p_Data) {
        int dataLength;
        dataLength = Conv.hexStringToBytes(p_Data, m_Data) / 2; // Fills m_data
        // debugMsg("Got "+p_StartAddr+" "+Convert.toString(p_Data.length())+"):
        // "+Convert.toString(dataLength));
        switch (m_logState) {
        case C_LOG_ACTIVE:
        case C_LOG_RECOVER:
            if (m_NextReadAddr == p_StartAddr) {
                m_logState = C_LOG_ACTIVE;
                int j = 0;

                // The Palm platform showed problems writing 0x800 blocks.
                // This splits it in smaller blocks and solves that problem.
                if (dataLength != 0x800 && dataLength != m_Step
                        && ((m_NextReadAddr + dataLength) != m_NextReqAddr)) {
                    // Received data is not the right size - transmission error.
                    // Can happen on Palm over BT.
                    m_logState = C_LOG_RECOVER;
                } else {
                    // Data seems ok
                    for (int i = dataLength; i > 0; i -= C_MAX_FILEBLOCK_WRITE) {
                        int l = i;
                        if (l > C_MAX_FILEBLOCK_WRITE) {
                            l = C_MAX_FILEBLOCK_WRITE;
                        }
                        // debugMsg("Writing("+Convert.toString(p_StartAddr)+"):
                        // "+Convert.toString(j)+" "+Convert.toString(l));

                        try {
                            if ((m_logFile.writeBytes(m_Data, j, l)) != l) {
                                // debugMsg("Problem during anaLog:
                                // "+Convert.toString(m_logFile.lastError));
                                cancelGetLog();
                                // debugMsg(Convert.toString(q));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            cancelGetLog();
                        }
                        j += l;
                    }
                    m_NextReadAddr += dataLength;
                    // m_ProgressBar.repaintNow();
                    if (m_getFullLog
                            && (((p_StartAddr - 1 + dataLength) & 0xFFFF0000) >= p_StartAddr)) {
                        // Block boundery (0xX0000) is inside data.
                        int blockStart = 0xFFFF & (0x10000 - (p_StartAddr & 0xFFFF));
                        if (!(((m_Data[blockStart] & 0xFF) == 0xFF) && ((m_Data[blockStart + 1] & 0xFF) == 0xFF))) {
                            // This block is full, next block is still data
                            int minEndAddr;
                            minEndAddr = (p_StartAddr & 0xFFFF0000) + 0x20000 - 1; // This
                            // block
                            // and
                            // next
                            // one.
                            if (minEndAddr > logMemSize - 1) {
                                minEndAddr = logMemSize - 1;
                            }
                            if (minEndAddr > m_EndAddr) {
                                m_EndAddr = minEndAddr;
                            }
                        }
                    }
                }
                if (m_NextReadAddr > m_EndAddr) {
                    endGetLog();
                } else {
                    getNextLogPart();
                }
            } else {
                recoverFromLogError();
            }
            break;
        case C_LOG_CHECK:
            m_logState = C_LOG_NOLOGGING; // Default.
            if ((p_StartAddr == C_BLOCKVERIF_START)
                    && (dataLength == C_BLOCKVERIF_SIZE)) {
                // The block we got should be the block to check
                byte[] m_localdata = new byte[dataLength];
                int result = 0;
                boolean success = false;
                try {
                    m_logFile.setPos(p_StartAddr);
                    result = m_logFile.readBytes(m_localdata, 0, dataLength);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                if (result == dataLength) {
                    success = true;
                    for (int i = dataLength - 1; i >= 0; i--) {
                        if (m_Data[i] != m_localdata[i]) {
                            // The log is not the same, data is different
                            success = false;
                            break; // Exit from the loop
                        }
                    }
                }

                String fileName = m_logFile.getPath();
                if (success) {
                    // Downloaded data seems to correspond - start incremental
                    // download
                    reOpenLogWrite(fileName, m_logFileCard);
                    try {
                        m_logFile.setPos(m_NextReadAddr);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    m_logState = C_LOG_ACTIVE;
                } else {
                    postEvent(GpsEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY);
                }
            }
            break;
            default:
                break;
        } // Switch m_logState
        postEvent(GpsEvent.DOWNLOAD_STATE_CHANGE);
    }
    
    public final void replyToOkToOverwrite(final boolean overwrite) {
        if(m_logState==C_LOG_DATA_NOT_SAME_WAITING_FOR_REPLY) {
            if(overwrite) {
                String fileName = m_logFile.getPath();
                openNewLog(fileName, m_logFileCard);
                m_NextReadAddr = 0;
                m_NextReadAddr = 0;
                m_logState = C_LOG_ACTIVE;
            } else {
                endGetLog();
            }
        }
    }

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
     * @param nmea
     *            Elements of the NMEA packet to analyze. <br>
     *            Example: PMTK182,3,4 <br>
     *            nmea[0] PMTK182 <br>
     *            nmea[1] 3 <br>
     *            nmea[2] 4
     * @return
     */
    private int analyseLogNmea(final String[] nmea) {
        // if(GPS_DEBUG) {
        // waba.sys.debugMsg("LOG:"+p_nmea.length+':'+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+"\n");}
        // Suppose that the command is ok (PMTK182)

        // Currently taking care of replies from the device only.
        // The other data we send ourselves
        resetLogTimeOut(); // Reset timeout
        if (nmea.length > 2) {
            switch (Convert.toInt(nmea[1])) {
            case BT747Constants.PMTK_LOG_DT:
                // Parameter information
                // TYPE = Parameter type
                // DATA = Parameter data
                // $PMTK182,3,TYPE,DATA
                int z_type = Convert.toInt(nmea[2]);
                if (nmea.length == 4) {
                    switch (z_type) {
                    case BT747Constants.PMTK_LOG_FLASH_STAT:
                        if (m_logState == C_LOG_ERASE_STATE) {
                            switch (Convert.toInt(nmea[3])) {
                            case 1:
                                m_logState = C_LOG_NOLOGGING;
                                if(isEraseOngoing) {
                                    signalEraseDone();
                                }
                                if (forcedErase) {
                                    forcedErase = false;
                                    postRecoveryEraseLog();
                                }
                                break;
                                default:
                                    break;
                            }
                        }
                        break;

                    case BT747Constants.PMTK_LOG_FORMAT: // 2;
                        // if(GPS_DEBUG) {
                        // waba.sys.debugMsg("FMT:"+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+","+p_nmea[3]+"\n");}
                        logFormat = Conv.hex2Int(nmea[3]);
                        logRecordMaxSize = BT747Constants.logRecordMinSize(
                                logFormat, false);
                        dataOK |= C_OK_FORMAT;
                        postEvent(GpsEvent.LOG_FORMAT_UPDATE);
                        break;
                    case BT747Constants.PMTK_LOG_TIME_INTERVAL: // 3;
                        logTimeInterval = Convert.toInt(nmea[3]);
                        dataOK |= C_OK_TIME;
                        PostStatusUpdateEvent();
                        break;
                    case BT747Constants.PMTK_LOG_DISTANCE_INTERVAL: // 4;
                        logDistanceInterval = Convert.toInt(nmea[3]);
                        dataOK |= C_OK_DIST;
                        PostStatusUpdateEvent();
                        break;
                    case BT747Constants.PMTK_LOG_SPEED_INTERVAL: // 5;
                        logSpeedInterval = Convert.toInt(nmea[3]) / 10;
                        dataOK |= C_OK_SPEED;
                        PostStatusUpdateEvent();
                        break;
                    case BT747Constants.PMTK_LOG_REC_METHOD: // 6;
                        logFullOverwrite = (Convert.toInt(nmea[3]) == 1);
                        PostStatusUpdateEvent();
                        break;
                    case BT747Constants.PMTK_LOG_LOG_STATUS: // 7; // bit 2 = logging
                        // on/off
                        logStatus = Convert.toInt(nmea[3]);
                        isLoggingActive = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGONOF_MASK) != 0));
                        loggerIsFull = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGISFULL_MASK) != 0));
                        loggerNeedsInit = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGMUSTINIT_MASK) != 0));
                        loggerIsDisabled = (((logStatus & BT747Constants.PMTK_LOG_STATUS_LOGDISABLED_MASK) != 0));
                        PostStatusUpdateEvent();
                        break;
                    case BT747Constants.PMTK_LOG_MEM_USED: // 8;
                        logMemUsed = Conv.hex2Int(nmea[3]);
                        logMemUsedPercent = (100 * (logMemUsed - (0x200 * ((logMemUsed + 0xFFFF) / 0x10000))))
                                / logMemUsefullSize();
                        PostStatusUpdateEvent();
                        break;
                    case BT747Constants.PMTK_LOG_FLASH: // 9;
                        FlashManuProdID = Conv.hex2Int(nmea[3]);
                        analyseFlashManuProdID();
                        break;
                    case BT747Constants.PMTK_LOG_NBR_LOG_PTS: // 10;
                        logNbrLogPts = Conv.hex2Int(nmea[3]);
                        PostStatusUpdateEvent();
                        break;
                    case BT747Constants.PMTK_LOG_FLASH_SECTORS: // 11;
                        break;
                    case BT747Constants.PMTK_LOG_VERSION: // 12:
                        MtkLogVersion = "V"
                                + Convert.toString(
                                        Convert.toInt(nmea[3]) / 100f, 2);
                        break;
                    default:
                    }
                }
                break;
            case BT747Constants.PMTK_LOG_DT_LOG:
                // Data from the log
                // $PMTK182,8,START_ADDRESS,DATA

                try {
                    // waba.sys.debugMsg("Before
                    // AnalyzeLog:"+p_nmea[3].length());
                    analyzeLogPart(Conv.hex2Int(nmea[2]), nmea[3]);
                } catch (Exception e) {
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
        gpsRxTx.setIgnoreNMEA((!this.gpsDecode)
                || (m_logState != C_LOG_NOLOGGING));
    }

    /**
     * @return Returns the flashManuProdID.
     */
    public final int getFlashManuProdID() {
        return FlashManuProdID;
    }

    /**
     * @return Returns the flashDesc.
     */
    public final String getFlashDesc() {
        return FlashDesc;
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
     * @param holux
     *            Indicates if this device needs special holux decoding.
     */
    public final void setHolux(final boolean holux) {
        this.holux = holux;
    }

    /**
     * @return Returns the holuxName.
     */
    public final String getHoluxName() {
        return holuxName;
    }

    private void PostStatusUpdateEvent() {
        postEvent(new GpsEvent(GpsEvent.DATA_UPDATE));
    }

    private void postGpsEvent(final int type, final Object o) {
        postEvent(new GpsEvent(type, o));
    }

    public final GPSRecord getGpsRecord() {
        return gps;
    }

    private HashSet listeners = new HashSet();

    /** add a listener to event thrown by this class */
    public final void addListener(final GPSListener l) {
        listeners.add(l);
    }

    public final void removeListener(final GPSListener l) {
        listeners.remove(l);
    }

    private void postEvent(final int event) {
        postEvent(new GpsEvent(event));
    }

    protected final void postEvent(final GpsEvent e) {
        Iterator it = listeners.iterator();
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

    public final int getDgpsMode() {
        return dgps_mode;
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

    public final void getDPL700Log(final String p_FileName, final int Card) {
        DPL700LogFileName = p_FileName;
        DPL700Card = Card;
        enterDPL700Mode();
        DPL700_State = C_DPL700_NEEDGETLOG;
    }

    public final void reqDPL700Log() {
        DPL700_State = C_DPL700_GETLOG;
        gpsRxTx.sendCmdAndGetDPL700Response(0x60B50000, 10 * 1024 * 1024);
        //m_GPSrxtx.virtualReceive("sample dataWP Update Over\0");
    }

    public final void enterDPL700Mode() {
        exitDPL700Mode(); // Exit previous session if still open
        gpsRxTx.sendCmdAndGetDPL700Response("W'P Camera Detect", 255);
        //m_GPSrxtx.virtualReceive("WP GPS+BT\0");
    }

    public final void exitDPL700Mode() {
        gpsRxTx.sendDPL700Cmd("WP AP-Exit"); // No reply expected
        DPL700_State = C_DPL700_OFF;
    }

    public final void reqDPL700LogSize() {
        gpsRxTx.sendCmdAndGetDPL700Response(0x60B50000, 255);
    }

    public final void reqDPL700Erase() {
        gpsRxTx.sendCmdAndGetDPL700Response(0x60B50000, 255);
    }

    public final void reqDPL700DeviceInfo() {
        gpsRxTx.sendCmdAndGetDPL700Response(0x5BB00000, 255);
    }

    public final void getDPL700GetSettings() {
        gpsRxTx.sendCmdAndGetDPL700Response(0x62B60000, 255);
    }

    private void AnalyseDPL700Data(final String s) {
        if (GPS_DEBUG) {
            debugMsg("<DPL700 " + s);
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
                    openNewLog(DPL700LogFileName, DPL700Card);
                    try {
                        m_logFile.writeBytes(gpsRxTx.DPL700_buffer, 0,
                                gpsRxTx.DPL700_buffer_idx);
                        m_logFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }
                    DPL700LogFileName = null;
                    exitDPL700Mode();
                }
            }
        }
    }

    public final void setLogRequestAhead(final int logRequestAhead) {
        this.logRequestAhead = logRequestAhead;
    }


}
