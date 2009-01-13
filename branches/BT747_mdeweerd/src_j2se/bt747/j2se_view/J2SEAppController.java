// ********************************************************************
// *** BT 747 ***
// *** April 14, 2007 ***
// *** (c)2007 Mario De Weerd ***
// *** m.deweerd@ieee.org ***
// *** ********************************************************** ***
// *** Software is provided "AS IS," without a warranty of any ***
// *** kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
// *** INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS ***
// *** FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY ***
// *** EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
// *** IS ASSUMED BY THE USER. ***
// *** See the GNU General Public License Version 3 for details. ***
// *** *********************************************************** ***
package bt747.j2se_view;

import gps.BT747Constants;
import gps.connection.GPSPort;
import gps.connection.GPSrxtx;
import gps.log.GPSRecord;
import gps.log.TracksAndWayPoints;
import gps.log.out.AllWayPointStyles;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import bt747.Version;
import bt747.model.AppSettings;
import bt747.model.BT747View;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Generic;
import bt747.sys.Settings;
import bt747.sys.interfaces.BT747Vector;

public final class J2SEAppController extends J2SEController {

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

    private static Image appIcon;
    private static final String iconPath = "icons/bt747_16x16.gif";

    private static Image app128Icon;
    private static final String icon128Path = "icons/bt747_128x128.gif";

    public static final String MAPCACHEDIRECTORYPROPERTY = "mapcachedirectory";

    J2SEAppModel m;

    private static final void setAppIcon() {
        URL u = BT747Main.class.getResource("/" + iconPath);

        if (u != null) {
            appIcon = Toolkit.getDefaultToolkit().getImage(u);
        } else {
            appIcon = Toolkit.getDefaultToolkit().getImage(iconPath);
        }

        u = BT747Main.class.getResource("/" + icon128Path);

        if (u != null) {
            app128Icon = Toolkit.getDefaultToolkit().getImage(u);
        } else {
            app128Icon = Toolkit.getDefaultToolkit().getImage(icon128Path);
        }
    }

    static {
        setAppIcon();
    }

    public final Image getIcon16() {
        return appIcon;
    }

    public final Image getIcon128() {
        return app128Icon;
    }

    /**
     * The lower level controller. This should become a separate instance in
     * the future.
     */
    private Controller c;

    /** Options for the first warning message. */
    private static String[] C_ERASE_OR_CANCEL;
    /** Options for the first warning message. */
    private static String[] C_YES_OR_CANCEL;
    /** Options for the second warning message - reverse order on purpose. */
    private static String[] C_CANCEL_OR_CONFIRM_ERASE;

    private void initStaticsFirstTime() {
        if (bundle == null) {
            bundle = java.util.ResourceBundle
                    .getBundle("bt747/j2se_view/Bundle");
            final String[] my_ERASE_OR_CANCEL = { getString("ERASE_BUTTON"),
                    getString("CANCEL_BUTTON") };
            /** Options for the first warning message. */
            final String[] my_YES_OR_CANCEL = { getString("YES_BUTTON"),
                    getString("CANCEL_BUTTON") };
            /**
             * Options for the second warning message - reverse order on
             * purpose.
             */
            final String[] my_CANCEL_OR_CONFIRM_ERASE = {
                    getString("CANCEL_BUTTON"),
                    getString("CONFIRM_ERASE_BUTTON") };
            C_ERASE_OR_CANCEL = my_ERASE_OR_CANCEL;
            C_YES_OR_CANCEL = my_YES_OR_CANCEL;
            C_CANCEL_OR_CONFIRM_ERASE = my_CANCEL_OR_CONFIRM_ERASE;
        }
        /** Options for the first warning message. */
    }

    public static final Locale localeFromString(final String localeStr) {
        if (localeStr.length() != 0) {
            String arg1 = "";
            String arg2 = "";
            String arg3 = "";
            if (localeStr.length() >= 2) {
                arg1 = localeStr.substring(0, 2);

            }
            if (localeStr.length() >= 5) {
                arg2 = localeStr.substring(3, 5);

            }
            if (localeStr.length() >= 8) {
                arg3 = localeStr.substring(6);

            }
            return (new Locale(arg1, arg2, arg3));
        }
        return Locale.getDefault();
    }

