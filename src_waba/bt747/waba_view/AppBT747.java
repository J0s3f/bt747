package bt747.waba_view;

// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** seesite@bt747.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
// *** The application was written using the SuperWaba toolset. ***
// *** This is a proprietary development environment based in ***
// *** part on the Waba development environment developed by ***
// *** WabaSoft, Inc. ***
// ********************************************************************
import waba.fx.Font;
import waba.fx.Sound;
import waba.sys.Settings;
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

import gps.BT747Constants;
import gps.GpsEvent;
import gps.connection.*;
import net.sf.bt747.waba.system.WabaJavaTranslations;

import bt747.Txt;
import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Exception;
import bt747.waba_view.ui.BT747MessageBox;

/**
 * Main class (application entry).
 * 
 * @author Mario De Weerd
 */
public class AppBT747 extends MainWindow implements ModelListener {
	/*
	 * Using Model, AppController, View.
	 */
	protected final Model m;
	protected final AppController c;

	static {
		// Set the low level interface.
		JavaLibBridge.setJavaLibImplementation(new WabaJavaTranslations());

		Sound.setEnabled(false);
	}

	private final void initGeneral() {
		// Set up the port.
		if (!GPSrxtx.hasDefaultPortInstance()) {
			GPSPort gpsPort;

			try {
				gpsPort = (GPSPort) Class.forName("gps.connection.GPSRxTxPort")
						.newInstance();
			} catch (final Exception e) {
				gpsPort = new GPSWabaPort();
			}

			/**
			 * Set the defaults of the device according to preset, guessed
			 * values.
			 */
			// Settings.platform:
			// PalmOS, PalmOS/SDL, WindowsCE, PocketPC, MS_SmartPhone,
			// Win32, Symbian, Linux, Posix
			final String Platform = waba.sys.Settings.platform;

			if ((Platform.equals("Java")) || (Platform.equals("Win32"))
					|| (Platform.equals("Posix")) || (Platform.equals("Linux"))) {
				// Try USB Port
				gpsPort.setUSB();
			} else if (Platform.startsWith("PalmOS")) {
				gpsPort.setBlueTooth();
			} else {
				gpsPort.setPort(0); // Should be bluetooth in WinCE
			}
			// Set the serial port class instance to use (also system
			// specific).
			GPSrxtx.setDefaultGpsPortInstance(gpsPort);
		}

		AppController.initAppSettings();
	}

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

	private MenuItem miFile;
	private MenuItem miExitApplication;
	private MenuItem miSettings;
	private MenuItem miStopLogOnConnect;
	private MenuItem miStopConnection;
	private MenuItem miGpxUTC0;
	private MenuItem miGpxTrkSegWhenBig;
	private MenuItem miGpsDecode;
	private MenuItem miRecordNumberInLogs;
	private MenuItem miTraversableFocus;

	private MenuItem miDebug;
	private MenuItem miDebugConn;
	private MenuItem miStats;
	private MenuItem miImperial;
	private MenuItem miOutputLogConditions;
	private MenuItem miDevice;
	private MenuItem miDefaultDevice;
	private MenuItem miGisteqType1;
	private MenuItem miGisteqType2;
	private MenuItem miGisteqType3;
	private MenuItem miHolux;
	private MenuItem miHolux245;
	private MenuItem miSkytraq;

	private MenuItem miInfo;
	private MenuItem miAboutBT747;
	private MenuItem miAboutSuperWaba;
	private MenuItem miLanguage;
	private MenuItem miLangDE;
	private MenuItem miLangEN;
	private MenuItem miLangES;
	private MenuItem miLangFR;
	private MenuItem miLangIT;
	private MenuItem miLangJP;
	private MenuItem miLangKO;
	private MenuItem miLangNL;
	private MenuItem miLangZH;

