// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package gps.mvc;

import gps.BT747Constants;
import gps.GPSListener;
import gps.GpsEvent;
import gps.connection.DPL700ResponseModel;
import gps.connection.GPSrxtx;
import gps.log.GPSRecord;
import gps.log.in.CommonIn;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747StringTokenizer;

/**
 * Refactoring ongoing (split in Model and Controller).
 * 
 * GPSstate maintains a higher level state of communication with the GPS
 * device. It currently contains very specific commands MTK loggers but that
 * could change in the future by extending GPSstate with such features in a
 * derived class.
 * 
 * Must still move stuff to MtkController, move to MtkModel mostly done.
 * 
 * @author Mario De Weerd
 * @see GPSrxtx
 * 
 */
/* Final for the moment */
public class Model {
    private final GPSLinkHandler handler;
    // For the moment pointing to 'this' for the MtkModel.
    // After refactoring this should be effectively fully delegated.
    private final MtkModel mtkModel;

    protected final MTKLogDownloadHandler mtkLogHandler;

    /**
     * Initialiser
     * 
     * 
     */
    public Model(final GPSrxtx gpsRxTx) {
        handler = new GPSLinkHandler();
        mtkModel = new MtkModel(this, new GPSLinkHandler());
        mtkLogHandler = new MTKLogDownloadHandler(this, mtkModel);
        setGPSRxtx(gpsRxTx);
    }

    public final MtkModel getMtkModel() {
        return mtkModel;
    }

    /*************************************************************************
     * Start of code that should be part of the final interface after
     * refactoring.
     */

    public final GPSLinkHandler getHandler() {
        return handler;
    }

    /*************************************************************************
     * Start
     * 
     * @param gpsRxTx
     */
    public final void setGPSRxtx(final GPSrxtx gpsRxTx) {
        handler.setGPSRxtx(gpsRxTx);
    }

    private boolean GPS_STATS = false; // (!Settings.onDevice);

    public final void setStats(final boolean stats) {
        GPS_STATS = stats;
    }

    /**
     * Invalidates any data previously fetched from the device.
     */
    public void setAllUnavailable() {
        // Delegate to the model(s) in use.
        mtkModel.setAllUnavailable();
    }

    /*************************************************************************
     * End of reviewed code for final interface.
     ************************************************************************/

    /*************************************************************************
     * The code that follows still needs refactoring.
     */

    /**
     * Set the logging format of the device. <br>
     * Best followed by eraseLog.
     * 
     * @param newLogFormat
     *                The format to set.
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
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_FORMAT_STR + ","
                + JavaLibBridge.unsigned2hex(logFmt, 8));
        mtkModel.setChanged(MtkModel.DATA_LOG_FORMAT);
    }

    public final void doHotStart() {
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_HOT_START_STR);
    }

    public final void doWarmStart() {
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_WARM_START_STR);
    }

    public final void doColdStart() {
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_COLD_START_STR);
    }

    public final void doFullColdStart() {
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_FULL_COLD_START_STR);
    }

    private final void reqDeviceVersion() {
        sendCmd("PMTK" + BT747Constants.PMTK_Q_VERSION_STR);
    }

    private final void reqDeviceRelease() {
        sendCmd("PMTK" + BT747Constants.PMTK_Q_RELEASE_STR);
    }

    public final void reqDeviceInfo() {
        setDataNeeded(MtkModel.DATA_MTK_RELEASE);
        setDataNeeded(MtkModel.DATA_MTK_VERSION);
        setDataNeeded(MtkModel.DATA_FLASH_TYPE);
        setDataNeeded(MtkModel.DATA_LOG_VERSION);
    }

    /**
     * Request the initial log mode (the first value logged in memory). Will
     * be analyzed in {@link #analyseLogNmea(String[])}.<br>
     * Must be accessed through {@link #DATA_INITIAL_LOG}
     */
    public final void reqInitialLogMode() {
        mtkLogHandler.readLog(6, 2); // 6 is the log mode offset in the
        // log,
        // 2 is the size
        // Required to know if log is in overwrite mode.
    }

