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
package gps;
import waba.sys.Convert;


/** Constants for the iBlue 747 (BT747) device
 * @author Mario De Weerd
 */
public final class BT747_dev {  // dev as in device
    
    /** String description of the log format items of the iBlue 747
     * device.<br>
     * Entries are in order.  The entry position corresponds to the
     * bit position in the log format 'byte'.
     */
    public static final String [] logFmtItems = {
            "UTC",      // = 0x00001    // 0
            "VALID",    // = 0x00002    // 1
            "LATITUDE", // = 0x00004    // 2
            "LONGITUDE",// = 0x00008    // 3
            "HEIGHT",   // = 0x00010    // 4
            "SPEED",    // = 0x00020    // 5
            "HEADING",  // = 0x00040    // 6
            "DSTA",     // = 0x00080    // 7
            "DAGE",     // = 0x00100    // 8
            "PDOP",     // = 0x00200    // 9
            "HDOP",     // = 0x00400    // A
            "VDOP",     // = 0x00800    // B
            "NSAT",     // = 0x01000    // C
            "SID",      // = 0x02000    // D
            "ELEVATION",// = 0x04000    // E
            "AZIMUTH",  // = 0x08000    // F
            "SNR",      // = 0x10000    // 10
            "RCR",      // = 0x20000    // 11
            "MILISECOND",// = 0x40000   // 12
            "DISTANCE"  // = 0x80000    // 13
    };
    /** Index of bit for log format setting */
    public static final int FMT_UTC_IDX =        0;
    /** Index of bit for log format setting */
    public static final int FMT_VALID_IDX =      1;
    /** Index of bit for log format setting */
    public static final int FMT_LATITUDE_IDX =   2;
    /** Index of bit for log format setting */
    public static final int FMT_LONGITUDE_IDX =  3;
    /** Index of bit for log format setting */
    public static final int FMT_HEIGHT_IDX =     4;
    /** Index of bit for log format setting */
    public static final int FMT_SPEED_IDX =      5;
    /** Index of bit for log format setting */
    public static final int FMT_HEADING_IDX =    6;
    /** Index of bit for log format setting */
    public static final int FMT_DSTA_IDX =       7;
    /** Index of bit for log format setting */
    public static final int FMT_DAGE_IDX =       8;
    /** Index of bit for log format setting */
    public static final int FMT_PDOP_IDX =       9;
    /** Index of bit for log format setting */
    public static final int FMT_HDOP_IDX =       10;
    /** Index of bit for log format setting */
    public static final int FMT_VDOP_IDX =       11;
    /** Index of bit for log format setting */
    public static final int FMT_NSAT_IDX =       12;
    /** Index of bit for log format setting */
    public static final int FMT_SID_IDX =        13;
    /** Index of bit for log format setting */
    public static final int FMT_ELEVATION_IDX =  14;
    /** Index of bit for log format setting */
    public static final int FMT_AZIMUTH_IDX =    15;
    /** Index of bit for log format setting */
    public static final int FMT_SNR_IDX =        16;
    /** Index of bit for log format setting */
    public static final int FMT_RCR_IDX =        17;
    /** Index of bit for log format setting */
    public static final int FMT_MILISECOND_IDX = 18;
    /** Index of bit for log format setting */
    public static final int FMT_DISTANCE_IDX =   19;
    
    /** Size for each item of the log format of the iBlue 747.<br>
     * <b>The log record length is variable is satelite information
     * is logged:</b><br>
     * SID, ELEVATION, AZIMUTH and SNR are repeated NSAT times.<br>
     * <br> 
     * Entries are in order.  The entry position corresponds to the
     * bit position in the log format 'byte'.
     */
    // TODO: know about all sizes.  When '0', size is unknown
    protected static final int[] logFmtByteSizes = {
            4, //"UTC",     // = 0x00001    // 0
            2, //"VALID",   // = 0x00002    // 1
            8, //"LATITUDE",    // = 0x00004    // 2
            4, //"LONGITUDE",// = 0x00008   // 3
            4, //"HEIGHT",  // = 0x00010    // 4
            4, //"SPEED",   // = 0x00020    // 5
            4, //"HEADING", // = 0x00040    // 6
            2, //"DSTA",        // = 0x00080    // 7
            4, //"DAGE",        // = 0x00100    // 8
            2, //"PDOP",        // = 0x00200    // 9
            2, //"HDOP",        // = 0x00400    // A
            2, //"VDOP",        // = 0x00800    // B
            2, //"NSAT",        // = 0x01000    // C
            2, //"SID",     // = 0x02000    // D
            2, //"ELEVATION",// = 0x04000   // E
            2, //"AZIMUTH", // = 0x08000    // F
            2, //"SNR",     // = 0x10000    // 10
            2, //"RCR",     // = 0x20000    // 11
            2, //"MILISECOND",// = 0x40000  // 12
            8, //"DISTANCE" // = 0x80000    // 13
    };  
    
