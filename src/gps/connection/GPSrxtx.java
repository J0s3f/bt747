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

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Semaphore;

/**
 * This class implements the low level driver of the GPS device.<br>
 * The serial link reader is defined as a State. The State defines the
 * protocol.<br>
 * The {@link #getResponse()} function should be called regularly to retrieve
 * and interpret the GPS device's response.
 * 
 * @author Mario De Weerd
 */
public final class GPSrxtx {
    private static GPSPort gpsPort;

    /** The decoding of the serial link is determined by state. */

    /** The current state */
    private DecoderStateInterface state = DecoderStateFactory
            .getInstance(DecoderStateFactory.NMEA_STATE);

    /** Called by state or other means to change state. */
    protected final void newState(final int newState) {
        state.exitState(this);
        state = DecoderStateFactory.getInstance(newState);
        state.enterState(this);
    }

    /** Returns the current state which is part of the context. */
    protected final Object getState() {
        return state;
    }

    /** Semaphore to avoid that two resources are writing to the link. */
    private final BT747Semaphore writeOngoing = JavaLibBridge
            .getSemaphoreInstance(1);

    /** Semaphore to avodi that two resources are reading the link. */
    private final BT747Semaphore getResponseOngoing = JavaLibBridge
            .getSemaphoreInstance(1);

    public static void setGpsPortInstance(final GPSPort portInstance) {
        GPSrxtx.gpsPort = portInstance;
    }

    protected GPSPort getGpsPortInstance() {
        return GPSrxtx.gpsPort;
    }

    /**
     * Set the defaults for the device according to the given parameters.
     * 
     * @param port
     * @param speed
     */
    public final void setDefaults(final int port, final int speed) {
        GPSrxtx.gpsPort.setPort(port);
        GPSrxtx.gpsPort.setSpeed(speed);
    }

    public final void setBluetoothAndOpen() {
        GPSrxtx.gpsPort.setBlueTooth();
        GPSrxtx.gpsPort.openPort();
    }

    public final void setUSBAndOpen() {
        GPSrxtx.gpsPort.setUSB();
        GPSrxtx.gpsPort.openPort();
    }

    public final void closePort() {
        GPSrxtx.gpsPort.closePort();
    }

    public final void openPort() {
        closePort();
        GPSrxtx.gpsPort.openPort();
    }

    /**
     * Set and open a normal port (giving the port number)
     * 
     * @param port
     *                Port number of the port to open
     * @return result of opening the port, 0 if success.
     */
    public final int setPortAndOpen(final int port) {
        GPSrxtx.gpsPort.setPort(port);
        return GPSrxtx.gpsPort.openPort();
    }

    public final int setFreeTextPortAndOpen(final String s) {
        GPSrxtx.gpsPort.setFreeTextPort(s);
        return GPSrxtx.gpsPort.openPort();
    }

    public final String getFreeTextPort() {
        return GPSrxtx.gpsPort.getFreeTextPort();
    }

    public final int getPort() {
        return GPSrxtx.gpsPort.getPort();
    }

    public final int getSpeed() {
        return GPSrxtx.gpsPort.getSpeed();
    }

    public final void setBaudRate(final int speed) {
        GPSrxtx.gpsPort.setSpeed(speed);
    }

    static final int ERR_NOERROR = 0;
    static final int ERR_CHECKSUM = 1;
    static final int ERR_INCOMPLETE = 2;
    static final int ERR_TOO_LONG = 3;

    public final boolean isConnected() {
        return (GPSrxtx.gpsPort != null) && GPSrxtx.gpsPort.isConnected();
    }

    public final void write(final String text) {
        writeOngoing.down(); // Semaphore - reserve link
        if (Generic.isDebug()) {
            final String debugText = ">" + text;
            if (Generic.getDebugLevel() > 1) {
                Generic.debug(debugText);
            }
            GPSrxtx.gpsPort.writeDebug(debugText);
        }
        GPSrxtx.gpsPort.write(text);
        writeOngoing.up(); // Semaphore - release link
    }

    public final void write(final byte[] bytes) {
        writeOngoing.down(); // Semaphore - reserve link
        GPSrxtx.gpsPort.write(bytes);
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
        final Object result = state.getResponse(this);
        getResponseOngoing.up();
        return result;
    }

    public final void setDebugConn(final boolean gps_debug, final String s) {
        GPSrxtx.gpsPort.setDebugFileName(s + "/gpsRawDebug.txt");
        if (gps_debug) {
            GPSrxtx.gpsPort.startDebug();
        } else {
            GPSrxtx.gpsPort.endDebug();
        }
    }

    public final boolean isDebugConn() {
        return GPSrxtx.gpsPort.debugActive();
    }

    private final Buffer buffer = new Buffer();

    protected final boolean isReadBufferEmpty() {
        return buffer.isReadBufferEmpty();
    }

    protected final char getReadBufferChar() {
        return buffer.getReadBufferChar();
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
                        int max = GPSrxtx.gpsPort.readCheck();
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
                                bytesRead = GPSrxtx.gpsPort.readBytes(
                                        read_buf, 0, max);
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
                    if (GPSrxtx.gpsPort.debugActive()) {
                        final String q = "(" + Generic.getTimeStamp() + ")";
                        GPSrxtx.gpsPort.writeDebug(q.getBytes(), 0, q
                                .length());
                        GPSrxtx.gpsPort.writeDebug(read_buf, 0, bytesRead);
                    }
                }
            }
            return result;
        }
    }
}
