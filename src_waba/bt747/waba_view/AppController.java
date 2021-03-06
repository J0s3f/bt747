package bt747.waba_view;

import waba.sys.Settings;

import net.sf.bt747.waba.system.WabaFile;
import gps.BT747Constants;
import gps.log.GPSRecord;
import moio.util.HashSet;

import bt747.Txt;
import bt747.model.AppSettings;
import bt747.model.BT747View;
import bt747.model.Controller;
import bt747.model.Model;

import bt747.sys.File;
import bt747.sys.Generic;
import bt747.sys.interfaces.BT747Path;
import bt747.waba_view.ui.BT747MessageBox;

// #if RXTX import bt747.sys.JavaLibBridge;

//import moio.util.Iterator;  Needed later when communicating with views.

public final class AppController extends Controller {

	private static String CONFIG_FILE_NAME =
	// #if RXTX java.lang.System.getProperty("bt747_settings", // bt747_settings
	// or default value
	// #if RXTX ((java.lang.System.getProperty("user.home").length()!=0) ?
	// #if RXTX java.lang.System.getProperty("user.home")
	// #if RXTX +java.lang.System.getProperty("file.separator")
	// #if RXTX +"SettingsBT747.pdb":(

	(Settings.platform.startsWith("Win32")
			|| Settings.platform.startsWith("Windows") || Settings.platform
			.startsWith("Mac")
	// #if RXTX || java.lang.System.getProperty("os.name").startsWith("Mac")
	) ? "SettingsBT747.pdb" : "/My Documents/SettingsBT747.pdb"
	// #if RXTX )
	// #if RXTX ))
	;

	/**
	 * The lower level controller. This should become a separate instance in the
	 * future.
	 */
	private Controller c;

	/**
	 * Reference to the model.
	 */
	private Model m;

	/**
	 * @param model
	 *            The model to associate with this controller.
	 */
	public AppController(final Model model) {
		this.m = model;
		c = this; // Temporary solution until application controller methods

		m.init();
		// moved from lower level Controller.
		super.setModel(m);
		super.init();
		// c = new Controller(model);

		// Set up the (default) port handler
	}

	// The next methods are to be moved to the application controller.
	/**
	 * Convert the log given the provided parameters using other methods.
	 * 
	 * @param logType
	 *            Indicates the type of log that should be written. For example
	 *            Model.CSV_LOGTYPE .
	 * @see Model#CSV_LOGTYPE
	 * @see Model#TRK_LOGTYPE
	 * @see Model#KML_LOGTYPE
	 * @see Model#PLT_LOGTYPE
	 * @see Model#GPX_LOGTYPE
	 * @see Model#NMEA_LOGTYPE
	 * @see Model#GMAP_LOGTYPE
	 */
	public final void convertLog(final int logType) {
		if (doConvertLog(logType) != 0) {
			reportError(c.getLastError(), c.getLastErrorInfo());
		}
	}

	/**
	 * Convert the log into an array of trackpoints.
	 * 
	 * @return Array of selected trackpoints.
	 */
	public final GPSRecord[] convertLogToTrackPoints() {
		GPSRecord[] result;
		result = c.doConvertLogToTrackPoints();
		if (result == null) {
			reportError(c.getLastError(), c.getLastErrorInfo());
		}
		return result;
	}

	/**
	 * A 'recovery Erase' attempts to recover memory that was previously
	 * identified as 'bad'.
	 */
	public final void recoveryErase() {
		/** Object to open multiple message boxes */
		BT747MessageBox mb;
		mb = new BT747MessageBox(Txt.getString(Txt.TITLE_ATTENTION), Txt
				.getString(Txt.C_msgEraseWarning), new String[] {
				Txt.getString(Txt.ERASE), Txt.getString(Txt.CANCEL) });
		mb.popupBlockingModal();
		if (mb.getPressedButtonIndex() == 0) {
			mb = new BT747MessageBox(Txt.getString(Txt.TITLE_ATTENTION), Txt
					.getString(Txt.C_msgEraseWarning2),
					new String[] { Txt.getString(Txt.CANCEL),
							Txt.getString(Txt.CONFIRM_ERASE) });
			mb.popupBlockingModal();
			if (mb.getPressedButtonIndex() == 1) {
				// Erase log
				c.recoveryEraseLog();
			}
		}
	}

