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
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package gps;

import bt747.model.Model;
import bt747.sys.Generic;

/**
 * Constants for the iBlue 747 (BT747) device
 * 
 * @author Mario De Weerd
 */
public final class BT747Constants { // dev as in device

    /**
     * String description of the log format items of the iBlue 747 device.<br>
     * Entries are in order. The entry position corresponds to the bit
     * position in the log format 'byte'.
     */
    public static String[] logFmtItems = Txt.logFmtItems;
    /** Index of bit for log format setting */
    public static final int FMT_UTC_IDX = 0;
    /** Index of bit for log format setting */
    public static final int FMT_VALID_IDX = 1;
    /** Index of bit for log format setting */
    public static final int FMT_LATITUDE_IDX = 2;
    /** Index of bit for log format setting */
    public static final int FMT_LONGITUDE_IDX = 3;
    /** Index of bit for log format setting */
    public static final int FMT_HEIGHT_IDX = 4;
    /** Index of bit for log format setting */
    public static final int FMT_SPEED_IDX = 5;
    /** Index of bit for log format setting */
    public static final int FMT_HEADING_IDX = 6;
    /** Index of bit for log format setting */
    public static final int FMT_DSTA_IDX = 7;
    /** Index of bit for log format setting */
    public static final int FMT_DAGE_IDX = 8;
    /** Index of bit for log format setting */
    public static final int FMT_PDOP_IDX = 9;
    /** Index of bit for log format setting */
    public static final int FMT_HDOP_IDX = 10;
    /** Index of bit for log format setting */
    public static final int FMT_VDOP_IDX = 11;
    /** Index of bit for log format setting */
    public static final int FMT_NSAT_IDX = 12;
    public static final int FMT_MAX_SATS = 32; // Guess (maximum)
    /** Index of bit for log format setting */
    public static final int FMT_SID_IDX = 13;
    /** Index of bit for log format setting */
    public static final int FMT_ELEVATION_IDX = 14;
    /** Index of bit for log format setting */
    public static final int FMT_AZIMUTH_IDX = 15;
    /** Index of bit for log format setting */
    public static final int FMT_SNR_IDX = 16;
    /** Index of bit for log format setting */
    public static final int FMT_RCR_IDX = 17;
    /** Index of bit for log format setting */
    public static final int FMT_MILLISECOND_IDX = 18;
    /** Index of bit for log format setting */
    public static final int FMT_DISTANCE_IDX = 19;
    /** Index of bit for log format setting */
    public static final int FMT_ROYALTEKNEW_IDX = 20;
    /** Index of bit for log format setting */
    public static final int FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX = 31;

    /**
     * Size for each item of the log format of the iBlue 747.<br>
     * <b>The log record length is variable is satelite information is
     * logged:</b><br>
     * SID, ELEVATION, AZIMUTH and SNR are repeated NSAT times.<br>
     * <br>
     * Entries are in order. The entry position corresponds to the bit
     * position in the log format 'byte'.
     */
    public static final byte[] logFmtByteSizes = { 4, // "UTC", // = 0x00001
            // // // 0
            2, // "VALID", // = 0x00002 // 1
            8, // "LATITUDE", // = 0x00004 // 2
            8, // "LONGITUDE",// = 0x00008 // 3
            4, // "HEIGHT", // = 0x00010 // 4
            4, // "SPEED", // = 0x00020 // 5
            4, // "HEADING", // = 0x00040 // 6
            2, // "DSTA", // = 0x00080 // 7
            4, // "DAGE", // = 0x00100 // 8
            2, // "PDOP", // = 0x00200 // 9
            2, // "HDOP", // = 0x00400 // A
            2, // "VDOP", // = 0x00800 // B
            2, // "NSAT", // = 0x01000 // C
            4, // "SID", // = 0x02000 // D
            2, // "ELEVATION",// = 0x04000 // E
            2, // "AZIMUTH", // = 0x08000 // F
            2, // "SNR", // = 0x10000 // 10
            2, // "RCR", // = 0x20000 // 11
            2, // "MILISECOND",// = 0x40000 // 12
            8, // "DISTANCE" // = 0x80000 // 13
            1, // 14  //Royaltek new ???
            0, // 15
            0, // 16
            0, // 17
            0, // 18
            0, // 19
            0, // 1A
            0, // 1B
            0, // 1C
            0, // 1D
            0, // 1E
            0, // 1F // Log points with valid fix only
    };

    public static final byte[] logFmtByteSizesHolux = { 4, // "UTC", // = //
            // 0x00001 // 0
            2, // "VALID", // = 0x00002 // 1
            4, // "LATITUDE", // = 0x00004 // 2
            4, // "LONGITUDE",// = 0x00008 // 3
            3, // "HEIGHT", // = 0x00010 // 4
            4, // "SPEED", // = 0x00020 // 5
            4, // "HEADING", // = 0x00040 // 6
            2, // "DSTA", // = 0x00080 // 7
            4, // "DAGE", // = 0x00100 // 8
            2, // "PDOP", // = 0x00200 // 9
            2, // "HDOP", // = 0x00400 // A
            2, // "VDOP", // = 0x00800 // B
            2, // "NSAT", // = 0x01000 // C
            4, // "SID", // = 0x02000 // D
            2, // "ELEVATION",// = 0x04000 // E
            2, // "AZIMUTH", // = 0x08000 // F
            2, // "SNR", // = 0x10000 // 10
            2, // "RCR", // = 0x20000 // 11
            2, // "MILISECOND",// = 0x40000 // 12
            8, // "DISTANCE" // = 0x80000 // 13
            0, // 14
            0, // 15
            0, // 16
            0, // 17
            0, // 18
            0, // 19
            0, // 1A
            0, // 1B
            0, // 1C
            0, // 1D
            0, // 1E
            0, // 1F // Log points with valid fix only
    };

    public static final int RCR_TIME_MASK = 0x01;
    public static final int RCR_SPEED_MASK = 0x02;
    public static final int RCR_DISTANCE_MASK = 0x04;
    public static final int RCR_BUTTON_MASK = 0x08;
    public static final int RCR_APP0_MASK = 0x0010;
    public static final int RCR_APP1_MASK = 0x0020;
    public static final int RCR_APP2_MASK = 0x0040;
    public static final int RCR_APP3_MASK = 0x0080;
    public static final int RCR_APP4_MASK = 0x0100;
    public static final int RCR_APP5_MASK = 0x0200;
    public static final int RCR_APP6_MASK = 0x0400;
    public static final int RCR_APP7_MASK = 0x0800;
    public static final int RCR_APP8_MASK = 0x1000;
    public static final int RCR_APP9_MASK = 0x2000;
    public static final int RCR_APPY_MASK = 0x4000;
    public static final int RCR_APPZ_MASK = 0x8000;
    public static final int RCR_ALL_APP_MASK = 0xFFF0;

