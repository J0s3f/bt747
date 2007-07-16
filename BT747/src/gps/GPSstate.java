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
import waba.sys.Settings;
import waba.sys.Thread;
import waba.sys.Vm;
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.MainWindow;
import waba.ui.ProgressBar;
import waba.ui.Timer;
import waba.util.Vector;

import gps.convert.Conv;



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
public class GPSstate implements Thread {
    static final boolean GPS_DEBUG = !Settings.onDevice;
    static final boolean GPS_TEST = false;
    
    private GPSrxtx m_GPSrxtx=new GPSrxtx();    	
    ProgressBar m_ProgressBar=null;
    
    // Fields to keep track of logging status
    private int logTimer=Vm.getTimeStamp();  // Value that increases at each timer event
    private int m_StartAddr;
    private int m_EndAddr;
    private int m_NextReqAddr;
    private int m_NextReadAddr;
    private int m_Step;
    private boolean m_isLogging = false;       // True when dumping log
    private boolean m_isSearchingLog = false;  // True when doing binary search
    private boolean m_isSearchingEnd = false;  // True when doing binary search
    private boolean m_isCheckingLog = false;  // True when doing binary search
    private boolean m_getFullLog= true; // If true, get the entire log (based on block head)
    private boolean m_recoverFromError= false; // When true, must recover from error
    private boolean m_getNextLogOnNextTimer = false;
    
    
    public int logFormat = 0;
    public int logEntrySize = 0;
    public int logRecordMaxSize = 0;
    public int logTimeInterval = 0;
    public int logSpeedInterval = 0;
    public int logDistanceInterval = 0;
    public int logStatus = 0;
    public int logRecMethod = 0;
    public int logNbrLogPts = 0;
    public int logMemSize = 16*1024*1024/8; //16Mb -> 2MB
    public int logMemUsefullSize = (int)(logMemSize*0.875); //16Mb
    public int logMemUsed = 0;
    public int logMemUsedPercent = 0;
    public int logMemFree = 0;
    public int logMemMax = 0;
    
    public int logFix = 0; // Time between fixes
    public int datum = 0; // Datum WGS84, TOKYO-M, TOKYO-A
    
    public boolean loggingIsActive = false;
    
    public int dgps_mode=0;
    
    private byte[] m_Data= new byte[0x800];  // buffer used for reading data.
    
    // Number of log entries to request 'ahead'.
    final static int C_MAX_LOG_AHEAD = 1;
    
    Control m_EventPosterObject= null;
    
    private settings m_settings;
    
    /** Initialiser
     * 
     *
     */
    public GPSstate(settings s) {
        m_settings = s;
    }
    
    public void setEventPosterObject(Control s) {
        m_EventPosterObject = s;
    }
    
    /** Provide the progress bar to use (download progress)
     * 
     * @param pb ProgressBar
     */
    public void setProgressBar(ProgressBar pb) {
        m_ProgressBar= pb;
    }
    
    /** Some initialisation
     * 
     *
     */
    public void onStart() {
        int port= m_settings.getPortnbr();
        if(port!=0x5555) {
            m_GPSrxtx.setDefaults(port,m_settings.getBaudRate());
        }
        
        if (m_settings.getStartupOpenPort()) {
            GPS_connect();
        }
        
    }
    
    /** Restart the GPS connection
     * Will close the current connection (if still open) and
     * open it again (use the same parameters)
     */
    public void GPS_restart() {
        m_GPSrxtx.closePort();
        GPS_connect();
    }
    
    private void GPS_connect() {
        m_GPSrxtx.openPort();
        GPS_postConnect();
    }

    private void GPS_postConnect() {
        if(m_GPSrxtx.isConnected()) {
            PostStatusEvent(GpsEvent.CONNECTED);
            getStatus();
            setupTimer();
        }
    }
    
    /** Close the GPS connection
     */
    public void GPS_close() {
        m_GPSrxtx.closePort();
    }
    
    /** open a Bluetooth connection
     * Calls getStatus to request initial parameters from the device.
     * Set up the timer to regurarly poll the connection for data.
     */
    public void setBluetooth() {
        m_GPSrxtx.setBluetooth();
        GPS_postConnect();
    }
    
    /** open a Usb connection
     * Calls getStatus to request initial parameters from the device.
     * Set up the timer to regurarly poll the connection for data.
     */
    public void setUsb() {
        m_GPSrxtx.setUsb();
        GPS_postConnect();
    }
    
    /** open a connection on the given port number.
     * Calls getStatus to request initial parameters from the device.
     * Set up the timer to regurarly poll the connection for data.
     * @param port Port number to open
     */
    public void setPort(int port) {
        m_GPSrxtx.setPort(port);
        GPS_postConnect();
    }
    
