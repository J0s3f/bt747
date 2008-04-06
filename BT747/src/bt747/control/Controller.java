package bt747.control;

import bt747.ui.MessageBox;

import bt747.Txt;
import bt747.sys.Settings;

import gps.BT747_dev;
import gps.GPSListener;
import gps.convert.Conv;
import gps.log.BT747LogConvert;
import gps.log.CSVLogConvert;
import gps.log.GPSCSVFile;
import gps.log.GPSCompoGPSTrkFile;
import gps.log.GPSFile;
import gps.log.GPSFilter;
import gps.log.GPSGPXFile;
import gps.log.GPSGmapsHTMLEncodedFile;
import gps.log.GPSKMLFile;
import gps.log.GPSLogConvert;
import gps.log.GPSNMEAFile;
import gps.log.GPSPLTFile;
import gps.log.HoluxTrlLogConvert;
import gps.port.GPSPort;
import gps.port.GPSWabaPort;

import bt747.model.Model;
import bt747.sys.Convert;

/**
 * @author Mario De Weerd
 * 
 */
public class Controller {

    Model m;

    public Controller(Model m) {
        this.m = m;
        getLogFilterSettings();
    }

    /**
     * @param on
     *            True when Imperial units are to be used where possible
     */
    public final void setImperial(boolean on) {
        m.setImperial(on);
    }

    public final void setBaseDirPath(String s) {
        m.setBaseDirPath(s);
    }

    public final void setLogFilePath(String s) {
        m.setLogFile(s);
    }

    public final void setOutputFileBasePath(String s) {
        m.setReportFileBase(s);
    }

    public final void setChunkSize(int i) {
        m.setChunkSize(i);
    }

    public final void setDownloadTimeOut(int i) {
        m.setDownloadTimeOut(i);
    }

    public final void setCard(int i) {
        m.setCard(i);
    }

    public final void setLogRequestAhead(int i) {
        m.setLogRequestAhead(i);
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

    /**
     * Device connection
     */
    public final void setBluetooth() {
        m.gpsModel().GPS_close();
        m.gpsModel().setBluetooth();
        m.setFreeTextPort("");
    }

    public final void setUsb() {
        m.gpsModel().GPS_close();
        m.gpsModel().setUsb();
        m.setFreeTextPort("");
    }

    public final void setPort(final int port) {
        m.gpsModel().GPS_close();
        m.gpsModel().setPort(port);
        m.setFreeTextPort("");
        // TODO: review save settings | saving port currently for debug
        m.setPortnbr(port);
        m.saveSettings();
    }

    public final void setFreeTextPort(final String s) {
        m.gpsModel().GPS_close();
        m.gpsModel().setFreeTextPort(s);
        // TODO: review save settings | saving port currently for debug
        m.setFreeTextPort(s);
        m.saveSettings();
    }

    public final String getFreeTextPort() {
        return m.gpsModel().getFreeTextPort();
    }

    public final void setSpeed(final int speed) {
        m.gpsModel().setSpeed(speed);
    }

    public final void GPS_close() {
        m.gpsModel().GPS_close();
    }

    public final void GPS_restart() {
        m.gpsModel().GPS_restart();
    }

    public final void setDebug(boolean b) {
        m.gpsModel().setDebug(b);
    }

    public final void setDebugConn(boolean b) {
        m.gpsModel().setDebugConn(b);
    }

    public void addGPSListener(GPSListener l) {
        m.gpsModel().addListener(l);
    }

    public static final int DECODER_ORG = 1;
    public static final int DECODER_THOMAS = 2;

    public void setBinDecoder(int decoder_idx) {
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
    
    public boolean isDataOK(int i) {
        // TODO: This function serves to enable 'save settings'.
        //        should do this through an event to the view.
        return m.gpsModel().isDataOK(i);
    }
}