	/**
	 * The text for the menu.
	 */
	private MenuItem[][] menu;
	/** MenuBar item for File->Exit. */
	private static final int C_MENU_FILE_EXIT = 001;
	// private static final int C_MENU_CONNECTION_SETTINGS = 101;
	/** MenuBar item for Settings->Stop connection. */
	private static final int C_MENU_STOP_CONNECTION = 101;
	/** MenuBar item for Settings->Stop logging on connect. */
	private static final int C_MENU_STOP_LOG_ON_CONNECT = 102;
	/** MenuBar item for Settings->GPX UTC 0. */
	private static final int C_MENU_GPX_UTC0 = 104;
	/** MenuBar item for Settings->GPX Trk Sep when big only. */
	private static final int C_MENU_GPX_TRKSEG_BIGONLY = 105;
	/** MenuBar item for Settings->GPS Decode Active. */
	private static final int C_MENU_GPS_DECODE_ACTIVE = 106;
	/** MenuBar item for Settings->Record number in logs. */
	private static final int C_MENU_RECORDNMBR_IN_LOGS = 107;
	/** MenuBar item for Settings->Debug. */
	private static final int C_MENU_FOCUS_HIGHLIGHT = 109;
	/** MenuBar item for Settings->Debug. */
	private static final int C_MENU_DEBUG_ACTIVE = 111;
	private static final int C_MENU_DEBUG_CONN = 112;
	/** MenuBar item for Settings->Conn. Stats */
	private static final int C_MENU_STATS_ACTIVE = 113;
	/** MenuBar item for Settings->Imperial units. */
	private static final int C_MENU_IMPERIAL = 114;
	/** MenuBar item for Settings->OutputLogConditions. */
	private static final int C_MENU_OUTPUT_LOGCONDITIONS = 115;

	private static final int C_MENU_DEFAULTDEVICE = 201;
	private static final int C_MENU_GISTEQ_TYPE1 = 202;
	private static final int C_MENU_GISTEQ_TYPE2 = 203;
	private static final int C_MENU_GISTEQ_TYPE3 = 204;
	/** MenuBar item for Settings->Holux M-241. */
	private static final int C_MENU_HOLUX_241 = 205;
	private static final int C_MENU_HOLUX_245 = 206;

	/** MenuBar item for Info->About BT747. */
	private static final int C_MENU_ABOUT = 301;
	/** MenuBar item for Info->About Superwaba. */
	private static final int C_MENU_ABOUT_SW = 302;
	/** MenuBar item for Info->Info. */
	private static final int C_MENU_INFO = 303;

	private final static int C_MENU_LANG_DE = 401;
	private final static int C_MENU_LANG_EN = 402;
	private final static int C_MENU_LANG_ES = 403;
	private final static int C_MENU_LANG_FR = 404;
	private final static int C_MENU_LANG_IT = 405;
	private final static int C_MENU_LANG_JP = 406;
	private final static int C_MENU_LANG_KO = 407;
	private final static int C_MENU_LANG_NL = 408;
	private final static int C_MENU_LANG_ZH = 409;

	/** The tab panel. */
	private TabPanel tabPanel;
	/** The captions for the tab panel. */
	private String[] c_tpCaptions;
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
		if (Settings.onDevice) {
			waba.sys.Vm.debug(waba.sys.Vm.ERASE_DEBUG);
		}
		orgAutoOnOff = waba.sys.Vm.setDeviceAutoOff(0); // Avoid auto-off
		// causing BT trouble

		/**
         * 
         */
		if (Txt.getString(Txt.fontFile) != null) {
			MainWindow.defaultFont = Font.getFont(Txt.getString(Txt.fontFile),
					false, 12);
			MainWindow.getMainWindow().setTitleFont(MainWindow.defaultFont);
		}

		setDoubleBuffer(true);
		setBorderStyle(Window.TAB_ONLY_BORDER);
		setTitle(Txt.getString(Txt.S_TITLE));
		waba.sys.Settings.setUIStyle(waba.sys.Settings.Flat);

		initGeneral();

