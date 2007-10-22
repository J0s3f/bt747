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
import waba.sys.Settings;
import waba.sys.Vm;
import waba.util.Vector;

import gps.port.GPSPort;
import gps.port.GPSWabaPort;

/** This class implements the low level driver of the GPS device.
 * It extracs NMEA strings.
 * The getResponse function should be called regurarly to get the GPS
 * device's response.
 * @author Mario De Weerd
 */
public class GPSrxtx {
    private static final boolean GPS_DEBUG = false; //!Settings.onDevice;
    
    private GPSPort gpsPort;
    
    private Semaphore m_writeOngoing = new Semaphore(1);
    
    
    /** Class constructor.
     */
    public  GPSrxtx() {
        gpsPort=new GPSWabaPort(); // TODO: select according to OS (done during compile currently).
        setDefaults();
    }
    
    /** Set the defaults for the device according to the given parameters.
     * @param port
     * @param speed
     */
    public void setDefaults(final int port, final int speed) {
        gpsPort.setPort(port);
        gpsPort.setSpeed(speed);
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
            gpsPort.setUSB();
        } else
            if (Platform.startsWith("PalmOS")) {
                gpsPort.setBlueTooth();
            } else{
                gpsPort.setPort(0);  // Should be bluetooth in WinCE
            }			
    }
    
    public void setBluetoothAndOpen() {
        gpsPort.setBlueTooth();
        gpsPort.openPort();
    }

    public final void setUSBAndOpen() {
        gpsPort.setUSB();
        gpsPort.openPort();
    }
    
    public final void closePort() {
        gpsPort.closePort();
    }

    public final void openPort() {
        gpsPort.openPort();
    }

    
    /** Set and open a normal port (giving the port number)
     * 
     * @param port Port number of the port to open
     * @return result of opening the port, 0 if success.
     */
    public int setPortAndOpen(int port) {
        gpsPort.setPort(port);
        return gpsPort.openPort();
    }
    
    public int getPort() {
        return gpsPort.getPort();
    }
    public int getSpeed() {
        return gpsPort.getSpeed();
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
    private static final char[] EOL_BYTES = {'\015','\012'};
    
    StringBuffer rec= new StringBuffer(256);
    
    public final boolean isConnected() {
        return (gpsPort!=null)&&gpsPort.isConnected();
    }
    
    public void sendPacket(final String p_Packet) {       
        if(isConnected()) {
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

            rec.setLength(0);
            rec.append('$');
            rec.append(p_Packet);
            rec.append('*');
            rec.append(Convert.unsigned2hex(z_Checksum,2));
            rec.append(EOL_BYTES);
            gpsPort.write(rec.toString());
            
            m_writeOngoing.up();  // Semaphore - release link
        }
    }
    
    public String[] getResponse() {
        boolean continueReading;
        int myError= ERR_NOERROR;
        final boolean skipError=true;
        continueReading = gpsPort.isConnected();

        if(gpsPort.debugActive()) {
            // Test to avoid unnecessary lost time
            gpsPort.writeDebug("\r\n:"+Convert.toString(Vm.getTimeStamp())+":");
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
                if(isConnected()) {
                    try {
                        int max= gpsPort.readCheck();
                        if(max>C_BUF_SIZE) {
                            max=C_BUF_SIZE;
                        }
                        if(max>0) {
                            bytesRead= gpsPort.readBytes(read_buf,0,max);
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
                        if(gpsPort.debugActive()) {
                            String q="("+Convert.toString(Vm.getTimeStamp())+")";
                            gpsPort.writeDebug(q.getBytes(),0,q.length());
                            gpsPort.writeDebug(read_buf, 0, bytesRead);
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