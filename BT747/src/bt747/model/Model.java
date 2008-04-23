package bt747.model;

import gps.BT747_dev;
import gps.GPSstate;
import gps.log.GPSFilter;
import gps.log.GPSFilterAdvanced;

import bt747.util.Date;

public class Model extends AppSettings implements gps.settings{

    
    /*
     * Log conversion related information
     */

    public static final int C_NO_LOG = 0;
    public static final int C_CSV_LOG = 1;
    public static final int C_GMAP_LOG = 2;
    public static final int C_GPX_LOG = 3;
    public static final int C_KML_LOG = 4;
    public static final int C_NMEA_LOG = 5;
    public static final int C_PLT_LOG = 6;
    public static final int C_TRK_LOG = 7;

    private Date startDate = new Date(1, 1, 1983);
    private Date endDate = new Date();
    
    private int lastConversionOngoing= C_NO_LOG;
    private boolean conversionOngoing= false;
    
    private static final int C_NBR_FILTERS=2;
    private GPSFilter[] logFilters=new GPSFilter[C_NBR_FILTERS];
    private GPSFilterAdvanced[] logFiltersAdv=new GPSFilterAdvanced[C_NBR_FILTERS];

    private GPSstate gpsModel;
    
    private boolean incremental=true; // Incremental download - default
    
    public Model() {
        for (int i = 0; i < logFilters.length; i++) {
            logFilters[i]=new GPSFilter();
            logFiltersAdv[i]=new GPSFilterAdvanced();
        }
        gpsModel=new GPSstate(this);
    }
    
    public final GPSstate gpsModel()  {
        return this.gpsModel;
    }
    /**
     * @return the lastConversionOngoing
     */
    public final int getLastConversionOngoing() {
        return lastConversionOngoing;
    }
    /**
     * @param lastConversionOngoing the lastConversionOngoing to set
     */
    protected final void setLastConversionOngoing(int lastConversionOngoing) {
        this.lastConversionOngoing = lastConversionOngoing;
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
    
    public int getValidMask(int i) {
        return logFilters[i].getValidMask();
    }

    public int getRcrMask(int i) {
        return logFilters[i].getRcrMask();
    }

    /**
     * @param logFilters the logFilters to set
     */
    protected final void setLogFilters(GPSFilter[] logFilters) {
        this.logFilters = logFilters;
    }
    
    /**
     * @return the logFiltersAdv
     */
    protected final GPSFilterAdvanced[] getLogFiltersAdv() {
        return logFiltersAdv;
    }
    /**
     * @param logFiltersAdv the logFiltersAdv to set
     */
    protected final void setLogFiltersAdv(GPSFilterAdvanced[] logFiltersAdv) {
        this.logFiltersAdv = logFiltersAdv;
    }
    /**
     * @return the startDate
     */
    public final Date getStartDate() {
        return startDate;
    }
    /**
     * @param startDate the startDate to set
     */
    protected final void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    /**
     * @return the endDate
     */
    public final Date getEndDate() {
        return endDate;
    }
    /**
     * @param endDate the endDate to set
     */
    protected final void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public final void logConversionStarted(int type) {
        this.lastConversionOngoing= type;
        this.conversionOngoing= true;
        postEvent(ModelEvent.CONVERSION_STARTED);
    }

    public final void logConversionEnded(int type) {
        this.conversionOngoing= false;
        postEvent(ModelEvent.CONVERSION_ENDED);
    }
    
    
    
    /*
     * Log download related information
     */
    int startAddr;
    int endAddr;
    boolean downloadOnGoing=false;
    int nextReadAddr;

    /**
     * @return the startAddr
     */
    public final int getStartAddr() {
        return startAddr;
    }
    /**
     * @param startAddr the startAddr to set
     */
    public final void setStartAddr(int startAddr) {
        this.startAddr = startAddr;
    }
    /**
     * @return the endAddr
     */
    public final int getEndAddr() {
        return endAddr;
    }
    /**
     * @param endAddr the endAddr to set
     */
    public final void setEndAddr(int endAddr) {
        this.endAddr = endAddr;
    }
    /**
     * @return the downloadOnGoing
     */
    public final boolean isDownloadOnGoing() {
        return downloadOnGoing;
    }
    /**
     * @param downloadOnGoing the downloadOnGoing to set
     */
    public final void setDownloadOnGoing(boolean downloadOnGoing) {
        this.downloadOnGoing = downloadOnGoing;
        postEvent(ModelEvent.DOWNLOAD_PROGRESS_UPDATE);

    }
    /**
     * @return the nextReadAddr
     */
    public final int getNextReadAddr() {
        return nextReadAddr;
    }
    /**
     * @param nextReadAddr the nextReadAddr to set
     */
    public final void setNextReadAddr(int nextReadAddr) {
        this.nextReadAddr = nextReadAddr;
        if(this.downloadOnGoing) {
            postEvent(ModelEvent.DOWNLOAD_PROGRESS_UPDATE);
        }
    }

    public final boolean isIncremental() {
        return incremental;
    }

    protected final void setIncremental(boolean incremental) {
        this.incremental = incremental;
        postEvent(ModelEvent.INCREMENTAL_CHANGE);
    }
    
    
    public final boolean loggingIsActive() {
        return gpsModel.loggingIsActive;
    }

    public final boolean logFullOverwrite() {
        return gpsModel.isLogFullOverwrite();
    }

    public final int logMemUsed() {
        return gpsModel.logMemUsed;
    }

    public final int logMemUsedPercent() {
        return gpsModel.logMemUsedPercent;
    }

     public final int logNbrLogPts() {
        return gpsModel.logNbrLogPts;
    }
     
     
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
     
     public final int getNMEAPeriod(int i) {
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

     public int getDgpsMode() {
         return gpsModel.getDgpsMode();
     }

     public boolean isLogFullOverwrite() {
         return gpsModel.isLogFullOverwrite();
     }
     
     public boolean isSBASEnabled() {
         return gpsModel.isSBASEnabled();
     }
     
     public boolean isSBASTestEnabled() {
         return gpsModel.isSBASTestEnabled();
     }
     
     public boolean isPowerSaveEnabled() {
         return gpsModel.isPowerSaveEnabled();
     }
     
     public int getDatum() {
         return gpsModel.getDatum();
     }

     public int getEstimatedNbrRecords(int logFormat) {
         int count=0;
         try {
             int size=BT747_dev.logRecordSize(logFormat, isHolux(),12);
             if(isHolux()) {
                 size+=1;
             } else {
                 size+=2;
             }
             if(size!=0) {
                 count= logMemUsefullSize()/size;
             }
         } catch (Exception e) {
             e.printStackTrace();
             // TODO: handle exception
         }
         return count;
     }
     
     public final boolean isDebug() {
         return gpsModel.isDebug();
     }
     
     public final boolean isDebugConn() {
         return gpsModel.isDebugConn();
     }
}
