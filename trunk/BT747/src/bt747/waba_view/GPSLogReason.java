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
import bt747.model.Controller;
import bt747.model.Model;
import bt747.sys.Convert;
/**
 * @author Mario De Weerd
 */
public class GPSLogReason extends Container {
    private static final boolean ENABLE_PWR_SAVE_CONTROL=false;
    private Controller c;
    private Model m;
    
    private MyCheck m_chkTimeOnOff;
    private MyCheck m_chkDistanceOnOff;
    private MyCheck m_chkSpeedOnOff;
    private MyCheck m_chkFixOnOff;
    private Edit m_edTime;
    private Edit m_edDistance;
    private Edit m_edSpeed;
    private Edit m_edFix;
    
    private Button m_btSet;
    
    private MyCheck m_chkPowerSaveOnOff;
    private MyCheck m_chkSBASOnOff;
    private MyCheck m_chkSBASTestOnOff;
    private static final String[] strDGPSMode= {Txt.NO_DGPS, Txt.RTCM,Txt.WAAS};
    private ComboBox m_cbDGPSMode;
    private static final String[] strDatumMode= {"WGS84", "TOKYO-M","TOKYO-A"};
    private ComboBox m_cbDatumMode;
    
    
    public GPSLogReason(Controller c, Model m) {
        this.c=c;
        this.m=m;
        
    }
    
    protected void onStart() {
        super.onStart();
        add(m_chkTimeOnOff      = new MyCheck(Txt.RCR_TIME), LEFT, TOP); //$NON-NLS-1$
        add(m_edTime = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkSpeedOnOff     = new MyCheck(Txt.RCR_SPD), LEFT, AFTER); //$NON-NLS-1$
        add(m_edSpeed = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkDistanceOnOff  = new MyCheck(Txt.RCR_DIST), LEFT, AFTER); //$NON-NLS-1$
        add(m_edDistance = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkFixOnOff = new MyCheck(Txt.FIX_PER), LEFT,AFTER); //$NON-NLS-1$
        add(m_edFix = new Edit(), AFTER, SAME); //$NON-NLS-1$
        m_edSpeed.setValidChars(Edit.numbersSet);
        m_edDistance.setValidChars(Edit.numbersSet);
        m_edTime.setValidChars(Edit.numbersSet+".");
        add(m_chkSBASOnOff = new MyCheck("SBAS"), LEFT, AFTER+3); //$NON-NLS-1$
        m_cbDGPSMode=new ComboBox();
        m_cbDGPSMode.add(strDGPSMode);
        add(m_cbDGPSMode, AFTER, SAME);
        add(m_chkSBASTestOnOff = new MyCheck(Txt.INCL_TST_SBAS), RIGHT, SAME); //$NON-NLS-1$
        m_cbDatumMode=new ComboBox();
        m_cbDatumMode.setEnabled(false);
        m_cbDatumMode.add(strDatumMode);
        add(m_cbDatumMode, LEFT, AFTER+3);
        if(ENABLE_PWR_SAVE_CONTROL) {
            add(m_chkPowerSaveOnOff = new MyCheck(Txt.PWR_SAVE_INTRNL), LEFT, AFTER+3); //$NON-NLS-1$
        }
        
        
        add(m_btSet = new Button(Txt.SET), CENTER, AFTER+3); //$NON-NLS-1$
    }
    
    
    public void updateButtons() {
        m_chkTimeOnOff.setChecked(m.getLogTimeInterval()!=0);
        m_edTime.setEnabled(m.getLogTimeInterval()!=0);
        if(m.getLogTimeInterval()!=0) {
            m_edTime.setText(Convert.toString(((float)m.getLogTimeInterval())/10,1));
        }
        m_chkSpeedOnOff.setChecked(m.getLogSpeedInterval()!=0);
        m_edSpeed.setEnabled(m.getLogSpeedInterval()!=0);
        if(m.getLogSpeedInterval()!=0) {
            m_edSpeed.setText(Convert.toString(m.getLogSpeedInterval()));
        }
        m_chkDistanceOnOff.setChecked(m.getLogDistanceInterval()!=0);
        m_edDistance.setEnabled(m.getLogDistanceInterval()!=0);
        if(m.getLogDistanceInterval()!=0) {
            m_edDistance.setText(Convert.toString((float)m.getLogDistanceInterval()/10,1));
        }
        m_chkFixOnOff.setChecked(m.getLogFixPeriod()!=0);
        m_edFix.setEnabled(m.getLogFixPeriod()!=0);
        if(m.getLogFixPeriod()!=0) {
            m_edFix.setText(Convert.toString(m.getLogFixPeriod()));
        }
        m_chkSBASOnOff.setChecked(m.isSBASEnabled());
        m_chkSBASTestOnOff.setChecked(m.isSBASTestEnabled());
        m_cbDGPSMode.select(m.getDgpsMode());
        if(ENABLE_PWR_SAVE_CONTROL) {
            m_chkPowerSaveOnOff.setChecked(m.isPowerSaveEnabled());
        }
        m_cbDatumMode.select(m.getDatum());

    }
    
    public void setSettings() {
        if(m_chkTimeOnOff.getChecked()) {
            c.setLogTimeInterval((int)(10*Convert.toFloat(m_edTime.getText())));
        } else {
            c.setLogTimeInterval(0);
        }
        if(m_chkSpeedOnOff.getChecked()) {
            c.setLogSpeedInterval(Convert.toInt(m_edSpeed.getText()));
        } else {
            c.setLogSpeedInterval(0);
        }
        if(m_chkDistanceOnOff.getChecked()) {
            c.setLogDistanceInterval((int)(10*Convert.toFloat(m_edDistance.getText())));
        } else {
            c.setLogDistanceInterval(0);
        }
        if(m_chkFixOnOff.getChecked()) {
            c.setFixInterval(Convert.toInt(m_edFix.getText()));
        }
        c.reqLogReasonStatus();
    }
    
    
    public void onEvent(Event event) {
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed=true;
        if(event.target==m_chkTimeOnOff) {
            m_edTime.setEnabled(m_chkTimeOnOff.getChecked());
        } else if(event.target==m_chkSpeedOnOff) {
            m_edSpeed.setEnabled(m_chkSpeedOnOff.getChecked());
        } else if(event.target==m_chkDistanceOnOff) {
            m_edDistance.setEnabled(m_chkDistanceOnOff.getChecked());
        } else if(event.target==m_chkFixOnOff) {
            m_edFix.setEnabled(m_chkFixOnOff.getChecked());
        } else if(event.target==m_btSet) {
            setSettings();
        } else if (event.target == this) {
            c.reqLogReasonStatus();
            c.reqFixInterval();
            c.reqSBASEnabled();
            c.reqSBASTestEnabled();
            c.reqDGPSMode();
        } else if (event.target==m_chkSBASOnOff) {
            c.setSBASEnabled(m_chkSBASOnOff.getChecked());
        } else if (event.target==m_chkSBASTestOnOff) {
            c.setSBASTestEnabled(m_chkSBASTestOnOff.getChecked());
        } else if (ENABLE_PWR_SAVE_CONTROL && (event.target==m_chkPowerSaveOnOff)) {
            c.setPowerSaveEnabled(m_chkPowerSaveOnOff.getChecked());
        } else if (event.target==m_cbDGPSMode) {
            c.setDGPSMode(m_cbDGPSMode.getSelectedIndex());
        } else {
            event.consumed=false;
        }
        break;
        default:
            if(event.type==GpsEvent.DATA_UPDATE) {
                if(event.target==this) {
                    updateButtons();
                    event.consumed=true;
                }
            }
        }
    }
    
    
    
}
