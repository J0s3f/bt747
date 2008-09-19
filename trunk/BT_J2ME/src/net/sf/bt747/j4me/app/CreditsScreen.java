package net.sf.bt747.j4me.app;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.Label;

/**
 * The "Log" screen. This shows the contents of the application's log. It is an
 * advanced screen intended for us to diagnose the application.
 */
public class CreditsScreen extends Dialog {
    private Label lbText;

    DeviceScreen previous;
    /**
     * Constructs the "Log" screen.
     * 
     * @param previous
     *            is the screen that invoked this one. If this is <c>null</c>
     *            the application will exit when this screen is dismissed.
     */
    public CreditsScreen(DeviceScreen previous) {
       this.previous = previous;
        // Set the title.
        setTitle("Credits");

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
    }

    protected void acceptNotify() {
        previous.show();
    }
    

    protected void declineNotify() {
        previous.show();
    }
}
