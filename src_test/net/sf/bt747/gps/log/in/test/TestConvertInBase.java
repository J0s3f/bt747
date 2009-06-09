/**
 * 
 */
package net.sf.bt747.gps.log.in.test;

import java.io.PipedOutputStream;

import net.sf.bt747.test.IBlue747Model;
import net.sf.bt747.test.MyVirtualPort;
import net.sf.bt747.test.TestModelConnect;
import gps.BT747Constants;
import gps.connection.GPSrxtx;
import gps.log.GPSFilter;
import gps.log.GPSRecord;
import gps.log.TracksAndWayPoints;
import gps.log.in.GPSLogConvertInterface;
import gps.log.out.GPSArray;
import gps.log.out.GPSConversionParameters;
import gps.log.out.GPSKMLFile;

import bt747.model.Model;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.JavaLibImplementation;

/**
 * @author Mario
 * 
 */
public class TestConvertInBase extends junit.framework.TestCase {
    
    /**
     * Initialize the bridge to the platform. Required for BT747 that runs on
     * at least 3 different platforms.
     */
    static {
        /* Get instance of implementation */
        final JavaLibImplementation imp = new net.sf.bt747.j2se.system.J2SEJavaTranslations();
        /* Declare the implementation */
        JavaLibBridge.setJavaLibImplementation(imp);
        /* Set the serial port class instance to use (also system specific). */
    }

    GPSLogConvertInterface lc;
    GPSArray gpsFile; // A GPS file that we can access in the test.

    GPSFilter[] logFilters = { new GPSFilter(), new GPSFilter() };

    /**
     * @param inputConverter
     *                the inputConverter to set
     */
    public void setInputConverter(GPSLogConvertInterface inputConverter) {
        this.lc = inputConverter;
    }

    public void configureGpsArray(GPSArray gpsFile) {
        gpsFile.setWayPointTimeCorrection(0);
        gpsFile.setMaxDiff(300);
        gpsFile.setOverridePreviousTag(true);
        gpsFile.setAddLogConditionInfo(false);
        gpsFile.setImperial(false);
        gpsFile.setRecordNbrInLogs(true);
        gpsFile.setBadTrackColor("0000FF");
        gpsFile.setGoodTrackColor("0000FF");
        gpsFile.setIncludeTrkComment(true);
        gpsFile.setIncludeTrkName(true);
        gpsFile.setFilters(logFilters);
        gpsFile.setOutputFields(GPSRecord
                .getLogFormatRecord((1 << BT747Constants.FMT_UTC_IDX)
                        | (1 << BT747Constants.FMT_VALID_IDX)
                        | (1 << BT747Constants.FMT_LATITUDE_IDX)
                        | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                        | (1 << BT747Constants.FMT_HEIGHT_IDX)
                        | (1 << BT747Constants.FMT_SPEED_IDX)
                        | (1 << BT747Constants.FMT_HEADING_IDX)
                        | (1 << BT747Constants.FMT_DSTA_IDX)
                        | (1 << BT747Constants.FMT_DAGE_IDX)
                        | (1 << BT747Constants.FMT_PDOP_IDX)
                        | (1 << BT747Constants.FMT_HDOP_IDX)
                        | (1 << BT747Constants.FMT_VDOP_IDX)
                        | (1 << BT747Constants.FMT_NSAT_IDX)
                        | (1 << BT747Constants.FMT_SID_IDX)
                        | (1 << BT747Constants.FMT_ELEVATION_IDX)
                        | (1 << BT747Constants.FMT_AZIMUTH_IDX)
                        | (1 << BT747Constants.FMT_SNR_IDX)
                        | (1 << BT747Constants.FMT_RCR_IDX)
                        | (1 << BT747Constants.FMT_MILLISECOND_IDX)
                        | (1 << BT747Constants.FMT_DISTANCE_IDX)));
        gpsFile.setTrackSepTime(600);
        gpsFile.setUserWayPointList(null);
        gpsFile.getParamObject().setBoolParam(
                GPSConversionParameters.TRACK_SPLIT_IF_SMALL_BOOL, false);
        gpsFile.getParamObject().setBoolParam(
                GPSConversionParameters.GPX_LINK_TEXT, false);
        gpsFile.getParamObject().setBoolParam(
                GPSConversionParameters.GPX_1_1, false);
        gpsFile.getParamObject().setParam(
                GPSConversionParameters.GOOGLEMAPKEY_STRING, "");
        gpsFile.getParamObject().setIntParam(
                GPSConversionParameters.NMEA_OUTFIELDS, 0xFF);
        // altMode = GPSKMLFile.CLAMPED_HEIGHT;
        // altMode = GPSKMLFile.RELATIVE_HEIGHT;
        String altMode = GPSKMLFile.ABSOLUTE_HEIGHT;
        gpsFile.getParamObject().setParam(
                GPSConversionParameters.KML_TRACK_ALTITUDE_STRING, altMode);
    }

    /**
     * Sets up the log converter parameters.
     */
    public void converterSetup() {
        lc.setConvertWGS84ToMSL(+1);
        lc.setLoggerType(BT747Constants.GPS_TYPE_DEFAULT);

        gpsFile = new GPSArray();

        configureGpsArray(gpsFile);

        gpsFile.initialiseFile("", "", -1, Model.SPLIT_ONE_FILE);
    }
    
    public TracksAndWayPoints doConvert(String path) {
        int error = -1;
        TracksAndWayPoints result;
        try {
            error = lc.toGPSFile(path,
                    gpsFile, 0);
        } catch (final Throwable e) {
            Generic.debug("During conversion", e);
        }

        if (error != 0) {
//            lastError = error;
//            lastErrorInfo = lc.getErrorInfo();
            result = null;
        } else {
            result = gpsFile.getResult();
        }
        return result;
    }
}
