import waba.ui.Check;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;





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