    public static final String[] C_STR_RCR = Txt.C_STR_RCR;
    public static final int C_RCR_COUNT = 16;

    // PMTK182 commands/replies.
    /** Set a parameter concerning the log (1). */
    public static final int PMTK_LOG_SET = 1;
    /** Get a query concerning the log (2) */
    public static final int PMTK_LOG_Q = 2;
    /** Received data concerning the log (3). */
    public static final int PMTK_LOG_DT = 3; // REPLY
    /** Switch logging on. (4). */
    public static final int PMTK_LOG_ON = 4;
    /** Switch logging off. (5). */
    public static final int PMTK_LOG_OFF = 5;
    /** Erase log. (6). */
    public static final int PMTK_LOG_ERASE = 6;
    /**
     * (7)
     */
    public static final int PMTK_LOG_Q_LOG = 7;
    public static final int PMTK_LOG_DT_LOG = 8; // REPLY.
    public static final int PMTK_LOG_INIT = 9;
    public static final int PMTK_LOG_ENABLE = 10;
    public static final int PMTK_LOG_DISABLE = 11;
    public static final int PMTK_LOG_WRITE = 12;

    // PMTK182,1,DATA (DATA = what parameter to set/read/replied)
    // PMTK182,1,DATA,CMD (DATA = what parameter to set/read/replied, CMD =
    // extra param)

    /** User specific log position (1). */
    public static final int PMTK_LOG_USER = 1;
    /** Indicates flash (erase) status (1). */
    public static final int PMTK_LOG_FLASH_STAT = 1;
    /** User initiated log point, takes RCR as parameter. (2) */
    public static final int PMTK_LOG_FORMAT = 2;
    public static final int PMTK_LOG_TIME_INTERVAL = 3;
    public static final int PMTK_LOG_DISTANCE_INTERVAL = 4;
    public static final int PMTK_LOG_SPEED_INTERVAL = 5;
    public static final int PMTK_LOG_REC_METHOD = 6;
    /**
     * Logger Status.
     * <table>
     * <tr>
     * <th>bit 11</th>
     * <td>Logging is full.</td>
     * <td>
     * 
     * @see {@link #PMTK_LOG_STATUS_LOGISFULL_MASK}</td>
     *      </tr>
     *      <tr>
     *      <th>bit 10</th> <td>Logging needs initialisation (format).</td>
     *      <td>
     * @see {@link #PMTK_LOG_STATUS_LOGMUSTINIT_MASK} </td>
     *      </tr>
     *      <tr>
     *      <th>bit 9 </th> <td>Logging is enabled.</td> <td>
     * @see {@link #PMTK_LOG_STATUS_DISABLED_MASK}</td>
     *      </tr>
     *      <tr>
     *      <th>bit 8</th> <td>Logging is enabled.</td> <td>
     * @see {@link #PMTK_LOG_STATUS_LOGENABLED_MASK}</td>
     *      </tr>
     *      <tr>
     *      <th>bit 2</th> <td>Logging is in stop or overwrite mode. </td> 
     *      <td>
     * @see {@link #PMTK_LOG_STATUS_LOGSTOP_OVER_MASK}</td>
     *      </tr>
     *      *
     *      <tr>
     *      <th>bit 2</th> <td>Logging is on/off</td> <td>
     * @see {@link #PMTK_LOG_STATUS_LOGONOF_MASK}</td>
     *      </tr>
     *      </table>
     * 
     * 
     */
    public static final int PMTK_LOG_LOG_STATUS = 7;
    /** Logger Memory used in bytes - 8 */
    public static final int PMTK_LOG_MEM_USED = 8; // bit 2 = logging on/off
    public static final int PMTK_LOG_FLASH = 9; // Requires extra param
    public static final int PMTK_LOG_NBR_LOG_PTS = 10;
    public static final int PMTK_LOG_FLASH_SECTORS = 11;
    public static final int PMTK_LOG_VERSION = 12;

    // Other MTK commands
    public static final int PMTK_TEST = 000;
    public static final int PMTK_ACK = 001;
    public static final int PMTK_SYS_MSG = 010;
    public static final int PMTK_CMD_HOT_START = 101;
    public static final int PMTK_CMD_WARM_START = 102;
    public static final int PMTK_CMD_COLD_START = 103;
    public static final int PMTK_CMD_FULL_COLD_START = 104;
    public static final int PMTK_CMD_EPO_CLEAR = 127;
    public static final int PMTK_CMD_LOG = 182;
    public static final int PMTK_SET_NMEA_BAUD_RATE = 251;
    /** Set binary mode used in AGPS - will be tried for other too */
    public static final int PMTK_SET_BIN_MODE = 253;
    /* REPLIES: SET - Set values same as futher below, but 3XX */
    public static final int PMTK_API_SET_FIX_CTL = 300;
    public static final int PMTK_API_SET_DGPS_MODE = 301;
    public static final int PMTK_API_SET_SBAS = 313;
    /**
     * Define NMEA output string periods.
     */
    public static final int PMTK_API_SET_NMEA_OUTPUT = 314;
    public static final int PMTK_API_SET_SBAS_TEST = 319;
    public static final int PMTK_API_SET_PWR_SAV_MODE = 320;
    public static final int PMTK_API_SET_DATUM = 330;
    public static final int PMTK_API_SET_DATUM_ADVANCE = 331;
    public static final int PMTK_API_SET_USER_OPTION = 390;
    public static final int PMTK_API_SET_BT_MAC_ADDR = 392;
    /* REPLIES: Q - Same as above, but 4XX in stead of 3XX */
    public static final int PMTK_API_Q_FIX_CTL = 400;
    public static final int PMTK_API_Q_DGPS_MODE = 401;
    public static final int PMTK_API_Q_SBAS = 413;
    public static final int PMTK_API_Q_NMEA_OUTPUT = 414;
    public static final int PMTK_API_Q_SBAS_TEST = 419;
    public static final int PMTK_API_Q_PWR_SAV_MOD = 420;
    public static final int PMTK_API_Q_DATUM = 430;
    public static final int PMTK_API_Q_DATUM_ADVANCE = 431;
    public static final int PMTK_API_Q_GET_USER_OPTION = 490;
    public static final int PMTK_API_Q_BT_MAC_ADDR = 492;
    /* REPLIES: DT - Same as above, but 5XX in stead of 4XX */
    public static final int PMTK_DT_FIX_CTL = 500;
    public static final int PMTK_DT_DGPS_MODE = 501;
    public static final int PMTK_DT_SBAS = 513;
    public static final int PMTK_DT_NMEA_OUTPUT = 514;
    public static final int PMTK_DT_SBAS_TEST = 519;
    public static final int PMTK_DT_PWR_SAV_MODE = 520;
    public static final int PMTK_DT_DATUM = 530;
    public static final int PMTK_DT_FLASH_USER_OPTION = 590;
    public static final int PMTK_DT_BT_MAC_ADDR = 592;

