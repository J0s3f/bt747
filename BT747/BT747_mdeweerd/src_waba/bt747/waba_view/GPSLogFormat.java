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
//***  IS ASSUMED BY THE USER.                                     ***
//***  See the GNU General Public License Version 3 for details.   ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************                              
import gps.BT747Constants;
import gps.mvc.MtkModel;
import waba.ui.Button;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import bt747.Txt;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * @author Mario De Weerd
 */
public final class GPSLogFormat extends Container implements ModelListener {
    private static final int C_LOG_FMT_COUNT = 21;
    /** The object that is used to communicate with the GPS device. */
    private final Model m;
    private final AppController c;
    /** The tick boxes for the format items */
    private final MyCheck[] chkLogFmtItems = new MyCheck[C_LOG_FMT_COUNT];
    /** The button that requests to change the log format of the device */
    private Button btChangeFormatErase;
    private Button btChangeFormat;
    private Button btErase;

    private Label lbEstNbrRecords;

    /**
     * Initializer of this Container.
     */
    public GPSLogFormat(final Model m, final AppController c) {
        // super("Log ON/OFF", Container.);
        this.m = m;
        this.c = c;
    };

    /**
     * Initiliazer once all objects received initial setup.
     * 
     */
    public final void onStart() {
        // Add all tick buttons.
        for (int i = 0; i < C_LOG_FMT_COUNT; i++) {
            chkLogFmtItems[i] = new MyCheck(Txt.getLogFmtItem(i));
            // int
            // extra_offset=chkLogFmtItems[i].fm.height-chkLogFmtItems[i].getPreferredHeight();
            // add(chkLogFmtItems[i]);
            // int x=((i==0)?LEFT:((i==((C_LOG_FMT_COUNT/2)))?
            // getClientRect().width/2:SAME));
            // if(((i==0) ||i==((C_LOG_FMT_COUNT/2)))) {
            // chkLogFmtItems[i].setRect(x, TOP, PREFERRED+extra_offset-1,
            // chkLogFmtItems[i].fm.height-1);
            // } else {
            // chkLogFmtItems[i].setRect(x, AFTER-1, PREFERRED+extra_offset-1,
            // chkLogFmtItems[i].fm.height-1);
            // }
            add(
                    chkLogFmtItems[i],
                    ((i == 0) ? LEFT
                            : ((i == ((C_LOG_FMT_COUNT / 2))) ? getClientRect().width / 2
                                    : SAME)),
                    ((i == 0) || i == ((C_LOG_FMT_COUNT / 2))) ? TOP
                            : AFTER - 1);
            chkLogFmtItems[i].setEnabled(true);
        }
        lbEstNbrRecords = new Label("0000000" + Txt.getString(Txt.REC_ESTIMATED));
        add(lbEstNbrRecords, LEFT, AFTER);
        lbEstNbrRecords.setText("");


        // Add button confirming change of log format.
        btChangeFormatErase = new Button(Txt.getString(Txt.SET_ERASE));
        add(btChangeFormatErase, LEFT, AFTER + 5);
        add(btChangeFormat = new Button(Txt.getString(Txt.SET_NOERASE)), AFTER + 10, SAME);
        add(btErase = new Button(Txt.getString(Txt.ERASE)), RIGHT, SAME);
        setLogFormatControls();
    }

    /** Get the format set by the user in the user interface. */
    private int getSelectedLogFormat() {
        int bitMask = 1;
        int logFormat = 0;
        for (int i = 0; i < C_LOG_FMT_COUNT - 1; i++) {
            if (chkLogFmtItems[i].getChecked()) {
                logFormat |= bitMask;
            }
            bitMask <<= 1;
        }
        // Special case : valid fix only
        if (chkLogFmtItems[C_LOG_FMT_COUNT - 1].getChecked()) {
            logFormat |= (1 << BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX);
        }
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

        for (int i = 0; i < C_LOG_FMT_COUNT - 1; i++) {
            chkLogFmtItems[i].setChecked((pLogFormat & bitMask) != 0);
            // chkLogFmtItems[i].repaintNow();
            bitMask <<= 1;
        }
        chkLogFmtItems[C_LOG_FMT_COUNT - 1]
                .setChecked((pLogFormat & (1 << BT747Constants.FMT_LOG_PTS_WITH_VALID_FIX_ONLY_IDX)) != 0);
        setLogFormatControls();
    }

    private void setLogFormatControls() {
        boolean sidSet;
        sidSet = chkLogFmtItems[BT747Constants.FMT_SID_IDX].getChecked();
        chkLogFmtItems[BT747Constants.FMT_ELEVATION_IDX].setEnabled(sidSet);
        chkLogFmtItems[BT747Constants.FMT_AZIMUTH_IDX].setEnabled(sidSet);
        chkLogFmtItems[BT747Constants.FMT_SNR_IDX].setEnabled(sidSet);

        lbEstNbrRecords.setText(m
                .getEstimatedNbrRecords(getSelectedLogFormat())
                + Txt.getString(Txt.REC_ESTIMATED));
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
            if (event.target == btChangeFormatErase) {
                c.changeLogFormatAndErase(getSelectedLogFormat());
            } else if (event.target == btChangeFormat) {
                c.changeLogFormat(getSelectedLogFormat());
            } else if (event.target == btErase) {
                c.eraseLogWithDialogs();
            } else if (event.target == this) {
                c.setMtkDataNeeded(MtkModel.DATA_LOG_FORMAT);
                updateLogFormat(m.getLogFormat());
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
            }

            break;
        default:
        }
    }

    public final void modelEvent(final ModelEvent event) {
        switch(event.getType()) {
        case ModelEvent.UPDATE_LOG_FLASH:
            setLogFormatControls();
            break;
        case ModelEvent.UPDATE_LOG_FORMAT:
            updateLogFormat(m.getLogFormat());
            break;
        }
    }
}
