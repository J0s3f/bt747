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

package gps.connection;

//import bt747.io.DataStream;
import bt747.generic.Generic;
import bt747.io.File;

/**
 * abstract class defining interface for serial port implementation. Allows
 * differentiation according to platform.
 * 
 * @author Mario De Weerd
 */
public abstract class GPSPort {
    // protected DataStream ds=null;

    protected int spPortNbr;
    protected int spSpeed = 115200; // Does not really matter on most platforms

    protected static final boolean GPS_FILE_LOG = true; // When true log
                                                        // communication to file
                                                        // for debug

    protected File m_debugFile = null;
    protected String debugFileName = "/Palm/gpsRAW.txt";

    // Hooked reference to java os string h ere to avoid creating extra classes
    // and
    // exceptions in compilation flow.
    public static String os_name = "unknown";

    public GPSPort() {
    }

    /**
     * Indicates if the device is connected or not.
     * 
     * @return <code>true</code> if the device is connected.
     */
    public boolean isConnected() {
        return false;
    }

    /**
     * Set and open a normal port (giving the port number)
     * 
     * @param port
     *            Port number of the port to open
     */
    public void setPort(int port) {
        spPortNbr = port;
        freeTextPort = "";
    }

    public int openPort() {
        return -1;
    }

    public void closePort() {
    }

    public void setBlueTooth() {
        freeTextPort = "";
    }

    public void setUSB() {
        freeTextPort = "";
    }

    protected String freeTextPort = "";

    public void setFreeTextPort(String s) {
        freeTextPort = s;
    }

    public String getFreeTextPort() {
        return freeTextPort;
    }

    public final void setSpeed(int speed) {
        spSpeed = speed;
    }

    public final int getSpeed() {
        return spSpeed;
    }

    /**
     * Get the port number (getter for spPortNbr)
     * 
     * @return Port number
     */
    public final int getPort() {
        return spPortNbr;
    }

    public abstract void write(final String s);

    public abstract void write(final byte[] b);

    public void writeDebug(final String s) {
        byte[] b;
        int l;
        if (GPS_FILE_LOG && (m_debugFile != null)) {
            b = s.getBytes();
            l = b.length;
            try {
                m_debugFile.writeBytes(b, 0, l);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    public final void writeDebug(final byte[] b, final int index, final int len) {
        if (debugActive()) {
            try {
                m_debugFile.writeBytes(b, index, len);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    public int readCheck() {
        return -1;
    }

    public int readBytes(byte[] b, int start, int max) {
        return -1;
    }

    public final boolean debugActive() {
        return GPS_FILE_LOG && (m_debugFile != null);
    }

    public final void startDebug() {
        if (GPS_FILE_LOG && (m_debugFile == null)) {
            try {
                new File(debugFileName).delete();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                // Having some trouble on Palm - doing it like this.
                File tmp = new File(debugFileName, File.CREATE);
                tmp.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                m_debugFile = new File(debugFileName, File.READ_WRITE);
            } catch (Exception e) {
                Generic.debug(debugFileName, e);
            }
        }
    }

    public final void endDebug() {
        if (m_debugFile != null) {
            try {
                m_debugFile.close();
            } catch (Exception e) {
                Generic.debug(debugFileName, e);
            }
            m_debugFile = null;
        }
    }

    /**
     * @return Returns the debugFileName.
     */
    public String getDebugFileName() {
        return debugFileName;
    }

    /**
     * @param debugFileName
     *            The debugFileName to set.
     */
    public final void setDebugFileName(final String debugFileName) {
        this.debugFileName = debugFileName;
    }
}
