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
import waba.io.SerialPort;
import bt747.ui.Button;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;

import gps.GPSFile;
import gps.GPSRecord;
import gps.GPSstate;
import gps.GpsEvent;
import gps.convert.Conv;

import bt747.Version;
import bt747.sys.Convert;
import bt747.sys.Time;

/**
 * @author Mario De Weerd
 */

public class GPSconctrl extends Container {
    //private PushButtonGroup btnChannelSelect;

    private Button btnRestartGps;
    private Button btnStopGps;
    private Button btnBluetooth;
    private Button btnUSB;
    private Button btnConnectPort;

    private GPSstate m_GPSstate;

    private Label lbLat;  // GPS information
    private Label lbLon; // GPS information
    private Label lbGeoid; // GPS information
    
    private Label lbFirmwareMainVersion;
    private Label lbFirmwareName;
    private Label lbModel;
    ComboBox m_cbPorts;

    

    private final static int C_MAX_PORTNBR = 32;
    private AppSettings m_Settings;

    public GPSconctrl(GPSstate p_GPSstate, AppSettings settings) {
        m_GPSstate = p_GPSstate;
        m_Settings = settings;
    }

    public void onStart() {

        btnBluetooth=new Button("BLUETOOTH");
        btnUSB=new Button("USB");
        btnConnectPort=new Button("Connect Port Nbr");
        
        String[] portNbrs=new String[C_MAX_PORTNBR+1]; 
        for(int i=0; i<=C_MAX_PORTNBR;i++) {
            portNbrs[i]=Convert.toString(i);
        }
        m_cbPorts=new ComboBox(portNbrs);
        
        int portNbr=m_Settings.getPortnbr();

        add(btnBluetooth,LEFT,TOP);
        add(btnUSB,RIGHT,SAME);
        add(btnConnectPort,LEFT,AFTER+2);
        add(m_cbPorts,AFTER+3, SAME);
        if(m_Settings.getPortnbr()<C_MAX_PORTNBR) {
            m_cbPorts.select(portNbr);
        }
        //repaintNow();
        
        add(lbLat=new Label(""), LEFT, AFTER); //$NON-NLS-1$)
        add(lbLon=new Label(""), LEFT, AFTER); //$NON-NLS-1$)
        add(lbGeoid=new Label(""), LEFT, AFTER); //$NON-NLS-1$)


        btnRestartGps = new Button("(Re)open COM port");
        btnRestartGps.setGap(5);
        btnStopGps = new Button("Close port");
        btnStopGps.setGap(5);
        add(btnStopGps, LEFT + 5, BOTTOM - 5);
        add(btnRestartGps, RIGHT - 5, BOTTOM - 5);
        add(new Label("This SW: V"+Version.VERSION_NUMBER+"("+Version.DATE+")"), LEFT, BEFORE); //$NON-NLS-1$)
        add(lbFirmwareMainVersion=new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
        add(lbFirmwareName=new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
        add(lbModel=new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
    }

    private void GPS_setChannel(int channel) {
        switch (channel) {
        case SerialPort.BLUETOOTH:
            m_GPSstate.setBluetooth();
            btnBluetooth.press(true);
            break;
        case SerialPort.USB:
            m_GPSstate.setUsb();
            btnUSB.press(true);
            break;
        default:
            m_GPSstate.setPort(channel);
            break;
        }
    }
    
    private void updateButtons() {
        lbFirmwareMainVersion.setText(((m_GPSstate.getMainVersion().length()!=0)?"Main:":"")+m_GPSstate.getMainVersion());
        lbFirmwareName.setText(((m_GPSstate.getFirmwareVersion().length()!=0)?"Firmware:":"")+m_GPSstate.getFirmwareVersion());
        lbModel.setText(((m_GPSstate.getModel().length()!=0)?"Model:":"")+m_GPSstate.getModel());
        //lbFirmwareMainVersion.repaintNow();
        //lbFirmwareName.repaintNow();
        //lbModel.repaintNow();
    }
    
    private String TimeStr="";
    
    private void updateRMCData(final GPSRecord gps) {
        if(gps.utc>0) {
            Time t = new Time();
            GPSFile.setUTCTime(t, gps.utc);
            TimeStr="  - Time:"+
            //Convert.toString(
//                    t.getYear())+"/"
//            +( t.getMonth()<10?"0":"")+Convert.toString(t.getMonth())+"/"
//            +(   t.getDay()<10?"0":"")+Convert.toString(t.getDay())+" "
             (  t.getHour()<10?"0":"")+Convert.toString(t.getHour())+":"
            +(t.getMinute()<10?"0":"")+Convert.toString(t.getMinute())+":"
            +(t.getSecond()<10?"0":"")+Convert.toString(t.getSecond())
            ;

        }
    }
    
    private void updateGPSData(final GPSRecord gps) {

        lbLat.setText("Lat:"+Convert.toString(gps.latitude,5)+" - Hght:"+Convert.toString(gps.height,3));
        lbLon.setText("Lon:"+Convert.toString(gps.longitude,5)+
                TimeStr);
        lbGeoid.setText("Geoid:"+Convert.toString(gps.geoid,3)+"(calc:"+
                Convert.toString(Conv.wgs84_separation(gps.latitude, gps.longitude),3)+")");

        //lbLat.repaintNow();
        //lbLon.repaintNow();
        //lbGeoid.repaintNow();
    }

    public void onEvent(Event event) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target == btnBluetooth) {
                GPS_setChannel(SerialPort.BLUETOOTH);
            } else if (event.target == btnConnectPort) {
                GPS_setChannel(Convert.toInt(((String)m_cbPorts.getSelectedItem())));
            } else if (event.target == btnUSB) {
                GPS_setChannel(SerialPort.USB);
            } else if (event.target == m_cbPorts) {
                
            } else if (event.target==this) {
                m_GPSstate.getDeviceInfo();
                event.consumed=true;
            } else if (event.target == btnStopGps) {
                m_GPSstate.GPS_close();
            } else if (event.target == btnRestartGps) {
                m_GPSstate.GPS_restart();
            }
            break;
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                updateButtons();
                event.consumed=true;
            }
            break;
        case GpsEvent.GPGGA:
            GpsEvent eb=(GpsEvent) event;
            updateGPSData(m_GPSstate.getGpsRecord());
            break;
        case GpsEvent.GPRMC:
            GpsEvent ec=(GpsEvent) event;
            updateRMCData(m_GPSstate.getGpsRecord());
        break;
        }
        
    }

}
