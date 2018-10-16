package bt747.waba_view;

//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     seesite@bt747.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************                              
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;

import gps.BT747Constants;

import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * @author Mario De Weerd
 * 
 * User interface to select NMEA output strings
 */
public final class GPSFileNMEAOutputSel extends Container implements
        ModelListener {
    /** The object that is used to communicate with the GPS device. */
    private final MyCheck[] chkNMEAItems = new MyCheck[BT747Constants.C_NMEA_SEN_COUNT+1];
    /** The button that requests to change the log format of the device */

    private static final int C_NMEAactiveFilters = 0x0002003A;

    private final Model m;
    private final AppController c;

    /**
     * 
     */
    public GPSFileNMEAOutputSel(final AppController c, final Model m) {
        this.m = m;
        this.c = c;
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Container#onStart()
     */
    protected final void onStart() {
        int bit = 1;
        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            chkNMEAItems[i] = new MyCheck(BT747Constants.getNmeaDescription(i));
            add(chkNMEAItems[i]);
            chkNMEAItems[i]
                    .setRect(
                            ((i < ((BT747Constants.C_NMEA_SEN_COUNT / 2) + 1)) ? LEFT
                                    : (getClientRect().width / 2)),
                            ((i == 0) || i == ((BT747Constants.C_NMEA_SEN_COUNT / 2) + 1)) ? TOP
                                    : AFTER - 1, PREFERRED, PREFERRED - 1);
            chkNMEAItems[i].setEnabled((C_NMEAactiveFilters & bit) != 0);
            bit <<= 1;
        }
        updateNMEAset();
    }

    private void updateNMEAset() {
        int maskNMEAset;
        int bit;
        maskNMEAset = m.getNMEAset();
        bit = 1;
        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            chkNMEAItems[i].setChecked((maskNMEAset & bit) != 0);
            bit <<= 1;
            // chkNMEAItems[i].repaintNow();
        }
    }

    private void setNMEAset() {
        int maskNMEAset;
        int bit;
        maskNMEAset = 0;

        bit = 1;
        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            maskNMEAset |= chkNMEAItems[i].getChecked() ? bit : 0;
            bit <<= 1;
        }
        c.setNMEAset(maskNMEAset);
    }

    /**
     * Handle events for this object.
     * 
     * @param event
     *            The event to be interpreted.
     */
    public final void onEvent(final Event event) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
                if (event.target == chkNMEAItems[i]) {
                    setNMEAset();
                    break;
                }
            }
            break;
        default:
            break;
        }
    }

    public final void modelEvent(final ModelEvent event) {
        // Do nothing
    }
}
