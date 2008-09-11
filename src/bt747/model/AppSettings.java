package bt747.model;

//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************                              

import gps.BT747Constants;
import gps.convert.Conv;
import moio.util.HashSet;
import moio.util.Iterator;

import bt747.io.File;
import bt747.sys.Convert;
import bt747.sys.Settings;

/**
 * @author Mario De Weerd
 * @author Herbert Geus (initial code for saving settings on WindowsCE)
 */
public class AppSettings {
    private static final int C_PORTNBR_IDX = 0;
    private static final int C_PORTNBR_SIZE = 8;
    private static final int C_BAUDRATE_IDX = C_PORTNBR_IDX + C_PORTNBR_SIZE;
    private static final int C_BAUDRATE_SIZE = 8;
    private static final int C_VERSION_IDX = C_BAUDRATE_IDX + C_BAUDRATE_SIZE;
    private static final int C_VERSION_SIZE = 8;
    private static final int C_BASEDIRPATH_IDX = C_VERSION_IDX + C_VERSION_SIZE;
    private static final int C_BASEDIRPATH_SIZE = 256;
    private static final int C_REPORTFILEBASE_IDX = C_BASEDIRPATH_IDX
            + C_BASEDIRPATH_SIZE;
    private static final int C_REPORTFILEBASE_SIZE = 40;
    private static final int C_LOGFILE_IDX = C_REPORTFILEBASE_IDX
            + C_REPORTFILEBASE_SIZE;
    private static final int C_LOGFILE_SIZE = 40;
    private static final int C_OPENSTARTUP_IDX = C_LOGFILE_IDX + C_LOGFILE_SIZE;
    private static final int C_OPENSTARTUP_SIZE = 40;
    private static final int C_CHUNKSIZE_IDX = C_OPENSTARTUP_IDX
            + C_OPENSTARTUP_SIZE;
    private static final int C_CHUNKSIZE_SIZE = 8;
    private static final int C_DOWNLOADTIMEOUT_IDX = C_CHUNKSIZE_IDX
            + C_CHUNKSIZE_SIZE;
    private static final int C_DOWNLOADTIMEOUT_SIZE = 8;
    private static final int C_CARD_IDX = C_DOWNLOADTIMEOUT_IDX
            + C_DOWNLOADTIMEOUT_SIZE;
    private static final int C_CARD_SIZE = 4;
    private static final int C_TIMEOFFSETHOURS_IDX = C_CARD_IDX + C_CARD_SIZE;
    private static final int C_TIMEOFFSETHOURS_SIZE = 4;
    private static final int C_WAYPT_RCR_IDX = C_TIMEOFFSETHOURS_IDX
            + C_TIMEOFFSETHOURS_SIZE;
    private static final int C_WAYPT_RCR_SIZE = 4;
    private static final int C_WAYPT_VALID_IDX = C_WAYPT_RCR_IDX
            + C_WAYPT_RCR_SIZE;
    private static final int C_WAYPT_VALID_SIZE = 4;
    private static final int C_TRKPT_RCR_IDX = C_WAYPT_VALID_IDX
            + C_WAYPT_VALID_SIZE;
    private static final int C_TRKPT_RCR_SIZE = 4;
    private static final int C_TRKPT_VALID_IDX = C_TRKPT_RCR_IDX
            + C_TRKPT_RCR_SIZE;
    private static final int C_TRKPT_VALID_SIZE = 4;
    private static final int C_ONEFILEPERDAY_IDX = C_TRKPT_VALID_IDX
            + C_TRKPT_VALID_SIZE;
    private static final int C_ONEFILEPERDAY_SIZE = 1;
    private static final int C_WGS84_TO_MSL_IDX = C_ONEFILEPERDAY_IDX
            + C_ONEFILEPERDAY_SIZE;
    private static final int C_WGS84_TO_MSL_SIZE = 4;
    private static final int C_LOGAHEAD_IDX = C_WGS84_TO_MSL_IDX
            + C_WGS84_TO_MSL_SIZE;
    private static final int C_LOGAHEAD_SIZE = 1;
    private static final int C_NMEASET_IDX = C_LOGAHEAD_IDX + C_LOGAHEAD_SIZE;
    private static final int C_NMEASET_SIZE = 8;
    private static final int C_GPXUTC0_IDX = C_NMEASET_IDX + C_NMEASET_SIZE;
    private static final int C_GPXUTC0_SIZE = 1;
    private static final int C_TRKSEP_IDX = C_GPXUTC0_IDX + C_GPXUTC0_SIZE;
    private static final int C_TRKSEP_SIZE = 4;
    private static final int C_ADVFILTACTIVE_IDX = C_TRKSEP_IDX + C_TRKSEP_SIZE;
    private static final int C_ADVFILTACTIVE_SIZE = 1;
    private static final int C_minDist_IDX = C_ADVFILTACTIVE_IDX
            + C_ADVFILTACTIVE_SIZE;
    private static final int C_minDist_SIZE = 8;
    private static final int C_MAX_DISTANCE_IDX = C_minDist_IDX
            + C_minDist_SIZE;
    private static final int C_MAX_DISTANCE_SIZE = 8;
    private static final int C_minSpeed_IDX = C_MAX_DISTANCE_IDX
            + C_MAX_DISTANCE_SIZE;
    private static final int C_minSpeed_SIZE = 8;
    private static final int C_maxSpeed_IDX = C_minSpeed_IDX + C_minSpeed_SIZE;
    private static final int C_maxSpeed_SIZE = 8;
    private static final int C_maxHDOP_IDX = C_maxSpeed_IDX + C_maxSpeed_SIZE;
    private static final int C_maxHDOP_SIZE = 8;
    private static final int C_maxPDOP_IDX = C_maxHDOP_IDX + C_maxHDOP_SIZE;
    private static final int C_maxPDOP_SIZE = 8;
    private static final int C_maxVDOP_IDX = C_maxPDOP_IDX + C_maxPDOP_SIZE;
    private static final int C_maxVDOP_SIZE = 8;
    private static final int C_minRecCount_IDX = C_maxVDOP_IDX + C_maxVDOP_SIZE;
    private static final int C_minRecCount_SIZE = 8;
    private static final int C_maxRecCount_IDX = C_minRecCount_IDX
            + C_minRecCount_SIZE;
    private static final int C_maxRecCount_SIZE = 8;
    private static final int C_minNSAT_IDX = C_maxRecCount_IDX
            + C_maxRecCount_SIZE;
    private static final int C_minNSAT_SIZE = 4;
    private static final int C_GPXTRKSEGBIG_IDX = C_minNSAT_IDX
            + C_minNSAT_SIZE;
    private static final int C_GPXTRKSEGBIG_SIZE = 1;
    private static final int C_DECODEGPS_IDX = C_GPXTRKSEGBIG_IDX
            + C_GPXTRKSEGBIG_SIZE;
    private static final int C_DECODEGPS_SIZE = 4;
    private static final int C_COLOR_INVALIDTRACK_IDX = C_DECODEGPS_IDX
            + C_DECODEGPS_SIZE;
    private static final int C_COLOR_INVALIDTRACK_SIZE = 8;
    private static final int C_ISTRAVERSABLE_IDX = C_COLOR_INVALIDTRACK_IDX
            + C_COLOR_INVALIDTRACK_SIZE;
    private static final int C_ISTRAVERSABLE_SIZE = 4;
    private static final int C_SETTING1_TIME_IDX = C_ISTRAVERSABLE_IDX
            + C_ISTRAVERSABLE_SIZE;
    private static final int C_SETTING1_TIME_SIZE = 8;
    private static final int C_SETTING1_SPEED_IDX = C_SETTING1_TIME_IDX
            + C_SETTING1_TIME_SIZE;
    private static final int C_SETTING1_SPEED_SIZE = 8;
    private static final int C_SETTING1_DIST_IDX = C_SETTING1_SPEED_IDX
            + C_SETTING1_SPEED_SIZE;
    private static final int C_SETTING1_DIST_SIZE = 8;
    private static final int C_SETTING1_FIX_IDX = C_SETTING1_DIST_IDX
            + C_SETTING1_DIST_SIZE;
    private static final int C_SETTING1_FIX_SIZE = 8;
    private static final int C_SETTING1_NMEA_IDX = C_SETTING1_FIX_IDX
            + C_SETTING1_FIX_SIZE;
    private static final int C_SETTING1_NMEA_SIZE = 20;
    private static final int C_SETTING1_DGPS_IDX = C_SETTING1_NMEA_IDX
            + C_SETTING1_NMEA_SIZE;
    private static final int C_SETTING1_DGPS_SIZE = 8;
    private static final int C_SETTING1_TEST_IDX = C_SETTING1_DGPS_IDX
            + C_SETTING1_DGPS_SIZE;
    private static final int C_SETTING1_TEST_SIZE = 2;
    private static final int C_SETTING1_LOG_OVR_IDX = C_SETTING1_TEST_IDX
            + C_SETTING1_TEST_SIZE;
    private static final int C_SETTING1_LOG_OVR_SIZE = 1;
    private static final int C_SETTING1_LOG_FORMAT_IDX = C_SETTING1_LOG_OVR_IDX
            + C_SETTING1_LOG_OVR_SIZE;
    private static final int C_SETTING1_LOG_FORMAT_SIZE = 8;
    private static final int C_SETTING1_SBAS_IDX = C_SETTING1_LOG_FORMAT_IDX
            + C_SETTING1_LOG_FORMAT_SIZE;
    private static final int C_SETTING1_SBAS_SIZE = 1;
    private static final int C_RECORDNBR_IN_LOGS_IDX = C_SETTING1_SBAS_IDX
            + C_SETTING1_SBAS_SIZE;
    private static final int C_RECORDNBR_IN_LOGS_SIZE = 4;
    private static final int C_HOLUX241_IDX = C_RECORDNBR_IN_LOGS_IDX
            + C_RECORDNBR_IN_LOGS_SIZE;
    private static final int C_HOLUX241_SIZE = 1;
    private static final int C_IMPERIAL_IDX = C_HOLUX241_IDX + C_HOLUX241_SIZE;
    private static final int C_IMPERIAL_SIZE = 1;
    private static final int C_FREETEXT_PORT_IDX = C_IMPERIAL_IDX
            + C_IMPERIAL_SIZE;
    private static final int C_FREETEXT_PORT_SIZE = 50;
    private static final int C_BIN_DECODER_IDX = C_FREETEXT_PORT_IDX
            + C_FREETEXT_PORT_SIZE;
    private static final int C_BIN_DECODER_SIZE = 4;
    private static final int C_GPSType_IDX = C_BIN_DECODER_IDX
            + C_BIN_DECODER_SIZE;
    private static final int C_GPSType_SIZE = 1;
    private static final int C_OUTPUTLOGCONDITIONS_IDX = C_GPSType_IDX
            + C_GPSType_SIZE;
    private static final int C_OUTPUTLOGCONDITIONS_SIZE = 1;
    private static final int C_IS_WRITE_TRACKPOINT_COMMENT_IDX = C_OUTPUTLOGCONDITIONS_IDX
            + C_OUTPUTLOGCONDITIONS_SIZE;
    private static final int C_IS_WRITE_TRACKPOINT_COMMENT_SIZE = 4;
    private static final int C_IS_WRITE_TRACKPOINT_NAME_IDX = C_IS_WRITE_TRACKPOINT_COMMENT_IDX
            + C_IS_WRITE_TRACKPOINT_COMMENT_SIZE;
    private static final int C_IS_WRITE_TRACKPOINT_NAME_SIZE = 4;
    private static final int C_FILEFIELDFORMAT_IDX = C_IS_WRITE_TRACKPOINT_NAME_IDX
            + C_IS_WRITE_TRACKPOINT_NAME_SIZE;
    private static final int C_FILEFIELDFORMAT_SIZE = 8;
    private static final int C_STOP_LOG_ON_CONNECT_IDX = C_FILEFIELDFORMAT_IDX
            + C_FILEFIELDFORMAT_SIZE;
    private static final int C_STOP_LOG_ON_CONNECT_SIZE = 1;
    private static final int C_NEXT_IDX = C_STOP_LOG_ON_CONNECT_IDX
            + C_STOP_LOG_ON_CONNECT_SIZE;
    // Next lines just to add new items faster using replace functions
    private static final int C_NEXT_SIZE = 4;
    private static final int C_NEW_NEXT_IDX = C_NEXT_IDX + C_NEXT_SIZE;

