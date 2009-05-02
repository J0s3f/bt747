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

    private DecoderStateInterface state = DecoderStateFactory
            .getInstance(DecoderStateFactory.NMEA_STATE);

    private final BT747Semaphore writeOngoing = JavaLibBridge
            .getSemaphoreInstance(1);

    private boolean stableStrategy = false; // Some improvement on PDA when
    // true.
    private int prevReadCheck = 0;

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

    // The maximum length of each packet is restricted to 255 bytes (except
    // for
    // logger)
    private static final int C_BUF_SIZE = 0x1100;

    private final byte[] read_buf = new byte[GPSrxtx.C_BUF_SIZE];

    private int read_buf_p = 0;
    private int bytesRead = 0;

    static final int ERR_NOERROR = 0;
    static final int ERR_CHECKSUM = 1;
    static final int ERR_INCOMPLETE = 2;
    static final int ERR_TOO_LONG = 3;

    private static final char[] EOL_BYTES = { '\015', '\012' };

    private final StringBuffer rec = new StringBuffer(256);

    public final boolean isConnected() {
        return (GPSrxtx.gpsPort != null) && GPSrxtx.gpsPort.isConnected();
    }

    public final void sendPacket(final String p_Packet) {
        if (isConnected()) {
            // Calculate checksum
            int z_Index = p_Packet.length();
            byte z_Checksum = 0;
            while (--z_Index >= 0) {
                z_Checksum ^= (byte) p_Packet.charAt(z_Index);
            }
            writeOngoing.down(); // Semaphore - reserve link

            try {
                rec.setLength(0);
                rec.append('$');
                rec.append(p_Packet);
                rec.append('*');
                rec.append(JavaLibBridge.unsigned2hex(z_Checksum, 2));
                if (Generic.getDebugLevel() > 1) {
                    Generic.debug(">" + rec.toString());
                }
                if (Generic.isDebug()) {
                    GPSrxtx.gpsPort.writeDebug(">" + rec.toString());
                }

                rec.append(GPSrxtx.EOL_BYTES);
                GPSrxtx.gpsPort.write(rec.toString());
            } catch (final Exception e) {
                Generic.debug("sendPacket", e);
            }
            writeOngoing.up(); // Semaphore - release link
        } else {
            Generic.debug("Not connected: skipped " + p_Packet);
        }
    }

    protected final void newState(final int newState) {
        this.state.exitState(this);
        this.state = DecoderStateFactory.getInstance(newState);
        this.state.enterState(this);
    }

    /**
     * The DPL700 state gets the buffer size from this method. Not a very
     * 'clean' way to do that.
     */
    private int DPL700BufferSize;

    protected int getDPL700BufferSize() {
        return DPL700BufferSize;
    }

    public final void sendCmdAndGetDPL700Response(final int cmd,
            final int buffer_size) {
        if (isConnected()) {
            final byte[] sendbuffer = new byte[7];
            writeOngoing.down(); // Semaphore - reserve link
            try {
                DPL700BufferSize = buffer_size;
                newState(DecoderStateFactory.DPL700_STATE);
                if (Generic.isDebug()) {
                    Generic.debug(">0x" + JavaLibBridge.unsigned2hex(cmd, 8)
                            + "000000");
                }
                sendbuffer[0] = (byte) ((cmd >> 24) & 0xFF);
                sendbuffer[1] = (byte) ((cmd >> 16) & 0xFF);
                sendbuffer[2] = (byte) ((cmd >> 8) & 0xFF);
                sendbuffer[3] = (byte) ((cmd >> 0) & 0xFF);
                sendbuffer[4] = 0;
                sendbuffer[5] = 0;
                sendbuffer[6] = 0;
                GPSrxtx.gpsPort.write(sendbuffer);
            } catch (final Exception e) {
                Generic.debug("sendAndGetDPL700", e);
            }
            writeOngoing.up(); // Semaphore - release link
        }
    }

    public final void sendCmdAndGetDPL700Response(final String cmd,
            final int buffer_size) {
        if (isConnected()) {
            writeOngoing.down(); // Semaphore - reserve link
            try {
                DPL700BufferSize = buffer_size;
                newState(DecoderStateFactory.DPL700_STATE);
                if (Generic.isDebug()) {
                    Generic.debug(">" + cmd);
                }
                rec.setLength(0);
                rec.append(cmd);
                rec.append("\0");
                GPSrxtx.gpsPort.write(rec.toString());
            } catch (final Exception e) {
                Generic.debug("send and get resp", e);
            }
            writeOngoing.up(); // Semaphore - release link
        }
    }

    public final void sendDPL700Cmd(final String cmd) {
        if (isConnected()) {
            writeOngoing.down(); // Semaphore - reserve link
            try {
                DPL700BufferSize = 0;
                newState(DecoderStateFactory.DPL700_STATE);
                if (Generic.isDebug()) {
                    Generic.debug(">" + cmd);
                }
                rec.setLength(0);
                rec.append(cmd);
                rec.append("\0");
                GPSrxtx.gpsPort.write(rec.toString());
            } catch (final Exception e) {
                Generic.debug("sendDPL700Cmd", e);
            }
            writeOngoing.up(); // Semaphore - release link
        }
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

    private final BT747Semaphore getResponseOngoing = JavaLibBridge
            .getSemaphoreInstance(1);

    protected final boolean isReadBufferEmpty() {
        if (read_buf_p >= bytesRead) {
            if (isReadAgain()) {
                // Buffer is empty. Fill it if ok.
                refillBuffer();
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
                            || (max > GPSrxtx.C_BUF_SIZE)) {

                        // gpsPort.writeDebug("\r\nC1:" + max + ":");
                        if ((max > GPSrxtx.C_BUF_SIZE)) {
                            prevReadCheck = max - GPSrxtx.C_BUF_SIZE;
                            max = GPSrxtx.C_BUF_SIZE;
                        } else {
                            prevReadCheck = 0;
                        }

                        // gpsPort.writeDebug("\r\nC2:" + max + ":");
                        if (max > 0) {
                            bytesRead = GPSrxtx.gpsPort.readBytes(read_buf,
                                    0, max);
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
                    GPSrxtx.gpsPort.writeDebug(q.getBytes(), 0, q.length());
                    GPSrxtx.gpsPort.writeDebug(read_buf, 0, bytesRead);
                }
            }
        }
        readAgain = false;
        return result;
    }

    private boolean readAgain;

    private boolean isReadAgain() {
        return readAgain;
    }

    public final Object getResponse() {
        getResponseOngoing.down();
        readAgain = true;
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
}
