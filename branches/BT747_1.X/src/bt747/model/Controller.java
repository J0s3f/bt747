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
package bt747.model;

import gps.BT747Constants;
import gps.GPSstate;
import gps.GpsEvent;
import gps.log.GPSFilter;
import gps.log.GPSFilterAdvanced;
import gps.log.GPSRecord;
import gps.log.LogFileInfo;
import gps.log.TracksAndWayPoints;
import gps.log.in.BT747LogConvert;
import gps.log.in.DPL700LogConvert;
import gps.log.in.GPSInputConversionFactory;
import gps.log.in.GPSLogConvertInterface;
import gps.log.in.MultiLogConvert;
import gps.log.out.CommonOut;
import gps.log.out.GPSArray;
import gps.log.out.GPSCSVFile;
import gps.log.out.GPSCompoGPSTrkFile;
import gps.log.out.GPSConversionParameters;
import gps.log.out.GPSFile;
import gps.log.out.GPSGPXFile;
import gps.log.out.GPSGmapsHTMLEncodedFile;
import gps.log.out.GPSKMLFile;
import gps.log.out.GPSNMEAFile;
import gps.log.out.GPSPLTFile;
import gps.log.out.WayPointStyle;
import gps.log.out.WayPointStyleSet;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747FileName;
import bt747.sys.interfaces.BT747Vector;

/**
 * @author Mario De Weerd
 * 
 */
public class Controller implements ModelListener {
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
     *                The model to associate with this controller.
     */
    public Controller(final Model model) {
        setModel(model);
    }

    /**
     * Currently needed because class is extended.
     */
    public Controller() {
    }

    public void setModel(final Model model) {
        if(m!=null) {
            m.removeListener(this);
        }
        m = model;
        m.addListener(this);
    }

    public Model getModel() {
        return m;
    }
    
    /* (non-Javadoc)
     * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
     */
    public void modelEvent(final ModelEvent e) {
        if(e.getType() == ModelEvent.UPDATE_LOG_LOG_STATUS) {
            if(m.gpsModel().isLoggingDisabled()) {
                setAutoLog(true);
            }
        }
        // TODO Auto-generated method stub
    }

    public final void setAutoLog(final boolean enable) {
        m.gpsModel().setAutoLog(enable);
    }
    
    /**
     * Called when the Controller starts. Used for initialization.
     */
    public void init() {
        m.gpsModel().setGpsDecode(m.getBooleanOpt(AppSettings.DECODEGPS));
        m.gpsModel().setDownloadTimeOut(m.getDownloadTimeOut());
        m.gpsModel().setLogRequestAhead(m.getIntOpt(AppSettings.LOGAHEAD));

        final int port = m.getIntOpt(AppSettings.PORTNBR);
        if (port != Controller.NOT_A_PORT_NUMBER) {
            // TODO: review this, especially with 'freetext port'
            m.gpsRxTx().setDefaults(port, m.getIntOpt(AppSettings.BAUDRATE));
        }
        if (m.getBooleanOpt(AppSettings.OPENPORTATSTARTUP)) {
            connectGPS();
        }
    }

    /**
     * Set the output file path (including basename, no extension) relative to
     * the BaseDirPath.
     * 
     * @param s
     *                The relative log file path (including basename)
     */
    public final void setOutputFileRelPath(final String s) {
        setStringOpt(AppSettings.REPORTFILEBASE, s);
    }

    /**
     * @param chunkSize
     *                The amount of data that is requested from the device in
     *                a single command when downloading the data.
     */

    public final void setChunkSize(final int chunkSize) {
        m.setChunkSize(chunkSize);
    }

    /**
     * @param timeout
     *                The timeout in ms after which the lack of reply from the
     *                device will be considered as a communication failure.
     *                Depending on the operation, a recovery will be
     *                attempted.
     */
    public final void setDownloadTimeOut(final int timeout) {
        m.setDownloadTimeOut(timeout);
        m.gpsModel().setDownloadTimeOut(timeout);
    }

    /**
     * @param card
     *                The card number that the filepaths refer to. This is
     *                usefull on PDA's only and probably only on a Palm. This
     *                is used by the 'SuperWaba' environment if used. '-1'
     *                refers to the 'last card in the system' which is
     *                normally the card that can be inserted by the PDA user.
     */
    public final void setCard(final int card) {
        m.setCard(card);
    }

    /**
     * @param numberOfRequestsAhead
     *                Especially when downloading using Bluetooth, downloading
     *                is slow when no request pipeline is implemented. This
     *                number defines how many 'chunk download' request will be
     *                sent to device while the first reply is still pending.
     */
    public final void setLogRequestAhead(final int numberOfRequestsAhead) {
        setIntOpt(AppSettings.LOGAHEAD, numberOfRequestsAhead);
        m.gpsModel().setLogRequestAhead(m.getIntOpt(AppSettings.LOGAHEAD));
    }

    /**
     * Points to the object that is currently converting (to be able to stop
     * it).
     */
    private GPSLogConvertInterface currentGPSLogConvert;

    /**
     * Stop the conversion that is ongoing.
     */
    public final void stopLogConvert() {
        if (currentGPSLogConvert != null) {
            currentGPSLogConvert.stopConversion();
        }
    }

    private BT747FileName filenameBuilder = null;

    public final void setFileNameBuilder(final BT747FileName builder) {
        filenameBuilder = builder;
    }

    public final void setWayPointStyles(final WayPointStyleSet set) {
        CommonOut.setWayPointStyles(set);
    }

    public final void addWayPointStyle(final WayPointStyle style) {
        CommonOut.getWayPointStyles().add(style);
    }

