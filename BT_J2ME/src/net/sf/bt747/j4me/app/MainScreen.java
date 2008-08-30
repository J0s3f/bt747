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

        lbText = new Label(
                "Demonstration/BETA version of a J2ME "
                        + "implementatation of BT747 (http://sf.net/projects/bt747)."
                        + " This application demonstrates log sownload (very slow currently)"
                        + " and enables you to set some basic log conditions.");
        append(lbText);

        // Add the menu buttons.
        setFullScreenMode(false);

        setMenuText("Log", "Other");
    }

    /**
     * Called when this screen is going to be displayed.
     * 
     * @see DeviceScreen#showNotify()
     */
    public final void showNotify() {
        // Clear this form.
        deleteAll();

    }

    /**
     * Called when the user presses the "Log" button.
     * 
     * @see DeviceScreen#declineNotify()
     */
    protected final void declineNotify() {
        Menu menu = new Menu("Log", this);
        // Reset the current location provider.

        menu.appendMenuOption(new DownloadLog(c, this));
        menu.appendMenuOption("To GPX", new ConvertTo(c, this));
        menu.appendMenuOption("GPS Position", new GpsPositionScreen(c, this));
        menu.appendMenuOption("Log Conditions", new LogConditionsConfigScreen(
                c, this));
        menu.appendMenuOption("Download Settings", new LogDownloadConfigScreen(
                c, this));
        menu.appendMenuOption("MTK Logger Config",
                new LoggerInfoScreen(c, this));
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
        menu.appendMenuOption("Application Log", new LogScreen(this));
        // TODO: not very clean for an exit.
        menu.appendMenuOption(new MenuItem() {
            public String getText() {
                return "Exit application";
            }

            public void onSelection() {
                c.closeGPS();
                midlet.notifyDestroyed();
            }
        }
                );

        menu.show();

        // Continue processing the event.
        super.acceptNotify();
    }
}