    /**
     * @param model
     *                The model to associate with this controller.
     */
    public J2SEAppController(final J2SEAppModel model) {
        initGpsPort();

        m = model;
        c = this; // Temporary solution until application controller methods

        initAppSettings();
        m.init();
        // moved from lower level Controller.
        super.setModel(m);
        super.init();
        // c = new Controller(model);
        final String localeStr = m.getStringOpt(AppSettings.LANGUAGE);
        Locale.setDefault(localeFromString(localeStr));
        // Initialised here to be sure that the app language can be changed
        // after
        // static evaluation.
        initStaticsFirstTime();
        myLookAndFeel();
        c.setWayPointStyles(new AllWayPointStyles());
    }

    private int startTimeNoOffset;
    private int endTimeNoOffset;
    private int timeOffset;

    /**
     * 
     */
    public void setLogConversionParameters() {
        int startTime;
        int endTime;

        startTime = timeOffset + startTimeNoOffset;
        endTime = timeOffset + endTimeNoOffset;
        // Now actually set time filter.
        c.setFilterStartTime((startTime));
        c.setFilterEndTime((endTime));
    }

    // The next methods are to be moved to the application controller.
    /**
     * Convert the log given the provided parameters using other methods.
     * 
     * @param logType
     *                Indicates the type of log that should be written. For
     *                example Model.CSV_LOGTYPE .
     * @see Model#CSV_LOGTYPE
     * @see Model#TRK_LOGTYPE
     * @see Model#KML_LOGTYPE
     * @see Model#PLT_LOGTYPE
     * @see Model#GPX_LOGTYPE
     * @see Model#NMEA_LOGTYPE
     * @see Model#GMAP_LOGTYPE
     */

    public final void convertLog(final int logType) {
        c.setUserWayPoints(m.getPositionData().getSortedGPSRecords());
        if (logType == Model.KMZ_LOGTYPE) {
            if (doConvertLog(logType, new GPSKMZFile(), ".kmz") != 0) {
                reportError(c.getLastError(), c.getLastErrorInfo());
            }
        } else {
            if (doConvertLog(logType) != 0) {
                reportError(c.getLastError(), c.getLastErrorInfo());
            }
        }
        m.getPositionData().userWaypointsUpdated();
    }

    /**
     * Convert the log into an array of trackpoints.
     * 
     * @return Array of selected trackpoints.
     */
    public final TracksAndWayPoints convertLogToTrackAndWayPoints() {
        TracksAndWayPoints result;
        c.setUserWayPoints(m.getPositionData().getSortedGPSRecords());
        result = c.doConvertLogToTracksAndWayPoints();
        if (result == null) {
            reportError(c.getLastError(), c.getLastErrorInfo());
        }
        m.getPositionData().dataUpdated();
        return result;
    }

    /**
     * The resource bundle used for localization.
     */
    private static ResourceBundle bundle = null;

    /**
     * I18N. Internationalization - get the localized string.
     * 
     * @param s
     *                String reference for localization.
     * @return Localized String.
     */
    public static final String getString(final String s) {
        try {
            return bundle.getString(s);
        } catch (final Exception e) {
            Generic.debug("No text found for \"" + s + "\"", e);
            return s;
        }
    }

    public static final Icon getIcon(final String iconPath) {
        Icon i = null;
        try {
            final URL u = BT747Main.class
                    .getResource("/bt747/j2se_view/resources/" + iconPath);
            i = new javax.swing.ImageIcon(u);
        } catch (final Exception e) {
            // TODO: handle exception
        }
        return i;
    }

