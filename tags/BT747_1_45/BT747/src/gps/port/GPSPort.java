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

import waba.io.DataStream;
import bt747.io.File;

/** abstract class defining interface for serial port implementation.
 * Allows differentiation according to platform.
 * 
 * @author Mario De Weerd
 */
public abstract class GPSPort {
    protected DataStream ds=null;

    protected int spPortNbr;
    protected int spSpeed=115200;  // Does not really matter on most platforms
    
    public static final boolean GPS_FILE_LOG = true;//false;  // When true log communication to file for debug

    protected File m_debugFile=null;
    protected static final String C_DEBUG_FILE="/Palm/gpsRAW.txt";
    
    // Hooked reference to java os string h ere to avoid creating extra classes and
    // exceptions in compilation flow.
    public static String os_name="unknown";  

    public GPSPort() {}
    
    /** Indicates if the device is connected or not.
     * 
     * @return <code>true</code> if the device is connected.
     */
    public boolean isConnected() { return false; }

    /** Set and open a normal port (giving the port number)
     * 
     * @param port Port number of the port to open
     * @return result of opening the port, 0 if success.
     */
    public void setPort(int port) {
        spPortNbr= port;
    }
    
    public int openPort() {
        return -1;
    }
    public void closePort() { }
    
    public void setBlueTooth() {}
    public void setUSB() {}
    
    public final void setSpeed(int speed) {
        spSpeed= speed;
    }

    public final int getSpeed() {
        return spSpeed;
    }
    
    /** Get the port number (getter for spPortNbr)
     * 
     * @return Port number
     */
    public final int getPort() {
        return spPortNbr;
    }

    public void write(final String s) {}

    public void writeDebug(final String s) {
        byte[] b;
        int l;
        if(GPS_FILE_LOG&&(m_debugFile!=null)) {
            b=s.getBytes();
            l=b.length;
            try {
                m_debugFile.writeBytes(b,0,l);
            } catch (Exception e) {
            	//e.printStackTrace();
            }
        }
    }

    public final void writeDebug(final byte[] b, final int index, final int len) {
        if(debugActive()) {
            try {
                m_debugFile.writeBytes(b,index,len);
            } catch (Exception e) {
            	//e.printStackTrace();
            }
        }
    }

    public int readCheck() { return -1; }
    public int readBytes(byte[]b,int start, int max) { return -1; }
    public final boolean debugActive() {
        return GPS_FILE_LOG&&(m_debugFile!=null);
    }
}
