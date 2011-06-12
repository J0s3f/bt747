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
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;

import gps.mvc.MtkModel;

import bt747.Txt;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.JavaLibBridge;

/**
 * @author Mario De Weerd
 */
public final class GPSLogReason extends Container implements ModelListener {
    private static final boolean ENABLE_PWR_SAVE_CONTROL = false;
    private final AppController c;
    private final Model m;

    private MyCheck chkTimeOnOff;
    private MyCheck chkDistanceOnOff;
    private MyCheck chkSpeedOnOff;
    private Edit edTime;
    private Edit edDistance;
    private Edit edSpeed;
    private Edit edFix;

    private Button btSet;

    private MyCheck chkPowerSaveOnOff;
    private MyCheck chkSBASOnOff;
    private MyCheck chkSBASTestOnOff;
    private ComboBox cbDGPSMode;
    private static final String[] strDatumMode = { "WGS84", "TOKYO-M",
            "TOKYO-A" };
    private ComboBox cbDatumMode;

    public GPSLogReason(final AppController c, final Model m) {
        this.c = c;
        this.m = m;

    }

    protected final void onStart() {
        super.onStart();
        add(chkTimeOnOff = new MyCheck(Txt.getString(Txt.RCR_TIME)), LEFT, TOP); //$NON-NLS-1$
        add(edTime = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(chkSpeedOnOff = new MyCheck(Txt.getString(Txt.RCR_SPD)), LEFT, AFTER); //$NON-NLS-1$
        add(edSpeed = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(chkDistanceOnOff = new MyCheck(Txt.getString(Txt.RCR_DIST)), LEFT, AFTER); //$NON-NLS-1$
        add(edDistance = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(edFix = new Edit(), SAME, AFTER); //$NON-NLS-1$
        add(new Label(Txt.getString(Txt.FIX_PER)),BEFORE, SAME);
        edSpeed.setValidChars(Edit.numbersSet);
        edDistance.setValidChars(Edit.numbersSet);
        edTime.setValidChars(Edit.numbersSet + ".");
        add(chkSBASOnOff = new MyCheck("SBAS"), LEFT, AFTER + 3); //$NON-NLS-1$
        cbDGPSMode = new ComboBox();
        final String[] tmp = { Txt.getString(Txt.NO_DGPS), Txt.getString(Txt.RTCM),
                Txt.getString(Txt.WAAS) };
        cbDGPSMode.add(tmp);
        add(cbDGPSMode, AFTER, SAME);
        add(chkSBASTestOnOff = new MyCheck(Txt.getString(Txt.INCL_TST_SBAS)), RIGHT, SAME); //$NON-NLS-1$
        cbDatumMode = new ComboBox();
        cbDatumMode.setEnabled(false);
        cbDatumMode.add(strDatumMode);
        add(cbDatumMode, LEFT, AFTER + 6);
        if (ENABLE_PWR_SAVE_CONTROL) {
            add(chkPowerSaveOnOff = new MyCheck(Txt.getString(Txt.PWR_SAVE_INTRNL)), LEFT,
                    AFTER + 3); //$NON-NLS-1$
        }

        add(btSet = new Button(Txt.getString(Txt.SET)), CENTER, AFTER + 3); //$NON-NLS-1$
    }

    public final void updateButtons() {
        chkTimeOnOff.setChecked(m.getLogTimeInterval() != 0);
        edTime.setEnabled(m.getLogTimeInterval() != 0);
        if (m.getLogTimeInterval() != 0) {
            edTime.setText(JavaLibBridge.toString(
                    ((float) m.getLogTimeInterval()) / 10, 1));
        }
        chkSpeedOnOff.setChecked(m.getLogSpeedInterval() != 0);
        edSpeed.setEnabled(m.getLogSpeedInterval() != 0);
        if (m.getLogSpeedInterval() != 0) {
            edSpeed.setText("" + m.getLogSpeedInterval());
        }
        chkDistanceOnOff.setChecked(m.getLogDistanceInterval() != 0);
        edDistance.setEnabled(m.getLogDistanceInterval() != 0);
        if (m.getLogDistanceInterval() != 0) {
            edDistance.setText(JavaLibBridge.toString((float) m
                    .getLogDistanceInterval() / 10, 1));
        }
        edFix.setText("" + m.getLogFixPeriod());
        chkSBASOnOff.setChecked(m.isSBASEnabled());
        chkSBASTestOnOff.setChecked(m.isSBASTestEnabled());
        cbDGPSMode.select(m.getDgpsMode());
        if (ENABLE_PWR_SAVE_CONTROL) {
            chkPowerSaveOnOff.setChecked(m.isPowerSaveEnabled());
        }
        cbDatumMode.select(m.getDatum());

    }

    public final void setSettings() {
        if (chkTimeOnOff.getChecked()) {
            c.setLogTimeInterval((int) (10 * JavaLibBridge
                    .toFloat(edTime.getText())));
        } else {
            c.setLogTimeInterval(0);
        }
        if (chkSpeedOnOff.getChecked()) {
            c.setLogSpeedInterval(JavaLibBridge.toInt(edSpeed.getText()));
        } else {
            c.setLogSpeedInterval(0);
        }
        if (chkDistanceOnOff.getChecked()) {
            c.setLogDistanceInterval((int) (10 * JavaLibBridge.toFloat(edDistance
                    .getText())));
        } else {
            c.setLogDistanceInterval(0);
        }
        c.setFixInterval(JavaLibBridge.toInt(edFix.getText()));
        c.setMtkDataNeeded(MtkModel.DATA_LOG_TIME_INTERVAL);
        c.setMtkDataNeeded(MtkModel.DATA_LOG_SPEED_INTERVAL);
        c.setMtkDataNeeded(MtkModel.DATA_LOG_DISTANCE_INTERVAL);
    }

    public final void onEvent(final Event event) {
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed = true;
            if (event.target == chkTimeOnOff) {
                edTime.setEnabled(chkTimeOnOff.getChecked());
            } else if (event.target == chkSpeedOnOff) {
                edSpeed.setEnabled(chkSpeedOnOff.getChecked());
            } else if (event.target == chkDistanceOnOff) {
                edDistance.setEnabled(chkDistanceOnOff.getChecked());
            } else if (event.target == btSet) {
                setSettings();
            } else if (event.target == this) {
                c.setMtkDataNeeded(MtkModel.DATA_LOG_TIME_INTERVAL);
                c.setMtkDataNeeded(MtkModel.DATA_LOG_SPEED_INTERVAL);
                c.setMtkDataNeeded(MtkModel.DATA_LOG_DISTANCE_INTERVAL);
                c.setMtkDataNeeded(MtkModel.DATA_FIX_PERIOD);
                c.setMtkDataNeeded(MtkModel.DATA_SBAS_STATUS);
                c.setMtkDataNeeded(MtkModel.DATA_SBAS_TEST_STATUS);
                c.setMtkDataNeeded(MtkModel.DATA_DGPS_MODE);
            } else if (event.target == chkSBASOnOff) {
                c.setSBASEnabled(chkSBASOnOff.getChecked());
            } else if (event.target == chkSBASTestOnOff) {
                c.setSBASTestEnabled(chkSBASTestOnOff.getChecked());
            } else if (ENABLE_PWR_SAVE_CONTROL
                    && (event.target == chkPowerSaveOnOff)) {
                c.setPowerSaveEnabled(chkPowerSaveOnOff.getChecked());
            } else if (event.target == cbDGPSMode) {
                c.setDGPSMode(cbDGPSMode.getSelectedIndex());
            } else {
                event.consumed = false;
            }
            break;
        default:
        }
    }
    
    public final void modelEvent(final ModelEvent event) {
        switch (event.getType()) {
        case ModelEvent.UPDATE_LOG_TIME_INTERVAL:
        case ModelEvent.UPDATE_LOG_SPEED_INTERVAL:
        case ModelEvent.UPDATE_LOG_DISTANCE_INTERVAL:
        case ModelEvent.UPDATE_FIX_PERIOD:
        case ModelEvent.UPDATE_DGPS_MODE:
        case ModelEvent.UPDATE_PWR_SAV_MODE:
        case ModelEvent.UPDATE_SBAS:
        case ModelEvent.UPDATE_SBAS_TEST:
        case ModelEvent.UPDATE_DATUM:
            updateButtons();
            break;

        default:
            break;
        }
    }

}
