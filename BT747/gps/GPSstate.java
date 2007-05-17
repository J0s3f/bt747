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


import waba.io.File;
import waba.sys.Convert;
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.MessageBox;
import waba.ui.Timer;
import waba.util.IntVector;
import waba.util.Vector;



/*
 * Created on 12 mai 2007
 */

/** GPSstate maintains a higher level state of communication with the
 * GPS device.
 * It currently contains very specific commands for the BT747 device
 * but that could change in the future by extending GPSstate with
 * such features in a derived class.
 * As the initial development is for the BT747, we do not care for now.
 * 
 * 
 * @author Mario De Weerd
 * @see GPSrxtx
 *
 */
public class GPSstate extends Control {
	static final boolean GPS_DEBUG = true;
	static final boolean GPS_TEST = false;
	static GPSstate a;
	
	public GPSrxtx m_GPSrxtx=new GPSrxtx();    	
	
	// Fields to keep track of logging status
	private int logTimer=0;  // Value that increases at each timer event
	private int m_StartAddr;
	private int m_EndAddr;
	private int m_NextReqAddr;
	private int m_NextReadAddr;
	private int m_Step;
	private GPSstate m_GPSstate;
	private boolean m_isLogging = false;       // True when dumping log
	private boolean m_isSearchingLog = false;  // True when doing binary search
	
	public int logFormat = 0;
	public int logEntrySize = 0;
	public int logHeaderSize = 0;
	public int logTimeInterval = 0;
	public int logSpeedInterval = 0;
	public int logDistanceInterval = 0;
	public int logStatus = 0;
	public int logRecMethod = 0;
	public int logNbrLogPts = 0;
	public int logMemUsed = 0;
	public int logMemFree = 0;
	public int logMemMax = 0;
	
	
	// Number of log entries to request 'ahead'.
	final static int C_MAX_LOG_AHEAD = 2;
	

	static final int C_LOG_TIMEOUT  = 200; // ms
	static final int C_TIMER_PERIOD = 50; // ms
	static final int C_LOG_TIMEOUT_CNT = C_LOG_TIMEOUT; 
	private Timer linkTimer= null;
	
	
	public GPSstate() {
	}
	
	public void onStart() {
		if (BT747Settings.getStartupOpenPort()) {
			m_GPSrxtx.openPort();		
			getStatus();
		}
	}
	
	/** Restart the GPS connection
	 * Will close the current connection (if still open) and
	 * open it again (use the same parameters)
	 * @author Mario De Weerd
	 *
	 */
	public void GPS_restart() {
		m_GPSrxtx.closePort();
		m_GPSrxtx.openPort();		
		getStatus();
	}
	
	/** Close the GPS connection
	 * @author Mario De Weerd
	 */
	public void GPS_close() {
		m_GPSrxtx.closePort();
	}
	
	/** open a Bluetooth connection
	 * Calls getStatus to request initial parameters from the device.
	 * Set up the timer to regurarly poll the connection for data.
	 * @author Mario De Weerd
	 */
	public void setBluetooth() {
		m_GPSrxtx.setBluetooth();
		getStatus();
		setupTimer();
	}

	/** open a Usb connection
	 * Calls getStatus to request initial parameters from the device.
	 * Set up the timer to regurarly poll the connection for data.
	 * @author Mario De Weerd
	 */
	public void setUsb() {
		m_GPSrxtx.setUsb();
		getStatus();
		setupTimer();
	}
	
	/** open a connection on the given port number.
	 * Calls getStatus to request initial parameters from the device.
	 * Set up the timer to regurarly poll the connection for data.
	 * @author Mario De Weerd
	 */
	public void setPort(int port) {
		m_GPSrxtx.setPort(port);
		getStatus();
		setupTimer();
	}
	
