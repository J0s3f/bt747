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
import waba.fx.Font;
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.MainWindow;
import waba.ui.MenuBar;
import waba.ui.MenuItem;
import waba.ui.ProgressBar;
import waba.ui.TabPanel;
import waba.ui.Window;

import gps.GPSListener;
import gps.GpsEvent;

import bt747.Txt;
import bt747.control.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Settings;
import bt747.ui.MessageBox;

/**
 * Main class (application entry)
 * 
 * @author Mario De Weerd
 */
public class BT747 extends MainWindow implements ModelListener,GPSListener {

    /*
     * Using Model, Controller, View.
     */
    protected Model m = new Model();
    protected Controller c= new Controller(m);

    /**
     * The 'GPS state'. Used to get current GPS information and get access to
     * it.
     */
    //private GPSstate    m_GPSstate;
    /** The label next to the progressbar. Hidden when not in use. */
    private Label       m_ProgressLabel;
    /** The progress bar itself. Hidden when not in use. */
    private ProgressBar pb;

    // private BT747model m_model;
    /** The application's MenuBar */
    private MenuBar     m_MenuBar;
    /** The content of the menu bar */

    private MenuItem miFile = new MenuItem(Txt.S_FILE);
    private MenuItem miExitApplication = new MenuItem(Txt.S_EXIT_APPLICATION);  
    private MenuItem miSettings = new MenuItem(Txt.S_SETTINGS);
    private MenuItem miRestartConnection = new MenuItem(Txt.S_RESTART_CONNECTION);
    private MenuItem miStopConnection = new MenuItem(Txt.S_STOP_CONNECTION);
    private MenuItem miGpxUTC0 = new MenuItem(Txt.S_GPX_UTC_OFFSET_0,false);
    private MenuItem miGpxTrkSegWhenBig = new MenuItem(Txt.S_GPX_TRKSEG_WHEN_SMALL,false); 
    private MenuItem miGpsDecode = new MenuItem(Txt.S_GPS_DECODE_ACTIVE,false); 
    private MenuItem miRecordNumberInLogs = new MenuItem(Txt.ADD_RECORD_NUMBER,false); 
    private MenuItem miTraversableFocus = new MenuItem(Txt.S_FOCUS_HIGHLIGHT,false); 

    private MenuItem miDebug = new MenuItem(Txt.S_DEBUG,false);
    private MenuItem miDebugConn = new MenuItem(Txt.S_DEBUG_CONN,false); 
    private MenuItem miStats = new MenuItem(Txt.S_STATS,false); 
    private MenuItem miHolux = new MenuItem("Holux M241",false);
    private MenuItem miImperial = new MenuItem(Txt.S_IMPERIAL,false);
    private MenuItem miInfo = new MenuItem(Txt.S_INFO);
    private MenuItem miAboutBT747 = new MenuItem(Txt.S_ABOUT_BT747);
    private MenuItem miAboutSuperWaba = new MenuItem(Txt.S_ABOUT_SUPERWABA);


    private final MenuItem[][] menu = {
            {   miFile,
                miExitApplication
            },  
            {
                miSettings,
                miRestartConnection,
                miStopConnection,
                new MenuItem(), 
                miGpxUTC0, 
                miGpxTrkSegWhenBig, 
                miGpsDecode, 
                miRecordNumberInLogs, 
                new MenuItem(), 
                miTraversableFocus, 
                new MenuItem(), 
                miDebug, 
                miDebugConn, 
                miStats, 
                miHolux,
                miImperial,
            },
            {   miInfo,
                miAboutBT747,
                miAboutSuperWaba,
                miInfo
            }    
    };
    /** MenuBar item for File->Exit */
    private static final int C_MENU_FILE_EXIT = 001;
    /** MenuBar item for Settings->Restart connection */
//  private static final int C_MENU_CONNECTION_SETTINGS = 101;
//  /** MenuBar item for Settings->Restart connection */
    private static final int C_MENU_RESTART_CONNECTION = 101;
    /** MenuBar item for Settings->Stop connection */
    private static final int C_MENU_STOP_CONNECTION = 102;
    /** MenuBar item for Settings->GPX UTC 0 */
    private static final int C_MENU_GPX_UTC0 = 104;
    /** MenuBar item for Settings->GPX Trk Sep when big only */
    private static final int C_MENU_GPX_TRKSEG_BIGONLY = 105;
    /** MenuBar item for Settings->GPS Decode Active */
    private static final int C_MENU_GPS_DECODE_ACTIVE = 106;
    /** MenuBar item for Settings->Record number in logs */
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
    private TabPanel m_TabPanel;
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
    private static final int C_LOG_CTRL_IDX= 0;
    private static final int C_GPS_LOGINFO_IDX= 1;    
    private static final int C_GPS_LOGGET_IDX= 2;
    private static final int C_GPS_FILECTRL_IDX= 3;
    private static final int C_GPS_FILTERCTRL_IDX= 4;
    private static final int C_GPS_EASYCTRL_IDX= 5;
    private static final int C_GPS_CONCTRL_IDX= 6;
    /** Tab Panel container - Other settings */
    private static final int C_GPS_FLASH_IDX= 7;

