/**
 * 
 */
package gps;

/**
 * Constants for Holux (PHLX) devices. Includes:
 * - M-1000C
 * 
 * @author Robert Tomanek <bt747.free.fr@mail.robert.tomanek.org>
 * @author Mario De Weerd
 *
 */
public class HoluxConstants {
	/**
	 * Reply: Number of tracks in device?
	 * $PHLX601,3*24  //Number of tracks response (3)
	 */
	public static final String PHLX_DT_NUMBER_OF_TRACKS = "PHLX601";
	/**
	 * Query: number of tracks recorded.
	 * $PHLX701*3A
	 * (reply = PHLX601) 
	 */
	public static final String PHLX_Q_NUMBER_OF_TRACKS = "PHLX701";
	/**
	 * Query: request 3 tracks’ metadata
	 * $PHLX702,0,3*3A   
	 */
	public static final String PHLX_Q_TRACK_METADATA = "PHLX702";
	/**
	 * Query: Get data blocks (in holux GPSSport 260, a block is 32 bytes)
	 * $PHLX703,0,41*0D   // 41 blocks starting from 0
	 */
	public static final String PHLX_ = "PHLX703";
	// PHLX704?
	// PHLX705?
	// PHLX706?
	// PHLX707?
	// PHLX708?
	/**
	 * Query: get memory used (in %).
	 * $PHLX709*32
	 */
	public static final String PHLX_Q_MEMORY_USED_PERCENT = "PHLX709";

	/**
	 * Query: Get ID (device type)
	 * $PHLX810*35   //ID request
	 * Reply: $PHLX852,GR260*3E
	 */
	public static final String PHLX_Q_DEVICE_ID = "PHLX810";
	
	/**
	 * Start connection (Holux GPSPort 260).
	 * Shows USB symbol on screen.
	 * $PHLX826*30 
	 */
	public static final String PHLX_CMD_SHOW_USB_SYMBOL = "PHLX826";
	/**
	 * End connection (Holux GPSPort 260).
	 * Stops showing USB symbol on screen.
	 * $PHLX827*31 
	 */
	public static final String PHLX_CMD_STOP_SHOW_USB_SYMBOL = "PHLX827";

    /**
     * Prefix commands. 
     * Needs to be sent before many other commands. 
     * Parameters: none
     */  
    public static final String PHLX_CMD_PREFIX = "PHLX828";

    /**
     * Query: get firmware version.
     * $PHLX829*3F
     */
    public static final String PHLX_Q_FW_VERSION = "PHLX829";
    
    /**
     * Set device name. 
     * Needs to be prefixed with PHLX_CMD_PREFIX
     * Parameters:
     * - Device name string
     * $PHLX830,devname*XX
     */  
    public static final String PHLX_NAME_SET_REQUEST = "PHLX830";

    /**
     * To GPS: Get device name.
     * Needs to be prefixed with PHLX_CMD_PREFIX
     * Parameters: none
     * $PHLX831*36
     */  
    public static final String PHLX_NAME_GET_REQUEST = "PHLX831";
	
    /**
     * To GPS: Get logging criteria.
     * Parameters: none
     * $PHLX833*34
     */  
    public static final String PHLX_LOG_GET_CRITERIA_REQUEST = "PHLX833";

    /**
     * To GPS: Set logging criteria.
     * Parameters:
     * - PHLX_LOG_CRITERIUM_TIME_PARAM ^ PHLX_LOG_CRITERIUM_DISTANCE_PARAM
     * - time [s] (1-120)
     * - distance [m] (1-1000)
     * 
     * $PHLX834,0,23,50*2B // Set logging to every 23 seconds (what is 50 here?)
     */  
    public static final String PHLX_LOG_SET_CRITERIA = "PHLX834";
    
    /**
     * Parameter: Logging based on time interval.
     */  
    public static final String PHLX_LOG_CRITERIUM_TIME_PARAM = "0";

    /**
     * Parameter: Logging based on distance interval.
     */  
    public static final String PHLX_LOG_CRITERIUM_DISTANCE_PARAM = "1";

    /**
     * To GPS: ??
     * Parameters: none
     * $PHLX836*31
     * Reply: $PHLX869,0*27
     */  
    public static final String PHLX_Q_UNKNOWN1 = "PHLX836";

    /**
     * To GPS: Erase log command.
     * Parameters: none
     * $PHLX839*3E
     */  
    public static final String PHLX_LOG_ERASE_REQUEST = "PHLX839";

    /**
     * To GPS: Query/Set sports mode.
	 * $PHLX841,0*2D  //sport mode 0=request
	 * $PHLX841,1,GR260,1*3C //sport mode 1=set, GR260,1=jog
	 * Reply: $PHLX871,GR260,0*23	//sport mode response,0=bike 1=run 2=hike 3=car 
     */
	public static final String PHLX_CMD_SPORTS_MODE = "PHLX841";

