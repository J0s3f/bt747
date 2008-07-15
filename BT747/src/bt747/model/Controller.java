package bt747.model;

import gps.BT747Constants;
import gps.GPSListener;
import gps.GPSstate;
import gps.log.GPSFilter;
import gps.log.GPSFilterAdvanced;
import gps.log.GPSRecord;
import gps.log.in.BT747LogConvert;
import gps.log.in.CSVLogConvert;
import gps.log.in.DPL700LogConvert;
import gps.log.in.GPSLogConvert;
import gps.log.in.HoluxTrlLogConvert;
import gps.log.out.GPSArray;
import gps.log.out.GPSCSVFile;
import gps.log.out.GPSCompoGPSTrkFile;
import gps.log.out.GPSFile;
import gps.log.out.GPSGPXFile;
import gps.log.out.GPSGmapsHTMLEncodedFile;
import gps.log.out.GPSKMLFile;
import gps.log.out.GPSNMEAFile;
import gps.log.out.GPSPLTFile;

/**
 * @author Mario De Weerd
 * 
 */
/**
 * @author Mario
 * 
 */
public class Controller {
    /**
     * Fixed number indicating that the port number does not correspond to an
     * actual port.
     */
    private static final int NOT_A_PORT_NUMBER = 0x5555;
    /**
     * Multiplier to convert floating *DOP value to int value for device.
     */
    private static final int XDOP_FLOAT_TO_INT_100 = 100;
    /**
     * The number of seconds in a minute.
     */
    private static final int SECONDS_PER_MINUTE = 60;
    /**
     * The number of seconds in a day.
     */
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;
    /**
     * The number of seconds in an hour.
     */
    private static final int SECONDS_PER_HOUR = 3600;
    /**
     * Reference to the model.
     */
    private Model m;

    /**
     * @param model
     *            The model to associate with this controller.
     */
    public Controller(final Model model) {
        setModel(m);
    }

    /**
     * Currently needed because class is extended.
     */
    public Controller() {
    }

    public final void setModel(final Model model) {
        this.m = model;
        init();
    }

    /**
     * Called when the Controller starts. Used for initialization.
     */
    private void init() {
        getLogFilterSettings();
        m.setGpsDecode(m.getGpsDecode());
        m.gpsModel().setDownloadTimeOut(m.getDownloadTimeOut());
        m.gpsModel().setLogRequestAhead(m.getLogRequestAhead());

        int port = m.getPortnbr();
        if (port != NOT_A_PORT_NUMBER) {
            // TODO: review this, especially with 'freetext port'
            m.gpsRxTx().setDefaults(port, m.getBaudRate());
        }
        if (m.getStartupOpenPort()) {
            connectGPS();
        }
    }

    /**
     * @param on
     *            True when Imperial units are to be used where possible
     */
    public final void setImperial(final boolean on) {
        m.setImperial(on);
        m.saveSettings();
    }

    /**
     * @param on
     *            When true, writes the log conditions to certain output
     *            formats. A log condition is for example: log a point every
     *            second.
     * 
     */
    public final void setOutputLogConditions(final boolean on) {
        m.setOutputLogConditions(on);
        m.saveSettings();
    }

    /**
     * @param s
     *            The base path for the files referenced.
     */
    public final void setBaseDirPath(final String s) {
        m.setBaseDirPath(s);
        m.saveSettings();
    }

    /**
     * Set the log file path (including basename) relative to the BaseDirPath.
     * 
     * @param s
     *            The relative log file path (including basename)
     */
    public final void setLogFileRelPath(final String s) {
        m.setLogFileRelPath(s);
        m.saveSettings();
    }

    /**
     * Set the output file path (including basename, no extension) relative to
     * the BaseDirPath.
     * 
     * @param s
     *            The relative log file path (including basename)
     */
    public final void setOutputFileRelPath(final String s) {
        m.setReportFileBase(s);
        m.saveSettings();
    }

    /**
     * @param chunkSize
     *            The amount of data that is requested from the device in a
     *            single command when downloading the data.
     */

    public final void setChunkSize(final int chunkSize) {
        m.setChunkSize(chunkSize);
        m.saveSettings();
    }

    /**
     * @param timeout
     *            The timeout in ms after which the lack of reply from the
     *            device will be considered as a communication failure.
     *            Depending on the operation, a recovery will be attempted.
     */
    public final void setDownloadTimeOut(final int timeout) {
        m.setDownloadTimeOut(timeout);
        m.gpsModel().setDownloadTimeOut(timeout);
        m.saveSettings();
    }

    /**
     * @param card
     *            The card number that the filepaths refer to. This is usefull
     *            on PDA's only and probably only on a Palm. This is used by the
     *            'SuperWaba' environment if used. '-1' refers to the 'last card
     *            in the system' which is normally the card that can be inserted
     *            by the PDA user.
     */
    public final void setCard(final int card) {
        m.setCard(card);
        m.saveSettings();
    }

    /**
     * @param numberOfRequestsAhead
     *            Especially when downloading using Bluetooth, downloading is
     *            slow when no request pipeline is implemented. This number
     *            defines how many 'chunk download' request will be sent to
     *            device while the first reply is still pending.
     */
    public final void setLogRequestAhead(final int numberOfRequestsAhead) {
        m.setLogRequestAhead(numberOfRequestsAhead);
        m.gpsModel().setLogRequestAhead(m.getLogRequestAhead());
        m.saveSettings();
    }

