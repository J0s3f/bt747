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

import gps.GpsEvent;

import bt747.Txt;
import bt747.model.Controller;
import bt747.model.Model;

/**
 * @author Mario De Weerd
 * 
 */
public class GPSFiltersTabPanel extends Container {

    private Model m;
    private Controller c;

    private TabPanel m_TabPanel;

    private final String[] c_tpCaptions = { Txt.STANDARD, Txt.ADVANCED };

    public GPSFiltersTabPanel(final Model m, final Controller c) {
        this.m = m;
        this.c = c;
    }

    /**
     * 
     */
    public final void onStart() {
        add(m_TabPanel = new TabPanel(c_tpCaptions), CENTER, CENTER);
        m_TabPanel.setBorderStyle(Window.NO_BORDER);
        m_TabPanel.setRect(getClientRect().modifiedBy(0, 0, 0, 0));
        m_TabPanel.setPanel(0, new GPSLogFilter(c, m));
        m_TabPanel.setPanel(1, new GPSLogFilterAdv(c, m));
        // m_TabPanel.setPanel(2,m_GPSNMEAFile = new GPSFileNMEAOutputSel(m));
    }

    public final void onEvent(final Event event) {
        //
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target == m_TabPanel || event.target == this) {
                Control cntrl;
                cntrl = m_TabPanel.getChildren()[0];
                cntrl.postEvent(new Event(ControlEvent.PRESSED, cntrl, 0));
            }
            break;
        default:
            if (event.type == GpsEvent.DATA_UPDATE) {
                if (event.target == this) {
                    Control ctrnl;
                    ctrnl = m_TabPanel.getChildren()[0];
                    ctrnl.postEvent(new Event(GpsEvent.DATA_UPDATE, ctrnl, 0));
                    event.consumed = true;
                }
            }
        }
    }
}
