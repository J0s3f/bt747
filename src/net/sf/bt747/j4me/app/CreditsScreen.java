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
// *** IS ASSUMED BY THE USER. See the GNU General Public License ***
// *** for more details. ***
// *** *********************************************************** ***
package net.sf.bt747.j4me.app;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;
import org.j4me.ui.components.Label;

/**
 * The "Credit" screen.
 * 
 * Gives references to the application, license and credits to other projects
 * where portions of the code are coming from.
 */
public class CreditsScreen extends Dialog {
    private Label lbText;

    private DeviceScreen previous;

    private boolean screenSetup = false;

    /**
     * Constructs the "Credits" screen.
     * 
     * @param previous
     *                is the screen that invoked this one. If this is <c>null</c>
     *                the application will exit when this screen is dismissed.
     */
    public CreditsScreen(final DeviceScreen previous) {
        this.previous = previous;

    }

    private void setupScreen() {
        if (!screenSetup) {
            setTitle("Credits");
            screenSetup = true;
            lbText = new Label(
                    "ALPHA/BETA version of a J2ME "
                            + "implementation of BT747\n"
                            + "(http://sf.net/projects/bt747).\n"
                            + " This application demonstrates log download"
                            + " and enables you to set some basic log conditions.\n"
                            + "It is available under the GNU GENERAL PUBLIC LICENSE v3.\n"
                            + "Portions of the code are subject to the APACHE V2"
                            + " license http://www.apache.org/licenses/LICENSE-2.0 .\n"
                            + "This SW uses some code from http://www.j4me.org, "
                            + "http://gpsd.berlios.de/, and, "
                            + "http://sourceforge.net/projects/swcollections.\n"
                            + "For a list of people that made this happen,"
                            + "see the documentation and the project site.\n"
                            + "*** DISCLAIMER ***\n"
                            + "This SW is free and comes without any guarantee.\n"
                            + "Use this SW at your own risk.");
            append(lbText);
        }
    }

    public void showNotify() {
        setupScreen();
    }

    protected void acceptNotify() {
        previous.show();
    }

    protected void declineNotify() {
        previous.show();
    }
}
