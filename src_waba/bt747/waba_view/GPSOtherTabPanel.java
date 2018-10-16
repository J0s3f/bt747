package bt747.waba_view;

//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     seesite@bt747.org                       ***
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

import bt747.Txt;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/**
 * Implements "Other" panel for miscelaneous settings.
 * 
 * @author Mario De Weerd
 */
public final class GPSOtherTabPanel extends Container implements ModelListener {

    private TabPanel tabPanel;
    private final AppController c;
    private final Model m;

    private final String[] c_tpCaptions = { Txt.getString(Txt.TAB_FLSH), Txt.getString(Txt.TAB_NMEA_OUT),
            Txt.getString(Txt.TAB_NMEA_FILE), Txt.getString(Txt.TAB_HOLUX) };

    public GPSOtherTabPanel(final AppController c, final Model m) {
        this.c = c;
        this.m = m;
    }

    /**
     * 
     */
    public final void onStart() {
        add(tabPanel = new TabPanel(c_tpCaptions), CENTER, CENTER);
        tabPanel.setBorderStyle(Window.NO_BORDER);
        tabPanel.setRect(getClientRect().modifiedBy(0, 0, 0, 0));

        tabPanel.setPanel(0, new GPSFlashOption(m, c));
        tabPanel.setPanel(1, new GPSNMEAOutput(m, c));
        tabPanel.setPanel(2, new GPSFileNMEAOutputSel(c, m));
        tabPanel.setPanel(3, new GPSHoluxSpecific(m, c));
    }

    public final void onEvent(final Event event) {
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
            break;
        }
    }

    public final void modelEvent(final ModelEvent event) {
        if (this.isVisible()) {
            ModelListener l;
            l = (ModelListener) tabPanel.getChildren()[0];
            l.modelEvent(event);
        }
    }
}
