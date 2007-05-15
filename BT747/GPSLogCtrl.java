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
		
	static final String [] logFmtItems = {
		"UTC",		// = 0x00001	// 0
		"VALID",	// = 0x00002	// 1
		"LATITUDE",	// = 0x00004	// 2
		"LONGITUDE",// = 0x00008	// 3
		"HEIGHT",	// = 0x00010	// 4
		"SPEED",	// = 0x00020	// 5
		"HEADING",	// = 0x00040	// 6
		"DSTA",		// = 0x00080	// 7
		"DAGE",		// = 0x00100	// 8
		"PDOP",		// = 0x00200	// 9
		"HDOP",		// = 0x00400	// A
		"VDOP",		// = 0x00800	// B
		"NSAT",		// = 0x01000	// C
		"SID",		// = 0x02000	// D
		"ELEVATION",// = 0x04000	// E
		"AZIMUTH",	// = 0x08000	// F
		"SNR",		// = 0x10000	// 10
		"RCR",		// = 0x20000	// 11
		"MILISECOND",// = 0x40000	// 12
		"DISTANCE"	// = 0x80000	// 13
	};	

	// TODO: know about all sizes.  When '0', size is unknown
	static final int[] logFmtByteSizes = {
			4, //"UTC",		// = 0x00001	// 0
			2, //"VALID",	// = 0x00002	// 1
			8, //"LATITUDE",	// = 0x00004	// 2
			4, //"LONGITUDE",// = 0x00008	// 3
			4, //"HEIGHT",	// = 0x00010	// 4
			4, //"SPEED",	// = 0x00020	// 5
			4, //"HEADING",	// = 0x00040	// 6
			0, //"DSTA",		// = 0x00080	// 7
			0, //"DAGE",		// = 0x00100	// 8
			2, //"PDOP",		// = 0x00200	// 9
			2, //"HDOP",		// = 0x00400	// A
			2, //"VDOP",		// = 0x00800	// B
			2, //"NSAT",		// = 0x01000	// C
			0, //"SID",		// = 0x02000	// D
			0, //"ELEVATION",// = 0x04000	// E
			0, //"AZIMUTH",	// = 0x08000	// F
			0, //"SNR",		// = 0x10000	// 10
			2, //"RCR",		// = 0x20000	// 11
			2, //"MILISECOND",// = 0x40000	// 12
			8, //"DISTANCE"	// = 0x80000	// 13
		};	
	
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
			chkLogFmtItems[i]= new Check(logFmtItems[i]);
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