		m = new Model();
		c = new AppController(m);
		Txt.setLang(m.getStringOpt(AppSettings.LANGUAGE));
		if (Txt.getString(Txt.encoding) != null) {
			waba.sys.Convert.setDefaultConverter(Txt.getString(Txt.encoding));
		}
		initGUIVars();
	}

	private int numPanels;

	public static final String requiredVersionStr = "5.82";
	public static final int requiredVersion = 582;

	/**
	 * Called after application start.
	 * 
	 * @see waba.ui.MainWindow#onStart()
	 */
	public void onStart() {
		super.onStart();

		setLang(m.getStringOpt(AppSettings.LANGUAGE));
		if (Settings.version < AppBT747.requiredVersion) {
			new BT747MessageBox(Txt.getString(Txt.TITLE_ATTENTION), Txt
					.getString(Txt.BAD_SUPERWABAVERSION)
					+ AppBT747.requiredVersionStr
					+ Txt.getString(Txt.BAD_SUPERWABAVERSION_CONT)
					+ Settings.versionStr
					+ Txt.getString(Txt.BAD_SUPERWABAVERSION_CONT2)

			).popupBlockingModal();
			MainWindow.getMainWindow().exit(0);
		}

		// m_GPSstate=m.gpsModel();
		menuBar = new MenuBar(menu);
		setMenuBar(menuBar);
		miDebug.isChecked = Model.isDebug();
		miDebugConn.isChecked = m.isDebugConn();
		// Next line is for modeling a device for debug.
		// Doing this on the windows platform
		// if (Settings.platform.equals("Java")) m_model= new BT747model();
		// sp.writeBytes(buf,0,1);
		miGpxUTC0.isChecked = m.getBooleanOpt(AppSettings.GPXUTC0);

		miGpxTrkSegWhenBig.isChecked = m
				.getBooleanOpt(AppSettings.GPXTRKSEGBIG);

		miGpsDecode.isChecked = m.getBooleanOpt(AppSettings.DECODEGPS);

		miRecordNumberInLogs.isChecked = m
				.getBooleanOpt(AppSettings.IS_RECORDNBR_IN_LOGS);
		miImperial.isChecked = m.getBooleanOpt(AppSettings.IMPERIAL);
		miOutputLogConditions.isChecked = m
				.getBooleanOpt(AppSettings.OUTPUTLOGCONDITIONS);

		tabPanel = new TabPanel(c_tpCaptions);
		add(tabPanel, Control.CENTER, Control.CENTER);
		// Progress bar to show download progress (separate thread)
		progressLabel = new Label(Txt.getString(Txt.LB_DOWNLOAD));
		progressBar = new ProgressBar();
		add(progressLabel, Control.LEFT, Control.BOTTOM);
		progressLabel.setRect(Control.LEFT, Control.BOTTOM, Control.PREFERRED,
				Control.PREFERRED);
		progressLabel.setVisible(false);
		// m_ProgressLabel.setVisible(false);
		tabPanel.setBorderStyle(Window.NO_BORDER);
		tabPanel.setRect(getClientRect().modifiedBy(0, 0, 0,
				-progressBar.getPreferredHeight()));

		add(progressBar, Control.RIGHT, Control.SAME);
		progressBar.setRect(Control.RIGHT,
				Control.BOTTOM, // BOTTOM,RIGHT,
				getClientRect().width - progressLabel.getRect().width - 2,
				Control.PREFERRED);
		updateProgressBar();

		numPanels = 0;
		tabPanel.setPanel(AppBT747.TAB_LOG_CTRL_IDX, new GPSLogFormat(m, c));
		numPanels++;
		tabPanel.setPanel(AppBT747.TAB_LOGINFO_IDX, new GPSLogReason(c, m));
		numPanels++;
		tabPanel.setPanel(AppBT747.TAB_LOG_GET_IDX, new GPSLogGet(m, c));
		numPanels++;
		tabPanel.setPanel(AppBT747.C_GPS_FILECTRL_IDX, new GPSLogFile(c, m));
		numPanels++;
		tabPanel.setPanel(AppBT747.C_GPS_FILTERCTRL_IDX, new GpsFilterTabPanel(
				m, c));
		numPanels++;
		tabPanel.setPanel(AppBT747.C_GPS_EASYCTRL_IDX, new GPSLogEasy(m, c));
		numPanels++;
		tabPanel.setPanel(AppBT747.C_GPS_CONCTRL_IDX, new GPSconctrl(c, m));
		numPanels++;
		// m_TabPanel.setPanel(C_GPS_FLASH_IDX,m_GPSFlash = new
		// GPSFlashOption(m_GPSstate));
		// C_NUM_PANELS++;
		tabPanel.setPanel(AppBT747.C_GPS_FLASH_IDX, new GPSOtherTabPanel(c, m));
		numPanels++;
		// m_TabPanel.setPanel(1,dataEdit = new dataEdit());
		// m_TabPanel.setPanel(2,grid = new Grid(gridCaptions,false));
		// SerialPort sp;
		// sp=new SerialPort(4,9600);
		// byte[] buf = {'H','e','l','l','o' };
		// m_ProgressBar.setRect(m_ProgressLabel.getRect().x2(),m_ProgressLabel.getRect().y,//BOTTOM,RIGHT,
		// 10,//getClientRect().width-m_ProgressLabel.getRect().width,
		// 10+0*PREFERRED);
		tabPanel.setActiveTab(AppBT747.C_GPS_CONCTRL_IDX);

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

	private final void initGUIVars() {
		miFile = new MenuItem(Txt.getString(Txt.S_FILE));
		miExitApplication = new MenuItem(Txt.getString(Txt.S_EXIT_APPLICATION));
		miSettings = new MenuItem(Txt.getString(Txt.S_SETTINGS));
		miStopLogOnConnect = new MenuItem(Txt
				.getString(Txt.S_STOP_LOGGING_ON_CONNECT), false);
		miStopConnection = new MenuItem(Txt.getString(Txt.S_STOP_CONNECTION));
		miGpxUTC0 = new MenuItem(Txt.getString(Txt.S_GPX_UTC_OFFSET_0), false);
		miGpxTrkSegWhenBig = new MenuItem(Txt
				.getString(Txt.S_GPX_TRKSEG_WHEN_SMALL), false);
		miGpsDecode = new MenuItem(Txt.getString(Txt.S_GPS_DECODE_ACTIVE),
				false);
		miRecordNumberInLogs = new MenuItem(Txt
				.getString(Txt.ADD_RECORD_NUMBER), false);
		miTraversableFocus = new MenuItem(Txt.getString(Txt.S_FOCUS_HIGHLIGHT),
				false);

		miDebug = new MenuItem(Txt.getString(Txt.S_DEBUG), false);
		miDebugConn = new MenuItem(Txt.getString(Txt.S_DEBUG_CONN), false);
		miStats = new MenuItem(Txt.getString(Txt.S_STATS), false);
		miImperial = new MenuItem(Txt.getString(Txt.S_IMPERIAL), false);
		miOutputLogConditions = new MenuItem(Txt
				.getString(Txt.S_OUTPUT_LOGCONDITIONS), false);

		miDevice = new MenuItem(Txt.getString(Txt.S_DEVICE));
		miDefaultDevice = new MenuItem(Txt.getString(Txt.S_DEFAULTDEVICE), true);
		miGisteqType1 = new MenuItem(Txt.getString(Txt.S_GISTEQTYPE1), false);
		miGisteqType2 = new MenuItem(Txt.getString(Txt.S_GISTEQTYPE2), false);
		miGisteqType3 = new MenuItem(Txt.getString(Txt.S_GISTEQTYPE3), false);
		miHolux = new MenuItem("Holux M241", false);
		miHolux245 = new MenuItem("Holux 245", false);
		miSkytraq = new MenuItem("Skytraq", false);

		miInfo = new MenuItem(Txt.getString(Txt.S_INFO));
		miAboutBT747 = new MenuItem(Txt.getString(Txt.S_ABOUT_BT747));
		miAboutSuperWaba = new MenuItem(Txt.getString(Txt.S_ABOUT_SUPERWABA));

		miLanguage = new MenuItem(Txt.getString(Txt.MI_LANGUAGE));

		miLangDE = new MenuItem(Txt.getString(Txt.LANG_DE), false);
		miLangEN = new MenuItem(Txt.getString(Txt.LANG_EN), false);
		miLangES = new MenuItem(Txt.getString(Txt.LANG_ES), false);
		miLangFR = new MenuItem(Txt.getString(Txt.LANG_FR), false);
		miLangIT = new MenuItem(Txt.getString(Txt.LANG_IT), false);
		miLangJP = new MenuItem(Txt.getString(Txt.LANG_JP), false);
		miLangKO = new MenuItem(Txt.getString(Txt.LANG_KO), false);
		miLangNL = new MenuItem(Txt.getString(Txt.LANG_NL), false);
		miLangZH = new MenuItem(Txt.getString(Txt.LANG_ZH), false);

		final String[] tmp = { Txt.getString(Txt.C_FMT),
				Txt.getString(Txt.C_CTRL), Txt.getString(Txt.C_LOG),
				Txt.getString(Txt.C_FILE), Txt.getString(Txt.C_FLTR),
				Txt.getString(Txt.C_EASY), Txt.getString(Txt.C_CON),
				Txt.getString(Txt.C_OTHR) };
		c_tpCaptions = tmp;
		
		final MenuItem[][] tmp2 = {
				{ miFile, miExitApplication },
				{ miSettings, miStopConnection, miStopLogOnConnect, new MenuItem(),
						miGpxUTC0, miGpxTrkSegWhenBig, miGpsDecode,
						miRecordNumberInLogs, new MenuItem(), miTraversableFocus,
						new MenuItem(), miDebug, miDebugConn, miStats, miImperial,
						miOutputLogConditions, },
				{ miDevice, miDefaultDevice, miGisteqType1, miGisteqType2,
						miGisteqType3, miHolux, miHolux245 },
				{ miInfo, miAboutBT747, miAboutSuperWaba, miInfo },
				{ miLanguage, miLangDE, miLangEN, miLangES, miLangFR, miLangIT,
						miLangJP, miLangKO, miLangNL, miLangZH } };
		menu = tmp2;
		
		final String[] tmp3 = { Txt.getString(Txt.CANCEL_WAITING) };
		eraseWait = tmp3;
		
		mbErase = new BT747MessageBox(Txt
				.getString(Txt.TITLE_WAITING_ERASE), Txt
				.getString(Txt.TXT_WAITING_ERASE), eraseWait);
	}

	private void setLang(final String lang) {
		miLangDE.isChecked = lang.equals("de");
		miLangEN.isChecked = lang.equals("en");
		miLangES.isChecked = lang.equals("es");
		miLangFR.isChecked = lang.equals("fr");
		miLangIT.isChecked = lang.equals("it");
		miLangJP.isChecked = lang.equals("jp");
		miLangKO.isChecked = lang.equals("ko");
		miLangNL.isChecked = lang.equals("nl");
		miLangZH.isChecked = lang.equals("zh");
		c.setStringOpt(AppSettings.LANGUAGE, lang);
	}

	/**
	 * Sets the GPS Device type from the menu settings.
	 */
	private void gpsType() {
		miDefaultDevice.isChecked = false;
		miGisteqType1.isChecked = false;
		miGisteqType2.isChecked = false;
		miGisteqType3.isChecked = false;
		miHolux.isChecked = false;
		miHolux245.isChecked = false;
		miSkytraq.isChecked = false;
		switch (m.getIntOpt(AppSettings.GPSTYPE)) {
		case BT747Constants.GPS_TYPE_DEFAULT:
			miDefaultDevice.isChecked = true;
			break;
		case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX:
			miGisteqType1.isChecked = true;
			break;
		case BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR:
			miGisteqType2.isChecked = true;
			break;
		case BT747Constants.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII:
			miGisteqType3.isChecked = true;
			break;
		case BT747Constants.GPS_TYPE_HOLUX_GR245:
			miHolux245.isChecked = true;
			break;
		case BT747Constants.GPS_TYPE_HOLUX_M241:
			miHolux.isChecked = true;
		case BT747Constants.GPS_TYPE_SKYTRAQ:
			miSkytraq.isChecked = true;
		default:
			break;
		}
	}

	/**
	 * Process GUI event.
	 * 
	 * @param event
	 *            The event to process.
	 * @see waba.ui.Control#onEvent(waba.ui.Event)
	 */
	public final void onEvent(final Event event) {
		//
		// if(event.type>9999) { Vm.debug("EventB:"+event.type+"
		// "+event.consumed); }
		switch (event.type) {
		case ControlEvent.TIMER:
			if ((Window.topMost == this) && AppSettings.isSolveMacLagProblem()
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
					BT747MessageBox mb;
					final String[] szExitButtonArray = {
							Txt.getString(Txt.YES), Txt.getString(Txt.NO) };
					mb = new BT747MessageBox(
							Txt.getString(Txt.TITLE_ATTENTION), Txt
									.getString(Txt.CONFIRM_APP_EXIT),
							szExitButtonArray);
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
					c.setBooleanOpt(AppSettings.IS_TRAVERSABLE,
							miTraversableFocus.isChecked);
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
				case C_MENU_OUTPUT_LOGCONDITIONS:
					c.setBooleanOpt(AppSettings.OUTPUTLOGCONDITIONS,
							miOutputLogConditions.isChecked);
					break;
				case C_MENU_IMPERIAL:
					c.setBooleanOpt(AppSettings.IMPERIAL, miImperial.isChecked);
					break;
				case C_MENU_GPX_UTC0:
					c.setBooleanOpt(AppSettings.GPXUTC0, miGpxUTC0.isChecked);
					break;
				case C_MENU_GPX_TRKSEG_BIGONLY:
					c.setBooleanOpt(AppSettings.GPXTRKSEGBIG,
							miGpxTrkSegWhenBig.isChecked);
					break;
				case C_MENU_GPS_DECODE_ACTIVE:
					c.setGpsDecode(miGpsDecode.isChecked);
					break;
				case C_MENU_RECORDNMBR_IN_LOGS:
					c.setBooleanOpt(AppSettings.IS_RECORDNBR_IN_LOGS,
							miRecordNumberInLogs.isChecked);
					break;
				case C_MENU_ABOUT:
					new BT747MessageBox(Txt.getString(Txt.ABOUT_TITLE), Txt
							.getString(Txt.ABOUT_TXT)).popupModal();
					break;
				case C_MENU_ABOUT_SW:
					new BT747MessageBox(Txt
							.getString(Txt.ABOUT_SUPERWABA_TITLE), Txt
							.getString(Txt.ABOUT_SUPERWABA_TXT)
							+ Settings.versionStr
							+ Txt.getString(Txt.ABOUT_SUPERWABA_TXT))
							.popupModal();
					break;
				case C_MENU_INFO:
					new BT747MessageBox(Txt.getString(Txt.DISCLAIMER_TITLE),
							Txt.getString(Txt.DISCLAIMER_TXT)).popupModal();
					break;
				case C_MENU_DEFAULTDEVICE:
					c.setIntOpt(AppSettings.GPSTYPE,
							BT747Constants.GPS_TYPE_DEFAULT);
					gpsType();
					break;
				case C_MENU_HOLUX_241:
					c.setIntOpt(AppSettings.GPSTYPE,
							BT747Constants.GPS_TYPE_HOLUX_M241);
					gpsType();
					break;
				case C_MENU_HOLUX_245:
					c.setIntOpt(AppSettings.GPSTYPE,
							BT747Constants.GPS_TYPE_HOLUX_GR245);
					gpsType();
					break;
				case C_MENU_GISTEQ_TYPE1:
					c.setIntOpt(AppSettings.GPSTYPE,
							BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_NEMERIX);
					gpsType();
					break;
				case C_MENU_GISTEQ_TYPE2:
					c.setIntOpt(AppSettings.GPSTYPE,
							BT747Constants.GPS_TYPE_GISTEQ_ITRACKU_PHOTOTRACKR);
					gpsType();
					break;
				case C_MENU_GISTEQ_TYPE3:
					c
							.setIntOpt(
									AppSettings.GPSTYPE,
									BT747Constants.GPS_TYPE_GISTEQ_GISTEQ_ITRACKU_SIRFIII);
					gpsType();
					break;
				case C_MENU_LANG_DE:
					setLang("de");
					break;
				case C_MENU_LANG_EN:
					setLang("en");
					break;
				case C_MENU_LANG_ES:
					setLang("es");
					break;
				case C_MENU_LANG_FR:
					setLang("fr");
					break;
				case C_MENU_LANG_IT:
					setLang("it");
					break;
				case C_MENU_LANG_JP:
					setLang("jp");
					break;
				case C_MENU_LANG_KO:
					setLang("ko");
					break;
				case C_MENU_LANG_NL:
					setLang("nl");
					break;
				case C_MENU_LANG_ZH:
					setLang("zh");
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

	/**
	 * Update the progress bar.
	 */
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

	/**
	 * Handle the event from the model.
	 * 
	 * @param event
	 *            The event to handle.
	 * 
	 * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
	 */
	public final void modelEvent(final ModelEvent event) {
		switch (event.getType()) {
		case ModelEvent.CONNECTED:
			if (!waba.sys.Settings.keyboardFocusTraversable) {
				tabPanel.setActiveTab(AppBT747.TAB_LOG_GET_IDX);
			}
			break;
		case GpsEvent.DOWNLOAD_STATE_CHANGE:
		case GpsEvent.LOG_DOWNLOAD_STARTED:
		case GpsEvent.LOG_DOWNLOAD_DONE:
			updateProgressBar();
			break;
		case GpsEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
			requestLogOverwriteConfirmation();
			break;
		case GpsEvent.ERASE_ONGOING_NEED_POPUP:
			createErasePopup();
			break;
		case GpsEvent.ERASE_DONE_REMOVE_POPUP:
			removeErasePopup();
			break;
		case GpsEvent.COULD_NOT_OPEN_FILE:
			couldNotOpenFileMessage((String) event.getArg());
			break;
		// case ModelEvent.DEBUG_MSG:
		// Generic.debug((String) event.getArg());
		// break;
		default:
			ModelListener cntrl;
			cntrl = (ModelListener) tabPanel.getPanel(tabPanel.getActiveTab());
			cntrl.modelEvent(event);
		}
	}

	/**
	 * Called before application exit.
	 * 
	 * @see waba.ui.MainWindow#onExit()
	 */
	public final void onExit() {
		waba.sys.Vm.setDeviceAutoOff(orgAutoOnOff); // Avoid auto-off causing
		// BT
		// trouble
		c.saveSettings();
	}

	/**
	 * Request to the user to know if he agrees with overwrite or not.
	 */
	private void requestLogOverwriteConfirmation() {
		// Log is not the same - delete the log and reopen.
		BT747MessageBox mb;
		final String[] mbStr = { Txt.getString(Txt.OVERWRITE),
				Txt.getString(Txt.ABORT_DOWNLOAD) };
		mb = new BT747MessageBox(Txt.getString(Txt.TITLE_ATTENTION), Txt
				.getString(Txt.DATA_NOT_SAME), mbStr);
		mb.popupBlockingModal();
		try {
			c.replyToOkToOverwrite(mb.getPressedButtonIndex() == 0);
		} catch (BT747Exception e) {
			notifyBT747Exception(e);
		}
	}

	/**
	 * Menu options for erase pop up.
	 */
	private String[] eraseWait;
	/**
	 * Erase pop up.
	 */
	private BT747MessageBox mbErase;

	/**
	 * Show the pop up.
	 */
	private void createErasePopup() {
		mbErase.popupModal();
	}

	/**
	 * Relive the pop up.
	 */
	private void removeErasePopup() {
		mbErase.unpop();
	}

	/**
	 * The user requested to stop waiting for erasal.
	 */
	private void stopErase() {
		c.stopErase();
	}

	public final static void notifyBT747Exception(final BT747Exception e) {
		String msg = e.getMessage();
		if (msg.equals(BT747Exception.ERR_COULD_NOT_OPEN)) {
			couldNotOpenFileMessage(e.getCause().toString());
		} else {
			String box = e.getCause().toString() + '|' + e.getMessage();
			(new BT747MessageBox(Txt.getString(Txt.ERROR), box))
					.popupBlockingModal();
		}
	}

	/**
	 * Show error message that file could not be opened.
	 * 
	 * @param fileName
	 *            The file that could not be opened.
	 */
	private static void couldNotOpenFileMessage(final String fileName) {
		(new BT747MessageBox(Txt.getString(Txt.ERROR), Txt
				.getString(Txt.COULD_NOT_OPEN)
				+ fileName + Txt.getString(Txt.CHK_PATH))).popupBlockingModal();
	}
}
