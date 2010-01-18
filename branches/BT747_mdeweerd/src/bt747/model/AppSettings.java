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
import gps.log.LogFileInfo;

import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.Settings;
import bt747.sys.interfaces.BT747HashSet;
import bt747.sys.interfaces.BT747Path;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747Thread;
import bt747.sys.interfaces.BT747Vector;

/**
 * @author Mario De Weerd
 */

// Implementation notes:
// To add stuff, it is fairly simple:
// a) Add a new key (say just after public final static int OSMPASS = 58;, add
// key 59). Must be unique of course.
// b) 'Reserve' the space for the data:
// - Copy the definitions for
// private static final int C_NEXT_SIZE = 4;
// private static final int C_NEW_NEXT_IDX = AppSettings.C_NEXT_IDX
//	 
// just above those lines.
// On three lines (2 of which you just created), replace 'C_NEXT' with
// 'C_NEWKEY'.
// Adjust the size of the field to what you need.
//
// c) Add the key and the offset, size to the paramsList (at the end, must be
// in the right order):
// Copy entry for say 'OSMPASS' and replace 'OSMPASS' with 'NEWKEY'.
// Set the right type for the entry (probably STRING for you).
// d) in 'updateSettings', find the last case entry. Add a new one just above
// setting the 'VERSION'. Indicate '/* fall through */. Increment number for
// case and number for version. Initialise all the new settings you just
// added.
// e) Use/Test.
// Set the setting in the program:
// c.setStringOpt(... c.setIntOpt(...., etc.
// To read the setting,
// m.getStringOpt(param), ...
// The model will notify changes.
public class AppSettings implements BT747Thread {

    /**
     * Google map file name (basename).
     */
    public static final String C_GMAP_KEY_FILENAME = "gmapkey.txt";
    public static final String DUMMY_AGPS_STRING = "ftp://login:passwd@siteURL.com/MTK7d.EPO";

    private static final int C_DEFAULT_DEVICE_TIMEOUT = 4000; // ms
    private static final int C_DEFAULT_LOG_REQUEST_AHEAD = 3;

    // Parameter types
    //
    private static final int INT = 1;
    private static final int BOOL = 2;
    private static final int STRING = 3;
    private static final int FLOAT = 4;

