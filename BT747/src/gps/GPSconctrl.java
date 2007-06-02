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
package gps;
import waba.ui.Button;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.PushButtonGroup;
import gps.*;

/**
 * @author Mario De Weerd
 */

public class GPSconctrl extends Container {
	static final boolean GPS_DEBUG = true;
	
	PushButtonGroup btnChannelSelect;
	Button btnRestartGps;
	Button btnTestGps;
	GPSstate m_GPSstate;
	
	static final int C_CHN_BLUETOOTH = 0;
	static final int C_CHN_USB       = 1;
	static final int C_CHN_0         = 2;
	
	
	static final String[] txtChannel = {
			"BLUETOOTH",
			"USB",
			"0","1","2","3","4","5","6","7"
	};
	
	
	public GPSconctrl (GPSstate p_GPSstate) {
		m_GPSstate=p_GPSstate;
	}
	
	public void onStart() {	
		
		// Button group to select channel type
		btnChannelSelect = new PushButtonGroup(
				txtChannel, // Names
				true,       // At least one
				C_CHN_BLUETOOTH, // Default selected - get this from rxtx
				-1,			// gap
				6,			// inside gap
				1,			// rows
				false,		// all same width
				PushButtonGroup.NORMAL
		);
		add(btnChannelSelect, CENTER, AFTER+2);
		//gpsTimer = addTimer(100);
		
		btnRestartGps = new Button( "Reset COM port");
		btnRestartGps.setGap(5);
		add(btnRestartGps,RIGHT-5,BOTTOM-5);
		
		btnTestGps = new Button( "Send Test");
		btnTestGps.setGap(5);
		add(btnTestGps,LEFT-5,BOTTOM-5);
	}
	
	private void GPS_setChannel(int channel) {
		switch (channel) {
		case C_CHN_BLUETOOTH :
			m_GPSstate.setBluetooth();
			break;
		case C_CHN_USB       :
			m_GPSstate.setUsb();
			break;
		default:
			m_GPSstate.setPort(channel-C_CHN_0);
		break;
		}
	}
	
	final String[] TestReply= {"PMTK001","182","2"};
	
	public void onEvent(Event event) {
		switch (event.type) {
		case ControlEvent.PRESSED:
			if (event.target == btnChannelSelect) {
				GPS_setChannel(btnChannelSelect.getSelected());
			} else
				if (event.target == btnRestartGps) {
					m_GPSstate.GPS_restart();
				} else if (event.target == btnTestGps) {
					if(GPS_DEBUG) {	waba.sys.Vm.debug("TEST\n");}
					m_GPSstate.sendNMEA("PMTK182,1,6,1");
					if(GPS_DEBUG) {	waba.sys.Vm.debug("TEST_i\n");}
					m_GPSstate.analyseNMEA(TestReply);
					if(GPS_DEBUG) {	waba.sys.Vm.debug("TEST_ii\n");}
				}
		break;
		}
	}
	
}
