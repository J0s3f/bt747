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
import waba.io.DataStream;
import waba.io.File;
import waba.io.SerialPort;
import waba.sys.Convert;
import waba.sys.Settings;
import waba.sys.Vm;
import waba.ui.Control;
import waba.ui.MessageBox;
import waba.util.Vector;

/** This class implements the low level driver of the GPS device.
 * It extracs NMEA strings.
 * The getResponse function should be called regurarly to get the GPS
 * device's response.
 * @author Mario De Weerd
 */
public class GPSrxtx {
    private static final boolean GPS_DEBUG = false; //!Settings.onDevice;
    private static final boolean GPS_FILE_LOG = false;  // When true log communication to file for debug
    private File m_debugFile=null;
    private final static String C_DEBUG_FILE="/Palm/gpsRAW.txt";
    
    protected int spPortNbr;
    protected int spSpeed=115200;  // Does not really matter on most platforms
    private SerialPort sp=null;
    private DataStream ds=null;
    
    boolean portIsOK=false;
    
    private Semaphore m_writeOngoing = new Semaphore(1);
    
    
    /** Class constructor.
     */
    public  GPSrxtx() {
        setDefaults();
    }
    
    /** Set the defaults for the device according to the given parameters.
     * @param port
     * @param speed
     */
    public void setDefaults(final int port, final int speed) {
        spPortNbr= port;
        spSpeed=   speed;
    }
    
    /** Set the defaults of the device according to preset, guessed values.
     */
    public void setDefaults() {
        // Settings.platform:
        // PalmOS, PalmOS/SDL, WindowsCE, PocketPC, MS_SmartPhone,
        // Win32, Symbian, Linux, Posix
        String Platform = Settings.platform;
        
        if ((Platform.equals("Java"))||
                (Platform.equals("Win32"))||
                (Platform.equals("Posix"))||
                (Platform.equals("Linux"))) {
            // Try USB Port
            spPortNbr= SerialPort.USB;
        } else
            if (Platform.startsWith("PalmOS")) {
                spPortNbr= SerialPort.BLUETOOTH;
            } else{
                spPortNbr= 0;  // Should be bluetooth in WinCE
            }			
    }
    
    /** Set and open a bluetooth connection
     * 
     *
     */
    public void setBluetooth() {
        spPortNbr= SerialPort.BLUETOOTH;
        openPort();
    }
    
    /** Set and open an USB connection
     * 
     *
     */
    public void setUsb() {
        spPortNbr= SerialPort.USB;
        openPort();
    }
    
    /** Set and open a normal port (giving the port number)
     * 
     * @param port Port number of the port to open
     * @return result of opening the port, 0 if success.
     */
    public int setPort(int port) {
        spPortNbr= port;
        return openPort();
    }
    
    /** Get the port number (getter for spPortNbr)
     * 
     * @return Port number
     */
    public int getPort() {
        return spPortNbr;
    }
    
    /** Indicates if the device is connected or not.
     * 
     * @return <code>true</code> if the device is connected.
     */
    public boolean isConnected() {
        return (sp!=null && sp.isOpen());
    }
    
    /** Close the connection.
     *
     *
     */
    public void closePort() {
        if (sp!= null && sp.isOpen()) {
            portIsOK= false;
            ds.close();
        } 
    }
    
    /** Open a connection
     * 
     * @return status result of the opening of the serial port.
     */
    public int openPort() {
        int result=-1;
        closePort();
        if(GPS_FILE_LOG&&(m_debugFile==null)) {
            try {
                new File(C_DEBUG_FILE).delete();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                // Having some trouble on Palm - doing it like this.
                new File(C_DEBUG_FILE,File.CREATE).close();
            } catch (Exception e) {
                // TODO: handle exception
            }
            m_debugFile=new File(C_DEBUG_FILE,File.READ_WRITE);
        }

        try {
            sp=new SerialPort(spPortNbr,spSpeed);
            result=sp.lastError;
            portIsOK= sp.isOpen();
            if(portIsOK) {
                // Read time out gives problems on windows: data is skipped!!!O
                sp.setReadTimeout(0);//small to read data in chunks and have good resp.
                //				sp.setReadTimeout(50);//small to read data in chunks and have good resp.
                //sp.setFlowControl(true);
                ds=new DataStream(sp);
            }
        }
        catch (Exception e) {
            new MessageBox("SerialPort open","Unexpected exception catched").popupBlockingModal();
            ;//if(GPS_DEBUG) {waba.sys.Vm.debug("Exception when opening port\n");}; 
        }
        return result;
    }
    
