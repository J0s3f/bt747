/**
 * 
 */
package gps;

/**
 * Constants for Wondeproud Devices. Includes: - BT-CD100m
 * 
 * @author Mario De Weerd
 * 
 */
public interface WondeproudConstants {
    /**
     * {@link #REQ_DEV_INFO1} response example: 0x000A091E00000000
     * "WondeProud Tech. Co." 0x00EA927C64F71200 0x30000000
     * "BT-CD160MTK.ech. Co" 0x00EA927C64F71200 0x30000000 0xAE652680C8542A03
     * 0x0B236926120000FF "WP Update Over."
     */
    public static final int REQ_DEV_INFO1 = 0x5BB00000;
    /** {@link #REQ_UNKNOWN2} And arg 0x01 // could be code */
    public static final int REQ_UNKNOWN2 = 0x5DB20000;
    public static final int REQ_LOG = 0x60B50000;
    /**
     * Erase Response should be WP Update Over.
     */
    public static final int REQ_ERASE = 0x61B60000;

    public static final int REQ_DEV_PARAM = 0x62B60000;

    /**
     * {@link #REQ_SELFTEST} response example: 0x00004000
     */
    public static final int REQ_SELFTEST = 0x63B70000;
    public static final int REQ_DATE_TIME = 0x64B80000;
    public static final int REQ_MEM_IN_USE = 0x65B90000;
    public static final int REQ_UNKNOWN3 = 0x64B90000;

    /** Likely first 2 are command - other 4 are length and checksum of block
    /* that follows.
     * 
     * Response expected = Checksum    OK\0WPUpdate Over\0  
     */
    public static final int SET_PARAM = 0x55AA0000;

    public static final String WP_GPS_PLUS_RESPONSE = "WP GPS+BT";
    public static final String WP_CAMERA_DETECT = "W'P Camera Detect";
    public static final String WP_AP_EXIT = "WP AP-Exit";
    public static final String WP_UPDATE_OVER = "WP Update Over";

    /**
     * Firmware update.<br>
     * // -> Check device.<br>
     * // -> Erase<br>
     * // -> Get device info<br>
     * //-> {@link #REQ_UNKNOWN2}
     **/
}