    /**
     * Convert the log given the provided parameters using other methods.
     * 
     * @param logType
     *            Indicates the type of log that should be written. For example
     *            Model.CSV_LOGTYPE .
     * @see Model#CSV_LOGTYPE
     * @see Model#TRK_LOGTYPE
     * @see Model#KML_LOGTYPE
     * @see Model#PLT_LOGTYPE
     * @see Model#GPX_LOGTYPE
     * @see Model#NMEA_LOGTYPE
     * @see Model#GMAP_LOGTYPE
     */
    public final int doConvertLog(final int logType) {
        int result;
        String ext = "";
        GPSFile gpsFile = null;
        GPSLogConvert lc;
        result = 0;

        /*
         * Check the input file
         */
        if (m.getLogFilePath().toLowerCase().endsWith(".trl")) {
            lc = new HoluxTrlLogConvert();
        } else if (m.getLogFilePath().toLowerCase().endsWith(".csv")) {
            lc = new CSVLogConvert();
        } else if (m.getLogFilePath().toLowerCase().endsWith(".sr")) {
            lc = new DPL700LogConvert();
            // / TODO: set SR Log type correctly.
            ((DPL700LogConvert) lc)
                    .setLogType(m.getGPSType() == GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR ? 0
                            : 1);
        } else {
            switch (m.getBinDecoder()) {
            case DECODER_THOMAS:
                try {
                    // TODO: Reference directly once integrated for PDA too.
                    lc = (GPSLogConvert) Class.forName(
                            "gps.parser.NewLogConvert").newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    lc = new BT747LogConvert();
                }
                break;
            default:
            case DECODER_ORG:
                lc = new BT747LogConvert();
                ((BT747LogConvert) lc).setHolux(m.getForceHolux241());
                break;
            }
        }
        GPSFilter[] usedFilters;
        getLogFilterSettings(); // TODO : Do this another way (in the model for
        // example)
        if (m.getAdvFilterActive()) {
            usedFilters = m.getLogFiltersAdv();
        } else {
            usedFilters = m.getLogFilters();
        }
        lc.setTimeOffset(m.getTimeOffsetHours() * SECONDS_PER_HOUR);
        lc.setConvertWGS84ToMSL(m.isConvertWGS84ToMSL());

        switch (logType) {
        case Model.CSV_LOGTYPE:
            gpsFile = new GPSCSVFile();
            ext = ".csv";
            break;
        case Model.TRK_LOGTYPE:
            gpsFile = new GPSCompoGPSTrkFile();
            ext = ".TRK";
            break;
        case Model.KML_LOGTYPE:
            gpsFile = new GPSKMLFile();
            ext = ".kml";
            break;
        case Model.PLT_LOGTYPE:
            gpsFile = new GPSPLTFile();
            ext = ".plt";
            break;
        case Model.GPX_LOGTYPE:
            gpsFile = new GPSGPXFile();
            ext = ".gpx";
            // Force offset to 0 if selected in menu.
            if (m.getGpxUTC0()) {
                lc.setTimeOffset(0);
            }
            ((GPSGPXFile) gpsFile).setTrkSegSplitOnlyWhenSmall(m
                    .getGpxTrkSegWhenBig());
            break;
        case Model.NMEA_LOGTYPE:
            gpsFile = new GPSNMEAFile();
            ((GPSNMEAFile) gpsFile).setNMEAoutput(m.getNMEAset());
            ext = ".nmea";
            break;
        case Model.GMAP_LOGTYPE:
            gpsFile = new GPSGmapsHTMLEncodedFile();
            ((GPSGmapsHTMLEncodedFile) gpsFile).setGoogleKeyCode(m
                    .getGoogleMapKey());
            ext = ".html";
            break;
        default:
            lastError = BT747Constants.ERROR_UNKNOWN_OUTPUT_FORMAT;
            lastErrorInfo = ext;
            result = lastError;
        }

        if (gpsFile != null) {
            m.logConversionStarted(logType);

            gpsFile.setAddLogConditionInfo(m.getOutputLogConditions());
            gpsFile.setImperial(m.getImperial());
            gpsFile.setRecordNbrInLogs(m.getRecordNbrInLogs());
            gpsFile.setBadTrackColor(m.getColorInvalidTrack());
            for (int i = 0; i < usedFilters.length; i++) {
                usedFilters[i].setStartTime(m.getFilterStartTime());
                usedFilters[i].setEndTime(m.getFilterEndTime()
                        + (SECONDS_PER_DAY - 1));
            }
            gpsFile.setFilters(usedFilters);
            gpsFile.initialiseFile(m.getReportFileBasePath(), ext, m.getCard(),
                    m.getFileSeparationFreq());
            gpsFile.setTrackSepTime(m.getTrkSep() * SECONDS_PER_MINUTE);
            lastError = lc.toGPSFile(m.getLogFilePath(), gpsFile, m.getCard());
            lastErrorInfo = lc.getErrorInfo();
            result = lastError;
        } else {
            // TODO report error
        }
        m.logConversionEnded(logType);
        return result;
    }

    private int lastError;
    private String lastErrorInfo = "";

    /**
     * Convert the log into an array of trackpoints.
     * 
     * @return Array of selected trackpoints.
     */

    public final GPSRecord[] doConvertLogToTrackPoints() {
        int error = 0;
        GPSRecord[] result;
        GPSArray gpsFile = null;
        GPSLogConvert lc;

        /*
         * Check the input file
         */
        if (m.getLogFilePath().toLowerCase().endsWith(".trl")) {
            lc = new HoluxTrlLogConvert();
        } else if (m.getLogFilePath().toLowerCase().endsWith(".csv")) {
            lc = new CSVLogConvert();
        } else if (m.getLogFilePath().toLowerCase().endsWith(".sr")) {
            lc = new DPL700LogConvert();
            // / TODO: set SR Log type correctly.
            ((DPL700LogConvert) lc)
                    .setLogType(m.getGPSType() == GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR ? 0
                            : 1);
        } else {
            switch (m.getBinDecoder()) {
            case DECODER_THOMAS:
                try {
                    // TODO: Reference directly once integrated for PDA too.
                    lc = (GPSLogConvert) Class.forName(
                            "gps.parser.NewLogConvert").newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    lc = new BT747LogConvert();
                }
                break;
            default:
            case DECODER_ORG:
                lc = new BT747LogConvert();
                ((BT747LogConvert) lc).setHolux(m.getForceHolux241());
                break;
            }
        }
        GPSFilter[] usedFilters;
        getLogFilterSettings(); // TODO : Do this another way (in the model for
        // example)
        if (m.getAdvFilterActive()) {
            usedFilters = m.getLogFiltersAdv();
        } else {
            usedFilters = m.getLogFilters();
        }
        lc.setTimeOffset(m.getTimeOffsetHours() * SECONDS_PER_HOUR);
        lc.setConvertWGS84ToMSL(m.isConvertWGS84ToMSL());

        gpsFile = new GPSArray();

        // m.logConversionStarted(log_type);

        // gpsFile.setAddLogConditionInfo(m.getOutputLogConditions());
        // gpsFile.setImperial(m.getImperial());
        // gpsFile.setRecordNbrInLogs(m.getRecordNbrInLogs());
        // gpsFile.setBadTrackColor(m.getColorInvalidTrack());

        for (int i = 0; i < usedFilters.length; i++) {
            usedFilters[i].setStartTime(m
                    .getFilterStartTime());
            usedFilters[i].setEndTime(m.getFilterEndTime());
        }
        gpsFile.setFilters(usedFilters);
        // gpsFile.initialiseFile(m.getReportFileBasePath(), ext, m.getCard(),
        // m.getFileSeparationFreq());
        // gpsFile.setTrackSepTime(m.getTrkSep() * 60);
        error = lc.toGPSFile(m.getLogFilePath(), gpsFile, m.getCard());
        if (error != 0) {
            lastError = error;
            lastErrorInfo = lc.getErrorInfo();
            result = null;
        } else {
            result = gpsFile.getGpsTrackPoints();
        }
        return result;
    }