    /* Special requests * SW ? */
    public static final int PMTK_Q_DGPS_INFO = 602;
    /** Query device version (604 0x25C) */
    public static final int PMTK_Q_VERSION = 604;
    /** Get EPO date range (607 0x261) */
    public static final int PMTK_Q_EPO_INFO = 607;
    /**
     * Requests release data 605.
     */
    public static final int PMTK_Q_RELEASE = 605;

    public static final int PMTK_DT_DGPS_INFO = 702;
    public static final int PMTK_DT_VERSION = 704;
    public static final int PMTK_DT_RELEASE = 705;
    /**
     * Response to EPO_INFO query.<br>
     * Answer Example<br>
     * $PMTK707,28,1511,518400,1512,496800,1511,540000,1511,540000*1C<br>
     * <ol>
     * <li>$PMTK707 = AGPS Status</li>
     * <li>28 = Number of the effective data?</li>
     * <li>1511 = GPS Weeks of validity start</li>
     * <li>518400 = GPS Seconds of validity start</li>
     * <li>1512 = GPS Weeks of validity end</li>
     * <li>496800 = GPS Seconds of validity end</li>
     * <li>1511 = GPS Weeks of ??1</li>
     * <li>540000 = GPS Seconds of ??1</li>
     * <li>1511 = GPS Weeks of ??2</li>
     * <li>540000 = GPS Seconds of ??2</li>
     * </ol>
     */
    public static final int PMTK_DT_EPO_INFO = 707;
    /** Send EPO data to device (722 0x2D2) */
    public static final int PMTK_SET_EPO_DATA = 722;

    /**
     * Parameter 1 of PMTK_ACK reply. Packet invalid.
     */
    public static final int PMTK_ACK_INVALID = 0;
    /**
     * Parameter 1 of PMTK_ACK reply. Packet not supported by device.
     */
    public static final int PMTK_ACK_UNSUPPORTED = 1;
    /**
     * Parameter 1 of PMTK_ACK reply. Packet supported, but action failed.
     */
    public static final int PMTK_ACK_FAILED = 2;
    /**
     * Parameter 1 of PMTK_ACK reply. Packet success.
     */
    public static final int PMTK_ACK_SUCCEEDED = 3;

    public static final String PMTK_LOG_ERASE_YES_STR = "1";

    // PMTK182,1,DATA (DATA_STR = "" + (DATAwhat parameter to
    // set/read/replied)

    public static final String PMTK_LOG_USER_STR = ""
            + BT747Constants.PMTK_LOG_USER;
    public static final String PMTK_LOG_FLASH_STAT_STR = ""
            + BT747Constants.PMTK_LOG_FLASH_STAT;
    public static final String PMTK_LOG_FORMAT_STR = ""
            + BT747Constants.PMTK_LOG_FORMAT;
    public static final String PMTK_LOG_TIME_INTERVAL_STR = ""
            + BT747Constants.PMTK_LOG_TIME_INTERVAL;
    public static final String PMTK_LOG_DISTANCE_INTERVAL_STR = ""
            + BT747Constants.PMTK_LOG_DISTANCE_INTERVAL;
    public static final String PMTK_LOG_SPEED_INTERVAL_STR = ""
            + BT747Constants.PMTK_LOG_SPEED_INTERVAL;
    public static final String PMTK_LOG_REC_METHOD_STR = ""
            + BT747Constants.PMTK_LOG_REC_METHOD;
    public static final String PMTK_LOG_LOG_STATUS_STR = ""
            + BT747Constants.PMTK_LOG_LOG_STATUS;

    public static final String PMTK_LOG_MEM_USED_STR = ""
            + BT747Constants.PMTK_LOG_MEM_USED; // bit
    // 2 =
    // logging
    // on/off
    public static final String PMTK_LOG_FLASH_STR = ""
            + BT747Constants.PMTK_LOG_FLASH;
    public static final String PMTK_LOG_NBR_LOG_PTS_STR = ""
            + BT747Constants.PMTK_LOG_NBR_LOG_PTS;
    public static final String PMTK_LOG_FLASH_SECTORS_STR = ""
            + BT747Constants.PMTK_LOG_FLASH_SECTORS;
    public static final String PMTK_LOG_VERSION_STR = ""
            + BT747Constants.PMTK_LOG_VERSION;

    /** Mask for bit in log status indicating that log is active */
    public static final int PMTK_LOG_STATUS_LOGISFULL_MASK = 0x800; // bit 11
    public static final int PMTK_LOG_STATUS_LOGMUSTINIT_MASK = 0x400; // bit
    // 10
    public static final int PMTK_LOG_STATUS_LOGDISABLED_MASK = 0x200; // bit
    // 9
    public static final int PMTK_LOG_STATUS_LOGENABLED_MASK = 0x100; // bit
    // 8
    public static final int PMTK_LOG_STATUS_LOGSTOP_OVER_MASK = 0x04; // bit
    // 2
    public static final int PMTK_LOG_STATUS_LOGONOF_MASK = 0x02; // bit 1

