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
 */
public class GPSLogCtrl extends Container {
	static final int C_LOG_FMT_COUNT = 20;
    /** The object that is used to communicate with the GPS device. */
    private GPSstate m_GPSstate;
    /** The tickboxes for the format items */
	private Check [] chkLogFmtItems =new Check[C_LOG_FMT_COUNT];
	/** The button that requests to change the log format of the device */
	private Button m_btChangeFormat;
		
    /** Initialiser of this Container.<br>
     * Requires Object to communicate with GPS device.
     * @param p_GPSstate Object to communicate with GPS device.
     */
	public GPSLogCtrl(GPSstate p_GPSstate) {
		//super("Log ON/OFF", Container.);
		m_GPSstate = p_GPSstate;
	};
	
    /** Initiliaser once all objects received initial setup
     * 
     */
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
        setLogFormatControls();
		
		// Add button confirming change of log format.
		m_btChangeFormat=new Button("Change Log Format");
		add(m_btChangeFormat,CENTER,AFTER+5);
	}
	
    /** Message warning user about impact of changing log format */
	private static final String C_msgWarningFormat = 
	        "You are about to change the" +
	        "|logging format of your device." +
	        "|" +
	        "|It is required to ERASE the log" +
	        "|content on the device after this" +
	        "|operation." +
	        "|" +
	        "|LOG FORMAT CHANGE & ERASE?";
    /** Message warning the user again about the impact of a log format change */	        
	private static final String C_msgWarningFormat2 =
	        "This is your last chance to avoid" +
	        "|reformatting your device." +
	        "|" +
	        "|LOG FORMAT CHANGE & ERASE?";

	/** Options for the first warning message */
	private static final String[] C_YesCancel = {
	        "Erase", "Cancel"
	};
	/** Options for the second warning message - reverse order on purpose */
	private static final String[] C_CancelConfirm = {
	        "Cancel", "Confirm erase"
	};
    /** String saying "Attention" (used multiple times) */
    private static final String C_Attention = "Attention";
  
	/** (User) request to change the log format.
	 * Warns about requirement to erase the log too.
	 */
	public void changeLogFormat() {
        /** Object to open multiple message boxes */
	    MessageBox m_mb; 
	    m_mb=new MessageBox(C_Attention,C_msgWarningFormat,C_YesCancel);
	    m_mb.popupBlockingModal();
	    if(m_mb.getPressedButtonIndex()==0) {
	        m_mb=new MessageBox(C_Attention,C_msgWarningFormat2,C_CancelConfirm);
		    m_mb.popupBlockingModal();
		    if(m_mb.getPressedButtonIndex()==1) {
		        // Set format and reset log
		        m_GPSstate.setLogFormat(getSelectedLogFormat());
		        m_GPSstate.eraseLog();
		    }
	    }
	}

    /** Get the format set by the user in the user interface. */
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

	/** Updates the format options shown the the user.<br>
     * This is typically done when the device responded with the current settings.
	 * @param p_logFormat LogFormat to set
	 */
	public void updateLogFormat(final int p_logFormat) {
		int bitMask=1;
		//if(GPS_DEBUG) {	waba.sys.Vm.debug("UPD:"+Convert.unsigned2hex(p_logFormat,2)+"\n");}
		

		for (int i=0;i<C_LOG_FMT_COUNT;i++) {
			chkLogFmtItems[i].setChecked((p_logFormat & bitMask)!=0);
			bitMask<<=1;
		}
        setLogFormatControls();
	}
    boolean sidSet;
    
    public void setLogFormatControls() {
        boolean sidSet;
        sidSet=chkLogFmtItems[BT747_dev.FMT_SID_IDX].getChecked();
        chkLogFmtItems[BT747_dev.FMT_ELEVATION_IDX].setEnabled(sidSet);
        chkLogFmtItems[BT747_dev.FMT_AZIMUTH_IDX].setEnabled(sidSet);
        chkLogFmtItems[BT747_dev.FMT_SNR_IDX].setEnabled(sidSet);
    }

    /** Handle events for this object.
     * @param event The event to be interpreted.
     */
  	public void onEvent( Event event ) {
  		switch (event.type) {

  			case ControlEvent.PRESSED:
  			    	if (event.target==m_btChangeFormat) {
  			    	    changeLogFormat();
  			    	} else {
                      boolean z_updated=false;
                      for (int i=0;i<C_LOG_FMT_COUNT;i++) {
						if (event.target==chkLogFmtItems[i]) {
                            z_updated=true;
						}
                      }
                      if(z_updated) {
                          setLogFormatControls();
                      }
					}

  				break;
  		}
  	}
}
