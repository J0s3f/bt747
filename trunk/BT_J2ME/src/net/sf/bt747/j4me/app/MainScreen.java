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
//***  *********************************************************** ***
package net.sf.bt747.j4me.app;

import gps.BT747Constants;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Display;

import net.sf.bt747.j4me.app.log.LogScreen;
import net.sf.bt747.j4me.app.screens.DelayedDialog;
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

import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * The "Main" screen. This is the application's entry screen. It will show the
 * available special waypoints when the device is available. It will show a
 * message remembering that the device must be available if it is not available.
 */
public final class MainScreen extends Dialog implements ModelListener {
    /**
     * Reference to the Application Controller.
     */
    private AppController c;

    /**
     * Reference to the application Midlet instantiation because we need it.
     */
    private MTKMidlet midlet;

    /**
     * The different screens that we can go to from the main screen. J4ME
     * requires that those are all created when the menu is created.
     */
    private final LogDownloadScreen downloadLogScreen;
    private final DeviceScreen loggerInfoScreen;
    private final LogScreen logScreen;
    private final DebugConfigScreen debugConfigScreen;
    private final InitializingGPSAlert initialiseGPSAlert;
    private final FindingGPSDevicesAlert findingGPSDevicesAlert;
    private final PathSelectionScreen baseDirScreen;
    private final CreditsScreen creditsScreen;
    private final DeviceScreen logFieldSelectScreen;

    java.util.Timer tm = new Timer();
    TimerTask tt;

    /**
     * We use only one instantiation of the confirmation screen (sequence). This
     * property says what it is for. This information is required when the
     * confirm screen sequence hands over control to the main screen again where
     * the result is analyzed. {@link #NO_CONFIRM} indicates the screen is
     * unused. {@link #ERASE_CONFIRM} indicates that the confirmScreen is used
     * for erase confirmation.
     */
    private int confirmScreenOption = 0;
    private final int NO_CONFIRM = 0;
    private final int ERASE_CONFIRM = 1;
    private final int DOWNLOAD_OVERWRITE_CONFIRM = 2;

    /**
     * A reference to the confirmation screen.
     */
    private ConfirmScreen confirmScreen;
    /**
     * The left menu for this screen.
     */
    final private Menu rootMenu;
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
        UIManager.setTheme(new BlueTheme(getScreenWidth()));

        tt = new TimerTask() {
            public void run() {
                myself.timedRun();
            }
        };

        // Set the title.
        setTitle("MTK Logger Control V"
                + midlet.getAppProperty("MIDlet-Version"));

        // Add the menu buttons.
        setFullScreenMode(false);

        setMenuText("Logger Menu", "App Menu");

        downloadLogScreen = new LogDownloadScreen(c, this);
        loggerInfoScreen = new DelayedDialog(LogDownloadConfigScreen.class, c,
                this, this);
        logScreen = new LogScreen(this);
        debugConfigScreen = new DebugConfigScreen(c, this);
        initialiseGPSAlert = new InitializingGPSAlert(c, this);
        findingGPSDevicesAlert = new FindingGPSDevicesAlert(c,
                initialiseGPSAlert);
        baseDirScreen = new PathSelectionScreen("Base directory", this, m()
                .getBaseDirPath(), true) {
            protected void notifyPathSelected(final String path) {
                c.setBaseDirPath(path);
            }
        };
        creditsScreen = new CreditsScreen(this);
        logFieldSelectScreen = new DelayedDialog(LogFieldSelectScreen.class, c,
                this, this);

        // Call here for debug
        // c.doConvertLog(Model.GPX_LOGTYPE);
        rootMenu = new Menu("Log", this);
        // Reset the current location provider.

        rootMenu.appendMenuOption(downloadLogScreen);
        rootMenu.appendMenuOption("MTK Logger status", loggerInfoScreen);
        rootMenu.appendMenuOption("GPS Position", new DelayedDialog(
                GpsPositionScreen.class, c, this, this));

        Menu subMenu;
        subMenu = new Menu("App Settings", rootMenu);
        subMenu.appendMenuOption("Working dir", baseDirScreen);
        subMenu.appendMenuOption("Debug Conditions", debugConfigScreen);
        subMenu.appendMenuOption("Download Settings", new DelayedDialog(
                LogDownloadConfigScreen.class, c, this, this));
        rootMenu.appendSubmenu(subMenu);

        subMenu = new Menu("Convert Menu", rootMenu);
        subMenu.appendMenuOption("Select File Fields", new DelayedDialog(
                FileFieldSelectScreen.class, c, rootMenu, rootMenu));
        subMenu.appendMenuOption("Convert", new DelayedDialog(
                ConvertToScreen.class, c, rootMenu, rootMenu));
        rootMenu.appendSubmenu(subMenu);

        subMenu = new Menu("Connection", rootMenu);
        subMenu.appendMenuOption("Reconnect to GPS", initialiseGPSAlert);
        subMenu.appendMenuOption("Find and Connect", findingGPSDevicesAlert);
        rootMenu.appendSubmenu(subMenu);

        subMenu = new Menu("Logger", rootMenu);
        // private final LogConditionsConfigScreen logConditionsConfigScreen;

