package net.sf.bt747.j4me.app;

import org.j4me.examples.log.LogScreen;
import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.Menu;
import org.j4me.ui.MenuItem;

/**
 * The "Log" screen. This shows the contents of the application's log. It is an
 * advanced screen intended for us to diagnose the application.
 */
public class MainScreen extends Dialog {

    AppController c;

    /**
     * Constructs the "Log" screen.
     * 
     * @param previous
     *            is the screen that invoked this one. If this is <c>null</c>
     *            the application will exit when this screen is dismissed.
     */
    public MainScreen(AppController c) {
        this.c = c;
        // Set the title.
        setTitle("MTK Log Control (BT747)");

        // Add the menu buttons.
        setFullScreenMode(false);
        setMenuText("Log", "Other");
    }

    /**
     * Called when this screen is going to be displayed.
     * 
     * @see DeviceScreen#showNotify()
     */
    public void showNotify() {
        // Clear this form.
        deleteAll();

    }

    /**
     * Called when the user presses the "Log" button.
     * 
     * @see DeviceScreen#declineNotify()
     */
    protected void declineNotify() {
        Menu menu = new Menu("Log", this);
        // Reset the current location provider.

        menu.appendMenuOption(new DownloadLog(c, this));
        menu.appendMenuOption("To GPX", new ConvertTo(c, this));
        menu.appendMenuOption("Info from GPS",new GPSInfo(c));
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
    protected void acceptNotify() {
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
        menu.appendMenuOption(new LogScreen(this));

        menu.show();

        // Continue processing the event.
        super.acceptNotify();
    }
}
