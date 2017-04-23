/**
 * 
 */
package net.sf.bt747.test;

import java.util.StringTokenizer;

import gps.connection.DecoderStateInterface;
import gps.connection.GPSPort;
import gps.connection.GPSrxtx;
import gps.convert.Conv;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Semaphore;
import bt747.sys.interfaces.BT747Vector;

/**
 * This State will interpret the input stream as NMEA data.
 * 
 * @author Mario De Weerd
 * 
 */
public final class TextOrNMEADecoderState implements DecoderStateInterface {

    private static final int C_INITIAL_STATE = 0;
    private static final int C_START_STATE = 1;
    private static final int C_TEXT_STATE = 2;
    private static final int C_EOL_STATE = 3;
    private static final int C_FOUND_STATE = 4;
    private static final int C_ERROR_STATE = 5;

    private int current_state = TextOrNMEADecoderState.C_INITIAL_STATE;
    private final BT747Vector vCmd = JavaLibBridge.getVectorInstance();

    private static final int C_CMDBUF_SIZE = 0x1100;
    private final char[] cmd_buf = new char[TextOrNMEADecoderState.C_CMDBUF_SIZE];

    private int cmd_buf_p = 0;

    @SuppressWarnings("unused")
    private int checksum = 0;

    @SuppressWarnings("unused")
    private int read_checksum;

    private static boolean ignoreNMEA = false;

    /**
     * @return Returns the ignoreNMEA.
     */
    @SuppressWarnings("unused")
    private static final boolean isIgnoreNMEA() {
        return ignoreNMEA;
    }

    /**
     * @param ignoreNMEA
     *            The ignoreNMEA to set.
     */
    public final static void setIgnoreNMEA(final boolean ignoreNMEA) {
        TextOrNMEADecoderState.ignoreNMEA = ignoreNMEA;
    }

    private final BT747Semaphore rcvSema = JavaLibBridge
            .getSemaphoreInstance(1);

    /*
     * (non-Javadoc)
     * 
     * @see gps.connection.DecoderInterface#getResponse()
     */
    public final Object getResponse(final GPSrxtx context) {
        Object result = null;
        rcvSema.down();
        try {
            result = myGetResponse(context);
        } catch (Exception e) {
            Generic.debug("getResponse exception", e);
        }
        rcvSema.up();
        return result;
    }

    private final Object myGetResponse(final GPSrxtx context) {
        final GPSPort gpsPort = context.getGpsPort();
        boolean continueReading;
        int myError = GPSrxtx.ERR_NOERROR;
        final boolean skipError = true;
        // final boolean ignoreNMEA = isIgnoreNMEA(); // Cached for
        // efficiency
        continueReading = gpsPort.isConnected();

        if (current_state == TextOrNMEADecoderState.C_FOUND_STATE) {
            current_state = TextOrNMEADecoderState.C_START_STATE;
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
            default:
            case C_FOUND_STATE:
                current_state = TextOrNMEADecoderState.C_START_STATE;
                /* Fall through */
            case C_INITIAL_STATE:
            case C_START_STATE:
            case C_TEXT_STATE:
                cmd_buf[cmd_buf_p++] = c;
                if (c == 13) {
                    current_state = C_EOL_STATE;
                } else if (c == 0) {
                    current_state = C_FOUND_STATE;
                    continueReading = false;
                    cmd_buf_p--;
                }
                break;
            case C_EOL_STATE:
                cmd_buf[cmd_buf_p++] = c;
                if (c == 10) {
                    current_state = C_FOUND_STATE;
                    continueReading = false;
                } else if (c == 0) {
                    current_state = C_FOUND_STATE;
                    continueReading = false;
                } else if (c != 13) {
                    current_state = C_TEXT_STATE;
                }
                break;
            }
            if (cmd_buf_p > (TextOrNMEADecoderState.C_CMDBUF_SIZE - 1)) {
                myError = GPSrxtx.ERR_TOO_LONG;
                current_state = TextOrNMEADecoderState.C_ERROR_STATE;
            }
            if (current_state == TextOrNMEADecoderState.C_ERROR_STATE) {
                current_state = TextOrNMEADecoderState.C_INITIAL_STATE;
                vCmd.removeAllElements();
                if (!skipError) {
                    continueReading = false;
                }
            }
        }

        if (myError == TextOrNMEADecoderState.C_ERROR_STATE) {
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
        if (current_state == TextOrNMEADecoderState.C_FOUND_STATE) {
            final String line = new String(cmd_buf, 0, cmd_buf_p); // Strip
            cmd_buf_p = 0;
            // end
            // of
            // line.

            if (line.startsWith("$") && line.charAt(line.length() - 5) == '*') {
                // Could be NMEA
                String checksumStr = line.substring(line.length() - 4,line.length() - 2);
                int checksum = Conv.hex2Int(checksumStr);
                String nmeaStr = line.substring(1, line.length() - 5);
                int c = 0;
                for (int i = 0; i < nmeaStr.length(); i++) {
                    c ^= nmeaStr.charAt(i);
                }

                if (c == checksum) {
                    final StringTokenizer fields = new StringTokenizer(
                            nmeaStr, ",");
                    while (fields.hasMoreTokens()) {
                        vCmd.addElement(fields.nextToken());
                    }
                    return JavaLibBridge.toStringArrayAndEmpty(vCmd);
                } else {
                }
            }
            vCmd.removeAllElements();
            return line.substring(0, line.length());
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.connection.DecoderStateInterface#enterState(gps.connection.GPSrxtx)
     */
    public final void enterState(final GPSrxtx context) {
        current_state = TextOrNMEADecoderState.C_INITIAL_STATE;
        vCmd.removeAllElements();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gps.connection.DecoderStateInterface#exitState(gps.connection.GPSrxtx)
     */
    public void exitState(final GPSrxtx context) {
        // TODO Auto-generated method stub

    }

}