        subMenu.appendMenuOption("Log Conditions", new DelayedDialog(
                LogConditionsConfigScreen.class, c, this, this));
        subMenu.appendMenuOption("Log Fields", logFieldSelectScreen);
        subMenu.appendMenuOption("MTK Logger status", loggerInfoScreen);
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
                        "Do you confirm erasal?\nYOU MIGHT LOOSE DATA.", myself);
                confirmScreen.show();
            }
        });

        rootMenu.appendSubmenu(subMenu);

        m().addListener(this);
    }

    /**
     * Indicates which data is shown
     */
    private int dataShown = SHOWN_NOTHING;
    private final static int SHOWN_NOTHING = 0;
    private final static int SHOWN_WAYPOINTS_DATA = 1;
    private final static int SHOWN_NOCONNECTION = 2;

    private Label[] labels = null;

    private void setupScreen() {
        if ((m().getLogFormat() & (1 << BT747Constants.FMT_RCR_IDX)) != 0) {
            if (dataShown != SHOWN_WAYPOINTS_DATA) {
                dataShown = SHOWN_WAYPOINTS_DATA;
                labels = null;
                deleteAll();
                labels = new Label[12];
                append(labels[0] = new Label("0. Took a picture"));
                append(labels[1] = new Label("1. Gaz station"));
                append(labels[2] = new Label("2. Phone booth"));
                append(labels[3] = new Label("3. ATM"));
                append(labels[4] = new Label("4. Bus stop"));
                append(labels[5] = new Label("5. Parking"));
                append(labels[6] = new Label("6. Post box"));
                append(labels[7] = new Label("7. Railway"));
                append(labels[8] = new Label("8. Restaurant"));
                append(labels[9] = new Label("9. Bridge"));
                append(labels[10] = new Label("*. Magnificent View"));
                append(labels[11] = new Label("#. Other 3"));
                repaint();
            }
        } else {
            if (dataShown != SHOWN_NOCONNECTION) {
                dataShown = SHOWN_NOCONNECTION;
                deleteAll();
                append(new Label("Enable the RCR log field to enable"
                        + " advanced waypoint selection.\n"
                        + "(This message disappears once connected"
                        + " with RCR log field set)."));
                repaint();
            }
        }
    }

    /**
     * Is true when the application is launched and when initialisation is
     * required when the main screen is shown.
     */
    private static boolean isFirstLaunch = true;

    /**
     * Get the application model
     * 
     * @return Reference to applicaiton model object.
     */
    private final AppModel m() {
        return c.getAppModel();
    }

    /**
     * When true, the apllication waits until the erase is done (when erase is
     * ongoing).
     */
    private boolean waitErase;

    /**
     * Called when this display is displayed
     * 
     * @see org.j4me.ui.DeviceScreen#show()
     */
    public void show() {
        // When this screen is shown, we are no longer waiting for erasal end
        // whatever happens.
        waitErase = false;
        if (isFirstLaunch) {
            isFirstLaunch = false;
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
                    c.replyToOkToOverwrite(confirmScreen.getConfirmation());
                    if (interruptedScreen != null) {
                        confirmScreen = null;
                        interruptedScreen.show();
                        return;
                    }
                    break;
                }
                confirmScreen = null;
            }
            super.show();
        }
    }

    /**
     * Called when this screen is going to be displayed.
     * 
     * @see DeviceScreen#showNotify()
     */
    public final void showNotify() {
        setupScreen();
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

        // Continue processing the event.
        super.declineNotify();
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
                } catch (Exception e) {
                    Log.debug("When closing app", e);
                }
            }
        });

        menu.show();

        // Continue processing the event.
        super.acceptNotify();
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
        if (labels != null) {
            labels[i].setFontColor(Theme.RED);
            labels[i].repaint();
            tm.schedule(tt, 500);
        }
    }

    /**
     * Called to reset all labels to the initial caller. To be called after
     * timeout indicated by {@link #nextResetLabelTime}.
     */
    private synchronized void resetLabels() {
        if (labels != null) {
            int color = UIManager.getTheme().getFontColor();
            for (int i = 0; i < labels.length; i++) {
                if (labels[i].getFontColor() != color) {
                    labels[i].setFontColor(color);
                    labels[i].repaint();
                }
            }
        }
    }

    /**
     * Called when key is pressed by user. Handles key.
     * 
     * @see org.j4me.ui.Dialog#keyPressed(int)
     */
    protected void keyPressed(int keyCode) {
        if (keyCode == DeviceScreen.RIGHT) {
            rootMenu.show();
        } else {
            super.keyPressed(keyCode);
        }
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
     * Override because default would select main menu.
     * 
     * @see org.j4me.ui.DeviceScreen#returnNotify()
     */
    protected void returnNotify() {
        // Override
        // Do nothing for the moment - should prompt to exit the application.
    }

    public void timedRun() {
        resetLabels();
    }

    DeviceScreen interruptedScreen = null;

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
            // com.nokia.mid.ui.DeviceControl
            break;
        case ModelEvent.ERASE_DONE_REMOVE_POPUP:
            if (waitErase) {
                this.show();
            }
            break;
        case ModelEvent.LOG_FORMAT_UPDATE:
            setupScreen();
            break;
        case ModelEvent.CONNECTED:
            c.reqLogFormat();
            break;
        case ModelEvent.DOWNLOAD_DATA_NOT_SAME_NEEDS_REPLY:
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
        default:
            break;
        }
    }
}
