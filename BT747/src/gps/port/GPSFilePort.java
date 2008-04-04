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

// This is a trial to access the device port through input streams.
// For the moment unsuccessfull.
package gps.port;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import bt747.sys.Convert;

/**
 * This class implements the serial port by accessing the file as a port.
 * 
 * @author Mario De Weerd
 */
public class GPSFilePort extends GPSPort {
    private RandomAccessFile portfile = null;
    private InputStream spIn = null;
    private OutputStream spOut = null;

    private String portPrefix = "";
    private boolean hasPortNbr = true;
    public static final String os_name = java.lang.System
            .getProperty("os.name");

    /**
     * 
     */
    public GPSFilePort() {
        // TODO Auto-generated constructor stub
        super();

        if (os_name.startsWith("Windows")) {
            portPrefix = "COM";
        } else if (os_name.startsWith("Linux")) {
            portPrefix = "/dev/ttyUSB";
        } else if (os_name.startsWith("Mac")) {
            // portPrefix="/dev/tty.iBt-GPS-SPPslave-";
            portPrefix = "/dev/cu.SLAB_USBtoUART";
            hasPortNbr = false;
        }
        portPrefix = java.lang.System.getProperty("bt747_prefix", portPrefix);
    }

    /**
     * Indicates if the device is connected or not.
     * 
     * @return <code>true</code> if the device is connected.
     */
    public boolean isConnected() {
        return myIsConnected();
    }

    private boolean myIsConnected() {
        return (portfile != null);
    }

    /**
     * Close the connection.
     * 
     * 
     */
    public void closePort() {
        if (myIsConnected()) {
            // ds.close();
            try {
                portfile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Open a connection
     * 
     * @return status result of the opening of the serial port.
     */
    public int openPort() {
        int result = -1;
        String portStr;
        portStr = java.lang.System.getProperty("bt747_port", portPrefix
                + (hasPortNbr ? Convert.toString(spPortNbr) : ""));

        closePort();

        try {
            System.out.println("Info: trying to open " + portStr);
            portfile = new RandomAccessFile(portStr, "rw");
            // if(portfile.canRead()) {
            // spIn = new FileInputStream(portfile);
            // br=new InputStreamReader(spIn);
            // }
            // if(portfile.canWrite()) {
            // // spOut = new FileOutputStream(portfile);
            // }
            // if(spIn==null) { // || spOut==null) {
            // result=-1;
            // spIn=null;
            // spOut=null;
            // portfile=null;
            // }
            // spOut = new FileOutputStream(portStr);
            result = 0;
        } catch (IOException e) {
            e.printStackTrace();
            spIn = null;
            spOut = null;
            portfile = null;
        }

        return result;
    }

    /**
     * Set a bluetooth connection
     * 
     * 
     */
    public void setBlueTooth() {
        spPortNbr = 0;
    }

    /**
     * Set an USB connection
     * 
     * 
     */
    public void setUSB() {
        spPortNbr = 0;
    }

    /**
     * getter to retrieve the last error report by the serial port driver.
     * 
     * @return last error from the SerialPort driver
     */
    public int error() {
        return 0;
    }

    public void write(final String s) {
        byte[] b = s.getBytes();
        int l = b.length;
        try {
            if (spOut != null) {
                spOut.write(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (GPS_FILE_LOG && (m_debugFile != null)) {
                m_debugFile.writeBytes(b, 0, l);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public int readCheck() {
        if (myIsConnected()) {
            try {
                // return br.ready()?1:0;
                return (int) 4096;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        } else {
            return 0;
        }
    }

    public int readBytes(byte[] b, int start, int max) {
        try {
            return portfile.read(b, start, max);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
