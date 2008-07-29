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

import bt747.Txt;
import bt747.model.AppController;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;

/*
 * Created on 3 sept. 2007
 *
 */

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GPSOtherTabPanel extends Container implements ModelListener {

    private TabPanel tabPanel;
    private AppController c;
    private Model m;

    private final String c_tpCaptions[] = { Txt.TAB_FLSH, Txt.TAB_NMEA_OUT,
            Txt.TAB_NMEA_FILE, Txt.TAB_HOLUX };


    public GPSOtherTabPanel(final AppController c, Model m) {
        this.c = c;
        this.m = m;
    }

    /**
     * 
     */
    public void onStart() {
        add(tabPanel = new TabPanel(c_tpCaptions), CENTER, CENTER);
        tabPanel.setBorderStyle(Window.NO_BORDER);
        tabPanel.setRect(getClientRect().modifiedBy(0, 0, 0, 0));
        // TODO Auto-generated method stub
        tabPanel.setPanel(0, new GPSFlashOption(m, c));
        tabPanel.setPanel(1, new GPSNMEAOutput(m, c));
        tabPanel.setPanel(2, new GPSFileNMEAOutputSel(c, m));
        tabPanel.setPanel(3, new GPSHoluxSpecific(m, c));
    }

    public void onEvent(Event event) {
        //
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target == tabPanel || event.target == this) {
                Control c;
                c = tabPanel.getChildren()[0];
                c.postEvent(new Event(ControlEvent.PRESSED, c, 0));
            }
            break;
        }
    }
    
    public final void modelEvent(final ModelEvent event) {
        if (event.getType() == ModelEvent.DATA_UPDATE) {
            if(this.isVisible()) {
                ModelListener c;
                c = (ModelListener)tabPanel.getChildren()[0];
                c.modelEvent(event);
            }
        }
    }
}
