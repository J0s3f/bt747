package net.sf.bt747.j4me.app;

import javax.microedition.midlet.MIDlet;

import org.j4me.examples.log.LogScreen;
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

    final DownloadLogScreen downloadLogScreen;
    final ConvertToScreen convertToScreen;
    final GpsPositionScreen gpsPositionScreen;
    final LogConditionsConfigScreen logConditionsConfigScreen;
    final LogDownloadConfigScreen logDownloadConfigScreen;
    final LoggerInfoScreen loggerInfoScreen;
    final LogScreen logScreen;
    final DebugConfigScreen debugConfigScreen;
    final InitializingGPSAlert initialiseGPSAlert;
    final FindingGPSDevicesAlert findingGPSDevicesAlert;

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
        setTitle("MTK Log Control (BT747)");

        lbText = new Label("ALPHA/BETA version of a J2ME "
                + "implementation of BT747\n"
                + "(http://sf.net/projects/bt747).\n"
                + " This application demonstrates log download\n"
                + " and enables you to set some basic log conditions.");
        append(lbText);

        // Add the menu buttons.
        setFullScreenMode(false);

        setMenuText("Logger Menu", "App Menu");

        downloadLogScreen = new DownloadLogScreen(c, this);
        convertToScreen = new ConvertToScreen(c, this);
        gpsPositionScreen = new GpsPositionScreen(c, this);
        logConditionsConfigScreen = new LogConditionsConfigScreen(c, this);
        logDownloadConfigScreen = new LogDownloadConfigScreen(c, this);
        loggerInfoScreen = new LoggerInfoScreen(c, this);
        logScreen = new LogScreen(this);
        debugConfigScreen = new DebugConfigScreen(c, this);
        initialiseGPSAlert = new InitializingGPSAlert(c, this);
        findingGPSDevicesAlert = new FindingGPSDevicesAlert(c,
                initialiseGPSAlert);
    }

    private static boolean isFirstLaunch = true;

    public void show() {

        if (isFirstLaunch) {
            isFirstLaunch = false;
            findingGPSDevicesAlert.show();
        } else {
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
        Menu menu = new Menu("Log", this);
        // Reset the current location provider.

        menu.appendMenuOption(downloadLogScreen);
        menu.appendMenuOption("To GPX", convertToScreen);
        menu.appendMenuOption("GPS Position", gpsPositionScreen);
        menu.appendMenuOption("Log Conditions", logConditionsConfigScreen);
        menu.appendMenuOption("Debug Conditions", debugConfigScreen);
        menu.appendMenuOption("Download Settings", logDownloadConfigScreen);
        menu.appendMenuOption("MTK Logger Config", loggerInfoScreen);
        menu.appendMenuOption("Reconnect to GPS", initialiseGPSAlert);
        menu.appendMenuOption("Find and Connect", findingGPSDevicesAlert);
        /*
         * menu.appendMenuOption( new MenuItem() { public String getText () {
         * return "Download"; }
         * 
         * public void onSelection () { show(); } } );
         */
        menu.show();

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
    
}
