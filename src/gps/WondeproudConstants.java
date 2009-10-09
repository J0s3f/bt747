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
    public static final int REQ_LOG_SIZE  = 0x60B50000;
    public static final int REQ_LOG       = 0x60B50000;
    public static final int REQ_ERASE     = 0x61B60000;
    public static final int REQ_DEV_PARAM = 0x62B60000;
    public static final int REQ_SELFTEST  = 0x63B70000;
    public static final int REQ_DATE_TIME = 0x64B80000;
    public static final int REQ_DEV_INFO1 = 0x5BB00000;
}
