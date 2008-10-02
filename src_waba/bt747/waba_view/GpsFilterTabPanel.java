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

import bt747.model.ModelEvent;

import bt747.Txt;
import bt747.model.Model;
import bt747.model.ModelListener;

/**
 * @author Mario De Weerd
 * 
 */
public final class GpsFilterTabPanel extends Container implements ModelListener {

    private Model m;
    private AppController c;

    private TabPanel tabPanel;

    private final String[] tpCaptions = { Txt.STANDARD, Txt.ADVANCED, Txt.C_FMT };

    public GpsFilterTabPanel(final Model m, final AppController c) {
        this.m = m;
        this.c = c;
    }

    /**
     * 
     */
    protected void onStart() {
        add(tabPanel = new TabPanel(tpCaptions), CENTER, CENTER);
        tabPanel.setBorderStyle(Window.NO_BORDER);
        tabPanel.setRect(getClientRect().modifiedBy(0, 0, 0, 0));
        tabPanel.setPanel(0, new GPSLogFilter(c, m));
        tabPanel.setPanel(1, new GPSLogFilterAdv(c, m));
        tabPanel.setPanel(2, new GpsFileLogFormat(c, m));
        // m_TabPanel.setPanel(2,m_GPSNMEAFile = new GPSFileNMEAOutputSel(m));
    }

    public void onEvent(final Event event) {
        //
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target == tabPanel || event.target == this) {
                Control cntrl;
                cntrl = tabPanel.getChildren()[0];
                cntrl.postEvent(new Event(ControlEvent.PRESSED, cntrl, 0));
            }
            break;
        default:
        }
    }

    public void modelEvent(final ModelEvent event) {
        int eventType = event.getType();
        if (eventType == ModelEvent.DATA_UPDATE) {
            // Todo - avoid event transofrmation.
            Control ctrnl;
            ctrnl = tabPanel.getChildren()[0];
            ctrnl.postEvent(new Event(ModelEvent.DATA_UPDATE, ctrnl, 0));
        }
    }

}
