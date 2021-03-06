package bt747.waba_view;

//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     seesite@bt747.org                       ***
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

import gps.convert.ExternalUtils;
import gps.log.GPSRecord;

import bt747.Txt;
import bt747.Version;
import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Time;

/**
 * @author Mario De Weerd
 */

public final class GPSconctrl extends Container implements ModelListener {
    // private PushButtonGroup btnChannelSelect;

    private Button btnRestartGps;
    private Button btnStopGps;
    private Button btnBluetooth;
    // private Button btnUSB;
    private Button btnConnectPort;

    private final AppController c;

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
    private final Model m;

    public GPSconctrl(final AppController c, final Model m) {
        this.c = c;
        this.m = m;
    }

    protected final void onStart() {
        btnBluetooth = new Button(Txt.getString(Txt.BT_BLUETOOTH));
        // Functionality only valid on Palm and Mac platform.
        // if (!bt747.sys.Settings.platform.startsWith("Palm")) {
        // btnBluetooth.setVisible(false);
        // }

        // btnUSB=new Button("USB");
        btnConnectPort = new Button(Txt.getString(Txt.BT_CONNECT_PRT));
        btnStopGps = new Button(Txt.getString(Txt.BT_CLOSE_PRT));
        btnRestartGps = new Button(Txt.getString(Txt.BT_REOPEN_PRT));
        // btnRestartGps.setGap(5);

        String[] portNbrs = new String[C_MAX_PORTNBR + 1];
        for (int i = 0; i <= C_MAX_PORTNBR; i++) {
            portNbrs[i] = "" + i;
        }
        cbPorts = new ComboBox(portNbrs);

        int portNbr = m.getIntOpt(AppSettings.PORTNBR);

        String baudRate = "" + m.getIntOpt(AppSettings.BAUDRATE);
        cbBaud = new ComboBox(BaudRates);

        add(btnBluetooth, LEFT, TOP);
        // add(btnRestartGps, RIGHT - 5, BOTTOM - 5);
        add(btnRestartGps, AFTER + 10, SAME);
        // add(btnUSB,RIGHT,SAME);
        add(cbBaud, RIGHT, SAME);
        add(btnConnectPort, LEFT, AFTER + 2);
        add(cbPorts, AFTER + 3, SAME);
        add(btnStopGps, RIGHT, SAME);
        if (m.getIntOpt(AppSettings.PORTNBR) < C_MAX_PORTNBR) {
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
            c.setIntOpt(AppSettings.BAUDRATE, JavaLibBridge.toInt(baudRate));
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
                + ")   " + Txt.getString(Txt.LOGGER) + m.getMtkLogVersion());
        lbFirmwareMainVersion
                .setText(((m.getMainVersion().length() != 0) ? Txt.getString(Txt.MAIN) : "")
                        + m.getMainVersion());
        lbFirmwareName
                .setText(((m.getFirmwareVersion().length() != 0) ? Txt.getString(Txt.FIRMWARE)
                        : "")
                        + m.getFirmwareVersion());
        String model = m.getModelStr();
        // model.replaceFirst("\?",Txt.getString(Txt.UNKNOWN));
        lbModel.setText(((model.length() != 0) ? Txt.getString(Txt.MODEL) : "") + model);
        lbFlashInfo.setText(((m.getFlashManuProdID() != 0) ? Txt.getString(Txt.FLASHINFO)
                + JavaLibBridge.unsigned2hex(m.getFlashManuProdID(), 8) + " "
                + m.getFlashDesc() : ""));
        // lbFirmwareMainVersion.repaintNow();
        // lbFirmwareName.repaintNow();
        // lbModel.repaintNow();
    }

    private String TimeStr = "";

    private void updateRMCData(final GPSRecord gps) {
        if (gps.utc > 0) {
            BT747Time t = JavaLibBridge.getTimeInstance();
            t.setUTCTime(gps.utc);
            TimeStr = Txt.getString(Txt.TIME_SEP)
                    +
                    // JavaLibBridge.toString(
                    // t.getYear())+"/"
                    // +(
                    // t.getMonth()<10?"0":"")+JavaLibBridge.toString(t.getMonth())+"/"
                    // +( t.getDay()<10?"0":"")+JavaLibBridge.toString(t.getDay())+" "
                    (t.getHour() < 10 ? "0" : "")
                    + t.getHour() + ":"
                    + (t.getMinute() < 10 ? "0" : "")
                    + t.getMinute() + ":"
                    + (t.getSecond() < 10 ? "0" : "")
                    + t.getSecond();

        }
    }

    private void updateGPSData(final GPSRecord gps) {

        lbLat.setText(Txt.getString(Txt.LAT) + JavaLibBridge.toString(gps.latitude, 5)
                + Txt.getString(Txt.HGHT_SEP) + JavaLibBridge.toString(gps.height, 3)
                + Txt.getString(Txt.METERS_ABBR));
        lbLon.setText(Txt.getString(Txt.LON) + JavaLibBridge.toString(gps.longitude, 5) + TimeStr);
        lbGeoid.setText(Txt.getString(Txt.GEOID)
                + JavaLibBridge.toString(gps.geoid, 3)
                + Txt.getString(Txt.METERS_ABBR)
                + Txt.getString(Txt.CALC)
                + JavaLibBridge.toString(ExternalUtils.wgs84Separation(gps.latitude,
                        gps.longitude), 3) + Txt.getString(Txt.METERS_ABBR) + ")");

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
                c.setBaudRate(JavaLibBridge.toInt((String) cbBaud.getSelectedItem()));
                GPS_setChannel(JavaLibBridge.toInt(((String) cbPorts
                        .getSelectedItem())));
                // } else if (event.target == btnUSB) {
                // GPS_setChannel(SerialPort.USB);
            } else if (event.target == cbBaud) {
                // m.setBaudRate(JavaLibBridge.toInt((String)m_cbBaud.getSelectedItem()));
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
            break;
        }
    }

    public final void modelEvent(final ModelEvent event) {
        switch (event.getType()) {
        case ModelEvent.GPGGA:
            updateGPSData((GPSRecord) (event.getArg()));
            break;
        case ModelEvent.GPRMC:
            updateRMCData((GPSRecord) (event.getArg()));
            updateGPSData((GPSRecord) (event.getArg()));
            break;
        case ModelEvent.UPDATE_MTK_RELEASE:
        case ModelEvent.UPDATE_MTK_VERSION:
        case ModelEvent.UPDATE_LOG_VERSION:
        case ModelEvent.UPDATE_LOG_FLASH:
            if (this.isVisible()) {
                updateButtons();
            }
            break;
        }
    }
}
