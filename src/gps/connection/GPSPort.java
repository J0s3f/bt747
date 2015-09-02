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
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package gps.connection;

// import bt747.io.DataStream;
import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Path;

/**
 * abstract class defining interface for serial port implementation. Allows
 * differentiation according to platform.
 * 
 * @author Mario De Weerd
 */
public abstract class GPSPort {
	// protected DataStream ds=null;

	protected int spPortNbr;
	protected int spSpeed = 115200; // Does not really matter on most
	// platforms

	protected static final boolean GPS_FILE_LOG = true; // When true log
	// communication to
	// file
	// for debug

	protected File debugFile = null;
	protected BT747Path debugFileName = new BT747Path("/Palm/gpsRAW.txt");

	// Hooked reference to java os string h ere to avoid creating extra
	// classes
	// and
	// exceptions in compilation flow.
	public static String os_name = "unknown";

	protected final static int PORT_USB = 0;
	protected final static int PORT_BT = 1;
	protected final static int PORT_PATH = 2;
	protected final static int PORT_NUMBER = 3;
	protected int portType = PORT_USB;

	private final String BLUETOOTH_TEXT = "BLUETOOTH";
	private final String USB_TEXT = "USB";
	private final String PORTNBR_TEXT = "";

	public GPSPort() {
	}

	/** Returns new instance of same class. */
	public abstract GPSPort clone ();
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
	public void setPort(final int port) {
		spPortNbr = port;
		portType = PORT_NUMBER;
	}

	public int openPort() {
		return -1;
	}

	public void closePort() {
	}

	public void setBlueTooth() {
		portType = PORT_BT;
	}

	public void setUSB() {
		portType = PORT_USB;
	}

	private String freeTextPort = "";

	/**
	 * Set the "Free text port" - does not open it.
	 * 
	 * @param s
	 */
	public void setFreeTextPort(final String s) {
		freeTextPort = s;
	}

	public String getFreeTextPort() {
		switch (portType) {
		case PORT_USB:
			return USB_TEXT;
		case PORT_NUMBER:
			return PORTNBR_TEXT;
		case PORT_BT:
			return BLUETOOTH_TEXT;
		case PORT_PATH:
		default:
			return freeTextPort;
		}
	}

	public final void setSpeed(final int speed) {
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
		if (GPSPort.GPS_FILE_LOG && (debugFile != null)) {
			b = s.getBytes();
			l = b.length;
			try {
				debugFile.writeBytes(b, 0, l);
			} catch (final Exception e) {
				// e.printStackTrace();
			}
		}
	}

	public final void writeDebug(final byte[] b, final int index, final int len) {
		if (debugActive()) {
			try {
				// final byte[] cb = new byte[5];
				// cb[1] = '(';
				// cb[4] = ')';
				// for (int i = index; i < len; i++) {
				// final byte c = b[i];
				// final String s = JavaLibBridge.unsigned2hex(c, 2);
				// cb[0] = c;
				// cb[2] = (byte) s.charAt(0);
				// cb[3] = (byte) s.charAt(1);
				// debugFile.writeBytes(cb, 0, 5);
				// }
				debugFile.writeBytes(b, index, len);
			} catch (final Exception e) {
				// e.printStackTrace();
			}
		}
	}

	public int readCheck() {
		return -1;
	}

	public int readBytes(final byte[] b, final int start, final int max) {
		return -1;
	}

	public final boolean debugActive() {
		return GPSPort.GPS_FILE_LOG && (debugFile != null);
	}

	public final void startDebug() {
		if (GPSPort.GPS_FILE_LOG && (debugFile == null)) {
			try {
				new File(debugFileName).delete();
			} catch (final Exception e) {
				// TODO: handle exception
			}
			try {
				// Having some trouble on Palm - doing it like this.
				final File tmp = new File(debugFileName, File.CREATE);
				tmp.close();
			} catch (final Exception e) {
				// TODO: handle exception
			}
			try {
				debugFile = new File(debugFileName, File.READ_WRITE);
			} catch (final Exception e) {
				Generic.debug(debugFileName.toString(), e);
			}
		}
	}

	public final void endDebug() {
		if (debugFile != null) {
			try {
				debugFile.close();
			} catch (final Exception e) {
				Generic.debug(debugFileName.toString(), e);
			}
			debugFile = null;
		}
	}

	/**
	 * @return Returns the debugFileName.
	 */
	public BT747Path getDebugFileName() {
		return debugFileName;
	}

	/**
	 * @param debugFileName
	 *            The debugFileName to set.
	 */
	public final void setDebugFileName(final String debugFileName) {
		this.debugFileName = new BT747Path(debugFileName);
	}
}
