/**
 * 
 */
package gps;

/**
 * Constants for Holux (PHLX) devices. Includes:
 * - M-1000C
 * 
 * @author Robert Tomanek <bt747.free.fr@mail.robert.tomanek.org>
 *
 */
public class HoluxConstants {
    /**
     * Prefix commands. Needs to be before many other commands. 
     * Parameters: none
     */  
    public static final String PHLX_CMD_PREFIX = "PHLX828";

    /**
     * Set device name. Needs to be prefixed with PHLX_CMD_PREFIX
     * Parameters:
     * - Device name string
     */  
    public static final String PHLX_NAME_SET_REQUEST = "PHLX830";
	
	/**
     * Erase log command
     * Parameters: none
     */  
    public static final String PHLX_LOG_ERASE = "PHLX839";
}
