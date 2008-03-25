//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************                              
package gps.model;

import bt747.sys.Convert;
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;

import gps.BT747_dev;
import gps.GPSrxtx;
import gps.convert.Conv;

/**
 * Implement a model of the BT747 (to run on PC).
 * 
 * @author Mario De Weerd
 */
public class BT747model extends Control {

    public int logFormat = 0x3E;

    GPSrxtx m_GPSrxtx = null;

    /**
     *  
     */
    public BT747model() {
    }

    public void onStart() {
        m_GPSrxtx = new GPSrxtx();
        m_GPSrxtx.setDefaults(2, 115200);
        //m_GPSrxtx.openPort();
        addTimer(10); // Palm minimum timer resolution= 10 ms
    }

    public void replyMTK_Ack(final String[] p_nmea) {
        m_GPSrxtx.sendPacket("PMTK" + BT747_dev.PMTK_ACK_STR + ","
                + BT747_dev.PMTK_CMD_LOG + "," + p_nmea[1] + ","
                + BT747_dev.PMTK_ACK_SUCCEEDED);
    }

    public int replyLogNmea(final String[] p_nmea) {
        if (p_nmea.length > 2) {
            switch (Convert.toInt(p_nmea[1])) {
            case BT747_dev.PMTK_LOG_Q:
                // Parameter information
                // TYPE = Parameter type
                // DATA = Parameter data
                // $PMTK182,3,TYPE,DATA
                int z_type = Convert.toInt(p_nmea[2]);
                if (p_nmea.length == 3) {
                    switch (z_type) {
                    case BT747_dev.PMTK_LOG_FORMAT: // 2;
                        //if(GPS_DEBUG) {
                        // waba.sys.Vm.debug("FMT:"+p_nmea[0]+","+p_nmea[1]+","+p_nmea[2]+","+p_nmea[3]+"\n");}
                        m_GPSrxtx.sendPacket("PMTK" + BT747_dev.PMTK_CMD_LOG
                                + "," + BT747_dev.PMTK_LOG_RESP_STR + ","
                                + p_nmea[2] + ","
                                + Convert.unsigned2hex(logFormat, 2) // Address
                        );
                        break;
                    case BT747_dev.PMTK_LOG_TIME_INTERVAL: // 3;
                        break;
                    case BT747_dev.PMTK_LOG_DISTANCE_INTERVAL: //4;
                        break;
                    case BT747_dev.PMTK_LOG_SPEED_INTERVAL: // 5;
                        break;
                    case BT747_dev.PMTK_LOG_REC_METHOD: // 6;
                        break;
                    case BT747_dev.PMTK_LOG_LOG_STATUS: // 7; // bit 2 = logging
                                                        // on/off
                        break;
                    case BT747_dev.PMTK_LOG_MEM_USED: // 8;
                        break;
                    case BT747_dev.PMTK_LOG_INIT: // 9;
                        break;
                    case BT747_dev.PMTK_LOG_NBR_LOG_PTS: // 10;
                        break;
                    case BT747_dev.PMTK_LOG_FLASH_SECTORS: // 11;
                        break;
                    default:
                    }
                }
                break;
            case BT747_dev.PMTK_LOG_Q_LOG:
                // Send data from the log
                // $PMTK182,7,START_ADDRESS,DATA
                StringBuffer s = new StringBuffer(Conv.hex2Int(p_nmea[3]) * 2);
                s.setLength(0);
                for (int i = Conv.hex2Int(p_nmea[3]) * 2; i > 0; i--) {
                    s.append('F');
                }
                m_GPSrxtx.sendPacket("PMTK" + BT747_dev.PMTK_CMD_LOG_STR + ","
                        + BT747_dev.PMTK_LOG_DT_LOG + "," + p_nmea[2] // Address
                        + "," + s);
                break;
            default:
            // Nothing - unexpected
            }
        }
        return 0; // Done.

    }

