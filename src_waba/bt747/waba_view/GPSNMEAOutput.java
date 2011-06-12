package bt747.waba_view;

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
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************                              

import waba.ui.Button;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;

import gps.BT747Constants;
import gps.mvc.MtkModel;

import bt747.model.ModelEvent;

import bt747.Txt;
import bt747.model.Model;
import bt747.model.ModelListener;

/**
 * Implements tab to select Device NMEA output.
 * 
 * @author Mario De Weerd
 */
public final class GPSNMEAOutput extends Container implements ModelListener {
    /** The object that is used to communicate with the GPS device. */
    private final ComboBox[] chkNMEAItems = new ComboBox[BT747Constants.C_NMEA_SEN_COUNT];
    /** The button that requests to change the log format of the device */

    private static final String[] C_NMEA_PERIODS = { "0", "1", "2", "3", "4",
            "5" };

    private Button btSet;
    private Button btSetDefaults;

    private final Model m;
    private final AppController c;

    /**
     * 
     */
    public GPSNMEAOutput(final Model m, final AppController c) {
        this.m = m;
        this.c = c;
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Container#onStart()
     */
    protected final void onStart() {
        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            chkNMEAItems[i] = new ComboBox(C_NMEA_PERIODS);
            add(chkNMEAItems[i]);
            chkNMEAItems[i]
                    .setRect(
                            ((i < ((BT747Constants.C_NMEA_SEN_COUNT / 2) + 1)) ? LEFT
                                    : (getClientRect().width / 2)),
                            ((i == 0) || i == ((BT747Constants.C_NMEA_SEN_COUNT / 2) + 1)) ? TOP
                                    : AFTER - 1, PREFERRED, PREFERRED - 1);
            add(new Label(BT747Constants.getNmeaDescription(i)), AFTER, SAME);
            // chkNMEAItems[i].setEnabled(true);
        }
        btSet = new Button(Txt.getString(Txt.SET));
        add(btSet, (getClientRect().width / 2), AFTER);

        btSetDefaults = new Button(Txt.getString(Txt.DEFAULTS));
        add(btSetDefaults, AFTER, SAME);
    }

    private final void updatePeriods() {
        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            chkNMEAItems[i].select(m.getNMEAPeriod(i));
            // chkNMEAItems[i].repaintNow();
        }
    }

    private void setPeriods() {
        int[] Periods = new int[BT747Constants.C_NMEA_SEN_COUNT];

        for (int i = 0; i < BT747Constants.C_NMEA_SEN_COUNT; i++) {
            Periods[i] = chkNMEAItems[i].getSelectedIndex();
        }
        c.setNMEAPeriods(Periods);
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
            if (event.target == this) {
                c.setMtkDataNeeded(MtkModel.DATA_NMEA_OUTPUT_PERIODS);
                event.consumed = true;
            } else if (event.target == btSet) {
                setPeriods();
                event.consumed = true;
            } else if (event.target == btSetDefaults) {
                c.setNMEADefaultPeriods();
                event.consumed = true;
            } else {
                // boolean z_updated=false;
                // for (int i=0;i<C_LOG_FMT_COUNT;i++) {
                // if (event.target==chkLogFmtItems[i]) {
                // z_updated=true;
                // }
                // }
                // if(z_updated) {
                // setLogFormatControls();
                // }
            }

            break;
            default:
                break;
        }
    }

    public final void modelEvent(final ModelEvent event) {
        int eventType = event.getType();
        if (eventType == ModelEvent.UPDATE_OUTPUT_NMEA_PERIOD) {
            // updateLogFormat(m_GPSstate.logFormat);
            updatePeriods();
        }
    }
}
