/**
 * 
 */
package gps.connection;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Vector;

/**
 * This State will interpret the input stream as NMEA data.
 * 
 * @author Mario De Weerd
 * 
 */
public final class NMEADecoderState implements DecoderStateInterface {

    private static final int C_INITIAL_STATE = 0;
    private static final int C_START_STATE = 1;
    private static final int C_FIELD_STATE = 2;
    private static final int C_STAR_STATE = 3;
    private static final int C_CHECKSUM_CHAR1_STATE = 4;
    // private static final int C_CHECKSUM_CHAR2_STATE = 5;
    private static final int C_EOL_STATE = 6;
    private static final int C_ERROR_STATE = 7;
    private static final int C_FOUND_STATE = 8;

    private int current_state = NMEADecoderState.C_INITIAL_STATE;
    private final BT747Vector vCmd = JavaLibBridge.getVectorInstance();

    private static final int C_CMDBUF_SIZE = 0x1100;
    private final char[] cmd_buf = new char[NMEADecoderState.C_CMDBUF_SIZE];

    private int cmd_buf_p = 0;

    private int checksum = 0;

    private int read_checksum;
    
    private static boolean ignoreNMEA = false;
    /**
     * @return Returns the ignoreNMEA.
     */
    private static final boolean isIgnoreNMEA() {
        return ignoreNMEA;
    }

    /**
     * @param ignoreNMEA
     *                The ignoreNMEA to set.
     */
    public final static void setIgnoreNMEA(final boolean ignoreNMEA) {
        NMEADecoderState.ignoreNMEA = ignoreNMEA;
    }


    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderInterface#getResponse()
     */
    public final String[] getResponse(final GPSrxtx context) {
        final GPSPort gpsPort = context.getGpsPortInstance();
        boolean continueReading;
        int myError = GPSrxtx.ERR_NOERROR;
        final boolean skipError = true;
        final boolean ignoreNMEA = isIgnoreNMEA(); // Cached for
                                                            // efficiency
        continueReading = gpsPort.isConnected();
        if (gpsPort.debugActive()) {
            // Test to avoid unnecessary lost time
            gpsPort.writeDebug("\r\nR:" + Generic.getTimeStamp() + ":");
        }

        if (current_state == NMEADecoderState.C_FOUND_STATE) {
            current_state = NMEADecoderState.C_START_STATE;
        }

        while (continueReading && !context.isReadBufferEmpty()) {
            // Still bytes in read buffer to interpret
            final char c = context.getReadBufferChar();
            // buffer
            // if((vCmd.getCount()!=0)&&((String[])vCmd.toStringArray())[0].charAt(0)=='P')
            // {
            // bt747.sys.Vm.debug(JavaLibBridge.toString(c));
            // System.err.print("["+c+"]");
            // }

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
            default:
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
            return JavaLibBridge.toStringArrayAndEmpty(vCmd);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#enterState(gps.connection.GPSrxtx)
     */
    public final void enterState(final GPSrxtx context) {
        current_state = NMEADecoderState.C_INITIAL_STATE;
        vCmd.removeAllElements();
    }

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderStateInterface#exitState(gps.connection.GPSrxtx)
     */
    public void exitState(final GPSrxtx context) {
        // TODO Auto-generated method stub

    }

}