    /**
     * Must be accessed through {@link #DATA_FLASH_TYPE}
     */
    private final void reqFlashManuID() {
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_FLASH_STR + "," + "9F");
    }

    public final void setDownloadTimeOut(final int downloadTimeOut) {
        handler.setDownloadTimeOut(downloadTimeOut);
    }

    /**
     * Request the current log format from the device.<br>
     * Must use {@link #setDataNeeded(int)} and {@link #DATA_LOG_FORMAT}.
     */
    private final void reqLogFormat() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_FORMAT_STR);
    }

    /**
     * Request the current log status from the device.<br>
     * Must use {@link #setDataNeeded(int)} and {@link #DATA_LOG_STATUS}.
     */
    protected final void reqLogStatus() {

        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_LOG_STATUS_STR);
    }

    /**
     * Requests the amount of memory currently used.<br>
     * Must use {@link #setDataNeeded(int)} and {@link #DATA_MEM_USED}.
     */
    private final void reqLogMemUsed() {
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_MEM_USED_STR);
    }

    /**
     * Requests the number of points logged in memory.<br>
     * Must use {@link #setDataNeeded(int)} and {@link #DATA_MEM_PTS_LOGGED}.
     */
    private final void reqLogMemPtsLogged() {
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_NBR_LOG_PTS_STR);
    }

    private final void reqMtkLogVersion() {
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_VERSION_STR);
    }

    public final void reqLogReasonStatus() {
        /* Get log distance interval */
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_DISTANCE_INTERVAL_STR);
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_SPEED_INTERVAL_STR);
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_TIME_INTERVAL_STR);

    }

    public final void reqLogFlashStatus() {
        /* Get flash status - immediate (not in output buffer) */
        /* Needed for erase */
        doSendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_FLASH_STAT_STR);
    }

    public final void reqLogFlashSectorStatus() {
        /* Get flash status - immediate (not in output buffer) */
        /* Needed for erase */
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_FLASH_SECTORS_STR);
    }

    public final void logImmediate(final int value) {
        if (!mtkModel.isLoggingActive()) {
            startLog();
        }
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_USER + ","
                + JavaLibBridge.unsigned2hex(value, 4));
    }

    public final void setLogTimeInterval(final int value) {
        int z_value = value;
        if ((z_value != 0) && (z_value > 36000)) {
            z_value = 36000;
        }
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_TIME_INTERVAL_STR + "," + z_value);
    }

    public final void setLogDistanceInterval(final int value) {
        int z_value = value;
        if ((z_value != 0) && (z_value > 36000)) {
            z_value = 36000;
        } else if ((z_value != 0) && (z_value < 1)) {
            z_value = 1;
        }

        /* Get log distance interval */
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_DISTANCE_INTERVAL_STR + ","
                + z_value);
    }

    public final void setLogSpeedInterval(final int value) {
        int z_value = value;
        if ((z_value != 0) && (z_value > 36000)) {
            z_value = 36000;
        } else if ((z_value != 0) && (z_value < 1)) {
            z_value = 1;
        }
        /* Get log distance interval */
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_SPEED_INTERVAL_STR + ","
                + (z_value * 10));
    }

    public final void setFixInterval(final int value) {
        int z_value = value;
        if (z_value > 30000) {
            z_value = 30000;
        } else if (z_value < 200) {
            z_value = 200;
        }

        /* Set log distance interval */
        sendCmd("PMTK" + BT747Constants.PMTK_API_SET_FIX_CTL + "," + z_value
                + ",0,0,0.0,0.0");
    }

    public final void reqFixInterval() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_Q_FIX_CTL);
    }

    /** Get the current status of the device */
    public final void reqStatus() {
        setDataNeeded(MtkModel.DATA_LOG_FORMAT);
        getLogCtrlInfo();
        // getLogReasonStatus();
        // getPowerSaveEnabled();
        // getSBASEnabled();
        // getDGPSMode();
        // getDatumMode();
        // getFixInterval();
        reqHoluxName(); // Mainly here to identify Holux device
    }

    /**
     * Check if the data is available and if it is not, requests it.
     * 
     * @param dataType
     * @return
     */
    public final void setDataNeeded(final int dataType) {
        if (handler.isConnected()) {
            final int ts = Generic.getTimeStamp();
            if (mtkModel.isDataNeedsRequest(ts, dataType)) {
                switch (dataType) {
                case MtkModel.DATA_MEM_USED:
                    reqLogMemUsed();
                    break;
                case MtkModel.DATA_MEM_PTS_LOGGED:
                    reqLogMemPtsLogged();
                    break;
                case MtkModel.DATA_FLASH_TYPE:
                    reqFlashManuID();
                    break;
                case MtkModel.DATA_LOG_FORMAT:
                    reqLogFormat();
                    break;
                case MtkModel.DATA_MTK_VERSION:
                    reqDeviceVersion();
                    break;
                case MtkModel.DATA_MTK_RELEASE:
                    reqDeviceRelease();
                    break;
                case MtkModel.DATA_INITIAL_LOG:
                    reqInitialLogMode();
                    break;
                case MtkModel.DATA_LOG_STATUS:
                    reqLogStatus();
                    break;
                case MtkModel.DATA_LOG_VERSION:
                    reqMtkLogVersion();
                    break;
                default:
                    break;
                }
            }
        }
        if (Generic.isDebug() && (Generic.getDebugLevel() >= 2)) {
            Generic.debug("Data request of " + dataType + " skipped");
        }
    }

    public final void reqLogOnOffStatus() {
        reqLogStatus();
    }

    private final void getLogCtrlInfo() {
        setDataNeeded(MtkModel.DATA_LOG_VERSION);
        setDataNeeded(MtkModel.DATA_MEM_USED);
        setDataNeeded(MtkModel.DATA_MEM_PTS_LOGGED);
        reqLogOverwrite();
    }

    /** Activate the logging by the device. */
    public final void startLog() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_ON);
        mtkModel.setLoggingActive(true); // This should be the result of
        // the action.
        // The device will eventually tell the new status
    }

    /** Stop the automatic logging of the device. */
    public final void stopLog() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_OFF);
        mtkModel.setLoggingActive(false); // This should be the result of
        // the action.
        // The device will eventually tell the new status
    }

    public final void setLogOverwrite(final boolean set) {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_REC_METHOD_STR + ","
                + (set ? "1" : "2"));
    }

    public final void reqLogOverwrite() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_Q + ","
                + BT747Constants.PMTK_LOG_REC_METHOD_STR);
    }

    public final void setSBASTestEnabled(final boolean set) {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_SET_SBAS_TEST_STR + ","
                + (set ? "0" : "1"));
    }

    public final void reqSBASTestEnabled() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_Q_SBAS_TEST_STR);
    }

    public final void setSBASEnabled(final boolean set) {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_SET_SBAS_STR + ","
                + (set ? "1" : "0"));
    }

    public final void reqSBASEnabled() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_Q_SBAS_STR);
    }

    public final void setPowerSaveEnabled(final boolean set) {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_SET_PWR_SAV_MODE_STR + ","
                + (set ? "1" : "0"));
    }

    public final void reqPowerSaveEnabled() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_Q_PWR_SAV_MOD_STR);
    }

    public final void setDGPSMode(final int mode) {
        // Request log format from device
        if ((mode >= 0) && (mode <= 2)) {
            sendCmd("PMTK" + BT747Constants.PMTK_API_SET_DGPS_MODE_STR + ","
                    + mode);
        }
    }

    public final void reqDGPSMode() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_Q_DGPS_MODE_STR);
    }

    public final void setDatumMode(final int mode) {
        // Request log format from device
        if ((mode >= 0) && (mode <= 2)) {
            sendCmd("PMTK" + BT747Constants.PMTK_API_SET_DATUM_STR + ","
                    + mode);
        }
    }

    public final void reqDatumMode() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_Q_DATUM_STR);
    }

    public final void reqNMEAPeriods() {
        sendCmd("PMTK" + BT747Constants.PMTK_API_Q_NMEA_OUTPUT);
    }

    public final void reqHoluxName() {
        sendCmd(BT747Constants.HOLUX_MAIN_CMD
                + BT747Constants.HOLUX_API_Q_NAME);
    }

    /**
     * @param holuxName
     *                The holuxName to set.
     */
    public final void setHoluxName(final String holuxName) {
        sendCmd(BT747Constants.HOLUX_MAIN_CMD
                + BT747Constants.HOLUX_API_SET_NAME + "," + holuxName);
        reqHoluxName();
    }

    /**
     * Requests the current mac address for bluetooth (Holux 241 devices).
     */
    public final void reqBtMacAddr() {
        sendCmd("PMTK" + BT747Constants.PMTK_API_Q_BT_MAC_ADDR);
    }

    /**
     * Sets the current mac address for bluetooth (Holux 241 devices).
     * 
     * @param btMacAddr
     *                The Mac address to set in the following format:<br>
     *                00:1F:14:15:12:13.
     */
    public final void setBtMacAddr(final String btMacAddr) {
        String myMacAddr = "";
        final BT747StringTokenizer fields = JavaLibBridge
                .getStringTokenizerInstance(btMacAddr, ':');
        while (fields.hasMoreTokens()) {
            myMacAddr = fields.nextToken() + myMacAddr;
        }

        if (myMacAddr.length() == 12) {
            sendCmd("PMTK" + BT747Constants.PMTK_API_SET_BT_MAC_ADDR + ","
                    + myMacAddr.substring(0, 6) + ","
                    + myMacAddr.substring(6, 12));
            reqBtMacAddr();
        }
    }

    public final void setNMEAPeriods(final int[] periods) {
        final StringBuffer sb = new StringBuffer(255);
        sb.setLength(0);
        sb.append("PMTK" + BT747Constants.PMTK_API_SET_NMEA_OUTPUT);
        for (int i = 0; i < periods.length; i++) {
            sb.append(',');
            sb.append(periods[i]);
        }
        sendCmd(sb.toString());
    }

    public final void setNMEADefaultPeriods() {
        final int[] periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

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
        sendCmd("PMTK"
                + BT747Constants.PMTK_API_SET_USER_OPTION
                + ","
                + "0" // Lock:
                // currently
                // ignore
                // parameter
                + "," + GLL_Period + "," + RMC_Period + "," + VTG_Period
                + "," + GSA_Period + "," + GSV_Period + "," + GGA_Period
                + "," + ZDA_Period + "," + MCHN_Period);

    }

    public final void reqFlashUserOption() {
        // Request log format from device
        sendCmd("PMTK" + BT747Constants.PMTK_API_GET_USER_OPTION_STR);
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
    private final GPSRecord gpsPos = GPSRecord.getLogFormatRecord(0);

    /**
     * Local singleton of DPL700Controller.
     * 
     * Intermediate step in refactoring.
     */
    private DPL700Controller dpl700C;

    public final DPL700Controller getDPL700Controller() {
        if (dpl700C == null) {
            dpl700C = new DPL700Controller(handler, mtkLogHandler);
        }
        return dpl700C;
    }

    public final void analyseResponse(final Object response) {
        if (response instanceof DPL700ResponseModel) {
            if (dpl700C != null) {
                dpl700C.analyseDPL700Data((DPL700ResponseModel) response);
            }
        }
        if (response instanceof MtkBinTransportMessageModel) {
            mtkModel
                    .analyseMtkBinData((MtkBinTransportMessageModel) response);
        } else {
            analyseNMEA((String[]) response);
        }
    }

    public final int analyseNMEA(final String[] sNmea) {
        final int cmd;
        int result;
        result = 0;
        try {
            // if(GPS_DEBUG&&!p_nmea[0].startsWith("G")) {
            // waba.sys.debugMsg("ANA:"+p_nmea[0]+","+p_nmea[1]);}
            if (sNmea.length == 0) {
                // Should not happen, problem in program
                Generic.debug("Problem - report NMEA is 0 length");
            } else if (gpsDecode
                    && !isLogDownloadOnGoing() // Not
                    // during
                    // log
                    // download for
                    // performance.
                    && (sNmea[0].length() != 0)
                    && (sNmea[0].charAt(0) == 'G')) {
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
                // //
                // GPSTPV,$epoch.$msec,?,$lat,$lon,,$alt,,$speed,,$bear,,,,A
                // }
            } else {
                return mtkModel.analyseMtkNmea(sNmea);
            }
        } catch (final Exception e) {
            Generic.debug("AnalyzeNMEA", e);
        }
        return result;
    } // End method

    /*************************************************************************
     * LOGGING FUNCTIONALITY
     ************************************************************************/

    /*************************************************************************
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
     *                Activate gps decoding if true, do not decode if false.
     *                This may improve performance.
     */
    public final void setGpsDecode(final boolean gpsDecode) {
        this.gpsDecode = gpsDecode;
        updateIgnoreNMEA();
    }

    protected final void updateIgnoreNMEA() {
        handler.setIgnoreNMEA((!gpsDecode) || isLogDownloadOnGoing());
    }

    protected void postGpsEvent(final int type, final Object o) {
        postEvent(new GpsEvent(type, o));
    }

    public final GPSRecord getGpsRecord() {
        return gpsPos;
    }

    private final BT747HashSet listeners = JavaLibBridge.getHashSetInstance();

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
        final BT747HashSet it = listeners.iterator();
        while (it.hasNext()) {
            final GPSListener l = (GPSListener) it.next();
            l.gpsEvent(e);
        }
    }

    public final void setLogRequestAhead(final int logRequestAhead) {
        mtkLogHandler.setLogRequestAhead(logRequestAhead);
    }

    /**
     * Send an NMEA string to the link. The parameter should not include the
     * checksum - this is added by the method.
     * 
     * @param s
     *                NMEA string to send.
     */
    public final void sendCmd(final Object s) {
        handler.sendCmd(s);
    }

    /**
     * Immediate string sending.
     * 
     * @param s
     */
    protected final void doSendCmd(final Object s) {
        handler.doSendCmd(s);
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

    protected final int timeSinceLastStamp() {
        return handler.timeSinceLastStamp();
    }

    protected final void resetLogTimeOut() {
        handler.resetLogTimeOut();
    }

    protected final boolean isConnected() {
        return handler.isConnected();
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
     * existing log with different data can be overwritten. This method must
     * be called to replay to this question which is in principle the result
     * of a user reply to a message box.
     * 
     * @param isOkToOverwrite
     *                If true, the existing log can be overwritten
     */
    public final void replyToOkToOverwrite(final boolean isOkToOverwrite) {
        mtkLogHandler.replyToOkToOverwrite(isOkToOverwrite);
    }

    public final void getLogInit(final int startAddr, final int endAddr,
            final int requestStep, final String fileName, final int card,
            final boolean isIncremental, // True if incremental read
            final boolean disableLogging) {
        mtkLogHandler.getLogInit(startAddr, endAddr, requestStep, fileName,
                card, isIncremental, disableLogging);
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

    // ///////////////////////////////////////////////////////////////
    // To be removed after refactoring.

    protected final MTKLogDownloadHandler getMtkLogHandler() {
        return mtkLogHandler;
    }
}
