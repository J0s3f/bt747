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
package net.sf.bt747.test;

import gps.BT747Constants;
import gps.connection.DecoderStateFactory;
import gps.connection.GPSPort;
import gps.connection.GPSrxtx;
import gps.connection.NMEAWriter;
import gps.convert.Conv;
import gps.mvc.commands.GpsRxtxExecCommand;

import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.bt747.gps.mtk.MtkBinTransportMessageModel;
import net.sf.bt747.test.models.mtk.commands.Acknowledge;
import net.sf.bt747.test.models.mtk.commands.EpoReply;

import bt747.sys.Generic;
import bt747.sys.JavaLibBridge;

/**
 * Implement a model of the BT747 (to run on PC).
 * 
 * @author Mario De Weerd
 */
public class IBlue747Model {

    private static GPSPort modelPort;

    public final static void setGpsPort(final GPSPort port) {
        IBlue747Model.modelPort = port;
    }

    private int logFormat = 0x000215FF;// 0x3E;

    enum Model {
        ML7
    };

    private Model model = Model.ML7;

    private GPSrxtx gpsRxTx = null;

    private byte[] logData = null;
    private static final String logFile = "c:/bt747/Test.bin";

    /**
     * Set up system specific classes.
     */
    static {
        // Set up the low level functions interface.
        JavaLibBridge
                .setJavaLibImplementation(new net.sf.bt747.j2se.system.J2SEJavaTranslations());
        // Set the serial port class instance to use (also system specific).
        if (!GPSrxtx.hasDefaultPortInstance()) {
            GPSrxtx
                    .setDefaultGpsPortInstance(new gps.connection.GPSRxTxPort());
        }

    }