    /**
     * Set the 'incremental download' configuration.
     * 
     * @param incrementalDownload
     *            true if the log download should be incremental.
     */
    public final void setIncremental(final boolean incrementalDownload) {
        m.setIncremental(incrementalDownload);
    }

    /**
     * Cancel the log download process.
     */
    public final void cancelGetLog() {
        m.gpsModel().cancelGetLog();
    }

    /**
     * Start the log download process.
     */
    public final void startDownload() {
        switch (m.getGPSType()) {
        default:
        case GPS_TYPE_DEFAULT:
            startDefaultDownload();
            break;
        case GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
        case GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
        case GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
            startDPL700Download();
            break;
        }
    }

    /**
     * Start the default log download process without taking into account the
     * device type.
     */
    public final void startDefaultDownload() {
        try {
            m.gpsModel().getLogInit(0, /* StartPosition */
            m.gpsModel().logMemUsed - 1, /* EndPosition */
            m.getChunkSize(), /* Size per request */
            m.getLogFilePath(), /* Log file name */
            m.getCard(), /* Card for file operations */
            m.isIncremental() /* Incremental download */);
        } catch (Exception e) {
            // TODO: handle exception
        }
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
        m.gpsModel().replyToOkToOverwrite(isOkToOverwrite);
    }

    /**
     * The log is being erased - the user request to abandon waiting for the end
     * of this operation.
     */
    public final void stopErase() {
        m.gpsModel().stopErase();
    }

    /**
     * Initiate the download of a 'DPL700' log.
     */
    public final void startDPL700Download() {
        m.gpsModel().getDPL700Log(m.getLogFilePath(), m.getCard());
    }

    /**
     * Set the gpsType used for log conversion and some other operations (needed
     * in cases where the type can not be automatically detected).
     * 
     * @param gpsType
     *            A value out of: - {@link #GPS_TYPE_DEFAULT}<br> -
     *            {@link #GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII}<br> -
     *            {@link #GPS_TYPE_GISTEQ_ITRACKU_NEMERIX}<br> -
     *            {@link #GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR}<br>
     */
    public final void setGPSType(final int gpsType) {
        m.setGPSType(gpsType);
    }

    /**
     * Default gps type selection (MTK Logger).
     */
    public static final int GPS_TYPE_DEFAULT = 0;
    /**
     * ITrackU-Nemerix type.
     */
    public static final int GPS_TYPE_GISTEQ_ITRACKU_NEMERIX = 1;
    /**
     * ITrackU-Phototrackr type.
     */
    public static final int GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR = 2;
    /**
     * ITrackU-SirfIII type.
     */
    public static final int GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII = 3;

    /***************************************************************************
     * Device state
     **************************************************************************/

    /**
     * Activate logging on the device.
     */
    public final void startLog() {
        m.gpsModel().startLog();
        m.gpsModel().reqLogOnOffStatus();
    }

    /**
     * Stop logging on the device.
     */
    public final void stopLog() {
        m.gpsModel().stopLog();
        m.gpsModel().reqLogOnOffStatus();
    };

    /**
     * Set log overwrite mode on the device.
     * 
     * @param isOverWriteLog
     *            true - overwrite data in device when full false - stop logging
     *            when device is full
     */
    public final void setLogOverwrite(final boolean isOverWriteLog) {
        m.gpsModel().setLogOverwrite(isOverWriteLog);
        m.gpsModel().reqLogOverwrite();
    };

    /**
     * Request the Logger Version data from the device.<br>
     * Once retrieved an event {@link gps.GpsEvent#DATA_UPDATE} will be
     * generated so that {@link Model#getMtkLogVersion()} can be used to
     * retrieve the actual value.
     * 
     */
    public final void reqMtkLogVersion() {
        m.gpsModel().reqMtkLogVersion();
    }

    /**
     * Request the amount of memory in use from the device.
     */
    public final void reqLogMemUsed() {
        m.gpsModel().reqLogMemUsed();
    }

    /**
     * Request the number of points logged in memory.
     */
    public final void reqLogMemPtsLogged() {
        m.gpsModel().reqLogMemPtsLogged();
    }

    /**
     * Request the log overwrite status from the device.<br>
     * {@link Model#isLogFullOverwrite()} must be used on a {link
     * {@link gps.GpsEvent#DATA_UPDATE} event to get the data.
     */
    public final void reqLogOverwrite() {
        m.gpsModel().reqLogOverwrite();
    }

    /**
     * Request a set of device information from the GPS device that can be after
     * {@link gps.GpsEvent#DATA_UPDATE} events.
     */
    public final void reqDeviceInfo() {
        m.gpsModel().reqDeviceInfo();
    }

    /**
     * Request the status register from the device.<br>
     * After the associated {@link gps.GpsEvent#DATA_UPDATE} event one can
     * retrieve the data using:<br> - {@link Model#isLoggingActive()} <br> -
     * {@link GPSstate#loggerIsFull} (not currently public)<br> -
     * {@link GPSstate#loggerNeedsInit} (not currently public)<br> -
     * {@link GPSstate#loggerIsDisabled} (not currently public)<br>
     */
    public final void reqLogStatus() {
        m.gpsModel().reqLogStatus();
    }

    /**
     * Request the current log format from the device.<br>
     * After the associated {@link gps.GpsEvent#DATA_UPDATE} event one can
     * retrieve the data using:<br> - {@link Model#getLogFormat()} <br>
     */
    public final void reqLogFormat() {
        m.gpsModel().reqLogFormat();
    }