    // Other MTK commands
    public static final String PMTK_TEST_STR = "000"; // "" + PMTK_TEST;
    public static final String PMTK_ACK_STR = "001"; // "" + PMTK_ACK;
    public static final String PMTK_SYS_MSG_STR = "010"; // "" +
    // PMTK_SYS_MSG;
    public static final String PMTK_CMD_HOT_START_STR = ""
            + BT747Constants.PMTK_CMD_HOT_START;
    public static final String PMTK_CMD_WARM_START_STR = ""
            + BT747Constants.PMTK_CMD_WARM_START;
    public static final String PMTK_CMD_COLD_START_STR = ""
            + BT747Constants.PMTK_CMD_COLD_START;
    public static final String PMTK_CMD_FULL_COLD_START_STR = ""
            + BT747Constants.PMTK_CMD_FULL_COLD_START;
    public static final String PMTK_CMD_LOG_STR = ""
            + BT747Constants.PMTK_CMD_LOG;
    public static final String PMTK_SET_NMEA_BAUD_RATE_STR = ""
            + BT747Constants.PMTK_SET_NMEA_BAUD_RATE;
    public static final String PMTK_SET_BIN_MODE_STR = ""
            + +BT747Constants.PMTK_SET_BIN_MODE;
    public static final String PMTK_API_SET_FIX_CTL_STR = ""
            + BT747Constants.PMTK_API_SET_FIX_CTL;
    public static final String PMTK_API_SET_DGPS_MODE_STR = ""
            + BT747Constants.PMTK_API_SET_DGPS_MODE;
    public static final String PMTK_API_SET_SBAS_STR = ""
            + BT747Constants.PMTK_API_SET_SBAS;
    public static final String PMTK_API_SET_NMEA_OUTPUT_STR = ""
            + BT747Constants.PMTK_API_SET_NMEA_OUTPUT;
    public static final String PMTK_API_SET_SBAS_TEST_STR = ""
            + BT747Constants.PMTK_API_SET_SBAS_TEST;
    public static final String PMTK_API_SET_PWR_SAV_MODE_STR = ""
            + BT747Constants.PMTK_API_SET_PWR_SAV_MODE;
    public static final String PMTK_API_SET_DATUM_STR = ""
            + BT747Constants.PMTK_API_SET_DATUM;
    public static final String PMTK_API_SET_DATUM_ADVANCE_STR = ""
            + BT747Constants.PMTK_API_SET_DATUM_ADVANCE;
    public static final String PMTK_API_SET_USER_OPTION_STR = ""
            + BT747Constants.PMTK_API_SET_USER_OPTION;
    public static final String PMTK_API_Q_FIX_CTL_STR = ""
            + BT747Constants.PMTK_API_Q_FIX_CTL;
    public static final String PMTK_API_Q_DGPS_MODE_STR = ""
            + BT747Constants.PMTK_API_Q_DGPS_MODE;
    public static final String PMTK_API_Q_SBAS_STR = ""
            + BT747Constants.PMTK_API_Q_SBAS;
    public static final String PMTK_API_Q_NMEA_OUTPUT_STR = ""
            + BT747Constants.PMTK_API_Q_NMEA_OUTPUT;
    public static final String PMTK_API_Q_SBAS_TEST_STR = ""
            + BT747Constants.PMTK_API_Q_SBAS_TEST;
    public static final String PMTK_API_Q_PWR_SAV_MOD_STR = ""
            + BT747Constants.PMTK_API_Q_PWR_SAV_MOD;
    public static final String PMTK_API_Q_DATUM_STR = ""
            + BT747Constants.PMTK_API_Q_DATUM;
    public static final String PMTK_API_Q_DATUM_ADVANCE_STR = ""
            + BT747Constants.PMTK_API_Q_DATUM_ADVANCE;
    public static final String PMTK_API_GET_USER_OPTION_STR = ""
            + BT747Constants.PMTK_API_Q_GET_USER_OPTION;
    public static final String PMTK_DT_FIX_CTL_STR = ""
            + BT747Constants.PMTK_DT_FIX_CTL;
    public static final String PMTK_DT_DGPS_MODE_STR = ""
            + BT747Constants.PMTK_DT_DGPS_MODE;
    public static final String PMTK_DT_SBAS_STR = ""
            + BT747Constants.PMTK_DT_SBAS;
    public static final String PMTK_DT_NMEA_OUTPUT_STR = ""
            + BT747Constants.PMTK_DT_NMEA_OUTPUT;
    public static final String PMTK_DT_SBAS_TEST_STR = ""
            + BT747Constants.PMTK_DT_SBAS_TEST;
    public static final String PMTK_DT_PWR_SAV_MODE_STR = ""
            + BT747Constants.PMTK_DT_PWR_SAV_MODE;
    public static final String PMTK_DT_DATUM_STR = ""
            + BT747Constants.PMTK_DT_DATUM;
    public static final String PMTK_DT_FLASH_USER_OPTION_STR = ""
            + BT747Constants.PMTK_DT_FLASH_USER_OPTION;
    public static final String PMTK_Q_VERSION_STR = ""
            + BT747Constants.PMTK_Q_VERSION;
    public static final String PMTK_Q_RELEASE_STR = ""
            + BT747Constants.PMTK_Q_RELEASE;
    public static final String PMTK_Q_DGPS_INFO_STR = ""
            + BT747Constants.PMTK_Q_DGPS_INFO;

    /**
     * Parameter 1 of PMTK_ACK reply. Packet invalid.
     */
    public static final String PMTK_ACK_INVALID_STR = ""
            + BT747Constants.PMTK_ACK_INVALID;
    /**
     * Parameter 1 of PMTK_ACK reply. Packet not supported by device.
     */
    public static final String PMTK_ACK_UNSUPPORTED_STR = ""
            + BT747Constants.PMTK_ACK_UNSUPPORTED;
    /**
     * Parameter 1 of PMTK_ACK reply. Packet supported, but action failed.
     */
    public static final String PMTK_ACK_FAILED_STR = ""
            + BT747Constants.PMTK_ACK_FAILED;
    /**
     * Parameter 1 of PMTK_ACK reply. Packet success.
     */
    public static final String PMTK_ACK_SUCCEEDED_STR = ""
            + BT747Constants.PMTK_ACK_SUCCEEDED;

    /*************************************************************************
     * Holux specific
     */

    public static final String HOLUX_MAIN_CMD = "HOLUX241,";
    public static final int HOLUX_API_SET_CONN = 1;
    public static final int HOLUX_API_SET_DISCONN = 2;
    public static final int HOLUX_API_Q_FIRMWARE_VERSION = 3;

