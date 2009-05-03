// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
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
import java.util.Enumeration;

import bt747.Version;
import bt747.sys.Generic;

/**
 * This class implements the serial port for rxtx (on Linux).
 * 
 * @author Mario De Weerd
 */
public final class GPSRxTxPort extends GPSPort {
    private SerialPort sp = null;

    private OutputStream ds = null;
    private InputStream in = null;
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
    public final synchronized boolean isConnected() {
        return (ds != null);
    }

    /**
     * Close the connection.
     * 
     * 
     */
    public final synchronized void closePort() {
        if (ds != null) {
            try {
                in.close();
                in = null;
            } catch (final Exception e) {
                Generic.debug("", e);
            }
            try {
                ds.close();
                ds = null;
            } catch (final Exception e) {
                Generic.debug("", e);
            }
            try {
                if (sp != null) {
                    sp.close();
                    sp = null;
                }
            } catch (final Exception e) {
                Generic.debug("", e);
            }
        }
    }

    /**
     * Open a connection.
     * 
     * @return status result of the opening of the serial port.
     */
    public final synchronized int openPort() {
        int result = -1;
        String portStr;
        if (freeTextPort.length() == 0) {
            portStr = java.lang.System.getProperty("bt747_port", portPrefix
                    + (hasPortNbr ? spPortNbr : ""));
        } else {
            portStr = freeTextPort;
        }

        closePort();

        try {
            if (Generic.isDebug()) {
                Generic.debug("Info: trying to open '" + portStr + "'", null);
            }
            CommPortIdentifier portIdentifier;
            portIdentifier = CommPortIdentifier.getPortIdentifier(portStr);
            if (portIdentifier.isCurrentlyOwned()) {
                Generic.debug("Error: Port " + portStr
                        + "is currently in use", null);
            } else {
                final CommPort commPort = portIdentifier.open(
                        "BT747 " + Version.VERSION_NUMBER + " "
                                + getClass().getName(), 2000);
                if (commPort instanceof SerialPort) {
                    final SerialPort serialPort = (SerialPort) commPort;
                    sp = serialPort;
                    serialPort.setSerialPortParams(getSpeed(), 8, 1, 0);
                    in = sp.getInputStream();
                    ds = sp.getOutputStream();
                    result = 0;
                } else {
                    sp = null;
                    in = null;
                    ds = null;
                    System.out
                            .println("Error: Only serial ports are handled.");
                }
            }
        } catch (final NoSuchPortException e) {
            Generic.debug("", e);
            Generic.debug("\nListing known ports:");
            try {
                final Enumeration<?> list = CommPortIdentifier
                        .getPortIdentifiers();
                while (list.hasMoreElements()) {
                    final CommPortIdentifier iden = (CommPortIdentifier) list
                            .nextElement();
                    String type;
                    switch (iden.getPortType()) {
                    case CommPortIdentifier.PORT_SERIAL: // rs232 Port
                        type = "SER  :";
                        break;
                    case CommPortIdentifier.PORT_PARALLEL: // Parallel Port
                        type = "PAR  :";
                        break;
                    case CommPortIdentifier.PORT_I2C: // i2c Port
                        type = "I2C  :";
                        break;
                    case CommPortIdentifier.PORT_RS485: // rs485 Port
                        type = "RS485:";
                        break;
                    case CommPortIdentifier.PORT_RAW: // Raw
                        // Port
                        type = "RAW  :";
                        break;
                    default:
                        type = "UNK  :";
                    }
                    Generic.debug(type + iden.getName());
                }
            } catch (Exception exp) {
                // Do not care.

            }
        } catch (final PortInUseException e) {
            Generic.debug("", e);
        } catch (final UnsupportedCommOperationException e) {
            Generic.debug("", e);
        } catch (final IOException e) {
            Generic.debug("", e);
        }

        return result;
    }

    private final boolean isValidPort(final String path) {
        try {
            return (new File(path)).exists()
                    && (CommPortIdentifier.getPortIdentifier(path)
                            .getPortType() == CommPortIdentifier.PORT_SERIAL);
        } catch (final Exception e) {
            return false;
        }

    }

