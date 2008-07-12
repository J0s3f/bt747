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
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Settings;
import bt747.sys.Vm;
import bt747.ui.MessageBox;

/**
 * Main class (application entry)
 * 
 * @author Mario De Weerd
 */
public class BT747 extends MainWindow implements ModelListener, GPSListener {

    /*
     * Using Model, Controller, View.
     */
    protected Model m = new Model();
    protected Controller c = new Controller(m);

    /**
     * The 'GPS state'. Used to get current GPS information and get access to
     * it.
     */
    // private GPSstate m_GPSstate;
    /** The label next to the progressbar. Hidden when not in use. */
    private Label progressLabel;
    /** The progress bar itself. Hidden when not in use. */
    private ProgressBar pb;

    // private BT747model m_model;
    /** The application's MenuBar. */
    private MenuBar menuBar;
    /** The content of the menu bar. */

    private MenuItem miFile = new MenuItem(Txt.S_FILE);
    private MenuItem miExitApplication = new MenuItem(Txt.S_EXIT_APPLICATION);
    private MenuItem miSettings = new MenuItem(Txt.S_SETTINGS);
    private MenuItem miRestartConnection = new MenuItem(
            Txt.S_RESTART_CONNECTION);
    private MenuItem miStopConnection = new MenuItem(Txt.S_STOP_CONNECTION);
    private MenuItem miGpxUTC0 = new MenuItem(Txt.S_GPX_UTC_OFFSET_0, false);
    private MenuItem miGpxTrkSegWhenBig = new MenuItem(
            Txt.S_GPX_TRKSEG_WHEN_SMALL, false);
    private MenuItem miGpsDecode = new MenuItem(Txt.S_GPS_DECODE_ACTIVE, false);
    private MenuItem miRecordNumberInLogs = new MenuItem(Txt.ADD_RECORD_NUMBER,
            false);
    private MenuItem miTraversableFocus = new MenuItem(Txt.S_FOCUS_HIGHLIGHT,
            false);

    private MenuItem miDebug = new MenuItem(Txt.S_DEBUG, false);
    private MenuItem miDebugConn = new MenuItem(Txt.S_DEBUG_CONN, false);
    private MenuItem miStats = new MenuItem(Txt.S_STATS, false);
    private MenuItem miImperial = new MenuItem(Txt.S_IMPERIAL, false);
    private MenuItem miOutputLogConditions = new MenuItem(
            Txt.S_OUTPUT_LOGCONDITIONS, false);

    private MenuItem miDevice = new MenuItem(Txt.S_DEVICE);
    private MenuItem miDefaultDevice = new MenuItem(Txt.S_DEFAULTDEVICE, true);
    private MenuItem miGisteqType1 = new MenuItem(Txt.S_GISTEQTYPE1, false);
    private MenuItem miGisteqType2 = new MenuItem(Txt.S_GISTEQTYPE2, false);
    private MenuItem miGisteqType3 = new MenuItem(Txt.S_GISTEQTYPE3, false);
    private MenuItem miHolux = new MenuItem("Holux M241", false);

    private MenuItem miInfo = new MenuItem(Txt.S_INFO);
    private MenuItem miAboutBT747 = new MenuItem(Txt.S_ABOUT_BT747);
    private MenuItem miAboutSuperWaba = new MenuItem(Txt.S_ABOUT_SUPERWABA);

    private final MenuItem[][] menu = {
            { miFile, miExitApplication },
            { miSettings, miRestartConnection, miStopConnection,
                    new MenuItem(), miGpxUTC0, miGpxTrkSegWhenBig, miGpsDecode,
                    miRecordNumberInLogs, new MenuItem(), miTraversableFocus,
                    new MenuItem(), miDebug, miDebugConn, miStats, miImperial,
                    miOutputLogConditions, },
            { miDevice, miDefaultDevice, miGisteqType1, miGisteqType2,
                    miGisteqType3, miHolux, },
            { miInfo, miAboutBT747, miAboutSuperWaba, miInfo } };
    /** MenuBar item for File->Exit */
    private static final int C_MENU_FILE_EXIT = 001;
    /** MenuBar item for Settings->Restart connection */
    // private static final int C_MENU_CONNECTION_SETTINGS = 101;
    // /** MenuBar item for Settings->Restart connection */
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
    /** MenuBar item for Settings->Imperial units */
    private static final int C_MENU_IMPERIAL = 114;
    /** MenuBar item for Settings->OutputLogConditions */
    private static final int C_MENU_OUTPUT_LOGCONDITIONS = 115;

