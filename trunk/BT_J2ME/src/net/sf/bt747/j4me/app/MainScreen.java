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
package net.sf.bt747.j4me.app;

import gps.BT747Constants;
import gps.GpsEvent;
import gps.mvc.MtkController;
import gps.mvc.MtkModel;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Display;

import net.sf.bt747.j4me.app.conn.FindingGPSDevicesAlert;
import net.sf.bt747.j4me.app.conn.InitializingGPSAlert;
import net.sf.bt747.j4me.app.log.LogScreen;
import net.sf.bt747.j4me.app.screens.DelayedDialog;
import net.sf.bt747.j4me.app.screens.ErrorAlert;
import net.sf.bt747.j4me.app.screens.PathSelectionScreen;
import net.sf.bt747.j4me.app.screens.ProgressAlert;

import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.Menu;
import org.j4me.ui.MenuItem;
import org.j4me.ui.Theme;
import org.j4me.ui.UIManager;
import org.j4me.ui.components.Label;
import org.j4me.ui.components.RadioButton;

import bt747.model.AppSettings;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.interfaces.BT747Exception;

/**
 * The "Main" screen. This is the application's entry screen. It will show the
 * available special waypoints when the device is available. It will show a
 * message remembering that the device must be available if it is not
 * available.
 */
public final class MainScreen extends Dialog implements ModelListener {
    /**
     * Reference to the Application Controller.
     */
    private final AppController c;

    /**
     * Reference to the application Midlet instantiation because we need it.
     */
    private final MTKMidlet midlet;

    /**
     * The different screens that we can go to from the main screen. J4ME
     * requires that those are all created when the menu is created.
     */
    private LogDownloadScreen downloadLogScreen;
    private DeviceScreen loggerInfoScreen;
    private LogScreen logScreen;
    private DebugConfigScreen debugConfigScreen;
    private InitializingGPSAlert initialiseGPSAlert;
    private FindingGPSDevicesAlert findingGPSDevicesAlert;
    private PathSelectionScreen baseDirScreen;
    private CreditsScreen creditsScreen;
    private DeviceScreen logFieldSelectScreen;
    private DelayedDialog connectConfig;
    private PosSrvMonitorScreen posSrvScreen;
    private RadioButton appUnits;
    private RadioButton speedDisplay;

    private final java.util.Timer tm = new Timer();
    private TimerTask ttLabels;

    /**
     * We use only one instantiation of the confirmation screen (sequence).
     * This property says what it is for. This information is required when
     * the confirm screen sequence hands over control to the main screen again
     * where the result is analyzed. {@link #NO_CONFIRM} indicates the screen
     * is unused. {@link #ERASE_CONFIRM} indicates that the confirmScreen is
     * used for erase confirmation.
     */
    private int confirmScreenOption = 0;
    private final static int NO_CONFIRM = 0;
    private final static int ERASE_CONFIRM = 1;
    private final static int DOWNLOAD_OVERWRITE_CONFIRM = 2;
    private final static int AGPS_CLEAR_CONFIRM = 3;
    private final static int COLD_START_CONFIRM = 4;

    /**
     * A reference to the confirmation screen.
     */
    private ConfirmScreen confirmScreen;
    /**
     * The left menu for this screen.
     */
    private Menu rootMenu;
    /**
     * A reference to this object to use in local classes.
     */
    final private MainScreen myself = this;

    /**
     * Constructs the "Main" screen.
     * 
     * @param c
     *            Reference to the application controller
     * @param midlet
     *            Reference to the midlet object.
     */
    public MainScreen(final AppController c, final MTKMidlet midlet) {
        this.c = c;
        this.midlet = midlet;
        // UIManager.init(midlet);
        UIManager
                .setTheme(new BlueTheme(getScreenHeight(), getScreenWidth()));

        // Set the title.
        setTitle("MTK Logger Control V"
                + midlet.getAppProperty("MIDlet-Version"));

        // Add the menu buttons.
        setFullScreenMode(false);

        // initialSetupScreen();
    }

    private boolean initialScreenSetup = false;