	/**
	 * To GPS: Query/Set overwrite mode
	 * 	$PHLX842,0*2E  //log write mode 0=request
	 * 	$PHLX842,1,GR260,0*3E //write mode 1=set, GR260,0=overwrite
	 * 
	 * Reply: $PHLX872,GR260,1*21	//log write mode response, 0=overwrite 1=stop
	 */
	public static final String PHLX_CMD_OVERWRITE_MODE = "PHLX842";
	
	/**
	 * From GPS: ID (device type) (reply to $PHLX810)
	 * $PHLX852,GR260*3E
	 */
	public static final String PHLX_DT_DEVICE_ID = "PHLX852";
	
	/**
	 * Start connection (Holux GPSPort 260).
	 * Shows USB symbol on screen.
	 * $PHLX826*30 
	 */
	public static final String PHLX_ACK_SHOW_USB_SYMBOL = "PHLX859";
	
	/**
	 * End connection (Holux GPSPort 260).
	 * Stops showing USB symbol on screen.
	 * $PHLX827*31 
	 */
	public static final String PHLX_ACK_STOP_SHOW_USB_SYMBOL = "PHLX860";

    /**
     * From GPX: firmware version. (reply to PHLX829)
     * $PHLX861,103*2D // Version 1.03
     */
    public static final String PHLX_DT_FW_VERSION = "PHLX861";

    /**
     * Acknowledges Set device name. // Reply to PHLX830 
     * 	$PHLX862*30
     */  
    public static final String PHLX_ACK_NAME_SET = "PHLX862";

    /**
     * From GPS: Current device name.
     * Parameters:
     * - Device name string
     * $PHLX863,devname*XX
     */  
    public static final String PHLX_NAME_GET_RESPONSE = "PHLX863";

    /**
     * From GPS: current logging criteria.
     * Reply to PHLX833
     * Parameters:
     * - PHLX_LOG_CRITERIUM_TIME_PARAM ^ PHLX_LOG_CRITERIUM_DISTANCE_PARAM
     * - time [s] (1-120)
     * - distance [m] (1-1000)
     */  
    public static final String PHLX_LOG_GET_CRITERIA_RESPONSE = "PHLX866";

    /**
     * From GPS: Acknowledge setting log criteria (reply to $PHLX834)
     * $PHLX867*35
     */  
    public static final String PHLX_ACK_LOG_SET_CRITERIA = "PHLX867";
    
    /**
     * From GPS: ?? reply to PHLX836
     * 
     * Parameter: 1 ??
     * $PHLX869,0*27
     */
    public static final String PHLX_DT_UNKNOWN1 = "PHLX869";
    
    /**
     * From GPS: Log erased.
     * Parameters: none
     */  
    public static final String PHLX_LOG_ERASE_ACK = "PHLX870";
    
	/**
	 * From GPS: Sports mode.
	 * Reply to PHLX841
	 * $PHLX871,GR260,0*23	//sport mode response,0=bike 1=run 2=hike 3=car
	 */
	public static final String PHLX_DT_SPORTS_MODE = "PHLX871";

	/**
	 * From GPS:  overwrite mode (reply to PHLX842).
	 * $PHLX872,GR260,1*21	//log write mode response, 0=overwrite 1=stop
	 */
	public static final String PHLX_DT_OVERWRITE_MODE = "PHLX872";


	/**
	 * From GPS: Used memory in % (reply to PHLX709)
	 * 	$PHLX873,2*2E  //percent mem used response (2%)
	 */
	public static final String PHLX_DT_MEMUSED_PERCENT = "PHLX873";
	
	/**
	 * From GPS: Acks ?
	 * 	$PHLX900,702,3*33  //ack
	 * 	$PHLX900,703,3*32   //ack
	 * 	$PHLX900,841,3*3B  //sport mode set ack
	 * 	$PHLX900,842,3*38  //write mode set ack 
	 * 	$PHLX900,901,3*3E   //ack
	 * 	$PHLX900,902,3*3D   //ack
	 */
	public static final String PHLX_ACK_GENERIC_ACK = "PHLX900";
	
	
	/**
	 * From GPS: Announce total data that will be sent:
	 * $PHLX901,1312,500387F2*48 //sending 1312 bytes with checksum 500387F2?
	 * Must confirm with:
	 * $PHLX900,901,3*3E
	 * 
	 */
	public static final String PHLX_DT_LOG_DOWNLOAD_ANNOUNCE_TOTAL = "PHLX901";

	/**
	 * From GPS: Announce chunk of data that will be sent:
	 * $PHLX902,0,1312,500387F2*57 //firstchunk 1312 bytes with checksum 500387F2?
	 *                             // Starting at address 0
	 * Must confirm with:
	 * $PHLX900,902,3*3D
	 * 
	 * Then 1312 bytes of binary data follow.
	 */
	public static final String PHLX_DT_LOG_DOWNLOAD_ANNOUNCE_CHUNK = "PHLX902";

}