    private static final int C_DEFAULT_DEVICE_TIMEOUT = 3500; // ms
    private static final int C_DEFAULT_LOG_REQUEST_AHEAD = 3;

    // Parameter types
    //
    public static final int INT = 1;
    public static final int BOOL = 2;
    public static final int STRING = 3;
    public static final int FLOAT = 4;

    // New method for parameters.

    // List of indexes to parameters.
    // To be used as parameter to functions to get the values.
    public static final int IS_WRITE_TRACKPOINT_COMMENT = 0;
    public static final int IS_WRITE_TRACKPOINT_NAME = 1;
    public static final int OUTPUTLOGCONDITIONS = 2;
    public static final int IMPERIAL = 3;
    /**
     * Param indicating forcing the data interpretation as holux.
     */
    public static final int IS_HOLUXM241 = 4;
    /**
     * Param indicating that the record number for a position is to be written
     * to the log.
     */
    public static final int IS_RECORDNBR_IN_LOGS = 5;

    /**
     * Parameter indicating if on Waba the interface must be traversable.
     */
    public static final int IS_TRAVERSABLE = 6;
    /**
     * Parameter selecting the fields for the output file.
     * 
     * <br>
     * The bits can be defined using a bitwise OR of expressions like<br>
     * (1<< IDX) <br>
     * where IDX is one of the following:<br> -
     * {@link BT747Constants#FMT_UTC_IDX} <br> -
     * {@link BT747Constants#FMT_VALID_IDX} <br> -
     * {@link BT747Constants#FMT_LATITUDE_IDX} <br> -
     * {@link BT747Constants#FMT_LONGITUDE_IDX} <br> -
     * {@link BT747Constants#FMT_HEIGHT_IDX} <br> -
     * {@link BT747Constants#FMT_SPEED_IDX} <br> -
     * {@link BT747Constants#FMT_HEADING_IDX} <br> -
     * {@link BT747Constants#FMT_DSTA_IDX} <br> -
     * {@link BT747Constants#FMT_DAGE_IDX} <br> -
     * {@link BT747Constants#FMT_PDOP_IDX} <br> -
     * {@link BT747Constants#FMT_HDOP_IDX} <br> -
     * {@link BT747Constants#FMT_VDOP_IDX} <br> -
     * {@link BT747Constants#FMT_NSAT_IDX} <br> -
     * {@link BT747Constants#FMT_SID_IDX} <br> -
     * {@link BT747Constants#FMT_ELEVATION_IDX} <br> -
     * {@link BT747Constants#FMT_AZIMUTH_IDX} <br> -
     * {@link BT747Constants#FMT_SNR_IDX} <br> -
     * {@link BT747Constants#FMT_RCR_IDX} <br> -
     * {@link BT747Constants#FMT_MILLISECOND_IDX} <br> -
     * {@link BT747Constants#FMT_DISTANCE_IDX} <br> - <br>
     */
    public static final int FILEFIELDFORMAT = 7;

