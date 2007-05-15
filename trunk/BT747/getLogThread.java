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
public class getLogThread implements Thread {
	private int m_StartAddr;
	private int m_EndAddr;
	private int m_NextAddr;
	private int m_Step;
	private GPSstate m_GPSstate;
	

	/**
	 * 
	 */
	public getLogThread(GPSstate p_GPSstate,
			int p_StartAddr, int p_EndAddr, int p_Step) {
		m_StartAddr= p_StartAddr;
		m_NextAddr= m_StartAddr;
		m_EndAddr=p_EndAddr;
		m_Step=p_Step;
		m_GPSstate=p_GPSstate;
	}
	
	public void getLog(int p_StartAddr, int p_EndAddr, int p_Step) {
		m_StartAddr= p_StartAddr;
		m_EndAddr=p_EndAddr;
		m_NextAddr= m_StartAddr;
		m_Step=p_Step;
	}

	/* (non-Javadoc)
	 * @see waba.sys.Thread#run()
	 */
	public void run() {
		//TODO: Stop the thread at the end.
		int z_Step;
		z_Step=m_EndAddr-m_NextAddr;
		if(z_Step>0) {
			if(z_Step>m_Step){
				z_Step=m_Step;
			}
			m_GPSstate.readLog(m_NextAddr,z_Step);
			m_NextAddr+=z_Step;
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
