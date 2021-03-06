// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
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

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Semaphore;

/**
 * This class implements the low level driver of the GPS device.<br>
 * The serial link reader is defined as a State. The State defines the protocol.<br>
 * The {@link #getResponse()} function should be called regularly to retrieve
 * and interpret the GPS device's response.
 * 
 * @author Mario De Weerd
 */
public final class GPSrxtx {
	private static GPSPort defaultGpsPort;

	private GPSPort gpsPort;

	/**
     * 
     */
	public GPSrxtx() {
		this(defaultGpsPort!=null?defaultGpsPort.clone():null);	
	}

	public GPSrxtx(final GPSPort port) {
		if (port == null) {
			Generic
					.debug("GPSrxtPort instance is null - review initialisation");
		}
		gpsPort = port;
		updatePortDebug();
		// Generic.setDebugLevel(1);
	}

	/** The decoding of the serial link is determined by state. */

	/** The current state */
	private DecoderStateInterface state = DecoderStateFactory
			.getInstance(DecoderStateFactory.NMEA_STATE);

	/** Called by state or other means to change state. */
	public final void newState(final int newState) {
		newState(DecoderStateFactory.getInstance(newState));
	}

	/**
	 * Enters a state that can be provided for externally.
	 * 
	 * @param newState
	 */
	public final void newState(final DecoderStateInterface newState) {
		if (!newState.equals(state)) {
			state.exitState(this);
			state = newState;
			state.enterState(this);
		}
	}

	/** Returns the current state which is part of the context. */
	public final Object getState() {
		return state;
	}

	/** Semaphore to avoid that two resources are writing to the link. */
	private final BT747Semaphore writeOngoing = JavaLibBridge
			.getSemaphoreInstance(1);

	/** Semaphore to avoid that two resources are reading the link. */
	private final BT747Semaphore getResponseOngoing = JavaLibBridge
			.getSemaphoreInstance(1);

	public static final void setDefaultGpsPortInstance(
			final GPSPort portInstance) {
		// Generic.debug("Setting GPS port");
		GPSrxtx.defaultGpsPort = portInstance;
	}

	public static final boolean hasDefaultPortInstance() {
		return GPSrxtx.defaultGpsPort != null;
	}

	public final GPSPort getGpsPort() {
		return gpsPort;
	}

	/**
	 * Set the defaults for the device according to the given parameters.
	 * 
	 * @param port
	 * @param speed
	 */
	public final void setDefaults(final int port, final int speed) {
		gpsPort.setPort(port);
		gpsPort.setSpeed(speed);
	}

	private final int myOpenPort() {
		if (gpsPort == null) {
			gpsPort = defaultGpsPort;
		}
		return gpsPort.openPort();
	}

	public final void setBluetoothAndOpen() {
		gpsPort.setBlueTooth();
		myOpenPort();
	}

	public final void setUSBAndOpen() {
		gpsPort.setUSB();
		myOpenPort();
	}

	public final void closePort() {
		gpsPort.closePort();
	}

	public final void openPort() {
		closePort();
		myOpenPort();
	}

	/**
	 * Set and open a normal port (giving the port number)
	 * 
	 * @param port
	 *            Port number of the port to open
	 * @return result of opening the port, 0 if success.
	 */
	public final int setPortAndOpen(final int port) {
		gpsPort.setPort(port);
		return myOpenPort();
	}

	public final int setFreeTextPortAndOpen(final String s) {
		int result;
		if (gpsPort != null) {
			Generic.debug("Class" + gpsPort.getClass().getName());
			gpsPort.setFreeTextPort(s);
			result = myOpenPort();
			Generic.debug("Port opened");
		} else {
			Generic.debug("Must set gpsPort handler");
			result = -1;
		}
		return result;
	}

	public final String getFreeTextPort() {
		return gpsPort.getFreeTextPort();
	}

	public final int getPort() {
		return gpsPort.getPort();
	}

	public final int getSpeed() {
		return gpsPort.getSpeed();
	}

	public final void setBaudRate(final int speed) {
		gpsPort.setSpeed(speed);
	}

	public static final int ERR_NOERROR = 0;
	public static final int ERR_CHECKSUM = 1;
	public static final int ERR_INCOMPLETE = 2;
	public static final int ERR_TOO_LONG = 3;

	public final boolean isConnected() {
		return (gpsPort != null) && gpsPort.isConnected();
	}

	public final void write(final String text) {
		writeOngoing.down(); // Semaphore - reserve link
		if (Generic.isDebug()) {
			final String debugText = ">" + text + "<";
			// + (int) text.charAt(text.length() - 1) + "|";
			// if (Generic.getDebugLevel() > 1) {
			// Generic.debug(debugText);
			// }
			gpsPort.writeDebug(debugText);
		}
		gpsPort.write(text);
		writeOngoing.up(); // Semaphore - release link
	}

	public final void write(final byte[] bytes) {
		writeOngoing.down(); // Semaphore - reserve link
		gpsPort.write(bytes);
		if (Generic.isDebug()) {
			final String debugText = ">" + bytes.length + " bytes sent";
			if (Generic.getDebugLevel() > 1) {
				Generic.debug(debugText);
				// StringBuffer hexStr = new StringBuffer(10);
				// for (int i = 0; i < bytes.length; i++) {
				// hexStr.append(Convert.unsigned2hex(bytes[i], 2));
				// }
				// Generic.debug(hexStr.toString());
			}
			gpsPort.writeDebug(debugText);
		}
		writeOngoing.up(); // Semaphore - release link
	}

