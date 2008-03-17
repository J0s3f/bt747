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
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.TabPanel;
import waba.ui.Window;

import gps.GPSstate;
import gps.GpsEvent;

import bt747.Txt;

/*
 * Created on 3 sept. 2007
 *
 */

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPSOtherTabPanel extends Container {

    private TabPanel m_TabPanel;
    private GPSFlashOption  m_GPSFlash;
    private GPSNMEAOutput m_GPSNMEAOutput;
    private GPSFileNMEAOutputSel m_GPSNMEAFile;
    private GPSHoluxSpecific m_GPSHolux;
    private GPSstate m_GPSstate;
    private AppSettings m_settings;
    
    private final String c_tpCaptions[]= {
            Txt.TAB_FLSH,
            Txt.TAB_NMEA_OUT,
            Txt.TAB_NMEA_FILE,
            Txt.TAB_HOLUX
    };
    
    /**
     * @param settings TODO
     * 
     */
    public GPSOtherTabPanel(final GPSstate state, AppSettings settings) {
        m_GPSstate=state;
        m_settings=settings;
    }
    
    /**
     * 
     */
    public void onStart() {
        add(m_TabPanel=new TabPanel(c_tpCaptions),CENTER,CENTER);
        m_TabPanel.setBorderStyle(Window.NO_BORDER);
        m_TabPanel.setRect(getClientRect().modifiedBy(0,0,0,0));
        // TODO Auto-generated method stub
        m_TabPanel.setPanel(0,m_GPSFlash = new GPSFlashOption(m_GPSstate));
        m_TabPanel.setPanel(1,m_GPSNMEAOutput = new GPSNMEAOutput(m_GPSstate));
        m_TabPanel.setPanel(2,m_GPSNMEAFile = new GPSFileNMEAOutputSel(m_settings));
        m_TabPanel.setPanel(3,m_GPSHolux = new GPSHoluxSpecific(m_settings,m_GPSstate));
    }
    public void onEvent(Event event) {
        //
        switch (event.type) {
        case ControlEvent.PRESSED:
            if(event.target==m_TabPanel||event.target==this) {
                Control c;
                c=m_TabPanel.getChildren()[0];
                c.postEvent(new Event(ControlEvent.PRESSED,c,0));                
            }
            break;
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                Control c;
                c=m_TabPanel.getChildren()[0];
                c.postEvent(new Event(GpsEvent.DATA_UPDATE,c,0));
                event.consumed=true;
            }
            break;
        }
    }
}