    public static void main(final String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            // Model m = new Model();
            // Controller c = new Controller(m);

            public void run() {
                (new IBlue747Model()).onStart();
            }
        });
        Generic.setDebugLevel(1);
    }

    /**
     * 
     */
    public IBlue747Model() {
    }

    public void onStart() {
        if (IBlue747Model.modelPort != null) {
            gpsRxTx = new GPSrxtx(IBlue747Model.modelPort);
        } else {
            gpsRxTx = new GPSrxtx();
        }
        gpsRxTx.setDefaults(20, 115200);
        gpsRxTx.openPort();
        // addTimer(10); // Palm minimum timer resolution= 10 ms

        TimerTask t;
        t = new TimerTask() {

            @Override
            public void run() {
                try {
                    if (gpsRxTx.isConnected()) { // gpsRxTx.getGpsPort().readCheck()
                        final Object lastResponse = gpsRxTx.getResponse();
                        if (lastResponse != null) {
                            Generic.debug("Model received:" + lastResponse.toString());
                            analyseResponse(lastResponse);
                        }
                    } else {
                        gpsRxTx.setFreeTextPortAndOpen("COM20");
                    }
                } catch (final Exception e) {
                    // TODO: handle exception
                }
            }
        };
        final Timer tm = new Timer();
        tm.scheduleAtFixedRate(t, 100, 100);
    }

    public static final int DEVICE_MODE_NMEA = 0;
    public static final int DEVICE_MODE_MTKBIN = 1;
    private int device_mode = IBlue747Model.DEVICE_MODE_NMEA;

    /**
     * Set the communication mode of the device. The device can commumnicate
     * in NMEA mode or MTKBIN mode.
     * 
     * The mode value must be one of:
     * <ul>
     * <li>{@link #DEVICE_MODE_NMEA}</li>
     * <li> {@link #DEVICE_MODE_NMEA}</li>
     * </ul>
     * 
     * @param mode
     */
    private final void setDeviceMode(final int mode) {
        if (device_mode != mode) {
            device_mode = mode;
            switch (mode) {
            case DEVICE_MODE_NMEA:
                gpsRxTx.newState(DecoderStateFactory.NMEA_STATE);
                break;
            case DEVICE_MODE_MTKBIN:
                gpsRxTx.newState(DecoderStateFactory.MTKBIN_STATE);
                break;
            default:
                break;
            }
        }
    }

    private final void analyseResponse(final Object response) {
        if (response instanceof MtkBinTransportMessageModel) {
            analyseMtkBinData((MtkBinTransportMessageModel) response);
        } else {
            analyseNMEA((String[]) response);
        }
    }

    private final void analyseMtkBinData(final MtkBinTransportMessageModel msg) {
        GpsRxtxExecCommand reply = null;
        switch (msg.getType()) {
        case BT747Constants.PMTK_SET_EPO_DATA:
            Generic.debug("Model received AGPS DATA" + msg.toString());
            reply = new EpoReply(msg);
            break;
        case BT747Constants.PMTK_SET_BIN_MODE:
            // TODO: should look at payload too .
            setDeviceMode(DEVICE_MODE_NMEA);
            break;
        default:

        }
        if(reply!=null) {
            reply.execute(gpsRxTx);
        }
    }

    private static class MtkDataModel {
        protected int logStatus = 1284;
    }

    private final MtkDataModel mtkData = new MtkDataModel();

    public void replyMTK_Ack(final String[] p_nmea) {
        switch (device_mode) {
        case DEVICE_MODE_NMEA:
            try {
                sendPacket("PMTK" + BT747Constants.PMTK_ACK_STR + ","
                        + p_nmea[0].substring(4) + ","
                        + BT747Constants.PMTK_ACK_SUCCEEDED);
            } catch (final Exception e) {
                Generic.debug("Send failed ", e);
            }
        case DEVICE_MODE_MTKBIN:

        }
    }

    public void replyMTK_Log_Ack(final String[] p_nmea) {
        try {
            sendPacket("PMTK" + BT747Constants.PMTK_ACK_STR + ","
                    + BT747Constants.PMTK_CMD_LOG + "," + p_nmea[1] + ","
                    + BT747Constants.PMTK_ACK_SUCCEEDED);
        } catch (final Exception e) {
            Generic.debug("Send failed ", e);
        }
    }

    // PMTK182,3,7,2 seems to interrupt the log transfer ...

    public int replyLogNmea(final String[] p_nmea) {
        try {
            replyMTK_Log_Ack(p_nmea);
            switch (JavaLibBridge.toInt(p_nmea[1])) {
            case BT747Constants.PMTK_LOG_Q:
                // Parameter information
                // TYPE = Parameter type
                // DATA = Parameter data
                // $PMTK182,3,TYPE,DATA
                final int z_type = JavaLibBridge.toInt(p_nmea[2]);
                if (p_nmea.length >= 3) {
                    switch (z_type) {
                    case BT747Constants.PMTK_LOG_FORMAT: // 2;
                        // if(GPS_DEBUG) {
                        // waba.sys.Vm.debug("FMT:"+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+","+p_nmea[3]+"\n");}
                        sendPacket("PMTK"
                                + BT747Constants.PMTK_CMD_LOG
                                + ","
                                + BT747Constants.PMTK_LOG_DT
                                + ","
                                + p_nmea[2]
                                + ","
                                + JavaLibBridge.unsigned2hex(logFormat,
                                        logFormat <= 0xFF ? 2 : 3) // Address
                        );
                        break;
                    case BT747Constants.PMTK_LOG_TIME_INTERVAL: // 3;
                        break;
                    case BT747Constants.PMTK_LOG_DISTANCE_INTERVAL: // 4;
                        break;
                    case BT747Constants.PMTK_LOG_SPEED_INTERVAL: // 5;
                        break;
                    case BT747Constants.PMTK_LOG_REC_METHOD: // 6;
                        break;
                    case BT747Constants.PMTK_LOG_LOG_STATUS: // 7; // bit 2
                        // =
                        sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                                + BT747Constants.PMTK_LOG_DT + ","
                                + BT747Constants.PMTK_LOG_LOG_STATUS + ","
                                + mtkData.logStatus); // 9F
                        // logging
                        // on/off
                        break;
                    case BT747Constants.PMTK_LOG_MEM_USED: // 8;
                        sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                                + BT747Constants.PMTK_LOG_DT + ","
                                + BT747Constants.PMTK_LOG_MEM_USED + ","
                                + "000019D0");
                        break;
                    case BT747Constants.PMTK_LOG_FLASH: // 9;
                        sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                                + BT747Constants.PMTK_LOG_DT + ","
                                + BT747Constants.PMTK_LOG_FLASH + ","
                                + "C22015C2"); // PMTK182,3,9,C22015C2

                        break;
                    case BT747Constants.PMTK_LOG_NBR_LOG_PTS: // 10;
                        sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG + ","
                                + BT747Constants.PMTK_LOG_DT + ","
                                + BT747Constants.PMTK_LOG_NBR_LOG_PTS + ","
                                + "00000072");
                        break;
                    case BT747Constants.PMTK_LOG_FLASH_SECTORS: // 11;
                        break;
                    default:
                    }
                }
                break;
            case BT747Constants.PMTK_LOG_Q_LOG:
                // Send data from the log
                // $PMTK182,7,START_ADDRESS,DATA
                if (logData == null) {
                    final File f = new File(IBlue747Model.logFile);
                    final FileInputStream fi = new FileInputStream(f);
                    logData = new byte[fi.available()];
                    fi.read(logData);
                    fi.close();
                }
                int address = Conv.hex2Int(p_nmea[2]);
                final StringBuffer s = new StringBuffer(Conv
                        .hex2Int(p_nmea[3]) * 2);
                for (int length = Conv.hex2Int(p_nmea[3]); length > 0;) {
                    int payload;
                    if (length <= 0x800) {
                        payload = length;
                    } else {
                        payload = 0x800;
                    }
                    length -= payload;
                    s.setLength(0);
                    int cur_addr = address;
                    for (; (cur_addr < logData.length)
                            && (cur_addr < address + payload); cur_addr++) {
                        s.append(JavaLibBridge.unsigned2hex(
                                logData[cur_addr], 2));
                    }
                    while (cur_addr++ < address + payload) {
                        s.append("FF");
                    }
                    System.err.println("PMTK"
                            + BT747Constants.PMTK_CMD_LOG_STR + ","
                            + BT747Constants.PMTK_LOG_DT_LOG + ","
                            + JavaLibBridge.unsigned2hex(address, 8) // Address
                    // + "," + s
                            );
                    sendPacket("PMTK" + BT747Constants.PMTK_CMD_LOG_STR + ","
                            + BT747Constants.PMTK_LOG_DT_LOG + ","
                            + JavaLibBridge.unsigned2hex(address, 8) // Address
                            + "," + s);
                    address += payload;
                    if (((length & 1) != 0) && (address >= logData.length)) {
                        break;
                    }
                }
                break;
            case BT747Constants.PMTK_LOG_DISABLE:
                mtkData.logStatus &= ~BT747Constants.PMTK_LOG_STATUS_LOGDISABLED_MASK;
                break;
            case BT747Constants.PMTK_LOG_ENABLE:
                mtkData.logStatus |= BT747Constants.PMTK_LOG_STATUS_LOGDISABLED_MASK;
                break;

            default:
                // Nothing - unexpected
            }
        } catch (final Exception e) {
            // TODO: handle exception
        }
        return 0; // Done.

    }

    public int analyseNMEA(final String[] p_nmea) {
        int z_Cmd;
        int z_Result = 0;
        // if(GPS_DEBUG) {
        // waba.sys.Vm.debug("ANA:"+p_nmea[0]+","+p_nmea[1]+"\n");}
        final StringBuffer nmea = new StringBuffer();
        Object response = null;
        Acknowledge acknowledge = null;
        for (final String s : p_nmea) {
            nmea.append(s);
            nmea.append(',');
        }

        Generic.debug(nmea.toString());

        if (p_nmea[0].startsWith("PMTK")) {
            z_Cmd = JavaLibBridge.toInt(p_nmea[0].substring(4));

            if (z_Cmd != BT747Constants.PMTK_CMD_LOG) {
                acknowledge = new Acknowledge(p_nmea);
            }

            z_Result = -1; // Suppose cmd not treated
            switch (z_Cmd) {
            case BT747Constants.PMTK_CMD_LOG: // CMD 182;
                z_Result = replyLogNmea(p_nmea);
                break;
            case BT747Constants.PMTK_TEST: // CMD 000
            case BT747Constants.PMTK_ACK: // CMD 001
                // Device does not reply with this
                break;
            case BT747Constants.PMTK_SYS_MSG: // CMD 010
            case BT747Constants.PMTK_CMD_HOT_START: // CMD 101
            case BT747Constants.PMTK_CMD_WARM_START: // CMD 102
            case BT747Constants.PMTK_CMD_COLD_START: // CMD 103
            case BT747Constants.PMTK_CMD_FULL_COLD_START: // CMD 104
            case BT747Constants.PMTK_SET_NMEA_BAUD_RATE: // CMD 251
            case BT747Constants.PMTK_SET_BIN_MODE: // CMD 253
                setDeviceMode(IBlue747Model.DEVICE_MODE_MTKBIN);
                break;
            case BT747Constants.PMTK_API_SET_FIX_CTL: // CMD 300
            case BT747Constants.PMTK_API_SET_DGPS_MODE: // CMD 301
            case BT747Constants.PMTK_API_SET_SBAS: // CMD 313
            case BT747Constants.PMTK_API_SET_NMEA_OUTPUT: // CMD 314
            case BT747Constants.PMTK_API_SET_PWR_SAV_MODE: // CMD 320
            case BT747Constants.PMTK_API_SET_DATUM: // CMD 330
            case BT747Constants.PMTK_API_SET_DATUM_ADVANCE: // CMD 331
            case BT747Constants.PMTK_API_SET_USER_OPTION: // CMD 390
            case BT747Constants.PMTK_API_Q_FIX_CTL: // CMD 400
            case BT747Constants.PMTK_API_Q_DGPS_MODE: // CMD 401
            case BT747Constants.PMTK_API_Q_SBAS: // CMD 413
            case BT747Constants.PMTK_API_Q_NMEA_OUTPUT: // CMD 414
                response = "PMTK514,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
                break;
            case BT747Constants.PMTK_API_Q_PWR_SAV_MOD: // CMD 420
            case BT747Constants.PMTK_API_Q_DATUM: // CMD 430
            case BT747Constants.PMTK_API_Q_DATUM_ADVANCE: // CMD 431
            case BT747Constants.PMTK_API_Q_GET_USER_OPTION: // CMD 490
                response = "PMTK590,0,1,115200,0,1,0,1,1,1,0,0,0,2,9600";
                break;
            // case BT747_dev.PMTK_DT_FIX_CTL: // CMD 500
            // case BT747_dev.PMTK_DT_DGPS_MODE: // CMD 501
            // case BT747_dev.PMTK_DT_SBAS: // CMD 513
            case BT747Constants.PMTK_DT_NMEA_OUTPUT: // CMD 514
                response = "PMTK514,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
                break;
            // case BT747_dev.PMTK_DT_PWR_SAV_MODE: // CMD 520
            // case BT747_dev.PMTK_DT_DATUM: // CMD 530
            case BT747Constants.PMTK_DT_FLASH_USER_OPTION: // CMD 590
                response = "PMTK590,0,1,115200,0,1,0,1,1,1,0,0,0,2,115200";
                break;
            // break;
            case BT747Constants.PMTK_Q_RELEASE: // CMD 605
                // m_sendPacket("PMTK" +
                // BT747Constants.PMTK_DT_RELEASE
                // + "," + "AXN_1.0-B_1.3_C01" + "," + "0001" + ","
                // + "TSI_747A+" + "," + "1.0");
                switch (model) {
                case ML7:
                    response = "PMTK705,M-core_2.02,231B,,1.0";
                    break;
                default:
                    response = "PMTK" + BT747Constants.PMTK_DT_RELEASE + ","
                            + "AXN_1.0-B_1.3_C01" + "," + "8805" + ","
                            + "QST1300" + "," + "1.0";

                    // AXN_0.3-B_1.3_C01
                    break;
                }
            case BT747Constants.PMTK_Q_VERSION:
                break;
            case BT747Constants.PMTK_SET_EPO_DATA: // CMD 722
                break;
            default:
                System.err.println("Not supported in model:" + z_Cmd);
            } // End switch
        } else if (p_nmea[0].startsWith("PTSI")) {
            z_Cmd = JavaLibBridge.toInt(p_nmea[0].substring(4));

            replyMTK_Ack(p_nmea);

            switch (z_Cmd) {
            case 999:
                if (p_nmea[1].equals("IAMAP")) {
                    response = "PTSI999,IAMAP";
                }
                break;

            default:
                break;
            }
        } // End if
        if (acknowledge != null) {
           acknowledge.execute(gpsRxTx);
        }
        if (response != null) {
            if (response instanceof String) {
                final String resp = (String) response;
                sendPacket(resp);
            }
        }
        return z_Result;
    } // End method

    private final void sendPacket(final String p) {
        System.out.println(p);
        NMEAWriter.sendPacket(gpsRxTx, p);
    }

    public String[] lastResponse;

    // public void onEvent(Event e) {
    // switch (e.type) {
    // case ControlEvent.TIMER:
    // if (m_GPSrxtx.isConnected()) {
    // lastResponse = m_GPSrxtx.getResponse();
    // if (lastResponse != null) {
    // analyseNMEA(lastResponse);
    // }
    // } else {
    // }
    // break;
    //
    // }
    // }

}