    /** Start the timer
     * To be called once the port is opened.
     * The timer is used to launch functions that will check if there
     * is information on the serial connection or to send to the GPS
     * device.
     */
    private void setupTimer() {
        if( m_GPSrxtx.isConnected()) {
            // Check if old timer still present (for safety)
            // TODO: check if old thread is still present
            //if(linkTimer!=null) removeTimer(linkTimer);
            MainWindow.getMainWindow().addThread(this, false);

            // Remember defaults
            m_settings.setPortnbr(m_GPSrxtx.spPortNbr);
            m_settings.setBaudRate(m_GPSrxtx.spSpeed);
        }
    }
    
    /** Set the loggin format of the device.<br>
     * Must be followed by eraseLog.
     * @param p_logFormat The format to set.
     */
    public void setLogFormat(final int p_logFormat) {
        // Ensure option consistency.
        int logFormat;
        logFormat= p_logFormat;
        if((logFormat&(1<<BT747_dev.FMT_SID_IDX))==0) {
            // If SID is not set, some other settings can not be
            // set either.  Be sure they are disabled in that
            // case.
            logFormat&=~(  (1<<BT747_dev.FMT_ELEVATION_IDX)
                    |(1<<BT747_dev.FMT_AZIMUTH_IDX)
                    |(1<<BT747_dev.FMT_SNR_IDX)
            );
        }
        m_GPSrxtx.sendPacket("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_SET_STR
                +","+BT747_dev.PMTK_LOG_FORMAT_STR
                +","+Convert.unsigned2hex(logFormat,8)					
        );
    }
    
    /** erase the log - takes a while
     * TODO: Find out a way to follow up on erasal (status)
     */
    
    public void eraseLog() {
        m_GPSrxtx.sendPacket("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_ERASE
                +","+BT747_dev.PMTK_LOG_ERASE_YES_STR				
        );
    }
    
    /** A single request to get information from the device's log.
     * @param startAddr start address of the data range requested
     * @param size      size of the data range requested
     */
    public void readLog(final int startAddr, final int size) {
        m_GPSrxtx.sendPacket("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_REQ_DATA_STR
                +","+Convert.unsigned2hex(startAddr,8)			
                +","+Convert.unsigned2hex(size,8)			
        );
        //		if(GPS_DEBUG) {	waba.sys.Vm.debug("PMTK"+Convert.toString(PMTK_CMD_LOG)
        //				+","+Convert.toString(PMTK_LOG_Q_LOG)
        //				+","+Convert.unsigned2hex(startAddr,8)			
        //				+","+Convert.unsigned2hex(size,8));			
        //				}
    }
    
    /** Handle time outs on log requests
     * Will be called if the device did not respond to a log request.
     * TODO: Could be extended to all packets.
     * TODO: Implement handleLogTimeOUt
     */
    public void handleLogTimeOut() {
//        Vm.debug("Timeout");
        logTimer=Vm.getTimeStamp();
        m_recoverFromError=false; 
        recoverFromLogError();
    }
    
