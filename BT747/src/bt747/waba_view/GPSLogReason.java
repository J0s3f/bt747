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

import gps.GpsEvent;

import bt747.Txt;
import bt747.model.AppController;
import bt747.model.Model;
import bt747.sys.Convert;

/**
 * @author Mario De Weerd
 */
public class GPSLogReason extends Container {
    private static final boolean ENABLE_PWR_SAVE_CONTROL = false;
    private AppController c;
    private Model m;

    private MyCheck chkTimeOnOff;
    private MyCheck chkDistanceOnOff;
    private MyCheck chkSpeedOnOff;
    private MyCheck chkFixOnOff;
    private Edit edTime;
    private Edit edDistance;
    private Edit edSpeed;
    private Edit edFix;

    private Button btSet;

    private MyCheck chkPowerSaveOnOff;
    private MyCheck chkSBASOnOff;
    private MyCheck chkSBASTestOnOff;
    private static final String[] strDGPSMode = { Txt.NO_DGPS, Txt.RTCM,
            Txt.WAAS };
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
        add(chkTimeOnOff = new MyCheck(Txt.RCR_TIME), LEFT, TOP); //$NON-NLS-1$
        add(edTime = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(chkSpeedOnOff = new MyCheck(Txt.RCR_SPD), LEFT, AFTER); //$NON-NLS-1$
        add(edSpeed = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(chkDistanceOnOff = new MyCheck(Txt.RCR_DIST), LEFT, AFTER); //$NON-NLS-1$
        add(edDistance = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(chkFixOnOff = new MyCheck(Txt.FIX_PER), LEFT, AFTER); //$NON-NLS-1$
        add(edFix = new Edit(), AFTER, SAME); //$NON-NLS-1$
        edSpeed.setValidChars(Edit.numbersSet);
        edDistance.setValidChars(Edit.numbersSet);
        edTime.setValidChars(Edit.numbersSet + ".");
        add(chkSBASOnOff = new MyCheck("SBAS"), LEFT, AFTER + 3); //$NON-NLS-1$
        cbDGPSMode = new ComboBox();
        cbDGPSMode.add(strDGPSMode);
        add(cbDGPSMode, AFTER, SAME);
        add(chkSBASTestOnOff = new MyCheck(Txt.INCL_TST_SBAS), RIGHT, SAME); //$NON-NLS-1$
        cbDatumMode = new ComboBox();
        cbDatumMode.setEnabled(false);
        cbDatumMode.add(strDatumMode);
        add(cbDatumMode, LEFT, AFTER + 3);
        if (ENABLE_PWR_SAVE_CONTROL) {
            add(chkPowerSaveOnOff = new MyCheck(Txt.PWR_SAVE_INTRNL), LEFT,
                    AFTER + 3); //$NON-NLS-1$
        }

        add(btSet = new Button(Txt.SET), CENTER, AFTER + 3); //$NON-NLS-1$
    }

    public final void updateButtons() {
        chkTimeOnOff.setChecked(m.getLogTimeInterval() != 0);
        edTime.setEnabled(m.getLogTimeInterval() != 0);
        if (m.getLogTimeInterval() != 0) {
            edTime.setText(Convert.toString(
                    ((float) m.getLogTimeInterval()) / 10, 1));
        }
        chkSpeedOnOff.setChecked(m.getLogSpeedInterval() != 0);
        edSpeed.setEnabled(m.getLogSpeedInterval() != 0);
        if (m.getLogSpeedInterval() != 0) {
            edSpeed.setText(Convert.toString(m.getLogSpeedInterval()));
        }
        chkDistanceOnOff.setChecked(m.getLogDistanceInterval() != 0);
        edDistance.setEnabled(m.getLogDistanceInterval() != 0);
        if (m.getLogDistanceInterval() != 0) {
            edDistance.setText(Convert.toString((float) m
                    .getLogDistanceInterval() / 10, 1));
        }
        chkFixOnOff.setChecked(m.getLogFixPeriod() != 0);
        edFix.setEnabled(m.getLogFixPeriod() != 0);
        if (m.getLogFixPeriod() != 0) {
            edFix.setText(Convert.toString(m.getLogFixPeriod()));
        }
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
            c.setLogTimeInterval((int) (10 * Convert
                    .toFloat(edTime.getText())));
        } else {
            c.setLogTimeInterval(0);
        }
        if (chkSpeedOnOff.getChecked()) {
            c.setLogSpeedInterval(Convert.toInt(edSpeed.getText()));
        } else {
            c.setLogSpeedInterval(0);
        }
        if (chkDistanceOnOff.getChecked()) {
            c.setLogDistanceInterval((int) (10 * Convert.toFloat(edDistance
                    .getText())));
        } else {
            c.setLogDistanceInterval(0);
        }
        if (chkFixOnOff.getChecked()) {
            c.setFixInterval(Convert.toInt(edFix.getText()));
        }
        c.reqLogReasonStatus();
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
            } else if (event.target == chkFixOnOff) {
                edFix.setEnabled(chkFixOnOff.getChecked());
            } else if (event.target == btSet) {
                setSettings();
            } else if (event.target == this) {
                c.reqLogReasonStatus();
                c.reqFixInterval();
                c.reqSBASEnabled();
                c.reqSBASTestEnabled();
                c.reqDGPSMode();
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
            if (event.type == GpsEvent.DATA_UPDATE) {
                if (event.target == this) {
                    updateButtons();
                    event.consumed = true;
                }
            }
        }
    }

}
