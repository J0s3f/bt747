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
import bt747.sys.Convert;
import waba.ui.Button;
import waba.ui.Check;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;

import gps.GPSstate;
import gps.GpsEvent;
/**
 * @author Mario De Weerd
 */
public class GPSLogReason extends Container {
    final private static boolean ENABLE_PWR_SAVE_CONTROL=false;
    private GPSstate m_GPSstate;
    
    private Check m_chkTimeOnOff;
    private Check m_chkDistanceOnOff;
    private Check m_chkSpeedOnOff;
    private Check m_chkFixOnOff;
    private Edit m_edTime;
    private Edit m_edDistance;
    private Edit m_edSpeed;
    private Edit m_edFix;
    
    private Button m_btSet;
    
    private Check m_chkPowerSaveOnOff;
    private Check m_chkSBASOnOff;
    private Check m_chkSBASTestOnOff;
    private final static String[] strDGPSMode= {"No DGPS", "RTCM","WAAS"};
    private ComboBox m_cbDGPSMode;
    private final static String[] strDatumMode= {"WGS84", "TOKYO-M","TOKYO-A"};
    private ComboBox m_cbDatumMode;
    
    
    public GPSLogReason(GPSstate state) {
        m_GPSstate= state;
        
    }
    
    protected void onStart() {
        super.onStart();
        add(m_chkTimeOnOff      = new Check("Time (s)    "), LEFT, TOP); //$NON-NLS-1$
        add(m_edTime = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkSpeedOnOff     = new Check("Speed (km/h)"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edSpeed = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkDistanceOnOff  = new Check("Distance (m)"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edDistance = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkFixOnOff = new Check("Fix (ms)"), LEFT,AFTER); //$NON-NLS-1$
        add(m_edFix = new Edit(), AFTER, SAME); //$NON-NLS-1$
        m_edSpeed.setValidChars(Edit.numbersSet);
        m_edDistance.setValidChars(Edit.numbersSet);
        m_edTime.setValidChars(Edit.numbersSet+".");
        add(m_chkSBASOnOff = new Check("SBAS"), LEFT, AFTER+3); //$NON-NLS-1$
        m_cbDGPSMode=new ComboBox();
        m_cbDGPSMode.add(strDGPSMode);
        add(m_cbDGPSMode, AFTER, SAME);
        add(m_chkSBASTestOnOff = new Check("Incl. Test SBAS"), RIGHT, SAME); //$NON-NLS-1$
        m_cbDatumMode=new ComboBox();
        m_cbDatumMode.setEnabled(false);
        m_cbDatumMode.add(strDatumMode);
        add(m_cbDatumMode, LEFT, AFTER+3);
        if(ENABLE_PWR_SAVE_CONTROL) {
            add(m_chkPowerSaveOnOff = new Check("Power Save (Internal)"), LEFT, AFTER+3); //$NON-NLS-1$
        }
        
        
        add(m_btSet = new Button("SET"), CENTER, AFTER+3); //$NON-NLS-1$
        
        
    }
    
    
    public void updateButtons() {
        m_chkTimeOnOff.setChecked(m_GPSstate.logTimeInterval!=0);
        m_edTime.setEnabled(m_GPSstate.logTimeInterval!=0);
        if(m_GPSstate.logTimeInterval!=0) {
            m_edTime.setText(Convert.toString(((float)m_GPSstate.logTimeInterval)/10,1));
        }
        m_chkSpeedOnOff.setChecked(m_GPSstate.logSpeedInterval!=0);
        m_edSpeed.setEnabled(m_GPSstate.logSpeedInterval!=0);
        if(m_GPSstate.logSpeedInterval!=0) {
            m_edSpeed.setText(Convert.toString(m_GPSstate.logSpeedInterval));
        }
        m_chkDistanceOnOff.setChecked(m_GPSstate.logDistanceInterval!=0);
        m_edDistance.setEnabled(m_GPSstate.logDistanceInterval!=0);
        if(m_GPSstate.logDistanceInterval!=0) {
            m_edDistance.setText(Convert.toString(m_GPSstate.logDistanceInterval));
        }
        m_chkFixOnOff.setChecked(m_GPSstate.logFix!=0);
        m_edFix.setEnabled(m_GPSstate.logFix!=0);
        if(m_GPSstate.logFix!=0) {
            m_edFix.setText(Convert.toString(m_GPSstate.logFix));
        }
        m_chkSBASOnOff.setChecked(m_GPSstate.SBASEnabled);
        m_chkSBASTestOnOff.setChecked(m_GPSstate.SBASTestEnabled);
        m_cbDGPSMode.select(m_GPSstate.dgps_mode);
        if(ENABLE_PWR_SAVE_CONTROL) {
            m_chkPowerSaveOnOff.setChecked(m_GPSstate.powerSaveEnabled);
            m_chkPowerSaveOnOff.repaintNow();
        }
        m_cbDatumMode.select(m_GPSstate.datum);
        m_chkTimeOnOff.repaintNow();
        m_edTime.repaintNow();
        m_chkSpeedOnOff.repaintNow();
        m_edSpeed.repaintNow();
        m_chkDistanceOnOff.repaintNow();
        m_edDistance.repaintNow();
        m_chkFixOnOff.repaintNow();
        m_edFix.repaintNow();
        m_chkSBASOnOff.repaintNow();
        m_chkSBASTestOnOff.repaintNow();
        m_cbDGPSMode.repaintNow();
        m_cbDatumMode.repaintNow();
    }
    
    public void setSettings() {
        if(m_chkTimeOnOff.getChecked()) {
            m_GPSstate.setLogTimeInterval((int)(10*Convert.toFloat(m_edTime.getText())));
        } else {
            m_GPSstate.setLogTimeInterval(0);
        }
        if(m_chkSpeedOnOff.getChecked()) {
            m_GPSstate.setLogSpeedInterval(Convert.toInt(m_edSpeed.getText()));
        } else {
            m_GPSstate.setLogSpeedInterval(0);
        }
        if(m_chkDistanceOnOff.getChecked()) {
            m_GPSstate.setLogDistanceInterval(Convert.toInt(m_edDistance.getText()));
        } else {
            m_GPSstate.setLogDistanceInterval(0);
        }
        if(m_chkFixOnOff.getChecked()) {
            m_GPSstate.setFixInterval(Convert.toInt(m_edFix.getText()));
            m_GPSstate.getFixInterval();
        }
        m_GPSstate.getLogReasonStatus();
    }
    
    
    public void onEvent(Event event) {
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed=true;
        if(event.target==m_chkTimeOnOff) {
            m_edTime.setEnabled(m_chkTimeOnOff.getChecked());
            m_edTime.repaintNow();
        } else if(event.target==m_chkSpeedOnOff) {
            m_edSpeed.setEnabled(m_chkSpeedOnOff.getChecked());
            m_edSpeed.repaintNow();
        } else if(event.target==m_chkDistanceOnOff) {
            m_edDistance.setEnabled(m_chkDistanceOnOff.getChecked());
            m_edDistance.repaintNow();
        } else if(event.target==m_chkFixOnOff) {
            m_edFix.setEnabled(m_chkFixOnOff.getChecked());
            m_edFix.repaintNow();
        } else if(event.target==m_btSet) {
            setSettings();
        } else if (event.target == this) {
            m_GPSstate.getLogReasonStatus();
            m_GPSstate.getFixInterval();
            m_GPSstate.getSBASEnabled();
            m_GPSstate.getSBASTestEnabled();
            m_GPSstate.getDGPSMode();
        } else if (event.target==m_chkSBASOnOff) {
            m_GPSstate.setSBASEnabled(m_chkSBASOnOff.getChecked());
            m_GPSstate.getSBASEnabled();
        } else if (event.target==m_chkSBASTestOnOff) {
            m_GPSstate.setSBASTestEnabled(m_chkSBASTestOnOff.getChecked());
            m_GPSstate.getSBASTestEnabled();
        } else if (ENABLE_PWR_SAVE_CONTROL && (event.target==m_chkPowerSaveOnOff)) {
            m_GPSstate.setPowerSaveEnabled(m_chkPowerSaveOnOff.getChecked());
            m_GPSstate.getPowerSaveEnabled();
        } else if (event.target==m_cbDGPSMode) {
            m_GPSstate.setDGPSMode(m_cbDGPSMode.getSelectedIndex());
            m_GPSstate.getDGPSMode();
        } else {
            event.consumed=false;
        }
        break;
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                updateButtons();
                event.consumed=true;
            }
        }
    }
    
    
    
}