    private static final int C_MENU_DEFAULTDEVICE = 201;
    private static final int C_MENU_GISTEQ_TYPE1 = 202;
    private static final int C_MENU_GISTEQ_TYPE2 = 203;
    private static final int C_MENU_GISTEQ_TYPE3 = 204;
    /** MenuBar item for Settings->Holux M-241 */
    private static final int C_MENU_HOLUX_241 = 205;

    /** MenuBar item for Info->About BT747 */
    private static final int C_MENU_ABOUT = 301;
    /** MenuBar item for Info->About Superwaba */
    private static final int C_MENU_ABOUT_SW = 302;
    /** MenuBar item for Info->Info */
    private static final int C_MENU_INFO = 303;

    /** The tab panel */
    private TabPanel m_TabPanel;
    /** The captions for the tab panel */
    private final String[] c_tpCaptions = { Txt.C_FMT, Txt.C_CTRL, Txt.C_LOG,
            Txt.C_FILE, Txt.C_FLTR, Txt.C_EASY, Txt.C_CON, Txt.C_OTHR };
    private static final int C_LOG_CTRL_IDX = 0;
    private static final int C_GPS_LOGINFO_IDX = 1;
    private static final int C_GPS_LOGGET_IDX = 2;
    private static final int C_GPS_FILECTRL_IDX = 3;
    private static final int C_GPS_FILTERCTRL_IDX = 4;
    private static final int C_GPS_EASYCTRL_IDX = 5;
    private static final int C_GPS_CONCTRL_IDX = 6;
    /** Tab Panel container - Other settings */
    private static final int C_GPS_FLASH_IDX = 7;

    private int orgAutoOnOff;

    /**
     * Initialiser of the application.
     */
    public BT747() {
        if (Settings.onDevice) {
            bt747.sys.Vm.debug(bt747.sys.Vm.ERASE_DEBUG);
        }
        orgAutoOnOff = waba.sys.Vm.setDeviceAutoOff(0); // Avoid auto-off
        // causing BT trouble

        /**
         * 
         */
        if (Txt.fontFile != null) {
            MainWindow.defaultFont = Font.getFont(Txt.fontFile, false, 12);
            MainWindow.getMainWindow().setTitleFont(MainWindow.defaultFont);
        }
        if (Txt.encoding != null) {
            waba.sys.Convert.setDefaultConverter(Txt.encoding);
        }

        setDoubleBuffer(true);
        setBorderStyle(TAB_ONLY_BORDER);
        setTitle(Txt.S_TITLE);
        waba.sys.Settings.setUIStyle(waba.sys.Settings.Flat);
        // Using the original decoder only in this interface:
        c.setBinDecoder(0);
    }

    private int numPanels;

