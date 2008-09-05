package bt747.model;

import gps.BT747Constants;
import gps.log.GPSRecord;
import moio.util.HashSet;

import bt747.Txt;
import bt747.io.File;
import bt747.sys.Settings;
import bt747.ui.MessageBox;

//import moio.util.Iterator;  Needed later when communicating with views.

public class AppController extends Controller {

    private static String CONFIG_FILE_NAME =
    // #if RXTX java.lang.System.getProperty("bt747_settings", // bt747_settings
    // or default value
    // #if RXTX ((java.lang.System.getProperty("user.home").length()!=0) ?
    // #if RXTX
    // java.lang.System.getProperty("user.home")+java.lang.System.getProperty("file.separator")+"SettingsBT747.pdb":(

    (bt747.sys.Settings.platform.startsWith("Win32")
            || bt747.sys.Settings.platform.startsWith("Windows") || bt747.sys.Settings.platform
            .startsWith("Mac")
    // #if RXTX || java.lang.System.getProperty("os.name").startsWith("Mac")
    ) ? "SettingsBT747.pdb" : "/My Documents/BT747/SettingsBT747.pdb"
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

        initAppSettings();
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

    /** Options for the first warning message. */
    private static final String[] C_ERASE_OR_CANCEL = { Txt.ERASE, Txt.CANCEL };
    /** Options for the first warning message. */
    private static final String[] C_YES_OR_CANCEL = { Txt.YES, Txt.CANCEL };
    /** Options for the second warning message - reverse order on purpose. */
    private static final String[] C_CANCEL_OR_CONFIRM_ERASE = { Txt.CANCEL,
            Txt.CONFIRM_ERASE };

    /**
     * A 'recovery Erase' attempts to recover memory that was previously
     * identified as 'bad'.
     */
    public final void recoveryErase() {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning,
                C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning2,
                    C_CANCEL_OR_CONFIRM_ERASE);
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
        MessageBox mb;
        mb = new MessageBox(Txt.TITLE_ATTENTION,
                Txt.C_msgWarningFormatAndErase, C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(Txt.TITLE_ATTENTION,
                    Txt.C_msgWarningFormatAndErase2, C_CANCEL_OR_CONFIRM_ERASE);
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
        MessageBox mb;
        mb = new MessageBox(true, Txt.TITLE_ATTENTION,
                Txt.C_msgWarningFormatIncompatibilityRisk, C_YES_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            c.setLogFormat(logFormat);
        }
    }