    /** Calculate the size of one log entry.
     * <b>Only works if the satellite information is not requested.</b><br>
     * Satellite information is of variant size.
     * @param p_logFormat : configuration representing format
     * @return Number of bytes needed for one record.
     */
    static public int logEntrySize(final int p_logFormat) {
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
    
    /** Get the (approximate) location of the given record number.
     * The result is not exact: the position may be different.
     * @param p_RecordNumber The record number for which to find the address
     * @return Address for record number
     */
    public int logEntryAddr(final int p_RecordNumber) {
        return logRecordMaxSize+p_RecordNumber*logEntrySize;
    }
    
    /* TODO: Could implement specific event structure */
    private void PostStatusUpdateEvent() {
        if(m_EventPosterObject!=null) {
            m_EventPosterObject.postEvent(new Event(GpsEvent.DATA_UPDATE, m_EventPosterObject,0));
        }
    }
    //getParent().postEvent(new Event(ControlEvent.TIMER,getParent(),0));

    private void PostStatusEvent(final int event) {
        if(m_EventPosterObject!=null) {
            m_EventPosterObject.postEvent(new Event(event, m_EventPosterObject,0));
        }
    }

    public int analyseLogNmea(String[] p_nmea) {
        //if(GPS_DEBUG) {	waba.sys.Vm.debug("LOG:"+p_nmea.length+':'+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+"\n");}
        // Suppose that the command is ok (PMTK182)
        
        // Currently taking care of replies from the device only.
        // The other data we send ourselves
        if(p_nmea.length>2) {
            switch( Convert.toInt(p_nmea[1]) ) {
            case BT747_dev.PMTK_LOG_DT:
                // Parameter information
                // TYPE = Parameter type
                // DATA = Parameter data
                // $PMTK182,3,TYPE,DATA
                int z_type= Convert.toInt(p_nmea[2]);
            if(p_nmea.length==4) {
                switch( z_type ) {
                case BT747_dev.PMTK_LOG_FORMAT: 			// 2;
                    //if(GPS_DEBUG) {	waba.sys.Vm.debug("FMT:"+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+","+p_nmea[3]+"\n");}
                    logFormat=Conv.hex2Int(p_nmea[3]);
                logEntrySize=logEntrySize(logFormat);
                logRecordMaxSize=BT747_dev.logRecordMinSize(logFormat);
                PostStatusUpdateEvent();
                break;
                case BT747_dev.PMTK_LOG_TIME_INTERVAL: 	// 3;
                    logTimeInterval=Convert.toInt(p_nmea[3])/10;
                PostStatusUpdateEvent();
                break;
                case BT747_dev.PMTK_LOG_DISTANCE_INTERVAL: //4;
                    logDistanceInterval=Convert.toInt(p_nmea[3])/10;
                PostStatusUpdateEvent();
                break;
                case BT747_dev.PMTK_LOG_SPEED_INTERVAL:	// 5;
                    logSpeedInterval=Convert.toInt(p_nmea[3])/10;
                PostStatusUpdateEvent();
                break;
                case BT747_dev.PMTK_LOG_REC_METHOD:		// 6;
                    logRecMethod=Convert.toInt(p_nmea[3])/10;
                PostStatusUpdateEvent();
                break;
                case BT747_dev.PMTK_LOG_LOG_STATUS:		// 7; // bit 2 = logging on/off
                    logStatus=Conv.hex2Int(p_nmea[3]);
                loggingIsActive=((logStatus&BT747_dev.PMTK_LOG_STATUS_LOGONOF_MASK)!=0);
                PostStatusUpdateEvent();
                break;
                case BT747_dev.PMTK_LOG_MEM_USED:			// 8; 
                    logMemUsed=Conv.hex2Int(p_nmea[3]);
                logMemUsedPercent=
                    (100*(logMemUsed-(0x200*((logMemUsed+0xFFFF)/0x10000))))
                    /logMemUsefullSize;
                PostStatusUpdateEvent();
                break;
                case BT747_dev.PMTK_LOG_TBD3:			// 9;
                    break;
                case BT747_dev.PMTK_LOG_NBR_LOG_PTS:		// 10;
                    logNbrLogPts=Conv.hex2Int(p_nmea[3]);
                PostStatusUpdateEvent();
                break;
                case BT747_dev.PMTK_LOG_TBD2:				// 11;
                    break;
                default:
                }
            }
            break;
            case BT747_dev.PMTK_LOG_DT_LOG:
                // Data from the log
                // $PMTK182,8,START_ADDRESS,DATA
                
                try {
                    logTimer=Vm.getTimeStamp();
//                    waba.sys.Vm.debug("Before AnalyzeLog:"+p_nmea[3].length());
                    analyzeLogPart(Conv.hex2Int(p_nmea[2]), p_nmea[3]);
                } catch (Exception e) {
                    // During debug: array index out of bounds
                    // TODO: handle exception
                }
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
    
    /** Request the current log format from the device */
    public void getLogFormat() {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_QUERY_STR+
                ","+BT747_dev.PMTK_LOG_FORMAT_STR           
        );
    }
    
    public void getLogCtrlInfo() {
        // Request log status from device
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_QUERY_STR+
                ","+BT747_dev.PMTK_LOG_LOG_STATUS_STR           
        );
        // Request mem size from device
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_QUERY_STR+
                ","+BT747_dev.PMTK_LOG_MEM_USED_STR           
        );
        // Request number of log points
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_QUERY_STR+
                ","+BT747_dev.PMTK_LOG_NBR_LOG_PTS_STR          
        );
        //        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
        //                +","+BT747_dev.PMTK_LOG_QUERY_STR+
        //                ","+BT747_dev.PMTK_LOG_TBD3_STR          
        //        );
        //        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
        //                +","+BT747_dev.PMTK_LOG_QUERY_STR+
        //                ","+BT747_dev.PMTK_LOG_TBD2_STR          
        //        );
        //      sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
        //              +","+BT747_dev.PMTK_LOG_QUERY_STR+
        //              ","+12 );       
    }
    
