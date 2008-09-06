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

import bt747.Txt;
import bt747.model.AppSettings;
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
public class AppBT747 extends MainWindow implements ModelListener {

    /*
     * Using Model, AppController, View.
     */
    protected Model m;
    protected AppController c;
    /**
     * The 'GPS state'. Used to get current GPS information and get access to
     * it.
     */
    // private GPSstate m_GPSstate;
    /** The label next to the progressbar. Hidden when not in use. */
    private Label progressLabel;
    /** The progress bar itself. Hidden when not in use. */
    private ProgressBar progressBar;

    // private BT747model m_model;
    /** The application's MenuBar. */
    private MenuBar menuBar;
    /** The content of the menu bar. */

    private MenuItem miFile = new MenuItem(Txt.S_FILE);
    private MenuItem miExitApplication = new MenuItem(Txt.S_EXIT_APPLICATION);
    private MenuItem miSettings = new MenuItem(Txt.S_SETTINGS);
    private MenuItem miStopLogOnConnect = new MenuItem(
            Txt.S_STOP_LOGGING_ON_CONNECT, false);
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
            { miSettings, miStopConnection, miStopLogOnConnect, new MenuItem(),
                    miGpxUTC0, miGpxTrkSegWhenBig, miGpsDecode,
                    miRecordNumberInLogs, new MenuItem(), miTraversableFocus,
                    new MenuItem(), miDebug, miDebugConn, miStats, miImperial,
                    miOutputLogConditions, },
            { miDevice, miDefaultDevice, miGisteqType1, miGisteqType2,
                    miGisteqType3, miHolux, },
            { miInfo, miAboutBT747, miAboutSuperWaba, miInfo } };
    /** MenuBar item for File->Exit */
    private static final int C_MENU_FILE_EXIT = 001;
    // private static final int C_MENU_CONNECTION_SETTINGS = 101;
    /** MenuBar item for Settings->Stop connection */
    private static final int C_MENU_STOP_CONNECTION = 101;
    /** MenuBar item for Settings->Stop logging on connect */
    private static final int C_MENU_STOP_LOG_ON_CONNECT = 102;
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
    private TabPanel tabPanel;
    /** The captions for the tab panel */
    private final String[] c_tpCaptions = { Txt.C_FMT, Txt.C_CTRL, Txt.C_LOG,
            Txt.C_FILE, Txt.C_FLTR, Txt.C_EASY, Txt.C_CON, Txt.C_OTHR };
    private static final int TAB_LOG_CTRL_IDX = 0;
    private static final int TAB_LOGINFO_IDX = 1;
    private static final int TAB_LOG_GET_IDX = 2;
    private static final int C_GPS_FILECTRL_IDX = 3;
    private static final int C_GPS_FILTERCTRL_IDX = 4;
    private static final int C_GPS_EASYCTRL_IDX = 5;
    private static final int C_GPS_CONCTRL_IDX = 6;
    /** Tab Panel container - Other settings */
    private static final int C_GPS_FLASH_IDX = 7;

    /**
     * The auto on/off value at startup so that it can be restored at shutdown.
     */
    private int orgAutoOnOff;

    /**
     * Initialiser of the application.
     */
    public AppBT747() {
        m = new Model();
        c = new AppController(m);
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
            new MessageBox(Txt.TITLE_ATTENTION, Txt.BAD_SUPERWABAVERSION
                    + Settings.requiredVersionStr
                    + Txt.BAD_SUPERWABAVERSION_CONT + Settings.versionStr
                    + Txt.BAD_SUPERWABAVERSION_CONT2

            ).popupBlockingModal();
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

        miRecordNumberInLogs.isChecked = m
                .getBooleanOpt(AppSettings.IS_RECORDNBR_IN_LOGS);
        miHolux.isChecked = m.getBooleanOpt(AppSettings.IS_HOLUXM241);
        miImperial.isChecked = m.getBooleanOpt(AppSettings.IMPERIAL);
        miOutputLogConditions.isChecked = m
                .getBooleanOpt(AppSettings.OUTPUTLOGCONDITIONS);

        tabPanel = new TabPanel(c_tpCaptions);
        add(tabPanel, CENTER, CENTER);
        // Progress bar to show download progress (separate thread)
        progressLabel = new Label(Txt.LB_DOWNLOAD);
        progressBar = new ProgressBar();
        add(progressLabel, LEFT, BOTTOM);
        progressLabel.setRect(LEFT, BOTTOM, PREFERRED, PREFERRED);
        progressLabel.setVisible(false);
        // m_ProgressLabel.setVisible(false);
        tabPanel.setBorderStyle(Window.NO_BORDER);
        tabPanel.setRect(getClientRect().modifiedBy(0, 0, 0,
                -progressBar.getPreferredHeight()));

        add(progressBar, RIGHT, SAME);
        progressBar.setRect(RIGHT,
                BOTTOM, // BOTTOM,RIGHT,
                getClientRect().width - progressLabel.getRect().width - 2,
                PREFERRED);
        updateProgressBar();

        numPanels = 0;
        tabPanel.setPanel(TAB_LOG_CTRL_IDX, new GPSLogFormat(m, c));
        numPanels++;
        tabPanel.setPanel(TAB_LOGINFO_IDX, new GPSLogReason(c, m));
        numPanels++;
        tabPanel.setPanel(TAB_LOG_GET_IDX, new GPSLogGet(m, c));
        numPanels++;
        tabPanel.setPanel(C_GPS_FILECTRL_IDX, new GPSLogFile(c, m));
        numPanels++;
        tabPanel.setPanel(C_GPS_FILTERCTRL_IDX, new GpsFilterTabPanel(m, c));
        numPanels++;
        tabPanel.setPanel(C_GPS_EASYCTRL_IDX, new GPSLogEasy(m, c));
        numPanels++;
        tabPanel.setPanel(C_GPS_CONCTRL_IDX, new GPSconctrl(c, m));
        numPanels++;
        // m_TabPanel.setPanel(C_GPS_FLASH_IDX,m_GPSFlash = new
        // GPSFlashOption(m_GPSstate));
        // C_NUM_PANELS++;
        tabPanel.setPanel(C_GPS_FLASH_IDX, new GPSOtherTabPanel(c, m));
        numPanels++;
        // m_TabPanel.setPanel(1,dataEdit = new dataEdit());
        // m_TabPanel.setPanel(2,grid = new Grid(gridCaptions,false));
        // SerialPort sp;
        // sp=new SerialPort(4,9600);
        // byte[] buf = {'H','e','l','l','o' };
        // m_ProgressBar.setRect(m_ProgressLabel.getRect().x2(),m_ProgressLabel.getRect().y,//BOTTOM,RIGHT,
        // 10,//getClientRect().width-m_ProgressLabel.getRect().width,
        // 10+0*PREFERRED);
        tabPanel.setActiveTab(C_GPS_CONCTRL_IDX);

        waba.sys.Settings.keyboardFocusTraversable = m
                .getBooleanOpt(AppSettings.IS_TRAVERSABLE);
        miTraversableFocus.isChecked = m
                .getBooleanOpt(AppSettings.IS_TRAVERSABLE);
        miStopLogOnConnect.isChecked = m
                .getBooleanOpt(AppSettings.IS_STOP_LOGGING_ON_CONNECT);

        m.addListener(this);
        addTimer(this, 55);

        gpsType();
    }

    private void gpsType() {
        miDefaultDevice.isChecked = false;
        miGisteqType1.isChecked = false;
        miGisteqType2.isChecked = false;
        miGisteqType3.isChecked = false;
        switch (m.getGPSType()) {
        case AppController.GPS_TYPE_DEFAULT:
            miDefaultDevice.isChecked = true;
            break;
        case AppController.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
            miGisteqType1.isChecked = true;
            break;
        case AppController.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
            miGisteqType2.isChecked = true;
            break;
        case AppController.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
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
            if ((topMost == this) && Model.isSolveMacLagProblem()
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
                case C_MENU_STOP_LOG_ON_CONNECT:
                    c.setBooleanOpt(AppSettings.IS_STOP_LOGGING_ON_CONNECT,
                            miStopLogOnConnect.isChecked);
                    break;
                case C_MENU_STOP_CONNECTION:
                    c.closeGPS();
                    break;
                case C_MENU_FOCUS_HIGHLIGHT:
                    c.setTraversableFocus(miTraversableFocus.isChecked);
                    waba.sys.Settings.keyboardFocusTraversable = m
                            .getBooleanOpt(AppSettings.IS_TRAVERSABLE);
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
                            Txt.ABOUT_SUPERWABA_TXT + Settings.versionStr
                                    + Txt.ABOUT_SUPERWABA_TXT).popupModal();
                    break;
                case C_MENU_INFO:
                    new MessageBox(Txt.DISCLAIMER_TITLE, Txt.DISCLAIMER_TXT)
                            .popupModal();
                    break;
                case C_MENU_DEFAULTDEVICE:
                    c.setGPSType(AppController.GPS_TYPE_DEFAULT);
                    gpsType();
                    break;
                case C_MENU_GISTEQ_TYPE1:
                    c.setGPSType(AppController.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX);
                    gpsType();
                    break;
                case C_MENU_GISTEQ_TYPE2:
                    c
                            .setGPSType(AppController.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR);
                    gpsType();
                    break;
                case C_MENU_GISTEQ_TYPE3:
                    c
                            .setGPSType(AppController.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII);
                    gpsType();
                    break;
                default:
                    break;

                }

            }
            break;
        case ControlEvent.PRESSED:
            if (event.target == tabPanel) {
                Control cntrl;
                cntrl = tabPanel.getPanel(tabPanel.getActiveTab());
                cntrl.postEvent(new Event(ControlEvent.PRESSED, cntrl, 0));
            }
            break;
        default:
            if (event.target == this) {
                for (int i = 0; i < numPanels; i++) {
                    tabPanel.getPanel(i).onEvent(event);
                }
            }
        }
    }

    private void updateProgressBar() {
        if (progressBar != null) {
            if (m.isDownloadOnGoing()) {
                progressBar.min = m.getStartAddr();
                progressBar.max = m.getEndAddr();
                progressBar.setValue(m.getNextReadAddr(), "", " b");
            }
            progressBar.setVisible(m.isDownloadOnGoing());
            progressLabel.setVisible(m.isDownloadOnGoing());
        }
    }

    public final void modelEvent(final ModelEvent event) {
        switch (event.getType()) {
        case ModelEvent.CONNECTED:
            tabPanel.setActiveTab(TAB_LOG_GET_IDX);
            break;
        case ModelEvent.DOWNLOAD_STATE_CHANGE:
        case ModelEvent.LOG_DOWNLOAD_STARTED:
        case ModelEvent.LOG_DOWNLOAD_DONE:
            updateProgressBar();
            break;
        case ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
            requestLogOverwriteConfirmation();
            break;
        case ModelEvent.ERASE_ONGOING_NEED_POPUP:
            createErasePopup();
            break;
        case ModelEvent.ERASE_DONE_REMOVE_POPUP:
            removeErasePopup();
            break;
        case ModelEvent.COULD_NOT_OPEN_FILE:
            couldNotOpenFileMessage((String) event.getArg());
            break;
        case ModelEvent.DEBUG_MSG:
            Vm.debug((String) event.getArg());
            break;
        default:
            ModelListener cntrl;
            cntrl = (ModelListener) tabPanel.getPanel(tabPanel.getActiveTab());
            cntrl.modelEvent(event);
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

    private void removeErasePopup() {
        mbErase.unpop();
    }

    private void stopErase() {
        c.stopErase();
    }

    private void couldNotOpenFileMessage(final String fileName) {
        (new MessageBox(Txt.ERROR, Txt.COULD_NOT_OPEN + fileName + Txt.CHK_PATH))
                .popupBlockingModal();
    }
}
