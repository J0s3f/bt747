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
import waba.ui.Check;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.MessageBox;





/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPSLogCtrl extends Container {
	//static String szWidth = Convert.toString(Settings.screenWidth,1);
	//static String szHeight = Convert.toString(Settings.screenHeight,1);
	static final boolean GPS_DEBUG = false;
	static final int C_LOG_FMT_COUNT = 20;
	private GPSstate m_GPSstate;
	private Check [] chkLogFmtItems =new Check[C_LOG_FMT_COUNT];
	
	private Button m_btChangeFormat;
		
	public GPSLogCtrl(GPSstate p_GPSstate) {
		//super("Log ON/OFF", Container.);
		m_GPSstate = p_GPSstate;
	};
	
	public void onStart () {
		super.onStart();

		// Add all tick buttons.
		for (int i=0;i<C_LOG_FMT_COUNT;i++) {
			chkLogFmtItems[i]= new Check(BT747_dev.logFmtItems[i]);
			add( chkLogFmtItems[i],
				((i==0)?LEFT:((i==((C_LOG_FMT_COUNT/2)))? getClientRect().width/2:SAME)),
				((i==0) ||i==((C_LOG_FMT_COUNT/2)))? TOP:AFTER-1
			   );
			chkLogFmtItems[i].setEnabled(true);
		}
		
		// Add button confirming change of log format.
		m_btChangeFormat=new Button("Change Log Format");
		add(m_btChangeFormat,CENTER,AFTER+5);
	}
	
	private static final String C_msgWarningFormat = 
	        "You are about to change the" +
	        "|logging format of your device." +
	        "|" +
	        "|It is required to ERASE the log" +
	        "|content on the device after this" +
	        "|operation." +
	        "|" +
	        "|LOG FORMAT CHANGE & ERASE?";
	        
	private static final String C_msgWarningFormat2 =
	        "This is your last chance to avoid" +
	        "|reformatting your device." +
	        "|" +
	        "|LOG FORMAT CHANGE & ERASE?";

	
	private static final String[] C_YesCancel = {
	        "Erase", "Cancel"
	};

	private static final String[] C_CancelConfirm = {
	        "Cancel", "Confirm erase"
	};
  
	/** (User) request to change the log format.
	 * Warns about requirement to erase the log too.
	 * @author Mario De Weerd 
	 */
	public void changeLogFormat() {
	    MessageBox m_mb;
	    m_mb=new MessageBox("Attention",C_msgWarningFormat,C_YesCancel);
	    m_mb.popupBlockingModal();
	    if(m_mb.getPressedButtonIndex()==0) {
	        m_mb=new MessageBox("Attention",C_msgWarningFormat2,C_CancelConfirm);
		    m_mb.popupBlockingModal();
		    if(m_mb.getPressedButtonIndex()==1) {
		        // Set format and reset log
		        m_GPSstate.setLogFormat(getSelectedLogFormat());
		        m_GPSstate.eraseLog();
		    }
	    }
	}

	public int getSelectedLogFormat() {
		int bitMask=1;
		int logFormat=0;
		for (int i=0;i<C_LOG_FMT_COUNT;i++) {
			if(chkLogFmtItems[i].getChecked()) {
			    logFormat|=bitMask;
			}
			bitMask<<=1;
		}
		return logFormat;
	}

	
	public void updateLogFormat(final int p_logFormat) {
		int bitMask=1;
		//if(GPS_DEBUG) {	waba.sys.Vm.debug("UPD:"+Convert.unsigned2hex(p_logFormat,2)+"\n");}
		

		for (int i=0;i<C_LOG_FMT_COUNT;i++) {
			chkLogFmtItems[i].setChecked((p_logFormat & bitMask)!=0);
			bitMask<<=1;
		}
	}

  	public void onEvent( Event event ) {
  		switch (event.type) {
  			case ControlEvent.PRESSED:
  			    	if (event.target==m_btChangeFormat) {
  			    	    changeLogFormat();
  			    	} else
					for (int i=0;i<C_LOG_FMT_COUNT;i++) {
					    // TODO: check consistency of options.
						if (event.target==chkLogFmtItems[i]) {
							;//chkLogFmtItems[i].drawHighlight();
						}
					}
  				break;
  		}
  	}
}
