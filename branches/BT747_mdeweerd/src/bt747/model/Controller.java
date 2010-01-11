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
import gps.GpsEvent;
import gps.ProtocolConstants;
import gps.connection.GPSrxtx;
import gps.log.GPSFilter;
import gps.log.GPSFilterAdvanced;
import gps.log.GPSRecord;
import gps.log.LogFileInfo;
import gps.log.TracksAndWayPoints;
import gps.log.in.GPSInputConversionFactory;
import gps.log.in.GPSLogConvertInterface;
import gps.log.in.MultiLogConvert;
import gps.log.out.CommonOut;
import gps.log.out.GPSArray;
import gps.log.out.GPSCSVFile;
import gps.log.out.GPSCompoGPSTrkFile;
import gps.log.out.GPSConversionParameters;
import gps.log.out.GPSFile;
import gps.log.out.GPSFileConfInterface;
import gps.log.out.GPSFileInterface;
import gps.log.out.GPSGPXFile;
import gps.log.out.GPSGmapsHTMLEncodedFile;
import gps.log.out.GPSGoogleStaticMapUrl;
import gps.log.out.GPSKMLFile;
import gps.log.out.GPSNMEAFile;
import gps.log.out.GPSPLTFile;
import gps.log.out.GPSPostGISFile;
import gps.log.out.GPSSqlFile;
import gps.log.out.WayPointStyle;
import gps.log.out.WayPointStyleSet;
import gps.mvc.CmdParam;
import gps.mvc.MtkController;
import gps.mvc.MtkModel;
import gps.mvc.commands.GpsLinkExecCommand;
import net.sf.bt747.loc.LocationSender;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Exception;
import bt747.sys.interfaces.BT747FileName;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Vector;