    /**
     * (User) request to change the log format. Warns about requirement to erase
     * the log too.
     */
    public final void eraseLogFormat() {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning,
                C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(Txt.TITLE_ATTENTION, Txt.C_msgEraseWarning2,
                    C_CANCEL_OR_CONFIRM_ERASE);
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
            errorMsg = Txt.COULD_NOT_OPEN + errorInfo;
            bt747.sys.Vm.debug(errorMsg);
            new MessageBox(Txt.ERROR, errorMsg).popupBlockingModal();
            break;
        case BT747Constants.ERROR_NO_FILES_WERE_CREATED:
            (new MessageBox(Txt.WARNING, Txt.NO_FILES_WERE_CREATED))
                    .popupBlockingModal();
            break;
        case BT747Constants.ERROR_READING_FILE:
            new MessageBox(Txt.ERROR, Txt.PROBLEM_READING + errorInfo)
                    .popupBlockingModal();
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
    protected void performOperationsAfterGPSConnect() {
        if (m.isConnected()) {
            if (m.getBooleanOpt(AppSettings.IS_STOP_LOGGING_ON_CONNECT)) {
                c.stopLog(); // First command could fail, so repeat.
                c.stopLog();
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
    private boolean isWin32LikeDevice() {
        return bt747.sys.Settings.platform.startsWith("WindowsCE")
                || bt747.sys.Settings.platform.startsWith("PocketPC")
                || (bt747.sys.Settings.platform.startsWith("Win32") && Settings.onDevice)
                || !Settings.isWaba();
    }

    private void initAppSettings() {
        if (bt747.sys.Settings.platform.startsWith("Palm")) {
            AppSettings.defaultBaseDirPath = "/Palm";
        } else if (isWin32LikeDevice()) {
            if (bt747.io.File.getCardVolumePath() == null) {
                AppSettings.defaultBaseDirPath = "/EnterYourDir";
            } else {
                AppSettings.defaultBaseDirPath = File.getCardVolumePath();

            }
        } else {
            AppSettings.defaultBaseDirPath = "/BT747";
        }

        if ((Settings.getAppSettings() == null)
                || (Settings.getAppSettings().length() < 100)
        // #if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
        ) {
            Settings.setAppSettings(new String(new byte[2048]));
            if (isWin32LikeDevice()
            // #if RXTX ||
            // java.lang.System.getProperty("os.name").startsWith("Mac")
            // #if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
            // #if RXTX ||java.lang.System.getProperty("user.home").length()!=0
            ) {
                int readLength = 0;

                // bt747.sys.Vm.debug("on Device "+bt747.sys.Settings.platform);
                // bt747.sys.Vm.debug("loading config file "+CONFIG_FILE_NAME);
                File preferencesFile = new File("");
                try {
                    preferencesFile = new File(CONFIG_FILE_NAME, File.READ_ONLY);
                    readLength = preferencesFile.getSize();
                    if (readLength >= 100) {
                        byte[] appSettingsArray = new byte[2048];

                        preferencesFile.readBytes(appSettingsArray, 0,
                                readLength);
                        Settings.setAppSettings(new String(appSettingsArray));
                    }
                } catch (Exception e) {
                    // Vm.debug("Exception new log create");
                }
                try {
                    preferencesFile.close();
                } catch (Exception e) {

                }
            }
        }
        // #if RXTX
        // if(Convert.toInt(java.lang.System.getProperty("bt747_Mac_solvelag",java.lang.System.getProperty("os.name").startsWith("Mac")?"1":"0"))==1)
        // {
        // #if RXTX AppSettings.solveMacLagProblem=true;
        // #if RXTX }
    }
    
    public final void saveSettings() {
        if (isWin32LikeDevice()
        // #if RXTX || java.lang.System.getProperty("os.name").startsWith("Mac")
        // #if RXTX ||java.lang.System.getProperty("bt747_settings")!=null
        ) {
            // bt747.sys.Vm.debug("on Device "+bt747.sys.Settings.platform);
            // bt747.sys.Vm.debug("saving config file "+CONFIG_FILE_NAME);
            File preferencesFile = new File("");
            try {
                File m_Dir = new File(CONFIG_FILE_NAME.substring(0,
                        CONFIG_FILE_NAME.lastIndexOf('/')), File.DONT_OPEN);
                if (!m_Dir.exists()) {
                    m_Dir.createDir();
                }
            } catch (Exception e) {
                // Vm.debug("Exception new log delete");
                // e.printStackTrace();
            }
            try {
                preferencesFile = new File(CONFIG_FILE_NAME, File.DONT_OPEN);
                if (preferencesFile.exists()) {
                    preferencesFile.delete();
                }
            } catch (Exception e) {
                // Vm.debug("Exception new log delete");
            }
            try {
                preferencesFile = new File(CONFIG_FILE_NAME, File.CREATE);
                preferencesFile.close();
                preferencesFile = new File(CONFIG_FILE_NAME, File.READ_WRITE);
                preferencesFile.writeBytes(
                        Settings.getAppSettings().getBytes(), 0, Settings
                                .getAppSettings().length());
                preferencesFile.close();
            } catch (Exception e) {
                // Vm.debug("Exception new log create");
                e.printStackTrace();
            }
            // bt747.sys.Vm.debug("saved config file length
            // "+Settings.appSettings.length());
        }
    }

}