    /**
     * Sets a new log format on the device.<br>
     * 
     * @param newLogFormat
     *            <br>
     *            The bits in the newLogFormat can be defined using a bitwise OR
     *            of expressions like<br>
     *            (1<< IDX) <br>
     *            where IDX is one of the following:<br> -
     *            {@link BT747Constants#FMT_UTC_IDX} <br> -
     *            {@link BT747Constants#FMT_UTC_IDX} <br> -
     *            {@link BT747Constants#FMT_VALID_IDX} <br> -
     *            {@link BT747Constants#FMT_LATITUDE_IDX} <br> -
     *            {@link BT747Constants#FMT_LONGITUDE_IDX} <br> -
     *            {@link BT747Constants#FMT_HEIGHT_IDX} <br> -
     *            {@link BT747Constants#FMT_SPEED_IDX} <br> -
     *            {@link BT747Constants#FMT_HEADING_IDX} <br> -
     *            {@link BT747Constants#FMT_DSTA_IDX} <br> -
     *            {@link BT747Constants#FMT_DAGE_IDX} <br> -
     *            {@link BT747Constants#FMT_PDOP_IDX} <br> -
     *            {@link BT747Constants#FMT_HDOP_IDX} <br> -
     *            {@link BT747Constants#FMT_VDOP_IDX} <br> -
     *            {@link BT747Constants#FMT_NSAT_IDX} <br> -
     *            {@link BT747Constants#FMT_SID_IDX} <br> -
     *            {@link BT747Constants#FMT_ELEVATION_IDX} <br> -
     *            {@link BT747Constants#FMT_AZIMUTH_IDX} <br> -
     *            {@link BT747Constants#FMT_SNR_IDX} <br> -
     *            {@link BT747Constants#FMT_RCR_IDX} <br> -
     *            {@link BT747Constants#FMT_MILLISECOND_IDX} <br> -
     *            {@link BT747Constants#FMT_DISTANCE_IDX} <br> -
     *            {@link BT747Constants#FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX}
     *            <br>
     */
    public final void setLogFormat(final int newLogFormat) {
        m.gpsModel().setLogFormat(newLogFormat);
    }

    /**
     * Do the actual erase.
     */
    public final void eraseLog() {
        // TODO: Handle erase popup.
        m.gpsModel().eraseLog();
    }

    /**
     * A 'recovery Erase' attempts to recover memory that was previously
     * identified as 'bad'.
     */
    public final void recoveryEraseLog() {
        m.gpsModel().recoveryEraseLog();
    }

    /***************************************************************************
     * SECTION FOR CONNECTION RELATED METHODS.
     **************************************************************************/

    /**
     * Connect to the GPS (open the serial connection).
     */
    public final void connectGPS() {
        closeGPS();
        m.gpsRxTx().openPort();
        if (m.gpsRxTx().isConnected()) {
            performOperationsAfterGPSConnect();
        }
    }

    /**
     * Close the GPS connection.
     */
    public final void closeGPS() {
        if (m.gpsRxTx().isConnected()) {
            m.gpsRxTx().closePort();
            // TODO Move event posting to appropriate place (in model)
            m.postEvent(ModelEvent.DISCONNECTED);
        }
    }

    /**
     * open a Bluetooth connection Calls getStatus to request initial parameters
     * from the device. Set up the timer to regurarly poll the connection for
     * data.
     */
    public final void setBluetooth() {
        closeGPS();
        m.gpsRxTx().setBluetoothAndOpen();
        performOperationsAfterGPSConnect();
    }

    /**
     * open a Usb connection Calls getStatus to request initial parameters from
     * the device. Set up the timer to regurarly poll the connection for data.
     */
    public final void setUsb() {
        closeGPS();
        m.gpsRxTx().setUSBAndOpen();
        performOperationsAfterGPSConnect();
    }

    /**
     * open a connection on the given port number. Calls getStatus to request
     * initial parameters from the device. Set up the timer to regurarly poll
     * the connection for data.
     * 
     * @param port
     *            Port number to open
     */
    public final void setPort(final int port) {
        closeGPS();
        m.gpsRxTx().setPortAndOpen(port);
        performOperationsAfterGPSConnect();
    }

    /**
     * Sets the port's speed (baud rate).
     * 
     * @param baudRate
     *            The baud rate to set.
     */
    public final void setBaudRate(final int baudRate) {
        m.gpsRxTx().setBaudRate(baudRate);
    }

    /**
     * Select a port by its 'path' (/dev/usb9 for example or /dev/com1.
     * 
     * @param portName
     *            The path to the port.
     */
    public final void setFreeTextPort(final String portName) {
        closeGPS();
        m.gpsRxTx().setFreeTextPortAndOpen(portName);
        performOperationsAfterGPSConnect();
    }

    /**
     * Get the port text string.
     * 
     * @return A text string for the serial port (if not a port number).
     */
    public final String getFreeTextPort() {
        return m.gpsRxTx().getFreeTextPort();
    }

    /**
     * This does a number of operations once the GPS is effectively connected.
     * It stores the settings related to the port (since the connection was
     * successful) and starts of the Model to do port queries.
     */
    private void performOperationsAfterGPSConnect() {
        if (m.gpsRxTx().isConnected()) {
            m.gpsModel().getStatus();
            // TODO: Setup timer in gpsRxTx instead of in the gpsModel
            m.gpsModel().setupTimer();
            // Remember defaults
            m.setPortnbr(m.gpsRxTx().getPort());
            m.setBaudRate(m.gpsRxTx().getSpeed());
            m.setFreeTextPort(m.gpsRxTx().getFreeTextPort());
            m.saveSettings();
            m.postEvent(ModelEvent.CONNECTED);
        }
    }

    /**
     * Set the debugging state of the connection.
     * 
     * @param isConnDebugActive
     *            When true, the connection debug information is active.
     */
    public final void setDebugConn(final boolean isConnDebugActive) {
        m.gpsRxTx().setDebugConn(isConnDebugActive, m.getBaseDirPath());
    }

    /***************************************************************************
     * END OF SECTION FOR CONNECTION RELATED METHODS
     **************************************************************************/

    /**
     * Save all the user settings to disk.
     */
    public final void saveSettings() {
        m.saveSettings(); // Explicitly save settings
    }

    /**
     * Set the general debugging state.
     * 
     * @param isDebugActive
     *            If true, activate general debug.
     * 
     * @see #setDebugConn(boolean) for other debug functionality.
     */
    public final void setDebug(final boolean isDebugActive) {
        m.gpsModel().setDebug(isDebugActive);
    }

    /**
     * Selects the original decoder.
     */
    public static final int DECODER_ORG = 1;
    /**
     * Selects an experimental decoder - not fully debugged yet.
     */
    public static final int DECODER_THOMAS = 2;

    /**
     * Sets the MTK binary log decoder to use.
     * 
     * @param logDecoderType
     *            One of the following values:<br> - {@link #DECODER_ORG}<br> -
     *            {@link #DECODER_THOMAS}
     */
    public final void setBinDecoder(final int logDecoderType) {
        m.setBinDecoder(logDecoderType);
    }

