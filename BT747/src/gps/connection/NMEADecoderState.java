/**
 * 
 */
package gps.connection;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Vector;

/** Refactoring ongoing.  First step was putting getResponse in separate class.
 * Next step is changing GPSrxtx class to use this class.
 * Following step is to split this class in different states (NMEA/DPL700).
 * @author Mario
 *
 */
public class NMEADecoderState implements DecoderStateInterface {

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

    
    private int current_state = NMEADecoderState.C_INITIAL_STATE;
    private final BT747Vector vCmd = JavaLibBridge.getVectorInstance();
    
    
    private byte[] DPL700_buffer;
    private int DPL700_buffer_idx;
    private final byte[] DPL700_EndString = new byte[200];
    private int endStringIdx;

    private static final int C_CMDBUF_SIZE = 0x1100;
    private final char[] cmd_buf = new char[NMEADecoderState.C_CMDBUF_SIZE];

    private int cmd_buf_p = 0;
    
    private int checksum = 0;
    
    public final byte[] getDPL700_buffer() {
        return DPL700_buffer;
    }

    public final int getDPL700_buffer_idx() {
        return DPL700_buffer_idx;
    }

    private static final int NORMAL_MODE = 0;
    private static final int DPL700_MODE = 1;

    private int rxtxMode = NMEADecoderState.NORMAL_MODE;
    
    private int read_checksum;


    /* (non-Javadoc)
     * @see gps.connection.DecoderInterface#getResponse()
     */
    public String[] getResponse(final GPSrxtx context) {
        final GPSPort gpsPort = context.getGpsPortInstance();
        boolean continueReading;
        boolean readAgain = true;
        int myError = GPSrxtx.ERR_NOERROR;
        final boolean skipError = true;
        final boolean ignoreNMEA = context.isIgnoreNMEA(); // Cached for efficiency
        continueReading = gpsPort.isConnected();

        context.getResponseOngoing.down();
        if (gpsPort.debugActive()) {
            // Test to avoid unnecessary lost time
            gpsPort.writeDebug("\r\nR:" + Generic.getTimeStamp()
                    + ":");
        }

        if (current_state == NMEADecoderState.C_FOUND_STATE) {
            current_state = NMEADecoderState.C_START_STATE;
        }

        while (continueReading) {

            while (continueReading && !context.isReadBufferEmpty()) {
                // Still bytes in read buffer to interpret
                final char c = context.getReadBufferChar();
                // buffer
                // if((vCmd.getCount()!=0)&&((String[])vCmd.toStringArray())[0].charAt(0)=='P')
                // {
                // bt747.sys.Vm.debug(JavaLibBridge.toString(c));
                // System.err.print("["+c+"]");
                // }
                if (rxtxMode == NMEADecoderState.DPL700_MODE) {
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
                        current_state = NMEADecoderState.C_FOUND_STATE;
                        continueReading = false;
                        // System.err.println("[NEW]");

                        if (ignoreNMEA) {
                            // Skip NMEA strings if requested.
                            continueReading = ((String) vCmd.elementAt(0))
                                    .startsWith("GP");
                        }
                    } else {
                        current_state = NMEADecoderState.C_ERROR_STATE;
                    }
                    break;

                case C_FOUND_STATE:
                    current_state = NMEADecoderState.C_START_STATE;
                    /* Fall through */
                case C_INITIAL_STATE:
                case C_START_STATE:
                    vCmd.removeAllElements();
                    if (c == '$') {
                        // First character of NMEA string found
                        current_state = NMEADecoderState.C_FIELD_STATE;
                        cmd_buf_p = 0;

                        // cmd_and_param=new String()[];
                        // cmd_buf[cmd_buf_p++]= c;
                        checksum = 0;
                    } else if (!((c == 10) || (c == 13))) {
                        if (current_state == NMEADecoderState.C_START_STATE) {
                            myError = GPSrxtx.ERR_INCOMPLETE;
                            current_state = NMEADecoderState.C_ERROR_STATE;
                        }
                    }
                    break;
                case C_FIELD_STATE:
                    if ((c == 10) || (c == 13)) {
                        current_state = NMEADecoderState.C_EOL_STATE;
                    } else if (c == '*') {
                        current_state = NMEADecoderState.C_STAR_STATE;
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
                        current_state = NMEADecoderState.C_ERROR_STATE;
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
                        current_state = NMEADecoderState.C_CHECKSUM_CHAR1_STATE;
                    } else {
                        myError = GPSrxtx.ERR_INCOMPLETE;
                        current_state = NMEADecoderState.C_ERROR_STATE;
                    }
                    break;
                case C_CHECKSUM_CHAR1_STATE:
                    if ((c == 10) || (c == 13)) {
                        myError = GPSrxtx.ERR_INCOMPLETE;
                        current_state = NMEADecoderState.C_ERROR_STATE;
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
                            current_state = NMEADecoderState.C_ERROR_STATE;
                        }
                        current_state = NMEADecoderState.C_EOL_STATE;
                    } else {
                        myError = GPSrxtx.ERR_INCOMPLETE;
                        current_state = NMEADecoderState.C_ERROR_STATE;
                    }
                    break;
                case C_ERROR_STATE:
                    if (((c == 10) || (c == 13))) {
                        // EOL found, start is ok.
                        current_state = NMEADecoderState.C_START_STATE;
                    }
                    break;
                case C_DPL700_STATE:
                    // Vm.debug("INIT_STATE");
                    // System.err.print(c);
                    if (c == 'W') {
                        endStringIdx = 0;
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = NMEADecoderState.C_DPL700_W_STATE;
                    } else {
                        current_state = NMEADecoderState.C_DPL700_STATE;
                    }
                    break;
                case C_DPL700_W_STATE:
                    // Vm.debug("W_STATE");
                    if (c == 'P') {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = NMEADecoderState.C_DPL700_P_STATE;
                        break;
                    } else if (c == 'W') {
                        endStringIdx = 0;
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = NMEADecoderState.C_DPL700_W_STATE;
                    } else if (c == '\'') {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = NMEADecoderState.C_DPL700_TICK_STATE;
                    } else {
                        current_state = NMEADecoderState.C_DPL700_STATE;
                    }
                    break;
                case C_DPL700_TICK_STATE:
                    // Vm.debug("TICK_STATE");
                    if (c == 'P') {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = NMEADecoderState.C_DPL700_P_STATE;
                        break;
                    } else if (c == 'W') {
                        endStringIdx = 0;
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = NMEADecoderState.C_DPL700_W_STATE;
                    } else {
                        current_state = NMEADecoderState.C_DPL700_STATE;
                    }
                    break;
                case C_DPL700_P_STATE:
                    // Vm.debug("P_STATE");
                    if (c == ' ') {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        current_state = NMEADecoderState.C_DPL700_TEXT_STATE;
                        break;
                    } else if (c == 'W') {
                        current_state = NMEADecoderState.C_DPL700_W_STATE;
                    } else {
                        current_state = NMEADecoderState.C_DPL700_STATE;
                    }
                    break;
                case C_DPL700_TEXT_STATE:
                    // Vm.debug("TXT_STATE");
                    // Trying to read end string
                    if (c == 0) {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                        DPL700_buffer_idx -= endStringIdx;
                        rxtxMode = NMEADecoderState.NORMAL_MODE;
                        current_state = NMEADecoderState.C_DPL700_END_STATE;
                        Generic.debug("End DPL700");
                        continueReading = false;
                    } else if (((c >= 'A') && (c <= 'Z'))
                            || ((c >= 'a') && (c <= 'z')) || (c == ' ')
                            || (c == '+') || (c == '\'')) {
                        DPL700_EndString[endStringIdx++] = (byte) c;
                    } else {
                        current_state = NMEADecoderState.C_DPL700_STATE;
                    }
                    break;
                default:
                    rxtxMode = NMEADecoderState.NORMAL_MODE;
                    current_state = NMEADecoderState.C_ERROR_STATE;
                    break;
                }
                if (cmd_buf_p > (NMEADecoderState.C_CMDBUF_SIZE - 1)) {
                    myError = GPSrxtx.ERR_TOO_LONG;
                    current_state = NMEADecoderState.C_ERROR_STATE;
                }
                if (current_state == NMEADecoderState.C_ERROR_STATE) {
                    current_state = NMEADecoderState.C_INITIAL_STATE;
                    vCmd.removeAllElements();
                    if (!skipError) {
                        continueReading = false;
                    }
                }
            }

