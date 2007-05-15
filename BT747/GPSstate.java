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
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPSstate extends Control {
	static final boolean GPS_DEBUG = true;
	static final boolean GPS_TEST = false;
	
	public GPSrxtx m_GPSrxtx=new GPSrxtx();    	
	
	// Fields to keep track of loggin status
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
	
	
	private Timer linkTimer;
	
	// PMTK182 commands/replies.
	static final int PMTK_LOG_SET   	= 1;
	static final int PMTK_LOG_QUERY 	= 2;
	static final int PMTK_LOG_RESP  	= 3;  // REPLY
	static final int PMTK_LOG_ON		= 4;
	static final int PMTK_LOG_OFF		= 5;
	static final int PMTK_LOG_ERASE 	= 6;
	static final int PMTK_LOG_REQ_DATA 	= 7;
	static final int PMTK_LOG_RESP_DATA	= 8;  // REPLY.
	static final int PMTK_LOG_TBD		= 9;
	
	// PMTK182,1,DATA  (DATA = what parameter to set/read/replied)
	
	static final int PMTK_LOG_FORMAT 			= 2;
	static final int PMTK_LOG_TIME_INTERVAL 	= 3;
	static final int PMTK_LOG_DISTANCE_INTERVAL = 4;
	static final int PMTK_LOG_SPEED_INTERVAL	= 5;
	static final int PMTK_LOG_REC_METHOD		= 6;
	static final int PMTK_LOG_LOG_STATUS		= 7;
	static final int PMTK_LOG_MEM_USED			= 8;  // bit 2 = logging on/off
	static final int PMTK_LOG_TBD_3				= 9;
	static final int PMTK_LOG_NBR_LOG_PTS		= 10;
	static final int PMTK_LOG_TBD2				= 11;
	
	// Other MTK commands
	static final int PMTK_TEST	= 000;
	static final int PMTK_ACK	= 001;
	static final int PMTK_SYS_MSG	= 010;
	static final int PMTK_CMD_HOT_START	= 101;
	static final int PMTK_CMD_WARM_START	= 102;
	static final int PMTK_CMD_COLD_START	= 103;
	static final int PMTK_CMD_FULL_COLD_START	= 104;
	static final int PMTK_CMD_LOG = 182;
	static final int PMTK_SET_NMEA_BAUD_RATE	= 251;
	static final int PMTK_API_SET_FIX_CTL	= 300;
	static final int PMTK_API_SET_DGPS_MODE	= 301;
	static final int PMTK_API_SET_SBAS_ENABLED	= 313;
	static final int PMTK_API_SET_NMEA_OUTPUT	= 314;
	static final int PMTK_API_SET_PWR_SAV_MODE	= 320;
	static final int PMTK_API_SET_DATUM	= 330;
	static final int PMTK_API_SET_DATUM_ADVANCE	= 331;
	static final int PMTK_API_SET_USER_OPTION	= 390;
	static final int PMTK_API_Q_FIX_CTL	= 400;
	static final int PMTK_API_Q_DGPS_MODE	= 401;
	static final int PMTK_API_Q_SBAS_ENABLED	= 413;
	static final int PMTK_API_Q_NMEA_OUTPUT	= 414;
	static final int PMTK_API_Q_PWR_SAV_MOD	= 420;
	static final int PMTK_API_Q_DATUM 	= 430;
	static final int PMTK_API_Q_DATUM_ADVANCE	= 431;
	static final int PMTK_API_GET_USER_OPTION	= 490;
	static final int PMTK_DT_FIX_CTL	= 500;
	static final int PMTK_DT_DGPS_MODE	= 501;
	static final int PMTK_DT_SBAS_ENABLED	= 513;
	static final int PMTK_DT_NMEA_OUTPUT	= 514;
	static final int PMTK_DT_PWR_SAV_MODE	= 520;
	static final int PMTK_DT_DATUM	= 530;
	static final int PMTK_DT_FLASH_USER_OPTION	= 590;
	static final int PMTK_Q_VERSION	= 604;
	
	public GPSstate() {
	}
	
	public void onStart() {
	}
	
	public void GPS_restart() {
		m_GPSrxtx.closePort();
		m_GPSrxtx.openPort();		
		getStatus();
	}
	
	public void GPS_close() {
		m_GPSrxtx.closePort();
	}
	
	public void setBluetooth() {
		m_GPSrxtx.setBluetooth();
		getStatus();
		setupTimer();
	}
	
	
	public void getStatus() {
		// Request log format from device
		m_GPSrxtx.sendPacket("PMTK"+Convert.toString(PMTK_CMD_LOG)
				+","+Convert.toString(PMTK_LOG_QUERY)+
				","+Convert.toString(PMTK_LOG_FORMAT)			
		);
		
		//if(GPS_TEST) {analyseNMEA(Convert.tokenizeString("PMTK001,2,3,3",','));}
		//if(GPS_TEST) {analyseNMEA(Convert.tokenizeString("PMTK182,3,2,3F",','));}
	}
	
	public void readLog(int startAddr, int size) {
		m_GPSrxtx.sendPacket("PMTK"+Convert.toString(PMTK_CMD_LOG)
				+","+Convert.toString(PMTK_LOG_REQ_DATA)
				+","+Convert.unsigned2hex(startAddr,8)			
				+","+Convert.unsigned2hex(size,8)			
		);
		//		if(GPS_DEBUG) {	waba.sys.Vm.debug("PMTK"+Convert.toString(PMTK_CMD_LOG)
		//				+","+Convert.toString(PMTK_LOG_REQ_DATA)
		//				+","+Convert.unsigned2hex(startAddr,8)			
		//				+","+Convert.unsigned2hex(size,8));			
		//				}
	}
	
	public void setUsb() {
		m_GPSrxtx.setUsb();
		getStatus();
		setupTimer();
	}
	
	public void setPort(int port) {
		m_GPSrxtx.setPort(port);
		getStatus();
		setupTimer();
	}
	
	private void setupTimer() {
		linkTimer = addTimer(50);
	}
	
	
	
	static public int Hex2Int(String p_Value) {
		int p_Result=0;
		for (int i = 0; i < p_Value.length(); i++) {
			int z_nibble = (byte)p_Value.charAt(i);
			if( (z_nibble>='0')&&(z_nibble<='9') ) {
				z_nibble-='0';
			} else if ( (z_nibble>='A')&&(z_nibble<='F') ) {
				z_nibble+=-'A'+10;
			} else if ( (z_nibble>='a')&&(z_nibble<='f') ) {
				z_nibble+=-'a'+10;			
			} else {
				z_nibble=0;			
			}
			p_Result<<=4;
			p_Result+=z_nibble;
		}
		return p_Result;
	}
	
	// TODO: Empty vector on reset of connection
	public IntVector logUpdate=new IntVector();
	
	
	
	/**
	 * @param p_logFormat : configuration representing format
	 * @return Bytes taken by format.
	 */
	static public int logEntrySize(int p_logFormat) {
		int z_BitMask = 0x1;
		int z_Size = 2; // one for the '*' and one for the checksum (xor)
		for (int i = 0; i < GPSLogCtrl.logFmtByteSizes.length; z_BitMask<<=1, i++) {
			if((z_BitMask & p_logFormat)!=0) {
				// Bit is set
				z_Size+= GPSLogCtrl.logFmtByteSizes[i];
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
			case PMTK_LOG_RESP:
				// Parameter information
				// TYPE = Parameter type
				// DATA = Parameter data
				// $PMTK182,3,TYPE,DATA
				int z_type= Convert.toInt(p_nmea[2]);
				if(p_nmea.length==4) {
					switch( z_type ) {
					case PMTK_LOG_FORMAT: 			// 2;
						//if(GPS_DEBUG) {	waba.sys.Vm.debug("FMT:"+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+","+p_nmea[3]+"\n");}
						logFormat=Hex2Int(p_nmea[3]);
						logEntrySize=logEntrySize(logFormat);
						logHeaderSize=logHeaderSize(logFormat);
						logUpdate.push(logFormat);
						break;
					case PMTK_LOG_TIME_INTERVAL: 	// 3;
						logTimeInterval=Convert.toInt(p_nmea[3]);
						break;
					case PMTK_LOG_DISTANCE_INTERVAL: //4;
						logDistanceInterval=Convert.toInt(p_nmea[3])/10;
						break;
					case PMTK_LOG_SPEED_INTERVAL:	// 5;
						logSpeedInterval=Convert.toInt(p_nmea[3])/10;
						break;
					case PMTK_LOG_REC_METHOD:		// 6;
						logRecMethod=Convert.toInt(p_nmea[3])/10;
						break;
					case PMTK_LOG_LOG_STATUS:		// 7; // bit 2 = logging on/off
						logStatus=Hex2Int(p_nmea[3]);
						break;
					case PMTK_LOG_MEM_USED:			// 8; 
						logMemUsed=Hex2Int(p_nmea[3]);
						break;
					case PMTK_LOG_TBD_3:			// 9;
						break;
					case PMTK_LOG_NBR_LOG_PTS:		// 10;
						logNbrLogPts=Hex2Int(p_nmea[3]);
						break;
					case PMTK_LOG_TBD2:				// 11;
						break;
					default:
					}
				}
				break;
			case PMTK_LOG_RESP_DATA:
				// Data from the log
				// $PMTK182,8,START_ADDRESS,DATA
				analyzeLogPart(Hex2Int(p_nmea[2]),p_nmea[3]);
				break;
			default:
				// Nothing - unexpected
			}
		}
		return 0; // Done.
		
	}
	static final int C_SEND_BUF_SIZE = 5;
	
	Vector sentCmds = new Vector();
	static final int C_MAX_SENT_COMMANDS = 5;  // Max commands to put in list
	
	public int sendNMEA(String p_Cmd) {
		StringBuffer z_CmdToSend = new StringBuffer(C_SEND_BUF_SIZE);
		if(p_Cmd.startsWith("PMTK")) {
			sentCmds.add(p_Cmd.substring(0,p_Cmd.indexOf(',')));
		}
		m_GPSrxtx.sendPacket(p_Cmd);
		if(sentCmds.getCount()>C_MAX_SENT_COMMANDS) {
			sentCmds.del(0);
		}
		return 0;
	}
	
	static final int PMTK_ACK_INVALID = 0;
	static final int PMTK_ACK_UNSUPPORTED = 1;
	static final int PMTK_ACK_FAILED = 2;
	static final int PMTK_ACK_SUCCEEDED = 3;
	static final int PMTK_ACK_TBD1 = 4;
	static final int PMTK_ACK_TBD2 = 5;
	
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
			case PMTK_ACK_INVALID:
				// 0: Invalid cmd or packet
				z_Result=0;
				break;
			case PMTK_ACK_UNSUPPORTED:
				// 1:	Unsupported cmd or packet
				z_Result=0;
				break;
			case PMTK_ACK_FAILED:
				// 2: Valid cmd or packet but action failed
				z_Result=0;
				break;
			case PMTK_ACK_SUCCEEDED: 
				//3: Valid cmd or packat but action succeeded
				z_Result=0;
				break;
			case PMTK_ACK_TBD1:
				// ??
				z_Result=0;
				break;
			case PMTK_ACK_TBD2:
				// ??
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
		case PMTK_CMD_LOG:	// CMD 182;
			z_Result= analyseLogNmea(p_nmea);
			break;
		case PMTK_TEST:	// CMD  000
		case PMTK_ACK:	// CMD  001
			z_Result= analyseMTK_Ack(p_nmea);
			break;
		case PMTK_SYS_MSG:	// CMD  010
		case PMTK_CMD_HOT_START:	// CMD  101
		case PMTK_CMD_WARM_START:	// CMD  102
		case PMTK_CMD_COLD_START:	// CMD  103
		case PMTK_CMD_FULL_COLD_START:	// CMD  104
		case PMTK_SET_NMEA_BAUD_RATE:	// CMD  251
		case PMTK_API_SET_FIX_CTL:	// CMD  300
		case PMTK_API_SET_DGPS_MODE:	// CMD  301
		case PMTK_API_SET_SBAS_ENABLED:	// CMD  313
		case PMTK_API_SET_NMEA_OUTPUT:	// CMD  314
		case PMTK_API_SET_PWR_SAV_MODE:	// CMD  320
		case PMTK_API_SET_DATUM:	// CMD  330
		case PMTK_API_SET_DATUM_ADVANCE:	// CMD  331
		case PMTK_API_SET_USER_OPTION:	// CMD  390
		case PMTK_API_Q_FIX_CTL:	// CMD  400
		case PMTK_API_Q_DGPS_MODE:	// CMD  401
		case PMTK_API_Q_SBAS_ENABLED:	// CMD  413
		case PMTK_API_Q_NMEA_OUTPUT:	// CMD  414
		case PMTK_API_Q_PWR_SAV_MOD:	// CMD  420
		case PMTK_API_Q_DATUM:	// CMD  430
		case PMTK_API_Q_DATUM_ADVANCE:	// CMD  431
		case PMTK_API_GET_USER_OPTION:	// CMD  490
		case PMTK_DT_FIX_CTL:	// CMD  500
		case PMTK_DT_DGPS_MODE:	// CMD  501
		case PMTK_DT_SBAS_ENABLED:	// CMD  513
		case PMTK_DT_NMEA_OUTPUT:	// CMD  514
		case PMTK_DT_PWR_SAV_MODE:	// CMD  520
		case PMTK_DT_DATUM:	// CMD  530
		case PMTK_DT_FLASH_USER_OPTION:	// CMD  590
		case PMTK_Q_VERSION:	// CMD  604
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
			lastResponse= m_GPSrxtx.getResponse();
			if(lastResponse!=null) {
				analyseNMEA(lastResponse);
			}
		} else {
			removeTimer(linkTimer);
		}
	break;
	
	}
}  


}