    /** getter to retrieve the last error report by the serial port driver.
     * 
     * @return last error from the SerialPort driver
     */
    public int error() {
        return sp.lastError;
    }
    
    
    private static final int C_INITIAL_STATE = 0;
    private static final int C_START_STATE = 1;
    private static final int C_FIELD_STATE = 2;
    private static final int C_STAR_STATE  = 3;
    private static final int C_CHECKSUM_CHAR1_STATE = 4;
    private static final int C_CHECKSUM_CHAR2_STATE = 5;
    private static final int C_EOL_STATE = 6;
    private static final int C_ERROR_STATE = 7;
    private static final int C_FOUND_STATE = 8;
    
    //The maximum length of each packet is restricted to 255 bytes (except for logger)	
    private static final int C_BUF_SIZE = 0x1100;
    private static final int C_CMDBUF_SIZE = 0x1100;
    
    private static int current_state = C_INITIAL_STATE;
    
    private byte[] read_buf  = new byte[C_BUF_SIZE];
    private char[] cmd_buf   = new char[C_CMDBUF_SIZE];
    
    private int read_buf_p= 0;
    private int cmd_buf_p = 0;
    private int bytesRead= 0;	
    private int checksum= 0;	
    private int read_checksum;
    
    
    static final int ERR_NOERROR= 0;	
    static final int ERR_CHECKSUM= 1;
    static final int ERR_INCOMPLETE= 2;
    static final int ERR_TOO_LONG= 3;
    
    private Vector vCmd = new Vector();
    private static final String[] Empty_vCmd = {};
    private static final byte[] EOL_BYTES = {'\015','\012'};
    
    public int sendPacket(final String p_Packet) {       
        if((ds!=null)&&(ds.isOpen())) {
            if(GPS_DEBUG) {
                waba.sys.Vm.debug(">"+p_Packet);
            }
            // Calculate checksum
            int z_Index= p_Packet.length();
            byte z_Checksum= 0;
            int z_Result=0;
            while(--z_Index>=0) {
                z_Checksum^=(byte)p_Packet.charAt(z_Index);
            }
            m_writeOngoing.down();  // Semaphore - reserve link
            z_Result+=ds.writeByte('$');
            z_Result+=ds.writeBytes(p_Packet.getBytes());
            z_Result+=ds.writeByte('*');
            z_Result+=ds.writeBytes(Convert.unsigned2hex(z_Checksum,2).getBytes());
            z_Result+=ds.writeBytes(EOL_BYTES);
            if(GPS_FILE_LOG&&(m_debugFile!=null)) {
                m_debugFile.writeBytes(">>>>".getBytes(), 0, 4);
                m_debugFile.writeBytes(p_Packet.getBytes(), 0, p_Packet.length());
                m_debugFile.writeBytes("*".getBytes(), 0, 1);
                m_debugFile.writeBytes(Convert.unsigned2hex(z_Checksum,2).getBytes(),0,2);
                m_debugFile.writeBytes(EOL_BYTES,0,2);
            }
            
            m_writeOngoing.up();  // Semaphore - release link
            return z_Result;
        } else {
            return -1;
        }
    }
    