    /**
     * boolean setting to indicate that logging should be stopped on connect.
     * Application specific.
     */
    public static final int IS_STOP_LOGGING_ON_CONNECT = 8;

    private static final int[][] paramsList =
    // Type, idx, start, size
    {
            { BOOL, IS_WRITE_TRACKPOINT_COMMENT,
                    C_IS_WRITE_TRACKPOINT_COMMENT_IDX,
                    C_IS_WRITE_TRACKPOINT_COMMENT_SIZE },
            { BOOL, IS_WRITE_TRACKPOINT_NAME, C_IS_WRITE_TRACKPOINT_NAME_IDX,
                    C_IS_WRITE_TRACKPOINT_NAME_SIZE },
            { BOOL, OUTPUTLOGCONDITIONS, C_OUTPUTLOGCONDITIONS_IDX,
                    C_OUTPUTLOGCONDITIONS_SIZE },
            { BOOL, IMPERIAL, C_IMPERIAL_IDX, C_IMPERIAL_SIZE },
            { BOOL, IS_HOLUXM241, C_HOLUX241_IDX, C_HOLUX241_SIZE },
            { BOOL, IS_RECORDNBR_IN_LOGS, C_RECORDNBR_IN_LOGS_IDX,
                    C_RECORDNBR_IN_LOGS_SIZE },
            { BOOL, IS_TRAVERSABLE, C_ISTRAVERSABLE_IDX, C_ISTRAVERSABLE_SIZE },
            { INT, FILEFIELDFORMAT, C_FILEFIELDFORMAT_IDX,
                    C_FILEFIELDFORMAT_SIZE },
            // Application parameter
            { BOOL, IS_STOP_LOGGING_ON_CONNECT, C_STOP_LOG_ON_CONNECT_IDX,
                    C_STOP_LOG_ON_CONNECT_SIZE }, };

    private int TYPE_IDX = 0;
    private int PARAM_IDX = 1;
    private int START_IDX = 2;
    private int SIZE_IDX = 3;

    private String baseDirPath;
    private String logFile;
    private String reportFileBase;
    
    public static boolean defaultTraversable = false;
    public static int defaultChunkSize = 0x10000;

    private static boolean solveMacLagProblem = false;

    /**
     * Controller must get settings and call {@link #init()} afterwards before
     * actual use of the model.
     */
    public AppSettings() {
    }

    public final void init() {
        String mVersion;
        int VersionX100 = 0;
        // Sanity check of paramList
        for (int i = 0; i < paramsList.length; i++) {
            if (paramsList[i][PARAM_IDX] != i) {
                bt747.sys.Vm.debug("ASSERT:Problem with param index " + i);
            }
        }

        mVersion = getStringOpt(C_VERSION_IDX, C_VERSION_SIZE);
        if ((mVersion.length() == 4) && (mVersion.charAt(1) == '.')) {
            getSettings();
            VersionX100 = Convert.toInt(mVersion.charAt(0)
                    + mVersion.substring(2, 4));
        }
        updateSettings(VersionX100);
    }

    public static String defaultBaseDirPath = "";