    // PMTK182 commands/replies.
    /** Set a parameter concerning the log */
    public static final int PMTK_LOG_SET    = 1;
    /** Get a query concerning the log */
    public static final int PMTK_LOG_Q      = 2;
    /** Received data concerning the log */
    public static final int PMTK_LOG_DT     = 3;  // REPLY
    public static final int PMTK_LOG_ON     = 4;
    public static final int PMTK_LOG_OFF    = 5;
    public static final int PMTK_LOG_ERASE  = 6;
    public static final int PMTK_LOG_Q_LOG  = 7;
    public static final int PMTK_LOG_DT_LOG = 8;  // REPLY.
    public static final int PMTK_LOG_TBD    = 9;
    
    // PMTK182,1,DATA  (DATA = what parameter to set/read/replied)
    
    public static final int PMTK_LOG_FORMAT         = 2;
    public static final int PMTK_LOG_TIME_INTERVAL  = 3;
    public static final int PMTK_LOG_DISTANCE_INTERVAL = 4;
    public static final int PMTK_LOG_SPEED_INTERVAL = 5;
    public static final int PMTK_LOG_REC_METHOD     = 6;
    public static final int PMTK_LOG_LOG_STATUS     = 7;
    public static final int PMTK_LOG_MEM_USED       = 8;  // bit 2 = logging on/off
    public static final int PMTK_LOG_TBD3           = 9;
    public static final int PMTK_LOG_NBR_LOG_PTS    = 10;
    public static final int PMTK_LOG_TBD2           = 11;
    
    // Other MTK commands
    public static final int PMTK_TEST                   = 000;
    public static final int PMTK_ACK                    = 001;
    public static final int PMTK_SYS_MSG                = 010;
    public static final int PMTK_CMD_HOT_START          = 101;
    public static final int PMTK_CMD_WARM_START         = 102;
    public static final int PMTK_CMD_COLD_START         = 103;
    public static final int PMTK_CMD_FULL_COLD_START    = 104;
    public static final int PMTK_CMD_LOG                = 182;
    public static final int PMTK_SET_NMEA_BAUD_RATE     = 251;
    /* REPLIES: SET - Set values same as futher below, but 3XX */
    public static final int PMTK_API_SET_FIX_CTL        = 300;
    public static final int PMTK_API_SET_DGPS_MODE      = 301;
    public static final int PMTK_API_SET_SBAS           = 313;
    public static final int PMTK_API_SET_NMEA_OUTPUT    = 314;
    public static final int PMTK_API_SET_SBAS_TEST      = 319;
    public static final int PMTK_API_SET_PWR_SAV_MODE   = 320;
    public static final int PMTK_API_SET_DATUM          = 330;
    public static final int PMTK_API_SET_DATUM_ADVANCE  = 331;
    public static final int PMTK_API_SET_USER_OPTION    = 390;
    /* REPLIES: Q - Same as above, but 4XX in stead of 3XX */
    public static final int PMTK_API_Q_FIX_CTL          = 400;
    public static final int PMTK_API_Q_DGPS_MODE        = 401;
    public static final int PMTK_API_Q_SBAS             = 413;
    public static final int PMTK_API_Q_NMEA_OUTPUT      = 414;
    public static final int PMTK_API_Q_SBAS_TEST        = 419;
    public static final int PMTK_API_Q_PWR_SAV_MOD      = 420;
    public static final int PMTK_API_Q_DATUM            = 430;
    public static final int PMTK_API_Q_DATUM_ADVANCE    = 431;
    public static final int PMTK_API_Q_GET_USER_OPTION    = 490;
    /* REPLIES: DT - Same as above, but 5XX in stead of 4XX */
    public static final int PMTK_DT_FIX_CTL             = 500;
    public static final int PMTK_DT_DGPS_MODE           = 501;
    public static final int PMTK_DT_SBAS        = 513;
    public static final int PMTK_DT_NMEA_OUTPUT         = 514;
    public static final int PMTK_DT_SBAS_TEST           = 519;
    public static final int PMTK_DT_PWR_SAV_MODE        = 520;
    public static final int PMTK_DT_DATUM               = 530;
    public static final int PMTK_DT_FLASH_USER_OPTION   = 590;
    /* Special requests * SW ? */
    public static final int PMTK_Q_VERSION              = 604;
    
    
    /** Parameter 1 of PMTK_ACK reply.
     * Packet invalid. 
     */
    public static final int PMTK_ACK_INVALID = 0;
    /** Parameter 1 of PMTK_ACK reply.
     * Packet not supported by device. 
     */
    public static final int PMTK_ACK_UNSUPPORTED = 1;
    /** Parameter 1 of PMTK_ACK reply.
     * Packet supported, but action failed. 
     */
    public static final int PMTK_ACK_FAILED = 2;
    /** Parameter 1 of PMTK_ACK reply.
     * Packet success. 
     */
    public static final int PMTK_ACK_SUCCEEDED = 3;
    
    
    // The next constants are the same as above but convered to strings.
    public static final String PMTK_LOG_SET_STR = Convert.toString(PMTK_LOG_SET);
    public static final String PMTK_LOG_QUERY_STR = Convert.toString(PMTK_LOG_Q);
    public static final String PMTK_LOG_RESP_STR = Convert.toString(PMTK_LOG_DT);  // REPLY
    public static final String PMTK_LOG_ON_STR = Convert.toString(PMTK_LOG_ON);
    public static final String PMTK_LOG_OFF_STR = Convert.toString(PMTK_LOG_OFF);
    public static final String PMTK_LOG_ERASE_STR = Convert.toString(PMTK_LOG_ERASE);
    public static final String PMTK_LOG_REQ_DATA_STR = Convert.toString(PMTK_LOG_Q_LOG);
    public static final String PMTK_LOG_RESP_DATA_STR = Convert.toString(PMTK_LOG_DT_LOG);  // REPLY.
    public static final String PMTK_LOG_TBD_STR = Convert.toString(PMTK_LOG_TBD);
    