	// Implemention to allow 'virtual debug' of protocol
	private StringBuffer virtualInput = null;

	public final void virtualReceive(final String rvd) {
		if (virtualInput == null) {
			virtualInput = new StringBuffer(rvd);
		} else {
			virtualInput.append(rvd);
		}
	}

	public final Object getResponse() {
		getResponseOngoing.down();
		buffer.resetReadStrategy();

		if (defaultGpsPort.debugActive()) {
			// Test to avoid unnecessary lost time
			defaultGpsPort.writeDebug("\r\nR:" + Generic.getTimeStamp() + ":");
		}

		final Object result = state.getResponse(this);
		getResponseOngoing.up();
		return result;
	}

	private String debugCon = null;
	private boolean isGpsDebug = false;

	public final void setDebugConn(final boolean gps_debug, final String s) {
		debugCon = s + "/gpsRawDebug.txt";
		isGpsDebug = gps_debug;
		updatePortDebug();
	}

	private final void updatePortDebug() {
		if (gpsPort != null) {
			gpsPort.setDebugFileName(debugCon);
			if (isGpsDebug) {
				gpsPort.startDebug();
			} else {
				gpsPort.endDebug();
			}
		}

	}

	public final boolean isDebugConn() {
		return isGpsDebug;
	}

	private final Buffer buffer = new Buffer();

	public final boolean isReadBufferEmpty() {
		return buffer.isReadBufferEmpty();
	}

	public final char getReadBufferChar() {
		return buffer.getReadBufferChar();
	}

	public final byte getReadBufferByte() {
		return buffer.getReadBufferByte();
	}

	/** Maintains the buffer logic. */
	private final class Buffer {
		private static final int C_BUF_SIZE = 0x1100;
		private final byte[] read_buf = new byte[Buffer.C_BUF_SIZE];

		private int read_buf_p = 0;
		private int bytesRead = 0;

		private boolean stableStrategy = false; // Some improvement on PDA
		// when
		// true.
		private int prevReadCheck = 0;

		private boolean readAgain;

		private boolean isReadAgain() {
			return readAgain;
		}

		protected void resetReadStrategy() {
			readAgain = true;
		}

		protected final boolean isReadBufferEmpty() {
			if (read_buf_p >= bytesRead) {
				// Buffer is empty. Try to fill it (empty the input queue)
				refillBuffer();
				if (bytesRead > 100 || isReadAgain()) {

					readAgain = false;
				}
			}
			return read_buf_p >= bytesRead;
		}

		protected final char getReadBufferChar() {
			return (char) read_buf[read_buf_p++];
		}

		protected final byte getReadBufferByte() {
			return (byte) read_buf[read_buf_p++];
		}

		/**
		 * @return true if bytes found.
		 */
		private final boolean refillBuffer() {
			boolean result = true;
			read_buf_p = 0;
			bytesRead = 0;
			if (isConnected()) { // && rxtxMode != DPL700_MODE) {
				if (virtualInput != null) {
					final byte[] ns = virtualInput.toString().getBytes();
					int l = ns.length;
					if (l > read_buf.length) {
						l = read_buf.length;
					}
					bytesRead = l;
					while (--l >= 0) {
						read_buf[l] = ns[l];
					}
					Generic.debug("Virtual:" + virtualInput.toString());
					virtualInput = null;
				} else {
					try {
						int max = gpsPort.readCheck();
						if (!stableStrategy || (prevReadCheck == max)
								|| (max > Buffer.C_BUF_SIZE)) {

							// gpsPort.writeDebug("\r\nC1:" + max + ":");
							if ((max > Buffer.C_BUF_SIZE)) {
								prevReadCheck = max - Buffer.C_BUF_SIZE;
								max = Buffer.C_BUF_SIZE;
							} else {
								prevReadCheck = 0;
							}

							// gpsPort.writeDebug("\r\nC2:" + max + ":");
							if (max > 0) {
								bytesRead = gpsPort.readBytes(read_buf, 0, max);
								// if (bytesRead != 0) {
								// String sb = new String(read_buf, 0,
								// bytesRead);
								// System.out.println("RCVD:"
								// + JavaLibBridge.toString(bytesRead)
								// + ":" + sb + ":");
								// }
							}
							// gpsPort.writeDebug("\r\nC3:" + bytesRead +
							// ":");
						} else {
							prevReadCheck = max;
						}
						// if(prevReadCheck!=0)
						// {System.out.println("prev:"+prevReadCheck+":");}
					} catch (final Exception e) {
						// new
						// MessageBox("Waiting","Exception").popupBlockingModal();
						bytesRead = 0;
					}
				}
				if (bytesRead == 0) {
					result = false;
				} else {
					if (gpsPort.debugActive()) {
						final String q = "(" + Generic.getTimeStamp() + ")";
						gpsPort.writeDebug(q.getBytes(), 0, q.length());
						gpsPort.writeDebug(read_buf, 0, bytesRead);
					}
				}
			}
			return result;
		}
	}
}