    private void updateSettings(final int versionX100) {
        switch (versionX100) {
        case 0:
            setPortnbr(-1);
            setBaudRate(115200);
            setCard(-1);
            setBaseDirPath(defaultBaseDirPath);
            setLogFileRelPath("BT747log.bin");
            setReportFileBase("GPSDATA");
            setStartupOpenPort(false);
            setChunkSize(defaultChunkSize);
            setDownloadTimeOut(C_DEFAULT_DEVICE_TIMEOUT);
            /* fall through */
        case 1:
            setFilterDefaults();
            /* fall through */
        case 2:
            /* fall through */
            setOutputFileSplitType(0);
            /* fall through */
        case 3:
            setConvertWGS84ToMSL(false);
            /* fall through */
        case 4:
            setLogRequestAhead(C_DEFAULT_LOG_REQUEST_AHEAD);
            /* fall through */
        case 5:
            setNMEAset(0x0002000A);
            /* fall through */
        case 6:
            setGpxUTC0(false);
            /* fall through */
        case 7:
            setTrkSep(60);
            /* fall through */
        case 8:
            setAdvFilterActive(false);
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
            /* fall through */
        case 9:
            setGpxTrkSegWhenBig(true);
            /* fall through */
        case 10:
            setGpsDecode(true);
            /* fall through */
        case 11:
            setColorInvalidTrack("0000FF");
            /* fall through */
        case 12:
            setBooleanOpt(IS_TRAVERSABLE, defaultTraversable);
            /* fall through */
        case 13:
            setBooleanOpt(IS_RECORDNBR_IN_LOGS, false);
            /* fall through */
        case 14:
            setBooleanOpt(IS_HOLUXM241, false);
            /* fall through */
        case 15:
            /* Value interpretation changed */
            setDistConditionSetting1(getDistConditionSetting1() * 10);
            /* fall through */
        case 16:
            setBooleanOpt(IMPERIAL, false);
            /* fall through */
        case 18:
            setFreeTextPort("");
            /* fall through */
        case 19:
            setBinDecoder(0);
            setGPSType(0);
        case 20:
            setBooleanOpt(OUTPUTLOGCONDITIONS, false);
            /* fall through */

        case 21:
            setBooleanOpt(IS_WRITE_TRACKPOINT_COMMENT, true);
            setBooleanOpt(IS_WRITE_TRACKPOINT_NAME, true);
            /* fall through */
        case 22:
            setIntOpt(FILEFIELDFORMAT, -1);
            /* fall through */
        case 23:
            /* Next line is application specific */
            setBooleanOpt(IS_STOP_LOGGING_ON_CONNECT, false);
            /* fall through */

            /* Must be last line in case (not 'default'), sets settings version */
            setStringOpt(0, "0.24", C_VERSION_IDX, C_VERSION_SIZE);
        default:
            // Always force lat and lon and utc and height active on restart for
            // basic users.
            setIntOpt(FILEFIELDFORMAT, getIntOpt(FILEFIELDFORMAT)
                    | (1 << BT747Constants.FMT_LATITUDE_IDX)
                    | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                    | (1 << BT747Constants.FMT_UTC_IDX)
                    | (1 << BT747Constants.FMT_HEIGHT_IDX));
            break;
        }
        getSettings();
    }

    public final void defaultSettings() {
        updateSettings(0);
    }

    private void setFilterDefaults() {
        setTrkPtValid(0xFFFFFFFE);
        setTrkPtRCR(0xFFFFFFFF);
        setWayPtValid(0xFFFFFFFE);
        setWayPtRCR(0x00000008);
    }

    public final void getSettings() {
        // setPortnbr(0);
        // setBaudRate(115200);
        baseDirPath = getStringOpt(C_BASEDIRPATH_IDX, C_BASEDIRPATH_SIZE);
        reportFileBase = getStringOpt(C_REPORTFILEBASE_IDX,
                C_REPORTFILEBASE_SIZE);
        logFile = getStringOpt(C_LOGFILE_IDX, C_LOGFILE_SIZE);
    }

    private final void setOpt(final int eventType, final String src,
            final int idx, final int size) {
        Settings.setAppSettings(Settings.getAppSettings().substring(0, idx)
                + src.substring(0, (src.length() < (size)) ? src.length()
                        : size)
                + Settings.getAppSettings().substring(
                        (src.length() < (size - 1)) ? (idx + src.length())
                                : (idx + size)));
        if (eventType != 0) {
            postEvent(eventType);
        }
    }

    private final void setLocalIntOpt(final int param, final int src,
            final int idx, final int size) {
        setOpt(ModelEvent.SETTING_CHANGE, Convert.unsigned2hex(src, size), idx,
                size);
    }

    private final int getLocalIntOpt(final int idx, final int size) {
        return Conv.hex2Int(getStringOpt(idx, size));
    }

    private final void setLocalBooleanOpt(final int param, final boolean value,
            final int idx, final int size) {
        setStringOpt(ModelEvent.SETTING_CHANGE, (value ? "1" : "0"), idx, size);
    }

    private final boolean getLocalBooleanOpt(final int idx, final int size) {
        return getLocalIntOpt(idx, size) == 1;
    }

    private final void setFloatOpt(final int eventType, final float src,
            final int idx, final int size) {
        setOpt(eventType,
                Convert.unsigned2hex(Convert.toIntBitwise(src), size), idx,
                size);
    }

    private final float getFloatOpt(final int idx, final int size) {
        return Convert.toFloatBitwise(Conv.hex2Int(getStringOpt(idx, size)));
    }

    private final void setStringOpt(final int eventType, final String src,
            final int idx, final int size) {
        Settings.setAppSettings(Settings.getAppSettings().substring(0, idx)
                + src.substring(0, (src.length() < size) ? src.length() : size)
                + ((src.length() < size) ? "\0" : "")
                + ((src.length() < (size - 1)) ? new String(new byte[size
                        - src.length() - 1]) : "")
                + ((Settings.getAppSettings().length() > idx + size) ? Settings
                        .getAppSettings().substring(idx + size,
                                Settings.getAppSettings().length()) : ""));
        if (eventType != 0) {
            postEvent(eventType);
        }
    }

    private final String getStringOpt(final int idx, final int size) {
        if ((idx + size) <= Settings.getAppSettings().length()) {
            String s;
            int i;
            s = Settings.getAppSettings().substring(idx, idx + size);
            if ((i = s.indexOf("\0")) != -1) {
                return s.substring(0, i);
            } else {
                return s;
            }
        } else {
            return "";
        }
    }

