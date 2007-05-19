import waba.ui.Check;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
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





/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPSLogCtrl extends Container {
	//static String szWidth = Convert.toString(Settings.screenWidth,1);
	//static String szHeight = Convert.toString(Settings.screenHeight,1);
	static final boolean GPS_DEBUG = true;
	static final int C_LOG_FMT_COUNT = 20;
	GPSstate m_GPSstate;
	Check [] chkLogFmtItems =new Check[C_LOG_FMT_COUNT];
		
	public GPSLogCtrl(GPSstate p_GPSstate) {
		//super("Log ON/OFF", Container.);

		m_GPSstate = p_GPSstate;
		
	};
	
	public void updateLogFormat(final int p_logFormat) {
		int bitMask=1;
		//if(GPS_DEBUG) {	waba.sys.Vm.debug("UPD:"+Convert.unsigned2hex(p_logFormat,2)+"\n");}
		

		for (int i=0;i<C_LOG_FMT_COUNT;i++) {
			chkLogFmtItems[i].setChecked((p_logFormat & bitMask)!=0);
			bitMask<<=1;
		}
	}

	public void onStart () {
		super.onStart();
//		new MessageBox("Log status", logFmtItems[0]).popupModal();		

		for (int i=0;i<C_LOG_FMT_COUNT;i++) {
			chkLogFmtItems[i]= new Check(BT747_dev.logFmtItems[i]);
			add( chkLogFmtItems[i],
				((i==0)?LEFT:((i==((C_LOG_FMT_COUNT/2)))? getClientRect().width/2:SAME)),
				((i==0) ||i==((C_LOG_FMT_COUNT/2)))? TOP:AFTER-1
			   );
			chkLogFmtItems[i].setEnabled(true);
		}		
	}
  
  	public void onEvent( Event event ) {
  		switch (event.type) {
  			case ControlEvent.PRESSED:
					for (int i=0;i<C_LOG_FMT_COUNT;i++) {
						if (event.target==chkLogFmtItems[i]) {
							;//chkLogFmtItems[i].drawHighlight();
						}
					}
  				break;
  		}
  	}
}
