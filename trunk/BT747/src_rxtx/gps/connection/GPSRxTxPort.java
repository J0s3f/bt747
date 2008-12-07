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
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
package gps.connection;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bt747.sys.Convert;
import bt747.sys.Generic;

/**
 * This class implements the serial port for rxtx (on Linux)
 * 
 * @author Mario De Weerd
 */
public final class GPSRxTxPort extends GPSPort {
    private SerialPort sp = null;

    private OutputStream ds;
    private InputStream in;
    private String portPrefix = "";
    private boolean hasPortNbr = true;
    public static final String os_name = java.lang.System
            .getProperty("os.name");

    /**
     * 
     */
    public GPSRxTxPort() {
        super();

        if (os_name.startsWith("Windows")) {
            portPrefix = "COM";
        } else if (os_name.startsWith("Linux")) {
            portPrefix = "/dev/ttyUSB";
        } else if (os_name.startsWith("Mac")) {
            setUSB();
            setBlueTooth();
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
        return (sp != null);
    }

    /**
     * Close the connection.
     * 
     * 
     */
    public void closePort() {
        if (sp != null) {
            // ds.close();
            try {
                sp.close();
                ds = null;
                sp = null;
            } catch (Exception e) {
                Generic.debug("",e);
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
        if (freeTextPort.length() == 0) {
            portStr = java.lang.System.getProperty("bt747_port", portPrefix
                    + (hasPortNbr ? Convert.toString(spPortNbr) : ""));
        } else {
            portStr = freeTextPort;
        }

        closePort();
        
        try {
            if (Generic.isDebug()) {
                Generic.debug("Info: trying to open " + portStr,null);
            }
            CommPortIdentifier portIdentifier;
            portIdentifier = CommPortIdentifier.getPortIdentifier(portStr);
            if (portIdentifier.isCurrentlyOwned()) {
                Generic.debug("Error: Port " + portStr
                        + "is currently in use",null);
            } else {
                CommPort commPort = portIdentifier.open(getClass().getName(),
                        2000);
                if (commPort instanceof SerialPort) {
                    SerialPort serialPort = (SerialPort) commPort;
                    sp = serialPort;
                    serialPort.setSerialPortParams(getSpeed(), 8, 1, 0);
                    in = sp.getInputStream();
                    ds = sp.getOutputStream();
                    result = 0;
                } else {
                    sp = null;
                    in = null;
                    ds = null;
                    System.out.println("Error: Only serial ports are handled.");
                }
            }
        } catch (NoSuchPortException e) {
            Generic.debug("",e);
        } catch (PortInUseException e) {
            Generic.debug("",e);
        } catch (UnsupportedCommOperationException e) {
            Generic.debug("",e);
        } catch (IOException e) {
            Generic.debug("",e);
        }

        return result;
    }

    /**
     * Set a bluetooth connection
     * 
     * 
     */
    public void setBlueTooth() {
        super.setBlueTooth();
        spPortNbr = 0;
        boolean portFound = false;
        if (os_name.toLowerCase().startsWith("mac")) {
            for (int i = 0; !portFound && (i < 3); i++) {
                // /dev/tty.HOLUX_M-241-SPPSlave-1
                freeTextPort = "/dev/tty.HOLUX_M-241-SSPSlave-" + i;
                portFound = (new File(freeTextPort)).exists();
            }
            for (int i = 0; !portFound && (i < 3); i++) {
                freeTextPort = "/dev/tty.iBT-GPS-SPPSlave-" + i;
                portFound = (new File(freeTextPort)).exists();
            }
            for (int i = 0; !portFound && (i < 3); i++) {
                freeTextPort = "/dev/tty.iBT-GPS-SPPslave-" + i;
                portFound = (new File(freeTextPort)).exists();
            }
        }
        if (!portFound) {
            freeTextPort = "";
        }
    }

    /**
     * Set an USB connection
     * 
     * 
     */
    public void setUSB() {
        super.setUSB();
        // POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/cu.SLAB_USBtoUART"
        // POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.HOLUX_M-241-SPPSlave-1"
        // POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.HOLUX_M-241-SPPSlave-0"
        // POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.iBT-GPS-SPPSlave-1"
        // POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/cu.serial-0001"
        // POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.iBT-GPS-SPPslave-1"
        boolean portFound = false;
        if (!portFound) {
            freeTextPort = "/dev/cu.serial-0001";
            portFound = (new File(freeTextPort)).exists();
        }
        if (!portFound) {
            freeTextPort = "/dev/cu.SLAB_USBtoUART";
            portFound = (new File(freeTextPort)).exists();
        }
        if (!portFound && os_name.toLowerCase().startsWith("lin")) {
            for (int i = 0; !portFound && (i < 6); i++) {
                freeTextPort = "/dev/ttyUSB" + i;
                portFound = (new File(freeTextPort)).canRead();
            }
            for (int i = 0; !portFound && (i < 6); i++) {
                freeTextPort = "/dev/ttyACM" + i;
                portFound = (new File(freeTextPort)).canRead();
            }
        }
        if (!portFound) {
            freeTextPort = "";
        }
    }

    public void setFreeTextPort(String s) {
        super.setFreeTextPort(s);
    }

    public String getFreeTextPort() {
        return freeTextPort;
    }

    /**
     * getter to retrieve the last error report by the serial port driver.
     * 
     * @return last error from the SerialPort driver
     */
    public int error() {
        return 0;
    }

    public void write(final byte[] b) {
        try {
            ds.write(b);
        } catch (Exception e) {
            Generic.debug("",e);
        }
        if (GPS_FILE_LOG && (debugFile != null)) {
            try {
                debugFile.writeBytes("\nWrite:".getBytes(), 0, 2);
                debugFile.writeBytes(b, 0, b.length);
            } catch (Exception e) {
                Generic.debug("",e);
            }
        }
    }

    public void write(final String s) {
        write(s.getBytes());
    }

    public int readCheck() {
        if (sp != null) {
            try {
                // System.err.println("Available: "+in.available());
                // return 100;
                return in.available();// getInputStream().available();
            } catch (Exception e) {
                Generic.debug("",e);
                return 0;
            }
        } else {
            return 0;
        }
    }

    public int readBytes(byte[] b, int start, int max) {
        try {
            return in.read(b, start, max);
        } catch (Exception e) {
            Generic.debug("",e);
            return 0;
        }
    }
}