    private GPSFile getOutFileHandler(final int logType) {
        GPSFile gpsFile = null;
        switch (logType) {
        case Model.CSV_LOGTYPE:
            gpsFile = new GPSCSVFile();
            break;
        case Model.TRK_LOGTYPE:
            gpsFile = new GPSCompoGPSTrkFile();
            break;
        case Model.KML_LOGTYPE:
            gpsFile = new GPSKMLFile();
            break;
        case Model.PLT_LOGTYPE:
            gpsFile = new GPSPLTFile();
            break;
        case Model.GPX_LOGTYPE:
        case Model.OSM_LOGTYPE:
            gpsFile = new GPSGPXFile();
            break;
        case Model.NMEA_LOGTYPE:
            gpsFile = new GPSNMEAFile();
            break;
        case Model.GMAP_LOGTYPE:
            gpsFile = new GPSGmapsHTMLEncodedFile();
            break;
        case Model.ARRAY_LOGTYPE:
            gpsFile = new GPSArray();
            break;
        default:
            lastError = BT747Constants.ERROR_UNKNOWN_OUTPUT_FORMAT;
            lastErrorInfo = "" + logType;
        }
        return gpsFile;
    }

    private void configureGpsFile(final GPSFile gpsFile) {
        if (gpsFile != null) {
            if (!((gpsFile.getClass() == GPSGPXFile.class) && m
                    .getBooleanOpt(AppSettings.GPXUTC0))) {
                gpsFile.setTimeOffset(m
                        .getIntOpt(AppSettings.GPSTIMEOFFSETHOURS)
                        * Controller.SECONDS_PER_HOUR);
            }
            gpsFile.setWayPointTimeCorrection(-m
                    .getIntOpt(AppSettings.FILETIMEOFFSET));
            gpsFile
                    .setMaxDiff(m
                            .getIntOpt(AppSettings.TAG_MAXTIMEDIFFERENCE));
            gpsFile.setOverridePreviousTag(m
                    .getBooleanOpt(AppSettings.TAG_OVERRIDEPOSITIONS));
            gpsFile.setAddLogConditionInfo(m
                    .getBooleanOpt(AppSettings.OUTPUTLOGCONDITIONS));
            gpsFile.setImperial(m.getBooleanOpt(AppSettings.IMPERIAL));
            gpsFile.setRecordNbrInLogs(m
                    .getBooleanOpt(AppSettings.IS_RECORDNBR_IN_LOGS));
            gpsFile.setBadTrackColor(m
                    .getStringOpt(AppSettings.COLOR_INVALIDTRACK));
            gpsFile.setGoodTrackColor(m
                    .getStringOpt(AppSettings.COLOR_VALIDTRACK));
            gpsFile.setIncludeTrkComment(m
                    .getBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_COMMENT));
            gpsFile.setIncludeTrkName(m
                    .getBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_NAME));
            gpsFile.setFilters(getLogFiltersToUse());
            gpsFile.setOutputFields(GPSRecord.getLogFormatRecord(m
                    .getIntOpt(AppSettings.FILEFIELDFORMAT)));
            gpsFile.setTrackSepTime(m.getIntOpt(AppSettings.TRKSEP)
                    * Controller.SECONDS_PER_MINUTE);
            gpsFile.setUserWayPointList(userWayPoints);
            gpsFile.getParamObject().setBoolParam(
                    GPSConversionParameters.TRACK_SPLIT_IF_SMALL_BOOL,
                    m.getBooleanOpt(AppSettings.GPXTRKSEGBIG));
            gpsFile.getParamObject().setBoolParam(
                    GPSConversionParameters.GPX_LINK_TEXT,
                    m.getBooleanOpt(AppSettings.GPX_LINK_INFO));
            gpsFile.getParamObject().setBoolParam(
                    GPSConversionParameters.GPX_1_1,
                    m.getBooleanOpt(AppSettings.IS_GPX_1_1));
            gpsFile.getParamObject().setParam(
                    GPSConversionParameters.GOOGLEMAPKEY_STRING,
                    m.getStringOpt(AppSettings.GOOGLEMAPKEY));
            gpsFile.getParamObject().setIntParam(
                    GPSConversionParameters.NMEA_OUTFIELDS, m.getNMEAset());
            String altMode = null;
            switch (m.getIntOpt(AppSettings.KML_ALTITUDEMODE)) {
            case 0:
                altMode = GPSKMLFile.CLAMPED_HEIGHT;
                break;
            case 1:
                altMode = GPSKMLFile.RELATIVE_HEIGHT;
                break;
            case 2:
                altMode = GPSKMLFile.ABSOLUTE_HEIGHT;
                break;
            }
            if (altMode != null) {
                gpsFile.getParamObject().setParam(
                        GPSConversionParameters.KML_TRACK_ALTITUDE_STRING,
                        altMode);
            }

        }
    }

    private String getOutFileExt(final int logType) {
        String ext; // For debug
        switch (logType) {
        case Model.CSV_LOGTYPE:
            ext = ".csv";
            break;
        case Model.TRK_LOGTYPE:
            ext = ".TRK";
            break;
        case Model.KML_LOGTYPE:
            ext = ".kml";
            break;
        case Model.PLT_LOGTYPE:
            ext = ".plt";
            break;
        case Model.GPX_LOGTYPE:
        case Model.OSM_LOGTYPE:
            ext = ".gpx";
            break;
        case Model.NMEA_LOGTYPE:
            ext = ".nmea";
            break;
        case Model.GMAP_LOGTYPE:
            ext = ".html";
            break;
        default:
            ext = "";
        }
        return ext;
    }

    /**
     * Convert the log given the provided parameters using other methods.
     * 
     * @return 0 if success, otherwise an error was encountered (error number
     *         returned and related text in {@link #getLastErrorInfo()}
     * 
     * @param logType
     *                Indicates the type of log that should be written. For
     *                example Model.CSV_LOGTYPE .
     * @see Model#CSV_LOGTYPE
     * @see Model#TRK_LOGTYPE
     * @see Model#KML_LOGTYPE
     * @see Model#PLT_LOGTYPE
     * @see Model#GPX_LOGTYPE
     * @see Model#NMEA_LOGTYPE
     * @see Model#GMAP_LOGTYPE
     */
    public final int doConvertLog(final int logType) {
        return doConvertLog(logType, getOutFileHandler(logType),
                getOutFileExt(logType));
    }

    public int getHeightReference(final int logType) {
        return BT747Constants.getHeightReference(logType);
    }

    public GPSLogConvertInterface getInputConversionInstance(final int logType) {
        final GPSLogConvertInterface lc;
        if (Controller.logFiles.size() != 0) {
            final MultiLogConvert mlc = new MultiLogConvert();
            mlc.setLogFiles(Controller.logFiles);
            lc = mlc;
        } else {
            lc = getInputConversionInstance();
        }
        final int destinationHeightReference = getHeightReference(logType);
        final int sourceHeightReference = getHeightReference(lc.getType());
        String parameters = "";

        switch (m.getHeightConversionMode()) {
        case AppSettings.HEIGHT_AUTOMATIC:
            if ((sourceHeightReference == BT747Constants.HEIGHT_MSL)
                    && (destinationHeightReference == BT747Constants.HEIGHT_WGS84)) {
                /* Need to add the height in automatic mode */
                lc.setConvertWGS84ToMSL(+1);
            } else if ((sourceHeightReference == BT747Constants.HEIGHT_WGS84)
                    && (destinationHeightReference == BT747Constants.HEIGHT_MSL)) {
                /* Need to substract the height in automatic mode */
                lc.setConvertWGS84ToMSL(-1);
            } else {
                /* Do nothing */
                lc.setConvertWGS84ToMSL(0);
            }
            break;
        case AppSettings.HEIGHT_WGS84_TO_MSL:
            lc.setConvertWGS84ToMSL(-1);
            break;
        case AppSettings.HEIGHT_NOCHANGE:
            lc.setConvertWGS84ToMSL(0);
            break;
        case AppSettings.HEIGHT_MSL_TO_WGS84:
            lc.setConvertWGS84ToMSL(1);
            break;
        }

        if (lc instanceof BT747LogConvert) {
            final BT747LogConvert b = (BT747LogConvert) lc;
            b.setHolux(m.getBooleanOpt(AppSettings.FORCE_HOLUXM241));
            parameters += "Force Holux:"
                    + m.getBooleanOpt(AppSettings.FORCE_HOLUXM241) + "\n";
        } else if (lc instanceof DPL700LogConvert) {
            final DPL700LogConvert b = (DPL700LogConvert) lc;
            // / TODO: set SR Log type correctly.
            b
                    .setLogType(m.getIntOpt(AppSettings.GPSTYPE) == AppSettings.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR ? 0
                            : 1);
        }
        if (Generic.isDebug()) {
            Generic.debug(parameters);
        }

        return lc;
    }

    public final GPSLogConvertInterface getInputConversionInstance() {
        return GPSInputConversionFactory.getHandler()
                .getInputConversionInstance(
                        m.getStringOpt(AppSettings.LOGFILEPATH));
    }

    public final GPSFilter[] getLogFiltersToUse() {
        GPSFilter[] usedFilters;
        String parameters = "";

        parameters += "From:"
                + gps.log.out.CommonOut
                        .getDateTimeStr(m.getFilterStartTime()) + "("
                + m.getFilterStartTime() + ")\n";
        parameters += "To:"
                + gps.log.out.CommonOut.getDateTimeStr(m.getFilterEndTime())
                + "(" + m.getFilterEndTime() + ")\n";

        if (m.getBooleanOpt(AppSettings.ADVFILTACTIVE)) {
            usedFilters = m.getLogFiltersAdv();
            parameters += "Advanced filter:\n";

        } else {
            parameters += "Standard filter:\n";
            usedFilters = m.getLogFilters();
        }

        for (int i = 0; i < usedFilters.length; i++) {
            usedFilters[i].setStartTime(m.getFilterStartTime());
            usedFilters[i].setEndTime(m.getFilterEndTime());
        }
        parameters += usedFilters[0].toString() + usedFilters[1].toString();
        if (Generic.isDebug()) {
            Generic.debug(parameters);
        }

        return usedFilters;
    }

    // TODO : Move to a model
    private GPSRecord[] userWayPoints = null;

    public final void setUserWayPoints(final GPSRecord[] rcrds) {
        userWayPoints = rcrds;
    }

    public final GPSRecord[] getUserWayPoints() {
        return userWayPoints;
    }

    public final int doConvertLog(final int logType, final GPSFile gpsFile,
            final String ext) {
        int result;
        final String parameters = ""; // For debug
        GPSLogConvertInterface lc;
        result = 0;
        configureGpsFile(gpsFile);
        
        if(logType == Model.OSM_LOGTYPE) {
            // For OSM output, we optimize the output file.
            gpsFile.setIncludeTrkComment(false);
            gpsFile.setIncludeTrkName(false);
            gpsFile
                    .setOutputFields(GPSRecord
                            .getLogFormatRecord((1 << BT747Constants.FMT_LONGITUDE_IDX)
                                    | (1 << BT747Constants.FMT_LATITUDE_IDX)
                                    | (1 << BT747Constants.FMT_UTC_IDX)
                                    | (1 << BT747Constants.FMT_MILLISECOND_IDX)
                                    | (1 << BT747Constants.FMT_HEIGHT_IDX)));

        }

        if (Generic.isDebug()) {
            Generic.debug("Converting with parameters:\n");
        }

        lc = getInputConversionInstance(logType);

        if (gpsFile != null) {
            if (filenameBuilder != null) {
                gpsFile.setFilenameBuilder(filenameBuilder);
            }

            currentGPSLogConvert = lc;
            if (Generic.isDebug()) {
                Generic.debug(parameters);
            }
            gpsFile.initialiseFile(m.getReportFileBasePath(), ext, m
                    .getCard(), m.getOutputFileSplitType());
            m.logConversionStarted(logType);
            try {
                lastError = lc.toGPSFile(m
                        .getStringOpt(AppSettings.LOGFILEPATH), gpsFile, m
                        .getCard());
            } catch (final Throwable e) {
                Generic.debug("During conversion", e);
            }
            currentGPSLogConvert = null;
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
     * Vector of LogFileInfo.
     */
    public final static BT747Vector logFiles = JavaLibBridge.getVectorInstance();

    /**
     * Convert the log into an array of trackpoints.
     * 
     * @return Array of selected trackpoints.
     */
    public final GPSRecord[] doConvertLogToTrackPoints() {
        return doConvertLogToTracksAndWayPoints().getTrackPoints();
    }

    public final TracksAndWayPoints doConvertLogToTracksAndWayPoints() {
        int error = 0;
        gps.log.TracksAndWayPoints result;
        GPSArray gpsFile = null;
        GPSLogConvertInterface lc;

        lc = getInputConversionInstance(Model.GMAP_LOGTYPE); // For height
        // conversion.

        gpsFile = (GPSArray) getOutFileHandler(Model.ARRAY_LOGTYPE);
        configureGpsFile(gpsFile);

        gpsFile.initialiseFile("", "", -1, m.getOutputFileSplitType());
        m.logConversionStarted(Model.ARRAY_LOGTYPE);
        // gpsFile.setTrackSepTime(m.getTrkSep() * 60);
        currentGPSLogConvert = lc;
        try {
            error = lc.toGPSFile(m.getStringOpt(AppSettings.LOGFILEPATH),
                    gpsFile, m.getCard());
        } catch (final Throwable e) {
            Generic.debug("During conversion", e);
        }
        m.logConversionEnded(Model.ARRAY_LOGTYPE);
        currentGPSLogConvert = null;

        if (error != 0) {
            lastError = error;
            lastErrorInfo = lc.getErrorInfo();
            result = null;
        } else {
            result = gpsFile.getResult();
        }
        return result;
    }

    /**
     * Set the 'incremental download' configuration.
     * 
     * @deprecated Use {@link #setDownloadMethod(int)} instead
     * @param incrementalDownload
     *                true if the log download should be incremental.
     */
    public final void setIncremental(final boolean incrementalDownload) {
        m.setIncremental(incrementalDownload);
    }

    /**
     * Set the download method. <br>
     * Possible values:<br> - {@link #DOWNLOAD_FILLED}<br> -{@link #DOWNLOAD_FULL}<br> -
     * {@link #DOWNLOAD_SMART}
     */
    public final void setDownloadMethod(final int downloadMethod) {
        m.setDownloadMethod(downloadMethod);
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
        switch (m.getIntOpt(AppSettings.GPSTYPE)) {
        default:
        case AppSettings.GPS_TYPE_DEFAULT:
            startDefaultDownload();
            break;
        case AppSettings.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
        case AppSettings.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
        case AppSettings.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
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
            int endAddress;
            if ((m.getDownloadMethod() == Model.DOWNLOAD_FULL)
                    || m.gpsModel().isInitialLogOverwrite()) {
                endAddress = m.logMemSize() - 1;
            } else {
                endAddress = m.logMemUsed() - 1;
            }
            m.gpsModel().getLogInit(0, /* StartPosition */
            endAddress, /* EndPosition */
            m.getChunkSize(), /* Size per request */
            m.getStringOpt(AppSettings.LOGFILEPATH), /* Log file name */
            m.getCard(), /* Card for file operations */
            /** Incremental download */
            m.getDownloadMethod() == Model.DOWNLOAD_SMART,
            m.getBooleanOpt(AppSettings.DISABLELOGDURINGDOWNLOAD));
        } catch (final Exception e) {
            Generic.debug("StartDefaultDownload", e);
            // TODO: handle exception
        }
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
        m.gpsModel().replyToOkToOverwrite(isOkToOverwrite);
    }

    /**
     * The log is being erased - the user request to abandon waiting for the
     * end of this operation.
     */
    public final void stopErase() {
        m.gpsModel().stopErase();
    }

    /**
     * Initiate the download of a 'DPL700' log.
     */
    public final void startDPL700Download() {
        m.gpsModel().getDPL700Log(m.getStringOpt(AppSettings.LOGFILEPATH),
                m.getCard());
    }

    /**
     * Set logging status of device.
     * 
     * @param on
     *                When true, logging will be turned on.
     */
    public final void setLoggingActive(final boolean on) {
        if (on) {
            m.gpsModel().startLog();
        } else {
            m.gpsModel().stopLog();
        }
        m.gpsModel().reqLogOnOffStatus();
    }

    /**
     * Set log overwrite mode on the device.
     * 
     * @param isOverWriteLog
     *                true - overwrite data in device when full false - stop
     *                logging when device is full
     */
    public final void setLogOverwrite(final boolean isOverWriteLog) {
        m.gpsModel().setLogOverwrite(isOverWriteLog);
        m.gpsModel().reqLogOverwrite();
    };

    /**
     * Request the Logger Version data from the device.<br>
     * Once retrieved an event {@link gps.GpsEvent#UPDATE_LOG_VERSION} will be
     * generated so that {@link Model#getMtkLogVersion()} can be used to
     * retrieve the actual value.
     * 
     */
    public final void reqMtkLogVersion() {
        m.gpsModel().setDataNeeded(GPSstate.DATA_LOG_VERSION);
    }

    /**
     * Request the amount of memory in use from the device.
     */
    public final void reqLogMemUsed() {
        m.gpsModel().setDataNeeded(GPSstate.DATA_MEM_USED);
    }

    public final void reqInitialLogMode() {
        m.gpsModel().setDataNeeded(GPSstate.DATA_INITIAL_LOG);
    }

    /**
     * Request the number of points logged in memory.
     */
    public final void reqLogMemPtsLogged() {
        m.gpsModel().setDataNeeded(GPSstate.DATA_MEM_PTS_LOGGED);
    }

    /**
     * Request the log overwrite status from the device.<br>
     * {@link Model#isLogFullOverwrite()} must be used on a {link
     * {@link gps.GpsEvent#UPDATE_LOG_REC_METHOD} event to get the data.
     */
    public final void reqLogOverwrite() {
        m.gpsModel().reqLogOverwrite();
    }

    /**
     * Request a set of device information from the GPS device that can be
     * after specific {@link GpsEvent} events.
     */
    public final void reqDeviceInfo() {
        m.gpsModel().reqDeviceInfo();
    }

    /**
     * Request the status register from the device.<br>
     * After the associated {@link gps.GpsEvent#UPDATE_LOG_LOG_STATUS} event
     * one can retrieve the data using:<br> - {@link Model#isLoggingActive()}
     * <br> - {@link GPSstate#loggerIsFull} (not currently public)<br> -
     * {@link GPSstate#loggerNeedsInit} (not currently public)<br> -
     * {@link GPSstate#loggerIsDisabled} (not currently public)<br>
     */
    public final void reqLogStatus() {
        m.gpsModel().setDataNeeded(GPSstate.DATA_LOG_STATUS);
    }

    /**
     * Request the current log format from the device.<br>
     * After the associated {@link gps.GpsEvent#UPDATE_LOG_FORMAT} event one
     * can retrieve the data using:<br> - {@link Model#getLogFormat()} <br>
     */
    public final void reqLogFormat() {
        m.gpsModel().setDataNeeded(GPSstate.DATA_LOG_FORMAT);
    }

    /**
     * Sets a new log format on the device.<br>
     * 
     * @param newLogFormat
     *                <br>
     *                The bits in the newLogFormat can be defined using a
     *                bitwise OR of expressions like<br>
     *                (1<< IDX) <br>
     *                where IDX is one of the following:<br> -
     *                {@link BT747Constants#FMT_UTC_IDX} <br> -
     *                {@link BT747Constants#FMT_VALID_IDX} <br> -
     *                {@link BT747Constants#FMT_LATITUDE_IDX} <br> -
     *                {@link BT747Constants#FMT_LONGITUDE_IDX} <br> -
     *                {@link BT747Constants#FMT_HEIGHT_IDX} <br> -
     *                {@link BT747Constants#FMT_SPEED_IDX} <br> -
     *                {@link BT747Constants#FMT_HEADING_IDX} <br> -
     *                {@link BT747Constants#FMT_DSTA_IDX} <br> -
     *                {@link BT747Constants#FMT_DAGE_IDX} <br> -
     *                {@link BT747Constants#FMT_PDOP_IDX} <br> -
     *                {@link BT747Constants#FMT_HDOP_IDX} <br> -
     *                {@link BT747Constants#FMT_VDOP_IDX} <br> -
     *                {@link BT747Constants#FMT_NSAT_IDX} <br> -
     *                {@link BT747Constants#FMT_SID_IDX} <br> -
     *                {@link BT747Constants#FMT_ELEVATION_IDX} <br> -
     *                {@link BT747Constants#FMT_AZIMUTH_IDX} <br> -
     *                {@link BT747Constants#FMT_SNR_IDX} <br> -
     *                {@link BT747Constants#FMT_RCR_IDX} <br> -
     *                {@link BT747Constants#FMT_MILLISECOND_IDX} <br> -
     *                {@link BT747Constants#FMT_DISTANCE_IDX} <br> -
     *                {@link BT747Constants#FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX}
     *                <br>
     */
    public final void setLogFormat(final int newLogFormat) {
        m.gpsModel().setLogFormat(newLogFormat);
        reqLogFormat();
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

    /*************************************************************************
     * SECTION FOR CONNECTION RELATED METHODS.
     ************************************************************************/

    /**
     * Connect to the GPS (open the serial connection).
     */
    public final void connectGPS() {
        closeGPS();
        if (m.getStringOpt(AppSettings.FREETEXTPORT).length() != 0) {
            openFreeTextPort(m.getStringOpt(AppSettings.FREETEXTPORT));
        } else {
            m.gpsRxTx().openPort();
            if (m.isConnected()) {
                performOperationsAfterGPSConnect();
            }
        }
    }

    /**
     * Close the GPS connection.
     */
    public final void closeGPS() {
        if (m.isConnected()) {
            m.gpsRxTx().closePort();
            // TODO Move event posting to appropriate place (in model)
            m.postEvent(ModelEvent.DISCONNECTED);
        }
    }

    /**
     * Send an arbitrary NMEA string.
     * 
     */
    public final void sendNMEA(final String s) {
        m.gpsModel().sendNMEA(s);
    }

    /**
     * open a Bluetooth connection Calls getStatus to request initial
     * parameters from the device. Set up the timer to regurarly poll the
     * connection for data.
     */
    public final void setBluetooth() {
        closeGPS();
        m.gpsRxTx().setBluetoothAndOpen();
        performOperationsAfterGPSConnect();
    }

    /**
     * open a Usb connection Calls getStatus to request initial parameters
     * from the device. Set up the timer to regurarly poll the connection for
     * data.
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
     *                Port number to open
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
     *                The baud rate to set.
     */
    public final void setBaudRate(final int baudRate) {
        m.gpsRxTx().setBaudRate(baudRate);
    }

    /**
     * Select a port by its 'path' (/dev/usb9 for example or /dev/com1.
     * 
     * @param portName
     *                The path to the port.
     */
    public final void openFreeTextPort(final String portName) {
        closeGPS();
        m.gpsRxTx().setFreeTextPortAndOpen(portName);
        performOperationsAfterGPSConnect();
    }

    /**
     * This does a number of operations once the GPS is effectively connected.
     * Can be extended by the application. It gets certain informations
     * required by the application. It stores the settings related to the port
     * (since the connection was successful) and starts of the Model to do
     * port queries.
     */
    protected void performOperationsAfterGPSConnect() {
        if (m.isConnected()) {
            final GPSstate gpsModel = m.gpsModel();
            gpsModel.setDataNeeded(GPSstate.DATA_INITIAL_LOG); // First may
            // fail.
            gpsModel.reqStatus();
            gpsModel.setDataNeeded(GPSstate.DATA_FLASH_TYPE);
            reqLogFormat();
            gpsModel.setDataNeeded(GPSstate.DATA_INITIAL_LOG);
            // TODO: Setup timer in gpsRxTx instead of in the gpsModel
            gpsModel.initConnection();
            // Remember defaults
            setIntOpt(AppSettings.PORTNBR, m.gpsRxTx().getPort());
            setIntOpt(AppSettings.BAUDRATE, m.gpsRxTx().getSpeed());
            setStringOpt(AppSettings.FREETEXTPORT, m.gpsRxTx()
                    .getFreeTextPort());
            m.postEvent(ModelEvent.CONNECTED);
        }
    }

    /**
     * Set the debugging state of the connection.
     * 
     * @param isConnDebugActive
     *                When true, the connection debug information is active.
     */
    public final void setDebugConn(final boolean isConnDebugActive) {
        m.gpsRxTx().setDebugConn(isConnDebugActive,
                m.getStringOpt(AppSettings.OUTPUTDIRPATH));
    }

    /*************************************************************************
     * END OF SECTION FOR CONNECTION RELATED METHODS
     ************************************************************************/

    /**
     * Save all the user settings to disk.
     */
    public void saveSettings() {
        // saveSettings(); // Explicitly save settings
    }

    /**
     * Set the general debugging state.
     * 
     * @param isDebugActive
     *                If true, activate general debug.
     * 
     * @see #setDebugConn(boolean) for other debug functionality.
     */
    public final void setDebug(final boolean isDebugActive) {
        Generic.setDebugLevel(isDebugActive ? 1 : 0);
    }

    public final void resetFilters() {
        setTrkPtValid(0xFFFFFFFF & (~(BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
        setTrkPtRCR(0xFFFFFFFF);
        setWayPtValid(0xFFFFFFFF & (~(BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
        setWayPtRCR(BT747Constants.RCR_BUTTON_MASK
                | BT747Constants.RCR_ALL_APP_MASK);
        setBooleanOpt(AppSettings.ADVFILTACTIVE, false);
        setFilterMinRecCount(0);
        setFilterMaxRecCount(0);
        setFilterMinSpeed(0);
        setFilterMaxSpeed(0);
        setFilterMinDist(0);
        setFilterMaxDist(0);
        setFilterMaxPDOP(0);
        setFilterMaxHDOP(0);
        setFilterMaxVDOP(0);
        setFilterMinNSAT(0);
        setBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_COMMENT, true);
        setBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_NAME, true);
    }

    /**
     * Sets the trackpoint RCR mask for the active filter.
     * 
     * @param rcrMask -
     *                {@link BT747Constants#RCR_TIME_MASK}<br> -
     *                {@link BT747Constants#RCR_SPEED_MASK}<br> -
     *                {@link BT747Constants#RCR_DISTANCE_MASK}<br> -
     *                {@link BT747Constants#RCR_BUTTON_MASK}<br> -
     *                {@link BT747Constants#RCR_APP0_MASK}<br> -
     *                {@link BT747Constants#RCR_APP1_MASK}<br> -
     *                {@link BT747Constants#RCR_APP2_MASK}<br> -
     *                {@link BT747Constants#RCR_APP3_MASK}<br> -
     *                {@link BT747Constants#RCR_APP4_MASK}<br> -
     *                {@link BT747Constants#RCR_APP5_MASK}<br> -
     *                {@link BT747Constants#RCR_APP6_MASK}<br> -
     *                {@link BT747Constants#RCR_APP7_MASK}<br> -
     *                {@link BT747Constants#RCR_APP8_MASK}<br> -
     *                {@link BT747Constants#RCR_APP9_MASK}<br> -
     *                {@link BT747Constants#RCR_APPY_MASK}<br> -
     *                {@link BT747Constants#RCR_APPZ_MASK}<br> -
     *                {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final void setTrkPtRCR(final int rcrMask) {
        m.setTrkPtRCR(rcrMask);
    }

    /**
     * Sets the waypoint RCR mask for the active filter.
     * 
     * @param rcrMask -
     *                {@link BT747Constants#RCR_TIME_MASK}<br> -
     *                {@link BT747Constants#RCR_SPEED_MASK}<br> -
     *                {@link BT747Constants#RCR_DISTANCE_MASK}<br> -
     *                {@link BT747Constants#RCR_BUTTON_MASK}<br> -
     *                {@link BT747Constants#RCR_APP0_MASK}<br> -
     *                {@link BT747Constants#RCR_APP1_MASK}<br> -
     *                {@link BT747Constants#RCR_APP2_MASK}<br> -
     *                {@link BT747Constants#RCR_APP3_MASK}<br> -
     *                {@link BT747Constants#RCR_APP4_MASK}<br> -
     *                {@link BT747Constants#RCR_APP5_MASK}<br> -
     *                {@link BT747Constants#RCR_APP6_MASK}<br> -
     *                {@link BT747Constants#RCR_APP7_MASK}<br> -
     *                {@link BT747Constants#RCR_APP8_MASK}<br> -
     *                {@link BT747Constants#RCR_APP9_MASK}<br> -
     *                {@link BT747Constants#RCR_APPY_MASK}<br> -
     *                {@link BT747Constants#RCR_APPZ_MASK}<br> -
     *                {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final void setWayPtRCR(final int rcrMask) {
        m.setWayPtRCR(rcrMask);
    }

    /**
     * Sets the 'Valid' filter mask for the current track filter.
     * 
     * @param validMask
     *                The filter mask to set for the validity filter. Use the
     *                following constants:<br> -
     *                {@link BT747Constants#VALID_NO_FIX_MASK} <br> -
     *                {@link BT747Constants#VALID_SPS_MASK} <br> -
     *                {@link BT747Constants#VALID_DGPS_MASK} <br> -
     *                {@link BT747Constants#VALID_PPS_MASK} <br> -
     *                {@link BT747Constants#VALID_RTK_MASK} <br> -
     *                {@link BT747Constants#VALID_FRTK_MASK} <br> -
     *                {@link BT747Constants#VALID_ESTIMATED_MASK} <br> -
     *                {@link BT747Constants#VALID_MANUAL_MASK} <br> -
     *                {@link BT747Constants#VALID_SIMULATOR_MASK} <br> -
     * 
     */
    public final void setTrkPtValid(final int validMask) {
        m.setTrkPtValid(validMask);
    }

    /**
     * Sets the 'Valid' filter mask for the current waypoint filter.
     * 
     * @param validMask
     *                The filter mask to set for the validity filter. Use the
     *                following constants:<br> -
     *                {@link BT747Constants#VALID_NO_FIX_MASK} <br> -
     *                {@link BT747Constants#VALID_SPS_MASK} <br> -
     *                {@link BT747Constants#VALID_DGPS_MASK} <br> -
     *                {@link BT747Constants#VALID_PPS_MASK} <br> -
     *                {@link BT747Constants#VALID_RTK_MASK} <br> -
     *                {@link BT747Constants#VALID_FRTK_MASK} <br> -
     *                {@link BT747Constants#VALID_ESTIMATED_MASK} <br> -
     *                {@link BT747Constants#VALID_MANUAL_MASK} <br> -
     *                {@link BT747Constants#VALID_SIMULATOR_MASK} <br> -
     * 
     */
    public final void setWayPtValid(final int validMask) {
        m.setWayPtValid(validMask);
    }

    // The way logfilters are handled should be reviewed.

    /**
     * This sets MTK specific settings into its flash.<br>
     * The MTK device stores a number of settings in its internal flash which
     * is different from the log memory. These settings are restored after
     * loss of power for example.
     * 
     * 
     * @param lock
     *                When true, subsequent changes in these settings will be
     *                impossible.
     * @param updateRate
     *                The 'fix period' of the GPS in ms. When this is 200,
     *                then the Fix is 5Hz.
     * @param baudRate
     *                The speed of the serial communication of the MTK
     *                chipset. Be carefull - this may be the internal speed -
     *                not the external speed!
     * @param periodGLL
     *                The period of emission of the GLL sentence (relative to
     *                the fix).
     * @param periodRMC
     *                The period of emission of the RMC sentence (relative to
     *                the fix).
     * @param periodVTG
     *                The period of emission of the VTG sentence (relative to
     *                the fix).
     * @param periodGSA
     *                The period of emission of the GSA sentence (relative to
     *                the fix).
     * @param periodGSV
     *                The period of emission of the GSV sentence (relative to
     *                the fix).
     * @param periodGGA
     *                The period of emission of the GGA sentence (relative to
     *                the fix).
     * @param periodZDA
     *                The period of emission of the ZDA sentence (relative to
     *                the fix).
     * @param periodMCHN
     *                The period of emission of the MCHN sentence (relative to
     *                the fix).
     */
    public final void setFlashUserOption(final boolean lock,
            final int updateRate, final int baudRate, final int periodGLL,
            final int periodRMC, final int periodVTG, final int periodGSA,
            final int periodGSV, final int periodGGA, final int periodZDA,
            final int periodMCHN) {
        m.gpsModel().setFlashUserOption(lock, updateRate, baudRate,
                periodGLL, periodRMC, periodVTG, periodGSA, periodGSV,
                periodGGA, periodZDA, periodMCHN);
        reqFlashUserOption();
    }

    /**
     * Request the flash user settings from the device. Following the relevant
     * event, the settings must be retrieved using
     * {@link Model#getDtUpdateRate()}<br> - {@link Model#getDtGLL_Period()}<br> -
     * {@link Model#getDtRMC_Period()}<br> - {@link Model#getDtVTG_Period()}<br> -
     * {@link Model#getDtGSA_Period()}<br> - {@link Model#getDtGSV_Period()}<br> -
     * {@link Model#getDtGGA_Period()}<br> - {@link Model#getDtZDA_Period()}<br> -
     * {@link Model#getDtMCHN_Period()}<br> - {@link Model#getDtBaudRate()}<br> -
     * {@link Model#getDtUserOptionTimesLeft()}<br> -
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
     * Sets the MAC address for bluetooth (for devices that support it).
     * 
     * @param btMacAddr
     *                The Mac address to set in the following format:<br>
     *                00:1F:14:15:12:13.
     */
    public final void setBTMacAddr(final String btMacAddr) {
        m.gpsModel().setBtMacAddr(btMacAddr);
        reqBTAddr();
    }

    /**
     * Set the textual description of the holux device.
     * 
     * @param holuxName
     *                The string to set as the Holux Name.
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
     *                The array indexes are given by:<br> -
     *                {@link BT747Constants#NMEA_SEN_GLL_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_RMC_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_VTG_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GGA_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GSA_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GSV_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GRS_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GST_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MALM_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MEPH_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MDGP_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MDBG_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_ZDA_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MCHN_IDX}<br> -
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
     *                When true, enable test satellites.
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
     *                When true, enables SBAS.
     */
    public final void setSBASEnabled(final boolean set) {
        m.gpsModel().setSBASEnabled(set);
        reqSBASEnabled();
    }

    /**
     * Request the current status of SBAS (DGPS) enable setting. Actual value
     * to be retrieved later with {@link Model#isSBASEnabled()}.
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
     *                The datum mode to set.
     */
    public final void setDatumMode(final int mode) {
        m.gpsModel().setDatumMode(mode);
        reqDatumMode();
    }

    /**
     * Set the DGPS mode to use when SBAS enabled.
     * 
     * @param mode
     *                The mode to use.
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
     *                If true, enable power save mode.
     */
    public final void setPowerSaveEnabled(final boolean set) {
        m.gpsModel().setPowerSaveEnabled(set);
        reqPowerSaveEnabled();
    }

    /**
     * Request the power save mode setting of the device. Need to get the
     * actual setting later with {@link Model#isPowerSaveEnabled()}.
     */
    public final void reqPowerSaveEnabled() {
        m.gpsModel().reqPowerSaveEnabled();
    }

    /**
     * Request the log condition (reason) settings of the device. Need to get
     * actual values through:<br>
     * {@link Model#getLogTimeInterval()
     * 
     * @link Model#getLogDistanceInterval()} <br>
     *       {@link Model#getLogSpeedInterval()}
     */
    public final void reqLogReasonStatus() {
        m.gpsModel().reqLogReasonStatus();
    }

    /**
     * Request the fix interval setting from the device. Need to get the
     * actual value through {@link Model#getLogFixPeriod()}.
     * 
     */
    public final void reqFixInterval() {
        m.gpsModel().reqFixInterval();
    }

    /**
     * Force the logger to record the current position tagged with the given
     * log reason.
     * 
     * @param value
     *                {@link BT747Constants#RCR_TIME_MASK}
     *                {@link BT747Constants#RCR_SPEED_MASK}
     *                {@link BT747Constants#RCR_DISTANCE_MASK}
     *                {@link BT747Constants#RCR_BUTTON_MASK}
     *                {@link BT747Constants#RCR_APP0_MASK}
     *                {@link BT747Constants#RCR_APP1_MASK}
     *                {@link BT747Constants#RCR_APP2_MASK}
     *                {@link BT747Constants#RCR_APP3_MASK}
     *                {@link BT747Constants#RCR_APP4_MASK}
     *                {@link BT747Constants#RCR_APP5_MASK}
     *                {@link BT747Constants#RCR_APP6_MASK}
     *                {@link BT747Constants#RCR_APP7_MASK}
     *                {@link BT747Constants#RCR_APP8_MASK}
     *                {@link BT747Constants#RCR_APP9_MASK}
     *                {@link BT747Constants#RCR_APPY_MASK}
     *                {@link BT747Constants#RCR_APPZ_MASK}
     *                {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final void logImmediate(final int value) {
        m.gpsModel().logImmediate(value);
    }

    public final void setFixInterval(final int value) {
        if (value != 0) {
            m.gpsModel().setFixInterval(value);
            reqFixInterval();
        }
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
        setBooleanOpt(AppSettings.DECODEGPS, value);
        m.gpsModel().setGpsDecode(value);
    }

    public final void setGpxTrkSegWhenBig(final boolean b) {
        setBooleanOpt(AppSettings.GPXTRKSEGBIG, b);
    }

    public final void setGpxUTC0(final boolean b) {
        setBooleanOpt(AppSettings.GPXUTC0, b);
    }

    public final void setFilterEndTime(final int d) {
        m.setFilterEndTime(d);
    }

    public final void setFilterStartTime(final int d) {
        m.setFilterStartTime(d);
    }

    public final void storeSetting1() {
        setIntOpt(AppSettings.SETTING1_TIME, m.getLogTimeInterval());
        setIntOpt(AppSettings.SETTING1_DIST, m.getLogDistanceInterval());
        setIntOpt(AppSettings.SETTING1_SPEED, m.getLogSpeedInterval());
        setIntOpt(AppSettings.SETTING1_LOG_FORMAT, m.getLogFormat());
        setIntOpt(AppSettings.SETTING1_FIX, m.getLogFixPeriod());
        setBooleanOpt(AppSettings.SETTING1_SBAS, m.isSBASEnabled());
        setIntOpt(AppSettings.SETTING1_DGPS, m.getDgpsMode());
        setBooleanOpt(AppSettings.SETTING1_TEST, m.isSBASTestEnabled());
        setBooleanOpt(AppSettings.SETTING1_LOG_OVR, m.isLogFullOverwrite());
        String sNMEA = "";
        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            sNMEA += (m.getNMEAPeriod(i));
        }
        setStringOpt(AppSettings.SETTING1_NMEA, sNMEA);
    }

    public final void restoreSetting1() {
        setLogTimeInterval(m.getIntOpt(AppSettings.SETTING1_TIME));
        setLogDistanceInterval(m.getIntOpt(AppSettings.SETTING1_DIST));
        setLogSpeedInterval(m.getIntOpt(AppSettings.SETTING1_SPEED));
        setLogFormat(m.getIntOpt(AppSettings.SETTING1_LOG_FORMAT));
        setFixInterval(m.getIntOpt(AppSettings.SETTING1_FIX));
        setSBASEnabled(m.getBooleanOpt(AppSettings.SETTING1_SBAS));
        setSBASTestEnabled(m.getBooleanOpt(AppSettings.SETTING1_TEST));
        setDGPSMode(m.getIntOpt(AppSettings.SETTING1_DGPS));
        setLogOverwrite(m.getBooleanOpt(AppSettings.SETTING1_LOG_OVR));

        final String sNMEA = m.getStringOpt(AppSettings.SETTING1_NMEA);
        final int[] periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            periods[i] = (sNMEA.charAt(i) - '0');
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
            final GPSFilterAdvanced filter = m.getLogFiltersAdv()[i];
            filter.setMinRecCount(m.getFilterMinRecCount());
            filter.setMaxRecCount(m.getFilterMaxRecCount());
            filter.setMinSpeed(m.getFilterMinSpeed());
            filter.setMaxSpeed(m.getFilterMaxSpeed());
            filter.setMinDist(m.getFilterMinDist());
            filter.setMaxDist(m.getFilterMaxDist());
            filter
                    .setMaxPDOP((int) (m.getFilterMaxPDOP() * Controller.XDOP_FLOAT_TO_INT_100));
            filter
                    .setMaxHDOP((int) (m.getFilterMaxHDOP() * Controller.XDOP_FLOAT_TO_INT_100));
            filter
                    .setMaxVDOP((int) (m.getFilterMaxVDOP() * Controller.XDOP_FLOAT_TO_INT_100));
            filter.setMinNSAT(m.getFilterMinNSAT());
        }
    }

    /**
     * Sets the NMEA string types to write to the NMEA output file format.
     * 
     * @param formatNMEA
     *                Bit format using following bit indexes:<br> -
     *                {@link BT747Constants#NMEA_SEN_GLL_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_RMC_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_VTG_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GGA_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GSA_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GSV_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GRS_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_GST_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MALM_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MEPH_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MDGP_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MDBG_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_ZDA_IDX}<br> -
     *                {@link BT747Constants#NMEA_SEN_MCHN_IDX}<br>
     */
    public final void setNMEAset(final int formatNMEA) {
        m.setNMEAset(formatNMEA);
    }

    /**
     * The way we split the input data.
     * 
     * @param value
     *                0=all data in one file;<br>
     *                1=one file per day (split at midnight after time offset
     *                calculation)<br>
     *                2=one file per track (a track separation occurs when the
     *                time between two log points is bigger than a given
     *                number.
     */
    public final void setOutputFileSplitType(final int value) {
        m.setOutputFileSplitType(value);
    }

    /**
     * @param heightConversionMode
     * 
     * When true the height (WGS84) will be converted to Mean Sea Level.
     * {@link Model#HEIGHT_AUTOMATIC} {@link Model#HEIGHT_NOCHANGE}
     * {@link Model#HEIGHT_WGS84_TO_MSL}
     */
    public final void setHeightConversionMode(final int heightConversionMode) {
        m.setHeightConversionMode(heightConversionMode);
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

    public void setBooleanOpt(final int param, final boolean value) {
        m.setBooleanOpt(param, value);
    }

    public void setIntOpt(final int param, final int value) {
        m.setIntOpt(param, value);
    }

    public void setStringOpt(final int param, final String value) {
        m.setStringOpt(param, value);
    }

    public final static void addLogFile(final String path, final int card) {
        final LogFileInfo loginfo = new LogFileInfo(path, card);
        Controller.logFiles.addElement(loginfo);
    }
}