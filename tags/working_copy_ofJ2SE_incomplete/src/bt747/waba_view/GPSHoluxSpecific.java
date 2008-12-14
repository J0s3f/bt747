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
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;

import gps.GPSstate;
import gps.GpsEvent;

import bt747.Txt;
import bt747.model.Model;

/**
 * @author Mario De Weerd
 *
 * Implements Holux Specific operations
 */
public class GPSHoluxSpecific extends Container {
  
    private Model m_settings;
    private GPSstate m_gpsState;
    
    private Label lbHoluxName;
    private Edit  edHoluxName;
    
    private Button btSet;
    /**
     * 
     */
    public GPSHoluxSpecific(final Model settings, final GPSstate gpsState) {
        m_settings=settings;
        m_gpsState=gpsState;
    }

    
    /* (non-Javadoc)
     * @see waba.ui.Container#onStart()
     */
    protected void onStart() {
        lbHoluxName= new Label(Txt.HOLUX_NAME);
        add(lbHoluxName,LEFT,TOP);
        add(edHoluxName= new Edit(""),AFTER,SAME);
        add(btSet=new Button(Txt.SET),CENTER,AFTER+5);
    }
    
    private void doSet() {
        m_gpsState.setHoluxName(edHoluxName.getText().replace(',',';'));
    }
    
    private void updateData() {
        edHoluxName.setText(m_gpsState.getHoluxName());
    }
    

    /** Handle events for this object.
     * @param event The event to be interpreted.
     */
     public void onEvent( Event event ) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target==this) {
                m_gpsState.requestHoluxName();
                event.consumed=true;
            } else if(event.target==btSet) {
                doSet();
            }
            break;
        default:
            if(event.type==GpsEvent.DATA_UPDATE) {
                if(event.target==this) {
                    updateData();
                    event.consumed=true;
                }
            }
        }
    }

}