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
import gps.convert.Conv;

import bt747.sys.Convert;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.Interface;
import bt747.sys.Settings;
import bt747.sys.interfaces.BT747HashSet;

/**
 * @author Mario De Weerd
 */
public class AppSettings {

    /**
     * Google map file name (basename).
     */
    public static final String C_GMAP_KEY_FILENAME = "gmapkey.txt";
    /**
     * Default gps type selection (MTK Logger).
     */
    public static final int GPS_TYPE_DEFAULT = 0;
    /**
     * ITrackU-SirfIII type.
     */
    public static final int GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII = 3;
    /**
     * Holux type.
     */
    public static final int GPS_TYPE_GISTEQ_ITRACKU_NEMERIX = 1;
    /**
     * ITrackU-Phototrackr type.
     */
    public static final int GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR = 2;

    private static final int C_DEFAULT_DEVICE_TIMEOUT = 4000; // ms
    private static final int C_DEFAULT_LOG_REQUEST_AHEAD = 3;

    // Parameter types
    //
    private static final int INT = 1;
    private static final int BOOL = 2;
    private static final int STRING = 3;
    private static final int FLOAT = 4;

    // New method for parameters.

    // List of indexes to parameters.
    // To be used as parameter to functions to get the values.
    public static final int IS_WRITE_TRACKPOINT_COMMENT = 0;
    public static final int IS_WRITE_TRACKPOINT_NAME = 1;
    /**
     * When true, writes the log conditions to certain output formats. A log
     * condition is for example: log a point every second.
     * 
     */
    public static final int OUTPUTLOGCONDITIONS = 2;
    /**
     * True when Imperial units are to be used where possible
     */
    public static final int IMPERIAL = 3;
    /**
     * Param indicating forcing the data interpretation as holux.
     */
    public static final int FORCE_HOLUXM241 = 4;
    /**
     * Param indicating that the record number for a position is to be written
     * to the log.
     */
    public static final int IS_RECORDNBR_IN_LOGS = 5;

    /**
     * Parameter indicating if on Waba the interface must be traversable. (for
     * PDA)
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

    public static final int OUTPUTDIRPATH = 9;
    public static final int REPORTFILEBASE = 10;
    /**
     * Available for the application - must also set {@link #LOGFILEPATH}
     * parameter.
     */
    public static final int LOGFILERELPATH = 11;
    public static final int LOGFILEPATH = 12;
    /**
     * The application's language. Empty if not set by the user.
     */
    public static final int LANGUAGE = 13;
    /**
     * The directory where the images are stored or where images were last
     * chosen from. Can be used freely by the application.
     */
    public static final int IMAGEDIR = 14;
    /**
     * The time offset for files and images.
     */
    public static final int FILETIMEOFFSET = 15;
    /**
     * The serial port number to open.
     */
    public static final int PORTNBR = 16;
    public static final int BAUDRATE = 17;
    /**
     * Select a port by its 'path' (/dev/usb9 for example or /dev/com1.
     */
    public static final int FREETEXTPORT = 18;
    /**
     * Indicates if the port must be opened at startup of the application.
     */
    public static final int OPENPORTATSTARTUP = 19;
    /**
     * @return The GPS time offset (UTC vs. local time). (used in adjusting
     *         the time in the output formats).
     */
    public static final int GPSTIMEOFFSETHOURS = 20;
    /**
     * When true, will override already set positions while tagging.
     */
    public static final int TAG_OVERRIDEPOSITIONS = 21;

    /**
     * Max time difference between GPS time and file time to tag the file with
     * the position.
     */
    public static final int TAG_MAXTIMEDIFFERENCE = 22;

    /**
     * Set the gpsType used for log conversion and some other operations
     * (needed in cases where the type can not be automatically detected).
     * 
     * @param gpsType
     *                A value out of: - {@link #GPS_TYPE_DEFAULT}<br> -
     *                {@link #GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII}<br> -
     *                {@link #GPS_TYPE_GISTEQ_ITRACKU_NEMERIX}<br> -
     *                {@link #GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR}<br>
     */
    public static final int GPSTYPE = 23;
    /**
     * When true logging is disabled during download.
     */
    public static final int DISABLELOGDURINGDOWNLOAD = 24;

    /**
     * The directory where the map cache is to be stored.
     */
    public static final int MAPCACHEDIRECTORY = 25;

    /**
     * Version of the parameters. Do not change this!
     */
    public static final int VERSION = 26;

    /**
     * The type of the map to show (certain apps).
     */
    public static final int MAPTYPE = 27;

    /**
     * Look for the google map site key in a file called "gmapkey.txt" Will
     * look in the output dir first, then in the source dir, then in the
     * settings dir.
     * 
     * @return The google map key
     */
    public static final int GOOGLEMAPKEY = 28;

    /**
     * Stored setting for NMEA data.
     */
    public static final int SETTING1_NMEA = 29;

    public final static int SETTING1_TIME = 30;
    public final static int SETTING1_SPEED = 31;
    public final static int SETTING1_DIST = 32;
    public final static int SETTING1_FIX = 33;
    public final static int SETTING1_DGPS = 34;
    public final static int SETTING1_TEST = 35;
    public final static int SETTING1_LOG_OVR = 36;
    public final static int SETTING1_LOG_FORMAT = 37;
    public final static int SETTING1_SBAS = 38;
    public final static int ADVFILTACTIVE = 39;
    public final static int GPXUTC0 = 40;
    public final static int DECODEGPS = 41;
    public final static int GPXTRKSEGBIG = 42;
    public final static int COLOR_VALIDTRACK = 43;
    public final static int COLOR_INVALIDTRACK = 44;
    public final static int TAGGEDFILE_TEMPLATE = 45;
    public final static int KML_ALTITUDEMODE = 46;
    /**
     * Set the track separation time. When two positions are separated by this
     * time or more, a track separation is inserted.
     * 
     * @param value
     *                Time in minutes for a track separation.
     */
    public final static int TRKSEP = 47;
    /**
     * Number of chunks to read ahead of time
     */
    public final static int LOGAHEAD = 48;
    /**
     * Enable adding links to GPX data output.
     */
    public final static int GPX_LINK_INFO = 49;
    public final static int IS_GPX_1_1 = 50;
    /** Type of device to download from - used in GUI */
    public final static int DOWNLOAD_DEVICE = 51;

    private final static int TYPE_IDX = 0;
    private final static int PARAM_IDX = 1;
    private final static int START_IDX = 2;
    private final static int SIZE_IDX = 3;

    private static boolean defaultTraversable = false;
    private static int defaultChunkSize = 0x10000;

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
        for (int i = 0; i < AppSettings.paramsList.length; i++) {
            if (AppSettings.paramsList[i][AppSettings.PARAM_IDX] != i) {
                Generic.debug("ASSERT:Problem with param index " + i);
            }
        }

