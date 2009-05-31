/**
 * 
 */
package gps.mvc;

import gps.BT747Constants;
import gps.mvc.commands.mtk.MtkBinCommand;
import gps.mvc.commands.mtk.SetMtkBinModeCommand;
import gps.mvc.commands.mtk.SetNmeaModeCommand;
import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747StringTokenizer;

/**
 * Controller for MTK based device (Transystem Type logger currently
 * included).
 * 
 * Refactoring ongoing.
 * 
 * @author Mario De Weerd
 * 
 */
public class MtkController implements ProtectedDevControllerIF {
    private MtkModel m;
    protected final MTKLogDownloadHandler mtkLogHandler;

    /** Default prefix to MTK command. */
    protected static final String PMTK = "PMTK";

    MtkController(final MtkModel m) {
        this.m = m;
        mtkLogHandler = new MTKLogDownloadHandler(this, m);
        // TODO: Log handler should be split in model and controller
        // or use current model for data.
        m.setLogHandler(mtkLogHandler);
    }

    public final MtkModel getMtkModel() {
        return m;
    }

    public final void sendMtkBin(final MtkBinTransportMessageModel msg) {
        m.getHandler().sendCmd(new MtkBinCommand(msg));
    }

    /**
     * Sets the device to binary mode.
     */
    public final void setBinMode() {
        m.getHandler().sendCmd(new SetMtkBinModeCommand());
    }

    /**
     * Set the device to Nmea mode.
     */
    public final void setNmeaMode() {
        m.getHandler().sendCmd(new SetNmeaModeCommand());
    }

    /**
     * Delegates NMEA sending to handler.
     * 
     * @param cmd
     */
    protected final void sendCmd(final Object cmd) {
        m.getHandler().sendCmd(cmd);
    }