    public static final int HOLUX_API_SET_NAME = 4;
    public static final int HOLUX_API_Q_NAME = 5;
    public static final int HOLUX_API_KEEP_ALIVE = 6;
    public static final int HOLUX_API_Q_HW_VERSION = 7;

    public static final int HOLUX_API_SET_TZ_OFFSET = 9;
    public static final int HOLUX_API_Q_TZ_OFFSET = 10;

    public static final int HOLUX_API_DT_FIRMWARE_VERSION = 3;
    public static final int HOLUX_API_DT_NAME = 5;
    public static final int HOLUX_API_DT_HW_VERSION = 7;
    public static final int HOLUX_API_DT_TZ_OFFSET = 10;

    /**
     * Connection protocols.
     */
    public static final int PROTOCOL_MTK = 0;
    public static final int PROTOCOL_WONDE_PROUD = 1;
    public static final int PROTOCOL_PHLX = 2;

    /**
     * Default gps type selection (MTK Logger).
     */
    public static final int GPS_TYPE_DEFAULT = 0;
    /**
     * ITrackU-SirfIII type.
     */
    public static final int GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII = 3;

    /**
     * Specific Holux type.
     * 
     */
    public static final int GPS_TYPE_HOLUX_GR245 = 4;
    /**
     * Specific Holux type.
     * 
     */
    public static final int GPS_TYPE_HOLUX_M241 = 5;

    /**
     * Holux type.
     */
    public static final int GPS_TYPE_GISTEQ_ITRACKU_NEMERIX = 1;
    /**
     * ITrackU-Phototrackr type.
     */
    public static final int GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR = 2;

    /**
     * Get the minimum size for a log record. This does not include the
     * checksum.
     * 
     * @param p_logFormat
     *            The log format of the device.
     * @param holux
     *            True when this must be calculated for a Holux M241 device.
     * 
     * @return Size of the header
     */
    public static final int logRecordMinSize(final int p_logFormat,
            final int gpsType) {
        int total = 0;
        try {
            int bits = p_logFormat;
            int index = 0;
            final byte[] byteSizes = getByteSizes(gpsType);
            do {
                if ((bits & 1) != 0) {
                    switch (index) {
                    case FMT_ELEVATION_IDX:
                    case FMT_AZIMUTH_IDX:
                    case FMT_SNR_IDX:
                        // These fields do not contribute to the minimum size
                        break;
                    default:
                        // Other fields contribute
                        total += byteSizes[index];
                        break;
                    }
                }
                index++;
            } while ((bits >>>= 1) != 0);
        } catch (final Exception e) {
            // Should not happen, but catch it anyway.
            Generic.debug(Txt.C_BAD_LOG_FORMAT, e);
        }
        return total;
    }

    public static final int logRecordAndChecksumSize(final int logFormat,
            final int gpsType, final int sats) {
        int checksumSize;
        switch (gpsType) {
        default:
        case BT747Constants.GPS_TYPE_DEFAULT:
            checksumSize = 2;
            break;
        case BT747Constants.GPS_TYPE_HOLUX_GR245:
        case BT747Constants.GPS_TYPE_HOLUX_M241:
            checksumSize = 1;
            break;
        }
        return checksumSize + logRecordSize(logFormat, gpsType, sats);
    }

    /**
     * Calculate the log record size
     * 
     * @param logFormat
     * @param holux
     *            When true, then the log record size is calculated for a
     *            Holux M241 device.
     * @param sats
     *            Estimated number of satellites per record.
     * @return The estimated record size.
     */
    public static final int logRecordSize(final int logFormat,
            final int gpsType, final int sats) {
        int cnt = 0;
        final byte[] byteSizes = getByteSizes(gpsType);
        if ((logFormat & (1 << BT747Constants.FMT_SID_IDX)) != 0) {
            cnt += byteSizes[BT747Constants.FMT_SID_IDX];
            cnt += (logFormat & (1 << BT747Constants.FMT_ELEVATION_IDX)) != 0 ? byteSizes[BT747Constants.FMT_ELEVATION_IDX]
                    : 0;
            cnt += (logFormat & (1 << BT747Constants.FMT_AZIMUTH_IDX)) != 0 ? byteSizes[BT747Constants.FMT_AZIMUTH_IDX]
                    : 0;
            cnt += (logFormat & (1 << BT747Constants.FMT_SNR_IDX)) != 0 ? byteSizes[BT747Constants.FMT_SNR_IDX]
                    : 0;
            cnt *= sats - 1;
        }
        return cnt + BT747Constants.logRecordMinSize(logFormat, gpsType);
    }

    public static final int logRecordMaxSize(final int p_logFormat,
            final int gpsType) {
        return BT747Constants.logRecordSize(p_logFormat, gpsType,
                BT747Constants.FMT_MAX_SATS);
    }

    private static final byte[] getByteSizes(final int gpsType) {
        switch (gpsType) {
        default:
        case BT747Constants.GPS_TYPE_DEFAULT:
            return BT747Constants.logFmtByteSizes;
        case BT747Constants.GPS_TYPE_HOLUX_GR245:
        case BT747Constants.GPS_TYPE_HOLUX_M241:
            return BT747Constants.logFmtByteSizesHolux;
        }
    }

    public static final int[] logRecordSatOffsetAndSize(final int logFormat,
            final int gpsType) {
        int bits = logFormat;
        int index = 0;
        int total = 0;
        int satRecSize = 0;
        final byte[] byteSizes = getByteSizes(gpsType);
        do {
            if ((bits & 1) != 0) {
                switch (index) {
                case BT747Constants.FMT_LATITUDE_IDX:
                case BT747Constants.FMT_LONGITUDE_IDX:
                    total += byteSizes[index];
                    break;
                case BT747Constants.FMT_HEIGHT_IDX:
                    total += byteSizes[index];
                    break;

                case BT747Constants.FMT_SID_IDX:
                case BT747Constants.FMT_ELEVATION_IDX:
                case BT747Constants.FMT_AZIMUTH_IDX:
                case BT747Constants.FMT_SNR_IDX:
                    satRecSize += byteSizes[index];
                    break;
                case BT747Constants.FMT_RCR_IDX:
                case BT747Constants.FMT_MILLISECOND_IDX:
                case BT747Constants.FMT_DISTANCE_IDX:
                case BT747Constants.FMT_ROYALTEKNEW_IDX:

                    // These fields do not contribute to the sat offset
                    break;
                default:
                    // Other fields contribute
                    total += byteSizes[index];
                    break;
                }
            }
            index++;
        } while ((bits >>>= 1) != 0);
        final int[] result = { total, satRecSize };
        return result;
    }

