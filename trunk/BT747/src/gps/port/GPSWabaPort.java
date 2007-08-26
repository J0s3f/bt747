/*
 * Created on 25 août 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gps.port;

import waba.io.DataStream;
import waba.io.File;
import waba.ui.MessageBox;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPSWabaPort extends GPSPort {
    private waba.io.SerialPort sp=null;

    private boolean portIsOK=false;  

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
           sp=new waba.io.SerialPort(spPortNbr,spSpeed);
           result=sp.lastError;
           portIsOK= sp.isOpen();
           if(portIsOK) {
               // Read time out gives problems on windows: data is skipped!!!O
               sp.setReadTimeout(0);//small to read data in chunks and have good resp.
               //               sp.setReadTimeout(50);//small to read data in chunks and have good resp.
               //sp.setFlowControl(true);
               ds=new DataStream(sp);
           }
       }
       catch (Exception e) {
           new MessageBox("waba.io.SerialPort open","Unexpected exception catched").popupBlockingModal();
           ;//if(GPS_DEBUG) {waba.sys.Vm.debug("Exception when opening port\n");}; 
       }
       return result;
   }    

   
   /** Set a bluetooth connection
    * 
    *
    */
   public void setBlueTooth() {
       spPortNbr= waba.io.SerialPort.BLUETOOTH;
   }
   
   /** Set an USB connection
    * 
    *
    */
   public void setUSB() {
       spPortNbr= waba.io.SerialPort.USB;
   }
   
   /** getter to retrieve the last error report by the serial port driver.
    * 
    * @return last error from the waba.io.SerialPort driver
    */
   public int error() {
       return sp.lastError;
   }

   
   public void write(final String s) {
       byte[] b=s.getBytes();
       int l=b.length;
       sp.writeBytes(b,0,l);
       if(GPS_FILE_LOG&&(m_debugFile!=null)) {
           m_debugFile.writeBytes(b,0,l);
       }
   }
   
   public int readCheck() {
       if(sp!=null) {
           return sp.readCheck();
       } else {
           return -1;
       }
   }
   
   public int readBytes(byte[]b,int start, int max) {
       return sp.readBytes(b, start, max);
   }
}