	/** Start the timer
	 * To be called once the port is opened.
	 * The timer is used to launch functions that will check if there
	 * is information on the serial connection or to send to the GPS
	 * device.
	 * @author Mario De Weerd
	 */
	private void setupTimer() {
		if( m_GPSrxtx.isConnected()) {
			// Check if old timer still present (for safety)
			if(linkTimer!=null) removeTimer(linkTimer);
			linkTimer = addTimer(C_TIMER_PERIOD);
		}
	}
	
	
	/** A single request to get information from the device's log.
	 * @author Mario De Weerd
	 * @param startAddr start address of the data range requested
	 * @param size      size of the data range requested
	 */
	public void readLog(final int startAddr, final int size) {
		m_GPSrxtx.sendPacket("PMTK"+Convert.toString(BT747_dev.PMTK_CMD_LOG)
				+","+Convert.toString(BT747_dev.PMTK_LOG_REQ_DATA)
				+","+Convert.unsigned2hex(startAddr,8)			
				+","+Convert.unsigned2hex(size,8)			
		);
		//		if(GPS_DEBUG) {	waba.sys.Vm.debug("PMTK"+Convert.toString(PMTK_CMD_LOG)
		//				+","+Convert.toString(PMTK_LOG_REQ_DATA)
		//				+","+Convert.unsigned2hex(startAddr,8)			
		//				+","+Convert.unsigned2hex(size,8));			
		//				}
	}
	
	/** Handle time outs on log requests
	 * Will be called if the device did not respond to a log request.
	 * TODO: Could be extended to all packets.
	 * TODO: Implement handleLogTimeOUt
	 * @author Mario De Weerd
	 */
	public void handleLogTimeOut() {
		
	}

	/** A vector monitored by a Thread to know if the logFormat has been updated.<br>
	 * TODO: This method should be improved.
	 */
	// TODO: Empty vector on reset of connection
	public IntVector logUpdate=new IntVector();
	
	
	
	/** Calculate the size of one log entry.
	 * <b>Only works if the satellite information is not requested.</b><br>
	 * Satellite information is of variant size.
	 * @param p_logFormat : configuration representing format
	 * @return Number of bytes needed for one record.
	 */
	static public int logEntrySize(int p_logFormat) {
		int z_BitMask = 0x1;
		int z_Size = 2; // one for the '*' and one for the checksum (xor)
		for (int i = 0; i < BT747_dev.logFmtByteSizes.length; z_BitMask<<=1, i++) {
			if((z_BitMask & p_logFormat)!=0) {
				// Bit is set
				z_Size+= BT747_dev.logFmtByteSizes[i];
			}
		}
		return z_Size;
	}
	
	
	static public int logHeaderSize(int p_logFormat) {
		return 22;
	}
	
	public int logEntryAddr(int p_RecordNumber) {
		return logHeaderSize+p_RecordNumber*logEntrySize;
	}
	
	public int analyseLogNmea(String[] p_nmea) {
		//if(GPS_DEBUG) {	waba.sys.Vm.debug("LOG:"+p_nmea.length+':'+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+"\n");}
		// Suppose that the command is ok (PMTK182)
		
		// Currently taking care of replies from the device only.
		// The other data we send ourselves
		if(p_nmea.length>2) {
			switch( Convert.toInt(p_nmea[1]) ) {
			case BT747_dev.PMTK_LOG_RESP:
				// Parameter information
				// TYPE = Parameter type
				// DATA = Parameter data
				// $PMTK182,3,TYPE,DATA
				int z_type= Convert.toInt(p_nmea[2]);
				if(p_nmea.length==4) {
					switch( z_type ) {
					case BT747_dev.PMTK_LOG_FORMAT: 			// 2;
						//if(GPS_DEBUG) {	waba.sys.Vm.debug("FMT:"+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+","+p_nmea[3]+"\n");}
						logFormat=BT747Settings.Hex2Int(p_nmea[3]);
						logEntrySize=logEntrySize(logFormat);
						logHeaderSize=logHeaderSize(logFormat);
						logUpdate.push(logFormat);
						break;
					case BT747_dev.PMTK_LOG_TIME_INTERVAL: 	// 3;
						logTimeInterval=Convert.toInt(p_nmea[3]);
						break;
					case BT747_dev.PMTK_LOG_DISTANCE_INTERVAL: //4;
						logDistanceInterval=Convert.toInt(p_nmea[3])/10;
						break;
					case BT747_dev.PMTK_LOG_SPEED_INTERVAL:	// 5;
						logSpeedInterval=Convert.toInt(p_nmea[3])/10;
						break;
					case BT747_dev.PMTK_LOG_REC_METHOD:		// 6;
						logRecMethod=Convert.toInt(p_nmea[3])/10;
						break;
					case BT747_dev.PMTK_LOG_LOG_STATUS:		// 7; // bit 2 = logging on/off
						logStatus=BT747Settings.Hex2Int(p_nmea[3]);
						break;
					case BT747_dev.PMTK_LOG_MEM_USED:			// 8; 
						logMemUsed=BT747Settings.Hex2Int(p_nmea[3]);
						break;
					case BT747_dev.PMTK_LOG_TBD_3:			// 9;
						break;
					case BT747_dev.PMTK_LOG_NBR_LOG_PTS:		// 10;
						logNbrLogPts=BT747Settings.Hex2Int(p_nmea[3]);
						break;
					case BT747_dev.PMTK_LOG_TBD2:				// 11;
						break;
					default:
					}
				}
				break;
			case BT747_dev.PMTK_LOG_RESP_DATA:
				// Data from the log
				// $PMTK182,8,START_ADDRESS,DATA
				analyzeLogPart(BT747Settings.Hex2Int(p_nmea[2]),p_nmea[3]);
				break;
			default:
				// Nothing - unexpected
			}
		}
		return 0; // Done.
		
	}
	static final int C_SEND_BUF_SIZE = 5;
	
