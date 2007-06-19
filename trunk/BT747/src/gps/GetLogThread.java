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
import waba.sys.Thread;
import waba.ui.ProgressBar;
/** Implements a thread to fetch the log.
 * @author Mario De Weerd
 */
public class GetLogThread implements Thread {
	private int m_StartAddr;
	private int m_EndAddr;
	private int m_NextReqAddr;
    private int m_NextReadAddr;
	private int m_Step;
	private GPSstate m_GPSstate;
    private ProgressBar m_pb;
	

	/** Initiates the parametrs of the thread
	 * TODO: Move thread here.
	 */
	public GetLogThread(GPSstate p_GPSstate,
			int p_StartAddr, int p_EndAddr, int p_Step,
            String fileName, ProgressBar pb) {
        m_StartAddr= p_StartAddr;
        m_EndAddr=p_EndAddr;
        m_NextReqAddr= m_StartAddr;
        m_NextReadAddr= m_StartAddr;
        m_Step=p_Step;		
		m_GPSstate=p_GPSstate;
        m_pb = pb;
        if(m_pb!=null) {
            m_pb.min=m_StartAddr;
            m_pb.max=m_EndAddr;
            m_pb.setValue(m_NextReadAddr,""," b");
            m_pb.setVisible(true);
        }

	}
	
	public void getLog(int p_StartAddr, int p_EndAddr, int p_Step) {
		m_StartAddr= p_StartAddr;
		m_EndAddr=p_EndAddr;
		m_NextReadAddr= m_StartAddr;
		m_Step=p_Step;
	}

	/* (non-Javadoc)
	 * @see waba.sys.Thread#run()
	 */
	public void run() {
		//TODO: Stop the thread at the end.
		int z_Step;
		z_Step=m_EndAddr-m_NextReadAddr;
		if(z_Step>0) {
			if(z_Step>m_Step){
				z_Step=m_Step;
			}
			m_GPSstate.readLog(m_NextReadAddr,z_Step);
			m_NextReadAddr+=z_Step;
		}
	}

	/* (non-Javadoc)
	 * @see waba.sys.Thread#started()
	 */
	public void started() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see waba.sys.Thread#stopped()
	 */
	public void stopped() {
		// TODO Auto-generated method stub

	}

}
