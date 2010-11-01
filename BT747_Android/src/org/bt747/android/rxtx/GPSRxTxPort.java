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
package org.bt747.android.rxtx;

import gps.connection.GPSPort;
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
	// private CommPort sp = null;

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

		// portType = PORT_NUMBER;
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
//				if (sp != null) {
//					sp.close();
//					sp = null;
//				}
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
		return result;
	}


	/**
	 * Set a bluetooth connection.
	 */
	public final synchronized void setBlueTooth() {
		super.setBlueTooth();
	}



	public final void setUSB() {
		// portType = PORT_USB;
	}

	/**
	 * Set an USB connection.
	 */
	public final synchronized String getUsbPort() {
		return  null;
	}

	public final synchronized void setFreeTextPort(final String s) {
		if (s != null && s.length() != 0) {
			// portType = PORT_PATH;
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