	Vector sentCmds = new Vector();	// List of sent commands
	static final int C_MAX_SENT_COMMANDS = 10;  // Max commands to put in list
	
	public int sendNMEA(final String p_Cmd) {
		if(p_Cmd.startsWith("PMTK")) {
			sentCmds.add(p_Cmd);
		}
		m_GPSrxtx.sendPacket(p_Cmd);
		if(sentCmds.getCount()>C_MAX_SENT_COMMANDS) {
			sentCmds.del(0);
		}
		return 0;
	}

	public void getStatus() {
		// Request log format from device
		sendNMEA("PMTK"+Convert.toString(BT747_dev.PMTK_CMD_LOG)
				+","+Convert.toString(BT747_dev.PMTK_LOG_QUERY)+
				","+Convert.toString(BT747_dev.PMTK_LOG_FORMAT)			
		);
		
		//if(GPS_TEST) {analyseNMEA(Convert.tokenizeString("PMTK001,2,3,3",','));}
		//if(GPS_TEST) {analyseNMEA(Convert.tokenizeString("PMTK182,3,2,3F",','));}
	}
	

	
	public int analyseMTK_Ack(String[]p_nmea) {
		// PMTK001,Cmd,Flag
		int z_Cmd;
		int z_Flag;
		int z_Result=-1;
		//if(GPS_DEBUG) {	waba.sys.Vm.debug(p_nmea[0]+","+p_nmea[1]+"\n");}
		
		if(p_nmea.length>=3) {
			z_Cmd=Convert.toInt(p_nmea[1]);
			z_Flag=Convert.toInt(p_nmea[2]);
			int z_CmdIdx=sentCmds.find("PMTK"+p_nmea[1]);
			//if(GPS_DEBUG) {	waba.sys.Vm.debug("IDX:"+Convert.toString(z_CmdIdx)+"\n");}
			//if(GPS_DEBUG) {	waba.sys.Vm.debug("FLAG:"+Convert.toString(z_Flag)+"\n");}
			switch (z_Flag) {
			case BT747_dev.PMTK_ACK_INVALID:
				// 0: Invalid cmd or packet
				z_Result=0;
				break;
			case BT747_dev.PMTK_ACK_UNSUPPORTED:
				// 1:	Unsupported cmd or packet
				z_Result=0;
				break;
			case BT747_dev.PMTK_ACK_FAILED:
				// 2: Valid cmd or packet but action failed
				z_Result=0;
				break;
			case BT747_dev.PMTK_ACK_SUCCEEDED: 
				//3: Valid cmd or packat but action succeeded
				z_Result=0;
				break;
			default:
				z_Result=-1;
			break;
			}
			// Remove all cmds up to 
			for (int i = z_CmdIdx; i >= 0; i--) {
				sentCmds.del(0);
			}
		}
		return z_Result;
	}
	
