package bt747.model;

import gps.BT747Constants;
import gps.GPSListener;
import gps.GPSstate;
import gps.convert.Conv;
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
import moio.util.HashSet;
import moio.util.Iterator;

import bt747.Txt;
import bt747.ui.MessageBox;

/**
 * @author Mario De Weerd
 * 
 */
public class Controller {

    private static final int SECONDS_PER_HOUR = 3600;
    /**
     * Reference to the model
     */
    private Model m;

    /**
     * @param model
     *            The model to associate with this controller.
     */
    public Controller(final Model model) {
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
        if (port != 0x5555) {
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

    public final void setLogFilePath(final String s) {
        m.setLogFile(s);
        m.saveSettings();
    }

    public final void setOutputFileBasePath(final String s) {
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

    /** Convert the log given the provided parameters using other methods.
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
    public final void convertLog(final int logType) {
        String ext = "";
        GPSFile gpsFile = null;
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
                    .setLogType(m.getGPSType() == GPS_TYPE_GISTEQ2 ? 0 : 1);
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
        lc.setNoGeoid(m.getNoGeoid());

        switch (logType) {
        default:
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
        }

        if (gpsFile != null) {
            m.logConversionStarted(logType);

            gpsFile.setAddLogConditionInfo(m.getOutputLogConditions());
            gpsFile.setImperial(m.getImperial());
            gpsFile.setRecordNbrInLogs(m.getRecordNbrInLogs());
            gpsFile.setBadTrackColor(m.getColorInvalidTrack());
            for (int i = 0; i < usedFilters.length; i++) {
                usedFilters[i].setStartDate(Conv.dateToUTCepoch1970(m
                        .getStartDate()));
                usedFilters[i].setEndDate(Conv.dateToUTCepoch1970(m
                        .getEndDate())
                        + (24 * 60 * 60 - 1));
            }
            gpsFile.setFilters(usedFilters);
            gpsFile.initialiseFile(m.getReportFileBasePath(), ext, m.getCard(),
                    m.getFileSeparationFreq());
            gpsFile.setTrackSepTime(m.getTrkSep() * 60);
            int error;
            error = lc.toGPSFile(m.getLogFilePath(), gpsFile, m.getCard());
            reportError(error, lc.getErrorInfo());
        } else {
            // TODO report error
        }
        m.logConversionEnded(logType);
    }

    public final GPSRecord[] getTrackPoints() {
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
                    .setLogType(m.getGPSType() == GPS_TYPE_GISTEQ2 ? 0 : 1);
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
        lc.setTimeOffset(m.getTimeOffsetHours() * 3600);
        lc.setNoGeoid(m.getNoGeoid());

        gpsFile = new GPSArray();

        // m.logConversionStarted(log_type);

        // gpsFile.setAddLogConditionInfo(m.getOutputLogConditions());
        // gpsFile.setImperial(m.getImperial());
        // gpsFile.setRecordNbrInLogs(m.getRecordNbrInLogs());
        // gpsFile.setBadTrackColor(m.getColorInvalidTrack());

        for (int i = 0; i < usedFilters.length; i++) {
            usedFilters[i].setStartDate(Conv.dateToUTCepoch1970(m
                    .getStartDate()));
            usedFilters[i].setEndDate(Conv.dateToUTCepoch1970(m.getEndDate())
                    + (24 * 60 * 60 - 1));
        }
        gpsFile.setFilters(usedFilters);
        // gpsFile.initialiseFile(m.getReportFileBasePath(), ext, m.getCard(),
        // m.getFileSeparationFreq());
        // gpsFile.setTrackSepTime(m.getTrkSep() * 60);
        int error;
        error = lc.toGPSFile(m.getLogFilePath(), gpsFile, m.getCard());
        reportError(error, lc.getErrorInfo());
        // m.logConversionEnded(log_type);
        return gpsFile.getGpsTrackPoints();
    }

    private void reportError(final int error, final String errorInfo) {
        String errorMsg;
        switch (error) {
        case BT747Constants.ERROR_COULD_NOT_OPEN:
            errorMsg = Txt.COULD_NOT_OPEN + errorInfo;
            bt747.sys.Vm.debug(errorMsg);
            new MessageBox(Txt.ERROR, errorMsg).popupBlockingModal();
            break;
        case BT747Constants.ERROR_NO_FILES_WERE_CREATED:
            (new MessageBox(Txt.WARNING, Txt.NO_FILES_WERE_CREATED))
                    .popupBlockingModal();
            break;
        case BT747Constants.ERROR_READING_FILE:
            new MessageBox(Txt.ERROR, Txt.PROBLEM_READING + errorInfo)
                    .popupBlockingModal();
            break;
        default:
            break;
        }
    }

    public final void setIncremental(final boolean b) {
        m.setIncremental(b);
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
        case GPS_TYPE_GISTEQ1:
        case GPS_TYPE_GISTEQ2:
        case GPS_TYPE_GISTEQ3:
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

    public final void startDPL700Download() {
        m.gpsModel().getDPL700Log(m.getLogFilePath(), m.getCard());
    }

    public final void setGPSType(final int i) {
        m.setGPSType(i);
    }

    public static final int GPS_TYPE_DEFAULT = 0;
    public static final int GPS_TYPE_GISTEQ1 = 1;
    public static final int GPS_TYPE_GISTEQ2 = 2;
    public static final int GPS_TYPE_GISTEQ3 = 3;

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
     * Set log overwrite mode on the device
     * 
     * @param b
     *            true - overwrite data in device when full false - stop logging
     *            when device is full
     */
    public final void setLogOverwrite(final boolean b) {
        m.gpsModel().setLogOverwrite(b);
        m.gpsModel().reqLogOverwrite();
    };

    public final void reqLogVersion() {
        m.gpsModel().reqLoggerVersion();
    }

    public final void reqLogMemUsed() {
        m.gpsModel().reqLogMemUsed();
    }

    public final void reqLogMemPoints() {
        m.gpsModel().reqLogMemPoints();
    }

    public final void reqLogOverwrite() {
        m.gpsModel().reqLogOverwrite();
    }

    public final void reqDeviceInfo() {
        m.gpsModel().reqDeviceInfo();
    }

    public void reqLogStatus() {
        m.gpsModel().reqLogStatus();
    }

    public void reqLogFormat() {
        m.gpsModel().reqLogFormat();
    }

    public void setLogFormat(int i) {
        m.gpsModel().setLogFormat(i);
    }

    private void eraseLog() {
        // TODO: Handle erase popup.
        m.gpsModel().eraseLog();
    }

    /** Options for the first warning message */
    private static final String[] C_ERASE_OR_CANCEL = { Txt.ERASE, Txt.CANCEL };
    /** Options for the first warning message */
    private static final String[] C_YES_OR_CANCEL = { Txt.YES, Txt.CANCEL };
    /** Options for the second warning message - reverse order on purpose */
    private static final String[] C_CANCEL_OR_CONFIRM_ERASE = { Txt.CANCEL,
            Txt.CONFIRM_ERASE };

    /**
     * (User) request to change the log format. Warns about requirement to erase
     * the log too.
     */
    public void changeLogFormatAndErase(final int logFormat) {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(Txt.TITLE_ATTENTION,
                Txt.C_msgWarningFormatAndErase, C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(Txt.TITLE_ATTENTION,
                    Txt.C_msgWarningFormatAndErase2, C_CANCEL_OR_CONFIRM_ERASE);
            mb.popupBlockingModal();
            if (mb.getPressedButtonIndex() == 1) {
                // Set format and reset log
                setLogFormat(logFormat);
                eraseLog();
            }
        }
    }

    /**
     * (User) request to change the log format. The log is not erased and may be
     * incompatible with other applications
     */
    public final void changeLogFormat(final int logFormat) {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(true, Txt.TITLE_ATTENTION,
                Txt.C_msgWarningFormatIncompatibilityRisk, C_YES_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            setLogFormat(logFormat);
        }
    }

    /**
     * (User) request to change the log format. Warns about requirement to erase
     * the log too.
     */
    public final void eraseLogFormat() {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning,
                C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning2,
                    C_CANCEL_OR_CONFIRM_ERASE);
            mb.popupBlockingModal();
            if (mb.getPressedButtonIndex() == 1) {
                // Erase log
                eraseLog();
            }
        }
    }

    public final void forceErase() {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning,
                C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning2,
                    C_CANCEL_OR_CONFIRM_ERASE);
            mb.popupBlockingModal();
            if (mb.getPressedButtonIndex() == 1) {
                // Erase log
                m.gpsModel().recoveryEraseLog();
            }
        }
    }

    /***************************************************************************
     * SECTION FOR CONNECTION RELATED METHODS
     **************************************************************************/

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

    public final void setSpeed(final int speed) {
        m.gpsRxTx().setSpeed(speed);
    }

    public final void setFreeTextPort(final String s) {
        closeGPS();
        m.gpsRxTx().setFreeTextPortAndOpen(s);
        performOperationsAfterGPSConnect();
    }

    public final String getFreeTextPort() {
        return m.gpsRxTx().getFreeTextPort();
    }

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

    public final void setDebugConn(final boolean dbg) {
        m.gpsRxTx().setDebugConn(dbg, m.getBaseDirPath());
    }

    /***************************************************************************
     * END OF SECTION FOR CONNECTION RELATED METHODS
     **************************************************************************/

    public final void setDebug(final boolean b) {
        m.gpsModel().setDebug(b);
    }

    public final void addGPSListener(GPSListener l) {
        m.gpsModel().addListener(l);
    }

    public final void removeGPSListener(GPSListener l) {
        m.gpsModel().removeListener(l);
    }

    public static final int DECODER_ORG = 1;
    public static final int DECODER_THOMAS = 2;

    public final void setBinDecoder(final int decoder_idx) {
        m.setBinDecoder(decoder_idx);
    }

    public final void saveSettings() {
        m.saveSettings(); // Explicitly save settings
    }

    public final void setTrkPtRCR(final int i) {
        m.setTrkPtRCR(i);
        getLogFilterSettings();
    }

    public final void setWayPtRCR(final int i) {
        m.setWayPtRCR(i);
        getLogFilterSettings();
    }

    public final void setTrkPtValid(final int i) {
        m.setTrkPtValid(i);
        getLogFilterSettings();
    }

    public final void setWayPtValid(final int i) {
        m.setWayPtValid(i);
        getLogFilterSettings();
    }

    // The way logfilter are handle should be reviewed.
    private final void getSettings(GPSFilter[] logFilters) {
        logFilters[GPSFilter.C_TRKPT_IDX].setRcrMask(m.getTrkPtRCR());
        logFilters[GPSFilter.C_TRKPT_IDX].setValidMask(m.getTrkPtValid());
        logFilters[GPSFilter.C_WAYPT_IDX].setRcrMask(m.getWayPtRCR());
        logFilters[GPSFilter.C_WAYPT_IDX].setValidMask(m.getWayPtValid());
    };

    private final void getLogFilterSettings() {
        getSettings(m.getLogFilters());
        getSettings(m.getLogFiltersAdv());
    }

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

    public final void reqFlashUserOption() {
        m.gpsModel().reqFlashUserOption();
    }

    public final void reqHoluxName() {
        m.gpsModel().reqHoluxName();
    }

    public final void reqBTAddr() {
        m.gpsModel().reqBT_MAC_ADDR();
    }

    public final void setHoluxName(final String s) {
        m.gpsModel().setHoluxName(s);
        reqHoluxName();
    }

    public final void reqNMEAPeriods() {
        m.gpsModel().reqNMEAPeriods();
    }

    public final void setNMEAPeriods(final int periods[]) {
        m.gpsModel().setNMEAPeriods(periods);
        reqNMEAPeriods();
    }

    public final void setNMEADefaultPeriods() {
        m.gpsModel().setNMEADefaultPeriods();
    }

    public final void setSBASTestEnabled(final boolean set) {
        m.gpsModel().setSBASEnabled(set);
        reqSBASTestEnabled();
    }

    public final void reqSBASTestEnabled() {
        m.gpsModel().reqSBASTestEnabled();
    }

    public final void setSBASEnabled(final boolean set) {
        m.gpsModel().setSBASEnabled(set);
        reqSBASEnabled();
    }

    public final void reqSBASEnabled() {
        m.gpsModel().reqSBASEnabled();
    }

    public final void reqDatumMode() {
        m.gpsModel().reqDatumMode();
    }

    public final void setDatumMode(final int mode) {
        m.gpsModel().setDatumMode(mode);
        reqDatumMode();
    }

    public final void setDGPSMode(final int mode) {
        m.gpsModel().setDGPSMode(mode);
        reqDGPSMode();
    }

    public final void reqDGPSMode() {
        m.gpsModel().reqDGPSMode();
    }

    public final void setPowerSaveEnabled(final boolean set) {
        m.gpsModel().setPowerSaveEnabled(set);
        reqPowerSaveEnabled();
    }

    public final void reqPowerSaveEnabled() {
        m.gpsModel().reqPowerSaveEnabled();
    }

    public final void reqLogReasonStatus() {
        m.gpsModel().reqLogReasonStatus();
    }

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

    public final void setEndDate(final bt747.util.Date d) {
        m.setEndDate(d);
    }

    public final void setStartDate(final bt747.util.Date d) {
        m.setStartDate(d);
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
            filter.setMaxPDOP((int) (m.getFilterMaxPDOP() * 100));
            filter.setMaxHDOP((int) (m.getFilterMaxHDOP() * 100));
            filter.setMaxVDOP((int) (m.getFilterMaxVDOP() * 100));
            filter.setMinNSAT(m.getFilterMinNSAT());
        }
    }

    public final void setNMEAset(final int value) {
        m.setNMEAset(value);
    }

    public final void setValidMask(final int i, final int mask) {
        // TODO: not sure this is needed anymore
        m.getLogFilters()[i].setValidMask(mask);
    }

    public final void setRcrMask(final int i, final int rcrmask) {
        // TODO: not sure this is needed anymore
        m.getLogFilters()[i].setRcrMask(rcrmask);
    }

    // View handling.
    private HashSet views = new HashSet();

    /** add a listener to event thrown by this class. */
    public final void addView(final BT747View v) {
        views.add(v);
        v.setController(this);
        v.setModel(this.m);
    }

    // protected void postEvent(final int type) {
    // Iterator it = views.iterator();
    // while (it.hasNext()) {
    // BT747View l=(BT747View)it.next();
    // Event e=new Event(l, type, null);
    // l.newEvent(e);
    // }
    // }

    public final void setTimeOffsetHours(final int timeOffsetHours) {
        m.setTimeOffsetHours(timeOffsetHours);
    }

    public final void setOneFilePerDay(final int value) {
        m.setOneFilePerDay(value);
    }

    public final void setNoGeoid(final boolean value) {
        m.setNoGeoid(value);
    }

}
