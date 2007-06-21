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
import waba.sys.Convert;
import waba.ui.Button;
import waba.ui.Check;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;

import gps.GPSstate;
/**
 * @author Mario De Weerd
 */
public class GPSLogReason extends Container {
    GPSstate m_GPSstate;
    
    Check m_chkTimeOnOff;
    Check m_chkDistanceOnOff;
    Check m_chkSpeedOnOff;
    Label m_lbTime;
    Label m_lbDistance;
    Label m_lbkSpeed;
    Edit m_edTime;
    Edit m_edDistance;
    Edit m_edSpeed;
    
    Button m_btSet;
    
    
    public GPSLogReason(GPSstate state) {
        m_GPSstate= state;
        
    }
    
    private final static String C_DIGITS="0123456789";
    
    protected void onStart() {
        super.onStart();
        add(m_chkTimeOnOff = new Check("Time    "), LEFT, TOP); //$NON-NLS-1$
        add(m_edTime = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkSpeedOnOff = new Check("Speed   "), LEFT, AFTER); //$NON-NLS-1$
        add(m_edSpeed = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkDistanceOnOff = new Check("Distance"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edDistance = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_btSet = new Button("SET"), CENTER, AFTER+3); //$NON-NLS-1$
        m_edSpeed.setValidChars(C_DIGITS);
        m_edDistance.setValidChars(C_DIGITS);
        m_edTime.setValidChars(C_DIGITS);
    }
    
    public void updateButtons() {
        m_edTime.setText(Convert.toString(m_GPSstate.logTimeInterval));
        m_edSpeed.setText(Convert.toString(m_GPSstate.logSpeedInterval));
        m_edDistance.setText(Convert.toString(m_GPSstate.logDistanceInterval));
    }

    public void setSettings() {
        if(m_chkTimeOnOff.getChecked()) {
            m_GPSstate.setLogTimeInterval(Convert.toInt(m_edTime.getText()));
        }
        if(m_chkSpeedOnOff.getChecked()) {
            m_GPSstate.setLogSpeedInterval(Convert.toInt(m_edSpeed.getText()));
        }
        if(m_chkDistanceOnOff.getChecked()) {
            m_GPSstate.setLogDistanceInterval(Convert.toInt(m_edDistance.getText()));
        }
        m_GPSstate.getLogReasonStatus();
    }

    
    public void onEvent(Event event) {
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed=true;
            if (event.target==m_chkTimeOnOff) {
                
            } else if(event.target==m_chkTimeOnOff) {
            } else if(event.target==m_chkSpeedOnOff) {
            } else if(event.target==m_chkDistanceOnOff) {
            } else if(event.target==m_btSet) {
                setSettings();
            } else if (event.target == this) {
                m_GPSstate.getLogReasonStatus();
            } else if (event.target==null) {
                updateButtons();
            } else {
                event.consumed=false;
            }
            break;
        }
    }

    
    
}