    /** Maximum index when looking for BT port number given a base name. */
    private static final int MAX_BTPORT_SEARCH_IDX = 3;
    /** Maximum index when looking for USB port number given a base name. */
    private static final int MAX_USBPORT_SEARCH_IDX = 6;
    /**
     * Set a bluetooth connection.
     */
    public final synchronized void setBlueTooth() {
        super.setBlueTooth();
        spPortNbr = 0;
        boolean portFound = false;
        if (os_name.toLowerCase().startsWith("mac")) {
            for (int i = 0; !portFound && (i < MAX_BTPORT_SEARCH_IDX); i++) {
                // /dev/tty.HOLUX_M-241-SPPSlave-1
                freeTextPort = "/dev/tty.HOLUX_M-241-SPPSlave-" + i;
                portFound = isValidPort(freeTextPort);
            }
            for (int i = 0; !portFound && (i < MAX_BTPORT_SEARCH_IDX); i++) {
                // /dev/tty.HOLUX_M-241-SPPSlave-1
                freeTextPort = "/dev/tty.HoluxM-1000C-SPPslave-" + i;
                portFound = isValidPort(freeTextPort);
            }
            for (int i = 0; !portFound && (i < MAX_BTPORT_SEARCH_IDX); i++) {
                freeTextPort = "/dev/tty.iBT-GPS-SPPSlave-" + i;
                portFound = isValidPort(freeTextPort);
            }
            for (int i = 0; !portFound && (i < MAX_BTPORT_SEARCH_IDX); i++) {
                freeTextPort = "/dev/tty.iBT-GPS-SPPslave-" + i;
                portFound = isValidPort(freeTextPort);
            }
            for (int i = 0; !portFound && (i < MAX_BTPORT_SEARCH_IDX); i++) {
                freeTextPort = "/dev/cu.QstarzGPS-SPPslave-" + i;
                portFound = isValidPort(freeTextPort);
            }
            for (int i = 0; !portFound && (i < MAX_BTPORT_SEARCH_IDX); i++) {
                freeTextPort = "/dev/tty.QstarzGPS-SPPslave-" + i;
                portFound = isValidPort(freeTextPort);
            }
        }
        if (!portFound) {
            freeTextPort = "";
        }
    }

    /**
     * Set an USB connection.
     */
    public final synchronized void setUSB() {
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
            portFound = isValidPort(freeTextPort);
        }
        if (!portFound) {
            freeTextPort = "/dev/cu.SLAB_USBtoUART";
            portFound = isValidPort(freeTextPort);
        }
        if (!portFound) {
            freeTextPort = "/dev/cu.usbmodem1b10";
            portFound = isValidPort(freeTextPort);
        }
        if (!portFound) {
            freeTextPort = "/dev/cu.usbmodem1d10";
            portFound = isValidPort(freeTextPort);
        }
        if (!portFound && os_name.toLowerCase().startsWith("lin")) {
            for (int i = 0; !portFound && (i < MAX_USBPORT_SEARCH_IDX); i++) {
                freeTextPort = "/dev/ttyUSB" + i;
                portFound = isValidPort(freeTextPort);
            }
            for (int i = 0; !portFound && (i < MAX_USBPORT_SEARCH_IDX); i++) {
                freeTextPort = "/dev/ttyACM" + i;
                portFound = isValidPort(freeTextPort);
            }
        }
        if (!portFound) {
            freeTextPort = "";
        }
    }

    public final void setFreeTextPort(final String s) {
        super.setFreeTextPort(s);
    }

    public final synchronized String getFreeTextPort() {
        return freeTextPort;
    }

    /**
     * getter to retrieve the last error report by the serial port driver.
     * 
     * @return last error from the SerialPort driver
     */
    public final int error() {
        return 0;
    }

    public final synchronized void write(final byte[] b) {
        try {
            ds.write(b);
        } catch (final Exception e) {
            Generic.debug("", e);
        }
        if (GPS_FILE_LOG && (debugFile != null)) {
            try {
                debugFile.writeBytes("\nWrite:".getBytes(), 0, 2);
                debugFile.writeBytes(b, 0, b.length);
            } catch (final Exception e) {
                Generic.debug("", e);
            }
        }
    }

    public final void write(final String s) {
        write(s.getBytes());
    }

    public final synchronized int readCheck() {
        if (in != null) {
            try {
                // System.err.println("Available: "+in.available());
                // return 100;
                return in.available();
            } catch (final Exception e) {
                Generic.debug("", e);
                return 0;
            }
        } else {
            return 0;
        }
    }

    public final synchronized int readBytes(final byte[] b, final int start,
            final int max) {
        try {
            return in.read(b, start, max);
        } catch (final Exception e) {
            Generic.debug("", e);
            return 0;
        }
    }
}