    /**
     * Sets the trackpoint RCR mask for the active filter.
     * 
     * @param rcrMask -
     *            {@link BT747Constants#RCR_TIME_MASK}<br>-
     *            {@link BT747Constants#RCR_SPEED_MASK}<br>-
     *            {@link BT747Constants#RCR_DISTANCE_MASK}<br>-
     *            {@link BT747Constants#RCR_BUTTON_MASK}<br>-
     *            {@link BT747Constants#RCR_APP1_MASK}<br>-
     *            {@link BT747Constants#RCR_APP2_MASK}<br>-
     *            {@link BT747Constants#RCR_APP3_MASK}<br>-
     *            {@link BT747Constants#RCR_APP4_MASK}<br>-
     *            {@link BT747Constants#RCR_APP5_MASK}<br>-
     *            {@link BT747Constants#RCR_APP6_MASK}<br>-
     *            {@link BT747Constants#RCR_APP7_MASK}<br>-
     *            {@link BT747Constants#RCR_APP8_MASK}<br>-
     *            {@link BT747Constants#RCR_APP9_MASK}<br>-
     *            {@link BT747Constants#RCR_APPX_MASK}<br>-
     *            {@link BT747Constants#RCR_APPY_MASK}<br>-
     *            {@link BT747Constants#RCR_APPZ_MASK}<br>-
     *            {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final void setTrkPtRCR(final int rcrMask) {
        m.setTrkPtRCR(rcrMask);
        getLogFilterSettings();
    }

    /**
     * Sets the waypoint RCR mask for the active filter.
     * 
     * @param rcrMask -
     *            {@link BT747Constants#RCR_TIME_MASK}<br>-
     *            {@link BT747Constants#RCR_SPEED_MASK}<br>-
     *            {@link BT747Constants#RCR_DISTANCE_MASK}<br>-
     *            {@link BT747Constants#RCR_BUTTON_MASK}<br>-
     *            {@link BT747Constants#RCR_APP1_MASK}<br>-
     *            {@link BT747Constants#RCR_APP2_MASK}<br>-
     *            {@link BT747Constants#RCR_APP3_MASK}<br>-
     *            {@link BT747Constants#RCR_APP4_MASK}<br>-
     *            {@link BT747Constants#RCR_APP5_MASK}<br>-
     *            {@link BT747Constants#RCR_APP6_MASK}<br>-
     *            {@link BT747Constants#RCR_APP7_MASK}<br>-
     *            {@link BT747Constants#RCR_APP8_MASK}<br>-
     *            {@link BT747Constants#RCR_APP9_MASK}<br>-
     *            {@link BT747Constants#RCR_APPX_MASK}<br>-
     *            {@link BT747Constants#RCR_APPY_MASK}<br>-
     *            {@link BT747Constants#RCR_APPZ_MASK}<br>-
     *            {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final void setWayPtRCR(final int rcrMask) {
        m.setWayPtRCR(rcrMask);
        getLogFilterSettings();
    }

    /**
     * Sets the 'Valid' filter mask for the current track filter.
     * 
     * @param validMask
     *            The filter mask to set for the validity filter. Use the
     *            following constants:<br>-
     *            {@link BT747Constants#VALID_NO_FIX_MASK} <br>-
     *            {@link BT747Constants#VALID_SPS_MASK} <br>-
     *            {@link BT747Constants#VALID_DGPS_MASK} <br>-
     *            {@link BT747Constants#VALID_PPS_MASK} <br>-
     *            {@link BT747Constants#VALID_RTK_MASK} <br>-
     *            {@link BT747Constants#VALID_FRTK_MASK} <br>-
     *            {@link BT747Constants#VALID_ESTIMATED_MASK} <br>-
     *            {@link BT747Constants#VALID_MANUAL_MASK} <br>-
     *            {@link BT747Constants#VALID_SIMULATOR_MASK} <br>-
     * 
     */
    public final void setTrkPtValid(final int validMask) {
        m.setTrkPtValid(validMask);
        getLogFilterSettings();
    }

    /**
     * Sets the 'Valid' filter mask for the current waypoint filter.
     * 
     * @param validMask
     *            The filter mask to set for the validity filter. Use the
     *            following constants:<br>-
     *            {@link BT747Constants#VALID_NO_FIX_MASK} <br>-
     *            {@link BT747Constants#VALID_SPS_MASK} <br>-
     *            {@link BT747Constants#VALID_DGPS_MASK} <br>-
     *            {@link BT747Constants#VALID_PPS_MASK} <br>-
     *            {@link BT747Constants#VALID_RTK_MASK} <br>-
     *            {@link BT747Constants#VALID_FRTK_MASK} <br>-
     *            {@link BT747Constants#VALID_ESTIMATED_MASK} <br>-
     *            {@link BT747Constants#VALID_MANUAL_MASK} <br>-
     *            {@link BT747Constants#VALID_SIMULATOR_MASK} <br>-
     * 
     */
    public final void setWayPtValid(final int validMask) {
        m.setWayPtValid(validMask);
        getLogFilterSettings();
    }

    // The way logfilters are handled should be reviewed.

    /**
     * Get the settings for the filters (from the settings).
     * 
     * @param logFilters
     *            logFilters to replicate the settings to.
     */
    private void getSettings(final GPSFilter[] logFilters) {
        logFilters[GPSFilter.C_TRKPT_IDX].setRcrMask(m.getTrkPtRCR());
        logFilters[GPSFilter.C_TRKPT_IDX].setValidMask(m.getTrkPtValid());
        logFilters[GPSFilter.C_WAYPT_IDX].setRcrMask(m.getWayPtRCR());
        logFilters[GPSFilter.C_WAYPT_IDX].setValidMask(m.getWayPtValid());
    };

    /**
     * Get the settings for the filters and restore the common settings to the
     * normal and advanced log filters.
     */
    private void getLogFilterSettings() {
        getSettings(m.getLogFilters());
        getSettings(m.getLogFiltersAdv());
    }