    /**
     * Get a representation of the model.
     * 
     * @return Textual identification of the device model.
     */
    public static final String modelName(final int md, final String device) {
        String mdStr;
        switch (md) {
        case 0x0000:
        case 0x0001:
        case 0x0013:
        case 0x0051:
            // Can also be Polaris iBT-GPS or Holux M1000
            mdStr = "iBlue 737/Qstarz 810";
            break;
        case 0x0002:
            mdStr = "Qstarz 815/iBlue 747";
            break;
        case 0x0005:
            mdStr = "Holux M-241/QT-1000P";
            break;
        case 0x0007:
            mdStr = "Just Mobile� Bluecard";
            break;
        case 0x0011: // Seen in FCC OUP940760101
        case 0x001B:
            mdStr = "iBlue 747";
            break;
        case 0x001D:
            mdStr = "747/Q1000/BGL-32";
            break;
        case 0x0023:
        case 0x0033:
            mdStr = "Holux M-241";
            break;
        case 0x0035:
            mdStr = "Holux M1000C";
            break;
        case 0x0131:
            mdStr = "EB-85A";
            break;
        case 0x1388:
            mdStr = "757/ZI v1";
            // logMemSize = 8 * 1024 * 1024 / 8; //8Mb -> 1MB
            break;
        // Not needed because other detection works
        // case 0x0415:
        // mdStr = "NCS-NAVI 150+";
        // break;
        case 0x231B:
            mdStr = "ML7";
            break;
        case 0x5202:
            mdStr = "757/ZI v2";
            // logMemSize = 8 * 1024 * 1024 / 8; //8Mb -> 1MB
            break;
        case 0x8300:
            mdStr = "Qstarz BT-1200";
            // logMemSize = 32 * 1024 * 1024 / 8; //32Mb -> 4MB
            break;
        default:
            mdStr = "?"; // Unknown model
        }
        // Recognition based on 'device'
        if (device.length() == 0) {
            // Do nothing
        } else if (device.startsWith("TSI_747A+")) {
            mdStr = "iBlue 747 A+";
        } else if (device.startsWith("MBT-1100")) {
            mdStr = "Royaltek MBT-1100";
        } else if (device.startsWith("TSI747")) {
            mdStr = "iBlue 747";
        } else if (device.startsWith("TSI757")) {
            mdStr = "iBlue 757";
        } else if (device.startsWith("TSI_821")) {
            mdStr = "iBlue 821";
        } else if (device.startsWith("TSI_887")) {
            mdStr = "photoMate887";
        } else if (device.equals("QST1000P")) {
            mdStr = "Qstarz BT-1000P";
        } else if (device.equals("QST1000")) {
            mdStr = "Qstarz BT-1000X";
        } else if (device.equals("QST1300")) {
            mdStr = "Qstarz BT-1300";
        } else if (device.equals("GR-245")) {
            mdStr = "Holux GPSport 245";
        } else if (device.equals("R150+")) {
            mdStr = "NCS-NAVI 150+";
        }
        return mdStr;
    }

    /**
     * Next entries are related to <code>PMTK_API_Q_NMEA_OUTPUT</code> and
     * similar.
     * 
     */

    /**
     * Number of NMEA sentence types.
     */
    public static final int C_NMEA_SEN_COUNT = 19;
    private static final String[] NMEA_STRINGS = { "GLL", // 0 // GPGLL
            // interval -
            // Geographic
            // Position -
            // Latitude
            // longitude
            "RMC", // 1 // GPRMC interval - Recommended Min. specic GNSS
            // sentence
            "VTG", // 2 / / GPVTG interval - Course Over Ground and Ground
            // Speed
            "GGA", // 3 / / GPGGA interval - GPS Fix Data
            "GSA", // 4 / / GPGSA interval - GNSS DOPS and Active Satellites
            "GSV", // 5 / / GPGSV interval - GNSS Satellites in View
            "GRS", // 6 / / GPGRS interval - GNSS Range Residuals
            "GST", // 7 // GPGST interval - GNSS Pseudorange Error Statistics
            "?8", // 8
            "?9", // 9
            "?10", // 10
            "?11", // 11
            "?12", // 12
            "MALM", // 13 // PMTKALM interval - GPS almanac information
            "MEPH", // 14 // PMTKEPH interval - GPS ephemeris information
            "MDGP", // 15 // PMTKDGP interval - GPS differential correction
            // information
            "MDBG", // 16 // PMTKDBG interval - MTK debug information
            "ZDA", // 17 // GPZDA interval - Time & Date
            "MCHN", // 18 // PMTKCHN interval - GPS channel status
    };

    public static final String getNmeaDescription(final int idx) {
        return NMEA_STRINGS[idx];
    }

    /** GPGLL interval - Geographic Position - Latitude longitude. */
    public static final int NMEA_SEN_GLL_IDX = 0;
    /** GPRMC interval - Recommended Min. specic - GNSS sentence. */
    public static final int NMEA_SEN_RMC_IDX = 1;
    /** GPVTG interval - Course Over Ground and Ground Speed. */
    public static final int NMEA_SEN_VTG_IDX = 2;
    /** GPGGA interval - GPS Fix Data. */
    public static final int NMEA_SEN_GGA_IDX = 3;
    /** GPGSA interval - GNSS DOPS and Active Satellites. */
    public static final int NMEA_SEN_GSA_IDX = 4;
    /** GPGSV interval - GNSS Satellites in View. */
    public static final int NMEA_SEN_GSV_IDX = 5;
    /** GPGRS interval - GNSS Range Residuals. */
    public static final int NMEA_SEN_GRS_IDX = 6;
    /** GPGST interval - GNSS Pseudorange Error Statistics. */
    public static final int NMEA_SEN_GST_IDX = 7;
    /** Unknown - Called it type 8. */
    public static final int NMEA_SEN_TYPE8_IDX = 8;
    /** Unknown - Called it type 9. */
    public static final int NMEA_SEN_TYPE9_IDX = 9;
    /** Unknown - Called it type 10. */
    public static final int NMEA_SEN_TYPE10_IDX = 10;
    /** Unknown - Called it type 11. */
    public static final int NMEA_SEN_TYPE11_IDX = 11;
    /** Unknown - Called it type 12. */
    public static final int NMEA_SEN_TYPE12_IDX = 12;
    /** PMTKALM interval - GPS almanac information. */
    public static final int NMEA_SEN_MALM_IDX = 13;
    /** PMTKEPH interval - GPS ephemeris information */
    public static final int NMEA_SEN_MEPH_IDX = 14;
    /** PMTKDGP interval - GPS differential correction information. */
    public static final int NMEA_SEN_MDGP_IDX = 15;
    /** PMTKDBG interval - MTK debug information. */
    public static final int NMEA_SEN_MDBG_IDX = 16;
    /** GPZDA interval - Time & Date. */
    public static final int NMEA_SEN_ZDA_IDX = 17;
    /** PMTKCHN interval - GPS channel status. */
    public static final int NMEA_SEN_MCHN_IDX = 18;
    
