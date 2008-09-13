package net.sf.bt747.j4me.app;

import javax.microedition.midlet.MIDlet;

import org.j4me.examples.log.LogScreen;
import org.j4me.logging.Log;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.Menu;
import org.j4me.ui.MenuItem;
import org.j4me.ui.components.Label;

/**
 * The "Log" screen. This shows the contents of the application's log. It is an
 * advanced screen intended for us to diagnose the application.
 */
public class MainScreen extends Dialog {

    private AppController c;
    private MIDlet midlet;

    private Label lbText;

    private final LogDownloadScreen downloadLogScreen;
    private final ConvertToScreen convertToScreen;
    private final GpsPositionScreen gpsPositionScreen;
    private final LogConditionsConfigScreen logConditionsConfigScreen;
    private final LogDownloadConfigScreen logDownloadConfigScreen;
    private final LoggerStatusScreen loggerInfoScreen;
    private final LogScreen logScreen;
    private final DebugConfigScreen debugConfigScreen;
    private final InitializingGPSAlert initialiseGPSAlert;
    private final FindingGPSDevicesAlert findingGPSDevicesAlert;
    private final PathSelectionScreen baseDirScreen;

    private final int NO_CONFIRM = 0;
    private final int ERASE_CONFIRM = 1;
    private int confirmScreenOption = 0;

    final private Menu rootMenu;
    final private DeviceScreen myself = this;
    private ConfirmScreen confirmScreen;

    /**
     * Constructs the "Log" screen.
     * 
     * @param previous
     *            is the screen that invoked this one. If this is <c>null</c>
     *            the application will exit when this screen is dismissed.
     */
    public MainScreen(final AppController c, final MIDlet midlet) {
        this.c = c;
        this.midlet = midlet;
        // Set the title.
        setTitle("MTK Log Control (BT747) V"
                + midlet.getAppProperty("MIDlet-Version"));

        lbText = new Label("ALPHA/BETA version of a J2ME "
                + "implementation of BT747\n"
                + "(http://sf.net/projects/bt747).\n"
                + " This application demonstrates log download"
                + " and enables you to set some basic log conditions.\n"
                + "It is available under the GNU GENERAL PUBLIC LICENSE v3.\n"
                + "Portions of the code are subject to the APACHE V2"
                + " license http://www.apache.org/licenses/LICENSE-2.0 .\n"
                + "This SW uses code from http://www.j4me.org, "
                + "http://gpsd.berlios.de/, and, "
                + "http://sourceforge.net/projects/swcollections.\n"
                + "For a list of people that made this happen,"
                + "see the documentation and the project site.\n"
                + "DISCLAIMER\n"
                + "This SW is free and comes without any guarantee\n"
                + "Use this SW at your own risk.");
        append(lbText);

        // Add the menu buttons.
        setFullScreenMode(false);

        setMenuText("Logger Menu", "App Menu");

        downloadLogScreen = new LogDownloadScreen(c, this);
        convertToScreen = new ConvertToScreen(c, this);
        gpsPositionScreen = new GpsPositionScreen(c, this);
        logConditionsConfigScreen = new LogConditionsConfigScreen(c, this);
        logDownloadConfigScreen = new LogDownloadConfigScreen(c, this);
        loggerInfoScreen = new LoggerStatusScreen(c, this);
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

        // Call here for debug
        // c.doConvertLog(Model.GPX_LOGTYPE);
        rootMenu = new Menu("Log", this);
        // Reset the current location provider.

        rootMenu.appendMenuOption(downloadLogScreen);
        rootMenu.appendMenuOption("GPS Position", gpsPositionScreen);

        Menu subMenu;
        subMenu = new Menu("App Settings", rootMenu);
        subMenu.appendMenuOption("Working dir", baseDirScreen);
        subMenu.appendMenuOption("Debug Conditions", debugConfigScreen);
        subMenu.appendMenuOption("Download Settings", logDownloadConfigScreen);
        rootMenu.appendSubmenu(subMenu);

        subMenu = new Menu("Convert", rootMenu);
        subMenu.appendMenuOption("To GPX", convertToScreen);
        rootMenu.appendSubmenu(subMenu);

        subMenu = new Menu("Connection", rootMenu);
        subMenu.appendMenuOption("Reconnect to GPS", initialiseGPSAlert);
        subMenu.appendMenuOption("Find and Connect", findingGPSDevicesAlert);
        rootMenu.appendSubmenu(subMenu);

        subMenu = new Menu("Logger", rootMenu);
        subMenu.appendMenuOption("Log Conditions", logConditionsConfigScreen);
        subMenu.appendMenuOption("MTK Logger Config", loggerInfoScreen);
        subMenu.appendMenuOption(new MenuItem() {
            public final String getText() {
                return "Erase";
            }

            public final void onSelection() {
                confirmScreenOption = ERASE_CONFIRM;
                confirmScreen = new ConfirmScreen(
                        "Confirm erasal",
                        "Do you confirm erasal of all data in memory."
                                + "If you did not download, you remove this info.",
                        "Do you confirm erasal.  YOU MIGHT LOOSE DATA.", myself);
                confirmScreen.show();
            }
        });

        rootMenu.appendSubmenu(subMenu);

    }

    private static boolean isFirstLaunch = true;

    private final AppModel m() {
        return c.getAppModel();
    }

    public void show() {

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
                    break;
                case ERASE_CONFIRM:
                    if (confirmScreen.getConfirmation()) {
                        Log.debug("Request erase");
                        c.eraseLog();
                    }
                    break;
                }
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

    Menu menu;

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
        // TODO: not very clean for an exit.
        menu.appendMenuOption(new MenuItem() {
            public String getText() {
                return "Exit application";
            }

            public void onSelection() {
                c.closeGPS();
                midlet.notifyDestroyed();
            }
        });

        menu.show();

        // Continue processing the event.
        super.acceptNotify();
    }

    protected void keyPressed(int keyCode) {
        if (keyCode == DeviceScreen.RIGHT) {
            rootMenu.show();
        } else {
            super.keyPressed(keyCode);
        }
    }

    protected void returnNotify() {
        // Override
        // Do nothing for the moment - should prompt to exit the application.
    }

}