    public void getLogReasonStatus() {
        /* Get log distance interval */
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_QUERY_STR+
                ","+BT747_dev.PMTK_LOG_DISTANCE_INTERVAL_STR           
        );
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_QUERY_STR+
                ","+BT747_dev.PMTK_LOG_SPEED_INTERVAL_STR           
        );
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_QUERY_STR+
                ","+BT747_dev.PMTK_LOG_TIME_INTERVAL_STR       
        );
        
    }
    public void setLogTimeInterval(final int value) {
        int z_value=value;
        if(z_value!=0 && z_value>999) {
            z_value=999;
        }
        /* Get log distance interval */
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_SET_STR+
                ","+BT747_dev.PMTK_LOG_TIME_INTERVAL_STR+           
                ","+Convert.toString(z_value*10)           
        );
    }
    
    public void setLogDistanceInterval(final int value) {
        int z_value=value;
        if(z_value!=0 && z_value>9999) {
            z_value=9999;
        } else if(z_value!=0 && z_value<10) {
            z_value=10;
        }
        
        /* Get log distance interval */
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_SET_STR+
                ","+BT747_dev.PMTK_LOG_DISTANCE_INTERVAL_STR+           
                ","+Convert.toString(z_value*10)           
        );
    }
    
    
    public void setLogSpeedInterval(final int value) {
        int z_value=value;
        if(z_value!=0 && z_value>999) {
            z_value=999;
        } else if(z_value!=0 && z_value<10) {
            z_value=10;
        }
        /* Get log distance interval */
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_SET_STR+
                ","+BT747_dev.PMTK_LOG_SPEED_INTERVAL_STR+           
                ","+Convert.toString(z_value*10)           
        );
    }
    
    public void setFixInterval(final int value) {
        int z_value=value;
        if(z_value>1000) {
            z_value=1000;
        } else if(z_value<200) {
            z_value=200;
        }
        
        /* Set log distance interval */
        sendNMEA("PMTK"+BT747_dev.PMTK_API_SET_FIX_CTL           
                +","+Convert.toString(z_value)
                +",0,0,0.0,0.0"
        );
    }
    
    public void getFixInterval() {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_API_Q_FIX_CTL
        );
    }
    
    
    /** Get the current status of the device */
    public void getStatus() {
        getLogFormat();
        getLogCtrlInfo();
        //getLogReasonStatus();
        //getPowerSaveEnabled();
        //getSBASEnabled();
        //getDGPSMode();
        //getDatumMode();
        //getFixInterval();
    }
    
    public void getLogOnOffStatus() {
        getLogCtrlInfo();
    }
    
    /** Activate the logging by the device */
    public void startLog() {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_ON           
        );
    }
    
    
    /** Stop the automatic logging of the device */
    public void stopLog() {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_CMD_LOG_STR
                +","+BT747_dev.PMTK_LOG_OFF           
        );
    }
    public boolean SBASEnabled=false;
    public boolean SBASTestEnabled=false;
    public boolean powerSaveEnabled=false;
    
    public void setSBASTestEnabled(final boolean set) {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_API_SET_SBAS_TEST_STR
                +","+(set?"0":"1")           
        );
    }
    public void getSBASTestEnabled() {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_API_Q_SBAS_TEST_STR);
    }
    
    
    public void setSBASEnabled(final boolean set) {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_API_SET_SBAS_STR
                +","+(set?"1":"0")           
        );
    }
    public void getSBASEnabled() {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_API_Q_SBAS_STR);
    }
    
    
    public void setPowerSaveEnabled(final boolean set) {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_API_SET_PWR_SAV_MODE_STR
                +","+(set?"1":"0")           
        );
    }
    public void getPowerSaveEnabled() {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_API_Q_PWR_SAV_MOD_STR);
    }
    public void setDGPSMode(final int mode) {
        // Request log format from device
        if (mode >=0 && mode <=2) {
            sendNMEA("PMTK"+BT747_dev.PMTK_API_SET_DGPS_MODE_STR
                    +","+Convert.toString(mode)           
            );
        }
    }
    public void getDGPSMode() {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_API_Q_DGPS_MODE_STR);
    }
    
    public void setDatumMode(final int mode) {
        // Request log format from device
        if (mode >=0 && mode <=2) {
            sendNMEA("PMTK"+BT747_dev.PMTK_API_SET_DATUM_STR
                    +","+Convert.toString(mode)           
            );
        }
    }
    public void getDatumMode() {
        // Request log format from device
        sendNMEA("PMTK"+BT747_dev.PMTK_API_Q_DATUM_STR);
    }
    
    // TODO: When acknowledge is missing for some commands, take appropriate action.
    public int analyseMTK_Ack(final String[]p_nmea) {
        // PMTK001,Cmd,Flag
        int z_Flag;
        int z_Result=-1;
        //if(GPS_DEBUG) {	waba.sys.Vm.debug(p_nmea[0]+","+p_nmea[1]+"\n");}
        
        if(p_nmea.length>=3) {
            String z_MatchString;
            z_Flag=Convert.toInt(p_nmea[p_nmea.length-1]); // Last parameter
            z_MatchString="PMTK"+p_nmea[1];
            for (int i = 2; i < p_nmea.length-2; i++) {
                // ACK is variable length, can have parameters of cmd.
                z_MatchString+=","+p_nmea[i];
            }
            int z_CmdIdx=sentCmds.find(z_MatchString);
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
            for (int i= z_CmdIdx; i >= 0; i--) {
                sentCmds.del(0);
            }
        }
        return z_Result;
    }
    
    private File m_logFile=new File("");
    public void closeLog() {
        try {
            if(m_logFile!=null  && m_logFile.isOpen()) {
//                Vm.debug("CloseLog");
                m_logFile.close();
            }
        }
        catch (Exception e) {
        }
    }
    public void endGetLog() {
        m_isLogging= false;
        closeLog();
        if(m_ProgressBar!=null) {
            m_ProgressBar.setVisible(false);
            m_ProgressBar.repaintNow();
        }
    }
    
    public void cancelGetLog() {
        endGetLog();
    }
    
    private final static int C_BLOCKVERIF_START = 0x200; 
    private final static int C_BLOCKVERIF_SIZE  = 0x200;    
    /**
     * @param p_StartAddr
     * @param p_EndAddr
     * @param p_Step
     * @param p_FileName
     */
    public void getLogInit(
            final int p_StartAddr,
            final int p_EndAddr,
            final int p_Step,
            final String p_FileName,
            final boolean incremental,  // True if incremental read
            ProgressBar pb) {
        m_isLogging= false;
        m_isCheckingLog=false;
        m_StartAddr= p_StartAddr;
        m_EndAddr=((p_EndAddr+0xFFFF)&0xFFFF0000)-1;
        m_NextReqAddr= m_StartAddr;
        m_NextReadAddr= m_StartAddr;
        m_Step=p_Step;
        m_ProgressBar=pb;
        if(pb!=null) {
            pb.min=m_StartAddr;
            pb.max=m_EndAddr;
            pb.setValue(m_NextReadAddr,""," b");
            pb.setVisible(true);
        }
        if(incremental) {
            reOpenLogRead(p_FileName);
            if(m_logFile!=null&&m_logFile.isOpen()) {
                // There is a file with data.
                if (m_logFile.getSize()>=(C_BLOCKVERIF_START+C_BLOCKVERIF_SIZE)){
                    // There are enough bytes in the saved file.
                    
                    // Find first incomplete block
                    int blockHeadPos=0;
                    boolean continueLoop;
                    do {
                        byte[] bytes=new byte[2];
                        m_logFile.setPos(blockHeadPos);
                        continueLoop=(m_logFile.readBytes(bytes, 0, 2)==2);
                        if(continueLoop) {
                            // Break the loop if this block was incomplete.
                            continueLoop=!(((bytes[0]&0xFF)==0xFF)
                                    &&((bytes[1]&0xFF)==0xFF));
                        }
                        if(continueLoop) {
                            // This block is fully filled
                            blockHeadPos+=0x10000;
                            continueLoop=(blockHeadPos<=(m_logFile.getSize()&0xFFFF0000));
                        }
                    } while(continueLoop);
                    

                    if(blockHeadPos>m_logFile.getSize()) {
                        // All blocks already had data - continue from end of file.
                        m_NextReadAddr=m_logFile.getSize();
                        m_NextReqAddr=m_NextReadAddr;
                    } else {
                        // Start just past block header
                        m_NextReadAddr=blockHeadPos+0x200;
                        do {
                            // Find a block 
                            m_logFile.setPos(m_NextReadAddr);
                            continueLoop=((m_logFile.readBytes(m_Data, 0, 0x200)==0x200));
                            if(continueLoop) {
                                boolean allFF=true;
                                // Check if all FFs in the file.
                                for (int i = 0; allFF&&(i < 0x200); i++) {
                                    allFF=((m_Data[i]&0xFF)==0xFF);
                                }
                                continueLoop=!allFF;
                                if(continueLoop) {
                                    m_NextReadAddr+=0x200;
                                }
                            }
                        } while (continueLoop);
                        m_NextReadAddr-=0x200;
                        m_NextReqAddr=m_NextReadAddr;
                        
                        //TODO: should read 2 bytes in header once rest of block was loaded
                        // in order to have precise header information
                        // -> We can not load this value from memory know as we might
                        //    corrupt the data (0xFFFF present if restarting download)
                    }
                    
                    readLog(C_BLOCKVERIF_START,C_BLOCKVERIF_SIZE);  // Read 200 bytes, just past header.
                    m_isCheckingLog=true;
                }
            }
        }
        if(!m_isCheckingLog) {
            // File could not be opened or is not incremental.
            openNewLog(p_FileName);
            m_isLogging=true;
            m_getNextLogOnNextTimer=true;
        }
    }
    
    private void openNewLog(final String fileName) {
        if(m_logFile!=null&&m_logFile.isOpen()) {m_logFile.close();}
        try {
            m_logFile=new File(fileName);
            if(m_logFile.exists()) {
                m_logFile.delete();
            }
        } catch (Exception e) {
//            Vm.debug("Exception new log delete");
        }
        try {
            m_logFile=new File(fileName,File.CREATE);
            m_logFile.close();
            m_logFile=new File(fileName,File.READ_WRITE);
        } catch (Exception e) {
//            Vm.debug("Exception new log create");
        }
    }
    
    
    private void reOpenLogRead(final String fileName) {
        closeLog();
        try {
            m_logFile=new File(fileName,File.READ_ONLY);
        } catch (Exception e) {
//            Vm.debug("Exception reopen log read");
        }
        
    }
    
    private void reOpenLogWrite(final String fileName) {
        closeLog();
        try {
            m_logFile=new File(fileName,File.READ_WRITE);
        } catch (Exception e) {
//            Vm.debug("Exception reopen log write");
        }
    }
    
    
    // Called regurarly
    public void getNextLogPart() {
        if ( m_isLogging ) {
            //Vm.debug("NextLogPart");
            //TODO: Stop the thread at the end.
            int z_Step;
            if((m_NextReqAddr+(C_MAX_LOG_AHEAD-1)*m_Step)<=m_NextReadAddr) {
                z_Step=m_EndAddr-m_NextReqAddr+1;
                if(m_recoverFromError && z_Step>0x800) {
                    z_Step=0x800;
                }
                if(z_Step>0) {
                    if(z_Step>m_Step){
                        z_Step=m_Step;
                    }
                    readLog(m_NextReqAddr,z_Step);
                    m_NextReqAddr+=z_Step;
                }            
            }
        }
    }
    
    private final int HexStringToBytes(final String hexStr) {
        char[] z_Data = hexStr.toCharArray();
        int length=z_Data.length/2;
        for (int i = 0; i < z_Data.length; i += 2) {
            int c1 = z_Data[i]-'0';  // Uppercase
            int c2 = z_Data[i + 1]-'0'; // Uppercase
            if (!((c1 >= 0) && (c1 <= 9))) {
                c1&=~0x20; // Uppercase
                if ((c1 >= 'A'-'0') && (c1 <= 'F'-'0')) {
                    c1 += -('A'-'0') + 10;
                } else {
                    c1 = 0;
                }
            }
            if (!((c2 >= 0) && (c2 <= 9))) {
                c2&=~0x20; // Uppercase
                if ((c2 >= 'A'-'0') && (c2 <= 'F'-'0')) {
                    c2 += -('A'-'0') + 10;
                } else {
                    c2 = 0;
                }
            }
            m_Data[i >> 1] = (byte) ((c1 << 4) + c2);
        }
        
        return length;
    }
    
    public void recoverFromLogError() {
        // TODO: Handle error in order of data
        //m_isLogging= false;
        m_NextReqAddr=m_NextReadAddr;
        if(!m_recoverFromError) {
            m_recoverFromError=true;
            m_getNextLogOnNextTimer=true;
        }
    }
    private static final int C_MAX_FILEBLOCK_WRITE = 0x800;
    public void	analyzeLogPart(final int p_StartAddr, final String p_Data) {
        int dataLength;
        dataLength=HexStringToBytes(p_Data); // Fills m_data
//        Vm.debug("Got "+Convert.toString(p_Data.length())+"): "+Convert.toString(dataLength));
        if( m_isLogging) {
            if(m_NextReadAddr==p_StartAddr) {
                m_recoverFromError=false;
                int j=0;
                
                // The Palm platform showed problems writing 0x800 blocks.
                // This splits it in smaller blocks and solves that problem.
                if(dataLength!=0x800 && ((m_NextReadAddr+dataLength)!=m_NextReqAddr)) {
                    // Received data is not the right size - transmission error.
                    //  Can happen on Palm over BT.
                    getNextLogPart();
                } else {
                    // Data seems ok
                    for (int i = dataLength; i>0; i-=C_MAX_FILEBLOCK_WRITE) {
                        int l=i;
                        if(l>C_MAX_FILEBLOCK_WRITE) {
                            l=C_MAX_FILEBLOCK_WRITE;
                        }
//                        Vm.debug("Writing("+Convert.toString(p_StartAddr)+"): "+Convert.toString(j)+" "+Convert.toString(l));
                        
                        int q;
                        if((q=m_logFile.writeBytes(m_Data,j,l))!=l) {
//                            Vm.debug("Problem during anaLog: "+Convert.toString(m_logFile.lastError));
                            cancelGetLog();
                            Vm.debug(Convert.toString(q));
                        };
                        j+=l;
                    }
                    m_NextReadAddr+=dataLength;
                    if(m_ProgressBar!=null) {
                        m_ProgressBar.setValue(m_NextReadAddr);
                    }
                    if(m_getFullLog
                            &&
                            (((p_StartAddr-1+dataLength)&0xFFFF0000)>=p_StartAddr)
                    ) {
                        // Block boundery (0xX0000) is inside data.
                        int blockStart=0xFFFF&(0x10000-(p_StartAddr&0xFFFF));
                        if(!(((m_Data[blockStart]&0xFF)==0xFF)
                                &&((m_Data[blockStart+1]&0xFF)==0xFF))) {
                            // This block is full, next block is still data
                            int minEndAddr;
                            minEndAddr= (p_StartAddr&0xFFFF0000)+0x20000-1; // This block and next one.
                            if(minEndAddr>logMemSize-1) {
                                minEndAddr=logMemSize-1;
                            }
                            if(minEndAddr>m_EndAddr) {
                                m_EndAddr= minEndAddr;
                                if(m_ProgressBar!=null) {
                                    m_ProgressBar.max=m_EndAddr;
                                    m_ProgressBar.repaintNow();
                                }
                            }
                        }
                    }
                }
                if(m_NextReadAddr>m_EndAddr) {
                    endGetLog();
                } else {
                    m_getNextLogOnNextTimer=true;
                }
            } else {
                //recoverFromLogError();  // Let the timeout handle it to avoid repeated requests
            }
        } else if ( m_isCheckingLog) {
            if((p_StartAddr==C_BLOCKVERIF_START) && (dataLength==C_BLOCKVERIF_SIZE)) {
                // The block we got should be the block to check
                byte[] m_localdata=new byte[dataLength];
                int result;
                boolean success=false;
                m_logFile.setPos(p_StartAddr);
                result=m_logFile.readBytes(m_localdata,0, dataLength);
                if(result==dataLength) {
                    success=true;
                    for(int i=dataLength-1;i>=0;i--) {
                        if(m_Data[i]!=m_localdata[i]) {
                            // The log is not the same, data is different
                            success=false;
                            break; // Exit from the loop
                        }
                    }
                }
                String fileName=m_logFile.getPath();
                if(success) {
                    // Downloaded data seems to correspond - start incremental download
                    // TODO: Could find exact position at the end of the log to continue
                    // download.  Currently starting with last full block (0x10000)
                    reOpenLogWrite(fileName);
//                    m_NextReqAddr=((m_logFile.getSize()-1) & 0xFFFF0000);
//                    if(m_NextReadAddr<m_NextReqAddr) {
//                        m_NextReadAddr=m_NextReqAddr;
//                    } else {
//                        m_NextReqAddr=m_NextReadAddr;
//                    }
                    m_logFile.setPos(m_NextReadAddr);
                    m_isCheckingLog=false;
                    m_isLogging=true;
                    m_getNextLogOnNextTimer=true;
                } else {
                    // Log is not the same - delete the log and reopen.
                    openNewLog(fileName);
                    m_isLogging=true;
                    m_NextReadAddr=0;
                    m_NextReadAddr=0;
                    m_getNextLogOnNextTimer=true;
                }
            }
        }
    }
    
    public int analyseNMEA(String[] p_nmea) {
        int z_Cmd;
        int z_Result;
        z_Result = 0;
        //if(GPS_DEBUG) {	waba.sys.Vm.debug("ANA:"+p_nmea[0]+","+p_nmea[1]+"\n");}
        if(p_nmea[0].startsWith("GPZDA")) {
            // GPZDA,$time,$msec,$DD,$MO,$YYYY,03,00
            
        } else if(p_nmea[0].startsWith("GPRMC")) {
            // GPRMC,$time,$fix,$latf1,$ns,$lonf1,$ew,$knots,$bear,$date,$magnvar,$magnew,$magnfix
        } else if(p_nmea[0].startsWith("GPSTPV")) {
            // GPSTPV,$epoch.$msec,?,$lat,$lon,,$alt,,$speed,,$bear,,,,A
            
        } else if(p_nmea[0].startsWith("PMTK")) {
            if(GPS_DEBUG&&(!p_nmea[1].startsWith("8"))) {
                String s;
                s="<";
                waba.sys.Vm.debug("<");
                for (int i = 0; i < p_nmea.length; i++) {
                    s+=p_nmea[i];
                    s+=",";
                };
                waba.sys.Vm.debug(s);
            }   
            z_Cmd= Convert.toInt(p_nmea[0].substring(4));
            
            z_Result=-1;  // Suppose cmd not treated
            switch(z_Cmd) {
            case BT747_dev.PMTK_CMD_LOG:	// CMD 182;
                z_Result= analyseLogNmea(p_nmea);
            break;
            case BT747_dev.PMTK_TEST:	// CMD  000
                break;
            case BT747_dev.PMTK_ACK:	// CMD  001
                z_Result= analyseMTK_Ack(p_nmea);
            break;
            case BT747_dev.PMTK_SYS_MSG:	// CMD  010
                break;
            case BT747_dev.PMTK_CMD_HOT_START:	// CMD  101
                break;
            case BT747_dev.PMTK_CMD_WARM_START:	// CMD  102
                break;
            case BT747_dev.PMTK_CMD_COLD_START:	// CMD  103
                break;
            case BT747_dev.PMTK_CMD_FULL_COLD_START:	// CMD  104
                break;
            case BT747_dev.PMTK_SET_NMEA_BAUD_RATE:	// CMD  251
                break;
            case BT747_dev.PMTK_API_SET_FIX_CTL:	// CMD  300
                break;
            case BT747_dev.PMTK_API_SET_DGPS_MODE:	// CMD  301
                break;
            case BT747_dev.PMTK_API_SET_SBAS:	// CMD  313
                break;
            case BT747_dev.PMTK_API_SET_NMEA_OUTPUT:	// CMD  314
                break;
            case BT747_dev.PMTK_API_SET_PWR_SAV_MODE:	// CMD  320
                break;
            case BT747_dev.PMTK_API_SET_DATUM:	// CMD  330
                break;
            case BT747_dev.PMTK_API_SET_DATUM_ADVANCE:	// CMD  331
                break;
            case BT747_dev.PMTK_API_SET_USER_OPTION:	// CMD  390
                break;
            case BT747_dev.PMTK_API_Q_FIX_CTL:	// CMD  400
                break;
            case BT747_dev.PMTK_API_Q_DGPS_MODE:	// CMD  401
                break;
            case BT747_dev.PMTK_API_Q_SBAS:	// CMD  413
                break;
            case BT747_dev.PMTK_API_Q_NMEA_OUTPUT:	// CMD  414
                break;
            case BT747_dev.PMTK_API_Q_PWR_SAV_MOD:	// CMD  420
                break;
            case BT747_dev.PMTK_API_Q_DATUM:	// CMD  430
                break;
            case BT747_dev.PMTK_API_Q_DATUM_ADVANCE:	// CMD  431
                break;
            case BT747_dev.PMTK_API_Q_GET_USER_OPTION:	// CMD  490
                break;
            case BT747_dev.PMTK_DT_FIX_CTL:	// CMD  500
                if(p_nmea.length>=2) {
                    logFix=Convert.toInt(p_nmea[1]);
                };
                break;
            case BT747_dev.PMTK_DT_DGPS_MODE:	// CMD  501
                if(p_nmea.length==2) {
                    dgps_mode=Convert.toInt(p_nmea[1]);
                };
                PostStatusUpdateEvent();
                break;
            case BT747_dev.PMTK_DT_SBAS:	// CMD  513
                if(p_nmea.length==2) {
                    SBASEnabled=(p_nmea[1].equals("1"));
                };
                PostStatusUpdateEvent();
                break;
            case BT747_dev.PMTK_DT_NMEA_OUTPUT:	// CMD  514
                break;
            case BT747_dev.PMTK_DT_SBAS_TEST:    // CMD  513
                if(p_nmea.length==2) {
                    SBASTestEnabled=(p_nmea[1].equals("0"));
                };
                PostStatusUpdateEvent();
                break;            
            case BT747_dev.PMTK_DT_PWR_SAV_MODE:	// CMD  520
                if(p_nmea.length==2) {
                    powerSaveEnabled=(p_nmea[1].equals("1"));
                };
                PostStatusUpdateEvent();
                break;
            case BT747_dev.PMTK_DT_DATUM:	// CMD  530
                if(p_nmea.length==2) {
                    datum=Convert.toInt(p_nmea[1]);
                };
                PostStatusUpdateEvent();
                break;
            case BT747_dev.PMTK_DT_FLASH_USER_OPTION:	// CMD  590
                PostStatusUpdateEvent();
            break;
            case BT747_dev.PMTK_Q_VERSION:	// CMD  604
                // Not handled
                break;
            } // End switch
        } // End if
        return z_Result;
    } // End method
    
    String[] lastResponse;
    
    public void onEvent(Event e){
        switch (e.type) {
        case ControlEvent.TIMER:
        break;
        
        }
    }  
    /* (non-Javadoc)
     * @see waba.sys.Thread#run()
     */
    public void run() {
        // TODO Auto-generated method stub
        if(m_GPSrxtx.isConnected()) {
            if(m_getNextLogOnNextTimer) {
                // Sending command on next timer adds some delay after
                // the end of the previous command (reception)
                m_getNextLogOnNextTimer=false;
                getNextLogPart();
            }
            lastResponse= m_GPSrxtx.getResponse();
            while(lastResponse!=null) {
                analyseNMEA(lastResponse);
                lastResponse= m_GPSrxtx.getResponse();
            }
            if( (m_isLogging||m_isSearchingLog)
                &&
                ( (sentCmds.getCount()==0) // All acks or responses received.
                  ||
                  ((Vm.getTimeStamp()-logTimer)>=m_settings.getDownloadTimeOut())
                )
               ) {
                handleLogTimeOut();  // On time out resend request packet.
            }
        } else {
            MainWindow.getMainWindow().removeThread(this);
            //removeTimer(linkTimer);
            //linkTimer=null;
        }
    }

    /* (non-Javadoc)
     * @see waba.sys.Thread#started()
     */
    public void started() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see waba.sys.Thread#stopped()
     */
    public void stopped() {
        // TODO Auto-generated method stub

    }
}