	static File m_logFile=new File("");
	public void stopLog() {
		if(m_logFile!=null  && m_logFile.isOpen()) {
			m_logFile.close();
		}
		m_isLogging= false;
	}
	
	public void getLogInit(int p_StartAddr, int p_EndAddr, int p_Step,String p_FileName) {
		m_StartAddr= p_StartAddr;
		m_EndAddr=p_EndAddr;
		m_NextReqAddr= m_StartAddr;
		m_NextReadAddr= m_StartAddr;
		m_Step=p_Step;
		m_isLogging= true;
		// Put some logging requests in the buffer
		for(int i=C_MAX_LOG_AHEAD;i>0;i--) {
			getNextLogPart();
		}
		m_logFile=new File(p_FileName,File.CREATE);
		if(m_logFile!=null) {
			new MessageBox("File open",
					m_logFile.getPath()+"|"+
					Convert.toString(m_logFile.exists())
			).popupBlockingModal();
		}
		
	}
	
	
	// Called regurarly
	public void getNextLogPart() {
		if ( m_isLogging ) {
			
			//TODO: Stop the thread at the end.
			int z_Step;
			z_Step=m_EndAddr-m_NextReqAddr;
			if(z_Step>0) {
				if(z_Step>m_Step){
					z_Step=m_Step;
				}
				readLog(m_NextReqAddr,z_Step);
				m_NextReqAddr+=z_Step;
			}
		}
	}
	
	public final byte[] HexStringToBytes(final String p_Data) {
		char[] z_Data= p_Data.toCharArray();
		byte[] z_Result= new byte[z_Data.length];
		for (int i = 0; i < z_Data.length; i+=2) {
			char c1 = z_Data[i];
			char c2 = z_Data[i+1];
			if( (c1>='0')&&(c1<='9') ) {
				c1-='0';
			} else if ( (c1>='A')&&(c1<='F') ) {
				c1+=-'A'+10;
			} else if ( (c1>='a')&&(c1<='f') ) {
				c1+=-'a'+10;			
			} else {
				c1=0;			
			}
			if( (c2>='0')&&(c2<='9') ) {
				c2-='0';
			} else if ( (c2>='A')&&(c2<='F') ) {
				c2+=-'A'+10;
			} else if ( (c2>='a')&&(c2<='f') ) {
				c2+=-'a'+10;			
			} else {
				c2=0;			
			}
			z_Result[i>>1]=(byte)((c1<<4)+c2);
		}
		
		return z_Result;
	}
	
	private boolean findMinimumLogEntry= true;
	private int binarySearchLeft = 0;
	int curRecNumber;
	private int p_minLogEntry;
	private int p_maxLogEntry;
	
	public void initBinarySearch(double p_UTC, boolean p_min_notmax) {
		// 
	}
	
