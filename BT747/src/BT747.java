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
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  *********************************************************** ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************                              
import waba.sys.Settings;
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.MainWindow;
import waba.ui.MenuBar;
import waba.ui.ProgressBar;
import waba.ui.TabPanel;
import waba.ui.Window;

import gps.GPSFilter;
import gps.GPSFilterAdvanced;
import gps.GPSstate;
import gps.GpsEvent;

import bt747.Txt;
import bt747.ui.MessageBox;

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
            {   Txt.S_FILE,
                Txt.S_EXIT_APPLICATION},  
            {   Txt.S_SETTINGS,
                 Txt.S_RESTART_CONNECTION,
                 Txt.S_STOP_CONNECTION,
                "-", 
                MenuBar.UNCHECKED+Txt.S_GPX_UTC_OFFSET_0, 
                MenuBar.UNCHECKED+Txt.S_GPX_TRKSEG_WHEN_SMALL, 
                MenuBar.UNCHECKED+Txt.S_GPS_DECODE_ACTIVE, 
                "-", 
                MenuBar.UNCHECKED+Txt.S_FOCUS_HIGHLIGHT, 
                "-", 
                MenuBar.UNCHECKED+Txt.S_DEBUG, 
                MenuBar.UNCHECKED+Txt.S_STATS 
            },
            {   Txt.S_INFO,
                Txt.S_ABOUT_BT747,
                Txt.S_ABOUT_SUPERWABA,
                Txt.S_INFO}    
    };
    /** MenuBar item for File->Exit */
    private final static int C_MENU_FILE_EXIT = 001;
    /** MenuBar item for Settings->Restart connection */
