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
package gps.port;

import waba.sys.Vm;

import bt747.sys.Convert;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;

/** This class implements the serial port for rxtx (on Linux)
 * @author Mario De Weerd
 */
public class GPSRxTxPort extends GPSPort {
    private SerialPort sp=null;

    private OutputStream ds;
    private String portPrefix="";
    private boolean hasPortNbr=true;
    public static String os_name=java.lang.System.getProperty("os.name");
    
    /**
     * 
     */
    public GPSRxTxPort() {
        // TODO Auto-generated constructor stub
        super();
        
        if(os_name.startsWith("Windows")) {
            portPrefix="COM";
        } else if(os_name.startsWith("Linux")) {
            portPrefix="/dev/ttyUSB";
        } else if(os_name.startsWith("Mac")) {
            //portPrefix="/dev/tty.iBt-GPS-SPPslave-";
            portPrefix="/dev/cu.SLAB_USBtoUART";
            hasPortNbr=false;
        }
        portPrefix=java.lang.System.getProperty("bt747_prefix",portPrefix);
     }

    /** Indicates if the device is connected or not.
     * 
     * @return <code>true</code> if the device is connected.
     */
    public boolean isConnected() {
        return (sp!=null);
    }
    
    /** Close the connection.
    *
    *
    */
   public void closePort() {
       if (sp!= null) {
           //ds.close();
         try {
               ds.close();
           } catch (Exception e) {
               e.printStackTrace();
           }
       } 
   }
   
   /** Open a connection
    * 
    * @return status result of the opening of the serial port.
    */
   public int openPort() {
       int result=-1;
       String portStr;
       portStr=java.lang.System.getProperty("bt747_port",
               portPrefix+(hasPortNbr?Convert.toString(spPortNbr):""));
       
       closePort();
       
       try {
           System.out.println("Info: trying to open "+portStr);
           CommPortIdentifier portIdentifier;
               portIdentifier = CommPortIdentifier.getPortIdentifier(portStr);
           if(portIdentifier.isCurrentlyOwned())
           {
               System.out.println("Error: Port is currently in use");
           } else
           {
               CommPort commPort = portIdentifier.open(getClass().getName(), 2000);
               if(commPort instanceof SerialPort)
               {
                   SerialPort serialPort = (SerialPort)commPort;
                   sp = serialPort;
                   serialPort.setSerialPortParams(0x1c200, 8, 1, 0);
                   ds = sp.getOutputStream();
                   result=0;
               } else
               {
                   sp=null;
                   ds=null;
                   System.out.println("Error: Only serial ports are handled by this example.");
               }
           }
           } catch (NoSuchPortException e) {
               e.printStackTrace();
           } catch (PortInUseException e) {
               e.printStackTrace();
           } catch (UnsupportedCommOperationException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }

       return result;
   }    

   
   /** Set a bluetooth connection
    * 
    *
    */
   public void setBlueTooth() {
       spPortNbr= 0;
   }
   
   /** Set an USB connection
    * 
    *
    */
   public void setUSB() {
       spPortNbr= 0;
   }
   
   /** getter to retrieve the last error report by the serial port driver.
    * 
    * @return last error from the SerialPort driver
    */
   public int error() {
       return 0;
   }

   
   public void write(final String s) {
       byte[] b=s.getBytes();
       int l=b.length;
       try {
           ds.write(b);
       } catch (Exception e) {
           e.printStackTrace();
       }
       if(GPS_FILE_LOG&&(m_debugFile!=null)) {
           m_debugFile.writeBytes(b,0,l);
       }
   }
   
   public int readCheck() {
       if(sp!=null) {
           try {
               return sp.getInputStream().available();//getInputStream().available();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
       } else {
           return 0;
       }
   }
   
   public int readBytes(byte[]b,int start, int max) {
       try {
           return sp.getInputStream().read(b, start, max);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
   }
}