    /**
     * This sets MTK specific settings into its flash.<br>
     * The MTK device stores a number of settings in its internal flash which is
     * different from the log memory. These settings are restored after loss of
     * power for example.
     * 
     * 
     * @param lock
     *            When true, subsequent changes in these settings will be
     *            impossible.
     * @param updateRate
     *            The 'fix period' of the GPS in ms. When this is 200, then the
     *            Fix is 5Hz.
     * @param baudRate
     *            The speed of the serial communication of the MTK chipset. Be
     *            carefull - this may be the internal speed - not the external
     *            speed!
     * @param periodGLL
     *            The period of emission of the GLL sentence (relative to the
     *            fix).
     * @param periodRMC
     *            The period of emission of the RMC sentence (relative to the
     *            fix).
     * @param periodVTG
     *            The period of emission of the VTG sentence (relative to the
     *            fix).
     * @param periodGSA
     *            The period of emission of the GSA sentence (relative to the
     *            fix).
     * @param periodGSV
     *            The period of emission of the GSV sentence (relative to the
     *            fix).
     * @param periodGGA
     *            The period of emission of the GGA sentence (relative to the
     *            fix).
     * @param periodZDA
     *            The period of emission of the ZDA sentence (relative to the
     *            fix).
     * @param periodMCHN
     *            The period of emission of the MCHN sentence (relative to the
     *            fix).
     */
    public final void setFlashUserOption(final boolean lock,
            final int updateRate, final int baudRate, final int periodGLL,
            final int periodRMC, final int periodVTG, final int periodGSA,
            final int periodGSV, final int periodGGA, final int periodZDA,
            final int periodMCHN) {
        m.gpsModel().setFlashUserOption(lock, updateRate, baudRate, periodGLL,
                periodRMC, periodVTG, periodGSA, periodGSV, periodGGA,
                periodZDA, periodMCHN);
        reqFlashUserOption();
    }

    /**
     * Request the flash user settings from the device. Following the relevant
     * event, the settings must be retrieved using
     * {@link Model#getDtUpdateRate()}<br>- {@link Model#getDtGLL_Period()}<br>-
     * {@link Model#getDtRMC_Period()}<br>- {@link Model#getDtVTG_Period()}<br>-
     * {@link Model#getDtGSA_Period()}<br>- {@link Model#getDtGSV_Period()}<br>-
     * {@link Model#getDtGGA_Period()}<br>- {@link Model#getDtZDA_Period()}<br>-
     * {@link Model#getDtMCHN_Period()}<br>- {@link Model#getDtBaudRate()}<br>-
     * {@link Model#getDtUserOptionTimesLeft()}<br>-
     * 
     */
    public final void reqFlashUserOption() {
        m.gpsModel().reqFlashUserOption();
    }

    /**
     * Get the flash user settings from the device.
     */
    public final void reqHoluxName() {
        m.gpsModel().reqHoluxName();
    }

    /**
     * Request the bluetooth Mac Address from the device.
     */
    public final void reqBTAddr() {
        m.gpsModel().reqBtMacAddr();
    }

    /**
     * Set the textual description of the holux device.
     * 
     * @param holuxName
     *            The string to set as the Holux Name.
     */
    public final void setHoluxName(final String holuxName) {
        m.gpsModel().setHoluxName(holuxName);
        reqHoluxName();
    }

    /**
     * Request the current NMEA period settings of the device.
     */
    public final void reqNMEAPeriods() {
        m.gpsModel().reqNMEAPeriods();
    }

    /**
     * Set the NMEA period settings of the device.
     * 
     * @param periods
     *            The array indexes are given by:<br>-
     *            {@link BT747Constants#NMEA_SEN_GLL_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_RMC_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_VTG_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GGA_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GSA_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GSV_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GRS_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GST_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MALM_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MEPH_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MDGP_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MDBG_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_ZDA_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MCHN_IDX}<br>-
     */
    public final void setNMEAPeriods(final int[] periods) {
        m.gpsModel().setNMEAPeriods(periods);
        reqNMEAPeriods();
    }

    /**
     * Sets default NMEA periods (as observed on one iBlue 747 device).
     */
    public final void setNMEADefaultPeriods() {
        m.gpsModel().setNMEADefaultPeriods();
    }

    /**
     * Sets the enable of SBAS (DGPS) satellites that are in test.
     * 
     * @param isSBASTestEnabled
     *            When true, enable test satellites.
     */
    public final void setSBASTestEnabled(final boolean isSBASTestEnabled) {
        m.gpsModel().setSBASTestEnabled(isSBASTestEnabled);
        reqSBASTestEnabled();
    }

    /**
     * Request the current status of SBAS (DGPS) satellites in test enable
     * setting. Actual value to be retrieved later with
     * {@link Model#isSBASTestEnabled()}.
     */
    public final void reqSBASTestEnabled() {
        m.gpsModel().reqSBASTestEnabled();
    }

    /**
     * Enable SBAS (DGPS).
     * 
     * @param set
     *            When true, enables SBAS.
     */
    public final void setSBASEnabled(final boolean set) {
        m.gpsModel().setSBASEnabled(set);
        reqSBASEnabled();
    }

    /**
     * Request the current status of SBAS (DGPS) enable setting. Actual value to
     * be retrieved later with {@link Model#isSBASEnabled()}.
     */
    public final void reqSBASEnabled() {
        m.gpsModel().reqSBASEnabled();
    }

    /**
     * Request the GPS's Datum mode. To be retrieved later using
     * {@link Model#getDatum()}.
     */
    public final void reqDatumMode() {
        m.gpsModel().reqDatumMode();
    }

    /**
     * Set GPS's Datum mode.
     * 
     * @param mode
     *            The datum mode to set.
     */
    public final void setDatumMode(final int mode) {
        m.gpsModel().setDatumMode(mode);
        reqDatumMode();
    }

    /**
     * Set the DGPS mode to use when SBAS enabled.
     * 
     * @param mode
     *            The mode to use.
     */
    public final void setDGPSMode(final int mode) {
        m.gpsModel().setDGPSMode(mode);
        reqDGPSMode();
    }

    /**
     * Request the current DGPS mode in use. Get actual setting with
     * {@link Model#getDgpsMode()} later.
     */
    public final void reqDGPSMode() {
        m.gpsModel().reqDGPSMode();
    }

    /**
     * Enable the power save mode on the device - this setting is untested.
     * 
     * @param set
     *            If true, enable power save mode.
     */
    public final void setPowerSaveEnabled(final boolean set) {
        m.gpsModel().setPowerSaveEnabled(set);
        reqPowerSaveEnabled();
    }

    /**
     * Request the power save mode setting of the device. Need to get the actual
     * setting later with {@link Model#isPowerSaveEnabled()}.
     */
    public final void reqPowerSaveEnabled() {
        m.gpsModel().reqPowerSaveEnabled();
    }

