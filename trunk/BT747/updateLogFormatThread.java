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

import waba.sys.Thread;

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
