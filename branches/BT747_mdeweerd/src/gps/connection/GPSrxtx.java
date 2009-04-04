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
import bt747.sys.interfaces.BT747Vector;

/**
 * This class implements the low level driver of the GPS device. It extracs
 * NMEA strings. The getResponse function should be called regurarly to get
 * the GPS device's response.
 * 
 * @author Mario De Weerd
 */
public final class GPSrxtx {
    private static GPSPort gpsPort;

    private final BT747Semaphore writeOngoing = JavaLibBridge
            .getSemaphoreInstance(1);

    private boolean ignoreNMEA = false;

    private boolean stableStrategy = false; // Some improvement on PDA when
    // true.
    private int prevReadCheck = 0;

    /**
     * Class constructor.
     */
    public GPSrxtx() {
    }

    public static void setGpsPortInstance(final GPSPort portInstance) {
        GPSrxtx.gpsPort = portInstance;
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

    private static final int C_INITIAL_STATE = 0;
    private static final int C_START_STATE = 1;
    private static final int C_FIELD_STATE = 2;
    private static final int C_STAR_STATE = 3;
    private static final int C_CHECKSUM_CHAR1_STATE = 4;
    // private static final int C_CHECKSUM_CHAR2_STATE = 5;
    private static final int C_EOL_STATE = 6;
    private static final int C_ERROR_STATE = 7;
    private static final int C_FOUND_STATE = 8;
    private static final int C_DPL700_STATE = 9;
    private static final int C_DPL700_W_STATE = 10;
    private static final int C_DPL700_P_STATE = 11;
    private static final int C_DPL700_TEXT_STATE = 12;
    private static final int C_DPL700_END_STATE = 13;
    private static final int C_DPL700_TICK_STATE = 14;

    // The maximum length of each packet is restricted to 255 bytes (except
    // for
    // logger)
    private static final int C_BUF_SIZE = 0x1100;
    private static final int C_CMDBUF_SIZE = 0x1100;

    private int current_state = GPSrxtx.C_INITIAL_STATE;

    private final byte[] read_buf = new byte[GPSrxtx.C_BUF_SIZE];
    private final char[] cmd_buf = new char[GPSrxtx.C_CMDBUF_SIZE];

    private int read_buf_p = 0;
    private int cmd_buf_p = 0;
    private int bytesRead = 0;
    private int checksum = 0;
    private int read_checksum;

    static final int ERR_NOERROR = 0;
    static final int ERR_CHECKSUM = 1;
    static final int ERR_INCOMPLETE = 2;
    static final int ERR_TOO_LONG = 3;

    private final BT747Vector vCmd = JavaLibBridge.getVectorInstance();
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

    private static final int NORMAL_MODE = 0;
    private static final int DPL700_MODE = 1;

    private int rxtxMode = GPSrxtx.NORMAL_MODE;

    private byte[] DPL700_buffer;
    private int DPL700_buffer_idx;
    private final byte[] DPL700_EndString = new byte[200];
    private int endStringIdx;

    public final byte[] getDPL700_buffer() {
        return DPL700_buffer;
    }

    public final int getDPL700_buffer_idx() {
        return DPL700_buffer_idx;
    }

    public final void sendCmdAndGetDPL700Response(final int cmd,
            final int buffer_size) {
        if (isConnected()) {
            final byte[] sendbuffer = new byte[7];
            writeOngoing.down(); // Semaphore - reserve link
            try {
                DPL700_buffer = new byte[buffer_size];
                rxtxMode = GPSrxtx.DPL700_MODE;
                endStringIdx = 0;
                current_state = GPSrxtx.C_DPL700_STATE;
                DPL700_buffer_idx = 0;
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
                DPL700_buffer = new byte[buffer_size];
                rxtxMode = GPSrxtx.DPL700_MODE;
                endStringIdx = 0;
                current_state = GPSrxtx.C_DPL700_STATE;
                DPL700_buffer_idx = 0;
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
                current_state = GPSrxtx.C_DPL700_STATE;
                rxtxMode = GPSrxtx.DPL700_MODE;
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

    public String[] getResponse() {
        boolean continueReading;
        boolean readAgain = true;
        int myError = GPSrxtx.ERR_NOERROR;
        final boolean skipError = true;
        continueReading = GPSrxtx.gpsPort.isConnected();

        getResponseOngoing.down();
        if (GPSrxtx.gpsPort.debugActive()) {
            // Test to avoid unnecessary lost time
            GPSrxtx.gpsPort.writeDebug("\r\nR:" + Generic.getTimeStamp()
                    + ":");
        }

        if (current_state == GPSrxtx.C_FOUND_STATE) {
            current_state = GPSrxtx.C_START_STATE;
        }

        while (continueReading) {

            while (continueReading && (read_buf_p < bytesRead)) {
                // Still bytes in read buffer to interpret
                char c;
                c = (char) read_buf[read_buf_p++]; // Next character from
                // buffer
                // if((vCmd.getCount()!=0)&&((String[])vCmd.toStringArray())[0].charAt(0)=='P')
                // {
                // bt747.sys.Vm.debug(JavaLibBridge.toString(c));
                // System.err.print("["+c+"]");
                // }
                if (rxtxMode == GPSrxtx.DPL700_MODE) {
                    if (DPL700_buffer_idx < DPL700_buffer.length) {
                        DPL700_buffer[DPL700_buffer_idx++] = (byte) c;
                        // } else {
                        // rxtxMode = NORMAL_MODE;
                    }
                }

                switch (current_state) {
                case C_EOL_STATE:
                    // EOL found, record is ok.
                    // StringBuffer sb = new StringBuffer(cmd_buf_p);
                    // sb.setLength(0);
                    // sb.append(cmd_buf,0,cmd_buf_p);
                    // if(GPS_DEBUG)
                    // {bt747.sys.Vm.debug(sb.toString()+"\n");};
                    if (((c == 10) || (c == 13))) {
                        current_state = GPSrxtx.C_FOUND_STATE;
                        continueReading = false;
                        // System.err.println("[NEW]");

                        if (ignoreNMEA) {
                            // Skip NMEA strings if requested.
                            continueReading = ((String) vCmd.elementAt(0))
                                    .startsWith("GP");
                        }
                    } else {
                        current_state = GPSrxtx.C_ERROR_STATE;
                    }
                    break;

                case C_FOUND_STATE:
                    current_state = GPSrxtx.C_START_STATE;
                    /* Fall through */
                case C_INITIAL_STATE:
                case C_START_STATE:
                    vCmd.removeAllElements();
                    if (c == '$') {
                        // First character of NMEA string found
                        current_state = GPSrxtx.C_FIELD_STATE;
                        cmd_buf_p = 0;

                        // cmd_and_param=new String()[];
                        // cmd_buf[cmd_buf_p++]= c;
                        checksum = 0;
                    } else if (!((c == 10) || (c == 13))) {
                        if (current_state == GPSrxtx.C_START_STATE) {
                            myError = GPSrxtx.ERR_INCOMPLETE;
                            current_state = GPSrxtx.C_ERROR_STATE;
                        }
                    }
                    break;
                case C_FIELD_STATE:
                    if ((c == 10) || (c == 13)) {
                        current_state = GPSrxtx.C_EOL_STATE;
                    } else if (c == '*') {
                        current_state = GPSrxtx.C_STAR_STATE;
                        vCmd.addElement(new String(cmd_buf, 0, cmd_buf_p));
                        // if((vCmd.getCount()!=0)&&((String[])vCmd.toObjectArray())[0].charAt(0)=='P')
                        // {
                        // bt747.sys.Vm.debug(((String[])vCmd.toObjectArray())[vCmd.getCount()-1]);
                        // }
                    } else if (c == ',') {
                        checksum ^= c;
                        vCmd.addElement(new String(cmd_buf, 0, cmd_buf_p));
                        cmd_buf_p = 0;
                    } else {
                        cmd_buf[cmd_buf_p++] = c;
                        checksum ^= c;
                    }
                    break;
                case C_STAR_STATE:
                    if ((c == 10) || (c == 13)) {
                        current_state = GPSrxtx.C_ERROR_STATE;
                    } else if ((((c >= '0') && (c <= '9'))
                            || ((c >= 'A') && (c <= 'F')) || ((c >= 'a') && (c <= 'f')))) {
                        // cmd_buf[cmd_buf_p++]= c;
                        if ((c >= '0') && (c <= '9')) {
                            read_checksum = (c - '0') << 4;
                        } else if ((c >= 'A') && (c <= 'F')) {
                            read_checksum = (c - 'A' + 10) << 4;
                        } else {
                            read_checksum = (c - 'a' + 10) << 4;
                        }
                        current_state = GPSrxtx.C_CHECKSUM_CHAR1_STATE;
                    } else {
                        myError = GPSrxtx.ERR_INCOMPLETE;
                        current_state = GPSrxtx.C_ERROR_STATE;
                    }
                    break;
                case C_CHECKSUM_CHAR1_STATE:
                    if ((c == 10) || (c == 13)) {
                        myError = GPSrxtx.ERR_INCOMPLETE;
                        current_state = GPSrxtx.C_ERROR_STATE;
                    } else if ((((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'F')))) {
                        // cmd_buf[cmd_buf_p++]= c;
                        if ((c >= '0') && (c <= '9')) {
                            read_checksum += c - '0';
                        } else if ((c >= 'A') && (c <= 'F')) {
                            read_checksum += c - 'A' + 10;
                        } else {
                            read_checksum += c - 'a' + 10;
                        }

                        if (read_checksum != checksum) {
                            myError = GPSrxtx.ERR_CHECKSUM;
                            current_state = GPSrxtx.C_ERROR_STATE;
                        }
                        current_state = GPSrxtx.C_EOL_STATE;
                    } else {
                        myError = GPSrxtx.ERR_INCOMPLETE;
                        current_state = GPSrxtx.C_ERROR_STATE;
                    }
                    break;
                case C_ERROR_STATE:
                    if (((c == 10) || (c == 13))) {
                        // EOL found, start is ok.
                        current_state = GPSrxtx.C_START_STATE;
                    }
                    break;
                case C_DPL700_STATE:
                    // Vm.debug("INIT_STATE");
                    // System.err.print(c);
                    if (c == 'W') {
                        endStringIdx = 0;
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = GPSrxtx.C_DPL700_W_STATE;
                    } else {
                        current_state = GPSrxtx.C_DPL700_STATE;
                    }
                    break;
                case C_DPL700_W_STATE:
                    // Vm.debug("W_STATE");
                    if (c == 'P') {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = GPSrxtx.C_DPL700_P_STATE;
                        break;
                    } else if (c == 'W') {
                        endStringIdx = 0;
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = GPSrxtx.C_DPL700_W_STATE;
                    } else if (c == '\'') {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = GPSrxtx.C_DPL700_TICK_STATE;
                    } else {
                        current_state = GPSrxtx.C_DPL700_STATE;
                    }
                    break;
                case C_DPL700_TICK_STATE:
                    // Vm.debug("TICK_STATE");
                    if (c == 'P') {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = GPSrxtx.C_DPL700_P_STATE;
                        break;
                    } else if (c == 'W') {
                        endStringIdx = 0;
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = GPSrxtx.C_DPL700_W_STATE;
                    } else {
                        current_state = GPSrxtx.C_DPL700_STATE;
                    }
                    break;
                case C_DPL700_P_STATE:
                    // Vm.debug("P_STATE");
                    if (c == ' ') {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = GPSrxtx.C_DPL700_TEXT_STATE;
                        break;
                    } else if (c == 'W') {
                        current_state = GPSrxtx.C_DPL700_W_STATE;
                    } else {
                        current_state = GPSrxtx.C_DPL700_STATE;
                    }
                    break;
                case C_DPL700_TEXT_STATE:
                    // Vm.debug("TXT_STATE");
                    // Trying to read end string
                    if (c == 0) {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        DPL700_buffer_idx -= endStringIdx;
                        rxtxMode = GPSrxtx.NORMAL_MODE;
                        current_state = GPSrxtx.C_DPL700_END_STATE;
                        Generic.debug("End DPL700");
                        continueReading = false;
                    } else if (((c >= 'A') && (c <= 'Z'))
                            || ((c >= 'a') && (c <= 'z')) || (c == ' ')
                            || (c == '+') || (c == '\'')) {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                    } else {
                        current_state = GPSrxtx.C_DPL700_STATE;
                    }
                    break;
                default:
                    rxtxMode = GPSrxtx.NORMAL_MODE;
                    current_state = GPSrxtx.C_ERROR_STATE;
                    break;
                }
                if (cmd_buf_p > (GPSrxtx.C_BUF_SIZE - 1)) {
                    myError = GPSrxtx.ERR_TOO_LONG;
                    current_state = GPSrxtx.C_ERROR_STATE;
                }
                if (current_state == GPSrxtx.C_ERROR_STATE) {
                    current_state = GPSrxtx.C_INITIAL_STATE;
                    vCmd.removeAllElements();
                    if (!skipError) {
                        continueReading = false;
                    }
                }
            }

            // All bytes in buffer are read.
            // If the command is not complete, we continue reading
            if (continueReading) {
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
                    } else if (readAgain) {
                        readAgain = false;
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
                        continueReading = false;
                    } else {
                        if (GPSrxtx.gpsPort.debugActive()) {
                            final String q = "(" + Generic.getTimeStamp()
                                    + ")";
                            GPSrxtx.gpsPort.writeDebug(q.getBytes(), 0, q
                                    .length());
                            GPSrxtx.gpsPort
                                    .writeDebug(read_buf, 0, bytesRead);
                        }
                    }
                }
            } // continueReading
        }
        if (myError == GPSrxtx.C_ERROR_STATE) {
            // bt747.sys.Vm.debug("Error on reception");
            //
            // if(GPS_DEBUG&&(vCmd.getCount()!=0)) {
            // String s;
            // s="-";
            // bt747.sys.Vm.debug(s);
            // for (int i = 0; i < vCmd.getCount(); i++) {
            // bt747.sys.Vm.debug(((String[])vCmd.toObjectArray())[i]);
            // };
            // //bt747.sys.Vm.debug(s);
            // }
            vCmd.removeAllElements();
        }
        // if((vCmd.getCount()!=0)&&(Settings.platform.equals("Java"))) {
        // String s=new String();
        // s="<";
        // bt747.sys.Vm.debug("<");
        // for (int i = 0; i < vCmd.getCount(); i++) {
        // s+=((String[])vCmd.toObjectArray())[i];
        // };
        // bt747.sys.Vm.debug(s);
        // }
        if (current_state == GPSrxtx.C_FOUND_STATE) {
            // if((vCmd.getCount()!=0)&&((String[])vCmd.toObjectArray())[0].charAt(0)=='P')
            // {
            // // return (String[])vCmd.toObjectArray();
            // // }
            // for (int i = 0; i < vCmd.getCount(); i++) {
            // bt747.sys.Vm.debug("Rec:"+JavaLibBridge.toString(
            // ((String[])vCmd.toObjectArray())[i].length()));
            // };
            // }
            getResponseOngoing.up();
            return JavaLibBridge.toStringArrayAndEmpty(vCmd);
        } else if (current_state == GPSrxtx.C_DPL700_END_STATE) {
            current_state = GPSrxtx.C_FOUND_STATE;
            final String[] resp = new String[1];
            resp[0] = new String(DPL700_EndString, 0, endStringIdx - 1);
            if (GPSrxtx.gpsPort.debugActive()) {
                // Test to avoid unnecessary lost time
                GPSrxtx.gpsPort.writeDebug("\r\nDPL700:" + resp[0]);
            }
            getResponseOngoing.up();
            return resp;
        } else {
            getResponseOngoing.up();
            return null;
        }
    }

    /**
     * @return Returns the ignoreNMEA.
     */
    public final boolean isIgnoreNMEA() {
        return ignoreNMEA;
    }

    /**
     * @param ignoreNMEA
     *                The ignoreNMEA to set.
     */
    public final void setIgnoreNMEA(final boolean ignoreNMEA) {
        this.ignoreNMEA = ignoreNMEA;
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
