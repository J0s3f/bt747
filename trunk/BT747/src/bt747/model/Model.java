package bt747.model;

import gps.BT747Constants;
import gps.GPSstate;
import gps.connection.GPSrxtx;
import gps.log.GPSFilter;
import gps.log.GPSFilterAdvanced;

import bt747.util.Date;

/**
 * The model in the Model-Controller-View schematic. Information regarding the
 * state of the GPS device or settings must use this model interface.
 * 
 * @author Mario
 * 
 */
public class Model extends AppSettings {

    /**
     * The gpsModel communicates with the GPS device and stores some information
     * regarding the state of the GPS device.
     */
    private GPSstate gpsModel;
    /**
     * The low level communication class with the GPS device. Needed by the
     * gpsModel.
     */
    private GPSrxtx gpsRxTx;

    /*
     * Log conversion related information
     */

    /**
     * Indicate that the log type does not reference any log type.
     */
    public static final int NO_LOG_LOGTYPE = 0;
    /**
     * CSV log type (Comma Separated Values).
     */
    public static final int CSV_LOGTYPE = 1;
    /**
     * GMAP log type (Google Map - html output).
     */
    public static final int GMAP_LOGTYPE = 2;
    /**
     * GPX log type (gpx format).
     */
    public static final int GPX_LOGTYPE = 3;
    /**
     * KML log type ('Google Earth' format).
     */
    public static final int KML_LOGTYPE = 4;
    /**
     * NMEA log type (NMEA strings - text format - similar to GPS output).
     */
    public static final int NMEA_LOGTYPE = 5;
    /**
     * Compe GPS log type (Writes PLT and WPT files).
     */
    public static final int PLT_LOGTYPE = 6;
    /**
     * log type (Writes TRK and WPT files).
     */
    public static final int TRK_LOGTYPE = 7;

    /**
     * Start date for the date filter. 01/01/1983 is the earliest date that can
     * be logged. This ensures that all logged data points are retrieved by
     * default if the date filter is not changed.
     */
    private Date startDate = new Date(1, 1, 1983);
    /**
     * End date for the date filter.
     */
    private Date endDate = new Date();

    /**
     * Indicate which conversion is ongoing. Helps for GUI interface.
     */
    private int lastConversionOngoing = NO_LOG_LOGTYPE;

    /**
     * True when a log conversion is ongoing.
     */
    private boolean conversionOngoing = false;

    /**
     * The number of filters that are needed. Currently 2 filters: trackpoint
     * filter and waypoint filter.
     */
    private static final int C_NBR_FILTERS = 2;
    /**
     * Standard log filters.
     */
    private GPSFilter[] logFilters = new GPSFilter[C_NBR_FILTERS];
    /**
     * Advanced log filters.
     */
    private GPSFilterAdvanced[] logFiltersAdv =
        new GPSFilterAdvanced[C_NBR_FILTERS];

    /**
     * When true, then dynamic download is active.
     */
    private boolean incremental = true; // Incremental download - default

    /**
     * The default constructor of the model.
     */
    public Model() {
        for (int i = 0; i < logFilters.length; i++) {
            logFilters[i] = new GPSFilter();
            logFiltersAdv[i] = new GPSFilterAdvanced();
        }
        gpsRxTx = new GPSrxtx();
        gpsModel = new GPSstate(gpsRxTx);
        // gpsModel.setGPSRxtx(gpsRxTx);
    }

    /**
     * @return The gpsModel instantiation.
     */
    protected final GPSstate gpsModel() {
        return this.gpsModel;
    }

    /**
     * @return The gpsRxTx instantiatiation (low level communication)
     */
    protected final GPSrxtx gpsRxTx() {
        return this.gpsRxTx;
    }

    /**
     * @return The type of the last conversion that was ongoing
     *         lastConversionOngoing
     */
    public final int getLastConversionOngoing() {
        return lastConversionOngoing;
    }

    /**
     * @param pLastConversionOngoing
     *            the lastConversionOngoing to set
     */
    protected final void setLastConversionOngoing(
            final int pLastConversionOngoing) {
        this.lastConversionOngoing = pLastConversionOngoing;
    }

    /**
     * @return the conversionOngoing
     */
    public final boolean isConversionOngoing() {
        return conversionOngoing;
    }

    /**
     * @return the logFilters
     */
    protected final GPSFilter[] getLogFilters() {
        return logFilters;
    }

    /**
     * Get the log point validity mask.
     * 
     * @param logFilterType
     *            The type of log filter that we request the information for
     *            (index of the log filter).
     * 
     * @return The log point validity filter mask. This corresponds to 'no fix',
     *         '2d fix', '3d fix', 'Estimated', ... filtering.
     */
    public final int getValidMask(final int logFilterType) {
        return logFilters[logFilterType].getValidMask();
    }

