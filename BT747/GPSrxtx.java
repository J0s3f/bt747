// Copyright (c) 2007 Mario De Weerd

import waba.io.DataStream;
import waba.io.SerialPort;
import waba.sys.Convert;
import waba.sys.Settings;
import waba.ui.Control;
import waba.util.Vector;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPSrxtx extends Control {
	static final boolean GPS_DEBUG = false;
	
	int spPortNbr;
	int spSpeed=9600;  // Does not really matter on most platforms
	static SerialPort sp;
	private DataStream ds;
	
	boolean portIsOK=false;
	
	Semaphore m_writeOngoing = new Semaphore(1);
	
	
	public  GPSrxtx() {
		setDefaults();
	}
	
	public void setDefaults() {
		String Platform = Settings.platform;
		spPortNbr= BT747Settings.getPortnbr();
		if(spPortNbr!=0x5555) {
			spSpeed=BT747Settings.getBaudRate();
		} else if ((Platform.equals("Java"))||
				(Platform.equals("Win32"))||
				(Platform.equals("Linux"))) {
			// Try USB Port
			spPortNbr= SerialPort.USB;
		} else
			if ((Platform.equals("PalmOS/SDL"))||
					(Platform.equals("PalmOS"))) {
				spPortNbr= SerialPort.BLUETOOTH;
			} else{
				spPortNbr= 0;  // Should be bluetooth in WinCE
			}
		openPort();			
	}
	
	public void setBluetooth() {
		spPortNbr= SerialPort.BLUETOOTH;
		openPort();
	}
	
	public void setUsb() {
		spPortNbr= SerialPort.USB;
		openPort();
	}
	public int setPort(int port) {
		spPortNbr= port;
		return openPort();
	}
	
	public int getPort() {
		return spPortNbr;
	}
	
	public boolean isConnected() {
		return sp.isOpen();
	}
	
	public void closePort() {
		if (sp!= null && sp.isOpen()) {
			portIsOK= false;
			ds.close();
		} 
	}
	
	public int openPort() {
		closePort();
		int result=-1;
		try {
			sp=new SerialPort(spPortNbr,spSpeed);
			result=sp.lastError;
			if(portIsOK= sp.isOpen()) {
				sp.setReadTimeout(50);//small to read data in chunks and have good resp.
				//sp.writeTimeout=20;			
				sp.setFlowControl(true);
				ds=new DataStream(sp);
				BT747Settings.setPortnbr(spPortNbr);
				BT747Settings.setBaudRate(spSpeed);
			}
		}
		catch (Exception e) {
			;//if(GPS_DEBUG) {waba.sys.Vm.debug("Exception when opening port\n");}; 
		}
		return result;
	}
	
	public int error() {
		return sp.lastError;
	}
	
	
	static final int C_INITIAL_STATE = 0;
	static final int C_START_STATE = 1;
	static final int C_FIELD_STATE = 2;
	static final int C_STAR_STATE  = 3;
	static final int C_CHECKSUM_CHAR1_STATE = 4;
	static final int C_CHECKSUM_CHAR2_STATE = 5;
	static final int C_EOL_STATE = 6;
	static final int C_ERROR_STATE = 7;
	
	//The maximum length of each packet is restricted to 255 bytes	
	static final int C_BUF_SIZE = 256;
	static final int C_CMDBUF_SIZE = 256;
	
	int current_state = C_INITIAL_STATE;
	
	byte[] read_buf  = new byte[C_BUF_SIZE];
	char[] cmd_buf   = new char[C_CMDBUF_SIZE];
	char[] chk_buf   = new char[C_CMDBUF_SIZE];
	
	String[] cmd_and_param;
	
	int read_buf_p= 0;
	int cmd_buf_p = 0;
	int bytesRead= 0;	
	int checksum= 0;	
	int read_checksum;
	int cmd_idx;
	
	
	static final int ERR_NOERROR= 0;	
	static final int ERR_CHECKSUM= 1;
	static final int ERR_INCOMPLETE= 2;
	static final int ERR_TOO_LONG= 3;
	
	private int lastError;
	private static Vector vCmd = new Vector();
	private static final byte[] EOL_BYTES = {'\015','\012'};
	
	public int sendPacket(String p_Packet) {
		// Calculate checksum
		int z_Index= p_Packet.length();
		byte z_Checksum= 0;
		int z_Result=0;
		while(--z_Index>=0) {
			z_Checksum^=(byte)p_Packet.charAt(z_Index);
		}
		m_writeOngoing.down();  // Semaphore - reserve link
		try {
			z_Result+=ds.writeByte('$');
			z_Result+=ds.writeBytes(p_Packet.getBytes());
			z_Result+=ds.writeByte('*');
			z_Result+=ds.writeBytes(Convert.unsigned2hex(z_Checksum,2).getBytes());
			z_Result+=ds.writeBytes(EOL_BYTES);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		m_writeOngoing.up();  // Semaphore - release link
		return z_Result;
	}
	
	public String[] getResponse() {
		boolean continueReading;
		int myError= ERR_NOERROR;
		boolean skipError=true;
		continueReading = true;
		
		while(continueReading) {
			
			while(continueReading && (read_buf_p < bytesRead)) {
				// Still bytes in read buffer to interpret
				char c;
				chk_buf[read_buf_p]=(char)checksum;
				c= (char)read_buf[read_buf_p++];
				switch (current_state) {
				case C_EOL_STATE:
					// EOL found, record is ok.
					//						StringBuffer sb = new StringBuffer(cmd_buf_p);
					//						sb.setLength(0);
					//						sb.append(cmd_buf,0,cmd_buf_p);
					//						if(GPS_DEBUG) {waba.sys.Vm.debug(sb.toString()+"\n");}; 
					continueReading = false;
					current_state = C_START_STATE;
					// fall through
					
				case C_INITIAL_STATE:
				case C_START_STATE:
					if (c=='$') {
						// First character of NMEA string found
						current_state = C_FIELD_STATE;
						cmd_buf_p=0;
						cmd_idx=0;
						vCmd.removeAllElements();
						//cmd_and_param=new String()[];
						//cmd_buf[cmd_buf_p++]= c;
						checksum= 0;
					} else if ( ! ( (c==10) || (c==13))) {
						if(current_state==C_START_STATE) {
							myError = ERR_INCOMPLETE;
							current_state = C_ERROR_STATE;
						}
					}
					break;
				case C_FIELD_STATE:
					if ((c==10) || (c==13)) {
						current_state = C_EOL_STATE;
					} else if ( c == '*') {
						current_state = C_STAR_STATE;
						vCmd.add(new String(cmd_buf,0,cmd_buf_p));
					} else if ( c == ',') {
						checksum^= c;
						vCmd.add(new String(cmd_buf,0,cmd_buf_p));
						cmd_buf_p= 0;
					} else {
						cmd_buf[cmd_buf_p++]= c;
						checksum^= c;
					}
					break;
				case C_STAR_STATE:
					if ((c==10) || (c==13)) {
						current_state = C_ERROR_STATE;
					} else if (  (   (c>='0' && c<='9')
							||(c>='A' && c<='F')
							||(c>='a' && c<='f')
					)
					) { 
						//cmd_buf[cmd_buf_p++]= c;
						if (c>='0' && c<='9') {
							read_checksum=(c-'0')<<4;
						} else if (c>='A' && c<='F') {
							read_checksum=(c-'A'+10)<<4;
						} else {
							read_checksum=(c-'a'+10)<<4;
						}
						current_state = C_CHECKSUM_CHAR1_STATE;
					} else {
						myError = ERR_INCOMPLETE;
						current_state = C_ERROR_STATE;							
					}
					break;
				case C_CHECKSUM_CHAR1_STATE:
					if ((c==10) || (c==13)) {
						myError = ERR_INCOMPLETE;
						current_state = C_ERROR_STATE;
					} else if (  (   (c>='0' && c<='9')
							||(c>='A' && c<='F'))) { 
						//cmd_buf[cmd_buf_p++]= c;
						if (c>='0' && c<='9') {
							read_checksum+=c-'0';
						} else if (c>='A' && c<='F') {
							read_checksum+=c-'A'+10;
						} else {
							read_checksum+=c-'a'+10;
						}
						
						if(read_checksum!=checksum) {
							myError = ERR_CHECKSUM;
							current_state=C_ERROR_STATE;
						}
						current_state = C_EOL_STATE;
					} else {
						myError = ERR_INCOMPLETE;
						current_state = C_ERROR_STATE;							
					}
					break;
				case C_ERROR_STATE:
					if ( ( (c==10) || (c==13))) {
						// EOL found, start is ok.
						current_state = C_START_STATE;
					}
				}
				if(cmd_buf_p>(C_BUF_SIZE-1)) {
					myError = ERR_TOO_LONG;
					current_state = C_ERROR_STATE;
				}
				if(current_state == C_ERROR_STATE) {
					current_state = C_INITIAL_STATE;
					if (!skipError) {
						continueReading= false;
					}
				}
			}
			
			// All bytes in buffer are read.
			// If the command is not complete, we continue reading
			if(continueReading) {
				read_buf_p= 0;
				bytesRead=  0;
				try {
					int max= sp.readCheck();
					if(max>C_BUF_SIZE) {
						max=C_BUF_SIZE;
					}
					if(max>0) {
						bytesRead= sp.readBytes(read_buf,0,C_BUF_SIZE);
					}
				}
				catch (Exception e) {
					// new MessageBox("Waiting","Exception").popupBlockingModal();
					bytesRead= 0;
				}
				if(bytesRead==0) {
					continueReading=false;
				}
			}
		}		
		if (myError==ERR_NOERROR) {
		} else {
			vCmd.removeAllElements();
		}
		return (String[])vCmd.toObjectArray();
	}
	
}