    /**
     * Request the log condition (reason) settings of the device. Need to get
     * actual values through:<br>
     * {@link Model#getLogTimeInterval()<br>
     * {@link Model#getLogDistanceInterval()} <br>
     * {@link Model#getLogSpeedInterval()}
     */
    public final void reqLogReasonStatus() {
        m.gpsModel().reqLogReasonStatus();
    }

    /**
     * Request the fix interval setting from the device. Need to get the actual
     * value through {@link Model#getLogFixPeriod()}.
     * 
     */
    public final void reqFixInterval() {
        m.gpsModel().reqFixInterval();
    }

    public final void logImmediate(final int value) {
        m.gpsModel().logImmediate(value);
    }

    public final void setFixInterval(final int value) {
        m.gpsModel().setFixInterval(value);
        reqFixInterval();
    }

    public final void setLogTimeInterval(final int value) {
        m.gpsModel().setLogTimeInterval(value);
        // TODO : request time interval
    }

    public final void setLogDistanceInterval(final int value) {
        m.gpsModel().setLogDistanceInterval(value);
        // TODO : request distance interval
    }

    public final void setLogSpeedInterval(final int value) {
        m.gpsModel().setLogSpeedInterval(value);
        // TODO : request speed interval
    }

    public final void doHotStart() {
        m.gpsModel().doHotStart();
    }

    public final void doColdStart() {
        m.gpsModel().doColdStart();
    }

    public final void doWarmStart() {
        m.gpsModel().doWarmStart();
    }

    public final void doFullColdStart() {
        m.gpsModel().doFullColdStart();
    }

    public final boolean isEnableStoreOK() {
        // TODO: This function serves to enable 'save settings'.
        // should do this through an event to the view.
        return m.gpsModel().isDataOK(
                (GPSstate.C_OK_FIX | GPSstate.C_OK_DGPS | GPSstate.C_OK_SBAS
                        | GPSstate.C_OK_NMEA | GPSstate.C_OK_SBAS_TEST
                        |
                        // GPSstate.C_OK_SBAS_DATUM |
                        GPSstate.C_OK_TIME | GPSstate.C_OK_SPEED
                        | GPSstate.C_OK_DIST | GPSstate.C_OK_FORMAT));
    }

    public final void setStats(final boolean b) {
        m.gpsModel().setStats(b);
    }

    public final void setGpsDecode(final boolean value) {
        m.setGpsDecode(value);
        m.gpsModel().setGpsDecode(value);
    }

    public final void setForceHolux241(final boolean b) {
        m.setForceHolux241(b);
    }

    public final void setGpxTrkSegWhenBig(final boolean b) {
        m.setGpxTrkSegWhenBig(b);
    }

    public final void setGpxUTC0(final boolean b) {
        m.setGpxUTC0(b);
    }

    // For PDA - move through the menus using the arrows.
    public final void setTraversableFocus(final boolean b) {
        m.setTraversableFocus(b);
    }

    public final void setEndDate(final int d) {
        m.setFilterEndTime(d);
    }

    public final void setStartDate(final int d) {
        m.setFilterStartTime(d);
    }

    public final void setRecordNbrInLogs(final boolean b) {
        m.setRecordNbrInLogs(b);
    }

    public final void setTrkSep(final int value) {
        m.setTrkSep(value);
    }

    public final void setColorInvalidTrack(final String s) {
        m.setColorInvalidTrack(s);
    }

    // "Volatile" settings of the MTK loggers:
    // - Log conditions;
    // - Time, Speed, Distance[3x 4 byte]
    // - Log format; [4 byte]
    // - Fix period [4 byte]
    // - SBAS /DGPS / TEST SBAS [byte, byte, byte]
    // - Log overwrite/STOP [byte, byte]
    // - NMEA output [18 byte]

