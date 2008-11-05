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
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;

import gps.GPSstate;
import gps.GpsEvent;
/**
 * @author Mario De Weerd
 */
public class GPSFlashOption extends Container {
    final private static boolean ENABLE_PWR_SAVE_CONTROL=false;
    private GPSstate m_GPSstate;
    
    private Edit m_userOptionTimesLeft;
    private Edit m_edUpdateRate;
    private Edit m_edBaudRate;
    private Edit m_edGLL_Period;
    private Edit m_edRMC_Period;
    private Edit m_edVTG_Period;
    private Edit m_edGSA_Period;
    private Edit m_edGSV_Period;
    private Edit m_edGGA_Period;
    private Edit m_edZDA_Period;
    private Edit m_edMCHN_Period;
    
    
    private Button m_btSet;
    
    public GPSFlashOption(GPSstate state) {
        m_GPSstate= state;
    }
    
    protected void onStart() {
        super.onStart();
        
        add(new Label("TimesLeft"),LEFT,TOP);
        add(m_userOptionTimesLeft= new Edit(),AFTER,SAME);
        m_userOptionTimesLeft.setEnabled(false);
        add(new Label("Update Rate (Hz)"),LEFT,AFTER);
        add(m_edUpdateRate= new Edit(),AFTER,SAME);
        add(new Label("Baud Rate"),LEFT,AFTER);
        add(m_edBaudRate= new Edit(),AFTER,SAME);
        add(new Label("GLL Per"),LEFT,AFTER);
        add(m_edGLL_Period= new Edit(),AFTER,SAME);
        add(new Label("RMC Per"),AFTER,SAME);
        add(m_edRMC_Period= new Edit(),AFTER,SAME);
        add(new Label("VTG Per"),LEFT,AFTER);
        add(m_edVTG_Period= new Edit(),AFTER,SAME);
        add(new Label("GSA Per"),AFTER,SAME);
        add(m_edGSA_Period= new Edit(),AFTER,SAME);
        add(new Label("GSV Per"),LEFT,AFTER);
        add(m_edGSV_Period= new Edit(),AFTER,SAME);
        add(new Label("GGA Per"),AFTER,SAME);
        add(m_edGGA_Period= new Edit(),AFTER,SAME);
        add(new Label("ZDA Per"),LEFT,AFTER);
        add(m_edZDA_Period= new Edit(),AFTER,SAME);
        add(new Label("MCHN Per"),AFTER,SAME);
        add(m_edMCHN_Period= new Edit(),AFTER,SAME);
        
        m_btSet = new Button("SET");
        //add(m_btSet, CENTER, AFTER+3); //$NON-NLS-1$
        
        
    }
    
    
    public void updateButtons() {
        m_userOptionTimesLeft.setText(Convert.toString(m_GPSstate.userOptionTimesLeft));
        m_edUpdateRate.setText(Convert.toString(m_GPSstate.dtUpdateRate));
        m_edBaudRate.setText(Convert.toString(m_GPSstate.dtBaudRate));
        m_edGLL_Period.setText(Convert.toString(m_GPSstate.dtGLL_Period));
        m_edRMC_Period.setText(Convert.toString(m_GPSstate.dtRMC_Period));
        m_edVTG_Period.setText(Convert.toString(m_GPSstate.dtVTG_Period));
        m_edGSA_Period.setText(Convert.toString(m_GPSstate.dtGSA_Period));
        m_edGSV_Period.setText(Convert.toString(m_GPSstate.dtGSV_Period));
        m_edGGA_Period.setText(Convert.toString(m_GPSstate.dtGGA_Period));
        m_edZDA_Period.setText(Convert.toString(m_GPSstate.dtZDA_Period));
        m_edMCHN_Period.setText(Convert.toString(m_GPSstate.dtMCHN_Period));
        
        
        m_userOptionTimesLeft.repaintNow();
        m_edUpdateRate.repaintNow();
        m_edBaudRate.repaintNow();
        m_edGLL_Period.repaintNow();
        m_edRMC_Period.repaintNow();
        m_edVTG_Period.repaintNow();
        m_edGSA_Period.repaintNow();
        m_edGSV_Period.repaintNow();
        m_edGGA_Period.repaintNow();
        m_edZDA_Period.repaintNow();
        m_edMCHN_Period.repaintNow();

    }
    
    public void setSettings() {
    }
    
    
    public void onEvent(Event event) {
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed=true;
        if(event.target==m_btSet) {
            setSettings();
        } else if (event.target == this) {
            m_GPSstate.getFlashUserOption();
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