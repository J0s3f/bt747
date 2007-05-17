import waba.sys.Thread;

import gps.GPSstate;
/*
 * Created on 14 mai 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class updateLogFormatThread implements Thread {
	GPSstate m_GPSstate;
	GPSLogCtrl m_GPSlogCtrl;
	//static final boolean GPS_DEBUG = true;
	
	public updateLogFormatThread(GPSLogCtrl p_LogCtrl,GPSstate p_State) {
		//if(GPS_DEBUG) {	waba.sys.Vm.debug("THREADinit:\n");}
		m_GPSstate=  p_State;
		m_GPSlogCtrl=p_LogCtrl;
	}
	
	public void run() {
		//if(GPS_DEBUG) {	waba.sys.Vm.debug("THREADrun:\n");}
		if(m_GPSstate.logUpdate.getCount()!=0) {
			int z_Val;
			z_Val=m_GPSstate.logUpdate.peek();
			m_GPSstate.logUpdate.clear();
			m_GPSlogCtrl.updateLogFormat(z_Val);
		}
	}
	
	public void stopped() {
		
	}
	
	public void started() {
		
	}

}