    public void onStart() {
        super.onStart();

        if (Settings.version < Settings.requiredVersion) {
            new MessageBox(Txt.TITLE_ATTENTION, Txt.BAD_SUPERWABAVERSION)
                    .popupBlockingModal();
            MainWindow.getMainWindow().exit(0);
        }

        // m_GPSstate=m.gpsModel();
        menuBar = new MenuBar(menu);
        setMenuBar(menuBar);
        // Next line is for modeling a device for debug.
        // Doing this on the windows platform
        // if (Settings.platform.equals("Java")) m_model= new BT747model();
        // sp.writeBytes(buf,0,1);
        miGpxUTC0.isChecked = m.getGpxUTC0();

        miGpxTrkSegWhenBig.isChecked = m.getGpxTrkSegWhenBig();

        miGpsDecode.isChecked = m.getGpsDecode();

        miRecordNumberInLogs.isChecked = m.getRecordNbrInLogs();
        miHolux.isChecked = m.getForceHolux241();
        miImperial.isChecked = m.getImperial();
        miOutputLogConditions.isChecked = m.getOutputLogConditions();

        m_TabPanel = new TabPanel(c_tpCaptions);
        add(m_TabPanel, CENTER, CENTER);
        // Progress bar to show download progress (separate thread)
        progressLabel = new Label(Txt.LB_DOWNLOAD);
        pb = new ProgressBar();
        add(progressLabel, LEFT, BOTTOM);
        progressLabel.setRect(LEFT, BOTTOM, PREFERRED, PREFERRED);
        progressLabel.setVisible(false);
        // m_ProgressLabel.setVisible(false);
        m_TabPanel.setBorderStyle(Window.NO_BORDER);
        m_TabPanel.setRect(getClientRect().modifiedBy(0, 0, 0,
                -pb.getPreferredHeight()));

        add(pb, RIGHT, SAME);
        pb.setRect(RIGHT,
                BOTTOM, // BOTTOM,RIGHT,
                getClientRect().width - progressLabel.getRect().width - 2,
                PREFERRED);
        updateProgressBar();

        numPanels = 0;
        m_TabPanel.setPanel(C_LOG_CTRL_IDX, new GPSLogFormat(m, c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_LOGINFO_IDX, new GPSLogReason(c, m));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_LOGGET_IDX, new GPSLogGet(m, c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_FILECTRL_IDX, new GPSLogFile(c, m));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_FILTERCTRL_IDX, new GPSFiltersTabPanel(m, c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_EASYCTRL_IDX, new GPSLogEasy(m, c));
        numPanels++;
        m_TabPanel.setPanel(C_GPS_CONCTRL_IDX, new GPSconctrl(c, m));
        numPanels++;
        // m_TabPanel.setPanel(C_GPS_FLASH_IDX,m_GPSFlash = new
        // GPSFlashOption(m_GPSstate));
        // C_NUM_PANELS++;
        m_TabPanel.setPanel(C_GPS_FLASH_IDX, new GPSOtherTabPanel(c, m));
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
        miTraversableFocus.isChecked = m.isTraversableFocus();

        m.addListener((ModelListener) this);
        c.addGPSListener(this);
        addTimer(this, 55);

        gpsType();
    }

    private void gpsType() {
        miDefaultDevice.isChecked = false;
        miGisteqType1.isChecked = false;
        miGisteqType2.isChecked = false;
        miGisteqType3.isChecked = false;
        switch (m.getGPSType()) {
        case Controller.GPS_TYPE_DEFAULT:
            miDefaultDevice.isChecked = true;
            break;
        case Controller.GPS_TYPE_GISTEQ1:
            miGisteqType1.isChecked = true;
            break;
        case Controller.GPS_TYPE_GISTEQ2:
            miGisteqType2.isChecked = true;
            break;
        case Controller.GPS_TYPE_GISTEQ3:
            miGisteqType3.isChecked = true;
            break;
        default:
            break;
        }
    }

    public final void onEvent(final Event event) {
        //
        // if(event.type>9999) { Vm.debug("EventB:"+event.type+"
        // "+event.consumed); }
        switch (event.type) {
        case ControlEvent.TIMER:
            if ((topMost == this) && m.isSolveMacLagProblem()
                    && (event.target == this)) {
                this._doPaint();
            }
            break;
        case ControlEvent.WINDOW_CLOSED:
            if (event.target == mbErase) {
                if (!mbErase.isPopped()) {
                    stopErase();
                }
            } else if (event.target == menubar) {
                switch (menuBar.getSelectedMenuItem()) {
                case -1:
                    break; // No item selected
                case C_MENU_FILE_EXIT:
                    MessageBox mb;
                    String[] szExitButtonArray = { Txt.YES, Txt.NO };
                    mb = new MessageBox(Txt.TITLE_ATTENTION,
                            Txt.CONFIRM_APP_EXIT, szExitButtonArray);
                    mb.popupBlockingModal();
                    if (mb.getPressedButtonIndex() == 0) {
                        // Exit application
                        MainWindow.getMainWindow().exit(0);
                        break;
                    }
                    // Back to application
                    break;
                case C_MENU_RESTART_CONNECTION:
                    c.connectGPS();
                    break;
                case C_MENU_STOP_CONNECTION:
                    c.closeGPS();
                    break;
                case C_MENU_FOCUS_HIGHLIGHT:
                    c.setTraversableFocus(miTraversableFocus.isChecked);
                    waba.sys.Settings.keyboardFocusTraversable = m
                            .isTraversableFocus();
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
                case C_MENU_OUTPUT_LOGCONDITIONS:
                    c.setOutputLogConditions(miOutputLogConditions.isChecked);
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
                    c.setRecordNbrInLogs(miRecordNumberInLogs.isChecked);
                    break;
                case C_MENU_ABOUT:
                    new MessageBox(Txt.ABOUT_TITLE, Txt.ABOUT_TXT).popupModal();
                    break;
                case C_MENU_ABOUT_SW:
                    new MessageBox(Txt.ABOUT_SUPERWABA_TITLE,
                            Txt.ABOUT_SUPERWABA_TXT).popupModal();
                    break;
                case C_MENU_INFO:
                    new MessageBox(Txt.DISCLAIMER_TITLE, Txt.DISCLAIMER_TXT)
                            .popupModal();
                    break;
                case C_MENU_DEFAULTDEVICE:
                    c.setGPSType(Controller.GPS_TYPE_DEFAULT);
                    gpsType();
                    break;
                case C_MENU_GISTEQ_TYPE1:
                    c.setGPSType(Controller.GPS_TYPE_GISTEQ1);
                    gpsType();
                    break;
                case C_MENU_GISTEQ_TYPE2:
                    c.setGPSType(Controller.GPS_TYPE_GISTEQ2);
                    gpsType();
                    break;
                case C_MENU_GISTEQ_TYPE3:
                    c.setGPSType(Controller.GPS_TYPE_GISTEQ3);
                    gpsType();
                    break;
                default:
                    break;

                }

            }
            break;
        case ControlEvent.PRESSED:
            if (event.target == m_TabPanel) {
                Control cntrl;
                cntrl = m_TabPanel.getPanel(m_TabPanel.getActiveTab());
                cntrl.postEvent(new Event(ControlEvent.PRESSED, cntrl, 0));
            }
            break;
        default:
            if (event.target == this) {
                for (int i = 0; i < numPanels; i++) {
                    m_TabPanel.getPanel(i).onEvent(event);
                }
            }
        }
    }

    private void updateProgressBar() {
        if (pb != null) {
            if (m.isDownloadOnGoing()) {
                pb.min = m.getStartAddr();
                pb.max = m.getEndAddr();
                pb.setValue(m.getNextReadAddr(), "", " b");
            }
            pb.setVisible(m.isDownloadOnGoing());
            progressLabel.setVisible(m.isDownloadOnGoing());
        }
    }

    public final void newEvent(final bt747.ui.Event event) {
        if ((event.getType() == ModelEvent.CONNECTED)) {
            m_TabPanel.setActiveTab(C_GPS_LOGGET_IDX);
        }
        this.postEvent(event);
    }

    public final void gpsEvent(final GpsEvent event) {
        int eventType = event.getType();
        if ((event.getType() == GpsEvent.GPRMC)
                || (event.getType() == GpsEvent.GPGGA)) {
            Control cntrl;
            cntrl = m_TabPanel.getPanel(m_TabPanel.getActiveTab());
            event.target = cntrl;
            cntrl.postEvent(event);
        } else if ((event.getType() == GpsEvent.DOWNLOAD_STATE_CHANGE)) {
            updateProgressBar();
        } else if ((event.getType() == GpsEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY)) {
            requestLogOverwriteConfirmation();
        } else if ((event.getType() == GpsEvent.ERASE_ONGOING_NEED_POPUP)) {
            createErasePopup();
        } else if ((event.getType() == GpsEvent.COULD_NOT_OPEN_FILE)) {
            couldNotOpenFileMessage((String) event.getArg());
        } else if ((event.getType() == GpsEvent.DEBUG_MSG)) {
            Vm.debug((String) event.getArg());
        } else {
            Control cntrl;
            cntrl = m_TabPanel.getPanel(m_TabPanel.getActiveTab());
            cntrl.postEvent(new Event(eventType, cntrl, 0));
        }

    }

    public final void onExit() {
        waba.sys.Vm.setDeviceAutoOff(orgAutoOnOff); // Avoid auto-off causing BT
        // trouble
        c.saveSettings();
    }

    public final void requestLogOverwriteConfirmation() {
        // TODO: Make this non blocking (with multiple interfaces open)
        // Log is not the same - delete the log and reopen.
        MessageBox mb;
        String[] mbStr = { Txt.OVERWRITE, Txt.ABORT_DOWNLOAD };
        mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.DATA_NOT_SAME, mbStr);
        mb.popupBlockingModal();
        c.replyToOkToOverwrite(mb.getPressedButtonIndex() == 0);
    }

    private final String[] eraseWait = { Txt.CANCEL_WAITING };
    private MessageBox mbErase = new MessageBox(Txt.TITLE_WAITING_ERASE,
            Txt.TXT_WAITING_ERASE, eraseWait);

    private void createErasePopup() {
        mbErase.popupModal();
    }

    private void stopErase() {
        c.stopErase();
    }

    private void couldNotOpenFileMessage(final String fileName) {
        (new MessageBox(Txt.ERROR, Txt.COULD_NOT_OPEN + fileName + Txt.CHK_PATH))
                .popupBlockingModal();
    }

}