	/**
	 * (User) request to change the log format. Warns about requirement to erase
	 * the log too.
	 * 
	 * @param logFormat
	 *            The logFormat to set upon erase.
	 */
	public final void changeLogFormatAndErase(final int logFormat) {
		/** Object to open multiple message boxes */
		BT747MessageBox mb;
		mb = new BT747MessageBox(Txt.getString(Txt.TITLE_ATTENTION), Txt
				.getString(Txt.C_msgWarningFormatAndErase), new String[] {
				Txt.getString(Txt.ERASE), Txt.getString(Txt.CANCEL) });
		mb.popupBlockingModal();
		if (mb.getPressedButtonIndex() == 0) {
			mb = new BT747MessageBox(Txt.getString(Txt.TITLE_ATTENTION), Txt
					.getString(Txt.C_msgWarningFormatAndErase2),
					new String[] { Txt.getString(Txt.CANCEL),
							Txt.getString(Txt.CONFIRM_ERASE) });
			mb.popupBlockingModal();
			if (mb.getPressedButtonIndex() == 1) {
				// Set format and reset log
				c.setLogFormat(logFormat);
				c.eraseLog();
			}
		}
	}

	/**
	 * (User) request to change the log format. The log is not erased and may be
	 * incompatible with other applications.
	 * 
	 * @param logFormat
	 *            The new log format to set.
	 */
	public final void changeLogFormat(final int logFormat) {
		/** Object to open multiple message boxes */
		BT747MessageBox mb;
		mb = new BT747MessageBox(true, Txt.getString(Txt.TITLE_ATTENTION), Txt
				.getString(Txt.C_msgWarningFormatIncompatibilityRisk),
				new String[] { Txt.getString(Txt.YES),
						Txt.getString(Txt.CANCEL) });
		mb.popupBlockingModal();
		if (mb.getPressedButtonIndex() == 0) {
			c.setLogFormat(logFormat);
		}
	}

	/**
	 * User request to format the device. Warns about requirement to erase the
	 * log too.
	 */
	public final void eraseLogWithDialogs() {
		/** Object to open multiple message boxes */
		BT747MessageBox mb;
		mb = new BT747MessageBox(Txt.getString(Txt.TITLE_ATTENTION), Txt
				.getString(Txt.C_msgEraseWarning), new String[] {
				Txt.getString(Txt.ERASE), Txt.getString(Txt.CANCEL) });
		mb.popupBlockingModal();
		if (mb.getPressedButtonIndex() == 0) {
			mb = new BT747MessageBox(Txt.getString(Txt.TITLE_ATTENTION), Txt
					.getString(Txt.C_msgEraseWarning2),
					new String[] { Txt.getString(Txt.CANCEL),
							Txt.getString(Txt.CONFIRM_ERASE) });
			mb.popupBlockingModal();
			if (mb.getPressedButtonIndex() == 1) {
				// Erase log
				c.eraseLog();
			}
		}
	}

	/**
	 * Report an error.
	 * 
	 * @param error
	 *            The error number.
	 * @param errorInfo
	 *            A text string related to the error (filename, ...).
	 */
	private void reportError(final int error, final String errorInfo) {
		String errorMsg;
		switch (error) {
		case BT747Constants.ERROR_COULD_NOT_OPEN:
			errorMsg = Txt.getString(Txt.COULD_NOT_OPEN) + errorInfo;
			Generic.debug(errorMsg, null);
			new BT747MessageBox(Txt.getString(Txt.ERROR), errorMsg)
					.popupBlockingModal();
			break;
		case BT747Constants.ERROR_NO_FILES_WERE_CREATED:
			(new BT747MessageBox(Txt.getString(Txt.WARNING), Txt
					.getString(Txt.NO_FILES_WERE_CREATED)))
					.popupBlockingModal();
			break;
		case BT747Constants.ERROR_READING_FILE:
			new BT747MessageBox(Txt.getString(Txt.ERROR), Txt
					.getString(Txt.PROBLEM_READING)
					+ errorInfo).popupBlockingModal();
			break;
		default:
			break;
		}
	}

	/**
	 * The list of views attached to this controller.
	 */
	private HashSet views = new HashSet();

	/**
	 * Attach a view to the controller.
	 * 
	 * @param view
	 *            The view that must be attached.
	 */
	public final void addView(final BT747View view) {
		views.add(view);
		view.setController(this);
		view.setModel(this.m);
	}

	/*
	 * Overriding the operations to be performed after successfull connect.
	 * 
	 * @see bt747.model.Controller#performOperationsAfterGPSConnect()
	 */
	public final void performOperationsAfterGPSConnect() {
		if (m.isConnected()) {
			if (m.getBooleanOpt(AppSettings.IS_STOP_LOGGING_ON_CONNECT)) {
				c.setLoggingActive(false); // First command could fail, so
											// repeat.
				c.setLoggingActive(false);
			}
			super.performOperationsAfterGPSConnect();
			saveSettings();
		}
	}

	// protected void postEvent(final int type) {
	// Iterator it = views.iterator();
	// while (it.hasNext()) {
	// BT747View l=(BT747View)it.next();
	// Event e=new Event(l, type, null);
	// l.newEvent(e);
	// }
	// }
	private final static boolean isWin32LikeDevice() {
		return waba.sys.Settings.platform.startsWith("WindowsCE")
				|| waba.sys.Settings.platform.startsWith("PocketPC")
				|| (waba.sys.Settings.platform.startsWith("Win32") && Settings.onDevice);
	}

