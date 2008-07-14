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
import bt747.ui.Event;

/**
 * @author Mario De Weerd
 * @author Herbert Geus (initial code for saving settings on WindowsCE)
 */
public class AppSettings {
    private static String CONFIG_FILE_NAME =
    // #if RXTX java.lang.System.getProperty("bt747_settings", // bt747_settings
    // or default value
    // #if RXTX ((java.lang.System.getProperty("user.home").length()!=0) ?
    // #if RXTX
    // java.lang.System.getProperty("user.home")+java.lang.System.getProperty("file.separator")+"SettingsBT747.pdb":(

    (bt747.sys.Settings.platform.startsWith("Win32")
            || bt747.sys.Settings.platform.startsWith("Windows") || bt747.sys.Settings.platform
            .startsWith("Mac")
    // #if RXTX || java.lang.System.getProperty("os.name").startsWith("Mac")
    ) ? "SettingsBT747.pdb" : "/My Documents/BT747/SettingsBT747.pdb"
    // #if RXTX )
    // #if RXTX ))
    ;
    // private static final String CONFIG_FILE_NAME =
    // java.lang.System.getProperty("bt747_settings",
    // ((java.lang.System.getProperty("user.home").length()!=0) ?
    // java.lang.System.getProperty("user.home")+java.lang.System.getProperty("file.separator")+"SettingsBT747.pdb":(
    //
    // (bt747.sys.Settings.platform.startsWith("Win32")||bt747.sys.Settings.platform.startsWith("Windows")
    // ||bt747.sys.Settings.platform.startsWith("Mac")
    // || java.lang.System.getProperty("os.name").startsWith("Mac")
    // ) ?
    // "SettingsBT747.pdb"
    // :
    // "/My Documents/BT747/SettingsBT747.pdb"
    // )
    // ) )
    // ;

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
    private static final int C_LOGAHEAD_IDX = C_WGS84_TO_MSL_IDX + C_WGS84_TO_MSL_SIZE;
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
    private static final int C_NEXT_IDX = C_OUTPUTLOGCONDITIONS_IDX
            + C_OUTPUTLOGCONDITIONS_SIZE;
    // Next lines just to add new items faster using replace functions
    private static final int C_NEXT_SIZE = 4;
    private static final int C_NEW_NEXT_IDX = C_NEXT_IDX + C_NEXT_SIZE;
    private static final int C_DEFAULT_DEVICE_TIMEOUT = 3500; // ms
    private static final int C_DEFAULT_LOG_REQUEST_AHEAD = 3;

    private String baseDirPath;
    private String logFile;
    private String reportFileBase;

    private boolean solveMacLagProblem = false;

    public AppSettings() {
        init();
        // bt747.sys.Vm.debug(CONFIG_FILE_NAME);
        // #if RXTX
        // bt747.sys.Vm.debug(java.lang.System.getProperty("bt747_settings"));
    }

    private boolean isWin32LikeDevice() {
        return bt747.sys.Settings.platform.startsWith("WindowsCE")
                || bt747.sys.Settings.platform.startsWith("PocketPC")
                || (bt747.sys.Settings.platform.startsWith("Win32") && Settings.onDevice)
                || !Settings.isWaba();
    }

    public final void init() {
        String mVersion;
        int VersionX100 = 0;
        if ((Settings.getAppSettings() == null)
                || (Settings.getAppSettings().length() < 100)
        // #if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
        ) {
            Settings.setAppSettings(new String(new byte[2048]));
            if (isWin32LikeDevice()
            // #if RXTX ||
            // java.lang.System.getProperty("os.name").startsWith("Mac")
            // #if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
            // #if RXTX ||java.lang.System.getProperty("user.home").length()!=0
            ) {
                int readLength = 0;

                // bt747.sys.Vm.debug("on Device "+bt747.sys.Settings.platform);
                // bt747.sys.Vm.debug("loading config file "+CONFIG_FILE_NAME);
                File preferencesFile = new File("");
                try {
                    preferencesFile = new File(CONFIG_FILE_NAME, File.READ_ONLY);
                    readLength = preferencesFile.getSize();
                    if (readLength >= 100) {
                        byte[] appSettingsArray = new byte[2048];

                        preferencesFile.readBytes(appSettingsArray, 0,
                                readLength);
                        Settings.setAppSettings(new String(appSettingsArray));
                    }
                } catch (Exception e) {
                    // Vm.debug("Exception new log create");
                }
                try {
                    preferencesFile.close();
                } catch (Exception e) {

                }
            }
        }
        // #if RXTX
        // if(Convert.toInt(java.lang.System.getProperty("bt747_Mac_solvelag",java.lang.System.getProperty("os.name").startsWith("Mac")?"1":"0"))==1)
        // {
        // #if RXTX solveMacLagProblem=true;
        // #if RXTX }

        mVersion = getStringOpt(C_VERSION_IDX, C_VERSION_SIZE);
        if ((mVersion.length() == 4) && (mVersion.charAt(1) == '.')) {
            getSettings();
            VersionX100 = Convert.toInt(mVersion.charAt(0)
                    + mVersion.substring(2, 4));
        }
        updateSettings(VersionX100);
    }