            continueReading &= readAgain;
            readAgain = false;
            // All bytes in buffer are read.
            // If the command is not complete, we continue reading
            if (continueReading && readAgain) {
                readAgain = false;
                continueReading = context.refillBuffer();
            } // continueReading
        }
        if (myError == NMEADecoderState.C_ERROR_STATE) {
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
        if (current_state == NMEADecoderState.C_FOUND_STATE) {
            // if((vCmd.getCount()!=0)&&((String[])vCmd.toObjectArray())[0].charAt(0)=='P')
            // {
            // // return (String[])vCmd.toObjectArray();
            // // }
            // for (int i = 0; i < vCmd.getCount(); i++) {
            // bt747.sys.Vm.debug("Rec:"+JavaLibBridge.toString(
            // ((String[])vCmd.toObjectArray())[i].length()));
            // };
            // }
            context.getResponseOngoing.up();
            return JavaLibBridge.toStringArrayAndEmpty(vCmd);
        } else if (current_state == NMEADecoderState.C_DPL700_END_STATE) {
            current_state = NMEADecoderState.C_FOUND_STATE;
            final String[] resp = new String[1];
            resp[0] = new String(DPL700_EndString, 0, endStringIdx - 1);
            if (gpsPort.debugActive()) {
                // Test to avoid unnecessary lost time
                gpsPort.writeDebug("\r\nDPL700:" + resp[0]);
            }
            context.getResponseOngoing.up();
            return resp;
        } else {
            context.getResponseOngoing.up();
            return null;
        }
    }

}
