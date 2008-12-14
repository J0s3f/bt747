package net.sourceforge.bt747.bt747.waba_view;

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
import waba.ui.Button;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;

import net.sourceforge.bt747.gps.convert.Conv;
import net.sourceforge.bt747.gps.log.GPSRecord;

import net.sourceforge.bt747.bt747.Txt;
import net.sourceforge.bt747.bt747.Version;
import net.sourceforge.bt747.bt747.model.AppController;
import net.sourceforge.bt747.bt747.model.Model;
import net.sourceforge.bt747.bt747.model.ModelEvent;
import net.sourceforge.bt747.bt747.model.ModelListener;
import net.sourceforge.bt747.bt747.sys.Convert;
import net.sourceforge.bt747.bt747.sys.Time;

/**
 * @author Mario De Weerd
 */

public class GPSconctrl extends Container implements ModelListener {
    // private PushButtonGroup btnChannelSelect;

    private Button btnRestartGps;
    private Button btnStopGps;
    private Button btnBluetooth;
    // private Button btnUSB;
    private Button btnConnectPort;

    private AppController c;

    private Label lbLat; // GPS information
    private Label lbLon; // GPS information
    private Label lbGeoid; // GPS information

    private Label lbVersion;
    private Label lbFlashInfo;
    private Label lbFirmwareMainVersion;
    private Label lbFirmwareName;
    private Label lbModel;
    private ComboBox cbPorts;

    private ComboBox cbBaud;
    private static final String[] BaudRates = { "115200", "38400" };

    private static final int C_MAX_PORTNBR = 32;
    private Model m;

    public GPSconctrl(final AppController c, final Model m) {
        this.c = c;
        this.m = m;
    }