	public void	analyzeLogPart(final int p_StartAddr, final String p_Data) {
		byte[] z_Data=HexStringToBytes(p_Data);
		if( m_isLogging) {
			if(m_NextReadAddr==p_StartAddr) {
				m_logFile.writeBytes(z_Data,0,z_Data.length);
				m_NextReadAddr+=z_Data.length;
			} else {
				// TODO: Handle error in order of data
				m_isLogging= false;
			}
		} else if ( m_isSearchingLog) {
			// Binary search
			// TODO: Validate binary search, finish it.
			// TODO: Get time from log entry & compare to move
			while(binarySearchLeft-- > 0) {
				// Get one log entry
				//if(time<reftime)
				// TODO: complete this
				readLog(logEntryAddr(curRecNumber),logEntrySize);
				
			}
		}
	}


public int analyseNMEA(String[] p_nmea) {
	int z_Cmd;
	int z_Result;
	//if(GPS_DEBUG) {	waba.sys.Vm.debug("ANA:"+p_nmea[0]+","+p_nmea[1]+"\n");}
	if(p_nmea[0].startsWith("GPZDA")) {
		// GPZDA,$time,$msec,$DD,$MO,$YYYY,03,00
		
	} else if(p_nmea[0].startsWith("GPRMC")) {
		// GPRMC,$time,$fix,$latf1,$ns,$lonf1,$ew,$knots,$bear,$date,$magnvar,$magnew,$magnfix
	} else if(p_nmea[0].startsWith("GPSTPV")) {
		// GPSTPV,$epoch.$msec,?,$lat,$lon,,$alt,,$speed,,$bear,,,,A
		
	} else if(p_nmea[0].startsWith("PMTK")) {
		z_Cmd= Convert.toInt(p_nmea[0].substring(4));
		
		z_Result=-1;  // Suppose cmd not treated
		switch(z_Cmd) {
		case BT747_dev.PMTK_CMD_LOG:	// CMD 182;
			z_Result= analyseLogNmea(p_nmea);
			break;
		case BT747_dev.PMTK_TEST:	// CMD  000
		case BT747_dev.PMTK_ACK:	// CMD  001
			z_Result= analyseMTK_Ack(p_nmea);
			break;
		case BT747_dev.PMTK_SYS_MSG:	// CMD  010
		case BT747_dev.PMTK_CMD_HOT_START:	// CMD  101
		case BT747_dev.PMTK_CMD_WARM_START:	// CMD  102
		case BT747_dev.PMTK_CMD_COLD_START:	// CMD  103
		case BT747_dev.PMTK_CMD_FULL_COLD_START:	// CMD  104
		case BT747_dev.PMTK_SET_NMEA_BAUD_RATE:	// CMD  251
		case BT747_dev.PMTK_API_SET_FIX_CTL:	// CMD  300
		case BT747_dev.PMTK_API_SET_DGPS_MODE:	// CMD  301
		case BT747_dev.PMTK_API_SET_SBAS_ENABLED:	// CMD  313
		case BT747_dev.PMTK_API_SET_NMEA_OUTPUT:	// CMD  314
		case BT747_dev.PMTK_API_SET_PWR_SAV_MODE:	// CMD  320
		case BT747_dev.PMTK_API_SET_DATUM:	// CMD  330
		case BT747_dev.PMTK_API_SET_DATUM_ADVANCE:	// CMD  331
		case BT747_dev.PMTK_API_SET_USER_OPTION:	// CMD  390
		case BT747_dev.PMTK_API_Q_FIX_CTL:	// CMD  400
		case BT747_dev.PMTK_API_Q_DGPS_MODE:	// CMD  401
		case BT747_dev.PMTK_API_Q_SBAS_ENABLED:	// CMD  413
		case BT747_dev.PMTK_API_Q_NMEA_OUTPUT:	// CMD  414
		case BT747_dev.PMTK_API_Q_PWR_SAV_MOD:	// CMD  420
		case BT747_dev.PMTK_API_Q_DATUM:	// CMD  430
		case BT747_dev.PMTK_API_Q_DATUM_ADVANCE:	// CMD  431
		case BT747_dev.PMTK_API_GET_USER_OPTION:	// CMD  490
		case BT747_dev.PMTK_DT_FIX_CTL:	// CMD  500
		case BT747_dev.PMTK_DT_DGPS_MODE:	// CMD  501
		case BT747_dev.PMTK_DT_SBAS_ENABLED:	// CMD  513
		case BT747_dev.PMTK_DT_NMEA_OUTPUT:	// CMD  514
		case BT747_dev.PMTK_DT_PWR_SAV_MODE:	// CMD  520
		case BT747_dev.PMTK_DT_DATUM:	// CMD  530
		case BT747_dev.PMTK_DT_FLASH_USER_OPTION:	// CMD  590
		case BT747_dev.PMTK_Q_VERSION:	// CMD  604
			// Not handled
			break;
		} // End switch
	} // End if
	return 0;
} // End method

String[] lastResponse;

public void onEvent(Event e){
	switch (e.type) {
	case ControlEvent.TIMER:
		if(m_GPSrxtx.isConnected()) {
			if(m_isLogging||m_isSearchingLog) logTimer++;  // Increase log timer to determine timeout
			lastResponse= m_GPSrxtx.getResponse();
			if(lastResponse!=null) {
				analyseNMEA(lastResponse);
			}
			if((m_isLogging||m_isSearchingLog)&& (logTimer>C_LOG_TIMEOUT_CNT)) {
				handleLogTimeOut();  // On time out resend request packet.
			}
		} else {
			removeTimer(linkTimer);
			linkTimer=null;
		}
	break;
	
	}
}  


}