    public static final String PMTK_LOG_ERASE_YES_STR = "1";
    
    // PMTK182,1,DATA  (DATA_STR = Convert.toString((DATA)what parameter to set/read/replied)
    
    public static final String PMTK_LOG_FORMAT_STR = Convert.toString(PMTK_LOG_FORMAT);
    public static final String PMTK_LOG_TIME_INTERVAL_STR = Convert.toString(PMTK_LOG_TIME_INTERVAL);
    public static final String PMTK_LOG_DISTANCE_INTERVAL_STR = Convert.toString(PMTK_LOG_DISTANCE_INTERVAL);
    public static final String PMTK_LOG_SPEED_INTERVAL_STR = Convert.toString(PMTK_LOG_SPEED_INTERVAL);
    public static final String PMTK_LOG_REC_METHOD_STR = Convert.toString(PMTK_LOG_REC_METHOD);
    public static final String PMTK_LOG_LOG_STATUS_STR = Convert.toString(PMTK_LOG_LOG_STATUS);
    public static final String PMTK_LOG_MEM_USED_STR = Convert.toString(PMTK_LOG_MEM_USED);  // bit 2 = logging on/off
    public static final String PMTK_LOG_TBD3_STR = Convert.toString(PMTK_LOG_TBD3);
    public static final String PMTK_LOG_NBR_LOG_PTS_STR = Convert.toString(PMTK_LOG_NBR_LOG_PTS);
    public static final String PMTK_LOG_TBD2_STR = Convert.toString(PMTK_LOG_TBD2);
    
    /** Mask for bit in log status indicating that log is active */
    public static final int PMTK_LOG_STATUS_LOGONOF_MASK= 0x02;
    