	public final static void initAppSettings() {
		AppSettings.setDefaultChunkSize(waba.sys.Settings.onDevice ? 220
				: 0x10000);
		AppSettings.setDefaultTraversable(waba.sys.Settings.onDevice
				&& (!waba.sys.Settings.platform.startsWith("Palm")));

		if (waba.sys.Settings.platform.startsWith("Palm")) {
			AppSettings.setDefaultBaseDirPath("/Palm");
		} else if (isWin32LikeDevice()) {
			if (WabaFile.getCardVolumePath() == null) {
				AppSettings.setDefaultBaseDirPath("/EnterYourDir");
			} else {
				AppSettings.setDefaultBaseDirPath(WabaFile.getCardVolumePath());

			}
		} else {
			AppSettings.setDefaultBaseDirPath("/BT747");
		}

		if ((bt747.sys.Settings.getAppSettings() == null)
				|| (bt747.sys.Settings.getAppSettings().length() < 100)
		// #if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
		) {
			bt747.sys.Settings.setAppSettings(new String(
					new byte[AppSettings.SIZE]));

			if (isWin32LikeDevice()
			// #if RXTX ||java.lang.System.getProperty("os.name")
			// #if RXTX .startsWith("Mac")
			// #if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
			// #if RXTX ||java.lang.System.getProperty("user.home").length()!=0
			) {
				int readLength = 0;

				// bt747.sys.Vm.debug("on Device "+bt747.sys.Settings.platform);
				// bt747.sys.Vm.debug("loading config file "+CONFIG_FILE_NAME);
				File preferencesFile = null;
				try {
					preferencesFile = new File(new BT747Path(CONFIG_FILE_NAME),
							File.READ_ONLY);
					readLength = preferencesFile.getSize();
					if (readLength >= 100) {
						final int len = Math.max(AppSettings.SIZE, readLength);
						byte[] appSettingsArray = new byte[len];

						preferencesFile.readBytes(appSettingsArray, 0,
								readLength);
						bt747.sys.Settings.setAppSettings(new String(
								appSettingsArray));
					}
				} catch (Exception e) {
					// Vm.debug("Exception new log create");
				}

				try {
					if (preferencesFile != null) {
						preferencesFile.close();
					}
				} catch (Exception e) {

				}
			}
		}

		// #if RXTX if(JavaLibBridge.toInt(
		// #if RXTX java.lang.System.getProperty("bt747_Mac_solvelag",
		// #if RXTX java.lang.System.getProperty("os.name")
		// #if RXTX .startsWith("Mac")?"1":"0"))==1) {
		// #if RXTX AppSettings.setSolveMacLagProblem(true);
		// #if RXTX }
	}

	/* @author Herbert Geus (initial code for saving settings on WindowsCE) */

	public final void saveSettings() {
		if (Generic.isDebug()) {
			Generic.debug("Platform " + Settings.platform, null);
			// #if RXTX Generic.debug( "os.name:" +
			// #if RXTX java.lang.System.getProperty("os.name"), null);
			// #if RXTX Generic.debug( "bt747_settings:" +
			// #if RXTX java.lang.System.getProperty("bt747_settings"), null);
			Generic.debug("Saving config file " + CONFIG_FILE_NAME, null);
			Generic.debug("If true: " + isWin32LikeDevice(), null);
		}
		if (isWin32LikeDevice()
		// #if RXTX ||java.lang.System.getProperty("os.name").startsWith("Mac")
		// #if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
		) {
			File preferencesFile = null;
			BT747Path configPath = new BT747Path(CONFIG_FILE_NAME);
			try {
				File dir = new File(configPath.proto(CONFIG_FILE_NAME
						.substring(0, CONFIG_FILE_NAME.lastIndexOf('/'))),
						File.DONT_OPEN);
				if (!dir.exists()) {
					dir.createDir();
				}
			} catch (Exception e) {
				// Vm.debug("Exception new log delete");
				// e.printStackTrace();
			}
			try {
				preferencesFile = new File(configPath, File.DONT_OPEN);
				if (preferencesFile.exists()) {
					preferencesFile.delete();
				}
			} catch (Exception e) {
				Generic.debug("Exception config delete", e);
			}
			try {
				preferencesFile = new File(configPath, File.CREATE);
				preferencesFile.close();
				preferencesFile = new File(configPath, File.READ_WRITE);
				preferencesFile.writeBytes(bt747.sys.Settings.getAppSettings()
						.getBytes(), 0, bt747.sys.Settings.getAppSettings()
						.length());
				preferencesFile.close();
			} catch (Exception e) {
				Generic.debug("Exception config create", e);
			}
			// bt747.sys.Vm.debug("saved config file length
			// "+Settings.appSettings.length());
		}
	}

}
