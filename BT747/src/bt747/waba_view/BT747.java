package bt747.waba_view;
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

import gps.GPSstate;
import gps.GpsEvent;

import bt747.Txt;
import bt747.control.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.ui.MessageBox;

/** Main class (application entry)
 * 
 * @author Mario De Weerd
 */
public class BT747 extends MainWindow implements ModelListener {
    
    /*
     * Using Model, Controller, View.
     */
    protected Model m = new Model();
    protected Controller c= new Controller(m);
    
    /** The 'GPS state'.  Used to get current GPS information and get access
     * to it.
     */
    private static GPSstate    m_GPSstate;
    /** The label next to the progressbar.  Hidden when not in use. */
    private static Label       m_ProgressLabel;
    /** The progress bar itself.  Hidden when not in use. */
    private static ProgressBar pb;
    
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
                MenuBar.UNCHECKED+Txt.ADD_RECORD_NUMBER, 
                "-", 
                MenuBar.UNCHECKED+Txt.S_FOCUS_HIGHLIGHT, 
                "-", 
                MenuBar.UNCHECKED+Txt.S_DEBUG, 
                MenuBar.UNCHECKED+Txt.S_DEBUG_CONN, 
                MenuBar.UNCHECKED+Txt.S_STATS, 
                MenuBar.UNCHECKED+"Holux M241",
                MenuBar.UNCHECKED+Txt.S_IMPERIAL
            },
            {   Txt.S_INFO,
                Txt.S_ABOUT_BT747,
                Txt.S_ABOUT_SUPERWABA,
                Txt.S_INFO}    
    };
    /** MenuBar item for File->Exit */
    private static final int C_MENU_FILE_EXIT = 001;
    /** MenuBar item for Settings->Restart connection */
