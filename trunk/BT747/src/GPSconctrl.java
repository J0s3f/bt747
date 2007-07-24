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
import waba.ui.Button;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.PushButtonGroup;

import gps.GPSstate;
import gps.GpsEvent;

/**
 * @author Mario De Weerd
 */

public class GPSconctrl extends Container {
    private PushButtonGroup btnChannelSelect;

    private Button btnRestartGps;

    private GPSstate m_GPSstate;
    
    private Label lbFirmwareMainVersion;
    private Label lbFirmwareName;
    private Label lbModel;
    

    static final int C_CHN_BLUETOOTH = 0;

    static final int C_CHN_USB = 1;

    static final int C_CHN_0 = 2;

    private static final String[] txtChannel = { "BLUETOOTH", "USB", "0", "1", "2",
            "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14","15"};

    public GPSconctrl(GPSstate p_GPSstate) {
        m_GPSstate = p_GPSstate;
    }

    public void onStart() {

        // Button group to select channel type
        btnChannelSelect = new PushButtonGroup(txtChannel, // Names
                true, // At least one
                C_CHN_BLUETOOTH, // Default selected - get this from rxtx
                -1, // gap
                6, // inside gap
                2, // rows
                false, // all same width
                PushButtonGroup.NORMAL);
        add(btnChannelSelect, CENTER, AFTER + 2);
        //gpsTimer = addTimer(100);

        btnRestartGps = new Button("Reset COM port");
        btnRestartGps.setGap(5);
        add(btnRestartGps, RIGHT - 5, BOTTOM - 5);
        add(new Label("Version:"+Version.NUMBER+"("+Version.DATE+")"), LEFT, BEFORE); //$NON-NLS-1$)
        add(lbFirmwareMainVersion=new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
        add(lbFirmwareName=new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
        add(lbModel=new Label(""), LEFT, BEFORE); //$NON-NLS-1$)
    }

    private void GPS_setChannel(int channel) {
        switch (channel) {
        case C_CHN_BLUETOOTH:
            m_GPSstate.setBluetooth();
            break;
        case C_CHN_USB:
            m_GPSstate.setUsb();
            break;
        default:
            m_GPSstate.setPort(channel - C_CHN_0);
            break;
        }
    }
    
    private void updateButtons() {
        lbFirmwareMainVersion.setText(((m_GPSstate.MainVersion.length()!=0)?"MainVersion:":"")+m_GPSstate.MainVersion);
        lbFirmwareName.setText(((m_GPSstate.FirmwareVersion.length()!=0)?"Firmware:":"")+m_GPSstate.FirmwareVersion);
        lbModel.setText(((m_GPSstate.Model.length()!=0)?"Model:":"")+m_GPSstate.Model);
        lbFirmwareMainVersion.repaintNow();
        lbFirmwareName.repaintNow();
        lbModel.repaintNow();
    }

    public void onEvent(Event event) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target == btnChannelSelect) {
                GPS_setChannel(btnChannelSelect.getSelected());
            } else if (event.target==this) {
                m_GPSstate.getDeviceInfo();
                event.consumed=true;
                break;
            } else if (event.target == btnRestartGps) {
                m_GPSstate.GPS_restart();
            }
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                updateButtons();
                event.consumed=true;
            }
        }
    }

}
