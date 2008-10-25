package bt747.j2se_view;

import gps.BT747Constants;
import gps.connection.GPSPort;
import gps.connection.GPSrxtx;
import gps.log.GPSRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.HashSet;

import javax.swing.JOptionPane;

import bt747.model.AppSettings;
import bt747.model.BT747View;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.sys.Generic;
import bt747.sys.Settings;

public final class J2SEAppController extends Controller {

    private final static String platform = java.lang.System
            .getProperty("os.name");
    private final static String SETTINGS_NAME = "BT747SettingsJ2SE.pdb";
    private final static String CONFIG_FILE_NAME = java.lang.System
            .getProperty(
                    "bt747_settings", // bt747_settings or default value
                    ((java.lang.System.getProperty("user.home").length() != 0) ? java.lang.System
                            .getProperty("user.home")
                            + java.lang.System.getProperty("file.separator")
                            + SETTINGS_NAME
                            : (

                            (platform.startsWith("Win32")
                                    || platform.startsWith("Windows") || platform
                                    .startsWith("Mac")) ? SETTINGS_NAME
                                    : "/My Documents/BT747/" + SETTINGS_NAME)));

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
    public J2SEAppController(final Model model) {
        initGpsPort();

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

    private static final String getString(final String s) {
        return java.util.ResourceBundle.getBundle("bt747/j2se_view/Bundle")
                .getString(s);
    }

    /** Options for the first warning message. */
    private static final String[] C_ERASE_OR_CANCEL = {
            getString("ERASE_BUTTON"), getString("CANCEL_BUTTON") };
    /** Options for the first warning message. */
    private static final String[] C_YES_OR_CANCEL = { getString("YES_BUTTON"),
            getString("CANCEL_BUTTON") };
    /** Options for the second warning message - reverse order on purpose. */
    private static final String[] C_CANCEL_OR_CONFIRM_ERASE = { getString("CANCEL_BUTTON"),
            getString("CONFIRM_ERASE_BUTTON") };

    /**
     * A 'recovery Erase' attempts to recover memory that was previously
     * identified as 'bad'.
     */
    public final void recoveryErase() {
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(getString("ERASE_WARNING_TITLE"), getString("ERASE_WARNING_1_TEXT"),
                C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(getString("ERASE_WARNING_TITLE"), getString("ERASE_WARNING_2_TEXT"),
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
        mb = new MessageBox(getString("ATTENTION"),
                getString("FORMAT_ERASE_WARNING_TEXT"), C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(getString("ATTENTION"),
                    getString("FORMAT_ERASE_WARNING2_TEXT"), C_CANCEL_OR_CONFIRM_ERASE);
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
        mb = new MessageBox(true, getString("ATTENTION"),
                getString("CHANGE_LOG_FORMAT_TEXT"), C_YES_OR_CANCEL);
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
        // TODO: Use JOptionPane.showMessageDialog
        /** Object to open multiple message boxes */
        MessageBox mb;
        mb = new MessageBox(getString("ERASE_WARNING_TITLE"),
                getString("ERASE_WARNING_TEXT"), C_ERASE_OR_CANCEL);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            mb = new MessageBox(getString("ATTENTION"), getString("ERASE_WARNING_2_TEXT"),
                    C_CANCEL_OR_CONFIRM_ERASE);
            mb.popupBlockingModal();
            if (mb.getPressedButtonIndex() == 1) {
                // Erase log
                c.eraseLog();
            }
        }
    }

    public final void doFactoryReset() {
        MessageBox mb;
        String[] szExitButtonArray = {
                getString("YES_BUTTON"),
                getString("NO_BUTTON"), };
        mb = new MessageBox(getString("ATTENTION"), getString("FACT_RESET_TEXT"),
                szExitButtonArray);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            // Exit application
            c.doFullColdStart();
        }

    }

    public final void setFlashConfig(final boolean lock, final int updateRate,
            final int baudRate, final int periodGLL, final int periodRMC,
            final int periodVTG, final int periodGSA, final int periodGSV,
            final int periodGGA, final int periodZDA, final int periodMCHN) {
        MessageBox mb;
        String[] mbStr = { getString("WRITE_FLASH_BUTTON"), getString("CANCEL_BUTTON") };
        mb = new MessageBox(getString("ATTENTION"), getString("FLASH_LIMITED_TEXT"),
                mbStr);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            c.setFlashUserOption(lock, updateRate, baudRate, periodGLL,
                    periodRMC, periodVTG, periodGSA, periodGSV, periodGGA,
                    periodZDA, periodMCHN);
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
            errorMsg = getString("COULD_NOT_OPEN_FILE") + errorInfo;
            Generic.debug(errorMsg);
            new MessageBox(getString("ERROR_TITLE"), errorMsg).popupBlockingModal();
            break;
        case BT747Constants.ERROR_NO_FILES_WERE_CREATED:
            (new MessageBox(getString("WARNING_TITLE"), getString("NO_FILES_CREATED") ))
                    .popupBlockingModal();
            break;
        case BT747Constants.ERROR_READING_FILE:
            new MessageBox(getString("ERROR_TITLE"), getString("PROBLEM_READING")+ errorInfo)
                    .popupBlockingModal();
            break;
        default:
            break;
        }
    }

    /**
     * The list of views attached to this controller.
     */
    private HashSet<Object> views = new HashSet<Object>();

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
    protected final void performOperationsAfterGPSConnect() {
        if (m.isConnected()) {
            if (m.getBooleanOpt(AppSettings.IS_STOP_LOGGING_ON_CONNECT)) {
                c.stopLog(); // First command could fail, so repeat.
                c.stopLog();
            }
            super.performOperationsAfterGPSConnect();
            saveSettings();
        }
    }

    private void initAppSettings() {
        AppSettings.defaultBaseDirPath = "/BT747";

        if ((Settings.getAppSettings() == null)
                || (Settings.getAppSettings().length() < 100)
                || java.lang.System.getProperty("bt747_settings") != null) {
            Settings.setAppSettings(new String(new byte[2048]));
            int readLength = 0;

            FileInputStream preferencesFile = null;
            try {
                preferencesFile = new FileInputStream(CONFIG_FILE_NAME);
                readLength = preferencesFile.available();
                if (readLength >= 100) {
                    byte[] appSettingsArray = new byte[2048];

                    preferencesFile.read(appSettingsArray, 0, readLength);
                    Settings.setAppSettings(new String(appSettingsArray));
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

    public final void saveSettings() {
        File preferencesFile = new File("");
        try {
            File m_Dir = new File(CONFIG_FILE_NAME.substring(0,
                    CONFIG_FILE_NAME.lastIndexOf('/')));
            if (!m_Dir.exists()) {
                m_Dir.mkdirs();
            }
        } catch (Exception e) {
            // Vm.debug("Exception new log delete");
            // e.printStackTrace();
        }
        try {
            preferencesFile = new File(CONFIG_FILE_NAME);
            if (preferencesFile.exists()) {
                preferencesFile.delete();
            }
        } catch (Exception e) {
            // Vm.debug("Exception new log delete");
        }
        try {
            preferencesFile = new File(CONFIG_FILE_NAME);
            preferencesFile.createNewFile();
            FileOutputStream os;
            os = new FileOutputStream(CONFIG_FILE_NAME);
            os.write(Settings.getAppSettings().getBytes(), 0, Settings
                    .getAppSettings().length());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGpsPort() {
        GPSPort gpsPort;

        try {
            gpsPort = new gps.connection.GPSRxTxPort();
            gpsPort.setPort(4);
            GPSrxtx.setGpsPortInstance(gpsPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