    public String[] getResponse() {
        boolean continueReading;
        int myError= ERR_NOERROR;
        final boolean skipError=true;
        continueReading = sp.isOpen();
        try {
            if(GPS_FILE_LOG&&(m_debugFile!=null)) {
                String q="\r\n:"+Convert.toString(Vm.getTimeStamp())+":";
                m_debugFile.writeBytes(q.getBytes(),0,q.length());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        if(current_state==C_FOUND_STATE) {
            current_state=C_START_STATE;
        }
        
        while(continueReading) {
            
            while(continueReading && (read_buf_p < bytesRead)) {
                // Still bytes in read buffer to interpret
                char c;
                c= (char)read_buf[read_buf_p++];
                //                if((vCmd.getCount()!=0)&&((String[])vCmd.toObjectArray())[0].charAt(0)=='P') {
                //                    waba.sys.Vm.debug(Convert.toString(c));
                //                }
                switch (current_state) {
                case C_EOL_STATE:
                    // EOL found, record is ok.
                    //						StringBuffer sb = new StringBuffer(cmd_buf_p);
                    //						sb.setLength(0);
                    //						sb.append(cmd_buf,0,cmd_buf_p);
                    //						if(GPS_DEBUG) {waba.sys.Vm.debug(sb.toString()+"\n");}; 
                    if ( ( (c==10) || (c==13))) {
                        current_state = C_FOUND_STATE;
                        continueReading = false;
                    } else {
                        current_state = C_ERROR_STATE;											        
                    }
                    break;
                    
                case C_FOUND_STATE:
                case C_INITIAL_STATE:
                case C_START_STATE:
                    vCmd.removeAllElements();
                    if (c=='$') {
                        // First character of NMEA string found
                        current_state = C_FIELD_STATE;
                        cmd_buf_p=0;
                        
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
                        //                        if((vCmd.getCount()!=0)&&((String[])vCmd.toObjectArray())[0].charAt(0)=='P') {
                        //                            waba.sys.Vm.debug(((String[])vCmd.toObjectArray())[vCmd.getCount()-1]);
                        //                        }
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
                    vCmd.removeAllElements();
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
                if(sp.isOpen()) {
                    try {
                        int max= sp.readCheck();
                        if(max>C_BUF_SIZE) {
                            max=C_BUF_SIZE;
                        }
                        if(max>0) {
                            bytesRead= sp.readBytes(read_buf,0,max);
                            //						String sb=new String(read_buf,0,bytesRead);
                            //						System.out.println("RCVD:"+Convert.toString(bytesRead)+":"+sb+":");
                        }
                    }
                    catch (Exception e) {
                        // new MessageBox("Waiting","Exception").popupBlockingModal();
                        bytesRead= 0;
                    }
                    if(bytesRead==0) {
                        continueReading=false;
                    } else {
                        if(GPS_FILE_LOG&&(m_debugFile!=null)) {
                            String q="("+Convert.toString(Vm.getTimeStamp())+")";
                            m_debugFile.writeBytes(q.getBytes(),0,q.length());
                            m_debugFile.writeBytes(read_buf, 0, bytesRead);
                        }
                    }
                }
            }
        }		
        if (myError==C_ERROR_STATE) {
//            waba.sys.Vm.debug("Error on reception");
//
//            if(GPS_DEBUG&&(vCmd.getCount()!=0)) {
//                String s;
//                s="-";
//                waba.sys.Vm.debug(s);
//                for (int i = 0; i < vCmd.getCount(); i++) {
//                    waba.sys.Vm.debug(((String[])vCmd.toObjectArray())[i]);
//                };
//                //waba.sys.Vm.debug(s);
//            }        
            vCmd.removeAllElements();
        }
        //        if((vCmd.getCount()!=0)&&(Settings.platform.equals("Java"))) {
        //            String s=new String();
        //            s="<";
        //            waba.sys.Vm.debug("<");
        //            for (int i = 0; i < vCmd.getCount(); i++) {
        //                s+=((String[])vCmd.toObjectArray())[i];
        //            };
        //            waba.sys.Vm.debug(s);
        //        }        
        if(current_state==C_FOUND_STATE) {
//            if((vCmd.getCount()!=0)&&((String[])vCmd.toObjectArray())[0].charAt(0)=='P') {
////                return (String[])vCmd.toObjectArray();                
////            }
//                for (int i = 0; i < vCmd.getCount(); i++) {
//                    waba.sys.Vm.debug("Rec:"+Convert.toString( ((String[])vCmd.toObjectArray())[i].length()));
//                };
//            }

            return (String[])vCmd.toObjectArray();
        } else {
            return null;
        }
    }
    
}