    // Other MTK commands
    public static final String PMTK_TEST_STR = "000"; //Convert.toString(PMTK_TEST);
    public static final String PMTK_ACK_STR = "001"; //Convert.toString(PMTK_ACK);
    public static final String PMTK_SYS_MSG_STR = "010"; //Convert.toString(PMTK_SYS_MSG);
    public static final String PMTK_CMD_HOT_START_STR = Convert.toString(PMTK_CMD_HOT_START);
    public static final String PMTK_CMD_WARM_START_STR = Convert.toString(PMTK_CMD_WARM_START);
    public static final String PMTK_CMD_COLD_START_STR = Convert.toString(PMTK_CMD_COLD_START);
    public static final String PMTK_CMD_FULL_COLD_START_STR = Convert.toString(PMTK_CMD_FULL_COLD_START);
    public static final String PMTK_CMD_LOG_STR = Convert.toString(PMTK_CMD_LOG);
    public static final String PMTK_SET_NMEA_BAUD_RATE_STR = Convert.toString(PMTK_SET_NMEA_BAUD_RATE);
    public static final String PMTK_API_SET_FIX_CTL_STR = Convert.toString(PMTK_API_SET_FIX_CTL);
    public static final String PMTK_API_SET_DGPS_MODE_STR = Convert.toString(PMTK_API_SET_DGPS_MODE);
    public static final String PMTK_API_SET_SBAS_STR = Convert.toString(PMTK_API_SET_SBAS);
    public static final String PMTK_API_SET_NMEA_OUTPUT_STR = Convert.toString(PMTK_API_SET_NMEA_OUTPUT);
    public static final String PMTK_API_SET_SBAS_TEST_STR = Convert.toString(PMTK_API_SET_SBAS_TEST);
    public static final String PMTK_API_SET_PWR_SAV_MODE_STR = Convert.toString(PMTK_API_SET_PWR_SAV_MODE);
    public static final String PMTK_API_SET_DATUM_STR = Convert.toString(PMTK_API_SET_DATUM);
    public static final String PMTK_API_SET_DATUM_ADVANCE_STR = Convert.toString(PMTK_API_SET_DATUM_ADVANCE);
    public static final String PMTK_API_SET_USER_OPTION_STR = Convert.toString(PMTK_API_SET_USER_OPTION);
    public static final String PMTK_API_Q_FIX_CTL_STR = Convert.toString(PMTK_API_Q_FIX_CTL);
    public static final String PMTK_API_Q_DGPS_MODE_STR = Convert.toString(PMTK_API_Q_DGPS_MODE);
    public static final String PMTK_API_Q_SBAS_STR = Convert.toString(PMTK_API_Q_SBAS);
    public static final String PMTK_API_Q_NMEA_OUTPUT_STR = Convert.toString(PMTK_API_Q_NMEA_OUTPUT);
    public static final String PMTK_API_Q_SBAS_TEST_STR = Convert.toString(PMTK_API_Q_SBAS_TEST);
    public static final String PMTK_API_Q_PWR_SAV_MOD_STR = Convert.toString(PMTK_API_Q_PWR_SAV_MOD);
    public static final String PMTK_API_Q_DATUM_STR = Convert.toString(PMTK_API_Q_DATUM);
    public static final String PMTK_API_Q_DATUM_ADVANCE_STR = Convert.toString(PMTK_API_Q_DATUM_ADVANCE);
    public static final String PMTK_API_GET_USER_OPTION_STR = Convert.toString(PMTK_API_Q_GET_USER_OPTION);
    public static final String PMTK_DT_FIX_CTL_STR = Convert.toString(PMTK_DT_FIX_CTL);
    public static final String PMTK_DT_DGPS_MODE_STR = Convert.toString(PMTK_DT_DGPS_MODE);
    public static final String PMTK_DT_SBAS_STR = Convert.toString(PMTK_DT_SBAS);
    public static final String PMTK_DT_NMEA_OUTPUT_STR = Convert.toString(PMTK_DT_NMEA_OUTPUT);
    public static final String PMTK_DT_SBAS_TEST_STR = Convert.toString(PMTK_DT_SBAS_TEST);
    public static final String PMTK_DT_PWR_SAV_MODE_STR = Convert.toString(PMTK_DT_PWR_SAV_MODE);
    public static final String PMTK_DT_DATUM_STR = Convert.toString(PMTK_DT_DATUM);
    public static final String PMTK_DT_FLASH_USER_OPTION_STR = Convert.toString(PMTK_DT_FLASH_USER_OPTION);
    public static final String PMTK_Q_VERSION_STR = Convert.toString(PMTK_Q_VERSION);
    
    
    /** Parameter 1 of PMTK_ACK reply.
     * Packet invalid. 
     */
    public static final String PMTK_ACK_INVALID_STR = Convert.toString(PMTK_ACK_INVALID);
    /** Parameter 1 of PMTK_ACK reply.
     * Packet not supported by device. 
     */
    public static final String PMTK_ACK_UNSUPPORTED_STR = Convert.toString(PMTK_ACK_UNSUPPORTED);
    /** Parameter 1 of PMTK_ACK reply.
     * Packet supported, but action failed. 
     */
    public static final String PMTK_ACK_FAILED_STR = Convert.toString(PMTK_ACK_FAILED);
    /** Parameter 1 of PMTK_ACK reply.
     * Packet success. 
     */
    public static final String PMTK_ACK_SUCCEEDED_STR = Convert.toString(PMTK_ACK_SUCCEEDED);
    
    
}