        mVersion = getStringOpt(AppSettings.VERSION);
        if ((mVersion.length() == 4) && (mVersion.charAt(1) == '.')) {
            VersionX100 = Convert.toInt(mVersion.charAt(0)
                    + mVersion.substring(2, 4));
        }
        updateSettings(VersionX100);
    }

    private static String defaultBaseDirPath = "";

    private void updateSettings(final int versionX100) {
        switch (versionX100) {
        case 0:
            setIntOpt(AppSettings.PORTNBR, -1);
            setIntOpt(AppSettings.BAUDRATE, 115200);
            setCard(-1);
            setStringOpt(AppSettings.OUTPUTDIRPATH,
                    AppSettings.defaultBaseDirPath);
            setStringOpt(AppSettings.LOGFILERELPATH, "BT747log.bin");
            setStringOpt(AppSettings.REPORTFILEBASE, "GPSDATA");
            setBooleanOpt(AppSettings.OPENPORTATSTARTUP, false);
            setChunkSize(AppSettings.defaultChunkSize);
            setDownloadTimeOut(AppSettings.C_DEFAULT_DEVICE_TIMEOUT);
            /* fall through */
        case 1:
            setFilterDefaults();
            /* fall through */
        case 2:
            /* fall through */
            setOutputFileSplitType(0);
            /* fall through */
        case 3:
            setHeightConversionMode(AppSettings.HEIGHT_AUTOMATIC);
            /* fall through */
        case 4:
            setIntOpt(AppSettings.LOGAHEAD,
                    AppSettings.C_DEFAULT_LOG_REQUEST_AHEAD);
            /* fall through */
        case 5:
            setNMEAset(0x0002000A);
            /* fall through */
        case 6:
            setBooleanOpt(AppSettings.GPXUTC0, false);
            /* fall through */
        case 7:
            setIntOpt(AppSettings.TRKSEP, 60);
            /* fall through */
        case 8:
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
            /* fall through */
        case 9:
            setBooleanOpt(AppSettings.GPXTRKSEGBIG, true);
            /* fall through */
        case 10:
            setBooleanOpt(AppSettings.DECODEGPS, true);
            /* fall through */
        case 11:
            setStringOpt(AppSettings.COLOR_INVALIDTRACK, "0000FF");
            /* fall through */
        case 12:
            setBooleanOpt(AppSettings.IS_TRAVERSABLE,
                    AppSettings.defaultTraversable);
            /* fall through */
        case 13:
            setBooleanOpt(AppSettings.IS_RECORDNBR_IN_LOGS, false);
            /* fall through */
        case 14:
            setBooleanOpt(AppSettings.FORCE_HOLUXM241, false);
            /* fall through */
        case 15:
            /* Value interpretation changed */
            setIntOpt(AppSettings.SETTING1_DIST,
                    getIntOpt(AppSettings.SETTING1_DIST) * 10);
            /* fall through */
        case 16:
            setBooleanOpt(AppSettings.IMPERIAL, false);
            /* fall through */
        case 18:
            setStringOpt(AppSettings.FREETEXTPORT, "");
            /* fall through */
        case 19:
            // setBinDecoder(0); // No longer used
            setIntOpt(AppSettings.GPSTYPE, 0);
        case 20:
            setBooleanOpt(AppSettings.OUTPUTLOGCONDITIONS, false);
            /* fall through */

        case 21:
            setBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_COMMENT, true);
            setBooleanOpt(AppSettings.IS_WRITE_TRACKPOINT_NAME, true);
            /* fall through */
        case 22:
            setIntOpt(AppSettings.FILEFIELDFORMAT, -1);
            /* fall through */
        case 23:
            /* Next line is application specific */
            setBooleanOpt(AppSettings.IS_STOP_LOGGING_ON_CONNECT, false);
            /* fall through */

            /*
             * Must be last line in case (not 'default'), sets settings
             * version
             */
        case 24:
            setStringOpt(AppSettings.COLOR_VALIDTRACK, "0000FF");
            /* fall through */

        case 25:
            setStringOpt(AppSettings.LOGFILEPATH,
                    getStringOpt(AppSettings.OUTPUTDIRPATH)
                            + bt747.sys.File.separatorStr
                            + getStringOpt(AppSettings.LOGFILERELPATH));
            /* fall through */

        case 26:
            setStringOpt(AppSettings.LANGUAGE, "");
            /* fall through */
        case 27:
            setStringOpt(AppSettings.IMAGEDIR,
                    getStringOpt(AppSettings.LOGFILEPATH));
            /* fall through */
        case 28:
            setIntOpt(AppSettings.FILETIMEOFFSET, 0);
            /* fall through */
        case 29:
            setBooleanOpt(AppSettings.TAG_OVERRIDEPOSITIONS, false);
            setIntOpt(AppSettings.TAG_MAXTIMEDIFFERENCE, 300);
            /* fall through */
        case 30:
            setBooleanOpt(AppSettings.DISABLELOGDURINGDOWNLOAD, true);
            /* fall through */
        case 31:
            setStringOpt(AppSettings.MAPCACHEDIRECTORY, "/temp/mapcache");
            /* fall through */
        case 32:
            setIntOpt(AppSettings.MAPTYPE, 0);
            /* fall through */
        case 33:
            setStringOpt(AppSettings.TAGGEDFILE_TEMPLATE, "%p%f_tagged%e");
            /* fall through */
        case 34:
            setIntOpt(AppSettings.KML_ALTITUDEMODE, 0);
            /* fall through */
        case 35:
            setBooleanOpt(AppSettings.GPX_LINK_INFO, true);
            setBooleanOpt(AppSettings.IS_GPX_1_1, false);
            /* fall through */
        case 36:
            setIntOpt(AppSettings.DOWNLOAD_DEVICE, 0);
            setStringOpt(AppSettings.VERSION, "0.37");
            /* fall through */
        default:
            // Always force lat and lon and utc and height active on restart
            // for
            // basic users.
            setIntOpt(AppSettings.FILEFIELDFORMAT,
                    getIntOpt(AppSettings.FILEFIELDFORMAT)
                            | (1 << BT747Constants.FMT_LATITUDE_IDX)
                            | (1 << BT747Constants.FMT_LONGITUDE_IDX)
                            | (1 << BT747Constants.FMT_UTC_IDX)
                            | (1 << BT747Constants.FMT_HEIGHT_IDX));
            break;
        }
        // Generic.debug(
        // "File field selection is "
        // + bt747.sys.Convert.unsigned2hex(
        // getIntOpt(FILEFIELDFORMAT), 8), null);
    }

    public final void defaultSettings() {
        updateSettings(0);
    }

    // Next methods can be merged - eventually, setStringOpt should check
    // the param type.

    // the new way of setting parameters - not a method per parameter, but an
    // index ;-).
    public final boolean getBooleanOpt(final int param) {
        if ((param < AppSettings.paramsList.length)
                && (AppSettings.paramsList[param][AppSettings.TYPE_IDX] == AppSettings.BOOL)) {
            return getLocalIntOpt(
                    AppSettings.paramsList[param][AppSettings.START_IDX],
                    AppSettings.paramsList[param][AppSettings.SIZE_IDX]) == 1;
        } else {
            // TODO: throw something
            Generic.debug("Invalid parameter index " + param);
            return false;
        }
    }

    protected final void setBooleanOpt(final int param, final boolean value) {
        if ((param < AppSettings.paramsList.length)
                && (AppSettings.paramsList[param][AppSettings.TYPE_IDX] == AppSettings.BOOL)) {
            setStringOpt(param, (value ? "1" : "0"),
                    AppSettings.paramsList[param][AppSettings.START_IDX],
                    AppSettings.paramsList[param][AppSettings.SIZE_IDX]);
        } else {
            // TODO: throw something
            Generic.debug("Invalid parameter index " + param);
        }
    }

    public final int getIntOpt(final int param) {
        // if (param == FILEFIELDFORMAT) {
        // Generic.debug("File field selection is "
        // + bt747.sys.Convert.unsigned2hex(
        // getLocalIntOpt(paramsList[param][START_IDX],
        // paramsList[param][SIZE_IDX]),8), null);
        //
        // }
        if ((param < AppSettings.paramsList.length)
                && (AppSettings.paramsList[param][AppSettings.TYPE_IDX] == AppSettings.INT)) {
            return getLocalIntOpt(
                    AppSettings.paramsList[param][AppSettings.START_IDX],
                    AppSettings.paramsList[param][AppSettings.SIZE_IDX]);
        } else {
            // TODO: throw something
            Generic.debug("Invalid parameter @ index " + param);
            return 0;
        }
    }

    protected final void setIntOpt(final int param, final int value) {
        // if (param == FILEFIELDFORMAT) {
        // Generic.debug("File field selection is "
        // + bt747.sys.Convert.unsigned2hex(
        // getIntOpt(FILEFIELDFORMAT), 8) + " to "
        // + bt747.sys.Convert.unsigned2hex(value, 8), null);
        //
        // }
        if ((param < AppSettings.paramsList.length)
                && (AppSettings.paramsList[param][AppSettings.TYPE_IDX] == AppSettings.INT)) {
            setLocalIntOpt(param, value,
                    AppSettings.paramsList[param][AppSettings.START_IDX],
                    AppSettings.paramsList[param][AppSettings.SIZE_IDX]);
        } else {
            // TODO: throw something
            Generic.debug("Invalid parameter index " + param);
        }
    }

    public final String getStringOpt(final int param) {
        switch (param) {
        case GOOGLEMAPKEY:
            return getGoogleMapKeyInternal();

        default:
            break;
        }
        if ((param < AppSettings.paramsList.length)
                && (AppSettings.paramsList[param][AppSettings.TYPE_IDX] == AppSettings.STRING)) {
            return getStringOpt(
                    AppSettings.paramsList[param][AppSettings.START_IDX],
                    AppSettings.paramsList[param][AppSettings.SIZE_IDX]);
        } else {
            // TODO: throw something
            Generic.debug("Invalid parameter index " + param);
            return null;
        }
    }

    protected final void setStringOpt(final int param, final String value) {
        if ((param < AppSettings.paramsList.length)
                && (AppSettings.paramsList[param][AppSettings.TYPE_IDX] == AppSettings.STRING)) {
            setStringOpt(param, value,
                    AppSettings.paramsList[param][AppSettings.START_IDX],
                    AppSettings.paramsList[param][AppSettings.SIZE_IDX]);
        } else {
            // TODO: throw something
            Generic.debug("Invalid parameter index " + param);
        }
    }

    /**
     * Get the chunk size (or the default).
     * 
     * @return The default chunk size
     */
    public final int getChunkSize() {
        // ChunkSize must be multiple of 2
        int chunkSize = getLocalIntOpt(AppSettings.C_CHUNKSIZE_IDX,
                AppSettings.C_CHUNKSIZE_SIZE) & 0xFFFFFFFE;
        if (chunkSize < 16) {
            chunkSize = 0x200;
        }
        return chunkSize;
    }

    /**
     * Set the chunk size.
     * 
     * @param chunkSize
     *                The ChunkSize to set as a default.
     */
    public final void setChunkSize(final int chunkSize) {
        setLocalIntOpt(0, chunkSize, AppSettings.C_CHUNKSIZE_IDX,
                AppSettings.C_CHUNKSIZE_SIZE);
    }

    /**
     * @return The default chunk size
     */
    public final int getDownloadTimeOut() {
        int DownloadTimeOut = getLocalIntOpt(
                AppSettings.C_DOWNLOADTIMEOUT_IDX,
                AppSettings.C_DOWNLOADTIMEOUT_SIZE);
        if (DownloadTimeOut <= 0) {
            DownloadTimeOut = 0x200;
        }
        return DownloadTimeOut;
    }

    /**
     * @param downloadTimeOut
     *                The DownloadTimeOut to set as a default.
     */
    public final void setDownloadTimeOut(final int downloadTimeOut) {
        setLocalIntOpt(0, downloadTimeOut, AppSettings.C_DOWNLOADTIMEOUT_IDX,
                AppSettings.C_DOWNLOADTIMEOUT_SIZE);
    }

    /**
     * @return The default chunk size
     */
    public final int getCard() {
        int card = getLocalIntOpt(AppSettings.C_CARD_IDX,
                AppSettings.C_CARD_SIZE);
        if ((card <= 0) || (card >= 255)) {
            card = -1;
        }
        return card;
    }

    /**
     * @param card
     *                The Card to set as a default.
     */
    public final void setCard(final int card) {
        setLocalIntOpt(0, card, AppSettings.C_CARD_IDX,
                AppSettings.C_CARD_SIZE);
    }

    public final String getReportFileBasePath() {
        return getStringOpt(AppSettings.OUTPUTDIRPATH) + "/"
                + getStringOpt(AppSettings.REPORTFILEBASE);
    }

    public final int getWayPtRCR() {
        return getLocalIntOpt(AppSettings.C_WAYPT_RCR_IDX,
                AppSettings.C_WAYPT_RCR_SIZE);
    }

    /**
     * @param value
     *                The default value for opening the port.
     */
    protected final void setWayPtRCR(final int value) {
        setLocalIntOpt(0, value, AppSettings.C_WAYPT_RCR_IDX,
                AppSettings.C_WAYPT_RCR_SIZE);
        postEvent(ModelEvent.WAY_RCR_CHANGE);
    }

    public final int getWayPtValid() {
        return getLocalIntOpt(AppSettings.C_WAYPT_VALID_IDX,
                AppSettings.C_WAYPT_VALID_SIZE);
    }

    /**
     * @param value
     *                The default value for opening the port.
     */
    protected final void setWayPtValid(final int value) {
        setLocalIntOpt(0, value, AppSettings.C_WAYPT_VALID_IDX,
                AppSettings.C_WAYPT_VALID_SIZE);
        postEvent(ModelEvent.WAY_VALID_CHANGE);
    }

    public final int getTrkPtRCR() {
        return getLocalIntOpt(AppSettings.C_TRKPT_RCR_IDX,
                AppSettings.C_TRKPT_RCR_SIZE);
    }

    /**
     * @param value
     *                The default value for opening the port.
     */
    protected final void setTrkPtRCR(final int value) {
        setLocalIntOpt(0, value, AppSettings.C_TRKPT_RCR_IDX,
                AppSettings.C_TRKPT_RCR_SIZE);
        postEvent(ModelEvent.TRK_RCR_CHANGE);
    }

    public final int getTrkPtValid() {
        return getLocalIntOpt(AppSettings.C_TRKPT_VALID_IDX,
                AppSettings.C_TRKPT_VALID_SIZE);
    }

    /**
     * @param value
     *                The default value for opening the port.
     */
    protected final void setTrkPtValid(final int value) {
        setLocalIntOpt(0, value, AppSettings.C_TRKPT_VALID_IDX,
                AppSettings.C_TRKPT_VALID_SIZE);
        postEvent(ModelEvent.TRK_VALID_CHANGE);
    }

    /**
     * The way we split the input track:<br>
     * ONE_FILE = 0<br>
     * ONE_FILE_PER_DAY = 1<br>
     * ONE_FILE_PER_TRACK = 2
     * 
     * @return Current setting.
     */
    public final int getOutputFileSplitType() {
        return getLocalIntOpt(AppSettings.C_ONEFILEPERDAY_IDX,
                AppSettings.C_ONEFILEPERDAY_SIZE);
    }

    /**
     * The way we split the input track:<br>
     * ONE_FILE = 0<br>
     * ONE_FILE_PER_DAY = 1<br>
     * ONE_FILE_PER_TRACK = 2
     * 
     * @param value
     *                New setting
     */
    protected final void setOutputFileSplitType(final int value) {
        setLocalIntOpt(0, value, AppSettings.C_ONEFILEPERDAY_IDX,
                AppSettings.C_ONEFILEPERDAY_SIZE);
    }

    public final static int HEIGHT_NOCHANGE = 0;
    public final static int HEIGHT_WGS84_TO_MSL = 1;
    public final static int HEIGHT_AUTOMATIC = 2;
    public final static int HEIGHT_MSL_TO_WGS84 = 3;

    /**
     * 
     * @return height conversion mode.
     * @see #HEIGHT_AUTOMATIC<br>
     * @see #HEIGHT_MSL<br>
     * @see #HEIGHT_NOCHANGE
     */
    public final int getHeightConversionMode() {
        return getLocalIntOpt(AppSettings.C_WGS84_TO_MSL_IDX,
                AppSettings.C_WGS84_TO_MSL_SIZE);
    }

    /**
     * @param value
     *                {@link #HEIGHT_WGS84_TO_MSL} - Setting is to convert the
     *                WGS84 height to MSL height.
     */
    public final void setHeightConversionMode(final int value) {
        setLocalIntOpt(0, value, AppSettings.C_WGS84_TO_MSL_IDX,
                AppSettings.C_WGS84_TO_MSL_SIZE);
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
        return getLocalIntOpt(AppSettings.C_NMEASET_IDX,
                AppSettings.C_NMEASET_SIZE);
    }

    /**
     * Sets the NMEA string types to write to the NMEA output file format.
     * 
     * @param formatNMEA
     *                Bit format using following bit indexes:<br>-
     *                {@link BT747Constants#NMEA_SEN_GLL_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_RMC_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_VTG_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_GGA_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_GSA_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_GSV_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_GRS_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_GST_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_MALM_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_MEPH_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_MDGP_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_MDBG_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_ZDA_IDX}<br>-
     *                {@link BT747Constants#NMEA_SEN_MCHN_IDX}<br>
     */
    protected final void setNMEAset(final int formatNMEA) {
        setLocalIntOpt(0, formatNMEA, AppSettings.C_NMEASET_IDX,
                AppSettings.C_NMEASET_SIZE);
    }

    /**
     * @return Returns the maxDist.
     */
    public final float getFilterMaxDist() {
        return getFloatOpt(AppSettings.C_MAX_DISTANCE_IDX,
                AppSettings.C_MAX_DISTANCE_SIZE);
    }

    /**
     * @param maxDist
     *                The maxDist to setFilter.
     */
    protected final void setFilterMaxDist(final float maxDist) {
        setFloatOpt(0, maxDist, AppSettings.C_MAX_DISTANCE_IDX,
                AppSettings.C_MAX_DISTANCE_SIZE);
    }

    /**
     * @return Returns the maxHDOP.
     */
    public final float getFilterMaxHDOP() {
        return getFloatOpt(AppSettings.C_maxHDOP_IDX,
                AppSettings.C_maxHDOP_SIZE);
    }

    /**
     * @param maxHDOP
     *                The maxHDOP to setFilter.
     */
    protected final void setFilterMaxHDOP(final float maxHDOP) {
        setFloatOpt(0, maxHDOP, AppSettings.C_maxHDOP_IDX,
                AppSettings.C_maxHDOP_SIZE);
    }

    /**
     * @return Returns the maxPDOP.
     */
    public final float getFilterMaxPDOP() {
        return getFloatOpt(AppSettings.C_maxPDOP_IDX,
                AppSettings.C_maxPDOP_SIZE);
    }

    /**
     * @param maxPDOP
     *                The maxPDOP to setFilter.
     */
    protected final void setFilterMaxPDOP(final float maxPDOP) {
        setFloatOpt(0, maxPDOP, AppSettings.C_maxPDOP_IDX,
                AppSettings.C_maxPDOP_SIZE);
    }

    /**
     * @return Returns the maxRecCnt.
     */
    public final int getFilterMaxRecCount() {
        return getLocalIntOpt(AppSettings.C_maxRecCount_IDX,
                AppSettings.C_maxRecCount_SIZE);
    }

    /**
     * @param maxRecCnt
     *                The maxRecCnt to setFilter.
     */
    protected final void setFilterMaxRecCount(final int maxRecCnt) {
        setLocalIntOpt(0, maxRecCnt, AppSettings.C_maxRecCount_IDX,
                AppSettings.C_maxRecCount_SIZE);
    }

    /**
     * @return Returns the maxSpeed.
     */
    public final float getFilterMaxSpeed() {
        return getFloatOpt(AppSettings.C_maxSpeed_IDX,
                AppSettings.C_maxSpeed_SIZE);
    }

    /**
     * @param maxSpeed
     *                The maxSpeed to setFilter.
     */
    protected final void setFilterMaxSpeed(final float maxSpeed) {
        setFloatOpt(0, maxSpeed, AppSettings.C_maxSpeed_IDX,
                AppSettings.C_maxSpeed_SIZE);
    }

    /**
     * @return Returns the maxVDOP.
     */
    public final float getFilterMaxVDOP() {
        return getFloatOpt(AppSettings.C_maxVDOP_IDX,
                AppSettings.C_maxVDOP_SIZE);
    }

    /**
     * @param maxVDOP
     *                The maxVDOP to setFilter.
     */
    protected final void setFilterMaxVDOP(final float maxVDOP) {
        setFloatOpt(0, maxVDOP, AppSettings.C_maxVDOP_IDX,
                AppSettings.C_maxVDOP_SIZE);
    }

    /**
     * @return Returns the minDist.
     */
    public final float getFilterMinDist() {
        return getFloatOpt(AppSettings.C_minDist_IDX,
                AppSettings.C_minDist_SIZE);
    }

    /**
     * @param minDist
     *                The minDist to setFilter.
     */
    protected final void setFilterMinDist(final float minDist) {
        setFloatOpt(0, minDist, AppSettings.C_minDist_IDX,
                AppSettings.C_minDist_SIZE);
    }

    /**
     * @return Returns the minNSAT.
     */
    public final int getFilterMinNSAT() {
        return getLocalIntOpt(AppSettings.C_minNSAT_IDX,
                AppSettings.C_minNSAT_SIZE);
    }

    /**
     * @param minNSAT
     *                The minNSAT to setFilter.
     */
    protected final void setFilterMinNSAT(final int minNSAT) {
        setLocalIntOpt(0, minNSAT, AppSettings.C_minNSAT_IDX,
                AppSettings.C_minNSAT_SIZE);
    }

    /**
     * @return Returns the minRecCnt.
     */
    public final int getFilterMinRecCount() {
        return getLocalIntOpt(AppSettings.C_minRecCount_IDX,
                AppSettings.C_minRecCount_SIZE);
    }

    /**
     * @param minRecCnt
     *                The minRecCnt to setFilter.
     */
    protected final void setFilterMinRecCount(final int minRecCnt) {
        setLocalIntOpt(0, minRecCnt, AppSettings.C_minRecCount_IDX,
                AppSettings.C_minRecCount_SIZE);
    }

    /**
     * @return Returns the minSpeed.
     */
    public final float getFilterMinSpeed() {
        return getFloatOpt(AppSettings.C_minSpeed_IDX,
                AppSettings.C_minSpeed_SIZE);
    }

    /**
     * @param minSpeed
     *                The minSpeed to setFilter.
     */
    protected final void setFilterMinSpeed(final float minSpeed) {
        setFloatOpt(0, minSpeed, AppSettings.C_minSpeed_IDX,
                AppSettings.C_minSpeed_SIZE);
    }

    /**
     * @return Returns the solveMacLagProblem.
     */
    public final static boolean isSolveMacLagProblem() {
        return AppSettings.solveMacLagProblem;
    }

    /**
     * @param solveMacLagProblem
     *                The solveMacLagProblem to set.
     */
    public final static void setSolveMacLagProblem(final boolean arg) {
        AppSettings.solveMacLagProblem = arg;
    }

    public final boolean isStoredSetting1() {
        return getStringOpt(AppSettings.SETTING1_NMEA).length() > 15;
    }

    /**
     * Look for the google map site key in a file called "gmapkey.txt" Will
     * look in the output dir first, then in the source dir, then in the
     * settings dir.
     * 
     * @return The google map key
     */
    private final String getGoogleMapKeyInternal() {
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
                path = getStringOpt(AppSettings.OUTPUTDIRPATH) + "/";
                break;
            case 2:
                path = getStringOpt(AppSettings.LOGFILEPATH);
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
                final String gmapPath = path + "/"
                        + AppSettings.C_GMAP_KEY_FILENAME;
                if (new File(gmapPath).exists()) {
                    final File gmap = new File(gmapPath, File.READ_ONLY);

                    if (gmap.isOpen()) {
                        final byte[] b = new byte[100];
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
                }
            } catch (final Exception e) {
                // TODO: handle exception
            }
        }
        return gkey;
    }

    /**
     * @param defaultTraversable
     *                the defaultTraversable to set
     */
    public static void setDefaultTraversable(final boolean defaultTraversable) {
        AppSettings.defaultTraversable = defaultTraversable;
    }

    /**
     * @return the defaultTraversable
     */
    public static boolean isDefaultTraversable() {
        return AppSettings.defaultTraversable;
    }

    /**
     * @param defaultChunkSize
     *                the defaultChunkSize to set
     */
    public static void setDefaultChunkSize(final int defaultChunkSize) {
        AppSettings.defaultChunkSize = defaultChunkSize;
    }

    /**
     * @return the defaultChunkSize
     */
    public static int getDefaultChunkSize() {
        return AppSettings.defaultChunkSize;
    }

    /**
     * @param defaultBaseDirPath
     *                the defaultBaseDirPath to set
     */
    public static void setDefaultBaseDirPath(final String defaultBaseDirPath) {
        AppSettings.defaultBaseDirPath = defaultBaseDirPath;
    }

    /**
     * @return the defaultBaseDirPath
     */
    public static String getDefaultBaseDirPath() {
        return AppSettings.defaultBaseDirPath;
    }

    private void setFilterDefaults() {
        setTrkPtValid(0xFFFFFFFF & (~(BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
        setTrkPtRCR(0xFFFFFFFF);
        setWayPtValid(0xFFFFFFFF & (~(BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK)));
        setWayPtRCR(BT747Constants.RCR_BUTTON_MASK
                | BT747Constants.RCR_ALL_APP_MASK);
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
            postEvent(ModelEvent.SETTING_CHANGE, "" + eventType);
        }
    }

    private final void setStringOpt(final int eventType, final String src,
            final int idx, final int size) {
        Settings
                .setAppSettings(Settings.getAppSettings().substring(0, idx)
                        + src.substring(0, (src.length() < size) ? src
                                .length() : size)
                        + ((src.length() < size) ? "\0" : "")
                        + ((src.length() < (size - 1)) ? new String(
                                new byte[size - src.length() - 1]) : "")
                        + ((Settings.getAppSettings().length() > idx + size) ? Settings
                                .getAppSettings().substring(idx + size,
                                        Settings.getAppSettings().length())
                                : ""));
        if (eventType != 0) {
            postEvent(ModelEvent.SETTING_CHANGE, "" + eventType);
        }
    }

    private final void setLocalIntOpt(final int param, final int src,
            final int idx, final int size) {
        setOpt(param, Convert.unsigned2hex(src, size), idx, size);
    }

    private final int getLocalIntOpt(final int idx, final int size) {
        final String str = getStringOpt(idx, size);
        return Conv.hex2SignedInt(str);
    }

    private final void setFloatOpt(final int eventType, final float src,
            final int idx, final int size) {
        setOpt(eventType, Convert.unsigned2hex(Convert.toIntBitwise(src),
                size), idx, size);
    }

    private final float getFloatOpt(final int idx, final int size) {
        return Convert.toFloatBitwise(Conv.hex2Int(getStringOpt(idx, size)));
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

    /*
     * Event posting
     */

    private final BT747HashSet listeners = Interface.getHashSetInstance();

    /** add a listener to event thrown by this class */
    public final void addListener(final ModelListener l) {
        listeners.add(l);
    }

    public final void removeListener(final ModelListener l) {
        listeners.remove(l);
    }

    protected final void postEvent(final int type, final Object o) {
        final BT747HashSet it = listeners.iterator();
        while (it.hasNext()) {
            final ModelListener l = (ModelListener) it.next();
            final ModelEvent e = new ModelEvent(type, o);
            try {
                l.modelEvent(e);
            } catch (final Exception evt) {
                Generic.debug("Listener " + l.getClass(), evt);
            }
        }
    }

    protected final void postEvent(final int type) {
        final BT747HashSet it = listeners.iterator();
        while (it.hasNext()) {
            final ModelListener l = (ModelListener) it.next();
            final ModelEvent e = new ModelEvent(type, l);
            l.modelEvent(e);
        }
    }

    public final void postEvent(final ModelEvent e) {
        final BT747HashSet it = listeners.iterator();
        while (it.hasNext()) {
            final ModelListener l = (ModelListener) it.next();
            l.modelEvent(new ModelEvent(e));
        }
    }

    private static final int C_PORTNBR_IDX = 0;
    private static final int C_PORTNBR_SIZE = 8;
    private static final int C_BAUDRATE_IDX = AppSettings.C_PORTNBR_IDX
            + AppSettings.C_PORTNBR_SIZE;
    private static final int C_BAUDRATE_SIZE = 8;
    private static final int C_VERSION_IDX = AppSettings.C_BAUDRATE_IDX
            + AppSettings.C_BAUDRATE_SIZE;
    private static final int C_VERSION_SIZE = 8;
    private static final int C_BASEDIRPATH_IDX = AppSettings.C_VERSION_IDX
            + AppSettings.C_VERSION_SIZE;
    private static final int C_BASEDIRPATH_SIZE = 256;
    private static final int C_REPORTFILEBASE_IDX = AppSettings.C_BASEDIRPATH_IDX
            + AppSettings.C_BASEDIRPATH_SIZE;
    private static final int C_REPORTFILEBASE_SIZE = 40;
    private static final int C_LOGFILERELPATH_IDX = AppSettings.C_REPORTFILEBASE_IDX
            + AppSettings.C_REPORTFILEBASE_SIZE;
    private static final int C_LOGFILERELPATH_SIZE = 40;
    private static final int C_OPENSTARTUP_IDX = AppSettings.C_LOGFILERELPATH_IDX
            + AppSettings.C_LOGFILERELPATH_SIZE;
    private static final int C_OPENSTARTUP_SIZE = 40;
    private static final int C_CHUNKSIZE_IDX = AppSettings.C_OPENSTARTUP_IDX
            + AppSettings.C_OPENSTARTUP_SIZE;
    private static final int C_CHUNKSIZE_SIZE = 8;
    private static final int C_DOWNLOADTIMEOUT_IDX = AppSettings.C_CHUNKSIZE_IDX
            + AppSettings.C_CHUNKSIZE_SIZE;
    private static final int C_DOWNLOADTIMEOUT_SIZE = 8;
    private static final int C_CARD_IDX = AppSettings.C_DOWNLOADTIMEOUT_IDX
            + AppSettings.C_DOWNLOADTIMEOUT_SIZE;
    private static final int C_CARD_SIZE = 4;
    private static final int C_GPSTIMEOFFSETHOURS_IDX = AppSettings.C_CARD_IDX
            + AppSettings.C_CARD_SIZE;
    private static final int C_GPSTIMEOFFSETHOURS_SIZE = 4;
    private static final int C_WAYPT_RCR_IDX = AppSettings.C_GPSTIMEOFFSETHOURS_IDX
            + AppSettings.C_GPSTIMEOFFSETHOURS_SIZE;
    private static final int C_WAYPT_RCR_SIZE = 4;
    private static final int C_WAYPT_VALID_IDX = AppSettings.C_WAYPT_RCR_IDX
            + AppSettings.C_WAYPT_RCR_SIZE;
    private static final int C_WAYPT_VALID_SIZE = 4;
    private static final int C_TRKPT_RCR_IDX = AppSettings.C_WAYPT_VALID_IDX
            + AppSettings.C_WAYPT_VALID_SIZE;
    private static final int C_TRKPT_RCR_SIZE = 4;
    private static final int C_TRKPT_VALID_IDX = AppSettings.C_TRKPT_RCR_IDX
            + AppSettings.C_TRKPT_RCR_SIZE;
    private static final int C_TRKPT_VALID_SIZE = 4;
    private static final int C_ONEFILEPERDAY_IDX = AppSettings.C_TRKPT_VALID_IDX
            + AppSettings.C_TRKPT_VALID_SIZE;
    private static final int C_ONEFILEPERDAY_SIZE = 1;
    private static final int C_WGS84_TO_MSL_IDX = AppSettings.C_ONEFILEPERDAY_IDX
            + AppSettings.C_ONEFILEPERDAY_SIZE;
    private static final int C_WGS84_TO_MSL_SIZE = 4;
    private static final int C_LOGAHEAD_IDX = AppSettings.C_WGS84_TO_MSL_IDX
            + AppSettings.C_WGS84_TO_MSL_SIZE;
    private static final int C_LOGAHEAD_SIZE = 1;
    private static final int C_NMEASET_IDX = AppSettings.C_LOGAHEAD_IDX
            + AppSettings.C_LOGAHEAD_SIZE;
    private static final int C_NMEASET_SIZE = 8;
    private static final int C_GPXUTC0_IDX = AppSettings.C_NMEASET_IDX
            + AppSettings.C_NMEASET_SIZE;
    private static final int C_GPXUTC0_SIZE = 1;
    private static final int C_TRKSEP_IDX = AppSettings.C_GPXUTC0_IDX
            + AppSettings.C_GPXUTC0_SIZE;
    private static final int C_TRKSEP_SIZE = 4;
    private static final int C_ADVFILTACTIVE_IDX = AppSettings.C_TRKSEP_IDX
            + AppSettings.C_TRKSEP_SIZE;
    private static final int C_ADVFILTACTIVE_SIZE = 1;
    private static final int C_minDist_IDX = AppSettings.C_ADVFILTACTIVE_IDX
            + AppSettings.C_ADVFILTACTIVE_SIZE;
    private static final int C_minDist_SIZE = 8;
    private static final int C_MAX_DISTANCE_IDX = AppSettings.C_minDist_IDX
            + AppSettings.C_minDist_SIZE;
    private static final int C_MAX_DISTANCE_SIZE = 8;
    private static final int C_minSpeed_IDX = AppSettings.C_MAX_DISTANCE_IDX
            + AppSettings.C_MAX_DISTANCE_SIZE;
    private static final int C_minSpeed_SIZE = 8;
    private static final int C_maxSpeed_IDX = AppSettings.C_minSpeed_IDX
            + AppSettings.C_minSpeed_SIZE;
    private static final int C_maxSpeed_SIZE = 8;
    private static final int C_maxHDOP_IDX = AppSettings.C_maxSpeed_IDX
            + AppSettings.C_maxSpeed_SIZE;
    private static final int C_maxHDOP_SIZE = 8;
    private static final int C_maxPDOP_IDX = AppSettings.C_maxHDOP_IDX
            + AppSettings.C_maxHDOP_SIZE;
    private static final int C_maxPDOP_SIZE = 8;
    private static final int C_maxVDOP_IDX = AppSettings.C_maxPDOP_IDX
            + AppSettings.C_maxPDOP_SIZE;
    private static final int C_maxVDOP_SIZE = 8;
    private static final int C_minRecCount_IDX = AppSettings.C_maxVDOP_IDX
            + AppSettings.C_maxVDOP_SIZE;
    private static final int C_minRecCount_SIZE = 8;
    private static final int C_maxRecCount_IDX = AppSettings.C_minRecCount_IDX
            + AppSettings.C_minRecCount_SIZE;
    private static final int C_maxRecCount_SIZE = 8;
    private static final int C_minNSAT_IDX = AppSettings.C_maxRecCount_IDX
            + AppSettings.C_maxRecCount_SIZE;
    private static final int C_minNSAT_SIZE = 4;
    private static final int C_GPXTRKSEGBIG_IDX = AppSettings.C_minNSAT_IDX
            + AppSettings.C_minNSAT_SIZE;
    private static final int C_GPXTRKSEGBIG_SIZE = 1;
    private static final int C_DECODEGPS_IDX = AppSettings.C_GPXTRKSEGBIG_IDX
            + AppSettings.C_GPXTRKSEGBIG_SIZE;
    private static final int C_DECODEGPS_SIZE = 4;
    private static final int C_COLOR_INVALIDTRACK_IDX = AppSettings.C_DECODEGPS_IDX
            + AppSettings.C_DECODEGPS_SIZE;
    private static final int C_COLOR_INVALIDTRACK_SIZE = 8;
    private static final int C_ISTRAVERSABLE_IDX = AppSettings.C_COLOR_INVALIDTRACK_IDX
            + AppSettings.C_COLOR_INVALIDTRACK_SIZE;
    private static final int C_ISTRAVERSABLE_SIZE = 4;
    private static final int C_SETTING1_TIME_IDX = AppSettings.C_ISTRAVERSABLE_IDX
            + AppSettings.C_ISTRAVERSABLE_SIZE;
    private static final int C_SETTING1_TIME_SIZE = 8;
    private static final int C_SETTING1_SPEED_IDX = AppSettings.C_SETTING1_TIME_IDX
            + AppSettings.C_SETTING1_TIME_SIZE;
    private static final int C_SETTING1_SPEED_SIZE = 8;
    private static final int C_SETTING1_DIST_IDX = AppSettings.C_SETTING1_SPEED_IDX
            + AppSettings.C_SETTING1_SPEED_SIZE;
    private static final int C_SETTING1_DIST_SIZE = 8;
    private static final int C_SETTING1_FIX_IDX = AppSettings.C_SETTING1_DIST_IDX
            + AppSettings.C_SETTING1_DIST_SIZE;
    private static final int C_SETTING1_FIX_SIZE = 8;
    private static final int C_SETTING1_NMEA_IDX = AppSettings.C_SETTING1_FIX_IDX
            + AppSettings.C_SETTING1_FIX_SIZE;
    private static final int C_SETTING1_NMEA_SIZE = 20;
    private static final int C_SETTING1_DGPS_IDX = AppSettings.C_SETTING1_NMEA_IDX
            + AppSettings.C_SETTING1_NMEA_SIZE;
    private static final int C_SETTING1_DGPS_SIZE = 8;
    private static final int C_SETTING1_TEST_IDX = AppSettings.C_SETTING1_DGPS_IDX
            + AppSettings.C_SETTING1_DGPS_SIZE;
    private static final int C_SETTING1_TEST_SIZE = 2;
    private static final int C_SETTING1_LOG_OVR_IDX = AppSettings.C_SETTING1_TEST_IDX
            + AppSettings.C_SETTING1_TEST_SIZE;
    private static final int C_SETTING1_LOG_OVR_SIZE = 1;
    private static final int C_SETTING1_LOG_FORMAT_IDX = AppSettings.C_SETTING1_LOG_OVR_IDX
            + AppSettings.C_SETTING1_LOG_OVR_SIZE;
    private static final int C_SETTING1_LOG_FORMAT_SIZE = 8;
    private static final int C_SETTING1_SBAS_IDX = AppSettings.C_SETTING1_LOG_FORMAT_IDX
            + AppSettings.C_SETTING1_LOG_FORMAT_SIZE;
    private static final int C_SETTING1_SBAS_SIZE = 1;
    private static final int C_RECORDNBR_IN_LOGS_IDX = AppSettings.C_SETTING1_SBAS_IDX
            + AppSettings.C_SETTING1_SBAS_SIZE;
    private static final int C_RECORDNBR_IN_LOGS_SIZE = 4;
    private static final int C_HOLUX241_IDX = AppSettings.C_RECORDNBR_IN_LOGS_IDX
            + AppSettings.C_RECORDNBR_IN_LOGS_SIZE;
    private static final int C_HOLUX241_SIZE = 1;
    private static final int C_IMPERIAL_IDX = AppSettings.C_HOLUX241_IDX
            + AppSettings.C_HOLUX241_SIZE;
    private static final int C_IMPERIAL_SIZE = 1;
    private static final int C_FREETEXTPORT_IDX = AppSettings.C_IMPERIAL_IDX
            + AppSettings.C_IMPERIAL_SIZE;
    private static final int C_FREETEXTPORT_SIZE = 50;
    private static final int C_NOT_USED1_IDX = AppSettings.C_FREETEXTPORT_IDX
            + AppSettings.C_FREETEXTPORT_SIZE;
    private static final int C_NOT_USED1_SIZE = 4;
    private static final int C_GPSTYPE_IDX = AppSettings.C_NOT_USED1_IDX
            + AppSettings.C_NOT_USED1_SIZE;
    private static final int C_GPSTYPE_SIZE = 1;
    private static final int C_OUTPUTLOGCONDITIONS_IDX = AppSettings.C_GPSTYPE_IDX
            + AppSettings.C_GPSTYPE_SIZE;
    private static final int C_OUTPUTLOGCONDITIONS_SIZE = 1;
    private static final int C_IS_WRITE_TRACKPOINT_COMMENT_IDX = AppSettings.C_OUTPUTLOGCONDITIONS_IDX
            + AppSettings.C_OUTPUTLOGCONDITIONS_SIZE;
    private static final int C_IS_WRITE_TRACKPOINT_COMMENT_SIZE = 4;
    private static final int C_IS_WRITE_TRACKPOINT_NAME_IDX = AppSettings.C_IS_WRITE_TRACKPOINT_COMMENT_IDX
            + AppSettings.C_IS_WRITE_TRACKPOINT_COMMENT_SIZE;
    private static final int C_IS_WRITE_TRACKPOINT_NAME_SIZE = 4;
    private static final int C_FILEFIELDFORMAT_IDX = AppSettings.C_IS_WRITE_TRACKPOINT_NAME_IDX
            + AppSettings.C_IS_WRITE_TRACKPOINT_NAME_SIZE;
    private static final int C_FILEFIELDFORMAT_SIZE = 8;
    private static final int C_STOP_LOG_ON_CONNECT_IDX = AppSettings.C_FILEFIELDFORMAT_IDX
            + AppSettings.C_FILEFIELDFORMAT_SIZE;
    private static final int C_STOP_LOG_ON_CONNECT_SIZE = 1;
    private static final int C_COLOR_VALIDTRACK_IDX = AppSettings.C_STOP_LOG_ON_CONNECT_IDX
            + AppSettings.C_STOP_LOG_ON_CONNECT_SIZE;
    private static final int C_COLOR_VALIDTRACK_SIZE = 8;
    private static final int C_LOGFILEPATH_IDX = AppSettings.C_COLOR_VALIDTRACK_IDX
            + AppSettings.C_COLOR_VALIDTRACK_SIZE;
    private static final int C_LOGFILEPATH_SIZE = 300;
    private static final int C_LANGUAGE_IDX = AppSettings.C_LOGFILEPATH_IDX
            + AppSettings.C_LOGFILEPATH_SIZE;
    private static final int C_LANGUAGE_SIZE = 8;
    private static final int C_IMAGEDIR_IDX = AppSettings.C_LANGUAGE_IDX
            + AppSettings.C_LANGUAGE_SIZE;
    private static final int C_IMAGEDIR_SIZE = 256;
    private static final int C_FILETIMEOFFSET_IDX = AppSettings.C_IMAGEDIR_IDX
            + AppSettings.C_IMAGEDIR_SIZE;
    private static final int C_FILETIMEOFFSET_SIZE = 4;
    private static final int C_TAG_OVERRIDEPOSITIONS_IDX = AppSettings.C_FILETIMEOFFSET_IDX
            + AppSettings.C_FILETIMEOFFSET_SIZE;
    private static final int C_TAG_OVERRIDEPOSITIONS_SIZE = 1;
    private static final int C_TAG_MAXTIMEDIFFERENCE_IDX = AppSettings.C_TAG_OVERRIDEPOSITIONS_IDX
            + AppSettings.C_TAG_OVERRIDEPOSITIONS_SIZE;
    private static final int C_TAG_MAXTIMEDIFFERENCE_SIZE = 4;
    private static final int C_DISABLELOGDURINGDOWNLOAD_IDX = AppSettings.C_TAG_MAXTIMEDIFFERENCE_IDX
            + AppSettings.C_TAG_MAXTIMEDIFFERENCE_SIZE;
    private static final int C_DISABLELOGDURINGDOWNLOAD_SIZE = 1;
    private static final int C_MAPCACHEDIRECTORY_IDX = AppSettings.C_DISABLELOGDURINGDOWNLOAD_IDX
            + AppSettings.C_DISABLELOGDURINGDOWNLOAD_SIZE;
    private static final int C_MAPCACHEDIRECTORY_SIZE = 255;
    private static final int C_MAPTYPE_IDX = AppSettings.C_MAPCACHEDIRECTORY_IDX
            + AppSettings.C_MAPCACHEDIRECTORY_SIZE;
    private static final int C_MAPTYPE_SIZE = 1;
    private static final int C_TAGGED_TEMPLATE_IDX = AppSettings.C_MAPTYPE_IDX
            + AppSettings.C_MAPTYPE_SIZE;
    private static final int C_TAGGED_TEMPLATE_SIZE = 255;
    private static final int C_KML_ALTITUDEMODE_IDX = AppSettings.C_TAGGED_TEMPLATE_IDX
            + AppSettings.C_TAGGED_TEMPLATE_SIZE;
    private static final int C_KML_ALTITUDEMODE_SIZE = 1;
    private static final int C_GPX_LINK_INFO_IDX = AppSettings.C_KML_ALTITUDEMODE_IDX
            + AppSettings.C_KML_ALTITUDEMODE_SIZE;
    private static final int C_GPX_LINK_INFO_SIZE = 1;
    private static final int C_IS_GPX_1_1_IDX = AppSettings.C_GPX_LINK_INFO_IDX
            + AppSettings.C_GPX_LINK_INFO_SIZE;
    private static final int C_IS_GPX_1_1_SIZE = 1;
    private static final int C_DOWNLOAD_DEVICE_IDX = AppSettings.C_IS_GPX_1_1_IDX
            + AppSettings.C_IS_GPX_1_1_SIZE;
    private static final int C_DOWNLOAD_DEVICE_SIZE = 1;
    private static final int C_NEXT_IDX = AppSettings.C_DOWNLOAD_DEVICE_IDX
            + AppSettings.C_DOWNLOAD_DEVICE_SIZE;

    // Next lines just to add new items faster using replace functions
    private static final int C_NEXT_SIZE = 4;
    private static final int C_NEW_NEXT_IDX = AppSettings.C_NEXT_IDX
            + AppSettings.C_NEXT_SIZE;

    private static final int[][] paramsList =
    // Type, idx, start, size
    {
            { AppSettings.BOOL, AppSettings.IS_WRITE_TRACKPOINT_COMMENT,
                    AppSettings.C_IS_WRITE_TRACKPOINT_COMMENT_IDX,
                    AppSettings.C_IS_WRITE_TRACKPOINT_COMMENT_SIZE },
            { AppSettings.BOOL, AppSettings.IS_WRITE_TRACKPOINT_NAME,
                    AppSettings.C_IS_WRITE_TRACKPOINT_NAME_IDX,
                    AppSettings.C_IS_WRITE_TRACKPOINT_NAME_SIZE },
            { AppSettings.BOOL, AppSettings.OUTPUTLOGCONDITIONS,
                    AppSettings.C_OUTPUTLOGCONDITIONS_IDX,
                    AppSettings.C_OUTPUTLOGCONDITIONS_SIZE },
            { AppSettings.BOOL, AppSettings.IMPERIAL,
                    AppSettings.C_IMPERIAL_IDX, AppSettings.C_IMPERIAL_SIZE },
            { AppSettings.BOOL, AppSettings.FORCE_HOLUXM241,
                    AppSettings.C_HOLUX241_IDX, AppSettings.C_HOLUX241_SIZE },
            { AppSettings.BOOL, AppSettings.IS_RECORDNBR_IN_LOGS,
                    AppSettings.C_RECORDNBR_IN_LOGS_IDX,
                    AppSettings.C_RECORDNBR_IN_LOGS_SIZE },
            { AppSettings.BOOL, AppSettings.IS_TRAVERSABLE,
                    AppSettings.C_ISTRAVERSABLE_IDX,
                    AppSettings.C_ISTRAVERSABLE_SIZE },
            { AppSettings.INT, AppSettings.FILEFIELDFORMAT,
                    AppSettings.C_FILEFIELDFORMAT_IDX,
                    AppSettings.C_FILEFIELDFORMAT_SIZE },
            // Application parameter
            { AppSettings.BOOL, AppSettings.IS_STOP_LOGGING_ON_CONNECT,
                    AppSettings.C_STOP_LOG_ON_CONNECT_IDX,
                    AppSettings.C_STOP_LOG_ON_CONNECT_SIZE },
            { AppSettings.STRING, AppSettings.OUTPUTDIRPATH,
                    AppSettings.C_BASEDIRPATH_IDX,
                    AppSettings.C_BASEDIRPATH_SIZE },
            { AppSettings.STRING, AppSettings.REPORTFILEBASE,
                    AppSettings.C_REPORTFILEBASE_IDX,
                    AppSettings.C_REPORTFILEBASE_SIZE },
            { AppSettings.STRING, AppSettings.LOGFILERELPATH,
                    AppSettings.C_LOGFILERELPATH_IDX,
                    AppSettings.C_LOGFILERELPATH_SIZE },
            { AppSettings.STRING, AppSettings.LOGFILEPATH,
                    AppSettings.C_LOGFILEPATH_IDX,
                    AppSettings.C_LOGFILEPATH_SIZE },
            { AppSettings.STRING, AppSettings.LANGUAGE,
                    AppSettings.C_LANGUAGE_IDX, AppSettings.C_LANGUAGE_SIZE },
            { AppSettings.STRING, AppSettings.IMAGEDIR,
                    AppSettings.C_IMAGEDIR_IDX, AppSettings.C_IMAGEDIR_SIZE },
            { AppSettings.INT, AppSettings.FILETIMEOFFSET,
                    AppSettings.C_FILETIMEOFFSET_IDX,
                    AppSettings.C_FILETIMEOFFSET_SIZE },
            { AppSettings.INT, AppSettings.PORTNBR,
                    AppSettings.C_PORTNBR_IDX, AppSettings.C_PORTNBR_SIZE },
            { AppSettings.INT, AppSettings.BAUDRATE,
                    AppSettings.C_BAUDRATE_IDX, AppSettings.C_BAUDRATE_SIZE },
            { AppSettings.STRING, AppSettings.FREETEXTPORT,
                    AppSettings.C_FREETEXTPORT_IDX,
                    AppSettings.C_FREETEXTPORT_SIZE },
            { AppSettings.BOOL, AppSettings.OPENPORTATSTARTUP,
                    AppSettings.C_OPENSTARTUP_IDX,
                    AppSettings.C_OPENSTARTUP_SIZE },
            { AppSettings.INT, AppSettings.GPSTIMEOFFSETHOURS,
                    AppSettings.C_GPSTIMEOFFSETHOURS_IDX,
                    AppSettings.C_GPSTIMEOFFSETHOURS_SIZE },
            { AppSettings.BOOL, AppSettings.TAG_OVERRIDEPOSITIONS,
                    AppSettings.C_TAG_OVERRIDEPOSITIONS_IDX,
                    AppSettings.C_TAG_OVERRIDEPOSITIONS_SIZE },
            { AppSettings.INT, AppSettings.TAG_MAXTIMEDIFFERENCE,
                    AppSettings.C_TAG_MAXTIMEDIFFERENCE_IDX,
                    AppSettings.C_TAG_MAXTIMEDIFFERENCE_SIZE },
            { AppSettings.INT, AppSettings.GPSTYPE,
                    AppSettings.C_GPSTYPE_IDX, AppSettings.C_GPSTYPE_SIZE },
            { AppSettings.BOOL, AppSettings.DISABLELOGDURINGDOWNLOAD,
                    AppSettings.C_DISABLELOGDURINGDOWNLOAD_IDX,
                    AppSettings.C_DISABLELOGDURINGDOWNLOAD_SIZE },
            { AppSettings.STRING, AppSettings.MAPCACHEDIRECTORY,
                    AppSettings.C_MAPCACHEDIRECTORY_IDX,
                    AppSettings.C_MAPCACHEDIRECTORY_SIZE },
            { AppSettings.STRING, AppSettings.VERSION,
                    AppSettings.C_VERSION_IDX, AppSettings.C_VERSION_SIZE },
            { AppSettings.INT, AppSettings.MAPTYPE,
                    AppSettings.C_MAPTYPE_IDX, AppSettings.C_MAPTYPE_SIZE },
            { AppSettings.STRING, AppSettings.GOOGLEMAPKEY, 0, 0 },
            { AppSettings.STRING, AppSettings.SETTING1_NMEA,
                    AppSettings.C_SETTING1_NMEA_IDX,
                    AppSettings.C_SETTING1_NMEA_SIZE },
            { AppSettings.INT, AppSettings.SETTING1_TIME,
                    AppSettings.C_SETTING1_TIME_IDX,
                    AppSettings.C_SETTING1_TIME_SIZE },
            { AppSettings.INT, AppSettings.SETTING1_SPEED,
                    AppSettings.C_SETTING1_SPEED_IDX,
                    AppSettings.C_SETTING1_SPEED_SIZE },
            { AppSettings.INT, AppSettings.SETTING1_DIST,
                    AppSettings.C_SETTING1_DIST_IDX,
                    AppSettings.C_SETTING1_DIST_SIZE },
            { AppSettings.INT, AppSettings.SETTING1_FIX,
                    AppSettings.C_SETTING1_FIX_IDX,
                    AppSettings.C_SETTING1_FIX_SIZE },
            { AppSettings.INT, AppSettings.SETTING1_DGPS,
                    AppSettings.C_SETTING1_DGPS_IDX,
                    AppSettings.C_SETTING1_DGPS_SIZE },
            { AppSettings.BOOL, AppSettings.SETTING1_TEST,
                    AppSettings.C_SETTING1_TEST_IDX,
                    AppSettings.C_SETTING1_TEST_SIZE },
            { AppSettings.BOOL, AppSettings.SETTING1_LOG_OVR,
                    AppSettings.C_SETTING1_LOG_OVR_IDX,
                    AppSettings.C_SETTING1_LOG_OVR_SIZE },
            { AppSettings.INT, AppSettings.SETTING1_LOG_FORMAT,
                    AppSettings.C_SETTING1_LOG_FORMAT_IDX,
                    AppSettings.C_SETTING1_LOG_FORMAT_SIZE },
            { AppSettings.BOOL, AppSettings.SETTING1_SBAS,
                    AppSettings.C_SETTING1_SBAS_IDX,
                    AppSettings.C_SETTING1_SBAS_SIZE },
            { AppSettings.BOOL, AppSettings.ADVFILTACTIVE,
                    AppSettings.C_ADVFILTACTIVE_IDX,
                    AppSettings.C_ADVFILTACTIVE_SIZE },
            { AppSettings.BOOL, AppSettings.GPXUTC0,
                    AppSettings.C_GPXUTC0_IDX, AppSettings.C_GPXUTC0_SIZE },
            { AppSettings.BOOL, AppSettings.DECODEGPS,
                    AppSettings.C_DECODEGPS_IDX, AppSettings.C_DECODEGPS_SIZE },
            { AppSettings.BOOL, AppSettings.GPXTRKSEGBIG,
                    AppSettings.C_GPXTRKSEGBIG_IDX,
                    AppSettings.C_GPXTRKSEGBIG_SIZE },
            { AppSettings.STRING, AppSettings.COLOR_VALIDTRACK,
                    AppSettings.C_COLOR_VALIDTRACK_IDX,
                    AppSettings.C_COLOR_VALIDTRACK_SIZE },
            { AppSettings.STRING, AppSettings.COLOR_INVALIDTRACK,
                    AppSettings.C_COLOR_INVALIDTRACK_IDX,
                    AppSettings.C_COLOR_INVALIDTRACK_SIZE },
            { AppSettings.STRING, AppSettings.TAGGEDFILE_TEMPLATE,
                    AppSettings.C_TAGGED_TEMPLATE_IDX,
                    AppSettings.C_TAGGED_TEMPLATE_SIZE },
            { AppSettings.INT, AppSettings.KML_ALTITUDEMODE,
                    AppSettings.C_KML_ALTITUDEMODE_IDX,
                    AppSettings.C_KML_ALTITUDEMODE_SIZE },
            { AppSettings.INT, AppSettings.TRKSEP, AppSettings.C_TRKSEP_IDX,
                    AppSettings.C_TRKSEP_SIZE },
            { AppSettings.INT, AppSettings.LOGAHEAD,
                    AppSettings.C_LOGAHEAD_IDX, AppSettings.C_LOGAHEAD_SIZE },
                    { AppSettings.BOOL, AppSettings.GPX_LINK_INFO,
                        AppSettings.C_GPX_LINK_INFO_IDX, AppSettings.C_GPX_LINK_INFO_SIZE },
                        { AppSettings.BOOL, AppSettings.IS_GPX_1_1,
                            AppSettings.C_IS_GPX_1_1_IDX, AppSettings.C_IS_GPX_1_1_SIZE },
                        { AppSettings.INT, AppSettings.DOWNLOAD_DEVICE,
                            AppSettings.C_DOWNLOAD_DEVICE_IDX, AppSettings.C_DOWNLOAD_DEVICE_SIZE },
    // End of list
    };

}
