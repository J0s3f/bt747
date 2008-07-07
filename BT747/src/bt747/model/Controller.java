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

    Model m;

    public Controller(Model m) {
        this.m = m;
        init();
        
    }
   

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
    public final void setImperial(boolean on) {
        m.setImperial(on);
        m.saveSettings();
    }

    public final void setOutputLogConditions(boolean on) {
        m.setOutputLogConditions(on);
        m.saveSettings();
    }

    public final void setBaseDirPath(String s) {
        m.setBaseDirPath(s);
        m.saveSettings();
    }

    public final void setLogFilePath(String s) {
        m.setLogFile(s);
        m.saveSettings();
    }

    public final void setOutputFileBasePath(String s) {
        m.setReportFileBase(s);
        m.saveSettings();
    }

    public final void setChunkSize(int i) {
        m.setChunkSize(i);
        m.saveSettings();
    }

    public final void setDownloadTimeOut(int i) {
        m.setDownloadTimeOut(i);
        m.gpsModel().setDownloadTimeOut(i);
        m.saveSettings();
    }

    public final void setCard(int i) {
        m.setCard(i);
        m.saveSettings();
    }

    public final void setLogRequestAhead(int i) {
        m.setLogRequestAhead(i);
        m.gpsModel().setLogRequestAhead(m.getLogRequestAhead());
        m.saveSettings();
    }

    public final void writeLog(final int log_type) {
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
            /// TODO: set SR Log type correctly.
            ((DPL700LogConvert)lc).setLogType(m.getGPSType()==GPS_TYPE_GISTEQ2?0:1);
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

        switch (log_type) {
        case Model.C_CSV_LOG:
            gpsFile = new GPSCSVFile();
            ext = ".csv";
            break;
        case Model.C_TRK_LOG:
            gpsFile = new GPSCompoGPSTrkFile();
            ext = ".TRK";
            break;
        case Model.C_KML_LOG:
            gpsFile = new GPSKMLFile();
            ext = ".kml";
            break;
        case Model.C_PLT_LOG:
            gpsFile = new GPSPLTFile();
            ext = ".plt";
            break;
        case Model.C_GPX_LOG:
            gpsFile = new GPSGPXFile();
            ext = ".gpx";
            // Force offset to 0 if selected in menu.
            if (m.getGpxUTC0()) {
                lc.setTimeOffset(0);
            }
            ((GPSGPXFile) gpsFile).setTrkSegSplitOnlyWhenSmall(m
                    .getGpxTrkSegWhenBig());
            break;
        case Model.C_NMEA_LOG:
            gpsFile = new GPSNMEAFile();
            ((GPSNMEAFile) gpsFile).setNMEAoutput(m.getNMEAset());
            ext = ".nmea";
            break;
        case Model.C_GMAP_LOG:
            gpsFile = new GPSGmapsHTMLEncodedFile();
            ((GPSGmapsHTMLEncodedFile) gpsFile).setGoogleKeyCode(m
                    .getGoogleMapKey());
            ext = ".html";
            break;
        }

        if (gpsFile != null) {
            m.logConversionStarted(log_type);

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
            lc.toGPSFile(m.getLogFilePath(), gpsFile, m.getCard());
        } else {
            // TODO report error
        }
        m.logConversionEnded(log_type);
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
            /// TODO: set SR Log type correctly.
            ((DPL700LogConvert)lc).setLogType(m.getGPSType()==GPS_TYPE_GISTEQ2?0:1);
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

        //m.logConversionStarted(log_type);

//        gpsFile.setAddLogConditionInfo(m.getOutputLogConditions());
//        gpsFile.setImperial(m.getImperial());
//        gpsFile.setRecordNbrInLogs(m.getRecordNbrInLogs());
//        gpsFile.setBadTrackColor(m.getColorInvalidTrack());

        for (int i = 0; i < usedFilters.length; i++) {
            usedFilters[i].setStartDate(Conv.dateToUTCepoch1970(m
                    .getStartDate()));
            usedFilters[i].setEndDate(Conv.dateToUTCepoch1970(m
                    .getEndDate())
                    + (24 * 60 * 60 - 1));
        }
        gpsFile.setFilters(usedFilters);
//        gpsFile.initialiseFile(m.getReportFileBasePath(), ext, m.getCard(),
//                    m.getFileSeparationFreq());
//            gpsFile.setTrackSepTime(m.getTrkSep() * 60);
        lc.toGPSFile(m.getLogFilePath(), gpsFile, m.getCard());
//        m.logConversionEnded(log_type);
        return gpsFile.getGpsTrackPoints();
    }

    
    public final void setIncremental(boolean b) {
        m.setIncremental(b);
    }

    /**
     * Log download cancel
     */
    public final void cancelGetLog() {
        m.gpsModel().cancelGetLog();
    }

    public final void startDownload() {
        switch(m.getGPSType()) {
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

    public final void startFullDownload() {
        try {
            m.gpsModel().getLogInit(0, /* StartPosition */
            m.gpsModel().logMemSize - 1, /* EndPosition */
            m.getChunkSize(), /* Size per request */
            m.getLogFilePath(), /* Log file name */
            m.getCard(), /* Card for file operations */
            m.isIncremental() /* Incremental download */);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public final void startDPL700Download() {
        m.gpsModel().getDPL700Log(m.getLogFilePath(), m.getCard());
    }
    
    public final void setGPSType(int i) {
        m.setGPSType(i);
    }
    public final static int GPS_TYPE_DEFAULT = 0; 
    public final static int GPS_TYPE_GISTEQ1 = 1; 
    public final static int GPS_TYPE_GISTEQ2 = 2; 
    public final static int GPS_TYPE_GISTEQ3 = 3; 

    /***************************************************************************
     * Device state
     **************************************************************************/

    public final void startLog() {
        m.gpsModel().startLog();
        m.gpsModel().reqLogOnOffStatus();
    }

    public final void stopLog() {
        m.gpsModel().stopLog();
        m.gpsModel().reqLogOnOffStatus();
    };

    public final void setLogOverwrite(boolean b) {
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
    private static final String[] C_EraseOrCancel = { Txt.ERASE, Txt.CANCEL };
    /** Options for the first warning message */
    private static final String[] C_YesrCancel = { Txt.YES, Txt.CANCEL };
    /** Options for the second warning message - reverse order on purpose */
    private static final String[] C_CancelConfirmErase = { Txt.CANCEL,
            Txt.CONFIRM_ERASE };

    /**
     * (User) request to change the log format. Warns about requirement to erase
     * the log too.
     */
    public void changeLogFormatAndErase(int logFormat) {
        /** Object to open multiple message boxes */
        MessageBox m_mb;
        m_mb = new MessageBox(Txt.TITLE_ATTENTION,
                Txt.C_msgWarningFormatAndErase, C_EraseOrCancel);
        m_mb.popupBlockingModal();
        if (m_mb.getPressedButtonIndex() == 0) {
            m_mb = new MessageBox(Txt.TITLE_ATTENTION,
                    Txt.C_msgWarningFormatAndErase2, C_CancelConfirmErase);
            m_mb.popupBlockingModal();
            if (m_mb.getPressedButtonIndex() == 1) {
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
    public void changeLogFormat(int logFormat) {
        /** Object to open multiple message boxes */
        MessageBox m_mb;
        m_mb = new MessageBox(true, Txt.TITLE_ATTENTION,
                Txt.C_msgWarningFormatIncompatibilityRisk, C_YesrCancel);
        m_mb.popupBlockingModal();
        if (m_mb.getPressedButtonIndex() == 0) {
            setLogFormat(logFormat);
        }
    }

    /**
     * (User) request to change the log format. Warns about requirement to erase
     * the log too.
     */
    public void eraseLogFormat() {
        /** Object to open multiple message boxes */
        MessageBox m_mb;
        m_mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning,
                C_EraseOrCancel);
        m_mb.popupBlockingModal();
        if (m_mb.getPressedButtonIndex() == 0) {
            m_mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning2,
                    C_CancelConfirmErase);
            m_mb.popupBlockingModal();
            if (m_mb.getPressedButtonIndex() == 1) {
                // Erase log
                eraseLog();
            }
        }
    }

    public void forceErase() {
        /** Object to open multiple message boxes */
        MessageBox m_mb;
        m_mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning,
                C_EraseOrCancel);
        m_mb.popupBlockingModal();
        if (m_mb.getPressedButtonIndex() == 0) {
            m_mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning2,
                    C_CancelConfirmErase);
            m_mb.popupBlockingModal();
            if (m_mb.getPressedButtonIndex() == 1) {
                // Erase log
                m.gpsModel().recoveryEraseLog();
            }
        }
    }

    /* ***********************************************************
     * SECTION FOR CONNECTION RELATED METHODS
     * ***********************************************************/ 

    public void connectGPS() {
        closeGPS();
        m.gpsRxTx().openPort();
        if (m.gpsRxTx().isConnected()) {
            performOperationsAfterGPSConnect();
        }
    }


    /**
     * Close the GPS connection
     */
    public void closeGPS() {
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
    public void setBluetooth() {
        closeGPS();
        m.gpsRxTx().setBluetoothAndOpen();
        performOperationsAfterGPSConnect();
    }

    /**
     * open a Usb connection Calls getStatus to request initial parameters from
     * the device. Set up the timer to regurarly poll the connection for data.
     */
    public void setUsb() {
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


    public void setDebugConn(final boolean dbg) {
        m.gpsRxTx().setDebugConn(dbg, m.getBaseDirPath());
    }
    
    /* ***********************************************************
     * END OF SECTION FOR CONNECTION RELATED METHODS
     * ***********************************************************/ 
    
    
    public final void setDebug(boolean b) {
        m.gpsModel().setDebug(b);
    }

    public void addGPSListener(GPSListener l) {
        m.gpsModel().addListener(l);
    }

    public void removeGPSListener(GPSListener l) {
        m.gpsModel().removeListener(l);
    }

    public static final int DECODER_ORG = 1;
    public static final int DECODER_THOMAS = 2;

    public final void setBinDecoder(final int decoder_idx) {
        m.setBinDecoder(decoder_idx);
    }

    public void saveSettings() {
        m.saveSettings(); // Explicitally save settings
    }

    public void setTrkPtRCR(int i) {
        m.setTrkPtRCR(i);
        getLogFilterSettings();
    }

    public void setWayPtRCR(int i) {
        m.setWayPtRCR(i);
        getLogFilterSettings();
    }

    public void setTrkPtValid(int i) {
        m.setTrkPtValid(i);
        getLogFilterSettings();
    }

    public void setWayPtValid(int i) {
        m.setWayPtValid(i);
        getLogFilterSettings();
    }

    // The way logfilter are handle should be reviewed.
    private void getSettings(GPSFilter[] logFilters) {
        logFilters[GPSFilter.C_TRKPT_IDX].setRcrMask(m.getTrkPtRCR());
        logFilters[GPSFilter.C_TRKPT_IDX].setValidMask(m.getTrkPtValid());
        logFilters[GPSFilter.C_WAYPT_IDX].setRcrMask(m.getWayPtRCR());
        logFilters[GPSFilter.C_WAYPT_IDX].setValidMask(m.getWayPtValid());
    };

    private void getLogFilterSettings() {
        getSettings(m.getLogFilters());
        getSettings(m.getLogFiltersAdv());
    }

    public void setFlashUserOption(final boolean lock, final int updateRate,
            final int baudRate, final int GLL_Period, final int RMC_Period,
            final int VTG_Period, final int GSA_Period, final int GSV_Period,
            final int GGA_Period, final int ZDA_Period, final int MCHN_Period) {
        m.gpsModel().setFlashUserOption(lock, updateRate, baudRate, GLL_Period,
                RMC_Period, VTG_Period, GSA_Period, GSV_Period, GGA_Period,
                ZDA_Period, MCHN_Period);
        reqFlashUserOption();
    }

    public void reqFlashUserOption() {
        m.gpsModel().reqFlashUserOption();
    }

    public void reqHoluxName() {
        m.gpsModel().reqHoluxName();
    }

    public void reqBTAddr() {
        m.gpsModel().reqBT_MAC_ADDR();
    }

    public void setHoluxName(String s) {
        m.gpsModel().setHoluxName(s);
        reqHoluxName();
    }

    public void reqNMEAPeriods() {
        m.gpsModel().reqNMEAPeriods();
    }

    public void setNMEAPeriods(final int periods[]) {
        m.gpsModel().setNMEAPeriods(periods);
        reqNMEAPeriods();
    }

    public void setNMEADefaultPeriods() {
        m.gpsModel().setNMEADefaultPeriods();
    }

    public void setSBASTestEnabled(final boolean set) {
        m.gpsModel().setSBASEnabled(set);
        reqSBASTestEnabled();
    }

    public void reqSBASTestEnabled() {
        m.gpsModel().reqSBASTestEnabled();
    }

    public void setSBASEnabled(final boolean set) {
        m.gpsModel().setSBASEnabled(set);
        reqSBASEnabled();
    }

    public void reqSBASEnabled() {
        m.gpsModel().reqSBASEnabled();
    }

    public void reqDatumMode() {
        m.gpsModel().reqDatumMode();
    }

    public void setDatumMode(final int mode) {
        m.gpsModel().setDatumMode(mode);
        reqDatumMode();
    }

    public void setDGPSMode(final int mode) {
        m.gpsModel().setDGPSMode(mode);
        reqDGPSMode();
    }

    public void reqDGPSMode() {
        m.gpsModel().reqDGPSMode();
    }

    public void setPowerSaveEnabled(final boolean set) {
        m.gpsModel().setPowerSaveEnabled(set);
        reqPowerSaveEnabled();
    }

    public void reqPowerSaveEnabled() {
        m.gpsModel().reqPowerSaveEnabled();
    }

    public void reqLogReasonStatus() {
        m.gpsModel().reqLogReasonStatus();
    }

    public void reqFixInterval() {
        m.gpsModel().reqFixInterval();
    }

    public void logImmediate(final int value) {
        m.gpsModel().logImmediate(value);
    }

    public void setFixInterval(final int value) {
        m.gpsModel().setFixInterval(value);
        reqFixInterval();
    }

    public void setLogTimeInterval(final int value) {
        m.gpsModel().setLogTimeInterval(value);
        // TODO : request time interval
    }

    public void setLogDistanceInterval(final int value) {
        m.gpsModel().setLogDistanceInterval(value);
        // TODO : request distance interval
    }

    public void setLogSpeedInterval(final int value) {
        m.gpsModel().setLogSpeedInterval(value);
        // TODO : request speed interval
    }

    public void doHotStart() {
        m.gpsModel().doHotStart();
    }

    public void doColdStart() {
        m.gpsModel().doColdStart();
    }

    public void doWarmStart() {
        m.gpsModel().doWarmStart();
    }

    public void doFullColdStart() {
        m.gpsModel().doFullColdStart();
    }

    public boolean isEnableStoreOK() {
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

    public void setStats(boolean b) {
        m.gpsModel().setStats(b);
    }

    public void setGpsDecode(boolean value) {
        m.setGpsDecode(value);
        m.gpsModel().setGpsDecode(value);
    }

    public void setForceHolux241(boolean b) {
        m.setForceHolux241(b);
    }

    public void setGpxTrkSegWhenBig(boolean b) {
        m.setGpxTrkSegWhenBig(b);
    }

    public void setGpxUTC0(boolean b) {
        m.setGpxUTC0(b);
    }

    // For PDA - move through the menus using the arrows.
    public void setTraversableFocus(boolean b) {
        m.setTraversableFocus(b);
    }

    public void setEndDate(bt747.util.Date d) {
        m.setEndDate(d);
    }

    public void setStartDate(bt747.util.Date d) {
        m.setStartDate(d);
    }

    public void setRecordNbrInLogs(boolean b) {
        m.setRecordNbrInLogs(b);
    }

    public void setTrkSep(int value) {
        m.setTrkSep(value);
    }

    public void setColorInvalidTrack(String s) {
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

    public void StoreSetting1() {
        m.setTimeConditionSetting1(m.getLogTimeInterval());
        m.setDistConditionSetting1(m.getLogDistanceInterval());
        m.setSpeedConditionSetting1(m.getLogSpeedInterval());
        m.setLogFormatConditionSetting1(m.getLogFormat());
        m.setFixSetting1(m.getLogFixPeriod());
        m.setSBASSetting1(m.isSBASEnabled());
        m.setDGPSSetting1(m.getDgpsMode());
        m.setTestSBASSetting1(m.isSBASTestEnabled());
        m.setLogOverwriteSetting1(m.isLogFullOverwrite());
        String NMEA = "";
        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            NMEA += (m.getNMEAPeriod(i));
        }
        m.setNMEASetting1(NMEA);
    }

    public void RestoreSetting1() {
        setLogTimeInterval(m.getTimeConditionSetting1());
        setLogDistanceInterval(m.getDistConditionSetting1());
        setLogSpeedInterval(m.getSpeedConditionSetting1());
        setLogFormat(m.getLogFormatSetting1());
        setFixInterval(m.getFixSetting1());
        setSBASEnabled(m.getSBASSetting1());
        setSBASTestEnabled(m.getTestSBASSetting1());
        setDGPSMode(m.getDPGSSetting1());
        setLogOverwrite(m.getLogOverwriteSetting1());

        String NMEA = m.getNMEASetting1();
        int[] Periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            Periods[i] = (int) (NMEA.charAt(i) - '0');
        }
        setNMEAPeriods(Periods);
    }

    public void reqSettingsForStorage() {
        reqLogReasonStatus();
        reqLogFormat();
        reqFixInterval();
        reqSBASEnabled();
        reqSBASTestEnabled();
        reqDGPSMode();
        reqLogOverwrite();
        reqNMEAPeriods();
    }

    public void setAdvFilterActive(boolean b) {
        m.setAdvFilterActive(b);
    };

    public void setFilterMinRecCount(int i) {
        m.setFilterMinRecCount(i);
    }

    public void setFilterMaxRecCount(int i) {
        m.setFilterMaxRecCount(i);
    }

    public void setFilterMinSpeed(float i) {
        m.setFilterMinSpeed(i);
    }

    public void setFilterMaxSpeed(float i) {
        m.setFilterMaxSpeed(i);
    }

    public void setFilterMinDist(float i) {
        m.setFilterMinDist(i);
    }

    public void setFilterMaxDist(float i) {
        m.setFilterMaxDist(i);
    }

    public void setFilterMaxPDOP(float i) {
        m.setFilterMaxPDOP(i);
    }

    public void setFilterMaxHDOP(float i) {
        m.setFilterMaxHDOP(i);
    }

    public void setFilterMaxVDOP(float i) {
        m.setFilterMaxVDOP(i);
    }

    public void setFilterMinNSAT(int i) {
        m.setFilterMinNSAT(i);
    }
    
    
    public void setFilters() {
        // TODO : Should schedule this after a while.
        for (int i = m.getLogFiltersAdv().length-1; i >=0; i--) {
            GPSFilterAdvanced filter = m.getLogFiltersAdv()[i];
            filter.setMinRecCount(m.getFilterMinRecCount());
            filter.setMaxRecCount(m.getFilterMaxRecCount());
            filter.setMinSpeed(m.getFilterMinSpeed());
            filter.setMaxSpeed(m.getFilterMaxSpeed());
            filter.setMinDist(m.getFilterMinDist());
            filter.setMaxDist(m.getFilterMaxDist());
            filter.setMaxPDOP((int)(m.getFilterMaxPDOP()*100));
            filter.setMaxHDOP((int)(m.getFilterMaxHDOP()*100));
            filter.setMaxVDOP((int)(m.getFilterMaxVDOP()*100));
            filter.setMinNSAT(m.getFilterMinNSAT());
        }
    }
    
    public void setNMEAset(int value) {
        m.setNMEAset(value);
    }

    
    public void setValidMask(int i, int mask) {
        // TODO: not sure this is needed anymore
        m.getLogFilters()[i].setValidMask(mask);
    }

    public void setRcrMask(int i, int rcrmask) {
        // TODO: not sure this is needed anymore
        m.getLogFilters()[i].setRcrMask(rcrmask);
    }

    
    // View handling.
    private HashSet views = new HashSet();

    /**add a listener to event thrown by this class*/
    public void addView(BT747View v){        
        views.add(v);
        v.setController(this);
        v.setModel(this.m);
    }

    protected void MessageBoxModal() {
        Iterator it = views.iterator();
        while (it.hasNext()) {
//            BT747View l=(BT747View)it.next();
//            l.newEvent(e);
        }
    }
    
    public void setTimeOffsetHours(int timeOffsetHours) {
        m.setTimeOffsetHours(timeOffsetHours);
    }
    
    public void setOneFilePerDay(int value) {
        m.setOneFilePerDay(value);
    }
    
    public void setNoGeoid(boolean value) {
        m.setNoGeoid(value);
    }

//    protected void postEvent(final int type) {
//        Iterator it = views.iterator();
//        while (it.hasNext()) {
//            BT747View l=(BT747View)it.next();
//            Event e=new Event(l, type, null);
//            l.newEvent(e);
//        }
//    }

}
