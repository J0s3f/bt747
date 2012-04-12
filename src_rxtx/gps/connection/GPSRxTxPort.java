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
public final class GPSRxTxPort extends gps.connection.GPSPort {
	private CommPort sp = null;

	private OutputStream ds = null;
	private InputStream in = null;
	private String portPrefix = "";
	public static final String os_name = java.lang.System
			.getProperty("os.name");
	public static boolean dubiousTried = false;

	/**
     * 
     */
	public GPSRxTxPort() {
		super();

		portType = PORT_NUMBER;
		if (os_name.startsWith("Windows")) {
			portPrefix = "COM";
			dubiousTried = true; // Dubious not needed on Win
		} else if (os_name.startsWith("Linux")) {
			portPrefix = "/dev/ttyUSB";
		} else if (os_name.startsWith("Mac")) {
			dubiousTried = true; // Dubious not needed on Mac
			setUSB();
			if (getFreeTextPort().length() == 0) {
				setBlueTooth();
			}
			// portPrefix="/dev/tty.iBt-GPS-SPPslave-";
			portPrefix = "/dev/cu.SLAB_USBtoUART";
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

	private final void openDubiousPortAsWorkaroundOnLinux() {
		if (!dubiousTried) {
			dubiousTried = true;
			try {
				CommPortIdentifier portIdentifier;
				portIdentifier = CommPortIdentifier
						.getPortIdentifier("/dev/dubiousport");

				final CommPort commPort = portIdentifier.open("BT747 "
						+ Version.VERSION_NUMBER + " " + getClass().getName(),
						2000);
				commPort.close();
			} catch (Throwable e) {
				// Do nothing, this is expected.
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

		closePort();

		switch (portType) {
		case PORT_USB:
			portStr = getUsbPort();
			break;
		case PORT_BT:
			portStr = getBluetoothPort();
			break;
		default:
		case PORT_NUMBER:
			portStr = java.lang.System.getProperty("bt747_port",
					(portType == PORT_NUMBER) ? portPrefix + spPortNbr : "");
			break;
		case PORT_PATH:
			portStr = getFreeTextPort();
			break;
		}

		openDubiousPortAsWorkaroundOnLinux();

		try {
			if (Generic.isDebug()) {
				Generic.debug("Info: trying to open '" + portStr + "'", null);
			}
			CommPortIdentifier portIdentifier;
			portIdentifier = CommPortIdentifier.getPortIdentifier(portStr);

			final CommPort commPort = portIdentifier
					.open("BT747 " + Version.VERSION_NUMBER + " "
							+ getClass().getName(), 2000);
			if (Generic.isDebug()) {
				Generic.debug("Info: Opened port, setting parameters", null);
			}
			if (commPort instanceof SerialPort) {
				final SerialPort serialPort = (SerialPort) commPort;
				sp = serialPort;
				in = sp.getInputStream();
				ds = sp.getOutputStream();
				final int baud = getSpeed();
				final int databits = SerialPort.DATABITS_8;
				int stopbits = SerialPort.STOPBITS_1;
				final int parity = SerialPort.PARITY_NONE;
				boolean parametersSet = false;
				try {
					serialPort.setSerialPortParams(baud, databits, stopbits,
							parity);
					parametersSet = true;
				} catch (Throwable e) {
					Generic.debug("Issue when setting parameters:" + baud + " "
							+ databits + " " + stopbits + " " + parity, e);
				}
				if (!parametersSet) {
					stopbits = SerialPort.STOPBITS_2;
					Generic.debug("Trying stopbits = " + stopbits, null);
					try {
						serialPort.setSerialPortParams(baud, databits,
								stopbits, parity);
						parametersSet = true;
					} catch (Throwable e) {
						Generic.debug("Issue when setting parameters:" + baud
								+ " " + databits + " " + stopbits + " "
								+ parity, e);
					}
				}
				result = 0;
			} else {
				sp = null;
				in = null;
				ds = null;
				System.out.println("Error: Only serial ports are handled.");
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
			Generic.debug("Error: Port " + portStr + "is currently in use", e);
			// } catch (final UnsupportedCommOperationException e) {
			// Generic.debug("", e);
		} catch (final IOException e) {
			Generic.debug("", e);
		}

		return result;
	}

	private final boolean isValidPort(final String path) {
		boolean result = false;
		int portType = -1;
		try {
			result = (new File(path)).exists()
					&& ((portType = CommPortIdentifier.getPortIdentifier(path)
							.getPortType()) == CommPortIdentifier.PORT_SERIAL);
		} catch (final Exception e) {
			// return false;
		}
		if (Generic.isDebug()) {
			Generic.debug("isValidPort('" + path + "')?: " + result + " "
					+ portType);
		}
		return result;
	}

	/** Maximum index when looking for BT port number given a base name. */
	private static final int MAX_BTPORT_SEARCH_IDX = 3;
	/** Maximum index when looking for USB port number given a base name. */
	private static final int MAX_USBPORT_SEARCH_IDX = 6;

	/**
	 * List of valid bluetooth port prefixes for the mac.
	 */
	private static final String[] macPortPrefixes = {
			"/dev/tty.HOLUX_M-241-SPPSlave", // Port for Holux M-241
			"/dev/tty.HoluxM-1000C-SPPslave", // Port for Holux M1000C
			"/dev/tty.HOLUX_M-1200E-SPPslave", // Port for
			"/dev/tty.iBT-GPS-SPPSlave", // Port for
			"/dev/tty.iBT-GPS-SPPslave", // Port for
			"/dev/cu.QstarzGPS-SPPslave", // Port for some Qstartz devices
			"/dev/tty.QstarzGPS-SPPslave", // Port for some Qstartz devices
			"/dev/tty.Qstarz1000XT-SPPslave", // Port for some Qstartz devices
			"/dev/tty.BlumaxBT-GPS-SPPSlave", // Port for Blumax device
	};

	private static final String[] possibleUsbPorts = { "/dev/cu.usbmodemfd110",
			"/dev/tty.usbmodemfd110", "/dev/tty.usbmodemfd510","/dev/tty.usbserial",
	};

	/**
	 * Set a bluetooth connection.
	 */
	public final synchronized void setBlueTooth() {
		super.setBlueTooth();
		portType = PORT_BT;
	}

	private final synchronized String getBluetoothPort() {
		String portName = null;

		if (os_name.toLowerCase().startsWith("mac")) {
			portName = getPortFromPrefixes(macPortPrefixes,
					MAX_BTPORT_SEARCH_IDX, "-");
		}

		return portName;
	}

	/**
	 * Goes through a list of port prefixes for indexes from 0 up to maxIdx.
	 * 
	 * @param prefixes
	 *            list of prefixes for ports that will be appended with an
	 *            index.
	 * @param maxIdx
	 *            maximum appended index for ports.
	 *            By setting this index to '-1' and 'extra' to "" only the basename
	 *            in the prefixes list is checked.
	 * @return port if a valid port was found and set, otherwise null
	 */
	private final synchronized String getPortFromPrefixes(
			final String[] prefixes, final int maxIdx, final String extra) {
		String portName = null;
		for (int prefixIdx = 0; portName == null && prefixIdx < prefixes.length; prefixIdx++) {
			final String baseNamePort = prefixes[prefixIdx];
			if (extra != null) {
				final String testPort = baseNamePort;
				if (isValidPort(testPort)) {
					portName = testPort;
				}
			}
			for (int i = 0; portName == null && (i < maxIdx); i++) {
				final String testPort = baseNamePort
						+ (extra != null ? extra : "") + i;
				if (isValidPort(testPort)) {
					portName = testPort;
				}
			}
		}
		return portName;
	}

	private static final String[] linUsbPortPrefixes = { //
	"/dev/ttyUSB", // Prefix on linux for USB ports
			"/dev/ttyACM" // Prefix on linux for newer USB ports of GPS
	};

	public final void setUSB() {
		portType = PORT_USB;
	}

	/**
	 * Set an USB connection.
	 */
	public final synchronized String getUsbPort() {
		// POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/cu.SLAB_USBtoUART"
		// POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.HOLUX_M-241-SPPSlave-1"
		// POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.HOLUX_M-241-SPPSlave-0"
		// POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.iBT-GPS-SPPSlave-1"
		// POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/cu.serial-0001"
		// POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.iBT-GPS-SPPslave-1"
		boolean portFound = false;
		String portName = null;
		if (!portFound && os_name.startsWith("Windows")) {
			String[] ports = SerialUtils.getSerialPorts();
			if (ports != null && ports.length > 0) {
				for (int i = 0; i < ports.length; i++) {
					portName = ports[i];
					portFound = true;
				}
			}
		}
		if (!portFound) {
			portName = "/dev/cu.serial-0001";
			portFound = isValidPort(portName);
		}
		if (!portFound) {
			portName = "/dev/cu.SLAB_USBtoUART";
			portFound = isValidPort(portName);
		}
		if (!portFound) {
			portName = "/dev/cu.iBTAGPS-SPPslave";
			portFound = isValidPort(portName);
		}

		if (!portFound && os_name.toLowerCase().startsWith("lin")) {
			portName = getPortFromPrefixes(linUsbPortPrefixes,
					MAX_USBPORT_SEARCH_IDX, null);
			portFound = portName != null;
		}
		if (!portFound) {
			portName = "/dev/cu.usbmodem1b10";
			portFound = isValidPort(portName);
		}
		if (!portFound) {
			portName = "/dev/cu.usbmodem1d10";
			portFound = isValidPort(portName);
		}
		if (!portFound) {
			portName = "/dev/cu.usbmodem3d10";
			portFound = isValidPort(portName);
		}
		if (!portFound) {
			portName = "/dev/cu.usbmodem3a20";
			portFound = isValidPort(portName);
		}
		if (!portFound) {
			portName = "/dev/cu.usbmodem620";
			portFound = isValidPort(portName);
		}
		if (!portFound) {
			portName = getPortFromPrefixes(possibleUsbPorts, -1, "");
			portFound = portName != null;
		}
		if (!portFound) {
			portName = null;
		}
		return portName;
	}

	public final synchronized void setFreeTextPort(final String s) {
		if (s != null && s.length() != 0) {
			portType = PORT_PATH;
		}
		super.setFreeTextPort(s);
	}

	public final synchronized String getFreeTextPort() {
		return super.getFreeTextPort();
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
			if (ds != null) {
				ds.write(b);
			}
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
		if (isConnected() && in != null) {
			try {
				// System.err.println("Available: "+in.available());
				// return 100;
				return in.available();
			} catch (IOException e) {
				Generic.debug("readCheck", e);
				closePort();
				return 0;
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