    public final void storeSetting1() {
        m.setTimeConditionSetting1(m.getLogTimeInterval());
        m.setDistConditionSetting1(m.getLogDistanceInterval());
        m.setSpeedConditionSetting1(m.getLogSpeedInterval());
        m.setLogFormatConditionSetting1(m.getLogFormat());
        m.setFixSetting1(m.getLogFixPeriod());
        m.setSBASSetting1(m.isSBASEnabled());
        m.setDGPSSetting1(m.getDgpsMode());
        m.setTestSBASSetting1(m.isSBASTestEnabled());
        m.setLogOverwriteSetting1(m.isLogFullOverwrite());
        String sNMEA = "";
        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            sNMEA += (m.getNMEAPeriod(i));
        }
        m.setNMEASetting1(sNMEA);
    }

    public final void restoreSetting1() {
        setLogTimeInterval(m.getTimeConditionSetting1());
        setLogDistanceInterval(m.getDistConditionSetting1());
        setLogSpeedInterval(m.getSpeedConditionSetting1());
        setLogFormat(m.getLogFormatSetting1());
        setFixInterval(m.getFixSetting1());
        setSBASEnabled(m.getSBASSetting1());
        setSBASTestEnabled(m.getTestSBASSetting1());
        setDGPSMode(m.getDPGSSetting1());
        setLogOverwrite(m.getLogOverwriteSetting1());

        String sNMEA = m.getNMEASetting1();
        int[] periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            periods[i] = (int) (sNMEA.charAt(i) - '0');
        }
        setNMEAPeriods(periods);
    }

    public final void reqSettingsForStorage() {
        reqLogReasonStatus();
        reqLogFormat();
        reqFixInterval();
        reqSBASEnabled();
        reqSBASTestEnabled();
        reqDGPSMode();
        reqLogOverwrite();
        reqNMEAPeriods();
    }

    public final void setAdvFilterActive(final boolean b) {
        m.setAdvFilterActive(b);
    };

    public final void setFilterMinRecCount(final int i) {
        m.setFilterMinRecCount(i);
    }

    public final void setFilterMaxRecCount(final int i) {
        m.setFilterMaxRecCount(i);
    }

    public final void setFilterMinSpeed(final float i) {
        m.setFilterMinSpeed(i);
    }

    public final void setFilterMaxSpeed(final float i) {
        m.setFilterMaxSpeed(i);
    }

    public final void setFilterMinDist(final float i) {
        m.setFilterMinDist(i);
    }

    public final void setFilterMaxDist(final float i) {
        m.setFilterMaxDist(i);
    }

    public final void setFilterMaxPDOP(final float i) {
        m.setFilterMaxPDOP(i);
    }

    public final void setFilterMaxHDOP(final float i) {
        m.setFilterMaxHDOP(i);
    }

    public final void setFilterMaxVDOP(final float i) {
        m.setFilterMaxVDOP(i);
    }

    public final void setFilterMinNSAT(final int i) {
        m.setFilterMinNSAT(i);
    }

    public final void setFilters() {
        // TODO : Should schedule this after a while.
        for (int i = m.getLogFiltersAdv().length - 1; i >= 0; i--) {
            GPSFilterAdvanced filter = m.getLogFiltersAdv()[i];
            filter.setMinRecCount(m.getFilterMinRecCount());
            filter.setMaxRecCount(m.getFilterMaxRecCount());
            filter.setMinSpeed(m.getFilterMinSpeed());
            filter.setMaxSpeed(m.getFilterMaxSpeed());
            filter.setMinDist(m.getFilterMinDist());
            filter.setMaxDist(m.getFilterMaxDist());
            filter
                    .setMaxPDOP((int) (m.getFilterMaxPDOP() * XDOP_FLOAT_TO_INT_100));
            filter
                    .setMaxHDOP((int) (m.getFilterMaxHDOP() * XDOP_FLOAT_TO_INT_100));
            filter
                    .setMaxVDOP((int) (m.getFilterMaxVDOP() * XDOP_FLOAT_TO_INT_100));
            filter.setMinNSAT(m.getFilterMinNSAT());
        }
    }

    /**
     * Sets the NMEA string types to write to the NMEA output file format.
     * 
     * @param formatNMEA
     *            Bit format using following bit indexes:<br>-
     *            {@link BT747Constants#NMEA_SEN_GLL_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_RMC_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_VTG_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GGA_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GSA_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GSV_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GRS_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_GST_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MALM_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MEPH_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MDGP_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MDBG_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_ZDA_IDX}<br>-
     *            {@link BT747Constants#NMEA_SEN_MCHN_IDX}<br>
     */
    public final void setNMEAset(final int formatNMEA) {
        m.setNMEAset(formatNMEA);
    }

    /**
     * Sets the 'Valid' filter mask for the given filter type and the currently
     * active filters.
     * 
     * @param filterType
     *            A value out of:<br> - {@link GPSFilter#C_TRKPT_IDX} ;<br> -
     *            {@link GPSFilter#C_WAYPT_IDX} .<br>
     * @param mask
     *            The filter mask to set for the validity filter. Use the
     *            following constants:<br>-
     *            {@link BT747Constants#VALID_NO_FIX_MASK} <br>-
     *            {@link BT747Constants#VALID_SPS_MASK} <br>-
     *            {@link BT747Constants#VALID_DGPS_MASK} <br>-
     *            {@link BT747Constants#VALID_PPS_MASK} <br>-
     *            {@link BT747Constants#VALID_RTK_MASK} <br>-
     *            {@link BT747Constants#VALID_FRTK_MASK} <br>-
     *            {@link BT747Constants#VALID_ESTIMATED_MASK} <br>-
     *            {@link BT747Constants#VALID_MANUAL_MASK} <br>-
     *            {@link BT747Constants#VALID_SIMULATOR_MASK} <br>-
     * 
     */
    public final void setValidMask(final int filterType, final int mask) {
        // TODO: not sure this is needed anymore
        m.getLogFilters()[filterType].setValidMask(mask);
    }

    /**
     * Sets the 'RCR' filter mask for the given filter type and the currently
     * active filters.
     * 
     * @param filterType
     *            A value out of:<br> - {@link GPSFilter#C_TRKPT_IDX} ;<br> -
     *            {@link GPSFilter#C_WAYPT_IDX} .<br>
     * @param rcrMask
     *            The filter mask to set for the rcr filter. Use the following
     *            constants:<br>
     *            <br>- {@link BT747Constants#RCR_TIME_MASK}<br>-
     *            {@link BT747Constants#RCR_SPEED_MASK}<br>-
     *            {@link BT747Constants#RCR_DISTANCE_MASK}<br>-
     *            {@link BT747Constants#RCR_BUTTON_MASK}<br>-
     *            {@link BT747Constants#RCR_APP1_MASK}<br>-
     *            {@link BT747Constants#RCR_APP2_MASK}<br>-
     *            {@link BT747Constants#RCR_APP3_MASK}<br>-
     *            {@link BT747Constants#RCR_APP4_MASK}<br>-
     *            {@link BT747Constants#RCR_APP5_MASK}<br>-
     *            {@link BT747Constants#RCR_APP6_MASK}<br>-
     *            {@link BT747Constants#RCR_APP7_MASK}<br>-
     *            {@link BT747Constants#RCR_APP8_MASK}<br>-
     *            {@link BT747Constants#RCR_APP9_MASK}<br>-
     *            {@link BT747Constants#RCR_APPX_MASK}<br>-
     *            {@link BT747Constants#RCR_APPY_MASK}<br>-
     *            {@link BT747Constants#RCR_APPZ_MASK}<br>-
     *            {@link BT747Constants#RCR_ALL_APP_MASK}
     * 
     */
    public final void setRcrMask(final int filterType, final int rcrMask) {
        // TODO: not sure this is needed anymore
        m.getLogFilters()[filterType].setRcrMask(rcrMask);
    }

    /**
     * Set the local time offset versus UTC (used in adjusting the time in the
     * output formats).
     * 
     * @param timeOffsetHours
     *            The offset in hours.
     */
    public final void setTimeOffsetHours(final int timeOffsetHours) {
        m.setTimeOffsetHours(timeOffsetHours);
    }

    /**
     * @param value
     *            0=all data in one file;<br>
     *            1=one file per day (split at midnight after time offset
     *            calculation)<br>
     *            2=one file per track (a track separation occurs when the time
     *            between two log points is bigger than a given number.
     */
    public final void setOutputFileSplitType(final int value) {
        m.setOutputFileSplitType(value);
    }

    /**
     * @param value
     */
    public final void setNoGeoid(final boolean value) {
        m.setConvertWGS84ToMSL(value);
    }

    /**
     * Add a listener of GPS events.
     * 
     * @param listener
     *            The listener to add.
     */
    public final void addGPSListener(final GPSListener listener) {
        m.gpsModel().addListener(listener);
    }

    /**
     * Remove a listener of GPS events.
     * 
     * @param listener
     *            The listener to remove.
     */
    public final void removeGPSListener(final GPSListener listener) {
        m.gpsModel().removeListener(listener);
    }

    /**
     * @return the lastError
     */
    public final int getLastError() {
        return lastError;
    }

    /**
     * @return the lastErrorInfo
     */
    public final String getLastErrorInfo() {
        return lastErrorInfo;
    }

}