//    private static final int C_MENU_CONNECTION_SETTINGS = 101;
//    /** MenuBar item for Settings->Restart connection */
    private static final int C_MENU_RESTART_CONNECTION = 101;
    /** MenuBar item for Settings->Stop connection */
    private static final int C_MENU_STOP_CONNECTION = 102;
    /** MenuBar item for Settings->GPX UTC 0 */
    private static final int C_MENU_GPX_UTC0 = 104;
    /** MenuBar item for Settings->GPX Trk Sep when big only */
    private static final int C_MENU_GPX_TRKSEG_BIGONLY = 105;
    /** MenuBar item for Settings->GPS Decode Active*/
    private static final int C_MENU_GPS_DECODE_ACTIVE = 106;
    /** MenuBar item for Settings->Record number in logs*/
    private static final int C_MENU_RECORDNMBR_IN_LOGS = 107;
    /** MenuBar item for Settings->Debug */
    private static final int C_MENU_FOCUS_HIGHLIGHT = 109;
    /** MenuBar item for Settings->Debug */
    private static final int C_MENU_DEBUG_ACTIVE = 111;
    private static final int C_MENU_DEBUG_CONN = 112;
    /** MenuBar item for Settings->Conn. Stats */
    private static final int C_MENU_STATS_ACTIVE = 113;
    /** MenuBar item for Settings->Holux M-241 */
    private static final int C_MENU_HOLUX_241 = 114;
    /** MenuBar item for Settings->Imperial units */
    private static final int C_MENU_IMPERIAL = 115;
    /** MenuBar item for Info->About BT747 */
    private static final int C_MENU_ABOUT = 201;
    /** MenuBar item for Info->About Superwaba */
    private static final int C_MENU_ABOUT_SW = 202;
    /** MenuBar item for Info->Info */
    private static final int C_MENU_INFO = 203;   
    
    /** The tab panel */
    private static TabPanel    m_TabPanel;
    /** The captions for the tab panel */
    private final String c_tpCaptions[]= {
            Txt.C_FMT,
            Txt.C_CTRL,
            Txt.C_LOG,
            Txt.C_FILE,
            Txt.C_FLTR,
            Txt.C_EASY,
            Txt.C_CON,
            Txt.C_OTHR
    };
    /** Tab Panel container - Logger control/configuration */
    private static GPSLogFormat  m_GPSLogCtrl;
    private static final int C_LOG_CTRL_IDX= 0;
     
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
    
    private int orgAutoOnOff;
    
    /** Initialiser of the application
     */
    public BT747() {
        if(Settings.onDevice) {
            bt747.sys.Vm.debug(bt747.sys.Vm.ERASE_DEBUG);
        }
        orgAutoOnOff=waba.sys.Vm.setDeviceAutoOff(0); // Avoid auto-off causing BT trouble

        Txt.init();
        setDoubleBuffer(true);
        setBorderStyle(TAB_ONLY_BORDER);
        setTitle(Txt.S_TITLE); 
        Settings.setUIStyle(Settings.Flat);
    }
    
    private int numPanels;
    
    public void onStart() {
        super.onStart();
        
        m_GPSstate=new GPSstate(m);
        m_GPSstate.setEventPosterObject(new bt747.generic.EventPosterObject(this));
        setMenuBar(m_MenuBar=new MenuBar(menu));
        // Next line is for modeling a device for debug.
        // Doing this on the windows platform
        //       if (Settings.platform.equals("Java")) m_model= new BT747model();       
        //          sp.writeBytes(buf,0,1);
        m_MenuBar.setChecked(C_MENU_GPX_UTC0,m.getGpxUTC0());

        m_MenuBar.setChecked(C_MENU_GPX_TRKSEG_BIGONLY,m.getGpxTrkSegWhenBig());

        m_MenuBar.setChecked(C_MENU_GPS_DECODE_ACTIVE,m.getGpsDecode());
        m_GPSstate.setGpsDecode(m.getGpsDecode());

        m_MenuBar.setChecked(C_MENU_RECORDNMBR_IN_LOGS,m.getRecordNbrInLogs());
        m_MenuBar.setChecked(C_MENU_HOLUX_241,m.getForceHolux241());
        m_MenuBar.setChecked(C_MENU_HOLUX_241,m.getImperial());

        
        add(m_TabPanel=new TabPanel(c_tpCaptions),CENTER,CENTER);
        // Progress bar to show download progress (separate thread)
        m_ProgressLabel=new Label(Txt.LB_DOWNLOAD); 
        pb=new ProgressBar();
        add(m_ProgressLabel,LEFT,BOTTOM);
        m_ProgressLabel.setRect(LEFT,BOTTOM,PREFERRED,PREFERRED);
        m_ProgressLabel.setVisible(false);
        //m_ProgressLabel.setVisible(false);
        m_TabPanel.setBorderStyle(Window.NO_BORDER);
        m_TabPanel.setRect(getClientRect().modifiedBy(0,0,0,-pb.getPreferredHeight()));
        
        add(pb,RIGHT,SAME);  
        pb.setRect(RIGHT,BOTTOM,//BOTTOM,RIGHT,
                getClientRect().width-m_ProgressLabel.getRect().width-2,
                PREFERRED);
        pb.setVisible(false);

        numPanels=0;
        m_TabPanel.setPanel(C_LOG_CTRL_IDX,m_GPSLogCtrl = new GPSLogFormat(m_GPSstate));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_LOGINFO_IDX,m_GPSLogInfo = new GPSLogReason(m_GPSstate));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_LOGGET_IDX,m_GPSLogGet = new GPSLogGet(m_GPSstate,pb,m,c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_FILECTRL_IDX,m_GPSLogFile = new GPSLogFile(m));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_FILTERCTRL_IDX,m_GPSFiltersTabPanel = new GPSFiltersTabPanel(m,c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_EASYCTRL_IDX,m_GPSLogEasy = new GPSLogEasy(m_GPSstate, m));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_CONCTRL_IDX,m_GPSconctrl = new GPSconctrl(m_GPSstate,m));
        numPanels++;
        //m_TabPanel.setPanel(C_GPS_FLASH_IDX,m_GPSFlash = new GPSFlashOption(m_GPSstate));
        //C_NUM_PANELS++;
        m_TabPanel.setPanel(C_GPS_FLASH_IDX,new GPSOtherTabPanel(m_GPSstate, m));
        numPanels++;
        //		m_TabPanel.setPanel(1,dataEdit = new dataEdit());
        //		m_TabPanel.setPanel(2,grid = new Grid(gridCaptions,false));
        //		SerialPort sp;
        //		sp=new SerialPort(4,9600);
        //			byte[] buf = {'H','e','l','l','o' };
        //		m_ProgressBar.setRect(m_ProgressLabel.getRect().x2(),m_ProgressLabel.getRect().y,//BOTTOM,RIGHT,
        //				10,//getClientRect().width-m_ProgressLabel.getRect().width,
        //				10+0*PREFERRED);
        m_TabPanel.setActiveTab(C_GPS_CONCTRL_IDX);

        Settings.keyboardFocusTraversable = m.isTraversableFocus();

        m.addListener((ModelListener)this);
        addTimer(this, 55);

    }
    
    public void onEvent(Event event) {
        //
        //if(event.type>9999) { Vm.debug("EventB:"+event.type+" "+event.consumed);  }
        switch (event.type) {
        case ControlEvent.TIMER:
            if ((topMost==this)&&m.isSolveMacLagProblem() &&  event.target==this) {
                this._doPaint();
            }
            break;
        case ControlEvent.WINDOW_CLOSED:
            if (event.target==menubar) {
                switch (m_MenuBar.getSelectedMenuItem()) {
                case -1:
                    break; // No item selected
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
                    m.setTraversableFocus(m_MenuBar.isChecked(C_MENU_FOCUS_HIGHLIGHT));
                    Settings.keyboardFocusTraversable = m.isTraversableFocus();
                    break;
                case C_MENU_DEBUG_ACTIVE:
                    m_GPSstate.setDebug(m_MenuBar.isChecked(C_MENU_DEBUG_ACTIVE));
                    break;
                case C_MENU_DEBUG_CONN:
                    m_GPSstate.setDebugConn(m_MenuBar.isChecked(C_MENU_DEBUG_CONN));
                    break;
                case C_MENU_STATS_ACTIVE:
                    m_GPSstate.setStats(m_MenuBar.isChecked(C_MENU_STATS_ACTIVE));
                    break;
                case C_MENU_HOLUX_241:
                    m.setForceHolux241(m_MenuBar.isChecked(C_MENU_HOLUX_241));
                    break;
                case C_MENU_IMPERIAL:
                    c.setImperial(m_MenuBar.isChecked(C_MENU_IMPERIAL));
                case C_MENU_GPX_UTC0:
                    m.setGpxUTC0(m_MenuBar.isChecked(C_MENU_GPX_UTC0));
                    break;
                case C_MENU_GPX_TRKSEG_BIGONLY:
                    m.setGpxTrkSegWhenBig(m_MenuBar.isChecked(C_MENU_GPX_TRKSEG_BIGONLY));
                    break;
                case C_MENU_GPS_DECODE_ACTIVE:
                    m.setGpsDecode(m_MenuBar.isChecked(C_MENU_GPS_DECODE_ACTIVE));
                    m_GPSstate.setGpsDecode(m.getGpsDecode());
                    break;
                case C_MENU_RECORDNMBR_IN_LOGS:
                    m.setRecordNbrInLogs(m_MenuBar.isChecked(C_MENU_GPS_DECODE_ACTIVE));
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
                c=m_TabPanel.getPanel(m_TabPanel.getActiveTab());
                c.postEvent(new Event(ControlEvent.PRESSED,c,0));                
            }
            break;
        default:
            if(event.target==null) {
                if(event.type==GpsEvent.DATA_UPDATE) {
                    Control c;
                    c=m_TabPanel.getPanel(m_TabPanel.getActiveTab());
                    c.postEvent(new Event(GpsEvent.DATA_UPDATE,c,0));
                    event.consumed=true;
                } else if (
                        (event.type==GpsEvent.GPRMC)
                      ||(event.type==GpsEvent.GPGGA)) {

                    Control c;
                    c=m_TabPanel.getPanel(m_TabPanel.getActiveTab());
                    event.target=c;
                    c.postEvent(event);
                    event.consumed=true;
                } else if ((event.type==GpsEvent.CONNECTED) ) {
                    m_TabPanel.setActiveTab(C_GPS_LOGGET_IDX);
                    event.consumed=true;
                }
            }
            if(event.target==this) {
                if(event.type==ModelEvent.DOWNLOAD_PROGRESS_UPDATE) {
                    if (pb != null) {
                        pb.min = m.getStartAddr();
                        pb.max = m.getEndAddr();
                        pb.setValue(m.getNextReadAddr(), "", " b");
                        pb.setVisible(m.isDownloadOnGoing());
                        m_ProgressLabel.setVisible(m.isDownloadOnGoing());
                    }
                    event.consumed=true;
                } else {
                    for (int i = 0; i<numPanels; i++) {
                        m_TabPanel.getPanel(i).onEvent(event);    
                    }
                }
            }
        }
    }
    
    public void newEvent(bt747.ui.Event e) {
        postEvent(e);
        
    }
    
    
    public void onExit() {
        m.saveSettings();
        waba.sys.Vm.setDeviceAutoOff(orgAutoOnOff); // Avoid auto-off causing BT trouble
    }
}