    public int analyseNMEA(String[] p_nmea) {
        int z_Cmd;
        int z_Result = 0;
        //if(GPS_DEBUG) {
        // waba.sys.Vm.debug("ANA:"+p_nmea[0]+","+p_nmea[1]+"\n");}
        if (p_nmea[0].startsWith("PMTK")) {
            replyMTK_Ack(p_nmea);
            z_Cmd = Convert.toInt(p_nmea[0].substring(4));

            z_Result = -1; // Suppose cmd not treated
            switch (z_Cmd) {
            case BT747_dev.PMTK_CMD_LOG: // CMD 182;
                z_Result = replyLogNmea(p_nmea);
                break;
            case BT747_dev.PMTK_TEST: // CMD 000
            case BT747_dev.PMTK_ACK: // CMD 001
                // Device does not reply with this
                break;
            case BT747_dev.PMTK_SYS_MSG: // CMD 010
            case BT747_dev.PMTK_CMD_HOT_START: // CMD 101
            case BT747_dev.PMTK_CMD_WARM_START: // CMD 102
            case BT747_dev.PMTK_CMD_COLD_START: // CMD 103
            case BT747_dev.PMTK_CMD_FULL_COLD_START: // CMD 104
            case BT747_dev.PMTK_SET_NMEA_BAUD_RATE: // CMD 251
            case BT747_dev.PMTK_API_SET_FIX_CTL: // CMD 300
            case BT747_dev.PMTK_API_SET_DGPS_MODE: // CMD 301
            case BT747_dev.PMTK_API_SET_SBAS: // CMD 313
            case BT747_dev.PMTK_API_SET_NMEA_OUTPUT: // CMD 314
            case BT747_dev.PMTK_API_SET_PWR_SAV_MODE: // CMD 320
            case BT747_dev.PMTK_API_SET_DATUM: // CMD 330
            case BT747_dev.PMTK_API_SET_DATUM_ADVANCE: // CMD 331
            case BT747_dev.PMTK_API_SET_USER_OPTION: // CMD 390
            case BT747_dev.PMTK_API_Q_FIX_CTL: // CMD 400
            case BT747_dev.PMTK_API_Q_DGPS_MODE: // CMD 401
            case BT747_dev.PMTK_API_Q_SBAS: // CMD 413
            case BT747_dev.PMTK_API_Q_NMEA_OUTPUT: // CMD 414
            case BT747_dev.PMTK_API_Q_PWR_SAV_MOD: // CMD 420
            case BT747_dev.PMTK_API_Q_DATUM: // CMD 430
            case BT747_dev.PMTK_API_Q_DATUM_ADVANCE: // CMD 431
            case BT747_dev.PMTK_API_Q_GET_USER_OPTION: // CMD 490
//            case BT747_dev.PMTK_DT_FIX_CTL: // CMD 500
//            case BT747_dev.PMTK_DT_DGPS_MODE: // CMD 501
//            case BT747_dev.PMTK_DT_SBAS: // CMD 513
//            case BT747_dev.PMTK_DT_NMEA_OUTPUT: // CMD 514
//            case BT747_dev.PMTK_DT_PWR_SAV_MODE: // CMD 520
//            case BT747_dev.PMTK_DT_DATUM: // CMD 530
//            case BT747_dev.PMTK_DT_FLASH_USER_OPTION: // CMD 590
            case BT747_dev.PMTK_Q_VERSION: // CMD 604
            default:
                // Not handled
                break;
            } // End switch
        } // End if
        return z_Result;
    } // End method

    public String[] lastResponse;

    public void onEvent(Event e) {
        switch (e.type) {
        case ControlEvent.TIMER:
            if (m_GPSrxtx.isConnected()) {
                lastResponse = m_GPSrxtx.getResponse();
                if (lastResponse != null) {
                    analyseNMEA(lastResponse);
                }
            } else {
            }
            break;

        }
    }
}
