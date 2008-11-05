package bt747.control;

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

import bt747.model.Model;

/**
 * @author Mario De Weerd
 *
 */
public class Controller {
    
    Model m;
    
    public Controller(Model m) {
        this.m = m;
    }
    
    
    /**
     * @param on  True when Imperial units are to be used where possible
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

    public final void writeLog(final int log_type) {
        String ext="";
        GPSFile gpsFile=null;
        GPSLogConvert lc;

        /*
         * Check the input file
         */
        if(m.getLogFilePath().toLowerCase().endsWith(".trl")) {
            lc=new HoluxTrlLogConvert();
//        } else if(m.getLogFilePath().toLowerCase().endsWith(".new")) {
//            // If the new parser is included then we try to use it
//            try {
//                //Class c = Class.forName("gps.parser.NewLogConvert");
//                //lc=(GPSLogConvert)(c.getConstructor().newInstance());
////                if(Class.forName("gps.parser.NewLogConvert")!=null) {
////                    lc=(GPSLogConvert)new gps.parser.NewLogConvert();
////                } else {
//                    lc=new BT747LogConvert();
////                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                lc=new BT747LogConvert();
//            }
        } else if(m.getLogFilePath().toLowerCase().endsWith(".csv")) {
            lc=new CSVLogConvert();
        } else {
            lc=new BT747LogConvert();
            ((BT747LogConvert)lc).setHolux(m.getForceHolux241());
        }
        GPSFilter[] usedFilters;
        if(m.getAdvFilterActive()) {
            usedFilters=m.getLogFiltersAdv();
        } else {
            usedFilters=m.getLogFilters();
        }
        lc.setTimeOffset(m.getTimeOffsetHours()*3600);
        lc.setNoGeoid(m.getNoGeoid());

        switch (log_type) {
        case Model.C_CSV_LOG:
            gpsFile=new GPSCSVFile();
            ext=".csv";
            break;
        case Model.C_TRK_LOG:
            gpsFile=new GPSCompoGPSTrkFile();
            ext=".TRK";
            break;
        case Model.C_KML_LOG:
            gpsFile=new GPSKMLFile();
            ext=".kml";
            break;
        case Model.C_PLT_LOG:
                gpsFile=new GPSPLTFile();
                ext=".plt";
                break;
        case Model.C_GPX_LOG:
            gpsFile=new GPSGPXFile();
            ext=".gpx";
            // Force offset to 0 if selected in menu.
            if(m.getGpxUTC0()) {
                lc.setTimeOffset(0);
            }
            ((GPSGPXFile)gpsFile).setTrkSegSplitOnlyWhenSmall(m.getGpxTrkSegWhenBig());
            break;
        case Model.C_NMEA_LOG:
            gpsFile=new GPSNMEAFile();
            ((GPSNMEAFile)gpsFile).setNMEAoutput(m.getNMEAset());
            ext=".nmea";
            break;
        case Model.C_GMAP_LOG:
            gpsFile=new GPSGmapsHTMLEncodedFile();
            ((GPSGmapsHTMLEncodedFile)gpsFile).setGoogleKeyCode(
                    m.getGoogleMapKey());
            ext=".html";
            break;
        }

        if(gpsFile!=null) {
            m.logConversionStarted(log_type);
            
            gpsFile.setImperial(m.getImperial());
            gpsFile.setRecordNbrInLogs(m.getRecordNbrInLogs());
            gpsFile.setBadTrackColor(m.getColorInvalidTrack());
            for (int i = 0; i < usedFilters.length; i++) {
                usedFilters[i].setStartDate(Conv.dateToUTCepoch1970(m.getStartDate()));
                usedFilters[i].setEndDate(Conv.dateToUTCepoch1970(m.getEndDate())+(24*60*60-1));
            }
            gpsFile.setFilters(usedFilters);
            gpsFile.initialiseFile(m.getReportFileBasePath(), ext, m.getCard(),
                    m.getFileSeparationFreq());
            gpsFile.setTrackSepTime(m.getTrkSep()*60);
            lc.toGPSFile(m.getLogFilePath(),gpsFile,m.getCard());
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
        m.gpsModel().getLogInit(0, /* StartPosition */
                m.gpsModel().logMemUsed - 1, /* EndPosition */
                m.getChunkSize(), /* Size per request */
                m.getLogFilePath(), /* Log file name */
                m.getCard(), /* Card for file operations */
                m.isIncremental() /* Incremental download */);

    }
    
    /***
     * Device state
     * **********************************************/
    
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

    public final void reqLogStatus() {
        m.gpsModel().reqLogStatus();
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


    /**
     * Device connection
     */
    public final void setBluetooth() {
        m.gpsModel().GPS_close();
        m.gpsModel().setBluetooth();
    }

    public final void setUsb() {
        m.gpsModel().GPS_close();
        m.gpsModel().setUsb();
    }

    public final void setPort(final int port) {
        m.gpsModel().GPS_close();
        m.gpsModel().setPort(port);
    }

    public final void setFreeTextPort(final String s) {
        m.gpsModel().GPS_close();
        m.gpsModel().setFreeTextPort(s);
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
}