/**
 * @author Mario De Weerd
 * @author Florian Unger for indicated parts.
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
     *            The model to associate with this controller.
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
        if (m != null) {
            m.removeListener(this);
        }
        m = model;
        m.addListener(this);
    }

    public Model getModel() {
        return m;
    }

    private final gps.mvc.GpsController getGpsC() {
        return m.gpsC();
    }

    private final gps.mvc.GpsModel getGpsOldC() {
        return m.gpsM();
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
     */
    public void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        case ModelEvent.CONNECTED:
            // Florian Unger
            // If position service is configured in the setting start
            // sending location data on conect to the GPS device
            if (m.getBooleanOpt(AppSettings.POS_SRV_AUTOSTART)) {
                this.startGPSPositionServing();
            }
            break;
        case ModelEvent.DISCONNECTED:
            // Florian Unger
            // stop sending location data when no position updates
            // can be retrieved from the GPS device.
            if (this.isLocationServingActive()) {
                this.stopGPSPositionServing();
            }
            break;
        case ModelEvent.POS_SRV_FATAL_FAILURE:
            // Florian Unger
            // This event has been sent from a LocationSender instance
            // On a fatal failure stop the sending process
            if (this.isLocationServingActive()) {
                this.stopGPSPositionServing();
            }
            break;
        case ModelEvent.UPDATE_LOG_LOG_STATUS:
            // TODO: Possibly move this to the mtkController.
            if (m.mtkModel().isLoggingDisabled()) {
                setAutoLog(true);
            }
            break;
        case ModelEvent.AGPS_UPLOAD_DONE:
            setMtkDataNeeded(MtkModel.DATA_AGPS_STORED_RANGE);
            break;
        case ModelEvent.SETTING_CHANGE: {
            final int setting = JavaLibBridge.toInt((String) e.getArg());
            switch (setting) {
            case Model.DEVICE_PROTOCOL:
                m.setProtocol(m.getIntOpt(Model.DEVICE_PROTOCOL));
            }
        }
        }
    }

    public final void setAutoLog(final boolean enable) {
        if (enable) {
            gpsCmd(MtkController.CMD_AUTOLOG_ON);
        } else {
            gpsCmd(MtkController.CMD_AUTOLOG_OFF);
        }
    }

    private final MtkController mtkC() {
        // private final DeviceControllerIF gpsC() {
        return getGpsC().getMtkController();
    }

    /**
     * Called when the Controller starts. Used for initialization.
     */
    public void init() {
        getGpsC().setGpsDecode(m.getBooleanOpt(AppSettings.DECODEGPS));
        getGpsC().setDownloadTimeOut(m.getDownloadTimeOut());
        mtkC().setLogRequestAhead(m.getIntOpt(AppSettings.LOGAHEAD));

        final int port = m.getIntOpt(AppSettings.PORTNBR);
        if (GPSrxtx.hasDefaultPortInstance()) {
            if (port != Controller.NOT_A_PORT_NUMBER) {
                // TODO: review this, especially with 'freetext port'
                m.gpsRxTx().setDefaults(port,
                        m.getIntOpt(AppSettings.BAUDRATE));
            }
            if (m.getBooleanOpt(AppSettings.OPENPORTATSTARTUP)) {
                connectGPS();
            }
        }
    }

    /**
     * Do a GPS command.
     * 
     * @param cmd
     */
    public void gpsCmd(final int cmd) {
        getGpsC().cmd(cmd);
    }

    /**
     * Do a GPS command.
     * 
     * @param cmd
     */
    public void gpsCmd(final int cmd, final CmdParam param) {
        getGpsC().cmd(cmd, param);
    }

    /**
     * Set the output file path (including basename, no extension) relative to
     * the BaseDirPath.
     * 
     * @param s
     *            The relative log file path (including basename)
     */
    public final void setOutputFileRelPath(final String s) {
        setStringOpt(AppSettings.REPORTFILEBASE, s);
    }

    /**
     * @param chunkSize
     *            The amount of data that is requested from the device in a
     *            single command when downloading the data.
     */

    public final void setChunkSize(final int chunkSize) {
        m.setChunkSize(chunkSize);
    }

    /**
     * @param timeout
     *            The timeout in ms after which the lack of reply from the
     *            device will be considered as a communication failure.
     *            Depending on the operation, a recovery will be attempted.
     */
    public final void setDownloadTimeOut(final int timeout) {
        m.setDownloadTimeOut(timeout);
        getGpsC().setDownloadTimeOut(timeout);
    }

    /**
     * @param numberOfRequestsAhead
     *            Especially when downloading using Bluetooth, downloading is
     *            slow when no request pipeline is implemented. This number
     *            defines how many 'chunk download' request will be sent to
     *            device while the first reply is still pending.
     */
    public final void setLogRequestAhead(final int numberOfRequestsAhead) {
        setIntOpt(AppSettings.LOGAHEAD, numberOfRequestsAhead);
        mtkC().setLogRequestAhead(m.getIntOpt(AppSettings.LOGAHEAD));
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

    public final GPSFile getOutFileHandler(final int logType) {
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
        case Model.GOOGLE_MAP_STATIC_URL_LOGTYPE:
            gpsFile = new GPSGoogleStaticMapUrl();
            break;
        case Model.POSTGIS_LOGTYPE:
            gpsFile = new GPSPostGISFile();
            break;
        case Model.SQL_LOGTYPE:
            gpsFile = new GPSSqlFile();
            break;
        default:
            lastError = BT747Constants.ERROR_UNKNOWN_OUTPUT_FORMAT;
            lastErrorInfo = "" + logType;
        }
        return gpsFile;
    }

    private void configureGpsFile(final GPSFileConfInterface gpsFile) {
        if (gpsFile != null) {
            if (!(((gpsFile.getClass() == GPSGPXFile.class) && m
                    .getBooleanOpt(AppSettings.GPXUTC0)) || ((gpsFile
                    .getClass() == GPSNMEAFile.class) && m
                    .getBooleanOpt(AppSettings.NMEAUTC0)))) {
                gpsFile.setTimeOffset((m
                        .getIntOpt(AppSettings.GPSTIMEOFFSETQUARTERS) - 48)
                        * (Controller.SECONDS_PER_HOUR / 4));
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
            gpsFile.getParamObject().setParam(
                    GPSConversionParameters.OSM_LOGIN,
                    m.getStringOpt(AppSettings.OSMLOGIN));
            gpsFile.getParamObject().setParam(
                    GPSConversionParameters.OSM_PASS,
                    m.getStringOpt(AppSettings.OSMPASS));
            gpsFile.getParamObject().setBoolParam(
                    GPSConversionParameters.NEW_TRACK_WHEN_LOG_ON,
                    m.getBooleanOpt(AppSettings.IS_NEW_TRACK_WHEN_LOG_ON));
            gpsFile.getParamObject().setIntParam(
                    GPSConversionParameters.SPLIT_DISTANCE,
                    m.getIntOpt(AppSettings.SPLIT_DISTANCE));
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

    public String getOutFileExt(final int logType) {
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
        case Model.GOOGLE_MAP_STATIC_URL_LOGTYPE:
            ext = ".txt";
            break;
        case Model.SQL_LOGTYPE:
            ext = ".sql";
            break;
        case Model.POSTGIS_LOGTYPE:
            ext = ".postgissql";
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
     *            Indicates the type of log that should be written. For
     *            example Model.CSV_LOGTYPE .
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
        if (Model.logFiles.size() != 0) {
            final MultiLogConvert mlc = new MultiLogConvert();
            mlc.setLogFiles(Model.logFiles);
            lc = mlc;
        } else {
            lc = getInputConversionInstance();
        }
        final int destinationHeightReference = getHeightReference(logType);
        final int sourceHeightReference = getHeightReference(lc.getType());
        String parameters = "";

        switch (m.getIntOpt(AppSettings.HEIGHT_CONVERSION_MODE)) {
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

        lc.setLoggerType(m.getIntOpt(AppSettings.GPSTYPE));
        if (Generic.isDebug()) {
            Generic.debug(parameters);
        }

        return lc;
    }

    public final GPSLogConvertInterface getInputConversionInstance() {
        return GPSInputConversionFactory.getHandler()
                .getInputConversionInstance(
                        m.getPath(AppSettings.LOGFILEPATH));
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

    public final int doConvertLog(final int logType,
            final GPSFileInterface gpsFile, final String ext) {
        int result;
        final String parameters = ""; // For debug
        GPSLogConvertInterface lc;
        result = 0;
        configureGpsFile(gpsFile);

        if (logType == Model.OSM_LOGTYPE) {
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
            gpsFile.initialiseFile(m.getPath(AppSettings.REPORTFILEBASEPATH), ext, m.getIntOpt(AppSettings.OUTPUTFILESPLITTYPE));
            m.logConversionStarted(logType);
            try {
                lastError = lc.toGPSFile(m.getPath(AppSettings.LOGFILEPATH),
                        gpsFile);
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

        gpsFile.initialiseFile(new BT747Path(""), "", m.getIntOpt(AppSettings.OUTPUTFILESPLITTYPE));
        m.logConversionStarted(Model.ARRAY_LOGTYPE);
        // gpsFile.setTrackSepTime(m.getTrkSep() * 60);
        currentGPSLogConvert = lc;
        try {
            error = lc.toGPSFile(m.getPath(AppSettings.LOGFILEPATH), gpsFile);
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
     *            true if the log download should be incremental.
     */
    public final void setIncremental(final boolean incrementalDownload) {
        m.setIncremental(incrementalDownload);
    }

    /**
     * Set the download method. <br>
     * Possible values:
     * <ul>
     * <li>{@link Model#DOWNLOAD_FILLED}</li>
     * <li>{@link Model#DOWNLOAD_FULL}</li>
     * <li>{@link Model#DOWNLOAD_SMART}</li>
     * </ul>
     */
    public final void setDownloadMethod(final int downloadMethod) {
        m.setDownloadMethod(downloadMethod);
    }

    /**
     * Cancel the log download process.
     */
    public final void cancelGetLog() {
        gpsCmd(MtkController.CMD_CANCEL_GETLOG);
    }

    /**
     * Start the log download process.
     */
    public final void startDownload() {
        switch (m.getIntOpt(Model.DEVICE_PROTOCOL)) {
        default:
        case ProtocolConstants.PROTOCOL_MTK:
        case ProtocolConstants.PROTOCOL_HOLUX_PHLX:
            startDefaultDownload();
            break;
        case ProtocolConstants.PROTOCOL_SIRFIII:
            // mtkC = new MtkController(this, mtkM);
            break;
        case ProtocolConstants.PROTOCOL_WONDEPROUD:
            startWPDownload();
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
            endAddress = m.logMemUsed() - 1;
            if ((m.getDownloadMethod() == Model.DOWNLOAD_FULL)
                    || m.mtkModel().isInitialLogOverwrite()) {
                if (endAddress < m.logMemSize()) {
                    // Download at least the reported used memory.
                    endAddress = m.logMemSize() - 1;
                }
            } else {
                endAddress = m.logMemUsed() - 1;
            }
            mtkC().getLogInit(0, /* StartPosition */
            endAddress, /* EndPosition */
            m.getChunkSize(), /* Size per request */
            m.getPath(AppSettings.LOGFILEPATH),
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
     *            If true, the existing log can be overwritten
     * @throws BT747Exception
     */
    public final void replyToOkToOverwrite(final boolean isOkToOverwrite)
            throws BT747Exception {
        mtkC().replyToOkToOverwrite(isOkToOverwrite);
    }

    /**
     * The log is being erased - the user request to abandon waiting for the
     * end of this operation.
     */
    public final void stopErase() {
        getGpsC().stopErase();
    }

    /**
     * Initiate the download of a 'Wonde Proud' log.
     */
    public final void startWPDownload() {
        // TODO: Should listen to AppSettings.GPSTYPE changes and activate
        // Wonde Proud when appropriate.
        // Initialisation method and download start should change.
        getGpsC()
                .getLog(m.getPath(AppSettings.LOGFILEPATH));
    }

    /**
     * Set logging status of device.
     * 
     * @param on
     *            When true, logging will be turned on.
     */
    public final void setLoggingActive(final boolean on) {
        if (on) {
            gpsCmd(MtkController.CMD_STARTLOG);
        } else {
            gpsCmd(MtkController.CMD_STOPLOG);
        }
        getGpsC().reqLogOnOffStatus();
    }

    /**
     * Indicates that some data is needed from the MtkDevice. This will
     * eventually issue a request to the device.
     * 
     * @param dataType
     */
    public final void setMtkDataNeeded(final int dataType) {
        getGpsC().setDataNeeded(dataType);
    }

    /**
     * Set log overwrite mode on the device.
     * 
     * @param isOverWriteLog
     *            true - overwrite data in device when full false - stop
     *            logging when device is full
     */
    public final void setLogOverwrite(final boolean isOverWriteLog) {
        gpsCmd(MtkController.CMD_SET_LOG_OVERWRITE, new CmdParam(
                isOverWriteLog));
        setMtkDataNeeded(MtkModel.DATA_LOG_OVERWRITE_STATUS);
    };

    /**
     * Request a set of device information from the GPS device that can be
     * after specific {@link GpsEvent} events.
     */
    public final void reqDeviceInfo() {
        getGpsC().reqDeviceInfo();
    }

    /**
     * Sets a new log format on the device.<br>
     * 
     * @param newLogFormat
     * <br>
     *            The bits in the newLogFormat can be defined using a bitwise
     *            OR of expressions like<br>
     *            (1<< IDX) <br>
     *            where IDX is one of the following:<br>
     *            - {@link BT747Constants#FMT_UTC_IDX} <br>
     *            - {@link BT747Constants#FMT_VALID_IDX} <br>
     *            - {@link BT747Constants#FMT_LATITUDE_IDX} <br>
     *            - {@link BT747Constants#FMT_LONGITUDE_IDX} <br>
     *            - {@link BT747Constants#FMT_HEIGHT_IDX} <br>
     *            - {@link BT747Constants#FMT_SPEED_IDX} <br>
     *            - {@link BT747Constants#FMT_HEADING_IDX} <br>
     *            - {@link BT747Constants#FMT_DSTA_IDX} <br>
     *            - {@link BT747Constants#FMT_DAGE_IDX} <br>
     *            - {@link BT747Constants#FMT_PDOP_IDX} <br>
     *            - {@link BT747Constants#FMT_HDOP_IDX} <br>
     *            - {@link BT747Constants#FMT_VDOP_IDX} <br>
     *            - {@link BT747Constants#FMT_NSAT_IDX} <br>
     *            - {@link BT747Constants#FMT_SID_IDX} <br>
     *            - {@link BT747Constants#FMT_ELEVATION_IDX} <br>
     *            - {@link BT747Constants#FMT_AZIMUTH_IDX} <br>
     *            - {@link BT747Constants#FMT_SNR_IDX} <br>
     *            - {@link BT747Constants#FMT_RCR_IDX} <br>
     *            - {@link BT747Constants#FMT_MILLISECOND_IDX} <br>
     *            - {@link BT747Constants#FMT_DISTANCE_IDX} <br>
     *            - {@link BT747Constants#FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX} <br>
     */
    public final void setLogFormat(final int newLogFormat) {
        mtkC().setLogFormat(newLogFormat);
        setMtkDataNeeded(MtkModel.DATA_LOG_FORMAT);
    }

    /**
     * Do the actual erase.
     */
    public final void eraseLog() {
        // TODO: Handle erase popup.
        getGpsC().eraseLog();
    }

    /**
     * A 'recovery Erase' attempts to recover memory that was previously
     * identified as 'bad'.
     */
    public final void recoveryEraseLog() {
        mtkC().recoveryEraseLog();
    }

    /*************************************************************************
     * SECTION FOR CONNECTION RELATED METHODS.
     ************************************************************************/

    /**
     * Connect to the GPS (open the serial connection).
     */
    public final void connectGPS() {
        closeGPS();
        Generic.debug("Freeport is "
                + m.getStringOpt(AppSettings.FREETEXTPORT));
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
     * Send an arbitrary GPS command.
     * 
     * @param cmd
     *            Otherwise the class must be an implementation of
     *            {@link GpsLinkExecCommand}.
     * 
     */
    public final void sendCmd(final GpsLinkExecCommand cmd) {
        getGpsOldC().sendCmd(cmd);
    }

    /**
     * Some data will be constantly requested from the device to keep it up to
     * date in the application. When inactive the benefit is that the
     * application fully controls what happens on the serial link.
     * 
     * @param isAuto
     *            When true, the data is fetched automatically.
     */
    public final void setAutoFetch(final boolean isAuto) {
        m.mtkModel().setAutoFetch(isAuto);
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
    public void performOperationsAfterGPSConnect() {
        if (m.isConnected()) {
            setMtkDataNeeded(MtkModel.DATA_INITIAL_LOG); // First may
            // fail.
            getGpsC().reqStatus();
            setMtkDataNeeded(MtkModel.DATA_FLASH_TYPE);
            setMtkDataNeeded(MtkModel.DATA_LOG_FORMAT);
            setMtkDataNeeded(MtkModel.DATA_INITIAL_LOG);
            // TODO: Setup timer in gpsRxTx instead of in the gpsModel
            getGpsC().initConnection();
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
     *            When true, the connection debug information is active.
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
     *            If true, activate general debug.
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
        setIntOpt(AppSettings.MIN_RECCOUNT, 0);
        setIntOpt(AppSettings.MAX_RECCOUNT, 0);
        setFloatOpt(AppSettings.MIN_SPEED, 0);
        setFloatOpt(AppSettings.MAX_SPEED, 0);
        setFloatOpt(AppSettings.MIN_DISTANCE, 0);
        setFloatOpt(AppSettings.MAX_DISTANCE, 0);
        setFloatOpt(AppSettings.MAX_PDOP, 0);
        setFloatOpt(AppSettings.MAX_HDOP,0);
        setFloatOpt(AppSettings.MAX_VDOP, 0);
        setIntOpt(AppSettings.MIN_NSAT, 0);
        setBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_COMMENT, true);
        setBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_NAME, true);
    }

    /**
     * Sets the trackpoint RCR mask for the active filter.
     * 
     * @param rcrMask
     *            - {@link BT747Constants#RCR_TIME_MASK}<br>
     *            - {@link BT747Constants#RCR_SPEED_MASK}<br>
     *            - {@link BT747Constants#RCR_DISTANCE_MASK}<br>
     *            - {@link BT747Constants#RCR_BUTTON_MASK}<br>
     *            - {@link BT747Constants#RCR_APP0_MASK}<br>
     *            - {@link BT747Constants#RCR_APP1_MASK}<br>
     *            - {@link BT747Constants#RCR_APP2_MASK}<br>
     *            - {@link BT747Constants#RCR_APP3_MASK}<br>
     *            - {@link BT747Constants#RCR_APP4_MASK}<br>
     *            - {@link BT747Constants#RCR_APP5_MASK}<br>
     *            - {@link BT747Constants#RCR_APP6_MASK}<br>
     *            - {@link BT747Constants#RCR_APP7_MASK}<br>
     *            - {@link BT747Constants#RCR_APP8_MASK}<br>
     *            - {@link BT747Constants#RCR_APP9_MASK}<br>
     *            - {@link BT747Constants#RCR_APPY_MASK}<br>
     *            - {@link BT747Constants#RCR_APPZ_MASK}<br>
     *            - {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final void setTrkPtRCR(final int rcrMask) {
        m.setTrkPtRCR(rcrMask);
    }

    /**
     * Sets the waypoint RCR mask for the active filter.
     * 
     * @param rcrMask
     *            - {@link BT747Constants#RCR_TIME_MASK}<br>
     *            - {@link BT747Constants#RCR_SPEED_MASK}<br>
     *            - {@link BT747Constants#RCR_DISTANCE_MASK}<br>
     *            - {@link BT747Constants#RCR_BUTTON_MASK}<br>
     *            - {@link BT747Constants#RCR_APP0_MASK}<br>
     *            - {@link BT747Constants#RCR_APP1_MASK}<br>
     *            - {@link BT747Constants#RCR_APP2_MASK}<br>
     *            - {@link BT747Constants#RCR_APP3_MASK}<br>
     *            - {@link BT747Constants#RCR_APP4_MASK}<br>
     *            - {@link BT747Constants#RCR_APP5_MASK}<br>
     *            - {@link BT747Constants#RCR_APP6_MASK}<br>
     *            - {@link BT747Constants#RCR_APP7_MASK}<br>
     *            - {@link BT747Constants#RCR_APP8_MASK}<br>
     *            - {@link BT747Constants#RCR_APP9_MASK}<br>
     *            - {@link BT747Constants#RCR_APPY_MASK}<br>
     *            - {@link BT747Constants#RCR_APPZ_MASK}<br>
     *            - {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final void setWayPtRCR(final int rcrMask) {
        m.setWayPtRCR(rcrMask);
    }

    /**
     * Sets the 'Valid' filter mask for the current track filter.
     * 
     * @param validMask
     *            The filter mask to set for the validity filter. Use the
     *            following constants:<br>
     *            - {@link BT747Constants#VALID_NO_FIX_MASK} <br>
     *            - {@link BT747Constants#VALID_SPS_MASK} <br>
     *            - {@link BT747Constants#VALID_DGPS_MASK} <br>
     *            - {@link BT747Constants#VALID_PPS_MASK} <br>
     *            - {@link BT747Constants#VALID_RTK_MASK} <br>
     *            - {@link BT747Constants#VALID_FRTK_MASK} <br>
     *            - {@link BT747Constants#VALID_ESTIMATED_MASK} <br>
     *            - {@link BT747Constants#VALID_MANUAL_MASK} <br>
     *            - {@link BT747Constants#VALID_SIMULATOR_MASK} <br>
     *            -
     * 
     */
    public final void setTrkPtValid(final int validMask) {
        m.setTrkPtValid(validMask);
    }

    /**
     * Sets the 'Valid' filter mask for the current waypoint filter.
     * 
     * @param validMask
     *            The filter mask to set for the validity filter. Use the
     *            following constants:<br>
     *            - {@link BT747Constants#VALID_NO_FIX_MASK} <br>
     *            - {@link BT747Constants#VALID_SPS_MASK} <br>
     *            - {@link BT747Constants#VALID_DGPS_MASK} <br>
     *            - {@link BT747Constants#VALID_PPS_MASK} <br>
     *            - {@link BT747Constants#VALID_RTK_MASK} <br>
     *            - {@link BT747Constants#VALID_FRTK_MASK} <br>
     *            - {@link BT747Constants#VALID_ESTIMATED_MASK} <br>
     *            - {@link BT747Constants#VALID_MANUAL_MASK} <br>
     *            - {@link BT747Constants#VALID_SIMULATOR_MASK} <br>
     *            -
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
     *            When true, subsequent changes in these settings will be
     *            impossible.
     * @param updateRate
     *            The 'fix period' of the GPS in ms. When this is 200, then
     *            the Fix is 5Hz.
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
        mtkC().setFlashUserOption(lock, updateRate, baudRate, periodGLL,
                periodRMC, periodVTG, periodGSA, periodGSV, periodGGA,
                periodZDA, periodMCHN);
        setMtkDataNeeded(MtkModel.DATA_FLASH_USER_OPTION);
    }

    /**
     * Sets the MAC address for bluetooth (for devices that support it).
     * 
     * @param btMacAddr
     *            The Mac address to set in the following format:<br>
     *            00:1F:14:15:12:13.
     */
    public final void setBTMacAddr(final String btMacAddr) {
        mtkC().setBtMacAddr(btMacAddr);
        setMtkDataNeeded(MtkModel.DATA_BT_MAC_ADDR);
    }

    /**
     * Set the textual description of the device. Currently supported by Holux
     * devices.
     * 
     * @param deviceName
     *            The string to set as the Device Name.
     */
    public final void setHoluxName(final String deviceName) {
        mtkC().cmd(MtkController.CMD_SET_DEVICE_NAME,
                new CmdParam(deviceName));
        setMtkDataNeeded(MtkModel.DATA_DEVICE_NAME);
    }

    /**
     * Set the NMEA period settings of the device.
     * 
     * @param periods
     *            The array indexes are given by:<br>
     *            - {@link BT747Constants#NMEA_SEN_GLL_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_RMC_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_VTG_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GGA_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GSA_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GSV_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GRS_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GST_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MALM_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MEPH_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MDGP_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MDBG_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_ZDA_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MCHN_IDX}<br>
     *            -
     */
    public final void setNMEAPeriods(final int[] periods) {
        mtkC().setNMEAPeriods(periods);
        setMtkDataNeeded(MtkModel.DATA_NMEA_OUTPUT_PERIODS);
    }

    /**
     * Sets default NMEA periods (as observed on one iBlue 747 device).
     */
    public final void setNMEADefaultPeriods() {
        mtkC().setNMEADefaultPeriods();
    }

    /**
     * Sets the enable of SBAS (DGPS) satellites that are in test.
     * 
     * @param isSBASTestEnabled
     *            When true, enable test satellites.
     */
    public final void setSBASTestEnabled(final boolean isSBASTestEnabled) {
        gpsCmd(MtkController.CMD_SET_SBAS_TEST_ENABLED, new CmdParam(
                isSBASTestEnabled));
        setMtkDataNeeded(MtkModel.DATA_SBAS_TEST_STATUS);
    }

    /**
     * Enable SBAS (DGPS).
     * 
     * @param set
     *            When true, enables SBAS.
     */
    public final void setSBASEnabled(final boolean set) {
        gpsCmd(MtkController.CMD_SET_SBAS_ENABLED, new CmdParam(set));
        setMtkDataNeeded(MtkModel.DATA_SBAS_STATUS);
    }

    /**
     * Set GPS's Datum mode.
     * 
     * @param mode
     *            The datum mode to set.
     */
    public final void setDatumMode(final int mode) {
        mtkC().setDatumMode(mode);
        setMtkDataNeeded(MtkModel.DATA_DATUM_MODE);
    }

    /**
     * Set the DGPS mode to use when SBAS enabled.
     * 
     * @param mode
     *            The mode to use.
     */
    public final void setDGPSMode(final int mode) {
        mtkC().setDGPSMode(mode);
        setMtkDataNeeded(MtkModel.DATA_DGPS_MODE);
    }

    /**
     * Enable the power save mode on the device - this setting is untested.
     * 
     * @param set
     *            If true, enable power save mode.
     */
    public final void setPowerSaveEnabled(final boolean set) {
        gpsCmd(MtkController.CMD_SET_POWERSAVE_ENABLED, new CmdParam(set));
        setMtkDataNeeded(MtkModel.DATA_POWERSAVE_STATUS);
    }

    /**
     * Force the logger to record the current position tagged with the given
     * log reason.
     * 
     * @param value
     *            {@link BT747Constants#RCR_TIME_MASK}
     *            {@link BT747Constants#RCR_SPEED_MASK}
     *            {@link BT747Constants#RCR_DISTANCE_MASK}
     *            {@link BT747Constants#RCR_BUTTON_MASK}
     *            {@link BT747Constants#RCR_APP0_MASK}
     *            {@link BT747Constants#RCR_APP1_MASK}
     *            {@link BT747Constants#RCR_APP2_MASK}
     *            {@link BT747Constants#RCR_APP3_MASK}
     *            {@link BT747Constants#RCR_APP4_MASK}
     *            {@link BT747Constants#RCR_APP5_MASK}
     *            {@link BT747Constants#RCR_APP6_MASK}
     *            {@link BT747Constants#RCR_APP7_MASK}
     *            {@link BT747Constants#RCR_APP8_MASK}
     *            {@link BT747Constants#RCR_APP9_MASK}
     *            {@link BT747Constants#RCR_APPY_MASK}
     *            {@link BT747Constants#RCR_APPZ_MASK}
     *            {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final void logImmediate(final int value) {
        mtkC().logImmediate(value);
    }

    public final void setFixInterval(final int value) {
        if (value != 0) {
            gpsCmd(MtkController.CMD_SET_GPS_FIX_INTERVAL,
                    new CmdParam(value));
            setMtkDataNeeded(MtkModel.DATA_FIX_PERIOD);
        }
    }

    public final void setLogTimeInterval(final int value) {
        gpsCmd(MtkController.CMD_SET_LOG_TIME_INTERVAL, new CmdParam(value));
        // TODO : request time interval
    }

    public final void setLogDistanceInterval(final int value) {
        gpsCmd(MtkController.CMD_SET_LOG_DISTANCE_INTERVAL, new CmdParam(
                value));
        // TODO : request distance interval
    }

    public final void setLogSpeedInterval(final int value) {
        gpsCmd(MtkController.CMD_SET_LOG_SPEED_INTERVAL, new CmdParam(value));
        // TODO : request speed interval
    }

    public final boolean isEnableStoreOK() {
        // TODO: This function serves to enable 'save settings'.
        // should do this through an event to the view.
        return m.mtkModel().isDataOK(
                (MtkModel.C_OK_FIX | MtkModel.C_OK_DGPS | MtkModel.C_OK_SBAS
                        | MtkModel.C_OK_NMEA | MtkModel.C_OK_SBAS_TEST
                        |
                        // MtkModel.C_OK_SBAS_DATUM |
                        MtkModel.C_OK_TIME | MtkModel.C_OK_SPEED
                        | MtkModel.C_OK_DIST | MtkModel.C_OK_FORMAT));
    }

    public final void setStats(final boolean b) {
        getGpsOldC().setStats(b);
    }

    public final void setGpsDecode(final boolean value) {
        setBooleanOpt(AppSettings.DECODEGPS, value);
        getGpsOldC().setGpsDecode(value);
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
        StringBuffer sNMEA = new StringBuffer();
        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            sNMEA.append(m.getNMEAPeriod(i));
        }
        setStringOpt(AppSettings.SETTING1_NMEA, sNMEA.toString());
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
        setMtkDataNeeded(MtkModel.DATA_LOG_TIME_INTERVAL);
        setMtkDataNeeded(MtkModel.DATA_LOG_SPEED_INTERVAL);
        setMtkDataNeeded(MtkModel.DATA_LOG_DISTANCE_INTERVAL);
        setMtkDataNeeded(MtkModel.DATA_LOG_FORMAT);
        setMtkDataNeeded(MtkModel.DATA_FIX_PERIOD);
        setMtkDataNeeded(MtkModel.DATA_SBAS_STATUS);
        setMtkDataNeeded(MtkModel.DATA_SBAS_TEST_STATUS);
        setMtkDataNeeded(MtkModel.DATA_DGPS_MODE);
        setMtkDataNeeded(MtkModel.DATA_LOG_OVERWRITE_STATUS);
        setMtkDataNeeded(MtkModel.DATA_NMEA_OUTPUT_PERIODS);
    }

    public final void setFilters() {
        // TODO : Should schedule this after a while.
        for (int i = m.getLogFiltersAdv().length - 1; i >= 0; i--) {
            final GPSFilterAdvanced filter = m.getLogFiltersAdv()[i];
            filter.setMinRecCount(m.getIntOpt(AppSettings.MIN_RECCOUNT));
            filter.setMaxRecCount(m.getIntOpt(AppSettings.MAX_RECCOUNT));
            filter.setMinSpeed(m.getFloatOpt(AppSettings.MIN_SPEED));
            filter.setMaxSpeed(m.getFloatOpt(AppSettings.MAX_SPEED));
            filter.setMinDist(m.getFloatOpt(AppSettings.MIN_DISTANCE));
            filter.setMaxDist(m.getFloatOpt(AppSettings.MAX_DISTANCE));
            filter
                    .setMaxPDOP((int) (m.getFloatOpt(AppSettings.MAX_PDOP) * Controller.XDOP_FLOAT_TO_INT_100));
            filter
                    .setMaxHDOP((int) (m.getFloatOpt(AppSettings.MAX_HDOP) * Controller.XDOP_FLOAT_TO_INT_100));
            filter
                    .setMaxVDOP((int) (m.getFloatOpt(AppSettings.MAX_VDOP) * Controller.XDOP_FLOAT_TO_INT_100));
            filter.setMinNSAT(m.getIntOpt(AppSettings.MIN_NSAT));
        }
    }

    /**
     * Sets the NMEA string types to write to the NMEA output file format.
     * 
     * @param formatNMEA
     *            Bit format using following bit indexes:<br>
     *            - {@link BT747Constants#NMEA_SEN_GLL_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_RMC_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_VTG_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GGA_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GSA_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GSV_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GRS_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_GST_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MALM_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MEPH_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MDGP_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MDBG_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_ZDA_IDX}<br>
     *            - {@link BT747Constants#NMEA_SEN_MCHN_IDX}<br>
     */
    public final void setNMEAset(final int formatNMEA) {
        m.setNMEAset(formatNMEA);
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

    public void setFloatOpt(final int param, final float value) {
        m.setFloatOpt(param, value);
    }

    public void setStringOpt(final int param, final String value) {
        m.setStringOpt(param, value);
    }

    public final static void addLogFile(final BT747Path path) {
        final LogFileInfo loginfo = new LogFileInfo(path);
        Model.logFiles.addElement(loginfo);
    }

    public final void setAgpsData(final byte[] agpsData) {
        getGpsC().setAgpsData(agpsData);
    }

    public final void sendBT747Exception(final BT747Exception e) {
        m.postEvent(GpsEvent.EXCEPTION, e);
    }

    /**
     * A LocationSender instance which is registered with this instance's
     * Model. Currently there may be only one LocationSender registered with a
     * Model from this Controller class.<br>
     * This instance is remembered here so that it can be used to be removed
     * again from the Model.
     * 
     * @author Florian Unger
     */
    private LocationSender registeredLocSender = null;

    /**
     * Check whether location updates currently are sent out to external
     * servers.
     * 
     * @return true if the location serving is currently active, false
     *         otherwise.
     */
    public boolean isLocationServingActive() {
        return registeredLocSender != null;
    }

    /**
     * If there is no LocationSender yet registered with the Model create such
     * a LocationSender and register it with the Model so that it starts
     * sending out position updates.<br>
     * As usually with Controllers this is an operation which implements the
     * reaction of the application to some user interaction (Although this is
     * called with other ways also). This is useful for example for a type of
     * interaction where a user activates position sending with pressing a
     * button.
     * 
     * <br>author Florian Unger
     */
    public void startGPSPositionServing() {
        if (registeredLocSender == null) {
            LocationSender ls = m.createAndConfigureLocationSender();
            this.getModel().addListener(ls);
            // remember this not before it really is registered with the
            // model.
            this.registeredLocSender = ls;
        }
    }

    /**
     * If there is a LocationSender currently registered as Listener with the
     * Model of this instance then remove it from the Listeners of the Model
     * and set registeredLocSender to null.<br>
     * This is the complementing operation to startGPSPositionServing and may
     * be used just the same way. For example the user might stop position
     * serving by pressing a push button.
     * 
     * <br>author Florian Unger
     */
    public void stopGPSPositionServing() {
        if (registeredLocSender != null) {
            try {
                this.getModel().removeListener(registeredLocSender);
            } catch (Exception e) {
                // Normally we would check whether the instance to be removed
                // from the listeners is register at all (it may have been
                // removed by someone else). If it would not be contained in
                // the listeners we would not try to remove.
                // Unfortunately there is no way in the implementation of
                // Model
                // to check for the existence of an Object in th listeners.
                // So
                // we just try the removing and take any resulting Exception
                // as sign that the Object to remove is not in teh listeners
                // anymore.
            }
            registeredLocSender = null;
        }

    }

}