    // Next methods can be merged - eventually, setStringOpt should check
    // the param type.

    // the new way of setting parameters - not a method per parameter, but an
    // index ;-).
    public final boolean getBooleanOpt(final int param) {
        if ((param < paramsList.length)
                && (paramsList[param][TYPE_IDX] == BOOL)) {
            return getLocalBooleanOpt(paramsList[param][START_IDX],
                    paramsList[param][SIZE_IDX]);
        } else {
            // TODO: throw something
            bt747.sys.Vm.debug("Invalid parameter index " + param);
            return false;
        }
    }

    protected final void setBooleanOpt(final int param, final boolean value) {
        if ((param < paramsList.length)
                && (paramsList[param][TYPE_IDX] == BOOL)) {
            setLocalBooleanOpt(param, value, paramsList[param][START_IDX],
                    paramsList[param][SIZE_IDX]);
        } else {
            // TODO: throw something
            bt747.sys.Vm.debug("Invalid parameter index " + param);
        }
    }

    public final int getIntOpt(final int param) {
        if ((param < paramsList.length) && (paramsList[param][TYPE_IDX] == INT)) {
            return getLocalIntOpt(paramsList[param][START_IDX],
                    paramsList[param][SIZE_IDX]);
        } else {
            // TODO: throw something
            bt747.sys.Vm.debug("Invalid parameter index " + param);
            return 0;
        }
    }

    protected final void setIntOpt(final int param, final int value) {
        if ((param < paramsList.length) && (paramsList[param][TYPE_IDX] == INT)) {
            setLocalIntOpt(param, value, paramsList[param][START_IDX],
                    paramsList[param][SIZE_IDX]);
        } else {
            // TODO: throw something
            bt747.sys.Vm.debug("Invalid parameter index " + param);
        }
    }

    /**
     * @return Returns the portnbr.
     */
    public final int getPortnbr() {
        return getLocalIntOpt(C_PORTNBR_IDX, C_PORTNBR_SIZE);
    }

    /**
     * @param portnbr
     *            The portnbr to set.
     */
    public final void setPortnbr(final int portnbr) {
        setLocalIntOpt(0, portnbr, C_PORTNBR_IDX, C_PORTNBR_SIZE);
    }

    public final String getFreeTextPort() {
        return getStringOpt(C_FREETEXT_PORT_IDX, C_FREETEXT_PORT_SIZE);
    }

    public final void setFreeTextPort(final String s) {
        setStringOpt(0, s, C_FREETEXT_PORT_IDX, C_FREETEXT_PORT_SIZE);
    }

    /**
     * @return The default baud rate
     */
    public final int getBaudRate() {
        return getLocalIntOpt(C_BAUDRATE_IDX, C_BAUDRATE_SIZE);
    }

    /**
     * @param baudRate
     *            The Baud rate to set as a default.
     */
    public final void setBaudRate(final int baudRate) {
        setLocalIntOpt(0, baudRate, C_BAUDRATE_IDX, C_BAUDRATE_SIZE);
    }

    /**
     * Get the chunk size (or the default).
     * 
     * @return The default chunk size
     */
    public final int getChunkSize() {
        // ChunkSize must be multiple of 2
        int chunkSize = getLocalIntOpt(C_CHUNKSIZE_IDX, C_CHUNKSIZE_SIZE) & 0xFFFFFFFE;
        if (chunkSize < 16) {
            chunkSize = 0x200;
        }
        return chunkSize;
    }

    /**
     * Set the chunk size.
     * 
     * @param chunkSize
     *            The ChunkSize to set as a default.
     */
    public final void setChunkSize(final int chunkSize) {
        setLocalIntOpt(0, chunkSize, C_CHUNKSIZE_IDX, C_CHUNKSIZE_SIZE);
    }

    /**
     * @return The default chunk size
     */
    public final int getDownloadTimeOut() {
        int DownloadTimeOut = getLocalIntOpt(C_DOWNLOADTIMEOUT_IDX,
                C_DOWNLOADTIMEOUT_SIZE);
        if (DownloadTimeOut <= 0) {
            DownloadTimeOut = 0x200;
        }
        return DownloadTimeOut;
    }

    /**
     * @param downloadTimeOut
     *            The DownloadTimeOut to set as a default.
     */
    public final void setDownloadTimeOut(final int downloadTimeOut) {
        setLocalIntOpt(0, downloadTimeOut, C_DOWNLOADTIMEOUT_IDX,
                C_DOWNLOADTIMEOUT_SIZE);
    }

    /**
     * @return The default chunk size
     */
    public final int getCard() {
        int card = getLocalIntOpt(C_CARD_IDX, C_CARD_SIZE);
        if ((card <= 0) || (card >= 255)) {
            card = -1;
        }
        return card;
    }

    /**
     * @param card
     *            The Card to set as a default.
     */
    public final void setCard(final int card) {
        setLocalIntOpt(0, card, C_CARD_IDX, C_CARD_SIZE);
    }

    /**
     * @return The time off set (UTC vs. local time)
     */
    public final int getTimeOffsetHours() {
        int timeOffsetHours = getLocalIntOpt(C_TIMEOFFSETHOURS_IDX,
                C_TIMEOFFSETHOURS_SIZE);
        if (timeOffsetHours > 100) {
            timeOffsetHours -= 0x10000;
        }
        return timeOffsetHours;
    }

    /**
     * @param timeOffsetHours
     *            The TIMEOFFSETHOURS to set as a default.
     */
    public final void setTimeOffsetHours(final int timeOffsetHours) {
        setLocalIntOpt(0, timeOffsetHours, C_TIMEOFFSETHOURS_IDX,
                C_TIMEOFFSETHOURS_SIZE);
    }