    /**
     * Get the record reason mask.
     * 
     * @param logFilterType
     *            The type of log filter that we request the information for
     *            (index of the log filter).
     * 
     * @return The record reason filter mask. This corresponds to time, speed,
     *         distance, button, ... record reasons filtering.
     */
    public final int getRcrMask(final int logFilterType) {
        return logFilters[logFilterType].getRcrMask();
    }

    /**
     * Set the standard log filters.
     * 
     * @param logFilterArray
     *            the logFilters to set
     */
    protected final void setLogFilters(final GPSFilter[] logFilterArray) {
        this.logFilters = logFilterArray;
    }

    /**
     * Get the standard log filters.
     * 
     * @return the logFiltersAdv
     */
    protected final GPSFilterAdvanced[] getLogFiltersAdv() {
        return logFiltersAdv;
    }

    /**
     * Set the advanced log filters.
     * 
     * @param advancedLogFiltersArray
     *            the logFiltersAdv to set
     */
    protected final void setLogFiltersAdv(
            final GPSFilterAdvanced[] advancedLogFiltersArray) {
        this.logFiltersAdv = advancedLogFiltersArray;
    }

    /**
     * Get the filter start date.
     * 
     * @return the startDate
     */
    public final Date getStartDate() {
        return startDate;
    }

    /**
     * Set the start date for the filters.
     * 
     * @param filterStartDate
     *            the startDate to set
     */
    protected final void setStartDate(final Date filterStartDate) {
        this.startDate = filterStartDate;
    }

    /**
     * Get the filter end date.
     * 
     * @return the endDate
     */
    public final Date getEndDate() {
        return endDate;
    }

    /**
     * Set the filter end date.
     * 
     * @param filterEndDate
     *            the endDate to set
     */
    protected final void setEndDate(final Date filterEndDate) {
        this.endDate = filterEndDate;
    }

    /**
     * Indicate that the log conversion started for the given log type.
     * 
     * @param outputLogType
     *            The log type for which the log conversion started.
     */
    protected final void logConversionStarted(final int outputLogType) {
        this.lastConversionOngoing = outputLogType;
        this.conversionOngoing = true;
        postEvent(ModelEvent.CONVERSION_STARTED);
    }

    /**
     * Indicate that the log conversion ended for the given log type.
     * 
     * @param outputLogType
     *            The log type for which the log conversion started.
     */
    protected final void logConversionEnded(final int outputLogType) {
        this.conversionOngoing = false;
        postEvent(ModelEvent.CONVERSION_ENDED);
    }

    /**
     * Get the start address for the log download. To be used for the download
     * progress bar.
     * 
     * @return the startAddr
     */
    public final int getStartAddr() {
        return gpsModel.getStartAddr();
    }

    /**
     * Get the end address for the log download. To be used for the download
     * progress bar.
     * 
     * @return the endAddr
     */
    public final int getEndAddr() {
        return gpsModel.getEndAddr();
    }

    /**
     * Get 'download ongoing' status.
     * 
     * @return true if the download is currently ongoing. This is usefull for
     *         the download progress bar.
     */
    public final boolean isDownloadOnGoing() {
        return gpsModel.isDownloadOnGoing();
    }

    /**
     * Get the log address that we are now expecting to receive data for. This
     * is usefull for the download progress bar.
     * 
     * @return the nextReadAddr
     */
    public final int getNextReadAddr() {
        return gpsModel.getNextReadAddr();
    }

    /**
     * Get the 'incremental download' status.
     * 
     * @return true if the Incremental log download is activated.
     */
    public final boolean isIncremental() {
        return incremental;
    }

    /**
     * Set the 'incremental download' configuration.
     * 
     * @param incrementalDownload
     *            true if the log download should be incremental.
     */
    protected final void setIncremental(final boolean incrementalDownload) {
        this.incremental = incrementalDownload;
        postEvent(ModelEvent.INCREMENTAL_CHANGE);
    }

    /**
     * Get the 'logging activation' status of the device.
     * 
     * @return true if the device is currently logging positions to memory.
     */
    public final boolean isLoggingActive() {
        return gpsModel.isLoggingActive;
    }

    /**
     * Get the amount of memory that is filled with data on the device.
     * 
     * @return The amount of memory used in bytes. This includes the memory used
     *         for other information than locations (e.g., headers)
     */
    public final int logMemUsed() {
        return gpsModel.logMemUsed;
    }

    /**
     * Get the amount of memory that is filled with data on the device.
     * 
     * @return The amount of memory used in percent. The percentage represents
     *         the fraction of useful memory. The device may have more memory,
     *         but not all memory can be used to log actual points.
     */
    public final int logMemUsedPercent() {
        return gpsModel.logMemUsedPercent;
    }

    /**
     * Get the number of positions that are currently stored in the memory of
     * the GPS device.
     * 
     * @return The number of logged positions.
     */
    public final int logNbrLogPts() {
        return gpsModel.logNbrLogPts;
    }

