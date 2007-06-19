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
import waba.ui.Check;
import waba.ui.Container;
import waba.ui.Edit;
import waba.ui.Label;

import gps.GPSstate;
/**
 * @author Mario De Weerd
 */
public class GPSLogInfo extends Container {
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
    
    
    public GPSLogInfo(GPSstate state) {
        m_GPSstate= state;
        
    }
    
    protected void onStart() {
        super.onStart();
        add(m_chkTimeOnOff = new Check("Time    "), LEFT, TOP); //$NON-NLS-1$
        add(m_edTime = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkSpeedOnOff = new Check("Speed   "), LEFT, AFTER); //$NON-NLS-1$
        add(m_edSpeed = new Edit(), AFTER, SAME); //$NON-NLS-1$
        add(m_chkDistanceOnOff = new Check("Distance"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edDistance = new Edit(), AFTER, SAME); //$NON-NLS-1$
    }
    
    
}
