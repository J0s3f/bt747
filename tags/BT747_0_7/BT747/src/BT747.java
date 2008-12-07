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
import waba.sys.Convert;
import waba.sys.Settings;
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.MainWindow;
import waba.ui.MenuBar;
import waba.ui.MessageBox;
import waba.ui.ProgressBar;
import waba.ui.TabPanel;
import waba.ui.Window;

import gps.GPSFilter;
import gps.GPSconctrl;
import gps.GPSstate;

/** Main class (application entry)
 * 
 * @author Mario De Weerd
 */
public class BT747 extends MainWindow {
    /** The 'GPS state'.  Used to get current GPS information and get access
     * to it.
     */
    private static GPSstate    m_GPSstate;
    /** The label next to the progressbar.  Hidden when not in use. */
    private static Label       m_ProgressLabel;
    /** The progress bar itself.  Hidden when not in use. */
    private static ProgressBar m_ProgressBar;
    
    //private BT747model m_model;
    /** The application's MenuBar */
    private static MenuBar     m_MenuBar;
    /** The content of the menu bar */
    private final String menu[][] = {
            {"File","Exit application"},
            {"Settings","Restart connection","Stop connection"},
            {"Info","About BT747","About SuperWaba VM","Info"}
    };
    /** MenuBar item for File->Exit */
    private final static int C_MENU_FILE_EXIT = 001;
    /** MenuBar item for Settings->Restart connection */
    private final static int C_MENU_RESTART_CONNECTION = 101;
    /** MenuBar item for Settings->Stop connection */
    private final static int C_MENU_STOP_CONNECTION = 102;
    /** MenuBar item for Info->About BT747 */
    private final static int C_MENU_ABOUT = 201;
    /** MenuBar item for Info->About Superwaba */
    private final static int C_MENU_ABOUT_SW = 202;
    /** MenuBar item for Info->Info */
    private final static int C_MENU_INFO = 203;   
    
    /** The tab panel */
    private static TabPanel    m_TabPanel;
    /** The captions for the tab panel */
    private final String c_tpCaptions[]= {
            "Log FMT","Ctrl","Log","File","Filter","Conn",
    };
    /** Tab Panel container - Logger control/configuration */
    private static GPSLogFormat  m_GPSLogCtrl;
    private static int C_LOG_CTRL_IDX= 0;
    
    private GPSFilter m_GPSFilter=new GPSFilter();
 
    /** Tab Panel container - Log information */
    private static GPSLogReason  m_GPSLogInfo;
    /** Tab Panel container - Log retrieval */
    private static GPSLogGet   m_GPSLogGet;
    /** Tab Panel container - Log file settings (name,...) */
    private static GPSLogFile  m_GPSLogFile;
    private static int C_GPS_FILECTRL_IDX= 3;
    /** Tab Panel container - Log filter settings (other than date)*/
    private static GPSLogFilter m_GPSLogFilter;
    private static int C_GPS_FILTERCTRL_IDX= 4;
    /** Tab Panel container - Connection control/configuration */
    private static GPSconctrl  m_GPSconctrl;
    private static int C_GPS_CONCTRL_IDX= 5;
    
    // TODO: Settings should just refer to function and not need to be instantiated
    private static AppSettings m_settings=new AppSettings();
    
    /** Initialiser of the application
     */
    public BT747() {
        // TODO: Initialise settings in AppSettings class
        if(Settings.appSettings==null) {
            Settings.appSettings="00005555000000000000";
            //                               12345678901234567890
        }
        setDoubleBuffer(true);
        setBorderStyle(TAB_ONLY_BORDER);
        setTitle("BT747");
        Settings.setUIStyle(Settings.Flat);
    }
    