    /**
     * Get the SW Logger Version of the MTK device.
     * 
     * @return Version of the MTK Logger SW.
     */
    public final String getMtkLogVersion() {
        return gpsModel.getMtkLogVersion();
    }

    public final String getMainVersion() {
        return gpsModel.getMainVersion();
    }

    public final String getFirmwareVersion() {
        return gpsModel.getFirmwareVersion();
    }

    public final String getModel() {
        return gpsModel.getModel();
    }

    /**
     * Get the manufacturer id of the flash memory.
     * 
     * @return The letter code of the manufacturer.
     */
    public final int getFlashManuProdID() {
        return gpsModel.getFlashManuProdID();
    }

    public final String getFlashDesc() {
        return gpsModel.getFlashDesc();
    }

    public final int getLogFormat() {
        return gpsModel.getLogFormat();
    }

    public final boolean isHolux() {
        return gpsModel.isHolux();
    }

    public final int logMemUsefullSize() {
        return gpsModel.logMemUsefullSize();
    }

    public final int getDtUpdateRate() {
        return gpsModel.getDtUpdateRate();
    }

    public final int getDtGLL_Period() {
        return gpsModel.getDtGLL_Period();
    }

    public final int getDtRMC_Period() {
        return gpsModel.getDtRMC_Period();
    }

    public final int getDtVTG_Period() {
        return gpsModel.getDtVTG_Period();
    }

    public final int getDtGSA_Period() {
        return gpsModel.getDtGSA_Period();
    }

    public final int getDtGSV_Period() {
        return gpsModel.getDtGSV_Period();
    }

    public final int getDtGGA_Period() {
        return gpsModel.getDtGGA_Period();
    }

    public final int getDtZDA_Period() {
        return gpsModel.getDtZDA_Period();
    }

    public final int getDtMCHN_Period() {
        return gpsModel.getDtMCHN_Period();
    }

    public final int getDtBaudRate() {
        return gpsModel.getDtBaudRate();
    }

    public final int getDtUserOptionTimesLeft() {
        return gpsModel.getDtUserOptionTimesLeft();
    }

    public final String getHoluxName() {
        return gpsModel.getHoluxName();
    }

    public final String getBTAddr() {
        return gpsModel.getBtMacAddr();
    }

    public final int getNMEAPeriod(final int i) {
        return gpsModel.NMEA_periods[i];
    }

    public final int getLogTimeInterval() {
        return gpsModel.getLogTimeInterval();
    }

    public final int getLogSpeedInterval() {
        return gpsModel.getLogSpeedInterval();
    }

    public final int getLogDistanceInterval() {
        return gpsModel.getLogDistanceInterval();
    }

    public final int getLogFixPeriod() {
        return gpsModel.getLogFixPeriod();
    }

    public final int getDgpsMode() {
        return gpsModel.getDgpsMode();
    }

    /**
     * Get the devices 'log overwrite' or 'log stop when full' status.
     * 
     * @return true - The device will overwrite data when the logger memory is
     *         full.<br>
     *         false - The device will stop logging data when the logger memory
     *         is full.
     */
    public final boolean isLogFullOverwrite() {
        return gpsModel.isLogFullOverwrite();
    }

    public final boolean isSBASEnabled() {
        return gpsModel.isSBASEnabled();
    }

    public final boolean isSBASTestEnabled() {
        return gpsModel.isSBASTestEnabled();
    }

    public final boolean isPowerSaveEnabled() {
        return gpsModel.isPowerSaveEnabled();
    }

    public final int getDatum() {
        return gpsModel.getDatum();
    }

    /**
     * Get the maximum number of record that can be logged with the provided log
     * format.
     * 
     * @param logFormat
     *            The log format to use to calculate the number of records.
     * @return The number of records that can be logged at most.
     */
    public final int getEstimatedNbrRecords(final int logFormat) {
        int count = 0;
        boolean forHolux;
        // Calculate for a holux either because this is the default setting or
        // because a holux was detected.
        forHolux = (isHolux() && gpsRxTx.isConnected())
                   || getForceHolux241();
        try {
            int size = BT747Constants.logRecordSize(logFormat, forHolux, 12);
            if (forHolux) {
                size += 1;
            } else {
                size += 2;
            }
            if (size != 0) {
                count = logMemUsefullSize() / size;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Get the debug status. This concerns 'high level' debug information.
     * 
     * @return true - Debug is active
     */
    public final boolean isDebug() {
        return gpsModel.isDebug();
    }

    /**
     * Get the connection debug status. This concerns the low level debug
     * information regarding the GPS connection.
     * 
     * @return true - connection debug is active.
     */
    public final boolean isDebugConn() {
        return gpsRxTx.isDebugConn();
    }
}