    public final boolean getStartupOpenPort() {
        return getLocalBooleanOpt(C_OPENSTARTUP_IDX, C_OPENSTARTUP_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setStartupOpenPort(final boolean value) {
        setLocalBooleanOpt(0, value, C_OPENSTARTUP_IDX, C_OPENSTARTUP_SIZE);
    }

    /**
     * The location of the logFile
     * 
     */
    /**
     * @return Returns the logFile full path.
     */
    public final String getLogFilePath() {
        return baseDirPath + File.separatorStr + logFile;
    }

    public final String getLogFile() {
        // Done this way to avoid 'refresh' of text with stored value.
        return logFile;
    }

    /**
     * @param logFile
     *            The logFile to set.
     */
    public final void setLogFileRelPath(final String logFile) {
        this.logFile = logFile;
        setStringOpt(ModelEvent.LOGFILEPATH_UPDATE, this.logFile,
                C_LOGFILE_IDX, C_LOGFILE_SIZE);
    }

    public final String getBaseDirPath() {
        return baseDirPath;
    }

    public final void setBaseDirPath(final String baseDirPath) {
        this.baseDirPath = baseDirPath;
        setStringOpt(ModelEvent.WORKDIRPATH_UPDATE, this.baseDirPath,
                C_BASEDIRPATH_IDX, C_BASEDIRPATH_SIZE);
    }

    public final String getReportFileBase() {
        return reportFileBase;
    }

    public final void setReportFileBase(final String reportFileBase) {
        this.reportFileBase = reportFileBase;
        setStringOpt(ModelEvent.OUTPUTFILEPATH_UPDATE, this.reportFileBase,
                C_REPORTFILEBASE_IDX, C_REPORTFILEBASE_SIZE);
    }

    public final String getReportFileBasePath() {
        return this.baseDirPath + "/" + reportFileBase;
    }

    public final int getWayPtRCR() {
        return getLocalIntOpt(C_WAYPT_RCR_IDX, C_WAYPT_RCR_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setWayPtRCR(final int value) {
        setLocalIntOpt(0, value, C_WAYPT_RCR_IDX, C_WAYPT_RCR_SIZE);
        postEvent(ModelEvent.WAY_RCR_CHANGE);
    }

    public final int getWayPtValid() {
        return getLocalIntOpt(C_WAYPT_VALID_IDX, C_WAYPT_VALID_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setWayPtValid(final int value) {
        setLocalIntOpt(0, value, C_WAYPT_VALID_IDX, C_WAYPT_VALID_SIZE);
        postEvent(ModelEvent.WAY_VALID_CHANGE);
    }

    public final int getTrkPtRCR() {
        return getLocalIntOpt(C_TRKPT_RCR_IDX, C_TRKPT_RCR_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setTrkPtRCR(final int value) {
        setLocalIntOpt(0, value, C_TRKPT_RCR_IDX, C_TRKPT_RCR_SIZE);
        postEvent(ModelEvent.TRK_RCR_CHANGE);
    }

    public final int getTrkPtValid() {
        return getLocalIntOpt(C_TRKPT_VALID_IDX, C_TRKPT_VALID_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setTrkPtValid(final int value) {
        setLocalIntOpt(0, value, C_TRKPT_VALID_IDX, C_TRKPT_VALID_SIZE);
        postEvent(ModelEvent.TRK_VALID_CHANGE);
    }

    public final int getFileSeparationFreq() {
        return getLocalIntOpt(C_ONEFILEPERDAY_IDX, C_ONEFILEPERDAY_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setOutputFileSplitType(final int value) {
        setLocalIntOpt(0, value, C_ONEFILEPERDAY_IDX, C_ONEFILEPERDAY_SIZE);
    }

    public final boolean isConvertWGS84ToMSL() {
        return getLocalBooleanOpt(C_WGS84_TO_MSL_IDX, C_WGS84_TO_MSL_SIZE);
    }

    /**
     * @param value
     *            true - Setting is to convert the WGS84 height to MSL height.
     */
    public final void setConvertWGS84ToMSL(final boolean value) {
        setLocalBooleanOpt(0, value, C_WGS84_TO_MSL_IDX, C_WGS84_TO_MSL_SIZE);
    }

    public final boolean getAdvFilterActive() {
        return getLocalBooleanOpt(C_ADVFILTACTIVE_IDX, C_ADVFILTACTIVE_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    protected final void setAdvFilterActive(final boolean value) {
        setLocalBooleanOpt(0, value, C_ADVFILTACTIVE_IDX, C_ADVFILTACTIVE_SIZE);
    }

    public final int getLogRequestAhead() {
        return getLocalIntOpt(C_LOGAHEAD_IDX, C_LOGAHEAD_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    protected final void setLogRequestAhead(final int value) {
        setLocalIntOpt(0, value, C_LOGAHEAD_IDX, C_LOGAHEAD_SIZE);
    }

    /**
     * Gets the NMEA string types to write to the NMEA output file format.
     * 
     * Bit format using following bit indexes:<br>-
     * {@link BT747Constants#NMEA_SEN_GLL_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_RMC_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_VTG_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_GGA_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_GSA_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_GSV_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_GRS_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_GST_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_MALM_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_MEPH_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_MDGP_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_MDBG_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_ZDA_IDX}<br>-
     * {@link BT747Constants#NMEA_SEN_MCHN_IDX}<br>
     */

    public final int getNMEAset() {
        return getLocalIntOpt(C_NMEASET_IDX, C_NMEASET_SIZE);
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
    protected final void setNMEAset(final int formatNMEA) {
        setLocalIntOpt(0, formatNMEA, C_NMEASET_IDX, C_NMEASET_SIZE);
    }

    public final boolean getGpxUTC0() {
        return getLocalBooleanOpt(C_GPXUTC0_IDX, C_GPXUTC0_SIZE);
    }

    protected final void setGpxUTC0(final boolean value) {
        setLocalBooleanOpt(0, value, C_GPXUTC0_IDX, C_GPXUTC0_SIZE);
    }

    public final boolean getGpsDecode() {
        return getLocalBooleanOpt(C_DECODEGPS_IDX, C_DECODEGPS_SIZE);
    }

    protected final void setGpsDecode(final boolean value) {
        setLocalBooleanOpt(0, value, C_DECODEGPS_IDX, C_DECODEGPS_SIZE);
    }

    public final boolean getGpxTrkSegWhenBig() {
        return getLocalBooleanOpt(C_GPXTRKSEGBIG_IDX, C_GPXTRKSEGBIG_SIZE);
    }

    protected final void setGpxTrkSegWhenBig(final boolean value) {
        setLocalBooleanOpt(0, value, C_GPXTRKSEGBIG_IDX, C_GPXTRKSEGBIG_SIZE);
    }

    public final int getTrkSep() {
        return getLocalIntOpt(C_TRKSEP_IDX, C_TRKSEP_SIZE);
    }

    protected final void setTrkSep(final int value) {
        setLocalIntOpt(0, value, C_TRKSEP_IDX, C_TRKSEP_SIZE);
    }

    /**
     * @return Returns the maxDist.
     */
    public final float getFilterMaxDist() {
        return getFloatOpt(C_MAX_DISTANCE_IDX, C_MAX_DISTANCE_SIZE);
    }

    /**
     * @param maxDist
     *            The maxDist to setFilter.
     */
    protected final void setFilterMaxDist(final float maxDist) {
        setFloatOpt(0, maxDist, C_MAX_DISTANCE_IDX, C_MAX_DISTANCE_SIZE);
    }

    /**
     * @return Returns the maxHDOP.
     */
    public final float getFilterMaxHDOP() {
        return getFloatOpt(C_maxHDOP_IDX, C_maxHDOP_SIZE);
    }

    /**
     * @param maxHDOP
     *            The maxHDOP to setFilter.
     */
    protected final void setFilterMaxHDOP(final float maxHDOP) {
        setFloatOpt(0, maxHDOP, C_maxHDOP_IDX, C_maxHDOP_SIZE);
    }

    /**
     * @return Returns the maxPDOP.
     */
    public final float getFilterMaxPDOP() {
        return getFloatOpt(C_maxPDOP_IDX, C_maxPDOP_SIZE);
    }

    /**
     * @param maxPDOP
     *            The maxPDOP to setFilter.
     */
    protected final void setFilterMaxPDOP(final float maxPDOP) {
        setFloatOpt(0, maxPDOP, C_maxPDOP_IDX, C_maxPDOP_SIZE);
    }

    /**
     * @return Returns the maxRecCnt.
     */
    public final int getFilterMaxRecCount() {
        return getLocalIntOpt(C_maxRecCount_IDX, C_maxRecCount_SIZE);
    }

    /**
     * @param maxRecCnt
     *            The maxRecCnt to setFilter.
     */
    protected final void setFilterMaxRecCount(final int maxRecCnt) {
        setLocalIntOpt(0, maxRecCnt, C_maxRecCount_IDX, C_maxRecCount_SIZE);
    }

    /**
     * @return Returns the maxSpeed.
     */
    public final float getFilterMaxSpeed() {
        return getFloatOpt(C_maxSpeed_IDX, C_maxSpeed_SIZE);
    }

    /**
     * @param maxSpeed
     *            The maxSpeed to setFilter.
     */
    protected final void setFilterMaxSpeed(final float maxSpeed) {
        setFloatOpt(0, maxSpeed, C_maxSpeed_IDX, C_maxSpeed_SIZE);
    }

    /**
     * @return Returns the maxVDOP.
     */
    public final float getFilterMaxVDOP() {
        return getFloatOpt(C_maxVDOP_IDX, C_maxVDOP_SIZE);
    }

    /**
     * @param maxVDOP
     *            The maxVDOP to setFilter.
     */
    protected final void setFilterMaxVDOP(final float maxVDOP) {
        setFloatOpt(0, maxVDOP, C_maxVDOP_IDX, C_maxVDOP_SIZE);
    }

    /**
     * @return Returns the minDist.
     */
    public final float getFilterMinDist() {
        return getFloatOpt(C_minDist_IDX, C_minDist_SIZE);
    }

    /**
     * @param minDist
     *            The minDist to setFilter.
     */
    protected final void setFilterMinDist(final float minDist) {
        setFloatOpt(0, minDist, C_minDist_IDX, C_minDist_SIZE);
    }

    /**
     * @return Returns the minNSAT.
     */
    public final int getFilterMinNSAT() {
        return getLocalIntOpt(C_minNSAT_IDX, C_minNSAT_SIZE);
    }

    /**
     * @param minNSAT
     *            The minNSAT to setFilter.
     */
    protected final void setFilterMinNSAT(final int minNSAT) {
        setLocalIntOpt(0, minNSAT, C_minNSAT_IDX, C_minNSAT_SIZE);
    }

    /**
     * @return Returns the minRecCnt.
     */
    public final int getFilterMinRecCount() {
        return getLocalIntOpt(C_minRecCount_IDX, C_minRecCount_SIZE);
    }

    /**
     * @param minRecCnt
     *            The minRecCnt to setFilter.
     */
    protected final void setFilterMinRecCount(final int minRecCnt) {
        setLocalIntOpt(0, minRecCnt, C_minRecCount_IDX, C_minRecCount_SIZE);
    }

    /**
     * @return Returns the minSpeed.
     */
    public final float getFilterMinSpeed() {
        return getFloatOpt(C_minSpeed_IDX, C_minSpeed_SIZE);
    }

    /**
     * @param minSpeed
     *            The minSpeed to setFilter.
     */
    protected final void setFilterMinSpeed(final float minSpeed) {
        setFloatOpt(0, minSpeed, C_minSpeed_IDX, C_minSpeed_SIZE);
    }

    public final String getColorInvalidTrack() {
        return getStringOpt(C_COLOR_INVALIDTRACK_IDX, C_COLOR_INVALIDTRACK_SIZE);
    }

    protected final void setColorInvalidTrack(final String colorInvalidTrack) {
        setStringOpt(0, colorInvalidTrack, C_COLOR_INVALIDTRACK_IDX,
                C_COLOR_INVALIDTRACK_SIZE);
    }

    /**
     * @return Returns the solveMacLagProblem.
     */
    public final static boolean isSolveMacLagProblem() {
        return solveMacLagProblem;
    }

    /**
     * @param solveMacLagProblem
     *            The solveMacLagProblem to set.
     */
    protected final static void setSolveMacLagProblem(final boolean arg) {
        solveMacLagProblem = arg;
    }

    protected final void setTimeConditionSetting1(final int value) {
        setLocalIntOpt(0, value, C_SETTING1_TIME_IDX, C_SETTING1_TIME_SIZE);
    }

    public final int getTimeConditionSetting1() {
        return getLocalIntOpt(C_SETTING1_TIME_IDX, C_SETTING1_TIME_SIZE);
    }

    protected final void setSpeedConditionSetting1(final int value) {
        setLocalIntOpt(0, value, C_SETTING1_SPEED_IDX, C_SETTING1_SPEED_SIZE);
    }

    public final int getSpeedConditionSetting1() {
        return getLocalIntOpt(C_SETTING1_SPEED_IDX, C_SETTING1_SPEED_SIZE);
    }

    protected final void setDistConditionSetting1(final int value) {
        setLocalIntOpt(0, value, C_SETTING1_DIST_IDX, C_SETTING1_DIST_SIZE);
    }

    public final int getDistConditionSetting1() {
        return getLocalIntOpt(C_SETTING1_DIST_IDX, C_SETTING1_DIST_SIZE);
    }

    protected final void setFixSetting1(final int value) {
        setLocalIntOpt(0, value, C_SETTING1_FIX_IDX, C_SETTING1_FIX_SIZE);
    }

    public final int getFixSetting1() {
        return getLocalIntOpt(C_SETTING1_FIX_IDX, C_SETTING1_FIX_SIZE);
    }

    protected final void setLogFormatConditionSetting1(final int value) {
        setLocalIntOpt(0, value, C_SETTING1_LOG_FORMAT_IDX,
                C_SETTING1_LOG_FORMAT_SIZE);
    }

    public final int getLogFormatSetting1() {
        return getLocalIntOpt(C_SETTING1_LOG_FORMAT_IDX,
                C_SETTING1_LOG_FORMAT_SIZE);
    }

    protected final void setSBASSetting1(final boolean value) {
        setLocalBooleanOpt(0, value, C_SETTING1_SBAS_IDX, C_SETTING1_SBAS_SIZE);
    }

    public final boolean getSBASSetting1() {
        return getLocalBooleanOpt(C_SETTING1_SBAS_IDX, C_SETTING1_SBAS_SIZE);
    }

    protected final void setDGPSSetting1(final int value) {
        setLocalIntOpt(0, value, C_SETTING1_DGPS_IDX, C_SETTING1_DGPS_SIZE);
    }

    public final int getDPGSSetting1() {
        return getLocalIntOpt(C_SETTING1_DGPS_IDX, C_SETTING1_DGPS_SIZE);
    }

    public final boolean getTestSBASSetting1() {
        return getLocalBooleanOpt(C_SETTING1_TEST_IDX, C_SETTING1_TEST_SIZE);
    }

    protected final void setTestSBASSetting1(final boolean value) {
        setLocalBooleanOpt(0, value, C_SETTING1_TEST_IDX, C_SETTING1_TEST_SIZE);
    }

    public final boolean getLogOverwriteSetting1() {
        return getLocalBooleanOpt(C_SETTING1_LOG_OVR_IDX,
                C_SETTING1_LOG_OVR_SIZE);
    }

    protected final void setLogOverwriteSetting1(final boolean value) {
        setLocalBooleanOpt(0, value, C_SETTING1_LOG_OVR_IDX,
                C_SETTING1_LOG_OVR_SIZE);
    }

    public final String getNMEASetting1() {
        return getStringOpt(C_SETTING1_NMEA_IDX, C_SETTING1_NMEA_SIZE);
    }

    protected final void setNMEASetting1(final String value) {
        setStringOpt(0, value, C_SETTING1_NMEA_IDX, C_SETTING1_NMEA_SIZE);
    }

    public final boolean isStoredSetting1() {
        return getNMEASetting1().length() > 15;
    }

    protected final void setBinDecoder(final int value) {
        setLocalIntOpt(0, value, C_BIN_DECODER_IDX, C_BIN_DECODER_SIZE);
    }

    public final int getBinDecoder() {
        return getLocalIntOpt(C_BIN_DECODER_IDX, C_BIN_DECODER_SIZE);
    }

    public final int getGPSType() {
        return getLocalIntOpt(C_GPSType_IDX, C_GPSType_SIZE);
    }

    protected final void setGPSType(final int value) {
        setLocalIntOpt(0, value, C_GPSType_IDX, C_GPSType_SIZE);
    }

    /**
     * Google map file name (basename).
     */
    public static final String C_GMAP_KEY_FILENAME = "gmapkey.txt";

    /**
     * Look for the google map site key in a file called "gmapkey.txt" Will look
     * in the output dir first, then in the source dir, then in the settings
     * dir.
     * 
     * @return The google map key
     */
    public final String getGoogleMapKey() {
        String path = "";
        String gkey = "";
        int idx;
        boolean notok = true;
        int i = 3;
        while (notok && (i >= 0)) {
            switch (i--) {
            case 0:
                // path = CONFIG_FILE_NAME;
                // break;
            case 1:
                path = getBaseDirPath() + "/";
                break;
            case 2:
                path = getLogFilePath();
                break;
            case 3:
                path = getReportFileBasePath();
                break;
            default:
                break;
            }
            idx = path.lastIndexOf('/');
            if (idx != -1) {
                path = path.substring(0, path.lastIndexOf('/'));
            }
            try {
                File gmap = new File(path + "/" + C_GMAP_KEY_FILENAME,
                        File.READ_ONLY);

                if (gmap.isOpen()) {
                    byte[] b = new byte[100];
                    int len;
                    len = gmap.readBytes(b, 0, 99);
                    gmap.close();
                    if (len != 0) {
                        gkey = new String(b, 0, len);
                        int min;
                        min = gkey.indexOf(10);
                        if (min != 0) {
                            gkey = gkey.substring(0, min);
                        }
                        min = gkey.indexOf(13);
                        if (min != 0) {
                            gkey = gkey.substring(0, min);
                        }
                        notok = false;
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return gkey;
    }

    /*
     * Event posting
     */

    private HashSet listeners = new HashSet();

    /** add a listener to event thrown by this class */
    public final void addListener(final ModelListener l) {
        listeners.add(l);
    }

    public final void removeListener(final ModelListener l) {
        listeners.remove(l);
    }

    protected final void postEvent(final int type, final Object o) {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ModelListener l = (ModelListener) it.next();
            ModelEvent e = new ModelEvent(type, o);
            l.modelEvent(e);
        }
    }

    protected final void postEvent(final int type) {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ModelListener l = (ModelListener) it.next();
            ModelEvent e = new ModelEvent(type, l);
            l.modelEvent(e);
        }
    }

    protected final void postEvent(final ModelEvent e) {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ModelListener l = (ModelListener) it.next();
            l.modelEvent(new ModelEvent(e));
        }
    }

}