    /** Application */
    public static final int NMEA_SEN_WPL_IDX = 19;

    // private static final int SPI_Y2_MAN_ID_MASK = 0xff00;
    // private static final int SPI_Y2_DEV_ID_MASK = 0x00ff;

    /* SPI flash manufacturer ID's */
    // private static final int SPI_MAN_ID_AMD = 0x01;
    // private static final int SPI_MAN_ID_FUJITSU = 0x04;
    // private static final int SPI_MAN_ID_ST_M25P10 = 0x10;
    // private static final int SPI_MAN_ID_ST_M25P20 = 0x11;
    private static final int SPI_MAN_ID_EON = 0x1C;
    // private static final int SPI_MAN_ID_MITSUBISHI = 0x1C;
    // private static final int SPI_MAN_ID_ATMEL = 0x1F;
    // private static final int SPI_MAN_ID_STMICROELECTRONICS = 0x20;
    // private static final int SPI_MAN_ID_CATALYST = 0x31;
    // private static final int SPI_MAN_ID_SYNCMOS = 0x40;
    // private static final int SPI_MAN_ID_INTEL = 0x89;
    // private static final int SPI_MAN_ID_HYUNDAI = 0xAD;
    // private static final int SPI_MAN_ID_SST = 0xBF; // Silicon Storage
    // Technology
    private static final int SPI_MAN_ID_MACRONIX = 0xC2; // MX

    // private static final int SPI_MAN_ID_WINBOND = 0xDA;

    // define MX_29F002 0xB0
    // +/* MX25L chips are SPI, first byte of device id is memory type,
    // + second byte of device id is log(bitsize)-9 */
    // +define MX_25L512 0x2010 /* 2^19 kbit or 2^16 kByte */
    // +define MX_25L1005 0x2011
    // +define MX_25L2005 0x2012
    // +define MX_25L4005 0x2013 /* MX25L4005{,A} */
    // +define MX_25L8005 0x2014
    // +define MX_25L1605 0x2015 /* MX25L1605{,A,D} */
    // +define MX_25L3205 0x2016 /* MX25L3205{,A} */
    // +define MX_25L6405 0x2017 /* MX25L3205{,D} */
    // +define MX_25L1635D 0x2415
    // +define MX_25L3235D 0x2416

    public static final int getFlashSize(final int flashManuProdID) {
        int manufacturer;
        int devType;
        int memSize = 1024 * 1024 * 2; // Default value
        // String flashDesc;

        manufacturer = (flashManuProdID >> 24) & 0xFF;
        devType = (flashManuProdID >> 16) & 0xFF;

        switch (manufacturer) {
        case BT747Constants.SPI_MAN_ID_MACRONIX:
            if ((devType == 0x20) || (devType == 0x24)) {
                // +/* MX25L chips are SPI, first byte of device id is memory
                // type,
                // + second byte of device id is log(bitsize)-9 */
                // +define MX_25L512 0x2010 /* 2^19 kbit or 2^16 kByte */
                // +define MX_25L1005 0x2011
                // +define MX_25L2005 0x2012
                // +define MX_25L4005 0x2013 /* MX25L4005{,A} */
                // +define MX_25L8005 0x2014
                // +define MX_25L1605 0x2015 /* MX25L1605{,A,D} */
                // +define MX_25L3205 0x2016 /* MX25L3205{,A} */
                // +define MX_25L6405 0x2017 /* MX25L3205{,D} */
                // +define MX_25L1635D 0x2415
                // +define MX_25L3235D 0x2416
                memSize = 0x1 << ((flashManuProdID >> 8) & 0xFF);
                // flashDesc = "(MX," + memSize / (1024 * 1024) + "MB)";

            }
            break;
        case BT747Constants.SPI_MAN_ID_EON:
            if ((devType == 0x20)) { // || (DevType == 0x24)) {
                // Supposing the same rule as macronix.
                // Example device: EN25P16
                memSize = 0x1 << ((flashManuProdID >> 8) & 0xFF);
                // flashDesc = "(EON," + memSize / (1024 * 1024) + "MB)";
            }
            break;

        default:
            break;
        }
        return memSize;
    }

    public static final String getFlashDesc(final int flashManuProdID) {
        int manufacturer;
        int devType;
        int memSize = 1024 * 1024 * 2;
        String flashDesc = "";

        manufacturer = (flashManuProdID >> 24) & 0xFF;
        devType = (flashManuProdID >> 16) & 0xFF;

        switch (manufacturer) {
        case BT747Constants.SPI_MAN_ID_MACRONIX:
            if ((devType == 0x20) || (devType == 0x24)) {
                // +/* MX25L chips are SPI, first byte of device id is memory
                // type,
                // + second byte of device id is log(bitsize)-9 */
                // +define MX_25L512 0x2010 /* 2^19 kbit or 2^16 kByte */
                // +define MX_25L1005 0x2011
                // +define MX_25L2005 0x2012
                // +define MX_25L4005 0x2013 /* MX25L4005{,A} */
                // +define MX_25L8005 0x2014
                // +define MX_25L1605 0x2015 /* MX25L1605{,A,D} */
                // +define MX_25L3205 0x2016 /* MX25L3205{,A} */
                // +define MX_25L6405 0x2017 /* MX25L3205{,D} */
                // +define MX_25L1635D 0x2415
                // +define MX_25L3235D 0x2416
                memSize = 0x1 << ((flashManuProdID >> 8) & 0xFF);
                flashDesc = "(MX," + memSize / (1024 * 1024) + "MB)";

            }
            break;
        case BT747Constants.SPI_MAN_ID_EON:
            if ((devType == 0x20)) { // || (DevType == 0x24)) {
                // Supposing the same rule as macronix.
                // Example device: EN25P16
                memSize = 0x1 << ((flashManuProdID >> 8) & 0xFF);
                flashDesc = "(EON," + memSize / (1024 * 1024) + "MB)";
            }
            break;

        default:
            break;
        }
        return flashDesc;
    }