    // Some virtual parameters
    public static final int REPORTFILEBASEPATH = -1;

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
     * 
     * @deprecated
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
     * where IDX is one of the following:<br>
     * - {@link BT747Constants#FMT_UTC_IDX} <br>
     * - {@link BT747Constants#FMT_VALID_IDX} <br>
     * - {@link BT747Constants#FMT_LATITUDE_IDX} <br>
     * - {@link BT747Constants#FMT_LONGITUDE_IDX} <br>
     * - {@link BT747Constants#FMT_HEIGHT_IDX} <br>
     * - {@link BT747Constants#FMT_SPEED_IDX} <br>
     * - {@link BT747Constants#FMT_HEADING_IDX} <br>
     * - {@link BT747Constants#FMT_DSTA_IDX} <br>
     * - {@link BT747Constants#FMT_DAGE_IDX} <br>
     * - {@link BT747Constants#FMT_PDOP_IDX} <br>
     * - {@link BT747Constants#FMT_HDOP_IDX} <br>
     * - {@link BT747Constants#FMT_VDOP_IDX} <br>
     * - {@link BT747Constants#FMT_NSAT_IDX} <br>
     * - {@link BT747Constants#FMT_SID_IDX} <br>
     * - {@link BT747Constants#FMT_ELEVATION_IDX} <br>
     * - {@link BT747Constants#FMT_AZIMUTH_IDX} <br>
     * - {@link BT747Constants#FMT_SNR_IDX} <br>
     * - {@link BT747Constants#FMT_RCR_IDX} <br>
     * - {@link BT747Constants#FMT_MILLISECOND_IDX} <br>
     * - {@link BT747Constants#FMT_DISTANCE_IDX} <br>
     * - <br>
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
     * The GPS time offset (UTC vs. local time). (used in adjusting the time
     * in the output formats).
     * 
     * @deprecated
     */
    public static final int GPSTIMEOFFSETHOURS_OBSOLETE = 20;

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
     * The {@link #GPSTYPE} parameter is a value out of:
     * <ul>
     * <li>{@link BT747Constants#GPS_TYPE_DEFAULT}</li>
     * <li>{@link BT747Constants#GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII}</li>
     * <li>{@link BT747Constants#GPS_TYPE_GISTEQ_ITRACKU_NEMERIX}</li>
     * <li>{@link BT747Constants#GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR}</li>
     * </ul>
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
     * Parameter index to get the Google Map key. This is not a stored
     * setting.
     * 
     * Looks for the google map site key in a file called "gmapkey.txt" Will
     * look in the output dir first, then in the source dir, then in the
     * settings dir.
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
    /**
     * Force GPX utc offset to 0.
     */
    public final static int GPXUTC0 = 40;
    public final static int DECODEGPS = 41;
    public final static int GPXTRKSEGBIG = 42;
    public final static int COLOR_VALIDTRACK = 43;
    public final static int COLOR_INVALIDTRACK = 44;
    public final static int TAGGEDFILE_TEMPLATE = 45;
    public final static int KML_ALTITUDEMODE = 46;
    /**
     * Parameter index for the track separation time. When two positions are
     * separated by this time or more, a track separation is inserted.
     * 
     * The parameter indicates the time in minutes for a track separation.
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
    public final static int DEVICE_PROTOCOL = 51;
    /**
     * The previous field for the filetime offset.
     */
    public static final int FILETIMEOFFSETOLD = 52;

    /** AGPS external link. */
    public static final int AGPSURL = 53;
    /** Whether precise GEOID calculation is to be used if available. */
    public static final int IS_USE_PRECISE_GEOID = 54;
    /**
     * Indicates if this is the first time a connection is made in the app.
     * Used to set some device dependent settings needed when no connection.
     */
    public static final int IS_FIRST_CONNECTION_TO_INIT = 55;
    /**
     * Force NMEA utc offset to 0.
     */
    public final static int NMEAUTC0 = 56;

    /**
     * OSM account.
     */
    public final static int OSMLOGIN = 57;
    /**
     * OSM password.
     */
    public final static int OSMPASS = 58;

    /**
     * New track when logger is switched on.
     */
    public final static int IS_NEW_TRACK_WHEN_LOG_ON = 59;
    public final static int SPLIT_DISTANCE = 60;
    // Some definitions for location server functionality
    /**
     * Hostname of the server to receive location data.
     */
    public final static int POS_SRV_HOSTNAME = 61;
    /**
     * Port of the server receiving location data.
     */
    public final static int POS_SRV_PORT = 62;
    /**
     * File part of the URL of the server to receiver the location data.
     */
    public final static int POS_SRV_FILE = 63;
    /**
     * Property indicating whether or not the application should start sending
     * out location updates when a GPS device is connected.
     */
    public final static int POS_SRV_AUTOSTART = 64;
    /**
     * Username of the server to receive location data.
     */
    public final static int POS_SRV_USER = 65;
    /**
     * Password for access of the server receiving location data.
     */
    public final static int POS_SRV_PASS = 66;
    /**
     * The period in seconds between location server updates
     */
    public final static int POS_SRV_PERIOD = 67;
    /**
     * The GPS time offset (UTC vs. local time) in quarters (15 minutes).
     * Replaces {@link #GPSTIMEOFFSETHOURS_OBSOLETE} (used in adjusting the
     * time in the output formats).
     */
    public static final int GPSTIMEOFFSETQUARTERS = 68;

    /**
     * External command
     */
    public final static int EXTCOMMAND = 69;

    /**
     * External intermediate format type.
     */
    public final static int EXTTYPE = 70;

    /**
     * Font scale in percent. 100 = original size.
     */
    public final static int FONTSCALE = 71;

    /**
     * 
     * The card number that the filepaths refer to. This is usefull on PDA's
     * only and probably only on a Palm. This is used by the 'SuperWaba'
     * environment if used. '-1' refers to the 'last card in the system' which
     * is normally the card that can be inserted by the PDA user.
     */
    public final static int CARD = 72;

    /** All data in one file ({@link #OUTPUTFILESPLITTYPE}). */
    public static final int SPLIT_ONE_FILE = 0;
    /** One file per day ({@link #OUTPUTFILESPLITTYPE}). */
    public static final int SPLIT_ONE_FILE_PER_DAY = 1;
    /**
     * One file per track (a track separation occurs when the time between two
     * log points is bigger than a given time or other track split condition)
     * ({@link #OUTPUTFILESPLITTYPE}).
     */
    public static final int SPLIT_ONE_FILE_PER_TRACK = 2;

    /**
     * The way we split the input track:
     * <ul>
     * <li>{@link #SPLIT_ONE_FILE}</li>
     * <li>{@link #SPLIT_ONE_FILE_PER_DAY}</li>
     * <li>{@link #SPLIT_ONE_FILE_PER_TRACK}</li>
     * </ul>
     */
    public final static int OUTPUTFILESPLITTYPE = 73;

    private final static int TYPE_IDX = 0;
    private final static int PARAM_IDX = 1;
    private final static int START_IDX = 2;
    private final static int SIZE_IDX = 3;

    public final static int MIN_SPEED = 74;
    public final static int MAX_SPEED = 75;
    public final static int MIN_NSAT = 76;
    public final static int MIN_RECCOUNT = 77;
    public final static int MAX_RECCOUNT = 78;
    public final static int MIN_DISTANCE = 79;
    public final static int MAX_VDOP = 80;
    public final static int MAX_PDOP = 81;
    public final static int MAX_HDOP = 82;
    public final static int MAX_DISTANCE = 83;
    /**
     * Parameter index for Height Conversion Mode.
     * 
     * @see #HEIGHT_AUTOMATIC
     * @see #HEIGHT_MSL_TO_WGS84
     * @see #HEIGHT_WGS84_TO_MSL
     * @see #HEIGHT_NOCHANGE
     */
    public final static int HEIGHT_CONVERSION_MODE = 84;

    public final static int HEIGHT_NOCHANGE = 0;
    public final static int HEIGHT_WGS84_TO_MSL = 1;
    public final static int HEIGHT_AUTOMATIC = 2;
    public final static int HEIGHT_MSL_TO_WGS84 = 3;

    public final static int DISTANCE_CALCULATION_MODE = 85;
    public final static int DISTANCE_CALC_MODE_NONE = 0;
    public final static int DISTANCE_CALC_MODE_WHEN_MISSING = 1;
    public final static int DISTANCE_CALC_MODE_ALWAYS = 2;

    /**
     * Sets the 'Valid' filter mask for the current waypoint filter.
     * 
     * The filter mask to set for the validity filter. Use the following
     * constants:<br>
     * - {@link BT747Constants#VALID_NO_FIX_MASK} <br>
     * - {@link BT747Constants#VALID_SPS_MASK} <br>
     * - {@link BT747Constants#VALID_DGPS_MASK} <br>
     * - {@link BT747Constants#VALID_PPS_MASK} <br>
     * - {@link BT747Constants#VALID_RTK_MASK} <br>
     * - {@link BT747Constants#VALID_FRTK_MASK} <br>
     * - {@link BT747Constants#VALID_ESTIMATED_MASK} <br>
     * - {@link BT747Constants#VALID_MANUAL_MASK} <br>
     * - {@link BT747Constants#VALID_SIMULATOR_MASK} <br>
     */
    public final static int WAYPT_VALID = 86;
    /**
     * The trackpoint RCR mask for the active filter.
     * 
     * - {@link BT747Constants#RCR_TIME_MASK}<br>
     * - {@link BT747Constants#RCR_SPEED_MASK}<br>
     * - {@link BT747Constants#RCR_DISTANCE_MASK}<br>
     * - {@link BT747Constants#RCR_BUTTON_MASK}<br>
     * - {@link BT747Constants#RCR_APP0_MASK}<br>
     * - {@link BT747Constants#RCR_APP1_MASK}<br>
     * - {@link BT747Constants#RCR_APP2_MASK}<br>
     * - {@link BT747Constants#RCR_APP3_MASK}<br>
     * - {@link BT747Constants#RCR_APP4_MASK}<br>
     * - {@link BT747Constants#RCR_APP5_MASK}<br>
     * - {@link BT747Constants#RCR_APP6_MASK}<br>
     * - {@link BT747Constants#RCR_APP7_MASK}<br>
     * - {@link BT747Constants#RCR_APP8_MASK}<br>
     * - {@link BT747Constants#RCR_APP9_MASK}<br>
     * - {@link BT747Constants#RCR_APPY_MASK}<br>
     * - {@link BT747Constants#RCR_APPZ_MASK}<br>
     * - {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final static int TRKPT_RCR = 87;
    /**
     * The 'Valid' filter mask for the current track filter.
     * 
     * The filter mask to set for the validity filter. Use the following
     * constants:<br>
     * - {@link BT747Constants#VALID_NO_FIX_MASK} <br>
     * - {@link BT747Constants#VALID_SPS_MASK} <br>
     * - {@link BT747Constants#VALID_DGPS_MASK} <br>
     * - {@link BT747Constants#VALID_PPS_MASK} <br>
     * - {@link BT747Constants#VALID_RTK_MASK} <br>
     * - {@link BT747Constants#VALID_FRTK_MASK} <br>
     * - {@link BT747Constants#VALID_ESTIMATED_MASK} <br>
     * - {@link BT747Constants#VALID_MANUAL_MASK} <br>
     * - {@link BT747Constants#VALID_SIMULATOR_MASK} <br>
     * -
     * 
     */
    public final static int TRKPT_VALID = 88;
    /**
     * The waypoint RCR mask for the active filter.
     * 
     * - {@link BT747Constants#RCR_TIME_MASK}<br>
     * - {@link BT747Constants#RCR_SPEED_MASK}<br>
     * - {@link BT747Constants#RCR_DISTANCE_MASK}<br>
     * - {@link BT747Constants#RCR_BUTTON_MASK}<br>
     * - {@link BT747Constants#RCR_APP0_MASK}<br>
     * - {@link BT747Constants#RCR_APP1_MASK}<br>
     * - {@link BT747Constants#RCR_APP2_MASK}<br>
     * - {@link BT747Constants#RCR_APP3_MASK}<br>
     * - {@link BT747Constants#RCR_APP4_MASK}<br>
     * - {@link BT747Constants#RCR_APP5_MASK}<br>
     * - {@link BT747Constants#RCR_APP6_MASK}<br>
     * - {@link BT747Constants#RCR_APP7_MASK}<br>
     * - {@link BT747Constants#RCR_APP8_MASK}<br>
     * - {@link BT747Constants#RCR_APP9_MASK}<br>
     * - {@link BT747Constants#RCR_APPY_MASK}<br>
     * - {@link BT747Constants#RCR_APPZ_MASK}<br>
     * - {@link BT747Constants#RCR_ALL_APP_MASK}
     */
    public final static int WAYPT_RCR = 89;

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

        final int initialLen = Settings.getAppSettings().length();
        if (initialLen < SIZE) {
            final StringBuffer s = new StringBuffer(SIZE);
            s.append(Settings.getAppSettings());
            for (int i = SIZE - initialLen; i > 0; i--) {
                // Fill buffer.
                s.append(' ');
            }
            Settings.setAppSettings(s.toString());
        }

        mVersion = getStringOpt(AppSettings.VERSION);
        if ((mVersion.length() == 4) && (mVersion.charAt(1) == '.')) {
            VersionX100 = JavaLibBridge.toInt(mVersion.charAt(0)
                    + mVersion.substring(2, 4));
        }
        updateSettings(VersionX100);
        Generic.addThread(this, true);
    }

    private static String defaultBaseDirPath = "";

    private void updateSettings(final int versionX100) {
        switch (versionX100) {
        case 0:
            setIntOpt(AppSettings.PORTNBR, -1);
            setIntOpt(AppSettings.BAUDRATE, 115200);
            setLocalIntOpt(0, (-1), AppSettings.C_CARD_IDX,
                    AppSettings.C_CARD_SIZE);
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
            setIntOpt(OUTPUTFILESPLITTYPE, 0);
            /* fall through */
        case 3:
            setIntOpt(HEIGHT_CONVERSION_MODE, AppSettings.HEIGHT_AUTOMATIC);
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
            setIntOpt(MIN_RECCOUNT, 0);
            setIntOpt(MAX_RECCOUNT, 0);
            setFloatOpt(MIN_SPEED, 0);
            setFloatOpt(MAX_SPEED, 0);
            setFloatOpt(MIN_DISTANCE, 0);
            setFloatOpt(MAX_DISTANCE, 0);
            setFloatOpt(MAX_PDOP, 0);
            setFloatOpt(MAX_HDOP, 0);
            setFloatOpt(MAX_VDOP, 0);
            setIntOpt(MIN_NSAT, 0);
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
            setIntOpt(AppSettings.DEVICE_PROTOCOL,
                    BT747Constants.PROTOCOL_MTK);
            /* fall through */
        case 37:
            // New field is coded on 4 byte (8 chars)
            setIntOpt(AppSettings.FILETIMEOFFSET,
                    getIntOpt(AppSettings.FILETIMEOFFSETOLD));
            /* fall through */
        case 38:
            setStringOpt(AppSettings.AGPSURL, DUMMY_AGPS_STRING);
            /* fall through */
        case 39:
            setBooleanOpt(AppSettings.IS_USE_PRECISE_GEOID, true);
            /* fall through */
        case 40:
            if (getBooleanOpt(FORCE_HOLUXM241)) {
                setIntOpt(GPSTYPE, BT747Constants.GPS_TYPE_HOLUX_GR245);
            }
            /* If user already set a log type, skip this init. */
            setBooleanOpt(IS_FIRST_CONNECTION_TO_INIT,
                    getIntOpt(GPSTYPE) == BT747Constants.GPS_TYPE_DEFAULT);
            /* fall through */
        case 41:
            setBooleanOpt(NMEAUTC0, false);
            /* fall through */
        case 42:
            setStringOpt(OSMLOGIN, "");
            setStringOpt(OSMPASS, "");
            /* fall through */
        case 43:
            setBooleanOpt(IS_NEW_TRACK_WHEN_LOG_ON, false);
            /* fall through */
        case 44:
            setIntOpt(SPLIT_DISTANCE, 0);
            /* fall through */
        case 45:
            setStringOpt(POS_SRV_HOSTNAME, "localhost");
            setIntOpt(POS_SRV_PORT, 80);
            setStringOpt(POS_SRV_FILE, "");
            setBooleanOpt(POS_SRV_AUTOSTART, false);
            setStringOpt(POS_SRV_USER, "");
            setStringOpt(POS_SRV_PASS, "");
            setIntOpt(POS_SRV_PERIOD, 300);
            /* fall through */
        case 46:
            setIntOpt(GPSTIMEOFFSETQUARTERS,
                    48 + getIntOpt(GPSTIMEOFFSETHOURS_OBSOLETE) * 4);
            /* fall through */
        case 47:
            setStringOpt(EXTCOMMAND, "echo Sample command for %f");
            /* fall through */
        case 48:
            setIntOpt(EXTTYPE, Model.GPX_LOGTYPE); // Changed field size
            setIntOpt(FONTSCALE, 100);
            /* fall through */
        case 49:
            setIntOpt(DISTANCE_CALCULATION_MODE, DISTANCE_CALC_MODE_NONE);
            setStringOpt(AppSettings.VERSION, "0.50");
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
            Generic.debug("Parameter is not Boolean @" + param);
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
            Generic.debug("Parameter is not Boolean @" + param);
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
            switch (param) {
            case CARD:
                return getLocalCard();
            default:
                return getLocalIntOpt(
                        AppSettings.paramsList[param][AppSettings.START_IDX],
                        AppSettings.paramsList[param][AppSettings.SIZE_IDX]);
            }
        } else {
            // TODO: throw something
            Generic.debug("Parameter is not Integer @" + param);
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
            Generic.debug("Parameter is not Integer @" + param);
        }
    }

    public final String getStringOpt(final int param) {
        switch (param) {
        case GOOGLEMAPKEY:
            return getGoogleMapKeyInternal();
        case REPORTFILEBASEPATH:
            return getStringOpt(AppSettings.OUTPUTDIRPATH) + "/"
                    + getStringOpt(AppSettings.REPORTFILEBASE);
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
            Generic.debug("Parameter is not String @" + param);
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
            Generic.debug("Parameter is not String @" + param);
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
     *            The ChunkSize to set as a default.
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
     *            The DownloadTimeOut to set as a default.
     */
    public final void setDownloadTimeOut(final int downloadTimeOut) {
        setLocalIntOpt(0, downloadTimeOut, AppSettings.C_DOWNLOADTIMEOUT_IDX,
                AppSettings.C_DOWNLOADTIMEOUT_SIZE);
    }

    /**
     * @return The default chunk size
     */
    private final int getLocalCard() {
        int card = getLocalIntOpt(AppSettings.C_CARD_IDX,
                AppSettings.C_CARD_SIZE);
        if ((card <= 0) || (card >= 255)) {
            card = -1;
        }
        return card;
    }

    /**
     * Gets the NMEA string types to write to the NMEA output file format.
     * 
     * Bit format using following bit indexes:<br>
     * - {@link BT747Constants#NMEA_SEN_GLL_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_RMC_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_VTG_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_GGA_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_GSA_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_GSV_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_GRS_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_GST_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_MALM_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_MEPH_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_MDGP_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_MDBG_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_ZDA_IDX}<br>
     * - {@link BT747Constants#NMEA_SEN_MCHN_IDX}<br>
     */

    public final int getNMEAset() {
        return getLocalIntOpt(AppSettings.C_NMEASET_IDX,
                AppSettings.C_NMEASET_SIZE);
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
    protected final void setNMEAset(final int formatNMEA) {
        setLocalIntOpt(0, formatNMEA, AppSettings.C_NMEASET_IDX,
                AppSettings.C_NMEASET_SIZE);
    }

    /**
     * @return Returns the solveMacLagProblem.
     */
    public final static boolean isSolveMacLagProblem() {
        return AppSettings.solveMacLagProblem;
    }

    /**
     * Mac systems exhibit an issue and this parameters permits the
     * application to detect that it has to be bypassed.
     * 
     * @param arg
     *            When true, solve the lag problem (on Mac systems).
     */
    public final static void setSolveMacLagProblem(final boolean arg) {
        AppSettings.solveMacLagProblem = arg;
    }

    public final boolean isStoredSetting1() {
        return getStringOpt(AppSettings.SETTING1_NMEA).length() > 15;
    }

    /**
     * Vector of LogFileInfo.
     */
    public final static BT747Vector logFiles = JavaLibBridge
            .getVectorInstance();

    /**
     * Look for the google map site key in a file called "gmapkey.txt" Will
     * look in the output dir first, then in the source dir, then in the
     * settings dir.
     * 
     * @return The google map key
     */
    private final String getGoogleMapKeyInternal() {
        BT747Path path = new BT747Path("");
        String gkey = "";
        int idx;
        boolean notok = true;
        int i = 3;
        int fileIdx = logFiles.size();
        if (fileIdx > 0) {
            i = 4; // Force to 4;

        }
        while (notok && (i >= 0)) {
            switch (i--) {
            case 0:
                // path = CONFIG_FILE_NAME;
                // break;
            case 1:
                BT747Path tmp = getPath(AppSettings.OUTPUTDIRPATH);
                path = tmp.proto(tmp.getPath() + "/");
                break;
            case 2:
                path = getPath(AppSettings.LOGFILEPATH);
                break;
            case 3:
                path = getPath(REPORTFILEBASEPATH);
                break;
            case 4:
                path = ((LogFileInfo) (logFiles.elementAt(--fileIdx)))
                        .getPath();
                if (fileIdx > 0) {
                    i = 4; // Continue in this case of the switch.
                }
                break;
            default:
                break;
            }
            idx = path.getPath().lastIndexOf('/');
            if (idx != -1) {
                path = path.proto(path.getPath().substring(0,
                        path.getPath().lastIndexOf('/')));
            }
            idx = path.getPath().lastIndexOf('\\');
            if (idx != -1) {
                path = path.proto(path.getPath().substring(0,
                        path.getPath().lastIndexOf('\\')));
            }
            try {
                final BT747Path gmapPath = path.proto(path.getPath() + "/"
                        + AppSettings.C_GMAP_KEY_FILENAME);
                // TODO: take into account BT747Path characteristics(card for
                // instance.)
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
     *            the defaultTraversable to set
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
     *            the defaultChunkSize to set
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
     *            the defaultBaseDirPath to set
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
        setIntOpt(
                TRKPT_VALID,
                (0xFFFFFFFF & (~(BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK))));
        setIntOpt(TRKPT_RCR, 0xFFFFFFFF);
        setIntOpt(
                WAYPT_VALID,
                (0xFFFFFFFF & (~(BT747Constants.VALID_NO_FIX_MASK | BT747Constants.VALID_ESTIMATED_MASK))));
        setIntOpt(
                WAYPT_RCR,
                (BT747Constants.RCR_BUTTON_MASK | BT747Constants.RCR_ALL_APP_MASK));
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
        boolean change;
        change = getStringOpt(idx, size).equals(src);
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
        if (change && eventType != 0) {
            postEvent(ModelEvent.SETTING_CHANGE, "" + eventType);
        }
    }

    private final void setLocalIntOpt(final int param, final int src,
            final int idx, final int size) {
        setOpt(param, JavaLibBridge.unsigned2hex(src, size), idx, size);
    }

    private final int getLocalIntOpt(final int idx, final int size) {
        final String str = getStringOpt(idx, size);
        return Conv.hex2SignedInt(str);
    }

    private final void setLocalFloatOpt(final int eventType,
            final float value, final int idx, final int size) {
        setOpt(eventType, JavaLibBridge.unsigned2hex(JavaLibBridge
                .toIntBitwise(value), size), idx, size);
    }

    private final float getLocalFloatOpt(final int idx, final int size) {
        return JavaLibBridge.toFloatBitwise(Conv.hex2Int(getStringOpt(idx,
                size)));
    }

    public final float getFloatOpt(final int param) {
        if ((param < AppSettings.paramsList.length)
                && (AppSettings.paramsList[param][AppSettings.TYPE_IDX] == AppSettings.FLOAT)) {
            return getLocalFloatOpt(
                    AppSettings.paramsList[param][AppSettings.START_IDX],
                    AppSettings.paramsList[param][AppSettings.SIZE_IDX]);
        } else {
            // TODO: throw something
            Generic.debug("Parameter is not Float @" + param);
            return 0.0f;
        }
    }

    protected final void setFloatOpt(final int param, final float value) {
        if ((param < AppSettings.paramsList.length)
                && (AppSettings.paramsList[param][AppSettings.TYPE_IDX] == AppSettings.FLOAT)) {
            setLocalFloatOpt(param, value,
                    AppSettings.paramsList[param][AppSettings.START_IDX],
                    AppSettings.paramsList[param][AppSettings.SIZE_IDX]);
        } else {
            // TODO: throw something
            Generic.debug("Parameter is not Float @" + param);
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

    /*
     * Event posting
     */

    /**
     * Can't use synchronisation here because SuperWaba does not support it.
     * Therefore using semaphores (making things more complex ;( ).
     */

    /**
     * List of listeners.
     */
    private final BT747HashSet listeners = JavaLibBridge.getHashSetInstance();
    private final BT747Semaphore listenerSema = JavaLibBridge
            .getSemaphoreInstance(1);
    /**
     * List of actions regarding listeners. Protected with
     * listenerActionsSema.
     */
    private final BT747Vector listenerActions = JavaLibBridge
            .getVectorInstance();
    private final BT747Semaphore listenerActionsSema = JavaLibBridge
            .getSemaphoreInstance(1);

    private static final class ListenerAction {
        public static final int ADD = 0;
        public static final int REMOVE = 1;
        public static final int EVENT = 2;
        protected ModelListener listener;
        protected int action;
        protected ModelEvent e;

        public ListenerAction(final ModelListener l, final int action) {
            this.listener = l;
            this.action = action;
        }

        public ListenerAction(final ModelEvent e) {
            this.action = ListenerAction.EVENT;
            this.e = e;
        }
    }

    /**
     * Get a path. Introduced so that an override is possible on some systems
     * to take additional parameters into account like the card number.
     * 
     * @param pathType
     *            One of the parameter indexes corresponding to a path entry.
     * @return returns the path for the given path type.
     */
    public BT747Path getPath(final int pathType) {
        return new BT747Path(getStringOpt(pathType));
    }

    /**
     * Indicates if there are changes to the listeners that are waiting in the
     * action list. Protected with listenerActionsSema.
     */
    private boolean hasListenerChangeAction = false;

    /** add a listener to event thrown by this class */
    public final void addListener(final ModelListener l) {
        listenerActionsSema.down();
        listenerActions.addElement(new ListenerAction(l, ListenerAction.ADD));
        hasListenerChangeAction = true;
        listenerActionsSema.up();
    }

    public final void removeListener(final ModelListener l) {
        listenerActionsSema.down();
        listenerActions.addElement(new ListenerAction(l,
                ListenerAction.REMOVE));
        hasListenerChangeAction = true;
        listenerActionsSema.up();
    }

    /**
     * Upon call, listener sema must be reserved already.
     */
    private final void updateListeners() {
        listenerActionsSema.down();
        if (hasListenerChangeAction && listenerActions.size() != 0) {
            // listenerSema.down(); // done in parent
            for (int i = 0; i < listenerActions.size(); /* update in case */) {
                final ListenerAction la = (ListenerAction) listenerActions
                        .elementAt(i);
                switch (la.action) {
                case ListenerAction.ADD:
                    listeners.add(la.listener);
                    // Remove element, no increment of index!
                    listenerActions.removeElementAt(i);
                    break;
                case ListenerAction.REMOVE:
                    listeners.remove(la.listener);
                    listenerActions.removeElementAt(i);
                    break;
                case ListenerAction.EVENT:
                    i++;
                    break;
                default:
                    i++;
                    Generic.debug("Internal problem in ListenerAction");
                }
            }
            // listenerSema.up(); // done in parent
            hasListenerChangeAction = false; // All listener change actions
            // treated
        }
        listenerActionsSema.up();
    }

    protected final void postEvent(final int type, final Object o) {
        postEvent(new ModelEvent(type, o));
    }

    protected final void postEvent(final int type) {
        postEvent(type, null);
    }

    public final void postEvent(final ModelEvent e) {
        listenerActionsSema.down();
        // Generic.debug("Adding "+e);
        listenerActions.addElement(new ListenerAction(e));
        listenerActionsSema.up();
    }

    private final void doEvent(final ModelEvent e) {
        listenerSema.down();
        // Update list of listeners just before posting events.
        // This way the listeners list should be ok.
        final BT747HashSet it = listeners.iterator();
        // Generic.debug("Sending "+e);
        while (it.hasNext()) {
            final ModelListener l = (ModelListener) it.next();
            l.modelEvent(new ModelEvent(e));
        }
        listenerSema.up();
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747Thread#run()
     */
    public void run() {
        updateListeners();
        listenerActionsSema.down();
        // Get current number of events - only handle as many in this 'run'.
        int maxNumEvents = listenerActions.size();
        listenerActionsSema.up();
        ListenerAction la;
        while (maxNumEvents > 0) {
            updateListeners();
            listenerActionsSema.down();
            la = null;
            if (listenerActions.size() > 0) {
                la = (ListenerAction) listenerActions.elementAt(0);
                if (la.action == ListenerAction.EVENT) {
                    listenerActions.removeElementAt(0);
                    maxNumEvents--;
                }
            }
            listenerActionsSema.up();
            if (la != null && la.action == ListenerAction.EVENT) {
                doEvent(la.e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747Thread#started()
     */
    public void started() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.sys.interfaces.BT747Thread#stopped()
     */
    public void stopped() {
        // TODO Auto-generated method stub

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
    private static final int C_OUTPUTFILESPLITTYPE_IDX = AppSettings.C_TRKPT_VALID_IDX
            + AppSettings.C_TRKPT_VALID_SIZE;
    private static final int C_OUTPUTFILESPLITTYPE_SIZE = 1;
    private static final int C_HEIGHT_CONVERSION_MODE_IDX = AppSettings.C_OUTPUTFILESPLITTYPE_IDX
            + AppSettings.C_OUTPUTFILESPLITTYPE_SIZE;
    private static final int C_HEIGHT_CONVERSION_MODE_SIZE = 4;
    private static final int C_LOGAHEAD_IDX = AppSettings.C_HEIGHT_CONVERSION_MODE_IDX
            + AppSettings.C_HEIGHT_CONVERSION_MODE_SIZE;
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
    private static final int C_FILETIMEOFFSETOLD_IDX = AppSettings.C_IMAGEDIR_IDX
            + AppSettings.C_IMAGEDIR_SIZE;
    private static final int C_FILETIMEOFFSETOLD_SIZE = 4;
    private static final int C_TAG_OVERRIDEPOSITIONS_IDX = AppSettings.C_FILETIMEOFFSETOLD_IDX
            + AppSettings.C_FILETIMEOFFSETOLD_SIZE;
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
    private static final int C_FILETIMEOFFSET_IDX = AppSettings.C_DOWNLOAD_DEVICE_IDX
            + AppSettings.C_DOWNLOAD_DEVICE_SIZE;
    private static final int C_FILETIMEOFFSET_SIZE = 8;
    private static final int C_AGPSURL_IDX = AppSettings.C_FILETIMEOFFSET_IDX
            + AppSettings.C_FILETIMEOFFSET_SIZE;
    private static final int C_AGPSURL_SIZE = 256;
    private static final int C_IS_USE_PRECISE_GEOID_IDX = AppSettings.C_AGPSURL_IDX
            + AppSettings.C_AGPSURL_SIZE;
    private static final int C_IS_USE_PRECISE_GEOID_SIZE = 1;
    private static final int C_IS_FIRST_CONNECTION_TO_INIT_IDX = AppSettings.C_IS_USE_PRECISE_GEOID_IDX
            + AppSettings.C_IS_USE_PRECISE_GEOID_SIZE;
    private static final int C_IS_FIRST_CONNECTION_TO_INIT_SIZE = 1;
    private static final int C_NMEAUTC0_IDX = AppSettings.C_IS_FIRST_CONNECTION_TO_INIT_IDX
            + AppSettings.C_IS_FIRST_CONNECTION_TO_INIT_SIZE;
    private static final int C_NMEAUTC0_SIZE = 1;
    private static final int C_OSMLOGIN_IDX = AppSettings.C_NMEAUTC0_IDX
            + AppSettings.C_NMEAUTC0_SIZE;
    private static final int C_OSMLOGIN_SIZE = 20;
    private static final int C_OSMPASS_IDX = AppSettings.C_OSMLOGIN_IDX
            + AppSettings.C_OSMLOGIN_SIZE;
    private static final int C_OSMPASS_SIZE = 20;
    private static final int C_ISNEWTRACKWHENLOGON_IDX = AppSettings.C_OSMPASS_IDX
            + AppSettings.C_OSMPASS_SIZE;
    private static final int C_ISNEWTRACKWHENLOGON_SIZE = 1;
    private static final int C_SPLIT_DISTANCE_IDX = AppSettings.C_ISNEWTRACKWHENLOGON_IDX
            + AppSettings.C_ISNEWTRACKWHENLOGON_SIZE;
    private static final int C_SPLIT_DISTANCE_SIZE = 8;
    private static final int POS_SRV_HOSTNAME_IDX = AppSettings.C_SPLIT_DISTANCE_IDX
            + AppSettings.C_SPLIT_DISTANCE_SIZE;
    private static final int POS_SRV_HOSTNAME_SIZE = 80;
    private static final int POS_SRV_PORT_IDX = AppSettings.POS_SRV_HOSTNAME_IDX
            + AppSettings.POS_SRV_HOSTNAME_SIZE;
    private static final int POS_SRV_PORT_SIZE = 4;
    //
    private static final int POS_SRV_FILE_IDX = AppSettings.POS_SRV_PORT_IDX
            + AppSettings.POS_SRV_PORT_SIZE;
    private static final int POS_SRV_FILE_SIZE = 80;
    //
    private static final int POS_SRV_AUTOSTART_IDX = AppSettings.POS_SRV_FILE_IDX
            + AppSettings.POS_SRV_FILE_SIZE;
    private static final int POS_SRV_AUTOSTART_SIZE = 1;
    //
    private static final int POS_SRV_USER_IDX = AppSettings.POS_SRV_AUTOSTART_IDX
            + AppSettings.POS_SRV_AUTOSTART_SIZE;
    private static final int POS_SRV_USER_SIZE = 20;
    //
    private static final int POS_SRV_PASS_IDX = AppSettings.POS_SRV_USER_IDX
            + AppSettings.POS_SRV_USER_SIZE;
    private static final int POS_SRV_PASS_SIZE = 20;
    //
    private static final int POS_SRV_PERIOD_IDX = AppSettings.POS_SRV_PASS_IDX
            + AppSettings.POS_SRV_PASS_SIZE;
    private static final int POS_SRV_PERIOD_SIZE = 8;

    private static final int GPSTIMEOFFSETQUARTERS_IDX = AppSettings.POS_SRV_PERIOD_IDX
            + AppSettings.POS_SRV_PERIOD_SIZE;
    private static final int GPSTIMEOFFSETQUARTERS_SIZE = 4;
    private static final int EXTCOMMAND_IDX = AppSettings.GPSTIMEOFFSETQUARTERS_IDX
            + AppSettings.GPSTIMEOFFSETQUARTERS_SIZE;
    private static final int EXTCOMMAND_SIZE = 256;
    private static final int EXTTYPE_IDX = AppSettings.EXTCOMMAND_IDX
            + AppSettings.EXTCOMMAND_SIZE;
    private static final int EXTTYPE_SIZE = 1;
    private static final int FONTSCALE_IDX = AppSettings.EXTTYPE_IDX
            + AppSettings.EXTTYPE_SIZE;
    private static final int FONTSCALE_SIZE = 2;
    private static final int DISTANCE_CALC_MODE_IDX = AppSettings.FONTSCALE_IDX
            + AppSettings.FONTSCALE_SIZE;
    private static final int DISTANCE_CALC_MODE_SIZE = 4;
    private static final int C_NEXT_IDX = AppSettings.DISTANCE_CALC_MODE_IDX
            + AppSettings.DISTANCE_CALC_MODE_SIZE;

    // Next lines just to add new items faster using replace functions
    private static final int C_NEXT_SIZE = 4;
    private static final int C_NEW_NEXT_IDX = AppSettings.C_NEXT_IDX
            + AppSettings.C_NEXT_SIZE;

    public static final int SIZE = C_NEW_NEXT_IDX;

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
            { AppSettings.INT, AppSettings.GPSTIMEOFFSETHOURS_OBSOLETE,
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
                    AppSettings.C_GPX_LINK_INFO_IDX,
                    AppSettings.C_GPX_LINK_INFO_SIZE },
            { AppSettings.BOOL, AppSettings.IS_GPX_1_1,
                    AppSettings.C_IS_GPX_1_1_IDX,
                    AppSettings.C_IS_GPX_1_1_SIZE },
            { AppSettings.INT, AppSettings.DEVICE_PROTOCOL,
                    AppSettings.C_DOWNLOAD_DEVICE_IDX,
                    AppSettings.C_DOWNLOAD_DEVICE_SIZE },
            { AppSettings.INT, AppSettings.FILETIMEOFFSETOLD,
                    AppSettings.C_FILETIMEOFFSETOLD_IDX,
                    AppSettings.C_FILETIMEOFFSETOLD_SIZE },
            { AppSettings.STRING, AppSettings.AGPSURL,
                    AppSettings.C_AGPSURL_IDX, AppSettings.C_AGPSURL_SIZE },
            { AppSettings.BOOL, AppSettings.IS_USE_PRECISE_GEOID,
                    AppSettings.C_IS_USE_PRECISE_GEOID_IDX,
                    AppSettings.C_IS_USE_PRECISE_GEOID_SIZE },
            { AppSettings.BOOL, AppSettings.IS_FIRST_CONNECTION_TO_INIT,
                    AppSettings.C_IS_FIRST_CONNECTION_TO_INIT_IDX,
                    AppSettings.C_IS_FIRST_CONNECTION_TO_INIT_SIZE },
            { AppSettings.BOOL, AppSettings.NMEAUTC0,
                    AppSettings.C_NMEAUTC0_IDX, AppSettings.C_NMEAUTC0_SIZE },
            { AppSettings.STRING, AppSettings.OSMLOGIN,
                    AppSettings.C_OSMLOGIN_IDX, AppSettings.C_OSMLOGIN_SIZE },
            { AppSettings.STRING, AppSettings.OSMPASS,
                    AppSettings.C_OSMPASS_IDX, AppSettings.C_OSMPASS_SIZE },
            { AppSettings.BOOL, AppSettings.IS_NEW_TRACK_WHEN_LOG_ON,
                    AppSettings.C_ISNEWTRACKWHENLOGON_IDX,
                    AppSettings.C_ISNEWTRACKWHENLOGON_SIZE },
            { AppSettings.INT, AppSettings.SPLIT_DISTANCE,
                    AppSettings.C_SPLIT_DISTANCE_IDX,
                    AppSettings.C_SPLIT_DISTANCE_SIZE },
            { AppSettings.STRING, AppSettings.POS_SRV_HOSTNAME,
                    AppSettings.POS_SRV_HOSTNAME_IDX,
                    AppSettings.POS_SRV_HOSTNAME_SIZE },
            { AppSettings.INT, AppSettings.POS_SRV_PORT,
                    AppSettings.POS_SRV_PORT_IDX,
                    AppSettings.POS_SRV_PORT_SIZE },
            { AppSettings.STRING, AppSettings.POS_SRV_FILE,
                    AppSettings.POS_SRV_FILE_IDX,
                    AppSettings.POS_SRV_FILE_SIZE },
            { AppSettings.BOOL, AppSettings.POS_SRV_AUTOSTART,
                    AppSettings.POS_SRV_AUTOSTART_IDX,
                    AppSettings.POS_SRV_AUTOSTART_SIZE },
            { AppSettings.STRING, AppSettings.POS_SRV_USER,
                    AppSettings.POS_SRV_USER_IDX,
                    AppSettings.POS_SRV_USER_SIZE },
            { AppSettings.STRING, AppSettings.POS_SRV_PASS,
                    AppSettings.POS_SRV_PASS_IDX,
                    AppSettings.POS_SRV_PASS_SIZE },
            { AppSettings.INT, AppSettings.POS_SRV_PERIOD,
                    AppSettings.POS_SRV_PERIOD_IDX,
                    AppSettings.POS_SRV_PERIOD_SIZE },
            { AppSettings.INT, AppSettings.GPSTIMEOFFSETQUARTERS,
                    AppSettings.GPSTIMEOFFSETQUARTERS_IDX,
                    AppSettings.GPSTIMEOFFSETQUARTERS_SIZE },
            { AppSettings.STRING, AppSettings.EXTCOMMAND,
                    AppSettings.EXTCOMMAND_IDX, AppSettings.EXTCOMMAND_SIZE },
            { AppSettings.INT, AppSettings.EXTTYPE, AppSettings.EXTTYPE_IDX,
                    AppSettings.EXTTYPE_SIZE },
            { AppSettings.INT, AppSettings.FONTSCALE,
                    AppSettings.FONTSCALE_IDX, AppSettings.FONTSCALE_SIZE },
            { AppSettings.INT, AppSettings.CARD, AppSettings.C_CARD_IDX,
                    AppSettings.C_CARD_SIZE },
            { AppSettings.INT, AppSettings.OUTPUTFILESPLITTYPE,
                    AppSettings.C_OUTPUTFILESPLITTYPE_IDX,
                    AppSettings.C_OUTPUTFILESPLITTYPE_SIZE },
            { AppSettings.FLOAT, AppSettings.MIN_SPEED,
                    AppSettings.C_minSpeed_IDX, AppSettings.C_minSpeed_SIZE },
            { AppSettings.FLOAT, AppSettings.MAX_SPEED,
                    AppSettings.C_maxSpeed_IDX, AppSettings.C_maxSpeed_SIZE },
            { AppSettings.INT, AppSettings.MIN_NSAT,
                    AppSettings.C_minNSAT_IDX, AppSettings.C_minNSAT_SIZE },
            { AppSettings.INT, AppSettings.MIN_RECCOUNT,
                    AppSettings.C_minRecCount_IDX,
                    AppSettings.C_minRecCount_SIZE },
            { AppSettings.INT, AppSettings.MAX_RECCOUNT,
                    AppSettings.C_maxRecCount_IDX,
                    AppSettings.C_maxRecCount_SIZE },
            { AppSettings.FLOAT, AppSettings.MIN_DISTANCE,
                    AppSettings.C_minDist_IDX, AppSettings.C_minDist_SIZE },
            { AppSettings.FLOAT, AppSettings.MAX_VDOP,
                    AppSettings.C_maxVDOP_IDX, AppSettings.C_maxVDOP_SIZE },
            { AppSettings.FLOAT, AppSettings.MAX_PDOP,
                    AppSettings.C_maxPDOP_IDX, AppSettings.C_maxPDOP_SIZE },
            { AppSettings.FLOAT, AppSettings.MAX_HDOP,
                    AppSettings.C_maxHDOP_IDX, AppSettings.C_maxHDOP_SIZE },
            { AppSettings.FLOAT, AppSettings.MAX_DISTANCE,
                    AppSettings.C_MAX_DISTANCE_IDX,
                    AppSettings.C_MAX_DISTANCE_SIZE },
            { AppSettings.INT, AppSettings.HEIGHT_CONVERSION_MODE,
                    AppSettings.C_HEIGHT_CONVERSION_MODE_IDX,
                    AppSettings.C_HEIGHT_CONVERSION_MODE_SIZE },
            { AppSettings.INT, AppSettings.DISTANCE_CALCULATION_MODE,
                    AppSettings.DISTANCE_CALC_MODE_IDX,
                    AppSettings.DISTANCE_CALC_MODE_SIZE },
            { AppSettings.INT, AppSettings.WAYPT_VALID,
                    AppSettings.C_WAYPT_VALID_IDX,
                    AppSettings.C_WAYPT_VALID_SIZE },
            { AppSettings.INT, AppSettings.TRKPT_RCR,
                    AppSettings.C_TRKPT_RCR_IDX, AppSettings.C_TRKPT_RCR_SIZE },
            { AppSettings.INT, AppSettings.TRKPT_VALID,
                    AppSettings.C_TRKPT_VALID_IDX,
                    AppSettings.C_TRKPT_VALID_SIZE },
            { AppSettings.INT, AppSettings.WAYPT_RCR,
                    AppSettings.C_WAYPT_RCR_IDX, AppSettings.C_WAYPT_RCR_SIZE },

    // End of list
    };
}