    private void updateSettings(final int versionX100) {
        switch (versionX100) {
        case 0:
            setPortnbr(-1);
            setBaudRate(115200);
            setCard(-1);
            if (bt747.sys.Settings.platform.startsWith("Palm")) {
                setBaseDirPath("/Palm");
            } else if (isWin32LikeDevice()) {
                if (bt747.io.File.getCardVolumePath() == null) {
                    setBaseDirPath("/EnterYourDir");
                } else {
                    setBaseDirPath(File.getCardVolumePath());
                }
            } else {
                setBaseDirPath("/BT747");
            }

            setLogFileRelPath("BT747log.bin");
            setReportFileBase("GPSDATA");
            setStartupOpenPort(false);
            setChunkSize(bt747.sys.Settings.onDevice ? 220 : 0x10000);
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
            setGpxTrkSegWhenBig(false);
            /* fall through */
        case 10:
            setGpsDecode(true);
            /* fall through */
        case 11:
            setColorInvalidTrack("FF0000");
            /* fall through */
        case 12:
            setTraversableFocus(Settings.onDevice
                    && (!bt747.sys.Settings.platform.startsWith("Palm")));
            /* fall through */
        case 13:
            setRecordNbrInLogs(false);
            /* fall through */
        case 14:
            setForceHolux241(false);
            /* fall through */
        case 15:
            /* Value interpretation changed */
            setDistConditionSetting1(getDistConditionSetting1() * 10);
            /* fall through */
        case 16:
            setImperial(false);
            /* fall through */
        case 18:
            setFreeTextPort("");
            /* fall through */
        case 19:
            setBinDecoder(0);
            setGPSType(0);
        case 20:
            setOutputLogConditions(false);
            /* fall through */

            /* Must be last line in case (not 'default') */
            setStringOpt(0, "0.21", C_VERSION_IDX, C_VERSION_SIZE);
        default:
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

    /**
     * Save all the user settings to disk.
     */
    protected final void saveSettings() {
        if (isWin32LikeDevice()
        // #if RXTX || java.lang.System.getProperty("os.name").startsWith("Mac")
        // #if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
        ) {
            // bt747.sys.Vm.debug("on Device "+bt747.sys.Settings.platform);
            // bt747.sys.Vm.debug("saving config file "+CONFIG_FILE_NAME);
            File preferencesFile = new File("");
            try {
                File m_Dir = new File(CONFIG_FILE_NAME.substring(0,
                        CONFIG_FILE_NAME.lastIndexOf('/')), File.DONT_OPEN);
                if (!m_Dir.exists()) {
                    m_Dir.createDir();
                }
            } catch (Exception e) {
                // Vm.debug("Exception new log delete");
                // e.printStackTrace();
            }
            try {
                preferencesFile = new File(CONFIG_FILE_NAME, File.DONT_OPEN);
                if (preferencesFile.exists()) {
                    preferencesFile.delete();
                }
            } catch (Exception e) {
                // Vm.debug("Exception new log delete");
            }
            try {
                preferencesFile = new File(CONFIG_FILE_NAME, File.CREATE);
                preferencesFile.close();
                preferencesFile = new File(CONFIG_FILE_NAME, File.READ_WRITE);
                preferencesFile.writeBytes(
                        Settings.getAppSettings().getBytes(), 0, Settings
                                .getAppSettings().length());
                preferencesFile.close();
            } catch (Exception e) {
                // Vm.debug("Exception new log create");
                e.printStackTrace();
            }
            // bt747.sys.Vm.debug("saved config file length
            // "+Settings.appSettings.length());
        }
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

    private final void setIntOpt(final int eventType, final int src,
            final int idx, final int size) {
        setOpt(eventType, Convert.unsigned2hex(src, size), idx, size);
    }

    private final int getIntOpt(final int idx, final int size) {
        return Conv.hex2Int(getStringOpt(idx, size));
    }

    private final void setBooleanOpt(final int eventType, final boolean value,
            final int idx, final int size) {
        setStringOpt(eventType, (value ? "1" : "0"), idx, size);
    }

    private final boolean getBooleanOpt(final int idx, final int size) {
        return getIntOpt(idx, size) == 1;
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

    /**
     * @return Returns the portnbr.
     */
    public final int getPortnbr() {
        return getIntOpt(C_PORTNBR_IDX, C_PORTNBR_SIZE);
    }

    /**
     * @param portnbr
     *            The portnbr to set.
     */
    public final void setPortnbr(final int portnbr) {
        setIntOpt(0, portnbr, C_PORTNBR_IDX, C_PORTNBR_SIZE);
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
        return getIntOpt(C_BAUDRATE_IDX, C_BAUDRATE_SIZE);
    }

    /**
     * @param baudRate
     *            The Baud rate to set as a default.
     */
    public final void setBaudRate(final int baudRate) {
        setIntOpt(0, baudRate, C_BAUDRATE_IDX, C_BAUDRATE_SIZE);
    }

    /**
     * Get the chunk size (or the default).
     * 
     * @return The default chunk size
     */
    public final int getChunkSize() {
        // ChunkSize must be multiple of 2
        int chunkSize = getIntOpt(C_CHUNKSIZE_IDX, C_CHUNKSIZE_SIZE) & 0xFFFFFFFE;
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
        setIntOpt(0, chunkSize, C_CHUNKSIZE_IDX, C_CHUNKSIZE_SIZE);
    }

    /**
     * @return The default chunk size
     */
    public final int getDownloadTimeOut() {
        int DownloadTimeOut = getIntOpt(C_DOWNLOADTIMEOUT_IDX,
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
        setIntOpt(0, downloadTimeOut, C_DOWNLOADTIMEOUT_IDX,
                C_DOWNLOADTIMEOUT_SIZE);
    }

    /**
     * @return The default chunk size
     */
    public final int getCard() {
        int card = getIntOpt(C_CARD_IDX, C_CARD_SIZE);
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
        setIntOpt(0, card, C_CARD_IDX, C_CARD_SIZE);
    }

    /**
     * @return The time off set (UTC vs. local time)
     */
    public final int getTimeOffsetHours() {
        int timeOffsetHours = getIntOpt(C_TIMEOFFSETHOURS_IDX,
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
        setIntOpt(0, timeOffsetHours, C_TIMEOFFSETHOURS_IDX,
                C_TIMEOFFSETHOURS_SIZE);
    }

    public final boolean getStartupOpenPort() {
        return getBooleanOpt(C_OPENSTARTUP_IDX, C_OPENSTARTUP_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setStartupOpenPort(final boolean value) {
        setBooleanOpt(0, value, C_OPENSTARTUP_IDX, C_OPENSTARTUP_SIZE);
    }

    /**
     * The location of the logFile
     * 
     */
    /**
     * @return Returns the logFile full path.
     */
    public final String getLogFilePath() {
        return baseDirPath + File.separatorChar + logFile;
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
        return getIntOpt(C_WAYPT_RCR_IDX, C_WAYPT_RCR_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setWayPtRCR(final int value) {
        setIntOpt(0, value, C_WAYPT_RCR_IDX, C_WAYPT_RCR_SIZE);
        postEvent(ModelEvent.WAY_RCR_CHANGE);
    }

    public final int getWayPtValid() {
        return getIntOpt(C_WAYPT_VALID_IDX, C_WAYPT_VALID_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setWayPtValid(final int value) {
        setIntOpt(0, value, C_WAYPT_VALID_IDX, C_WAYPT_VALID_SIZE);
        postEvent(ModelEvent.WAY_VALID_CHANGE);
    }

    public final int getTrkPtRCR() {
        return getIntOpt(C_TRKPT_RCR_IDX, C_TRKPT_RCR_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setTrkPtRCR(final int value) {
        setIntOpt(0, value, C_TRKPT_RCR_IDX, C_TRKPT_RCR_SIZE);
        postEvent(ModelEvent.TRK_RCR_CHANGE);
    }

    public final int getTrkPtValid() {
        return getIntOpt(C_TRKPT_VALID_IDX, C_TRKPT_VALID_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setTrkPtValid(final int value) {
        setIntOpt(0, value, C_TRKPT_VALID_IDX, C_TRKPT_VALID_SIZE);
        postEvent(ModelEvent.TRK_VALID_CHANGE);
    }

    public final int getFileSeparationFreq() {
        return getIntOpt(C_ONEFILEPERDAY_IDX, C_ONEFILEPERDAY_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    public final void setOutputFileSplitType(final int value) {
        setIntOpt(0, value, C_ONEFILEPERDAY_IDX, C_ONEFILEPERDAY_SIZE);
    }

    public final boolean isConvertWGS84ToMSL() {
        return getBooleanOpt(C_WGS84_TO_MSL_IDX, C_WGS84_TO_MSL_SIZE);
    }

    /**
     * @param value
     *            true - Setting is to convert the WGS84 height to MSL height.
     */
    public final void setConvertWGS84ToMSL(final boolean value) {
        setBooleanOpt(0, value, C_WGS84_TO_MSL_IDX, C_WGS84_TO_MSL_SIZE);
    }

    public final boolean getAdvFilterActive() {
        return getBooleanOpt(C_ADVFILTACTIVE_IDX, C_ADVFILTACTIVE_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    protected final void setAdvFilterActive(final boolean value) {
        setBooleanOpt(0, value, C_ADVFILTACTIVE_IDX, C_ADVFILTACTIVE_SIZE);
    }

    public final int getLogRequestAhead() {
        return getIntOpt(C_LOGAHEAD_IDX, C_LOGAHEAD_SIZE);
    }

    /**
     * @param value
     *            The default value for opening the port.
     */
    protected final void setLogRequestAhead(final int value) {
        setIntOpt(0, value, C_LOGAHEAD_IDX, C_LOGAHEAD_SIZE);
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
        return getIntOpt(C_NMEASET_IDX, C_NMEASET_SIZE);
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
        setIntOpt(0, formatNMEA, C_NMEASET_IDX, C_NMEASET_SIZE);
    }

    public final boolean getGpxUTC0() {
        return getBooleanOpt(C_GPXUTC0_IDX, C_GPXUTC0_SIZE);
    }

    protected final void setGpxUTC0(final boolean value) {
        setBooleanOpt(0, value, C_GPXUTC0_IDX, C_GPXUTC0_SIZE);
    }

    public final boolean getGpsDecode() {
        return getBooleanOpt(C_DECODEGPS_IDX, C_DECODEGPS_SIZE);
    }

    protected final void setGpsDecode(final boolean value) {
        setBooleanOpt(0, value, C_DECODEGPS_IDX, C_DECODEGPS_SIZE);
    }

    public final boolean getGpxTrkSegWhenBig() {
        return getBooleanOpt(C_GPXTRKSEGBIG_IDX, C_GPXTRKSEGBIG_SIZE);
    }

    protected final void setGpxTrkSegWhenBig(final boolean value) {
        setBooleanOpt(0, value, C_GPXTRKSEGBIG_IDX, C_GPXTRKSEGBIG_SIZE);
    }

    public final int getTrkSep() {
        return getIntOpt(C_TRKSEP_IDX, C_TRKSEP_SIZE);
    }

    protected final void setTrkSep(final int value) {
        setIntOpt(0, value, C_TRKSEP_IDX, C_TRKSEP_SIZE);
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
        return getIntOpt(C_maxRecCount_IDX, C_maxRecCount_SIZE);
    }

    /**
     * @param maxRecCnt
     *            The maxRecCnt to setFilter.
     */
    protected final void setFilterMaxRecCount(final int maxRecCnt) {
        setIntOpt(0, maxRecCnt, C_maxRecCount_IDX, C_maxRecCount_SIZE);
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
        return getIntOpt(C_minNSAT_IDX, C_minNSAT_SIZE);
    }

    /**
     * @param minNSAT
     *            The minNSAT to setFilter.
     */
    protected final void setFilterMinNSAT(final int minNSAT) {
        setIntOpt(0, minNSAT, C_minNSAT_IDX, C_minNSAT_SIZE);
    }

    /**
     * @return Returns the minRecCnt.
     */
    public final int getFilterMinRecCount() {
        return getIntOpt(C_minRecCount_IDX, C_minRecCount_SIZE);
    }

    /**
     * @param minRecCnt
     *            The minRecCnt to setFilter.
     */
    protected final void setFilterMinRecCount(final int minRecCnt) {
        setIntOpt(0, minRecCnt, C_minRecCount_IDX, C_minRecCount_SIZE);
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
    public final boolean isSolveMacLagProblem() {
        return solveMacLagProblem;
    }

    /**
     * @param solveMacLagProblem
     *            The solveMacLagProblem to set.
     */
    protected final void setSolveMacLagProblem(final boolean solveMacLagProblem) {
        this.solveMacLagProblem = solveMacLagProblem;
    }

    /**
     * @return Returns the solveMacLagProblem.
     */
    public final boolean isTraversableFocus() {
        return getBooleanOpt(C_ISTRAVERSABLE_IDX, C_ISTRAVERSABLE_SIZE);
    }

    /**
     * @param traversableFocus
     *            The traversableFocus to set.
     */
    protected final void setTraversableFocus(final boolean traversableFocus) {
        setBooleanOpt(0, traversableFocus, C_ISTRAVERSABLE_IDX,
                C_ISTRAVERSABLE_SIZE);
    }

    // - Log conditions;
    // - Time, Speed, Distance[3x 4 byte]
    // - Log format; [4 byte]
    // - Fix period [4 byte]
    // - SBAS / TEST SBAS [byte, byte]
    // - Log overwrite/STOP [byte, byte]
    // - NMEA output [18 byte]
    // - Total: 42 byte

    protected final void setTimeConditionSetting1(final int value) {
        setIntOpt(0, value, C_SETTING1_TIME_IDX, C_SETTING1_TIME_SIZE);
    }

    public final int getTimeConditionSetting1() {
        return getIntOpt(C_SETTING1_TIME_IDX, C_SETTING1_TIME_SIZE);
    }

    protected final void setSpeedConditionSetting1(final int value) {
        setIntOpt(0, value, C_SETTING1_SPEED_IDX, C_SETTING1_SPEED_SIZE);
    }

    public final int getSpeedConditionSetting1() {
        return getIntOpt(C_SETTING1_SPEED_IDX, C_SETTING1_SPEED_SIZE);
    }

    protected final void setDistConditionSetting1(final int value) {
        setIntOpt(0, value, C_SETTING1_DIST_IDX, C_SETTING1_DIST_SIZE);
    }

    public final int getDistConditionSetting1() {
        return getIntOpt(C_SETTING1_DIST_IDX, C_SETTING1_DIST_SIZE);
    }

    protected final void setFixSetting1(final int value) {
        setIntOpt(0, value, C_SETTING1_FIX_IDX, C_SETTING1_FIX_SIZE);
    }

    public final int getFixSetting1() {
        return getIntOpt(C_SETTING1_FIX_IDX, C_SETTING1_FIX_SIZE);
    }

    protected final void setLogFormatConditionSetting1(final int value) {
        setIntOpt(0, value, C_SETTING1_LOG_FORMAT_IDX,
                C_SETTING1_LOG_FORMAT_SIZE);
    }

    public final int getLogFormatSetting1() {
        return getIntOpt(C_SETTING1_LOG_FORMAT_IDX, C_SETTING1_LOG_FORMAT_SIZE);
    }

    protected final void setSBASSetting1(final boolean value) {
        setBooleanOpt(0, value, C_SETTING1_SBAS_IDX, C_SETTING1_SBAS_SIZE);
    }

    public final boolean getSBASSetting1() {
        return getBooleanOpt(C_SETTING1_SBAS_IDX, C_SETTING1_SBAS_SIZE);
    }

    protected final void setDGPSSetting1(final int value) {
        setIntOpt(0, value, C_SETTING1_DGPS_IDX, C_SETTING1_DGPS_SIZE);
    }

    public final int getDPGSSetting1() {
        return getIntOpt(C_SETTING1_DGPS_IDX, C_SETTING1_DGPS_SIZE);
    }

    public final boolean getTestSBASSetting1() {
        return getBooleanOpt(C_SETTING1_TEST_IDX, C_SETTING1_TEST_SIZE);
    }

    protected final void setTestSBASSetting1(final boolean value) {
        setBooleanOpt(0, value, C_SETTING1_TEST_IDX, C_SETTING1_TEST_SIZE);
    }

    public final boolean getLogOverwriteSetting1() {
        return getBooleanOpt(C_SETTING1_LOG_OVR_IDX, C_SETTING1_LOG_OVR_SIZE);
    }

    protected final void setLogOverwriteSetting1(final boolean value) {
        setBooleanOpt(0, value, C_SETTING1_LOG_OVR_IDX, C_SETTING1_LOG_OVR_SIZE);
    }

    public final String getNMEASetting1() {
        return getStringOpt(C_SETTING1_NMEA_IDX, C_SETTING1_NMEA_SIZE);
    }

    protected final void setNMEASetting1(final String value) {
        setStringOpt(0, value, C_SETTING1_NMEA_IDX, C_SETTING1_NMEA_SIZE);
    }

    public final boolean getRecordNbrInLogs() {
        return getBooleanOpt(C_RECORDNBR_IN_LOGS_IDX, C_RECORDNBR_IN_LOGS_SIZE);
    }

    protected final void setRecordNbrInLogs(final boolean value) {
        setBooleanOpt(0, value, C_RECORDNBR_IN_LOGS_IDX,
                C_RECORDNBR_IN_LOGS_SIZE);
    }

    /**
     * Get the forced interpretation status of data as holux data.
     * 
     * @return true - interpretation of data is as if data is from holux device.
     */
    public final boolean getForceHolux241() {
        return getBooleanOpt(C_HOLUX241_IDX, C_HOLUX241_SIZE);
    }

    /**
     * Set the forced interpretation of data as holux data.
     * 
     * @param value
     *            true - Interprete data is as if data is from holux device.
     */
    protected final void setForceHolux241(final boolean value) {
        setBooleanOpt(0, value, C_HOLUX241_IDX, C_HOLUX241_SIZE);
    }

    public final boolean getImperial() {
        return getBooleanOpt(C_IMPERIAL_IDX, C_IMPERIAL_SIZE);
    }

    protected final void setImperial(final boolean value) {
        setBooleanOpt(0, value, C_IMPERIAL_IDX, C_IMPERIAL_SIZE);
    }

    public final boolean isStoredSetting1() {
        return getNMEASetting1().length() > 15;
    }

    protected final void setBinDecoder(final int value) {
        setIntOpt(0, value, C_BIN_DECODER_IDX, C_BIN_DECODER_SIZE);
    }

    public final int getBinDecoder() {
        return getIntOpt(C_BIN_DECODER_IDX, C_BIN_DECODER_SIZE);
    }

    public final int getGPSType() {
        return getIntOpt(C_GPSType_IDX, C_GPSType_SIZE);
    }

    protected final void setGPSType(final int value) {
        setIntOpt(0, value, C_GPSType_IDX, C_GPSType_SIZE);
    }

    public final boolean getOutputLogConditions() {
        return getBooleanOpt(C_OUTPUTLOGCONDITIONS_IDX,
                C_OUTPUTLOGCONDITIONS_SIZE);
    }

    protected final void setOutputLogConditions(final boolean value) {
        setBooleanOpt(0, value, C_OUTPUTLOGCONDITIONS_IDX,
                C_OUTPUTLOGCONDITIONS_SIZE);
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
                path = CONFIG_FILE_NAME;
                break;
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
            Event e = new Event(l, type, o);
            l.newEvent(e);
        }
    }

    protected final void postEvent(final int type) {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ModelListener l = (ModelListener) it.next();
            Event e = new Event(l, type, null);
            l.newEvent(e);
        }
    }

}
