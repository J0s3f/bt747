import waba.sys.Settings;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.MainWindow;
import waba.ui.MenuBar;
import waba.ui.MessageBox;
import waba.ui.ProgressBar;
import waba.ui.TabPanel;
import waba.ui.Window;


public class BT747 extends MainWindow {
	private static MenuBar    m_MenuBar;
	private static TabPanel   m_TabPanel;
	private static GPSLogCtrl m_GPSLogCtrl;
	private static GPSLogInfo m_GPSLogInfo;
	private static GPSLogGet m_GPSLogGet;
	private static GPSconctrl m_GPSconctrl;
	private static GPSstate    m_GPSstate;
	private static ProgressBar m_ProgressBar;
	private static Label m_ProgressLabel;
	
	final static int C_SETTINGS_PORT_IDX = 0;  // 4 characters (hex)
	
	private static Settings m_Settings;
	
	
	String menu[][] = {
			{"File","Exit application"},
			{"Settings","Restart connection","Stop connection"},
			{"Info","About BT747","About SuperWaba VM","Info"}
	};
	static final int C_MENU_FILE_EXIT = 001;
	
	static final int C_MENU_RESTART_CONNECTION = 101;
	static final int C_MENU_STOP_CONNECTION = 102;
	
	static final int C_MENU_ABOUT = 201;
	static final int C_MENU_ABOUT_SW = 202;
	static final int C_MENU_INFO = 203;   
	
	String c_tpCaptions[]= {
			"Log FMT","Log Info","Log Get","Conn"
	};
	
	public BT747() {
		if(Settings.appSettings==null) {
			Settings.appSettings=new String("00005555000000000000");
			//                               12345678901234567890
		}
		setDoubleBuffer(true);
		setBorderStyle(TAB_ONLY_BORDER);
		setTitle("BT747");
		Settings.setUIStyle(Settings.Flat);
	}
	
	private updateLogFormatThread updateLogThread;
	
	public void onStart() {
		super.onStart();

		m_GPSstate=new GPSstate();
		setMenuBar(m_MenuBar=new MenuBar(menu));

		add(m_TabPanel=new TabPanel(c_tpCaptions),CENTER,CENTER);
		// Progress bar to show download progress (separate thread)
		m_ProgressLabel=new Label("Download");
		m_ProgressBar=new ProgressBar();
		add(m_ProgressLabel,LEFT,BOTTOM);
		m_ProgressLabel.setRect(LEFT,BOTTOM,PREFERRED,PREFERRED);
		//m_ProgressBar.setVisible(false);
		//m_ProgressLabel.setVisible(false);
		m_TabPanel.setBorderStyle(Window.NO_BORDER);
		m_TabPanel.setRect(getClientRect().modifiedBy(0,0,0,-m_ProgressBar.getPreferredHeight()));
		m_TabPanel.setPanel(0,m_GPSLogCtrl = new GPSLogCtrl(m_GPSstate));
		m_TabPanel.setPanel(1,m_GPSLogInfo = new GPSLogInfo(m_GPSstate));
		m_TabPanel.setPanel(2,m_GPSLogGet = new GPSLogGet(m_GPSstate));
		m_TabPanel.setPanel(3,m_GPSconctrl = new GPSconctrl(m_GPSstate));
		updateLogThread=new updateLogFormatThread(m_GPSLogCtrl,m_GPSstate);
		addThread(updateLogThread,false);
		//		m_TabPanel.setPanel(1,dataEdit = new dataEdit());
		//		m_TabPanel.setPanel(2,grid = new Grid(gridCaptions,false));
		//		SerialPort sp;
		//		sp=new SerialPort(4,9600);
		//			byte[] buf = {'H','e','l','l','o' };
		add(m_ProgressBar,RIGHT,SAME);	
		m_ProgressBar.setRect(RIGHT,BOTTOM,//BOTTOM,RIGHT,
				getClientRect().width-m_ProgressLabel.getRect().width-2,
				PREFERRED);
//		m_ProgressBar.setRect(m_ProgressLabel.getRect().x2(),m_ProgressLabel.getRect().y,//BOTTOM,RIGHT,
//				10,//getClientRect().width-m_ProgressLabel.getRect().width,
//				10+0*PREFERRED);
	
		
		//			sp.writeBytes(buf,0,1);
	}
	
	public void InterceptSystemKeys(){		
		//Vm.interceptSystemKeys(Vm.SK_ALL | Vm.SK_LAUNCH);		
		waba.sys.Vm.interceptSystemKeys(		
				//WinCE
				waba.sys.Vm.SK_HARD1 | 
				waba.sys.Vm.SK_HARD2 | 
				waba.sys.Vm.SK_HARD3 | 
				waba.sys.Vm.SK_HARD4 | 
				waba.sys.Vm.SK_PAGE_DOWN | 
				waba.sys.Vm.SK_PAGE_LEFT | 
				waba.sys.Vm.SK_PAGE_RIGHT | 
				waba.sys.Vm.SK_PAGE_UP|		
				//additional palmOS silkscreened keys		
				waba.sys.Vm.SK_SYNC | // Hotsync
				waba.sys.Vm.SK_FIND | // Search/Find
				waba.sys.Vm.SK_CALC | // Calculator
				waba.sys.Vm.SK_LAUNCH);//Home
	}	
	
	
	public void onEvent(Event event) {
		//
		switch (event.type) {
		case ControlEvent.WINDOW_CLOSED:
			if (event.target==menubar) {
				switch (m_MenuBar.getSelectedMenuItem()) {
				case C_MENU_FILE_EXIT:
					MessageBox mb;
					String []szExitButtonArray = {"Yes","No"};
					mb = new MessageBox("Attention",
							"You are about to exit the application|" +
							"Confirm application exit?",
							szExitButtonArray);				 					
					mb.popupBlockingModal();
					int idxExit = mb.getPressedButtonIndex();				 											
					if (mb.getPressedButtonIndex()==0){
						// Exit application
						MainWindow.getMainWindow().exit(0);
						break;
					}
					// Back to application
					break;
				case C_MENU_RESTART_CONNECTION:
					m_GPSstate.GPS_restart();
					break;
				case C_MENU_STOP_CONNECTION:
					m_GPSstate.GPS_close();
					break;
				case C_MENU_ABOUT:
					new MessageBox("About BT747",
							"Created with SuperWaba|" +
							"|http://www.superwaba.org"+
							"|"+
							"|This application allows control of" +
							"|the BT747 device." +
							"|To have full control over bluetooth" +
							"|it is necessary to apply a hardware" +
							"|hack.  Please see the web for more" +
							"|information." +
							"|" +
							"|Written by Mario De Weerd" +
							"|m.deweerd@ieee.org").popupModal();
					break;              
				case C_MENU_ABOUT_SW:
					new MessageBox("About SuperWaba",
							"SuperWaba Virtual Machine "+ Settings.versionStr +
							"|Copyright (c)2000-2007" +
							"|Guilherme Campos Hazan" +
							"|www.superwaba.com|" +
							"|" +
							"SuperWaba is an enhanced version" +
							"|of the Waba Virtual Machine" +
							"|Copyright (c) 1998,1999 WabaSoft" +
							"|www.wabasoft.com").popupModal();
					break;                  
				case C_MENU_INFO:    					
					new MessageBox("Info",
							"TBD.").popupModal();
					break;              
					
				default: break;
				
				}
				
			}
		}
	}
	
	//        public void onPaint(Graphics g) {
	//
	//        }       
	
}