//    private final static int C_MENU_CONNECTION_SETTINGS = 101;
//    /** MenuBar item for Settings->Restart connection */
    private final static int C_MENU_RESTART_CONNECTION = 101;
    /** MenuBar item for Settings->Stop connection */
    private final static int C_MENU_STOP_CONNECTION = 102;
    /** MenuBar item for Settings->GPX UTC 0 */
    private final static int C_MENU_GPX_UTC0 = 104;
    /** MenuBar item for Settings->GPX Trk Sep when big only */
    private final static int C_MENU_GPX_TRKSEG_BIGONLY = 105;
    /** MenuBar item for Settings->GPS Decode Active*/
    private final static int C_MENU_GPS_DECODE_ACTIVE = 106;
    /** MenuBar item for Settings->Debug */
    private final static int C_MENU_FOCUS_HIGHLIGHT = 108;
    /** MenuBar item for Settings->Debug */
    private final static int C_MENU_DEBUG_ACTIVE = 110;
    /** MenuBar item for Settings->Conn. Stats */
    private final static int C_MENU_STATS_ACTIVE = 111;
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
            "FMT","Ctrl","Log","File","Fltr","Easy","Con","Othr"        
    };
    /** Tab Panel container - Logger control/configuration */
    private static GPSLogFormat  m_GPSLogCtrl;
    private static final int C_LOG_CTRL_IDX= 0;
    
    private static final int C_NBR_FILTERS=2;
    private GPSFilter[] m_GPSFilter=new GPSFilter[C_NBR_FILTERS];
    private GPSFilterAdvanced[] m_GPSFilterAdv=new GPSFilterAdvanced[C_NBR_FILTERS];
 
    /** Tab Panel container - Log information */
    private static GPSLogReason  m_GPSLogInfo;
    private static final int C_GPS_LOGINFO_IDX= 1;    
    /** Tab Panel container - Log retrieval */
    private static GPSLogGet   m_GPSLogGet;
    private static final int C_GPS_LOGGET_IDX= 2;
    /** Tab Panel container - Log file settings (name,...) */
    private static GPSLogFile  m_GPSLogFile;
    private static final int C_GPS_FILECTRL_IDX= 3;
    /** Tab Panel container - Log filter settings (other than date)*/
    private static GPSFiltersTabPanel m_GPSFiltersTabPanel;
    private static final int C_GPS_FILTERCTRL_IDX= 4;
    /** Tab Panel container - Connection control/configuration */
    private static GPSLogEasy  m_GPSLogEasy;
    private static final int C_GPS_EASYCTRL_IDX= 5;
    /** Tab Panel container - Connection control/configuration */
    private static GPSconctrl  m_GPSconctrl;
    private static final int C_GPS_CONCTRL_IDX= 6;
    /** Tab Panel container - Other settings */
    private static final int C_GPS_FLASH_IDX= 7;
    
    private static AppSettings m_settings=new AppSettings();
    
    private int orgAutoOnOff;
    
    /** Initialiser of the application
     */
    public BT747() {
        if(Settings.onDevice) {
            waba.sys.Vm.debug(waba.sys.Vm.ERASE_DEBUG);
        }
        orgAutoOnOff=waba.sys.Vm.setDeviceAutoOff(0); // Avoid auto-off causing BT trouble

        setDoubleBuffer(true);
        setBorderStyle(TAB_ONLY_BORDER);
        setTitle(Txt.S_TITLE); 
        Settings.setUIStyle(Settings.Flat);
        Settings.keyboardFocusTraversable = m_settings.isTraversableFocus();
        for (int i = 0; i < m_GPSFilter.length; i++) {
            m_GPSFilter[i]=new GPSFilter();
            m_GPSFilterAdv[i]=new GPSFilterAdvanced();
        }
    }
    
    public void onStart() {
        super.onStart();
        
        m_GPSstate=new GPSstate(m_settings);
        m_GPSstate.setEventPosterObject(new bt747.generic.EventPosterObject(this));
        setMenuBar(m_MenuBar=new MenuBar(menu));

        add(m_TabPanel=new TabPanel(c_tpCaptions),CENTER,CENTER);
        // Progress bar to show download progress (separate thread)
        m_ProgressLabel=new Label(Txt.LB_DOWNLOAD); 
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
        m_TabPanel.setPanel(C_GPS_LOGINFO_IDX,m_GPSLogInfo = new GPSLogReason(m_GPSstate));
        m_TabPanel.setPanel(C_GPS_LOGGET_IDX,m_GPSLogGet = new GPSLogGet(m_GPSstate,m_ProgressBar,m_settings,m_GPSFilter,m_GPSFilterAdv));
        m_TabPanel.setPanel(C_GPS_FILECTRL_IDX,m_GPSLogFile = new GPSLogFile(m_settings));
        m_TabPanel.setPanel(C_GPS_FILTERCTRL_IDX,m_GPSFiltersTabPanel = new GPSFiltersTabPanel(m_settings, m_GPSFilter, m_GPSFilterAdv));
        m_TabPanel.setPanel(C_GPS_EASYCTRL_IDX,m_GPSLogEasy = new GPSLogEasy(m_GPSstate));
        m_TabPanel.setPanel(C_GPS_CONCTRL_IDX,m_GPSconctrl = new GPSconctrl(m_GPSstate,m_settings));
        //m_TabPanel.setPanel(C_GPS_FLASH_IDX,m_GPSFlash = new GPSFlashOption(m_GPSstate));
        m_TabPanel.setPanel(C_GPS_FLASH_IDX,new GPSOtherTabPanel(m_GPSstate, m_settings));
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
        //		 if (Settings.platform.equals("Java")) m_model= new BT747model();		
        //			sp.writeBytes(buf,0,1);
        m_MenuBar.setChecked(C_MENU_GPX_UTC0,m_settings.getGpxUTC0());

        m_MenuBar.setChecked(C_MENU_GPX_TRKSEG_BIGONLY,m_settings.getGpxTrkSegWhenBig());

        m_MenuBar.setChecked(C_MENU_GPS_DECODE_ACTIVE,m_settings.getGpsDecode());
        m_GPSstate.setGpsDecode(m_settings.getGpsDecode());
        
        
        addTimer(this, 55);

    }
    
    public void onEvent(Event event) {
        //
        switch (event.type) {
        case ControlEvent.TIMER:
            if ((topMost==this)&&m_settings.isSolveMacLagProblem() &&  event.target==this) {
                this._doPaint();
            }
            break;
        case ControlEvent.WINDOW_CLOSED:
            if (event.target==menubar) {
                switch (m_MenuBar.getSelectedMenuItem()) {
                case C_MENU_FILE_EXIT:
                    MessageBox mb;
                    String []szExitButtonArray = {Txt.YES,Txt.NO};  
                    mb = new MessageBox(
                            Txt.TITLE_ATTENTION,
                            Txt.CONFIRM_APP_EXIT, 
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
                case C_MENU_FOCUS_HIGHLIGHT:
                    m_settings.setTraversableFocus(m_MenuBar.isChecked(C_MENU_FOCUS_HIGHLIGHT));
                    Settings.keyboardFocusTraversable = m_settings.isTraversableFocus();
                    break;
                case C_MENU_DEBUG_ACTIVE:
                    m_GPSstate.setDebug(m_MenuBar.isChecked(C_MENU_DEBUG_ACTIVE));
                    break;
                case C_MENU_STATS_ACTIVE:
                    m_GPSstate.setStats(m_MenuBar.isChecked(C_MENU_STATS_ACTIVE));
                    break;
                case C_MENU_GPX_UTC0:
                    m_settings.setGpxUTC0(m_MenuBar.isChecked(C_MENU_GPX_UTC0));
                    break;
                case C_MENU_GPX_TRKSEG_BIGONLY:
                    m_settings.setGpxTrkSegWhenBig(m_MenuBar.isChecked(C_MENU_GPX_TRKSEG_BIGONLY));
                    break;
                case C_MENU_GPS_DECODE_ACTIVE:
                    m_settings.setGpsDecode(m_MenuBar.isChecked(C_MENU_GPS_DECODE_ACTIVE));
                    m_GPSstate.setGpsDecode(m_settings.getGpsDecode());
                    break;
                case C_MENU_ABOUT:
                    new MessageBox(Txt.ABOUT_TITLE,
                            Txt.ABOUT_TXT
                    ).popupModal();
                    break;              
                case C_MENU_ABOUT_SW:
                    new MessageBox(Txt.ABOUT_SUPERWABA_TITLE,
                            Txt.ABOUT_SUPERWABA_TXT).popupModal(); 
                    break;                  
                case C_MENU_INFO:    					
                    new MessageBox(
                            Txt.DISCLAIMER_TITLE,
                            Txt.DISCLAIMER_TXT
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
        case GpsEvent.DATA_UPDATE:
            if(event.target==null) {
                Control c;
                c=m_TabPanel.getChildren()[0];
                c.postEvent(new Event(GpsEvent.DATA_UPDATE,c,0));
                event.consumed=true;
            }
            break;
        case GpsEvent.GPRMC:
        case GpsEvent.GPGGA:
            if(event.target==null) {
                Control c;
                c=m_TabPanel.getChildren()[0];
                event.target=c;
                c.postEvent(event);
                event.consumed=true;
            }
            break;
        case GpsEvent.CONNECTED:
            m_TabPanel.setActiveTab(C_GPS_LOGGET_IDX);
            event.consumed=true;
        }
    }
    
    public void onExit() {
        m_settings.saveSettings();
        waba.sys.Vm.setDeviceAutoOff(orgAutoOnOff); // Avoid auto-off causing BT trouble
    }
}
