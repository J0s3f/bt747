import waba.io.File;
import waba.ui.Button;
import waba.ui.Check;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;
import waba.util.Vector;

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
public class GPSLogGet extends Container {
	GPSstate m_GPSstate;
	
	Check m_chkLogOnOff;;
	Edit m_edStartDate;
	Edit m_edEndDate;
	
	Button m_btGetLog;
	Button m_btStopLog;
	ComboBox m_cbFile;
	
	
	GPSLogGet(GPSstate state) {
		m_GPSstate= state;
	}
	
	
	// TODO: Just some code as a reference to find a path.
	void recursiveList(String path, Vector v)
	{
		if (path == null) return;
		File file = new File(path);
		String []list = file.listFiles();
		if (list != null)
			for (int i =0; i < list.length && i < 49; i++)
				if (list[i] != null)
				{
					waba.sys.Vm.debug(list[i].toString()+"\n"); 
					v.addElement(path+list[i]);
					if (list[i].endsWith("/")) // is a path?
						recursiveList(path+list[i],v);
				}
	}
	
	// TODO: Just some code as a reference to find a path.	
	void nonRecursiveList(String path, Vector v)
	{
		if (path == null) return;
		File file = new File(path);
		String []list = file.listFiles();
		if (list != null)
			for (int i =0; i < list.length && i < 49; i++)
				if (list[i] != null)
				{
					waba.sys.Vm.debug(list[i].toString()+"\n"); 
					v.addElement(path+list[i]);
					//if (list[i].endsWith("/")) // is a path?
					//	recursiveList(path+list[i],v);
				}
	}
	
	
	/* (non-Javadoc)
	 * @see waba.ui.Container#onStart()
	 * TODO: Handle date fields, ...
	 */
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		add(m_chkLogOnOff=new Check("Device log on(/off)"),LEFT,TOP);
		add(new Label("Start date"),LEFT,AFTER);
		add(m_edStartDate=new Edit("99/99/9999"),AFTER,SAME);
		m_edStartDate.setMode(Edit.DATE);
		add(m_edEndDate  =new Edit("99/99/9999"),SAME,AFTER);
		m_edEndDate.setMode(Edit.DATE);
		add(new Label("End date"),BEFORE,SAME);
		
		//add(new Label("End"),BEFORE,SAME);
		add(m_btGetLog=new Button("Get Log"),LEFT,AFTER+10);
		add(m_btStopLog=new Button("Stop Log"),RIGHT,SAME);
	}
	
	
	/* (non-Javadoc)
	 * @see waba.ui.Control#onEvent(waba.ui.Event)
	 * TODO : Make filename configureable.
	 */
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		super.onEvent(event);
		switch (event.type) {
		case ControlEvent.PRESSED:
			if (event.target==m_btGetLog) {
				// TODO: Get start log nbr and end log nbr to get
				// actual data from dates.
				//m_edStartDate;
				//m_edEndDate;
				//  				m_GPSstate.getLogInit(0,1000,100,m_cbFile.getSelectedItem()+"Test.txt");
				m_GPSstate.getLogInit(0,1000,100,"/Palm/BT747log.bin");
				m_btGetLog.press(false);
				
			} else if (event.target==m_btStopLog) {
				m_GPSstate.stopLog();
			}
		}
	}
}
