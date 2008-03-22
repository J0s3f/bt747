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
import waba.ui.Container;
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.TabPanel;
import waba.ui.Window;

import gps.GPSFilter;
import gps.GPSFilterAdvanced;
import gps.GpsEvent;

import bt747.Txt;
import bt747.model.AppSettings;

/**
 * @author Mario De Weerd

 */
public class GPSFiltersTabPanel extends Container {

    private TabPanel m_TabPanel;
    private GPSLogFilter  m_GPSLogFilter;
    private GPSLogFilterAdv  m_GPSLogFilterAdv;
    private GPSNMEAOutput m_GPSNMEAOutput;
    private GPSFileNMEAOutputSel m_GPSNMEAFile;
    private AppSettings m_settings;
    private GPSFilter[] m_logFilters;
    private GPSFilterAdvanced[] m_logFiltersAdv;
    
    private final String c_tpCaptions[]= {
            Txt.STANDARD,
            Txt.ADVANCED
    };
    
    public GPSFiltersTabPanel(AppSettings settings,GPSFilter[] logFilters,GPSFilterAdvanced[] logFiltersAdv) {
        m_settings=settings;
        m_logFilters=logFilters;
        m_logFiltersAdv=logFiltersAdv;
    }
    
    /**
     * 
     */
    public void onStart() {
        add(m_TabPanel=new TabPanel(c_tpCaptions),CENTER,CENTER);
        m_TabPanel.setBorderStyle(Window.NO_BORDER);
        m_TabPanel.setRect(getClientRect().modifiedBy(0,0,0,0));
        m_TabPanel.setPanel(0,m_GPSLogFilter = new GPSLogFilter(m_settings,m_logFilters,m_logFiltersAdv));
        m_TabPanel.setPanel(1,m_GPSLogFilterAdv = new GPSLogFilterAdv(m_settings, m_logFiltersAdv));
//        m_TabPanel.setPanel(2,m_GPSNMEAFile = new GPSFileNMEAOutputSel(m_settings));
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
        default:
            if(event.type==GpsEvent.DATA_UPDATE) {
                if(event.target==this) {
                    Control c;
                    c=m_TabPanel.getChildren()[0];
                    c.postEvent(new Event(GpsEvent.DATA_UPDATE,c,0));
                    event.consumed=true;
                }
            }
        }
    }
}
