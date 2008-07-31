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
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;

import gps.BT747Constants;

import bt747.Txt;
import bt747.model.AppController;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * Defines the container that allows the selection of the fields that will be
 * kept in the output format.
 * 
 * @author Mario De Weerd
 */
public class GpsFileLogFormat extends Container implements ModelListener {
    private static final int C_LOG_FMT_COUNT = 21 - 1;
    /** The object that is used to communicate with the GPS device. */
    private Model m;
    private AppController c;
    /** The tickboxes for the format items */
    private MyCheck[] chkLogFmtItems = new MyCheck[C_LOG_FMT_COUNT];
    /** When selected output 'comments' in certain formats */
    private MyCheck commentCheck;
    /** When selected give a name to individual trackpoints */
    private MyCheck nameCheck;

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
        commentCheck = new MyCheck(Txt.TRKPTCOMMENT);
        nameCheck = new MyCheck(Txt.TRKPTNAME);
        add(commentCheck,LEFT,AFTER+6);
        add(nameCheck,getClientRect().width / 2,SAME);
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
        commentCheck.setChecked(m
                .getBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT));
        nameCheck.setChecked(m.getBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME));
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
                updateLogFormat(m.getIntOpt(Model.FILEFIELDFORMAT));
                event.consumed = true;
            } else if (event.target == commentCheck) {
                c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_COMMENT, commentCheck
                        .getChecked());
            } else if (event.target == nameCheck) {
                c.setBooleanOpt(Model.IS_WRITE_TRACKPOINT_NAME, nameCheck
                        .getChecked());
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
                c.setIntOpt(Model.FILEFIELDFORMAT,  getSelectedLogFormat());
            }

            break;
        }
    }

    final public void modelEvent(ModelEvent event) {
        int eventType = event.getType();
        if (eventType == ModelEvent.FILE_LOG_FORMAT_UPDATE) {
            updateLogFormat(m.getIntOpt(Model.FILEFIELDFORMAT));
        } else if (eventType == ModelEvent.SETTING_CHANGE) {
            // switch (event.getArg()) {
            //            
            // }
        }
    }
}
