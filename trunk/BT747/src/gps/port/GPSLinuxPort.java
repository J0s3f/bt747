/*
 * Created on 25 août 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gps.port;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.Exception;

import waba.io.File;
import waba.sys.Convert;
import waba.sys.Settings;
import waba.sys.Vm;
import waba.util.Vector;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPSLinuxPort extends GPSPort {
    private SerialPort sp=null;

    private OutputStream ds;
    private boolean portIsOK=false;  

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
           portIsOK= false;
           //ds.close();
         try {
               ds.close();
           } catch (Exception e) {
               // TODO Auto-generated catch block
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
       closePort();
       try {
           CommPortIdentifier portIdentifier;
               portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB"+spPortNbr);
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
               } else
               {
                   System.out.println("Error: Only serial ports are handled by this example.");
               }
           }
           } catch (NoSuchPortException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           } catch (PortInUseException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           } catch (UnsupportedCommOperationException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }

       return result;
   }    

   
   /** Set a bluetooth connection
    * 
    *
    */
   public void setBluetooth() {
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
           // TODO: handle exception
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
            // TODO: handle exception
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
            // TODO: handle exception
            return 0;
        }
   }
}