    public final void onStart() {

        btnBluetooth = new Button(Txt.BT_BLUETOOTH);
        // Functionality only valid on Palm and Mac platform.
//        if (!bt747.sys.Settings.platform.startsWith("Palm")) {
//            btnBluetooth.setVisible(false);
//        }

        // btnUSB=new Button("USB");
        btnConnectPort = new Button(Txt.BT_CONNECT_PRT);
        btnStopGps = new Button(Txt.BT_CLOSE_PRT);
        btnRestartGps = new Button(Txt.BT_REOPEN_PRT);
        // btnRestartGps.setGap(5);

        String[] portNbrs = new String[C_MAX_PORTNBR + 1];
        for (int i = 0; i <= C_MAX_PORTNBR; i++) {
            portNbrs[i] = Convert.toString(i);
        }
        cbPorts = new ComboBox(portNbrs);

        int portNbr = m.getPortnbr();

        String baudRate = Convert.toString(m.getBaudRate());
        cbBaud = new ComboBox(BaudRates);

        add(btnBluetooth, LEFT, TOP);
        // add(btnRestartGps, RIGHT - 5, BOTTOM - 5);
        add(btnRestartGps, AFTER + 10, SAME);
        // add(btnUSB,RIGHT,SAME);
        add(cbBaud, RIGHT, SAME);
        add(btnConnectPort, LEFT, AFTER + 2);
        add(cbPorts, AFTER + 3, SAME);
        add(btnStopGps, RIGHT, SAME);
        if (m.getPortnbr() < C_MAX_PORTNBR) {
            cbPorts.select(portNbr);
        }
        // repaintNow();
        cbBaud.select(0);
        for (int i = 0; i < cbBaud.size(); i++) {
            if (baudRate.equals((String) cbBaud.getItemAt(i))) {
                cbBaud.select(i);
                break;
            }
        }
        // Set a default setting
        if (!baudRate.equals((String) cbBaud.getSelectedItem())) {
            m.setBaudRate(Convert.toInt(baudRate));
        }
        add(lbLat = new Label(""), LEFT, AFTER + 2); //$NON-NLS-1$)
        add(lbLon = new Label(""), LEFT, AFTER); //$NON-NLS-1$)
        add(lbGeoid = new Label(""), LEFT, AFTER); //$NON-NLS-1$)

        add(lbVersion = new Label(""), LEFT, BOTTOM - 5); //$NON-NLS-1$)
        add(lbFirmwareMainVersion = new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
        add(lbFirmwareName = new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
        add(lbModel = new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
        add(lbFlashInfo = new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
        updateButtons();
        m.addListener(this);
    }

    private void GPS_setChannel(final int channel) {
        switch (channel) {
        case SerialPort.BLUETOOTH:
            c.setBluetooth();
            btnBluetooth.press(true);
            break;
        // case SerialPort.USB:
        // m_GPSstate.setUsb();
        // btnUSB.press(true);
        // break;
        default:
            c.setPort(channel);
            break;
        }
    }

    private void updateButtons() {
        lbVersion.setText("V" + Version.VERSION_NUMBER + "(" + Version.DATE
                + ")   " + Txt.LOGGER + m.getMtkLogVersion());
        lbFirmwareMainVersion
                .setText(((m.getMainVersion().length() != 0) ? Txt.MAIN : "")
                        + m.getMainVersion());
        lbFirmwareName
                .setText(((m.getFirmwareVersion().length() != 0) ? Txt.FIRMWARE
                        : "")
                        + m.getFirmwareVersion());
        String model = m.getModel();
        // model.replaceFirst("\?",Txt.UNKNOWN);
        lbModel.setText(((model.length() != 0) ? Txt.MODEL : "") + model);
        lbFlashInfo.setText(((m.getFlashManuProdID() != 0) ? Txt.FLASHINFO
                + Convert.unsigned2hex(m.getFlashManuProdID(), 8) + " "
                + m.getFlashDesc() : ""));
        // lbFirmwareMainVersion.repaintNow();
        // lbFirmwareName.repaintNow();
        // lbModel.repaintNow();
    }

    private String TimeStr = "";

    private void updateRMCData(final GPSRecord gps) {
        if (gps.utc > 0) {
            Time t = new Time();
            t.setUTCTime(gps.utc);
            TimeStr = Txt.TIME_SEP
                    +
                    // Convert.toString(
                    // t.getYear())+"/"
                    // +(
                    // t.getMonth()<10?"0":"")+Convert.toString(t.getMonth())+"/"
                    // +( t.getDay()<10?"0":"")+Convert.toString(t.getDay())+" "
                    (t.getHour() < 10 ? "0" : "")
                    + Convert.toString(t.getHour()) + ":"
                    + (t.getMinute() < 10 ? "0" : "")
                    + Convert.toString(t.getMinute()) + ":"
                    + (t.getSecond() < 10 ? "0" : "")
                    + Convert.toString(t.getSecond());

        }
    }

    private void updateGPSData(final GPSRecord gps) {

        lbLat.setText(Txt.LAT + Convert.toString(gps.latitude, 5)
                + Txt.HGHT_SEP + Convert.toString(gps.height, 3)
                + Txt.METERS_ABBR);
        lbLon.setText(Txt.LON + Convert.toString(gps.longitude, 5) + TimeStr);
        lbGeoid.setText(Txt.GEOID
                + Convert.toString(gps.geoid, 3)
                + Txt.METERS_ABBR
                + Txt.CALC
                + Convert.toString(Conv.wgs84Separation(gps.latitude,
                        gps.longitude), 3) + Txt.METERS_ABBR + ")");

        // lbLat.repaintNow();
        // lbLon.repaintNow();
        // lbGeoid.repaintNow();
    }

    public final void onEvent(final Event event) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target == btnBluetooth) {
                GPS_setChannel(SerialPort.BLUETOOTH);
            } else if (event.target == btnConnectPort) {
                c.setBaudRate(Convert.toInt((String) cbBaud.getSelectedItem()));
                GPS_setChannel(Convert.toInt(((String) cbPorts
                        .getSelectedItem())));
                // } else if (event.target == btnUSB) {
                // GPS_setChannel(SerialPort.USB);
            } else if (event.target == cbBaud) {
                // m.setBaudRate(Convert.toInt((String)m_cbBaud.getSelectedItem()));
            } else if (event.target == cbPorts) {

            } else if (event.target == this) {
                c.reqDeviceInfo();
                event.consumed = true;
            } else if (event.target == btnStopGps) {
                c.closeGPS();
            } else if (event.target == btnRestartGps) {
                c.connectGPS();
            }
            break;
        default:
            if (event.type == ModelEvent.DATA_UPDATE) {
                if (event.target == this) {
                    updateButtons();
                    event.consumed = true;
                }
            } 
        }

    }

    public final void modelEvent(final ModelEvent event) {
        int eventType = event.getType();
        if (eventType == ModelEvent.GPGGA) {
            updateGPSData((GPSRecord) (event.getArg()));
        } else if (eventType == ModelEvent.GPRMC) {
            updateRMCData((GPSRecord) (event.getArg()));
            updateGPSData((GPSRecord) (event.getArg()));
        } 
    }
}