    private int orgAutoOnOff;

    /**
     * Initialiser of the application
     */
    public BT747() {
        if(Settings.onDevice) {
            bt747.sys.Vm.debug(bt747.sys.Vm.ERASE_DEBUG);
        }
        orgAutoOnOff=waba.sys.Vm.setDeviceAutoOff(0); // Avoid auto-off
        // causing BT trouble

        /**
         * 
         */
        if(Txt.fontFile!=null) {
            MainWindow.defaultFont = Font.getFont(Txt.fontFile, false, 12);
            MainWindow.getMainWindow().setTitleFont(MainWindow.defaultFont);
        }
        if(Txt.encoding!=null) {
            waba.sys.Convert.setDefaultConverter(Txt.encoding);
        }

        setDoubleBuffer(true);
        setBorderStyle(TAB_ONLY_BORDER);
        setTitle(Txt.S_TITLE); 
        waba.sys.Settings.setUIStyle(waba.sys.Settings.Flat);
    }

    private int numPanels;

    public void onStart() {
        super.onStart();

        if(Settings.version<Settings.requiredVersion) {
            new MessageBox(
                    Txt.TITLE_ATTENTION,
                    Txt.BAD_SUPERWABAVERSION).popupBlockingModal();
            MainWindow.getMainWindow().exit(0);
        }

        //m_GPSstate=m.gpsModel();
        setMenuBar(m_MenuBar=new MenuBar(menu));
        // Next line is for modeling a device for debug.
        // Doing this on the windows platform
        // if (Settings.platform.equals("Java")) m_model= new BT747model();
        // sp.writeBytes(buf,0,1);
        miGpxUTC0.isChecked=m.getGpxUTC0();

        miGpxTrkSegWhenBig.isChecked=m.getGpxTrkSegWhenBig();

        miGpsDecode.isChecked=m.getGpsDecode();

        miRecordNumberInLogs.isChecked=m.getRecordNbrInLogs();
        miHolux.isChecked=m.getForceHolux241();
        miImperial.isChecked=m.getImperial();


        add(m_TabPanel=new TabPanel(c_tpCaptions),CENTER,CENTER);
        // Progress bar to show download progress (separate thread)
        m_ProgressLabel=new Label(Txt.LB_DOWNLOAD); 
        pb=new ProgressBar();
        add(m_ProgressLabel,LEFT,BOTTOM);
        m_ProgressLabel.setRect(LEFT,BOTTOM,PREFERRED,PREFERRED);
        m_ProgressLabel.setVisible(false);
        // m_ProgressLabel.setVisible(false);
        m_TabPanel.setBorderStyle(Window.NO_BORDER);
        m_TabPanel.setRect(getClientRect().modifiedBy(0,0,0,-pb.getPreferredHeight()));

        add(pb,RIGHT,SAME);  
        pb.setRect(RIGHT,BOTTOM,// BOTTOM,RIGHT,
                getClientRect().width-m_ProgressLabel.getRect().width-2,
                PREFERRED);
        updateProgressBar();

        numPanels=0;
        m_TabPanel.setPanel(C_LOG_CTRL_IDX,new GPSLogFormat(m,c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_LOGINFO_IDX,new GPSLogReason(c,m));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_LOGGET_IDX,new GPSLogGet(m,c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_FILECTRL_IDX,new GPSLogFile(c,m));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_FILTERCTRL_IDX,new GPSFiltersTabPanel(m,c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_EASYCTRL_IDX,new GPSLogEasy(m,c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_CONCTRL_IDX,new GPSconctrl(c,m));
        numPanels++;
        // m_TabPanel.setPanel(C_GPS_FLASH_IDX,m_GPSFlash = new
        // GPSFlashOption(m_GPSstate));
        // C_NUM_PANELS++;
        m_TabPanel.setPanel(C_GPS_FLASH_IDX,new GPSOtherTabPanel(c,m));
        numPanels++;
        // m_TabPanel.setPanel(1,dataEdit = new dataEdit());
        // m_TabPanel.setPanel(2,grid = new Grid(gridCaptions,false));
        // SerialPort sp;
        // sp=new SerialPort(4,9600);
        // byte[] buf = {'H','e','l','l','o' };
        // m_ProgressBar.setRect(m_ProgressLabel.getRect().x2(),m_ProgressLabel.getRect().y,//BOTTOM,RIGHT,
        // 10,//getClientRect().width-m_ProgressLabel.getRect().width,
        // 10+0*PREFERRED);
        m_TabPanel.setActiveTab(C_GPS_CONCTRL_IDX);

        waba.sys.Settings.keyboardFocusTraversable = m.isTraversableFocus();
        miTraversableFocus.isChecked=m.isTraversableFocus();

        m.addListener((ModelListener)this);
        c.addGPSListener(this);
        addTimer(this, 55);

    }

    public void onEvent(Event event) {
        //
        // if(event.type>9999) { Vm.debug("EventB:"+event.type+"
        // "+event.consumed); }
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
                    c.GPS_restart();
                    break;
                case C_MENU_STOP_CONNECTION:
                    c.GPS_close();
                    break;
                case C_MENU_FOCUS_HIGHLIGHT:
                    c.setTraversableFocus(miTraversableFocus.isChecked);
                    waba.sys.Settings.keyboardFocusTraversable = m.isTraversableFocus();
                    break;
                case C_MENU_DEBUG_ACTIVE:
                    c.setDebug(miDebug.isChecked);
                    break;
                case C_MENU_DEBUG_CONN:
                    c.setDebugConn(miDebugConn.isChecked);
                    break;
                case C_MENU_STATS_ACTIVE:
                    c.setStats(miStats.isChecked);
                    break;
                case C_MENU_HOLUX_241:
                    c.setForceHolux241(miHolux.isChecked);
                    break;
                case C_MENU_IMPERIAL:
                    c.setImperial(miImperial.isChecked);
                    break;
                case C_MENU_GPX_UTC0:
                    c.setGpxUTC0(miGpxUTC0.isChecked);
                    break;
                case C_MENU_GPX_TRKSEG_BIGONLY:
                    c.setGpxTrkSegWhenBig(miGpxTrkSegWhenBig.isChecked);
                    break;
                case C_MENU_GPS_DECODE_ACTIVE:
                    c.setGpsDecode(miGpsDecode.isChecked);
                    break;
                case C_MENU_RECORDNMBR_IN_LOGS:
                    m.setRecordNbrInLogs(miRecordNumberInLogs.isChecked);
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
            if(event.target==this) {
                if(event.type==ModelEvent.DOWNLOAD_PROGRESS_UPDATE) {
                    updateProgressBar();
                    event.consumed=true;
                } else {
                    for (int i = 0; i<numPanels; i++) {
                        m_TabPanel.getPanel(i).onEvent(event);    
                    }
                }
            }
        }
    }

    private void updateProgressBar() {
        if (pb != null) {
            pb.min = m.getStartAddr();
            pb.max = m.getEndAddr();
            pb.setValue(m.getNextReadAddr(), "", " b");
            pb.setVisible(m.isDownloadOnGoing());
            m_ProgressLabel.setVisible(m.isDownloadOnGoing());
        }
    }

    public final void newEvent(bt747.ui.Event event) {
        this.postEvent(event);
    }

    public void gpsEvent(GpsEvent event) {
        if(event.getType()==GpsEvent.DATA_UPDATE) {
            Control c;
            c=m_TabPanel.getPanel(m_TabPanel.getActiveTab());
            c.postEvent(new Event(GpsEvent.DATA_UPDATE,c,0));
        } else if (
                (event.getType()==GpsEvent.GPRMC)
                ||(event.getType()==GpsEvent.GPGGA)) {

            Control c;
            c=m_TabPanel.getPanel(m_TabPanel.getActiveTab());
            event.target=c;
            c.postEvent(event);
        } else if ((event.getType()==GpsEvent.CONNECTED) ) {
            m_TabPanel.setActiveTab(C_GPS_LOGGET_IDX);
        }
    }


    public void onExit() {
        waba.sys.Vm.setDeviceAutoOff(orgAutoOnOff); // Avoid auto-off causing BT
        // trouble
        m.saveSettings();
    }
}