    /**
     * A 'recovery Erase' attempts to recover memory that was previously
     * identified as 'bad'.
     */
    public final void recoveryErase() {
        int choice;
        choice = JOptionPane.showOptionDialog(rootFrame,
                getString("ERASE_WARNING_1_TEXT"),
                getString("ERASE_WARNING_TITLE"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, C_ERASE_OR_CANCEL, C_ERASE_OR_CANCEL[1]);

        if (choice == 0) {
            choice = JOptionPane.showOptionDialog(rootFrame,
                    getString("ERASE_WARNING_2_TEXT"),
                    getString("ERASE_WARNING_TITLE"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null,
                    C_CANCEL_OR_CONFIRM_ERASE, C_CANCEL_OR_CONFIRM_ERASE[0]);
            if (choice == 1) {
                // Erase log
                c.recoveryEraseLog();
            }

        }
    }

    /**
     * (User) request to change the log format. Warns about requirement to
     * erase the log too.
     * 
     * @param logFormat
     *                The logFormat to set upon erase.
     */
    public final void changeLogFormatAndErase(final int logFormat) {
        int choice;
        choice = JOptionPane.showOptionDialog(rootFrame,
                getString("FORMAT_ERASE_WARNING_TEXT"),
                getString("ATTENTION"), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, C_ERASE_OR_CANCEL,
                C_ERASE_OR_CANCEL[1]);

        if (choice == 0) {
            choice = JOptionPane.showOptionDialog(rootFrame,
                    getString("FORMAT_ERASE_WARNING2_TEXT"),
                    getString("ATTENTION"), JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null,
                    C_CANCEL_OR_CONFIRM_ERASE, C_CANCEL_OR_CONFIRM_ERASE[0]);
            if (choice == 1) {
                // Set format and reset log
                c.setLogFormat(logFormat);
                c.eraseLog();
            }
        }
    }

    /**
     * (User) request to change the log format. The log is not erased and may
     * be incompatible with other applications.
     * 
     * @param logFormat
     *                The new log format to set.
     */
    public final void changeLogFormat(final int logFormat) {
        int choice;
        choice = JOptionPane.showOptionDialog(rootFrame,
                getString("CHANGE_LOG_FORMAT_TEXT"), getString("ATTENTION"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, C_YES_OR_CANCEL, C_YES_OR_CANCEL[1]);

        if (choice == 0) {
            c.setLogFormat(logFormat);
        }
    }

    /**
     * (User) request to change the log format. Warns about requirement to
     * erase the log too.
     */
    public final void eraseLogWithDialogs() {
        int choice;
        choice = JOptionPane.showOptionDialog(rootFrame,
                getString("ERASE_WARNING_1_TEXT"),
                getString("ERASE_WARNING_TITLE"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, C_ERASE_OR_CANCEL, C_ERASE_OR_CANCEL[1]);

        if (choice == 0) {
            choice = JOptionPane.showOptionDialog(rootFrame,
                    getString("ERASE_WARNING_2_TEXT"),
                    getString("ERASE_WARNING_TITLE"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null,
                    C_CANCEL_OR_CONFIRM_ERASE, C_CANCEL_OR_CONFIRM_ERASE[0]);
            if (choice == 1) {
                // Erase log
                c.eraseLog();
            }
        }
    }

    /**
     * Perform a factory reset, but first request confirmation from the user.
     */
    public final void doFactoryReset() {
        int choice;
        choice = JOptionPane.showOptionDialog(rootFrame,
                getString("FACT_RESET_TEXT"), getString("ATTENTION"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, null, 0);

        if (choice == JOptionPane.OK_OPTION) {
            c.doFullColdStart();
        }

    }

    /**
     * Change the flashconfiguration but first request confirmation from the
     * user. The MTK device stores a number of settings in its internal flash
     * which is different from the log memory. These settings are restored
     * after loss of power for example.
     * {@link Controller#setFlashUserOption(boolean, int, int, int, int, int, int, int, int, int, int)}
     * 
     * @param lock
     *                When true, subsequent changes in these settings will be
     *                impossible.
     * @param updateRate
     *                The 'fix period' of the GPS in ms. When this is 200,
     *                then the Fix is 5Hz.
     * @param baudRate
     *                The speed of the serial communication of the MTK
     *                chipset. Be carefull - this may be the internal speed -
     *                not the external speed!
     * @param periodGLL
     *                The period of emission of the GLL sentence (relative to
     *                the fix).
     * @param periodRMC
     *                The period of emission of the RMC sentence (relative to
     *                the fix).
     * @param periodVTG
     *                The period of emission of the VTG sentence (relative to
     *                the fix).
     * @param periodGSA
     *                The period of emission of the GSA sentence (relative to
     *                the fix).
     * @param periodGSV
     *                The period of emission of the GSV sentence (relative to
     *                the fix).
     * @param periodGGA
     *                The period of emission of the GGA sentence (relative to
     *                the fix).
     * @param periodZDA
     *                The period of emission of the ZDA sentence (relative to
     *                the fix).
     * @param periodMCHN
     *                The period of emission of the MCHN sentence (relative to
     *                the fix).
     */
    public final void setFlashConfig(final boolean lock,
            final int updateRate, final int baudRate, final int periodGLL,
            final int periodRMC, final int periodVTG, final int periodGSA,
            final int periodGSV, final int periodGGA, final int periodZDA,
            final int periodMCHN) {
        final String[] mbStr = { getString("WRITE_FLASH_BUTTON"),
                getString("CANCEL_BUTTON") };
        int choice;
        choice = JOptionPane.showOptionDialog(rootFrame,
                getString("FLASH_LIMITED_TEXT"), getString("ATTENTION"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, mbStr, mbStr[1]);

        if (choice == JOptionPane.OK_OPTION) {
            c.setFlashUserOption(lock, updateRate, baudRate, periodGLL,
                    periodRMC, periodVTG, periodGSA, periodGSV, periodGGA,
                    periodZDA, periodMCHN);
        }
    }

    /**
     * Report an error.
     * 
     * @param error
     *                The error number.
     * @param errorInfo
     *                A text string related to the error (filename, ...).
     */
    private void reportError(final int error, final String errorInfo) {
        String errorMsg;
        switch (error) {
        case BT747Constants.ERROR_COULD_NOT_OPEN:
            errorMsg = getString("COULD_NOT_OPEN_FILE") + errorInfo;
            Generic.debug(errorMsg);
            JOptionPane.showMessageDialog(rootFrame, errorMsg,
                    getString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
            break;
        case BT747Constants.ERROR_NO_FILES_WERE_CREATED:
            JOptionPane.showMessageDialog(rootFrame,
                    getString("NO_FILES_CREATED"),
                    getString("WARNING_TITLE"), JOptionPane.WARNING_MESSAGE);
            break;
        case BT747Constants.ERROR_READING_FILE:
            JOptionPane.showMessageDialog(rootFrame,
                    getString("PROBLEM_READING") + errorInfo,
                    getString("ERROR_TITLE"), JOptionPane.ERROR_MESSAGE);
            break;
        default:
            break;
        }
    }

    /**
     * The list of views attached to this controller.
     */
    private final HashSet<Object> views = new HashSet<Object>();

    /**
     * Attach a view to the controller.
     * 
     * @param view
     *                The view that must be attached.
     */
    public final void addView(final BT747View view) {
        views.add(view);
        view.setController(this);
        view.setModel(m);
    }

    /*
     * Overriding the operations to be performed after successfull connect.
     * 
     * @see bt747.model.Controller#performOperationsAfterGPSConnect()
     */
    protected final void performOperationsAfterGPSConnect() {
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

    /**
     * Initialise the application settings.
     */
    private void initAppSettings() {
        J2SEAppModel.defaultBaseDirPath = java.lang.System
                .getProperty("user.home");

        if ((Settings.getAppSettings() == null)
                || (Settings.getAppSettings().length() < 100)
                || (java.lang.System.getProperty("bt747_settings") != null)) {
            Settings.setAppSettings(new String(new byte[2048]));
            int readLength = 0;

            FileInputStream preferencesFile = null;
            try {
                preferencesFile = new FileInputStream(CONFIG_FILE_NAME);
                readLength = preferencesFile.available();
                if (readLength >= 100) {
                    final byte[] appSettingsArray = new byte[2048];

                    preferencesFile.read(appSettingsArray, 0, readLength);
                    Settings.setAppSettings(new String(appSettingsArray));
                }
            } catch (final Exception e) {
                // Vm.debug("Exception new log create");
            }
            try {
                if (preferencesFile != null) {
                    preferencesFile.close();
                }
            } catch (final Exception e) {

            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see bt747.model.Controller#saveSettings() Application specific
     *      implementation of saveSettings
     */
    public final void saveSettings() {
        File preferencesFile;
        try {
            final File m_Dir = new File(CONFIG_FILE_NAME.substring(0,
                    CONFIG_FILE_NAME.lastIndexOf('/')));
            if (!m_Dir.exists()) {
                m_Dir.mkdirs();
            }
        } catch (final Exception e) {
            // Vm.debug("Exception new log delete");
            // e.printStackTrace();
        }
        try {
            preferencesFile = new File(CONFIG_FILE_NAME);
            if (preferencesFile.exists()) {
                preferencesFile.delete();
            }
        } catch (final Exception e) {
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
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void initGpsPort() {
        GPSPort gpsPort;

        try {
            gpsPort = new gps.connection.GPSRxTxPort();
            gpsPort.setPort(4);
            GPSrxtx.setGpsPortInstance(gpsPort);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Erase pop up.
     */
    private JOptionPane mbErase;

    private Frame rootFrame = null;

    public void setRootFrame(final Frame f) {
        rootFrame = f;
        rootFrame.setIconImage(appIcon);
    }

    public Frame getRootFrame() {
        return rootFrame;
    }

    private JDialog mbEraseDialog;

    /**
     * Show the pop up.
     */
    public void createErasePopup() {
        final String[] eraseOption = { getString("CANCEL") };
        mbErase = new JOptionPane(getString("WAITING_ERASE_TEXT"),
                JOptionPane.WARNING_MESSAGE);
        // mbErase.add
        mbErase.setOptions(eraseOption);
        mbErase.setVisible(true);

        mbEraseDialog = mbErase.createDialog(rootFrame,
                getString("WAITING_ERASE_TITLE"));
        mbEraseDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        // mbEraseDialog.setVisible(true);
        // mbEraseDialog.addWindowListener(new WindowAdapter() {
        // public void windowClosing(WindowEvent we) {
        // setLabel("Thwarted user attempt to close window.");
        // }
        // });
        mbErase.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent e) {
                final String prop = e.getPropertyName();

                if ((mbEraseDialog != null) && mbEraseDialog.isVisible()
                        && (e.getSource() == mbErase)
                        && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                    // Stop waiting for erase
                    stopErase();
                    removeErasePopup();
                }
            }
        });
        mbEraseDialog.pack();
        mbEraseDialog.setModal(false);
        mbEraseDialog.setVisible(true);
    }

    /**
     * Remove the pop up.
     */
    public final void removeErasePopup() {
        if (mbEraseDialog != null) {
            mbEraseDialog.setVisible(false);
            mbEraseDialog = null;
            mbErase = null;
        }
    }

    /**
     * Show the about dialog box for the application.
     */
    public final void showAbout() {
        JOptionPane.showMessageDialog(rootFrame, "<html>BT747 V"
                + Version.VERSION_NUMBER + " (" + Version.DATE
                + ")<br>" // NOI18N
                + "Build: " + Version.BUILD_STR + "<br><br>"
                + getString("ABOUT_TEXT"), getString("ABOUT_TITLE"),
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(app128Icon));
    }

    /**
     * Show the information dialog box for the application.
     */
    public final void showLicense() {
        JOptionPane.showMessageDialog(rootFrame, getString("LICENSE_TEXT"),
                getString("LICENSE_TITLE"), JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(app128Icon));
    }

    /**
     * Show error message that file could not be opened.
     * 
     * @param fileName
     *                The file that could not be opened.
     */
    public void couldNotOpenFileMessage(final String fileName) {
        JOptionPane.showMessageDialog(rootFrame,
                getString("Problem_opening_file"),
                getString("The_application_could_not_open_") + fileName
                        + getString("_check_if_loc_exists")
                        + getString("in_case_it_is_an_output_file."),
                JOptionPane.WARNING_MESSAGE, null);
    }

    /**
     * Request the user if it is ok to overwrite the existing data or not.
     * 
     * @return true if data can be overwritten.
     */
    public final boolean getRequestToOverwriteFromDialog() {
        int overwriteResp;
        overwriteResp = JOptionPane.showOptionDialog(rootFrame,
                getString("OVERWRITE_DATA_QUESTION"),
                getString("OVERWRITING_DATA"), JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null /* icon */,
                null /* options */, null /* initialValue */);
        return overwriteResp == JOptionPane.OK_OPTION;
    }

    /*************************************************************************
     * Find the appropriate look and feel for the system
     ************************************************************************/
    private static final String[] lookAndFeels = {
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel", // NOI18N
            "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel", // NOI18N
            "com.sun.java.swing.plaf.gtk.GTKLookAndFeel", // NOI18N
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel", // NOI18N
            "javax.swing.plaf.metal.MetalLookAndFeel", // NOI18N
            "javax.swing.plaf.mac.MacLookAndFeel", // NOI18N
            "com.apple.mrj.swing.MacLookAndFeel", // NOI18N
            "apple.laf.AquaLookAndFeel" // NOI18N
    }; // NOI18N
    /** Index for Mac look and feel */
    private static final int C_MAC_LOOKANDFEEL_IDX = lookAndFeels.length - 3;
    /**
     * Selected Look And Feel
     */
    private static String lookAndFeel = ""; // NOI18N
    /**
     * Message build up during Look And Feel search.
     */
    public static String lookAndFeelMsg = ""; // NOI18N

    /**
     * Try setting a look and feel for the system - catch the Exception when
     * not found.
     * 
     * @return true if successfull
     */
    private final static boolean tryLookAndFeel(final String s) {
        try {
            UIManager.setLookAndFeel(s);
            lookAndFeel = s;
            lookAndFeelMsg += getString("Success_") + s + "\n";
            return true;
        } catch (final Exception e) {
        }
        lookAndFeelMsg += getString("Fail_") + s + "\n";
        return false;
    }

    /**
     * Set a good look and feel for the system.
     */
    public static void myLookAndFeel() {
        boolean lookAndFeelIsSet = false;
        if (java.lang.System.getProperty("bt747.laf") != null) {
            tryLookAndFeel(java.lang.System.getProperty("bt747.laf"));
        }

        if (java.lang.System.getProperty("os.name").toLowerCase().startsWith(
                "mac")) { // NOI18N
            for (int i = C_MAC_LOOKANDFEEL_IDX; !lookAndFeelIsSet
                    && (i < lookAndFeels.length); i++) {
                lookAndFeelIsSet = tryLookAndFeel(lookAndFeels[i]);
            }
        }
        for (int i = 0; !lookAndFeelIsSet && (i < lookAndFeels.length); i++) {
            lookAndFeelIsSet = tryLookAndFeel(lookAndFeels[i]);
        }
        if (!lookAndFeelIsSet) {
            tryLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        // List available managers
        final LookAndFeelInfo[] a = UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < a.length; i++) {
            lookAndFeelMsg += a[i].getClassName() + "\n"; // NOI18N
        }
    }

    /**
     * An integer input verifier available for use in the GUI buildup.
     */
    public static final InputVerifier IntVerifier = new InputVerifier() {
        public boolean verify(JComponent comp) {
            boolean returnValue;
            JTextField textField = (JTextField) comp;
            try {
                Integer.parseInt(textField.getText());
                returnValue = true;
            } catch (NumberFormatException e) {
                returnValue = false;
            }
            return returnValue;
        }
    };

    /**
     * A floating point input verifier available for use in the GUI buildup.
     */
    public static final InputVerifier FloatVerifier = new InputVerifier() {

        public boolean verify(JComponent comp) {
            boolean returnValue;
            JTextField textField = (JTextField) comp;
            try {
                Float.parseFloat(textField.getText());
                returnValue = true;
            } catch (NumberFormatException e) {
                returnValue = false;
            }
            return returnValue;
        }
    };

    public final void selectMapCacheDirectory() {
        javax.swing.JFileChooser CacheDirChooser;
        final File f = new File(m
                .getStringOpt(J2SEAppModel.MAPCACHEDIRECTORY));
        CacheDirChooser = new javax.swing.JFileChooser(f);
        CacheDirChooser.setSelectedFile(f);
        CacheDirChooser
                .setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        CacheDirChooser
                .setToolTipText(getString("SelectCacheDirectory.tooltip"));
        // if (curDir.exists()) {
        // CacheDirChooser.setCurrentDirectory(getOutputFilePath());
        // }
        if (CacheDirChooser.showDialog(rootFrame,
                getString("SetCacheDir.button")) == JFileChooser.APPROVE_OPTION) {
            try {
                String relPath = CacheDirChooser.getSelectedFile()
                        .getCanonicalPath();
                if (relPath.lastIndexOf('.') == relPath.length() - 4) {
                    relPath = relPath.substring(0, relPath.length() - 4);
                }
                c.setStringOpt(J2SEAppModel.MAPCACHEDIRECTORY, relPath);
            } catch (final Exception e) {
                Generic.debug(getString("CacheDirChooser"), e);
            }
        }
    }

    /**
     * Disable a panel and its children.
     * 
     * @param panel
     * @param en
     */
    public static final void disablePanel(final JPanel panel, final boolean en) {
        Component[] l;
        l = panel.getComponents();
        for (final Component component : l) {
            component.setEnabled(en);
            if (component.getClass() == JPanel.class) {
                disablePanel((JPanel) component, en);
            }
        }

    }

    private boolean changeToMap = false;

    /**
     * 
     */
    public void doLogConversion(final int selectedFormat) {
        new Thread("convert") {
            public final void run() {
                // setLogConversionParameters();
                switch (selectedFormat) {
                case J2SEAppModel.ARRAY_LOGTYPE:
                    TracksAndWayPoints r;
                    r = convertLogToTrackAndWayPoints();
                    if (r != null) {
                        final PositionTablePanel trackPanel = new PositionTablePanel();
                        trackPanel.setGpsRecords(r.getTrackPoints());
                        final PositionTablePanel waypointPanel = new PositionTablePanel();
                        waypointPanel.setGpsRecords(r.getWayPoints());
                        m.getPositionData().setWayPoints(r.getWayPoints());
                        final Vector<List<GPSRecord>> trks = new Vector<List<GPSRecord>>(
                                r.tracks.size());
                        for (int i = 0; i < r.tracks.size(); i++) {
                            final BT747Vector trk = (BT747Vector) r.tracks
                                    .elementAt(i);
                            final Vector<GPSRecord> ntrk = new Vector<GPSRecord>(
                                    trk.size());
                            for (int j = 0; j < trk.size(); j++) {
                                ntrk.add((GPSRecord) trk.elementAt(j));
                            }
                            trks.add(ntrk);
                        }
                        m.getPositionData().setTracks(trks);
                    } else {
                        m.getPositionData().setWayPoints(null);
                        m.getPositionData().setTracks(null);
                    }
                    if (changeToMap) {
                        m.postModelEvent(new ModelEvent(
                                J2SEAppModel.CHANGE_TO_MAP, null));
                    }
                    break;
                default:
                    convertLog(selectedFormat);
                    break;
                }
            }
        }.start();
    }

    public final J2SEAppModel getAppModel() {
        return m;
    }

    public final int getStartTimeNoOffset() {
        return startTimeNoOffset;
    }

    public final void setStartTimeNoOffset(final int startTimeNoOffset) {
        this.startTimeNoOffset = startTimeNoOffset;
    }

    public final int getEndTimeNoOffset() {
        return endTimeNoOffset;
    }

    public final void setEndTimeNoOffset(final int endTimeNoOffset) {
        this.endTimeNoOffset = endTimeNoOffset;
    }

    public final int getTimeOffset() {
        return timeOffset;
    }

    public final void setTimeOffset(final int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public final void setChangeToMap(final boolean changeToMap) {
        this.changeToMap = changeToMap;
    }

}