    public void initialSetupScreen() {
        if (!initialScreenSetup) {
            Log.debug("MainScreen initialSetupScreen taken");
            initialScreenSetup = true;
            setMenuText("Logger Menu", "App Menu");

            downloadLogScreen = new LogDownloadScreen(c, this);
            loggerInfoScreen = new DelayedDialog(
                    ScreenFactory.LOGGERSTATUSSCREEN, c, this, this);
            logScreen = new LogScreen(this);
            debugConfigScreen = new DebugConfigScreen(c, this);
            initialiseGPSAlert = new InitializingGPSAlert(c, this);
            findingGPSDevicesAlert = new FindingGPSDevicesAlert(c, this,
                    initialiseGPSAlert);
            connectConfig = new DelayedDialog(
                    ScreenFactory.CONNECTCONFIGSCREEN, c, this, this);
            baseDirScreen = new PathSelectionScreen("Base directory", this,
                    m().getStringOpt(AppSettings.OUTPUTDIRPATH), true) {
                protected void notifyPathSelected(final String path) {
                    c.setStringOpt(AppSettings.OUTPUTDIRPATH, path);
                    c.setPaths();
                }
            };
            creditsScreen = new CreditsScreen(this);
            logFieldSelectScreen = new DelayedDialog(
                    ScreenFactory.LOGFIELDSELECTSCREEN, c, this, this);

            // fun
            posSrvScreen = new PosSrvMonitorScreen(c, this);

            // Call here for debug
            // c.doConvertLog(Model.GPX_LOGTYPE);
            rootMenu = new Menu("Log", this);
            // Reset the current location provider.

            rootMenu.appendMenuOption(downloadLogScreen);
            rootMenu.appendMenuOption(new MenuItem() {
                private boolean logStatusShown = false;

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.j4me.ui.MenuItem#getText()
                 */
                public String getText() {
                    logStatusShown = m().isLoggingActive();
                    return logStatusShown ? "Logging is ON"
                            : "Logging is OFF";
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.j4me.ui.MenuItem#onSelection()
                 */
                public void onSelection() {
                    c.setLoggingActive(!logStatusShown);
                    rootMenu.show();
                }
            });

            rootMenu.appendMenuOption("MTK Logger status", loggerInfoScreen);
            rootMenu.appendMenuOption("GPS Position", new DelayedDialog(
                    ScreenFactory.GPSPOSITIONSCREEN, c, this, this));

            // fun
            rootMenu.appendMenuOption("Position Server", posSrvScreen);

            Menu subMenu;

            subMenu = new Menu("AGPS", rootMenu);
            subMenu.appendMenuOption("AGPS Status", new DelayedDialog(
                    ScreenFactory.AGPSSTATUSSCREEN, c, this, this));
            subMenu.appendMenuOption("AGPS Upload", new DelayedDialog(
                    ScreenFactory.AGPSSCREEN, c, this, this));
            subMenu.appendMenuOption(new MenuItem() {
                public final String getText() {
                    return "Clear AGPS data";
                }

                public final void onSelection() {
                    confirmScreenOption = AGPS_CLEAR_CONFIRM;
                    confirmScreen = new ConfirmScreen("Confirm APGS Clear",
                            "Do you confirm clearing APGS data???\n", null,
                            myself);
                    confirmScreen.show();
                }
            });
            rootMenu.appendSubmenu(subMenu);

            subMenu = new Menu("Miscellaneous", rootMenu);
            subMenu.appendMenuOption(new MenuItem() {
                /*
                 * (non-Javadoc)
                 * 
                 * @see org.j4me.ui.MenuItem#getText()
                 */
                public String getText() {
                    return "Hot start";
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.j4me.ui.MenuItem#onSelection()
                 */
                public void onSelection() {
                    c.gpsCmd(MtkController.CMD_HOTSTART);
                    rootMenu.show();
                }
            });
            subMenu.appendMenuOption(new MenuItem() {
                /*
                 * (non-Javadoc)
                 * 
                 * @see org.j4me.ui.MenuItem#getText()
                 */
                public String getText() {
                    return "Warm start";
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.j4me.ui.MenuItem#onSelection()
                 */
                public void onSelection() {
                    c.gpsCmd(MtkController.CMD_WARMSTART);
                    rootMenu.show();
                }
            });
            subMenu.appendMenuOption(new MenuItem() {
                public final String getText() {
                    return "Cold start";
                }

                public final void onSelection() {
                    confirmScreenOption = COLD_START_CONFIRM;
                    confirmScreen = new ConfirmScreen("Confirm cold start",
                            "Do you confirm a cold start of the GPS "
                                    + "(needs to lock on sats again"
                                    + " and get almanac data)???\n", null,
                            myself);
                    confirmScreen.show();
                }
            });

            rootMenu.appendSubmenu(subMenu);

            subMenu = new Menu("App Settings", rootMenu);
            subMenu.appendMenuOption("Working dir", baseDirScreen);
            subMenu.appendMenuOption("Debug Conditions", debugConfigScreen);
            subMenu.appendMenuOption("Download Settings", new DelayedDialog(
                    ScreenFactory.LOGDOWNLOADCONFIGSCREEN, c, this, this));
            subMenu.appendMenuOption("App log", logScreen);
            appUnits = new RadioButton();
            appUnits.append("Metric System (meters)");
            appUnits.append("English System (miles)");
            appUnits.setSelectedIndex(c.getAppModel().getBooleanOpt(
                    AppSettings.IMPERIAL) ? 1 : 0);
            subMenu.append(appUnits);
            speedDisplay = new RadioButton();
            speedDisplay.append("Std Speed Display");
            speedDisplay.append("Minutes/km display");
            speedDisplay.append("MM:SS/km display");
            int idx = 0;
            switch (c.getAppModel().getIntOpt(
                    AppSettings.SPEED_DISPLAY_OPTION)) {
            case AppSettings.SPEED_DISPLAY_FIELD_NORMAL:
                idx = 0;
                break;
            case AppSettings.SPEED_DISPLAY_FIELD_MINUTES_PER_KM:
                idx = 1;
                break;
            case AppSettings.SPEED_DISPLAY_FIELD_MMSS_PER_KM:
                idx = 2;
                break;
            default:
                break;
            }
            speedDisplay.setSelectedIndex(idx);
            subMenu.append(speedDisplay);

            rootMenu.appendSubmenu(subMenu);

            subMenu = new Menu("Convert Menu", rootMenu);
            subMenu.appendMenuOption("Select File Fields", new DelayedDialog(
                    ScreenFactory.FILEFIELDSELECTSCREEN, c, rootMenu,
                    rootMenu));
            subMenu.appendMenuOption("Convert", new DelayedDialog(
                    ScreenFactory.CONVERTTOSCREEN, c, rootMenu, rootMenu));
            rootMenu.appendSubmenu(subMenu);

            subMenu = new Menu("Connection", rootMenu);
            subMenu.appendMenuOption("Reconnect to GPS", initialiseGPSAlert);
            subMenu.appendMenuOption("Find and Connect",
                    findingGPSDevicesAlert);
            subMenu.appendMenuOption("Settings (protocol)", connectConfig);
            rootMenu.appendSubmenu(subMenu);

            subMenu = new Menu("Logger", rootMenu);
            // private final LogConditionsConfigScreen
            // logConditionsConfigScreen;

            subMenu.appendMenuOption("Log Conditions", new DelayedDialog(
                    ScreenFactory.LOGCONDITIONSCONFIGSCREEN, c, this, this));
            subMenu.appendMenuOption("Log Fields", logFieldSelectScreen);
            subMenu.appendMenuOption("MTK Logger status", loggerInfoScreen);
            subMenu.appendMenuOption(new MenuItem() {
                public final String getText() {
                    if (c.isEnableStoreOK()) {
                        return "Store Volatile Set";
                    } else {
                        return "- Getting values -";
                    }
                }

                public final void onSelection() {
                    c.storeSetting1();
                }
            });
            subMenu.appendMenuOption(new MenuItem() {
                private boolean firstTime = true;

                public final String getText() {
                    if (!m().isStoredSetting1()) {
                        c.reqSettingsForStorage();
                        firstTime = false;
                    }
                    if (m().isStoredSetting1()) {
                        return "Restore Volatile Set";
                    } else {
                        return "- No stored settings -";
                    }
                }

                public final void onSelection() {
                    c.restoreSetting1();
                }
            });

            subMenu.appendMenuOption(new MenuItem() {
                public final String getText() {
                    return "Erase";
                }

                public final void onSelection() {
                    confirmScreenOption = ERASE_CONFIRM;
                    confirmScreen = new ConfirmScreen(
                            "Confirm erasal",
                            "Do you confirm erasal of all data in memory ???\n"
                                    + "If you did not download, YOU WILL REMOVE ALL DATA LOGGED BY THE LOGGER.",
                            "Do you confirm erasal?\nYOU MIGHT LOOSE DATA.",
                            myself);
                    confirmScreen.show();
                }
            });

            rootMenu.appendSubmenu(subMenu);

            m().addListener(this);
        } else {
            // Screen already set up, get the options that might have changed.
            c.setBooleanOpt(AppSettings.IMPERIAL,
                    appUnits.getSelectedIndex() == 1);
        }
    }

    /**
     * Indicates which data is shown
     */
    private int dataShown = MainScreen.SHOWN_NOTHING;
    private final static int SHOWN_NOTHING = 0;
    private final static int SHOWN_WAYPOINTS_DATA = 1;
    private final static int SHOWN_NOCONNECTION = 2;

    private Label[] labels = null;

    private synchronized void setupScreen() {
        Log.debug("MainScreen setupScreen");
        initialSetupScreen();
        if ((m().getLogFormat() & (1 << BT747Constants.FMT_RCR_IDX)) != 0) {
            if (dataShown != MainScreen.SHOWN_WAYPOINTS_DATA) {
                dataShown = MainScreen.SHOWN_WAYPOINTS_DATA;
                labels = null;
                deleteAll();
                final Label[] tmpLabels = new Label[12];
                append(tmpLabels[0] = new Label("0. Took a picture"));
                append(tmpLabels[1] = new Label("1. Gas station"));
                append(tmpLabels[2] = new Label("2. Phone booth"));
                append(tmpLabels[3] = new Label("3. ATM"));
                append(tmpLabels[4] = new Label("4. Bus stop"));
                append(tmpLabels[5] = new Label("5. Parking"));
                append(tmpLabels[6] = new Label("6. Post box"));
                append(tmpLabels[7] = new Label("7. Railway"));
                append(tmpLabels[8] = new Label("8. Restaurant"));
                append(tmpLabels[9] = new Label("9. Bridge"));
                append(tmpLabels[10] = new Label("*. Magnificent View"));
                append(tmpLabels[11] = new Label("#. Other 3"));
                repaint();
                labels = tmpLabels;
                if (ttLabels == null) {
                    ttLabels = new TimerTask() {
                        public void run() {
                            myself.timedRun();
                        }
                    };
                }
            }
        } else {
            if (dataShown != MainScreen.SHOWN_NOCONNECTION) {
                dataShown = MainScreen.SHOWN_NOCONNECTION;
                deleteAll();
                append(new Label("Advanced waypoint events are"
                        + " available once your device is connected.\n"
                        + "The 'RCR field' also needs to be active "
                        + "(see log Format)."));
                repaint();
            }
        }
    }

    /**
     * Is true when the application is launched and when initialization is
     * required when the main screen is shown.
     */
    private static boolean isFirstLaunch = true;

    /**
     * Get the application model
     * 
     * @return Reference to application model object.
     */
    private final AppModel m() {
        return c.getAppModel();
    }

    /**
     * When true, the application waits until the erase is done (when erase is
     * ongoing).
     */
    private boolean waitErase;

    /**
     * Called when this display is displayed
     * 
     * @see org.j4me.ui.DeviceScreen#show()
     */
    public void showNotify() {
        Log.debug("MainScreen showNotify");
        setupScreen();
        int sd = 0;
        switch (speedDisplay.getSelectedIndex()) {
        case 0:
            sd = AppSettings.SPEED_DISPLAY_FIELD_NORMAL;
            break;
        case 1:
            sd = AppSettings.SPEED_DISPLAY_FIELD_MINUTES_PER_KM;
            break;
        case 2:
            sd = AppSettings.SPEED_DISPLAY_FIELD_MMSS_PER_KM;
            break;
        }
        c.setIntOpt(AppSettings.SPEED_DISPLAY_OPTION, sd);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.j4me.ui.DeviceScreen#show()
     */
    public void show() {
        super.show();
        // When this screen is shown, we are no longer waiting for erasal end
        // whatever happens.
        waitErase = false;
        if (MainScreen.isFirstLaunch) {
            MainScreen.isFirstLaunch = false;
            if (m().getBluetoothGPSURL() != null) {
                Log.debug("Port:" + m().getBluetoothGPSURL());

                initialiseGPSAlert.show();
            } else {
                findingGPSDevicesAlert.show();
            }
        } else {
            if (confirmScreen != null) {
                switch (confirmScreenOption) {
                case NO_CONFIRM:
                    // The confirm screen has no purpose
                    break;
                case ERASE_CONFIRM:
                    // The confirm screen confirms erasal or not
                    if (confirmScreen.getConfirmation()) {
                        Thread erase;
                        erase = new Thread(new Runnable() {
                            public void run() {
                                Log.debug("Request erase");
                                c.eraseLog();
                                ProgressAlert pa;
                                pa = new ProgressAlert(
                                        "Erasing",
                                        "Waiting until erase is done.\n"
                                                + "You can cancel waiting at your own risk") {
                                    public void onCancel() {
                                        myself.show();
                                    }

                                    protected DeviceScreen doWork() {
                                        return null;
                                    }
                                };
                                pa.show();
                            }
                        });
                        waitErase = true;
                        erase.start();
                    }
                    break;
                case DOWNLOAD_OVERWRITE_CONFIRM:
                    // The confirm screen confirms erasal or not
                    try {
                        c.replyToOkToOverwrite(confirmScreen
                                .getConfirmation());
                    } catch (BT747Exception e) {

                    }
                    if (interruptedScreen != null) {
                        confirmScreen = null;
                        interruptedScreen.show();
                        return;
                    }
                    break;
                case AGPS_CLEAR_CONFIRM:
                    if (confirmScreen.getConfirmation()) {
                        c.gpsCmd(MtkController.CMD_EPO_CLEAR);
                    }
                    break;
                case COLD_START_CONFIRM:
                    if (confirmScreen.getConfirmation()) {
                        c.gpsCmd(MtkController.CMD_COLDSTART);
                    }
                    break;
                }
                confirmScreen = null;
            }
        }
    }

    /**
     * Called when the user presses the "Log" button.
     * 
     * @see DeviceScreen#declineNotify()
     */
    protected final void declineNotify() {

        /*
         * menu.appendMenuOption( new MenuItem() { public String getText () {
         * return "Download"; }
         * 
         * public void onSelection () { show(); } } );
         */
        rootMenu.show();
    }

    /**
     * Called when the user presses the "Other" button.
     * 
     * @see DeviceScreen#acceptNotify()
     */
    protected final void acceptNotify() {
        Menu menu = new Menu("Other", this);

        // Choose different location provider criteria.
        // menu.appendMenuOption( new CriteriaSelectionScreen(model) );

        // Reset the current location provider.
        menu.appendMenuOption(new MenuItem() {
            public String getText() {
                return "Close connection";
            }

            public void onSelection() {
                c.closeGPS();
                show();
            }
        });

        // See the application's log.
        menu.appendMenuOption("Application Log", logScreen);
        menu.appendMenuOption("Credits", creditsScreen);
        // TODO: not very clean for an exit.
        menu.appendMenuOption(new MenuItem() {
            public String getText() {
                return "Exit application";
            }

            public void onSelection() {
                c.closeGPS();
                try {
                    midlet.destroyApp(true);
                } catch (final Exception e) {
                    Log.debug("When closing app", e);
                }
            }
        });

        menu.show();
        menu = null;
    }

    /**
     * Called to highlight a label (corresponding to a waypoint logged by the
     * user).
     * 
     * This is synchronized to avoid potential problems from multiple threads.
     * 
     * @param i
     *            The index of the label to be highlighted.
     */
    private synchronized void hightlightLabel(final int i) {
        if ((labels != null) && (i < labels.length)) {
            if (labels[i] != null) {
                labels[i].setFontColor(Theme.RED);
                labels[i].repaint();
                tm.schedule(ttLabels, 500);
            }
        }
    }

    /**
     * Called to reset all labels to the initial caller. To be called after
     * timeout indicated by {@link #nextResetLabelTime}.
     */
    private synchronized void resetLabels() {
        if (labels != null) {
            final int color = UIManager.getTheme().getFontColor();
            for (int i = 0; i < labels.length; i++) {
                if ((labels[i] != null)
                        && (labels[i].getFontColor() != color)) {
                    labels[i].setFontColor(color);
                    labels[i].repaint();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.j4me.ui.Dialog#keyReleased(int)
     */
    protected void keyReleased(final int keyCode) {
        if (keyCode == DeviceScreen.RIGHT) {
            rootMenu.show();
        } else {
            super.keyReleased(keyCode);
        }
    }

    private void waypointFromKey(final int keyCode) {
        switch (keyCode) {
        case DeviceScreen.KEY_NUM0:
            c.logImmediate(BT747Constants.RCR_APP0_MASK);
            hightlightLabel(0);
            break;
        case DeviceScreen.KEY_NUM1:
            c.logImmediate(BT747Constants.RCR_APP1_MASK);
            hightlightLabel(1);
            break;
        case DeviceScreen.KEY_NUM2:
            c.logImmediate(BT747Constants.RCR_APP2_MASK);
            hightlightLabel(2);
            break;
        case DeviceScreen.KEY_NUM3:
            c.logImmediate(BT747Constants.RCR_APP3_MASK);
            hightlightLabel(3);
            break;
        case DeviceScreen.KEY_NUM4:
            c.logImmediate(BT747Constants.RCR_APP4_MASK);
            hightlightLabel(4);
            break;
        case DeviceScreen.KEY_NUM5:
            c.logImmediate(BT747Constants.RCR_APP5_MASK);
            hightlightLabel(5);
            break;
        case DeviceScreen.KEY_NUM6:
            c.logImmediate(BT747Constants.RCR_APP6_MASK);
            hightlightLabel(6);
            break;
        case DeviceScreen.KEY_NUM7:
            c.logImmediate(BT747Constants.RCR_APP7_MASK);
            hightlightLabel(7);
            break;
        case DeviceScreen.KEY_NUM8:
            c.logImmediate(BT747Constants.RCR_APP8_MASK);
            hightlightLabel(8);
            break;
        case DeviceScreen.KEY_NUM9:
            c.logImmediate(BT747Constants.RCR_APP9_MASK);
            hightlightLabel(9);
            break;
        case DeviceScreen.KEY_STAR:
            c.logImmediate(BT747Constants.RCR_APPY_MASK);
            hightlightLabel(10);
            break;
        case DeviceScreen.KEY_POUND:
            c.logImmediate(BT747Constants.RCR_APPZ_MASK);
            hightlightLabel(11);
            break;
        default:
            break;
        }
    }

    /**
     * Called when key is pressed by user. Handles key.
     * 
     * @see org.j4me.ui.Dialog#keyPressed(int)
     */
    protected void keyPressed(final int keyCode) {
        waypointFromKey(keyCode);
        super.keyPressed(keyCode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.j4me.ui.Dialog#keyRepeated(int)
     */
    protected void keyRepeated(final int keyCode) {
        waypointFromKey(keyCode);
        super.keyRepeated(keyCode);
    }

    /**
     * Override because default would select main menu.
     * 
     * @see org.j4me.ui.DeviceScreen#returnNotify()
     */
    protected void returnNotify() {
        // Override
        // Do nothing for the moment - should prompt to exit the application.
    }

    public void timedRun() {
        try {
            resetLabels();
        } catch (final Exception e) {
            Log.error("timedRun in MainScreen", e);
        }
    }

    private void reportException(final BT747Exception e) {
        ErrorAlert es = new ErrorAlert("Error", e.getCause() + "\n"
                + e.getMessage(), UIManager.getScreen());
        es.show();
    }

    private DeviceScreen interruptedScreen = null;

    private final static int DEFAULT_RECONNECT_TRIALS = 5;
    private int reconnectTrialsToGo = 0;

    /**
     * Call back from the GPS Model to provide data.
     * 
     * @see bt747.model.ModelListener#modelEvent(bt747.model.ModelEvent)
     */
    public final void modelEvent(final ModelEvent e) {
        switch (e.getType()) {
        // case ModelEvent.DEBUG_MSG:
        // Log.debug((String) e.getArg());
        // break;
        case ModelEvent.DISCONNECTED:
            // Display.getDisplay(this).flashBacklight(500);
            Display.getDisplay(midlet).vibrate(200);
            if (reconnectTrialsToGo > 0) {
                reconnectTrialsToGo--;
                c.reconnectGpsAfterDisconnect();
            }
            // com.nokia.mid.ui.DeviceControl
            break;
        case GpsEvent.ERASE_DONE_REMOVE_POPUP:
            if (waitErase) {
                show();
            }
            break;
        case GpsEvent.UPDATE_LOG_FORMAT:
            setupScreen();
            break;
        case ModelEvent.CONNECTED:
            reconnectTrialsToGo = DEFAULT_RECONNECT_TRIALS;
            c.setMtkDataNeeded(MtkModel.DATA_LOG_FORMAT);
            c.setMtkDataNeeded(MtkModel.DATA_LOG_STATUS);
            break;
        case GpsEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
            interruptedScreen = UIManager.getScreen();
            confirmScreenOption = DOWNLOAD_OVERWRITE_CONFIRM;
            confirmScreen = new ConfirmScreen("Overwrite previous data",
                    "The data previously downloaded is different\n"
                            + "Do you confirm this data can be replace with"
                            + " new data from your device ??\n",
                    "Are you absolutely certain that previously downloaded"
                            + " data is irrelevant?", myself);
            confirmScreen.show();
            break;
        case ModelEvent.EXCEPTION:
            reportException((BT747Exception) e.getArg());
        default:
            break;
        }
    }
}