    public static final int VALID_NO_FIX_MASK = 0x0001;
    public static final int VALID_SPS_MASK = 0x0002;
    public static final int VALID_DGPS_MASK = 0x0004;
    public static final int VALID_PPS_MASK = 0x0008;
    public static final int VALID_RTK_MASK = 0x0010;
    public static final int VALID_FRTK_MASK = 0x0020;
    public static final int VALID_ESTIMATED_MASK = 0x0040;
    public static final int VALID_MANUAL_MASK = 0x0080;
    public static final int VALID_SIMULATOR_MASK = 0x0100;

    public static final int NO_ERROR = 0;
    public static final int ERROR_COULD_NOT_OPEN = -1;
    public static final int ERROR_NO_FILES_WERE_CREATED = -2;
    public static final int ERROR_READING_FILE = -3;
    /**
     * The requested output format is unknown.
     */
    public static final int ERROR_UNKNOWN_OUTPUT_FORMAT = -4;
    public final static int HEIGHT_MSL = 0;
    public final static int HEIGHT_WGS84 = 1;
    /**
     * Initialization of references for the height.
     */
    private static final int[][] INIT_REFERENCE_LIST = {
            { Model.CSV_LOGTYPE, BT747Constants.HEIGHT_WGS84 },
            { Model.TRK_LOGTYPE, BT747Constants.HEIGHT_MSL },
            { Model.KML_LOGTYPE, BT747Constants.HEIGHT_MSL },
            { Model.PLT_LOGTYPE, BT747Constants.HEIGHT_MSL },
            { Model.GPX_LOGTYPE, BT747Constants.HEIGHT_MSL },
            { Model.OSM_LOGTYPE, BT747Constants.HEIGHT_MSL },
            { Model.NMEA_LOGTYPE, BT747Constants.HEIGHT_WGS84 },
            { Model.GMAP_LOGTYPE, BT747Constants.HEIGHT_MSL },
            { Model.TRL_LOGTYPE, BT747Constants.HEIGHT_WGS84 },
            { Model.BIN_LOGTYPE, BT747Constants.HEIGHT_WGS84 },
            { Model.SR_LOGTYPE, BT747Constants.HEIGHT_MSL },
            { Model.KMZ_LOGTYPE, BT747Constants.HEIGHT_MSL },
            { Model.ARRAY_LOGTYPE, BT747Constants.HEIGHT_MSL },
            { Model.MULTI_LOGTYPE, BT747Constants.HEIGHT_WGS84 }, };
    /**
     * The reference for the height in the given format
     */
    private final static int[] heightReferenceList = new int[20];

    public final static int getHeightReference(final int type) {
        if ((type >= 0) && (type < BT747Constants.heightReferenceList.length)) {
            return BT747Constants.heightReferenceList[type];
        }
        return BT747Constants.HEIGHT_WGS84;
    }

    static {
        BT747Constants.initValues();
    }

    private final static void initValues() {
        for (int i = 0; i < BT747Constants.INIT_REFERENCE_LIST.length; i++) {
            if (BT747Constants.INIT_REFERENCE_LIST[i][0] < BT747Constants.heightReferenceList.length) {
                BT747Constants.heightReferenceList[BT747Constants.INIT_REFERENCE_LIST[i][0]] = BT747Constants.INIT_REFERENCE_LIST[i][1];
            }
        }
    }

    /**
     * List of existing timezones. To find hour: /4 -12 To find quarter: %4
     * *15
     */
    public final static int[] timeZones = {
    // List of existing timezones.
            48 - 12 * 4, //
            48 - 11 * 4,//
            48 - 10 * 4,//
            48 - 9 * 4 - 2,//
            48 - 9 * 4,//
            48 - 8 * 4,//
            48 - 7 * 4,//
            48 - 6 * 4,//
            48 - 5 * 4,//
            48 - 4 * 4 - 2,//
            48 - 4 * 4,//
            48 - 3 * 4 - 2,//
            48 - 3 * 4,//
            48 - 2 * 4,//
            48 - 1 * 4,//
            48 + 0 * 4,//
            48 + 1 * 4,//
            48 + 2 * 4,//
            48 + 3 * 4,//
            48 + 3 * 4 + 2,//
            48 + 4 * 4,//
            48 + 4 * 4 + 2,//
            48 + 5 * 4,//
            48 + 5 * 4 + 2,//
            48 + 5 * 4 + 3,//
            48 + 6 * 4,//
            48 + 6 * 4 + 2,//
            48 + 7 * 4,//
            48 + 8 * 4,//
            48 + 8 * 4 + 3,//
            48 + 9 * 4,//
            48 + 9 * 4 + 2,//
            48 + 10 * 4,//
            48 + 10 * 4 + 2,//
            48 + 11 * 4,//
            48 + 11 * 4 + 2,//
            48 + 12 * 4,//
            48 + 12 * 4 + 3,//
            48 + 13 * 4,//
            48 + 14 * 4,//
    };

    /**
     * @return list of UTC strings for GUI.
     */
    public final static String[] getUtcStrings(final String prefix) {
        String[] utcStrArr = new String[BT747Constants.timeZones.length];
        for (int i = 0; i < timeZones.length; i++) {
            final int zone = BT747Constants.timeZones[i] - 48;
            final int hour = zone / 4;
            int minutes = (zone % 4) * 15;
            if (minutes < 0) {
                minutes = -minutes;
            }
            String utc;
            utc = prefix;
            if (hour >= 0) {
                utc += "+";
            }
            utc += hour;
            if (minutes != 0) {
                utc += ":" + minutes;
            }
            utcStrArr[i] = utc;
        }
        return utcStrArr;
    }

    /**
     * @param encodedTZ
     * @return Index in UTC string list for the timezone.
     */
    public final static int getUtcIdx(final int encodedTZ) {
        for (int i = 0; i < timeZones.length; i++) {
            if (timeZones[i] >= encodedTZ) {
                return i;
            }
        }
        return 0;
    }
}
