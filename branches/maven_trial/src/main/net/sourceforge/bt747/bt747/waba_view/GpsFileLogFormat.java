package net.sourceforge.bt747.bt747.waba_view;

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
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;

import net.sourceforge.bt747.gps.BT747Constants;

import net.sourceforge.bt747.bt747.model.AppController;
import net.sourceforge.bt747.bt747.model.Model;
import net.sourceforge.bt747.bt747.model.ModelEvent;

/**
 * Defines the container that allows the selection of the fields that will be
 * kept in the output format.
 * 
 * @author Mario De Weerd
 */
public class GpsFileLogFormat extends Container {
    private static final int C_LOG_FMT_COUNT = 21 - 1;
    /** The object that is used to communicate with the GPS device. */
    private Model m;
    private AppController c;
    /** The tickboxes for the format items */
    private MyCheck[] chkLogFmtItems = new MyCheck[C_LOG_FMT_COUNT];
    /** The button that requests to change the log format of the device */

    /**
     * Initialiser of this Container.<br>
     */
    public GpsFileLogFormat(final AppController c, final Model m) {
        // super("Log ON/OFF", Container.);
        this.m = m;
        this.c = c;
    };

    /**
     * Initiliaser once all objects received initial setup
     * 
     */
    public final void onStart() {
        // Add all tick buttons.
        for (int i = 0; i < C_LOG_FMT_COUNT; i++) {
            chkLogFmtItems[i] = new MyCheck(BT747Constants.logFmtItems[i]);
            add(
                    chkLogFmtItems[i],
                    ((i == 0) ? LEFT
                            : ((i == ((C_LOG_FMT_COUNT / 2))) ? getClientRect().width / 2
                                    : SAME)),
                    ((i == 0) || i == ((C_LOG_FMT_COUNT / 2))) ? TOP
                            : AFTER - 1);
            chkLogFmtItems[i].setEnabled(true);
        }
        setLogFormatControls();
    }

    /** Get the format set by the user in the user interface. */
    private int getSelectedLogFormat() {
        int bitMask = 1;
        int logFormat = 0;
        for (int i = 0; i < C_LOG_FMT_COUNT; i++) {
            if (chkLogFmtItems[i].getChecked()) {
                logFormat |= bitMask;
            }
            bitMask <<= 1;
        }
        // Special case : valid fix only
        return logFormat;
    }

    /**
     * Updates the format options shown the the user.<br>
     * This is typically done when the device responded with the current
     * settings.
     * 
     * @param pLogFormat
     *            LogFormat to set
     */
    private void updateLogFormat(final int pLogFormat) {
        int bitMask = 1;
        // if(GPS_DEBUG) {
        // waba.sys.Vm.debug("UPD:"+Convert.unsigned2hex(p_logFormat,2)+"\n");}

        for (int i = 0; i < C_LOG_FMT_COUNT; i++) {
            chkLogFmtItems[i].setChecked((pLogFormat & bitMask) != 0);
            // chkLogFmtItems[i].repaintNow();
            bitMask <<= 1;
        }
        setLogFormatControls();
    }

    private void setLogFormatControls() {
        boolean sidSet;
        sidSet = chkLogFmtItems[BT747Constants.FMT_SID_IDX].getChecked();
        chkLogFmtItems[BT747Constants.FMT_ELEVATION_IDX].setEnabled(sidSet);
        chkLogFmtItems[BT747Constants.FMT_AZIMUTH_IDX].setEnabled(sidSet);
        chkLogFmtItems[BT747Constants.FMT_SNR_IDX].setEnabled(sidSet);
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
                updateLogFormat(m.getFileLogFormat());
                event.consumed = true;
            } else {
                boolean isLogFmtUpdated = false;
                for (int i = 0; i < C_LOG_FMT_COUNT; i++) {
                    if (event.target == chkLogFmtItems[i]) {
                        isLogFmtUpdated = true;
                    }
                }
                if (isLogFmtUpdated) {
                    setLogFormatControls();
                }
                c.setFileLogFormat(getSelectedLogFormat());
            }

            break;
        default:
            if (event.type == ModelEvent.FILE_LOG_FORMAT_UPDATE) {
                updateLogFormat(m.getFileLogFormat());
            }
        }
    }
}