    public void onStart() {
        super.onStart();
        
        m_GPSstate=new GPSstate(m_settings);
        m_GPSstate.setEventPosterObject(this);
        setMenuBar(m_MenuBar=new MenuBar(menu));
        
        add(m_TabPanel=new TabPanel(c_tpCaptions),CENTER,CENTER);
        // Progress bar to show download progress (separate thread)
        m_ProgressLabel=new Label("Download");
        m_ProgressBar=new ProgressBar();
        add(m_ProgressLabel,LEFT,BOTTOM);
        m_ProgressLabel.setRect(LEFT,BOTTOM,PREFERRED,PREFERRED);
        m_ProgressLabel.setVisible(false);
        //m_ProgressLabel.setVisible(false);
        m_TabPanel.setBorderStyle(Window.NO_BORDER);
        m_TabPanel.setRect(getClientRect().modifiedBy(0,0,0,-m_ProgressBar.getPreferredHeight()));
        
        add(m_ProgressBar,RIGHT,SAME);  
        m_ProgressBar.setRect(RIGHT,BOTTOM,//BOTTOM,RIGHT,
                getClientRect().width-m_ProgressLabel.getRect().width-2,
                PREFERRED);
        m_ProgressBar.setVisible(false);
        
        m_TabPanel.setPanel(C_LOG_CTRL_IDX,m_GPSLogCtrl = new GPSLogFormat(m_GPSstate));
        m_TabPanel.setPanel(1,m_GPSLogInfo = new GPSLogReason(m_GPSstate));
        m_TabPanel.setPanel(2,m_GPSLogGet = new GPSLogGet(m_GPSstate,m_ProgressBar,m_settings,m_GPSFilter));
        m_TabPanel.setPanel(C_GPS_FILECTRL_IDX,m_GPSLogFile = new GPSLogFile(m_settings));
        m_TabPanel.setPanel(C_GPS_FILTERCTRL_IDX,m_GPSLogFilter = new GPSLogFilter(m_GPSFilter));
        m_TabPanel.setPanel(C_GPS_CONCTRL_IDX,m_GPSconctrl = new GPSconctrl(m_GPSstate));
        //		m_TabPanel.setPanel(1,dataEdit = new dataEdit());
        //		m_TabPanel.setPanel(2,grid = new Grid(gridCaptions,false));
        //		SerialPort sp;
        //		sp=new SerialPort(4,9600);
        //			byte[] buf = {'H','e','l','l','o' };
        //		m_ProgressBar.setRect(m_ProgressLabel.getRect().x2(),m_ProgressLabel.getRect().y,//BOTTOM,RIGHT,
        //				10,//getClientRect().width-m_ProgressLabel.getRect().width,
        //				10+0*PREFERRED);
        m_TabPanel.setActiveTab(C_GPS_CONCTRL_IDX);
        m_GPSstate.setProgressBar(m_ProgressBar);
        
        // Next line is for modeling a device for debug.
        // Doing this on the windows platform
        // TODO: Remove for production version
        //		 if (Settings.platform.equals("Java")) m_model= new BT747model();		
        //			sp.writeBytes(buf,0,1);
        
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
                            "Created with SuperWaba" +
                            "|http://www.superwaba.org"+
                            "|" +Convert.toString(Version.BUILD)+
                            "|Written by Mario De Weerd" +
                            "|m.deweerd@ieee.org"+
                            "|"+
                            "|This application allows control of" +
                            "|the BT747 device." +
                            "|To have full control over bluetooth" +
                            "|it is necessary to apply a hardware" +
                            "|hack.  Please see the web for more" +
                            "|information."
                    ).popupModal();
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
                    new MessageBox(
                            "Disclaimer",
                            "Software is provided 'AS IS,' without" +
                            "|a warranty of any kind. ALL EXPRESS" +
                            "|OR IMPLIED REPRESENTATIONS AND " +
                            "|WARRANTIES, INCLUDING ANY IMPLIED" +
                            "|WARRANTY OF MERCHANTABILITY," +
                            "|FITNESS FOR A PARTICULAR PURPOSE" +
                            "|OR NON-INFRINGEMENT, ARE HEREBY" +
                            "|EXCLUDED. THE ENTIRE RISK ARISING " +
                            "|OUT OF USING THE SOFTWARE IS" +
                            "|ASSUMED BY THE USER. See the" +
                            "|GNU General Public License for more" +
                            "|details."
                    ).popupModal();
                    break;              
                    
                    
                default: break;
                
                }
                
            }
            break;
        case ControlEvent.PRESSED:
            if(event.target==m_TabPanel) {
                Control c;
                c=m_TabPanel.getChildren()[0];
                c.postEvent(new Event(ControlEvent.PRESSED,c,0));                
            }
            break;
        case ControlEvent.TIMER:
            if(event.target==this) {
                /* There is no timer here, set by a child (m_GPSstate) to
                 *  indicate that status has been retrieved */
                Control c;
                c=m_TabPanel.getChildren()[0];
                c.postEvent(new Event(ControlEvent.PRESSED,null,0));
                event.consumed=true;
            }
            break;
        }
    }
    
    //        public void onPaint(Graphics g) {
    //
    //        }       
    
}