    /**
     * Delegates NMEA sending to handler.
     * 
     * @param cmd
     */
    protected final void doSendCmd(final Object cmd) {
        m.getHandler().doSendCmd(cmd);
    }

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
        sendCmd(MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_FORMAT_STR + ","
                + JavaLibBridge.unsigned2hex(logFmt, 8));
        m.setChanged(MtkModel.DATA_LOG_FORMAT);
    }

    /** To perform a hot start on the device. */
    public final static int CMD_HOTSTART = 0;
    /** To perform a warm start on the device. */
    public final static int CMD_WARMSTART = 1;
    /** To perform a cold start on the device. */
    public final static int CMD_COLDSTART = 2;
    /** To perform a full cold start on the device. */
    public final static int CMD_FULLCOLDSTART = 3;
    /** Start logging on the device. */
    public final static int CMD_STARTLOG = 4;
    /** Stop logging on the device. */
    public final static int CMD_STOPLOG = 5;
    public final static int CMD_AUTOLOG_OFF = 6;
    public final static int CMD_AUTOLOG_ON = 7;

    /**
     * Perform a command.
     * 
     * @param cmd
     *                Command identification.
     * @return true is command is supported.
     */
    public final boolean cmd(final int cmd) {
        String nmeaCmd = null;
        switch (cmd) {
        case CMD_HOTSTART:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_CMD_HOT_START_STR;
            break;
        case CMD_WARMSTART:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_CMD_WARM_START_STR;
            break;
        case CMD_COLDSTART:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_CMD_COLD_START_STR;
            break;
        case CMD_FULLCOLDSTART:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_CMD_FULL_COLD_START_STR;
            break;
        case CMD_STARTLOG:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_ON;
            m.setLoggingActive(true); // This should be the result of
            break;
        case CMD_STOPLOG:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_OFF;
            m.setLoggingActive(false); // This should be the result of
            break;
        case CMD_AUTOLOG_OFF:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG_STR
                    + "," + BT747Constants.PMTK_LOG_DISABLE;
            break;
        case CMD_AUTOLOG_ON:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG_STR
                    + "," + BT747Constants.PMTK_LOG_ENABLE;
            break;
        default:
            return false;
        }
        if (nmeaCmd != null) {
            sendCmd(nmeaCmd);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.ProtectedDevControllerIF#reqData(int)
     */
    public boolean reqData(final int dataType) {
        String nmeaCmd = null;
        switch (dataType) {
        case MtkModel.DATA_FLASH_TYPE:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_FLASH_STR + "," + "9F";
            break;
        case MtkModel.DATA_LOG_FORMAT:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_FORMAT_STR;
            break;
        case MtkModel.DATA_LOG_STATUS:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_LOG_STATUS_STR;
            break;
        case MtkModel.DATA_MEM_USED:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_MEM_USED_STR;
            break;
        case MtkModel.DATA_MEM_PTS_LOGGED:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_NBR_LOG_PTS_STR;
            break;
        case MtkModel.DATA_LOG_VERSION:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_VERSION_STR;
            break;
        case MtkModel.DATA_MTK_VERSION:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_Q_VERSION_STR;
            break;
        case MtkModel.DATA_MTK_RELEASE:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_Q_RELEASE_STR;
            break;
        case MtkModel.DATA_INITIAL_LOG:
            /**
             * Request the initial log mode (the first value logged in
             * memory). Will be analyzed in {@link #analyseLogNmea(String[])}.<br>
             * Must be accessed through {@link #DATA_INITIAL_LOG}
             */
            // 6 is the log mode offset in the log,
            // 2 is the size
            // Required to know if log is in overwrite mode.
            mtkLogHandler.readLog(6, 2);
            return true;
        case MtkModel.DATA_LOG_TIME_INTERVAL:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_TIME_INTERVAL_STR;
            break;
        case MtkModel.DATA_LOG_SPEED_INTERVAL:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_SPEED_INTERVAL_STR;
            break;
        case MtkModel.DATA_LOG_DISTANCE_INTERVAL:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_DISTANCE_INTERVAL_STR;
            break;
        case MtkModel.DATA_LOG_FLASH_STATUS:
            /* Needed for erase */
            /* Immediate sending! */
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_FLASH_STAT_STR;
            doSendCmd(nmeaCmd);
            return true;
        case MtkModel.DATA_LOG_FLASH_SECTOR_STATUS:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_FLASH_SECTORS_STR;
            break;
        case MtkModel.DATA_FIX_PERIOD:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_API_Q_FIX_CTL;
            break;
        case MtkModel.DATA_AGPS_STORED_RANGE:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_Q_EPO_INFO;
            break;
        case MtkModel.DATA_LOG_OVERWRITE_STATUS:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                    + BT747Constants.PMTK_LOG_Q + ","
                    + BT747Constants.PMTK_LOG_REC_METHOD_STR;
            break;
        case MtkModel.DATA_SBAS_TEST_STATUS:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_SBAS_TEST_STR;
            break;
        case MtkModel.DATA_SBAS_STATUS:
            nmeaCmd = MtkController.PMTK + BT747Constants.PMTK_API_Q_SBAS_STR;
            ;
            break;
        case MtkModel.DATA_POWERSAVE_STATUS:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_PWR_SAV_MOD_STR;
            break;
        case MtkModel.DATA_DATUM_MODE:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_DATUM_STR;
            break;
        case MtkModel.DATA_NMEA_OUTPUT_PERIODS:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_NMEA_OUTPUT;
            break;

        case MtkModel.DATA_DGPS_MODE:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_DGPS_MODE_STR;
            break;
        case MtkModel.DATA_BT_MAC_ADDR:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_Q_BT_MAC_ADDR;
            break;
        case MtkModel.DATA_FLASH_USER_OPTION:
            nmeaCmd = MtkController.PMTK
                    + BT747Constants.PMTK_API_GET_USER_OPTION_STR;
            break;
        case MtkModel.DATA_DEVICE_NAME:
            nmeaCmd = BT747Constants.HOLUX_MAIN_CMD
                    + BT747Constants.HOLUX_API_Q_NAME;
            break;
        default:
            break;
        }
        if (nmeaCmd != null) {
            sendCmd(nmeaCmd);
            return true;
        } else {
            Generic.debug("Serious: (in MtkController) Unknown data type #"
                    + dataType);
            return false;
        }
    }

    /**
     * Takes int value in 0.1 seconds.
     */
    public final static int CMD_SET_LOG_TIME_INTERVAL = 8;
    /**
     * Takes int value in 1 meters.
     */
    public final static int CMD_SET_LOG_DISTANCE_INTERVAL = 9;
    /**
     * Takes int value in 0.1 km/h.
     */
    public final static int CMD_SET_LOG_SPEED_INTERVAL = 10;
    /**
     * Takes String value for the device name.
     */
    public final static int CMD_SET_DEVICE_NAME = 11;

    /**
     * Stop waiting for the erase to finish.
     */
    public final static int CMD_STOP_WAITING_FOR_ERASE = 12;
    
    /**
     * Erase the log.
     */
    public final static int CMD_ERASE_LOG = 13;

    /**
     * Check if a command is supported.
     * 
     * @param cmd
     *                Command identification.
     * @return true is command is supported.
     */
    public boolean isSupportedCmd(final int cmd) {
        switch (cmd) {
        case CMD_SET_LOG_TIME_INTERVAL:
        case CMD_SET_LOG_DISTANCE_INTERVAL:
        case CMD_SET_LOG_SPEED_INTERVAL:
        case CMD_SET_DEVICE_NAME:
        case CMD_STOP_WAITING_FOR_ERASE:
        case CMD_ERASE_LOG:
            return true;
        default:
            return false;
        }
    }

    /**
     * Perform a command.
     * 
     * @param cmd
     *                Command identification.
     * @return true is command is supported.
     */
    public boolean cmd(final int cmd, final CmdParam param) {
        if (param == null) {
            return cmd(cmd);
        } else {
            switch (cmd) {
            case CMD_SET_LOG_TIME_INTERVAL:
                setLogTimeInterval(param.getInt());
                break;
            case CMD_SET_LOG_DISTANCE_INTERVAL:
                setLogDistanceInterval(param.getInt());
                break;
            case CMD_SET_LOG_SPEED_INTERVAL:
                setLogSpeedInterval(param.getInt());
                break;
            case CMD_SET_DEVICE_NAME:
                setHoluxName(param.getString());
                break;
            case CMD_STOP_WAITING_FOR_ERASE:
                mtkLogHandler.stopErase();
                break;
            case CMD_ERASE_LOG:
                mtkLogHandler.eraseLog();
                break;
            default:
                Generic.debug("Unsupported cmd in " + this);
                return false;
            }
        }
        return true;
    }

    private final void setLogTimeInterval(final int value) {
        int z_value = value;
        if ((z_value != 0) && (z_value > 36000)) {
            z_value = 36000;
        }
        sendCmd(MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_TIME_INTERVAL_STR + "," + z_value);
    }

    private final void setLogDistanceInterval(final int value) {
        int z_value = value;
        if ((z_value != 0) && (z_value > 36000)) {
            z_value = 36000;
        } else if ((z_value != 0) && (z_value < 1)) {
            z_value = 1;
        }

        /* Get log distance interval */
        sendCmd(MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_DISTANCE_INTERVAL_STR + ","
                + z_value);
    }

    private final void setLogSpeedInterval(final int value) {
        int z_value = value;
        if ((z_value != 0) && (z_value > 36000)) {
            z_value = 36000;
        } else if ((z_value != 0) && (z_value < 1)) {
            z_value = 1;
        }
        /* Get log distance interval */
        sendCmd(MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
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
        sendCmd(MtkController.PMTK + BT747Constants.PMTK_API_SET_FIX_CTL
                + "," + z_value + ",0,0,0.0,0.0");
    }

    public final void setLogOverwrite(final boolean set) {
        // Request log format from device
        sendCmd(MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_REC_METHOD_STR + ","
                + (set ? "1" : "2"));
    }

    public final void setSBASTestEnabled(final boolean set) {
        // Request log format from device
        sendCmd(MtkController.PMTK
                + BT747Constants.PMTK_API_SET_SBAS_TEST_STR + ","
                + (set ? "0" : "1"));
    }

    public final void setSBASEnabled(final boolean set) {
        // Request log format from device
        sendCmd(MtkController.PMTK + BT747Constants.PMTK_API_SET_SBAS_STR
                + "," + (set ? "1" : "0"));
    }

    public final void setPowerSaveEnabled(final boolean set) {
        // Request log format from device
        sendCmd(MtkController.PMTK
                + BT747Constants.PMTK_API_SET_PWR_SAV_MODE_STR + ","
                + (set ? "1" : "0"));
    }

    public final void setDGPSMode(final int mode) {
        // Request log format from device
        if ((mode >= 0) && (mode <= 2)) {
            sendCmd(MtkController.PMTK
                    + BT747Constants.PMTK_API_SET_DGPS_MODE_STR + "," + mode);
        }
    }

    public final void setDatumMode(final int mode) {
        // Request log format from device
        if ((mode >= 0) && (mode <= 2)) {
            sendCmd(MtkController.PMTK
                    + BT747Constants.PMTK_API_SET_DATUM_STR + "," + mode);
        }
    }

    /**
     * @param holuxName
     *                The holuxName to set.
     */
    private void setHoluxName(final String holuxName) {
        sendCmd(BT747Constants.HOLUX_MAIN_CMD
                + BT747Constants.HOLUX_API_SET_NAME + "," + holuxName);
        reqData(MtkModel.DATA_DEVICE_NAME);
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
            sendCmd(MtkController.PMTK
                    + BT747Constants.PMTK_API_SET_BT_MAC_ADDR + ","
                    + myMacAddr.substring(0, 6) + ","
                    + myMacAddr.substring(6, 12));
            reqData(MtkModel.DATA_BT_MAC_ADDR);
        }
    }

    public final void setNMEAPeriods(final int[] periods) {
        final StringBuffer sb = new StringBuffer(255);
        sb.setLength(0);
        sb.append(MtkController.PMTK
                + BT747Constants.PMTK_API_SET_NMEA_OUTPUT);
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
        reqData(MtkModel.DATA_NMEA_OUTPUT_PERIODS);
    }

    public final void setFlashUserOption(final boolean lock,
            final int updateRate, final int baudRate, final int GLL_Period,
            final int RMC_Period, final int VTG_Period, final int GSA_Period,
            final int GSV_Period, final int GGA_Period, final int ZDA_Period,
            final int MCHN_Period) {
        // Request log format from device
        sendCmd(MtkController.PMTK
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

    public final void logImmediate(final int value) {
        if (!m.isLoggingActive()) {
            cmd(CMD_STARTLOG);
        }
        sendCmd(MtkController.PMTK + BT747Constants.PMTK_CMD_LOG + ","
                + BT747Constants.PMTK_LOG_SET + ","
                + BT747Constants.PMTK_LOG_USER + ","
                + JavaLibBridge.unsigned2hex(value, 4));
    }

    public final void setLogRequestAhead(final int logRequestAhead) {
        mtkLogHandler.setLogRequestAhead(logRequestAhead);
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

    /*
     * (non-Javadoc)
     * 
     * @see gps.mvc.ProtectedDevControllerIF#notifyRun()
     */
    public void notifyRun() {
        if (mtkLogHandler != null) {
            mtkLogHandler.notifyRun();
        }
    }
    
    /**
     * The environment indicates a disconnect happened.
     */
    public void notifyDisconnected() {
        if (mtkLogHandler!=null) {
            mtkLogHandler.notifyDisconnected();
        }
        
    }

}
